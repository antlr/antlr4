/* Copyright (c) 2012-2018 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

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
using namespace antlr4;

int main(int argc, const char * argv[]) {

  ANTLRInputStream input("🍴 = 🍐 + \"😎\";(((x * π))) * µ + ∰; a + (x * (y ? 0 : 1) + z);");
  TLexer lexer(&input);
  CommonTokenStream tokens(&lexer);

  TParser parser(&tokens);
  tree::ParseTree *tree = parser.main();

  std::string utf8 = tree->toStringTree(&parser) + "\n";
  std::wstring utf16 = antlrcpp::s2ws(utf8);

  OutputDebugString(utf16.data()); // Only works properly since VS 2015.

  SetConsoleOutputCP(65001);
  std::cout << "Parse Tree: " << utf8 << std::endl; // Output quality depends on used console font.

  return 0;
}
