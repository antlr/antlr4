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

using namespace antlrcpptest;
using namespace org::antlr::v4::runtime;

int main(int , const char **) {

  ANTLRInputStream input(L"(((x))) * y + z; a + (x * (y ? 0 : 1) + z);");
  TLexer lexer(&input);
  CommonTokenStream tokens(&lexer);

  TParser parser(&tokens);
  Ref<tree::ParseTree> tree = parser.main();

  std::cout << antlrcpp::ws2s(tree->toStringTree(&parser)) << std::endl;

  return 0;
}
