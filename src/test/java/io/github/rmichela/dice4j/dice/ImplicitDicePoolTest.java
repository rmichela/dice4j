package io.github.rmichela.dice4j.dice;

import static org.assertj.core.api.Assertions.*;

import io.github.rmichela.dice4j.roll.Rolled;
import io.github.rmichela.dice4j.roll.RolledDie;
import io.github.rmichela.dice4j.roll.RolledPool;
import io.github.rmichela.dice4j.roller.FixedSequenceRoller;
import java.util.List;
import org.junit.jupiter.api.Test;

class ImplicitDicePoolTest {

    @Test
    void testConstructorCreatesCorrectNumberOfDice() {
        ImplicitDicePool pool = new ImplicitDicePool(3, 6);

        assertThat(pool.getPool()).hasSize(3);
        assertThat(pool.getPool()).allMatch(rollable -> rollable instanceof Die);
    }

    @Test
    void testSingleDie() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 20);

        assertThat(pool.getPool()).hasSize(1);
    }

    @Test
    void testMultipleDice() {
        ImplicitDicePool pool = new ImplicitDicePool(5, 8);

        assertThat(pool.getPool()).hasSize(5);
    }

    @Test
    void testEmptyPool() {
        ImplicitDicePool pool = new ImplicitDicePool(0, 6);

        assertThat(pool.getPool()).isEmpty();
    }

    @Test
    void testRollWithFixedSequence() {
        ImplicitDicePool pool = new ImplicitDicePool(3, 6);
        FixedSequenceRoller roller = new FixedSequenceRoller(1, 3, 5);

        Rolled rolled = pool.roll(roller);

        assertThat(rolled).isInstanceOf(RolledPool.class);
        RolledPool rolledPool = (RolledPool) rolled;
        List<RolledDie> dice = rolledPool.gatherDice();
        assertThat(dice).hasSize(3);
        assertThat(dice.get(0).getValue()).isEqualTo(1);
        assertThat(dice.get(1).getValue()).isEqualTo(3);
        assertThat(dice.get(2).getValue()).isEqualTo(5);
    }

    @Test
    void testRollEachDieGetsValue() {
        ImplicitDicePool pool = new ImplicitDicePool(4, 10);
        FixedSequenceRoller roller = new FixedSequenceRoller(2, 7, 4, 9);

        Rolled rolled = pool.roll(roller);

        assertThat(rolled).isInstanceOf(RolledPool.class);
        RolledPool rolledPool = (RolledPool) rolled;
        assertThat(rolledPool.gatherDice())
                .extracting(RolledDie::getValue)
                .containsExactly(2, 7, 4, 9);
    }

    @Test
    void testRollEmptyPool() {
        ImplicitDicePool pool = new ImplicitDicePool(0, 6);
        FixedSequenceRoller roller = new FixedSequenceRoller(1);

        Rolled rolled = pool.roll(roller);

        assertThat(rolled).isInstanceOf(RolledPool.class);
        RolledPool rolledPool = (RolledPool) rolled;
        assertThat(rolledPool.gatherDice()).isEmpty();
    }

    @Test
    void testRollDefaultRoller() {
        ImplicitDicePool pool = new ImplicitDicePool(2, 6);

        Rolled rolled = pool.roll();

        assertThat(rolled).isInstanceOf(RolledPool.class);
        RolledPool rolledPool = (RolledPool) rolled;
        List<RolledDie> dice = rolledPool.gatherDice();
        assertThat(dice).hasSize(2);
        assertThat(dice).allMatch(die -> die.getValue() >= 1 && die.getValue() <= 6);
    }

    @Test
    void testToStringWithMultipleDice() {
        ImplicitDicePool pool = new ImplicitDicePool(3, 6);

        assertThat(pool.toString()).isEqualTo("{ d6, d6, d6 }");
    }

    @Test
    void testToStringWithSingleDie() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 20);

        assertThat(pool.toString()).isEqualTo("{ d20 }");
    }

    @Test
    void testToStringWithEmptyPool() {
        ImplicitDicePool pool = new ImplicitDicePool(0, 6);

        assertThat(pool.toString()).isEqualTo("{  }");
    }

    @Test
    void testDifferentDieSizes() {
        ImplicitDicePool d4Pool = new ImplicitDicePool(2, 4);
        ImplicitDicePool d8Pool = new ImplicitDicePool(2, 8);
        ImplicitDicePool d20Pool = new ImplicitDicePool(2, 20);

        assertThat(d4Pool.toString()).isEqualTo("{ d4, d4 }");
        assertThat(d8Pool.toString()).isEqualTo("{ d8, d8 }");
        assertThat(d20Pool.toString()).isEqualTo("{ d20, d20 }");
    }
}
