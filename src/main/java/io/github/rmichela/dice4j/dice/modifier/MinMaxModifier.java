package io.github.rmichela.dice4j.dice.modifier;

import io.github.rmichela.dice4j.roll.Rolled;
import io.github.rmichela.dice4j.roll.RolledDie;
import io.github.rmichela.dice4j.roll.RolledPool;
import io.github.rmichela.dice4j.roller.DieRoller;
import java.util.function.BiFunction;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MinMaxModifier extends AbstractModifier {

    @RequiredArgsConstructor
    public enum Action {
        MINIMUM("min", (value, threshold) -> value < threshold ? threshold : value),
        MAXIMUM("max", (value, threshold) -> value > threshold ? threshold : value),
        ;

        private final String modifierString;
        private final BiFunction<Integer, Integer, Integer> clamp;

        public int apply(int value, int threshold) {
            return clamp.apply(value, threshold);
        }
    }

    private final Action action;
    private final int value;

    public static MinMaxModifier minimum(int value) {
        return new MinMaxModifier(Action.MINIMUM, value);
    }

    public static MinMaxModifier maximum(int value) {
        return new MinMaxModifier(Action.MAXIMUM, value);
    }

    @Override
    protected String getModifierString() {
        return action.modifierString + value;
    }

    @Override
    public Rolled roll(DieRoller random) {
        var rolled = inner.roll(random);

        for (var die : rolled.gather()) {
            if (!die.isKept()) {
                continue; // Skip dropped dice
            }

            int originalValue = die.getValue();
            int clampedValue = action.apply(originalValue, value);

            // If the value needs to be changed, drop the old die and add a new one
            if (clampedValue != originalValue) {
                die.drop();
                var newDie = new RolledDie(die.getSides(), clampedValue);

                // Upgrade to pool if needed
                if (rolled instanceof RolledDie) {
                    rolled = new RolledPool(rolled);
                }
                ((RolledPool) rolled).addTo(newDie);
            }
        }

        rolled.modifiedBy(this);
        return rolled;
    }
}