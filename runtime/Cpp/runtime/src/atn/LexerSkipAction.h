/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
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

#pragma once

#include "atn/LexerAction.h"
#include "atn/LexerActionType.h"

namespace antlr4 {
namespace atn {

  /// <summary>
  /// Implements the {@code skip} lexer action by calling <seealso cref="Lexer#skip"/>.
  ///
  /// <para>The {@code skip} command does not have any parameters, so this action is
  /// implemented as a singleton instance exposed by <seealso cref="#INSTANCE"/>.</para>
  ///
  /// @author Sam Harwell
  /// @since 4.2
  /// </summary>
  class ANTLR4CPP_PUBLIC LexerSkipAction final : public LexerAction {
  public:
    /// Provides a singleton instance of this parameterless lexer action.
    static const Ref<LexerSkipAction> getInstance();

    /// <summary>
    /// {@inheritDoc} </summary>
    /// <returns> This method returns <seealso cref="LexerActionType#SKIP"/>. </returns>
    virtual LexerActionType getActionType() const override;

    /// <summary>
    /// {@inheritDoc} </summary>
    /// <returns> This method returns {@code false}. </returns>
    virtual bool isPositionDependent() const override;

    /// <summary>
    /// {@inheritDoc}
    ///
    /// <para>This action is implemented by calling <seealso cref="Lexer#skip"/>.</para>
    /// </summary>
    virtual void execute(Lexer *lexer) override;

    virtual size_t hashCode() const override;
    virtual bool operator == (const LexerAction &obj) const override;
    virtual std::string toString() const override;

  private:
    /// Constructs the singleton instance of the lexer {@code skip} command.
    LexerSkipAction();
  };

} // namespace atn
} // namespace antlr4
