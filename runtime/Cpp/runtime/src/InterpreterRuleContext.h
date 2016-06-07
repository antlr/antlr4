/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 * Copyright (c) 2013 Terence Parr
 * Copyright (c) 2013 Dan McLaughlin
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#pragma once

#include "ParserRuleContext.h"

namespace antlr4 {

  /**
   * This class extends {@link ParserRuleContext} by allowing the value of
   * {@link #getRuleIndex} to be explicitly set for the context.
   *
   * <p>
   * {@link ParserRuleContext} does not include field storage for the rule index
   * since the context classes created by the code generator override the
   * {@link #getRuleIndex} method to return the correct value for that context.
   * Since the parser interpreter does not use the context classes generated for a
   * parser, this class (with slightly more memory overhead per node) is used to
   * provide equivalent functionality.</p>
   */
  class ANTLR4CPP_PUBLIC InterpreterRuleContext : public ParserRuleContext {
  public:
    InterpreterRuleContext();

    /**
     * Constructs a new {@link InterpreterRuleContext} with the specified
     * parent, invoking state, and rule index.
     *
     * @param parent The parent context.
     * @param invokingStateNumber The invoking state number.
     * @param ruleIndex The rule index for the current context.
     */
    InterpreterRuleContext(std::weak_ptr<ParserRuleContext> parent, int invokingStateNumber, ssize_t ruleIndex);

    virtual ssize_t getRuleIndex() const override;

  protected:
    /** This is the backing field for {@link #getRuleIndex}. */
    const ssize_t _ruleIndex = -1;
};

} // namespace antlr4
