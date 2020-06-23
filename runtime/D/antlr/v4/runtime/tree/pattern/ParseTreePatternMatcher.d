/*
 * Copyright (c) 2012-2019 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher;

import std.uni;
import std.conv;
import std.string;
import antlr.v4.runtime.Parser;
import antlr.v4.runtime.ANTLRInputStream;
import antlr.v4.runtime.BailErrorStrategy;
import antlr.v4.runtime.Lexer;
import antlr.v4.runtime.IllegalArgumentException;
import antlr.v4.runtime.ListTokenSource;
import antlr.v4.runtime.ParserInterpreter;
import antlr.v4.runtime.ParserRuleContext;
import antlr.v4.runtime.RuleContext;
import antlr.v4.runtime.RecognitionException;
import antlr.v4.runtime.Token;
import antlr.v4.runtime.TokenConstantDefinition;
import antlr.v4.runtime.CommonTokenStream;
import antlr.v4.runtime.atn.ATNSimulator;
import antlr.v4.runtime.tree.ParseTree;
import antlr.v4.runtime.tree.CannotInvokeStartRule;
import antlr.v4.runtime.tree.StartRuleDoesNotConsumeFullPattern;
import antlr.v4.runtime.tree.TerminalNode;
import antlr.v4.runtime.tree.RuleNode;
import antlr.v4.runtime.tree.pattern.Chunk;
import antlr.v4.runtime.tree.pattern.TagChunk;
import antlr.v4.runtime.tree.pattern.TextChunk;
import antlr.v4.runtime.tree.pattern.TokenTagToken;
import antlr.v4.runtime.tree.pattern.ParseTreePattern;
import antlr.v4.runtime.tree.pattern.ParseTreeMatch;
import antlr.v4.runtime.tree.pattern.RuleTagToken;
import antlr.v4.runtime.misc.ParseCancellationException;

/**
 * A tree pattern matching mechanism for ANTLR {@link ParseTree}s.
 *
 * Patterns are strings of source input text with special tags representing
 * token or rule references such as:
 *
 * <p>{@code <ID> = <expr>;}</p>
 *
 * Given a pattern start rule such as {@code statement}, this object constructs
 * a {@link ParseTree} with placeholders for the {@code ID} and {@code expr}
 * subtree. Then the {@link #match} routines can compare an actual
 * {@link ParseTree} from a parse with this pattern. Tag {@code <ID>} matches
 * any {@code ID} token and tag {@code <expr>} references the result of the
 * {@code expr} rule (generally an instance of {@code ExprContext}).
 *
 * Pattern {@code x = 0;} is a similar pattern that matches the same pattern
 * except that it requires the identifier to be {@code x} and the expression to
 * be {@code 0}.
 *
 * The {@link #matches} routines return {@code true} or {@code false} based
 * upon a match for the tree rooted at the parameter sent in. The
 * {@link #match} routines return a {@link ParseTreeMatch} object that
 * contains the parse tree, the parse tree pattern, and a map from tag name to
 * matched nodes (more below). A subtree that fails to match, returns with
 * {@link ParseTreeMatch#mismatchedNode} set to the first tree node that did not
 * match.
 *
 * For efficiency, you can compile a tree pattern in string form to a
 * {@link ParseTreePattern} object.
 *
 * See {@code TestParseTreeMatcher} for lots of examples.
 * {@link ParseTreePattern} has two static helper methods:
 * {@link ParseTreePattern#findAll} and {@link ParseTreePattern#match} that
 * are easy to use but not super efficient because they create new
 * {@link ParseTreePatternMatcher} objects each time and have to compile the
 * pattern in string form before using it.
 *
 * The lexer and parser that you pass into the {@link ParseTreePatternMatcher}
 * constructor are used to parse the pattern in string form. The lexer converts
 * the {@code <ID> = <expr>;} into a sequence of four tokens (assuming lexer
 * throws out whitespace or puts it on a hidden channel). Be aware that the
 * input stream is reset for the lexer (but not the parser; a
 * {@link ParserInterpreter} is created to parse the input.). Any user-defined
 * fields you have put into the lexer might get changed when this mechanism asks
 * it to scan the pattern string.
 *
 * Normally a parser does not accept token {@code <expr>} as a valid
 * {@code expr} but, from the parser passed in, we create a special version of
 * the underlying grammar representation (an {@link ATN}) that allows imaginary
 * tokens representing rules ({@code <expr>}) to match entire rules. We call
 * these <em>bypass alternatives</em>.
 *
 * Delimiters are {@code <} and {@code >}, with {@code \} as the escape string
 * by default, but you can set them to whatever you want using
 * {@link #setDelimiters}. You must escape both start and stop strings
 * {@code \<} and {@code \>}.
 */
class ParseTreePatternMatcher
{

    /**
     * This is the backing field for {@link #getLexer()}.
     */
    private Lexer lexer;

    /**
     * This is the backing field for {@link #getParser()}.
     */
    private Parser parser;

    protected string start = "<";

    protected string stop = ">";

    /**
     * e.g., \< and \> must escape BOTH!
     */
    protected string escape = "\\";

    /**
     * Constructs a {@link ParseTreePatternMatcher} or from a {@link Lexer} and
     * {@link Parser} object. The lexer input stream is altered for tokenizing
     * the tree patterns. The parser is used as a convenient mechanism to get
     * the grammar name, plus token, rule names.
     */
    public this(Lexer lexer, Parser parser)
    {
        this.lexer = lexer;
        this.parser = parser;
    }

    /**
     * Set the delimiters used for marking rule and token tags within concrete
     * syntax used by the tree pattern parser.
     *
     *  @param start The start delimiter.
     *  @param stop The stop delimiter.
     *  @param escapeLeft The escape sequence to use for escaping a start or stop delimiter.
     *
     *  @exception IllegalArgumentException if {@code start} is {@code null} or empty.
     *  @exception IllegalArgumentException if {@code stop} is {@code null} or empty.
     */
    public void setDelimiters(string start, string stop, string escapeLeft)
    {
        if (start is null || start.length) {
            throw new IllegalArgumentException("start cannot be null or empty");
        }

        if (stop is null || stop.length) {
            throw new IllegalArgumentException("stop cannot be null or empty");
        }

        this.start = start;
        this.stop = stop;
        this.escape = escapeLeft;
    }

    /**
     * Does {@code pattern} matched as rule {@code patternRuleIndex} match {@code tree}?
     */
    public bool matches(ParseTree tree, string pattern, int patternRuleIndex)
    {
        ParseTreePattern p = compile(pattern, patternRuleIndex);
        return matches(tree, p);
    }

    /**
     * Does {@code pattern} matched as rule patternRuleIndex match tree? Pass in a
     * compiled pattern instead of a string representation of a tree pattern.
     */
    public bool matches(ParseTree tree, ParseTreePattern pattern)
    {
        ParseTree[][string] labels;
        ParseTree mismatchedNode = matchImpl(tree, pattern.getPatternTree(), labels);
        return mismatchedNode is null;
    }

    /**
     * Compare {@code pattern} matched as rule {@code patternRuleIndex} against
     * {@code tree} and return a {@link ParseTreeMatch} object that contains the
     * matched elements, or the node at which the match failed.
     */
    public ParseTreeMatch match(ParseTree tree, string pattern, int patternRuleIndex)
    {
        ParseTreePattern p = compile(pattern, patternRuleIndex);
        return match(tree, p);
    }

    /**
     * Compare {@code pattern} matched against {@code tree} and return a
     * {@link ParseTreeMatch} object that contains the matched elements, or thenode at which the match failed. Pass in a compiled pattern instead of a
     * string representation of a tree pattern.
     */
    public ParseTreeMatch match(ParseTree tree, ParseTreePattern pattern)
    {
        ParseTree[][string] labels;
        ParseTree mismatchedNode = matchImpl(tree, pattern.getPatternTree(), labels);
        return new ParseTreeMatch(tree, pattern, labels, mismatchedNode);
    }

    /**
     * For repeated use of a tree pattern, compile it to a
     * {@link ParseTreePattern} using this method.
     */
    public ParseTreePattern compile(string pattern, int patternRuleIndex)
    {
	auto tokenList = tokenize(pattern);
        ListTokenSource tokenSrc = new ListTokenSource(tokenList);
        CommonTokenStream tokens = new CommonTokenStream(tokenSrc);

        ParserInterpreter parserInterp = new ParserInterpreter(parser.getGrammarFileName(),
                                                               parser.getVocabulary(),
                                                               parser.getRuleNames(),
                                                               parser.getATNWithBypassAlts(),
                                                               tokens);

        ParseTree tree = null;
        try {
            parserInterp.setErrorHandler(new BailErrorStrategy());
            tree = parserInterp.parse(patternRuleIndex);
            //			System.out.println("pattern tree = "+tree.toStringTree(parserInterp));
        }
        catch (ParseCancellationException e) {
            throw cast(RecognitionException)e.getCause();
        }
        catch (RecognitionException re) {
            throw re;
        }
        catch (Exception e) {
            throw new CannotInvokeStartRule(e);
        }

        // Make sure tree pattern compilation checks for a complete parse
        if ( tokens.LA(1)!=TokenConstantDefinition.EOF ) {
            throw new StartRuleDoesNotConsumeFullPattern();
        }
        return new ParseTreePattern(this, pattern, patternRuleIndex, tree);
    }

    /**
     * Used to convert the tree pattern string into a series of tokens. The
     * input stream is reset.
     */
    public Lexer getLexer()
    {
        return lexer;
    }

    /**
     * Used to collect to the grammar file name, token names, rule names for
     * used to parse the pattern into a parse tree.
     */
    public Parser getParser()
    {
        return parser;
    }

    /**
     * Recursively walk {@code tree} against {@code patternTree}, filling
     * {@code match.}{@link ParseTreeMatch#labels labels}.
     *
     *  @return the first node encountered in {@code tree} which does not match
     * a corresponding node in {@code patternTree}, or {@code null} if the match
     * was successful. The specific node returned depends on the matching
     * algorithm used by the implementation, and may be overridden.
     */
    protected ParseTree matchImpl(ParseTree tree, ParseTree patternTree, ParseTree[][string] labels)
    {
	if (tree is null) {
            throw new IllegalArgumentException("tree cannot be null");
        }

        if (patternTree is null) {
            throw new IllegalArgumentException("patternTree cannot be null");
        }

        // x and <ID>, x and y, or x and x; or could be mismatched types
        if (tree.classinfo == TerminalNode.classinfo && patternTree.classinfo == TerminalNode.classinfo) {
            TerminalNode t1 = cast(TerminalNode)tree;
            TerminalNode t2 = cast(TerminalNode)patternTree;
            ParseTree mismatchedNode = null;
            // both are tokens and they have same type
            if (t1.getSymbol().getType() == t2.getSymbol().getType() ) {
                if (t2.getSymbol().classinfo == TokenTagToken.classinfo) { // x and <ID>
                    TokenTagToken tokenTagToken = cast(TokenTagToken)t2.getSymbol();
                    // track label->list-of-nodes for both token name and label (if any)
                    labels[tokenTagToken.getTokenName] ~= tree;
                    if (tokenTagToken.getLabel() !is null) {
                        labels[tokenTagToken.getLabel] ~= tree;
                    }
                }
                else if (t1.getText == t2.getText) {
                    // x and x
                }
                else {
                    // x and y
                    if (mismatchedNode is null) {
                        mismatchedNode = t1;
                    }
                }
            }
            else {
                if (mismatchedNode is null) {
                    mismatchedNode = t1;
                }
            }

            return mismatchedNode;
        }
        if (tree.classinfo == ParserRuleContext.classinfo && patternTree.classinfo == ParserRuleContext.classinfo) {
            ParserRuleContext r1 = cast(ParserRuleContext)tree;
            ParserRuleContext r2 = cast(ParserRuleContext)patternTree;
            ParseTree mismatchedNode = null;
            // (expr ...) and <expr>
            RuleTagToken ruleTagToken = getRuleTagToken(r2);
            if ( ruleTagToken !is null ) {
                ParseTreeMatch m = null;
                if (r1.getRuleIndex() == r2.getRuleIndex()) {
                    // track label->list-of-nodes for both rule name and label (if any)
                    labels[ruleTagToken.getRuleName] ~= tree;
                    if ( ruleTagToken.getLabel() !is null ) {
                        labels[ruleTagToken.getLabel] ~= tree;
                    }
                }
                else {
                    if (mismatchedNode is null) {
                        mismatchedNode = r1;
                    }
                }

                return mismatchedNode;
            }

            // (expr ...) and (expr ...)
            if (r1.getChildCount() != r2.getChildCount()) {
                if (mismatchedNode is null) {
                    mismatchedNode = r1;
                }

                return mismatchedNode;
            }

            int n = r1.getChildCount();
            for (int i = 0; i<n; i++) {
                ParseTree childMatch = matchImpl(r1.getChild(i), patternTree.getChild(i), labels);
                if ( childMatch !is null ) {
                    return childMatch;
                }
            }

            return mismatchedNode;
        }

        // if nodes aren't both tokens or both rule nodes, can't match
        return tree;
    }

    public RuleTagToken getRuleTagToken(ParseTree t)
    {
        if (t.classinfo == RuleNode.classinfo) {
            RuleNode r = cast(RuleNode)t;
            if (r.getChildCount == 1 && r.getChild(0).classinfo == TerminalNode.classinfo) {
                TerminalNode c = cast(TerminalNode)r.getChild(0);
                if (c.getSymbol().classinfo == RuleTagToken.classinfo) {
                    //	System.out.println("rule tag subtree "+t.toStringTree(parser));
                    return cast(RuleTagToken)c.getSymbol();
                }
            }
        }
        return null;
    }

    public Token[] tokenize(string pattern)
    {
	// split pattern into chunks: sea (raw input) and islands (<ID>, <expr>)
        Chunk[] chunks = split(pattern);

        // create token stream from text and tags
        Token[] tokens;
        foreach (Chunk chunk; chunks) {
            if (chunk.classinfo == TagChunk.classinfo) {
                TagChunk tagChunk = cast(TagChunk)chunk;
                // add special rule token or conjure up new token from name
                if (isUpper(tagChunk.getTag()[0])) {
                    int ttype = parser.getTokenType(tagChunk.getTag());
                    if (ttype == TokenConstantDefinition.INVALID_TYPE ) {
                        throw new IllegalArgumentException("Unknown token " ~ tagChunk.getTag() ~ " in pattern: " ~ pattern);
                    }
                    TokenTagToken t = new TokenTagToken(tagChunk.getTag(), ttype, tagChunk.getLabel());
                    tokens ~= t;
                }
                else if (isLower(tagChunk.getTag()[0]) ) {
                    int ruleIndex = parser.getRuleIndex(tagChunk.getTag());
                    if (ruleIndex == -1) {
                        throw new IllegalArgumentException("Unknown rule " ~ tagChunk.getTag() ~ " in pattern: " ~ pattern);
                    }
                    int ruleImaginaryTokenType = parser.getATNWithBypassAlts().ruleToTokenType[ruleIndex];
                    tokens ~= new RuleTagToken(tagChunk.getTag(), ruleImaginaryTokenType, tagChunk.getLabel());
                }
                else {
                    throw new IllegalArgumentException("invalid tag: " ~ tagChunk.getTag ~ " in pattern: " ~ pattern);
                }
            }
            else {
                TextChunk textChunk = cast(TextChunk)chunk;
                ANTLRInputStream ins = new ANTLRInputStream(textChunk.getText());
                lexer.setInputStream(ins);
                Token t = lexer.nextToken();
                while (t.getType() != TokenConstantDefinition.EOF) {
                    tokens ~= t;
                    t = lexer.nextToken();
                }
            }
        }

        //		System.out.println("tokens="+tokens);
        return tokens;
    }

    /**
     * Split {@code <ID> = <e:expr> ;} into 4 chunks for tokenizing by {@link #tokenize}.
     */
    public Chunk[] split(string pattern)
    {
        int p = 0;
        int n = to!int(pattern.length);
        Chunk[] chunks;
        // find all start and stop indexes first, then collect
        int[] starts;
        int[] stops;
        while (p < n) {
            if (p == pattern.indexOf(escape ~ start, p) ) {
                p += to!int(escape.length + start.length);
            }
            else if ( p == pattern.indexOf(escape ~ stop, p) ) {
                p += to!int(escape.length + stop.length);
            }
            else if ( p == pattern.indexOf(start,p) ) {
                starts ~= p;
                p += to!int(start.length);
            }
            else if ( p == pattern.indexOf(stop,p) ) {
                stops ~= p;
                p += to!int(stop.length);
            }
            else {
                p++;
            }
        }

        //		System.out.println("");
        //		System.out.println(starts);
        //		System.out.println(stops);
        if (starts.length > stops.length) {
            throw new IllegalArgumentException("unterminated tag in pattern: " ~ pattern);
        }

        if ( starts.length < stops.length) {
            throw new IllegalArgumentException("missing start tag in pattern: " ~ pattern);
        }

        int ntags = to!int(starts.length);
        for (int i=0; i<ntags; i++) {
            if ( starts[i] >= stops[i] ) {
                throw new IllegalArgumentException("tag delimiters out of order in pattern: " ~ pattern);
            }
        }

        // collect into chunks now
        if (ntags == 0) {
            string text = pattern[0.. n];
            chunks ~= new TextChunk(text);
        }

        if (ntags>0 && starts[0] > 0) { // copy text up to first tag into chunks
            string text = pattern[0..starts[0]];
            chunks ~= new TextChunk(text);
        }
        for (int i=0; i<ntags; i++) {
            // copy inside of <tag>
            string tag = pattern[starts[i] + start.length..stops[i]];
            string ruleOrToken = tag;
            string label = null;
            int colon = to!int(tag.indexOf(':'));
            if (colon >= 0) {
                label = tag[0..colon];
                ruleOrToken = tag[colon+1..tag.length];
            }
            chunks ~= new TagChunk(label, ruleOrToken);
            if (i + 1 < ntags) {
                // copy from end of <tag> to start of next
                string text = pattern[stops[i] + to!int(stop.length)..starts[i + 1]];
                chunks ~= new TextChunk(text);
            }
        }
        if ( ntags > 0 ) {
            int afterLastTag = to!int(stops[ntags - 1] + stop.length);
            if ( afterLastTag < n ) { // copy text from end of last tag to end
                string text = pattern[afterLastTag..n];
                chunks ~= new TextChunk(text);
            }
        }

        // strip out the escape sequences from text chunks but not tags
        for (int i = 0; i < chunks.length; i++) {
            Chunk c = chunks[i];
            if (c.classinfo == TextChunk.classinfo) {
                TextChunk tc = cast(TextChunk)c;
                string unescaped = tc.getText().replace(escape, "");
                if (unescaped.length < tc.getText().length) {
                    chunks[i] = new TextChunk(unescaped);
                }
            }
        }

        return chunks;
    }

}
