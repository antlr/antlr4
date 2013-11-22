/*
 * [The "BSD license"]
 * Copyright (c) 2013 Terence Parr
 * Copyright (c) 2013 Sam Harwell
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.runtime.tree.pattern;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.xpath.XPath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** A pattern like "<ID> = <expr>;" converted to a ParseTree by
 *  ParseTreePatternMatcher.compile().
 */
public class ParseTreePattern {
	public String patternRuleName;
	public String pattern;
	public ParseTree patternTree;

	public ParseTreePattern(String patternRuleName, String pattern, ParseTree patternTree) {
		this.patternRuleName = patternRuleName;
		this.pattern = pattern;
		this.patternTree = patternTree;
	}

	/** Find all nodes in the tree that match xpath and also the tree
	 *  pattern. Return the list of ParseTreeMatch objects for all matches.
	 */
	public static List<ParseTreeMatch> findAll(ParseTree tree, String xpath,
											   String pattern, String patternRuleName,
											   Lexer lexer, Parser parser)
	{
		Collection<ParseTree> subtrees = XPath.findAll(tree, xpath, parser);
		List<ParseTreeMatch> matches = new ArrayList<ParseTreeMatch>();
		ParseTreePatternMatcher p = new ParseTreePatternMatcher(lexer, parser);
		for (ParseTree t : subtrees) {
			ParseTreeMatch match = p.match(t, pattern, patternRuleName);
			if ( !match.succeeded() ) {
				matches.add(match);
			}
		}
		return matches;
	}

	/** Does the tree match the pattern matched as a patternRuleName? */
	public static ParseTreeMatch match(ParseTree tree, String pattern, String patternRuleName,
									   Lexer lexer, Parser parser) {
		ParseTreePatternMatcher p = new ParseTreePatternMatcher(lexer, parser);
		return p.match(tree, pattern, patternRuleName);
	}
}
