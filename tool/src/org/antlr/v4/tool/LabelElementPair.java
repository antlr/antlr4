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

package org.antlr.v4.tool;

import org.antlr.runtime.BitSet;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.tool.ast.GrammarAST;

public class LabelElementPair {
    public static final BitSet tokenTypeForTokens = new BitSet();
    static {
        tokenTypeForTokens.add(ANTLRParser.TOKEN_REF);
        tokenTypeForTokens.add(ANTLRParser.STRING_LITERAL);
        tokenTypeForTokens.add(ANTLRParser.WILDCARD);
    }

    public GrammarAST label;
    public GrammarAST element;
    public LabelType type;

    public LabelElementPair(Grammar g, GrammarAST label, GrammarAST element, int labelOp) {
        this.label = label;
        this.element = element;
        // compute general case for label type
        if ( element.getFirstDescendantWithType(tokenTypeForTokens)!=null ) {
            if ( labelOp==ANTLRParser.ASSIGN ) type = LabelType.TOKEN_LABEL;
            else type = LabelType.TOKEN_LIST_LABEL;
        }
        else if ( element.getFirstDescendantWithType(ANTLRParser.RULE_REF)!=null ) {
            if ( labelOp==ANTLRParser.ASSIGN ) type = LabelType.RULE_LABEL;
            else type = LabelType.RULE_LIST_LABEL;
        }

        // now reset if lexer and string
        if ( g.isLexer() ) {
            if ( element.getFirstDescendantWithType(ANTLRParser.STRING_LITERAL)!=null ) {
                if ( labelOp==ANTLRParser.ASSIGN ) type = LabelType.LEXER_STRING_LABEL;
            }
        }
    }

    @Override
    public String toString() {
        return label.getText()+" "+type+" "+element.toString();
    }
}
