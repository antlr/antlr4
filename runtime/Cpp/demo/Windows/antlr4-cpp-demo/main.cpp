//
//  main.cpp
//  antlr4-cpp-demo
//
//  Created by Mike Lischke on 13.03.16.
//

#include <iostream>

#include "antlr4-runtime.h"
#include "TLexer.h"
#include "TParser.h"

#include <Windows.h>

#pragma execution_character_set("utf-8")

using namespace antlrcpptest;
using namespace org::antlr::v4::runtime;

int main(int argc, const char * argv[]) {

  ANTLRInputStream input("🍴 = 🍐 + \"😎\";(((x * π))) * µ + ∰; a + (x * (y ? 0 : 1) + z);");
  TLexer lexer(&input);
  CommonTokenStream tokens(&lexer);

  TParser parser(&tokens);
  std::shared_ptr<tree::ParseTree> tree = parser.main();

  std::wstring s = antlrcpp::s2ws(tree->toStringTree(&parser)) + L"\n";
  OutputDebugString(s.data());

  return 0;
}
