package io.github.rmichela.dice4j.roll;

import java.util.*;
import java.util.stream.Collector;
import lombok.Getter;

@Getter
public class RolledPool extends Rolled {
    private final List<Rolled> rolls = new ArrayList<>();

    public RolledPool(Rolled... rolls) {
        this.rolls.addAll(Arrays.asList(rolls));
    }

    public RolledPool(List<RolledDie> rolls) {
        this.rolls.addAll(rolls);
    }

    public void addRolled(Rolled roll) {
        this.rolls.add(roll);
    }

    public RolledPool merge(RolledPool other) {
        var newPool = new RolledPool();
        newPool.rolls.addAll(this.rolls);
        newPool.rolls.addAll(other.rolls);
        return newPool;
    }

    public void addTo(RolledDie die) {
        rolls.add(die);
    }

    @Override
    public List<RolledDie> gatherDice() {
        return rolls.stream().flatMap(rolled -> rolled.gatherDice().stream()).toList();
    }

    @Override
    public List<RolledPool> gatherPools() {
        var childPools = rolls.stream().flatMap(rolled -> rolled.gatherPools().stream()).toList();
        var gatheredPools = new ArrayList<RolledPool>();
        var loosePool = new RolledPool();
        for (var childPool : childPools) {
            if (childPool.getModifiedBy() != null) {
                gatheredPools.add(childPool);
            } else {
                loosePool = loosePool.merge(childPool);
            }
        }
        if (!loosePool.isEmpty()) {
            loosePool.modifiedBy(this.getModifiedBy());
            gatheredPools.add(loosePool);
        }
        return gatheredPools;
    }

    public static Collector<Rolled, RolledPool, RolledPool> collector() {
        return Collector.of(RolledPool::new, RolledPool::addRolled, RolledPool::merge);
    }

    public boolean isEmpty() {
        return rolls.isEmpty();
    }

    @Override
    public String toString() {
        var sj = new StringJoiner(", ", "{ ", " }");
        rolls.forEach(rolled -> sj.add(rolled.toString()));
        return sj.toString();
    }
}
