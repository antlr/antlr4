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

namespace org {
namespace antlr {
namespace v4 {
namespace runtime {
namespace misc {

  /// <summary>
  /// Run a lexer/parser combo, optionally printing tree string or generating
  ///  postscript file. Optionally taking input file.
  ///
  ///  $ java org.antlr.v4.runtime.misc.TestRig GrammarName startRuleName
  ///        [-tree]
  ///        [-tokens] [-gui] [-ps file.ps]
  ///        [-trace]
  ///        [-diagnostics]
  ///        [-SLL]
  ///        [input-filename(s)]
  /// </summary>
  class ANTLR4CPP_PUBLIC TestRig {
  public:
    static const std::string LEXER_START_RULE_NAME;

    virtual ~TestRig() {};

  protected:
    std::string grammarName;
    std::string startRuleName;
    const std::vector<std::string> inputFiles;
    bool printTree;
    bool gui;
    std::string psFile;
    bool showTokens;
    bool trace;
    bool diagnostics;
    std::string encoding;
    bool SLL;

  public:
    TestRig(std::string args[]);

    static void main(std::string args[]);

    virtual void process();

  protected:
#ifdef TODO
    virtual void process(Lexer *lexer, Class *parserClass, Parser *parser, InputStream *is, Reader *r) throw(IOException, IllegalAccessException, InvocationTargetException, PrintException);
#endif
  private:
    void InitializeInstanceFields();
  };

} // namespace atn
} // namespace runtime
} // namespace v4
} // namespace antlr
} // namespace org
