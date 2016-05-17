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

#include "misc/TestRig.h"

using namespace org::antlr::v4::runtime::misc;

#ifdef TODO
const std::string TestRig::LEXER_START_RULE_NAME = "tokens";

TestRig::TestRig(std::string args[]) throw(std::exception) : inputFiles(new java::util::ArrayList<String>()) {
  InitializeInstanceFields();
  if (sizeof(args) / sizeof(args[0]) < 2) {
    System::err::println(std::string("java org.antlr.v4.runtime.misc.TestRig GrammarName startRuleName\n") + std::string("  [-tokens] [-tree] [-gui] [-ps file.ps] [-encoding encodingname]\n") + std::string("  [-trace] [-diagnostics] [-SLL]\n") + std::string("  [input-filename(s)]"));
    System::err::println("Use startRuleName='tokens' if GrammarName is a lexer grammar.");
    System::err::println("Omitting input-filename makes rig read from stdin.");
    return;
  }
  int i = 0;
  grammarName = args[i];
  i++;
  startRuleName = args[i];
  i++;
  while (i < sizeof(args) / sizeof(args[0])) {
    std::string arg = args[i];
    i++;
    if (arg[0] != '-') { // input file name
      inputFiles.push_back(arg);
      continue;
    }
    if (arg == "-tree") {
      printTree = true;
    }
    if (arg == "-gui") {
      gui = true;
    }
    if (arg == "-tokens") {
      showTokens = true;
    } else if (arg == "-trace") {
      trace = true;
    } else if (arg == "-SL") {
      SLL = true;
    } else if (arg == "-diagnostics") {
      diagnostics = true;
    } else if (arg == "-encoding") {
      if (i >= sizeof(args) / sizeof(args[0])) {
        System::err::println("missing encoding on -encoding");
        return;
      }
      encoding = args[i];
      i++;
    } else if (arg == "-ps") {
      if (i >= sizeof(args) / sizeof(args[0])) {
        System::err::println("missing filename on -ps");
        return;
      }
      psFile = args[i];
      i++;
    }
  }
}

void TestRig::main(std::string args[]) throw(std::exception) {
  TestRig *testRig = new TestRig(args);
  if (sizeof(args) / sizeof(args[0]) >= 2) {
    testRig->process();
  }
}

void TestRig::process() throw(std::exception) {
  //		System.out.println("exec "+grammarName+"."+startRuleName);
  std::string lexerName = grammarName + std::string("Lexer");
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
      System::err::println(std::string("Can't load ") + lexerName + std::string(" as lexer or parser"));
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
    std::string parserName = grammarName + std::string("Parser");
    parserClass = cl->loadClass(parserName)->asSubclass(Parser::typeid);
    if (parserClass == nullptr) {
      System::err::println(std::string("Can't load ") + parserName);
    }
    //JAVA TO C++ CONVERTER TODO TASK: Java wildcard generics are not converted to C++:
    //ORIGINAL LINE: Constructor<? extends org.antlr.v4.runtime.Parser> parserCtor = parserClass.getConstructor(org.antlr.v4.runtime.TokenStream.class);
    Constructor<? extends Parser> *parserCtor = parserClass->getConstructor(TokenStream::typeid);
    parser = parserCtor->newInstance(static_cast<TokenStream*>(nullptr));
  }

  if (inputFiles.empty()) {
    InputStream *is = System::in;
    Reader *r;
    if (encoding != "") {
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
    if (encoding != "") {
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

    if (printTree || gui || psFile != "") {
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
      if (psFile != "") {
        tree->save(parser, psFile); // Generate postscript
      }
    } catch (NoSuchMethodException nsme) {
      System::err::println(std::string("No method for rule ") + startRuleName + std::string(" or it has arguments"));
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
  psFile = "";
  showTokens = false;
  trace = false;
  diagnostics = false;
  encoding = "";
  SLL = false;
}
#endif
