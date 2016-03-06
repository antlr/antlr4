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

package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;

public class TerminalNodeImpl implements TerminalNode {
	public Token symbol;
	public ParseTree parent;

	public TerminalNodeImpl(Token symbol) {	this.symbol = symbol;	}

	@Override
	public ParseTree getChild(int i) {return null;}

	@Override
	public Token getSymbol() {return symbol;}

	@Override
	public ParseTree getParent() { return parent; }

	@Override
	public Token getPayload() { return symbol; }

	@Override
	public Interval getSourceInterval() {
		if ( symbol ==null ) return Interval.INVALID;

		int tokenIndex = symbol.getTokenIndex();
		return new Interval(tokenIndex, tokenIndex);
	}

	@Override
	public int getChildCount() { return 0; }

	@Override
	public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
		return visitor.visitTerminal(this);
	}

	@Override
	public String getText() { return symbol.getText(); }

	@Override
	public String toStringTree(Parser parser) {
		return toString();
	}

	@Override
	public String toString() {
			if ( symbol.getType() == Token.EOF ) return "<EOF>";
			return symbol.getText();
	}

	@Override
	public String toStringTree() {
		return toString();
	}
}
