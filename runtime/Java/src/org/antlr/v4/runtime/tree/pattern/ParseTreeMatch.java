package org.antlr.v4.runtime.tree.pattern;

import org.antlr.v4.runtime.misc.MultiMap;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Collections;
import java.util.List;

/** The result of matching a tree against a tree pattern.
 *  If there is no match, field mismatchedNode is set to the offending
 *  treenode.
 */
public class ParseTreeMatch {
	/** Tree we tried to match */
	protected ParseTree tree;

	/** To what pattern? */
	protected ParseTreePattern pattern;

	/** Map a label or token/rule name to List of nodes in tree we match */
	protected MultiMap<String, ParseTree> labels;

	/** Where in actual parse tree we failed if we can't match pattern */
	protected ParseTree mismatchedNode;

	public ParseTreeMatch(ParseTree tree, ParseTreePattern pattern) {
		this.tree = tree;
		this.pattern = pattern;
		this.labels = new MultiMap<String, ParseTree>();
	}

	/** Get the node associated with label. E.g., for  pattern <id:ID>,
	 *  get("id") returns the node matched for that ID. If there are more than
	 *  one nodes matched with that label, this returns the first one matched.
	 *  If there is no node associated with the label, this returns null.
	 *
	 *  Pattern tags like <ID> and <expr> without labels are considered to be
	 *  labeled with ID and expr, respectively.
	 */
	public ParseTree get(String label) {
		List<ParseTree> parseTrees = labels.get(label);
		if ( parseTrees==null ) return null;
		return parseTrees.get(0); // return first if multiple
	}

	/** Return all nodes matched that are labeled with parameter label.
	 *  If there is only one such node, return a list with one element.
	 *  If there are no nodes matched with that label, return an empty list.
	 *
	 *  Pattern tags like <ID> and <expr> without labels are considered to be
	 *  labeled with ID and expr, respectively.
	 */
	public List<ParseTree> getAll(String label) {
		List<ParseTree> nodes = labels.get(label);
		if ( nodes==null ) return Collections.emptyList();
		return nodes;
	}

	/** Return a mapping from label->[list of nodes]. This includes
	 *  token and rule references such as <ID> and <expr>
	 */
	public MultiMap<String, ParseTree> getLabels() {
		return labels;
	}

	/** Return the node at which we first detected a mismatch. Return
	 *  null if we have not found an error.
	 */
	public ParseTree getMismatchedNode() {
		return mismatchedNode;
	}

	/** Return the tree pattern we are matching against */
	public ParseTreePattern getPattern() {
		return pattern;
	}

	/** Return the parse tree we are trying to match to a pattern */
	public ParseTree getTree() {
		return tree;
	}
}
