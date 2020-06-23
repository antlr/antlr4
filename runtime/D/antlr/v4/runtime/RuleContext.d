/*
 * Copyright (c) 2012-2020 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.RuleContext;

import antlr.v4.runtime.InterfaceRecognizer;
import antlr.v4.runtime.InterfaceRuleContext;
import antlr.v4.runtime.ParserRuleContext;
import antlr.v4.runtime.Token;
import antlr.v4.runtime.atn.ATN;
import antlr.v4.runtime.misc;
import antlr.v4.runtime.tree.ParseTree;
import antlr.v4.runtime.tree.ParseTreeVisitor;
import antlr.v4.runtime.tree.RuleNode;
import antlr.v4.runtime.tree.Trees;
import std.array;
import std.conv : to;
import std.variant;

/**
 * A rule context is a record of a single rule invocation.
 *
 * We form a stack of these context objects using the parent
 * pointer. A parent pointer of null indicates that the current
 * context is the bottom of the stack. The ParserRuleContext subclass
 * as a children list so that we can turn this data structure into a
 * tree.
 *
 * The root node always has a null pointer and invokingState of -1.
 *
 * Upon entry to parsing, the first invoked rule function creates a
 * context object (asubclass specialized for that rule such as
 * SContext) and makes it the root of a parse tree, recorded by field
 * Parser._ctx.
 *
 *  public final SContext s() throws RecognitionException {
 *      SContext _localctx = new SContext(_ctx, getState()); <-- create new node
 *      enterRule(_localctx, 0, RULE_s);                     <-- push it
 *      ...
 *      exitRule();                                          <-- pop back to _localctx
 *      return _localctx;
 *  }
 *
 * A subsequent rule invocation of r from the start rule s pushes a
 * new context object for r whose parent points at s and use invoking
 * state is the state with r emanating as edge label.
 *
 * The invokingState fields from a context object to the root
 * together form a stack of rule indication states where the root
 * (bottom of the stack) has a -1 sentinel value. If we invoke start
 * symbol s then call r1, which calls r2, the  would look like
 *  this:
 *
 *     SContext[-1]   <- root node (bottom of the stack)
 *     R1Context[p]   <- p in rule s called r1
 *     R2Context[q]   <- q in rule r1 called r2
 *
 * So the top of the stack, _ctx, represents a call to the current
 * rule and it holds the return address from another rule that invoke
 * to this rule. To invoke a rule, we must always have a current context.
 *
 * The parent contexts are useful for computing lookahead sets and
 * getting error information.
 *
 * These objects are used during parsing and prediction.
 * For the special case of parsers, we use the subclass
 * ParserRuleContext.
 *
 *  @see ParserRuleContext
 */
class RuleContext : RuleNode, InterfaceRuleContext
{

    public static const ParserRuleContext EMPTY = new ParserRuleContext();

    public RuleContext parent;

    public int invokingState = -1;

    public this()
    {
    }

    public this(RuleContext parent, int invokingState)
    {
        this.parent = parent;
        this.invokingState = invokingState;
    }

    public int depth()
    {
        int n = 0;
        RuleContext p = this;
        while (p) {
            p = p.parent;
            n++;
        }
        return n;
    }

    /**
     * A context is empty if there is no invoking state; meaning nobody called
     * current context.
     */
    public bool isEmpty()
    {
        return invokingState == -1;
    }

    /**
     * satisfy the ParseTree / SyntaxTree interface
     */
    public Interval getSourceInterval()
    {
        return cast(Interval)Interval.INVALID;
    }

    public RuleContext getRuleContext()
    {
        return this;
    }

    public RuleContext getPayload()
    {
        return this;
    }

    /**
     * Return the combined text of all child nodes. This method only considers
     * tokens which have been added to the parse tree.
     * <p>
     * Since tokens on hidden channels (e.g. whitespace or comments) are not
     * added to the parse trees, they will not appear in the output of this
     * method.
     */
    public Variant getText()
    {
        if (getChildCount() == 0) {
            Variant v = "";
            return v;
        }
        auto builder = appender!(string);
        for (int i = 0; i < getChildCount(); i++) {
            builder.put(to!string(getChild(i).getText));
        }
        Variant v = builder.data;
        return v;
    }

    public size_t getRuleIndex()
    {
        return size_t.max;
    }

    /**
     * For rule associated with this parse tree internal node, return
     * the outer alternative number used to match the input. Default
     * implementation does not compute nor store this alt num. Create
     * a subclass of ParserRuleContext with backing field and set
     * option contextSuperClass.
     * to set it.
     */
    public int getAltNumber()
    {
        return ATN.INVALID_ALT_NUMBER;
    }

    /**
     * Set the outer alternative number for this context node. Default
     * implementation does nothing to avoid backing field overhead for
     * trees that don't need it.  Create
     * a subclass of ParserRuleContext with backing field and set
     * option contextSuperClass.
     */
    public void setAltNumber(int altNumber)
    {
    }

    public ParseTree getChild(int i)
    {
        return null;
    }

    public int getChildCount()
    {
        return 0;
    }

    public Variant accept(ParseTreeVisitor visitor)
    {
        return visitor.visitChildren(this);
    }

    /**
     * Print out a whole tree, not just a node, in LISP format
     * (root child1 .. childN). Print just a node if this is a leaf.
     * We have to know the recognizer so we can get rule names.
     */
    public string toStringTree(InterfaceRecognizer recog)
    {
        return Trees.toStringTree(cast(ParseTree)this, recog);
    }

    public string toStringTree(string[] ruleNames)
    {
        return Trees.toStringTree(cast(ParseTree)this, ruleNames);
    }

    public string toStringTree()
    {
        return toStringTree([]);
    }

    /**
     * @uml
     * @override
     */
    public override string toString()
    {
        return toString([]);
    }

    public string toString(InterfaceRecognizer recog)
    {
        return toString(recog, ParserRuleContext.EMPTY);
    }

    public string toString(string[] ruleNames)
    {
        return toString(ruleNames, null);
    }

    /**
     * recog null unless ParserRuleContext, in which case we use subclass toString(...)
     */
    public string toString(InterfaceRecognizer recog, RuleContext stop)
    {
        string[] ruleNames = recog !is null ? recog.getRuleNames() : null;
        string[] ruleNamesList = ruleNames !is null ? ruleNames : null;
        return toString(ruleNamesList) ~ stop.toString;

    }

    public string toString(string[] ruleNames, RuleContext stop)
    {
        auto buf = appender!(string);
        RuleContext p = this;
        buf.put("[");
        while (p !is null && p != stop) {
            if (ruleNames.length == 0) {
                if (!p.isEmpty) {
                    buf.put(to!string(p.invokingState));
                }
            }
            else {
                auto ruleIndex = p.getRuleIndex;
                string ruleName = ruleIndex >= 0 && ruleIndex < ruleNames.length ? ruleNames[ruleIndex] : to!string(ruleIndex);
                buf.put(ruleName);
            }
            if (p.parent !is null && (ruleNames.length || !p.parent.isEmpty)) {
                buf.put(" ");
            }
            p = p.parent;
        }
        buf.put("]");
        return buf.data;
    }

    public RuleContext getParent()
    {
        return parent;
    }

    /**
     * since 4.7. {@see ParseTree#setParent} comment
     */
    public void setParent(RuleContext parent)
    {
        this.parent = parent;
    }

}

version(unittest) {
    import dshould : be, equal, not, should;
    import std.typecons : tuple;
    import unit_threaded;

    class Test {
        @Tags("ruleCont", "reg")
        @("simpleRuleContext")
        unittest {
            auto rcp = new RuleContext(null, -1);
            auto rc = new RuleContext(rcp, -1);
            rc.should.not.be(null);
            rcp.depth.should.equal(1);
            rc.isEmpty.should.equal(true);
            rc.parent.isEmpty.should.equal(true);
            rc.getParent.isEmpty.should.equal(true);
            rc.depth.should.equal(2);
            rc.toString.should.equal("[]");
            rc.toStringTree.should.equal("[]");
            rc.getAltNumber.should.equal(0);
        }

        @Tags("ruleContVoc", "reg")
        @("ruleContextWithVocabulary")
        unittest {
            class RuleContextT : RuleContext {
                public this(RuleContext parent, int invokingState)
                {
                    super(parent, invokingState);
                }

                override public size_t getRuleIndex()
                {
                    return invokingState;
                }
            }
            auto rcp = new RuleContext(null, 1);
            auto rc = new RuleContext(rcp, 0);
            rc.should.not.be(null);
            rc.toString(["A1", "B1"]).should.equal("[" ~ to!string(size_t.max)
             ~ " " ~ to!string(size_t.max) ~ "]");

            rcp = new RuleContextT(null, 1);
            rc = new RuleContextT(rcp, 0);
            rc.should.not.be(null);
            rc.toString(["A1", "B1"]).should.equal("[A1 B1]");
        }
    }
}
