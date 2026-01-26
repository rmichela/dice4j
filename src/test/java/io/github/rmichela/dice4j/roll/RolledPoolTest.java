package io.github.rmichela.dice4j.roll;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class RolledPoolTest {

    @Test
    void testEmptyConstructor() {
        RolledPool pool = new RolledPool();
        assertThat(pool.getRolls()).isEmpty();
        assertThat(pool.gather()).isEmpty();
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

        assertThat(merged).isSameAs(pool1);
        assertThat(pool1.getRolls()).containsExactly(die1, die2, die3, die4);
    }

    @Test
    void testGatherFlattensNestedPools() {
        RolledDie die1 = new RolledDie(6, 2);
        RolledDie die2 = new RolledDie(6, 3);
        RolledDie die3 = new RolledDie(6, 4);

        RolledPool innerPool = new RolledPool(die2, die3);
        RolledPool outerPool = new RolledPool(die1, innerPool);

        assertThat(outerPool.gather()).containsExactly(die1, die2, die3);
    }

    @Test
    void testGatherWithDeeplyNestedPools() {
        RolledDie die1 = new RolledDie(6, 1);
        RolledDie die2 = new RolledDie(6, 2);
        RolledDie die3 = new RolledDie(6, 3);
        RolledDie die4 = new RolledDie(6, 4);

        RolledPool level3 = new RolledPool(die4);
        RolledPool level2 = new RolledPool(die3, level3);
        RolledPool level1 = new RolledPool(die2, level2);
        RolledPool level0 = new RolledPool(die1, level1);

        assertThat(level0.gather()).containsExactly(die1, die2, die3, die4);
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
}
