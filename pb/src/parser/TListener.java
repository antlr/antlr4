// Generated from T.g4 by ANTLR 4.5.1
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TParser}.
 */
public interface TListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(TParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link TParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(TParser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by {@link TParser#expr_or_assign}.
	 * @param ctx the parse tree
	 */
	void enterExpr_or_assign(TParser.Expr_or_assignContext ctx);
	/**
	 * Exit a parse tree produced by {@link TParser#expr_or_assign}.
	 * @param ctx the parse tree
	 */
	void exitExpr_or_assign(TParser.Expr_or_assignContext ctx);
	/**
	 * Enter a parse tree produced by {@link TParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(TParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link TParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(TParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link TParser#expr_primary}.
	 * @param ctx the parse tree
	 */
	void enterExpr_primary(TParser.Expr_primaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link TParser#expr_primary}.
	 * @param ctx the parse tree
	 */
	void exitExpr_primary(TParser.Expr_primaryContext ctx);
}