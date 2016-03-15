#pragma once

#include <string>
#include <vector>
#include <set>
#include <iostream>
#include <mutex>

#include "ATNSimulator.h"
#include "PredictionMode.h"
#include "DFAState.h"
#include "stringconverter.h"
#include "Declarations.h"
#include "Arrays.h"
#include "ActionTransition.h"
#include "PrecedencePredicateTransition.h"
#include "PredicateTransition.h"
#include "RuleTransition.h"
#include "SingletonPredictionContext.h"
#include "AtomTransition.h"
#include "SetTransition.h"
#include "NotSetTransition.h"
#include "EmptyPredictionContext.h"
#include "CommonTokenStream.h"
#include "Parser.h"


/*
 * [The "BSD license"]
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

namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {
                namespace atn {
                    /// <summary>
                    /// The embodiment of the adaptive LL(*), ALL(*), parsing strategy.
                    /// 
                    /// The basic complexity of the adaptive strategy makes it harder to
                    /// understand. We begin with ATN simulation to build paths in a
                    /// DFA. Subsequent prediction requests go through the DFA first. If
                    /// they reach a state without an edge for the current symbol, the
                    /// algorithm fails over to the ATN simulation to complete the DFA
                    /// path for the current input (until it finds a conflict state or
                    /// uniquely predicting state).
                    /// 
                    /// All of that is done without using the outer context because we
                    /// want to create a DFA that is not dependent upon the rule
                    /// invocation stack when we do a prediction.  One DFA works in all
                    /// contexts. We avoid using context not necessarily because it's
                    /// slower, although it can be, but because of the DFA caching
                    /// problem.  The closure routine only considers the rule invocation
                    /// stack created during prediction beginning in the decision rule.  For
                    /// example, if prediction occurs without invoking another rule's
                    /// ATN, there are no context stacks in the configurations.
                    /// When lack of context leads to a conflict, we don't know if it's
                    /// an ambiguity or a weakness in the strong LL(*) parsing strategy
                    /// (versus full LL(*)).
                    /// 
                    /// When SLL yields a configuration set with conflict, we rewind the
                    /// input and retry the ATN simulation, this time using
                    /// full outer context without adding to the DFA. Configuration context
                    /// stacks will be the full invocation stacks from the start rule. If
                    /// we get a conflict using full context, then we can definitively
                    /// say we have a true ambiguity for that input sequence. If we don't
                    /// get a conflict, it implies that the decision is sensitive to the
                    /// outer context. (It is not context-sensitive in the sense of
                    /// context-sensitive grammars.)
                    /// 
                    /// The next time we reach this DFA state with an SLL conflict, through
                    /// DFA simulation, we will again retry the ATN simulation using full
                    /// context mode. This is slow because we can't save the results and have
                    /// to "interpret" the ATN each time we get that input.
                    /// 
                    /// CACHING FULL CONTEXT PREDICTIONS
                    /// 
                    /// We could cache results from full context to predicted
                    /// alternative easily and that saves a lot of time but doesn't work
                    /// in presence of predicates. The set of visible predicates from
                    /// the ATN start state changes depending on the context, because
                    /// closure can fall off the end of a rule. I tried to cache
                    /// tuples (stack context, semantic context, predicted alt) but it
                    /// was slower than interpreting and much more complicated. Also
                    /// required a huge amount of memory. The goal is not to create the
                    /// world's fastest parser anyway. I'd like to keep this algorithm
                    /// simple. By launching multiple threads, we can improve the speed
                    /// of parsing across a large number of files.
                    /// 
                    /// There is no strict ordering between the amount of input used by
                    /// SLL vs LL, which makes it really hard to build a cache for full
                    /// context. Let's say that we have input A B C that leads to an SLL
                    /// conflict with full context X.  That implies that using X we
                    /// might only use A B but we could also use A B C D to resolve
                    /// conflict.  Input A B C D could predict alternative 1 in one
                    /// position in the input and A B C E could predict alternative 2 in
                    /// another position in input.  The conflicting SLL configurations
                    /// could still be non-unique in the full context prediction, which
                    /// would lead us to requiring more input than the original A B C.	To
                    /// make a	prediction cache work, we have to track	the exact input	used
                    /// during the previous prediction. That amounts to a cache that maps X
                    /// to a specific DFA for that context.
                    /// 
                    /// Something should be done for left-recursive expression predictions.
                    /// They are likely LL(1) + pred eval. Easier to do the whole SLL unless
                    /// error and retry with full LL thing Sam does.
                    /// 
                    /// AVOIDING FULL CONTEXT PREDICTION
                    /// 
                    /// We avoid doing full context retry when the outer context is empty,
                    /// we did not dip into the outer context by falling off the end of the
                    /// decision state rule, or when we force SLL mode.
                    /// 
                    /// As an example of the not dip into outer context case, consider
                    /// as super constructor calls versus function calls. One grammar
                    /// might look like this:
                    /// 
                    /// ctorBody : '{' superCall? stat* '}' ;
                    /// 
                    /// Or, you might see something like
                    /// 
                    /// stat : superCall ';' | expression ';' | ... ;
                    /// 
                    /// In both cases I believe that no closure operations will dip into the
                    /// outer context. In the first case ctorBody in the worst case will stop
                    /// at the '}'. In the 2nd case it should stop at the ';'. Both cases
                    /// should stay within the entry rule and not dip into the outer context.
                    /// 
                    /// PREDICATES
                    /// 
                    /// Predicates are always evaluated if present in either SLL or LL both.
                    /// SLL and LL simulation deals with predicates differently. SLL collects
                    /// predicates as it performs closure operations like ANTLR v3 did. It
                    /// delays predicate evaluation until it reaches and accept state. This
                    /// allows us to cache the SLL ATN simulation whereas, if we had evaluated
                    /// predicates on-the-fly during closure, the DFA state configuration sets
                    /// would be different and we couldn't build up a suitable DFA.
                    /// 
                    /// When building a DFA accept state during ATN simulation, we evaluate
                    /// any predicates and return the sole semantically valid alternative. If
                    /// there is more than 1 alternative, we report an ambiguity. If there are
                    /// 0 alternatives, we throw an exception. Alternatives without predicates
                    /// act like they have true predicates. The simple way to think about it
                    /// is to strip away all alternatives with false predicates and choose the
                    /// minimum alternative that remains.
                    /// 
                    /// When we start in the DFA and reach an accept state that's predicated,
                    /// we test those and return the minimum semantically viable
                    /// alternative. If no alternatives are viable, we throw an exception.
                    /// 
                    /// During full LL ATN simulation, closure always evaluates predicates and
                    /// on-the-fly. This is crucial to reducing the configuration set size
                    /// during closure. It hits a landmine when parsing with the Java grammar,
                    /// for example, without this on-the-fly evaluation.
                    /// 
                    /// SHARING DFA
                    /// 
                    /// All instances of the same parser share the same decision DFAs through
                    /// a static field. Each instance gets its own ATN simulator but they
                    /// share the same decisionToDFA field. They also share a
                    /// PredictionContextCache object that makes sure that all
                    /// PredictionContext objects are shared among the DFA states. This makes
                    /// a big size difference.
                    /// 
                    /// THREAD SAFETY
                    /// 
                    /// The parser ATN simulator locks on the decisionDFA field when it adds a
                    /// new DFA object to that array. addDFAEdge locks on the DFA for the
                    /// current decision when setting the edges[] field.  addDFAState locks on
                    /// the DFA for the current decision when looking up a DFA state to see if
                    /// it already exists.  We must make sure that all requests to add DFA
                    /// states that are equivalent result in the same shared DFA object. This
                    /// is because lots of threads will be trying to update the DFA at
                    /// once. The addDFAState method also locks inside the DFA lock but this
                    /// time on the shared context cache when it rebuilds the configurations'
                    /// PredictionContext objects using cached subgraphs/nodes. No other
                    /// locking occurs, even during DFA simulation. This is safe as long as we
                    /// can guarantee that all threads referencing s.edge[t] get the same
                    /// physical target DFA state, or none.  Once into the DFA, the DFA
                    /// simulation does not reference the dfa.state map. It follows the
                    /// edges[] field to new targets.  The DFA simulator will either find
                    /// dfa.edges to be null, to be non-null and dfa.edges[t] null, or
                    /// dfa.edges[t] to be non-null. The addDFAEdge method could be racing to
                    /// set the field but in either case the DFA simulator works; if null, and
                    /// requests ATN simulation.  It could also race trying to get
                    /// dfa.edges[t], but either way it will work because it's not doing a
                    /// test and set operation.
                    /// 
                    /// Starting with SLL then failing to combined SLL/LL
                    /// 
                    /// Sam pointed out that if SLL does not give a syntax error, then there
                    /// is no point in doing full LL, which is slower. We only have to try LL
                    /// if we get a syntax error.  For maximum speed, Sam starts the parser
                    /// with pure SLL mode:
                    /// 
                    ///     parser.getInterpreter().setSLL(true);
                    /// 
                    /// and with the bail error strategy:
                    /// 
                    ///     parser.setErrorHandler(new BailErrorStrategy());
                    /// 
                    /// If it does not get a syntax error, then we're done. If it does get a
                    /// syntax error, we need to retry with the combined SLL/LL strategy.
                    /// 
                    /// The reason this works is as follows.  If there are no SLL
                    /// conflicts then the grammar is SLL for sure, at least for that
                    /// input set. If there is an SLL conflict, the full LL analysis
                    /// must yield a set of ambiguous alternatives that is no larger
                    /// than the SLL set. If the LL set is a singleton, then the grammar
                    /// is LL but not SLL. If the LL set is the same size as the SLL
                    /// set, the decision is SLL. If the LL set has size > 1, then that
                    /// decision is truly ambiguous on the current input. If the LL set
                    /// is smaller, then the SLL conflict resolution might choose an
                    /// alternative that the full LL would rule out as a possibility
                    /// based upon better context information. If that's the case, then
                    /// the SLL parse will definitely get an error because the full LL
                    /// analysis says it's not viable. If SLL conflict resolution
                    /// chooses an alternative within the LL set, them both SLL and LL
                    /// would choose the same alternative because they both choose the
                    /// minimum of multiple conflicting alternatives.
                    /// 
                    /// Let's say we have a set of SLL conflicting alternatives {1, 2, 3} and
                    /// a smaller LL set called s. If s is {2, 3}, then SLL parsing will get
                    /// an error because SLL will pursue alternative 1. If s is {1, 2} or {1,
                    /// 3} then both SLL and LL will choose the same alternative because
                    /// alternative one is the minimum of either set. If s is {2} or {3} then
                    /// SLL will get a syntax error. If s is {1} then SLL will succeed.
                    /// 
                    /// Of course, if the input is invalid, then we will get an error for sure
                    /// in both SLL and LL parsing. Erroneous input will therefore require 2
                    /// passes over the input.
                    /// 
                    /// </summary>
                    class ParserATNSimulator : public ATNSimulator {
                    public:
                        static const bool debug = false;
                        static const bool debug_list_atn_decisions = false;
                        static const bool dfa_debug = false;
                        static const bool retry_debug = false;

                    protected:
                        Parser *const parser;

                    public:
                        std::vector<dfa::DFA *> _decisionToDFA;

                        /// <summary>
                        /// SLL, LL, or LL + exact ambig detection? </summary>
                    private:
                        PredictionMode mode;
						//Mutex to manage synchronized access for multithreading in the parser atn simulator
						std::mutex mtx;

                        /// <summary>
                        /// Each prediction operation uses a cache for merge of prediction contexts.
                        ///  Don't keep around as it wastes huge amounts of memory. DoubleKeyMap
                        ///  isn't synchronized but we're ok since two threads shouldn't reuse same
                        ///  parser/atnsim object because it can only handle one input at a time.
                        ///  This maps graphs a and b to merged result c. (a,b)->c. We can avoid
                        ///  the merge if we ever see a and b again.  Note that (b,a)->c should
                        ///  also be examined during cache lookup.
                        /// </summary>
                    protected:
                        misc::DoubleKeyMap<PredictionContext*, PredictionContext*, PredictionContext*> *mergeCache;

                        // LAME globals to avoid parameters!!!!! I need these down deep in predTransition
                        TokenStream *_input;
                        int _startIndex;
                        ParserRuleContext *_outerContext;

                        /// <summary>
                        /// Testing only! </summary>
                    public:
                        ParserATNSimulator(ATN *atn, const std::vector<dfa::DFA *>& decisionToDFA, PredictionContextCache *sharedContextCache);

                        ParserATNSimulator(Parser *parser, ATN *atn, const std::vector<dfa::DFA *>& decisionToDFA, PredictionContextCache *sharedContextCache);

                        virtual void reset() override;

                        virtual int adaptivePredict(TokenStream *input, int decision, ParserRuleContext *outerContext);

                        /// <summary>
                        /// Performs ATN simulation to compute a predicted alternative based
                        ///  upon the remaining input, but also updates the DFA cache to avoid
                        ///  having to traverse the ATN again for the same input sequence.
                        /// 
                        /// There are some key conditions we're looking for after computing a new
                        /// set of ATN configs (proposed DFA state):
                        /// if the set is empty, there is no viable alternative for current symbol
                        /// does the state uniquely predict an alternative?
                        /// does the state have a conflict that would prevent us from
                        ///         putting it on the work list?
                        /// 
                        /// We also have some key operations to do:
                        /// add an edge from previous DFA state to potentially new DFA state, D,
                        ///         upon current symbol but only if adding to work list, which means in all
                        ///         cases except no viable alternative (and possibly non-greedy decisions?)
                        /// collecting predicates and adding semantic context to DFA accept states
                        /// adding rule context to context-sensitive DFA accept states
                        /// consuming an input symbol
                        /// reporting a conflict
                        /// reporting an ambiguity
                        /// reporting a context sensitivity
                        /// reporting insufficient predicates
                        /// 
                        /// cover these cases:
                        ///    dead end
                        ///    single alt
                        ///    single alt + preds
                        ///    conflict
                        ///    conflict + preds
                        /// </summary>
                    protected:
                        virtual int execATN(dfa::DFA *dfa, dfa::DFAState *s0, TokenStream *input, int startIndex, ParserRuleContext *outerContext);

                        /// <summary>
                        /// Get an existing target state for an edge in the DFA. If the target state
                        /// for the edge has not yet been computed or is otherwise not available,
                        /// this method returns {@code null}.
                        /// </summary>
                        /// <param name="previousD"> The current DFA state </param>
                        /// <param name="t"> The next input symbol </param>
                        /// <returns> The existing target DFA state for the given input symbol
                        /// {@code t}, or {@code null} if the target state for this edge is not
                        /// already cached </returns>
                        virtual dfa::DFAState *getExistingTargetState(dfa::DFAState *previousD, int t);

                        /// <summary>
                        /// Compute a target state for an edge in the DFA, and attempt to add the
                        /// computed state and corresponding edge to the DFA.
                        /// </summary>
                        /// <param name="dfa"> The DFA </param>
                        /// <param name="previousD"> The current DFA state </param>
                        /// <param name="t"> The next input symbol
                        /// </param>
                        /// <returns> The computed target DFA state for the given input symbol
                        /// {@code t}. If {@code t} does not lead to a valid DFA state, this method
                        /// returns <seealso cref="#ERROR"/>. </returns>
                        virtual dfa::DFAState *computeTargetState(dfa::DFA *dfa, dfa::DFAState *previousD, int t);

                        virtual void predicateDFAState(dfa::DFAState *dfaState, DecisionState *decisionState);

                        // comes back with reach.uniqueAlt set to a valid alt
                        virtual int execATNWithFullContext(dfa::DFA *dfa, dfa::DFAState *D, ATNConfigSet *s0, TokenStream *input, int startIndex, ParserRuleContext *outerContext); // how far we got before failing over

                        virtual ATNConfigSet *computeReachSet(ATNConfigSet *closure, int t, bool fullCtx);

                        /// <summary>
                        /// Return a configuration set containing only the configurations from
                        /// {@code configs} which are in a <seealso cref="RuleStopState"/>. If all
                        /// configurations in {@code configs} are already in a rule stop state, this
                        /// method simply returns {@code configs}.
                        /// <p/>
                        /// When {@code lookToEndOfRule} is true, this method uses
                        /// <seealso cref="ATN#nextTokens"/> for each configuration in {@code configs} which is
                        /// not already in a rule stop state to see if a rule stop state is reachable
                        /// from the configuration via epsilon-only transitions.
                        /// </summary>
                        /// <param name="configs"> the configuration set to update </param>
                        /// <param name="lookToEndOfRule"> when true, this method checks for rule stop states
                        /// reachable by epsilon-only transitions from each configuration in
                        /// {@code configs}.
                        /// </param>
                        /// <returns> {@code configs} if all configurations in {@code configs} are in a
                        /// rule stop state, otherwise return a new configuration set containing only
                        /// the configurations from {@code configs} which are in a rule stop state </returns>
                        virtual ATNConfigSet *removeAllConfigsNotInRuleStopState(ATNConfigSet *configs, bool lookToEndOfRule);

                        virtual ATNConfigSet *computeStartState(ATNState *p, RuleContext *ctx, bool fullCtx);

                        virtual ATNState *getReachableTarget(Transition *trans, int ttype);

						virtual std::vector<SemanticContext*> getPredsForAmbigAlts(antlrcpp::BitSet *ambigAlts, ATNConfigSet *configs, int nalts);

                        virtual std::vector<dfa::DFAState::PredPrediction*> getPredicatePredictions(antlrcpp::BitSet *ambigAlts, std::vector<SemanticContext*> altToPred);

                        virtual int getAltThatFinishedDecisionEntryRule(ATNConfigSet *configs);

                        /// <summary>
                        /// Look through a list of predicate/alt pairs, returning alts for the
                        ///  pairs that win. A {@code NONE} predicate indicates an alt containing an
                        ///  unpredicated config which behaves as "always true." If !complete
                        ///  then we stop at the first predicate that evaluates to true. This
                        ///  includes pairs with null predicates.
                        /// </summary>
                        virtual antlrcpp::BitSet *evalSemanticContext(std::vector<dfa::DFAState::PredPrediction*> predPredictions, ParserRuleContext *outerContext, bool complete);


                        /* TODO: If we are doing predicates, there is no point in pursuing
                        	 closure operations if we reach a DFA state that uniquely predicts
                        	 alternative. We will not be caching that DFA state and it is a
                        	 waste to pursue the closure. Might have to advance when we do
                        	 ambig detection thought :(
                        	  */

                        virtual void closure(ATNConfig *config, ATNConfigSet *configs, std::set<ATNConfig*> *closureBusy, bool collectPredicates, bool fullCtx);

                        virtual void closureCheckingStopState(ATNConfig *config, ATNConfigSet *configs, std::set<ATNConfig*> *closureBusy, bool collectPredicates, bool fullCtx, int depth);

                        /// <summary>
                        /// Do the actual work of walking epsilon edges </summary>
                        virtual void closure_(ATNConfig *config, ATNConfigSet *configs, std::set<ATNConfig*> *closureBusy, bool collectPredicates, bool fullCtx, int depth);

                    public:
                        virtual std::wstring getRuleName(int index);

                    protected:
                        virtual ATNConfig *getEpsilonTarget(ATNConfig *config, Transition *t, bool collectPredicates, bool inContext, bool fullCtx);

                        virtual ATNConfig *actionTransition(ATNConfig *config, ActionTransition *t);

                    public:
                        virtual ATNConfig *precedenceTransition(ATNConfig *config, PrecedencePredicateTransition *pt, bool collectPredicates, bool inContext, bool fullCtx);

                    protected:
                        virtual ATNConfig *predTransition(ATNConfig *config, PredicateTransition *pt, bool collectPredicates, bool inContext, bool fullCtx);

                        virtual ATNConfig *ruleTransition(ATNConfig *config, RuleTransition *t);

                        virtual antlrcpp::BitSet getConflictingAlts(ATNConfigSet *configs);

                        /// <summary>
                        /// Sam pointed out a problem with the previous definition, v3, of
                        /// ambiguous states. If we have another state associated with conflicting
                        /// alternatives, we should keep going. For example, the following grammar
                        /// 
                        /// s : (ID | ID ID?) ';' ;
                        /// 
                        /// When the ATN simulation reaches the state before ';', it has a DFA
                        /// state that looks like: [12|1|[], 6|2|[], 12|2|[]]. Naturally
                        /// 12|1|[] and 12|2|[] conflict, but we cannot stop processing this node
                        /// because alternative to has another way to continue, via [6|2|[]].
                        /// The key is that we have a single state that has config's only associated
                        /// with a single alternative, 2, and crucially the state transitions
                        /// among the configurations are all non-epsilon transitions. That means
                        /// we don't consider any conflicts that include alternative 2. So, we
                        /// ignore the conflict between alts 1 and 2. We ignore a set of
                        /// conflicting alts when there is an intersection with an alternative
                        /// associated with a single alt state in the state->config-list map.
                        /// 
                        /// It's also the case that we might have two conflicting configurations but
                        /// also a 3rd nonconflicting configuration for a different alternative:
                        /// [1|1|[], 1|2|[], 8|3|[]]. This can come about from grammar:
                        /// 
                        /// a : A | A | A B ;
                        /// 
                        /// After matching input A, we reach the stop state for rule A, state 1.
                        /// State 8 is the state right before B. Clearly alternatives 1 and 2
                        /// conflict and no amount of further lookahead will separate the two.
                        /// However, alternative 3 will be able to continue and so we do not
                        /// stop working on this state. In the previous example, we're concerned
                        /// with states associated with the conflicting alternatives. Here alt
                        /// 3 is not associated with the conflicting configs, but since we can continue
                        /// looking for input reasonably, I don't declare the state done. We
                        /// ignore a set of conflicting alts when we have an alternative
                        /// that we still need to pursue.
                        /// </summary>

                        virtual antlrcpp::BitSet getConflictingAltsOrUniqueAlt(ATNConfigSet *configs);

                    public:
                        virtual std::wstring getTokenName(int t);

                        virtual std::wstring getLookaheadName(TokenStream *input);

                        /// <summary>
                        /// Used for debugging in adaptivePredict around execATN but I cut
                        ///  it out for clarity now that alg. works well. We can leave this
                        ///  "dead" code for a bit.
                        /// </summary>
                        virtual void dumpDeadEndConfigs(NoViableAltException *nvae);

                    protected:
                        virtual NoViableAltException *noViableAlt(TokenStream *input, ParserRuleContext *outerContext, ATNConfigSet *configs, int startIndex);

                        static int getUniqueAlt(ATNConfigSet *configs);

                        /// <summary>
                        /// Add an edge to the DFA, if possible. This method calls
                        /// <seealso cref="#addDFAState"/> to ensure the {@code to} state is present in the
                        /// DFA. If {@code from} is {@code null}, or if {@code t} is outside the
                        /// range of edges that can be represented in the DFA tables, this method
                        /// returns without adding the edge to the DFA.
                        /// <p/>
                        /// If {@code to} is {@code null}, this method returns {@code null}.
                        /// Otherwise, this method returns the <seealso cref="DFAState"/> returned by calling
                        /// <seealso cref="#addDFAState"/> for the {@code to} state.
                        /// </summary>
                        /// <param name="dfa"> The DFA </param>
                        /// <param name="from"> The source state for the edge </param>
                        /// <param name="t"> The input symbol </param>
                        /// <param name="to"> The target state for the edge
                        /// </param>
                        /// <returns> If {@code to} is {@code null}, this method returns {@code null};
                        /// otherwise this method returns the result of calling <seealso cref="#addDFAState"/>
                        /// on {@code to} </returns>
                        virtual dfa::DFAState *addDFAEdge(dfa::DFA *dfa, dfa::DFAState *from, int t, dfa::DFAState *to);

                        /// <summary>
                        /// Add state {@code D} to the DFA if it is not already present, and return
                        /// the actual instance stored in the DFA. If a state equivalent to {@code D}
                        /// is already in the DFA, the existing state is returned. Otherwise this
                        /// method returns {@code D} after adding it to the DFA.
                        /// <p/>
                        /// If {@code D} is <seealso cref="#ERROR"/>, this method returns <seealso cref="#ERROR"/> and
                        /// does not change the DFA.
                        /// </summary>
                        /// <param name="dfa"> The dfa </param>
                        /// <param name="D"> The DFA state to add </param>
                        /// <returns> The state stored in the DFA. This will be either the existing
                        /// state if {@code D} is already in the DFA, or {@code D} itself if the
                        /// state was not already present. </returns>
                        virtual dfa::DFAState *addDFAState(dfa::DFA *dfa, dfa::DFAState *D);

                        virtual void reportAttemptingFullContext(dfa::DFA *dfa, antlrcpp::BitSet *conflictingAlts, ATNConfigSet *configs, int startIndex, int stopIndex);

                        virtual void reportContextSensitivity(dfa::DFA *dfa, int prediction, ATNConfigSet *configs, int startIndex, int stopIndex);

                        /// <summary>
                        /// If context sensitive parsing, we know it's ambiguity not conflict </summary>
                        virtual void reportAmbiguity(dfa::DFA *dfa, dfa::DFAState *D, int startIndex, int stopIndex, bool exact, antlrcpp::BitSet *ambigAlts, ATNConfigSet *configs);

                    public:
                        void setPredictionMode(PredictionMode mode);

                        PredictionMode getPredictionMode();

                    private:
                        void InitializeInstanceFields();
                    };

                }
            }
        }
    }
}
