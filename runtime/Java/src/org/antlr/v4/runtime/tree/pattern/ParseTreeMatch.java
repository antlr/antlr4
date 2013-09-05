package org.antlr.v4.runtime.tree.pattern;

import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

public class ParseTreeMatch {
	/** Tree we tried to match */
	protected ParseTree tree;

	/** To what pattern? */
	protected ParseTreePattern pattern;

	protected List<Pair<String,? extends ParseTree>> labels;

	public ParseTreeMatch(ParseTree tree, ParseTreePattern pattern) {
		this.tree = tree;
		this.pattern = pattern;
	}
}
