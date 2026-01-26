package io.github.rmichela.dice4j.dice.modifier;

import io.github.rmichela.dice4j.expression.RelationalOperation;
import io.github.rmichela.dice4j.roll.Rolled;
import io.github.rmichela.dice4j.roll.RolledDie;
import io.github.rmichela.dice4j.roll.RolledPool;
import io.github.rmichela.dice4j.roller.DieRoller;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RerollModifier extends AbstractModifier {
    private final RelationalOperation operation;
    private final int value;
    private final boolean recursive;

    @Override
    protected String getModifierString() {
        return recursive ? "rr" : "r" + operation + value;
    }

    @Override
    public Rolled roll(DieRoller random) {
        var rolled = inner.roll(random);

        boolean rerolled;
        do {
            rerolled = false;
            for (var die : rolled.gather()) {
                if (die.isKept() && operation.test(die.getValue(), value)) {
                    die.drop();
                    var newDie = new RolledDie(die.getSides(), random.nextRoll(die.getSides()));

                    // Rerolling a die results in the old die being dropped, and a new die being
                    // added. If the initial inner is a single RolledDie, we have to upgrade it to
                    // a RolledPool before adding another die.
                    if (rolled instanceof RolledDie rd) {
                        var rp = new RolledPool(rd);
                        rp.addTo(newDie);
                        rolled = rp;
                    } else if (rolled instanceof RolledPool rp) {
                        rp.addTo(newDie);
                    } else {
                        throw new IllegalStateException(
                                "Pool modifiers don't apply to arithmetic modifiers");
                    }

                    rerolled = true;
                }
            }
        } while (rerolled && recursive);

        return rolled;
    }
}
