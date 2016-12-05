/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


/**
 * Represents the result of matching a {@link org.antlr.v4.runtime.tree.ParseTree} against a tree pattern.
 */

public class ParseTreeMatch: CustomStringConvertible {
    /**
     * This is the backing field for {@link #getTree()}.
     */
    private let tree: ParseTree

    /**
     * This is the backing field for {@link #getPattern()}.
     */
    private let pattern: ParseTreePattern

    /**
     * This is the backing field for {@link #getLabels()}.
     */
    private let labels: MultiMap<String, ParseTree>

    /**
     * This is the backing field for {@link #getMismatchedNode()}.
     */
    private let mismatchedNode: ParseTree?

    /**
     * Constructs a new instance of {@link org.antlr.v4.runtime.tree.pattern.ParseTreeMatch} from the specified
     * parse tree and pattern.
     *
     * @param tree The parse tree to match against the pattern.
     * @param pattern The parse tree pattern.
     * @param labels A mapping from label names to collections of
     * {@link org.antlr.v4.runtime.tree.ParseTree} objects located by the tree pattern matching process.
     * @param mismatchedNode The first node which failed to match the tree
     * pattern during the matching process.
     *
     * @exception IllegalArgumentException if {@code tree} is {@code null}
     * @exception IllegalArgumentException if {@code pattern} is {@code null}
     * @exception IllegalArgumentException if {@code labels} is {@code null}
     */
    public init(_ tree: ParseTree, _ pattern: ParseTreePattern, _ labels: MultiMap<String, ParseTree>, _ mismatchedNode: ParseTree?) {

        self.tree = tree
        self.pattern = pattern
        self.labels = labels
        self.mismatchedNode = mismatchedNode
    }

    /**
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
     * @param label The label to check.
     *
     * @return The last {@link org.antlr.v4.runtime.tree.ParseTree} to match a tag with the specified
     * label, or {@code null} if no parse tree matched a tag with the label.
     */

    public func get(_ label: String) -> ParseTree? {
        if let parseTrees = labels.get(label) , parseTrees.count > 0 {
            return parseTrees[parseTrees.count - 1]   // return last if multiple
        } else {
            return nil
        }

    }

    /**
     * Return all nodes matching a rule or token tag with the specified label.
     *
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
     * @param label The label.
     *
     * @return A collection of all {@link org.antlr.v4.runtime.tree.ParseTree} nodes matching tags with
     * the specified {@code label}. If no nodes matched the label, an empty list
     * is returned.
     */

    public func getAll(_ label: String) -> Array<ParseTree> {
        let nodes: Array<ParseTree>? = labels.get(label)
        if nodes == nil {
            return Array<ParseTree>()
        }

        return nodes!
    }

    /**
     * Return a mapping from label &rarr; [list of nodes].
     *
     * <p>The map includes special entries corresponding to the names of rules and
     * tokens referenced in tags in the original pattern. For additional
     * information, see the description of {@link #getAll(String)}.</p>
     *
     * @return A mapping from labels to parse tree nodes. If the parse tree
     * pattern did not contain any rule or token tags, this map will be empty.
     */

    public func getLabels() -> MultiMap<String, ParseTree> {
        return labels
    }

    /**
     * Get the node at which we first detected a mismatch.
     *
     * @return the node at which we first detected a mismatch, or {@code null}
     * if the match was successful.
     */

    public func getMismatchedNode() -> ParseTree? {
        return mismatchedNode
    }

    /**
     * Gets a value indicating whether the match operation succeeded.
     *
     * @return {@code true} if the match operation succeeded; otherwise,
     * {@code false}.
     */
    public func succeeded() -> Bool {
        return mismatchedNode == nil
    }

    /**
     * Get the tree pattern we are matching against.
     *
     * @return The tree pattern we are matching against.
     */

    public func getPattern() -> ParseTreePattern {
        return pattern
    }

    /**
     * Get the parse tree we are trying to match to a pattern.
     *
     * @return The {@link org.antlr.v4.runtime.tree.ParseTree} we are trying to match to a pattern.
     */

    public func getTree() -> ParseTree {
        return tree
    }

    /**
     * {@inheritDoc}
     */

    public func toString() -> String {
        return description
    }
    public var description: String {
        let info = succeeded() ? "succeeded" : "failed"
        return "Match \(info); found \(getLabels().size()) labels"
    }
}
