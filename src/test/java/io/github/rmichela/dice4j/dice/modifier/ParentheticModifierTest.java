package io.github.rmichela.dice4j.dice.modifier;

import static org.assertj.core.api.Assertions.*;

import io.github.rmichela.dice4j.dice.Constant;
import io.github.rmichela.dice4j.dice.Die;
import io.github.rmichela.dice4j.expression.BinaryOperation;
import io.github.rmichela.dice4j.roll.ParentheticOperator;
import io.github.rmichela.dice4j.roll.Rolled;
import io.github.rmichela.dice4j.roller.FixedSequenceRoller;
import org.junit.jupiter.api.Test;

class ParentheticModifierTest {

    @Test
    void testParentheticWithConstant() {
        Constant constant = new Constant(5);
        ParentheticModifier modifier = new ParentheticModifier(constant);

        FixedSequenceRoller roller = new FixedSequenceRoller(1);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(ParentheticOperator.class);
        assertThat(rolled.total()).isEqualTo(5);
    }

    @Test
    void testParentheticWithDie() {
        Die die = new Die(6);
        ParentheticModifier modifier = new ParentheticModifier(die);

        FixedSequenceRoller roller = new FixedSequenceRoller(4);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(ParentheticOperator.class);
        assertThat(rolled.total()).isEqualTo(4);
    }

    @Test
    void testParentheticWithBinaryOperation() {
        Constant left = new Constant(3);
        Constant right = new Constant(4);
        BinaryModifier binary = new BinaryModifier(BinaryOperation.ADD, left, right);
        ParentheticModifier modifier = new ParentheticModifier(binary);

        FixedSequenceRoller roller = new FixedSequenceRoller(1);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(ParentheticOperator.class);
        assertThat(rolled.total()).isEqualTo(7);
    }

    @Test
    void testParentheticWithComplexExpression() {
        Die die = new Die(20);
        Constant bonus = new Constant(5);
        BinaryModifier binary = new BinaryModifier(BinaryOperation.ADD, die, bonus);
        ParentheticModifier modifier = new ParentheticModifier(binary);

        FixedSequenceRoller roller = new FixedSequenceRoller(15);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(ParentheticOperator.class);
        assertThat(rolled.total()).isEqualTo(20);
    }

    @Test
    void testToStringWithConstant() {
        Constant constant = new Constant(7);
        ParentheticModifier modifier = new ParentheticModifier(constant);

        assertThat(modifier.toString()).isEqualTo("(7)");
    }

    @Test
    void testToStringWithDie() {
        Die die = new Die(8);
        ParentheticModifier modifier = new ParentheticModifier(die);

        assertThat(modifier.toString()).isEqualTo("(d8)");
    }

    @Test
    void testToStringWithBinaryOperation() {
        Constant left = new Constant(3);
        Constant right = new Constant(4);
        BinaryModifier binary = new BinaryModifier(BinaryOperation.MULTIPLY, left, right);
        ParentheticModifier modifier = new ParentheticModifier(binary);

        assertThat(modifier.toString()).isEqualTo("(3 * 4)");
    }

    @Test
    void testNestedParenthetics() {
        Constant constant = new Constant(10);
        ParentheticModifier inner = new ParentheticModifier(constant);
        ParentheticModifier outer = new ParentheticModifier(inner);

        FixedSequenceRoller roller = new FixedSequenceRoller(1);
        Rolled rolled = outer.roll(roller);

        assertThat(rolled).isInstanceOf(ParentheticOperator.class);
        assertThat(rolled.total()).isEqualTo(10);
        assertThat(outer.toString()).isEqualTo("((10))");
    }
}
