package io.github.rmichela.dice4j.dice.modifier;

import static org.assertj.core.api.Assertions.*;

import io.github.rmichela.dice4j.dice.Constant;
import io.github.rmichela.dice4j.dice.Die;
import io.github.rmichela.dice4j.expression.UnaryOperation;
import io.github.rmichela.dice4j.roll.Rolled;
import io.github.rmichela.dice4j.roll.UnaryOperator;
import io.github.rmichela.dice4j.roller.FixedSequenceRoller;
import org.junit.jupiter.api.Test;

class UnaryModifierTest {

    @Test
    void testNegateConstant() {
        Constant constant = new Constant(5);
        UnaryModifier modifier = new UnaryModifier(UnaryOperation.NEGATE, constant);

        FixedSequenceRoller roller = new FixedSequenceRoller(1);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(UnaryOperator.class);
        assertThat(rolled.total()).isEqualTo(-5);
    }

    @Test
    void testNegatePositiveDieRoll() {
        Die die = new Die(6);
        UnaryModifier modifier = new UnaryModifier(UnaryOperation.NEGATE, die);

        FixedSequenceRoller roller = new FixedSequenceRoller(4);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(UnaryOperator.class);
        assertThat(rolled.total()).isEqualTo(-4);
    }

    @Test
    void testNegateZero() {
        Constant constant = new Constant(0);
        UnaryModifier modifier = new UnaryModifier(UnaryOperation.NEGATE, constant);

        FixedSequenceRoller roller = new FixedSequenceRoller(1);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(UnaryOperator.class);
        assertThat(rolled.total()).isEqualTo(0);
    }

    @Test
    void testNegateNegativeConstant() {
        Constant constant = new Constant(-10);
        UnaryModifier modifier = new UnaryModifier(UnaryOperation.NEGATE, constant);

        FixedSequenceRoller roller = new FixedSequenceRoller(1);
        Rolled rolled = modifier.roll(roller);

        assertThat(rolled).isInstanceOf(UnaryOperator.class);
        assertThat(rolled.total()).isEqualTo(10);
    }

    @Test
    void testToStringWithConstant() {
        Constant constant = new Constant(7);
        UnaryModifier modifier = new UnaryModifier(UnaryOperation.NEGATE, constant);

        assertThat(modifier.toString()).isEqualTo("-7");
    }

    @Test
    void testToStringWithDie() {
        Die die = new Die(20);
        UnaryModifier modifier = new UnaryModifier(UnaryOperation.NEGATE, die);

        assertThat(modifier.toString()).isEqualTo("-d20");
    }
}
