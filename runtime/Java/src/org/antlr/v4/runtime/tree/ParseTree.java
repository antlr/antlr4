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

import org.antlr.v4.runtime.Token;

import java.util.List;

/** A record of the rules used to match a token sequence.  The tokens
 *  end up as the leaves of this tree and rule nodes are the interior nodes.
 *  This really adds no functionality, it is just an alias for CommonTree
 *  that is more meaningful (specific) and holds a String to display for a node.
 */
public class ParseTree extends BaseTree {
	public Object payload;
	public List hiddenTokens;

	public ParseTree(Object label) {
		this.payload = label;
	}

	public Tree dupNode() {
		return null;
	}

	public int getType() {
		return 0;
	}

	public String getText() {
		return toString();
	}

	public int getTokenStartIndex() {
		return 0;
	}

	public void setTokenStartIndex(int index) {
	}

	public int getTokenStopIndex() {
		return 0;
	}

	public void setTokenStopIndex(int index) {
	}

	public String toString() {
		if ( payload instanceof Token ) {
			Token t = (Token)payload;
			if ( t.getType() == Token.EOF ) {
				return "<EOF>";
			}
			return t.getText();
		}
		return payload.toString();
	}

	/** Emit a token and all hidden nodes before.  EOF node holds all
	 *  hidden tokens after last real token.
	 */
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

	public void _toStringLeaves(StringBuffer buf) {
		if ( payload instanceof Token ) { // leaf node token?
			buf.append(this.toStringWithHiddenTokens());
			return;
		}
		for (int i = 0; children!=null && i < children.size(); i++) {
			ParseTree t = (ParseTree)children.get(i);
			t._toStringLeaves(buf);
		}
	}
}
