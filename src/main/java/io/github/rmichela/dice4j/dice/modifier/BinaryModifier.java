package io.github.rmichela.dice4j.dice.modifier;

import io.github.rmichela.dice4j.dice.Rollable;
import io.github.rmichela.dice4j.expression.BinaryOperation;
import io.github.rmichela.dice4j.roll.BinaryOperator;
import io.github.rmichela.dice4j.roll.Rolled;
import io.github.rmichela.dice4j.roller.DieRoller;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BinaryModifier implements Rollable, PoolModifier {
    private final BinaryOperation op;
    private final Rollable left;
    private final Rollable right;

    @Override
    public Rolled roll(DieRoller random) {
        return new BinaryOperator(op, left.roll(random), right.roll(random));
    }

    @Override
    public String toString() {
        return left.toString() + " " + op + " " + right.toString();
    }
}
