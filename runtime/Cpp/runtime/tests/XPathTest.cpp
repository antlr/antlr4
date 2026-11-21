#include <memory>
#include <string>
#include <thread>
#include <vector>

#include "gtest/gtest.h"
#include "antlr4-runtime.h"
#include "tree/xpath/XPath.h"

namespace antlr4 {
namespace {

  class TestContext final : public ParserRuleContext {
  public:
    using ParserRuleContext::ParserRuleContext;

    size_t getRuleIndex() const override { return 0; }
  };

  class TestParser final : public Parser {
  public:
    explicit TestParser(TokenStream *input) : Parser(input) {}

    std::string getGrammarFileName() const override { return "Test.g4"; }

    const std::vector<std::string>& getRuleNames() const override {
      static const std::vector<std::string> ruleNames = { "r" };
      return ruleNames;
    }

    const dfa::Vocabulary& getVocabulary() const override {
      static const dfa::Vocabulary vocabulary({}, {});
      return vocabulary;
    }

    const atn::ATN& getATN() const override {
      static const atn::ATN atn;
      return atn;
    }
  };

  struct Tree final {
    std::vector<std::unique_ptr<TestContext>> nodes;
    TestContext *root = nullptr;
  };

  // Builds r(r r r ...) with `width` children.
  Tree makeTree(size_t width) {
    Tree tree;
    tree.nodes.push_back(std::make_unique<TestContext>());
    tree.root = tree.nodes.back().get();
    for (size_t i = 0; i < width; ++i) {
      tree.nodes.push_back(std::make_unique<TestContext>(tree.root, 0));
      tree.root->addChild(tree.nodes.back().get());
    }
    return tree;
  }

  TEST(XPath, FindAllReturnsOnlyTreeNodes) {
    std::vector<std::unique_ptr<Token>> tokens;
    tokens.push_back(std::make_unique<CommonToken>(Token::EOF));
    ListTokenSource source(std::move(tokens));
    CommonTokenStream stream(&source);
    TestParser parser(&stream);
    Tree tree = makeTree(3);

    std::vector<tree::ParseTree *> found = tree::xpath::XPath::findAll(tree.root, "//*", &parser);
    ASSERT_EQ(found.size(), tree.nodes.size());
    for (size_t i = 0; i < found.size(); ++i) {
      EXPECT_EQ(found[i], tree.nodes[i].get());
    }
  }

  TEST(XPath, FindAllIsThreadSafeAcrossParsers) {
    constexpr size_t kThreads = 8;
    constexpr size_t kIterations = 2000;
    constexpr size_t kWidth = 4;

    {
      // Populate the shared token type / rule index caches up front so that
      // only XPath::findAll runs concurrently below.
      std::vector<std::unique_ptr<Token>> tokens;
      tokens.push_back(std::make_unique<CommonToken>(Token::EOF));
      ListTokenSource source(std::move(tokens));
      CommonTokenStream stream(&source);
      TestParser parser(&stream);
      parser.getTokenType("r");
      parser.getRuleIndex("r");
    }

    std::vector<std::thread> threads;
    std::vector<int> failures(kThreads, 0);
    for (size_t t = 0; t < kThreads; ++t) {
      threads.emplace_back([&failures, t]() {
        std::vector<std::unique_ptr<Token>> tokens;
        tokens.push_back(std::make_unique<CommonToken>(Token::EOF));
        ListTokenSource source(std::move(tokens));
        CommonTokenStream stream(&source);
        TestParser parser(&stream);
        Tree tree = makeTree(kWidth);

        for (size_t i = 0; i < kIterations; ++i) {
          std::vector<tree::ParseTree *> found = tree::xpath::XPath::findAll(tree.root, "/r/r", &parser);
          bool ok = found.size() == kWidth;
          for (auto *node : found) {
            ok = ok && node->parent == tree.root;
          }
          if (!ok) {
            ++failures[t];
          }
        }
      });
    }
    for (auto &thread : threads) {
      thread.join();
    }
    for (size_t t = 0; t < kThreads; ++t) {
      EXPECT_EQ(failures[t], 0) << "thread " << t << " got nodes from another thread's tree";
    }
  }

} // namespace
} // namespace antlr4
