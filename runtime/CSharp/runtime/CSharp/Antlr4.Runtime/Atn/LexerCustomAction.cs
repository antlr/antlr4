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
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    /// <summary>
    /// Executes a custom lexer action by calling
    /// <see cref="Antlr4.Runtime.Recognizer{Symbol, ATNInterpreter}.Action(Antlr4.Runtime.RuleContext, int, int)"/>
    /// with the
    /// rule and action indexes assigned to the custom action. The implementation of
    /// a custom action is added to the generated code for the lexer in an override
    /// of
    /// <see cref="Antlr4.Runtime.Recognizer{Symbol, ATNInterpreter}.Action(Antlr4.Runtime.RuleContext, int, int)"/>
    /// when the grammar is compiled.
    /// <p>This class may represent embedded actions created with the <code>{...}</code>
    /// syntax in ANTLR 4, as well as actions created for lexer commands where the
    /// command argument could not be evaluated when the grammar was compiled.</p>
    /// </summary>
    /// <author>Sam Harwell</author>
    /// <since>4.2</since>
    public sealed class LexerCustomAction : ILexerAction
    {
        private readonly int ruleIndex;

        private readonly int actionIndex;

        /// <summary>
        /// Constructs a custom lexer action with the specified rule and action
        /// indexes.
        /// </summary>
        /// <remarks>
        /// Constructs a custom lexer action with the specified rule and action
        /// indexes.
        /// </remarks>
        /// <param name="ruleIndex">
        /// The rule index to use for calls to
        /// <see cref="Antlr4.Runtime.Recognizer{Symbol, ATNInterpreter}.Action(Antlr4.Runtime.RuleContext, int, int)"/>
        /// .
        /// </param>
        /// <param name="actionIndex">
        /// The action index to use for calls to
        /// <see cref="Antlr4.Runtime.Recognizer{Symbol, ATNInterpreter}.Action(Antlr4.Runtime.RuleContext, int, int)"/>
        /// .
        /// </param>
        public LexerCustomAction(int ruleIndex, int actionIndex)
        {
            this.ruleIndex = ruleIndex;
            this.actionIndex = actionIndex;
        }

        /// <summary>
        /// Gets the rule index to use for calls to
        /// <see cref="Antlr4.Runtime.Recognizer{Symbol, ATNInterpreter}.Action(Antlr4.Runtime.RuleContext, int, int)"/>
        /// .
        /// </summary>
        /// <returns>The rule index for the custom action.</returns>
        public int RuleIndex
        {
            get
            {
                return ruleIndex;
            }
        }

        /// <summary>
        /// Gets the action index to use for calls to
        /// <see cref="Antlr4.Runtime.Recognizer{Symbol, ATNInterpreter}.Action(Antlr4.Runtime.RuleContext, int, int)"/>
        /// .
        /// </summary>
        /// <returns>The action index for the custom action.</returns>
        public int ActionIndex
        {
            get
            {
                return actionIndex;
            }
        }

        /// <summary><inheritDoc/></summary>
        /// <returns>
        /// This method returns
        /// <see cref="LexerActionType.Custom"/>
        /// .
        /// </returns>
        public LexerActionType ActionType
        {
            get
            {
                return LexerActionType.Custom;
            }
        }

        /// <summary>Gets whether the lexer action is position-dependent.</summary>
        /// <remarks>
        /// Gets whether the lexer action is position-dependent. Position-dependent
        /// actions may have different semantics depending on the
        /// <see cref="Antlr4.Runtime.ICharStream"/>
        /// index at the time the action is executed.
        /// <p>Custom actions are position-dependent since they may represent a
        /// user-defined embedded action which makes calls to methods like
        /// <see cref="Antlr4.Runtime.Lexer.Text()"/>
        /// .</p>
        /// </remarks>
        /// <returns>
        /// This method returns
        /// <see langword="true"/>
        /// .
        /// </returns>
        public bool IsPositionDependent
        {
            get
            {
                return true;
            }
        }

        /// <summary>
        /// <inheritDoc/>
        /// <p>Custom actions are implemented by calling
        /// <see cref="Antlr4.Runtime.Recognizer{Symbol, ATNInterpreter}.Action(Antlr4.Runtime.RuleContext, int, int)"/>
        /// with the
        /// appropriate rule and action indexes.</p>
        /// </summary>
        public void Execute(Lexer lexer)
        {
            lexer.Action(null, ruleIndex, actionIndex);
        }

        public override int GetHashCode()
        {
            int hash = MurmurHash.Initialize();
            hash = MurmurHash.Update(hash, (int)(ActionType));
            hash = MurmurHash.Update(hash, ruleIndex);
            hash = MurmurHash.Update(hash, actionIndex);
            return MurmurHash.Finish(hash, 3);
        }

        public override bool Equals(object obj)
        {
            if (obj == this)
            {
                return true;
            }
            else
            {
                if (!(obj is Antlr4.Runtime.Atn.LexerCustomAction))
                {
                    return false;
                }
            }
            Antlr4.Runtime.Atn.LexerCustomAction other = (Antlr4.Runtime.Atn.LexerCustomAction)obj;
            return ruleIndex == other.ruleIndex && actionIndex == other.actionIndex;
        }
    }
}
