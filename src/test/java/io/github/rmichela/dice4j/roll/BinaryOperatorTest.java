package io.github.rmichela.dice4j.roll;

import static org.assertj.core.api.Assertions.*;

import io.github.rmichela.dice4j.expression.BinaryOperation;
import org.junit.jupiter.api.Test;

class BinaryOperatorTest {

    @Test
    void testAddition() {
        RolledDie left = new RolledDie(6, 3);
        RolledDie right = new RolledDie(6, 4);
        BinaryOperator operator = new BinaryOperator(BinaryOperation.ADD, left, right);

        assertThat(operator.total()).isEqualTo(7);
    }

    @Test
    void testSubtraction() {
        RolledDie left = new RolledDie(6, 5);
        RolledDie right = new RolledDie(6, 2);
        BinaryOperator operator = new BinaryOperator(BinaryOperation.SUBTRACT, left, right);

        assertThat(operator.total()).isEqualTo(3);
    }

    @Test
    void testSubtractionNegativeResult() {
        RolledDie left = new RolledDie(6, 2);
        RolledDie right = new RolledDie(6, 5);
        BinaryOperator operator = new BinaryOperator(BinaryOperation.SUBTRACT, left, right);

        assertThat(operator.total()).isEqualTo(-3);
    }

    @Test
    void testMultiplication() {
        RolledDie left = new RolledDie(6, 3);
        RolledDie right = new RolledDie(6, 4);
        BinaryOperator operator = new BinaryOperator(BinaryOperation.MULTIPLY, left, right);

        assertThat(operator.total()).isEqualTo(12);
    }

    @Test
    void testDivision() {
        RolledDie left = new RolledDie(6, 6);
        RolledDie right = new RolledDie(6, 2);
        BinaryOperator operator = new BinaryOperator(BinaryOperation.DIVIDE, left, right);

        assertThat(operator.total()).isEqualTo(3);
    }

    @Test
    void testDivisionRoundsDown() {
        RolledDie left = new RolledDie(6, 5);
        RolledDie right = new RolledDie(6, 2);
        BinaryOperator operator = new BinaryOperator(BinaryOperation.DIVIDE, left, right);

        assertThat(operator.total()).isEqualTo(2);
    }

    @Test
    void testWithRolledPools() {
        RolledDie die1 = new RolledDie(6, 2);
        RolledDie die2 = new RolledDie(6, 3);
        RolledPool left = new RolledPool(die1, die2);

        RolledDie die3 = new RolledDie(6, 4);
        RolledDie die4 = new RolledDie(6, 1);
        RolledPool right = new RolledPool(die3, die4);

        BinaryOperator operator = new BinaryOperator(BinaryOperation.ADD, left, right);

        assertThat(operator.total()).isEqualTo(10);
    }

    @Test
    void testWithDroppedDice() {
        RolledDie die1 = new RolledDie(6, 3);
        RolledDie die2 = new RolledDie(6, 2);
        die2.drop();
        RolledPool left = new RolledPool(die1, die2);

        RolledDie right = new RolledDie(6, 4);

        BinaryOperator operator = new BinaryOperator(BinaryOperation.ADD, left, right);

        assertThat(operator.total()).isEqualTo(7);
    }

    @Test
    void testGatherDiceReturnsConstantDie() {
        RolledDie left = new RolledDie(6, 3);
        RolledDie right = new RolledDie(6, 4);
        BinaryOperator operator = new BinaryOperator(BinaryOperation.ADD, left, right);

        var gathered = operator.gatherDice();
        assertThat(gathered).hasSize(1);
        RolledDie result = gathered.get(0);
        assertThat(result.isConstant()).isTrue();
        assertThat(result.getValue()).isEqualTo(7);
    }

    @Test
    void testToStringWithDifferentOperations() {
        RolledDie left = new RolledDie(6, 5);
        RolledDie right = new RolledDie(6, 2);

        BinaryOperator add = new BinaryOperator(BinaryOperation.ADD, left, right);
        assertThat(add.toString()).isEqualTo("[5/6] + [2/6]");

        BinaryOperator subtract = new BinaryOperator(BinaryOperation.SUBTRACT, left, right);
        assertThat(subtract.toString()).isEqualTo("[5/6] - [2/6]");

        BinaryOperator multiply = new BinaryOperator(BinaryOperation.MULTIPLY, left, right);
        assertThat(multiply.toString()).isEqualTo("[5/6] * [2/6]");

        BinaryOperator divide = new BinaryOperator(BinaryOperation.DIVIDE, left, right);
        assertThat(divide.toString()).isEqualTo("[5/6] / [2/6]");
    }

    @Test
    void testNestedBinaryOperators() {
        RolledDie die1 = new RolledDie(6, 2);
        RolledDie die2 = new RolledDie(6, 3);
        BinaryOperator inner = new BinaryOperator(BinaryOperation.ADD, die1, die2);

        RolledDie die3 = new RolledDie(6, 4);
        BinaryOperator outer = new BinaryOperator(BinaryOperation.MULTIPLY, inner, die3);

        assertThat(outer.total()).isEqualTo(20);
    }

    @Test
    void testWithConstants() {
        RolledDie constant = RolledDie.constant(10);
        RolledDie normal = new RolledDie(6, 5);
        BinaryOperator operator = new BinaryOperator(BinaryOperation.ADD, constant, normal);

        assertThat(operator.total()).isEqualTo(15);
    }
}
