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
using Antlr4.Runtime.Misc;
using Sharpen;

namespace Antlr4.Runtime
{
	/// <summary>The root of the ANTLR exception hierarchy.</summary>
	/// <remarks>
	/// The root of the ANTLR exception hierarchy. In general, ANTLR tracks just
	/// 3 kinds of errors: prediction errors, failed predicate errors, and
	/// mismatched input errors. In each case, the parser knows where it is
	/// in the input, where it is in the ATN, the rule invocation stack,
	/// and what kind of problem occurred.
	/// </remarks>
	[System.Serializable]
	public class RecognitionException : RuntimeException
	{
		private const long serialVersionUID = -3861826954750022374L;

		/// <summary>Who threw the exception?</summary>
		private Recognizer<object, object> recognizer;

		private RuleContext ctx;

		private IntStream input;

		/// <summary>The current Token when an error occurred.</summary>
		/// <remarks>
		/// The current Token when an error occurred.  Since not all streams
		/// can retrieve the ith Token, we have to track the Token object.
		/// For parsers.  Even when it's a tree parser, token might be set.
		/// </remarks>
		private Token offendingToken;

		private int offendingState;

		public RecognitionException(Lexer lexer, CharStream input)
		{
			// TODO: make a dummy recognizer for the interpreter to use?
			// Next two (ctx,input) should be what is in recognizer, but
			// won't work when interpreting
			this.recognizer = lexer;
			this.input = input;
		}

		public RecognitionException(Recognizer<Token, object> recognizer, IntStream input
			, ParserRuleContext ctx)
		{
			this.recognizer = recognizer;
			this.input = input;
			this.ctx = ctx;
			if (recognizer != null)
			{
				this.offendingState = recognizer.GetState();
			}
		}

		public RecognitionException(string message, Recognizer<Token, object> recognizer, 
			IntStream input, ParserRuleContext ctx) : base(message)
		{
			this.recognizer = recognizer;
			this.input = input;
			this.ctx = ctx;
			if (recognizer != null)
			{
				this.offendingState = recognizer.GetState();
			}
		}

		/// <summary>
		/// Where was the parser in the ATN when the error occurred?
		/// For No viable alternative exceptions, this is the decision state number.
		/// </summary>
		/// <remarks>
		/// Where was the parser in the ATN when the error occurred?
		/// For No viable alternative exceptions, this is the decision state number.
		/// For others, it is the state whose emanating edge we couldn't match.
		/// This will help us tie into the grammar and syntax diagrams in
		/// ANTLRWorks v2.
		/// </remarks>
		public virtual int GetOffendingState()
		{
			return offendingState;
		}

		protected internal void SetOffendingState(int offendingState)
		{
			this.offendingState = offendingState;
		}

		public virtual IntervalSet GetExpectedTokens()
		{
			// TODO: do we really need this type check?
			if (recognizer is Parser)
			{
				return ((Parser)recognizer).GetExpectedTokens();
			}
			return null;
		}

		public virtual RuleContext GetCtx()
		{
			return ctx;
		}

		public virtual IntStream GetInputStream()
		{
			return input;
		}

		public virtual Token GetOffendingToken()
		{
			return offendingToken;
		}

		protected internal void SetOffendingToken(Token offendingToken)
		{
			this.offendingToken = offendingToken;
		}

		public virtual Recognizer<object, object> GetRecognizer()
		{
			return recognizer;
		}
	}
}
