package io.github.rmichela.dice4j.roller;

import java.util.random.RandomGenerator;

public class RandomRoller implements DieRoller {
    private final RandomGenerator random;

    public static RandomRoller getDefault() {
        return new RandomRoller(RandomGenerator.getDefault());
    }

    public RandomRoller(RandomGenerator random) {
        this.random = random;
    }

    @Override
    public int nextRoll(int sides) {
        return random.nextInt(1, sides + 1);
    }
}
