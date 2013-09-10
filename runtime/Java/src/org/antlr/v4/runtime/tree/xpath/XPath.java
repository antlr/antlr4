package org.antlr.v4.runtime.tree.xpath;

import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Collection;

/** Represent a subset of XPath paths for use in identifying nodes in
 *  parse trees.
 *
 *  Split path into words and separators / and // then walk from left to right.
 *  At each separator-word pair, find set of nodes. Next stage uses those as
 *  work list.
 *
 *  ID					all IDs anywhere
 *  /ID					an ID node if at root
 *  /classdef/field		all field children of classdef at root.
 *  //ID				all IDs anywhere (same as ID)
 *  classdef//funcdef	all funcs under classdef somewhere
 *  classdef/*			all children of classdefs anywhere in tree
 *
 *  The "root" is relative to the node passed to evaluate().
 */
public class XPath {

	public static final String WILDCARD = "*"; // word not operator/separator

	protected String path;

	public XPath(String path) {
		this.path = path;
	}

	// following java xpath like methods; not sure it's best way
	/** Return a list of all nodes starting at t that satisfy the path.
	 *
	 */
	Collection<? extends ParseTree> evaluate(ParseTree t) {
		return null;
	}
}
