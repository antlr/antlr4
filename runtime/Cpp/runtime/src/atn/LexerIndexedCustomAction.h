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

#include "RuleContext.h"
#include "atn/LexerAction.h"

namespace antlr4 {
namespace atn {

  /// <summary>
  /// This implementation of <seealso cref="LexerAction"/> is used for tracking input offsets
  /// for position-dependent actions within a <seealso cref="LexerActionExecutor"/>.
  ///
  /// <para>This action is not serialized as part of the ATN, and is only required for
  /// position-dependent lexer actions which appear at a location other than the
  /// end of a rule. For more information about DFA optimizations employed for
  /// lexer actions, see <seealso cref="LexerActionExecutor#append"/> and
  /// <seealso cref="LexerActionExecutor#fixOffsetBeforeMatch"/>.</para>
  ///
  /// @author Sam Harwell
  /// @since 4.2
  /// </summary>
  class ANTLR4CPP_PUBLIC LexerIndexedCustomAction final : public LexerAction {
  public:
    /// <summary>
    /// Constructs a new indexed custom action by associating a character offset
    /// with a <seealso cref="LexerAction"/>.
    ///
    /// <para>Note: This class is only required for lexer actions for which
    /// <seealso cref="LexerAction#isPositionDependent"/> returns {@code true}.</para>
    /// </summary>
    /// <param name="offset"> The offset into the input <seealso cref="CharStream"/>, relative to
    /// the token start index, at which the specified lexer action should be
    /// executed. </param>
    /// <param name="action"> The lexer action to execute at a particular offset in the
    /// input <seealso cref="CharStream"/>. </param>
    LexerIndexedCustomAction(int offset, Ref<LexerAction> const& action);

    /// <summary>
    /// Gets the location in the input <seealso cref="CharStream"/> at which the lexer
    /// action should be executed. The value is interpreted as an offset relative
    /// to the token start index.
    /// </summary>
    /// <returns> The location in the input <seealso cref="CharStream"/> at which the lexer
    /// action should be executed. </returns>
    int getOffset() const;

    /// <summary>
    /// Gets the lexer action to execute.
    /// </summary>
    /// <returns> A <seealso cref="LexerAction"/> object which executes the lexer action. </returns>
    Ref<LexerAction> getAction() const;

    /// <summary>
    /// {@inheritDoc}
    /// </summary>
    /// <returns> This method returns the result of calling <seealso cref="#getActionType"/>
    /// on the <seealso cref="LexerAction"/> returned by <seealso cref="#getAction"/>. </returns>
    virtual LexerActionType getActionType() const override;

    /// <summary>
    /// {@inheritDoc} </summary>
    /// <returns> This method returns {@code true}. </returns>
    virtual bool isPositionDependent() const override;

    virtual void execute(Lexer *lexer) override;
    virtual size_t hashCode() const override;
    virtual bool operator == (const LexerAction &obj) const override;
    virtual std::string toString() const override;

  private:
    const int _offset;
    const Ref<LexerAction> _action;
  };

} // namespace atn
} // namespace antlr4

