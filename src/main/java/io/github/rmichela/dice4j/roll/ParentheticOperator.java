package io.github.rmichela.dice4j.roll;

import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParentheticOperator extends Rolled {
    private final Rolled inner;

    @Override
    public List<RolledDie> gather() {
        return inner.gather();
    }

    @Override
    public String toString() {
        return "(" + inner.toString() + ")";
    }
}
