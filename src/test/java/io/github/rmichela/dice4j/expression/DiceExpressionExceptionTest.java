package io.github.rmichela.dice4j.expression;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DiceExpressionExceptionTest {

    @Test
    void testConstructorWithMessageAndCause() {
        Throwable cause = new RuntimeException("Root cause");
        DiceExpressionException exception = new DiceExpressionException("Error message", cause);

        assertThat(exception.getMessage()).isEqualTo("Error message");
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    void testConstructorWithNullCause() {
        DiceExpressionException exception = new DiceExpressionException("Error message", null);

        assertThat(exception.getMessage()).isEqualTo("Error message");
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void testIsRuntimeException() {
        DiceExpressionException exception = new DiceExpressionException("Test", new Exception());

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
