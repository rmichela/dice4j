package io.github.rmichela.dice4j.roll;

import static org.assertj.core.api.Assertions.*;

import io.github.rmichela.dice4j.expression.UnaryOperation;
import org.junit.jupiter.api.Test;

class UnaryOperatorTest {

    @Test
    void testNegatePositiveValue() {
        RolledDie die = new RolledDie(6, 4);
        UnaryOperator operator = new UnaryOperator(UnaryOperation.NEGATE, die);

        assertThat(operator.total()).isEqualTo(-4);
    }

    @Test
    void testNegateWithRolledPool() {
        RolledDie die1 = new RolledDie(6, 3);
        RolledDie die2 = new RolledDie(6, 2);
        RolledPool pool = new RolledPool(die1, die2);
        UnaryOperator operator = new UnaryOperator(UnaryOperation.NEGATE, pool);

        assertThat(operator.total()).isEqualTo(-5);
    }

    @Test
    void testNegateWithDroppedDice() {
        RolledDie die1 = new RolledDie(6, 3);
        RolledDie die2 = new RolledDie(6, 2);
        die2.drop();
        RolledPool pool = new RolledPool(die1, die2);
        UnaryOperator operator = new UnaryOperator(UnaryOperation.NEGATE, pool);

        assertThat(operator.total()).isEqualTo(-3);
    }

    @Test
    void testNegateZero() {
        RolledDie die = new RolledDie(6, 3);
        die.drop();
        UnaryOperator operator = new UnaryOperator(UnaryOperation.NEGATE, die);

        assertThat(operator.total()).isEqualTo(0);
    }

    @Test
    void testGatherDiceReturnsConstantDie() {
        RolledDie die = new RolledDie(6, 4);
        UnaryOperator operator = new UnaryOperator(UnaryOperation.NEGATE, die);

        var gathered = operator.gatherDice();
        assertThat(gathered).hasSize(1);
        RolledDie result = gathered.get(0);
        assertThat(result.isConstant()).isTrue();
        assertThat(result.getValue()).isEqualTo(-4);
    }

    @Test
    void testToString() {
        RolledDie die = new RolledDie(6, 4);
        UnaryOperator operator = new UnaryOperator(UnaryOperation.NEGATE, die);

        assertThat(operator.toString()).isEqualTo("-[4/6]");
    }

    @Test
    void testNestedUnaryOperators() {
        RolledDie die = new RolledDie(6, 3);
        UnaryOperator inner = new UnaryOperator(UnaryOperation.NEGATE, die);
        UnaryOperator outer = new UnaryOperator(UnaryOperation.NEGATE, inner);

        assertThat(outer.total()).isEqualTo(3);
    }

    @Test
    void testWithConstant() {
        RolledDie constant = RolledDie.constant(10);
        UnaryOperator operator = new UnaryOperator(UnaryOperation.NEGATE, constant);

        assertThat(operator.total()).isEqualTo(-10);
    }

    @Test
    void testWithNestedPool() {
        RolledDie die1 = new RolledDie(6, 2);
        RolledDie die2 = new RolledDie(6, 3);
        RolledPool innerPool = new RolledPool(die1, die2);

        RolledDie die3 = new RolledDie(6, 4);
        RolledPool outerPool = new RolledPool(innerPool, die3);

        UnaryOperator operator = new UnaryOperator(UnaryOperation.NEGATE, outerPool);

        assertThat(operator.total()).isEqualTo(-9);
    }
}
