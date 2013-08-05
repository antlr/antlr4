/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
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
using System.Collections.Generic;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;
using Sharpen;

namespace Antlr4.Runtime.Atn
{
#if !PORTABLE
    [System.Serializable]
#endif
    public sealed class PredictionMode
    {
        /// <summary>
        /// Do only local context prediction (SLL style) and using
        /// heuristic which almost always works but is much faster
        /// than precise answer.
        /// </summary>
        /// <remarks>
        /// Do only local context prediction (SLL style) and using
        /// heuristic which almost always works but is much faster
        /// than precise answer.
        /// </remarks>
        public static readonly PredictionMode Sll = new PredictionMode();

        /// <summary>Full LL(*) that always gets right answer.</summary>
        /// <remarks>
        /// Full LL(*) that always gets right answer. For speed
        /// reasons, we terminate the prediction process when we know for
        /// sure which alt to predict. We don't always know what
        /// the ambiguity is in this mode.
        /// </remarks>
        public static readonly PredictionMode Ll = new PredictionMode();

        /// <summary>
        /// Tell the full LL prediction algorithm to pursue lookahead until
        /// it has uniquely predicted an alternative without conflict or it's
        /// certain that it's found an ambiguous input sequence.
        /// </summary>
        /// <remarks>
        /// Tell the full LL prediction algorithm to pursue lookahead until
        /// it has uniquely predicted an alternative without conflict or it's
        /// certain that it's found an ambiguous input sequence.  when this
        /// variable is false. When true, the prediction process will
        /// continue looking for the exact ambiguous sequence even if
        /// it has already figured out which alternative to predict.
        /// </remarks>
        public static readonly PredictionMode LlExactAmbigDetection = new PredictionMode(
            );

        /// <summary>A Map that uses just the state and the stack context as the key.</summary>
        /// <remarks>A Map that uses just the state and the stack context as the key.</remarks>
        internal class AltAndContextMap : Dictionary<ATNConfig, BitSet>
        {
            public AltAndContextMap() : base(PredictionMode.AltAndContextConfigEqualityComparator
                .Instance)
            {
            }
        }

        private sealed class AltAndContextConfigEqualityComparator : EqualityComparer<ATNConfig
            >
        {
            public static readonly PredictionMode.AltAndContextConfigEqualityComparator Instance
                 = new PredictionMode.AltAndContextConfigEqualityComparator();

            private AltAndContextConfigEqualityComparator()
            {
            }

            /// <summary>Code is function of (s, _, ctx, _)</summary>
            public override int GetHashCode(ATNConfig o)
            {
                int hashCode = MurmurHash.Initialize(7);
                hashCode = MurmurHash.Update(hashCode, o.State.stateNumber);
                hashCode = MurmurHash.Update(hashCode, o.Context);
                hashCode = MurmurHash.Finish(hashCode, 2);
                return hashCode;
            }

            public override bool Equals(ATNConfig a, ATNConfig b)
            {
                if (a == b)
                {
                    return true;
                }
                if (a == null || b == null)
                {
                    return false;
                }
                return a.State.stateNumber == b.State.stateNumber && a.Context.Equals(b.Context);
            }
        }

        /// <summary>Computes the SLL prediction termination condition.</summary>
        /// <remarks>
        /// Computes the SLL prediction termination condition.
        /// <p/>
        /// This method computes the SLL prediction termination condition for both of
        /// the following cases.
        /// <ul>
        /// <li>The usual SLL+LL fallback upon SLL conflict</li>
        /// <li>Pure SLL without LL fallback</li>
        /// </ul>
        /// <p/>
        /// <strong>COMBINED SLL+LL PARSING</strong>
        /// <p/>
        /// When LL-fallback is enabled upon SLL conflict, correct predictions are
        /// ensured regardless of how the termination condition is computed by this
        /// method. Due to the substantially higher cost of LL prediction, the
        /// prediction should only fall back to LL when the additional lookahead
        /// cannot lead to a unique SLL prediction.
        /// <p/>
        /// Assuming combined SLL+LL parsing, an SLL configuration set with only
        /// conflicting subsets should fall back to full LL, even if the
        /// configuration sets don't resolve to the same alternative (e.g.
        /// <code></code>
        /// 
        /// 1,2}} and
        /// <code></code>
        /// 
        /// 3,4}}. If there is at least one non-conflicting
        /// configuration, SLL could continue with the hopes that more lookahead will
        /// resolve via one of those non-conflicting configurations.
        /// <p/>
        /// Here's the prediction termination rule them: SLL (for SLL+LL parsing)
        /// stops when it sees only conflicting configuration subsets. In contrast,
        /// full LL keeps going when there is uncertainty.
        /// <p/>
        /// <strong>HEURISTIC</strong>
        /// <p/>
        /// As a heuristic, we stop prediction when we see any conflicting subset
        /// unless we see a state that only has one alternative associated with it.
        /// The single-alt-state thing lets prediction continue upon rules like
        /// (otherwise, it would admit defeat too soon):
        /// <p/>
        /// <code>[12|1|[], 6|2|[], 12|2|[]]. s : (ID | ID ID?) ';' ;</code>
        /// <p/>
        /// When the ATN simulation reaches the state before
        /// <code>';'</code>
        /// , it has a
        /// DFA state that looks like:
        /// <code>[12|1|[], 6|2|[], 12|2|[]]</code>
        /// . Naturally
        /// <code>12|1|[]</code>
        /// and
        /// <code>12|2|[]</code>
        /// conflict, but we cannot stop
        /// processing this node because alternative to has another way to continue,
        /// via
        /// <code>[6|2|[]]</code>
        /// .
        /// <p/>
        /// It also let's us continue for this rule:
        /// <p/>
        /// <code>[1|1|[], 1|2|[], 8|3|[]] a : A | A | A B ;</code>
        /// <p/>
        /// After matching input A, we reach the stop state for rule A, state 1.
        /// State 8 is the state right before B. Clearly alternatives 1 and 2
        /// conflict and no amount of further lookahead will separate the two.
        /// However, alternative 3 will be able to continue and so we do not stop
        /// working on this state. In the previous example, we're concerned with
        /// states associated with the conflicting alternatives. Here alt 3 is not
        /// associated with the conflicting configs, but since we can continue
        /// looking for input reasonably, don't declare the state done.
        /// <p/>
        /// <strong>PURE SLL PARSING</strong>
        /// <p/>
        /// To handle pure SLL parsing, all we have to do is make sure that we
        /// combine stack contexts for configurations that differ only by semantic
        /// predicate. From there, we can do the usual SLL termination heuristic.
        /// <p/>
        /// <strong>PREDICATES IN SLL+LL PARSING</strong>
        /// <p/>
        /// SLL decisions don't evaluate predicates until after they reach DFA stop
        /// states because they need to create the DFA cache that works in all
        /// semantic situations. In contrast, full LL evaluates predicates collected
        /// during start state computation so it can ignore predicates thereafter.
        /// This means that SLL termination detection can totally ignore semantic
        /// predicates.
        /// <p/>
        /// Implementation-wise,
        /// <see cref="ATNConfigSet">ATNConfigSet</see>
        /// combines stack contexts but not
        /// semantic predicate contexts so we might see two configurations like the
        /// following.
        /// <p/>
        /// <code></code>
        /// (s, 1, x,
        /// ), (s, 1, x', {p})}
        /// <p/>
        /// Before testing these configurations against others, we have to merge
        /// <code>x</code>
        /// and
        /// <code>x'</code>
        /// (without modifying the existing configurations).
        /// For example, we test
        /// <code>(x+x')==x''</code>
        /// when looking for conflicts in
        /// the following configurations.
        /// <p/>
        /// <code></code>
        /// (s, 1, x,
        /// ), (s, 1, x', {p}), (s, 2, x'', {})}
        /// <p/>
        /// If the configuration set has predicates (as indicated by
        /// <see cref="ATNConfigSet.HasSemanticContext()">ATNConfigSet.HasSemanticContext()</see>
        /// ), this algorithm makes a copy of
        /// the configurations to strip out all of the predicates so that a standard
        /// <see cref="ATNConfigSet">ATNConfigSet</see>
        /// will merge everything ignoring predicates.
        /// </remarks>
        public static bool HasSLLConflictTerminatingPrediction(PredictionMode mode, ATNConfigSet
             configs)
        {
            if (AllConfigsInRuleStopStates(configs))
            {
                return true;
            }
            // pure SLL mode parsing
            if (mode == PredictionMode.Sll)
            {
                // Don't bother with combining configs from different semantic
                // contexts if we can fail over to full LL; costs more time
                // since we'll often fail over anyway.
                if (configs.HasSemanticContext)
                {
                    // dup configs, tossing out semantic predicates
                    ATNConfigSet dup = new ATNConfigSet();
                    foreach (ATNConfig c in configs)
                    {
                        ATNConfig config = c.Transform(c.State, SemanticContext.None);
                        dup.AddItem(config);
                    }
                    configs = dup;
                }
            }
            // now we have combined contexts for configs with dissimilar preds
            // pure SLL or combined SLL+LL mode parsing
            ICollection<BitSet> altsets = GetConflictingAltSubsets(configs);
            bool heuristic = HasConflictingAltSet(altsets) && !HasStateAssociatedWithOneAlt(configs
                );
            return heuristic;
        }

        /// <summary>
        /// Checks if any configuration in
        /// <code>configs</code>
        /// is in a
        /// <see cref="RuleStopState">RuleStopState</see>
        /// . Configurations meeting this condition have reached
        /// the end of the decision rule (local context) or end of start rule (full
        /// context).
        /// </summary>
        /// <param name="configs">the configuration set to test</param>
        /// <returns>
        /// 
        /// <code>true</code>
        /// if any configuration in
        /// <code>configs</code>
        /// is in a
        /// <see cref="RuleStopState">RuleStopState</see>
        /// , otherwise
        /// <code>false</code>
        /// </returns>
        public static bool HasConfigInRuleStopState(IEnumerable<ATNConfig> configs)
        {
            foreach (ATNConfig c in configs)
            {
                if (c.State is RuleStopState)
                {
                    return true;
                }
            }
            return false;
        }

        /// <summary>
        /// Checks if all configurations in
        /// <code>configs</code>
        /// are in a
        /// <see cref="RuleStopState">RuleStopState</see>
        /// . Configurations meeting this condition have reached
        /// the end of the decision rule (local context) or end of start rule (full
        /// context).
        /// </summary>
        /// <param name="configs">the configuration set to test</param>
        /// <returns>
        /// 
        /// <code>true</code>
        /// if all configurations in
        /// <code>configs</code>
        /// are in a
        /// <see cref="RuleStopState">RuleStopState</see>
        /// , otherwise
        /// <code>false</code>
        /// </returns>
        public static bool AllConfigsInRuleStopStates(IEnumerable<ATNConfig> configs)
        {
            foreach (ATNConfig config in configs)
            {
                if (!(config.State is RuleStopState))
                {
                    return false;
                }
            }
            return true;
        }

        /// <summary>Full LL prediction termination.</summary>
        /// <remarks>
        /// Full LL prediction termination.
        /// <p/>
        /// Can we stop looking ahead during ATN simulation or is there some
        /// uncertainty as to which alternative we will ultimately pick, after
        /// consuming more input? Even if there are partial conflicts, we might know
        /// that everything is going to resolve to the same minimum alternative. That
        /// means we can stop since no more lookahead will change that fact. On the
        /// other hand, there might be multiple conflicts that resolve to different
        /// minimums. That means we need more look ahead to decide which of those
        /// alternatives we should predict.
        /// <p/>
        /// The basic idea is to split the set of configurations
        /// <code>C</code>
        /// , into
        /// conflicting subsets
        /// <code>(s, _, ctx, _)</code>
        /// and singleton subsets with
        /// non-conflicting configurations. Two configurations conflict if they have
        /// identical
        /// <see cref="ATNConfig.state">ATNConfig#state</see>
        /// and
        /// <see cref="ATNConfig.context">ATNConfig#context</see>
        /// values
        /// but different
        /// <see cref="ATNConfig.Alt()">ATNConfig.Alt()</see>
        /// value, e.g.
        /// <code>(s, i, ctx, _)</code>
        /// and
        /// <code>(s, j, ctx, _)</code>
        /// for
        /// <code>i!=j</code>
        /// .
        /// <p/>
        /// Reduce these configuration subsets to the set of possible alternatives.
        /// You can compute the alternative subsets in one pass as follows:
        /// <p/>
        /// <code></code>
        /// A_s,ctx =
        /// i | (s, i, ctx, _)}} for each configuration in
        /// <code>C</code>
        /// holding
        /// <code>s</code>
        /// and
        /// <code>ctx</code>
        /// fixed.
        /// <p/>
        /// Or in pseudo-code, for each configuration
        /// <code>c</code>
        /// in
        /// <code>C</code>
        /// :
        /// <pre>
        /// map[c] U= c.
        /// <see cref="ATNConfig.Alt()">getAlt()</see>
        /// # map hash/equals uses s and x, not
        /// alt and not pred
        /// </pre>
        /// <p/>
        /// The values in
        /// <code>map</code>
        /// are the set of
        /// <code>A_s,ctx</code>
        /// sets.
        /// <p/>
        /// If
        /// <code>|A_s,ctx|=1</code>
        /// then there is no conflict associated with
        /// <code>s</code>
        /// and
        /// <code>ctx</code>
        /// .
        /// <p/>
        /// Reduce the subsets to singletons by choosing a minimum of each subset. If
        /// the union of these alternative subsets is a singleton, then no amount of
        /// more lookahead will help us. We will always pick that alternative. If,
        /// however, there is more than one alternative, then we are uncertain which
        /// alternative to predict and must continue looking for resolution. We may
        /// or may not discover an ambiguity in the future, even if there are no
        /// conflicting subsets this round.
        /// <p/>
        /// The biggest sin is to terminate early because it means we've made a
        /// decision but were uncertain as to the eventual outcome. We haven't used
        /// enough lookahead. On the other hand, announcing a conflict too late is no
        /// big deal; you will still have the conflict. It's just inefficient. It
        /// might even look until the end of file.
        /// <p/>
        /// No special consideration for semantic predicates is required because
        /// predicates are evaluated on-the-fly for full LL prediction, ensuring that
        /// no configuration contains a semantic context during the termination
        /// check.
        /// <p/>
        /// <strong>CONFLICTING CONFIGS</strong>
        /// <p/>
        /// Two configurations
        /// <code>(s, i, x)</code>
        /// and
        /// <code>(s, j, x')</code>
        /// , conflict
        /// when
        /// <code>i!=j</code>
        /// but
        /// <code>x=x'</code>
        /// . Because we merge all
        /// <code>(s, i, _)</code>
        /// configurations together, that means that there are at
        /// most
        /// <code>n</code>
        /// configurations associated with state
        /// <code>s</code>
        /// for
        /// <code>n</code>
        /// possible alternatives in the decision. The merged stacks
        /// complicate the comparison of configuration contexts
        /// <code>x</code>
        /// and
        /// <code>x'</code>
        /// . Sam checks to see if one is a subset of the other by calling
        /// merge and checking to see if the merged result is either
        /// <code>x</code>
        /// or
        /// <code>x'</code>
        /// . If the
        /// <code>x</code>
        /// associated with lowest alternative
        /// <code>i</code>
        /// is the superset, then
        /// <code>i</code>
        /// is the only possible prediction since the
        /// others resolve to
        /// <code>min(i)</code>
        /// as well. However, if
        /// <code>x</code>
        /// is
        /// associated with
        /// <code>j&gt;i</code>
        /// then at least one stack configuration for
        /// <code>j</code>
        /// is not in conflict with alternative
        /// <code>i</code>
        /// . The algorithm
        /// should keep going, looking for more lookahead due to the uncertainty.
        /// <p/>
        /// For simplicity, I'm doing a equality check between
        /// <code>x</code>
        /// and
        /// <code>x'</code>
        /// that lets the algorithm continue to consume lookahead longer
        /// than necessary. The reason I like the equality is of course the
        /// simplicity but also because that is the test you need to detect the
        /// alternatives that are actually in conflict.
        /// <p/>
        /// <strong>CONTINUE/STOP RULE</strong>
        /// <p/>
        /// Continue if union of resolved alternative sets from non-conflicting and
        /// conflicting alternative subsets has more than one alternative. We are
        /// uncertain about which alternative to predict.
        /// <p/>
        /// The complete set of alternatives,
        /// <code>[i for (_,i,_)]</code>
        /// , tells us which
        /// alternatives are still in the running for the amount of input we've
        /// consumed at this point. The conflicting sets let us to strip away
        /// configurations that won't lead to more states because we resolve
        /// conflicts to the configuration with a minimum alternate for the
        /// conflicting set.
        /// <p/>
        /// <strong>CASES</strong>
        /// <ul>
        /// <li>no conflicts and more than 1 alternative in set =&gt; continue</li>
        /// <li>
        /// <code>(s, 1, x)</code>
        /// ,
        /// <code>(s, 2, x)</code>
        /// ,
        /// <code>(s, 3, z)</code>
        /// ,
        /// <code>(s', 1, y)</code>
        /// ,
        /// <code>(s', 2, y)</code>
        /// yields non-conflicting set
        /// <code></code>
        /// 
        /// 3}} U conflicting sets
        /// <code></code>
        /// min(
        /// 1,2})} U
        /// <code></code>
        /// min(
        /// 1,2})} =
        /// <code></code>
        /// 
        /// 1,3}} =&gt; continue
        /// </li>
        /// <li>
        /// <code>(s, 1, x)</code>
        /// ,
        /// <code>(s, 2, x)</code>
        /// ,
        /// <code>(s', 1, y)</code>
        /// ,
        /// <code>(s', 2, y)</code>
        /// ,
        /// <code>(s'', 1, z)</code>
        /// yields non-conflicting set
        /// <code></code>
        /// 
        /// 1}} U conflicting sets
        /// <code></code>
        /// min(
        /// 1,2})} U
        /// <code></code>
        /// min(
        /// 1,2})} =
        /// <code></code>
        /// 
        /// 1}} =&gt; stop and predict 1</li>
        /// <li>
        /// <code>(s, 1, x)</code>
        /// ,
        /// <code>(s, 2, x)</code>
        /// ,
        /// <code>(s', 1, y)</code>
        /// ,
        /// <code>(s', 2, y)</code>
        /// yields conflicting, reduced sets
        /// <code></code>
        /// 
        /// 1}} U
        /// <code></code>
        /// 
        /// 1}} =
        /// <code></code>
        /// 
        /// 1}} =&gt; stop and predict 1, can announce
        /// ambiguity
        /// <code></code>
        /// 
        /// 1,2}}</li>
        /// <li>
        /// <code>(s, 1, x)</code>
        /// ,
        /// <code>(s, 2, x)</code>
        /// ,
        /// <code>(s', 2, y)</code>
        /// ,
        /// <code>(s', 3, y)</code>
        /// yields conflicting, reduced sets
        /// <code></code>
        /// 
        /// 1}} U
        /// <code></code>
        /// 
        /// 2}} =
        /// <code></code>
        /// 
        /// 1,2}} =&gt; continue</li>
        /// <li>
        /// <code>(s, 1, x)</code>
        /// ,
        /// <code>(s, 2, x)</code>
        /// ,
        /// <code>(s', 3, y)</code>
        /// ,
        /// <code>(s', 4, y)</code>
        /// yields conflicting, reduced sets
        /// <code></code>
        /// 
        /// 1}} U
        /// <code></code>
        /// 
        /// 3}} =
        /// <code></code>
        /// 
        /// 1,3}} =&gt; continue</li>
        /// </ul>
        /// <strong>EXACT AMBIGUITY DETECTION</strong>
        /// <p/>
        /// If all states report the same conflicting set of alternatives, then we
        /// know we have the exact ambiguity set.
        /// <p/>
        /// <code>|A_<em>i</em>|&gt;1</code> and
        /// <code>A_<em>i</em> = A_<em>j</em></code> for all <em>i</em>, <em>j</em>.
        /// <p/>
        /// In other words, we continue examining lookahead until all
        /// <code>A_i</code>
        /// have more than one alternative and all
        /// <code>A_i</code>
        /// are the same. If
        /// <code></code>
        /// A=
        /// {1,2}, {1,3}}}, then regular LL prediction would terminate
        /// because the resolved set is
        /// <code></code>
        /// 
        /// 1}}. To determine what the real
        /// ambiguity is, we have to know whether the ambiguity is between one and
        /// two or one and three so we keep going. We can only stop prediction when
        /// we need exact ambiguity detection when the sets look like
        /// <code></code>
        /// A=
        /// {1,2}}} or
        /// <code></code>
        /// 
        /// {1,2},{1,2}}}, etc...
        /// </remarks>
        public static int ResolvesToJustOneViableAlt(IEnumerable<BitSet> altsets)
        {
            return GetSingleViableAlt(altsets);
        }

        /// <summary>
        /// Determines if every alternative subset in
        /// <code>altsets</code>
        /// contains more
        /// than one alternative.
        /// </summary>
        /// <param name="altsets">a collection of alternative subsets</param>
        /// <returns>
        /// 
        /// <code>true</code>
        /// if every
        /// <see cref="Sharpen.BitSet">Sharpen.BitSet</see>
        /// in
        /// <code>altsets</code>
        /// has
        /// <see cref="Sharpen.BitSet.Cardinality()">cardinality</see>
        /// &gt; 1, otherwise
        /// <code>false</code>
        /// </returns>
        public static bool AllSubsetsConflict(IEnumerable<BitSet> altsets)
        {
            return !HasNonConflictingAltSet(altsets);
        }

        /// <summary>
        /// Determines if any single alternative subset in
        /// <code>altsets</code>
        /// contains
        /// exactly one alternative.
        /// </summary>
        /// <param name="altsets">a collection of alternative subsets</param>
        /// <returns>
        /// 
        /// <code>true</code>
        /// if
        /// <code>altsets</code>
        /// contains a
        /// <see cref="Sharpen.BitSet">Sharpen.BitSet</see>
        /// with
        /// <see cref="Sharpen.BitSet.Cardinality()">cardinality</see>
        /// 1, otherwise
        /// <code>false</code>
        /// </returns>
        public static bool HasNonConflictingAltSet(IEnumerable<BitSet> altsets)
        {
            foreach (BitSet alts in altsets)
            {
                if (alts.Cardinality() == 1)
                {
                    return true;
                }
            }
            return false;
        }

        /// <summary>
        /// Determines if any single alternative subset in
        /// <code>altsets</code>
        /// contains
        /// more than one alternative.
        /// </summary>
        /// <param name="altsets">a collection of alternative subsets</param>
        /// <returns>
        /// 
        /// <code>true</code>
        /// if
        /// <code>altsets</code>
        /// contains a
        /// <see cref="Sharpen.BitSet">Sharpen.BitSet</see>
        /// with
        /// <see cref="Sharpen.BitSet.Cardinality()">cardinality</see>
        /// &gt; 1, otherwise
        /// <code>false</code>
        /// </returns>
        public static bool HasConflictingAltSet(IEnumerable<BitSet> altsets)
        {
            foreach (BitSet alts in altsets)
            {
                if (alts.Cardinality() > 1)
                {
                    return true;
                }
            }
            return false;
        }

        /// <summary>
        /// Determines if every alternative subset in
        /// <code>altsets</code>
        /// is equivalent.
        /// </summary>
        /// <param name="altsets">a collection of alternative subsets</param>
        /// <returns>
        /// 
        /// <code>true</code>
        /// if every member of
        /// <code>altsets</code>
        /// is equal to the
        /// others, otherwise
        /// <code>false</code>
        /// </returns>
        public static bool AllSubsetsEqual(IEnumerable<BitSet> altsets)
        {
            IEnumerator<BitSet> it = altsets.GetEnumerator();
            it.MoveNext();
            BitSet first = it.Current;
            while (it.MoveNext())
            {
                BitSet next = it.Current;
                if (!next.Equals(first))
                {
                    return false;
                }
            }
            return true;
        }

        /// <summary>
        /// Returns the unique alternative predicted by all alternative subsets in
        /// <code>altsets</code>
        /// . If no such alternative exists, this method returns
        /// <see cref="ATN.InvalidAltNumber">ATN.InvalidAltNumber</see>
        /// .
        /// </summary>
        /// <param name="altsets">a collection of alternative subsets</param>
        public static int GetUniqueAlt(IEnumerable<BitSet> altsets)
        {
            BitSet all = GetAlts(altsets);
            if (all.Cardinality() == 1)
            {
                return all.NextSetBit(0);
            }
            return ATN.InvalidAltNumber;
        }

        /// <summary>
        /// Gets the complete set of represented alternatives for a collection of
        /// alternative subsets.
        /// </summary>
        /// <remarks>
        /// Gets the complete set of represented alternatives for a collection of
        /// alternative subsets. This method returns the union of each
        /// <see cref="Sharpen.BitSet">Sharpen.BitSet</see>
        /// in
        /// <code>altsets</code>
        /// .
        /// </remarks>
        /// <param name="altsets">a collection of alternative subsets</param>
        /// <returns>
        /// the set of represented alternatives in
        /// <code>altsets</code>
        /// </returns>
        public static BitSet GetAlts(IEnumerable<BitSet> altsets)
        {
            BitSet all = new BitSet();
            foreach (BitSet alts in altsets)
            {
                all.Or(alts);
            }
            return all;
        }

        /// <summary>This function gets the conflicting alt subsets from a configuration set.
        ///     </summary>
        /// <remarks>
        /// This function gets the conflicting alt subsets from a configuration set.
        /// For each configuration
        /// <code>c</code>
        /// in
        /// <code>configs</code>
        /// :
        /// <pre>
        /// map[c] U= c.
        /// <see cref="ATNConfig.Alt()">getAlt()</see>
        /// # map hash/equals uses s and x, not
        /// alt and not pred
        /// </pre>
        /// </remarks>
        [return: NotNull]
        public static ICollection<BitSet> GetConflictingAltSubsets(IEnumerable<ATNConfig>
             configs)
        {
            PredictionMode.AltAndContextMap configToAlts = new PredictionMode.AltAndContextMap
                ();
            foreach (ATNConfig c in configs)
            {
                BitSet alts;
                if (!configToAlts.TryGetValue(c, out alts))
                {
                    alts = new BitSet();
                    configToAlts[c] = alts;
                }
                alts.Set(c.Alt);
            }
            return configToAlts.Values;
        }

        /// <summary>Get a map from state to alt subset from a configuration set.</summary>
        /// <remarks>
        /// Get a map from state to alt subset from a configuration set. For each
        /// configuration
        /// <code>c</code>
        /// in
        /// <code>configs</code>
        /// :
        /// <pre>
        /// map[c.
        /// <see cref="ATNConfig.state">state</see>
        /// ] U= c.
        /// <see cref="ATNConfig.Alt()">getAlt()</see>
        /// </pre>
        /// </remarks>
        [return: NotNull]
        public static IDictionary<ATNState, BitSet> GetStateToAltMap(IEnumerable<ATNConfig
            > configs)
        {
            IDictionary<ATNState, BitSet> m = new Dictionary<ATNState, BitSet>();
            foreach (ATNConfig c in configs)
            {
                BitSet alts;
                if (!m.TryGetValue(c.State, out alts))
                {
                    alts = new BitSet();
                    m[c.State] = alts;
                }
                alts.Set(c.Alt);
            }
            return m;
        }

        public static bool HasStateAssociatedWithOneAlt(IEnumerable<ATNConfig> configs)
        {
            IDictionary<ATNState, BitSet> x = GetStateToAltMap(configs);
            foreach (BitSet alts in x.Values)
            {
                if (alts.Cardinality() == 1)
                {
                    return true;
                }
            }
            return false;
        }

        public static int GetSingleViableAlt(IEnumerable<BitSet> altsets)
        {
            BitSet viableAlts = new BitSet();
            foreach (BitSet alts in altsets)
            {
                int minAlt = alts.NextSetBit(0);
                viableAlts.Set(minAlt);
                if (viableAlts.Cardinality() > 1)
                {
                    // more than 1 viable alt
                    return ATN.InvalidAltNumber;
                }
            }
            return viableAlts.NextSetBit(0);
        }
    }
}
