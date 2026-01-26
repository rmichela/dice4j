package io.github.rmichela.dice4j.dice.modifier;

import io.github.rmichela.dice4j.dice.Rollable;
import io.github.rmichela.dice4j.roll.ParentheticOperator;
import io.github.rmichela.dice4j.roll.Rolled;
import io.github.rmichela.dice4j.roller.DieRoller;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParentheticModifier implements Rollable, PoolModifier {
    private final Rollable inner;

    @Override
    public Rolled roll(DieRoller random) {
        return new ParentheticOperator(inner.roll(random));
    }

    @Override
    public String toString() {
        return "(" + inner.toString() + ")";
    }
}
