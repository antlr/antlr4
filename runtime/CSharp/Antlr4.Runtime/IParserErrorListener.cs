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
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
    /// <summary>How to emit recognition errors for parsers.</summary>
    /// <remarks>How to emit recognition errors for parsers.</remarks>
    public interface IParserErrorListener : IAntlrErrorListener<IToken>
    {
        /// <summary>
        /// This method is called by the parser when a full-context prediction
        /// results in an ambiguity.
        /// </summary>
        /// <remarks>
        /// This method is called by the parser when a full-context prediction
        /// results in an ambiguity.
        /// <p>Each full-context prediction which does not result in a syntax error
        /// will call either
        /// <see cref="ReportContextSensitivity(Parser, Antlr4.Runtime.Dfa.DFA, int, int, int, Antlr4.Runtime.Atn.SimulatorState)"/>
        /// or
        /// <see cref="ReportAmbiguity(Parser, Antlr4.Runtime.Dfa.DFA, int, int, bool, Antlr4.Runtime.Sharpen.BitSet, Antlr4.Runtime.Atn.ATNConfigSet)"/>
        /// .</p>
        /// <p>
        /// When
        /// <code>ambigAlts</code>
        /// is not null, it contains the set of potentially
        /// viable alternatives identified by the prediction algorithm. When
        /// <code>ambigAlts</code>
        /// is null, use
        /// <see cref="Antlr4.Runtime.Atn.ATNConfigSet.RepresentedAlternatives()"/>
        /// to obtain the represented
        /// alternatives from the
        /// <code>configs</code>
        /// argument.</p>
        /// <p>When
        /// <code>exact</code>
        /// is
        /// <code>true</code>
        /// , <em>all</em> of the potentially
        /// viable alternatives are truly viable, i.e. this is reporting an exact
        /// ambiguity. When
        /// <code>exact</code>
        /// is
        /// <code>false</code>
        /// , <em>at least two</em> of
        /// the potentially viable alternatives are viable for the current input, but
        /// the prediction algorithm terminated as soon as it determined that at
        /// least the <em>minimum</em> potentially viable alternative is truly
        /// viable.</p>
        /// <p>When the
        /// <see cref="Antlr4.Runtime.Atn.PredictionMode.LlExactAmbigDetection"/>
        /// prediction
        /// mode is used, the parser is required to identify exact ambiguities so
        /// <code>exact</code>
        /// will always be
        /// <code>true</code>
        /// .</p>
        /// </remarks>
        /// <param name="recognizer">the parser instance</param>
        /// <param name="dfa">the DFA for the current decision</param>
        /// <param name="startIndex">the input index where the decision started</param>
        /// <param name="stopIndex">the input input where the ambiguity was identified</param>
        /// <param name="exact">
        /// 
        /// <code>true</code>
        /// if the ambiguity is exactly known, otherwise
        /// <code>false</code>
        /// . This is always
        /// <code>true</code>
        /// when
        /// <see cref="Antlr4.Runtime.Atn.PredictionMode.LlExactAmbigDetection"/>
        /// is used.
        /// </param>
        /// <param name="ambigAlts">
        /// the potentially ambiguous alternatives, or
        /// <code>null</code>
        /// to indicate that the potentially ambiguous alternatives are the complete
        /// set of represented alternatives in
        /// <code>configs</code>
        /// </param>
        /// <param name="configs">
        /// the ATN configuration set where the ambiguity was
        /// identified
        /// </param>
        void ReportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, bool exact, BitSet ambigAlts, ATNConfigSet configs);

        /// <summary>
        /// This method is called when an SLL conflict occurs and the parser is about
        /// to use the full context information to make an LL decision.
        /// </summary>
        /// <remarks>
        /// This method is called when an SLL conflict occurs and the parser is about
        /// to use the full context information to make an LL decision.
        /// <p>If one or more configurations in
        /// <code>configs</code>
        /// contains a semantic
        /// predicate, the predicates are evaluated before this method is called. The
        /// subset of alternatives which are still viable after predicates are
        /// evaluated is reported in
        /// <code>conflictingAlts</code>
        /// .</p>
        /// </remarks>
        /// <param name="recognizer">the parser instance</param>
        /// <param name="dfa">the DFA for the current decision</param>
        /// <param name="startIndex">the input index where the decision started</param>
        /// <param name="stopIndex">the input index where the SLL conflict occurred</param>
        /// <param name="conflictingAlts">
        /// The specific conflicting alternatives. If this is
        /// <code>null</code>
        /// , the conflicting alternatives are all alternatives
        /// represented in
        /// <code>configs</code>
        /// .
        /// </param>
        /// <param name="conflictState">
        /// the simulator state when the SLL conflict was
        /// detected
        /// </param>
        void ReportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, SimulatorState conflictState);

        /// <summary>
        /// This method is called by the parser when a full-context prediction has a
        /// unique result.
        /// </summary>
        /// <remarks>
        /// This method is called by the parser when a full-context prediction has a
        /// unique result.
        /// <p>Each full-context prediction which does not result in a syntax error
        /// will call either
        /// <see cref="ReportContextSensitivity(Parser, Antlr4.Runtime.Dfa.DFA, int, int, int, Antlr4.Runtime.Atn.SimulatorState)"/>
        /// or
        /// <see cref="ReportAmbiguity(Parser, Antlr4.Runtime.Dfa.DFA, int, int, bool, Antlr4.Runtime.Sharpen.BitSet, Antlr4.Runtime.Atn.ATNConfigSet)"/>
        /// .</p>
        /// <p>For prediction implementations that only evaluate full-context
        /// predictions when an SLL conflict is found (including the default
        /// <see cref="Antlr4.Runtime.Atn.ParserATNSimulator"/>
        /// implementation), this method reports cases
        /// where SLL conflicts were resolved to unique full-context predictions,
        /// i.e. the decision was context-sensitive. This report does not necessarily
        /// indicate a problem, and it may appear even in completely unambiguous
        /// grammars.</p>
        /// <p>
        /// <code>configs</code>
        /// may have more than one represented alternative if the
        /// full-context prediction algorithm does not evaluate predicates before
        /// beginning the full-context prediction. In all cases, the final prediction
        /// is passed as the
        /// <code>prediction</code>
        /// argument.</p>
        /// <p>Note that the definition of "context sensitivity" in this method
        /// differs from the concept in
        /// <see cref="Antlr4.Runtime.Atn.DecisionInfo.contextSensitivities"/>
        /// .
        /// This method reports all instances where an SLL conflict occurred but LL
        /// parsing produced a unique result, whether or not that unique result
        /// matches the minimum alternative in the SLL conflicting set.</p>
        /// </remarks>
        /// <param name="recognizer">the parser instance</param>
        /// <param name="dfa">the DFA for the current decision</param>
        /// <param name="startIndex">the input index where the decision started</param>
        /// <param name="stopIndex">
        /// the input index where the context sensitivity was
        /// finally determined
        /// </param>
        /// <param name="prediction">the unambiguous result of the full-context prediction</param>
        /// <param name="acceptState">
        /// the simulator state when the unambiguous prediction
        /// was determined
        /// </param>
        void ReportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, SimulatorState acceptState);
    }
}
