package io.github.rmichela.dice4j;

import io.github.rmichela.dice4j.expression.DiceExpression;
import io.github.rmichela.dice4j.roller.RandomRoller;

public class RollerMain {
    public static void main(String[] args) {
        var expression = "4d4x";
        System.out.println("Expres: " + expression);

        var pool = DiceExpression.evaluate(expression);
        System.out.println("Parsed: " + pool);

        //        var roller = new FixedSequenceRoller(1, 2, 3, 4, 5);
        var roller = RandomRoller.getDefault();
        var rolled = pool.roll(roller);
        System.out.println("Rolled: " + rolled + " ==> " + rolled.total());
    }
}
