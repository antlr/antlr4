/*
 * Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.Predicate;
import org.antlr.v4.runtime.misc.Utils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** A set of utility routines useful for all kinds of ANTLR trees. */
public class Trees {
	/** A reflection cache tracking constructor methods for various tree nodes
	 *  so we can clone notes. If this starts to get too big,
	 *  call {@see resetCtorCache}.
	 */
	protected static Map<Class<? extends ParserRuleContext>, Constructor<? extends ParserRuleContext>> ctorCache;

	/** Print out a whole tree in LISP form. {@link #getNodeText} is used on the
	 *  node payloads to get the text for the nodes.  Detect
	 *  parse trees and extract data appropriately.
	 */
	public static String toStringTree(Tree t) {
		return toStringTree(t, (List<String>)null);
	}

	/** Print out a whole tree in LISP form. {@link #getNodeText} is used on the
	 *  node payloads to get the text for the nodes.  Detect
	 *  parse trees and extract data appropriately.
	 */
	public static String toStringTree(Tree t, Parser recog) {
		String[] ruleNames = recog != null ? recog.getRuleNames() : null;
		List<String> ruleNamesList = ruleNames != null ? Arrays.asList(ruleNames) : null;
		return toStringTree(t, ruleNamesList);
	}

	/** Print out a whole tree in LISP form. {@link #getNodeText} is used on the
	 *  node payloads to get the text for the nodes.
	 */
	public static String toStringTree(final Tree t, final List<String> ruleNames) {
		String s = Utils.escapeWhitespace(getNodeText(t, ruleNames), false);
		if ( t.getChildCount()==0 ) return s;
		StringBuilder buf = new StringBuilder();
		buf.append("(");
		s = Utils.escapeWhitespace(getNodeText(t, ruleNames), false);
		buf.append(s);
		buf.append(' ');
		for (int i = 0; i<t.getChildCount(); i++) {
			if ( i>0 ) buf.append(' ');
			buf.append(toStringTree(t.getChild(i), ruleNames));
		}
		buf.append(")");
		return buf.toString();
	}

	public static String getNodeText(Tree t, Parser recog) {
		String[] ruleNames = recog != null ? recog.getRuleNames() : null;
		List<String> ruleNamesList = ruleNames != null ? Arrays.asList(ruleNames) : null;
		return getNodeText(t, ruleNamesList);
	}

	public static String getNodeText(Tree t, List<String> ruleNames) {
		if ( ruleNames!=null ) {
			if ( t instanceof RuleContext ) {
				int ruleIndex = ((RuleContext)t).getRuleContext().getRuleIndex();
				String ruleName = ruleNames.get(ruleIndex);
				int altNumber = ((RuleContext) t).getAltNumber();
				if ( altNumber!=ATN.INVALID_ALT_NUMBER ) {
					return ruleName+":"+altNumber;
				}
				return ruleName;
			}
			else if ( t instanceof ErrorNode) {
				return t.toString();
			}
			else if ( t instanceof TerminalNode) {
				Token symbol = ((TerminalNode)t).getSymbol();
				if (symbol != null) {
					String s = symbol.getText();
					return s;
				}
			}
		}
		// no recog for rule names
		Object payload = t.getPayload();
		if ( payload instanceof Token ) {
			return ((Token)payload).getText();
		}
		return t.getPayload().toString();
	}

	/** Return ordered list of all children of this node */
	public static List<Tree> getChildren(Tree t) {
		List<Tree> kids = new ArrayList<Tree>();
		for (int i=0; i<t.getChildCount(); i++) {
			kids.add(t.getChild(i));
		}
		return kids;
	}

	/** Return a list of all ancestors of this node.  The first node of
	 *  list is the root and the last is the parent of this node.
	 *
	 *  @since 4.5.1
	 */
	public static List<? extends Tree> getAncestors(Tree t) {
		if ( t.getParent()==null ) return Collections.emptyList();
		List<Tree> ancestors = new ArrayList<Tree>();
		t = t.getParent();
		while ( t!=null ) {
			ancestors.add(0, t); // insert at start
			t = t.getParent();
		}
		return ancestors;
	}

	/** Return true if t is u's parent or a node on path to root from u.
	 *  Use == not equals().
	 *
	 *  @since 4.5.1
	 */
	public static boolean isAncestorOf(Tree t, Tree u) {
		if ( t==null || u==null || t.getParent()==null ) return false;
		Tree p = u.getParent();
		while ( p!=null ) {
			if ( t==p ) return true;
			p = p.getParent();
		}
		return false;
	}

	public static Collection<ParseTree> findAllTokenNodes(ParseTree t, int ttype) {
		return findAllNodes(t, ttype, true);
	}

	public static Collection<ParseTree> findAllRuleNodes(ParseTree t, int ruleIndex) {
		return findAllNodes(t, ruleIndex, false);
	}

	public static List<ParseTree> findAllNodes(ParseTree t, int index, boolean findTokens) {
		List<ParseTree> nodes = new ArrayList<ParseTree>();
		_findAllNodes(t, index, findTokens, nodes);
		return nodes;
	}

	public static void _findAllNodes(ParseTree t, int index, boolean findTokens,
									 List<? super ParseTree> nodes)
	{
		// check this node (the root) first
		if ( findTokens && t instanceof TerminalNode ) {
			TerminalNode tnode = (TerminalNode)t;
			if ( tnode.getSymbol().getType()==index ) nodes.add(t);
		}
		else if ( !findTokens && t instanceof ParserRuleContext ) {
			ParserRuleContext ctx = (ParserRuleContext)t;
			if ( ctx.getRuleIndex() == index ) nodes.add(t);
		}
		// check children
		for (int i = 0; i < t.getChildCount(); i++){
			_findAllNodes(t.getChild(i), index, findTokens, nodes);
		}
	}

	/** Get all descendents; includes t itself.
	 *
	 * @since 4.5.1
 	 */
	public static List<ParseTree> getDescendants(ParseTree t) {
		List<ParseTree> nodes = new ArrayList<ParseTree>();
		nodes.add(t);

		int n = t.getChildCount();
		for (int i = 0 ; i < n ; i++){
			nodes.addAll(getDescendants(t.getChild(i)));
		}
		return nodes;
	}

	/** @deprecated */
	public static List<ParseTree> descendants(ParseTree t) {
		return getDescendants(t);
	}

	/** Return a new TerminalNode with a copy of all relevant fields
	 *  from t and with null parent.
	 *
	 *  @since 4.6.1
	 */
	public static TerminalNodeImpl shallowCopy(TerminalNode t) {
		return new TerminalNodeImpl(t.getSymbol());
	}

	/** Create a shallow copy of node t; the copy has same type as t. Copy
	 *  all relevant fields to make a proper clone of t except
	 *  the children list. The only caveat is that there are nodes from t
	 *  are in fact copied into the clone.
	 *
	 *  t is not altered.
	 *
	 *  Because this uses reflection, it might be slower than using
	 *  "new X()" directly.  It uses {@see ctorCache} to speed things up a lot.
	 *
	 *  @since 4.6.1
	 */
	public static ParserRuleContext shallowCopy(ParserRuleContext t) {
		try {
			Class<? extends ParserRuleContext> cl = t.getClass();
			Class<?> sup = cl.getSuperclass();
			Class<?> supSup = sup.getSuperclass();
			if ( ctorCache==null ) { // in a multithreaded environment, some thread will win to set ctorCache
				ctorCache =	new ConcurrentHashMap<>();
			}
			boolean
				isAltLabelNode = sup!=ParserRuleContext.class && // usual case
				supSup!=ParserRuleContext.class;                 // if we use contextSuperClass option we are 2 below ParserRuleContext
			if ( isAltLabelNode ) {
				// AltLabel: nodes public UnarySuffixContext(StuffContext ctx) { copyFrom(ctx); }
				Constructor<? extends ParserRuleContext> ctor = ctorCache.get(cl);
				if ( ctor==null ) {
					ctor = cl.getConstructor(sup); // no need for explicit copyFrom
				}
				return ctor.newInstance(t);
			}
			else {
				// RuleNodes: public StatContext(ParserRuleContext parent, int invokingState) {
				Constructor<? extends ParserRuleContext> ctor = ctorCache.get(cl);
				if ( ctor==null ) {
					ctor = cl.getConstructor(ParserRuleContext.class, int.class);
					ctorCache.put(cl, ctor);
				}
				ParserRuleContext copy = ctor.newInstance(t.getParent(), t.invokingState);
				copy.copyFrom(t); // make sure we copy all necessary fields (and possibly error node children)
				return copy;
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** Make a shallow copy of a generic parse tree node t
	 *
	 *  @since 4.6.1
	 */
	public static ParseTree shallowCopy(ParseTree t) {
		if ( t instanceof TerminalNode ) return shallowCopy((TerminalNode)t);
		return shallowCopy((ParserRuleContext)t);
	}

	/** Make a complete copy of a parse tree rooted at t.
	 *  The original tree, t, is not altered.
	 *
	 *  @since 4.6.1
	 */
	public static ParseTree deepCopy(ParseTree t) {
		if ( t instanceof TerminalNodeImpl ) { // must assume an implementation to alter
			return shallowCopy(t);
		}
		ParserRuleContext ctx = (ParserRuleContext)t;
		ParserRuleContext copy = shallowCopy(ctx);
		int n = ctx.getChildCount();
		for (int i = 0; i<n; i++) {
			ParseTree child = ctx.getChild(i);
			if ( child instanceof TerminalNodeImpl ) { // must assume an implementation to alter
				TerminalNodeImpl cc = shallowCopy((TerminalNode)child);
				cc.parent = copy;
				copy.children.set(i, cc);
			}
			else {
				ParserRuleContext cc = shallowCopy((ParserRuleContext)child);
				cc.parent = copy;
				copy.children.set(i, cc);
			}
		}
		return copy;
	}

	/** Just in case the constructor cache gets too big, you can clear
	 *  with this method.
	 *
	 *  @since 4.6.1
	 */
	public static void resetCtorCache() { ctorCache.clear(); }

	/** Find smallest subtree of t enclosing range startTokenIndex..stopTokenIndex
	 *  inclusively using postorder traversal.  Recursive depth-first-search.
	 *
	 *  @since 4.5.1
	 */
	public static ParserRuleContext getRootOfSubtreeEnclosingRegion(ParseTree t,
																	int startTokenIndex, // inclusive
																	int stopTokenIndex)  // inclusive
	{
		int n = t.getChildCount();
		for (int i = 0; i<n; i++) {
			ParseTree child = t.getChild(i);
			ParserRuleContext r = getRootOfSubtreeEnclosingRegion(child, startTokenIndex, stopTokenIndex);
			if ( r!=null ) return r;
		}
		if ( t instanceof ParserRuleContext ) {
			ParserRuleContext r = (ParserRuleContext) t;
			if ( startTokenIndex>=r.getStart().getTokenIndex() && // is range fully contained in t?
				 (r.getStop()==null || stopTokenIndex<=r.getStop().getTokenIndex()) )
			{
				// note: r.getStop()==null likely implies that we bailed out of parser and there's nothing to the right
				return r;
			}
		}
		return null;
	}

	/** Replace any subtree siblings of root that are completely to left
	 *  or right of lookahead range with a CommonToken(Token.INVALID_TYPE,"...")
	 *  node. The source interval for t is not altered to suit smaller range!
	 *
	 *  WARNING: destructive to t.
	 *
	 *  @since 4.5.1
	 */
	public static void stripChildrenOutOfRange(ParserRuleContext t,
											   ParserRuleContext root,
											   int startIndex,
											   int stopIndex)
	{
		if ( t==null ) return;
		for (int i = 0; i < t.getChildCount(); i++) {
			ParseTree child = t.getChild(i);
			Interval range = child.getSourceInterval();
			if ( child instanceof ParserRuleContext && (range.b < startIndex || range.a > stopIndex) ) {
				if ( isAncestorOf(child, root) ) { // replace only if subtree doesn't have displayed root
					CommonToken abbrev = new CommonToken(Token.INVALID_TYPE, "...");
					t.children.set(i, new TerminalNodeImpl(abbrev));
				}
			}
		}
	}

	/** Return first node satisfying the pred
	 *
 	 *  @since 4.5.1
	 */
	public static Tree findNodeSuchThat(Tree t, Predicate<Tree> pred) {
		if ( pred.test(t) ) return t;

		int n = t.getChildCount();
		for (int i = 0 ; i < n ; i++){
			Tree u = findNodeSuchThat(t.getChild(i), pred);
			if ( u!=null ) return u;
		}
		return null;
	}

	private Trees() {
	}
}
