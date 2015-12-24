// Generated from Arithmetic.g4 by ANTLR 4.5.1
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ArithmeticParser}.
 */
public interface ArithmeticListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ArithmeticParser#equation}.
	 * @param ctx the parse tree
	 */
	void enterEquation(ArithmeticParser.EquationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArithmeticParser#equation}.
	 * @param ctx the parse tree
	 */
	void exitEquation(ArithmeticParser.EquationContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArithmeticParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(ArithmeticParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArithmeticParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(ArithmeticParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArithmeticParser#multiplyingExpression}.
	 * @param ctx the parse tree
	 */
	void enterMultiplyingExpression(ArithmeticParser.MultiplyingExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArithmeticParser#multiplyingExpression}.
	 * @param ctx the parse tree
	 */
	void exitMultiplyingExpression(ArithmeticParser.MultiplyingExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArithmeticParser#powExpression}.
	 * @param ctx the parse tree
	 */
	void enterPowExpression(ArithmeticParser.PowExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArithmeticParser#powExpression}.
	 * @param ctx the parse tree
	 */
	void exitPowExpression(ArithmeticParser.PowExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArithmeticParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterAtom(ArithmeticParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArithmeticParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitAtom(ArithmeticParser.AtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArithmeticParser#scientific}.
	 * @param ctx the parse tree
	 */
	void enterScientific(ArithmeticParser.ScientificContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArithmeticParser#scientific}.
	 * @param ctx the parse tree
	 */
	void exitScientific(ArithmeticParser.ScientificContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArithmeticParser#relop}.
	 * @param ctx the parse tree
	 */
	void enterRelop(ArithmeticParser.RelopContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArithmeticParser#relop}.
	 * @param ctx the parse tree
	 */
	void exitRelop(ArithmeticParser.RelopContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArithmeticParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(ArithmeticParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArithmeticParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(ArithmeticParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArithmeticParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(ArithmeticParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArithmeticParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(ArithmeticParser.VariableContext ctx);
}