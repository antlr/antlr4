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
using System.Collections.Generic;
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;
using Sharpen;

namespace Antlr4.Runtime
{
    public abstract class Recognizer<Symbol, ATNInterpreter> : IRecognizer
        where ATNInterpreter : ATNSimulator
    {
        public const int Eof = -1;

        [NotNull]
        private IAntlrErrorListener<Symbol>[] _listeners =
        {
#if !PORTABLE
            ConsoleErrorListener<Symbol>.Instance
#endif
        };

        protected internal ATNInterpreter _interp;

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
        public abstract string[] TokenNames
        {
            get;
        }

        public abstract string[] RuleNames
        {
            get;
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

        public virtual ATN Atn
        {
            get
            {
                return _interp.atn;
            }
        }

        public virtual ATNInterpreter Interpreter
        {
            get
            {
                return _interp;
            }
            set
            {
                ATNInterpreter interpreter = value;
                _interp = interpreter;
            }
        }

        /// <summary>What is the error header, normally line/character position information?</summary>
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
        public virtual string GetTokenErrorDisplay(IToken t)
        {
            if (t == null)
            {
                return "<no token>";
            }
            string s = t.Text;
            if (s == null)
            {
                if (t.Type == TokenConstants.Eof)
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

        /// <exception cref="System.ArgumentNullException">
        /// if
        /// <code>listener</code>
        /// is
        /// <code>null</code>
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

        [return: NotNull]
        public virtual IList<IAntlrErrorListener<Symbol>> GetErrorListeners()
        {
            return new List<IAntlrErrorListener<Symbol>>(_listeners);
        }

        public virtual IAntlrErrorListener<Symbol> GetErrorListenerDispatch()
        {
            return new ProxyErrorListener<Symbol>(GetErrorListeners());
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
