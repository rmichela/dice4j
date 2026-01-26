package io.github.rmichela.dice4j.expression;

import java.util.function.BiPredicate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RelationalOperation {
    GREATER_THAN(">", (l, r) -> l > r),
    LESS_THAN("<", (l, r) -> l < r),
    EQUAL("=", Integer::equals),
    ;

    private final String symbol;
    private final BiPredicate<Integer, Integer> comparison;

    public static RelationalOperation fromSymbol(String symbol) {
        for (var op : RelationalOperation.values()) {
            if (op.symbol.equals(symbol)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unknown rel op symbol: " + symbol);
    }

    public boolean test(int lhs, int rhs) {
        return comparison.test(lhs, rhs);
    }

    @Override
    public String toString() {
        return symbol;
    }
}
