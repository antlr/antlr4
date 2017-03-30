/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    public abstract class ATNState
    {
        public const int InitialNumTransitions = 4;

        public static readonly ReadOnlyCollection<string> serializationNames = new ReadOnlyCollection<string>(Arrays.AsList("INVALID", "BASIC", "RULE_START", "BLOCK_START", "PLUS_BLOCK_START", "STAR_BLOCK_START", "TOKEN_START", "RULE_STOP", "BLOCK_END", "STAR_LOOP_BACK", "STAR_LOOP_ENTRY", "PLUS_LOOP_BACK", "LOOP_END"));

        public const int InvalidStateNumber = -1;

        public ATN atn = null;

        public int stateNumber = InvalidStateNumber;

        public int ruleIndex;

        public bool epsilonOnlyTransitions = false;

        protected internal readonly List<Transition> transitions = new List<Transition>(InitialNumTransitions);

        protected internal List<Transition> optimizedTransitions;

        public IntervalSet nextTokenWithinRule;

        public virtual int NonStopStateNumber
        {
            get
            {
                return stateNumber;
            }
        }

        public override int GetHashCode()
        {
            return stateNumber;
        }

        public override bool Equals(object o)
        {
            return o==this ||
				(o is ATNState && stateNumber == ((ATNState)o).stateNumber);
        }

        public virtual bool IsNonGreedyExitState
        {
            get
            {
                return false;
            }
        }

        public override string ToString()
        {
            return stateNumber.ToString();
        }

        public virtual Transition[] TransitionsArray
        {
            get
            {
                return transitions.ToArray();
            }
        }

        public virtual int NumberOfTransitions
        {
            get
            {
                return transitions.Count;
            }
        }

        public virtual void AddTransition(Antlr4.Runtime.Atn.Transition e)
        {
            AddTransition(transitions.Count, e);
        }

        public virtual void AddTransition(int index, Antlr4.Runtime.Atn.Transition e)
        {
            if (transitions.Count == 0)
            {
                epsilonOnlyTransitions = e.IsEpsilon;
            }
            else
            {
                if (epsilonOnlyTransitions != e.IsEpsilon)
                {
#if !PORTABLE
                    System.Console.Error.WriteLine("ATN state {0} has both epsilon and non-epsilon transitions.", stateNumber);
#endif
                    epsilonOnlyTransitions = false;
                }
            }
            transitions.Insert(index, e);
        }

        public virtual Antlr4.Runtime.Atn.Transition Transition(int i)
        {
            return transitions[i];
        }

        public virtual void SetTransition(int i, Antlr4.Runtime.Atn.Transition e)
        {
            transitions[i] = e;
        }

        public virtual void RemoveTransition(int index)
        {
            transitions.RemoveAt(index);
        }

        public abstract Antlr4.Runtime.Atn.StateType StateType
        {
            get;
        }

        public bool OnlyHasEpsilonTransitions
        {
            get
            {
                return epsilonOnlyTransitions;
            }
        }

        public virtual void SetRuleIndex(int ruleIndex)
        {
            this.ruleIndex = ruleIndex;
        }

        public virtual bool IsOptimized
        {
            get
            {
                return optimizedTransitions != transitions;
            }
        }

        public virtual int NumberOfOptimizedTransitions
        {
            get
            {
                return optimizedTransitions.Count;
            }
        }

        public virtual Antlr4.Runtime.Atn.Transition GetOptimizedTransition(int i)
        {
            return optimizedTransitions[i];
        }

        public virtual void AddOptimizedTransition(Antlr4.Runtime.Atn.Transition e)
        {
            if (!IsOptimized)
            {
                optimizedTransitions = new List<Antlr4.Runtime.Atn.Transition>();
            }
            optimizedTransitions.Add(e);
        }

        public virtual void SetOptimizedTransition(int i, Antlr4.Runtime.Atn.Transition e)
        {
            if (!IsOptimized)
            {
                throw new InvalidOperationException();
            }
            optimizedTransitions[i] = e;
        }

        public virtual void RemoveOptimizedTransition(int i)
        {
            if (!IsOptimized)
            {
                throw new InvalidOperationException();
            }
            optimizedTransitions.RemoveAt(i);
        }

        public ATNState()
        {
            optimizedTransitions = transitions;
        }
    }
}
