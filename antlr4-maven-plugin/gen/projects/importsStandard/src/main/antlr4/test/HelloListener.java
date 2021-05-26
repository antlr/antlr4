// Generated from /Users/julianbissekkou/Documents/development/tapped/antlr4/antlr4-maven-plugin/src/test/projects/importsStandard/src/main/antlr4/test/Hello.g4 by ANTLR 4.9.1
package projects.importsStandard.src.main.antlr4.test;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link HelloParser}.
 */
public interface HelloListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link HelloParser#r}.
	 * @param ctx the parse tree
	 */
	void enterR(HelloParser.RContext ctx);
	/**
	 * Exit a parse tree produced by {@link HelloParser#r}.
	 * @param ctx the parse tree
	 */
	void exitR(HelloParser.RContext ctx);
}