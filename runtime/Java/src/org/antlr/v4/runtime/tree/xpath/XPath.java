package org.antlr.v4.runtime.tree.xpath;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Represent a subset of XPath paths for use in identifying nodes in
 *  parse trees.
 *
 *  Split path into words and separators / and // then walk from left to right.
 *  At each separator-word pair, find set of nodes. Next stage uses those as
 *  work list.
 *
 * See TestXPath
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

	// TODO: check for invalid token/rule names, bad syntax

	public XPathElement[] split(String path) {
		Pattern pattern = Pattern.compile("//|/|\\w+|'.+?'|\\*"); // TODO: handle escapes in strings?
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
			if ( el.startsWith("/") ) {
				i++;
				if ( i>=n ) {
					System.out.println("missing element name after operator");
				}
				String next = pathStrings.get(i);
				boolean anywhere = el.equals("//");
				elements.add( getXPathElement(next, anywhere) );
				i++;
			}
			else {
				elements.add( getXPathElement(el, false) );
				i++;
			}
		}
		return elements.toArray(new XPathElement[0]);
	}

	public XPathElement getXPathElement(String word, boolean anywhere) {
		Map<String, Integer> ruleIndexes = toMap(parser.getRuleNames());
		Map<String, Integer> tokenTypes = toMap(parser.getTokenNames());
		if ( word.equals(WILDCARD) ) {
			return anywhere ?
				new XPathWildcardAnywhereElement() :
				new XPathWildcardElement();
		}
		else if ( word.charAt(0)=='\'' || Character.isUpperCase(word.charAt(0)) ) {
			return anywhere ?
				new XPathTokenAnywhereElement(word, tokenTypes.get(word)) :
				new XPathTokenElement(word, tokenTypes.get(word));
		}
		else {
			return anywhere ?
				new XPathRuleAnywhereElement(word, ruleIndexes.get(word)) :
				new XPathRuleElement(word, ruleIndexes.get(word));
		}
	}

	// following java xpath like methods; not sure it's best way
	/** Return a list of all nodes starting at t as root that satisfy the path.
	 */
	public Collection<ParseTree> evaluate(final ParseTree t) {
		ParserRuleContext dummyRoot = new ParserRuleContext();
		dummyRoot.children = new ArrayList<ParseTree>() {{add(t);}}; // don't set t's parent.

		Collection<ParseTree> work = new ArrayList<ParseTree>();
		work.add(dummyRoot);

		int i = 0;
		while ( i < elements.length ) {
			Collection<ParseTree> next = new ArrayList<ParseTree>();
			for (ParseTree node : work) {
				if ( node.getChildCount()>0 ) {
					// only try to match next element if it has children
					// e.g., //func/*/stat might have a token node for which
					// we can't go looking for stat nodes.
					Collection<? extends ParseTree> matching = elements[i].evaluate(node);
					next.addAll(matching);
				}
			}
			i++;
			work = next;
		}

		return work;
	}

	public static Map<String, Integer> toMap(String[] keys) {
		Map<String, Integer> m = new HashMap<String, Integer>();
		for (int i=0; i<keys.length; i++) {
			m.put(keys[i], i);
		}
		return m;
	}

}
