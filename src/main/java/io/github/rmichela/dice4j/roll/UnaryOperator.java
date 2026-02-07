package io.github.rmichela.dice4j.roll;

import io.github.rmichela.dice4j.expression.UnaryOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UnaryOperator extends Rolled {
    private final UnaryOperation op;
    private final Rolled right;

    @Override
    public List<RolledDie> gatherDice() {
        return List.of(RolledDie.constant(op.apply(right.total())));
    }

    @Override
    public List<RolledPool> gatherPools() {
        return right.gatherPools();
    }

    @Override
    public String toString() {
        return op + right.toString();
    }
}
