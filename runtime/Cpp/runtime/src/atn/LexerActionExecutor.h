/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 *  Copyright (c) 2014 Terence Parr
 *  Copyright (c) 2014 Dan McLaughlin
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

#pragma once

#include "CharStream.h"
#include "atn/LexerAction.h"

namespace antlr4 {
namespace atn {

  /// Represents an executor for a sequence of lexer actions which traversed during
  /// the matching operation of a lexer rule (token).
  ///
  /// <para>The executor tracks position information for position-dependent lexer actions
  /// efficiently, ensuring that actions appearing only at the end of the rule do
  /// not cause bloating of the <seealso cref="DFA"/> created for the lexer.</para>
  class ANTLR4CPP_PUBLIC LexerActionExecutor : public std::enable_shared_from_this<LexerActionExecutor> {
  public:
    /// <summary>
    /// Constructs an executor for a sequence of <seealso cref="LexerAction"/> actions. </summary>
    /// <param name="lexerActions"> The lexer actions to execute. </param>
    LexerActionExecutor(const std::vector<Ref<LexerAction>> &lexerActions);
    virtual ~LexerActionExecutor() {};

    /// <summary>
    /// Creates a <seealso cref="LexerActionExecutor"/> which executes the actions for
    /// the input {@code lexerActionExecutor} followed by a specified
    /// {@code lexerAction}.
    /// </summary>
    /// <param name="lexerActionExecutor"> The executor for actions already traversed by
    /// the lexer while matching a token within a particular
    /// <seealso cref="LexerATNConfig"/>. If this is {@code null}, the method behaves as
    /// though it were an empty executor. </param>
    /// <param name="lexerAction"> The lexer action to execute after the actions
    /// specified in {@code lexerActionExecutor}.
    /// </param>
    /// <returns> A <seealso cref="LexerActionExecutor"/> for executing the combine actions
    /// of {@code lexerActionExecutor} and {@code lexerAction}. </returns>
    static Ref<LexerActionExecutor> append(Ref<LexerActionExecutor> const& lexerActionExecutor,
                                           Ref<LexerAction> const& lexerAction);

    /// <summary>
    /// Creates a <seealso cref="LexerActionExecutor"/> which encodes the current offset
    /// for position-dependent lexer actions.
    ///
    /// <para>Normally, when the executor encounters lexer actions where
    /// <seealso cref="LexerAction#isPositionDependent"/> returns {@code true}, it calls
    /// <seealso cref="IntStream#seek"/> on the input <seealso cref="CharStream"/> to set the input
    /// position to the <em>end</em> of the current token. This behavior provides
    /// for efficient DFA representation of lexer actions which appear at the end
    /// of a lexer rule, even when the lexer rule matches a variable number of
    /// characters.</para>
    ///
    /// <para>Prior to traversing a match transition in the ATN, the current offset
    /// from the token start index is assigned to all position-dependent lexer
    /// actions which have not already been assigned a fixed offset. By storing
    /// the offsets relative to the token start index, the DFA representation of
    /// lexer actions which appear in the middle of tokens remains efficient due
    /// to sharing among tokens of the same length, regardless of their absolute
    /// position in the input stream.</para>
    ///
    /// <para>If the current executor already has offsets assigned to all
    /// position-dependent lexer actions, the method returns {@code this}.</para>
    /// </summary>
    /// <param name="offset"> The current offset to assign to all position-dependent
    /// lexer actions which do not already have offsets assigned.
    /// </param>
    /// <returns> A <seealso cref="LexerActionExecutor"/> which stores input stream offsets
    /// for all position-dependent lexer actions. </returns>
    virtual Ref<LexerActionExecutor> fixOffsetBeforeMatch(int offset);

    /// <summary>
    /// Gets the lexer actions to be executed by this executor. </summary>
    /// <returns> The lexer actions to be executed by this executor. </returns>
    virtual std::vector<Ref<LexerAction>> getLexerActions() const;

    /// <summary>
    /// Execute the actions encapsulated by this executor within the context of a
    /// particular <seealso cref="Lexer"/>.
    ///
    /// <para>This method calls <seealso cref="IntStream#seek"/> to set the position of the
    /// {@code input} <seealso cref="CharStream"/> prior to calling
    /// <seealso cref="LexerAction#execute"/> on a position-dependent action. Before the
    /// method returns, the input position will be restored to the same position
    /// it was in when the method was invoked.</para>
    /// </summary>
    /// <param name="lexer"> The lexer instance. </param>
    /// <param name="input"> The input stream which is the source for the current token.
    /// When this method is called, the current <seealso cref="IntStream#index"/> for
    /// {@code input} should be the start of the following token, i.e. 1
    /// character past the end of the current token. </param>
    /// <param name="startIndex"> The token start index. This value may be passed to
    /// <seealso cref="IntStream#seek"/> to set the {@code input} position to the beginning
    /// of the token. </param>
    virtual void execute(Lexer *lexer, CharStream *input, int startIndex);

    virtual size_t hashCode() const;
    virtual bool operator == (const LexerActionExecutor &obj) const;

  private:
    const std::vector<Ref<LexerAction>> _lexerActions;

    /// Caches the result of <seealso cref="#hashCode"/> since the hash code is an element
    /// of the performance-critical <seealso cref="LexerATNConfig#hashCode"/> operation.
    const size_t _hashCode;

    size_t generateHashCode() const;
  };

} // namespace atn
} // namespace antlr4
