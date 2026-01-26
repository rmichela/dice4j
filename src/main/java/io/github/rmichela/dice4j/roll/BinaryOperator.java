package io.github.rmichela.dice4j.roll;

import io.github.rmichela.dice4j.expression.BinaryOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BinaryOperator extends Rolled {
    private final BinaryOperation op;
    private final Rolled left;
    private final Rolled right;

    @Override
    public List<RolledDie> gather() {
        return List.of(RolledDie.constant(op.apply(left.total(), right.total())));
    }

    @Override
    public String toString() {
        return left.toString() + " " + op + " " + right.toString();
    }
}
