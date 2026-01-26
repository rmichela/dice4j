package io.github.rmichela.dice4j.roll;

import static org.assertj.core.api.Assertions.*;

import io.github.rmichela.dice4j.expression.BinaryOperation;
import org.junit.jupiter.api.Test;

class ParentheticOperatorTest {

    @Test
    void testWithSingleDie() {
        RolledDie die = new RolledDie(6, 4);
        ParentheticOperator operator = new ParentheticOperator(die);

        assertThat(operator.total()).isEqualTo(4);
    }

    @Test
    void testWithRolledPool() {
        RolledDie die1 = new RolledDie(6, 3);
        RolledDie die2 = new RolledDie(6, 2);
        RolledPool pool = new RolledPool(die1, die2);
        ParentheticOperator operator = new ParentheticOperator(pool);

        assertThat(operator.total()).isEqualTo(5);
    }

    @Test
    void testGatherDelegates() {
        RolledDie die1 = new RolledDie(6, 3);
        RolledDie die2 = new RolledDie(6, 4);
        RolledPool pool = new RolledPool(die1, die2);
        ParentheticOperator operator = new ParentheticOperator(pool);

        assertThat(operator.gather()).containsExactly(die1, die2);
    }

    @Test
    void testGatherWithSingleDie() {
        RolledDie die = new RolledDie(6, 5);
        ParentheticOperator operator = new ParentheticOperator(die);

        assertThat(operator.gather()).containsExactly(die);
    }

    @Test
    void testWithDroppedDice() {
        RolledDie die1 = new RolledDie(6, 3);
        RolledDie die2 = new RolledDie(6, 2);
        die2.drop();
        RolledPool pool = new RolledPool(die1, die2);
        ParentheticOperator operator = new ParentheticOperator(pool);

        assertThat(operator.total()).isEqualTo(3);
    }

    @Test
    void testToString() {
        RolledDie die = new RolledDie(6, 4);
        ParentheticOperator operator = new ParentheticOperator(die);

        assertThat(operator.toString()).isEqualTo("([4/6])");
    }

    @Test
    void testToStringWithPool() {
        RolledDie die1 = new RolledDie(6, 3);
        RolledDie die2 = new RolledDie(6, 2);
        RolledPool pool = new RolledPool(die1, die2);
        ParentheticOperator operator = new ParentheticOperator(pool);

        assertThat(operator.toString()).isEqualTo("({ [3/6], [2/6] })");
    }

    @Test
    void testNestedParentheses() {
        RolledDie die = new RolledDie(6, 4);
        ParentheticOperator inner = new ParentheticOperator(die);
        ParentheticOperator outer = new ParentheticOperator(inner);

        assertThat(outer.total()).isEqualTo(4);
        assertThat(outer.toString()).isEqualTo("(([4/6]))");
    }

    @Test
    void testWithBinaryOperator() {
        RolledDie die1 = new RolledDie(6, 3);
        RolledDie die2 = new RolledDie(6, 2);
        BinaryOperator binOp = new BinaryOperator(BinaryOperation.ADD, die1, die2);
        ParentheticOperator operator = new ParentheticOperator(binOp);

        assertThat(operator.total()).isEqualTo(5);
        assertThat(operator.toString()).isEqualTo("([3/6] + [2/6])");
    }

    @Test
    void testWithConstant() {
        RolledDie constant = RolledDie.constant(10);
        ParentheticOperator operator = new ParentheticOperator(constant);

        assertThat(operator.total()).isEqualTo(10);
    }

    @Test
    void testGatherMaintainsOrder() {
        RolledDie die1 = new RolledDie(6, 1);
        RolledDie die2 = new RolledDie(6, 2);
        RolledDie die3 = new RolledDie(6, 3);
        RolledPool pool = new RolledPool(die1, die2, die3);
        ParentheticOperator operator = new ParentheticOperator(pool);

        assertThat(operator.gather()).containsExactly(die1, die2, die3);
    }

    @Test
    void testGatherWithNestedPools() {
        RolledDie die1 = new RolledDie(6, 1);
        RolledDie die2 = new RolledDie(6, 2);
        RolledDie die3 = new RolledDie(6, 3);

        RolledPool innerPool = new RolledPool(die2, die3);
        RolledPool outerPool = new RolledPool(die1, innerPool);
        ParentheticOperator operator = new ParentheticOperator(outerPool);

        assertThat(operator.gather()).containsExactly(die1, die2, die3);
    }
}
