package io.github.rmichela.dice4j.dice.modifier;

import static org.assertj.core.api.Assertions.*;

import io.github.rmichela.dice4j.dice.ImplicitDicePool;
import io.github.rmichela.dice4j.roll.Rolled;
import io.github.rmichela.dice4j.roll.RolledDie;
import io.github.rmichela.dice4j.roller.FixedSequenceRoller;
import java.util.List;
import org.junit.jupiter.api.Test;

class KeepDropModifierTest {

    @Test
    void testKeepHighest() {
        ImplicitDicePool pool = new ImplicitDicePool(4, 6);
        KeepDropModifier modifier = KeepDropModifier.keepHighest(2);
        modifier.decorate(pool);

        FixedSequenceRoller roller = new FixedSequenceRoller(1, 3, 5, 2);
        Rolled rolled = modifier.roll(roller);

        List<RolledDie> dice = rolled.gatherDice();
        assertThat(dice).hasSize(4);

        assertThat(dice.get(0).isKept()).isFalse(); // 1
        assertThat(dice.get(1).isKept()).isTrue(); // 3
        assertThat(dice.get(2).isKept()).isTrue(); // 5
        assertThat(dice.get(3).isKept()).isFalse(); // 2

        assertThat(rolled.total()).isEqualTo(8); // 5 + 3
    }

    @Test
    void testKeepHighestMultiple() {
        ImplicitDicePool pool = new ImplicitDicePool(4, 6);
        KeepDropModifier modifier = KeepDropModifier.keepHighest(3);
        modifier.decorate(pool);

        FixedSequenceRoller roller = new FixedSequenceRoller(1, 6, 4, 2);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled.total()).isEqualTo(12); // 6 + 4 + 2
    }

    @Test
    void testKeepLowest() {
        ImplicitDicePool pool = new ImplicitDicePool(4, 6);
        KeepDropModifier modifier = KeepDropModifier.keepLowest(2);
        modifier.decorate(pool);

        FixedSequenceRoller roller = new FixedSequenceRoller(5, 2, 4, 1);
        Rolled rolled = modifier.roll(roller);

        List<RolledDie> dice = rolled.gatherDice();
        assertThat(dice).hasSize(4);

        assertThat(dice.get(0).isKept()).isFalse(); // 5
        assertThat(dice.get(1).isKept()).isTrue(); // 2
        assertThat(dice.get(2).isKept()).isFalse(); // 4
        assertThat(dice.get(3).isKept()).isTrue(); // 1

        assertThat(rolled.total()).isEqualTo(3); // 2 + 1
    }

    @Test
    void testKeepLowestMultiple() {
        ImplicitDicePool pool = new ImplicitDicePool(5, 6);
        KeepDropModifier modifier = KeepDropModifier.keepLowest(3);
        modifier.decorate(pool);

        FixedSequenceRoller roller = new FixedSequenceRoller(6, 3, 5, 1, 4);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled.total()).isEqualTo(8); // 3 + 1 + 4
    }

    @Test
    void testDropHighest() {
        ImplicitDicePool pool = new ImplicitDicePool(4, 6);
        KeepDropModifier modifier = KeepDropModifier.dropHighest(2);
        modifier.decorate(pool);

        FixedSequenceRoller roller = new FixedSequenceRoller(1, 6, 5, 2);
        Rolled rolled = modifier.roll(roller);

        List<RolledDie> dice = rolled.gatherDice();
        assertThat(dice).hasSize(4);

        assertThat(dice.get(0).isKept()).isTrue(); // 1
        assertThat(dice.get(1).isKept()).isFalse(); // 6
        assertThat(dice.get(2).isKept()).isFalse(); // 5
        assertThat(dice.get(3).isKept()).isTrue(); // 2

        assertThat(rolled.total()).isEqualTo(3); // 1 + 2
    }

    @Test
    void testDropLowest() {
        ImplicitDicePool pool = new ImplicitDicePool(4, 6);
        KeepDropModifier modifier = KeepDropModifier.dropLowest(2);
        modifier.decorate(pool);

        FixedSequenceRoller roller = new FixedSequenceRoller(1, 6, 5, 2);
        Rolled rolled = modifier.roll(roller);

        List<RolledDie> dice = rolled.gatherDice();
        assertThat(dice).hasSize(4);

        assertThat(dice.get(0).isKept()).isFalse(); // 1
        assertThat(dice.get(1).isKept()).isTrue(); // 6
        assertThat(dice.get(2).isKept()).isTrue(); // 5
        assertThat(dice.get(3).isKept()).isFalse(); // 2

        assertThat(rolled.total()).isEqualTo(11); // 6 + 5
    }

    @Test
    void testKeepHighestWithSingleDie() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 20);
        KeepDropModifier modifier = KeepDropModifier.keepHighest(1);
        modifier.decorate(pool);

        FixedSequenceRoller roller = new FixedSequenceRoller(15);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled.total()).isEqualTo(15);
        assertThat(rolled.gatherDice().get(0).isKept()).isTrue();
    }

    @Test
    void testKeepAllDice() {
        ImplicitDicePool pool = new ImplicitDicePool(3, 6);
        KeepDropModifier modifier = KeepDropModifier.keepHighest(3);
        modifier.decorate(pool);

        FixedSequenceRoller roller = new FixedSequenceRoller(2, 4, 6);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled.total()).isEqualTo(12);
        assertThat(rolled.gatherDice()).allMatch(RolledDie::isKept);
    }

    @Test
    void testDropAllDice() {
        ImplicitDicePool pool = new ImplicitDicePool(3, 6);
        KeepDropModifier modifier = KeepDropModifier.dropHighest(3);
        modifier.decorate(pool);

        FixedSequenceRoller roller = new FixedSequenceRoller(2, 4, 6);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled.total()).isEqualTo(0);
        assertThat(rolled.gatherDice()).noneMatch(RolledDie::isKept);
    }

    @Test
    void testKeepHighestWithDuplicates() {
        ImplicitDicePool pool = new ImplicitDicePool(5, 6);
        KeepDropModifier modifier = KeepDropModifier.keepHighest(2);
        modifier.decorate(pool);

        FixedSequenceRoller roller = new FixedSequenceRoller(4, 4, 4, 2, 1);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled.total()).isEqualTo(8); // Two of the 4s
    }

    @Test
    void testToStringKeepHighest() {
        ImplicitDicePool pool = new ImplicitDicePool(4, 6);
        KeepDropModifier modifier = KeepDropModifier.keepHighest(2);
        modifier.decorate(pool);

        assertThat(modifier.toString()).contains("kh2");
    }

    @Test
    void testToStringKeepLowest() {
        ImplicitDicePool pool = new ImplicitDicePool(4, 6);
        KeepDropModifier modifier = KeepDropModifier.keepLowest(3);
        modifier.decorate(pool);

        assertThat(modifier.toString()).contains("kl3");
    }

    @Test
    void testToStringDropHighest() {
        ImplicitDicePool pool = new ImplicitDicePool(4, 6);
        KeepDropModifier modifier = KeepDropModifier.dropHighest(1);
        modifier.decorate(pool);

        assertThat(modifier.toString()).contains("dh1");
    }

    @Test
    void testToStringDropLowest() {
        ImplicitDicePool pool = new ImplicitDicePool(4, 6);
        KeepDropModifier modifier = KeepDropModifier.dropLowest(2);
        modifier.decorate(pool);

        assertThat(modifier.toString()).contains("dl2");
    }

    @Test
    void testStackedModifiersKeepThenDrop() {
        ImplicitDicePool pool = new ImplicitDicePool(5, 6);

        KeepDropModifier keepHighest = KeepDropModifier.keepHighest(4);
        keepHighest.decorate(pool);

        KeepDropModifier dropLowest = KeepDropModifier.dropLowest(1);
        dropLowest.decorate(keepHighest);

        FixedSequenceRoller roller = new FixedSequenceRoller(1, 2, 3, 4, 5);
        Rolled rolled = dropLowest.roll(roller);

        // First modifier keeps 4 highest: 2,3,4,5 (drops 1)
        // Second modifier drops 1 lowest: drops 1 (already dropped), so effective result is 2,3,4,5
        // kept
        assertThat(rolled.total()).isEqualTo(14); // 2 + 3 + 4 + 5
    }

    @Test
    void testModifiedByIsSet() {
        ImplicitDicePool pool = new ImplicitDicePool(3, 6);
        KeepDropModifier modifier = KeepDropModifier.keepHighest(2);
        modifier.decorate(pool);

        FixedSequenceRoller roller = new FixedSequenceRoller(1, 3, 5);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled.getModifiedBy()).isEqualTo(modifier);
    }
}
