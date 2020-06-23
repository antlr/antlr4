/*
 * Copyright (c) 2012-2020 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.atn.AND;

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
 * A semantic context which is true whenever none of the contained contexts
 * is false.

 */
class AND : Operator
{

    public SemanticContext[] opnds;

    public this(SemanticContext a, SemanticContext b)
    {
        SemanticContext[] operands;
        if (cast(AND)a)
            operands ~= (cast(AND)a).opnds;
        else
            operands ~= a;
        if (cast(AND)b)
            operands ~= (cast(AND)b).opnds;
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
            // interested in the transition with the lowest precedence
            SemanticContext.PrecedencePredicate reduced = minElement(precedencePredicates);
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
        if (!cast(AND)obj)
            return false;
        AND other = cast(AND)obj;
        return equal(this.opnds, other.opnds);
    }

    /**
     * @uml
     * @override
     * @trusted
     */
    public override size_t toHash() @trusted
    {
        size_t classId = 0;
        foreach(el; AND.classinfo.name)
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
        foreach (SemanticContext opnd; opnds)
        {
            if (!opnd.eval(parser, parserCallStack))
                return false;
        }
        return true;
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
            if (evaluated is null) {
                // The AND context is false if any element is false
                return null;
            }
            else if (evaluated != NONE) {
                // Reduce the result by skipping true elements
                operands ~= evaluated;
            }
        }

        if (!differs) {
            return this;
        }

        if (operands.length == 0) {
            // all elements were true, so the AND context is true
            return NONE;
        }

        SemanticContext result = operands[0];
        for (int i = 1; i < operands.length; i++) {
            result = SemanticContext.and(result, operands[i]);
        }

        return result;

    }

    /**
     * @uml
     * @override
     */
    public override string toString()
    {
        return to!string(map!(n => n.toString)(opnds).joiner("&&"));
    }

}
