/*
 * Copyright (c) 2012-2019 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.tree.xpath.XPath;

import std.array;
import std.stdio;
import std.file;
import std.format;
import std.conv;
import std.container : DList;
import antlr.v4.runtime.Parser;
import antlr.v4.runtime.Token;
import antlr.v4.runtime.TokenConstantDefinition;
import antlr.v4.runtime.CommonTokenStream;
import antlr.v4.runtime.ANTLRInputStream;
import antlr.v4.runtime.IllegalArgumentException;
import antlr.v4.runtime.LexerNoViableAltException;
import antlr.v4.runtime.ParserRuleContext;
import antlr.v4.runtime.tree.ParseTree;
import antlr.v4.runtime.tree.xpath.XPathElement;
import antlr.v4.runtime.tree.xpath.XPathLexerErrorListener;
import antlr.v4.runtime.tree.xpath.XPathLexer;
import antlr.v4.runtime.tree.xpath.XPathWildcardAnywhereElement;
import antlr.v4.runtime.tree.xpath.XPathWildcardElement;
import antlr.v4.runtime.tree.xpath.XPathRuleElement;
import antlr.v4.runtime.tree.xpath.XPathRuleAnywhereElement;
import antlr.v4.runtime.tree.xpath.XPathTokenElement;
import antlr.v4.runtime.tree.xpath.XPathTokenAnywhereElement;

/**
 * @uml
 * Represent a subset of XPath XML path syntax for use in identifying nodes in
 * parse trees.
 *
 * <p>
 * Split path into words and separators {@code /} and {@code //} via ANTLR
 * itself then walk path elements from left to right. At each separator-word
 * pair, find set of nodes. Next stage uses those as work list.</p>
 *
 * <p>
 * The basic interface is
 * {@link XPath#findAll ParseTree.findAll}{@code (tree, pathString, parser)}.
 * But that is just shorthand for:</p>
 *
 * <pre>
 * {@link XPath} p = new {@link XPath#XPath XPath}(parser, pathString);
 * return p.{@link #evaluate evaluate}(tree);
 * </pre>
 *
 * <p>
 * See {@code org.antlr.v4.test.TestXPath} for descriptions. In short, this
 * allows operators:</p>
 *
 * <dl>
 * <dt>/</dt> <dd>root</dd>
 * <dt>//</dt> <dd>anywhere</dd>
 * <dt>!</dt> <dd>invert; this must appear directly after root or anywhere
 * operator</dd>
 * </dl>
 *
 * <p>
 * and path elements:</p>
 *
 * <dl>
 * <dt>ID</dt> <dd>token name</dd>
 * <dt>'string'</dt> <dd>any string literal token from the grammar</dd>
 * <dt>expr</dt> <dd>rule name</dd>
 * <dt>*</dt> <dd>wildcard matching any node</dd>
 * </dl>
 *
 * <p>
 * Whitespace is not allowed.</p>
 */
class XPath
{

    /**
     * @uml
     * word not operator/separator
     */
    enum string WILDCARD = "*";

    /**
     * @uml
     * word for invert operator
     */
    enum string NOT = "!";

    protected string path;

    protected XPathElement[] elements;

    protected Parser parser;

    public this(Parser parser, string path)
    {
        this.parser = parser;
        this.path = path;
        elements = split(path);
        debug writefln("%s", elements);
    }

    /**
     * @uml
     * TODO: check for invalid token/rule names, bad syntax
     */
    public XPathElement[] split(string path)
    {
        ANTLRInputStream ins;
        try {
            ins = new ANTLRInputStream(readText(path));
        }
        catch (Exception ioe) {
            throw new IllegalArgumentException("Could not read path: " ~ path, ioe);
        }
        XPathLexer lexer = new XPathLexer(ins);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new XPathLexerErrorListener());
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        try {
            tokenStream.fill();
        }
        catch (LexerNoViableAltException e) {
            int pos = lexer.getCharPositionInLine();
            string msg = format("Invalid tokens or characters at index %1$s in path '%2$s'", pos, path);
            throw new IllegalArgumentException(msg);
        }

        Token[] tokens = tokenStream.getTokens();
        //		System.out.println("path="+path+"=>"+tokens);
        XPathElement[] elements;
        int n = to!int(tokens.length);
        int i=0;
    loop:
        while ( i<n ) {
            Token el = tokens[i];
            Token next = null;
            switch ( el.getType() ) {
            case XPathLexer.ROOT :
            case XPathLexer.ANYWHERE :
                bool anywhere = el.getType() == XPathLexer.ANYWHERE;
                i++;
                next = tokens[i];
                bool invert = next.getType() == XPathLexer.BANG;
                if ( invert ) {
                    i++;
                    next = tokens[i];
                }
                XPathElement pathElement = getXPathElement(next, anywhere);
                pathElement.invert = invert;
                elements ~= pathElement;
                i++;
                break;

            case XPathLexer.TOKEN_REF :
            case XPathLexer.RULE_REF :
            case XPathLexer.WILDCARD :
                elements ~= getXPathElement(el, false);
                i++;
                break;

            case TokenConstantDefinition.EOF :
                break loop;

            default :
                throw new IllegalArgumentException("Unknowth path element " ~ to!string(el));
            }
        }
        XPathElement[] nullArray;
        return elements = nullArray;
    }

    /**
     * @uml
     * Convert word like {@code *} or {@code ID} or {@code expr} to a path
     * element. {@code anywhere} is {@code true} if {@code //} precedes the
     * word.
     */
    public XPathElement getXPathElement(Token wordToken, bool anywhere)
    {
	if (wordToken.getType() == TokenConstantDefinition.EOF) {
            throw new IllegalArgumentException("Missing path element at end of path");
        }
        string word = to!string(wordToken.getText);
        int ttype = parser.getTokenType(word);
        int ruleIndex = parser.getRuleIndex(word);
        switch (wordToken.getType()) {
        case XPathLexer.WILDCARD :
            return anywhere ?
                new XPathWildcardAnywhereElement() :
            new XPathWildcardElement();
        case XPathLexer.TOKEN_REF :
        case XPathLexer.STRING :
            if (ttype == TokenConstantDefinition.INVALID_TYPE ) {
                throw new IllegalArgumentException(word~
                                                   " at index "~
                                                   to!string(wordToken.startIndex) ~
                                                   " isn't a valid token name");
            }
            return anywhere ?
                new XPathTokenAnywhereElement(word, ttype) :
                new XPathTokenElement(word, ttype);
        default :
            if ( ruleIndex==-1 ) {
                throw new IllegalArgumentException(word ~
                                                   " at index "~
                                                   to!string(wordToken.startIndex) ~
                                                   " isn't a valid rule name");
            }
            return anywhere ?
                new XPathRuleAnywhereElement(word, ruleIndex) :
                new XPathRuleElement(word, ruleIndex);
        }
    }

    public static ParseTree[] findAll(ParseTree tree, string xpath, Parser parser)
    {
        XPath p = new XPath(parser, xpath);
        return p.evaluate(tree);
    }

    /**
     * @uml
     * Return a list of all nodes starting at {@code t} as root that satisfy the
     * path. The root {@code /} is relative to the node passed to
     * {@link #evaluate}.
     */
    public ParseTree[] evaluate(ParseTree t)
    {
	ParserRuleContext dummyRoot = new ParserRuleContext();
        ParseTree[1] pt;
        pt[0] = t;
        dummyRoot.children = pt; // don't set t's parent.

        ParseTree[] work = [dummyRoot];

        int i = 0;
        while (i < elements.length) {
            DList!ParseTree next;
            foreach (ParseTree node; work) {
                if (node.getChildCount() > 0) {
                    // only try to match next element if it has children
                    // e.g., //func/*/stat might have a token node for which
                    // we can't go looking for stat nodes.
                    ParseTree[] matching = elements[i].evaluate(node);
                    next.insert(matching);
                }
            }
            i++;
            work ~= array(next[]);
        }
        return work;
    }

}
