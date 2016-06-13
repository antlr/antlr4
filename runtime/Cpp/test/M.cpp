
// Generated from /var/folders/fv/13zbdzw17cdczn_rbnt5m_140000gn/T/TestCompositeLexers-1465807145492/M.g4 by ANTLR 4.5.3


#include "M.h"


using namespace antlr4;


M::M(CharStream *input) : Lexer(input) {
  _interpreter = new atn::LexerATNSimulator(this, _atn, _decisionToDFA, _sharedContextCache);
}

M::~M() {
  delete _interpreter;
}

std::string M::getGrammarFileName() const {
  return "M.g4";
}

const std::vector<std::string>& M::getRuleNames() const {
  return _ruleNames;
}

const std::vector<std::string>& M::getModeNames() const {
  return _modeNames;
}

const std::vector<std::string>& M::getTokenNames() const {
  return _tokenNames;
}

dfa::Vocabulary& M::getVocabulary() const {
  return _vocabulary;
}

const std::vector<uint16_t> M::getSerializedATN() const {
  return _serializedATN;
}

const atn::ATN& M::getATN() const {
  return _atn;
}


void M::action(Ref<RuleContext> const& context, int ruleIndex, int actionIndex) {
  switch (ruleIndex) {
    case 0: AAction(std::dynamic_pointer_cast<RuleContext>(context), actionIndex); break;
    case 2: BAction(std::dynamic_pointer_cast<RuleContext>(context), actionIndex); break;

  default:
    break;
  }
}

void M::AAction(Ref<RuleContext> const& context, int actionIndex) {
  switch (actionIndex) {
    case 0: std::cout << "M.A" << std::endl; break;

  default:
    break;
  }
}

void M::BAction(Ref<RuleContext> const& context, int actionIndex) {
  switch (actionIndex) {
    case 1: std::cout << "S.B" << std::endl; break;

  default:
    break;
  }
}



// Static vars and initialization.
std::vector<dfa::DFA> M::_decisionToDFA;
atn::PredictionContextCache M::_sharedContextCache;

// We own the ATN which in turn owns the ATN states.
atn::ATN M::_atn;
std::vector<uint16_t> M::_serializedATN;

std::vector<std::string> M::_ruleNames = {
  "A", "WS", "B"
};

std::vector<std::string> M::_modeNames = {
  "DEFAULT_MODE"
};

std::vector<std::string> M::_literalNames = {
  "", "", "", "'b'"
};

std::vector<std::string> M::_symbolicNames = {
  "", "A", "WS", "B"
};

dfa::Vocabulary M::_vocabulary(_literalNames, _symbolicNames);

std::vector<std::string> M::_tokenNames;

M::Initializer::Initializer() {
  // This code could be in a static initializer lambda, but VS doesn't allow access to private class members from there. 
	for (size_t i = 0; i < _symbolicNames.size(); ++i) {
		std::string name = _vocabulary.getLiteralName(i);
		if (name.empty()) {
			name = _vocabulary.getSymbolicName(i);
		}

		if (name.empty()) {
			_tokenNames.push_back("<INVALID>");
		} else {
      _tokenNames.push_back(name);
    }
	}

  _serializedATN = {
    0x3, 0x430, 0xd6d1, 0x8206, 0xad2d, 0x4417, 0xaef1, 0x8d80, 0xaadd, 
    0x2, 0x5, 0x14, 0x8, 0x1, 0x4, 0x2, 0x9, 0x2, 0x4, 0x3, 0x9, 0x3, 0x4, 
    0x4, 0x9, 0x4, 0x3, 0x2, 0x3, 0x2, 0x3, 0x2, 0x3, 0x2, 0x3, 0x3, 0x3, 
    0x3, 0x3, 0x3, 0x3, 0x3, 0x3, 0x4, 0x3, 0x4, 0x3, 0x4, 0x2, 0x2, 0x5, 
    0x3, 0x3, 0x5, 0x4, 0x7, 0x5, 0x3, 0x2, 0x3, 0x4, 0x2, 0xc, 0xc, 0x22, 
    0x22, 0x13, 0x2, 0x3, 0x3, 0x2, 0x2, 0x2, 0x2, 0x5, 0x3, 0x2, 0x2, 0x2, 
    0x2, 0x7, 0x3, 0x2, 0x2, 0x2, 0x3, 0x9, 0x3, 0x2, 0x2, 0x2, 0x5, 0xd, 
    0x3, 0x2, 0x2, 0x2, 0x7, 0x11, 0x3, 0x2, 0x2, 0x2, 0x9, 0xa, 0x7, 0x63, 
    0x2, 0x2, 0xa, 0xb, 0x5, 0x7, 0x4, 0x2, 0xb, 0xc, 0x8, 0x2, 0x2, 0x2, 
    0xc, 0x4, 0x3, 0x2, 0x2, 0x2, 0xd, 0xe, 0x9, 0x2, 0x2, 0x2, 0xe, 0xf, 
    0x3, 0x2, 0x2, 0x2, 0xf, 0x10, 0x8, 0x3, 0x3, 0x2, 0x10, 0x6, 0x3, 0x2, 
    0x2, 0x2, 0x11, 0x12, 0x7, 0x64, 0x2, 0x2, 0x12, 0x13, 0x8, 0x4, 0x4, 
    0x2, 0x13, 0x8, 0x3, 0x2, 0x2, 0x2, 0x3, 0x2, 0x5, 0x3, 0x2, 0x2, 0x8, 
    0x2, 0x2, 0x3, 0x4, 0x3, 
  };

  atn::ATNDeserializer deserializer;
  _atn = deserializer.deserialize(_serializedATN);

  for (int i = 0; i < _atn.getNumberOfDecisions(); i++) { 
    _decisionToDFA.push_back(dfa::DFA(_atn.getDecisionState(i), i));
  }
}

M::Initializer M::_init;
