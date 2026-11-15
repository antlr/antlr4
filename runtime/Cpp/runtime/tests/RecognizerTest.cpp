#include <atomic>
#include <cstddef>
#include <map>
#include <memory>
#include <string>
#include <string_view>
#include <thread>
#include <vector>

#include "gtest/gtest.h"
#include "IntStream.h"
#include "Recognizer.h"
#include "Vocabulary.h"
#include "atn/ATN.h"
#include "atn/ATNType.h"

namespace antlr4 {
namespace {

  // Each instance has its own vocabulary and rule names, so every call misses
  // the shared caches in Recognizer and inserts a new entry.
  class TestRecognizer final : public Recognizer {
  public:
    explicit TestRecognizer(size_t id)
        : _ruleNames({"rule" + std::to_string(id)}),
          _vocabulary({"", "'a'"}, {"", "A"}),
          _atn(atn::ATNType::PARSER, 1) {}

    const std::vector<std::string>& getRuleNames() const override { return _ruleNames; }
    const dfa::Vocabulary& getVocabulary() const override { return _vocabulary; }
    std::string getGrammarFileName() const override { return "Test.g4"; }
    const atn::ATN& getATN() const override { return _atn; }
    IntStream* getInputStream() override { return nullptr; }
    void setInputStream(IntStream*) override {}
    TokenFactory<CommonToken>* getTokenFactory() override { return nullptr; }

  private:
    std::vector<std::string> _ruleNames;
    dfa::Vocabulary _vocabulary;
    atn::ATN _atn;
  };

  TEST(RecognizerTest, CachesAreSharedAcrossThreads) {
    constexpr size_t kThreads = 8;
    constexpr size_t kPerThread = 2000;

    std::vector<std::unique_ptr<TestRecognizer>> recognizers;
    for (size_t i = 0; i < kThreads * kPerThread; ++i) {
      recognizers.push_back(std::make_unique<TestRecognizer>(i));
    }

    std::atomic<bool> start{false};
    std::vector<std::thread> threads;
    for (size_t t = 0; t < kThreads; ++t) {
      threads.emplace_back([&recognizers, &start, t] {
        while (!start.load()) {
          std::this_thread::yield();
        }
        for (size_t i = 0; i < kPerThread; ++i) {
          TestRecognizer& recognizer = *recognizers[t * kPerThread + i];
          recognizer.getTokenTypeMap();
          recognizer.getRuleIndexMap();
        }
      });
    }
    start.store(true);
    for (auto& thread : threads) {
      thread.join();
    }

    for (size_t i = 0; i < recognizers.size(); ++i) {
      std::map<std::string_view, size_t> tokenTypes = recognizers[i]->getTokenTypeMap();
      EXPECT_EQ(tokenTypes.at("A"), 1u);
      EXPECT_EQ(tokenTypes.at("'a'"), 1u);
      std::map<std::string, size_t> ruleIndexes = recognizers[i]->getRuleIndexMap();
      EXPECT_EQ(ruleIndexes.at("rule" + std::to_string(i)), 0u);
    }
  }

}  // namespace
}  // namespace antlr4
