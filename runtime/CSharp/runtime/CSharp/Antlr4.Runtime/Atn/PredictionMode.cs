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
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    /// <summary>
    /// This enumeration defines the prediction modes available in ANTLR 4 along with
    /// utility methods for analyzing configuration sets for conflicts and/or
    /// ambiguities.
    /// </summary>
    /// <remarks>
    /// This enumeration defines the prediction modes available in ANTLR 4 along with
    /// utility methods for analyzing configuration sets for conflicts and/or
    /// ambiguities.
    /// </remarks>
    [System.Serializable]
    public sealed class PredictionMode
    {
        /// <summary>The SLL(*) prediction mode.</summary>
        /// <remarks>
        /// The SLL(*) prediction mode. This prediction mode ignores the current
        /// parser context when making predictions. This is the fastest prediction
        /// mode, and provides correct results for many grammars. This prediction
        /// mode is more powerful than the prediction mode provided by ANTLR 3, but
        /// may result in syntax errors for grammar and input combinations which are
        /// not SLL.
        /// <p>
        /// When using this prediction mode, the parser will either return a correct
        /// parse tree (i.e. the same parse tree that would be returned with the
        /// <see cref="Ll"/>
        /// prediction mode), or it will report a syntax error. If a
        /// syntax error is encountered when using the
        /// <see cref="Sll"/>
        /// prediction mode,
        /// it may be due to either an actual syntax error in the input or indicate
        /// that the particular combination of grammar and input requires the more
        /// powerful
        /// <see cref="Ll"/>
        /// prediction abilities to complete successfully.</p>
        /// <p>
        /// This prediction mode does not provide any guarantees for prediction
        /// behavior for syntactically-incorrect inputs.</p>
        /// </remarks>
        public static readonly PredictionMode Sll = new PredictionMode();

        /// <summary>The LL(*) prediction mode.</summary>
        /// <remarks>
        /// The LL(*) prediction mode. This prediction mode allows the current parser
        /// context to be used for resolving SLL conflicts that occur during
        /// prediction. This is the fastest prediction mode that guarantees correct
        /// parse results for all combinations of grammars with syntactically correct
        /// inputs.
        /// <p>
        /// When using this prediction mode, the parser will make correct decisions
        /// for all syntactically-correct grammar and input combinations. However, in
        /// cases where the grammar is truly ambiguous this prediction mode might not
        /// report a precise answer for <em>exactly which</em> alternatives are
        /// ambiguous.</p>
        /// <p>
        /// This prediction mode does not provide any guarantees for prediction
        /// behavior for syntactically-incorrect inputs.</p>
        /// </remarks>
        public static readonly PredictionMode Ll = new PredictionMode();

        /// <summary>The LL(*) prediction mode with exact ambiguity detection.</summary>
        /// <remarks>
        /// The LL(*) prediction mode with exact ambiguity detection. In addition to
        /// the correctness guarantees provided by the
        /// <see cref="Ll"/>
        /// prediction mode,
        /// this prediction mode instructs the prediction algorithm to determine the
        /// complete and exact set of ambiguous alternatives for every ambiguous
        /// decision encountered while parsing.
        /// <p>
        /// This prediction mode may be used for diagnosing ambiguities during
        /// grammar development. Due to the performance overhead of calculating sets
        /// of ambiguous alternatives, this prediction mode should be avoided when
        /// the exact results are not necessary.</p>
        /// <p>
        /// This prediction mode does not provide any guarantees for prediction
        /// behavior for syntactically-incorrect inputs.</p>
        /// </remarks>
        public static readonly PredictionMode LlExactAmbigDetection = new PredictionMode();

        /// <summary>A Map that uses just the state and the stack context as the key.</summary>
        /// <remarks>A Map that uses just the state and the stack context as the key.</remarks>
        internal class AltAndContextMap : Dictionary<ATNConfig, BitSet>
        {
            public AltAndContextMap()
                : base(PredictionMode.AltAndContextConfigEqualityComparator.Instance)
            {
            }
        }

        private sealed class AltAndContextConfigEqualityComparator : EqualityComparer<ATNConfig>
        {
            public static readonly PredictionMode.AltAndContextConfigEqualityComparator Instance = new PredictionMode.AltAndContextConfigEqualityComparator();

            private AltAndContextConfigEqualityComparator()
            {
            }

            /// <summary>
            /// The hash code is only a function of the
            /// <see cref="ATNState.stateNumber"/>
            /// and
            /// <see cref="ATNConfig.Context"/>
            /// .
            /// </summary>
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
        /// <p>
        /// This method computes the SLL prediction termination condition for both of
        /// the following cases.</p>
        /// <ul>
        /// <li>The usual SLL+LL fallback upon SLL conflict</li>
        /// <li>Pure SLL without LL fallback</li>
        /// </ul>
        /// <p><strong>COMBINED SLL+LL PARSING</strong></p>
        /// <p>When LL-fallback is enabled upon SLL conflict, correct predictions are
        /// ensured regardless of how the termination condition is computed by this
        /// method. Due to the substantially higher cost of LL prediction, the
        /// prediction should only fall back to LL when the additional lookahead
        /// cannot lead to a unique SLL prediction.</p>
        /// <p>Assuming combined SLL+LL parsing, an SLL configuration set with only
        /// conflicting subsets should fall back to full LL, even if the
        /// configuration sets don't resolve to the same alternative (e.g.
        /// <c/>
        /// 
        /// 1,2}} and
        /// <c/>
        /// 
        /// 3,4}}. If there is at least one non-conflicting
        /// configuration, SLL could continue with the hopes that more lookahead will
        /// resolve via one of those non-conflicting configurations.</p>
        /// <p>Here's the prediction termination rule them: SLL (for SLL+LL parsing)
        /// stops when it sees only conflicting configuration subsets. In contrast,
        /// full LL keeps going when there is uncertainty.</p>
        /// <p><strong>HEURISTIC</strong></p>
        /// <p>As a heuristic, we stop prediction when we see any conflicting subset
        /// unless we see a state that only has one alternative associated with it.
        /// The single-alt-state thing lets prediction continue upon rules like
        /// (otherwise, it would admit defeat too soon):</p>
        /// <p>
        /// <c>[12|1|[], 6|2|[], 12|2|[]]. s : (ID | ID ID?) ';' ;</c>
        /// </p>
        /// <p>When the ATN simulation reaches the state before
        /// <c>';'</c>
        /// , it has a
        /// DFA state that looks like:
        /// <c>[12|1|[], 6|2|[], 12|2|[]]</c>
        /// . Naturally
        /// <c>12|1|[]</c>
        /// and
        /// <c>12|2|[]</c>
        /// conflict, but we cannot stop
        /// processing this node because alternative to has another way to continue,
        /// via
        /// <c>[6|2|[]]</c>
        /// .</p>
        /// <p>It also let's us continue for this rule:</p>
        /// <p>
        /// <c>[1|1|[], 1|2|[], 8|3|[]] a : A | A | A B ;</c>
        /// </p>
        /// <p>After matching input A, we reach the stop state for rule A, state 1.
        /// State 8 is the state right before B. Clearly alternatives 1 and 2
        /// conflict and no amount of further lookahead will separate the two.
        /// However, alternative 3 will be able to continue and so we do not stop
        /// working on this state. In the previous example, we're concerned with
        /// states associated with the conflicting alternatives. Here alt 3 is not
        /// associated with the conflicting configs, but since we can continue
        /// looking for input reasonably, don't declare the state done.</p>
        /// <p><strong>PURE SLL PARSING</strong></p>
        /// <p>To handle pure SLL parsing, all we have to do is make sure that we
        /// combine stack contexts for configurations that differ only by semantic
        /// predicate. From there, we can do the usual SLL termination heuristic.</p>
        /// <p><strong>PREDICATES IN SLL+LL PARSING</strong></p>
        /// <p>SLL decisions don't evaluate predicates until after they reach DFA stop
        /// states because they need to create the DFA cache that works in all
        /// semantic situations. In contrast, full LL evaluates predicates collected
        /// during start state computation so it can ignore predicates thereafter.
        /// This means that SLL termination detection can totally ignore semantic
        /// predicates.</p>
        /// <p>Implementation-wise,
        /// <see cref="ATNConfigSet"/>
        /// combines stack contexts but not
        /// semantic predicate contexts so we might see two configurations like the
        /// following.</p>
        /// <p>
        /// <c/>
        /// (s, 1, x,
        /// ), (s, 1, x', {p})}</p>
        /// <p>Before testing these configurations against others, we have to merge
        /// <c>x</c>
        /// and
        /// <c>x'</c>
        /// (without modifying the existing configurations).
        /// For example, we test
        /// <c>(x+x')==x''</c>
        /// when looking for conflicts in
        /// the following configurations.</p>
        /// <p>
        /// <c/>
        /// (s, 1, x,
        /// ), (s, 1, x', {p}), (s, 2, x'', {})}</p>
        /// <p>If the configuration set has predicates (as indicated by
        /// <see cref="ATNConfigSet.HasSemanticContext()"/>
        /// ), this algorithm makes a copy of
        /// the configurations to strip out all of the predicates so that a standard
        /// <see cref="ATNConfigSet"/>
        /// will merge everything ignoring predicates.</p>
        /// </remarks>
        public static bool HasSLLConflictTerminatingPrediction(PredictionMode mode, ATNConfigSet configs)
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
                        c.Transform(c.State, SemanticContext.None, false);
                        dup.Add(c);
                    }
                    configs = dup;
                }
            }
            // now we have combined contexts for configs with dissimilar preds
            // pure SLL or combined SLL+LL mode parsing
            ICollection<BitSet> altsets = GetConflictingAltSubsets(configs);
            bool heuristic = HasConflictingAltSet(altsets) && !HasStateAssociatedWithOneAlt(configs);
            return heuristic;
        }

        /// <summary>
        /// Checks if any configuration in
        /// <paramref name="configs"/>
        /// is in a
        /// <see cref="RuleStopState"/>
        /// . Configurations meeting this condition have reached
        /// the end of the decision rule (local context) or end of start rule (full
        /// context).
        /// </summary>
        /// <param name="configs">the configuration set to test</param>
        /// <returns>
        /// 
        /// <see langword="true"/>
        /// if any configuration in
        /// <paramref name="configs"/>
        /// is in a
        /// <see cref="RuleStopState"/>
        /// , otherwise
        /// <see langword="false"/>
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
        /// <paramref name="configs"/>
        /// are in a
        /// <see cref="RuleStopState"/>
        /// . Configurations meeting this condition have reached
        /// the end of the decision rule (local context) or end of start rule (full
        /// context).
        /// </summary>
        /// <param name="configs">the configuration set to test</param>
        /// <returns>
        /// 
        /// <see langword="true"/>
        /// if all configurations in
        /// <paramref name="configs"/>
        /// are in a
        /// <see cref="RuleStopState"/>
        /// , otherwise
        /// <see langword="false"/>
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
        /// <p>Can we stop looking ahead during ATN simulation or is there some
        /// uncertainty as to which alternative we will ultimately pick, after
        /// consuming more input? Even if there are partial conflicts, we might know
        /// that everything is going to resolve to the same minimum alternative. That
        /// means we can stop since no more lookahead will change that fact. On the
        /// other hand, there might be multiple conflicts that resolve to different
        /// minimums. That means we need more look ahead to decide which of those
        /// alternatives we should predict.</p>
        /// <p>The basic idea is to split the set of configurations
        /// <c>C</c>
        /// , into
        /// conflicting subsets
        /// <c>(s, _, ctx, _)</c>
        /// and singleton subsets with
        /// non-conflicting configurations. Two configurations conflict if they have
        /// identical
        /// <see cref="ATNConfig.State"/>
        /// and
        /// <see cref="ATNConfig.Context"/>
        /// values
        /// but different
        /// <see cref="ATNConfig.Alt"/>
        /// value, e.g.
        /// <c>(s, i, ctx, _)</c>
        /// and
        /// <c>(s, j, ctx, _)</c>
        /// for
        /// <c>i!=j</c>
        /// .</p>
        /// <p/>
        /// Reduce these configuration subsets to the set of possible alternatives.
        /// You can compute the alternative subsets in one pass as follows:
        /// <p/>
        /// <c/>
        /// A_s,ctx =
        /// i | (s, i, ctx, _)}} for each configuration in
        /// <c>C</c>
        /// holding
        /// <c>s</c>
        /// and
        /// <c>ctx</c>
        /// fixed.
        /// <p/>
        /// Or in pseudo-code, for each configuration
        /// <c>c</c>
        /// in
        /// <c>C</c>
        /// :
        /// <pre>
        /// map[c] U= c.
        /// <see cref="ATNConfig.Alt()">getAlt()</see>
        /// # map hash/equals uses s and x, not
        /// alt and not pred
        /// </pre>
        /// <p>The values in
        /// <c>map</c>
        /// are the set of
        /// <c>A_s,ctx</c>
        /// sets.</p>
        /// <p>If
        /// <c>|A_s,ctx|=1</c>
        /// then there is no conflict associated with
        /// <c>s</c>
        /// and
        /// <c>ctx</c>
        /// .</p>
        /// <p>Reduce the subsets to singletons by choosing a minimum of each subset. If
        /// the union of these alternative subsets is a singleton, then no amount of
        /// more lookahead will help us. We will always pick that alternative. If,
        /// however, there is more than one alternative, then we are uncertain which
        /// alternative to predict and must continue looking for resolution. We may
        /// or may not discover an ambiguity in the future, even if there are no
        /// conflicting subsets this round.</p>
        /// <p>The biggest sin is to terminate early because it means we've made a
        /// decision but were uncertain as to the eventual outcome. We haven't used
        /// enough lookahead. On the other hand, announcing a conflict too late is no
        /// big deal; you will still have the conflict. It's just inefficient. It
        /// might even look until the end of file.</p>
        /// <p>No special consideration for semantic predicates is required because
        /// predicates are evaluated on-the-fly for full LL prediction, ensuring that
        /// no configuration contains a semantic context during the termination
        /// check.</p>
        /// <p><strong>CONFLICTING CONFIGS</strong></p>
        /// <p>Two configurations
        /// <c>(s, i, x)</c>
        /// and
        /// <c>(s, j, x')</c>
        /// , conflict
        /// when
        /// <c>i!=j</c>
        /// but
        /// <c>x=x'</c>
        /// . Because we merge all
        /// <c>(s, i, _)</c>
        /// configurations together, that means that there are at
        /// most
        /// <c>n</c>
        /// configurations associated with state
        /// <c>s</c>
        /// for
        /// <c>n</c>
        /// possible alternatives in the decision. The merged stacks
        /// complicate the comparison of configuration contexts
        /// <c>x</c>
        /// and
        /// <c>x'</c>
        /// . Sam checks to see if one is a subset of the other by calling
        /// merge and checking to see if the merged result is either
        /// <c>x</c>
        /// or
        /// <c>x'</c>
        /// . If the
        /// <c>x</c>
        /// associated with lowest alternative
        /// <c>i</c>
        /// is the superset, then
        /// <c>i</c>
        /// is the only possible prediction since the
        /// others resolve to
        /// <c>min(i)</c>
        /// as well. However, if
        /// <c>x</c>
        /// is
        /// associated with
        /// <c>j&gt;i</c>
        /// then at least one stack configuration for
        /// <c>j</c>
        /// is not in conflict with alternative
        /// <c>i</c>
        /// . The algorithm
        /// should keep going, looking for more lookahead due to the uncertainty.</p>
        /// <p>For simplicity, I'm doing a equality check between
        /// <c>x</c>
        /// and
        /// <c>x'</c>
        /// that lets the algorithm continue to consume lookahead longer
        /// than necessary. The reason I like the equality is of course the
        /// simplicity but also because that is the test you need to detect the
        /// alternatives that are actually in conflict.</p>
        /// <p><strong>CONTINUE/STOP RULE</strong></p>
        /// <p>Continue if union of resolved alternative sets from non-conflicting and
        /// conflicting alternative subsets has more than one alternative. We are
        /// uncertain about which alternative to predict.</p>
        /// <p>The complete set of alternatives,
        /// <c>[i for (_,i,_)]</c>
        /// , tells us which
        /// alternatives are still in the running for the amount of input we've
        /// consumed at this point. The conflicting sets let us to strip away
        /// configurations that won't lead to more states because we resolve
        /// conflicts to the configuration with a minimum alternate for the
        /// conflicting set.</p>
        /// <p><strong>CASES</strong></p>
        /// <ul>
        /// <li>no conflicts and more than 1 alternative in set =&gt; continue</li>
        /// <li>
        /// <c>(s, 1, x)</c>
        /// ,
        /// <c>(s, 2, x)</c>
        /// ,
        /// <c>(s, 3, z)</c>
        /// ,
        /// <c>(s', 1, y)</c>
        /// ,
        /// <c>(s', 2, y)</c>
        /// yields non-conflicting set
        /// <c/>
        /// 
        /// 3}} U conflicting sets
        /// <c/>
        /// min(
        /// 1,2})} U
        /// <c/>
        /// min(
        /// 1,2})} =
        /// <c/>
        /// 
        /// 1,3}} =&gt; continue
        /// </li>
        /// <li>
        /// <c>(s, 1, x)</c>
        /// ,
        /// <c>(s, 2, x)</c>
        /// ,
        /// <c>(s', 1, y)</c>
        /// ,
        /// <c>(s', 2, y)</c>
        /// ,
        /// <c>(s'', 1, z)</c>
        /// yields non-conflicting set
        /// <c/>
        /// 
        /// 1}} U conflicting sets
        /// <c/>
        /// min(
        /// 1,2})} U
        /// <c/>
        /// min(
        /// 1,2})} =
        /// <c/>
        /// 
        /// 1}} =&gt; stop and predict 1</li>
        /// <li>
        /// <c>(s, 1, x)</c>
        /// ,
        /// <c>(s, 2, x)</c>
        /// ,
        /// <c>(s', 1, y)</c>
        /// ,
        /// <c>(s', 2, y)</c>
        /// yields conflicting, reduced sets
        /// <c/>
        /// 
        /// 1}} U
        /// <c/>
        /// 
        /// 1}} =
        /// <c/>
        /// 
        /// 1}} =&gt; stop and predict 1, can announce
        /// ambiguity
        /// <c/>
        /// 
        /// 1,2}}</li>
        /// <li>
        /// <c>(s, 1, x)</c>
        /// ,
        /// <c>(s, 2, x)</c>
        /// ,
        /// <c>(s', 2, y)</c>
        /// ,
        /// <c>(s', 3, y)</c>
        /// yields conflicting, reduced sets
        /// <c/>
        /// 
        /// 1}} U
        /// <c/>
        /// 
        /// 2}} =
        /// <c/>
        /// 
        /// 1,2}} =&gt; continue</li>
        /// <li>
        /// <c>(s, 1, x)</c>
        /// ,
        /// <c>(s, 2, x)</c>
        /// ,
        /// <c>(s', 3, y)</c>
        /// ,
        /// <c>(s', 4, y)</c>
        /// yields conflicting, reduced sets
        /// <c/>
        /// 
        /// 1}} U
        /// <c/>
        /// 
        /// 3}} =
        /// <c/>
        /// 
        /// 1,3}} =&gt; continue</li>
        /// </ul>
        /// <p><strong>EXACT AMBIGUITY DETECTION</strong></p>
        /// <p>If all states report the same conflicting set of alternatives, then we
        /// know we have the exact ambiguity set.</p>
        /// <p><code>|A_<em>i</em>|&gt;1</code> and
        /// <code>A_<em>i</em> = A_<em>j</em></code> for all <em>i</em>, <em>j</em>.</p>
        /// <p>In other words, we continue examining lookahead until all
        /// <c>A_i</c>
        /// have more than one alternative and all
        /// <c>A_i</c>
        /// are the same. If
        /// <c/>
        /// A=
        /// {1,2}, {1,3}}}, then regular LL prediction would terminate
        /// because the resolved set is
        /// <c/>
        /// 
        /// 1}}. To determine what the real
        /// ambiguity is, we have to know whether the ambiguity is between one and
        /// two or one and three so we keep going. We can only stop prediction when
        /// we need exact ambiguity detection when the sets look like
        /// <c/>
        /// A=
        /// {1,2}}} or
        /// <c/>
        /// 
        /// {1,2},{1,2}}}, etc...</p>
        /// </remarks>
        public static int ResolvesToJustOneViableAlt(IEnumerable<BitSet> altsets)
        {
            return GetSingleViableAlt(altsets);
        }

        /// <summary>
        /// Determines if every alternative subset in
        /// <paramref name="altsets"/>
        /// contains more
        /// than one alternative.
        /// </summary>
        /// <param name="altsets">a collection of alternative subsets</param>
        /// <returns>
        /// 
        /// <see langword="true"/>
        /// if every
        /// <see cref="Antlr4.Runtime.Sharpen.BitSet"/>
        /// in
        /// <paramref name="altsets"/>
        /// has
        /// <see cref="Antlr4.Runtime.Sharpen.BitSet.Cardinality()">cardinality</see>
        /// &gt; 1, otherwise
        /// <see langword="false"/>
        /// </returns>
        public static bool AllSubsetsConflict(IEnumerable<BitSet> altsets)
        {
            return !HasNonConflictingAltSet(altsets);
        }

        /// <summary>
        /// Determines if any single alternative subset in
        /// <paramref name="altsets"/>
        /// contains
        /// exactly one alternative.
        /// </summary>
        /// <param name="altsets">a collection of alternative subsets</param>
        /// <returns>
        /// 
        /// <see langword="true"/>
        /// if
        /// <paramref name="altsets"/>
        /// contains a
        /// <see cref="Antlr4.Runtime.Sharpen.BitSet"/>
        /// with
        /// <see cref="Antlr4.Runtime.Sharpen.BitSet.Cardinality()">cardinality</see>
        /// 1, otherwise
        /// <see langword="false"/>
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
        /// <paramref name="altsets"/>
        /// contains
        /// more than one alternative.
        /// </summary>
        /// <param name="altsets">a collection of alternative subsets</param>
        /// <returns>
        /// 
        /// <see langword="true"/>
        /// if
        /// <paramref name="altsets"/>
        /// contains a
        /// <see cref="Antlr4.Runtime.Sharpen.BitSet"/>
        /// with
        /// <see cref="Antlr4.Runtime.Sharpen.BitSet.Cardinality()">cardinality</see>
        /// &gt; 1, otherwise
        /// <see langword="false"/>
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
        /// <paramref name="altsets"/>
        /// is equivalent.
        /// </summary>
        /// <param name="altsets">a collection of alternative subsets</param>
        /// <returns>
        /// 
        /// <see langword="true"/>
        /// if every member of
        /// <paramref name="altsets"/>
        /// is equal to the
        /// others, otherwise
        /// <see langword="false"/>
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
        /// <paramref name="altsets"/>
        /// . If no such alternative exists, this method returns
        /// <see cref="ATN.InvalidAltNumber"/>
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
        /// <see cref="Antlr4.Runtime.Sharpen.BitSet"/>
        /// in
        /// <paramref name="altsets"/>
        /// .
        /// </remarks>
        /// <param name="altsets">a collection of alternative subsets</param>
        /// <returns>
        /// the set of represented alternatives in
        /// <paramref name="altsets"/>
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

        /// <summary>This function gets the conflicting alt subsets from a configuration set.</summary>
        /// <remarks>
        /// This function gets the conflicting alt subsets from a configuration set.
        /// For each configuration
        /// <c>c</c>
        /// in
        /// <paramref name="configs"/>
        /// :
        /// <pre>
        /// map[c] U= c.
        /// <see cref="ATNConfig.Alt()">getAlt()</see>
        /// # map hash/equals uses s and x, not
        /// alt and not pred
        /// </pre>
        /// </remarks>
        [return: NotNull]
        public static ICollection<BitSet> GetConflictingAltSubsets(IEnumerable<ATNConfig> configs)
        {
            PredictionMode.AltAndContextMap configToAlts = new PredictionMode.AltAndContextMap();
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
        /// <c>c</c>
        /// in
        /// <paramref name="configs"/>
        /// :
        /// <pre>
        /// map[c.
        /// <see cref="ATNConfig.State"/>
        /// ] U= c.
        /// <see cref="ATNConfig.Alt"/>
        /// </pre>
        /// </remarks>
        [return: NotNull]
        public static IDictionary<ATNState, BitSet> GetStateToAltMap(IEnumerable<ATNConfig> configs)
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
