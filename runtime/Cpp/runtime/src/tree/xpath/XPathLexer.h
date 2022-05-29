
// Generated from XPathLexer.g4 by ANTLR 4.9.3

#pragma once


#include "antlr4-runtime.h"


class  XPathLexer : public antlr4::Lexer {
public:
  enum {
    TOKEN_REF = 1, RULE_REF = 2, ANYWHERE = 3, ROOT = 4, WILDCARD = 5, BANG = 6,
    ID = 7, STRING = 8
  };

  explicit XPathLexer(antlr4::CharStream *input);

  ~XPathLexer() override;

  virtual std::string getGrammarFileName() const override;

  virtual const std::vector<std::string>& getRuleNames() const override;

  virtual const std::vector<std::string>& getChannelNames() const override;

  virtual const std::vector<std::string>& getModeNames() const override;

  virtual const antlr4::dfa::Vocabulary& getVocabulary() const override;

  virtual antlr4::atn::SerializedATNView getSerializedATN() const override;

  virtual const antlr4::atn::ATN& getATN() const override;

  virtual void action(antlr4::RuleContext *context, size_t ruleIndex, size_t actionIndex) override;

  // By default the static state used to implement the lexer is lazily initialized during the first
  // call to the constructor. You can call this function if you wish to initialize the static state
  // ahead of time.
  static void initialize();
private:
  // Individual action functions triggered by action() above.
  void IDAction(antlr4::RuleContext *context, size_t actionIndex);

  // Individual semantic predicate functions triggered by sempred() above.
};

