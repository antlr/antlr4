/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
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
