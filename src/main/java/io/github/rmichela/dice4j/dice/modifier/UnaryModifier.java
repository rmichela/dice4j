package io.github.rmichela.dice4j.dice.modifier;

import io.github.rmichela.dice4j.dice.Rollable;
import io.github.rmichela.dice4j.expression.UnaryOperation;
import io.github.rmichela.dice4j.roll.Rolled;
import io.github.rmichela.dice4j.roll.UnaryOperator;
import io.github.rmichela.dice4j.roller.DieRoller;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UnaryModifier implements Rollable, PoolModifier {
    private final UnaryOperation op;
    private final Rollable right;

    @Override
    public Rolled roll(DieRoller random) {
        return new UnaryOperator(op, right.roll(random));
    }

    @Override
    public String toString() {
        return op + right.toString();
    }
}
