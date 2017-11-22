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

#include <tchar.h>
#include <locale>

#include <io.h>
#include <stdio.h>
#include <iostream>
#include <fcntl.h>

#include "antlr4-runtime.h"
#include "TLexer.h"
#include "TParser.h"

#include <iostream>
#include <string>

#include <codecvt>
#include <windows.h>

using namespace antlrcpptest;
using namespace antlr4;
using namespace std;
using namespace std::string_literals;

int main(int , const char **) {
  const string  in_str1 = u8"🍴 = 🍐 + \"😎\";(((x * π))) * µ + ∰; a + (x * (y ? 0 : 1) + z); \"Т\" + \"М\" + \"Приве́т नमस्ते שָׁלוֹם\" = \"♥♣♠○• ♡ ❤ ♥\";";
  const string in_str = in_str1;
  ANTLRInputStream input(in_str);
  TLexer lexer(&input);
  CommonTokenStream tokens(&lexer);

  SetConsoleOutputCP (CP_UTF8);
  _setmode (_fileno (stdout), _O_U8TEXT);

  tokens.fill();
  for (auto token : tokens.getTokens()) {
	  cout << token->toString() << endl;
  }

  TParser parser(&tokens);
  tree::ParseTree* tree = parser.main();

  cout << tree->toStringTree(&parser) << endl << endl;
  cout << in_str << endl << endl;

  return 0;
}
