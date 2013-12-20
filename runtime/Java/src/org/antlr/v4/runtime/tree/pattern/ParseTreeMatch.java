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

	/** Did the tree vs pattern match? */
	public boolean succeeded() {
		return mismatchedNode==null;
	}

	/** Return the tree pattern we are matching against */
	public ParseTreePattern getPattern() {
		return pattern;
	}

	/** Return the parse tree we are trying to match to a pattern */
	public ParseTree getTree() {
		return tree;
	}

	public String toString() {
		return tree.getText();
	}
}
