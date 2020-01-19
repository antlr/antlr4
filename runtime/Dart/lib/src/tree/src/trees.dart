/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import 'dart:core';

import '../../atn/atn.dart';
import '../../interval_set.dart';
import '../../parser.dart';
import '../../parser_rule_context.dart';
import '../../rule_context.dart';
import '../../token.dart';
import '../../util/utils.dart';
import 'tree.dart';

/** A set of utility routines useful for all kinds of ANTLR trees. */
class Trees {
  /** Print out a whole tree in LISP form. {@link #getNodeText} is used on the
   *  node payloads to get the text for the nodes.  Detect
   *  parse trees and extract data appropriately.
   */
  static String toStringTree(Tree t, {Parser recog, List<String> ruleNames}) {
    if (ruleNames == null) {
      ruleNames = recog?.ruleNames;
    }
    String s = escapeWhitespace(getNodeText(t, ruleNames: ruleNames), false);
    if (t.childCount == 0) return s;
    StringBuffer buf = new StringBuffer();
    buf.write("(");
    s = escapeWhitespace(getNodeText(t, ruleNames: ruleNames), false);
    buf.write(s);
    buf.write(' ');
    for (int i = 0; i < t.childCount; i++) {
      if (i > 0) buf.write(' ');
      buf.write(toStringTree(t.getChild(i), ruleNames: ruleNames));
    }
    buf.write(")");
    return buf.toString();
  }

  static String getNodeText(Tree t, {Parser recog, List<String> ruleNames}) {
    if (ruleNames == null) {
      ruleNames = recog?.ruleNames;
    }
    if (ruleNames != null) {
      if (t is RuleContext) {
        int ruleIndex = t.ruleContext.ruleIndex;
        String ruleName = ruleNames[ruleIndex];
        int altNumber = t.altNumber;
        if (altNumber != ATN.INVALID_ALT_NUMBER) {
          return ruleName + ":$altNumber";
        }
        return ruleName;
      } else if (t is ErrorNode) {
        return t.toString();
      } else if (t is TerminalNode) {
        Token symbol = (t).symbol;
        if (symbol != null) {
          String s = symbol.text;
          return s;
        }
      }
    }
    // no recog for rule names
    Object payload = t.payload;
    if (payload is Token) {
      return payload.text;
    }
    return t.payload.toString();
  }

  /** Return ordered list of all children of this node */
  static List<Tree> getChildren(Tree t) {
    List<Tree> kids = [];
    for (int i = 0; i < t.childCount; i++) {
      kids.add(t.getChild(i));
    }
    return kids;
  }

  /** Return a list of all ancestors of this node.  The first node of
   *  list is the root and the last is the parent of this node.
   *
   *  @since 4.5.1
   */
  static List<Tree> getAncestors(Tree t) {
    if (t.parent == null) return [];
    List<Tree> ancestors = [];
    t = t.parent;
    while (t != null) {
      ancestors.insert(0, t); // insert at start
      t = t.parent;
    }
    return ancestors;
  }

  /** Return true if t is u's parent or a node on path to root from u.
   *  Use == not equals().
   *
   *  @since 4.5.1
   */
  static bool isAncestorOf(Tree t, Tree u) {
    if (t == null || u == null || t.parent == null) return false;
    Tree p = u.parent;
    while (p != null) {
      if (t == p) return true;
      p = p.parent;
    }
    return false;
  }

  static List<ParseTree> findAllTokenNodes(ParseTree t, int ttype) {
    return findAllNodes(t, ttype, true);
  }

  static List<ParseTree> findAllRuleNodes(ParseTree t, int ruleIndex) {
    return findAllNodes(t, ruleIndex, false);
  }

  static List<ParseTree> findAllNodes(ParseTree t, int index, bool findTokens) {
    List<ParseTree> nodes = [];
    _findAllNodes(t, index, findTokens, nodes);
    return nodes;
  }

  static void _findAllNodes(
      ParseTree t, int index, bool findTokens, List<ParseTree> nodes) {
    // check this node (the root) first
    if (findTokens && t is TerminalNode) {
      TerminalNode tnode = t;
      if (tnode.symbol.type == index) nodes.add(t);
    } else if (!findTokens && t is ParserRuleContext) {
      ParserRuleContext ctx = t;
      if (ctx.ruleIndex == index) nodes.add(t);
    }
    // check children
    for (int i = 0; i < t.childCount; i++) {
      _findAllNodes(t.getChild(i), index, findTokens, nodes);
    }
  }

  /** Get all descendents; includes t itself.
   *
   * @since 4.5.1
   */
  static List<ParseTree> getDescendants(ParseTree t) {
    List<ParseTree> nodes = [];
    nodes.add(t);

    int n = t.childCount;
    for (int i = 0; i < n; i++) {
      nodes.addAll(getDescendants(t.getChild(i)));
    }
    return nodes;
  }

  /** @deprecated */
  static List<ParseTree> descendants(ParseTree t) {
    return getDescendants(t);
  }

  /** Find smallest subtree of t enclosing range startTokenIndex..stopTokenIndex
   *  inclusively using postorder traversal.  Recursive depth-first-search.
   *
   *  @since 4.5.1
   */
  static ParserRuleContext getRootOfSubtreeEnclosingRegion(
      ParseTree t,
      int startTokenIndex, // inclusive
      int stopTokenIndex) // inclusive
  {
    int n = t.childCount;
    for (int i = 0; i < n; i++) {
      ParseTree child = t.getChild(i);
      ParserRuleContext r = getRootOfSubtreeEnclosingRegion(
          child, startTokenIndex, stopTokenIndex);
      if (r != null) return r;
    }
    if (t is ParserRuleContext) {
      ParserRuleContext r = t;
      if (startTokenIndex >=
              r.start.tokenIndex && // is range fully contained in t?
          (r.stop == null || stopTokenIndex <= r.stop.tokenIndex)) {
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
  static void stripChildrenOutOfRange(ParserRuleContext t,
      ParserRuleContext root, int startIndex, int stopIndex) {
    if (t == null) return;
    for (int i = 0; i < t.childCount; i++) {
      ParseTree child = t.getChild(i);
      Interval range = child.sourceInterval;
      if (child is ParserRuleContext &&
          (range.b < startIndex || range.a > stopIndex)) {
        if (isAncestorOf(child, root)) {
          // replace only if subtree doesn't have displayed root
          CommonToken abbrev = new CommonToken(Token.INVALID_TYPE, text: "...");
          t.children[i] = new TerminalNodeImpl(abbrev);
        }
      }
    }
  }

  /** Return first node satisfying the pred
   *
   *  @since 4.5.1
   */
  static Tree findNodeSuchThat(Tree t, Predicate<Tree> pred) {
    if (pred.test(t)) return t;

    if (t == null) return null;

    int n = t.childCount;
    for (int i = 0; i < n; i++) {
      Tree u = findNodeSuchThat(t.getChild(i), pred);
      if (u != null) return u;
    }
    return null;
  }
}

abstract class Predicate<T> {
  bool test(T t);
}
