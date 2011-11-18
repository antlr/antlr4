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
 *  Unlike the common AST stuff in the runtime library, there is no such thing
 *  as a nil node. The payload is either a token or a context object.
 */
public interface ParseTree extends SyntaxTree {
	public interface RuleNode extends ParseTree {
		RuleContext getRuleContext();
	}

	public interface TerminalNode<TSymbol> extends ParseTree {
		TSymbol getSymbol();
	}

	public static class TerminalNodeImpl<TSymbol> implements TerminalNode<TSymbol> {
		public TSymbol symbol;
		public ParseTree parent;
		/** Which ATN node matched this token? */
		public int s;
		public TerminalNodeImpl(TSymbol symbol) {	this.symbol = symbol;	}

		@Override
		public ParseTree getChild(int i) {return null;}

		@Override
		public TSymbol getSymbol() {return symbol;}

		@Override
		public ParseTree getParent() { return parent; }

		@Override
		public TSymbol getPayload() { return symbol; }

		@Override
		public Interval getSourceInterval() {
			if ( symbol ==null ) return Interval.INVALID;

			if (symbol instanceof Token) {
				return new Interval(((Token)symbol).getStartIndex(), ((Token)symbol).getStopIndex());
			} else if (symbol instanceof SyntaxTree) {
				return ((SyntaxTree)symbol).getSourceInterval();
			} else {
				throw new UnsupportedOperationException("This symbol type is not supported by the default implementation.");
			}
		}

		@Override
		public int getChildCount() { return 0; }

		@Override
		public String toString() {
			if (symbol instanceof Token) {
				if ( ((Token)symbol).getType() == Token.EOF ) return "<EOF>";
				return ((Token)symbol).getText();
			} else if (symbol instanceof AST) {
				if (((AST)symbol).getType() == Token.EOF) {
					return "<EOF>";
				} else {
					return ((AST)symbol).getText();
				}
			} else {
				throw new UnsupportedOperationException("This symbol type is not supported by the default implementation.");
			}
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
	public static class ErrorNodeImpl<TSymbol> extends TerminalNodeImpl<TSymbol> {
		public ErrorNodeImpl(TSymbol token) {
			super(token);
		}
//		@Override
//		public String toString() { return "<ERROR:"+super.toString()+">"; }
	}

	// the following methods narrow the return type; they are not additional methods
	@Override
	ParseTree getParent();
	@Override
	ParseTree getChild(int i);
}
