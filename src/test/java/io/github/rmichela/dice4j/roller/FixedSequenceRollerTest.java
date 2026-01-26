package io.github.rmichela.dice4j.roller;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class FixedSequenceRollerTest {

    @Test
    void testSingleValueConstructor() {
        FixedSequenceRoller roller = new FixedSequenceRoller(5);
        assertThat(roller.nextRoll(6)).isEqualTo(5);
        assertThat(roller.nextRoll(6)).isEqualTo(5);
        assertThat(roller.nextRoll(6)).isEqualTo(5);
    }

    @Test
    void testMultipleValuesConstructor() {
        FixedSequenceRoller roller = new FixedSequenceRoller(1, 2, 3);
        assertThat(roller.nextRoll(6)).isEqualTo(1);
        assertThat(roller.nextRoll(6)).isEqualTo(2);
        assertThat(roller.nextRoll(6)).isEqualTo(3);
    }

    @Test
    void testSequentialRolling() {
        FixedSequenceRoller roller = new FixedSequenceRoller(4, 2, 6, 1);
        assertThat(roller.nextRoll(6)).isEqualTo(4);
        assertThat(roller.nextRoll(6)).isEqualTo(2);
        assertThat(roller.nextRoll(6)).isEqualTo(6);
        assertThat(roller.nextRoll(6)).isEqualTo(1);
    }

    @Test
    void testCyclingBehavior() {
        FixedSequenceRoller roller = new FixedSequenceRoller(1, 2, 3);
        // First cycle
        assertThat(roller.nextRoll(6)).isEqualTo(1);
        assertThat(roller.nextRoll(6)).isEqualTo(2);
        assertThat(roller.nextRoll(6)).isEqualTo(3);
        // Second cycle
        assertThat(roller.nextRoll(6)).isEqualTo(1);
        assertThat(roller.nextRoll(6)).isEqualTo(2);
        assertThat(roller.nextRoll(6)).isEqualTo(3);
        // Third cycle
        assertThat(roller.nextRoll(6)).isEqualTo(1);
    }

    @Test
    void testSidesParameterIsIgnored() {
        FixedSequenceRoller roller = new FixedSequenceRoller(10, 20, 30);
        // The sides parameter doesn't affect the returned value
        assertThat(roller.nextRoll(6)).isEqualTo(10);
        assertThat(roller.nextRoll(20)).isEqualTo(20);
        assertThat(roller.nextRoll(100)).isEqualTo(30);
        // Cycles back
        assertThat(roller.nextRoll(4)).isEqualTo(10);
    }

    @Test
    void testTwoValueSequence() {
        FixedSequenceRoller roller = new FixedSequenceRoller(1, 6);
        assertThat(roller.nextRoll(6)).isEqualTo(1);
        assertThat(roller.nextRoll(6)).isEqualTo(6);
        assertThat(roller.nextRoll(6)).isEqualTo(1);
        assertThat(roller.nextRoll(6)).isEqualTo(6);
    }

    @Test
    void testSequenceWithNegativeValues() {
        assertThatThrownBy(() -> new FixedSequenceRoller(-1, 0, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Die rolls must be greater than zero");
    }

    @Test
    void testSequenceWithZero() {
        assertThatThrownBy(() -> new FixedSequenceRoller(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Die rolls must be greater than zero");
    }

    @Test
    void testLongSequence() {
        FixedSequenceRoller roller = new FixedSequenceRoller(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        for (int i = 1; i <= 10; i++) {
            assertThat(roller.nextRoll(6)).isEqualTo(i);
        }
        // Should cycle back to 1
        assertThat(roller.nextRoll(6)).isEqualTo(1);
    }
}
