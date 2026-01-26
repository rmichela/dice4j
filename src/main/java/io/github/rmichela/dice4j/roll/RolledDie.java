package io.github.rmichela.dice4j.roll;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class RolledDie extends Rolled implements Comparable<RolledDie> {
    public static final int CONSTANT_SIDES = 1;

    @EqualsAndHashCode.Include private final int sides;
    @EqualsAndHashCode.Include private int value;
    @EqualsAndHashCode.Include private boolean kept;

    public RolledDie(int sides, int value) {
        if (sides <= 0) {
            throw new IllegalArgumentException("Die must have a positive number of sides");
        }
        if (sides != CONSTANT_SIDES && (value > sides || value < 1)) {
            throw new IllegalArgumentException("Die value must be between 1 and " + sides);
        }

        this.sides = sides;
        this.value = value;
        this.kept = true;
    }

    public static RolledDie constant(int value) {
        return new RolledDie(CONSTANT_SIDES, value);
    }

    public void keep() {
        this.kept = true;
    }

    public void drop() {
        this.kept = false;
    }

    public boolean isConstant() {
        return this.sides == CONSTANT_SIDES;
    }

    public boolean isMax() {
        return this.value == this.sides;
    }

    @Override
    public String toString() {
        return "[" + value + "/" + sides + (kept ? "" : "x") + "]";
    }

    @Override
    public List<RolledDie> gather() {
        return List.of(this);
    }

    @Override
    public int compareTo(RolledDie o) {
        return Integer.compare(this.value, o.value);
    }
}
