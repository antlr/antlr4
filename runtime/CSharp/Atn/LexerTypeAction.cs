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
    /// <c>type</c>
    /// lexer action by calling
    /// <see cref="Lexer.Type"/>
    /// with the assigned type.
    /// </summary>
    /// <author>Sam Harwell</author>
    /// <since>4.2</since>
    public class LexerTypeAction : ILexerAction
    {
        private readonly int type;

        /// <summary>
        /// Constructs a new
        /// <paramref name="type"/>
        /// action with the specified token type value.
        /// </summary>
        /// <param name="type">
        /// The type to assign to the token using
        /// <see cref="Lexer.Type"/>
        /// .
        /// </param>
        public LexerTypeAction(int type)
        {
            this.type = type;
        }

        /// <summary>Gets the type to assign to a token created by the lexer.</summary>
        /// <remarks>Gets the type to assign to a token created by the lexer.</remarks>
        /// <returns>The type to assign to a token created by the lexer.</returns>
        public virtual int Type
        {
            get
            {
                return type;
            }
        }

        /// <summary><inheritDoc/></summary>
        /// <returns>
        /// This method returns
        /// <see cref="LexerActionType.Type"/>
        /// .
        /// </returns>
        public virtual LexerActionType ActionType
        {
            get
            {
                return LexerActionType.Type;
            }
        }

        /// <summary><inheritDoc/></summary>
        /// <returns>
        /// This method returns
        /// <see langword="false"/>
        /// .
        /// </returns>
        public virtual bool IsPositionDependent
        {
            get
            {
                return false;
            }
        }

        /// <summary>
        /// <inheritDoc/>
        /// <p>This action is implemented by calling
        /// <see cref="Lexer.Type"/>
        /// with the
        /// value provided by
        /// <see cref="Type()"/>
        /// .</p>
        /// </summary>
        public virtual void Execute(Lexer lexer)
        {
            lexer.Type = type;
        }

        public override int GetHashCode()
        {
            int hash = MurmurHash.Initialize();
            hash = MurmurHash.Update(hash, (int)(ActionType));
            hash = MurmurHash.Update(hash, type);
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
                if (!(obj is Antlr4.Runtime.Atn.LexerTypeAction))
                {
                    return false;
                }
            }
            return type == ((Antlr4.Runtime.Atn.LexerTypeAction)obj).type;
        }

        public override string ToString()
        {
            return string.Format("type({0})", type);
        }
    }
}
