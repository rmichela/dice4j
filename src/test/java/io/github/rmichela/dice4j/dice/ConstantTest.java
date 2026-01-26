package io.github.rmichela.dice4j.dice;

import static org.assertj.core.api.Assertions.*;

import io.github.rmichela.dice4j.roll.Rolled;
import io.github.rmichela.dice4j.roll.RolledDie;
import io.github.rmichela.dice4j.roller.FixedSequenceRoller;
import org.junit.jupiter.api.Test;

class ConstantTest {

    @Test
    void testRollReturnsConstantValue() {
        Constant constant = new Constant(5);
        FixedSequenceRoller roller = new FixedSequenceRoller(1, 2, 3);

        Rolled rolled = constant.roll(roller);

        assertThat(rolled).isInstanceOf(RolledDie.class);
        RolledDie rolledDie = (RolledDie) rolled;
        assertThat(rolledDie.getValue()).isEqualTo(5);
        assertThat(rolledDie.isConstant()).isTrue();
    }

    @Test
    void testRollMultipleTimesReturnsConstant() {
        Constant constant = new Constant(10);
        FixedSequenceRoller roller = new FixedSequenceRoller(1, 2, 3);

        Rolled roll1 = constant.roll(roller);
        Rolled roll2 = constant.roll(roller);
        Rolled roll3 = constant.roll(roller);

        assertThat(((RolledDie) roll1).getValue()).isEqualTo(10);
        assertThat(((RolledDie) roll2).getValue()).isEqualTo(10);
        assertThat(((RolledDie) roll3).getValue()).isEqualTo(10);
    }

    @Test
    void testPositiveConstant() {
        Constant constant = new Constant(42);
        Rolled rolled = constant.roll(new FixedSequenceRoller(1));

        assertThat(((RolledDie) rolled).getValue()).isEqualTo(42);
    }

    @Test
    void testNegativeConstant() {
        Constant constant = new Constant(-15);
        Rolled rolled = constant.roll(new FixedSequenceRoller(1));

        assertThat(((RolledDie) rolled).getValue()).isEqualTo(-15);
    }

    @Test
    void testZeroConstant() {
        Constant constant = new Constant(0);
        Rolled rolled = constant.roll(new FixedSequenceRoller(1));

        assertThat(((RolledDie) rolled).getValue()).isEqualTo(0);
    }

    @Test
    void testToStringPositive() {
        Constant constant = new Constant(5);
        assertThat(constant.toString()).isEqualTo("5");
    }

    @Test
    void testToStringNegative() {
        Constant constant = new Constant(-10);
        assertThat(constant.toString()).isEqualTo("-10");
    }

    @Test
    void testToStringZero() {
        Constant constant = new Constant(0);
        assertThat(constant.toString()).isEqualTo("0");
    }

    @Test
    void testRollDefaultRoller() {
        Constant constant = new Constant(7);
        Rolled rolled = constant.roll();

        assertThat(rolled).isInstanceOf(RolledDie.class);
        assertThat(((RolledDie) rolled).getValue()).isEqualTo(7);
        assertThat(((RolledDie) rolled).isConstant()).isTrue();
    }
}
