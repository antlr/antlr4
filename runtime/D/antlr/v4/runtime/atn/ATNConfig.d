/*
 * Copyright (c) 2012-2020 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.atn.ATNConfig;

import antlr.v4.runtime.InterfaceRecognizer;
import antlr.v4.runtime.atn.ATNConfigObjectEqualityComparator;
import antlr.v4.runtime.atn.ATNState;
import antlr.v4.runtime.atn.PredictionContext;
import antlr.v4.runtime.atn.SemanticContext;
import antlr.v4.runtime.misc.MurmurHash;
import std.array;
import std.conv;
import std.stdio;

/**
 * A tuple: (ATN state, predicted alt, syntactic, semantic context).
 * The syntactic context is a graph-structured stack node whose
 * path(s) to the root is the rule invocation(s)
 * chain used to arrive at the state.  The semantic context is
 * the tree of semantic predicates encountered before reaching
 * an ATN state.
 */
class ATNConfig
{

    /**
     * This field stores the bit mask for implementing the
     * {@link #isPrecedenceFilterSuppressed} property as a bit within the
     * existing {@link #reachesIntoOuterContext} field.
     */
    public static immutable int SUPPRESS_PRECEDENCE_FILTER = 0x40000000;

    /**
     * The ATN state associated with this configuration.
     */
    public ATNState state;

    /**
     * What alt (or lexer rule) is predicted by this configuration?
     */
    public int alt;

    /**
     * The stack of invoking states leading to the rule/states associated
     * with this config.  We track only those contexts pushed during
     * execution of the ATN simulator.
     */
    public PredictionContext context;

    /**
     * We cannot execute predicates dependent upon local context unless
     * we know for sure we are in the correct context. Because there is
     * no way to do this efficiently, we simply cannot evaluate
     * dependent predicates unless we are in the rule that initially
     * invokes the ATN simulator.
     *
     * <p>
     * closure() tracks the depth of how far we dip into the outer context:
     * depth &gt; 0.  Note that it may not be totally accurate depth since I
     * don't ever decrement. TODO: make it a boolean then</p>
     *
     * <p>
     * For memory efficiency, the {@link #isPrecedenceFilterSuppressed} method
     * is also backed by this field. Since the field is publicly accessible, the
     * highest bit which would not cause the value to become negative is used to
     * store this field. This choice minimizes the risk that code which only
     * compares this value to 0 would be affected by the new purpose of the
     * flag. It also ensures the performance of the existing {@link ATNConfig}
     * constructors as well as certain operations like
     * {@link ATNConfigSet#add(ATNConfig, DoubleKeyMap)} method are
     * <em>completely</em> unaffected by the change.</p>
     */
    public int reachesIntoOuterContext;

    public SemanticContext semanticContext;

    public size_t function(Object o) @trusted nothrow hashOfFp;

    public bool function(Object a, Object b) opEqualsFp;

    /**
     * Duplication
     */
    public this(ATNConfig old)
    {
        this.state = old.state;
        this.alt = old.alt;
        this.context = old.context;
        this.semanticContext = old.semanticContext;
        this.reachesIntoOuterContext = old.reachesIntoOuterContext;
        this.hashOfFp = old.hashOfFp;
        this.opEqualsFp = old.opEqualsFp;
    }

    public this(ATNState state, int alt, PredictionContext context)
    {
        if (!SemanticContext.NONE) {
            auto sp = new SemanticContext;
            SemanticContext.NONE = sp.new SemanticContext.Predicate;
        }
        this(state, alt, context, SemanticContext.NONE);
    }

    public this(ATNState state, int alt, PredictionContext context, const SemanticContext semanticContext)
    {
        this.state = state;
        this.alt = alt;
        this.context = context;
        this.semanticContext = cast(SemanticContext)semanticContext;
        this.hashOfFp = &ATNConfigObjectEqualityComparator.toHash;
        this.opEqualsFp = &ATNConfigObjectEqualityComparator.opEquals;
    }

    public this(ATNConfig c, ATNState state)
    {
        //this.semanticContext = c.semanticContext;
        this(c, state, c.context, c.semanticContext);
    }

    public this(ATNConfig c, ATNState state, SemanticContext semanticContext)
    {
        this(c, state, c.context, semanticContext);
    }

    public this(ATNConfig c, SemanticContext semanticContext)
    {
        this(c, c.state, c.context, semanticContext);
    }

    public this(ATNConfig c, ATNState state, PredictionContext context)
    {
        this(c, state, context, c.semanticContext);
    }

    public this(ATNConfig c, ATNState state, PredictionContext context, const SemanticContext semanticContext)
    {
        this.state = state;
        this.alt = c.alt;
        this.context = context;
        this.semanticContext = cast(SemanticContext) semanticContext;
        this.reachesIntoOuterContext = c.reachesIntoOuterContext;
        this.hashOfFp = &ATNConfigObjectEqualityComparator.toHash;
        this.opEqualsFp = &ATNConfigObjectEqualityComparator.opEquals;
    }

    /**
     * This method gets the value of the {@link #reachesIntoOuterContext} field
     * as it existed prior to the introduction of the
     * {@link #isPrecedenceFilterSuppressed} method.
     */
    public int getOuterContextDepth()
    {
        return reachesIntoOuterContext & ~SUPPRESS_PRECEDENCE_FILTER;
    }

    public bool isPrecedenceFilterSuppressed()
    {
        return (reachesIntoOuterContext & SUPPRESS_PRECEDENCE_FILTER) != 0;
    }

    public void setPrecedenceFilterSuppressed(bool value)
    {
        if (value) {
            this.reachesIntoOuterContext |= 0x40000000;
        }
        else {
            this.reachesIntoOuterContext &= ~SUPPRESS_PRECEDENCE_FILTER;
        }
    }

    /**
     * An ATN configuration is equal to another if both have
     * the same state, they predict the same alternative, and
     * syntactic/semantic contexts are the same.
     * @uml
     * @override
     */
    public override bool opEquals(Object o)
    {
        if (!cast(ATNConfig)o) {
            return false;
        }
        return this.opEquals(cast(ATNConfig)o);
    }

    public bool opEquals(ATNConfig other)
    {
        return opEqualsFp(this, other);
    }

    /**
     * @uml
     * @trusted
     * @nothrow
     * @override
     */
    public override size_t toHash() @trusted nothrow
    {
        return hashOfFp(this);
    }

    /**
     * @uml
     * @override
     */
    public override string toString()
    {
        return toString(null, true);
    }

    public string toString(InterfaceRecognizer recog, bool showAlt)
    {
        auto buf = appender!string;
        //      if ( state.ruleIndex>=0 ) {
        //          if ( recog!=null ) buf.append(recog.getRuleNames()[state.ruleIndex]+":");
        //          else buf.append(state.ruleIndex+":");
        //      }
        buf.put('(');
        buf.put(state.toString);
        if ( showAlt ) {
            buf.put(",");
            buf.put(to!string(alt));
        }
        if (context !is null) {
            buf.put(",[");
            buf.put(context.toString());
            buf.put("]");
        }
        if (semanticContext && semanticContext != SemanticContext.NONE ) {
            buf.put(",");
            buf.put(semanticContext.toString);
        }
        if (getOuterContextDepth > 0) {
            buf.put(",up=");
            buf.put(to!string(getOuterContextDepth));
        }
        buf.put(')');
        return buf.data;
    }

}
