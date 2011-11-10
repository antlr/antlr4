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

package org.antlr.v4.tool.ast;

import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.Tree;

import java.util.HashMap;
import java.util.Map;

public class GrammarRootAST extends GrammarASTWithOptions {
    public static final Map<String, String> defaultOptions =
            new HashMap<String, String>() {
                {
                    put("language","Java");
                }
            };
    public int grammarType; // LEXER, PARSER, TREE, GRAMMAR (combined)
	public boolean hasErrors;
	/** Track stream used to create this tree */
	public TokenStream tokens;

	public GrammarRootAST(GrammarAST node) {
		super(node);
		this.grammarType = ((GrammarRootAST)node).grammarType;
		this.hasErrors = ((GrammarRootAST)node).hasErrors;
	}

	@Override
	public Tree dupNode() { return new GrammarRootAST(this); }

	public GrammarRootAST(int type) { super(type); }
    public GrammarRootAST(Token t) { super(t); }
    public GrammarRootAST(int type, Token t) { super(type, t); }
    public GrammarRootAST(int type, Token t, String text) {
        super(type,t,text);
    }

	@Override
	public Object visit(GrammarASTVisitor v) { return v.visit(this); }
}
