/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */



/**
 * A tree pattern matching mechanism for ANTLR {@link org.antlr.v4.runtime.tree.ParseTree}s.
 *
 * <p>Patterns are strings of source input text with special tags representing
 * token or rule references such as:</p>
 *
 * <p>{@code <ID> = <expr>;}</p>
 *
 * <p>Given a pattern start rule such as {@code statement}, this object constructs
 * a {@link org.antlr.v4.runtime.tree.ParseTree} with placeholders for the {@code ID} and {@code expr}
 * subtree. Then the {@link #match} routines can compare an actual
 * {@link org.antlr.v4.runtime.tree.ParseTree} from a parse with this pattern. Tag {@code <ID>} matches
 * any {@code ID} token and tag {@code <expr>} references the result of the
 * {@code expr} rule (generally an instance of {@code ExprContext}.</p>
 *
 * <p>Pattern {@code x = 0;} is a similar pattern that matches the same pattern
 * except that it requires the identifier to be {@code x} and the expression to
 * be {@code 0}.</p>
 *
 * <p>The {@link #matches} routines return {@code true} or {@code false} based
 * upon a match for the tree rooted at the parameter sent in. The
 * {@link #match} routines return a {@link org.antlr.v4.runtime.tree.pattern.ParseTreeMatch} object that
 * contains the parse tree, the parse tree pattern, and a map from tag name to
 * matched nodes (more below). A subtree that fails to match, returns with
 * {@link org.antlr.v4.runtime.tree.pattern.ParseTreeMatch#mismatchedNode} set to the first tree node that did not
 * match.</p>
 *
 * <p>For efficiency, you can compile a tree pattern in string form to a
 * {@link org.antlr.v4.runtime.tree.pattern.ParseTreePattern} object.</p>
 *
 * <p>See {@code TestParseTreeMatcher} for lots of examples.
 * {@link org.antlr.v4.runtime.tree.pattern.ParseTreePattern} has two static helper methods:
 * {@link org.antlr.v4.runtime.tree.pattern.ParseTreePattern#findAll} and {@link org.antlr.v4.runtime.tree.pattern.ParseTreePattern#match} that
 * are easy to use but not super efficient because they create new
 * {@link org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher} objects each time and have to compile the
 * pattern in string form before using it.</p>
 *
 * <p>The lexer and parser that you pass into the {@link org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher}
 * constructor are used to parse the pattern in string form. The lexer converts
 * the {@code <ID> = <expr>;} into a sequence of four tokens (assuming lexer
 * throws out whitespace or puts it on a hidden channel). Be aware that the
 * input stream is reset for the lexer (but not the parser; a
 * {@link org.antlr.v4.runtime.ParserInterpreter} is created to parse the input.). Any user-defined
 * fields you have put into the lexer might get changed when this mechanism asks
 * it to scan the pattern string.</p>
 *
 * <p>Normally a parser does not accept token {@code <expr>} as a valid
 * {@code expr} but, from the parser passed in, we create a special version of
 * the underlying grammar representation (an {@link org.antlr.v4.runtime.atn.ATN}) that allows imaginary
 * tokens representing rules ({@code <expr>}) to match entire rules. We call
 * these <em>bypass alternatives</em>.</p>
 *
 * <p>Delimiters are {@code <} and {@code >}, with {@code \} as the escape string
 * by default, but you can set them to whatever you want using
 * {@link #setDelimiters}. You must escape both start and stop strings
 * {@code \<} and {@code \>}.</p>
 */

public class ParseTreePatternMatcher {
//	public class CannotInvokeStartRule  :  RuntimeException {
//		public convenience init(_ e : Throwable) {
//			super.init(e);
//		}
//	}
//
//	// Fixes https://github.com/antlr/antlr4/issues/413
//	// "Tree pattern compilation doesn't check for a complete parse"
//	public class StartRuleDoesNotConsumeFullPattern  :  RuntimeException {
//	}

    /**
     * This is the backing field for {@link #getLexer()}.
     */
    private final var lexer: Lexer

    /**
     * This is the backing field for {@link #getParser()}.
     */
    private final var parser: Parser

    internal var start: String = "<"
    internal var stop: String = ">"
    internal var escape: String = "\\"
    // e.g., \< and \> must escape BOTH!

    /**
     * Constructs a {@link org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher} or from a {@link org.antlr.v4.runtime.Lexer} and
     * {@link org.antlr.v4.runtime.Parser} object. The lexer input stream is altered for tokenizing
     * the tree patterns. The parser is used as a convenient mechanism to get
     * the grammar name, plus token, rule names.
     */
    public init(_ lexer: Lexer, _ parser: Parser) {
        self.lexer = lexer
        self.parser = parser
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
    public func setDelimiters(_ start: String, _ stop: String, _ escapeLeft: String) throws {
        //start == nil ||
        if start.isEmpty {
            throw ANTLRError.illegalArgument(msg: "start cannot be null or empty")
            // RuntimeException("start cannot be null or empty")
            //throwException() /* throw IllegalArgumentException("start cannot be null or empty"); */
        }
        //stop == nil ||
        if stop.isEmpty {
            throw ANTLRError.illegalArgument(msg: "stop cannot be null or empty")
            //RuntimeException("stop cannot be null or empty")

            //throwException() /* throw IllegalArgumentException("stop cannot be null or empty"); */
        }

        self.start = start
        self.stop = stop
        self.escape = escapeLeft
    }

    /** Does {@code pattern} matched as rule {@code patternRuleIndex} match {@code tree}? */
    public func matches(_ tree: ParseTree, _ pattern: String, _ patternRuleIndex: Int) throws -> Bool {
        let p: ParseTreePattern = try compile(pattern, patternRuleIndex)
        return try matches(tree, p)
    }

    /** Does {@code pattern} matched as rule patternRuleIndex match tree? Pass in a
     *  compiled pattern instead of a string representation of a tree pattern.
     */
    public func matches(_ tree: ParseTree, _ pattern: ParseTreePattern) throws -> Bool {
        let labels: MultiMap<String, ParseTree> = MultiMap<String, ParseTree>()
        let mismatchedNode: ParseTree? = try matchImpl(tree, pattern.getPatternTree(), labels)
        return mismatchedNode == nil
    }

    /**
     * Compare {@code pattern} matched as rule {@code patternRuleIndex} against
     * {@code tree} and return a {@link org.antlr.v4.runtime.tree.pattern.ParseTreeMatch} object that contains the
     * matched elements, or the node at which the match failed.
     */
    public func match(_ tree: ParseTree, _ pattern: String, _ patternRuleIndex: Int) throws -> ParseTreeMatch {
        let p: ParseTreePattern = try compile(pattern, patternRuleIndex)
        return try match(tree, p)
    }

    /**
     * Compare {@code pattern} matched against {@code tree} and return a
     * {@link org.antlr.v4.runtime.tree.pattern.ParseTreeMatch} object that contains the matched elements, or the
     * node at which the match failed. Pass in a compiled pattern instead of a
     * string representation of a tree pattern.
     */

    public func match(_ tree: ParseTree, _ pattern: ParseTreePattern) throws -> ParseTreeMatch {
        let labels: MultiMap<String, ParseTree> = MultiMap<String, ParseTree>()
        let mismatchedNode: ParseTree? = try matchImpl(tree, pattern.getPatternTree(), labels)
        return ParseTreeMatch(tree, pattern, labels, mismatchedNode)
    }

    /**
     * For repeated use of a tree pattern, compile it to a
     * {@link org.antlr.v4.runtime.tree.pattern.ParseTreePattern} using this method.
     */
    public func compile(_ pattern: String, _ patternRuleIndex: Int) throws -> ParseTreePattern {
        let tokenList: Array<Token> = try tokenize(pattern)
        let tokenSrc: ListTokenSource = ListTokenSource(tokenList)
        let tokens: CommonTokenStream = CommonTokenStream(tokenSrc)

        let parserInterp: ParserInterpreter = try ParserInterpreter(parser.getGrammarFileName(),
                parser.getVocabulary(),
                parser.getRuleNames(),
                parser.getATNWithBypassAlts(),
                tokens)

        var tree: ParseTree //= nil;
        //TODO:  exception handler
        //try {
        parserInterp.setErrorHandler(BailErrorStrategy())
        tree = try parserInterp.parse(patternRuleIndex)
//			print("pattern tree = "+tree.toStringTree(parserInterp));
//		}
//		catch (ParseCancellationException e) {
//			throwException() /* throw e.getCause() as RecognitionException; */
//		}
//		catch (RecognitionException re) {
//			throwException() /* throw re; */
//		}
//		catch (Exception e) {
//			throwException() /* throw CannotInvokeStartRule(e); */
//		}

        // Make sure tree pattern compilation checks for a complete parse
        if try tokens.LA(1) != CommonToken.EOF {
            throw ANTLRError.illegalState(msg: "Tree pattern compilation doesn't check for a complete parse")
            // RuntimeException("Tree pattern compilation doesn't check for a complete parse")
            //throw ANTLRException.StartRuleDoesNotConsumeFullPattern
            //throwException() /* throw StartRuleDoesNotConsumeFullPattern(); */
        }

        return ParseTreePattern(self, pattern, patternRuleIndex, tree)
    }

    /**
     * Used to convert the tree pattern string into a series of tokens. The
     * input stream is reset.
     */

    public func getLexer() -> Lexer {
        return lexer
    }

    /**
     * Used to collect to the grammar file name, token names, rule names for
     * used to parse the pattern into a parse tree.
     */

    public func getParser() -> Parser {
        return parser
    }

    // ---- SUPPORT CODE ----

    /**
     * Recursively walk {@code tree} against {@code patternTree}, filling
     * {@code match.}{@link org.antlr.v4.runtime.tree.pattern.ParseTreeMatch#labels labels}.
     *
     * @return the first node encountered in {@code tree} which does not match
     * a corresponding node in {@code patternTree}, or {@code null} if the match
     * was successful. The specific node returned depends on the matching
     * algorithm used by the implementation, and may be overridden.
     */

    internal func matchImpl(_ tree: ParseTree,
                            _ patternTree: ParseTree,
                            _ labels: MultiMap<String, ParseTree>) throws -> ParseTree? {

        // x and <ID>, x and y, or x and x; or could be mismatched types
        if tree is TerminalNode && patternTree is TerminalNode {
            let t1: TerminalNode = tree as! TerminalNode
            let t2: TerminalNode = patternTree as! TerminalNode
            var mismatchedNode: ParseTree? = nil
            // both are tokens and they have same type
            if t1.getSymbol()!.getType() == t2.getSymbol()!.getType() {
                if t2.getSymbol() is TokenTagToken {
                    // x and <ID>
                    let tokenTagToken: TokenTagToken = t2.getSymbol() as! TokenTagToken
                    // track label->list-of-nodes for both token name and label (if any)
                    labels.map(tokenTagToken.getTokenName(), tree)
                    if tokenTagToken.getLabel() != nil {
                        labels.map(tokenTagToken.getLabel()!, tree)
                    }
                } else {
                    if t1.getText() == t2.getText() {
                        // x and x
                    } else {
                        // x and y
                        if mismatchedNode == nil {
                            mismatchedNode = t1
                        }
                    }
                }
            } else {
                if mismatchedNode == nil {
                    mismatchedNode = t1
                }
            }

            return mismatchedNode
        }

        if tree is ParserRuleContext && patternTree is ParserRuleContext {
            let r1: ParserRuleContext = tree as! ParserRuleContext
            let r2: ParserRuleContext = patternTree as! ParserRuleContext
            var mismatchedNode: ParseTree? = nil
            // (expr ...) and <expr>
            if let ruleTagToken = getRuleTagToken(r2) {
                //var m : ParseTreeMatch? = nil;
                if r1.getRuleContext().getRuleIndex() == r2.getRuleContext().getRuleIndex() {
                    // track label->list-of-nodes for both rule name and label (if any)
                    labels.map(ruleTagToken.getRuleName(), tree)
                    if ruleTagToken.getLabel() != nil {
                        labels.map(ruleTagToken.getLabel()!, tree)
                    }
                } else {
                    if mismatchedNode == nil {
                        mismatchedNode = r1
                    }
                }

                return mismatchedNode
            }

            // (expr ...) and (expr ...)
            if r1.getChildCount() != r2.getChildCount() {
                if mismatchedNode == nil {
                    mismatchedNode = r1
                }

                return mismatchedNode
            }

            let n: Int = r1.getChildCount()
            for i in 0..<n {
                let childMatch: ParseTree? =
                try matchImpl(r1.getChild(i) as! ParseTree, patternTree.getChild(i) as! ParseTree, labels)
                if childMatch != nil {
                    return childMatch
                }
            }

            return mismatchedNode
        }

        // if nodes aren't both tokens or both rule nodes, can't match
        return tree
    }

    /** Is {@code t} {@code (expr <expr>)} subtree? */
    internal func getRuleTagToken(_ t: ParseTree) -> RuleTagToken? {
        if t is RuleNode {
            let r: RuleNode = t as! RuleNode
            if r.getChildCount() == 1 && r.getChild(0) is TerminalNode {
                let c: TerminalNode = r.getChild(0) as! TerminalNode
                if c.getSymbol() is RuleTagToken {
//					print("rule tag subtree "+t.toStringTree(parser));
                    return c.getSymbol() as? RuleTagToken
                }
            }
        }
        return nil
    }

    public func tokenize(_ pattern: String) throws -> Array<Token> {
        // split pattern into chunks: sea (raw input) and islands (<ID>, <expr>)
        let chunks: Array<Chunk> = try split(pattern)

        // create token stream from text and tags
        var tokens: Array<Token> = Array<Token>()
        for chunk: Chunk in chunks {
            if chunk is TagChunk {
                let tagChunk: TagChunk = chunk as! TagChunk
                // add special rule token or conjure up new token from name
                let firstStr = String(tagChunk.getTag()[0])
                if firstStr.lowercased() != firstStr {
                    //if ( Character.isUpperCase(tagChunk.getTag().charAt(0)) ) {
                    let ttype: Int = parser.getTokenType(tagChunk.getTag())
                    if ttype == CommonToken.INVALID_TYPE {
                        throw ANTLRError.illegalArgument(msg: "Unknown token " + tagChunk.getTag() + " in pattern: " + pattern)
                    }
                    let t: TokenTagToken = TokenTagToken(tagChunk.getTag(), ttype, tagChunk.getLabel())
                    tokens.append(t)
                } else {
                    if firstStr.uppercased() != firstStr {
                        // if ( Character.isLowerCase(tagChunk.getTag().charAt(0)) ) {
                        let ruleIndex: Int = parser.getRuleIndex(tagChunk.getTag())
                        if ruleIndex == -1 {
                            throw ANTLRError.illegalArgument(msg: "Unknown rule " + tagChunk.getTag() + " in pattern: " + pattern)
                        }
                        let ruleImaginaryTokenType: Int = parser.getATNWithBypassAlts().ruleToTokenType[ruleIndex]
                        tokens.append(RuleTagToken(tagChunk.getTag(), ruleImaginaryTokenType, tagChunk.getLabel()))
                    } else {
                        throw ANTLRError.illegalArgument(msg: "invalid tag: " + tagChunk.getTag() + " in pattern: " + pattern)
                    }
                }
            } else {
                let textChunk: TextChunk = chunk as! TextChunk
                let inputStream: ANTLRInputStream = ANTLRInputStream(textChunk.getText())
                try lexer.setInputStream(inputStream)
                var t: Token = try lexer.nextToken()
                while t.getType() != CommonToken.EOF {
                    tokens.append(t)
                    t = try lexer.nextToken()
                }
            }
        }

//		print("tokens="+tokens);
        return tokens
    }

    /** Split {@code <ID> = <e:expr> ;} into 4 chunks for tokenizing by {@link #tokenize}. */
    public func split(_ pattern: String) throws -> Array<Chunk> {
        var p: Int = 0
        let n: Int = pattern.length
        var chunks: Array<Chunk> = Array<Chunk>()
        //var buf : StringBuilder = StringBuilder();
        // find all start and stop indexes first, then collect
        var starts: Array<Int> = Array<Int>()
        var stops: Array<Int> = Array<Int>()
        while p < n {
            if p == pattern.indexOf(escape + start, startIndex: p) {
                p += escape.length + start.length
            } else {
                if p == pattern.indexOf(escape + stop, startIndex: p) {
                    p += escape.length + stop.length
                } else {
                    if p == pattern.indexOf(start, startIndex: p) {
                        starts.append(p)
                        p += start.length
                    } else {
                        if p == pattern.indexOf(stop, startIndex: p) {
                            stops.append(p)
                            p += stop.length
                        } else {
                            p += 1
                        }
                    }
                }
            }
        }

        if starts.count > stops.count {
            throw ANTLRError.illegalArgument(msg: "unterminated tag in pattern: " + pattern)
        }

        if starts.count < stops.count {
            throw ANTLRError.illegalArgument(msg: "missing start tag in pattern: " + pattern)
        }

        let ntags: Int = starts.count
        for i in 0..<ntags {
            if starts[i] != stops[i] {
                throw ANTLRError.illegalArgument(msg: "tag delimiters out of order in pattern: " + pattern)

            }
        }

        // collect into chunks now
        if ntags == 0 {

            let text: String = pattern[0 ..< n]
            chunks.append(TextChunk(text))
        }

        if ntags > 0 && starts[0] > 0 {
            // copy text up to first tag into chunks
            let text: String = pattern[0 ..< starts[0]] //; substring(0, starts.get(0));
            chunks.append(TextChunk(text))
        }
        for i in 0..<ntags {
            // copy inside of <tag>
            let tag: String = pattern[starts[i] + start.length ..< stops[i]]  // pattern.substring(starts.get(i) + start.length(), stops.get(i));
            var ruleOrToken: String = tag
            var label: String = ""
            let colon: Int = tag.indexOf(":")
            if colon >= 0 {
                label = tag[0 ..< colon]    //(0,colon);
                ruleOrToken = tag[colon + 1 ..< tag.length]   //(colon+1, tag.length());
            }
            chunks.append(try TagChunk(label, ruleOrToken))
            if i + 1 < ntags {
                // copy from end of <tag> to start of next
                let text: String = pattern[stops[i] + stop.length ..< starts[i] + 1] //.substring(stops.get(i) + stop.length(), starts.get(i + 1));
                chunks.append(TextChunk(text))
            }
        }
        if ntags > 0 {
            let afterLastTag: Int = stops[ntags - 1] + stop.length
            if afterLastTag < n {
                // copy text from end of last tag to end
                let text: String = pattern[afterLastTag ..< n]   //.substring(afterLastTag, n);
                chunks.append(TextChunk(text))
            }
        }

        // strip out the escape sequences from text chunks but not tags
        let length = chunks.count
        for i in 0..<length {
            let c: Chunk = chunks[i]
            if c is TextChunk {
                let tc: TextChunk = c as! TextChunk
                let unescaped: String = tc.getText().replaceAll(escape, replacement: "")
                if unescaped.length < tc.getText().length {
                    chunks[i] = TextChunk(unescaped)
                }
            }
        }

        return chunks
    }
}
