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
using System;
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;
using Sharpen;

namespace Antlr4.Runtime
{
	/// <summary>
	/// This is the default error handling mechanism for ANTLR parsers
	/// and tree parsers.
	/// </summary>
	/// <remarks>
	/// This is the default error handling mechanism for ANTLR parsers
	/// and tree parsers.
	/// </remarks>
	public class DefaultErrorStrategy : IAntlrErrorStrategy
	{
		/// <summary>
		/// This is true after we see an error and before having successfully
		/// matched a token.
		/// </summary>
		/// <remarks>
		/// This is true after we see an error and before having successfully
		/// matched a token. Prevents generation of more than one error message
		/// per error.
		/// </remarks>
		protected internal bool errorRecoveryMode = false;

		/// <summary>The index into the input stream where the last error occurred.</summary>
		/// <remarks>
		/// The index into the input stream where the last error occurred.
		/// This is used to prevent infinite loops where an error is found
		/// but no token is consumed during recovery...another error is found,
		/// ad nauseum.  This is a failsafe mechanism to guarantee that at least
		/// one token/tree node is consumed for two errors.
		/// </remarks>
		protected internal int lastErrorIndex = -1;

		protected internal IntervalSet lastErrorStates;

		public virtual void BeginErrorCondition(Parser recognizer)
		{
			errorRecoveryMode = true;
		}

		public virtual bool InErrorRecoveryMode(Parser recognizer)
		{
			return errorRecoveryMode;
		}

		public virtual void EndErrorCondition(Parser recognizer)
		{
			errorRecoveryMode = false;
			lastErrorStates = null;
			lastErrorIndex = -1;
		}

		/// <exception cref="Antlr4.Runtime.RecognitionException"></exception>
		public virtual void ReportError(Parser recognizer, RecognitionException e)
		{
			// if we've already reported an error and have not matched a token
			// yet successfully, don't report any errors.
			if (errorRecoveryMode)
			{
				//			System.err.print("[SPURIOUS] ");
				return;
			}
			// don't count spurious errors
			recognizer._syntaxErrors++;
			BeginErrorCondition(recognizer);
			if (e is NoViableAltException)
			{
				ReportNoViableAlternative(recognizer, (NoViableAltException)e);
			}
			else
			{
				if (e is InputMismatchException)
				{
					ReportInputMismatch(recognizer, (InputMismatchException)e);
				}
				else
				{
					if (e is FailedPredicateException)
					{
						ReportFailedPredicate(recognizer, (FailedPredicateException)e);
					}
					else
					{
						System.Console.Error.WriteLine("unknown recognition error type: " + e.GetType().FullName
							);
						NotifyErrorListeners(recognizer, e.Message, e);
					}
				}
			}
		}

		protected internal virtual void NotifyErrorListeners(Parser recognizer, string message
			, RecognitionException e)
		{
			if (recognizer != null)
			{
				recognizer.NotifyErrorListeners(e.GetOffendingToken(), message, e);
			}
		}

		/// <summary>Recover from NoViableAlt errors.</summary>
		/// <remarks>
		/// Recover from NoViableAlt errors. Also there could be a mismatched
		/// token that the match() routine could not recover from.
		/// </remarks>
		public virtual void Recover(Parser recognizer, RecognitionException e)
		{
			//		System.out.println("recover in "+recognizer.getRuleInvocationStack()+
			//						   " index="+recognizer.getInputStream().index()+
			//						   ", lastErrorIndex="+
			//						   lastErrorIndex+
			//						   ", states="+lastErrorStates);
			if (lastErrorIndex == ((ITokenStream)recognizer.GetInputStream()).Index && lastErrorStates
				 != null && lastErrorStates.Contains(recognizer.GetState()))
			{
				// uh oh, another error at same token index and previously-visited
				// state in ATN; must be a case where LT(1) is in the recovery
				// token set so nothing got consumed. Consume a single token
				// at least to prevent an infinite loop; this is a failsafe.
				//			System.err.println("seen error condition before index="+
				//							   lastErrorIndex+", states="+lastErrorStates);
				//			System.err.println("FAILSAFE consumes "+recognizer.getTokenNames()[recognizer.getInputStream().LA(1)]);
				recognizer.Consume();
			}
			lastErrorIndex = ((ITokenStream)recognizer.GetInputStream()).Index;
			if (lastErrorStates == null)
			{
				lastErrorStates = new IntervalSet();
			}
			lastErrorStates.Add(recognizer.GetState());
			IntervalSet followSet = GetErrorRecoverySet(recognizer);
			ConsumeUntil(recognizer, followSet);
		}

		/// <summary>
		/// Make sure that the current lookahead symbol is consistent with
		/// what were expecting at this point in the ATN.
		/// </summary>
		/// <remarks>
		/// Make sure that the current lookahead symbol is consistent with
		/// what were expecting at this point in the ATN.
		/// At the start of a sub rule upon error, sync() performs single
		/// token deletion, if possible. If it can't do that, it bails
		/// on the current rule and uses the default error recovery,
		/// which consumes until the resynchronization set of the current rule.
		/// If the sub rule is optional, ()? or ()* or optional alternative,
		/// then the expected set includes what follows the subrule.
		/// During loop iteration, it consumes until it sees a token that can
		/// start a sub rule or what follows loop. Yes, that is pretty aggressive.
		/// We opt to stay in the loop as long as possible.
		/// </remarks>
		public virtual void Sync(Parser recognizer)
		{
			ATNState s = recognizer.GetInterpreter().atn.states[recognizer.GetState()];
			//		System.err.println("sync @ "+s.stateNumber+"="+s.getClass().getSimpleName());
			// If already recovering, don't try to sync
			if (errorRecoveryMode)
			{
				return;
			}
			ITokenStream tokens = ((ITokenStream)recognizer.GetInputStream());
			int la = tokens.La(1);
			// try cheaper subset first; might get lucky. seems to shave a wee bit off
			if (recognizer.GetATN().NextTokens(s).Contains(la) || la == IToken.Eof)
			{
				return;
			}
			// Return but don't end recovery. only do that upon valid token match
			if (recognizer.IsExpectedToken(la))
			{
				return;
			}
			switch (s.GetStateType())
			{
				case ATNState.BlockStart:
				case ATNState.StarBlockStart:
				case ATNState.PlusBlockStart:
				case ATNState.StarLoopEntry:
				{
					// report error and recover if possible
					if (SingleTokenDeletion(recognizer) != null)
					{
						return;
					}
					throw new InputMismatchException(recognizer);
				}

				case ATNState.PlusLoopBack:
				case ATNState.StarLoopBack:
				{
					//			System.err.println("at loop back: "+s.getClass().getSimpleName());
					ReportUnwantedToken(recognizer);
					IntervalSet expecting = recognizer.GetExpectedTokens();
					IntervalSet whatFollowsLoopIterationOrRule = expecting.Or(GetErrorRecoverySet(recognizer
						));
					ConsumeUntil(recognizer, whatFollowsLoopIterationOrRule);
					break;
				}

				default:
				{
					// do nothing if we can't identify the exact kind of ATN state
					break;
					break;
				}
			}
		}

		/// <exception cref="Antlr4.Runtime.RecognitionException"></exception>
		public virtual void ReportNoViableAlternative(Parser recognizer, NoViableAltException
			 e)
		{
			ITokenStream tokens = ((ITokenStream)recognizer.GetInputStream());
			string input;
			if (tokens != null)
			{
				if (e.GetStartToken().Type == IToken.Eof)
				{
					input = "<EOF>";
				}
				else
				{
					input = tokens.GetText(e.GetStartToken(), e.GetOffendingToken());
				}
			}
			else
			{
				input = "<unknown input>";
			}
			string msg = "no viable alternative at input " + EscapeWSAndQuote(input);
			NotifyErrorListeners(recognizer, msg, e);
		}

		/// <exception cref="Antlr4.Runtime.RecognitionException"></exception>
		public virtual void ReportInputMismatch(Parser recognizer, InputMismatchException
			 e)
		{
			string msg = "mismatched input " + GetTokenErrorDisplay(e.GetOffendingToken()) + 
				" expecting " + e.GetExpectedTokens().ToString(recognizer.GetTokenNames());
			NotifyErrorListeners(recognizer, msg, e);
		}

		/// <exception cref="Antlr4.Runtime.RecognitionException"></exception>
		public virtual void ReportFailedPredicate(Parser recognizer, FailedPredicateException
			 e)
		{
			string ruleName = recognizer.GetRuleNames()[recognizer._ctx.GetRuleIndex()];
			string msg = "rule " + ruleName + " " + e.Message;
			NotifyErrorListeners(recognizer, msg, e);
		}

		public virtual void ReportUnwantedToken(Parser recognizer)
		{
			if (errorRecoveryMode)
			{
				return;
			}
			recognizer._syntaxErrors++;
			BeginErrorCondition(recognizer);
			IToken t = recognizer.GetCurrentToken();
			string tokenName = GetTokenErrorDisplay(t);
			IntervalSet expecting = GetExpectedTokens(recognizer);
			string msg = "extraneous input " + tokenName + " expecting " + expecting.ToString
				(recognizer.GetTokenNames());
			recognizer.NotifyErrorListeners(t, msg, null);
		}

		public virtual void ReportMissingToken(Parser recognizer)
		{
			if (errorRecoveryMode)
			{
				return;
			}
			recognizer._syntaxErrors++;
			BeginErrorCondition(recognizer);
			IToken t = recognizer.GetCurrentToken();
			IntervalSet expecting = GetExpectedTokens(recognizer);
			string msg = "missing " + expecting.ToString(recognizer.GetTokenNames()) + " at "
				 + GetTokenErrorDisplay(t);
			recognizer.NotifyErrorListeners(t, msg, null);
		}

		/// <summary>Attempt to recover from a single missing or extra token.</summary>
		/// <remarks>
		/// Attempt to recover from a single missing or extra token.
		/// EXTRA TOKEN
		/// LA(1) is not what we are looking for.  If LA(2) has the right token,
		/// however, then assume LA(1) is some extra spurious token.  Delete it
		/// and LA(2) as if we were doing a normal match(), which advances the
		/// input.
		/// MISSING TOKEN
		/// If current token is consistent with what could come after
		/// ttype then it is ok to "insert" the missing token, else throw
		/// exception For example, Input "i=(3;" is clearly missing the
		/// ')'.  When the parser returns from the nested call to expr, it
		/// will have call chain:
		/// stat -&gt; expr -&gt; atom
		/// and it will be trying to match the ')' at this point in the
		/// derivation:
		/// =&gt; ID '=' '(' INT ')' ('+' atom)* ';'
		/// ^
		/// match() will see that ';' doesn't match ')' and report a
		/// mismatched token error.  To recover, it sees that LA(1)==';'
		/// is in the set of tokens that can follow the ')' token
		/// reference in rule atom.  It can assume that you forgot the ')'.
		/// </remarks>
		/// <exception cref="Antlr4.Runtime.RecognitionException"></exception>
		public virtual IToken RecoverInline(Parser recognizer)
		{
			// SINGLE TOKEN DELETION
			IToken matchedSymbol = SingleTokenDeletion(recognizer);
			if (matchedSymbol != null)
			{
				// we have deleted the extra token.
				// now, move past ttype token as if all were ok
				recognizer.Consume();
				return matchedSymbol;
			}
			// SINGLE TOKEN INSERTION
			if (SingleTokenInsertion(recognizer))
			{
				return GetMissingSymbol(recognizer);
			}
			// even that didn't work; must throw the exception
			throw new InputMismatchException(recognizer);
		}

		// if next token is what we are looking for then "delete" this token
		public virtual bool SingleTokenInsertion(Parser recognizer)
		{
			int currentSymbolType = ((ITokenStream)recognizer.GetInputStream()).La(1);
			// if current token is consistent with what could come after current
			// ATN state, then we know we're missing a token; error recovery
			// is free to conjure up and insert the missing token
			ATNState currentState = recognizer.GetInterpreter().atn.states[recognizer.GetState
				()];
			ATNState next = currentState.Transition(0).target;
			ATN atn = recognizer.GetInterpreter().atn;
			IntervalSet expectingAtLL2 = atn.NextTokens(next, PredictionContext.FromRuleContext
				(atn, recognizer._ctx));
			//		System.out.println("LT(2) set="+expectingAtLL2.toString(recognizer.getTokenNames()));
			if (expectingAtLL2.Contains(currentSymbolType))
			{
				ReportMissingToken(recognizer);
				return true;
			}
			return false;
		}

		public virtual IToken SingleTokenDeletion(Parser recognizer)
		{
			int nextTokenType = ((ITokenStream)recognizer.GetInputStream()).La(2);
			IntervalSet expecting = GetExpectedTokens(recognizer);
			if (expecting.Contains(nextTokenType))
			{
				ReportUnwantedToken(recognizer);
				recognizer.Consume();
				// simply delete extra token
				// we want to return the token we're actually matching
				IToken matchedSymbol = recognizer.GetCurrentToken();
				EndErrorCondition(recognizer);
				// we know current token is correct
				return matchedSymbol;
			}
			return null;
		}

		/// <summary>Conjure up a missing token during error recovery.</summary>
		/// <remarks>
		/// Conjure up a missing token during error recovery.
		/// The recognizer attempts to recover from single missing
		/// symbols. But, actions might refer to that missing symbol.
		/// For example, x=ID {f($x);}. The action clearly assumes
		/// that there has been an identifier matched previously and that
		/// $x points at that token. If that token is missing, but
		/// the next token in the stream is what we want we assume that
		/// this token is missing and we keep going. Because we
		/// have to return some token to replace the missing token,
		/// we have to conjure one up. This method gives the user control
		/// over the tokens returned for missing tokens. Mostly,
		/// you will want to create something special for identifier
		/// tokens. For literals such as '{' and ',', the default
		/// action in the parser or tree parser works. It simply creates
		/// a CommonToken of the appropriate type. The text will be the token.
		/// If you change what tokens must be created by the lexer,
		/// override this method to create the appropriate tokens.
		/// </remarks>
		protected internal virtual IToken GetMissingSymbol(Parser recognizer)
		{
			IToken currentSymbol = recognizer.GetCurrentToken();
			IntervalSet expecting = GetExpectedTokens(recognizer);
			int expectedTokenType = expecting.GetMinElement();
			// get any element
			string tokenText;
			if (expectedTokenType == IToken.Eof)
			{
				tokenText = "<missing EOF>";
			}
			else
			{
				tokenText = "<missing " + recognizer.GetTokenNames()[expectedTokenType] + ">";
			}
			IToken current = currentSymbol;
			IToken lookback = ((ITokenStream)recognizer.GetInputStream()).Lt(-1);
			if (current.Type == IToken.Eof && lookback != null)
			{
				current = lookback;
			}
			return ConstructToken(((ITokenStream)recognizer.GetInputStream()).TokenSource, expectedTokenType
				, tokenText, current);
		}

		protected internal virtual IToken ConstructToken(ITokenSource tokenSource, int expectedTokenType
			, string tokenText, IToken current)
		{
			ITokenFactory factory = tokenSource.TokenFactory;
			return factory.Create(Tuple.Create(tokenSource, current.TokenSource.InputStream), 
				expectedTokenType, tokenText, IToken.DefaultChannel, -1, -1, current.Line, current
				.Column);
		}

		public virtual IntervalSet GetExpectedTokens(Parser recognizer)
		{
			return recognizer.GetExpectedTokens();
		}

		/// <summary>
		/// How should a token be displayed in an error message? The default
		/// is to display just the text, but during development you might
		/// want to have a lot of information spit out.
		/// </summary>
		/// <remarks>
		/// How should a token be displayed in an error message? The default
		/// is to display just the text, but during development you might
		/// want to have a lot of information spit out.  Override in that case
		/// to use t.toString() (which, for CommonToken, dumps everything about
		/// the token). This is better than forcing you to override a method in
		/// your token objects because you don't have to go modify your lexer
		/// so that it creates a new Java type.
		/// </remarks>
		public virtual string GetTokenErrorDisplay(IToken t)
		{
			if (t == null)
			{
				return "<no token>";
			}
			string s = GetSymbolText(t);
			if (s == null)
			{
				if (GetSymbolType(t) == IToken.Eof)
				{
					s = "<EOF>";
				}
				else
				{
					s = "<" + GetSymbolType(t) + ">";
				}
			}
			return EscapeWSAndQuote(s);
		}

		protected internal virtual string GetSymbolText(IToken symbol)
		{
			return symbol.Text;
		}

		protected internal virtual int GetSymbolType(IToken symbol)
		{
			return symbol.Type;
		}

		protected internal virtual string EscapeWSAndQuote(string s)
		{
			//		if ( s==null ) return s;
			s = s.ReplaceAll("\n", "\\\\n");
			s = s.ReplaceAll("\r", "\\\\r");
			s = s.ReplaceAll("\t", "\\\\t");
			return "'" + s + "'";
		}

		protected internal virtual IntervalSet GetErrorRecoverySet(Parser recognizer)
		{
			ATN atn = recognizer.GetInterpreter().atn;
			RuleContext ctx = recognizer._ctx;
			IntervalSet recoverSet = new IntervalSet();
			while (ctx != null && ctx.invokingState >= 0)
			{
				// compute what follows who invoked us
				ATNState invokingState = atn.states[ctx.invokingState];
				RuleTransition rt = (RuleTransition)invokingState.Transition(0);
				IntervalSet follow = atn.NextTokens(rt.followState);
				recoverSet.AddAll(follow);
				ctx = ctx.parent;
			}
			recoverSet.Remove(IToken.Epsilon);
			//		System.out.println("recover set "+recoverSet.toString(recognizer.getTokenNames()));
			return recoverSet;
		}

		/// <summary>Consume tokens until one matches the given token set</summary>
		public virtual void ConsumeUntil(Parser recognizer, IntervalSet set)
		{
			//		System.err.println("consumeUntil("+set.toString(recognizer.getTokenNames())+")");
			int ttype = ((ITokenStream)recognizer.GetInputStream()).La(1);
			while (ttype != IToken.Eof && !set.Contains(ttype))
			{
				//System.out.println("consume during recover LA(1)="+getTokenNames()[input.LA(1)]);
				//			recognizer.getInputStream().consume();
				recognizer.Consume();
				ttype = ((ITokenStream)recognizer.GetInputStream()).La(1);
			}
		}
	}
}
