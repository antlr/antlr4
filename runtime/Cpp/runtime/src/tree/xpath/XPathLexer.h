
// Generated from XPathLexer.g4 by ANTLR 4.5.3

#pragma once


#include "antlr4-runtime.h"
#include "Vocabulary.h"

namespace antlr4 {

class XPathLexer : public Lexer {
public:
  enum {
    TOKEN_REF = 1, RULE_REF = 2, ANYWHERE = 3, ROOT = 4, WILDCARD = 5, BANG = 6, 
    ID = 7, STRING = 8
  };

  XPathLexer(CharStream *input);
  ~XPathLexer();

  virtual std::string getGrammarFileName() const override;
  virtual const std::vector<std::string>& getRuleNames() const override;

  virtual const std::vector<std::string>& getModeNames() const override;
  virtual const std::vector<std::string>& getTokenNames() const override; // deprecated, use vocabulary instead
  virtual const dfa::Vocabulary& getVocabulary() const override;

  virtual const std::vector<uint16_t> getSerializedATN() const override;
  virtual const atn::ATN& getATN() const override;

  virtual void action(Ref<RuleContext> const& context, int ruleIndex, int actionIndex) override;

private:
  static std::vector<dfa::DFA> _decisionToDFA;
  static atn::PredictionContextCache _sharedContextCache;
  static std::vector<std::string> _ruleNames;
  static std::vector<std::string> _tokenNames;
  static std::vector<std::string> _modeNames;

  static std::vector<std::string> _literalNames;
  static std::vector<std::string> _symbolicNames;
  static dfa::Vocabulary _vocabulary;
  static atn::ATN _atn;
  static std::vector<uint16_t> _serializedATN;


  // Individual action functions triggered by action() above.
  void IDAction(Ref<RuleContext> const& context, int actionIndex);

  // Individual semantic predicate functions triggered by sempred() above.

  struct Initializer {
    Initializer();
  };
  static Initializer _init;
};

}  // namespace antlr4
