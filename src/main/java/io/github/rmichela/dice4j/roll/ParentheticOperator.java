package io.github.rmichela.dice4j.roll;

import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParentheticOperator extends Rolled {
    private final Rolled inner;

    @Override
    public List<RolledDie> gatherDice() {
        return inner.gatherDice();
    }

    @Override
    public List<RolledPool> gatherPools() {
        return inner.gatherPools();
    }

    @Override
    public String toString() {
        return "(" + inner.toString() + ")";
    }
}
