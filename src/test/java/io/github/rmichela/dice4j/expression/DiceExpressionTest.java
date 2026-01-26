package io.github.rmichela.dice4j.expression;

import static org.assertj.core.api.Assertions.*;

import io.github.rmichela.dice4j.dice.*;
import io.github.rmichela.dice4j.dice.modifier.*;
import io.github.rmichela.dice4j.roller.FixedSequenceRoller;
import org.junit.jupiter.api.Test;

class DiceExpressionTest {

    @Test
    void testEvaluateConstant() {
        Rollable result = DiceExpression.evaluate("42");
        assertThat(result).isInstanceOf(Constant.class);

        var roll = result.roll();
        assertThat(roll.total()).isEqualTo(42);
    }

    @Test
    void testEvaluateSimpleDicePool() {
        Rollable result = DiceExpression.evaluate("2d6");
        assertThat(result).isInstanceOf(RollablePool.class);
    }

    @Test
    void testEvaluateSingleDie() {
        Rollable result = DiceExpression.evaluate("1d20");
        assertThat(result).isInstanceOf(ExplicitDicePool.class);
    }

    @Test
    void testEvaluateAddition() {
        Rollable result = DiceExpression.evaluate("1d6 + 1d6");
        assertThat(result).isInstanceOf(BinaryModifier.class);

        FixedSequenceRoller roller = new FixedSequenceRoller(3, 4);
        var roll = result.roll(roller);
        assertThat(roll.total()).isEqualTo(7);
    }

    @Test
    void testEvaluateSubtraction() {
        Rollable result = DiceExpression.evaluate("1d6 - 1d6");

        FixedSequenceRoller roller = new FixedSequenceRoller(5, 2);
        var roll = result.roll(roller);
        assertThat(roll.total()).isEqualTo(3);
    }

    @Test
    void testEvaluateMultiplication() {
        Rollable result = DiceExpression.evaluate("1d6 * 1d6");

        FixedSequenceRoller roller = new FixedSequenceRoller(3, 4);
        var roll = result.roll(roller);
        assertThat(roll.total()).isEqualTo(12);
    }

    @Test
    void testEvaluateDivision() {
        Rollable result = DiceExpression.evaluate("1d6 / 1d6");

        FixedSequenceRoller roller = new FixedSequenceRoller(6, 2);
        var roll = result.roll(roller);
        assertThat(roll.total()).isEqualTo(3);
    }

    @Test
    void testEvaluateUnaryNegate() {
        Rollable result = DiceExpression.evaluate("-5");
        assertThat(result).isInstanceOf(UnaryModifier.class);

        var roll = result.roll();
        assertThat(roll.total()).isEqualTo(-5);
    }

    @Test
    void testEvaluateUnaryNegateWithDice() {
        Rollable result = DiceExpression.evaluate("-(1d6)");

        FixedSequenceRoller roller = new FixedSequenceRoller(4);
        var roll = result.roll(roller);
        assertThat(roll.total()).isEqualTo(-4);
    }

    @Test
    void testEvaluateParentheses() {
        Rollable result = DiceExpression.evaluate("(1d6 + 1d6) * 1d6");

        FixedSequenceRoller roller = new FixedSequenceRoller(2, 3, 4);
        var roll = result.roll(roller);
        assertThat(roll.total()).isEqualTo(20);
    }

    @Test
    void testEvaluateKeepHighest() {
        Rollable result = DiceExpression.evaluate("3d6kh2");

        FixedSequenceRoller roller = new FixedSequenceRoller(1, 2, 6);
        var roll = result.roll(roller);
        assertThat(roll.total()).isEqualTo(8); // Keep 2 and 6
    }

    @Test
    void testEvaluateKeepLowest() {
        Rollable result = DiceExpression.evaluate("3d6kl2");

        FixedSequenceRoller roller = new FixedSequenceRoller(1, 3, 6);
        var roll = result.roll(roller);
        assertThat(roll.total()).isEqualTo(4); // Keep 1 and 3
    }

    @Test
    void testEvaluateDropHighest() {
        Rollable result = DiceExpression.evaluate("3d6dh1");

        FixedSequenceRoller roller = new FixedSequenceRoller(1, 3, 6);
        var roll = result.roll(roller);
        assertThat(roll.total()).isEqualTo(4); // Drop 6, keep 1 and 3
    }

    @Test
    void testEvaluateDropLowest() {
        Rollable result = DiceExpression.evaluate("3d6dl1");

        FixedSequenceRoller roller = new FixedSequenceRoller(1, 3, 6);
        var roll = result.roll(roller);
        assertThat(roll.total()).isEqualTo(9); // Drop 1, keep 3 and 6
    }

    @Test
    void testEvaluateReroll() {
        Rollable result = DiceExpression.evaluate("2d6r1");

        FixedSequenceRoller roller = new FixedSequenceRoller(1, 3, 5);
        var roll = result.roll(roller);
        assertThat(roll.total()).isEqualTo(8); // Reroll 1 once, get 3, then 5
    }

    @Test
    void testEvaluateRerollRecursive() {
        Rollable result = DiceExpression.evaluate("1d6rr1");

        FixedSequenceRoller roller = new FixedSequenceRoller(1, 1, 5);
        var roll = result.roll(roller);
        assertThat(roll.total()).isEqualTo(5); // Keep rerolling 1s until we get 5
    }

    @Test
    void testEvaluateExplicitDicePool() {
        Rollable result = DiceExpression.evaluate("{1d6, 1d6, 1d6}");

        FixedSequenceRoller roller = new FixedSequenceRoller(3, 4, 5);
        var roll = result.roll(roller);
        assertThat(roll.total()).isEqualTo(12);
    }

    @Test
    void testEvaluateComplexExpression() {
        Rollable result = DiceExpression.evaluate("2d6kh1 + 5");

        FixedSequenceRoller roller = new FixedSequenceRoller(4, 3);
        var roll = result.roll(roller);
        assertThat(roll.total()).isEqualTo(9); // Keep 4, add 5
    }

    @Test
    void testEvaluateWithWhitespace() {
        Rollable result = DiceExpression.evaluate("  1d6  +  1d6  ");

        FixedSequenceRoller roller = new FixedSequenceRoller(3, 4);
        var roll = result.roll(roller);
        assertThat(roll.total()).isEqualTo(7);
    }

    @Test
    void testEvaluateInvalidSyntax() {
        assertThatThrownBy(() -> DiceExpression.evaluate("2d"))
                .isInstanceOf(DiceExpressionException.class);
    }

    @Test
    void testEvaluateEmptyExpression() {
        assertThatThrownBy(() -> DiceExpression.evaluate(""))
                .isInstanceOf(DiceExpressionException.class);
    }

    @Test
    void testEvaluateInvalidCharacters() {
        assertThatThrownBy(() -> DiceExpression.evaluate("2d6@"))
                .isInstanceOf(DiceExpressionException.class);
    }

    @Test
    void testEvaluateUnmatchedParentheses() {
        assertThatThrownBy(() -> DiceExpression.evaluate("(2d6 + 3"))
                .isInstanceOf(DiceExpressionException.class);
    }

    @Test
    void testEvaluateMissingOperand() {
        assertThatThrownBy(() -> DiceExpression.evaluate("2d6 +"))
                .isInstanceOf(DiceExpressionException.class);
    }

    @Test
    void testEvaluateOrderOfOperations() {
        Rollable result = DiceExpression.evaluate("2 + 3 * 4");

        var roll = result.roll();
        assertThat(roll.total()).isEqualTo(14); // 2 + (3 * 4) = 14
    }

    @Test
    void testEvaluateOrderOfOperationsWithParentheses() {
        Rollable result = DiceExpression.evaluate("(2 + 3) * 4");

        var roll = result.roll();
        assertThat(roll.total()).isEqualTo(20); // (2 + 3) * 4 = 20
    }

    @Test
    void testEvaluateNestedParentheses() {
        Rollable result = DiceExpression.evaluate("((2 + 3) * 4) - 5");

        var roll = result.roll();
        assertThat(roll.total()).isEqualTo(15);
    }

    @Test
    void testEvaluateRerollWithRelationalGreaterThan() {
        Rollable result = DiceExpression.evaluate("2d6r>5");

        FixedSequenceRoller roller = new FixedSequenceRoller(6, 5, 1);
        var roll = result.roll(roller);
        assertThat(roll.total()).isEqualTo(6); // Reroll 6 once, get 5, then 1
    }

    @Test
    void testEvaluateRerollWithRelationalLessThan() {
        Rollable result = DiceExpression.evaluate("2d6r<3");

        FixedSequenceRoller roller = new FixedSequenceRoller(1, 3, 5);
        var roll = result.roll(roller);
        assertThat(roll.total()).isEqualTo(8); // Reroll 1 once, get 3, then 5
    }

    @Test
    void testEvaluateConstantInDicePool() {
        Rollable result = DiceExpression.evaluate("{5}");
        assertThat(result).isInstanceOf(ExplicitDicePool.class);

        var roll = result.roll();
        assertThat(roll.total()).isEqualTo(5);
    }

    @Test
    void testEvaluateMixedDicePool() {
        Rollable result = DiceExpression.evaluate("{1d6, 5, 1d6}");

        FixedSequenceRoller roller = new FixedSequenceRoller(4, 3);
        var roll = result.roll(roller);
        assertThat(roll.total()).isEqualTo(12); // 4 + 5 + 3
    }

    @Test
    void testEvaluateZeroDice() {
        Rollable result = DiceExpression.evaluate("0d6");
        // 0d6 is optimized to a Constant
        assertThat(result).isInstanceOf(Constant.class);

        var roll = result.roll();
        assertThat(roll.total()).isEqualTo(0);
    }
}
