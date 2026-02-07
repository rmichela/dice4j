package io.github.rmichela.dice4j.dice.modifier;

import static org.assertj.core.api.Assertions.*;

import io.github.rmichela.dice4j.dice.ImplicitDicePool;
import io.github.rmichela.dice4j.roll.Rolled;
import io.github.rmichela.dice4j.roll.RolledDie;
import io.github.rmichela.dice4j.roll.RolledPool;
import io.github.rmichela.dice4j.roller.FixedSequenceRoller;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExplodeModifierTest {

    @Test
    void testSingleDieExplosion() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 6);
        ExplodeModifier modifier = new ExplodeModifier(1);
        modifier.decorate(pool);

        // Roll a 6 (max), should explode to 3
        FixedSequenceRoller roller = new FixedSequenceRoller(6, 3);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(RolledPool.class);
        RolledPool rolledPool = (RolledPool) rolled;
        List<RolledDie> dice = rolledPool.gather();

        assertThat(dice).hasSize(2);
        assertThat(dice.get(0).getValue()).isEqualTo(6);
        assertThat(dice.get(0).isKept()).isTrue();
        assertThat(dice.get(1).getValue()).isEqualTo(3);
        assertThat(dice.get(1).isKept()).isTrue();

        assertThat(rolled.total()).isEqualTo(9); // 6 + 3
    }

    @Test
    void testNoExplosionWhenNotMax() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 6);
        ExplodeModifier modifier = new ExplodeModifier(1);
        modifier.decorate(pool);

        // Roll a 3 (not max), should not explode
        FixedSequenceRoller roller = new FixedSequenceRoller(3);
        Rolled rolled = modifier.roll(roller);

        List<RolledDie> dice = rolled.gather();

        assertThat(dice).hasSize(1);
        assertThat(dice.get(0).getValue()).isEqualTo(3);
        assertThat(rolled.total()).isEqualTo(3);
    }

    @Test
    void testRecursiveExplosion() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 6);
        ExplodeModifier modifier = new ExplodeModifier(ExplodeModifier.RECURSIVE);
        modifier.decorate(pool);

        // Roll 6, explode to 6, explode to 6, explode to 2 (stops)
        FixedSequenceRoller roller = new FixedSequenceRoller(6, 6, 6, 2);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(RolledPool.class);
        List<RolledDie> dice = rolled.gather();

        assertThat(dice).hasSize(4);
        assertThat(dice.get(0).getValue()).isEqualTo(6);
        assertThat(dice.get(1).getValue()).isEqualTo(6);
        assertThat(dice.get(2).getValue()).isEqualTo(6);
        assertThat(dice.get(3).getValue()).isEqualTo(2);

        assertThat(rolled.total()).isEqualTo(20); // 6 + 6 + 6 + 2
    }

    @Test
    void testLimitedExplosionCount() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 6);
        ExplodeModifier modifier = new ExplodeModifier(2);
        modifier.decorate(pool);

        // Roll 6, explode to 6, explode to 6, but limited to 2 explosions
        FixedSequenceRoller roller = new FixedSequenceRoller(6, 6, 6);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(RolledPool.class);
        List<RolledDie> dice = rolled.gather();

        assertThat(dice).hasSize(3); // Original + 2 explosions
        assertThat(dice.get(0).getValue()).isEqualTo(6);
        assertThat(dice.get(1).getValue()).isEqualTo(6);
        assertThat(dice.get(2).getValue()).isEqualTo(6);

        assertThat(rolled.total()).isEqualTo(18);
    }

    @Test
    void testSingleExplosionOnPool() {
        ImplicitDicePool pool = new ImplicitDicePool(3, 6);
        ExplodeModifier modifier = new ExplodeModifier(2);
        modifier.decorate(pool);

        // Roll 6, 3, 6, then explode the two 6s to 4, 5 (qty=2 allows 2 total explosions)
        FixedSequenceRoller roller = new FixedSequenceRoller(6, 3, 6, 4, 5);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(RolledPool.class);
        List<RolledDie> dice = rolled.gather();

        assertThat(dice).hasSize(5); // 3 original + 2 exploded
        assertThat(rolled.total()).isEqualTo(24); // 6 + 3 + 6 + 4 + 5
    }

    @Test
    void testMultipleExplosionsOnPool() {
        ImplicitDicePool pool = new ImplicitDicePool(2, 6);
        ExplodeModifier modifier = new ExplodeModifier(ExplodeModifier.RECURSIVE);
        modifier.decorate(pool);

        // Roll 6, 2, explode the 6 to another 6, explode to 3
        FixedSequenceRoller roller = new FixedSequenceRoller(6, 2, 6, 3);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(RolledPool.class);
        List<RolledDie> dice = rolled.gather();

        assertThat(dice).hasSize(4); // 2 original + 2 explosions
        assertThat(rolled.total()).isEqualTo(17); // 6 + 2 + 6 + 3
    }

    @Test
    void testNoExplosionsInPool() {
        ImplicitDicePool pool = new ImplicitDicePool(4, 6);
        ExplodeModifier modifier = new ExplodeModifier(ExplodeModifier.RECURSIVE);
        modifier.decorate(pool);

        // No max values rolled
        FixedSequenceRoller roller = new FixedSequenceRoller(1, 2, 3, 4);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(RolledPool.class);
        List<RolledDie> dice = rolled.gather();

        assertThat(dice).hasSize(4); // No explosions
        assertThat(rolled.total()).isEqualTo(10); // 1 + 2 + 3 + 4
    }

    @Test
    void testExplosionWithD20() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 20);
        ExplodeModifier modifier = new ExplodeModifier(1);
        modifier.decorate(pool);

        // Roll a 20 (max for d20), should explode to 15
        FixedSequenceRoller roller = new FixedSequenceRoller(20, 15);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(RolledPool.class);
        List<RolledDie> dice = rolled.gather();

        assertThat(dice).hasSize(2);
        assertThat(dice.get(0).getValue()).isEqualTo(20);
        assertThat(dice.get(1).getValue()).isEqualTo(15);
        assertThat(rolled.total()).isEqualTo(35);
    }

    @Test
    void testExplosionPreservesDieSides() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 20);
        ExplodeModifier modifier = new ExplodeModifier(ExplodeModifier.RECURSIVE);
        modifier.decorate(pool);

        // Roll 20, explode to 20, explode to 10
        FixedSequenceRoller roller = new FixedSequenceRoller(20, 20, 10);
        Rolled rolled = modifier.roll(roller);

        List<RolledDie> dice = rolled.gather();
        assertThat(dice).hasSize(3);
        assertThat(dice.get(0).getSides()).isEqualTo(20);
        assertThat(dice.get(1).getSides()).isEqualTo(20);
        assertThat(dice.get(2).getSides()).isEqualTo(20);
    }

    @Test
    void testAllDiceExplode() {
        ImplicitDicePool pool = new ImplicitDicePool(3, 6);
        ExplodeModifier modifier = new ExplodeModifier(3);
        modifier.decorate(pool);

        // All dice roll 6, then explode to 2, 3, 4 (qty=3 allows 3 total explosions)
        FixedSequenceRoller roller = new FixedSequenceRoller(6, 6, 6, 2, 3, 4);
        Rolled rolled = modifier.roll(roller);

        List<RolledDie> dice = rolled.gather();
        assertThat(dice).hasSize(6); // 3 original + 3 explosions
        assertThat(rolled.total()).isEqualTo(27); // 6 + 6 + 6 + 2 + 3 + 4
    }

    @Test
    void testExplosionWithZeroLimit() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 6);
        ExplodeModifier modifier = new ExplodeModifier(0);
        modifier.decorate(pool);

        // Roll a 6 but no explosions allowed
        FixedSequenceRoller roller = new FixedSequenceRoller(6);
        Rolled rolled = modifier.roll(roller);

        List<RolledDie> dice = rolled.gather();
        assertThat(dice).hasSize(1); // No explosions
        assertThat(rolled.total()).isEqualTo(6);
    }

    @Test
    void testToStringRecursive() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 6);
        ExplodeModifier modifier = new ExplodeModifier(ExplodeModifier.RECURSIVE);
        modifier.decorate(pool);

        assertThat(modifier.toString()).contains("x");
        assertThat(modifier.toString()).doesNotContain("x" + ExplodeModifier.RECURSIVE);
    }

    @Test
    void testToStringLimited() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 6);
        ExplodeModifier modifier = new ExplodeModifier(3);
        modifier.decorate(pool);

        assertThat(modifier.toString()).contains("x3");
    }

    @Test
    void testStackedWithKeepModifier() {
        ImplicitDicePool pool = new ImplicitDicePool(3, 6);

        KeepDropModifier keepHighest = KeepDropModifier.keepHighest(2);
        keepHighest.decorate(pool);

        ExplodeModifier explode = new ExplodeModifier(1);
        explode.decorate(keepHighest);

        // Roll 1, 3, 6. Keep highest 2 (3, 6). The 6 explodes to 4.
        FixedSequenceRoller roller = new FixedSequenceRoller(1, 3, 6, 4);
        Rolled rolled = explode.roll(roller);

        List<RolledDie> dice = rolled.gather();
        assertThat(dice).hasSize(4); // 3 original + 1 explosion
        assertThat(rolled.total()).isEqualTo(13); // 3 + 6 + 4 (1 is dropped)
    }

    @Test
    void testChainExplosions() {
        ImplicitDicePool pool = new ImplicitDicePool(2, 6);
        ExplodeModifier modifier = new ExplodeModifier(ExplodeModifier.RECURSIVE);
        modifier.decorate(pool);

        // Roll 6, 6, both explode to 6, 6, both explode again to 1, 1
        FixedSequenceRoller roller = new FixedSequenceRoller(6, 6, 6, 6, 1, 1);
        Rolled rolled = modifier.roll(roller);

        List<RolledDie> dice = rolled.gather();
        assertThat(dice).hasSize(6); // 2 original + 4 explosions
        assertThat(rolled.total()).isEqualTo(26); // 6+6+6+6+1+1
    }

    @Test
    void testModifiedByIsSet() {
        ImplicitDicePool pool = new ImplicitDicePool(1, 6);
        ExplodeModifier modifier = new ExplodeModifier(1);
        modifier.decorate(pool);

        FixedSequenceRoller roller = new FixedSequenceRoller(6, 3);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled.getModifiedBy()).isEqualTo(modifier);
    }
}