package org.antlr.v4.runtime.tree.pattern;

import org.antlr.v4.runtime.tree.ParseTree;

public class Pattern {
	public static final Pattern WildcardPattern =
		new Pattern("...") {
			public boolean matches(ParseTree t) {
				return true;
			}
		};

	protected String pattern;

	public Pattern(String pattern) {
		this.pattern = pattern;
	}

	public boolean matches(ParseTree t) {
		return false;
	}
}
