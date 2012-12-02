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

package org.antlr.v4.parse;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.GrammarASTErrorNode;
import org.antlr.v4.tool.ast.RuleAST;
import org.antlr.v4.tool.ast.TerminalAST;

public class GrammarASTAdaptor extends CommonTreeAdaptor {
    org.antlr.runtime.CharStream input; // where we can find chars ref'd by tokens in tree
    public GrammarASTAdaptor() { }
    public GrammarASTAdaptor(org.antlr.runtime.CharStream input) { this.input = input; }

    @Override
    public Object create(Token token) {
        return new GrammarAST(token);
    }

    @Override
    /** Make sure even imaginary nodes know the input stream */
    public Object create(int tokenType, String text) {
		GrammarAST t;
		if ( tokenType==ANTLRParser.RULE ) {
			// needed by TreeWizard to make RULE tree
        	t = new RuleAST(new CommonToken(tokenType, text));
		}
		else if ( tokenType==ANTLRParser.STRING_LITERAL ) {
			// implicit lexer construction done with wizard; needs this node type
			// whereas grammar ANTLRParser.g can use token option to spec node type
			t = new TerminalAST(new CommonToken(tokenType, text));
		}
		else {
			t = (GrammarAST)super.create(tokenType, text);
		}
        t.token.setInputStream(input);
        return t;
    }

    @Override
    public Object dupNode(Object t) {
        if ( t==null ) return null;
        return ((GrammarAST)t).dupNode(); //create(((GrammarAST)t).token);
    }

    @Override
    public Object errorNode(org.antlr.runtime.TokenStream input, org.antlr.runtime.Token start, org.antlr.runtime.Token stop,
                            org.antlr.runtime.RecognitionException e)
    {
        return new GrammarASTErrorNode(input, start, stop, e);
    }
}
