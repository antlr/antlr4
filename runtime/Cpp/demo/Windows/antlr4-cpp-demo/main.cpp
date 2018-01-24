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

void DumpString (LPWSTR pInput)
{
  while (*pInput != 0)
  {
    if (*pInput < 0x80)
      wprintf (L"%c", *pInput);
    else
      wprintf (L"\\x%4.4x", *pInput);
    pInput++;
  }
  wprintf (L"\n");
}

// Check if normalized and display normalized output for a particular normalization form 
void TryNormalization (NORM_FORM form, LPWSTR strInput)
{
  // Test if the string is normalized 
  if (IsNormalizedString (form, strInput, -1))
  {
    wprintf (L"Already normalized in this form\n");
  }
  else
  {
    // It was not normalized, so normalize it 
    int    iSizeGuess;
    LPWSTR pBuffer;

    // How big is our buffer (quick guess, usually enough) 
    iSizeGuess = NormalizeString (form, strInput, -1, NULL, 0);

    if (iSizeGuess == 0)
    {
      wprintf (L"Error %d checking for size\n", GetLastError ());
    }

    while (iSizeGuess > 0)
    {
      pBuffer = (LPWSTR)malloc (iSizeGuess * sizeof (WCHAR));
      if (pBuffer)
      {
        // Normalize the string 
        int iActualSize = NormalizeString (form, strInput, -1, pBuffer, iSizeGuess);
        iSizeGuess = 0;
        if (iActualSize <= 0 && GetLastError () != ERROR_SUCCESS)
        {
          // Error during normalization 
          wprintf (L"Error %d during normalization\n", GetLastError ());
          if (GetLastError () == ERROR_INSUFFICIENT_BUFFER)
          {
            // If the buffer is too small, try again with a bigger buffer. 
            wprintf (L"Insufficient buffer, new suggested buffer size %d\n", -iActualSize);
            iSizeGuess = -iActualSize;
          }
          else if (GetLastError () == ERROR_NO_UNICODE_TRANSLATION)
          {
            wprintf (L"Invalid Unicode found at input character index %d\n", -iActualSize);
          }
        }
        else
        {
          // Display the normalized string 
          DumpString (pBuffer);
        }

        // Free the buffer 
        free (pBuffer);
      }
      else
      {
        wprintf (L"Error allocating buffer\n");
        iSizeGuess = 0;
      }
    }
  }
}

int __cdecl wmain (int argc, WCHAR* argv[])
{
  locale loc1 ("german");
  const string  in_str1 = u8"🍴 = 🍐 + \"😎\";(((x * π))) * µ + ∰; a + (x * (y ? 0 : 1) + z); \"Т\" + \"М\" + \"Приве́т नमस्ते שָׁלוֹם\" = \"♥♣♠○• ♡ ❤ ♥\";";
  const string in_str = in_str1;
  ANTLRInputStream input(in_str);
  TLexer lexer(&input);
  CommonTokenStream tokens(&lexer);

  LPWSTR strInput = L"T\u00e8st string \uFF54\uFF4F n\u00f8rm\u00e4lize";
  DumpString (strInput);

  SetConsoleOutputCP (CP_UTF8);
  _setmode (_fileno (stdout), _O_U8TEXT);

  DumpString (strInput);
  TryNormalization (NormalizationC, strInput);
  TryNormalization (NormalizationKC, strInput);
  TryNormalization (NormalizationD, strInput);
  TryNormalization (NormalizationKD, strInput);
  wprintf (L"Attempt to normalize a string that expands beyond the initial guess\n");
  TryNormalization (NormalizationC,
    // These all expand to 2 characters 
    L"\u0958\u0958\u0958\u0958\u0958\u0958\u0958\u0958\u0958\u0958\u0958\u0958"
    L"\u0958\u0958\u0958\u0958\u0958\u0958\u0958\u0958\u0958\u0958\u0958\u0958"
    L"\u0958\u0958\u0958\u0958\u0958\u0958\u0958\u0958\u0958\u0958\u0958\u0958"
    L"\u0958\u0958\u0958\u0958\u0958\u0958\u0958\u0958\u0958\u0958\u0958\u0958"
    // These all expand to 3 characters 
    L"\ufb2c\ufb2c\ufb2c\ufb2c\ufb2c\ufb2c\ufb2c\ufb2c\ufb2c\ufb2c\ufb2c\ufb2c"
    L"\ufb2c\ufb2c\ufb2c\ufb2c\ufb2c\ufb2c\ufb2c\ufb2c\ufb2c\ufb2c\ufb2c\ufb2c");

  std::wstring_convert< std::codecvt<wchar_t, char, std::mbstate_t> > conv;
  tokens.fill();
  for (auto token : tokens.getTokens()) {
    std::wstring wstr = conv.from_bytes (token->toString ());
    wcout << wstr << endl;
  }

  TParser parser(&tokens);
  tree::ParseTree* tree = parser.main();

  std::wstring wstr = conv.from_bytes (tree->toStringTree (&parser));
  wcout << wstr << endl << endl;
  wstr = conv.from_bytes (in_str);
  wcout << wstr << endl << endl;

  wprintf (L"%s\n", u8"Это текст:");
  wprintf (L"%s\n", wstr);

  return 0;
}
