package io.github.rmichela.dice4j.expression;

import io.github.rmichela.dice4j.dice.*;
import io.github.rmichela.dice4j.dice.modifier.*;
import java.util.List;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;

public final class DiceExpression extends DiceBaseVisitor<Rollable> {

    private DiceExpression() {}

    public static Rollable evaluate(String expression) throws DiceExpressionException {
        try {
            var input = CharStreams.fromString(expression);

            var lexer = new DiceLexer(input);
            lexer.removeErrorListeners();
            lexer.addErrorListener(ThrowingErrorListener.INSTANCE);

            var tokens = new CommonTokenStream(lexer);

            var parser = new DiceParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(ThrowingErrorListener.INSTANCE);

            var tree = parser.expression();
            var visitor = new DiceExpression();
            return visitor.visit(tree);
        } catch (Throwable e) {
            throw new DiceExpressionException(e.getMessage(), e);
        }
    }

    private static class ThrowingErrorListener extends BaseErrorListener {
        public static final ThrowingErrorListener INSTANCE = new ThrowingErrorListener();

        @Override
        public void syntaxError(
                Recognizer<?, ?> recognizer,
                Object offendingSymbol,
                int line,
                int charPositionInLine,
                String msg,
                RecognitionException e)
                throws ParseCancellationException {
            throw new ParseCancellationException(
                    "line " + line + ":" + charPositionInLine + " " + msg);
        }
    }

    // ----------------------------
    // ARITHMETIC EXPRESSIONS
    // ----------------------------

    @Override
    public Rollable visitBinaryAddSubExpr(DiceParser.BinaryAddSubExprContext ctx) {
        var left = visit(ctx.left);
        var right = visit(ctx.right);
        var op = BinaryOperation.fromSymbol(ctx.op.getText());
        return new BinaryModifier(op, left, right);
    }

    @Override
    public Rollable visitBinaryMulDivExpr(DiceParser.BinaryMulDivExprContext ctx) {
        var left = visit(ctx.left);
        var right = visit(ctx.right);
        var op = BinaryOperation.fromSymbol(ctx.op.getText());
        return new BinaryModifier(op, left, right);
    }

    @Override
    public Rollable visitParenExpr(DiceParser.ParenExprContext ctx) {
        return new ParentheticModifier(visit(ctx.expression()));
    }

    @Override
    public Rollable visitUnaryExpr(DiceParser.UnaryExprContext ctx) {
        var right = visit(ctx.right);
        var op = UnaryOperation.fromSymbol(ctx.op.getText());
        return new UnaryModifier(op, right);
    }

    @Override
    public Rollable visitConstExpr(DiceParser.ConstExprContext ctx) {
        int constant = Integer.parseInt(ctx.constant.getText());
        return new Constant(constant);
    }

    @Override
    public Rollable visitDieExpr(DiceParser.DieExprContext ctx) {
        return visit(ctx.dicePool());
    }

    // ----------------------------
    // DICE POOL EXPRESSIONS
    // ----------------------------

    @Override
    public Rollable visitExplicitDicePool(DiceParser.ExplicitDicePoolContext ctx) {
        var pool = new ExplicitDicePool();
        for (var innerPoolCtx : ctx.dicePool()) {
            pool.add(visit(innerPoolCtx));
        }

        // Optimization: "pull up" nested dice pools into this dice pool
        // Using ListIterator to allow self-modification while iterating
        var it = pool.getPool().listIterator();
        while (it.hasNext()) {
            if (it.next() instanceof DicePool dp) {
                it.remove();
                dp.getPool().forEach(it::add);
            }
        }

        return decorate(pool, ctx.poolModifier());
    }

    @Override
    public Rollable visitImplicitDicePool(DiceParser.ImplicitDicePoolContext ctx) {
        int count = Integer.parseInt(ctx.count.getText());
        int sides = Integer.parseInt(ctx.sides.getText());
        RollablePool pool;
        if (count == 1) {
            pool = new ExplicitDicePool().add(new Die(sides));
        } else {
            pool = new ImplicitDicePool(count, sides);
        }

        return decorate(pool, ctx.poolModifier());
    }

    private RollablePool decorate(
            RollablePool pool, List<DiceParser.PoolModifierContext> modifierCtxList) {
        for (var modifierCtx : modifierCtxList) {
            pool = ((AbstractModifier) visit(modifierCtx)).decorate(pool);
        }
        return pool;
    }

    @Override
    public Rollable visitConstantDie(DiceParser.ConstantDieContext ctx) {
        int constant = Integer.parseInt(ctx.constant.getText());
        return new ExplicitDicePool().add(new Constant(constant));
    }

    // ----------------------------
    // POOL MODIFIER EXPRESSIONS
    // ----------------------------

    @Override
    public Rollable visitDropHighest(DiceParser.DropHighestContext ctx) {
        int qty = ctx.qty != null ? Integer.parseInt(ctx.qty.getText()) : 1;
        return KeepDropModifier.dropHighest(qty);
    }

    @Override
    public Rollable visitDropLowest(DiceParser.DropLowestContext ctx) {
        int qty = ctx.qty != null ? Integer.parseInt(ctx.qty.getText()) : 1;
        return KeepDropModifier.dropLowest(qty);
    }

    @Override
    public Rollable visitKeepHighest(DiceParser.KeepHighestContext ctx) {
        int qty = ctx.qty != null ? Integer.parseInt(ctx.qty.getText()) : 1;
        return KeepDropModifier.keepHighest(qty);
    }

    @Override
    public Rollable visitKeepLowest(DiceParser.KeepLowestContext ctx) {
        int qty = ctx.qty != null ? Integer.parseInt(ctx.qty.getText()) : 1;
        return KeepDropModifier.keepLowest(qty);
    }

    @Override
    public Rollable visitReroll(DiceParser.RerollContext ctx) {
        var rel =
                ctx.rel != null
                        ? RelationalOperation.fromSymbol(ctx.rel.getText())
                        : RelationalOperation.EQUAL;
        int qty = ctx.qty != null ? Integer.parseInt(ctx.qty.getText()) : 1;
        return new RerollModifier(rel, qty, false);
    }

    @Override
    public Rollable visitRerollRecursive(DiceParser.RerollRecursiveContext ctx) {
        var rel =
                ctx.rel != null
                        ? RelationalOperation.fromSymbol(ctx.rel.getText())
                        : RelationalOperation.EQUAL;
        int qty = ctx.qty != null ? Integer.parseInt(ctx.qty.getText()) : 1;
        return new RerollModifier(rel, qty, true);
    }

    @Override
    public Rollable visitExplode(DiceParser.ExplodeContext ctx) {
        int qty = ctx.qty != null ? Integer.parseInt(ctx.qty.getText()) : ExplodeModifier.RECURSIVE;
        return new ExplodeModifier(qty);
    }
}
