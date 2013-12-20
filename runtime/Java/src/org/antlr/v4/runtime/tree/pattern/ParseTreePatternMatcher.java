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

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ListTokenSource;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserInterpreter;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** A tree pattern matching mechanism for ANTLR ParseTrees.
 *
 *  Patterns are strings of source input text with special tags
 *  representing token or rule references such as:
 *
 *  "<ID> = <expr>;"
 *
 *  Given a pattern start rule such as statement, this object
 *  construct a parse tree with placeholders for the identifier and
 *  expression subtree. Then the match() routines can compare an
 *  actual parse tree from a parse with this pattern. Tag <ID> matches
 *  any ID token and tag <expr> references any expression subtree.
 *
 *  Pattern "x = 0;" is a similar pattern that matches the same
 *  pattern except that it requires the identifier to be x and the
 *  expression to be 0.
 *
 *  The matches() routines return true or false based upon a match for
 *  the tree rooted at the parameter sent in. The match() routines
 *  return a ParseTreeMatch object that contains the parse tree, the
 *  parse tree pattern, and a map from tag name to matched nodes (more
 *  below). A subtree that fails to match, returns with
 *  ParseTreeMatch.mismatchedNode set to the first tree node that did
 *  not match.
 *
 *  For efficiency, you can compile a tree pattern in string form to a
 *  ParseTreePattern object.
 *
 *  See TestParseTreeMatcher for lots of examples. ParseTreePattern
 *  has two static helper methods: findAll() and match() that are easy
 *  to use but not superefficient because they create new
 *  ParseTreePatternMatcher objects each time and have to compile the
 *  pattern in string form before using it.
 *
 *  The lexer and parser that you pass into the
 *  ParseTreePatternMatcher constructor are used to parse the pattern
 *  in string form. The lexer converts the "<ID> = <expr>;" into a
 *  sequence of four tokens (assuming lexer throws out whitespace or
 *  puts it on a hidden channel). Be aware that the input stream is
 *  reset for the lexer (but not the parser; a ParserInterpreter is
 *  created to parse the input.). Any user-defined fields you have put
 *  into the lexer might get changed when this mechanism asks it to
 *  scan the pattern string.
 *
 *  Normally a parser does not accept token "<expr>" as a valid expr
 *  but, from the parser passed in, we create a special version of the
 *  underlying grammar representation (an ATN) that allows imaginary
 *  tokens representing rules (<expr>) to match entire rules.
 *  We call these bypass alternatives.
 *
 *  Delimiters are < and > with \ as the escape string by default, but
 *  you can set them to whatever you want using setDelimiters(). You
 *  must escape both start and stop strings \< and \>.
 */
public class ParseTreePatternMatcher {
	public static class CannotInvokeStartRule extends RuntimeException {
		public CannotInvokeStartRule(Throwable e) {
			super(e);
		}
	}

	/** Used to convert the tree pattern string into a series of tokens.
	 *  The input stream is reset.
	 */
	protected Lexer lexer;

	/** Used to collect to the grammar file name, token names, rule names
	 *  for used to parse the pattern into a parse tree.
	 */
	protected Parser parser;

	protected String start = "<", stop=">";
	protected String escape = "\\"; // e.g., \< and \> must escape BOTH!

	/** Constructs a pattern match or from a lecture and parser object.
	 *  The lexer input stream is altered for tokenizing the tree patterns.
	 *  The parser is used as a convenient mechanism to get the grammar name,
	 *  plus token, rule names.
	 */
	public ParseTreePatternMatcher(Lexer lexer, Parser parser) {
		this.lexer = lexer;
		this.parser = parser;
	}

	public void setDelimiters(String start, String stop, String escapeLeft) {
		this.start = start;
		this.stop = stop;
		this.escape = escapeLeft;
	}

	/** Does pattern matched as rule patternRuleIndex match tree? */
	public boolean matches(ParseTree tree, String pattern, int patternRuleIndex) {
		ParseTreePattern p = compile(pattern, patternRuleIndex);
		return matches(tree, p);
	}

	/** Does pattern matched as rule patternRuleIndex match tree? Pass in a
	 *  compiled pattern instead of a string representation of a tree pattern.
	 */
	public boolean matches(ParseTree tree, ParseTreePattern pattern) {
		ParseTreeMatch match = new ParseTreeMatch(tree, pattern);
		matches_(tree, pattern.patternTree, match);
		return match.succeeded();
	}

	/** Compare pattern matched as rule patternRuleIndex against tree and
	 *  return a ParseTreeMatch object that contains the matched elements,
	 *  or the node at which the match failed.
	 */
	public ParseTreeMatch match(ParseTree tree, String pattern, int patternRuleIndex) {
		ParseTreePattern p = compile(pattern, patternRuleIndex);
		return match(tree, p);
	}

	/** Compare pattern matched against tree and
	 *  return a ParseTreeMatch object that contains the matched elements,
	 *  or the node at which the match failed. Pass in a compiled pattern
	 *  instead of a string representation of a tree pattern.
	 */
	public ParseTreeMatch match(ParseTree tree, ParseTreePattern pattern) {
		ParseTreeMatch match = new ParseTreeMatch(tree, pattern);
		matches_(tree, pattern.patternTree, match);
		return match;
	}

	/** For repeated use of a tree pattern, compile it to a ParseTreePattern
	 *  using this method.
	 */
	public ParseTreePattern compile(String pattern, int patternRuleIndex) {
		List<? extends Token> tokenList = tokenize(pattern);
		ListTokenSource tokenSrc = new ListTokenSource(tokenList);
		CommonTokenStream tokens = new CommonTokenStream(tokenSrc);

		ParserInterpreter parserInterp = new ParserInterpreter(parser.getGrammarFileName(),
															   Arrays.asList(parser.getTokenNames()),
															   Arrays.asList(parser.getRuleNames()),
															   parser.getATNWithBypassAlts(),
															   tokens);

		ParseTree tree = null;
		try {
			tree = parserInterp.parse(patternRuleIndex);
//			System.out.println("pattern tree = "+tree.toStringTree(parserInterp));
		}
		catch (Exception e) {
			throw new CannotInvokeStartRule(e);
		}

		return new ParseTreePattern(this, pattern, patternRuleIndex, tree);
	}

	public Lexer getLexer() {
		return lexer;
	}

	public Parser getParser() {
		return parser;
	}

	// ---- SUPPORT CODE ----

	/** Recursively walk tree against patternTree, filling match.labels */
	protected boolean matches_(ParseTree tree,
							   ParseTree patternTree,
							   ParseTreeMatch match)
	{
		if ( tree==null || patternTree==null ) {
			return false;
		}
		// x and <ID>, x and y, or x and x; or could be mismatched types
		if ( tree instanceof TerminalNode && patternTree instanceof TerminalNode ) {
			TerminalNode t1 = (TerminalNode)tree;
			TerminalNode t2 = (TerminalNode)patternTree;
			ParseTreeMatch m = null;
			// both are tokens and they have same type
			if ( t1.getSymbol().getType() == t2.getSymbol().getType() ) {
				if ( t2.getSymbol() instanceof TokenTagToken ) { // x and <ID>
					TokenTagToken tokenTagToken = (TokenTagToken)t2.getSymbol();
					// track label->list-of-nodes for both token name and label (if any)
					match.labels.map(tokenTagToken.tokenName, tree);
					if ( tokenTagToken.label!=null ) {
						match.labels.map(tokenTagToken.label, tree);
					}
				}
				else if ( t1.getText().equals(t2.getText()) ) { // x and x
				}
				else { // x and y
					match.mismatchedNode = t1;
				}
			}
			else {
				match.mismatchedNode = t1;
			}
			return match.succeeded();
		}
		if ( tree instanceof ParserRuleContext && patternTree instanceof ParserRuleContext ) {
			ParserRuleContext r1 = (ParserRuleContext)tree;
			ParserRuleContext r2 = (ParserRuleContext)patternTree;
			// (expr ...) and <expr>
			RuleTagToken ruleTagToken = getRuleTagToken(r2);
			if ( ruleTagToken!=null ) {
				ParseTreeMatch m = null;
				if ( r1.getRuleContext().getRuleIndex() == r2.getRuleContext().getRuleIndex() ) {
					// track label->list-of-nodes for both rule name and label (if any)
					match.labels.map(ruleTagToken.ruleName, tree);
					if ( ruleTagToken.label!=null ) {
						match.labels.map(ruleTagToken.label, tree);
					}
				}
				else {
					match.mismatchedNode = r1;
				}
				return match.succeeded();
			}
			// (expr ...) and (expr ...)
			if ( r1.getChildCount()!=r2.getChildCount() ) {
				match.mismatchedNode = r1;
				return false;
			}
			int n = r1.getChildCount();
			for (int i = 0; i<n; i++) {
				boolean childMatch =
					matches_(r1.getChild(i), patternTree.getChild(i), match);
				if ( !childMatch ) return false;
			}
			return true;
		}
		// if nodes aren't both tokens or both rule nodes, can't match
		match.mismatchedNode = tree;
		return false;
	}

	/** Is t (expr <expr>) subtree? */
	protected RuleTagToken getRuleTagToken(ParseTree t) {
		if ( t instanceof RuleNode ) {
			RuleNode r = (RuleNode)t;
			if ( r.getChildCount()==1 && r.getChild(0) instanceof TerminalNode ) {
				TerminalNode c = (TerminalNode)r.getChild(0);
				if ( c.getSymbol() instanceof RuleTagToken ) {
//					System.out.println("rule tag subtree "+t.toStringTree(parser));
					return (RuleTagToken)c.getSymbol();
				}
			}
		}
		return null;
	}

	public List<? extends Token> tokenize(String pattern) {
		// split pattern into chunks: sea (raw input) and islands (<ID>, <expr>)
		List<Chunk> chunks = split(pattern);

		// create token stream from text and tags
		List<Token> tokens = new ArrayList<Token>();
		for (Chunk chunk : chunks) {
			if ( chunk instanceof TagChunk ) {
				TagChunk tagChunk = (TagChunk)chunk;
				// add special rule token or conjure up new token from name
				if ( Character.isUpperCase(tagChunk.tag.charAt(0)) ) {
					Integer ttype = parser.getTokenType(tagChunk.tag);
					if ( ttype==Token.INVALID_TYPE ) {
						throw new IllegalArgumentException("Unknown token "+tagChunk.tag+" in pattern: "+pattern);
					}
					TokenTagToken t = new TokenTagToken(tagChunk.tag, ttype, tagChunk.label);
					tokens.add(t);
				}
				else if ( Character.isLowerCase(tagChunk.tag.charAt(0)) ) {
					int ruleIndex = parser.getRuleIndex(tagChunk.tag);
					if ( ruleIndex==-1 ) {
						throw new IllegalArgumentException("Unknown rule "+tagChunk.tag+" in pattern: "+pattern);
					}
					int ruleImaginaryTokenType = parser.getATNWithBypassAlts().ruleToTokenType[ruleIndex];
					tokens.add(new RuleTagToken(tagChunk.tag, ruleImaginaryTokenType, tagChunk.label));
				}
				else {
					throw new IllegalArgumentException("invalid tag: "+tagChunk.tag+" in pattern: "+pattern);
				}
			}
			else {
				TextChunk textChunk = (TextChunk)chunk;
				try {
					ANTLRInputStream in = new ANTLRInputStream(new StringReader(textChunk.text));
					lexer.setInputStream(in);
					Token t = lexer.nextToken();
					while ( t.getType()!=Token.EOF ) {
						tokens.add(t);
						t = lexer.nextToken();
					}
				}
				catch (IOException ioe) {
					// -----------------
					throw new IllegalArgumentException("IOException lexing pattern: "+pattern, ioe);
				}
			}
		}

//		System.out.println("tokens="+tokens);
		return tokens;
	}

	/** Split "<ID> = <e:expr> ;" into 4 chunks for tokenizing by tokenize() */
	public List<Chunk> split(String pattern) {
		int p = 0;
		int n = pattern.length();
		List<Chunk> chunks = new ArrayList<Chunk>();
		StringBuffer buf = new StringBuffer();
		// find all start and stop indexes first, then collect
		List<Integer> starts = new ArrayList<Integer>();
		List<Integer> stops = new ArrayList<Integer>();
		while ( p<n ) {
			if ( p == pattern.indexOf(escape+start,p) ) {
				p += escape.length() + start.length();
			}
			else if ( p == pattern.indexOf(escape+stop,p) ) {
				p += escape.length() + stop.length();
			}
			else if ( p == pattern.indexOf(start,p) ) {
				starts.add(p);
				p += start.length();
			}
			else if ( p == pattern.indexOf(stop,p) ) {
				stops.add(p);
				p += stop.length();
			}
			else {
				p++;
			}
		}

//		System.out.println("");
//		System.out.println(starts);
//		System.out.println(stops);
		if ( starts.size() > stops.size() ) {
			throw new IllegalArgumentException("unterminated tag in pattern: "+pattern);
		}

		if ( starts.size() < stops.size() ) {
			throw new IllegalArgumentException("missing start tag in pattern: "+pattern);
		}

		int ntags = starts.size();
		for (int i=0; i<ntags; i++) {
			if ( starts.get(i)>=stops.get(i) ) {
				throw new IllegalArgumentException("tag delimiters out of order in pattern: "+pattern);
			}
		}

		// collect into chunks now
		if ( ntags==0 ) {
			String text = pattern.substring(0, n);
			chunks.add(new TextChunk(text));
		}

		if ( ntags>0 && starts.get(0)>0 ) { // copy text up to first tag into chunks
			String text = pattern.substring(0, starts.get(0));
			chunks.add(new TextChunk(text));
		}
		for (int i=0; i<ntags; i++) {
			// copy inside of <tag>
			String tag = pattern.substring(starts.get(i) + start.length(), stops.get(i));
			String ruleOrToken = tag;
			String label = null;
			int colon = tag.indexOf(':');
			if ( colon >= 0 ) {
				label = tag.substring(0,colon);
				ruleOrToken = tag.substring(colon+1, tag.length());
			}
			chunks.add(new TagChunk(label, ruleOrToken));
			if ( i+1 < ntags ) {
				// copy from end of <tag> to start of next
				String text = pattern.substring(stops.get(i) + stop.length(), starts.get(i + 1));
				chunks.add(new TextChunk(text));
			}
		}
		if ( ntags>0 ) {
			int afterLastTag = stops.get(ntags - 1) + stop.length();
			if ( afterLastTag < n ) { // copy text from end of last tag to end
				String text = pattern.substring(afterLastTag, n);
				chunks.add(new TextChunk(text));
			}
		}

		// strip out the escape sequences from text chunks but not tags
		for (Chunk c : chunks) {
			if ( c instanceof TextChunk ) {
				TextChunk tc = (TextChunk)c;
				tc.text = tc.text.replace(escape, "");
			}
		}

		return chunks;
	}
}
