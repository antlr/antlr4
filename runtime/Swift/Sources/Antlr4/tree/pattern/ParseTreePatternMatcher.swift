/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */



/// 
/// A tree pattern matching mechanism for ANTLR _org.antlr.v4.runtime.tree.ParseTree_s.
/// 
/// Patterns are strings of source input text with special tags representing
/// token or rule references such as:
/// 
/// `<ID> = <expr>;`
/// 
/// Given a pattern start rule such as `statement`, this object constructs
/// a _org.antlr.v4.runtime.tree.ParseTree_ with placeholders for the `ID` and `expr`
/// subtree. Then the _#match_ routines can compare an actual
/// _org.antlr.v4.runtime.tree.ParseTree_ from a parse with this pattern. Tag `<ID>` matches
/// any `ID` token and tag `<expr>` references the result of the
/// `expr` rule (generally an instance of `ExprContext`.
/// 
/// Pattern `x = 0;` is a similar pattern that matches the same pattern
/// except that it requires the identifier to be `x` and the expression to
/// be `0`.
/// 
/// The _#matches_ routines return `true` or `false` based
/// upon a match for the tree rooted at the parameter sent in. The
/// _#match_ routines return a _org.antlr.v4.runtime.tree.pattern.ParseTreeMatch_ object that
/// contains the parse tree, the parse tree pattern, and a map from tag name to
/// matched nodes (more below). A subtree that fails to match, returns with
/// _org.antlr.v4.runtime.tree.pattern.ParseTreeMatch#mismatchedNode_ set to the first tree node that did not
/// match.
/// 
/// For efficiency, you can compile a tree pattern in string form to a
/// _org.antlr.v4.runtime.tree.pattern.ParseTreePattern_ object.
/// 
/// See `TestParseTreeMatcher` for lots of examples.
/// _org.antlr.v4.runtime.tree.pattern.ParseTreePattern_ has two static helper methods:
/// _org.antlr.v4.runtime.tree.pattern.ParseTreePattern#findAll_ and _org.antlr.v4.runtime.tree.pattern.ParseTreePattern#match_ that
/// are easy to use but not super efficient because they create new
/// _org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher_ objects each time and have to compile the
/// pattern in string form before using it.
/// 
/// The lexer and parser that you pass into the _org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher_
/// constructor are used to parse the pattern in string form. The lexer converts
/// the `<ID> = <expr>;` into a sequence of four tokens (assuming lexer
/// throws out whitespace or puts it on a hidden channel). Be aware that the
/// input stream is reset for the lexer (but not the parser; a
/// _org.antlr.v4.runtime.ParserInterpreter_ is created to parse the input.). Any user-defined
/// fields you have put into the lexer might get changed when this mechanism asks
/// it to scan the pattern string.
/// 
/// Normally a parser does not accept token `<expr>` as a valid
/// `expr` but, from the parser passed in, we create a special version of
/// the underlying grammar representation (an _org.antlr.v4.runtime.atn.ATN_) that allows imaginary
/// tokens representing rules (`<expr>`) to match entire rules. We call
/// these __bypass alternatives__.
/// 
/// Delimiters are `<` and `>`, with `\` as the escape string
/// by default, but you can set them to whatever you want using
/// _#setDelimiters_. You must escape both start and stop strings
/// `\<` and `\>`.
/// 

public class ParseTreePatternMatcher {

    /// 
    /// This is the backing field for _#getLexer()_.
    /// 
    private final let lexer: Lexer

    /// 
    /// This is the backing field for _#getParser()_.
    /// 
    private final let parser: Parser

    internal var start: String = "<"
    internal var stop: String = ">"
    internal var escape: String = "\\"

    /// 
    /// Constructs a _org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher_ or from a _org.antlr.v4.runtime.Lexer_ and
    /// _org.antlr.v4.runtime.Parser_ object. The lexer input stream is altered for tokenizing
    /// the tree patterns. The parser is used as a convenient mechanism to get
    /// the grammar name, plus token, rule names.
    /// 
    public init(_ lexer: Lexer, _ parser: Parser) {
        self.lexer = lexer
        self.parser = parser
    }

    /// 
    /// Set the delimiters used for marking rule and token tags within concrete
    /// syntax used by the tree pattern parser.
    /// 
    /// - Parameter start: The start delimiter.
    /// - Parameter stop: The stop delimiter.
    /// - Parameter escapeLeft: The escape sequence to use for escaping a start or stop delimiter.
    /// 
    /// - Throws: ANTLRError.ilegalArgument if `start` is `null` or empty.
    /// - Throws: ANTLRError.ilegalArgument if `stop` is `null` or empty.
    /// 
    public func setDelimiters(_ start: String, _ stop: String, _ escapeLeft: String) throws {
        if start.isEmpty {
            throw ANTLRError.illegalArgument(msg: "start cannot be null or empty")
        }
        if stop.isEmpty {
            throw ANTLRError.illegalArgument(msg: "stop cannot be null or empty")
        }

        self.start = start
        self.stop = stop
        self.escape = escapeLeft
    }

    /// 
    /// Does `pattern` matched as rule `patternRuleIndex` match `tree`?
    /// 
    public func matches(_ tree: ParseTree, _ pattern: String, _ patternRuleIndex: Int) throws -> Bool {
        let p: ParseTreePattern = try compile(pattern, patternRuleIndex)
        return try matches(tree, p)
    }

    /// 
    /// Does `pattern` matched as rule patternRuleIndex match tree? Pass in a
    /// compiled pattern instead of a string representation of a tree pattern.
    /// 
    public func matches(_ tree: ParseTree, _ pattern: ParseTreePattern) throws -> Bool {
        let labels: MultiMap<String, ParseTree> = MultiMap<String, ParseTree>()
        let mismatchedNode: ParseTree? = try matchImpl(tree, pattern.getPatternTree(), labels)
        return mismatchedNode == nil
    }

    /// 
    /// Compare `pattern` matched as rule `patternRuleIndex` against
    /// `tree` and return a _org.antlr.v4.runtime.tree.pattern.ParseTreeMatch_ object that contains the
    /// matched elements, or the node at which the match failed.
    /// 
    public func match(_ tree: ParseTree, _ pattern: String, _ patternRuleIndex: Int) throws -> ParseTreeMatch {
        let p: ParseTreePattern = try compile(pattern, patternRuleIndex)
        return try match(tree, p)
    }

    /// 
    /// Compare `pattern` matched against `tree` and return a
    /// _org.antlr.v4.runtime.tree.pattern.ParseTreeMatch_ object that contains the matched elements, or the
    /// node at which the match failed. Pass in a compiled pattern instead of a
    /// string representation of a tree pattern.
    /// 
    public func match(_ tree: ParseTree, _ pattern: ParseTreePattern) throws -> ParseTreeMatch {
        let labels: MultiMap<String, ParseTree> = MultiMap<String, ParseTree>()
        let mismatchedNode: ParseTree? = try matchImpl(tree, pattern.getPatternTree(), labels)
        return ParseTreeMatch(tree, pattern, labels, mismatchedNode)
    }

    /// 
    /// For repeated use of a tree pattern, compile it to a
    /// _org.antlr.v4.runtime.tree.pattern.ParseTreePattern_ using this method.
    /// 
    public func compile(_ pattern: String, _ patternRuleIndex: Int) throws -> ParseTreePattern {
        let tokenList = try tokenize(pattern)
        let tokenSrc = ListTokenSource(tokenList)
        let tokens = CommonTokenStream(tokenSrc)

        let parserInterp = try ParserInterpreter(parser.getGrammarFileName(),
                parser.getVocabulary(),
                parser.getRuleNames(),
                parser.getATNWithBypassAlts(),
                tokens)

        parserInterp.setErrorHandler(BailErrorStrategy())
        let tree = try parserInterp.parse(patternRuleIndex)

        // Make sure tree pattern compilation checks for a complete parse
        if try tokens.LA(1) != CommonToken.EOF {
            throw ANTLRError.illegalState(msg: "Tree pattern compilation doesn't check for a complete parse")
        }

        return ParseTreePattern(self, pattern, patternRuleIndex, tree)
    }

    /// 
    /// Used to convert the tree pattern string into a series of tokens. The
    /// input stream is reset.
    /// 
    public func getLexer() -> Lexer {
        return lexer
    }

    /// 
    /// Used to collect to the grammar file name, token names, rule names for
    /// used to parse the pattern into a parse tree.
    /// 
    public func getParser() -> Parser {
        return parser
    }

    // ---- SUPPORT CODE ----

    /// 
    /// Recursively walk `tree` against `patternTree`, filling
    /// `match.`_org.antlr.v4.runtime.tree.pattern.ParseTreeMatch#labels labels_.
    /// 
    /// - Returns: the first node encountered in `tree` which does not match
    /// a corresponding node in `patternTree`, or `null` if the match
    /// was successful. The specific node returned depends on the matching
    /// algorithm used by the implementation, and may be overridden.
    /// 
    internal func matchImpl(_ tree: ParseTree,
                            _ patternTree: ParseTree,
                            _ labels: MultiMap<String, ParseTree>) throws -> ParseTree? {

        // x and <ID>, x and y, or x and x; or could be mismatched types
        if tree is TerminalNode && patternTree is TerminalNode {
            let t1 = tree as! TerminalNode
            let t2 = patternTree as! TerminalNode
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

            for i in 0 ..< r1.getChildCount() {
                if let childMatch = try matchImpl(r1.getChild(i) as! ParseTree, patternTree.getChild(i) as! ParseTree, labels) {
                    return childMatch
                }
            }

            return mismatchedNode
        }

        // if nodes aren't both tokens or both rule nodes, can't match
        return tree
    }

    /// Is `t` `(expr <expr>)` subtree?
    internal func getRuleTagToken(_ t: ParseTree) -> RuleTagToken? {
        if let r = t as? RuleNode {
            if r.getChildCount() == 1 && r.getChild(0) is TerminalNode {
                let c = r.getChild(0) as! TerminalNode
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
        let chunks = try split(pattern)

        // create token stream from text and tags
        var tokens = [Token]()
        for chunk in chunks {
            if let tagChunk = chunk as? TagChunk {
                // add special rule token or conjure up new token from name
                let firstStr = String(tagChunk.getTag()[0])
                if firstStr.lowercased() != firstStr {
                    let ttype = parser.getTokenType(tagChunk.getTag())
                    if ttype == CommonToken.INVALID_TYPE {
                        throw ANTLRError.illegalArgument(msg: "Unknown token " + tagChunk.getTag() + " in pattern: " + pattern)
                    }
                    let t = TokenTagToken(tagChunk.getTag(), ttype, tagChunk.getLabel())
                    tokens.append(t)
                } else {
                    if firstStr.uppercased() != firstStr {
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
                let textChunk = chunk as! TextChunk
                let inputStream = ANTLRInputStream(textChunk.getText())
                try lexer.setInputStream(inputStream)
                var t = try lexer.nextToken()
                while t.getType() != CommonToken.EOF {
                    tokens.append(t)
                    t = try lexer.nextToken()
                }
            }
        }

//		print("tokens="+tokens);
        return tokens
    }

    /// 
    /// Split `<ID> = <e:expr> ;` into 4 chunks for tokenizing by _#tokenize_.
    /// 
    public func split(_ pattern: String) throws -> [Chunk] {
        var p = 0
        let n = pattern.length
        var chunks = [Chunk]()
        // find all start and stop indexes first, then collect
        var starts = [Int]()
        var stops = [Int]()
        while p < n {
            if p == pattern.indexOf(escape + start, startIndex: p) {
                p += escape.length + start.length
            }
            else if p == pattern.indexOf(escape + stop, startIndex: p) {
                p += escape.length + stop.length
            }
            else if p == pattern.indexOf(start, startIndex: p) {
                starts.append(p)
                p += start.length
            }
            else if p == pattern.indexOf(stop, startIndex: p) {
                stops.append(p)
                p += stop.length
            }
            else {
                p += 1
            }
        }

        if starts.count > stops.count {
            throw ANTLRError.illegalArgument(msg: "unterminated tag in pattern: " + pattern)
        }

        if starts.count < stops.count {
            throw ANTLRError.illegalArgument(msg: "missing start tag in pattern: " + pattern)
        }

        let ntags = starts.count
        for i in 0..<ntags {
            if starts[i] != stops[i] {
                throw ANTLRError.illegalArgument(msg: "tag delimiters out of order in pattern: " + pattern)
            }
        }

        // collect into chunks now
        if ntags == 0 {
            let text = pattern[0 ..< n]
            chunks.append(TextChunk(text))
        }

        if ntags > 0 && starts[0] > 0 {
            // copy text up to first tag into chunks
            let text = pattern[0 ..< starts[0]]
            chunks.append(TextChunk(text))
        }

        for i in 0 ..< ntags {
            // copy inside of <tag>
            let tag = pattern[starts[i] + start.length ..< stops[i]]
            var ruleOrToken = tag
            var label = ""
            let colon = tag.indexOf(":")
            if colon >= 0 {
                label = tag[0 ..< colon]
                ruleOrToken = tag[colon + 1 ..< tag.length]
            }
            chunks.append(try TagChunk(label, ruleOrToken))
            if i + 1 < ntags {
                // copy from end of <tag> to start of next
                let text = pattern[stops[i] + stop.length ..< starts[i] + 1]
                chunks.append(TextChunk(text))
            }
        }
        if ntags > 0 {
            let afterLastTag = stops[ntags - 1] + stop.length
            if afterLastTag < n {
                // copy text from end of last tag to end
                let text = pattern[afterLastTag ..< n]
                chunks.append(TextChunk(text))
            }
        }

        // strip out the escape sequences from text chunks but not tags
        for i in 0 ..< chunks.count {
            let c = chunks[i]
            if let tc = c as? TextChunk {
                let unescaped = tc.getText().replacingOccurrences(of: escape, with: "")
                if unescaped.length < tc.getText().length {
                    chunks[i] = TextChunk(unescaped)
                }
            }
        }

        return chunks
    }
}
