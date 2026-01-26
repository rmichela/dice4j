package io.github.rmichela.dice4j.roll;

import io.github.rmichela.dice4j.expression.UnaryOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UnaryOperator extends Rolled {
    private final UnaryOperation op;
    private final Rolled right;

    @Override
    public List<RolledDie> gather() {
        return List.of(RolledDie.constant(op.apply(right.total())));
    }

    @Override
    public String toString() {
        return op + right.toString();
    }
}
