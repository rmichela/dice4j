package io.github.rmichela.dice4j.roll;

import io.github.rmichela.dice4j.dice.modifier.AbstractModifier;
import java.util.List;
import lombok.Getter;

@Getter
public abstract class Rolled {
    private AbstractModifier modifiedBy;

    public abstract List<RolledDie> gather();

    public void modifiedBy(AbstractModifier modifier) {
        this.modifiedBy = modifier;
    }

    public int total() {
        return gather().stream().filter(RolledDie::isKept).mapToInt(RolledDie::getValue).sum();
    }
}
