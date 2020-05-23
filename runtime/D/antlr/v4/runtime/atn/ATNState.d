/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Terence Parr
 *  Copyright (c) 2016 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

module antlr.v4.runtime.atn.ATNState;

import std.stdio;
import std.conv;
import std.array;
import antlr.v4.runtime.atn.StateNames;
import antlr.v4.runtime.atn.Transition;
import antlr.v4.runtime.atn.ATN;
import antlr.v4.runtime.misc.IntervalSet;
import std.algorithm.mutation: remove;

/**
 * The following images show the relation of states and
 * {@link ATNState#transitions} for various grammar constructs.
 */
abstract class ATNState
{

    public static immutable int INITIAL_NUM_TRANSITIONS = 4;

    public static immutable int INVALID_STATE_NUMBER = -1;

    /**
     * @uml
     * Which ATN are we in?
     */
    public ATN atn = null;

    public int stateNumber = INVALID_STATE_NUMBER;

    /**
     * @uml
     * at runtime, we don't have Rule objects
     */
    public int ruleIndex;

    public bool epsilonOnlyTransitions = false;

    /**
     * @uml
     * Track the transitions emanating from this ATN state.
     */
    public Transition[] transitions;

    /**
     * @uml
     * Used to cache lookahead during parsing, not used during construction
     */
    public IntervalSet nextTokenWithinRule;

    /**
     * @uml
     * @read
     * @write
     */
    private Transition[] optimizedTransitions_;

    /**
     * @uml
     * @pure
     * @safe
     */
    public int hashCode() @safe pure
    {
        return stateNumber;
    }

    /**
     * @uml
     * @pure
     * @safe
     */
    public bool equals(Object o) @safe pure
    {
        return stateNumber==(cast(ATNState)o).stateNumber;
    }

    /**
     * @uml
     * @pure
     * @safe
     */
    public bool isNonGreedyExitState() @safe pure
    {
        return false;
    }

    /**
     * @uml
     * @pure
     * @safe
     * @override
     */
    public override string toString() @safe pure
    {
        return to!string(stateNumber);
    }

    /**
     * @uml
     * @pure
     * @safe
     */
    public Transition[] getTransitions() @safe pure
    {
        return transitions.dup;
    }

    /**
     * @uml
     * @pure
     * @safe
     */
    public int getNumberOfTransitions() @safe pure
    {
        return to!int(transitions.length);
    }

    public void addTransition(Transition e)
    {
        if (transitions.length == 0) {
            epsilonOnlyTransitions = e.isEpsilon;
        }
        else
            if (epsilonOnlyTransitions != e.isEpsilon()) {
                stderr.writefln("ATN state %1$s has both epsilon and non-epsilon transitions.\n", stateNumber);
                epsilonOnlyTransitions = false;
            }
        transitions ~= e;
    }

    public Transition transition(int i)
    {
        return transitions[i];
    }

    public void setTransition(int i, Transition e)
    {
        transitions[i] = e;
    }

    public Transition removeTransition(int index)
    {
        auto t = transitions[index];
        transitions = transitions[0..index] ~ transitions[index+1..$];
        return t;
    }

    abstract public int getStateType();

    public bool onlyHasEpsilonTransitions()
    {
        return epsilonOnlyTransitions;
    }

    public void setRuleIndex(int ruleIndex)
    {
        this.ruleIndex = ruleIndex;
    }

    public bool isOptimized()
    {
        return optimizedTransitions != transitions;
    }

    public size_t numberOfOptimizedTransitions()
    {
        return optimizedTransitions.length;
    }

    public Transition getOptimizedTransition(size_t i)
    {
        return optimizedTransitions[i];
    }

    public void addOptimizedTransition(Transition e)
    {
        if (!isOptimized)
            {
                optimizedTransitions_.length = 0;
            }
            optimizedTransitions_ ~= e;
    }

    public void setOptimizedTransition(size_t i, Transition e)
    {
        if (!isOptimized)
            {
                assert(false, "InvalidOperationException");
            }
            optimizedTransitions_[i] = e;
    }

    public void removeOptimizedTransition(size_t i)
    {
        if (!isOptimized)
            {
                assert(false, "InvalidOperationException");
            }
            optimizedTransitions_ = optimizedTransitions_.remove(i);
    }

    public this()
    {
        optimizedTransitions = transitions.dup;
    }

    public final Transition[] optimizedTransitions()
    {
        return this.optimizedTransitions_.dup;
    }

    public final void optimizedTransitions(Transition[] optimizedTransitions)
    {
        this.optimizedTransitions_ = optimizedTransitions.dup;
    }

}
