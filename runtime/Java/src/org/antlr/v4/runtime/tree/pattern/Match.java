package org.antlr.v4.runtime.tree.pattern;

import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

public class Match {
	protected ParseTree subtree;
	protected List<Pair<String,? extends ParseTree>> labels;
}
