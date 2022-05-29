/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.Collections.Generic;
using System.Runtime.CompilerServices;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;

namespace Antlr4.Runtime
{
    public abstract class Recognizer<Symbol, ATNInterpreter> : IRecognizer
        where ATNInterpreter : ATNSimulator
    {
        public const int Eof = -1;

        private static readonly ConditionalWeakTable<IVocabulary, IDictionary<string, int>> tokenTypeMapCache = new ConditionalWeakTable<IVocabulary, IDictionary<string, int>>();
        private static readonly ConditionalWeakTable<string[], IDictionary<string, int>> ruleIndexMapCache = new ConditionalWeakTable<string[], IDictionary<string, int>>();

        [NotNull]
        private IAntlrErrorListener<Symbol>[] _listeners =
        {
            ConsoleErrorListener<Symbol>.Instance
        };

        private ATNInterpreter _interp;

        private int _stateNumber = -1;

        /// <summary>
        /// Used to print out token names like ID during debugging and
        /// error reporting.
        /// </summary>
        /// <remarks>
        /// Used to print out token names like ID during debugging and
        /// error reporting.  The generated parsers implement a method
        /// that overrides this to point to their String[] tokenNames.
        /// </remarks>

        public abstract string[] RuleNames
        {
            get;
        }


        /// <summary>Get the vocabulary used by the recognizer.</summary>
        /// <remarks>Get the vocabulary used by the recognizer.</remarks>
        /// <returns>
        /// A
        /// <see cref="IVocabulary"/>
        /// instance providing information about the
        /// vocabulary used by the grammar.
        /// </returns>
        public abstract IVocabulary Vocabulary
        {
			get;
       }

        /// <summary>Get a map from token names to token types.</summary>
        /// <remarks>
        /// Get a map from token names to token types.
        /// <p>Used for XPath and tree pattern compilation.</p>
        /// </remarks>
        [NotNull]
        public virtual IDictionary<string, int> TokenTypeMap
        {
            get
            {
                return tokenTypeMapCache.GetValue(Vocabulary, CreateTokenTypeMap);
            }
        }

        protected virtual IDictionary<string, int> CreateTokenTypeMap(IVocabulary vocabulary)
        {
            var result = new Dictionary<string, int>();
            for (int i = 0; i <= Atn.maxTokenType; i++)
            {
                string literalName = vocabulary.GetLiteralName(i);
                if (literalName != null)
                {
                    result[literalName] = i;
                }
                string symbolicName = vocabulary.GetSymbolicName(i);
                if (symbolicName != null)
                {
                    result[symbolicName] = i;
                }
            }
            result["EOF"] = TokenConstants.EOF;
            return result;
        }

        /// <summary>Get a map from rule names to rule indexes.</summary>
        /// <remarks>
        /// Get a map from rule names to rule indexes.
        /// <p>Used for XPath and tree pattern compilation.</p>
        /// </remarks>
        [NotNull]
        public virtual IDictionary<string, int> RuleIndexMap
        {
            get
            {
                string[] ruleNames = RuleNames;
                if (ruleNames == null)
                {
                    throw new NotSupportedException("The current recognizer does not provide a list of rule names.");
                }

                return ruleIndexMapCache.GetValue(ruleNames, Utils.ToMap);
            }
        }

        public virtual int GetTokenType(string tokenName)
        {
            int ttype;
            if (TokenTypeMap.TryGetValue(tokenName, out ttype))
            {
                return ttype;
            }
            return TokenConstants.InvalidType;
        }

        /// <summary>
        /// If this recognizer was generated, it will have a serialized ATN
        /// representation of the grammar.
        /// </summary>
        /// <remarks>
        /// If this recognizer was generated, it will have a serialized ATN
        /// representation of the grammar.
        /// <p>For interpreters, we don't know their serialized ATN despite having
        /// created the interpreter from it.</p>
        /// </remarks>
        public virtual int[] SerializedAtn
        {
            [return: NotNull]
            get
            {
                throw new NotSupportedException("there is no serialized ATN");
            }
        }

        /// <summary>For debugging and other purposes, might want the grammar name.</summary>
        /// <remarks>
        /// For debugging and other purposes, might want the grammar name.
        /// Have ANTLR generate an implementation for this method.
        /// </remarks>
        public abstract string GrammarFileName
        {
            get;
        }

        /// <summary>
        /// Get the
        /// <see cref="Antlr4.Runtime.Atn.ATN"/>
        /// used by the recognizer for prediction.
        /// </summary>
        /// <returns>
        /// The
        /// <see cref="Antlr4.Runtime.Atn.ATN"/>
        /// used by the recognizer for prediction.
        /// </returns>
        public virtual ATN Atn
        {
            get
            {
                return _interp.atn;
            }
        }

        /// <summary>Get the ATN interpreter used by the recognizer for prediction.</summary>
        /// <remarks>Get the ATN interpreter used by the recognizer for prediction.</remarks>
        /// <returns>The ATN interpreter used by the recognizer for prediction.</returns>
        /// <summary>Set the ATN interpreter used by the recognizer for prediction.</summary>
        /// <remarks>Set the ATN interpreter used by the recognizer for prediction.</remarks>
        /// <value>
        /// The ATN interpreter used by the recognizer for
        /// prediction.
        /// </value>
        public virtual ATNInterpreter Interpreter
        {
            get
            {
                return _interp;
            }
            protected set
            {
				_interp = value;
            }
        }

        /// <summary>
        /// If profiling during the parse/lex, this will return DecisionInfo records
        /// for each decision in recognizer in a ParseInfo object.
        /// </summary>
        /// <remarks>
        /// If profiling during the parse/lex, this will return DecisionInfo records
        /// for each decision in recognizer in a ParseInfo object.
        /// </remarks>
        /// <since>4.3</since>
        public virtual Antlr4.Runtime.Atn.ParseInfo ParseInfo
        {
            get
            {
                return null;
            }
        }

        /// <summary>What is the error header, normally line/character position information?</summary>
        [return: NotNull]
        public virtual string GetErrorHeader(RecognitionException e)
        {
            int line = e.OffendingToken.Line;
            int charPositionInLine = e.OffendingToken.Column;
            return "line " + line + ":" + charPositionInLine;
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
        [ObsoleteAttribute(@"This method is not called by the ANTLR 4 Runtime. Specific implementations of IAntlrErrorStrategy may provide a similar feature when necessary. For example, see DefaultErrorStrategy.GetTokenErrorDisplay(IToken).")]
        public virtual string GetTokenErrorDisplay(IToken t)
        {
            if (t == null)
            {
                return "<no token>";
            }
            string s = t.Text;
            if (s == null)
            {
                if (t.Type == TokenConstants.EOF)
                {
                    s = "<EOF>";
                }
                else
                {
                    s = "<" + t.Type + ">";
                }
            }
            s = s.Replace("\n", "\\n");
            s = s.Replace("\r", "\\r");
            s = s.Replace("\t", "\\t");
            return "'" + s + "'";
        }

        /// <exception>
        /// NullPointerException
        /// if
        /// <paramref name="listener"/>
        /// is
        /// <see langword="null"/>
        /// .
        /// </exception>
        public virtual void AddErrorListener(IAntlrErrorListener<Symbol> listener)
        {
            Args.NotNull("listener", listener);

            IAntlrErrorListener<Symbol>[] listeners = _listeners;
            Array.Resize(ref listeners, listeners.Length + 1);
            listeners[listeners.Length - 1] = listener;
            _listeners = listeners;
        }

        public virtual void RemoveErrorListener(IAntlrErrorListener<Symbol> listener)
        {
            IAntlrErrorListener<Symbol>[] listeners = _listeners;
            int removeIndex = Array.IndexOf(listeners, listener);
            if (removeIndex < 0)
                return;

            Array.Copy(listeners, removeIndex + 1, listeners, removeIndex, listeners.Length - removeIndex - 1);
            Array.Resize(ref listeners, listeners.Length - 1);
            _listeners = listeners;
        }

        public virtual void RemoveErrorListeners()
        {
            _listeners = new IAntlrErrorListener<Symbol>[0];
        }

        [NotNull]
        public virtual IList<IAntlrErrorListener<Symbol>> ErrorListeners
        {
            get
            {
                return new List<IAntlrErrorListener<Symbol>>(_listeners);
            }
        }

        public virtual IAntlrErrorListener<Symbol> ErrorListenerDispatch
        {
            get
            {
                return new ProxyErrorListener<Symbol>(ErrorListeners);
            }
        }

        // subclass needs to override these if there are sempreds or actions
        // that the ATN interp needs to execute
        public virtual bool Sempred(RuleContext _localctx, int ruleIndex, int actionIndex)
        {
            return true;
        }

        public virtual bool Precpred(RuleContext localctx, int precedence)
        {
            return true;
        }

        public virtual void Action(RuleContext _localctx, int ruleIndex, int actionIndex)
        {
        }

        /// <summary>
        /// Indicate that the recognizer has changed internal state that is
        /// consistent with the ATN state passed in.
        /// </summary>
        /// <remarks>
        /// Indicate that the recognizer has changed internal state that is
        /// consistent with the ATN state passed in.  This way we always know
        /// where we are in the ATN as the parser goes along. The rule
        /// context objects form a stack that lets us see the stack of
        /// invoking rules. Combine this and we have complete ATN
        /// configuration information.
        /// </remarks>
        public int State
        {
            get
            {
                return _stateNumber;
            }
            set
            {
                int atnState = value;
                //		System.err.println("setState "+atnState);
                _stateNumber = atnState;
            }
        }

        public abstract IIntStream InputStream
        {
            get;
        }
        //		if ( traceATNStates ) _ctx.trace(atnState);
    }
}
