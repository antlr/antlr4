/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
