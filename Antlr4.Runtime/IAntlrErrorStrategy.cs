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
using Sharpen;

namespace Antlr4.Runtime
{
	/// <summary>
	/// The interface for defining strategies to deal with syntax errors
	/// encountered during a parse by ANTLR-generated parsers and tree parsers.
	/// </summary>
	/// <remarks>
	/// The interface for defining strategies to deal with syntax errors
	/// encountered during a parse by ANTLR-generated parsers and tree parsers.
	/// We distinguish between three different kinds of errors:
	/// o The parser could not figure out which path to take in the ATN
	/// (none of the available alternatives could possibly match)
	/// o The current input does not match what we were looking for.
	/// o A predicate evaluated to false.
	/// The default implementation of this interface reports errors to any
	/// error listeners of the parser. It also handles single token insertion
	/// and deletion for mismatched elements.
	/// We pass in the parser to each function so that the same strategy
	/// can be shared between multiple parsers running at the same time.
	/// This is just for flexibility, not that we need it for the default system.
	/// TODO: To bail out upon first error, simply rethrow e?
	/// TODO: what to do about lexers
	/// </remarks>
	public interface IAntlrErrorStrategy
	{
		/// <summary>
		/// When matching elements within alternative, use this method
		/// to recover.
		/// </summary>
		/// <remarks>
		/// When matching elements within alternative, use this method
		/// to recover. The default implementation uses single token
		/// insertion and deletion. If you want to change the way ANTLR
		/// response to mismatched element errors within an alternative,
		/// implement this method.
		/// From the recognizer, we can get the input stream to get
		/// the current input symbol and we can get the current context.
		/// That context gives us the current state within the ATN.
		/// From that state, we can look at its transition to figure out
		/// what was expected.
		/// Because we can recover from a single token deletions by
		/// "inserting" tokens, we need to specify what that implicitly created
		/// token is. We use object, because it could be a tree node.
		/// </remarks>
		/// <exception cref="Antlr4.Runtime.RecognitionException"></exception>
		IToken RecoverInline(Parser recognizer);

		/// <summary>
		/// Resynchronize the parser by consuming tokens until we find one
		/// in the resynchronization set--loosely the set of tokens that can follow
		/// the current rule.
		/// </summary>
		/// <remarks>
		/// Resynchronize the parser by consuming tokens until we find one
		/// in the resynchronization set--loosely the set of tokens that can follow
		/// the current rule. The exception contains info you might want to
		/// use to recover better.
		/// </remarks>
		void Recover(Parser recognizer, RecognitionException e);

		/// <summary>
		/// Make sure that the current lookahead symbol is consistent with
		/// what were expecting at this point in the ATN.
		/// </summary>
		/// <remarks>
		/// Make sure that the current lookahead symbol is consistent with
		/// what were expecting at this point in the ATN. You can call this
		/// anytime but ANTLR only generates code to check before subrules/loops
		/// and each iteration.
		/// Implements Jim Idle's magic sync mechanism in closures and optional
		/// subrules. E.g.,
		/// a : sync ( stuff sync )* ;
		/// sync : {consume to what can follow sync} ;
		/// Previous versions of ANTLR did a poor job of their recovery within
		/// loops. A single mismatch token or missing token would force the parser
		/// to bail out of the entire rules surrounding the loop. So, for rule
		/// classDef : 'class' ID '{' member* '}'
		/// input with an extra token between members would force the parser to
		/// consume until it found the next class definition rather than the
		/// next member definition of the current class.
		/// This functionality cost a little bit of effort because the parser
		/// has to compare token set at the start of the loop and at each
		/// iteration. If for some reason speed is suffering for you, you can
		/// turn off this functionality by simply overriding this method as
		/// a blank { }.
		/// </remarks>
		void Sync(Parser recognizer);

		/// <summary>Notify handler that parser has entered an error state.</summary>
		/// <remarks>
		/// Notify handler that parser has entered an error state.  The
		/// parser currently doesn't call this--the handler itself calls this
		/// in report error methods.  But, for symmetry with endErrorCondition,
		/// this method is in the interface.
		/// </remarks>
		void BeginErrorCondition(Parser recognizer);

		/// <summary>
		/// Is the parser in the process of recovering from an error? Upon
		/// a syntax error, the parser enters recovery mode and stays there until
		/// the next successful match of a token.
		/// </summary>
		/// <remarks>
		/// Is the parser in the process of recovering from an error? Upon
		/// a syntax error, the parser enters recovery mode and stays there until
		/// the next successful match of a token. In this way, we can
		/// avoid sending out spurious error messages. We only want one error
		/// message per syntax error
		/// </remarks>
		bool InErrorRecoveryMode(Parser recognizer);

		/// <summary>Reset the error handler.</summary>
		/// <remarks>
		/// Reset the error handler. Call this when the parser
		/// matches a valid token (indicating no longer in recovery mode)
		/// and from its own reset method.
		/// </remarks>
		void EndErrorCondition(Parser recognizer);

		/// <summary>Report any kind of RecognitionException.</summary>
		/// <remarks>Report any kind of RecognitionException.</remarks>
		/// <exception cref="Antlr4.Runtime.RecognitionException"></exception>
		void ReportError(Parser recognizer, RecognitionException e);
	}
}
