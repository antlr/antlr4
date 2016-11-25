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

#include "atn/LexerActionType.h"
#include "atn/LexerAction.h"

namespace antlr4 {
namespace atn {

  /// Implements the {@code type} lexer action by calling <seealso cref="Lexer#setType"/>
  /// with the assigned type.
  class ANTLR4CPP_PUBLIC LexerTypeAction : public LexerAction {
  public:
    /// <summary>
    /// Constructs a new {@code type} action with the specified token type value. </summary>
    /// <param name="type"> The type to assign to the token using <seealso cref="Lexer#setType"/>. </param>
    LexerTypeAction(int type);

    /// <summary>
    /// Gets the type to assign to a token created by the lexer. </summary>
    /// <returns> The type to assign to a token created by the lexer. </returns>
    virtual int getType() const;

    /// <summary>
    /// {@inheritDoc} </summary>
    /// <returns> This method returns <seealso cref="LexerActionType#TYPE"/>. </returns>
    virtual LexerActionType getActionType() const override;

    /// <summary>
    /// {@inheritDoc} </summary>
    /// <returns> This method returns {@code false}. </returns>
    virtual bool isPositionDependent() const override;

    /// <summary>
    /// {@inheritDoc}
    ///
    /// <para>This action is implemented by calling <seealso cref="Lexer#setType"/> with the
    /// value provided by <seealso cref="#getType"/>.</para>
    /// </summary>
    virtual void execute(Lexer *lexer) override;

    virtual size_t hashCode() const override;
    virtual bool operator == (const LexerAction &obj) const override;
    virtual std::string toString() const override;

  private:
    const int _type;
  };

} // namespace atn
} // namespace antlr4
