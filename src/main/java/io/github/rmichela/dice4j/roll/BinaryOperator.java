package io.github.rmichela.dice4j.roll;

import io.github.rmichela.dice4j.expression.BinaryOperation;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BinaryOperator extends Rolled {
    private final BinaryOperation op;
    private final Rolled left;
    private final Rolled right;

    @Override
    public List<RolledDie> gatherDice() {
        return List.of(RolledDie.constant(op.apply(left.total(), right.total())));
    }

    @Override
    public List<RolledPool> gatherPools() {
        var gatheredPools = new ArrayList<RolledPool>();
        var loosePool = new RolledPool();
        for (var childPool : left.gatherPools()) {
            if (childPool.getModifiedBy() != null) {
                gatheredPools.add(childPool);
            } else {
                loosePool = loosePool.merge(childPool);
            }
        }
        for (var childPool : right.gatherPools()) {
            if (childPool.getModifiedBy() != null) {
                gatheredPools.add(childPool);
            } else {
                loosePool = loosePool.merge(childPool);
            }
        }

        if (!loosePool.isEmpty()) {
            gatheredPools.add(loosePool);
        }
        return gatheredPools;
    }

    @Override
    public String toString() {
        return left.toString() + " " + op + " " + right.toString();
    }
}
