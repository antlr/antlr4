package org.antlr.v4.runtime;

import org.antlr.v4.runtime.atn.ATNConfig;
import org.antlr.v4.runtime.atn.DecisionState;
import org.antlr.v4.runtime.atn.SemanticContext;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.misc.OrderedHashSet;

/** The interface for defining strategies to deal with syntax errors
 *  encountered during a parse by ANTLR-generated parsers and tree parsers.
 *  We distinguish between three different kinds of errors:
 *
 *  	o The parser could not figure out which path to take in the ATN
 *        (none of the available alternatives could possibly match)
 *      o The current input does not match what we were looking for.
 *      o A predicate evaluated to false.
 *
 *  The default implementation of this interface reports errors to any
 *  error listeners of the parser. It also handles single token insertion
 *  and deletion for mismatched elements.
 *
 *  We pass in the parser to each function so that the same strategy
 *  can be shared between multiple parsers running at the same time.
 *  This is just for flexibility, not that we need it for the default system.
 *
 *  TODO: To bail out upon first error, simply rethrow e?
 *
 *  TODO: what to do about lexers
 */
public interface ANTLRErrorStrategy {
	/** Report any kind of RecognitionException. */
	void reportError(@NotNull BaseRecognizer recognizer,
					 @Nullable RecognitionException e)
		throws RecognitionException;

	/** When matching elements within alternative, use this method
	 *  to recover. The default implementation uses single token
	 *  insertion and deletion. If you want to change the way ANTLR
	 *  response to mismatched element errors within an alternative,
	 *  implement this method.
	 *
	 *  From the recognizer, we can get the input stream to get
	 *  the current input symbol and we can get the current context.
	 *  That context gives us the current state within the ATN.
	 *  From that state, we can look at its transition to figure out
	 *  what was expected.
	 *
	 *  Because we can recover from a single token deletions by
	 *  "inserting" tokens, we need to specify what that implicitly created
	 *  token is. We use object, because it could be a tree node.
	 */
	Token recoverInline(@NotNull BaseRecognizer recognizer)
		throws RecognitionException;

	/** Resynchronize the parser by consuming tokens until we find one
	 *  in the resynchronization set--loosely the set of tokens that can follow
	 *  the current rule. The exception contains info you might want to
	 *  use to recover better.
	 */
	void recover(@NotNull BaseRecognizer recognizer,
                 @Nullable RecognitionException e);

	/** Make sure that the current lookahead symbol is consistent with
	 *  what were expecting at this point in the ATN. You can call this
	 *  anytime but ANTLR only generates code to check before subrules/loops
	 *  and each iteration.
	 *
	 *  Implements Jim Idle's magic sync mechanism in closures and optional
	 *  subrules. E.g.,
	 *
	 * 		a : sync ( stuff sync )* ;
	 * 		sync : {consume to what can follow sync} ;
	 *
	 *  Previous versions of ANTLR did a poor job of their recovery within
	 *  loops. A single mismatch token or missing token would force the parser
	 *  to bail out of the entire rules surrounding the loop. So, for rule
	 *
	 *  classDef : 'class' ID '{' member* '}'
	 *
	 *  input with an extra token between members would force the parser to
	 *  consume until it found the next class definition rather than the
	 *  next member definition of the current class.
	 *
	 *  This functionality cost a little bit of effort because the parser
	 *  has to compare token set at the start of the loop and at each
	 *  iteration. If for some reason speed is suffering for you, you can
	 *  turn off this functionality by simply overriding this method as
	 *  a blank { }.
	 */
	void sync(@NotNull BaseRecognizer recognizer);

	/** Notify handler that parser has entered an error state.  The
	 *  parser currently doesn't call this--the handler itself calls this
	 *  in report error methods.  But, for symmetry with endErrorCondition,
	 *  this method is in the interface.
	 */
	void beginErrorCondition(@NotNull BaseRecognizer recognizer);

	/** Is the parser in the process of recovering from an error? Upon
	 *  a syntax error, the parser enters recovery mode and stays there until
	 *  the next successful match of a token. In this way, we can
	 *  avoid sending out spurious error messages. We only want one error
	 *  message per syntax error
	 */
	boolean inErrorRecoveryMode(@NotNull BaseRecognizer recognizer);

	/** Reset the error handler. Call this when the parser
	 *  matches a valid token (indicating no longer in recovery mode)
     *  and from its own reset method.
     */
    void endErrorCondition(@NotNull BaseRecognizer recognizer);

    /** Called when the parser detects a true ambiguity: an input sequence can be matched
     * literally by two or more pass through the grammar. ANTLR resolves the ambiguity in
     * favor of the alternative appearing first in the grammar. The start and stop index are
     * zero-based absolute indices into the token stream. ambigAlts is a set of alternative numbers
     * that can match the input sequence. This method is only called when we are parsing with
     * full context.
     */
    void reportAmbiguity(@NotNull BaseRecognizer recognizer,
						 DFA dfa, int startIndex, int stopIndex, @NotNull IntervalSet ambigAlts,
						 @NotNull OrderedHashSet<ATNConfig> configs);

    /** Called by the parser when it detects an input sequence that can be matched by two paths
     *  through the grammar. The difference between this and the reportAmbiguity method lies in
     *  the difference between Strong LL parsing and LL parsing. If we are not parsing with context,
     *  we can't be sure if a conflict is an ambiguity or simply a weakness in the Strong LL parsing
     *  strategy. If we are parsing with full context, this method is never called.
     */
//    void reportConflict(@NotNull BaseRecognizer recognizer,
//                        int startIndex, int stopIndex, @NotNull IntervalSet ambigAlts,
//                        @NotNull OrderedHashSet<ATNConfig> configs);


	void reportAttemptingFullContext(@NotNull BaseRecognizer recognizer,
									 @NotNull DFA dfa,
									 int startIndex, int stopIndex,
									 @NotNull OrderedHashSet<ATNConfig> configs);

	/** Called by the parser when it find a conflict that is resolved by retrying the parse
     *  with full context. This is not a warning; it simply notifies you that your grammar
     *  is more complicated than Strong LL can handle. The parser moved up to full context
     *  parsing for that input sequence.
     */
    void reportContextSensitivity(@NotNull BaseRecognizer recognizer,
                                  @NotNull DFA dfa,
                                  int startIndex, int stopIndex,
                                  @NotNull OrderedHashSet<ATNConfig> configs);

    /** Called by the parser when it finds less than n-1 predicates for n ambiguous alternatives.
     *  If there are n-1, we assume that the missing predicate is !(the "or" of the other predicates).
     *  If there are fewer than n-1, then we don't know which make it alternative to protect
     *  if the predicates fail.
     */
    void reportInsufficientPredicates(@NotNull BaseRecognizer recognizer,
									  @NotNull DFA dfa,
									  int startIndex, int stopIndex, @NotNull IntervalSet ambigAlts,
									  DecisionState decState,
									  @NotNull SemanticContext[] altToPred,
									  @NotNull OrderedHashSet<ATNConfig> configs, boolean fullContextParse);
}
