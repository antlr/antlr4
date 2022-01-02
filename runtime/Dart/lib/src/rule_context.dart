/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import 'atn/atn.dart';
import 'interval_set.dart';
import 'parser.dart';
import 'parser_rule_context.dart';
import 'recognizer.dart';
import 'tree/tree.dart';

/// A rule context is a record of a single rule invocation.
///
///  We form a stack of these context objects using the parent
///  pointer. A parent pointer of null indicates that the current
///  context is the bottom of the stack. The ParserRuleContext subclass
///  as a children list so that we can turn this data structure into a
///  tree.
///
///  The root node always has a null pointer and invokingState of -1.
///
///  Upon entry to parsing, the first invoked rule function creates a
///  context object (a subclass specialized for that rule such as
///  SContext) and makes it the root of a parse tree, recorded by field
///  Parser._ctx.
///
///  public final SContext s() throws RecognitionException {
///      SContext _localctx = new SContext(_ctx, getState()); <-- create new node
///      enterRule(_localctx, 0, RULE_s);                     <-- push it
///      ...
///      exitRule();                                          <-- pop back to _localctx
///      return _localctx;
///  }
///
///  A subsequent rule invocation of r from the start rule s pushes a
///  new context object for r whose parent points at s and use invoking
///  state is the state with r emanating as edge label.
///
///  The invokingState fields from a context object to the root
///  together form a stack of rule indication states where the root
///  (bottom of the stack) has a -1 sentinel value. If we invoke start
///  symbol s then call r1, which calls r2, the  would look like
///  this:
///
///     SContext[-1]   <- root node (bottom of the stack)
///     R1Context[p]   <- p in rule s called r1
///     R2Context[q]   <- q in rule r1 called r2
///
///  So the top of the stack, _ctx, represents a call to the current
///  rule and it holds the return address from another rule that invoke
///  to this rule. To invoke a rule, we must always have a current context.
///
///  The parent contexts are useful for computing lookahead sets and
///  getting error information.
///
///  These objects are used during parsing and prediction.
///  For the special case of parsers, we use the subclass
///  ParserRuleContext.
///
///  @see ParserRuleContext
abstract class RuleContext extends RuleNode {
  /// What context invoked this rule?
  RuleContext? _parent;

  /// What state invoked the rule associated with this context?
  /// The "return address" is the followState of invokingState
  /// If parent is null, this should be -1.
  int invokingState;

  RuleContext({RuleContext? parent, int? invokingState})
      : _parent=parent, invokingState = invokingState ?? -1;

  int depth() {
    var n = 0;
    RuleContext? p = this;
    while (p != null) {
      p = p.parent;
      n++;
    }
    return n;
  }

  @override
  // Work around for https://github.com/antlr/antlr4/issues/3248
  // ignore: unnecessary_getters_setters
  RuleContext? get parent => _parent;

  @override
  // Work around for https://github.com/antlr/antlr4/issues/3248
  // ignore: unnecessary_getters_setters
  set parent(RuleContext? parent) {
    _parent = parent;
  }

  /// A context is empty if there is no invoking state; meaning nobody call
  /// current context.
  bool get isEmpty => invokingState == -1;

  /// satisfy the ParseTree / SyntaxTree interface
  @override
  Interval get sourceInterval => Interval.INVALID;

  @override
  RuleContext get ruleContext => this;

  @override
  RuleContext get payload => this;

  /// Return the combined text of all child nodes. This method only considers
  ///  tokens which have been added to the parse tree.
  ///  <p>
  ///  Since tokens on hidden channels (e.g. whitespace or comments) are not
  ///  added to the parse trees, they will not appear in the output of this
  ///  method.
  @override
  String get text {
    if (childCount == 0) {
      return '';
    }

    final builder = StringBuffer();
    for (var i = 0; i < childCount; i++) {
      builder.write(getChild(i)!.text);
    }

    return builder.toString();
  }

  int get ruleIndex => -1;

  /// For rule associated with this parse tree internal node, return
  /// the outer alternative number used to match the input. Default
  /// implementation does not compute nor store this alt num. Create
  /// a subclass of ParserRuleContext with backing field and set
  /// option contextSuperClass.
  /// to set it.
  int get altNumber => ATN.INVALID_ALT_NUMBER;

  /// Set the outer alternative number for this context node. Default
  /// implementation does nothing to avoid backing field overhead for
  /// trees that don't need it.  Create
  /// a subclass of ParserRuleContext with backing field and set
  /// option contextSuperClass.
  set altNumber(int altNumber) {}

  @override
  ParseTree? getChild<T>(int i) {
    return null;
  }

  @override
  int get childCount => 0;

  @override
  T? accept<T>(ParseTreeVisitor<T> visitor) {
    return visitor.visitChildren(this);
  }

  /// Print out a whole tree, not just a node, in LISP format
  /// (root child1 .. childN). Print just a node if this is a leaf.
  ///
  @override
  String toStringTree({List<String>? ruleNames, Parser? parser}) {
    return Trees.toStringTree(this, ruleNames: ruleNames, recog: parser);
  }

  @override
  String toString({
    List<String>? ruleNames,
    Recognizer? recog,
    RuleContext? stop,
  }) {
    ruleNames = ruleNames ?? recog?.ruleNames;
    final buf = StringBuffer();
    RuleContext? p = this;
    buf.write('[');
    while (p != null && p != stop) {
      if (ruleNames == null) {
        if (!p.isEmpty) {
          buf.write(p.invokingState);
        }
      } else {
        final ruleIndex = p.ruleIndex;
        final ruleName = ruleIndex >= 0 && ruleIndex < ruleNames.length
            ? ruleNames[ruleIndex]
            : ruleIndex.toString();
        buf.write(ruleName);
      }

      if (p.parent != null && (ruleNames != null || !p.parent!.isEmpty)) {
        buf.write(' ');
      }

      p = p.parent;
    }

    buf.write(']');
    return buf.toString();
  }

  static final EMPTY = ParserRuleContext();
}
