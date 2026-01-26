package io.github.rmichela.dice4j.dice.modifier;

import static org.assertj.core.api.Assertions.*;

import io.github.rmichela.dice4j.dice.Constant;
import io.github.rmichela.dice4j.dice.Die;
import io.github.rmichela.dice4j.expression.BinaryOperation;
import io.github.rmichela.dice4j.roll.BinaryOperator;
import io.github.rmichela.dice4j.roll.Rolled;
import io.github.rmichela.dice4j.roller.FixedSequenceRoller;
import org.junit.jupiter.api.Test;

class BinaryModifierTest {

    @Test
    void testAdditionWithConstants() {
        Constant left = new Constant(5);
        Constant right = new Constant(3);
        BinaryModifier modifier = new BinaryModifier(BinaryOperation.ADD, left, right);

        FixedSequenceRoller roller = new FixedSequenceRoller(1);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(BinaryOperator.class);
        assertThat(rolled.total()).isEqualTo(8);
    }

    @Test
    void testSubtractionWithConstants() {
        Constant left = new Constant(10);
        Constant right = new Constant(4);
        BinaryModifier modifier = new BinaryModifier(BinaryOperation.SUBTRACT, left, right);

        FixedSequenceRoller roller = new FixedSequenceRoller(1);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(BinaryOperator.class);
        assertThat(rolled.total()).isEqualTo(6);
    }

    @Test
    void testMultiplicationWithConstants() {
        Constant left = new Constant(6);
        Constant right = new Constant(7);
        BinaryModifier modifier = new BinaryModifier(BinaryOperation.MULTIPLY, left, right);

        FixedSequenceRoller roller = new FixedSequenceRoller(1);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(BinaryOperator.class);
        assertThat(rolled.total()).isEqualTo(42);
    }

    @Test
    void testDivisionWithConstants() {
        Constant left = new Constant(15);
        Constant right = new Constant(3);
        BinaryModifier modifier = new BinaryModifier(BinaryOperation.DIVIDE, left, right);

        FixedSequenceRoller roller = new FixedSequenceRoller(1);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(BinaryOperator.class);
        assertThat(rolled.total()).isEqualTo(5);
    }

    @Test
    void testWithDiceRolls() {
        Die left = new Die(6);
        Die right = new Die(6);
        BinaryModifier modifier = new BinaryModifier(BinaryOperation.ADD, left, right);

        FixedSequenceRoller roller = new FixedSequenceRoller(4, 5);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(BinaryOperator.class);
        assertThat(rolled.total()).isEqualTo(9);
    }

    @Test
    void testMixedDiceAndConstant() {
        Die left = new Die(20);
        Constant right = new Constant(5);
        BinaryModifier modifier = new BinaryModifier(BinaryOperation.ADD, left, right);

        FixedSequenceRoller roller = new FixedSequenceRoller(15);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(BinaryOperator.class);
        assertThat(rolled.total()).isEqualTo(20);
    }

    @Test
    void testToStringWithAddition() {
        Constant left = new Constant(5);
        Constant right = new Constant(3);
        BinaryModifier modifier = new BinaryModifier(BinaryOperation.ADD, left, right);

        assertThat(modifier.toString()).isEqualTo("5 + 3");
    }

    @Test
    void testToStringWithSubtraction() {
        Die left = new Die(6);
        Constant right = new Constant(2);
        BinaryModifier modifier = new BinaryModifier(BinaryOperation.SUBTRACT, left, right);

        assertThat(modifier.toString()).isEqualTo("d6 - 2");
    }

    @Test
    void testToStringWithMultiplication() {
        Die left = new Die(4);
        Constant right = new Constant(3);
        BinaryModifier modifier = new BinaryModifier(BinaryOperation.MULTIPLY, left, right);

        assertThat(modifier.toString()).isEqualTo("d4 * 3");
    }

    @Test
    void testToStringWithDivision() {
        Die left = new Die(20);
        Constant right = new Constant(2);
        BinaryModifier modifier = new BinaryModifier(BinaryOperation.DIVIDE, left, right);

        assertThat(modifier.toString()).isEqualTo("d20 / 2");
    }
}
