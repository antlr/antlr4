#include <cstdint>
#include <stdexcept>
#include <vector>

#include "gtest/gtest.h"

#include "atn/ATN.h"
#include "atn/ATNDeserializer.h"
#include "atn/SerializedATNView.h"

using namespace antlr4::atn;

namespace {

  // A serialized ATN that declares zero states but then names state 100 as a
  // non-greedy decision state. A well-formed ATN never references a state that
  // does not exist; a corrupt one can. The C++ port must not index atn->states
  // out of bounds for such input (the Java runtime throws on the same data).
  TEST(ATNDeserializerTest, OutOfRangeStateNumberThrows) {
    const std::vector<int32_t> serialized = {
        static_cast<int32_t>(ATNDeserializer::SERIALIZED_VERSION), // version
        0,   // grammar type
        0,   // max token type
        0,   // number of states
        1,   // number of non-greedy states
        100, // state number that does not exist
    };

    ATNDeserializer deserializer;
    EXPECT_THROW(deserializer.deserialize(SerializedATNView(serialized)), std::out_of_range);
  }

  // An edge whose target state index is past the end of the state list must be
  // rejected rather than dereferencing an out-of-bounds ATNState pointer.
  TEST(ATNDeserializerTest, OutOfRangeEdgeTargetThrows) {
    const std::vector<int32_t> serialized = {
        static_cast<int32_t>(ATNDeserializer::SERIALIZED_VERSION), // version
        0,   // grammar type
        0,   // max token type
        0,   // number of states
        0,   // number of non-greedy states
        0,   // number of precedence states
        0,   // number of rules
        0,   // number of modes
        0,   // number of sets
        1,   // number of edges
        7,   // src
        9,   // trg (does not exist)
        1,   // edge type (epsilon)
        0, 0, 0, // args
    };

    ATNDeserializer deserializer;
    EXPECT_THROW(deserializer.deserialize(SerializedATNView(serialized)), std::out_of_range);
  }

}
