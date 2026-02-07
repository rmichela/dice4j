package io.github.rmichela.dice4j.roll;

import static org.assertj.core.api.Assertions.*;

import io.github.rmichela.dice4j.dice.modifier.NoOpModifier;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class RolledPoolTest {

    @Test
    void testEmptyConstructor() {
        RolledPool pool = new RolledPool();
        assertThat(pool.getRolls()).isEmpty();
        assertThat(pool.gatherDice()).isEmpty();
        assertThat(pool.total()).isEqualTo(0);
    }

    @Test
    void testVarargsConstructorWithRolled() {
        RolledDie die1 = new RolledDie(6, 3);
        RolledDie die2 = new RolledDie(6, 5);
        RolledPool pool = new RolledPool(die1, die2);

        assertThat(pool.getRolls()).containsExactly(die1, die2);
    }

    @Test
    void testListConstructorWithRolledDice() {
        RolledDie die1 = new RolledDie(6, 2);
        RolledDie die2 = new RolledDie(6, 4);
        RolledPool pool = new RolledPool(List.of(die1, die2));

        assertThat(pool.getRolls()).containsExactly(die1, die2);
    }

    @Test
    void testAddRolled() {
        RolledPool pool = new RolledPool();
        RolledDie die = new RolledDie(6, 3);
        pool.addRolled(die);

        assertThat(pool.getRolls()).containsExactly(die);
    }

    @Test
    void testAddTo() {
        RolledPool pool = new RolledPool();
        RolledDie die = new RolledDie(6, 4);
        pool.addTo(die);

        assertThat(pool.getRolls()).containsExactly(die);
    }

    @Test
    void testMerge() {
        RolledDie die1 = new RolledDie(6, 2);
        RolledDie die2 = new RolledDie(6, 3);
        RolledDie die3 = new RolledDie(6, 4);
        RolledDie die4 = new RolledDie(6, 5);

        RolledPool pool1 = new RolledPool(die1, die2);
        RolledPool pool2 = new RolledPool(die3, die4);

        RolledPool merged = pool1.merge(pool2);

        assertThat(merged.getRolls()).containsExactly(die1, die2, die3, die4);
    }

    @Test
    void testGatherDiceFlattensNestedPools() {
        RolledDie die1 = new RolledDie(6, 2);
        RolledDie die2 = new RolledDie(6, 3);
        RolledDie die3 = new RolledDie(6, 4);

        RolledPool innerPool = new RolledPool(die2, die3);
        RolledPool outerPool = new RolledPool(die1, innerPool);

        assertThat(outerPool.gatherDice()).containsExactly(die1, die2, die3);
    }

    @Test
    void testGatherDiceWithDeeplyNestedPools() {
        RolledDie die1 = new RolledDie(6, 1);
        RolledDie die2 = new RolledDie(6, 2);
        RolledDie die3 = new RolledDie(6, 3);
        RolledDie die4 = new RolledDie(6, 4);

        RolledPool level3 = new RolledPool(die4);
        RolledPool level2 = new RolledPool(die3, level3);
        RolledPool level1 = new RolledPool(die2, level2);
        RolledPool level0 = new RolledPool(die1, level1);

        assertThat(level0.gatherDice()).containsExactly(die1, die2, die3, die4);
    }

    @Test
    void testTotal() {
        RolledDie die1 = new RolledDie(6, 3);
        RolledDie die2 = new RolledDie(6, 5);
        RolledPool pool = new RolledPool(die1, die2);

        assertThat(pool.total()).isEqualTo(8);
    }

    @Test
    void testTotalWithDroppedDice() {
        RolledDie die1 = new RolledDie(6, 3);
        RolledDie die2 = new RolledDie(6, 5);
        die2.drop();
        RolledPool pool = new RolledPool(die1, die2);

        assertThat(pool.total()).isEqualTo(3);
    }

    @Test
    void testTotalWithAllDroppedDice() {
        RolledDie die1 = new RolledDie(6, 3);
        RolledDie die2 = new RolledDie(6, 5);
        die1.drop();
        die2.drop();
        RolledPool pool = new RolledPool(die1, die2);

        assertThat(pool.total()).isEqualTo(0);
    }

    @Test
    void testTotalWithNestedPools() {
        RolledDie die1 = new RolledDie(6, 2);
        RolledDie die2 = new RolledDie(6, 3);
        RolledDie die3 = new RolledDie(6, 4);

        RolledPool innerPool = new RolledPool(die2, die3);
        RolledPool outerPool = new RolledPool(die1, innerPool);

        assertThat(outerPool.total()).isEqualTo(9);
    }

    @Test
    void testCollector() {
        RolledDie die1 = new RolledDie(6, 2);
        RolledDie die2 = new RolledDie(6, 3);
        RolledDie die3 = new RolledDie(6, 4);

        RolledPool pool = Stream.of(die1, die2, die3).collect(RolledPool.collector());

        assertThat(pool.getRolls()).containsExactly(die1, die2, die3);
    }

    @Test
    void testToString() {
        RolledDie die1 = new RolledDie(6, 2);
        RolledDie die2 = new RolledDie(6, 4);
        RolledPool pool = new RolledPool(die1, die2);

        assertThat(pool.toString()).isEqualTo("{ [2/6], [4/6] }");
    }

    @Test
    void testToStringWithEmptyPool() {
        RolledPool pool = new RolledPool();
        assertThat(pool.toString()).isEqualTo("{  }");
    }

    @Test
    void testToStringWithNestedPools() {
        RolledDie die1 = new RolledDie(6, 2);
        RolledDie die2 = new RolledDie(6, 3);
        RolledPool innerPool = new RolledPool(die1, die2);
        RolledPool outerPool = new RolledPool(innerPool);

        assertThat(outerPool.toString()).isEqualTo("{ { [2/6], [3/6] } }");
    }

    @Test
    void testToStringWithDroppedDie() {
        RolledDie die1 = new RolledDie(6, 2);
        RolledDie die2 = new RolledDie(6, 4);
        die2.drop();
        RolledPool pool = new RolledPool(die1, die2);

        assertThat(pool.toString()).isEqualTo("{ [2/6], [4/6x] }");
    }

    // gatherPools() tests

    @Test
    void testGatherPoolsWithEmptyPool() {
        RolledPool pool = new RolledPool();
        List<RolledPool> gathered = pool.gatherPools();

        assertThat(gathered).isEmpty();
    }

    @Test
    void testGatherPoolsWithSingleDie() {
        RolledDie die = new RolledDie(6, 3);
        RolledPool pool = new RolledPool(die);
        List<RolledPool> gathered = pool.gatherPools();

        assertThat(gathered).hasSize(1);
        assertThat(gathered.get(0).gatherDice()).containsExactly(die);
        assertThat(gathered.get(0).getModifiedBy()).isNull();
    }

    @Test
    void testGatherPoolsWithMultipleDice() {
        RolledDie die1 = new RolledDie(6, 2);
        RolledDie die2 = new RolledDie(6, 4);
        RolledDie die3 = new RolledDie(6, 5);
        RolledPool pool = new RolledPool(die1, die2, die3);
        List<RolledPool> gathered = pool.gatherPools();

        assertThat(gathered).hasSize(1);
        assertThat(gathered.get(0).gatherDice()).containsExactly(die1, die2, die3);
        assertThat(gathered.get(0).getModifiedBy()).isNull();
    }

    @Test
    void testGatherPoolsFlattensUnmodifiedNestedPool() {
        RolledDie die1 = new RolledDie(6, 2);
        RolledDie die2 = new RolledDie(6, 3);
        RolledDie die3 = new RolledDie(6, 4);

        RolledPool innerPool = new RolledPool(die2, die3);
        RolledPool outerPool = new RolledPool(die1, innerPool);

        List<RolledPool> gathered = outerPool.gatherPools();

        // All dice should be flattened into a single pool since innerPool is unmodified
        assertThat(gathered).hasSize(1);
        assertThat(gathered.get(0).gatherDice()).containsExactly(die1, die2, die3);
        assertThat(gathered.get(0).getModifiedBy()).isNull();
    }

    @Test
    void testGatherPoolsDoesNotFlattenModifiedNestedPool() {
        RolledDie die1 = new RolledDie(6, 2);
        RolledDie die2 = new RolledDie(6, 3);
        RolledDie die3 = new RolledDie(6, 4);

        RolledPool innerPool = new RolledPool(die2, die3);
        innerPool.modifiedBy(new NoOpModifier());

        RolledPool outerPool = new RolledPool(die1, innerPool);

        List<RolledPool> gathered = outerPool.gatherPools();

        // Should have 2 pools: one for die1, one for the modified innerPool
        assertThat(gathered).hasSize(2);

        // First pool should be die1 (flattened loose dice)
        assertThat(gathered.get(0).gatherDice()).containsExactly(die2, die3);
        assertThat(gathered.get(0).getModifiedBy()).isNotNull();

        // Second pool should contain die1
        assertThat(gathered.get(1).gatherDice()).containsExactly(die1);
        assertThat(gathered.get(1).getModifiedBy()).isNull();
    }

    @Test
    void testGatherPoolsMixedModifiedAndUnmodifiedPools() {
        RolledDie die1 = new RolledDie(6, 1);
        RolledDie die2 = new RolledDie(6, 2);
        RolledDie die3 = new RolledDie(6, 3);
        RolledDie die4 = new RolledDie(6, 4);

        RolledPool unmodifiedPool1 = new RolledPool(die1);
        RolledPool modifiedPool = new RolledPool(die2);
        modifiedPool.modifiedBy(new NoOpModifier());
        RolledPool unmodifiedPool2 = new RolledPool(die3, die4);

        RolledPool outerPool = new RolledPool(unmodifiedPool1, modifiedPool, unmodifiedPool2);

        List<RolledPool> gathered = outerPool.gatherPools();

        // Should have 2 pools: modified pool stays separate, unmodified pools flatten together
        assertThat(gathered).hasSize(2);

        // Find the modified pool
        RolledPool modPool =
                gathered.stream().filter(p -> p.getModifiedBy() != null).findFirst().orElseThrow();
        assertThat(modPool.gatherDice()).containsExactly(die2);
        assertThat(modPool.getModifiedBy()).isNotNull();

        // Find the flattened unmodified pool
        RolledPool unmodPool =
                gathered.stream().filter(p -> p.getModifiedBy() == null).findFirst().orElseThrow();
        assertThat(unmodPool.gatherDice()).containsExactly(die1, die3, die4);
    }

    @Test
    void testGatherPoolsDeeplyNestedUnmodifiedPools() {
        RolledDie die1 = new RolledDie(6, 1);
        RolledDie die2 = new RolledDie(6, 2);
        RolledDie die3 = new RolledDie(6, 3);
        RolledDie die4 = new RolledDie(6, 4);

        RolledPool level3 = new RolledPool(die4);
        RolledPool level2 = new RolledPool(die3, level3);
        RolledPool level1 = new RolledPool(die2, level2);
        RolledPool level0 = new RolledPool(die1, level1);

        List<RolledPool> gathered = level0.gatherPools();

        // All should be flattened into a single pool
        assertThat(gathered).hasSize(1);
        assertThat(gathered.get(0).gatherDice()).containsExactly(die1, die2, die3, die4);
        assertThat(gathered.get(0).getModifiedBy()).isNull();
    }

    @Test
    void testGatherPoolsDeeplyNestedWithModifiedPoolsAtDifferentLevels() {
        RolledDie die1 = new RolledDie(6, 1);
        RolledDie die2 = new RolledDie(6, 2);
        RolledDie die3 = new RolledDie(6, 3);
        RolledDie die4 = new RolledDie(6, 4);

        RolledPool level3 = new RolledPool(die4);
        level3.modifiedBy(new NoOpModifier());

        RolledPool level2 = new RolledPool(die3, level3);

        RolledPool level1 = new RolledPool(die2, level2);
        level1.modifiedBy(new NoOpModifier());

        RolledPool level0 = new RolledPool(die1, level1);

        List<RolledPool> gathered = level0.gatherPools();

        // Should have 3 pools: level3 (modified), level1 (modified), and loose die1
        assertThat(gathered).hasSize(3);

        // Check that modified pools are preserved
        List<RolledPool> modifiedPools =
                gathered.stream().filter(p -> p.getModifiedBy() != null).toList();
        assertThat(modifiedPools).hasSize(2);
    }

    @Test
    void testGatherPoolsWithParentModifier() {
        RolledDie die1 = new RolledDie(6, 2);
        RolledDie die2 = new RolledDie(6, 3);

        RolledPool innerPool = new RolledPool(die2);
        RolledPool outerPool = new RolledPool(die1, innerPool);

        outerPool.modifiedBy(new NoOpModifier());

        List<RolledPool> gathered = outerPool.gatherPools();

        // Should flatten into single pool with parent modifier
        assertThat(gathered).hasSize(1);
        assertThat(gathered.get(0).gatherDice()).containsExactly(die1, die2);
        assertThat(gathered.get(0).getModifiedBy()).isNotNull();
    }

    @Test
    void testGatherPoolsMultipleModifiedPools() {
        RolledDie die1 = new RolledDie(6, 1);
        RolledDie die2 = new RolledDie(6, 2);
        RolledDie die3 = new RolledDie(6, 3);

        RolledPool pool1 = new RolledPool(die1);
        pool1.modifiedBy(new NoOpModifier());

        RolledPool pool2 = new RolledPool(die2);
        pool2.modifiedBy(new NoOpModifier());

        RolledPool pool3 = new RolledPool(die3);
        pool3.modifiedBy(new NoOpModifier());

        RolledPool outerPool = new RolledPool(pool1, pool2, pool3);

        List<RolledPool> gathered = outerPool.gatherPools();

        // All 3 modified pools should be kept separate
        assertThat(gathered).hasSize(3);
        assertThat(gathered).allMatch(p -> p.getModifiedBy() != null);
    }

    @Test
    void testGatherPoolsComplexNestedStructure() {
        // Create a complex structure:
        // outerPool
        //   ├─ die1 (loose)
        //   ├─ unmodifiedPool1
        //   │   ├─ die2
        //   │   └─ die3
        //   ├─ modifiedPool1 (modified)
        //   │   └─ die4
        //   ├─ nestedStructure (unmodified)
        //   │   ├─ die5
        //   │   ├─ modifiedPool2 (modified)
        //   │   │   └─ die6
        //   │   └─ die7
        //   └─ die8 (loose)

        RolledDie die1 = new RolledDie(6, 1);
        RolledDie die2 = new RolledDie(6, 2);
        RolledDie die3 = new RolledDie(6, 3);
        RolledDie die4 = new RolledDie(6, 4);
        RolledDie die5 = new RolledDie(6, 5);
        RolledDie die6 = new RolledDie(6, 6);
        RolledDie die7 = new RolledDie(8, 7);
        RolledDie die8 = new RolledDie(8, 8);

        RolledPool unmodifiedPool1 = new RolledPool(die2, die3);

        RolledPool modifiedPool1 = new RolledPool(die4);
        modifiedPool1.modifiedBy(new NoOpModifier());

        RolledPool modifiedPool2 = new RolledPool(die6);
        modifiedPool2.modifiedBy(new NoOpModifier());

        RolledPool nestedStructure = new RolledPool(die5, modifiedPool2, die7);

        RolledPool outerPool =
                new RolledPool(die1, unmodifiedPool1, modifiedPool1, nestedStructure, die8);

        List<RolledPool> gathered = outerPool.gatherPools();

        // Expected: 3 pools
        // 1. modifiedPool1 with die4
        // 2. modifiedPool2 with die6
        // 3. flattened pool with die1, die2, die3, die5, die7, die8
        assertThat(gathered).hasSize(3);

        // Find modified pools
        List<RolledPool> modifiedPools =
                gathered.stream().filter(p -> p.getModifiedBy() != null).toList();
        assertThat(modifiedPools).hasSize(2);

        // Find flattened unmodified pool
        RolledPool flatPool =
                gathered.stream().filter(p -> p.getModifiedBy() == null).findFirst().orElseThrow();
        assertThat(flatPool.gatherDice()).containsExactly(die1, die2, die3, die5, die7, die8);
    }
}
