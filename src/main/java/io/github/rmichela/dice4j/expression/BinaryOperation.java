package io.github.rmichela.dice4j.expression;

import java.util.function.BiFunction;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BinaryOperation {
    ADD("+", (l, r) -> l + r),
    SUBTRACT("-", (l, r) -> l - r),
    MULTIPLY("*", (l, r) -> l * r),
    DIVIDE("/", (l, r) -> l / r), // Integer division rounds down
    ;

    private final String symbol;
    private final BiFunction<Integer, Integer, Integer> action;

    public static BinaryOperation fromSymbol(String symbol) {
        for (var op : BinaryOperation.values()) {
            if (op.symbol.equals(symbol)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unknown binary op symbol: " + symbol);
    }

    public int apply(int lhs, int rhs) {
        return action.apply(lhs, rhs);
    }

    @Override
    public String toString() {
        return symbol;
    }
}
