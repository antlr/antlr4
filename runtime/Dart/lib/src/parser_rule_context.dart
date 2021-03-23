/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import 'error/error.dart';
import 'interval_set.dart';
import 'parser.dart';
import 'rule_context.dart';
import 'token.dart';
import 'tree/tree.dart';

/// A rule invocation record for parsing.
///
///  Contains all of the information about the current rule not stored in the
///  RuleContext. It handles parse tree children list, Any ATN state
///  tracing, and the default values available for rule invocations:
///  start, stop, rule index, current alt number.
///
///  Subclasses made for each rule and grammar track the parameters,
///  return values, locals, and labels specific to that rule. These
///  are the objects that are returned from rules.
///
///  Note text is not an actual field of a rule return value; it is computed
///  from start and stop using the input stream's toString() method.  I
///  could add a ctor to this so that we can pass in and store the input
///  stream, but I'm not sure we want to do that.  It would seem to be undefined
///  to get the .text property anyway if the rule matches tokens from multiple
///  input streams.
///
///  I do not use getters for fields of objects that are used simply to
///  group values such as this aggregate.  The getters/setters are there to
///  satisfy the superclass interface.
class ParserRuleContext extends RuleContext {
  /// If we are debugging or building a parse tree for a visitor,
  ///  we need to track all of the tokens and rule invocations associated
  ///  with this rule's context. This is empty for parsing w/o tree constr.
  ///  operation because we don't the need to track the details about
  ///  how we parse this rule.
  List<ParseTree>? children;

  /// Get the initial/final token in this context.
  /// Note that the range from start to stop is inclusive, so for rules that do not consume anything
  /// (for example, zero length or error productions) this token may exceed stop.
  Token? start, stop;

  /// The exception that forced this rule to return. If the rule successfully
  /// completed, this is null.
  RecognitionException? exception;

  ParserRuleContext([RuleContext? parent, int? invokingStateNumber])
      : super(parent: parent, invokingState: invokingStateNumber);

  /// COPY a ctx (I'm deliberately not using copy constructor) to avoid
  ///  confusion with creating node with parent. Does not copy children
  ///  (except error leaves).
  ///
  ///  This is used in the generated parser code to flip a generic XContext
  ///  node for rule X to a YContext for alt label Y. In that sense, it is
  ///  not really a generic copy function.
  ///
  ///  If we do an error sync() at start of a rule, we might add error nodes
  ///  to the generic XContext so this function must copy those nodes to
  ///  the YContext as well else they are lost!
  void copyFrom(ParserRuleContext ctx) {
    parent = ctx.parent;
    invokingState = ctx.invokingState;

    start = ctx.start;
    stop = ctx.stop;

    // copy any error nodes to alt label node
    if (ctx.children != null) {
      children = [];
      // reset parent pointer for any error nodes
      for (var child in ctx.children!) {
        if (child is ErrorNode) {
          addChild(child);
        }
      }
    }
  }

  // Double dispatch methods for listeners

  void enterRule(ParseTreeListener listener) {}

  void exitRule(ParseTreeListener listener) {}

  /// Add a parse tree node to this as a child.  Works for
  ///  internal and leaf nodes. Does not set parent link;
  ///  other add methods must do that. Other addChild methods
  ///  call this.
  ///
  ///  We cannot set the parent pointer of the incoming node
  ///  because the existing interfaces do not have a setParent()
  ///  method and I don't want to break backward compatibility for this.
  ///
  ///  @since 4.7
  T addAnyChild<T extends ParseTree>(T t) {
    children ??= [];
    children!.add(t);
    return t;
  }

  /// Add a token leaf node child and force its parent to be this node. */
  TerminalNode addChild(TerminalNode t) {
    t.parent = this;
    return addAnyChild(t);
  }

  /// Add an error node child and force its parent to be this node.
  ///
  /// @since 4.7
  ErrorNode addErrorNode(ErrorNode errorNode) {
    errorNode.parent = this;
    return addAnyChild(errorNode);
  }

  /// Used by enterOuterAlt to toss out a RuleContext previously added as
  ///  we entered a rule. If we have # label, we will need to remove
  ///  generic ruleContext object.
  void removeLastChild() {
    if (children != null) {
      children!.removeLast();
    }
  }

  // Override to make type more specific
  @override
  ParserRuleContext? get parent {
    return super.parent as ParserRuleContext?;
  }

  @override
  ParseTree? getChild<T>(int i) {
    if (children == null || i < 0 || i >= children!.length) {
      return null;
    }
    if (T == dynamic) {
      return children![i];
    }
    var j = -1; // what element have we found with ctxType?
    for (var o in children!) {
      if (o is T) {
        j++;
        if (j == i) {
          return o;
        }
      }
    }
    return null;
  }

  TerminalNode? getToken(int ttype, int i) {
    if (children == null || i < 0 || i >= children!.length) {
      return null;
    }

    var j = -1; // what token with ttype have we found?
    for (var o in children!) {
      if (o is TerminalNode) {
        final tnode = o;
        final symbol = tnode.symbol;
        if (symbol.type == ttype) {
          j++;
          if (j == i) {
            return tnode;
          }
        }
      }
    }

    return null;
  }

  List<TerminalNode> getTokens(int ttype) {
    if (children == null) {
      return [];
    }

    var tokens = <TerminalNode>[];
    for (var o in children!) {
      if (o is TerminalNode) {
        final tnode = o;
        final symbol = tnode.symbol;
        if (symbol.type == ttype) {
          tokens.add(tnode);
        }
      }
    }

    return tokens;
  }

  T? getRuleContext<T extends ParserRuleContext>(int i) {
    return getChild<T>(i) as T?;
  }

  List<T> getRuleContexts<T extends ParserRuleContext>() {
    if (children == null) {
      return [];
    }

    var contexts = <T>[];
    for (var o in children!) {
      if (o is T) {
        contexts.add(o);
      }
    }

    return contexts;
  }

  @override
  int get childCount => children?.length ?? 0;

  @override
  Interval get sourceInterval {
    if (stop == null || stop!.tokenIndex < start!.tokenIndex) {
      return Interval(start!.tokenIndex, start!.tokenIndex - 1); // empty
    }
    return Interval(start!.tokenIndex, stop!.tokenIndex);
  }

  /// Used for rule context info debugging during parse-time, not so much for ATN debugging */
  String toInfoString(Parser recognizer) {
    final rules = recognizer.getRuleInvocationStack(this);

    return "ParserRuleContext${rules.reversed}{start=$start, stop=$stop}'";
  }

  static final EMPTY = ParserRuleContext();
}

/// This class extends [ParserRuleContext] by allowing the value of
/// {@link #getRuleIndex} to be explicitly set for the context.
///
/// <p>
/// [ParserRuleContext] does not include field storage for the rule index
/// since the context classes created by the code generator override the
/// {@link #getRuleIndex} method to return the correct value for that context.
/// Since the parser interpreter does not use the context classes generated for a
/// parser, this class (with slightly more memory overhead per node) is used to
/// provide equivalent functionality.</p>
class InterpreterRuleContext extends ParserRuleContext {
  @override
  int ruleIndex = -1;

  /// Constructs a new [InterpreterRuleContext] with the specified
  /// parent, invoking state, and rule index.
  ///
  /// @param parent The parent context.
  /// @param invokingStateNumber The invoking state number.
  /// @param ruleIndex The rule index for the current context.
  InterpreterRuleContext(
    ParserRuleContext? parent,
    int invokingStateNumber,
    this.ruleIndex,
  ) : super(parent, invokingStateNumber);
}
