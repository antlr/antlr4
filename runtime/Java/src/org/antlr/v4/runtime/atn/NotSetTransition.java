package org.antlr.v4.runtime.atn;

import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.*;

public class NotSetTransition extends SetTransition {
	public NotSetTransition(GrammarAST ast, IntervalSet label, ATNState target) {
		super(ast, label, target);
	}

	public NotSetTransition(ATNState target) {
		super(target);
	}

	@Override
	public String toString(Grammar g) {
		return '~'+super.toString(g);
	}
}
