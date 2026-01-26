package io.github.rmichela.dice4j.expression;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BinaryOperationTest {

    @Test
    void testAddApply() {
        assertThat(BinaryOperation.ADD.apply(5, 3)).isEqualTo(8);
        assertThat(BinaryOperation.ADD.apply(-2, 7)).isEqualTo(5);
        assertThat(BinaryOperation.ADD.apply(0, 0)).isEqualTo(0);
        assertThat(BinaryOperation.ADD.apply(-3, -4)).isEqualTo(-7);
    }

    @Test
    void testSubtractApply() {
        assertThat(BinaryOperation.SUBTRACT.apply(10, 4)).isEqualTo(6);
        assertThat(BinaryOperation.SUBTRACT.apply(3, 8)).isEqualTo(-5);
        assertThat(BinaryOperation.SUBTRACT.apply(0, 5)).isEqualTo(-5);
        assertThat(BinaryOperation.SUBTRACT.apply(-3, -7)).isEqualTo(4);
    }

    @Test
    void testMultiplyApply() {
        assertThat(BinaryOperation.MULTIPLY.apply(4, 3)).isEqualTo(12);
        assertThat(BinaryOperation.MULTIPLY.apply(-2, 5)).isEqualTo(-10);
        assertThat(BinaryOperation.MULTIPLY.apply(-3, -4)).isEqualTo(12);
        assertThat(BinaryOperation.MULTIPLY.apply(0, 100)).isEqualTo(0);
    }

    @Test
    void testDivideApply() {
        assertThat(BinaryOperation.DIVIDE.apply(10, 2)).isEqualTo(5);
        assertThat(BinaryOperation.DIVIDE.apply(7, 3)).isEqualTo(2); // Integer division
        assertThat(BinaryOperation.DIVIDE.apply(-10, 3)).isEqualTo(-3);
        assertThat(BinaryOperation.DIVIDE.apply(0, 5)).isEqualTo(0);
    }

    @Test
    void testDivideRoundsDown() {
        assertThat(BinaryOperation.DIVIDE.apply(5, 2)).isEqualTo(2);
        assertThat(BinaryOperation.DIVIDE.apply(9, 4)).isEqualTo(2);
        assertThat(BinaryOperation.DIVIDE.apply(1, 2)).isEqualTo(0);
    }

    @Test
    void testFromSymbolAdd() {
        assertThat(BinaryOperation.fromSymbol("+")).isEqualTo(BinaryOperation.ADD);
    }

    @Test
    void testFromSymbolSubtract() {
        assertThat(BinaryOperation.fromSymbol("-")).isEqualTo(BinaryOperation.SUBTRACT);
    }

    @Test
    void testFromSymbolMultiply() {
        assertThat(BinaryOperation.fromSymbol("*")).isEqualTo(BinaryOperation.MULTIPLY);
    }

    @Test
    void testFromSymbolDivide() {
        assertThat(BinaryOperation.fromSymbol("/")).isEqualTo(BinaryOperation.DIVIDE);
    }

    @Test
    void testFromSymbolUnknownSymbol() {
        assertThatThrownBy(() -> BinaryOperation.fromSymbol("%"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown binary op symbol: %");
    }

    @Test
    void testFromSymbolEmptyString() {
        assertThatThrownBy(() -> BinaryOperation.fromSymbol(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown binary op symbol: ");
    }

    @Test
    void testToString() {
        assertThat(BinaryOperation.ADD.toString()).isEqualTo("+");
        assertThat(BinaryOperation.SUBTRACT.toString()).isEqualTo("-");
        assertThat(BinaryOperation.MULTIPLY.toString()).isEqualTo("*");
        assertThat(BinaryOperation.DIVIDE.toString()).isEqualTo("/");
    }

    @Test
    void testAllOperationsExist() {
        assertThat(BinaryOperation.values())
                .containsExactly(
                        BinaryOperation.ADD,
                        BinaryOperation.SUBTRACT,
                        BinaryOperation.MULTIPLY,
                        BinaryOperation.DIVIDE);
    }
}
