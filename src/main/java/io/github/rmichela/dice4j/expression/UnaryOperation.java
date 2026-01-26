package io.github.rmichela.dice4j.expression;

import java.util.function.Function;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UnaryOperation {
    NEGATE("-", r -> -r);

    private final String symbol;
    private final Function<Integer, Integer> action;

    public static UnaryOperation fromSymbol(String symbol) {
        for (var op : UnaryOperation.values()) {
            if (op.symbol.equals(symbol)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unknown unary op symbol: " + symbol);
    }

    public int apply(int value) {
        return action.apply(value);
    }

    @Override
    public String toString() {
        return symbol;
    }
}
