/* Copyright (c) 2026 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include <algorithm>
#include <memory>
#include <string>

#include "Token.h"
#include "Vocabulary.h"
#include "atn/ATNConfigSet.h"
#include "atn/BasicBlockStartState.h"
#include "dfa/DFA.h"
#include "dfa/DFAState.h"

#include "gtest/gtest.h"

namespace antlr4::dfa {
namespace {

  DFAState *NewState(int state_number) {
    auto *state = new DFAState(std::make_unique<atn::ATNConfigSet>());
    state->stateNumber = state_number;
    return state;
  }

  // Serializing is a diagnostic; the parser searches this same edge map.
  TEST(DFASerializerTest, SerializesEveryEdgeWithoutAddingAny) {
    atn::BasicBlockStartState start_state;
    DFA dfa(&start_state, 0);

    auto *from = NewState(0);
    const std::unique_ptr<DFAState> to(NewState(1));
    dfa.states.insert(from);  // takes ownership; the target stays out of it
    dfa.s0 = from;

    constexpr size_t kTokenType = 20;
    from->edges[kTokenType] = to.get();
    from->edges[Token::EOF] = to.get();  // SIZE_MAX: the largest possible key

    const std::string text = dfa.toString(Vocabulary());

    EXPECT_EQ(from->edges.size(), 2u);
    EXPECT_NE(text.find("s0-20->s1"), std::string::npos) << text;
    EXPECT_NE(text.find("s0-EOF->s1"), std::string::npos) << text;
    EXPECT_EQ(std::count(text.begin(), text.end(), '\n'), 2) << text;
  }

  // Every runtime emits a state's edges in ascending symbol order, and the
  // shared runtime tests compare the text, so the order is part of the output
  // and not an accident of how the edges are held.
  TEST(DFASerializerTest, NamesEdgesInAscendingSymbolOrder) {
    atn::BasicBlockStartState start_state;
    DFA dfa(&start_state, 0);

    auto *from = NewState(0);
    const std::unique_ptr<DFAState> to(NewState(1));
    dfa.states.insert(from);
    dfa.s0 = from;

    // The symbols of a lexer DFA state that ordered the edges by hash rather
    // than by value: see the shared SemPredEvalLexer/Indent test.
    for (size_t symbol : {size_t{'\n'}, size_t{'a'}, size_t{'d'}}) {
      from->edges[symbol] = to.get();
    }

    const std::string text = dfa.toString(Vocabulary());

    const size_t newline = text.find("s0-10->");
    const size_t a = text.find("s0-97->");
    const size_t d = text.find("s0-100->");
    ASSERT_NE(newline, std::string::npos) << text;
    ASSERT_NE(a, std::string::npos) << text;
    ASSERT_NE(d, std::string::npos) << text;
    EXPECT_LT(newline, a) << text;
    EXPECT_LT(a, d) << text;
  }

}  // namespace
}  // namespace antlr4::dfa
