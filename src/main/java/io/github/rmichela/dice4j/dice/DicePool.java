package io.github.rmichela.dice4j.dice;

import io.github.rmichela.dice4j.roll.Rolled;
import io.github.rmichela.dice4j.roll.RolledPool;
import io.github.rmichela.dice4j.roller.DieRoller;
import java.util.*;
import lombok.Getter;

@Getter
public abstract class DicePool implements RollablePool, Rollable {
    protected final List<Rollable> pool = new ArrayList<>();

    @Override
    public Rolled roll(DieRoller random) {
        return pool.stream().map(rollable -> rollable.roll(random)).collect(RolledPool.collector());
    }

    @Override
    public String toString() {
        var sj = new StringJoiner(", ", "{ ", " }");
        pool.forEach(rollable -> sj.add(rollable.toString()));
        return sj.toString();
    }
}
