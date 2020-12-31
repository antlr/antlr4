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
    /// <c>channel</c>
    /// lexer action by calling
    /// <see cref="Lexer.Channel"/>
    /// with the assigned channel.
    /// </summary>
    /// <author>Sam Harwell</author>
    /// <since>4.2</since>
    public sealed class LexerChannelAction : ILexerAction
    {
        private readonly int channel;

        /// <summary>
        /// Constructs a new
        /// <paramref name="channel"/>
        /// action with the specified channel value.
        /// </summary>
        /// <param name="channel">
        /// The channel value to pass to
        /// <see cref="Lexer.Channel"/>
        /// .
        /// </param>
        public LexerChannelAction(int channel)
        {
            this.channel = channel;
        }

        /// <summary>
        /// Gets the channel to use for the
        /// <see cref="Antlr4.Runtime.IToken"/>
        /// created by the lexer.
        /// </summary>
        /// <returns>
        /// The channel to use for the
        /// <see cref="Antlr4.Runtime.IToken"/>
        /// created by the lexer.
        /// </returns>
        public int Channel
        {
            get
            {
                return channel;
            }
        }

        /// <summary><inheritDoc/></summary>
        /// <returns>
        /// This method returns
        /// <see cref="LexerActionType.Channel"/>
        /// .
        /// </returns>
        public LexerActionType ActionType
        {
            get
            {
                return LexerActionType.Channel;
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
        /// <see cref="Lexer.Channel"/>
        /// with the
        /// value provided by
        /// <see cref="Channel()"/>
        /// .</p>
        /// </summary>
        public void Execute(Lexer lexer)
        {
            lexer.Channel = channel;
        }

        public override int GetHashCode()
        {
            int hash = MurmurHash.Initialize();
            hash = MurmurHash.Update(hash, (int)(ActionType));
            hash = MurmurHash.Update(hash, channel);
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
                if (!(obj is Antlr4.Runtime.Atn.LexerChannelAction))
                {
                    return false;
                }
            }
            return channel == ((Antlr4.Runtime.Atn.LexerChannelAction)obj).channel;
        }

        public override string ToString()
        {
            return string.Format("channel({0})", channel);
        }
    }
}
