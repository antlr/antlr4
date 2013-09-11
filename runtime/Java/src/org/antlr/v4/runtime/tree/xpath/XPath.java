package org.antlr.v4.runtime.tree.xpath;

import org.antlr.v4.runtime.Parser;
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
 *  //ID					all IDs anywhere
 *  /ID					an ID node if at root
 *  /classdef/field		all field children of classdef at root.
 *  ID					INVALID (must have // in front or /)
 *  //classdef//funcdef	all funcs under classdef somewhere
 *  //classdef/*			all children of classdefs anywhere in tree
 *  *					INVALID
 *  /*					root node
 *  //*					every node
 *  /*slash*			All children of root
 *  /* slash *			INVALID
 *
 * these are all the same: returns t if t is classdef root node
 *  [9/10/13 6:35:13 PM] Terence Parr: eval(t, "classdef")
 [9/10/13 6:35:45 PM] Terence Parr: eval(t, "/classdef")
 [9/10/13 6:36:44 PM] Terence Parr: eval(t, "/*")
 *
 *  The "root" is relative to the node passed to evaluate().
 */
public class XPath {
	public static final String WILDCARD = "*"; // word not operator/separator

	protected String path;
	protected XPathElement[] elements;
	protected Parser parser;

	public XPath(Parser parser, String path) {
		this.parser = parser;
		this.path = path;
		elements = split(path);
		System.out.println(Arrays.toString(elements));
	}

	public XPathElement[] split(String path) {
		Pattern pattern = Pattern.compile("//|/|\\w+|\\*");
		Matcher matcher = pattern.matcher(path);
		List<String> pathStrings = new ArrayList<String>();
		while (matcher.find()) {
			pathStrings.add(matcher.group());
		}
		System.out.println("path="+path+"=>"+pathStrings);
		List<XPathElement> elements = new ArrayList<XPathElement>();
		int n = pathStrings.size();
		int i=0;
		while ( i<n ) {
			String el = pathStrings.get(i);
			if ( el.equals("/") ) {
				i++;
				if ( i>=n ) {
					System.out.println("missing element name after operator");
				}
				String next = pathStrings.get(i);
				if ( i==1 ) { // "/ID" is rooted element if '/' is first el
					elements.add(new XPathRootRuleElement(next));
				}
				else {
					elements.add(new XPathTokenElement(next));
				}
				i++;
			}
			else if ( el.equals("//") ) {
				i++;
				if ( i>=n ) {
					System.out.println("missing element name after operator");
				}
				String next = pathStrings.get(i);
				elements.add(new XPathAnywhereElement(next));
				i++;
			}
			else {
				elements.add(new XPathTokenElement(el));
				i++;
			}
		}
		return elements.toArray(new XPathElement[0]);
	}

	// following java xpath like methods; not sure it's best way
	/** Return a list of all nodes starting at t as root that satisfy the path.
	 */
	public Collection<? extends ParseTree> evaluate(ParseTree t) {
		// do just first for now
		return elements[0].evaluate(t);
	}
}
