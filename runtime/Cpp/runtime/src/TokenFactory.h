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

namespace antlr4 {

  /// The default mechanism for creating tokens. It's used by default in Lexer and
  ///  the error handling strategy (to create missing tokens).  Notifying the parser
  ///  of a new factory means that it notifies it's token source and error strategy.
  template<typename Symbol>
  class ANTLR4CPP_PUBLIC TokenFactory {
  public:
    virtual ~TokenFactory() {};
    
    /// This is the method used to create tokens in the lexer and in the
    ///  error handling strategy. If text!=null, than the start and stop positions
    ///  are wiped to -1 in the text override is set in the CommonToken.
    virtual std::unique_ptr<Symbol> create(std::pair<TokenSource *, CharStream *> source, int type, const std::string &text,
      int channel, int start, int stop, int line, int charPositionInLine) = 0;

    /// Generically useful
    virtual std::unique_ptr<Symbol> create(int type, const std::string &text) = 0;
  };

} // namespace antlr4
