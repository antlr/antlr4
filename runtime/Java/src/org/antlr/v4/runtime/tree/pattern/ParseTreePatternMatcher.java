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
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ListTokenSource;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserInterpreter;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.misc.MultiMap;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A tree pattern matching mechanism for ANTLR {@link ParseTree}s.
 *
 * <p>Patterns are strings of source input text with special tags representing
 * token or rule references such as:</p>
 *
 * <p>{@code <ID> = <expr>;}</p>
 *
 * <p>Given a pattern start rule such as {@code statement}, this object constructs
 * a {@link ParseTree} with placeholders for the {@code ID} and {@code expr}
 * subtree. Then the {@link #match} routines can compare an actual
 * {@link ParseTree} from a parse with this pattern. Tag {@code <ID>} matches
 * any {@code ID} token and tag {@code <expr>} references the result of the
 * {@code expr} rule (generally an instance of {@code ExprContext}.</p>
 *
 * <p>Pattern {@code x = 0;} is a similar pattern that matches the same pattern
 * except that it requires the identifier to be {@code x} and the expression to
 * be {@code 0}.</p>
 *
 * <p>The {@link #matches} routines return {@code true} or {@code false} based
 * upon a match for the tree rooted at the parameter sent in. The
 * {@link #match} routines return a {@link ParseTreeMatch} object that
 * contains the parse tree, the parse tree pattern, and a map from tag name to
 * matched nodes (more below). A subtree that fails to match, returns with
 * {@link ParseTreeMatch#mismatchedNode} set to the first tree node that did not
 * match.</p>
 *
 * <p>For efficiency, you can compile a tree pattern in string form to a
 * {@link ParseTreePattern} object.</p>
 *
 * <p>See {@code TestParseTreeMatcher} for lots of examples.
 * {@link ParseTreePattern} has two static helper methods:
 * {@link ParseTreePattern#findAll} and {@link ParseTreePattern#match} that
 * are easy to use but not super efficient because they create new
 * {@link ParseTreePatternMatcher} objects each time and have to compile the
 * pattern in string form before using it.</p>
 *
 * <p>The lexer and parser that you pass into the {@link ParseTreePatternMatcher}
 * constructor are used to parse the pattern in string form. The lexer converts
 * the {@code <ID> = <expr>;} into a sequence of four tokens (assuming lexer
 * throws out whitespace or puts it on a hidden channel). Be aware that the
 * input stream is reset for the lexer (but not the parser; a
 * {@link ParserInterpreter} is created to parse the input.). Any user-defined
 * fields you have put into the lexer might get changed when this mechanism asks
 * it to scan the pattern string.</p>
 *
 * <p>Normally a parser does not accept token {@code <expr>} as a valid
 * {@code expr} but, from the parser passed in, we create a special version of
 * the underlying grammar representation (an {@link ATN}) that allows imaginary
 * tokens representing rules ({@code <expr>}) to match entire rules. We call
 * these <em>bypass alternatives</em>.</p>
 *
 * <p>Delimiters are {@code <} and {@code >}, with {@code \} as the escape string
 * by default, but you can set them to whatever you want using
 * {@link #setDelimiters}. You must escape both start and stop strings
 * {@code \<} and {@code \>}.</p>
 */
public class ParseTreePatternMatcher {
	public static class CannotInvokeStartRule extends RuntimeException {
		public CannotInvokeStartRule(Throwable e) {
			super(e);
		}
	}

	// Fixes https://github.com/antlr/antlr4/issues/413
	// "Tree pattern compilation doesn't check for a complete parse"
	public static class StartRuleDoesNotConsumeFullPattern extends RuntimeException {
	}

	/**
	 * This is the backing field for {@link #getLexer()}.
	 */
	private final Lexer lexer;

	/**
	 * This is the backing field for {@link #getParser()}.
	 */
	private final Parser parser;

	protected String start = "<";
	protected String stop = ">";
	protected String escape = "\\"; // e.g., \< and \> must escape BOTH!

	/**
	 * Constructs a {@link ParseTreePatternMatcher} or from a {@link Lexer} and
	 * {@link Parser} object. The lexer input stream is altered for tokenizing
	 * the tree patterns. The parser is used as a convenient mechanism to get
	 * the grammar name, plus token, rule names.
	 */
	public ParseTreePatternMatcher(Lexer lexer, Parser parser) {
		this.lexer = lexer;
		this.parser = parser;
	}

	/**
	 * Set the delimiters used for marking rule and token tags within concrete
	 * syntax used by the tree pattern parser.
	 *
	 * @param start The start delimiter.
	 * @param stop The stop delimiter.
	 * @param escapeLeft The escape sequence to use for escaping a start or stop delimiter.
	 *
	 * @exception IllegalArgumentException if {@code start} is {@code null} or empty.
	 * @exception IllegalArgumentException if {@code stop} is {@code null} or empty.
	 */
	public void setDelimiters(String start, String stop, String escapeLeft) {
		if (start == null || start.isEmpty()) {
			throw new IllegalArgumentException("start cannot be null or empty");
		}

		if (stop == null || stop.isEmpty()) {
			throw new IllegalArgumentException("stop cannot be null or empty");
		}

		this.start = start;
		this.stop = stop;
		this.escape = escapeLeft;
	}

	/** Does {@code pattern} matched as rule {@code patternRuleIndex} match {@code tree}? */
	public boolean matches(ParseTree tree, String pattern, int patternRuleIndex) {
		ParseTreePattern p = compile(pattern, patternRuleIndex);
		return matches(tree, p);
	}

	/** Does {@code pattern} matched as rule patternRuleIndex match tree? Pass in a
	 *  compiled pattern instead of a string representation of a tree pattern.
	 */
	public boolean matches(ParseTree tree, ParseTreePattern pattern) {
		MultiMap<String, ParseTree> labels = new MultiMap<String, ParseTree>();
		ParseTree mismatchedNode = matchImpl(tree, pattern.getPatternTree(), labels);
		return mismatchedNode == null;
	}

	/**
	 * Compare {@code pattern} matched as rule {@code patternRuleIndex} against
	 * {@code tree} and return a {@link ParseTreeMatch} object that contains the
	 * matched elements, or the node at which the match failed.
	 */
	public ParseTreeMatch match(ParseTree tree, String pattern, int patternRuleIndex) {
		ParseTreePattern p = compile(pattern, patternRuleIndex);
		return match(tree, p);
	}

	/**
	 * Compare {@code pattern} matched against {@code tree} and return a
	 * {@link ParseTreeMatch} object that contains the matched elements, or the
	 * node at which the match failed. Pass in a compiled pattern instead of a
	 * string representation of a tree pattern.
	 */
	@NotNull
	public ParseTreeMatch match(@NotNull ParseTree tree, @NotNull ParseTreePattern pattern) {
		MultiMap<String, ParseTree> labels = new MultiMap<String, ParseTree>();
		ParseTree mismatchedNode = matchImpl(tree, pattern.getPatternTree(), labels);
		return new ParseTreeMatch(tree, pattern, labels, mismatchedNode);
	}

	/**
	 * For repeated use of a tree pattern, compile it to a
	 * {@link ParseTreePattern} using this method.
	 */
	public ParseTreePattern compile(String pattern, int patternRuleIndex) {
		List<? extends Token> tokenList = tokenize(pattern);
		ListTokenSource tokenSrc = new ListTokenSource(tokenList);
		CommonTokenStream tokens = new CommonTokenStream(tokenSrc);

		ParserInterpreter parserInterp = new ParserInterpreter(parser.getGrammarFileName(),
															   parser.getVocabulary(),
															   Arrays.asList(parser.getRuleNames()),
															   parser.getATNWithBypassAlts(),
															   tokens);

		ParseTree tree = null;
		try {
			parserInterp.setErrorHandler(new BailErrorStrategy());
			tree = parserInterp.parse(patternRuleIndex);
//			System.out.println("pattern tree = "+tree.toStringTree(parserInterp));
		}
		catch (ParseCancellationException e) {
			throw (RecognitionException)e.getCause();
		}
		catch (RecognitionException re) {
			throw re;
		}
		catch (Exception e) {
			throw new CannotInvokeStartRule(e);
		}

		// Make sure tree pattern compilation checks for a complete parse
		if ( tokens.LA(1)!=Token.EOF ) {
			throw new StartRuleDoesNotConsumeFullPattern();
		}

		return new ParseTreePattern(this, pattern, patternRuleIndex, tree);
	}

	/**
	 * Used to convert the tree pattern string into a series of tokens. The
	 * input stream is reset.
	 */
	@NotNull
	public Lexer getLexer() {
		return lexer;
	}

	/**
	 * Used to collect to the grammar file name, token names, rule names for
	 * used to parse the pattern into a parse tree.
	 */
	@NotNull
	public Parser getParser() {
		return parser;
	}

	// ---- SUPPORT CODE ----

	/**
	 * Recursively walk {@code tree} against {@code patternTree}, filling
	 * {@code match.}{@link ParseTreeMatch#labels labels}.
	 *
	 * @return the first node encountered in {@code tree} which does not match
	 * a corresponding node in {@code patternTree}, or {@code null} if the match
	 * was successful. The specific node returned depends on the matching
	 * algorithm used by the implementation, and may be overridden.
	 */
	@Nullable
	protected ParseTree matchImpl(@NotNull ParseTree tree,
								  @NotNull ParseTree patternTree,
								  @NotNull MultiMap<String, ParseTree> labels)
	{
		if (tree == null) {
			throw new IllegalArgumentException("tree cannot be null");
		}

		if (patternTree == null) {
			throw new IllegalArgumentException("patternTree cannot be null");
		}

		// x and <ID>, x and y, or x and x; or could be mismatched types
		if ( tree instanceof TerminalNode && patternTree instanceof TerminalNode ) {
			TerminalNode t1 = (TerminalNode)tree;
			TerminalNode t2 = (TerminalNode)patternTree;
			ParseTree mismatchedNode = null;
			// both are tokens and they have same type
			if ( t1.getSymbol().getType() == t2.getSymbol().getType() ) {
				if ( t2.getSymbol() instanceof TokenTagToken ) { // x and <ID>
					TokenTagToken tokenTagToken = (TokenTagToken)t2.getSymbol();
					// track label->list-of-nodes for both token name and label (if any)
					labels.map(tokenTagToken.getTokenName(), tree);
					if ( tokenTagToken.getLabel()!=null ) {
						labels.map(tokenTagToken.getLabel(), tree);
					}
				}
				else if ( t1.getText().equals(t2.getText()) ) {
					// x and x
				}
				else {
					// x and y
					if (mismatchedNode == null) {
						mismatchedNode = t1;
					}
				}
			}
			else {
				if (mismatchedNode == null) {
					mismatchedNode = t1;
				}
			}

			return mismatchedNode;
		}

		if ( tree instanceof ParserRuleContext && patternTree instanceof ParserRuleContext ) {
			ParserRuleContext r1 = (ParserRuleContext)tree;
			ParserRuleContext r2 = (ParserRuleContext)patternTree;
			ParseTree mismatchedNode = null;
			// (expr ...) and <expr>
			RuleTagToken ruleTagToken = getRuleTagToken(r2);
			if ( ruleTagToken!=null ) {
				ParseTreeMatch m = null;
				if ( r1.getRuleContext().getRuleIndex() == r2.getRuleContext().getRuleIndex() ) {
					// track label->list-of-nodes for both rule name and label (if any)
					labels.map(ruleTagToken.getRuleName(), tree);
					if ( ruleTagToken.getLabel()!=null ) {
						labels.map(ruleTagToken.getLabel(), tree);
					}
				}
				else {
					if (mismatchedNode == null) {
						mismatchedNode = r1;
					}
				}

				return mismatchedNode;
			}

			// (expr ...) and (expr ...)
			if ( r1.getChildCount()!=r2.getChildCount() ) {
				if (mismatchedNode == null) {
					mismatchedNode = r1;
				}

				return mismatchedNode;
			}

			int n = r1.getChildCount();
			for (int i = 0; i<n; i++) {
				ParseTree childMatch = matchImpl(r1.getChild(i), patternTree.getChild(i), labels);
				if ( childMatch != null ) {
					return childMatch;
				}
			}

			return mismatchedNode;
		}

		// if nodes aren't both tokens or both rule nodes, can't match
		return tree;
	}

	/** Is {@code t} {@code (expr <expr>)} subtree? */
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
				if ( Character.isUpperCase(tagChunk.getTag().charAt(0)) ) {
					Integer ttype = parser.getTokenType(tagChunk.getTag());
					if ( ttype==Token.INVALID_TYPE ) {
						throw new IllegalArgumentException("Unknown token "+tagChunk.getTag()+" in pattern: "+pattern);
					}
					TokenTagToken t = new TokenTagToken(tagChunk.getTag(), ttype, tagChunk.getLabel());
					tokens.add(t);
				}
				else if ( Character.isLowerCase(tagChunk.getTag().charAt(0)) ) {
					int ruleIndex = parser.getRuleIndex(tagChunk.getTag());
					if ( ruleIndex==-1 ) {
						throw new IllegalArgumentException("Unknown rule "+tagChunk.getTag()+" in pattern: "+pattern);
					}
					int ruleImaginaryTokenType = parser.getATNWithBypassAlts().ruleToTokenType[ruleIndex];
					tokens.add(new RuleTagToken(tagChunk.getTag(), ruleImaginaryTokenType, tagChunk.getLabel()));
				}
				else {
					throw new IllegalArgumentException("invalid tag: "+tagChunk.getTag()+" in pattern: "+pattern);
				}
			}
			else {
				TextChunk textChunk = (TextChunk)chunk;
				ANTLRInputStream in = new ANTLRInputStream(textChunk.getText());
				lexer.setInputStream(in);
				Token t = lexer.nextToken();
				while ( t.getType()!=Token.EOF ) {
					tokens.add(t);
					t = lexer.nextToken();
				}
			}
		}

//		System.out.println("tokens="+tokens);
		return tokens;
	}

	/** Split {@code <ID> = <e:expr> ;} into 4 chunks for tokenizing by {@link #tokenize}. */
	public List<Chunk> split(String pattern) {
		int p = 0;
		int n = pattern.length();
		List<Chunk> chunks = new ArrayList<Chunk>();
		StringBuilder buf = new StringBuilder();
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
		for (int i = 0; i < chunks.size(); i++) {
			Chunk c = chunks.get(i);
			if ( c instanceof TextChunk ) {
				TextChunk tc = (TextChunk)c;
				String unescaped = tc.getText().replace(escape, "");
				if (unescaped.length() < tc.getText().length()) {
					chunks.set(i, new TextChunk(unescaped));
				}
			}
		}

		return chunks;
	}
}
