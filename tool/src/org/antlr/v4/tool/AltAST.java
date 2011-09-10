/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.tool;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;
import org.antlr.v4.parse.ANTLRParser;

/** Any ALT (which can be child of ALT_REWRITE node) */
public class AltAST extends GrammarAST {
	public Alternative alt;

	/** If someone specified an outermost alternative label with #foo.
	 *  Token type will be ID.
	 */
	public GrammarAST altLabel;

	public AltAST(GrammarAST node) {
		super(node);
		this.alt = ((AltAST)node).alt;
	}

	public AltAST(Token t) { super(t); }
	public AltAST(int type) { super(type); }
	public AltAST(int type, Token t) { super(type, t); }

	public GrammarAST getRewrite() {
		// ^(ALT_REWRITE ^(ALT ...) ^(-> ...)) ??
		if ( getParent().getType() == ANTLRParser.ALT_REWRITE ) {
			return (GrammarAST)getParent().getChild(1);
		}
		return null;
	}

	@Override
	public Tree dupNode() { return new AltAST(this); }
}
