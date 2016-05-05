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

#include "TestRig.h"

using namespace org::antlr::v4::runtime::misc;

#ifdef TODO
const std::wstring TestRig::LEXER_START_RULE_NAME = L"tokens";

TestRig::TestRig(std::wstring args[]) throw(std::exception) : inputFiles(new java::util::ArrayList<String>()) {
  InitializeInstanceFields();
  if (sizeof(args) / sizeof(args[0]) < 2) {
    System::err::println(std::wstring(L"java org.antlr.v4.runtime.misc.TestRig GrammarName startRuleName\n") + std::wstring(L"  [-tokens] [-tree] [-gui] [-ps file.ps] [-encoding encodingname]\n") + std::wstring(L"  [-trace] [-diagnostics] [-SLL]\n") + std::wstring(L"  [input-filename(s)]"));
    System::err::println(L"Use startRuleName='tokens' if GrammarName is a lexer grammar.");
    System::err::println(L"Omitting input-filename makes rig read from stdin.");
    return;
  }
  int i = 0;
  grammarName = args[i];
  i++;
  startRuleName = args[i];
  i++;
  while (i < sizeof(args) / sizeof(args[0])) {
    std::wstring arg = args[i];
    i++;
    if (arg[0] != L'-') { // input file name
      inputFiles.push_back(arg);
      continue;
    }
    if (arg == L"-tree") {
      printTree = true;
    }
    if (arg == L"-gui") {
      gui = true;
    }
    if (arg == L"-tokens") {
      showTokens = true;
    } else if (arg == L"-trace") {
      trace = true;
    } else if (arg == L"-SLL") {
      SLL = true;
    } else if (arg == L"-diagnostics") {
      diagnostics = true;
    } else if (arg == L"-encoding") {
      if (i >= sizeof(args) / sizeof(args[0])) {
        System::err::println(L"missing encoding on -encoding");
        return;
      }
      encoding = args[i];
      i++;
    } else if (arg == L"-ps") {
      if (i >= sizeof(args) / sizeof(args[0])) {
        System::err::println(L"missing filename on -ps");
        return;
      }
      psFile = args[i];
      i++;
    }
  }
}

void TestRig::main(std::wstring args[]) throw(std::exception) {
  TestRig *testRig = new TestRig(args);
  if (sizeof(args) / sizeof(args[0]) >= 2) {
    testRig->process();
  }
}

void TestRig::process() throw(std::exception) {
  //		System.out.println("exec "+grammarName+"."+startRuleName);
  std::wstring lexerName = grammarName + std::wstring(L"Lexer");
  ClassLoader *cl = Thread::currentThread()->getContextClassLoader();
  Class *lexerClass = nullptr;
  try {
    lexerClass = cl->loadClass(lexerName)->asSubclass(Lexer::typeid);
  } catch (java::lang::ClassNotFoundException cnfe) {
    // might be pure lexer grammar; no Lexer suffix then
    lexerName = grammarName;
    try {
      lexerClass = cl->loadClass(lexerName)->asSubclass(Lexer::typeid);
    } catch (ClassNotFoundException cnfe2) {
      System::err::println(std::wstring(L"Can't load ") + lexerName + std::wstring(L" as lexer or parser"));
      return;
    }
  }

  //JAVA TO C++ CONVERTER TODO TASK: Java wildcard generics are not converted to C++:
  //ORIGINAL LINE: Constructor<? extends org.antlr.v4.runtime.Lexer> lexerCtor = lexerClass.getConstructor(org.antlr.v4.runtime.CharStream.class);
  Constructor<? extends Lexer> *lexerCtor = lexerClass->getConstructor(CharStream::typeid);
  Lexer *lexer = lexerCtor->newInstance(static_cast<CharStream*>(nullptr));

  Class *parserClass = nullptr;
  Parser *parser = nullptr;
  if (startRuleName != LEXER_START_RULE_NAME) {
    std::wstring parserName = grammarName + std::wstring(L"Parser");
    parserClass = cl->loadClass(parserName)->asSubclass(Parser::typeid);
    if (parserClass == nullptr) {
      System::err::println(std::wstring(L"Can't load ") + parserName);
    }
    //JAVA TO C++ CONVERTER TODO TASK: Java wildcard generics are not converted to C++:
    //ORIGINAL LINE: Constructor<? extends org.antlr.v4.runtime.Parser> parserCtor = parserClass.getConstructor(org.antlr.v4.runtime.TokenStream.class);
    Constructor<? extends Parser> *parserCtor = parserClass->getConstructor(TokenStream::typeid);
    parser = parserCtor->newInstance(static_cast<TokenStream*>(nullptr));
  }

  if (inputFiles.empty()) {
    InputStream *is = System::in;
    Reader *r;
    if (encoding != L"") {
      r = new InputStreamReader(is, encoding);
    } else {
      r = new InputStreamReader(is);
    }

    process(lexer, parserClass, parser, is, r);
    return;
  }
  for (auto inputFile : inputFiles) {
    InputStream *is = System::in;
    if (inputFile != nullptr) {
      is = new FileInputStream(inputFile);
    }
    Reader *r;
    if (encoding != L"") {
      r = new InputStreamReader(is, encoding);
    } else {
      r = new InputStreamReader(is);
    }

    if (inputFiles.size() > 1) {
      System::err::println(inputFile);
    }
    process(lexer, parserClass, parser, is, r);
  }
}

void TestRig::process(Lexer *lexer, Class *parserClass, Parser *parser, InputStream *is, Reader *r) throw(IOException, IllegalAccessException, InvocationTargetException, PrintException) {
  try {
    ANTLRInputStream *input = new ANTLRInputStream(r);
    lexer->setInputStream(input);
    CommonTokenStream *tokens = new CommonTokenStream(lexer);

    tokens->fill();

    if (showTokens) {
      for (auto tok : tokens->getTokens()) {
        std::cout << tok << std::endl;
      }
    }

    if (startRuleName == LEXER_START_RULE_NAME) {
      return;
    }

    if (diagnostics) {
      parser->addErrorListener(new DiagnosticErrorListener());
      parser->getInterpreter()->setPredictionMode(PredictionMode::LL_EXACT_AMBIG_DETECTION);
    }

    if (printTree || gui || psFile != L"") {
      parser->setBuildParseTree(true);
    }

    if (SLL) { // overrides diagnostics
      parser->getInterpreter()->setPredictionMode(PredictionMode::SLL);
    }

    parser->setTokenStream(tokens);
    parser->setTrace(trace);

    try {
      Method *startRule = parserClass->getMethod(startRuleName);
      ParserRuleContext *tree = static_cast<ParserRuleContext*>(startRule->invoke(parser, static_cast<Object[]>(nullptr)));

      if (printTree) {
        std::cout << tree->toStringTree(parser) << std::endl;
      }
      if (gui) {
        tree->inspect(parser);
      }
      if (psFile != L"") {
        tree->save(parser, psFile); // Generate postscript
      }
    } catch (NoSuchMethodException nsme) {
      System::err::println(std::wstring(L"No method for rule ") + startRuleName + std::wstring(L" or it has arguments"));
    }
  } finally {
    if (r != nullptr) {
      r->close();
    }
    if (is != nullptr) {
      is->close();
    }
  }
}

void TestRig::InitializeInstanceFields() {
  printTree = false;
  gui = false;
  psFile = L"";
  showTokens = false;
  trace = false;
  diagnostics = false;
  encoding = L"";
  SLL = false;
}
#endif
