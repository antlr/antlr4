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
    /// Implements the
    /// <c>mode</c>
    /// lexer action by calling
    /// <see cref="Antlr4.Runtime.Lexer.Mode(int)"/>
    /// with
    /// the assigned mode.
    /// </summary>
    /// <author>Sam Harwell</author>
    /// <since>4.2</since>
    public sealed class LexerModeAction : ILexerAction
    {
        private readonly int mode;

        /// <summary>
        /// Constructs a new
        /// <paramref name="mode"/>
        /// action with the specified mode value.
        /// </summary>
        /// <param name="mode">
        /// The mode value to pass to
        /// <see cref="Antlr4.Runtime.Lexer.Mode(int)"/>
        /// .
        /// </param>
        public LexerModeAction(int mode)
        {
            this.mode = mode;
        }

        /// <summary>Get the lexer mode this action should transition the lexer to.</summary>
        /// <remarks>Get the lexer mode this action should transition the lexer to.</remarks>
        /// <returns>
        /// The lexer mode for this
        /// <c>mode</c>
        /// command.
        /// </returns>
        public int Mode
        {
            get
            {
                return mode;
            }
        }

        /// <summary><inheritDoc/></summary>
        /// <returns>
        /// This method returns
        /// <see cref="LexerActionType.Mode"/>
        /// .
        /// </returns>
        public LexerActionType ActionType
        {
            get
            {
                return LexerActionType.Mode;
            }
        }

        /// <summary><inheritDoc/></summary>
        /// <returns>
        /// This method returns
        /// <see langword="false"/>
        /// .
        /// </returns>
        public bool IsPositionDependent
        {
            get
            {
                return false;
            }
        }

        /// <summary>
        /// <inheritDoc/>
        /// <p>This action is implemented by calling
        /// <see cref="Antlr4.Runtime.Lexer.Mode(int)"/>
        /// with the
        /// value provided by
        /// <see cref="Mode()"/>
        /// .</p>
        /// </summary>
        public void Execute(Lexer lexer)
        {
            lexer.Mode(mode);
        }

        public override int GetHashCode()
        {
            int hash = MurmurHash.Initialize();
            hash = MurmurHash.Update(hash, (int)(ActionType));
            hash = MurmurHash.Update(hash, mode);
            return MurmurHash.Finish(hash, 2);
        }

        public override bool Equals(object obj)
        {
            if (obj == this)
            {
                return true;
            }
            else
            {
                if (!(obj is Antlr4.Runtime.Atn.LexerModeAction))
                {
                    return false;
                }
            }
            return mode == ((Antlr4.Runtime.Atn.LexerModeAction)obj).mode;
        }

        public override string ToString()
        {
            return string.Format("mode({0})", mode);
        }
    }
}
