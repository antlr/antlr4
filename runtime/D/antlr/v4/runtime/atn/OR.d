/*
 * Copyright (c) 2012-2020 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.atn.OR;

import antlr.v4.runtime.InterfaceRecognizer;
import antlr.v4.runtime.RuleContext;
import antlr.v4.runtime.Token;
import antlr.v4.runtime.atn.Operator;
import antlr.v4.runtime.atn.SemanticContext;
import antlr.v4.runtime.misc.MurmurHash;
import std.algorithm.comparison;
import std.algorithm.iteration;
import std.algorithm.searching;
import std.conv;

/**
 * A semantic context which is true whenever at least one of the contained
 * contexts is true.
 */
class OR : Operator
{

    public SemanticContext[] opnds;

    public this(SemanticContext a, SemanticContext b)
    {
        SemanticContext[] operands;
        if (cast(OR)a)
            operands ~= (cast(OR)a).opnds;
        else
            operands ~= a;
        if (cast(OR)b)
            operands ~= (cast(OR)b).opnds;
        else
        {
            auto foundEl = false;
            foreach (el; operands)
            {
                auto bHash = b.toHash;
                if (el.toHash == bHash)
                {
                    foundEl = true;
                    break;
                }
            }
            if (!foundEl)
                operands ~= b;
        }

        SemanticContext.PrecedencePredicate[] precedencePredicates =
            filterPrecedencePredicates(operands);
        if (precedencePredicates.length) {
            // interested in the transition with the highest precedence
            SemanticContext.PrecedencePredicate reduced = maxElement(precedencePredicates);
            operands ~= reduced;
        }
        this.opnds = operands;
    }

    /**
     * @uml
     * @override
     */
    public override SemanticContext[] getOperands()
    {
        return opnds;
    }

    /**
     * @uml
     * @override
     */
    public override bool opEquals(Object obj)
    {
        if (this is obj)
            return true;
        if (!cast(OR)obj)
            return false;
        return this.opnds == (cast(OR)obj).opnds;
    }

    /**
     * @uml
     * @override
     * @trusted
     */
    public override size_t toHash() @trusted
    {
        size_t classId = 0;
        foreach(el; OR.classinfo.name)
        {
                classId += cast(size_t)el;
        }
        return MurmurHash.hashCode(opnds, classId);
    }

    /**
     * @uml
     * @override
     */
    public override bool eval(InterfaceRecognizer parser, RuleContext parserCallStack)
    {
        foreach (SemanticContext opnd; opnds) {
            if (opnd.eval(parser, parserCallStack))
                return true;
        }
        return false;
    }

    /**
     * @uml
     * @override
     */
    public override SemanticContext evalPrecedence(InterfaceRecognizer parser, RuleContext parserCallStack)
    {
        bool differs = false;
        SemanticContext[] operands;
        foreach (SemanticContext context; opnds) {
            SemanticContext evaluated = context.evalPrecedence(parser, parserCallStack);
            differs |= (evaluated != context);
            if (evaluated == NONE) {
                // The OR context is true if any element is true
                return NONE;
            }
            else if (evaluated !is null) {
                // Reduce the result by skipping false elements
                operands ~= evaluated;
            }
        }

        if (!differs) {
            return this;
        }

        if (operands.length == 0) {
            // all elements were false, so the OR context is false
            return null;
        }

        SemanticContext result = operands[0];
        for (int i = 1; i < operands.length; i++) {
            result = SemanticContext.or(result, operands[i]);
        }
        return result;
    }

    /**
     * @uml
     * @override
     */
    public override string toString()
    {
        return to!string(map!(n => n.toString)(opnds).joiner("||"));
    }

}
