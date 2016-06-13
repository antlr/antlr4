/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Dan McLaughlin
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

#include "Exceptions.h"

namespace antlr4 {

  /// The root of the ANTLR exception hierarchy. In general, ANTLR tracks just
  /// 3 kinds of errors: prediction errors, failed predicate errors, and
  /// mismatched input errors. In each case, the parser knows where it is
  /// in the input, where it is in the ATN, the rule invocation stack,
  /// and what kind of problem occurred.
  class ANTLR4CPP_PUBLIC RecognitionException : public RuntimeException {
  private:
    /// The Recognizer where this exception originated.
    IRecognizer *_recognizer;
    IntStream *_input;
    Ref<ParserRuleContext> _ctx;

    /// The current Token when an error occurred. Since not all streams
    /// support accessing symbols by index, we have to track the Token
    /// instance itself.
    Token *_offendingToken;

    int _offendingState;

  public:
    RecognitionException(IRecognizer *recognizer, IntStream *input, Ref<ParserRuleContext> const& ctx,
                         Token *offendingToken = nullptr);
    RecognitionException(const std::string &message, IRecognizer *recognizer, IntStream *input,
                         Ref<ParserRuleContext> const& ctx, Token *offendingToken = nullptr);
    ~RecognitionException() {}
    
    /// Get the ATN state number the parser was in at the time the error
    /// occurred. For NoViableAltException and
    /// LexerNoViableAltException exceptions, this is the
    /// DecisionState number. For others, it is the state whose outgoing
    /// edge we couldn't match.
    ///
    /// If the state number is not known, this method returns -1.
    virtual int getOffendingState() const;

  protected:
    void setOffendingState(int offendingState);

    /// Gets the set of input symbols which could potentially follow the
    /// previously matched symbol at the time this exception was thrown.
    ///
    /// If the set of expected tokens is not known and could not be computed,
    /// this method returns an empty set.
    ///
    /// @returns The set of token types that could potentially follow the current
    /// state in the ATN, or an empty set if the information is not available.
  public:
    virtual misc::IntervalSet getExpectedTokens() const;

    /// <summary>
    /// Gets the <seealso cref="RuleContext"/> at the time this exception was thrown.
    /// <p/>
    /// If the context is not available, this method returns {@code null}.
    /// </summary>
    /// <returns> The <seealso cref="RuleContext"/> at the time this exception was thrown.
    /// If the context is not available, this method returns {@code null}. </returns>
    virtual Ref<RuleContext> getCtx() const;

    /// <summary>
    /// Gets the input stream which is the symbol source for the recognizer where
    /// this exception was thrown.
    /// <p/>
    /// If the input stream is not available, this method returns {@code null}.
    /// </summary>
    /// <returns> The input stream which is the symbol source for the recognizer
    /// where this exception was thrown, or {@code null} if the stream is not
    /// available. </returns>
    virtual IntStream* getInputStream() const;

    virtual Token* getOffendingToken() const;

    /// <summary>
    /// Gets the <seealso cref="Recognizer"/> where this exception occurred.
    /// <p/>
    /// If the recognizer is not available, this method returns {@code null}.
    /// </summary>
    /// <returns> The recognizer where this exception occurred, or {@code null} if
    /// the recognizer is not available. </returns>
    virtual IRecognizer* getRecognizer() const;

  private:
    void InitializeInstanceFields();
  };

} // namespace antlr4
