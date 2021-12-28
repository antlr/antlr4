/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import 'dart:core';

import '../../atn/atn.dart';
import '../../parser.dart';
import '../../parser_rule_context.dart';
import '../../rule_context.dart';
import '../../token.dart';
import '../../util/utils.dart';
import 'tree.dart';

/// A set of utility routines useful for all kinds of ANTLR trees. */
class Trees {
  /// Print out a whole tree in LISP form. {@link #getNodeText} is used on the
  ///  node payloads to get the text for the nodes.  Detect
  ///  parse trees and extract data appropriately.
  static String toStringTree(
    Tree? t, {
    Parser? recog,
    List<String>? ruleNames,
  }) {
    if (t == null) return 'null';
    ruleNames ??= recog?.ruleNames;
    var s = escapeWhitespace(getNodeText(t, ruleNames: ruleNames), false);
    if (t.childCount == 0) return s;
    final buf = StringBuffer();
    buf.write('(');
    s = escapeWhitespace(getNodeText(t, ruleNames: ruleNames), false);
    buf.write(s);
    buf.write(' ');
    for (var i = 0; i < t.childCount; i++) {
      if (i > 0) buf.write(' ');
      buf.write(toStringTree(t.getChild(i), ruleNames: ruleNames));
    }
    buf.write(')');
    return buf.toString();
  }

  static String getNodeText(
    Tree t, {
    Parser? recog,
    List<String>? ruleNames,
  }) {
    ruleNames ??= recog?.ruleNames;
    if (ruleNames != null) {
      if (t is RuleContext) {
        final ruleIndex = t.ruleContext.ruleIndex;
        final ruleName = ruleNames[ruleIndex];
        final altNumber = t.altNumber;
        if (altNumber != ATN.INVALID_ALT_NUMBER) {
          return ruleName + ':$altNumber';
        }
        return ruleName;
      } else if (t is ErrorNode) {
        return t.toString();
      } else if (t is TerminalNode) {
        final symbol = (t).symbol;
        final s = symbol.text;
        return s ?? '';
      }
    }
    // no recog for rule names
    Object payload = t.payload;
    if (payload is Token) {
      return payload.text ?? '';
    }
    return t.payload.toString();
  }

  /// Return ordered list of all children of this node */
  static List<Tree> getChildren(Tree t) {
    final kids = <Tree>[];
    for (var i = 0; i < t.childCount; i++) {
      kids.add(t.getChild(i)!);
    }
    return kids;
  }

  /// Return a list of all ancestors of this node.  The first node of
  ///  list is the root and the last is the parent of this node.
  ///
  ///  @since 4.5.1
  static List<Tree> getAncestors(Tree t) {
    if (t.parent == null) return [];
    var walker = t.parent;
    final ancestors = <Tree>[];
    while (walker != null) {
      ancestors.insert(0, walker); // insert at start
      walker = walker.parent;
    }
    return ancestors;
  }

  /// Return true if t is u's parent or a node on path to root from u.
  ///  Use == not equals().
  ///
  ///  @since 4.5.1
  static bool isAncestorOf(Tree? t, Tree? u) {
    if (t == null || u == null || t.parent == null) return false;
    var p = u.parent;
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
    final nodes = <ParseTree>[];
    _findAllNodes(t, index, findTokens, nodes);
    return nodes;
  }

  static void _findAllNodes(
    ParseTree t,
    int index,
    bool findTokens,
    List<ParseTree> nodes,
  ) {
    // check this node (the root) first
    if (findTokens && t is TerminalNode) {
      final tnode = t;
      if (tnode.symbol.type == index) nodes.add(t);
    } else if (!findTokens && t is ParserRuleContext) {
      final ctx = t;
      if (ctx.ruleIndex == index) nodes.add(t);
    }
    // check children
    for (var i = 0; i < t.childCount; i++) {
      _findAllNodes(t.getChild(i)!, index, findTokens, nodes);
    }
  }

  /// Get all descendents; includes t itself.
  ///
  /// @since 4.5.1
  static List<ParseTree> getDescendants(ParseTree t) {
    final nodes = <ParseTree>[];
    nodes.add(t);

    final n = t.childCount;
    for (var i = 0; i < n; i++) {
      nodes.addAll(getDescendants(t.getChild(i)!));
    }
    return nodes;
  }

  /// @deprecated */
  static List<ParseTree> descendants(ParseTree t) {
    return getDescendants(t);
  }

  /// Find smallest subtree of t enclosing range startTokenIndex..stopTokenIndex
  ///  inclusively using postorder traversal.  Recursive depth-first-search.
  ///
  ///  @since 4.5.1
  static ParserRuleContext? getRootOfSubtreeEnclosingRegion(
    ParseTree t,
    int startTokenIndex, // inclusive
    int stopTokenIndex, // inclusive
  ) {
    final n = t.childCount;
    for (var i = 0; i < n; i++) {
      final child = t.getChild(i)!;
      final r = getRootOfSubtreeEnclosingRegion(
        child,
        startTokenIndex,
        stopTokenIndex,
      );
      if (r != null) return r;
    }
    if (t is ParserRuleContext) {
      // is range fully contained in t?
      final start = t.start != null && startTokenIndex >= t.start!.tokenIndex;
      final end = t.stop == null || stopTokenIndex <= t.stop!.tokenIndex;
      if (start && end) {
        return t;
      }
    }
    return null;
  }

  /// Replace any subtree siblings of root that are completely to left
  ///  or right of lookahead range with a CommonToken(Token.INVALID_TYPE,"...")
  ///  node. The source interval for t is not altered to suit smaller range!
  ///
  ///  WARNING: destructive to t.
  ///
  ///  @since 4.5.1
  static void stripChildrenOutOfRange(
    ParserRuleContext? t,
    ParserRuleContext root,
    int startIndex,
    int stopIndex,
  ) {
    if (t == null) return;
    for (var i = 0; i < t.childCount; i++) {
      final child = t.getChild(i)!;
      final range = child.sourceInterval;
      if (child is ParserRuleContext &&
          (range.b < startIndex || range.a > stopIndex)) {
        if (isAncestorOf(child, root)) {
          // replace only if subtree doesn't have displayed root
          final abbrev = CommonToken(Token.INVALID_TYPE, text: '...');
          t.children![i] = TerminalNodeImpl(abbrev);
        }
      }
    }
  }

  /// Return first node satisfying the pred
  ///
  ///  @since 4.5.1
  static Tree? findNodeSuchThat(Tree? t, bool Function(Tree?) pred) {
    if (pred(t)) return t;

    if (t == null) return null;

    final n = t.childCount;
    for (var i = 0; i < n; i++) {
      final u = findNodeSuchThat(t.getChild(i), pred);
      if (u != null) return u;
    }
    return null;
  }
}
