package io.github.rmichela.dice4j.dice.modifier;

import static org.assertj.core.api.Assertions.*;

import io.github.rmichela.dice4j.dice.ImplicitDicePool;
import io.github.rmichela.dice4j.roll.Rolled;
import io.github.rmichela.dice4j.roll.RolledDie;
import io.github.rmichela.dice4j.roll.RolledPool;
import io.github.rmichela.dice4j.roller.FixedSequenceRoller;
import java.util.List;
import org.junit.jupiter.api.Test;

class MinMaxModifierTest {

    @Test
    void testMinimumOnSingleDie() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 6);
        MinMaxModifier modifier = MinMaxModifier.minimum(3);
        modifier.decorate(pool);

        // Roll a 2, should be replaced with 3
        FixedSequenceRoller roller = new FixedSequenceRoller(2);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(RolledPool.class);
        List<RolledDie> dice = rolled.gatherDice();

        assertThat(dice).hasSize(2);
        assertThat(dice.get(0).getValue()).isEqualTo(2);
        assertThat(dice.get(0).isKept()).isFalse(); // Original die is dropped
        assertThat(dice.get(1).getValue()).isEqualTo(3);
        assertThat(dice.get(1).isKept()).isTrue(); // Replaced die is kept

        assertThat(rolled.total()).isEqualTo(3);
    }

    @Test
    void testMaximumOnSingleDie() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 6);
        MinMaxModifier modifier = MinMaxModifier.maximum(4);
        modifier.decorate(pool);

        // Roll a 5, should be replaced with 4
        FixedSequenceRoller roller = new FixedSequenceRoller(5);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(RolledPool.class);
        List<RolledDie> dice = rolled.gatherDice();

        assertThat(dice).hasSize(2);
        assertThat(dice.get(0).getValue()).isEqualTo(5);
        assertThat(dice.get(0).isKept()).isFalse();
        assertThat(dice.get(1).getValue()).isEqualTo(4);
        assertThat(dice.get(1).isKept()).isTrue();

        assertThat(rolled.total()).isEqualTo(4);
    }

    @Test
    void testMinimumNoChangeWhenAboveMin() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 6);
        MinMaxModifier modifier = MinMaxModifier.minimum(3);
        modifier.decorate(pool);

        // Roll a 4, already above minimum
        FixedSequenceRoller roller = new FixedSequenceRoller(4);
        Rolled rolled = modifier.roll(roller);

        List<RolledDie> dice = rolled.gatherDice();
        assertThat(dice).hasSize(1);
        assertThat(dice.get(0).getValue()).isEqualTo(4);
        assertThat(dice.get(0).isKept()).isTrue();
        assertThat(rolled.total()).isEqualTo(4);
    }

    @Test
    void testMaximumNoChangeWhenBelowMax() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 6);
        MinMaxModifier modifier = MinMaxModifier.maximum(4);
        modifier.decorate(pool);

        // Roll a 3, already below maximum
        FixedSequenceRoller roller = new FixedSequenceRoller(3);
        Rolled rolled = modifier.roll(roller);

        List<RolledDie> dice = rolled.gatherDice();
        assertThat(dice).hasSize(1);
        assertThat(dice.get(0).getValue()).isEqualTo(3);
        assertThat(dice.get(0).isKept()).isTrue();
        assertThat(rolled.total()).isEqualTo(3);
    }

    @Test
    void testBothMinAndMaxOnSingleDie() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 10);
        MinMaxModifier min = MinMaxModifier.minimum(3);
        min.decorate(pool);
        MinMaxModifier max = MinMaxModifier.maximum(7);
        max.decorate(min);

        // Roll a 2, should be replaced with 3
        FixedSequenceRoller roller = new FixedSequenceRoller(2);
        Rolled rolled = max.roll(roller);

        assertThat(rolled.total()).isEqualTo(3);
    }

    @Test
    void testBothMinAndMaxWithHighRoll() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 10);
        MinMaxModifier min = MinMaxModifier.minimum(3);
        min.decorate(pool);
        MinMaxModifier max = MinMaxModifier.maximum(7);
        max.decorate(min);

        // Roll a 9, should be replaced with 7
        FixedSequenceRoller roller = new FixedSequenceRoller(9);
        Rolled rolled = max.roll(roller);

        assertThat(rolled.total()).isEqualTo(7);
    }

    @Test
    void testMinimumOnPool() {
        ImplicitDicePool pool = new ImplicitDicePool(4, 6);
        MinMaxModifier modifier = MinMaxModifier.minimum(3);
        modifier.decorate(pool);

        // Roll 1, 2, 4, 5 - the 1 and 2 should become 3
        FixedSequenceRoller roller = new FixedSequenceRoller(1, 2, 4, 5);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(RolledPool.class);
        List<RolledDie> dice = rolled.gatherDice();

        assertThat(dice).hasSize(6); // 4 original + 2 replaced
        assertThat(rolled.total()).isEqualTo(15); // 3 + 3 + 4 + 5
    }

    @Test
    void testMaximumOnPool() {
        ImplicitDicePool pool = new ImplicitDicePool(4, 6);
        MinMaxModifier modifier = MinMaxModifier.maximum(4);
        modifier.decorate(pool);

        // Roll 2, 3, 5, 6 - the 5 and 6 should become 4
        FixedSequenceRoller roller = new FixedSequenceRoller(2, 3, 5, 6);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(RolledPool.class);
        List<RolledDie> dice = rolled.gatherDice();

        assertThat(dice).hasSize(6); // 4 original + 2 replaced
        assertThat(rolled.total()).isEqualTo(13); // 2 + 3 + 4 + 4
    }

    @Test
    void testMinMaxOnPool() {
        ImplicitDicePool pool = new ImplicitDicePool(5, 10);
        MinMaxModifier min = MinMaxModifier.minimum(3);
        min.decorate(pool);
        MinMaxModifier max = MinMaxModifier.maximum(8);
        max.decorate(min);

        // Roll 1, 3, 5, 9, 10 - 1 becomes 3, 9 becomes 8, 10 becomes 8
        FixedSequenceRoller roller = new FixedSequenceRoller(1, 3, 5, 9, 10);
        Rolled rolled = max.roll(roller);

        List<RolledDie> dice = rolled.gatherDice();
        assertThat(dice).hasSize(8); // 5 original + 3 replaced
        assertThat(rolled.total()).isEqualTo(27); // 3 + 3 + 5 + 8 + 8
    }

    @Test
    void testMinimumAtBoundary() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 6);
        MinMaxModifier modifier = MinMaxModifier.minimum(3);
        modifier.decorate(pool);

        // Roll exactly the minimum
        FixedSequenceRoller roller = new FixedSequenceRoller(3);
        Rolled rolled = modifier.roll(roller);

        List<RolledDie> dice = rolled.gatherDice();
        assertThat(dice).hasSize(1); // No replacement needed
        assertThat(rolled.total()).isEqualTo(3);
    }

    @Test
    void testMaximumAtBoundary() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 6);
        MinMaxModifier modifier = MinMaxModifier.maximum(4);
        modifier.decorate(pool);

        // Roll exactly the maximum
        FixedSequenceRoller roller = new FixedSequenceRoller(4);
        Rolled rolled = modifier.roll(roller);

        List<RolledDie> dice = rolled.gatherDice();
        assertThat(dice).hasSize(1); // No replacement needed
        assertThat(rolled.total()).isEqualTo(4);
    }

    @Test
    void testMinimumWithD20() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 20);
        MinMaxModifier modifier = MinMaxModifier.minimum(10);
        modifier.decorate(pool);

        // Roll a 5, should become 10
        FixedSequenceRoller roller = new FixedSequenceRoller(5);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled.total()).isEqualTo(10);
    }

    @Test
    void testMaximumWithD20() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 20);
        MinMaxModifier modifier = MinMaxModifier.maximum(15);
        modifier.decorate(pool);

        // Roll a 18, should become 15
        FixedSequenceRoller roller = new FixedSequenceRoller(18);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled.total()).isEqualTo(15);
    }

    @Test
    void testMinMaxPreservesDieSides() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 20);
        MinMaxModifier min = MinMaxModifier.minimum(5);
        min.decorate(pool);
        MinMaxModifier max = MinMaxModifier.maximum(15);
        max.decorate(min);

        // Roll 2, should become 5
        FixedSequenceRoller roller = new FixedSequenceRoller(2);
        Rolled rolled = max.roll(roller);

        List<RolledDie> dice = rolled.gatherDice();
        assertThat(dice).hasSize(2);
        assertThat(dice.get(0).getSides()).isEqualTo(20);
        assertThat(dice.get(1).getSides()).isEqualTo(20);
    }

    @Test
    void testMinMaxDoesNotAffectDroppedDice() {
        ImplicitDicePool pool = new ImplicitDicePool(4, 6);

        KeepDropModifier keepHighest = KeepDropModifier.keepHighest(2);
        keepHighest.decorate(pool);

        MinMaxModifier minMax = MinMaxModifier.minimum(3);
        minMax.decorate(keepHighest);

        // Roll 1, 2, 5, 6. Keep highest 2 (5, 6). The 1 and 2 are dropped, so don't replace them.
        FixedSequenceRoller roller = new FixedSequenceRoller(1, 2, 5, 6);
        Rolled rolled = minMax.roll(roller);

        List<RolledDie> dice = rolled.gatherDice();
        assertThat(dice).hasSize(4); // No replacements because low dice were already dropped
        assertThat(rolled.total()).isEqualTo(11); // 5 + 6
    }

    @Test
    void testToStringMinimum() {
        ImplicitDicePool pool = new ImplicitDicePool(4, 6);
        MinMaxModifier modifier = MinMaxModifier.minimum(3);
        modifier.decorate(pool);

        assertThat(modifier.toString()).contains("min3");
    }

    @Test
    void testToStringMaximum() {
        ImplicitDicePool pool = new ImplicitDicePool(4, 6);
        MinMaxModifier modifier = MinMaxModifier.maximum(5);
        modifier.decorate(pool);

        assertThat(modifier.toString()).contains("max5");
    }

    @Test
    void testToStringBoth() {
        ImplicitDicePool pool = new ImplicitDicePool(4, 10);
        MinMaxModifier min = MinMaxModifier.minimum(3);
        min.decorate(pool);
        MinMaxModifier max = MinMaxModifier.maximum(8);
        max.decorate(min);

        assertThat(max.toString()).contains("min3");
        assertThat(max.toString()).contains("max8");
    }

    @Test
    void testModifiedByIsSet() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 6);
        MinMaxModifier modifier = MinMaxModifier.minimum(3);
        modifier.decorate(pool);

        FixedSequenceRoller roller = new FixedSequenceRoller(2);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled.getModifiedBy()).isEqualTo(modifier);
    }

    @Test
    void testAllDiceInRangeNoChanges() {
        ImplicitDicePool pool = new ImplicitDicePool(4, 6);
        MinMaxModifier min = MinMaxModifier.minimum(2);
        min.decorate(pool);
        MinMaxModifier max = MinMaxModifier.maximum(5);
        max.decorate(min);

        // All rolls are within range
        FixedSequenceRoller roller = new FixedSequenceRoller(2, 3, 4, 5);
        Rolled rolled = max.roll(roller);

        List<RolledDie> dice = rolled.gatherDice();
        assertThat(dice).hasSize(4); // No replacements needed
        assertThat(rolled.total()).isEqualTo(14); // 2 + 3 + 4 + 5
    }
}
