/*
 * [The "BSD license"]
 *  Copyright (c) 2010 Terence Parr
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *      derived from this software without specific prior written permission.
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
package org.antlr.v4.test;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.tree.*;
import org.junit.Test;

public class TestBufferedASTNodeStream extends TestASTNodeStream {
    // inherits tests; these methods make it use a new buffer

	public ASTNodeStream newStream(Object t) {
		return new BufferedASTNodeStream(t);
	}

    public String toTokenTypeString(ASTNodeStream stream) {
        return ((BufferedASTNodeStream)stream).toTokenTypeString();
    }

    @Test public void testSeek() throws Exception {
        // ^(101 ^(102 103 ^(106 107) ) 104 105)
        // stream has 7 real + 6 nav nodes
        // Sequence of types: 101 DN 102 DN 103 106 DN 107 UP UP 104 105 UP EOF
        BaseAST r0 = new CommonAST(new CommonToken(101));
        BaseAST r1 = new CommonAST(new CommonToken(102));
        r0.addChild(r1);
        r1.addChild(new CommonAST(new CommonToken(103)));
        BaseAST r2 = new CommonAST(new CommonToken(106));
        r2.addChild(new CommonAST(new CommonToken(107)));
        r1.addChild(r2);
        r0.addChild(new CommonAST(new CommonToken(104)));
        r0.addChild(new CommonAST(new CommonToken(105)));

        ASTNodeStream stream = newStream(r0);
        stream.consume(); // consume 101
        stream.consume(); // consume DN
        stream.consume(); // consume 102
        stream.seek(7);   // seek to 107
        assertEquals(107, ((AST)stream.LT(1)).getType());
        stream.consume(); // consume 107
        stream.consume(); // consume UP
        stream.consume(); // consume UP
        assertEquals(104, ((AST)stream.LT(1)).getType());
    }
}
