package io.github.rmichela.dice4j.dice;

import io.github.rmichela.dice4j.roll.Rolled;
import io.github.rmichela.dice4j.roll.RolledDie;
import io.github.rmichela.dice4j.roller.DieRoller;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Constant implements Rollable {
    private final int constant;

    @Override
    public Rolled roll(DieRoller random) {
        return RolledDie.constant(constant);
    }

    @Override
    public String toString() {
        return Integer.toString(constant);
    }
}
