package io.github.rmichela.dice4j.dice.modifier;

import static org.assertj.core.api.Assertions.*;

import io.github.rmichela.dice4j.dice.ImplicitDicePool;
import io.github.rmichela.dice4j.expression.RelationalOperation;
import io.github.rmichela.dice4j.roll.Rolled;
import io.github.rmichela.dice4j.roll.RolledDie;
import io.github.rmichela.dice4j.roll.RolledPool;
import io.github.rmichela.dice4j.roller.FixedSequenceRoller;
import java.util.List;
import org.junit.jupiter.api.Test;

class RerollModifierTest {

    @Test
    void testRerollLessThanOnSingleDie() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 6);
        RerollModifier modifier = new RerollModifier(RelationalOperation.LESS_THAN, 3, false);
        modifier.decorate(pool);

        // Roll a 2 (less than 3), should reroll to 5
        FixedSequenceRoller roller = new FixedSequenceRoller(2, 5);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(RolledPool.class);
        RolledPool rolledPool = (RolledPool) rolled;
        List<RolledDie> dice = rolledPool.gatherDice();

        assertThat(dice).hasSize(2);
        assertThat(dice.get(0).getValue()).isEqualTo(2);
        assertThat(dice.get(0).isKept()).isFalse(); // Original die is dropped
        assertThat(dice.get(1).getValue()).isEqualTo(5);
        assertThat(dice.get(1).isKept()).isTrue(); // Rerolled die is kept

        assertThat(rolled.total()).isEqualTo(5);
    }

    @Test
    void testRerollGreaterThanOnSingleDie() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 6);
        RerollModifier modifier = new RerollModifier(RelationalOperation.GREATER_THAN, 4, false);
        modifier.decorate(pool);

        // Roll a 5 (greater than 4), should reroll to 3
        FixedSequenceRoller roller = new FixedSequenceRoller(5, 3);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(RolledPool.class);
        List<RolledDie> dice = rolled.gatherDice();

        assertThat(dice).hasSize(2);
        assertThat(dice.get(0).getValue()).isEqualTo(5);
        assertThat(dice.get(0).isKept()).isFalse();
        assertThat(dice.get(1).getValue()).isEqualTo(3);
        assertThat(dice.get(1).isKept()).isTrue();

        assertThat(rolled.total()).isEqualTo(3);
    }

    @Test
    void testRerollEqualOnSingleDie() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 20);
        RerollModifier modifier = new RerollModifier(RelationalOperation.EQUAL, 1, false);
        modifier.decorate(pool);

        // Roll a 1, should reroll to 15
        FixedSequenceRoller roller = new FixedSequenceRoller(1, 15);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(RolledPool.class);
        List<RolledDie> dice = rolled.gatherDice();

        assertThat(dice).hasSize(2);
        assertThat(dice.get(0).getValue()).isEqualTo(1);
        assertThat(dice.get(0).isKept()).isFalse();
        assertThat(dice.get(1).getValue()).isEqualTo(15);
        assertThat(dice.get(1).isKept()).isTrue();

        assertThat(rolled.total()).isEqualTo(15);
    }

    @Test
    void testNoRerollWhenConditionNotMet() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 6);
        RerollModifier modifier = new RerollModifier(RelationalOperation.EQUAL, 1, false);
        modifier.decorate(pool);

        // Roll a 4, condition not met, no reroll
        FixedSequenceRoller roller = new FixedSequenceRoller(4);
        Rolled rolled = modifier.roll(roller);

        List<RolledDie> dice = rolled.gatherDice();
        assertThat(dice).hasSize(1);
        assertThat(dice.get(0).getValue()).isEqualTo(4);
        assertThat(dice.get(0).isKept()).isTrue();
        assertThat(rolled.total()).isEqualTo(4);
    }

    @Test
    void testRecursiveReroll() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 6);
        RerollModifier modifier = new RerollModifier(RelationalOperation.LESS_THAN, 3, true);
        modifier.decorate(pool);

        // Roll 2, reroll to 1, reroll to 5 (stops because 5 >= 3)
        FixedSequenceRoller roller = new FixedSequenceRoller(2, 1, 5);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(RolledPool.class);
        List<RolledDie> dice = rolled.gatherDice();

        assertThat(dice).hasSize(3);
        assertThat(dice.get(0).getValue()).isEqualTo(2);
        assertThat(dice.get(0).isKept()).isFalse();
        assertThat(dice.get(1).getValue()).isEqualTo(1);
        assertThat(dice.get(1).isKept()).isFalse();
        assertThat(dice.get(2).getValue()).isEqualTo(5);
        assertThat(dice.get(2).isKept()).isTrue();

        assertThat(rolled.total()).isEqualTo(5);
    }

    @Test
    void testNonRecursiveRerollStopsAfterFirstReroll() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 6);
        RerollModifier modifier = new RerollModifier(RelationalOperation.LESS_THAN, 3, false);
        modifier.decorate(pool);

        // Roll 2, reroll to 1, but stops because non-recursive
        FixedSequenceRoller roller = new FixedSequenceRoller(2, 1);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(RolledPool.class);
        List<RolledDie> dice = rolled.gatherDice();

        assertThat(dice).hasSize(2);
        assertThat(dice.get(0).getValue()).isEqualTo(2);
        assertThat(dice.get(0).isKept()).isFalse();
        assertThat(dice.get(1).getValue()).isEqualTo(1);
        assertThat(dice.get(1).isKept()).isTrue();

        assertThat(rolled.total()).isEqualTo(1);
    }

    @Test
    void testRerollOnDicePool() {
        ImplicitDicePool pool = new ImplicitDicePool(3, 6);
        RerollModifier modifier = new RerollModifier(RelationalOperation.EQUAL, 1, false);
        modifier.decorate(pool);

        // Roll 1, 3, 1, then reroll the two 1s to 4, 5
        FixedSequenceRoller roller = new FixedSequenceRoller(1, 3, 1, 4, 5);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(RolledPool.class);
        List<RolledDie> dice = rolled.gatherDice();

        assertThat(dice).hasSize(5); // 3 original + 2 rerolled
        assertThat(rolled.total()).isEqualTo(12); // 3 + 4 + 5
    }

    @Test
    void testRerollMultipleDiceInPool() {
        ImplicitDicePool pool = new ImplicitDicePool(4, 6);
        RerollModifier modifier = new RerollModifier(RelationalOperation.LESS_THAN, 3, false);
        modifier.decorate(pool);

        // Roll 1, 2, 5, 6, reroll 1 and 2 to 4 and 3
        FixedSequenceRoller roller = new FixedSequenceRoller(1, 2, 5, 6, 4, 3);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(RolledPool.class);
        List<RolledDie> dice = rolled.gatherDice();

        assertThat(dice).hasSize(6); // 4 original + 2 rerolled
        assertThat(rolled.total()).isEqualTo(18); // 4 + 3 + 5 + 6
    }

    @Test
    void testRecursiveRerollOnPool() {
        ImplicitDicePool pool = new ImplicitDicePool(2, 6);
        RerollModifier modifier = new RerollModifier(RelationalOperation.EQUAL, 1, true);
        modifier.decorate(pool);

        // Roll 1, 5, reroll the 1 to another 1, reroll again to 3
        FixedSequenceRoller roller = new FixedSequenceRoller(1, 5, 1, 3);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(RolledPool.class);
        List<RolledDie> dice = rolled.gatherDice();

        assertThat(dice).hasSize(4); // 2 original + 2 rerolled
        assertThat(rolled.total()).isEqualTo(8); // 3 + 5
    }

    @Test
    void testRerollDoesNotAffectDroppedDice() {
        ImplicitDicePool pool = new ImplicitDicePool(3, 6);

        KeepDropModifier keepHighest = KeepDropModifier.keepHighest(2);
        keepHighest.decorate(pool);

        RerollModifier reroll = new RerollModifier(RelationalOperation.EQUAL, 1, false);
        reroll.decorate(keepHighest);

        // Roll 1, 3, 5. Keep highest 2 (3, 5). The 1 is already dropped, so don't reroll it.
        FixedSequenceRoller roller = new FixedSequenceRoller(1, 3, 5);
        Rolled rolled = reroll.roll(roller);

        List<RolledDie> dice = rolled.gatherDice();
        assertThat(dice).hasSize(3); // No reroll because 1 was dropped
        assertThat(rolled.total()).isEqualTo(8); // 3 + 5
    }

    @Test
    void testToStringNonRecursive() {
        ImplicitDicePool pool = new ImplicitDicePool(4, 6);
        RerollModifier modifier = new RerollModifier(RelationalOperation.EQUAL, 1, false);
        modifier.decorate(pool);

        assertThat(modifier.toString()).contains("r=1");
    }

    @Test
    void testToStringRecursive() {
        ImplicitDicePool pool = new ImplicitDicePool(4, 6);
        RerollModifier modifier = new RerollModifier(RelationalOperation.LESS_THAN, 3, true);
        modifier.decorate(pool);

        assertThat(modifier.toString()).contains("rr");
    }

    @Test
    void testToStringGreaterThan() {
        ImplicitDicePool pool = new ImplicitDicePool(4, 6);
        RerollModifier modifier = new RerollModifier(RelationalOperation.GREATER_THAN, 5, false);
        modifier.decorate(pool);

        assertThat(modifier.toString()).contains("r>5");
    }

    @Test
    void testRerollPreservesDieSides() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 20);
        RerollModifier modifier = new RerollModifier(RelationalOperation.EQUAL, 1, false);
        modifier.decorate(pool);

        FixedSequenceRoller roller = new FixedSequenceRoller(1, 15);
        Rolled rolled = modifier.roll(roller);

        List<RolledDie> dice = rolled.gatherDice();
        assertThat(dice).hasSize(2);
        assertThat(dice.get(0).getSides()).isEqualTo(20);
        assertThat(dice.get(1).getSides()).isEqualTo(20);
    }
}
