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

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;

/** An interface to access the tree of RuleContext objects created
 *  during a parse that makes the data structure look like a simple parse tree.
 *  This node represents both internal nodes, rule invocations,
 *  and leaf nodes, token matches.
 *
 *  The payload is either a token or a context object.
 */
public interface ParseTree extends SyntaxTree {
	public interface RuleNode extends ParseTree {
		RuleContext getRuleContext();
	}

	public interface TerminalNode<Symbol extends Token> extends ParseTree {
		Symbol getSymbol();
	}

	public static class TerminalNodeImpl<Symbol extends Token> implements TerminalNode<Symbol> {
		public Symbol symbol;
		public ParseTree parent;
		/** Which ATN node matched this token? */
		public int s;
		public TerminalNodeImpl(Symbol symbol) {	this.symbol = symbol;	}

		@Override
		public ParseTree getChild(int i) {return null;}

		@Override
		public Symbol getSymbol() {return symbol;}

		@Override
		public ParseTree getParent() { return parent; }

		@Override
		public Symbol getPayload() { return symbol; }

		@Override
		public Interval getSourceInterval() {
			if ( symbol ==null ) return Interval.INVALID;

			return new Interval(symbol.getStartIndex(), symbol.getStopIndex());
		}

		@Override
		public int getChildCount() { return 0; }

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

	/** Represents a token that was consumed during resynchronization
	 *  rather than during a valid match operation. For example,
	 *  we will create this kind of a node during single token insertion
	 *  and deletion as well as during "consume until error recovery set"
	 *  upon no viable alternative exceptions.
	 */
	public static class ErrorNode<Symbol extends Token> extends TerminalNodeImpl<Symbol> {
		public ErrorNode(Symbol token) {
			super(token);
		}
	}

	// the following methods narrow the return type; they are not additional methods
	@Override
	ParseTree getParent();
	@Override
	ParseTree getChild(int i);
}
