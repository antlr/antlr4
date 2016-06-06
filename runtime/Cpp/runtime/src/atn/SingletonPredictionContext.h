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

#include "atn/PredictionContext.h"

namespace antlr4 {
namespace atn {

  class ANTLR4CPP_PUBLIC SingletonPredictionContext : public PredictionContext {
  public:
    // Usually a parent is linked via a weak ptr. Not so here as we have kinda reverse reference chain.
    // There are no child contexts stored here and often the parent context is left dangling when it's
    // owning ATNState is released. In order to avoid having this context released as well (leaving all other contexts
    // which got this one as parent with a null reference) we use a shared_ptr here instead, to keep those left alone
    // parent contexts alive.
    const Ref<PredictionContext> parent;
    const int returnState;

    SingletonPredictionContext(std::weak_ptr<PredictionContext> parent, int returnState);
    virtual ~SingletonPredictionContext() {};

    static Ref<SingletonPredictionContext> create(std::weak_ptr<PredictionContext> parent, int returnState);

    virtual size_t size() const override;
    virtual std::weak_ptr<PredictionContext> getParent(size_t index) const override;
    virtual int getReturnState(size_t index) const override;
    virtual bool operator == (const PredictionContext &o) const override;
    virtual std::string toString() const override;
  };

} // namespace atn
} // namespace antlr4
