/*
 [The "BSD license"]
 Copyright (c) 2005-2009 Terence Parr
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

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

/** A record of the rules used to match a token sequence.  The tokens
 *  end up as the leaves of this tree and rule nodes are the interior nodes.
 */
public abstract class ParseTree {

	public static class TokenNode extends ParseTree {
		public Token token;
		public TokenNode(Token token) {
			this.token = token;
		}

		@Override
		public String toString() {
			if ( token.getType() == Token.EOF ) return "<EOF>";
			return token.getText();
		}
	}

	public static class RuleNode extends ParseTree {
		public RuleContext ctx;
		public String ruleName;
		public RuleNode(String ruleName, RuleContext ctx) {
			this.ruleName = ruleName;
			this.ctx = ctx;
		}
		public String toString() { return ruleName; }
	}

	protected ParseTree parent;
	protected List<ParseTree> children;
	protected List hiddenTokens;

	/** Add t as child of this node.  t must not be nil node. */
	public void addChild(ParseTree t) {
		if ( children==null ) children = new ArrayList<ParseTree>();
		children.add(t);
	}

	public ParseTree getChild(int i) {
		if ( children==null || i>=children.size() ) {
			return null;
		}
		return children.get(i);
	}

	public ParseTree getParent() { return parent; }

	public void setParent(ParseTree t) { parent = t; }

	public String toStringWithHiddenTokens() {
		StringBuffer buf = new StringBuffer();
		if ( hiddenTokens!=null ) {
			for (int i = 0; i < hiddenTokens.size(); i++) {
				Token hidden = (Token) hiddenTokens.get(i);
				buf.append(hidden.getText());
			}
		}
		String nodeText = this.toString();
		if ( !nodeText.equals("<EOF>") ) buf.append(nodeText);
		return buf.toString();
	}

	/** Print out the leaves of this tree, which means printing original
	 *  input back out.
	 */
	public String toInputString() {
		StringBuffer buf = new StringBuffer();
		_toStringLeaves(buf);
		return buf.toString();
	}

	protected void _toStringLeaves(StringBuffer buf) {
		if ( children==null ) { // leaf node token?
			buf.append(this.toStringWithHiddenTokens());
			return;
		}
		for (int i = 0; children!=null && i < children.size(); i++) {
			ParseTree t = children.get(i);
			t._toStringLeaves(buf);
		}
	}
}
