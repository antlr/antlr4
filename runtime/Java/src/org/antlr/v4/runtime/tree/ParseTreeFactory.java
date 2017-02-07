package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

/* Sam comments:

I see three real pieces of machinery related to this:

	1. Inclusion of hidden tokens in the tree (as a newly exposed
	   property of the nearest TerminalNode, semantics can be discussed later)

	2. Ability to construct parse trees that are relevant for a grammar and have the proper types
	2a. The parser interpreter can create instances with the proper types
	2b. Users can create instances with a meaningful API

	3. Ability to transform a parse tree

	I have found Item 1 is extremely useful when attempting to do Item 3,
	such that I would include it almost as a requirement
 */

/** A generic mechanism for constructing parse tree nodes and
 *  assembling them into trees.
 */
public interface ParseTreeFactory {
	/** Create a rule node for ruleIndex as child of parent; invoked from invokingStateNumber */
	ParseTree createRuleNode(int ruleIndex, ParserRuleContext parent, int invokingStateNumber);

	/** Create a node for ruleIndex, copy ctx fields from src.
	 *  Does not copy children except for case mentioned at end of this comment.
	 *
	 *  This is used in the generated parser code to flip a generic XContext
	 *  node for rule X to a YContext for alt label Y. In that sense, it is
	 *  not really a generic copy function.
	 *
	 *  If we do an error sync() at start of a rule, we might add error nodes
	 *  to the generic XContext so this function must copy those nodes to
	 *  the YContext as well else they are lost!
	 */
	ParseTree createRuleNode(int ruleIndex, ParserRuleContext src);

	ErrorNode createErrorNode(Token badToken);

	TerminalNode createLeaf(Token matchedToken);

	void addChild(RuleNode parent, TerminalNode matchedTokenNode);

	void addChild(RuleNode parent, ParserRuleContext ruleInvocationNode);

	/** Used by enterOuterAlt to toss out a RuleContext previously added as
	 *  we entered a rule. If we have # label, we will need to remove
	 *  generic ruleContext object.
 	 */
	void replaceLastChild(ParserRuleContext parent, ParserRuleContext newChild);

	// -------------------

	/* Sam says:
	Last token on a line gets all hidden up to and including the first one
	that contains a newline.

	Last token in the file gets all hidden up to and not including EOF.

	In all other cases the hidden channel tokens are associated with the next
	non-hidden token.

	TerminalNodeImpl.getLeadingTrivia() returns the hidden tokens
	associated with the terminal that are before it (see previous rules).
	 TerminalNodeImpl.getTrailingTrivia() returns the hidden tokens
	  associated with the terminal that are after it. Names pulled
	   from Roslyn; can be adjusted.
	 */
//	Collection<TerminalNode> getLeadingHiddenTokens(ParseTree t);
//	Collection<TerminalNode> getTrailingHiddenTokens(ParseTree t);
}
