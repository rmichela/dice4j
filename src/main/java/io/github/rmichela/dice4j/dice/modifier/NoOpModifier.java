package io.github.rmichela.dice4j.dice.modifier;

import io.github.rmichela.dice4j.roll.Rolled;
import io.github.rmichela.dice4j.roller.DieRoller;

/**
 * A no-operation modifier for testing purposes. This modifier does nothing to the rolled result
 * except mark it as modified, allowing tests to verify modifier-related logic without side
 * effects.
 */
public class NoOpModifier extends AbstractModifier {

    @Override
    protected String getModifierString() {
        return "";
    }

    @Override
    public Rolled roll(DieRoller random) {
        var rolled = inner.roll(random);
        rolled.modifiedBy(this);
        return rolled;
    }
}