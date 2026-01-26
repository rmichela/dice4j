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
        this.rolls.addAll(other.rolls);
        return this;
    }

    public void addTo(RolledDie die) {
        rolls.add(die);
    }

    @Override
    public List<RolledDie> gather() {
        return rolls.stream().flatMap(rolled -> rolled.gather().stream()).toList();
    }

    public static Collector<Rolled, RolledPool, RolledPool> collector() {
        return Collector.of(RolledPool::new, RolledPool::addRolled, RolledPool::merge);
    }

    @Override
    public String toString() {
        var sj = new StringJoiner(", ", "{ ", " }");
        rolls.forEach(rolled -> sj.add(rolled.toString()));
        return sj.toString();
    }
}
