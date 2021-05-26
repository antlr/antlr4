// Generated from /Users/julianbissekkou/Documents/development/tapped/antlr4/runtime-testsuite/test/org/antlr/v4/test/runtime/java/api/VisitorCalc.g4 by ANTLR 4.9.1
package org.antlr.v4.test.runtime.java.api;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link VisitorCalcParser}.
 */
public interface VisitorCalcListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link VisitorCalcParser#s}.
	 * @param ctx the parse tree
	 */
	void enterS(VisitorCalcParser.SContext ctx);
	/**
	 * Exit a parse tree produced by {@link VisitorCalcParser#s}.
	 * @param ctx the parse tree
	 */
	void exitS(VisitorCalcParser.SContext ctx);
	/**
	 * Enter a parse tree produced by the {@code add}
	 * labeled alternative in {@link VisitorCalcParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAdd(VisitorCalcParser.AddContext ctx);
	/**
	 * Exit a parse tree produced by the {@code add}
	 * labeled alternative in {@link VisitorCalcParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAdd(VisitorCalcParser.AddContext ctx);
	/**
	 * Enter a parse tree produced by the {@code number}
	 * labeled alternative in {@link VisitorCalcParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNumber(VisitorCalcParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by the {@code number}
	 * labeled alternative in {@link VisitorCalcParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNumber(VisitorCalcParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by the {@code multiply}
	 * labeled alternative in {@link VisitorCalcParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMultiply(VisitorCalcParser.MultiplyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code multiply}
	 * labeled alternative in {@link VisitorCalcParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMultiply(VisitorCalcParser.MultiplyContext ctx);
}