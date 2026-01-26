package io.github.rmichela.dice4j.dice.modifier;

import io.github.rmichela.dice4j.dice.Rollable;
import io.github.rmichela.dice4j.dice.RollablePool;
import java.util.List;

public abstract class AbstractModifier implements RollablePool, PoolModifier {
    protected RollablePool inner;

    public RollablePool decorate(RollablePool inner) {
        this.inner = inner;
        return this;
    }

    @Override
    public String toString() {
        return inner.toString() + getModifierString();
    }

    protected abstract String getModifierString();

    @Override
    public List<Rollable> getPool() {
        return inner.getPool();
    }
}
