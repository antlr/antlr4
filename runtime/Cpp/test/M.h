
// Generated from /var/folders/fv/13zbdzw17cdczn_rbnt5m_140000gn/T/TestCompositeLexers-1465807145492/M.g4 by ANTLR 4.5.3

#pragma once


#include "antlr4-runtime.h"


using namespace antlr4;



class M : public Lexer {
public:
  enum {
    A = 1, WS = 2, B = 3
  };

  M(CharStream *input);
  ~M();

  virtual std::string getGrammarFileName() const override;
  virtual const std::vector<std::string>& getRuleNames() const override;

  virtual const std::vector<std::string>& getModeNames() const override;
  virtual const std::vector<std::string>& getTokenNames() const override; // deprecated, use vocabulary instead
  virtual dfa::Vocabulary& getVocabulary() const override;

  virtual const std::vector<uint16_t> getSerializedATN() const;
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
  void AAction(Ref<RuleContext> const& context, int actionIndex);
  void BAction(Ref<RuleContext> const& context, int actionIndex);

  // Individual semantic predicate functions triggered by sempred() above.

  struct Initializer {
    Initializer();
  };
  static Initializer _init;
};

