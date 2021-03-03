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
    /// This implementation of
    /// <see cref="ILexerAction"/>
    /// is used for tracking input offsets
    /// for position-dependent actions within a
    /// <see cref="LexerActionExecutor"/>
    /// .
    /// <p>This action is not serialized as part of the ATN, and is only required for
    /// position-dependent lexer actions which appear at a location other than the
    /// end of a rule. For more information about DFA optimizations employed for
    /// lexer actions, see
    /// <see cref="LexerActionExecutor.Append(LexerActionExecutor, ILexerAction)"/>
    /// and
    /// <see cref="LexerActionExecutor.FixOffsetBeforeMatch(int)"/>
    /// .</p>
    /// </summary>
    /// <author>Sam Harwell</author>
    /// <since>4.2</since>
    public sealed class LexerIndexedCustomAction : ILexerAction
    {
        private readonly int offset;

        private readonly ILexerAction action;

        /// <summary>
        /// Constructs a new indexed custom action by associating a character offset
        /// with a
        /// <see cref="ILexerAction"/>
        /// .
        /// <p>Note: This class is only required for lexer actions for which
        /// <see cref="ILexerAction.IsPositionDependent()"/>
        /// returns
        /// <see langword="true"/>
        /// .</p>
        /// </summary>
        /// <param name="offset">
        /// The offset into the input
        /// <see cref="Antlr4.Runtime.ICharStream"/>
        /// , relative to
        /// the token start index, at which the specified lexer action should be
        /// executed.
        /// </param>
        /// <param name="action">
        /// The lexer action to execute at a particular offset in the
        /// input
        /// <see cref="Antlr4.Runtime.ICharStream"/>
        /// .
        /// </param>
        public LexerIndexedCustomAction(int offset, ILexerAction action)
        {
            this.offset = offset;
            this.action = action;
        }

        /// <summary>
        /// Gets the location in the input
        /// <see cref="Antlr4.Runtime.ICharStream"/>
        /// at which the lexer
        /// action should be executed. The value is interpreted as an offset relative
        /// to the token start index.
        /// </summary>
        /// <returns>
        /// The location in the input
        /// <see cref="Antlr4.Runtime.ICharStream"/>
        /// at which the lexer
        /// action should be executed.
        /// </returns>
        public int Offset
        {
            get
            {
                return offset;
            }
        }

        /// <summary>Gets the lexer action to execute.</summary>
        /// <remarks>Gets the lexer action to execute.</remarks>
        /// <returns>
        /// A
        /// <see cref="ILexerAction"/>
        /// object which executes the lexer action.
        /// </returns>
        [NotNull]
        public ILexerAction Action
        {
            get
            {
                return action;
            }
        }

        /// <summary><inheritDoc/></summary>
        /// <returns>
        /// This method returns the result of calling
        /// <see cref="ActionType()"/>
        /// on the
        /// <see cref="ILexerAction"/>
        /// returned by
        /// <see cref="Action()"/>
        /// .
        /// </returns>
        public LexerActionType ActionType
        {
            get
            {
                return action.ActionType;
            }
        }

        /// <summary><inheritDoc/></summary>
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
        /// <p>This method calls
        /// <see cref="Execute(Antlr4.Runtime.Lexer)"/>
        /// on the result of
        /// <see cref="Action()"/>
        /// using the provided
        /// <paramref name="lexer"/>
        /// .</p>
        /// </summary>
        public void Execute(Lexer lexer)
        {
            // assume the input stream position was properly set by the calling code
            action.Execute(lexer);
        }

        public override int GetHashCode()
        {
            int hash = MurmurHash.Initialize();
            hash = MurmurHash.Update(hash, offset);
            hash = MurmurHash.Update(hash, action);
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
                if (!(obj is Antlr4.Runtime.Atn.LexerIndexedCustomAction))
                {
                    return false;
                }
            }
            Antlr4.Runtime.Atn.LexerIndexedCustomAction other = (Antlr4.Runtime.Atn.LexerIndexedCustomAction)obj;
            return offset == other.offset && action.Equals(other.action);
        }
    }
}
