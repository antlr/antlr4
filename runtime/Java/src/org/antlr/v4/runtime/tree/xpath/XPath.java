package org.antlr.v4.runtime.tree.xpath;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/** Represent a subset of XPath XML path syntax for use in identifying nodes in
 *  parse trees.
 *
 *  Split path into words and separators / and // via ANTLR itself then walk
 *  path elements from left to right.  At each separator-word pair, find set
 *  of nodes. Next stage uses those as work list.
 *
 *  The basic interface is ParseTree.findAll(parser, pathString). But that is
 *  just shorthand for:
 *
 *  XPath p = new XPath(parser, xpath);
 *  return p.evaluate(this);
 *
 *  See {@code org.antlr.v4.test.TestXPath} for descriptions. In short, this allows
 *  operators:
 *
 *      /         root
 *      //        anywhere
 *      !         invert; this must appear directly after root or anywhere operator
 *
 *  and path elements:
 *
 *      ID        token name
 *      'string'  any string literal token from the grammar
 *      expr      rule name
 *      *         wildcard matching any node
 *
 *  Whitespace is not allowed.
 */
public class XPath {
	public static final String WILDCARD = "*"; // word not operator/separator
	public static final String NOT = "!"; 	   // word for invert operator

	protected String path;
	protected XPathElement[] elements;
	protected Parser parser;

	public XPath(Parser parser, String path) {
		this.parser = parser;
		this.path = path;
		elements = split(path);
//		System.out.println(Arrays.toString(elements));
	}

	// TODO: check for invalid token/rule names, bad syntax

	public XPathElement[] split(String path) {
		ANTLRInputStream in;
		try {
			in = new ANTLRInputStream(new StringReader(path));
		}
		catch (IOException ioe) {
			throw new IllegalArgumentException("Could not read path: "+path, ioe);
		}
		XPathLexer lexer = new XPathLexer(in) {
			public void recover(LexerNoViableAltException e) { throw e;	}
		};
		lexer.removeErrorListeners();
		lexer.addErrorListener(new XPathLexerErrorListener());
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		try {
			tokenStream.fill();
		}
		catch (LexerNoViableAltException e) {
			int pos = lexer.getCharPositionInLine();
			String msg = "Invalid tokens or characters at index "+pos+" in path '"+path+"'";
			throw new IllegalArgumentException(msg, e);
		}

		List<Token> tokens = tokenStream.getTokens();
//		System.out.println("path="+path+"=>"+tokens);
		List<XPathElement> elements = new ArrayList<XPathElement>();
		int n = tokens.size();
		int i=0;
loop:
		while ( i<n ) {
			Token el = tokens.get(i);
			Token next = null;
			switch ( el.getType() ) {
				case XPathLexer.ROOT :
				case XPathLexer.ANYWHERE :
					boolean anywhere = el.getType() == XPathLexer.ANYWHERE;
					i++;
					next = tokens.get(i);
					boolean invert = next.getType()==XPathLexer.BANG;
					if ( invert ) {
						i++;
						next = tokens.get(i);
					}
					XPathElement pathElement = getXPathElement(next, anywhere);
					pathElement.invert = invert;
					elements.add(pathElement);
					i++;
					break;

				case XPathLexer.TOKEN_REF :
				case XPathLexer.RULE_REF :
				case XPathLexer.WILDCARD :
					elements.add( getXPathElement(el, false) );
					i++;
					break;

				case Token.EOF :
					break loop;

				default :
					throw new IllegalArgumentException("Unknowth path element "+el);
			}
		}
		return elements.toArray(new XPathElement[0]);
	}

	/** Convert word like * or ID or expr to a path element. anywhere is true
	 *  if // precedes the word.
	 */
	protected XPathElement getXPathElement(Token wordToken, boolean anywhere) {
		if ( wordToken.getType()==Token.EOF ) {
			throw new IllegalArgumentException("Missing path element at end of path");
		}
		String word = wordToken.getText();
		int ttype = parser.getTokenType(word);
		int ruleIndex = parser.getRuleIndex(word);
		switch ( wordToken.getType() ) {
			case XPathLexer.WILDCARD :
				return anywhere ?
					new XPathWildcardAnywhereElement() :
					new XPathWildcardElement();
			case XPathLexer.TOKEN_REF :
			case XPathLexer.STRING :
				if ( ttype==Token.INVALID_TYPE ) {
					throw new IllegalArgumentException(word+
													   " at index "+
													   wordToken.getStartIndex()+
													   " isn't a valid token name");
				}
				return anywhere ?
					new XPathTokenAnywhereElement(word, ttype) :
					new XPathTokenElement(word, ttype);
			default :
				if ( ruleIndex==-1 ) {
					throw new IllegalArgumentException(word+
													   " at index "+
													   wordToken.getStartIndex()+
													   " isn't a valid rule name");
				}
				return anywhere ?
					new XPathRuleAnywhereElement(word, ruleIndex) :
					new XPathRuleElement(word, ruleIndex);
		}
	}


	public static Collection<ParseTree> findAll(ParseTree tree, String xpath, Parser parser) {
		XPath p = new XPath(parser, xpath);
		return p.evaluate(tree);
	}

	/** Return a list of all nodes starting at t as root that satisfy the path.
	 *  The root / is relative to the node passed to evaluate().
	 */
	public Collection<ParseTree> evaluate(final ParseTree t) {
		ParserRuleContext dummyRoot = new ParserRuleContext();
		dummyRoot.children = Collections.singletonList(t); // don't set t's parent.

		Collection<ParseTree> work = Collections.<ParseTree>singleton(dummyRoot);

		int i = 0;
		while ( i < elements.length ) {
			Collection<ParseTree> next = new LinkedHashSet<ParseTree>();
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
}
