package io.github.rmichela.dice4j.dice;

import io.github.rmichela.dice4j.roll.Rolled;
import io.github.rmichela.dice4j.roller.DieRoller;
import io.github.rmichela.dice4j.roller.RandomRoller;

public interface Rollable {
    default Rolled roll() {
        return roll(RandomRoller.getDefault());
    }

    Rolled roll(DieRoller random);
}
