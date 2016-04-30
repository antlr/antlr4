/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Dan McLaughlin
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

#pragma once

#include "BitSet.h"

namespace org {
namespace antlr {
namespace v4 {
namespace runtime {
namespace atn {

  enum class PredictionMode {
    /// <summary>
    /// Do only local context prediction (SLL style) and using
    ///  heuristic which almost always works but is much faster
    ///  than precise answer.
    /// </summary>
    SLL,

    /// <summary>
    /// Full LL(*) that always gets right answer. For speed
    ///  reasons, we terminate the prediction process when we know for
    ///  sure which alt to predict. We don't always know what
    ///  the ambiguity is in this mode.
    /// </summary>
    LL,

    /// <summary>
    /// Tell the full LL prediction algorithm to pursue lookahead until
    ///  it has uniquely predicted an alternative without conflict or it's
    ///  certain that it's found an ambiguous input sequence.  when this
    ///  variable is false. When true, the prediction process will
    ///  continue looking for the exact ambiguous sequence even if
    ///  it has already figured out which alternative to predict.
    /// </summary>
    LL_EXACT_AMBIG_DETECTION
  };

  class PredictionModeClass {

  public:
    /// <summary>
    /// Computes the SLL prediction termination condition.
    ///
    /// <p/>
    ///
    /// This method computes the SLL prediction termination condition for both of
    /// the following cases.
    ///
    /// <ul>
    /// <li>The usual SLL+LL fallback upon SLL conflict</li>
    /// <li>Pure SLL without LL fallback</li>
    /// </ul>
    ///
    /// <p/>
    ///
    /// <strong>COMBINED SLL+LL PARSING</strong>
    ///
    /// <p/>
    ///
    /// When LL-fallback is enabled upon SLL conflict, correct predictions are
    /// ensured regardless of how the termination condition is computed by this
    /// method. Due to the substantially higher cost of LL prediction, the
    /// prediction should only fall back to LL when the additional lookahead
    /// cannot lead to a unique SLL prediction.
    ///
    /// <p/>
    ///
    /// Assuming combined SLL+LL parsing, an SLL configuration set with only
    /// conflicting subsets should fall back to full LL, even if the
    /// configuration sets don't resolve to the same alternative (e.g.
    /// {@code {1,2}} and {@code {3,4}}. If there is at least one non-conflicting
    /// configuration, SLL could continue with the hopes that more lookahead will
    /// resolve via one of those non-conflicting configurations.
    ///
    /// <p/>
    ///
    /// Here's the prediction termination rule them: SLL (for SLL+LL parsing)
    /// stops when it sees only conflicting configuration subsets. In contrast,
    /// full LL keeps going when there is uncertainty.
    ///
    /// <p/>
    ///
    /// <strong>HEURISTIC</strong>
    ///
    /// <p/>
    ///
    /// As a heuristic, we stop prediction when we see any conflicting subset
    /// unless we see a state that only has one alternative associated with it.
    /// The single-alt-state thing lets prediction continue upon rules like
    /// (otherwise, it would admit defeat too soon):
    ///
    /// <p/>
    ///
    /// {@code [12|1|[], 6|2|[], 12|2|[]]. s : (ID | ID ID?) ';' ;}
    ///
    /// <p/>
    ///
    /// When the ATN simulation reaches the state before {@code ';'}, it has a
    /// DFA state that looks like: {@code [12|1|[], 6|2|[], 12|2|[]]}. Naturally
    /// {@code 12|1|[]} and {@code 12|2|[]} conflict, but we cannot stop
    /// processing this node because alternative to has another way to continue,
    /// via {@code [6|2|[]]}.
    ///
    /// <p/>
    ///
    /// It also let's us continue for this rule:
    ///
    /// <p/>
    ///
    /// {@code [1|1|[], 1|2|[], 8|3|[]] a : A | A | A B ;}
    ///
    /// <p/>
    ///
    /// After matching input A, we reach the stop state for rule A, state 1.
    /// State 8 is the state right before B. Clearly alternatives 1 and 2
    /// conflict and no amount of further lookahead will separate the two.
    /// However, alternative 3 will be able to continue and so we do not stop
    /// working on this state. In the previous example, we're concerned with
    /// states associated with the conflicting alternatives. Here alt 3 is not
    /// associated with the conflicting configs, but since we can continue
    /// looking for input reasonably, don't declare the state done.
    ///
    /// <p/>
    ///
    /// <strong>PURE SLL PARSING</strong>
    ///
    /// <p/>
    ///
    /// To handle pure SLL parsing, all we have to do is make sure that we
    /// combine stack contexts for configurations that differ only by semantic
    /// predicate. From there, we can do the usual SLL termination heuristic.
    ///
    /// <p/>
    ///
    /// <strong>PREDICATES IN SLL+LL PARSING</strong>
    ///
    /// <p/>
    ///
    /// SLL decisions don't evaluate predicates until after they reach DFA stop
    /// states because they need to create the DFA cache that works in all
    /// semantic situations. In contrast, full LL evaluates predicates collected
    /// during start state computation so it can ignore predicates thereafter.
    /// This means that SLL termination detection can totally ignore semantic
    /// predicates.
    ///
    /// <p/>
    ///
    /// Implementation-wise, <seealso cref="ATNConfigSet"/> combines stack contexts
    /// but not
    /// semantic predicate contexts so we might see two configurations like the
    /// following.
    ///
    /// <p/>
    ///
    /// {@code (s, 1, x, {}), (s, 1, x', {p})}
    ///
    /// <p/>
    ///
    /// Before testing these configurations against others, we have to merge
    /// {@code x} and {@code x'} (without modifying the existing configurations).
    /// For example, we test {@code (x+x')==x''} when looking for conflicts in
    /// the following configurations.
    ///
    /// <p/>
    ///
    /// {@code (s, 1, x, {}), (s, 1, x', {p}), (s, 2, x'', {})}
    ///
    /// <p/>
    ///
    /// If the configuration set has predicates (as indicated by
    /// <seealso cref="ATNConfigSet#hasSemanticContext"/>), this algorithm makes a
    /// copy of
    /// the configurations to strip out all of the predicates so that a standard
    /// <seealso cref="ATNConfigSet"/> will merge everything ignoring predicates.
    /// </summary>
    static bool hasSLLConflictTerminatingPrediction(PredictionMode *mode, Ref<ATNConfigSet> configs);

    /// <summary>
    /// Checks if any configuration in {@code configs} is in a
    /// <seealso cref="RuleStopState"/>. Configurations meeting this condition have
    /// reached
    /// the end of the decision rule (local context) or end of start rule (full
    /// context).
    /// </summary>
    /// <param name="configs"> the configuration set to test </param>
    /// <returns> {@code true} if any configuration in {@code configs} is in a
    /// <seealso cref="RuleStopState"/>, otherwise {@code false} </returns>
    static bool hasConfigInRuleStopState(Ref<ATNConfigSet> configs);

    /// <summary>
    /// Checks if all configurations in {@code configs} are in a
    /// <seealso cref="RuleStopState"/>. Configurations meeting this condition have
    /// reached
    /// the end of the decision rule (local context) or end of start rule (full
    /// context).
    /// </summary>
    /// <param name="configs"> the configuration set to test </param>
    /// <returns> {@code true} if all configurations in {@code configs} are in a
    /// <seealso cref="RuleStopState"/>, otherwise {@code false} </returns>
    static bool allConfigsInRuleStopStates(Ref<ATNConfigSet> configs);

    /// <summary>
    /// Full LL prediction termination.
    ///
    /// <p/>
    ///
    /// Can we stop looking ahead during ATN simulation or is there some
    /// uncertainty as to which alternative we will ultimately pick, after
    /// consuming more input? Even if there are partial conflicts, we might know
    /// that everything is going to resolve to the same minimum alternative. That
    /// means we can stop since no more lookahead will change that fact. On the
    /// other hand, there might be multiple conflicts that resolve to different
    /// minimums. That means we need more look ahead to decide which of those
    /// alternatives we should predict.
    ///
    /// <p/>
    ///
    /// The basic idea is to split the set of configurations {@code C}, into
    /// conflicting subsets {@code (s, _, ctx, _)} and singleton subsets with
    /// non-conflicting configurations. Two configurations conflict if they have
    /// identical <seealso cref="ATNConfig#state"/> and <seealso
    /// cref="ATNConfig#context"/> values
    /// but different <seealso cref="ATNConfig#alt"/> value, e.g. {@code (s, i, ctx,
    /// _)}
    /// and {@code (s, j, ctx, _)} for {@code i!=j}.
    ///
    /// <p/>
    ///
    /// Reduce these configuration subsets to the set of possible alternatives.
    /// You can compute the alternative subsets in one pass as follows:
    ///
    /// <p/>
    ///
    /// {@code A_s,ctx = {i | (s, i, ctx, _)}} for each configuration in
    /// {@code C} holding {@code s} and {@code ctx} fixed.
    ///
    /// <p/>
    ///
    /// Or in pseudo-code, for each configuration {@code c} in {@code C}:
    ///
    /// <pre>
    /// map[c] U= c.<seealso cref="ATNConfig#alt alt"/> # map hash/equals uses s and
    /// x, not
    /// alt and not pred
    /// </pre>
    ///
    /// <p/>
    ///
    /// The values in {@code map} are the set of {@code A_s,ctx} sets.
    ///
    /// <p/>
    ///
    /// If {@code |A_s,ctx|=1} then there is no conflict associated with
    /// {@code s} and {@code ctx}.
    ///
    /// <p/>
    ///
    /// Reduce the subsets to singletons by choosing a minimum of each subset. If
    /// the union of these alternative subsets is a singleton, then no amount of
    /// more lookahead will help us. We will always pick that alternative. If,
    /// however, there is more than one alternative, then we are uncertain which
    /// alternative to predict and must continue looking for resolution. We may
    /// or may not discover an ambiguity in the future, even if there are no
    /// conflicting subsets this round.
    ///
    /// <p/>
    ///
    /// The biggest sin is to terminate early because it means we've made a
    /// decision but were uncertain as to the eventual outcome. We haven't used
    /// enough lookahead. On the other hand, announcing a conflict too late is no
    /// big deal; you will still have the conflict. It's just inefficient. It
    /// might even look until the end of file.
    ///
    /// <p/>
    ///
    /// No special consideration for semantic predicates is required because
    /// predicates are evaluated on-the-fly for full LL prediction, ensuring that
    /// no configuration contains a semantic context during the termination
    /// check.
    ///
    /// <p/>
    ///
    /// <strong>CONFLICTING CONFIGS</strong>
    ///
    /// <p/>
    ///
    /// Two configurations {@code (s, i, x)} and {@code (s, j, x')}, conflict
    /// when {@code i!=j} but {@code x=x'}. Because we merge all
    /// {@code (s, i, _)} configurations together, that means that there are at
    /// most {@code n} configurations associated with state {@code s} for
    /// {@code n} possible alternatives in the decision. The merged stacks
    /// complicate the comparison of configuration contexts {@code x} and
    /// {@code x'}. Sam checks to see if one is a subset of the other by calling
    /// merge and checking to see if the merged result is either {@code x} or
    /// {@code x'}. If the {@code x} associated with lowest alternative {@code i}
    /// is the superset, then {@code i} is the only possible prediction since the
    /// others resolve to {@code min(i)} as well. However, if {@code x} is
    /// associated with {@code j>i} then at least one stack configuration for
    /// {@code j} is not in conflict with alternative {@code i}. The algorithm
    /// should keep going, looking for more lookahead due to the uncertainty.
    ///
    /// <p/>
    ///
    /// For simplicity, I'm doing a equality check between {@code x} and
    /// {@code x'} that lets the algorithm continue to consume lookahead longer
    /// than necessary. The reason I like the equality is of course the
    /// simplicity but also because that is the test you need to detect the
    /// alternatives that are actually in conflict.
    ///
    /// <p/>
    ///
    /// <strong>CONTINUE/STOP RULE</strong>
    ///
    /// <p/>
    ///
    /// Continue if union of resolved alternative sets from non-conflicting and
    /// conflicting alternative subsets has more than one alternative. We are
    /// uncertain about which alternative to predict.
    ///
    /// <p/>
    ///
    /// The complete set of alternatives, {@code [i for (_,i,_)]}, tells us which
    /// alternatives are still in the running for the amount of input we've
    /// consumed at this point. The conflicting sets let us to strip away
    /// configurations that won't lead to more states because we resolve
    /// conflicts to the configuration with a minimum alternate for the
    /// conflicting set.
    ///
    /// <p/>
    ///
    /// <strong>CASES</strong>
    ///
    /// <ul>
    ///
    /// <li>no conflicts and more than 1 alternative in set =&gt; continue</li>
    ///
    /// <li> {@code (s, 1, x)}, {@code (s, 2, x)}, {@code (s, 3, z)},
    /// {@code (s', 1, y)}, {@code (s', 2, y)} yields non-conflicting set
    /// {@code {3}} U conflicting sets {@code min({1,2})} U {@code min({1,2})} =
    /// {@code {1,3}} =&gt; continue
    /// </li>
    ///
    /// <li>{@code (s, 1, x)}, {@code (s, 2, x)}, {@code (s', 1, y)},
    /// {@code (s', 2, y)}, {@code (s'', 1, z)} yields non-conflicting set
    /// {@code {1}} U conflicting sets {@code min({1,2})} U {@code min({1,2})} =
    /// {@code {1}} =&gt; stop and predict 1</li>
    ///
    /// <li>{@code (s, 1, x)}, {@code (s, 2, x)}, {@code (s', 1, y)},
    /// {@code (s', 2, y)} yields conflicting, reduced sets {@code {1}} U
    /// {@code {1}} = {@code {1}} =&gt; stop and predict 1, can announce
    /// ambiguity {@code {1,2}}</li>
    ///
    /// <li>{@code (s, 1, x)}, {@code (s, 2, x)}, {@code (s', 2, y)},
    /// {@code (s', 3, y)} yields conflicting, reduced sets {@code {1}} U
    /// {@code {2}} = {@code {1,2}} =&gt; continue</li>
    ///
    /// <li>{@code (s, 1, x)}, {@code (s, 2, x)}, {@code (s', 3, y)},
    /// {@code (s', 4, y)} yields conflicting, reduced sets {@code {1}} U
    /// {@code {3}} = {@code {1,3}} =&gt; continue</li>
    ///
    /// </ul>
    ///
    /// <strong>EXACT AMBIGUITY DETECTION</strong>
    ///
    /// <p/>
    ///
    /// If all states report the same conflicting set of alternatives, then we
    /// know we have the exact ambiguity set.
    ///
    /// <p/>
    ///
    /// <code>|A_<em>i</em>|&gt;1</code> and
    /// <code>A_<em>i</em> = A_<em>j</em></code> for all <em>i</em>, <em>j</em>.
    ///
    /// <p/>
    ///
    /// In other words, we continue examining lookahead until all {@code A_i}
    /// have more than one alternative and all {@code A_i} are the same. If
    /// {@code A={{1,2}, {1,3}}}, then regular LL prediction would terminate
    /// because the resolved set is {@code {1}}. To determine what the real
    /// ambiguity is, we have to know whether the ambiguity is between one and
    /// two or one and three so we keep going. We can only stop prediction when
    /// we need exact ambiguity detection when the sets look like
    /// {@code A={{1,2}}} or {@code {{1,2},{1,2}}}, etc...
    /// </summary>
    static int resolvesToJustOneViableAlt(const std::vector<antlrcpp::BitSet> &altsets);

    /// <summary>
    /// Determines if every alternative subset in {@code altsets} contains more
    /// than one alternative.
    /// </summary>
    /// <param name="altsets"> a collection of alternative subsets </param>
    /// <returns> {@code true} if every <seealso cref="BitSet"/> in {@code altsets}
    /// has
    /// <seealso cref="BitSet#cardinality cardinality"/> &gt; 1, otherwise {@code
    /// false} </returns>
    static bool allSubsetsConflict(const std::vector<antlrcpp::BitSet> &altsets);

    /// <summary>
    /// Determines if any single alternative subset in {@code altsets} contains
    /// exactly one alternative.
    /// </summary>
    /// <param name="altsets"> a collection of alternative subsets </param>
    /// <returns> {@code true} if {@code altsets} contains a <seealso
    /// cref="BitSet"/> with
    /// <seealso cref="BitSet#cardinality cardinality"/> 1, otherwise {@code false}
    /// </returns>
    static bool hasNonConflictingAltSet(const std::vector<antlrcpp::BitSet> &altsets);

    /// <summary>
    /// Determines if any single alternative subset in {@code altsets} contains
    /// more than one alternative.
    /// </summary>
    /// <param name="altsets"> a collection of alternative subsets </param>
    /// <returns> {@code true} if {@code altsets} contains a <seealso
    /// cref="BitSet"/> with
    /// <seealso cref="BitSet#cardinality cardinality"/> &gt; 1, otherwise {@code
    /// false} </returns>
    static bool hasConflictingAltSet(const std::vector<antlrcpp::BitSet> &altsets);

    /// <summary>
    /// Determines if every alternative subset in {@code altsets} is equivalent.
    /// </summary>
    /// <param name="altsets"> a collection of alternative subsets </param>
    /// <returns> {@code true} if every member of {@code altsets} is equal to the
    /// others, otherwise {@code false} </returns>
    static bool allSubsetsEqual(const std::vector<antlrcpp::BitSet> &altsets);

    /// <summary>
    /// Returns the unique alternative predicted by all alternative subsets in
    /// {@code altsets}. If no such alternative exists, this method returns
    /// <seealso cref="ATN#INVALID_ALT_NUMBER"/>.
    /// </summary>
    /// <param name="altsets"> a collection of alternative subsets </param>
    static int getUniqueAlt(const std::vector<antlrcpp::BitSet> &altsets);

    /// <summary>
    /// Gets the complete set of represented alternatives for a collection of
    /// alternative subsets. This method returns the union of each <seealso
    /// cref="BitSet"/>
    /// in {@code altsets}.
    /// </summary>
    /// <param name="altsets"> a collection of alternative subsets </param>
    /// <returns> the set of represented alternatives in {@code altsets} </returns>
    static antlrcpp::BitSet getAlts(const std::vector<antlrcpp::BitSet> &altsets);

    /// <summary>
    /// This function gets the conflicting alt subsets from a configuration set.
    /// For each configuration {@code c} in {@code configs}:
    ///
    /// <pre>
    /// map[c] U= c.<seealso cref="ATNConfig#alt alt"/> # map hash/equals uses s and
    /// x, not
    /// alt and not pred
    /// </pre>
    /// </summary>
    static std::vector<antlrcpp::BitSet> getConflictingAltSubsets(Ref<ATNConfigSet> configs);

    /// <summary>
    /// Get a map from state to alt subset from a configuration set. For each
    /// configuration {@code c} in {@code configs}:
    ///
    /// <pre>
    /// map[c.<seealso cref="ATNConfig#state state"/>] U= c.<seealso
    /// cref="ATNConfig#alt alt"/>
    /// </pre>
    /// </summary>
    static std::map<ATNState*, antlrcpp::BitSet> getStateToAltMap(Ref<ATNConfigSet> configs);

    static bool hasStateAssociatedWithOneAlt(Ref<ATNConfigSet> configs);

    static int getSingleViableAlt(const std::vector<antlrcpp::BitSet> &altsets);
  };

} // namespace atn
} // namespace runtime
} // namespace v4
} // namespace antlr
} // namespace org
