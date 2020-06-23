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

module antlr.v4.runtime.tree.pattern.ParseTreeMatch;

import std.format;
import antlr.v4.runtime.IllegalArgumentException;
import antlr.v4.runtime.tree.ParseTree;
import antlr.v4.runtime.tree.pattern.ParseTreePattern;

/**
 * TODO add class description
 * @uml
 * Represents the result of matching a {@link ParseTree} against a tree pattern.
 */
class ParseTreeMatch
{

    /**
     * @uml
     * This is the backing field for {@link #getTree()}.
     */
    public ParseTree tree;

    /**
     * @uml
     * This is the backing field for {@link #getPattern()}.
     */
    private ParseTreePattern pattern;

    /**
     * @uml
     * This is the backing field for {@link #getLabels()}.
     */
    private ParseTree[][string] labels;

    /**
     * @uml
     * This is the backing field for {@link #getMismatchedNode()}.
     */
    private ParseTree mismatchedNode;

    /**
     * @uml
     * Constructs a new instance of {@link ParseTreeMatch} from the specified
     * parse tree and pattern.
     *
     *  @param tree The parse tree to match against the pattern.
     *  @param pattern The parse tree pattern.
     *  @param labels A mapping from label names to collections of
     *  {@link ParseTree} objects located by the tree pattern matching process.
     *  @param mismatchedNode The first node which failed to match the tree
     * pattern during the matching process.
     *
     *  @exception IllegalArgumentException if {@code tree} is {@code null}
     *  @exception IllegalArgumentException if {@code pattern} is {@code null}
     *  @exception IllegalArgumentException if {@code labels} is {@code null}
     */
    public this(ParseTree tree, ParseTreePattern pattern, ParseTree[][string] labels, ParseTree mismatchedNode)
    {
        if (tree is null) {
            throw new IllegalArgumentException("tree cannot be null");
        }
        if (pattern is null) {
            throw new IllegalArgumentException("pattern cannot be null");
        }
        if (labels is null) {
            throw new IllegalArgumentException("labels cannot be null");
        }
        this.tree = tree;
        this.pattern = pattern;
        this.labels = labels;
        this.mismatchedNode = mismatchedNode;
    }

    /**
     * @uml
     * Get the last node associated with a specific {@code label}.
     *
     * <p>For example, for pattern {@code <id:ID>}, {@code get("id")} returns the
     * node matched for that {@code ID}. If more than one node
     * matched the specified label, only the last is returned. If there is
     * no node associated with the label, this returns {@code null}.</p>
     *
     * <p>Pattern tags like {@code <ID>} and {@code <expr>} without labels are
     * considered to be labeled with {@code ID} and {@code expr}, respectively.</p>
     *
     *  @param label The label to check.
     *
     *  @return The last {@link ParseTree} to match a tag with the specified
     * label, or {@code null} if no parse tree matched a tag with the label.
     */
    public ParseTree get(string label)
    {
        ParseTree[] parseTrees = labels[label];
        if (parseTrees is null || parseTrees.length == 0) {
            return null;
        }
        return parseTrees[parseTrees.length-1]; // return last if multiple
    }

    /**
     * @uml
     * Return all nodes matching a rule or token tag with the specified label.
     * 	 *
     * <p>If the {@code label} is the name of a parser rule or token in the
     * grammar, the resulting list will contain both the parse trees matching
     * rule or tags explicitly labeled with the label and the complete set of
     * parse trees matching the labeled and unlabeled tags in the pattern for
     * the parser rule or token. For example, if {@code label} is {@code "foo"},
     * the result will contain <em>all</em> of the following.</p>
     *
     * <ul>
     * <li>Parse tree nodes matching tags of the form {@code <foo:anyRuleName>} and
     * {@code <foo:AnyTokenName>}.</li>
     * <li>Parse tree nodes matching tags of the form {@code <anyLabel:foo>}.</li>
     * <li>Parse tree nodes matching tags of the form {@code <foo>}.</li>
     * </ul>
     *
     *  @param label The label.
     *
     *  @return A collection of all {@link ParseTree} nodes matching tags with
     * the specified {@code label}. If no nodes matched the label, an empty list
     * is returned.
     */
    public ParseTree[] getAll(string label)
    {
        ParseTree[] nodes = labels[label];
        if (nodes is null) {
            ParseTree[] empty;
            return empty;
        }
        return nodes;
    }

    /**
     * @uml
     * Return a mapping from label &rarr; [list of nodes].
     *
     * <p>The map includes special entries corresponding to the names of rules and
     * tokens referenced in tags in the original pattern. For additional
     * information, see the description of {@link #getAll(String)}.</p>
     *
     *  @return A mapping from labels to parse tree nodes. If the parse tree
     * pattern did not contain any rule or token tags, this map will be empty.
     */
    public ParseTree[][string] getLabels()
    {
        return labels;
    }

    /**
     * @uml
     * Get the node at which we first detected a mismatch.
     *
     *  @return the node at which we first detected a mismatch, or {@code null}
     * if the match was successful.
     */
    public ParseTree getMismatchedNode()
    {
        return mismatchedNode;
    }

    /**
     * @uml
     * Gets a value indicating whether the match operation succeeded.
     *
     *  @return {@code true} if the match operation succeeded; otherwise,
     * {@code false}.
     */
    public bool succeeded()
    {
        return mismatchedNode is null;
    }

    /**
     * @uml
     * Get the tree pattern we are matching against.
     *
     *  @return The tree pattern we are matching against.
     */
    public ParseTreePattern getPattern()
    {
        return pattern;
    }

    /**
     * @uml
     * Get the parse tree we are trying to match to a pattern.
     *
     *  @return The {@link ParseTree} we are trying to match to a pattern.
     */
    public ParseTree getTree()
    {
        return tree;
    }

    /**
     * @uml
     * @override
     */
    public override string toString()
    {
        return format("Match %s; found %d labels",
			succeeded() ? "succeeded" : "failed",
			getLabels().length);
    }

}
