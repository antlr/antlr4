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
  /// Executes a custom lexer action by calling <seealso cref="Recognizer#action"/> with the
  /// rule and action indexes assigned to the custom action. The implementation of
  /// a custom action is added to the generated code for the lexer in an override
  /// of <seealso cref="Recognizer#action"/> when the grammar is compiled.
  ///
  /// <para>This class may represent embedded actions created with the <code>{...}</code>
  /// syntax in ANTLR 4, as well as actions created for lexer commands where the
  /// command argument could not be evaluated when the grammar was compiled.</para>
  ///
  /// @author Sam Harwell
  /// @since 4.2
  /// </summary>
  class ANTLR4CPP_PUBLIC LexerCustomAction final : public LexerAction {
  public:
    /// <summary>
    /// Constructs a custom lexer action with the specified rule and action
    /// indexes.
    /// </summary>
    /// <param name="ruleIndex"> The rule index to use for calls to
    /// <seealso cref="Recognizer#action"/>. </param>
    /// <param name="actionIndex"> The action index to use for calls to
    /// <seealso cref="Recognizer#action"/>. </param>
    LexerCustomAction(int ruleIndex, int actionIndex);

    /// <summary>
    /// Gets the rule index to use for calls to <seealso cref="Recognizer#action"/>.
    /// </summary>
    /// <returns> The rule index for the custom action. </returns>
    int getRuleIndex() const;

    /// <summary>
    /// Gets the action index to use for calls to <seealso cref="Recognizer#action"/>.
    /// </summary>
    /// <returns> The action index for the custom action. </returns>
    int getActionIndex() const;

    /// <summary>
    /// {@inheritDoc}
    /// </summary>
    /// <returns> This method returns <seealso cref="LexerActionType#CUSTOM"/>. </returns>
    virtual LexerActionType getActionType() const override;

    /// <summary>
    /// Gets whether the lexer action is position-dependent. Position-dependent
    /// actions may have different semantics depending on the <seealso cref="CharStream"/>
    /// index at the time the action is executed.
    ///
    /// <para>Custom actions are position-dependent since they may represent a
    /// user-defined embedded action which makes calls to methods like
    /// <seealso cref="Lexer#getText"/>.</para>
    /// </summary>
    /// <returns> This method returns {@code true}. </returns>
    virtual bool isPositionDependent() const override;

    /// <summary>
    /// {@inheritDoc}
    ///
    /// <para>Custom actions are implemented by calling <seealso cref="Lexer#action"/> with the
    /// appropriate rule and action indexes.</para>
    /// </summary>
    virtual void execute(Lexer *lexer) override;

    virtual size_t hashCode() const override;
    virtual bool operator == (const LexerAction &obj) const override;
    virtual std::string toString() const override;

  private:
    const int _ruleIndex;
    const int _actionIndex;
  };

} // namespace atn
} // namespace antlr4
