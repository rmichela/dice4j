package io.github.rmichela.dice4j.dice;

public class ExplicitDicePool extends DicePool {
    public ExplicitDicePool add(Rollable die) {
        pool.add(die);
        return this;
    }
}
