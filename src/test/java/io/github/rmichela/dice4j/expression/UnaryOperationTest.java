package io.github.rmichela.dice4j.expression;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UnaryOperationTest {

    @Test
    void testNegateApply() {
        assertThat(UnaryOperation.NEGATE.apply(5)).isEqualTo(-5);
        assertThat(UnaryOperation.NEGATE.apply(-3)).isEqualTo(3);
        assertThat(UnaryOperation.NEGATE.apply(0)).isEqualTo(0);
    }

    @Test
    void testFromSymbolNegate() {
        assertThat(UnaryOperation.fromSymbol("-")).isEqualTo(UnaryOperation.NEGATE);
    }

    @Test
    void testFromSymbolUnknownSymbol() {
        assertThatThrownBy(() -> UnaryOperation.fromSymbol("+"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown unary op symbol: +");
    }

    @Test
    void testFromSymbolEmptyString() {
        assertThatThrownBy(() -> UnaryOperation.fromSymbol(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown unary op symbol: ");
    }

    @Test
    void testToString() {
        assertThat(UnaryOperation.NEGATE.toString()).isEqualTo("-");
    }

    @Test
    void testNegateWithIntegerBounds() {
        assertThat(UnaryOperation.NEGATE.apply(Integer.MAX_VALUE)).isEqualTo(-Integer.MAX_VALUE);
        assertThat(UnaryOperation.NEGATE.apply(Integer.MIN_VALUE + 1)).isEqualTo(Integer.MAX_VALUE);
    }
}
