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
package org.antlr.v4.runtime;

import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A rule invocation record for parsing and tree parsing.
 *
 *  Contains all of the information about the current rule not stored in the
 *  RuleContext. It handles parse tree children list, Any ATN state
 *  tracing, and the default values available for rule indications:
 *  start, stop, ST, rule index, current alt number, current
 *  ATN state.
 *
 *  Subclasses made for each rule and grammar track the parameters,
 *  return values, locals, and labels specific to that rule. These
 *  are the objects that are returned from rules.
 *
 *  Note text is not an actual property of the return value, it is computed
 *  from start and stop using the input stream's toString() method.  I
 *  could add a ctor to this so that we can pass in and store the input
 *  stream, but I'm not sure we want to do that.  It would seem to be undefined
 *  to get the .text property anyway if the rule matches tokens from multiple
 *  input streams.
 *
 *  I do not use getters for fields of objects that are used simply to
 *  group values such as this aggregate.  The getters/setters are there to
 *  satisfy the superclass interface.
 */
public class ParserRuleContext<Symbol extends Token> extends RuleContext {
	public static final ParserRuleContext<Token> EMPTY = new ParserRuleContext<Token>();

	/** If we are debugging or building a parse tree for a visitor,
	 *  we need to track all of the tokens and rule invocations associated
	 *  with this rule's context. This is empty for normal parsing
	 *  operation because we don't the need to track the details about
	 *  how we parse this rule.
	 */
	public List<ParseTree> children;

	/** For debugging/tracing purposes, we want to track all of the nodes in
	 *  the ATN traversed by the parser for a particular rule.
	 *  This list indicates the sequence of ATN nodes used to match
	 *  the elements of the children list. This list does not include
	 *  ATN nodes and other rules used to match rule invocations. It
	 *  traces the rule invocation node itself but nothing inside that
	 *  other rule's ATN submachine.
	 *
	 *  There is NOT a one-to-one correspondence between the children and
	 *  states list. There are typically many nodes in the ATN traversed
	 *  for each element in the children list. For example, for a rule
	 *  invocation there is the invoking state and the following state.
	 *
	 *  The parser setState() method updates field s and adds it to this list
	 *  if we are debugging/tracing.
     *
     *  This does not trace states visited during prediction.
	 */
//	public List<Integer> states;

	/** Current ATN state number we are executing.
	 *
	 *  Not used during ATN simulation/prediction; only used during parse that updates
	 *  current location in ATN.
	 */
	public int s = -1;

	public Symbol start, stop;

	/** Set during parsing to identify which rule parser is in. */
	public int ruleIndex;

	/** Set during parsing to identify which alt of rule parser is in. */
	public int altNum;

	public ParserRuleContext() { }

	/** COPY a ctx (I'm deliberately not using copy constructor) */
	public void copyFrom(ParserRuleContext<Symbol> ctx) {
		// from RuleContext
		this.parent = ctx.parent;
		this.s = ctx.s;
		this.invokingState = ctx.invokingState;

		this.start = ctx.start;
		this.stop = ctx.stop;
		this.ruleIndex = ctx.ruleIndex;
	}

	public ParserRuleContext(@Nullable ParserRuleContext<Symbol> parent, int invokingStateNumber, int stateNumber) {
		super(parent, invokingStateNumber);
		this.s = stateNumber;
	}

	public ParserRuleContext(@Nullable ParserRuleContext<Symbol> parent, int stateNumber) {
		this(parent, parent!=null ? parent.s : -1 /* invoking state */, stateNumber);
	}

	// Double dispatch methods for listeners

	// parse listener
	public void enterRule(ParseListener<Symbol> listener) { }
	public void exitRule(ParseListener<Symbol> listener) { }

	// parse tree listener
	public void enterRule(ParseTreeListener<Symbol> listener) { }
	public void exitRule(ParseTreeListener<Symbol> listener) { }

	/** Does not set parent link; other add methods do */
	public void addChild(TerminalNode<Symbol> t) {
		if ( children==null ) children = new ArrayList<ParseTree>();
		children.add(t);
	}

	public void addChild(RuleContext ruleInvocation) {
		if ( children==null ) children = new ArrayList<ParseTree>();
		children.add(ruleInvocation);
	}

	/** Used by enterOuterAlt to toss out a RuleContext previously added as
	 *  we entered a rule. If we have # label, we will need to remove
	 *  generic ruleContext object.
 	 */
	public void removeLastChild() {
		if ( children!=null ) {
			children.remove(children.size()-1);
		}
	}

//	public void trace(int s) {
//		if ( states==null ) states = new ArrayList<Integer>();
//		states.add(s);
//	}

	public void addChild(Symbol matchedToken) {
		TerminalNodeImpl<Symbol> t = new TerminalNodeImpl<Symbol>(matchedToken);
		addChild(t);
		t.parent = this;
	}

	public ErrorNode<Symbol> addErrorNode(Symbol badToken) {
		ErrorNodeImpl<Symbol> t = new ErrorNodeImpl<Symbol>(badToken);
		addChild(t);
		t.parent = this;
		return t;
	}

	@Override
	/** Override to make type more specific */
	public ParserRuleContext<Symbol> getParent() {
		return (ParserRuleContext<Symbol>)super.getParent();
	}

	@Override
	public ParseTree getChild(int i) {
		return children!=null && i>=0 && i<children.size() ? children.get(i) : null;
	}

	public <T extends ParseTree> T getChild(Class<? extends T> ctxType, int i) {
		if ( children==null || i < 0 || i >= children.size() ) {
			return null;
		}

		int j = -1; // what element have we found with ctxType?
		for (ParseTree o : children) {
			if ( ctxType.isInstance(o) ) {
				j++;
				if ( j == i ) {
					return ctxType.cast(o);
				}
			}
		}
		return null;
	}

	@SuppressWarnings("checked")
	public TerminalNode<Symbol> getToken(int ttype, int i) {
		if ( children==null || i < 0 || i >= children.size() ) {
			return null;
		}

		int j = -1; // what token with ttype have we found?
		for (ParseTree o : children) {
			if ( o instanceof TerminalNode<?> ) {
				TerminalNode<Symbol> tnode = (TerminalNode<Symbol>)o;
				Token symbol = tnode.getSymbol();
				if ( symbol.getType()==ttype ) {
					j++;
					if ( j == i ) {
						return tnode;
					}
				}
			}
		}

		return null;
	}

	@SuppressWarnings("checked")
	public List<TerminalNode<Symbol>> getTokens(int ttype) {
		if ( children==null ) {
			return Collections.emptyList();
		}

		List<TerminalNode<Symbol>> tokens = null;
		for (ParseTree o : children) {
			if ( o instanceof TerminalNode<?> ) {
				TerminalNode<Symbol> tnode = (TerminalNode<Symbol>)o;
				Token symbol = tnode.getSymbol();
				if ( symbol.getType()==ttype ) {
					if ( tokens==null ) {
						tokens = new ArrayList<TerminalNode<Symbol>>();
					}
					tokens.add(tnode);
				}
			}
		}

		if ( tokens==null ) {
			return Collections.emptyList();
		}

		return tokens;
	}

	public <T extends ParserRuleContext<?>> T getRuleContext(Class<? extends T> ctxType, int i) {
		return getChild(ctxType, i);
	}

	public <T extends ParserRuleContext<?>> List<? extends T> getRuleContexts(Class<? extends T> ctxType) {
		if ( children==null ) {
			return Collections.emptyList();
		}

		List<T> contexts = null;
		for (ParseTree o : children) {
			if ( ctxType.isInstance(o) ) {
				if ( contexts==null ) {
					contexts = new ArrayList<T>();
				}

				contexts.add(ctxType.cast(o));
			}
		}

		if ( contexts==null ) {
			return Collections.emptyList();
		}

		return contexts;
	}

	@Override
	public int getChildCount() { return children!=null ? children.size() : 0; }

	@Override
	public int getRuleIndex() { return ruleIndex; }

	public Symbol getStart() { return start; }
	public Symbol getStop() { return stop; }

	@Override
	public String toString(@NotNull Recognizer<?,?> recog, RuleContext stop) {
		if ( recog==null ) return super.toString(recog, stop);
		StringBuilder buf = new StringBuilder();
		ParserRuleContext<?> p = this;
		buf.append("[");
		while ( p != null && p != stop ) {
			ATN atn = recog.getATN();
			ATNState s = atn.states.get(p.s);
			String ruleName = recog.getRuleNames()[s.ruleIndex];
			buf.append(ruleName);
			if ( p.parent != null ) buf.append(" ");
			p = (ParserRuleContext<?>)p.parent;
		}
		buf.append("]");
		return buf.toString();
	}

    /** Used for rule context info debugging during parse-time, not so much for ATN debugging */
    public String toInfoString(Parser recognizer) {
        List<String> rules = recognizer.getRuleInvocationStack(this);
        Collections.reverse(rules);
        return "ParserRuleContext"+rules+"{" +
                "altNum=" + altNum +
                ", start=" + start +
                ", stop=" + stop +
                '}';
    }
}
