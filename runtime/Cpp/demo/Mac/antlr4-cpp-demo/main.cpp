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

using namespace antlrcpptest;
using namespace org::antlr::v4::runtime;

int main(int , const char **) {

  ANTLRInputStream input(u8"(((x * π))) * µ + ∰; a + (x * (y ? 0 : 1) + z);");
  TLexer lexer(&input);
  CommonTokenStream tokens(&lexer);

  TParser parser(&tokens);
  Ref<tree::ParseTree> tree = parser.main();

  std::cout << tree->toStringTree(&parser) << std::endl;
  std::fstream("test.txt") << tree->toStringTree(&parser);

  return 0;
}
