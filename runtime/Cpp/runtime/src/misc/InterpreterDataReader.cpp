/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include "atn/ATN.h"
#include "atn/ATNDeserializer.h"
#include "Vocabulary.h"
#include "VocabularyImpl.h"

#include "misc/InterpreterDataReader.h"

using namespace antlr4::dfa;
using namespace antlr4::atn;
using namespace antlr4::misc;
using namespace antlr4;

namespace {

  class InterpreterDataVocabulary final : public Vocabulary {
  public:
    InterpreterDataVocabulary(std::vector<std::string> literalNames, std::vector<std::string> symbolicNames)
        : Vocabulary(std::max(literalNames.size(), symbolicNames.size()) - 1),
          literalNames(std::move(literalNames)), symbolicNames(std::move(symbolicNames)),
          literalNamesView(this->literalNames.size()), symbolicNamesView(this->symbolicNames.size()),
          _impl(antlrcpp::Span<const std::string_view>(literalNamesView.data(), literalNamesView.size()), antlrcpp::Span<const std::string_view>(symbolicNamesView.data(), symbolicNamesView.size())) {
      for (size_t index = 0; index < this->literalNames.size(); index++) {
        literalNamesView[index] = this->literalNames[index];
      }
      for (size_t index = 0; index < this->symbolicNames.size(); index++) {
        symbolicNamesView[index] = this->symbolicNames[index];
      }
    }

    std::string_view getLiteralName(size_t tokenType) const override {
      return _impl.getLiteralName(tokenType);
    }

    std::string_view getSymbolicName(size_t tokenType) const override {
      return _impl.getSymbolicName(tokenType);
    }

    std::string getDisplayName(size_t tokenType) const override {
      return _impl.getDisplayName(tokenType);
    }

  private:
    std::vector<std::string> literalNames;
    std::vector<std::string> symbolicNames;
    std::vector<std::string_view> literalNamesView;
    std::vector<std::string_view> symbolicNamesView;
    VocabularyImpl _impl;
  };

}

InterpreterData InterpreterDataReader::parseFile(std::string const& fileName) {
  // The structure of the data file is very simple. Everything is line based with empty lines
  // separating the different parts. For lexers the layout is:
  // token literal names:
  // ...
  //
  // token symbolic names:
  // ...
  //
  // rule names:
  // ...
  //
  // channel names:
  // ...
  //
  // mode names:
  // ...
  //
  // atn:
  // <a single line with comma separated int values> enclosed in a pair of squared brackets.
  //
  // Data for a parser does not contain channel and mode names.

  std::ifstream input(fileName);
  if (!input.good())
    return {};

  std::vector<std::string> literalNames;
  std::vector<std::string> symbolicNames;

  std::string line;

  std::getline(input, line, '\n');
  assert(line == "token literal names:");
  while (true) {
    std::getline(input, line, '\n');
    if (line.empty())
      break;

    literalNames.push_back(line == "null" ? "" : line);
  };

  std::getline(input, line, '\n');
  assert(line == "token symbolic names:");
  while (true) {
    std::getline(input, line, '\n');
    if (line.empty())
      break;

    symbolicNames.push_back(line == "null" ? "" : line);
  };
  InterpreterData result;
  result.vocabulary = std::make_unique<InterpreterDataVocabulary>(std::move(literalNames), std::move(symbolicNames));

  std::getline(input, line, '\n');
  assert(line == "rule names:");
  while (true) {
    std::getline(input, line, '\n');
    if (line.empty())
      break;

    result.ruleNames.push_back(line);
  };

  std::getline(input, line, '\n');
  if (line == "channel names:") {
    while (true) {
      std::getline(input, line, '\n');
      if (line.empty())
        break;

      result.channels.push_back(line);
    };

    std::getline(input, line, '\n');
    assert(line == "mode names:");
    while (true) {
      std::getline(input, line, '\n');
      if (line.empty())
        break;

      result.modes.push_back(line);
    };
  }

  std::getline(input, line, '\n');
  assert(line == "atn:");
  std::getline(input, line, '\n');
  std::stringstream tokenizer(line);
  std::string value;
  while (tokenizer.good()) {
    std::getline(tokenizer, value, ',');
    unsigned long number;
    if (value[0] == '[')
      number = std::strtoul(&value[1], nullptr, 10);
    else
      number = std::strtoul(value.c_str(), nullptr, 10);
    result.serializedATN.push_back(static_cast<int32_t>(number));
  }


  ATNDeserializer deserializer;
  result.atn = deserializer.deserialize(antlrcpp::Span<const int32_t>(result.serializedATN.data(), result.serializedATN.size()));
  return result;
}
