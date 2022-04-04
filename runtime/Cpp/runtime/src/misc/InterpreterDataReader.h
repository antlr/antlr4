/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include "antlr4-common.h"
#include "atn/ATN.h"
#include "VocabularyImpl.h"

namespace antlr4 {
namespace misc {

  struct ANTLR4CPP_PUBLIC InterpreterData final {
    std::unique_ptr<atn::ATN> atn;
    std::vector<int32_t> serializedATN;
    std::unique_ptr<Vocabulary> vocabulary;
    std::vector<std::string> ruleNames;
    std::vector<std::string> channels; // Only valid for lexer grammars.
    std::vector<std::string> modes; // ditto
  };

  // A class to read plain text interpreter data produced by ANTLR.
  class ANTLR4CPP_PUBLIC InterpreterDataReader {
  public:
    static InterpreterData parseFile(std::string const& fileName);
  };

} // namespace atn
} // namespace antlr4
