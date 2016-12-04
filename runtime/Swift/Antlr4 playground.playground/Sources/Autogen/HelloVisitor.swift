// Generated from Hello.g4 by ANTLR 4.6
import Antlr4

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link HelloParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
open class HelloVisitor<T>: ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link HelloParser#r}.
	- Parameters:
	  - ctx: the parse tree
	- returns: the visitor result
	 */
	open func visitR(_ ctx: HelloParser.RContext) -> T{
	 	fatalError(#function + " must be overridden")
	}

}