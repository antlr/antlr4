/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import 'vocabulary.dart';
import 'atn/atn.dart';
import 'error/error.dart';
import 'input_stream.dart';
import 'rule_context.dart';
import 'token.dart';
import 'token_factory.dart';
import 'util/utils.dart';

abstract class Recognizer<ATNInterpreter extends ATNSimulator> {
  static const EOF = -1;

  static final Map<Vocabulary, Map<String, int>> tokenTypeMapCache = {};
  static final Map<List<String>, Map<String, int>> ruleIndexMapCache = {};
  final List<ErrorListener> _listeners = [ConsoleErrorListener.INSTANCE];

  /// The ATN interpreter used by the recognizer for prediction.
  ATNInterpreter? interpreter;
  int state = -1;

  List<String> get ruleNames;

  /// Get the vocabulary used by the recognizer.
  ///
  /// @return A [Vocabulary] instance providing information about the
  /// vocabulary used by the grammar.
  Vocabulary get vocabulary;

  /// Get a map from token names to token types.
  ///
  /// <p>Used for XPath and tree pattern compilation.</p>
  Map<String, int> get tokenTypeMap {
    final _vocabulary = vocabulary;

    var result = tokenTypeMapCache[_vocabulary];
    if (result == null) {
      result = {};
      for (var i = 0; i <= getATN().maxTokenType; i++) {
        final literalName = _vocabulary.getLiteralName(i);
        if (literalName != null) {
          result[literalName] = i;
        }

        final symbolicName = _vocabulary.getSymbolicName(i);
        if (symbolicName != null) {
          result[symbolicName] = i;
        }
      }

      result['EOF'] = Token.EOF;
      result = Map.unmodifiable(result);
      tokenTypeMapCache[_vocabulary] = result;
    }

    return result;
  }

  /// Get a map from rule names to rule indexes.
  ///
  /// <p>Used for XPath and tree pattern compilation.</p>
  Map<String, int> get ruleIndexMap {
    var result = ruleIndexMapCache[ruleNames];
    if (result == null) {
      result = Map.unmodifiable(toMap(ruleNames));
      ruleIndexMapCache[ruleNames] = result;
    }

    return result;
  }

  int getTokenType(String tokenName) {
    final ttype = tokenTypeMap[tokenName];
    if (ttype != null) return ttype;
    return Token.INVALID_TYPE;
  }

  /// If this recognizer was generated, it will have a serialized ATN
  /// representation of the grammar.
  ///
  /// <p>For interpreters, we don't know their serialized ATN despite having
  /// created the interpreter from it.</p>
  List<int> get serializedATN {
    throw UnsupportedError('there is no serialized ATN');
  }

  /// For debugging and other purposes, might want the grammar name.
  ///  Have ANTLR generate an implementation for this method.
  String get grammarFileName;

  /// Get the [ATN] used by the recognizer for prediction.
  ///
  /// @return The [ATN] used by the recognizer for prediction.
  ATN getATN();

  /// If profiling during the parse/lex, this will return DecisionInfo records
  ///  for each decision in recognizer in a ParseInfo object.
  ///
  /// @since 4.3
  ParseInfo? get parseInfo {
    return null;
  }

  /// What is the error header, normally line/character position information? */
  String getErrorHeader(RecognitionException e) {
    final line = e.offendingToken.line;
    final charPositionInLine = e.offendingToken.charPositionInLine;
    return 'line $line:$charPositionInLine';
  }

  void addErrorListener(
    ErrorListener listener,
  ) {
    _listeners.add(listener);
  }

  void removeErrorListener(ErrorListener listener) {
    _listeners.remove(listener);
  }

  void removeErrorListeners() {
    _listeners.clear();
  }

  List<ErrorListener> get errorListeners {
    return _listeners;
  }

  ErrorListener get errorListenerDispatch {
    return ProxyErrorListener(errorListeners);
  }

  // subclass needs to override these if there are sempreds or actions
  // that the ATN interp needs to execute
  bool sempred(RuleContext? _localctx, int ruleIndex, int actionIndex) {
    return true;
  }

  bool precpred(RuleContext? localctx, int precedence) {
    return true;
  }

  void action(RuleContext? _localctx, int ruleIndex, int actionIndex) {}

  IntStream get inputStream;

  set inputStream(covariant IntStream input);

  TokenFactory get tokenFactory;

  set tokenFactory(TokenFactory input);
}
