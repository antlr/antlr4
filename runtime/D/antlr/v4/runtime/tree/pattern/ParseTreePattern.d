/*
 * [The "BSD license"]
 * Copyright (c) 2013 Terence Parr
 * Copyright (c) 2013 Sam Harwell
 * Copyright (c) 2017 Egbert Voigt
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

module antlr.v4.runtime.tree.pattern.ParseTreePattern;

import antlr.v4.runtime.tree.ParseTree;
import antlr.v4.runtime.tree.pattern.ParseTreeMatch;
import antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher;
import antlr.v4.runtime.tree.xpath.XPath;

/**
 * @uml
 * A pattern like {@code <ID> = <expr>;} converted to a {@link ParseTree} by
 * {@link ParseTreePatternMatcher#compile(String, int)}.
 */
class ParseTreePattern
{

    /**
     * @uml
     * This is the backing field for {@link #getPatternRuleIndex()}.
     * @final
     */
    private int patternRuleIndex;

    /**
     * @uml
     * This is the backing field for {@link #getPattern()}.
     * @final
     */
    private string pattern;

    /**
     * @uml
     * This is the backing field for {@link #getPatternTree()}.
     */
    public ParseTree patternTree;

    /**
     * @uml
     * This is the backing field for {@link #getMatcher()}.
     */
    public ParseTreePatternMatcher matcher;

    /**
     * @uml
     * Construct a new instance of the {@link ParseTreePattern} class.
     *
     *  @param matcher The {@link ParseTreePatternMatcher} which created this
     *  tree pattern.
     *  @param pattern The tree pattern in concrete syntax form.
     *  @param patternRuleIndex The parser rule which serves as the root of the
     *  tree pattern.
     *  @param patternTree The tree pattern in {@link ParseTree} form.
     */
    public this(ParseTreePatternMatcher matcher, string pattern, int patternRuleIndex, ParseTree patternTree)
    {
        this.matcher = matcher;
        this.patternRuleIndex = patternRuleIndex;
        this.pattern = pattern;
        this.patternTree = patternTree;
    }

    /**
     * @uml
     * Match a specific parse tree against this tree pattern.
     *
     *  @param tree The parse tree to match against this tree pattern.
     *  @return A {@link ParseTreeMatch} object describing the result of the
     * match operation. The {@link ParseTreeMatch#succeeded()} method can be
     * used to determine whether or not the match was successful.
     */
    public ParseTreeMatch match(ParseTree tree)
    {
        return matcher.match(tree, this);
    }

    /**
     * @uml
     * Determine whether or not a parse tree matches this tree pattern.
     *
     *  @param tree The parse tree to match against this tree pattern.
     *  @return {@code true} if {@code tree} is a match for the current tree
     * pattern; otherwise, {@code false}.
     */
    public bool matches(ParseTree tree)
    {
        return matcher.match(tree, this).succeeded();
    }

    /**
     * @uml
     * Find all nodes using XPath and then try to match those subtrees against
     *  this tree pattern.
     *
     *  @param tree The {@link ParseTree} to match against this pattern.
     *  @param xpath An expression matching the nodes
     *
     *  @return A collection of {@link ParseTreeMatch} objects describing the
     * successful matches. Unsuccessful matches are omitted from the result,
     * regardless of the reason for the failure.
     */
    public ParseTreeMatch[] findAll(ParseTree tree, string xpath)
    {
        ParseTree[] subtrees = XPath.findAll(tree, xpath, matcher.getParser());
        ParseTreeMatch[] matches;
        foreach (ParseTree t; subtrees) {
            ParseTreeMatch match = match(t);
            if ( match.succeeded() ) {
                matches ~= match;
            }
        }
        return matches;
    }

    /**
     * @uml
     * et the {@link ParseTreePatternMatcher} which created this tree pattern.
     *
     *  @return The {@link ParseTreePatternMatcher} which created this tree
     * pattern.
     */
    public ParseTreePatternMatcher getMatcher()
    {
        return matcher;
    }

    /**
     * @uml
     * Get the tree pattern in concrete syntax form.
     *
     *  @return The tree pattern in concrete syntax form.
     */
    public string getPattern()
    {
        return pattern;
    }

    /**
     * @uml
     * Get the parser rule which serves as the outermost rule for the tree
     * pattern.
     *
     *  @return The parser rule which serves as the outermost rule for the tree
     * pattern.
     */
    public int getPatternRuleIndex()
    {
        return patternRuleIndex;
    }

    /**
     * @uml
     * Get the tree pattern as a {@link ParseTree}. The rule and token tags from
     * the pattern are present in the parse tree as terminal nodes with a symbol
     * of type {@link RuleTagToken} or {@link TokenTagToken}.
     *
     *  @return The tree pattern as a {@link ParseTree}.
     */
    public ParseTree getPatternTree()
    {
        return patternTree;
    }

}
