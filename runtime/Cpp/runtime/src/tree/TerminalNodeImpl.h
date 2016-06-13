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

#include "tree/TerminalNode.h"

namespace antlr4 {
namespace tree {

  class ANTLR4CPP_PUBLIC TerminalNodeImpl : public virtual TerminalNode {
  public:
    Token *symbol;
    std::weak_ptr<ParseTree> parent;

    TerminalNodeImpl(Token *symbol);

    virtual Token* getSymbol() override;
    virtual misc::Interval getSourceInterval() override;

    virtual std::size_t getChildCount() override;

    template<typename T, typename T1>
    T accept(ParseTreeVisitor<T1> *visitor) {
      return visitor->visitTerminal(this);
    }

    virtual std::string getText() override;
    virtual std::string toStringTree(Parser *parser) override;
    virtual std::string toString() override;
    virtual std::string toStringTree() override;

  protected:
    virtual std::weak_ptr<Tree> getParentReference() override;
    virtual Ref<Tree> getChildReference(size_t i) override;
  };

} // namespace tree
} // namespace antlr4
