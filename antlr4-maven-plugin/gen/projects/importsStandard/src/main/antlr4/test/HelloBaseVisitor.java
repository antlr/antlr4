// Generated from /Users/julianbissekkou/Documents/development/tapped/antlr4/antlr4-maven-plugin/src/test/projects/importsStandard/src/main/antlr4/test/Hello.g4 by ANTLR 4.9.1
package projects.importsStandard.src.main.antlr4.test;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/**
 * This class provides an empty implementation of {@link HelloVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public class HelloBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements HelloVisitor<T> {
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitR(HelloParser.RContext ctx) { return visitChildren(ctx); }
}