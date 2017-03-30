/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.tool.ast;

/** A simple visitor, based upon the classic double dispatch method,
 *  for walking GrammarAST trees resulting from parsing ANTLR grammars.
 *  There is also the GrammarTreeVisitor.g tree grammar that looks for
 *  subtree patterns and fires off high-level events as opposed to
 *  "found node" events like this visitor does. Also, like all
 *  visitors, the users of this interface are required to implement
 *  the node visitation of the children. The GrammarTreeVisitor mechanism
 *  fires events and the user is not required to do any walking code.
 *
 *  GrammarAST t = ...;
 *  GrammarASTVisitor v = new ...;
 *  t.visit(v);
 */
public interface GrammarASTVisitor {
	/** This is the generic visitor method that will be invoked
	 *  for any other kind of AST node not covered by the other visit methods.
	 */
	Object visit(GrammarAST node);

	Object visit(GrammarRootAST node);
	Object visit(RuleAST node);

	Object visit(BlockAST node);
	Object visit(OptionalBlockAST node);
	Object visit(PlusBlockAST node);
	Object visit(StarBlockAST node);

	Object visit(AltAST node);

	Object visit(NotAST node);
	Object visit(PredAST node);
	Object visit(RangeAST node);
	Object visit(SetAST node);
	Object visit(RuleRefAST node);
	Object visit(TerminalAST node);
}
