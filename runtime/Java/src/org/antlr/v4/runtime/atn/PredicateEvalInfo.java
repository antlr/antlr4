/*
 * [The "BSD license"]
 *  Copyright (c) 2014 Terence Parr
 *  Copyright (c) 2014 Sam Harwell
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

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.dfa.DFAState;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.BitSet;

/**
 * This class represents profiling event information for semantic predicate
 * evaluations which occur during prediction.
 *
 * @see ParserATNSimulator#evalSemanticContext
 */
public class PredicateEvalInfo extends DecisionEventInfo {
	/**
	 * The results of evaluating specific semantic contexts. The elements of
	 * this array correspond to the elements in {@link DFAState#predicates}, and
	 * the value of each element is the result of evaluating the semantic
	 * context {@link DFAState.PredPrediction#pred}.
	 */
	public final boolean[] evalResults;
	/**
	 * A {@link BitSet} identifying the represented alternatives of
	 * {@link #dfaState} which remain viable following the evaluation of
	 * semantic predicates.
	 */
	public final BitSet predictions;

	/**
	 * Constructs a new instance of the {@link PredicateEvalInfo} class with the
	 * specified detailed predicate evaluation information.
	 *
	 * @param state The simulator state 
	 * @param decision The decision number
	 * @param input The input token stream
	 * @param startIndex The start index for the current prediction
	 * @param stopIndex The index at which the predicate evaluation was
	 * triggered. Note that the input stream may be reset to other locations for
	 * the actual evaluation of individual predicates.
	 * @param evalResults The results of evaluating specific semantic contexts.
	 * The elements of this array correspond to the elements in
	 * {@link DFAState#predicates}, and the value of each element is the result
	 * of evaluating the semantic context {@link DFAState.PredPrediction#pred}.
	 * @param predictions A {@link BitSet} identifying the represented
	 * alternatives of {@code dfaState} which remain viable following the
	 * evaluation of semantic predicates
	 */
	public PredicateEvalInfo(@NotNull SimulatorState state, int decision,
							 @NotNull TokenStream input, int startIndex, int stopIndex,
							 @NotNull boolean[] evalResults,
							 @NotNull BitSet predictions)
	{
		super(decision, state, input, startIndex, stopIndex, state.useContext);
		this.evalResults = evalResults;
		this.predictions = predictions;
	}
}
