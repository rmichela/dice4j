package io.github.rmichela.dice4j.dice;

import static org.assertj.core.api.Assertions.*;

import io.github.rmichela.dice4j.roll.Rolled;
import io.github.rmichela.dice4j.roll.RolledPool;
import io.github.rmichela.dice4j.roller.FixedSequenceRoller;
import org.junit.jupiter.api.Test;

class DicePoolTest {

    @Test
    void testGetPoolReturnsModifiableList() {
        ImplicitDicePool pool = new ImplicitDicePool(2, 6);

        assertThat(pool.getPool()).isNotNull();
        assertThat(pool.getPool()).hasSize(2);
    }

    @Test
    void testRollablePoolInterface() {
        ExplicitDicePool pool = new ExplicitDicePool();
        pool.add(new Die(6));

        assertThat(pool).isInstanceOf(RollablePool.class);
        assertThat(pool.getPool()).isNotEmpty();
    }

    @Test
    void testRollableInterface() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 6);

        assertThat(pool).isInstanceOf(Rollable.class);
    }

    @Test
    void testRollProducesRolledPool() {
        ImplicitDicePool pool = new ImplicitDicePool(3, 6);
        FixedSequenceRoller roller = new FixedSequenceRoller(1, 2, 3);

        Rolled rolled = pool.roll(roller);

        assertThat(rolled).isInstanceOf(RolledPool.class);
    }

    @Test
    void testRollCollectsAllDice() {
        ExplicitDicePool pool = new ExplicitDicePool();
        pool.add(new Die(6));
        pool.add(new Die(8));
        pool.add(new Die(12));

        FixedSequenceRoller roller = new FixedSequenceRoller(4, 6, 10);
        Rolled rolled = pool.roll(roller);

        RolledPool rolledPool = (RolledPool) rolled;
        assertThat(rolledPool.gather()).hasSize(3);
    }

    @Test
    void testToStringFormat() {
        ExplicitDicePool pool = new ExplicitDicePool();
        pool.add(new Die(6));

        String str = pool.toString();

        assertThat(str).startsWith("{ ");
        assertThat(str).endsWith(" }");
        assertThat(str).contains("d6");
    }

    @Test
    void testToStringWithMultipleItems() {
        ExplicitDicePool pool = new ExplicitDicePool();
        pool.add(new Die(6));
        pool.add(new Die(8));
        pool.add(new Die(10));

        String str = pool.toString();

        assertThat(str).contains(", ");
        assertThat(str).contains("d6");
        assertThat(str).contains("d8");
        assertThat(str).contains("d10");
    }

    @Test
    void testEmptyPoolToString() {
        ExplicitDicePool pool = new ExplicitDicePool();

        assertThat(pool.toString()).isEqualTo("{  }");
    }

    @Test
    void testPoolWithConstantsToString() {
        ExplicitDicePool pool = new ExplicitDicePool();
        pool.add(new Constant(5));
        pool.add(new Constant(-3));

        String str = pool.toString();

        assertThat(str).contains("5");
        assertThat(str).contains("-3");
    }
}
