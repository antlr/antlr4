// Generated from SwiftTest.g4 by ANTLR 4.6
import Antlr4

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SwiftTestParser}.
 */
public protocol SwiftTestListener: ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SwiftTestParser#s}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func enterS(_ ctx: SwiftTestParser.SContext)
	/**
	 * Exit a parse tree produced by {@link SwiftTestParser#s}.
	 - Parameters:
	   - ctx: the parse tree
	 */
	func exitS(_ ctx: SwiftTestParser.SContext)
}