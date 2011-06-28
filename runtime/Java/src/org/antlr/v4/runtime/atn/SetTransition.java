package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.*;

/** A transition containing a set of values */
public class SetTransition extends Transition {
	public IntervalSet label;
	public GrammarAST ast; // ~ of ~atom tree, wildcard node

	public SetTransition(GrammarAST ast, IntervalSet label, ATNState target) {
		super(target);
		this.ast = ast;
		if ( label==null ) label = IntervalSet.of(Token.INVALID_TYPE);
		this.label = label;
	}

	public SetTransition(ATNState target) {
		super(target);
	}

	public IntervalSet label() { return label; }

	public String toString(Grammar g) {
		return label.toString(g);
	}

	public String toString() {
		return label.toString();
	}
}
