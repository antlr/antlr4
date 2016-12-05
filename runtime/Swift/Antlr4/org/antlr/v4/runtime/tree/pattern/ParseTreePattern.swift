/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


/**
 * A pattern like {@code <ID> = <expr>;} converted to a {@link org.antlr.v4.runtime.tree.ParseTree} by
 * {@link org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher#compile(String, int)}.
 */

public class ParseTreePattern {
    /**
     * This is the backing field for {@link #getPatternRuleIndex()}.
     */
    private let patternRuleIndex: Int

    /**
     * This is the backing field for {@link #getPattern()}.
     */

    private let pattern: String

    /**
     * This is the backing field for {@link #getPatternTree()}.
     */

    private let patternTree: ParseTree

    /**
     * This is the backing field for {@link #getMatcher()}.
     */

    private let matcher: ParseTreePatternMatcher

    /**
     * Construct a new instance of the {@link org.antlr.v4.runtime.tree.pattern.ParseTreePattern} class.
     *
     * @param matcher The {@link org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher} which created this
     * tree pattern.
     * @param pattern The tree pattern in concrete syntax form.
     * @param patternRuleIndex The parser rule which serves as the root of the
     * tree pattern.
     * @param patternTree The tree pattern in {@link org.antlr.v4.runtime.tree.ParseTree} form.
     */
    public init(_ matcher: ParseTreePatternMatcher,
                _ pattern: String, _ patternRuleIndex: Int, _ patternTree: ParseTree) {
        self.matcher = matcher
        self.patternRuleIndex = patternRuleIndex
        self.pattern = pattern
        self.patternTree = patternTree
    }

    /**
     * Match a specific parse tree against this tree pattern.
     *
     * @param tree The parse tree to match against this tree pattern.
     * @return A {@link org.antlr.v4.runtime.tree.pattern.ParseTreeMatch} object describing the result of the
     * match operation. The {@link org.antlr.v4.runtime.tree.pattern.ParseTreeMatch#succeeded()} method can be
     * used to determine whether or not the match was successful.
     */

    public func match(_ tree: ParseTree) throws -> ParseTreeMatch {
        return try matcher.match(tree, self)
    }

    /**
     * Determine whether or not a parse tree matches this tree pattern.
     *
     * @param tree The parse tree to match against this tree pattern.
     * @return {@code true} if {@code tree} is a match for the current tree
     * pattern; otherwise, {@code false}.
     */
    public func matches(_ tree: ParseTree) throws -> Bool {
        return try matcher.match(tree, self).succeeded()
    }

    /**
     * Find all nodes using XPath and then try to match those subtrees against
     * this tree pattern.
     *
     * @param tree The {@link org.antlr.v4.runtime.tree.ParseTree} to match against this pattern.
     * @param xpath An expression matching the nodes
     *
     * @return A collection of {@link org.antlr.v4.runtime.tree.pattern.ParseTreeMatch} objects describing the
     * successful matches. Unsuccessful matches are omitted from the result,
     * regardless of the reason for the failure.
     */

    /*public func findAll(tree : ParseTree, _ xpath : String) -> Array<ParseTreeMatch> {
        var subtrees : Array<ParseTree> = XPath.findAll(tree, xpath, matcher.getParser());
        var matches : Array<ParseTreeMatch> = Array<ParseTreeMatch>();
        for t : ParseTree in subtrees {
            var match : ParseTreeMatch = match(t);
            if ( match.succeeded() ) {
                matches.add(match);
            }
        }
        return matches;
    }*/

    /**
     * Get the {@link org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher} which created this tree pattern.
     *
     * @return The {@link org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher} which created this tree
     * pattern.
     */

    public func getMatcher() -> ParseTreePatternMatcher {
        return matcher
    }

    /**
     * Get the tree pattern in concrete syntax form.
     *
     * @return The tree pattern in concrete syntax form.
     */

    public func getPattern() -> String {
        return pattern
    }

    /**
     * Get the parser rule which serves as the outermost rule for the tree
     * pattern.
     *
     * @return The parser rule which serves as the outermost rule for the tree
     * pattern.
     */
    public func getPatternRuleIndex() -> Int {
        return patternRuleIndex
    }

    /**
     * Get the tree pattern as a {@link org.antlr.v4.runtime.tree.ParseTree}. The rule and token tags from
     * the pattern are present in the parse tree as terminal nodes with a symbol
     * of type {@link org.antlr.v4.runtime.tree.pattern.RuleTagToken} or {@link org.antlr.v4.runtime.tree.pattern.TokenTagToken}.
     *
     * @return The tree pattern as a {@link org.antlr.v4.runtime.tree.ParseTree}.
     */

    public func getPatternTree() -> ParseTree {
        return patternTree
    }
}
