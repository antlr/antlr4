/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

//
//  main.cpp
//  antlr4-cpp-demo
//
//  Created by Mike Lischke on 13.03.16.
//

#include <io.h>
#include <stdio.h>
#include <iostream>
#include <fcntl.h>

#include "antlr4-runtime.h"
#include "TLexer.h"
#include "TParser.h"

#include <windows.h>
#include <iostream>
#include <string>
#include <locale>
#include <codecvt>

using namespace antlrcpptest;
using namespace antlr4;
using namespace std;

int main(int argc, const char * argv[]) {

  const std::string in_str = u8"🍴 = 🍐 + \"😎\";(((x * π))) * µ + ∰; a + (x * (y ? 0 : 1) + z); \"Т\" + \"М\" + \"Приве́т नमस्ते שָׁלוֹם\" = \"♥♣♠○• ♡ ❤ ♥\";";
  ANTLRInputStream input(in_str);
  TLexer lexer(&input);
  CommonTokenStream tokens(&lexer);

  tokens.fill();
  for (auto token : tokens.getTokens()) {
    cout << token->toString() << endl;
  }

  TParser parser(&tokens);
  tree::ParseTree *tree = parser.main();

  SetConsoleOutputCP(CP_UTF8);

  std::string s = tree->toStringTree (&parser);
  OutputDebugStringW (wstring (s.begin (), s.end ()).c_str ()); // Only works properly since VS 2015.
  cout << s << endl << endl;

  return 0;
}
