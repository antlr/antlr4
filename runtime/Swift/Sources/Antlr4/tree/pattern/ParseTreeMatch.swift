/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


/// 
/// Represents the result of matching a _org.antlr.v4.runtime.tree.ParseTree_ against a tree pattern.
/// 

public class ParseTreeMatch: CustomStringConvertible {
    /// 
    /// This is the backing field for _#getTree()_.
    /// 
    private let tree: ParseTree

    /// 
    /// This is the backing field for _#getPattern()_.
    /// 
    private let pattern: ParseTreePattern

    /// 
    /// This is the backing field for _#getLabels()_.
    /// 
    private let labels: MultiMap<String, ParseTree>

    /// 
    /// This is the backing field for _#getMismatchedNode()_.
    /// 
    private let mismatchedNode: ParseTree?

    /// 
    /// Constructs a new instance of _org.antlr.v4.runtime.tree.pattern.ParseTreeMatch_ from the specified
    /// parse tree and pattern.
    /// 
    /// - Parameter tree: The parse tree to match against the pattern.
    /// - Parameter pattern: The parse tree pattern.
    /// - Parameter labels: A mapping from label names to collections of
    /// _org.antlr.v4.runtime.tree.ParseTree_ objects located by the tree pattern matching process.
    /// - Parameter mismatchedNode: The first node which failed to match the tree
    /// pattern during the matching process.
    /// 
    /// - Throws: ANTLRError.ilegalArgument if `tree` is `null`
    /// - Throws: ANTLRError.ilegalArgument if `pattern` is `null`
    /// - Throws: ANTLRError.ilegalArgument if `labels` is `null`
    /// 
    public init(_ tree: ParseTree, _ pattern: ParseTreePattern, _ labels: MultiMap<String, ParseTree>, _ mismatchedNode: ParseTree?) {

        self.tree = tree
        self.pattern = pattern
        self.labels = labels
        self.mismatchedNode = mismatchedNode
    }

    /// 
    /// Get the last node associated with a specific `label`.
    /// 
    /// For example, for pattern `<id:ID>`, `get("id")` returns the
    /// node matched for that `ID`. If more than one node
    /// matched the specified label, only the last is returned. If there is
    /// no node associated with the label, this returns `null`.
    /// 
    /// Pattern tags like `<ID>` and `<expr>` without labels are
    /// considered to be labeled with `ID` and `expr`, respectively.
    /// 
    /// - Parameter label: The label to check.
    /// 
    /// - Returns: The last _org.antlr.v4.runtime.tree.ParseTree_ to match a tag with the specified
    /// label, or `null` if no parse tree matched a tag with the label.
    /// 

    public func get(_ label: String) -> ParseTree? {
        if let parseTrees = labels.get(label) , parseTrees.count > 0 {
            return parseTrees[parseTrees.count - 1]   // return last if multiple
        } else {
            return nil
        }

    }

    /// 
    /// Return all nodes matching a rule or token tag with the specified label.
    /// 
    /// If the `label` is the name of a parser rule or token in the
    /// grammar, the resulting list will contain both the parse trees matching
    /// rule or tags explicitly labeled with the label and the complete set of
    /// parse trees matching the labeled and unlabeled tags in the pattern for
    /// the parser rule or token. For example, if `label` is `"foo"`,
    /// the result will contain __all__ of the following.
    /// 
    /// * Parse tree nodes matching tags of the form `<foo:anyRuleName>` and
    /// `<foo:AnyTokenName>`.
    /// * Parse tree nodes matching tags of the form `<anyLabel:foo>`.
    /// * Parse tree nodes matching tags of the form `<foo>`.
    /// 
    /// - Parameter label: The label.
    /// 
    /// - Returns: A collection of all _org.antlr.v4.runtime.tree.ParseTree_ nodes matching tags with
    /// the specified `label`. If no nodes matched the label, an empty list
    /// is returned.
    /// 
    public func getAll(_ label: String) -> Array<ParseTree> {
        let nodes: Array<ParseTree>? = labels.get(label)
        if nodes == nil {
            return Array<ParseTree>()
        }

        return nodes!
    }

    /// 
    /// Return a mapping from label &rarr; [list of nodes].
    /// 
    /// The map includes special entries corresponding to the names of rules and
    /// tokens referenced in tags in the original pattern. For additional
    /// information, see the description of _#getAll(String)_.
    /// 
    /// - Returns: A mapping from labels to parse tree nodes. If the parse tree
    /// pattern did not contain any rule or token tags, this map will be empty.
    /// 
    public func getLabels() -> MultiMap<String, ParseTree> {
        return labels
    }

    /// 
    /// Get the node at which we first detected a mismatch.
    /// 
    /// - Returns: the node at which we first detected a mismatch, or `null`
    /// if the match was successful.
    /// 
    public func getMismatchedNode() -> ParseTree? {
        return mismatchedNode
    }

    /// 
    /// Gets a value indicating whether the match operation succeeded.
    /// 
    /// - Returns: `true` if the match operation succeeded; otherwise,
    /// `false`.
    /// 
    public func succeeded() -> Bool {
        return mismatchedNode == nil
    }

    /// 
    /// Get the tree pattern we are matching against.
    /// 
    /// - Returns: The tree pattern we are matching against.
    /// 
    public func getPattern() -> ParseTreePattern {
        return pattern
    }

    /// 
    /// Get the parse tree we are trying to match to a pattern.
    /// 
    /// - Returns: The _org.antlr.v4.runtime.tree.ParseTree_ we are trying to match to a pattern.
    /// 
    public func getTree() -> ParseTree {
        return tree
    }

    public var description: String {
        let info = succeeded() ? "succeeded" : "failed"
        return "Match \(info); found \(getLabels().size()) labels"
    }
}
