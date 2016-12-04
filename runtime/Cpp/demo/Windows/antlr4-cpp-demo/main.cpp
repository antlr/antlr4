/* Copyright (c) 2012 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
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
  tree::ParseTree* tree = parser.main();

  std::wstring s = antlrcpp::s2ws(tree->toStringTree(&parser)) + L"\n";

  // Unfortunately, there is no way of showing the Unicode output properly in either the Intermediate Window in VS
  // (when using OutputDebugString), nor in a terminal (when using wcout). Instead set a breakpoint and view the
  // content of s in the debugger, which works fine.

  OutputDebugString(s.data());
  std::wcout << "Parse Tree: " << s << std::endl;

  return 0;
}
