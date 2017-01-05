/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

namespace antlr4 {
  class IllegalStateException;
  class IllegalArgumentException;
  class NoSuchElementException;
  class NullPointerException;
  class InputMismatchException;
  class ParseCancellationException;
  class InputMismatchException;
  class EmptyStackException;
  class LexerNoViableAltException;

  class ANTLRErrorListener;
  class ANTLRErrorStrategy;
  class ANTLRFileStream;
  class ANTLRInputStream;
  class BailErrorStrategy;
  class BaseErrorListener;
  class BufferedTokenStream;
  class CharStream;
  class CommonToken;
  class CommonTokenFactory;
  class CommonTokenStream;
  class ConsoleErrorListener;
  class DefaultErrorStrategy;
  class DiagnosticErrorListener;
  class FailedPredicateException;
  class InputMismatchException;
  class IntStream;
  class InterpreterRuleContext;
  class IRecognizer;
  class Lexer;
  class LexerInterpreter;
  class LexerNoViableAltException;
  class ListTokenSource;
  class NoViableAltException;
  class Parser;
  class ParserInterpreter;
  class ParserRuleContext;
  class ProxyErrorListener;
  class RecognitionException;
  class Recognizer;
  class RuleContext;
  class Token;
  template<typename Symbol> class TokenFactory;
  class TokenSource;
  class TokenStream;
  class TokenStreamRewriter;
  class UnbufferedCharStream;
  class UnbufferedTokenStream;
  class WritableToken;

  namespace misc {
    class Interval;
    class IntervalSet;
    class MurmurHash;
    class ParseCancellationException;
    class Utils;
    class Predicate;
  }
  namespace atn {
    class ATN;
    class ATNConfig;
    class ATNConfigSet;
    class ATNDeserializationOptions;
    class ATNDeserializer;
    class ATNSerializer;
    class ATNSimulator;
    class ATNState;
    enum class ATNType;
    class AbstractPredicateTransition;
    class ActionTransition;
    class ArrayPredictionContext;
    class AtomTransition;
    class BasicBlockStartState;
    class BasicState;
    class BlockEndState;
    class BlockStartState;
    class DecisionState;
    class EmptyPredictionContext;
    class EpsilonTransition;
    class LL1Analyzer;
    class LexerAction;
    class LexerActionExecutor;
    class LexerATNConfig;
    class LexerATNSimulator;
    class LexerMoreAction;
    class LexerPopModeAction;
    class LexerSkipAction;
    class LookaheadEventInfo;
    class LoopEndState;
    class NotSetTransition;
    class OrderedATNConfigSet;
    class ParseInfo;
    class ParserATNSimulator;
    class PlusBlockStartState;
    class PlusLoopbackState;
    class PrecedencePredicateTransition;
    class PredicateTransition;
    class PredictionContext;
    enum class PredictionMode;
    class PredictionModeClass;
    class RangeTransition;
    class RuleStartState;
    class RuleStopState;
    class RuleTransition;
    class SemanticContext;
    class SetTransition;
    class SingletonPredictionContext;
    class StarBlockStartState;
    class StarLoopEntryState;
    class StarLoopbackState;
    class TokensStartState;
    class Transition;
    class WildcardTransition;
  }
  namespace dfa {
    class DFA;
    class DFASerializer;
    class DFAState;
    class LexerDFASerializer;
    class Vocabulary;
  }
  namespace tree {
    class AbstractParseTreeVisitor;
    class ErrorNode;
    class ErrorNodeImpl;
    class ParseTree;
    class ParseTreeListener;
    template<typename T> class ParseTreeProperty;
    class ParseTreeVisitor;
    class ParseTreeWalker;
    class SyntaxTree;
    class TerminalNode;
    class TerminalNodeImpl;
    class Tree;
    class Trees;

    namespace pattern {
      class Chunk;
      class ParseTreeMatch;
      class ParseTreePattern;
      class ParseTreePatternMatcher;
      class RuleTagToken;
      class TagChunk;
      class TextChunk;
      class TokenTagToken;
    }

    namespace xpath {
      class XPath;
      class XPathElement;
      class XPathLexerErrorListener;
      class XPathRuleAnywhereElement;
      class XPathRuleElement;
      class XPathTokenAnywhereElement;
      class XPathTokenElement;
      class XPathWildcardAnywhereElement;
      class XPathWildcardElement;
    }
  }
}
