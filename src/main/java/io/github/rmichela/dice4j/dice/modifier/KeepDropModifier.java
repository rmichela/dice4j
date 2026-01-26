package io.github.rmichela.dice4j.dice.modifier;

import io.github.rmichela.dice4j.roll.Rolled;
import io.github.rmichela.dice4j.roll.RolledDie;
import io.github.rmichela.dice4j.roller.DieRoller;
import java.util.Comparator;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KeepDropModifier extends AbstractModifier {

    @RequiredArgsConstructor
    public enum Action {
        KEEP_HIGHEST("kh", Comparator.reverseOrder(), RolledDie::keep, RolledDie::drop),
        KEEP_LOWEST("kl", Comparator.naturalOrder(), RolledDie::keep, RolledDie::drop),
        DROP_HIGHEST("dh", Comparator.reverseOrder(), RolledDie::drop, RolledDie::keep),
        DROP_LOWEST("dl", Comparator.naturalOrder(), RolledDie::drop, RolledDie::keep),
        ;

        private final String modifierString;
        private final Comparator<RolledDie> comparator;
        private final Consumer<RolledDie> foreach;
        private final Consumer<RolledDie> clear;
    }

    private final Action action;
    private final int qty;

    public static KeepDropModifier keepHighest(int qty) {
        return new KeepDropModifier(Action.KEEP_HIGHEST, qty);
    }

    public static KeepDropModifier keepLowest(int qty) {
        return new KeepDropModifier(Action.KEEP_LOWEST, qty);
    }

    public static KeepDropModifier dropHighest(int qty) {
        return new KeepDropModifier(Action.DROP_HIGHEST, qty);
    }

    public static KeepDropModifier dropLowest(int qty) {
        return new KeepDropModifier(Action.DROP_LOWEST, qty);
    }

    @Override
    protected String getModifierString() {
        return action.modifierString + qty;
    }

    @Override
    public Rolled roll(DieRoller random) {
        var rolled = inner.roll(random);

        // Only reset the keep/drop status when directly modifying a dice pool. When modifiers
        // stack, they should not wipe out the work done by the inner modifiers.
        if (!(inner instanceof PoolModifier)) {
            rolled.gather().forEach(action.clear);
        }

        rolled.gather().stream().sorted(action.comparator).limit(qty).forEach(action.foreach);

        rolled.modifiedBy(this);
        return rolled;
    }
}
