package io.github.rmichela.dice4j.dice;

import io.github.rmichela.dice4j.roll.Rolled;
import io.github.rmichela.dice4j.roll.RolledDie;
import io.github.rmichela.dice4j.roller.DieRoller;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Die implements Rollable {
    private final int sides;

    @Override
    public Rolled roll(DieRoller r) {
        int value = r.nextRoll(sides);
        return new RolledDie(sides, value);
    }

    @Override
    public String toString() {
        return "d" + sides;
    }
}
