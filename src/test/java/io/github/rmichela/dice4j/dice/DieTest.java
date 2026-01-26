package io.github.rmichela.dice4j.dice;

import static org.assertj.core.api.Assertions.*;

import io.github.rmichela.dice4j.roll.Rolled;
import io.github.rmichela.dice4j.roll.RolledDie;
import io.github.rmichela.dice4j.roller.FixedSequenceRoller;
import org.junit.jupiter.api.Test;

class DieTest {

    @Test
    void testRollWithFixedSequence() {
        Die die = new Die(6);
        FixedSequenceRoller roller = new FixedSequenceRoller(3);

        Rolled rolled = die.roll(roller);

        assertThat(rolled).isInstanceOf(RolledDie.class);
        RolledDie rolledDie = (RolledDie) rolled;
        assertThat(rolledDie.getSides()).isEqualTo(6);
        assertThat(rolledDie.getValue()).isEqualTo(3);
    }

    @Test
    void testRollWithMultipleValues() {
        Die die = new Die(20);
        FixedSequenceRoller roller = new FixedSequenceRoller(1, 20, 10);

        Rolled roll1 = die.roll(roller);
        Rolled roll2 = die.roll(roller);
        Rolled roll3 = die.roll(roller);

        assertThat(((RolledDie) roll1).getValue()).isEqualTo(1);
        assertThat(((RolledDie) roll2).getValue()).isEqualTo(20);
        assertThat(((RolledDie) roll3).getValue()).isEqualTo(10);
    }

    @Test
    void testRollWithDifferentSides() {
        FixedSequenceRoller roller = new FixedSequenceRoller(4);

        Die d4 = new Die(4);
        Die d6 = new Die(6);
        Die d8 = new Die(8);
        Die d10 = new Die(10);
        Die d12 = new Die(12);
        Die d20 = new Die(20);
        Die d100 = new Die(100);

        assertThat(((RolledDie) d4.roll(roller)).getSides()).isEqualTo(4);
        assertThat(((RolledDie) d6.roll(roller)).getSides()).isEqualTo(6);
        assertThat(((RolledDie) d8.roll(roller)).getSides()).isEqualTo(8);
        assertThat(((RolledDie) d10.roll(roller)).getSides()).isEqualTo(10);
        assertThat(((RolledDie) d12.roll(roller)).getSides()).isEqualTo(12);
        assertThat(((RolledDie) d20.roll(roller)).getSides()).isEqualTo(20);
        assertThat(((RolledDie) d100.roll(roller)).getSides()).isEqualTo(100);
    }

    @Test
    void testToStringD6() {
        Die die = new Die(6);
        assertThat(die.toString()).isEqualTo("d6");
    }

    @Test
    void testToStringD20() {
        Die die = new Die(20);
        assertThat(die.toString()).isEqualTo("d20");
    }

    @Test
    void testToStringD100() {
        Die die = new Die(100);
        assertThat(die.toString()).isEqualTo("d100");
    }

    @Test
    void testRollDefaultRoller() {
        Die die = new Die(6);
        Rolled rolled = die.roll();

        assertThat(rolled).isInstanceOf(RolledDie.class);
        RolledDie rolledDie = (RolledDie) rolled;
        assertThat(rolledDie.getSides()).isEqualTo(6);
        assertThat(rolledDie.getValue()).isBetween(1, 6);
    }
}
