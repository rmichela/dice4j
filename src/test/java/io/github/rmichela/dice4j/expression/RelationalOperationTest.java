package io.github.rmichela.dice4j.expression;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RelationalOperationTest {

    @Test
    void testGreaterThanTest() {
        assertThat(RelationalOperation.GREATER_THAN.test(5, 3)).isTrue();
        assertThat(RelationalOperation.GREATER_THAN.test(3, 5)).isFalse();
        assertThat(RelationalOperation.GREATER_THAN.test(5, 5)).isFalse();
        assertThat(RelationalOperation.GREATER_THAN.test(-2, -5)).isTrue();
        assertThat(RelationalOperation.GREATER_THAN.test(-5, -2)).isFalse();
    }

    @Test
    void testLessThanTest() {
        assertThat(RelationalOperation.LESS_THAN.test(3, 5)).isTrue();
        assertThat(RelationalOperation.LESS_THAN.test(5, 3)).isFalse();
        assertThat(RelationalOperation.LESS_THAN.test(5, 5)).isFalse();
        assertThat(RelationalOperation.LESS_THAN.test(-5, -2)).isTrue();
        assertThat(RelationalOperation.LESS_THAN.test(-2, -5)).isFalse();
    }

    @Test
    void testEqualTest() {
        assertThat(RelationalOperation.EQUAL.test(5, 5)).isTrue();
        assertThat(RelationalOperation.EQUAL.test(0, 0)).isTrue();
        assertThat(RelationalOperation.EQUAL.test(-3, -3)).isTrue();
        assertThat(RelationalOperation.EQUAL.test(5, 3)).isFalse();
        assertThat(RelationalOperation.EQUAL.test(3, 5)).isFalse();
    }

    @Test
    void testFromSymbolGreaterThan() {
        assertThat(RelationalOperation.fromSymbol(">")).isEqualTo(RelationalOperation.GREATER_THAN);
    }

    @Test
    void testFromSymbolLessThan() {
        assertThat(RelationalOperation.fromSymbol("<")).isEqualTo(RelationalOperation.LESS_THAN);
    }

    @Test
    void testFromSymbolEqual() {
        assertThat(RelationalOperation.fromSymbol("=")).isEqualTo(RelationalOperation.EQUAL);
    }

    @Test
    void testFromSymbolUnknownSymbol() {
        assertThatThrownBy(() -> RelationalOperation.fromSymbol("!="))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown rel op symbol: !=");
    }

    @Test
    void testFromSymbolEmptyString() {
        assertThatThrownBy(() -> RelationalOperation.fromSymbol(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown rel op symbol: ");
    }

    @Test
    void testToString() {
        assertThat(RelationalOperation.GREATER_THAN.toString()).isEqualTo(">");
        assertThat(RelationalOperation.LESS_THAN.toString()).isEqualTo("<");
        assertThat(RelationalOperation.EQUAL.toString()).isEqualTo("=");
    }

    @Test
    void testAllOperationsExist() {
        assertThat(RelationalOperation.values())
                .containsExactly(
                        RelationalOperation.GREATER_THAN,
                        RelationalOperation.LESS_THAN,
                        RelationalOperation.EQUAL);
    }

    @Test
    void testBoundaryValues() {
        assertThat(RelationalOperation.GREATER_THAN.test(Integer.MAX_VALUE, Integer.MIN_VALUE))
                .isTrue();
        assertThat(RelationalOperation.LESS_THAN.test(Integer.MIN_VALUE, Integer.MAX_VALUE))
                .isTrue();
        assertThat(RelationalOperation.EQUAL.test(Integer.MAX_VALUE, Integer.MAX_VALUE)).isTrue();
        assertThat(RelationalOperation.EQUAL.test(Integer.MIN_VALUE, Integer.MIN_VALUE)).isTrue();
    }
}
