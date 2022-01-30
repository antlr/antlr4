/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include "atn/LexerAction.h"
#include "atn/ATNDeserializationOptions.h"

namespace antlr4 {
namespace atn {

class ANTLR4CPP_PUBLIC ATNDeserializer {
public:
  static constexpr size_t SERIALIZED_VERSION = 4;

  ATNDeserializer();

  explicit ATNDeserializer(const ATNDeserializationOptions& dso);

  virtual ~ATNDeserializer();

  virtual ATN deserialize(const std::vector<uint16_t> &input);
  virtual void verifyATN(const ATN &atn);

  static void checkCondition(bool condition);
  static void checkCondition(bool condition, const std::string &message);

  static Transition *edgeFactory(const ATN &atn, size_t type, size_t src, size_t trg, size_t arg1, size_t arg2,
                                  size_t arg3, const std::vector<misc::IntervalSet> &sets);

  static ATNState *stateFactory(size_t type, size_t ruleIndex);

protected:
  void markPrecedenceDecisions(const ATN &atn) const;
  Ref<LexerAction> lexerActionFactory(LexerActionType type, int data1, int data2) const;

private:
  const ATNDeserializationOptions _deserializationOptions;
};

} // namespace atn
} // namespace antlr4
