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
    /// Represents a single action which can be executed following the successful
    /// match of a lexer rule.
    /// </summary>
    /// <remarks>
    /// Represents a single action which can be executed following the successful
    /// match of a lexer rule. Lexer actions are used for both embedded action syntax
    /// and ANTLR 4's new lexer command syntax.
    /// </remarks>
    /// <author>Sam Harwell</author>
    /// <since>4.2</since>
    public interface ILexerAction
    {
        /// <summary>Gets the serialization type of the lexer action.</summary>
        /// <remarks>Gets the serialization type of the lexer action.</remarks>
        /// <returns>The serialization type of the lexer action.</returns>
        [NotNull]
        LexerActionType ActionType
        {
            get;
        }

        /// <summary>Gets whether the lexer action is position-dependent.</summary>
        /// <remarks>
        /// Gets whether the lexer action is position-dependent. Position-dependent
        /// actions may have different semantics depending on the
        /// <see cref="Antlr4.Runtime.ICharStream"/>
        /// index at the time the action is executed.
        /// <p>Many lexer commands, including
        /// <c>type</c>
        /// ,
        /// <c>skip</c>
        /// , and
        /// <c>more</c>
        /// , do not check the input index during their execution.
        /// Actions like this are position-independent, and may be stored more
        /// efficiently as part of the
        /// <see cref="ATNConfig.ActionExecutor()"/>
        /// .</p>
        /// </remarks>
        /// <returns>
        /// 
        /// <see langword="true"/>
        /// if the lexer action semantics can be affected by the
        /// position of the input
        /// <see cref="Antlr4.Runtime.ICharStream"/>
        /// at the time it is executed;
        /// otherwise,
        /// <see langword="false"/>
        /// .
        /// </returns>
        bool IsPositionDependent
        {
            get;
        }

        /// <summary>
        /// Execute the lexer action in the context of the specified
        /// <see cref="Antlr4.Runtime.Lexer"/>
        /// .
        /// <p>For position-dependent actions, the input stream must already be
        /// positioned correctly prior to calling this method.</p>
        /// </summary>
        /// <param name="lexer">The lexer instance.</param>
        void Execute(Lexer lexer);
    }
}
