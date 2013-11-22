package org.antlr.v4.runtime.tree.pattern;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.xpath.XPath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

	public static List<ParseTreeMatch> findAll(ParseTree tree, String xpath,
											   String pattern, String patternRuleName,
											   Lexer lexer, Parser parser)
	{
		Collection<ParseTree> subtrees = XPath.findAll(tree, xpath, parser);
		List<ParseTreeMatch> matches = new ArrayList<ParseTreeMatch>();
		ParseTreePatternMatcher p = new ParseTreePatternMatcher(lexer, parser);
		for (ParseTree t : subtrees) {
			ParseTreeMatch match = p.match(t, pattern, patternRuleName);
			boolean matched = match.getMismatchedNode() == null;
			if ( matched ) {
				matches.add(match);
			}
		}
		return matches;
	}

	public static ParseTreeMatch match(ParseTree tree, String pattern, String patternRuleName,
									   Lexer lexer, Parser parser) {
		ParseTreePatternMatcher p = new ParseTreePatternMatcher(lexer, parser);
		return p.match(tree, pattern, patternRuleName);
	}
}
