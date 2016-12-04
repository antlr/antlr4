/* Copyright (c) 2012 The ANTLR Project Contributors. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.Collections.Generic;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    public abstract class ATNSimulator
    {
        [Obsolete(@"Use ATNDeserializer.SerializedVersion instead.")]
        public static readonly int SerializedVersion = ATNDeserializer.SerializedVersion;

        /// <summary>This is the current serialized UUID.</summary>
        /// <remarks>This is the current serialized UUID.</remarks>
        [Obsolete(@"Use ATNDeserializer.CheckCondition(bool) instead.")]
        public static readonly Guid SerializedUuid = ATNDeserializer.SerializedUuid;

        public const char RuleVariantDelimiter = '$';

        public const string RuleLfVariantMarker = "$lf$";

        public const string RuleNolfVariantMarker = "$nolf$";

        /// <summary>Must distinguish between missing edge and edge we know leads nowhere</summary>
        [NotNull]
        public static readonly DFAState Error =
            new DFAState(new EmptyEdgeMap<DFAState>(0, -1), new EmptyEdgeMap<DFAState>(0, -1), new ATNConfigSet())
            {
                stateNumber = int.MaxValue
            };

        [NotNull]
        public readonly ATN atn;

        public ATNSimulator(ATN atn)
        {
            this.atn = atn;
        }

        public abstract void Reset();

        /// <summary>Clear the DFA cache used by the current instance.</summary>
        /// <remarks>
        /// Clear the DFA cache used by the current instance. Since the DFA cache may
        /// be shared by multiple ATN simulators, this method may affect the
        /// performance (but not accuracy) of other parsers which are being used
        /// concurrently.
        /// </remarks>
        /// <exception cref="System.NotSupportedException">
        /// if the current instance does not
        /// support clearing the DFA.
        /// </exception>
        /// <since>4.3</since>
        public virtual void ClearDFA()
        {
            atn.ClearDFA();
        }

        [Obsolete(@"Use ATNDeserializer.Deserialize(char[]) instead.")]
        public static ATN Deserialize(char[] data)
        {
            return new ATNDeserializer().Deserialize(data);
        }

        [return: NotNull]
        [Obsolete(@"Use ATNDeserializer.EdgeFactory(ATN, TransitionType, int, int, int, int, int, System.Collections.Generic.IList{E}) instead.")]
        public static Transition EdgeFactory(ATN atn, TransitionType type, int src, int trg, int arg1, int arg2, int arg3, IList<IntervalSet> sets)
        {
            return new ATNDeserializer().EdgeFactory(atn, type, src, trg, arg1, arg2, arg3, sets);
        }

        [Obsolete(@"Use ATNDeserializer.StateFactory(StateType, int) instead.")]
        public static ATNState StateFactory(StateType type, int ruleIndex)
        {
            return new ATNDeserializer().StateFactory(type, ruleIndex);
        }
    }
}
