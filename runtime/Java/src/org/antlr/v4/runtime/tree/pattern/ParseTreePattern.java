package org.antlr.v4.runtime.tree.pattern;

import org.antlr.v4.runtime.tree.ParseTree;

/** A pattern like "<ID> = <expr>;" converted to a ParseTree */
public class ParseTreePattern {
	public String patternRuleName;
	public String pattern;
	public ParseTree patternTree;

	public ParseTreePattern(String patternRuleName, String pattern, ParseTree patternTree) {
		this.patternRuleName = patternRuleName;
		this.pattern = pattern;
		this.patternTree = patternTree;
	}

	public boolean matches(ParseTree t) {
		return false;
	}
}
