package io.github.rmichela.dice4j.dice;

import static org.assertj.core.api.Assertions.*;

import io.github.rmichela.dice4j.roll.Rolled;
import io.github.rmichela.dice4j.roll.RolledDie;
import io.github.rmichela.dice4j.roll.RolledPool;
import io.github.rmichela.dice4j.roller.FixedSequenceRoller;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExplicitDicePoolTest {

    @Test
    void testAddSingleDie() {
        ExplicitDicePool pool = new ExplicitDicePool();
        pool.add(new Die(6));

        assertThat(pool.getPool()).hasSize(1);
    }

    @Test
    void testAddMultipleDice() {
        ExplicitDicePool pool = new ExplicitDicePool();
        pool.add(new Die(6)).add(new Die(8)).add(new Die(20));

        assertThat(pool.getPool()).hasSize(3);
    }

    @Test
    void testAddConstant() {
        ExplicitDicePool pool = new ExplicitDicePool();
        pool.add(new Constant(5));

        assertThat(pool.getPool()).hasSize(1);
        assertThat(pool.getPool().get(0)).isInstanceOf(Constant.class);
    }

    @Test
    void testAddMixedRollables() {
        ExplicitDicePool pool = new ExplicitDicePool();
        pool.add(new Die(6)).add(new Constant(3)).add(new Die(20));

        assertThat(pool.getPool()).hasSize(3);
        assertThat(pool.getPool().get(0)).isInstanceOf(Die.class);
        assertThat(pool.getPool().get(1)).isInstanceOf(Constant.class);
        assertThat(pool.getPool().get(2)).isInstanceOf(Die.class);
    }

    @Test
    void testEmptyPool() {
        ExplicitDicePool pool = new ExplicitDicePool();

        assertThat(pool.getPool()).isEmpty();
    }

    @Test
    void testAddReturnsPool() {
        ExplicitDicePool pool = new ExplicitDicePool();
        ExplicitDicePool result = pool.add(new Die(6));

        assertThat(result).isSameAs(pool);
    }

    @Test
    void testRollWithFixedSequence() {
        ExplicitDicePool pool = new ExplicitDicePool();
        pool.add(new Die(6)).add(new Die(8)).add(new Die(20));

        FixedSequenceRoller roller = new FixedSequenceRoller(3, 5, 15);
        Rolled rolled = pool.roll(roller);

        assertThat(rolled).isInstanceOf(RolledPool.class);
        RolledPool rolledPool = (RolledPool) rolled;
        List<RolledDie> dice = rolledPool.gather();
        assertThat(dice).hasSize(3);
        assertThat(dice.get(0).getValue()).isEqualTo(3);
        assertThat(dice.get(1).getValue()).isEqualTo(5);
        assertThat(dice.get(2).getValue()).isEqualTo(15);
    }

    @Test
    void testRollWithConstant() {
        ExplicitDicePool pool = new ExplicitDicePool();
        pool.add(new Die(6)).add(new Constant(10)).add(new Die(8));

        FixedSequenceRoller roller = new FixedSequenceRoller(2, 7);
        Rolled rolled = pool.roll(roller);

        assertThat(rolled).isInstanceOf(RolledPool.class);
        RolledPool rolledPool = (RolledPool) rolled;
        List<RolledDie> dice = rolledPool.gather();
        assertThat(dice).hasSize(3);
        assertThat(dice.get(0).getValue()).isEqualTo(2);
        assertThat(dice.get(1).getValue()).isEqualTo(10);
        assertThat(dice.get(2).getValue()).isEqualTo(7);
    }

    @Test
    void testRollEmptyPool() {
        ExplicitDicePool pool = new ExplicitDicePool();
        FixedSequenceRoller roller = new FixedSequenceRoller(1);

        Rolled rolled = pool.roll(roller);

        assertThat(rolled).isInstanceOf(RolledPool.class);
        RolledPool rolledPool = (RolledPool) rolled;
        assertThat(rolledPool.gather()).isEmpty();
    }

    @Test
    void testRollDefaultRoller() {
        ExplicitDicePool pool = new ExplicitDicePool();
        pool.add(new Die(6)).add(new Die(6));

        Rolled rolled = pool.roll();

        assertThat(rolled).isInstanceOf(RolledPool.class);
        RolledPool rolledPool = (RolledPool) rolled;
        List<RolledDie> dice = rolledPool.gather();
        assertThat(dice).hasSize(2);
        assertThat(dice).allMatch(die -> die.getValue() >= 1 && die.getValue() <= 6);
    }

    @Test
    void testToStringEmpty() {
        ExplicitDicePool pool = new ExplicitDicePool();

        assertThat(pool.toString()).isEqualTo("{  }");
    }

    @Test
    void testToStringWithDice() {
        ExplicitDicePool pool = new ExplicitDicePool();
        pool.add(new Die(6));
        pool.add(new Die(8));

        assertThat(pool.toString()).isEqualTo("{ d6, d8 }");
    }

    @Test
    void testToStringWithMixed() {
        ExplicitDicePool pool = new ExplicitDicePool();
        pool.add(new Die(6)).add(new Constant(5)).add(new Die(20));

        assertThat(pool.toString()).isEqualTo("{ d6, 5, d20 }");
    }

    @Test
    void testAddNestedPool() {
        ExplicitDicePool innerPool = new ExplicitDicePool();
        innerPool.add(new Die(6)).add(new Die(8));

        ExplicitDicePool outerPool = new ExplicitDicePool();
        outerPool.add(new Die(20)).add(innerPool);

        assertThat(outerPool.getPool()).hasSize(2);
        assertThat(outerPool.getPool().get(1)).isInstanceOf(ExplicitDicePool.class);
    }

    @Test
    void testRollNestedPool() {
        ExplicitDicePool innerPool = new ExplicitDicePool();
        innerPool.add(new Die(6)).add(new Die(8));

        ExplicitDicePool outerPool = new ExplicitDicePool();
        outerPool.add(new Die(20)).add(innerPool);

        FixedSequenceRoller roller = new FixedSequenceRoller(15, 3, 5);
        Rolled rolled = outerPool.roll(roller);

        assertThat(rolled).isInstanceOf(RolledPool.class);
        RolledPool rolledPool = (RolledPool) rolled;
        assertThat(rolledPool.getRolls()).hasSize(2);

        Rolled firstRoll = rolledPool.getRolls().get(0);
        assertThat(firstRoll).isInstanceOf(RolledDie.class);
        assertThat(((RolledDie) firstRoll).getValue()).isEqualTo(15);

        Rolled secondRoll = rolledPool.getRolls().get(1);
        assertThat(secondRoll).isInstanceOf(RolledPool.class);
        RolledPool nestedPool = (RolledPool) secondRoll;
        List<RolledDie> nestedDice = nestedPool.gather();
        assertThat(nestedDice).hasSize(2);
        assertThat(nestedDice.get(0).getValue()).isEqualTo(3);
        assertThat(nestedDice.get(1).getValue()).isEqualTo(5);
    }
}
