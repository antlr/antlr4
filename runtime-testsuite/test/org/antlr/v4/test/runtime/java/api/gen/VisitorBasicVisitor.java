// Generated from /Users/julianbissekkou/Documents/development/tapped/antlr4/runtime-testsuite/test/org/antlr/v4/test/runtime/java/api/VisitorBasic.g4 by ANTLR 4.9.1
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link VisitorBasicParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface VisitorBasicVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link VisitorBasicParser#s}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitS(VisitorBasicParser.SContext ctx);
}