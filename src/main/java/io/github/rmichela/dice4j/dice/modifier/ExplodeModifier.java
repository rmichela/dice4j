package io.github.rmichela.dice4j.dice.modifier;

import io.github.rmichela.dice4j.roll.Rolled;
import io.github.rmichela.dice4j.roll.RolledDie;
import io.github.rmichela.dice4j.roll.RolledPool;
import io.github.rmichela.dice4j.roller.DieRoller;
import lombok.RequiredArgsConstructor;

import java.util.ArrayDeque;
import java.util.Queue;

@RequiredArgsConstructor
public class ExplodeModifier extends AbstractModifier {
    public static final int RECURSIVE = Integer.MAX_VALUE;

    private final int qty;

    @Override
    protected String getModifierString() {
        return "x" + (qty == RECURSIVE ? "" : qty);
    }

    @Override
    public Rolled roll(DieRoller random) {
        var rolled = inner.roll(random);
        // Seed the rolled queue with any die primed to explode
        Queue<RolledDie> explosionQueue = new ArrayDeque<>(rolled.gather().stream().filter(RolledDie::isMax).toList());
        int remainingRolls = qty;
        while (remainingRolls > 0 && !explosionQueue.isEmpty()) {
            var explodingDie = explosionQueue.remove();
            var explodedDie = new RolledDie(explodingDie.getSides(), random.nextRoll(explodingDie.getSides()));

            // If rolled is a single die, upgrade it to a pool. Then, add the new exploded die
            if (rolled instanceof RolledDie) {
                rolled = new RolledPool(rolled);
            }
            ((RolledPool)rolled).addTo(explodedDie);

            // If the exploded die is also max, add it to the queue for another explosion
            if (explodedDie.isMax()) {
                explosionQueue.add(explodedDie);
            }

            // Decrement the remaining possible future explosions
            remainingRolls--;
        }

        rolled.modifiedBy(this);
        return rolled;
    }
}
