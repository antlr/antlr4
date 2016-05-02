//
//  main.cpp
//  antlr4-cpp-demo
//
//  Created by Mike Lischke on 13.03.16.
//

#include <iostream>

#include "ANTLRInputStream.h"
#include "CommonTokenStream.h"
#include "TLexer.h"
#include "TParser.h"

#include "Strings.h"

#include <Windows.h>

using namespace antlrcpptest;
using namespace org::antlr::v4::runtime;

int main(int argc, const char * argv[]) {

  ANTLRInputStream input(L"x * y + z");
  TLexer lexer(&input);
  CommonTokenStream tokens(&lexer);

  TParser parser(&tokens);
  std::shared_ptr<tree::ParseTree> tree = parser.main();

  std::wstring s = tree->toStringTree(&parser) + L"\n";
  OutputDebugString(s.data());

  return 0;
}
