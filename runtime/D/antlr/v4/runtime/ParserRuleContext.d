/*
 * Copyright (c) 2012-2020 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.ParserRuleContext;

import antlr.v4.runtime.InterfaceParser;
import antlr.v4.runtime.RecognitionException;
import antlr.v4.runtime.RuleContext;
import antlr.v4.runtime.Token;
import antlr.v4.runtime.misc;
import antlr.v4.runtime.tree.ErrorNode;
import antlr.v4.runtime.tree.ErrorNodeImpl;
import antlr.v4.runtime.tree.ParseTree;
import antlr.v4.runtime.tree.ParseTreeListener;
import antlr.v4.runtime.tree.TerminalNode;
import antlr.v4.runtime.tree.TerminalNodeImpl;
import std.algorithm;
import std.conv;
import std.format;
import std.variant;

/**
 * A rule invocation record for parsing.
 *
 * Contains all of the information about the current rule not stored in the
 * RuleContext. It handles parse tree children list, Any ATN state
 * tracing, and the default values available for rule invocations:
 * start, stop, rule index, current alt number.
 *
 * Subclasses made for each rule and grammar track the parameters,
 * return values, locals, and labels specific to that rule. These
 * are the objects that are returned from rules.
 *
 * Note text is not an actual field of a rule return value; it is computed
 * from start and stop using the input stream's toString() method.  I
 * could add a ctor to this so that we can pass in and store the input
 * stream, but I'm not sure we want to do that.  It would seem to be undefined
 * to get the .text property anyway if the rule matches tokens from multiple
 * input streams.
 *
 * I do not use getters for fields of objects that are used simply to
 * group values such as this aggregate.  The getters/setters are there to
 * satisfy the superclass interface.
 */
class ParserRuleContext : RuleContext
{

    /**
     * @uml
     * @__gshared
     */
    public static __gshared ParserRuleContext EMPTY = new ParserRuleContext;

    /**
     * If we are debugging or building a parse tree for a visitor,
     * we need to track all of the tokens and rule invocations associated
     * with this rule's context. This is empty for parsing w/o tree constr.
     * operation because we don't the need to track the details about
     * how we parse this rule.
     */
    public ParseTree[] children;

    public Token start;

    public Token stop;

    /**
     * The exception that forced this rule to return. If the rule successfully
     * completed, this is {@code null}.
     */
    public RecognitionException exception;

    public this()
    {
    }

    public this(ParserRuleContext parent, int invokingStateNumber)
    {
        super(parent, invokingStateNumber);
    }

    /**
     * COPY a ctx (I'm deliberately not using copy constructor) to avoid
     * confusion with creating node with parent. Does not copy children
     * (except error leaves).
     *
     *  This is used in the generated parser code to flip a generic XContext
     *  node for rule X to a YContext for alt label Y. In that sense, it is
     *  not really a generic copy function.
     *
     *  If we do an error sync() at start of a rule, we might add error nodes
     *  to the generic XContext so this function must copy those nodes to
     *  the YContext as well else they are lost!
     */
    public void copyFrom(ParserRuleContext ctx)
    {
        this.parent = ctx.parent;
        this.invokingState = ctx.invokingState;

        this.start = ctx.start;
        this.stop = ctx.stop;
        // copy any error nodes to alt label node
        if (ctx.children) {
            this.children.length = 0;
            // reset parent pointer for any error nodes
            foreach (ParseTree child; ctx.children) {
                if (cast(ErrorNode)child) {
                    addChild(cast(ErrorNode)child);
                }
            }
        }
    }

    public void enterRule(ParseTreeListener listener)
    {
    }

    public void exitRule(ParseTreeListener listener)
    {
    }

    /**
     * Add a parse tree node to this as a child.  Works for
     *  internal and leaf nodes. Does not set parent link;
     *  other add methods must do that. Other addChild methods
     *  call this.
     *
     *  We cannot set the parent pointer of the incoming node
     *  because the existing interfaces do not have a setParent()
     *  method and I don't want to break backward compatibility for this.
     *
     *  @since 4.7
     */
    public ParseTree addAnyChild(ParseTree t)
    {
        if (children is null) {
            ParseTree[] newChildren;
            children = newChildren;
        }
        children ~= t;
        return t;
    }

    public RuleContext addChild(RuleContext ruleInvocation)
    {
        return cast(RuleContext)addAnyChild(ruleInvocation);
    }

    /**
     * Add a token leaf node child and force its parent to be this node.
     */
    public TerminalNode addChild(TerminalNode t)
    {
        t.setParent(this);
        return cast(TerminalNode)addAnyChild(t);
    }

    /**
     * Used by enterOuterAlt to toss out a RuleContext previously added as
     * we entered a rule. If we have # label, we will need to remove
     * generic ruleContext object.
     */
    public void removeLastChild()
    {
        if (children !is null) {
            children.length--;
        }
    }

    public TerminalNode addChild(Token matchedToken)
    {
        TerminalNodeImpl t = new TerminalNodeImpl(matchedToken);
        addChild(t);
        t.parent = this;
        return t;
    }

    public ErrorNode addErrorNode(Token badToken)
    {
    ErrorNodeImpl t = new ErrorNodeImpl(badToken);
        addChild(t);
        t.parent = this;
        return t;
    }

    /**
     * Override to make type more specific
     * @uml
     * @override
     */
    public override ParserRuleContext getParent()
    {
        return cast(ParserRuleContext)super.getParent();
    }

    /**
     * @uml
     * @override
     */
    public override ParseTree getChild(int i)
    {
        return children && i >= 0 &&
            i < to!int(children.length) ? children[i] : null;
    }

    public auto getChild(T)(int i)
    {
        if (children is null || i < 0 || i >= children.length) {
            return null;
        }

        int j = -1; // what element have we found with ctxType?
        foreach (o; children) {
            if (cast(T)o) {
                j++;
                if (j == i) {
                    return cast(T)o;
                }
            }
        }
        return null;
    }

    public TerminalNode getToken(int ttype, int i)
    {
    if (children is null || i < 0 || i >= children.length) {
            return null;
        }

        int j = -1; // what token with ttype have we found?
        foreach (o; children) {
            if (cast(TerminalNode)o) {
                TerminalNode tnode = cast(TerminalNode)o;
                Token symbol = tnode.getSymbol;
                if (symbol.getType == ttype) {
                    j++;
                    if ( j == i ) {
                        return tnode;
                    }
                }
            }
        }
        return null;
    }

    public TerminalNode[] getTokens(int ttype)
    {
        TerminalNode[] emptyList;
        if (children is null) {
            return emptyList;
        }

        TerminalNode[] tokens = null;
        foreach (o; children) {
            if (cast(TerminalNode)o) {
                TerminalNode tnode = cast(TerminalNode)o;
                Token symbol = tnode.getSymbol;
                if (symbol.getType == ttype) {
                    if (tokens is null) {
                        tokens.length = 0;
                    }
                    tokens ~= tnode;
                }
            }
        }

        if (tokens is null) {
            return emptyList;
        }
        return tokens;
    }

    public T getRuleContext(T)(int i)
    {
        return getChild!T(i);
    }

    public T[] getRuleContexts(T)()
    {
        if (children is null) {
            T[] l;
            return l;
        }
        T[] contexts = null;
        foreach (o; children) {
            if (cast(T)o) {
                contexts ~= cast(T)o;
            }
        }

        if (contexts is null) {
            T[] l;
            return l;
        }
        return contexts;
    }

    /**
     * @uml
     * @override
     */
    public override int getChildCount()
    {
        return children ? to!int(children.length) : 0;
    }

    /**
     * @uml
     * @override
     */
    public override Interval getSourceInterval()
    {
        if (start is null) {
            return cast(Interval)Interval.INVALID;
        }
        if (stop is null || stop.getTokenIndex()<start.getTokenIndex()) {
            return Interval.of(to!int(start.getTokenIndex), to!int(start.getTokenIndex)-1); // empty
        }
        return Interval.of(to!int(start.getTokenIndex), to!int(stop.getTokenIndex));
    }

    /**
     * Get the initial token in this context.
     * Note that the range from start to stop is inclusive, so for rules that do not consume anything
     * (for example, zero length or error productions) this token may exceed stop.
     */
    public Token getStart()
    {
        return start;
    }

    /**
     * Get the final token in this context.
     * Note that the range from start to stop is inclusive, so for rules that do not consume anything
     * (for example, zero length or error productions) this token may precede start.
     */
    public Token getStop()
    {
        return stop;
    }

    /**
     * Used for rule context info debugging during parse-time, not so much for ATN debugging
     */
    public string toInfoString(InterfaceParser recognizer)
    {
        string[] rules = recognizer.getRuleInvocationStack(this);
        rules.reverse();
        return format("ParserRuleContext{ %1$s " ~
                      "start=%2$s, stop=%3$s}", rules,
                      start.getText, stop.getText);
    }

}

version(unittest) {
    import dshould : be, equal, not, should;
    import std.typecons : tuple;
    import unit_threaded;
    @Tags("parserRC")
    @("emptyInstanceParserRuleContext")
    unittest {
        auto rpc = ParserRuleContext.EMPTY;
        rpc.should.not.be(null);
        rpc.getChildCount.should.equal(0);
        auto rpc1 = ParserRuleContext.EMPTY;
        rpc1.should.not.be(null);
        rpc1.should.be(rpc);
        rpc.getStart.should.equal(null);
        rpc.getStop.should.equal(null);
        rpc.getSourceInterval.toString.should.equal("-1..-2");
        rpc.getParent.should.be(null);
    }
}
