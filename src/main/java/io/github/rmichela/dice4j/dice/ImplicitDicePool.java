package io.github.rmichela.dice4j.dice;

public class ImplicitDicePool extends DicePool {
    public ImplicitDicePool(int count, int sides) {
        for (int i = 0; i < count; i++) {
            pool.add(new Die(sides));
        }
    }
}
