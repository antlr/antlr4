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

package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Interval;

/** An interface to access the tree of RuleContext objects created
 *  during a parse that makes the data structure look like a simple parse tree.
 *  This node represents both internal nodes, rule invocations,
 *  and leaf nodes, token matches.
 *
 *  Unlike the common AST stuff in the runtime library, there is no such thing
 *  as a nil node. The payload is either a token or a context object.
 */
public interface ParseTree extends SyntaxTree {
	public interface RuleNode extends ParseTree {
		RuleContext getRuleContext();
	}
	public interface TokenNode extends ParseTree {
		Token getToken();
	}
	public static class TokenNodeImpl implements TokenNode {
		public Token token;
		public ParseTree parent;
		/** Which ATN node matched this token? */
		public int s;
		public TokenNodeImpl(Token token) {	this.token = token;	}

		public ParseTree getChild(int i) {return null;}

		public Token getToken() {return token;}

		public ParseTree getParent() { return parent; }

		public Token getPayload() { return token; }

		public Interval getSourceInterval() {
			if ( token==null ) return Interval.ZeroLength;
			return new Interval(token.getTokenIndex(), token.getTokenIndex());
		}

		public int getChildCount() { return 0; }

		@Override
		public String toString() {
			if ( token.getType() == Token.EOF ) return "<EOF>";
			return token.getText();
		}

		public String toStringTree() {
			return toString();
		}
	}
}
