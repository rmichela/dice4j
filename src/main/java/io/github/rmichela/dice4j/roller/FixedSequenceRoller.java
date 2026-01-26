package io.github.rmichela.dice4j.roller;

import java.util.Arrays;

public class FixedSequenceRoller implements DieRoller {
    private final int[] sequence;
    private int i = 0;

    public FixedSequenceRoller(int first, int... rest) {
        sequence = new int[rest.length + 1];
        sequence[0] = first;
        System.arraycopy(rest, 0, sequence, 1, rest.length);

        if (Arrays.stream(sequence).anyMatch(i -> i < 1)) {
            throw new IllegalArgumentException("Die rolls must be greater than zero");
        }
    }

    @Override
    public int nextRoll(int sides) {
        int next = sequence[i];
        if (++i == sequence.length) i = 0;
        return next;
    }
}
