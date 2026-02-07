package io.github.rmichela.dice4j.roll;

import io.github.rmichela.dice4j.dice.modifier.AbstractModifier;
import java.util.List;
import lombok.Getter;

@Getter
public abstract class Rolled {
    private AbstractModifier modifiedBy;

    public abstract List<RolledDie> gatherDice();

    public abstract List<RolledPool> gatherPools();

    public void modifiedBy(AbstractModifier modifier) {
        this.modifiedBy = modifier;
    }

    public int total() {
        return gatherDice().stream().filter(RolledDie::isKept).mapToInt(RolledDie::getValue).sum();
    }
}
