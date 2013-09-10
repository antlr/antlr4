package org.antlr.v4.runtime.tree.xpath;

import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 *  //ID				INVALID (same as ID)
 *  classdef//funcdef	all funcs under classdef somewhere
 *  classdef/*			all children of classdefs anywhere in tree
 *
 *  The "root" is relative to the node passed to evaluate().
 */
public class XPath {
	public static final String WILDCARD = "*"; // word not operator/separator

	protected String path;
	protected XPathElement[] elements;

	public XPath(String path) {
		this.path = path;
		elements = split(path);
	}

	public XPathElement[] split(String path) {
		Pattern pattern = Pattern.compile("//|/|\\w+|\\*");
		Matcher matcher = pattern.matcher(path);
		System.out.println("path="+path);
		List<String> pathStrings = new ArrayList<String>();
		while (matcher.find()) {
			pathStrings.add(matcher.group());
		}
		List<XPathElement> elements = new ArrayList<XPathElement>();
		for (String el : pathStrings) {
		System.out.println("\t"+ el);
			if ( el.equals("/") ) {

			}
			else if ( el.equals("//") ) {

			}
			else if ( el.equals("*") ) {

			}
			else {
				elements.add(new XPathNodeElement(el));
			}
		}
		return elements.toArray(new XPathElement[0]);
	}

	// following java xpath like methods; not sure it's best way
	/** Return a list of all nodes starting at t as root that satisfy the path.
	 */
	public Collection<? extends ParseTree> evaluate(ParseTree t) {
		return null;
	}
}
