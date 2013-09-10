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
 *  *					INVALID
 *  * slash *			INVALID
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
					elements.add(new XPathRootedElement(el));
				}
				else {
					elements.add(new XPathNodeElement(next));
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
				if ( i==0 ) { // "ID" is first element w/o a "//" or "/"
					elements.add(new XPathAnywhereElement(el));
				}
				else {
					elements.add(new XPathNodeElement(el));
				}
				i++;
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
