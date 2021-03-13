/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import 'dart:developer';

import 'atn/atn.dart';
import 'error/error.dart';
import 'input_stream.dart';
import 'interval_set.dart';
import 'misc/pair.dart';
import 'recognizer.dart';
import 'token.dart';
import 'token_factory.dart';
import 'token_source.dart';
import 'util/utils.dart';

abstract class Lexer extends Recognizer<LexerATNSimulator>
    implements TokenSource {
  static final DEFAULT_MODE = 0;
  static final MORE = -2;
  static final SKIP = -3;

  static final DEFAULT_TOKEN_CHANNEL = Token.DEFAULT_CHANNEL;
  static final HIDDEN = Token.HIDDEN_CHANNEL;
  static final MIN_CHAR_VALUE = 0x0000;
  static final MAX_CHAR_VALUE = 0x10FFFF;

  CharStream _input;

  late Pair<TokenSource, CharStream?> _tokenFactorySourcePair;

  @override
  TokenFactory tokenFactory = CommonTokenFactory.DEFAULT;

  /// The goal of all lexer rules/methods is to create a token object.
  /// this is an instance variable as multiple rules may collaborate to
  /// create a single token. nextToken will return this object after
  /// matching lexer rule(s). If you subclass to allow multiple token
  /// emissions, then set this to the last token to be matched or
  /// something nonnull so that the auto token emit mechanism will not
  /// emit another token.
  Token? _token;

  /// What character index in the stream did the current token start at?
  /// Needed, for example, to get the text for current token. Set at
  /// the start of nextToken.
  int tokenStartCharIndex = -1;

  /// The line on which the first character of the token resides
  int tokenStartLine = -1;

  /// The character position of first character within the line
  int tokenStartCharPositionInLine = -1;

  /// Once we see EOF on char stream, next token will be EOF.
  /// If you have DONE : EOF ; then you see DONE EOF.
  bool _hitEOF = false;

  /// The channel number for the current token
  int channel = Token.DEFAULT_CHANNEL;

  /// The token type for the current token
  int type = Token.INVALID_TYPE;

  final List<int> _modeStack = [];
  int mode_ = Lexer.DEFAULT_MODE;

  /// You can set the text for the current token to override what is in
  /// the input char buffer. Use setText() or can set this instance var.
  String? _text;

  Lexer(CharStream input) : _input = input {
    _tokenFactorySourcePair = Pair(this, input);
  }

  void reset([bool resetInput = false]) {
    // wacky Lexer state variables
    if (resetInput) {
      _input.seek(0); // rewind the input
    }
    _token = null;
    type = Token.INVALID_TYPE;
    channel = Token.DEFAULT_CHANNEL;
    tokenStartCharIndex = -1;
    tokenStartCharPositionInLine = -1;
    tokenStartLine = -1;
    _text = null;

    _hitEOF = false;
    mode_ = Lexer.DEFAULT_MODE;
    _modeStack.clear();

    interpreter?.reset();
  }

  /// Return a token from this source; i.e., match a token on the char stream.
  @override
  Token nextToken() {
    // Mark start location in char stream so unbuffered streams are
    // guaranteed at least have text of current token
    final tokenStartMarker = _input.mark();
    try {
      outer:
      while (true) {
        if (_hitEOF) {
          emitEOF();
          return _token!;
        }

        _token = null;
        channel = Token.DEFAULT_CHANNEL;
        tokenStartCharIndex = _input.index;
        tokenStartCharPositionInLine = interpreter!.charPositionInLine;
        tokenStartLine = interpreter!.line;
        _text = null;
        do {
          type = Token.INVALID_TYPE;
//				System.out.println("nextToken line "+tokenStartLine+" at "+((char)input.LA(1))+
//								   " in mode "+mode+
//								   " at index "+input.index());
          late int ttype;
          try {
            ttype = interpreter!.match(_input, mode_);
          } on LexerNoViableAltException catch (e) {
            notifyListeners(e); // report error
            recover(e);
            ttype = SKIP;
          }
          if (_input.LA(1) == IntStream.EOF) {
            _hitEOF = true;
          }
          if (type == Token.INVALID_TYPE) type = ttype;
          if (type == SKIP) {
            continue outer;
          }
        } while (type == MORE);
        if (_token == null) emit();
        return _token!;
      }
    } finally {
      // make sure we release marker after match or
      // unbuffered char stream will keep buffering
      _input.release(tokenStartMarker);
    }
  }

  /// Instruct the lexer to skip creating a token for current lexer rule
  /// and look for another token. nextToken() knows to keep looking when
  /// a lexer rule finishes with token set to SKIP_TOKEN. Recall that
  /// if token==null at end of any token rule, it creates one for you
  /// and emits it.
  void skip() {
    type = Lexer.SKIP;
  }

  void more() {
    type = Lexer.MORE;
  }

  void mode(int m) {
    mode_ = m;
  }

  void pushMode(int m) {
    if (LexerATNSimulator.debug) {
      log('pushMode $m');
    }
    _modeStack.add(mode_);
    mode(m);
  }

  int popMode() {
    if (_modeStack.isEmpty) throw StateError('');
    if (LexerATNSimulator.debug) log('popMode back to ${_modeStack.last}');
    mode(_modeStack.removeLast());
    return mode_;
  }

  /// Set the char stream and reset the lexer
  @override
  set inputStream(CharStream input) {
    _tokenFactorySourcePair = Pair(this, null);
    reset(false);
    _input = input;
    _tokenFactorySourcePair = Pair(this, _input);
  }

  @override
  String get sourceName {
    return _input.sourceName;
  }

  @override
  CharStream get inputStream {
    return _input;
  }

  /// By default does not support multiple emits per nextToken invocation
  ///  for efficiency reasons.  Subclass and override this method, nextToken,
  ///  and getToken (to push tokens into a list and pull from that list
  ///  rather than a single variable as this implementation does).
  void emitToken(Token token) {
    //System.err.println("emit "+token);
    _token = token;
  }

  /// The standard method called to automatically emit a token at the
  ///  outermost lexical rule.  The token object should point into the
  ///  char buffer start..stop.  If there is a text override in 'text',
  ///  use that to set the token's text.  Override this method to emit
  ///  custom Token objects or provide a new factory.
  Token emit() {
    final t = tokenFactory.create(
        type,
        _text,
        _tokenFactorySourcePair,
        channel,
        tokenStartCharIndex,
        charIndex - 1,
        tokenStartLine,
        tokenStartCharPositionInLine);
    emitToken(t);
    return t;
  }

  Token emitEOF() {
    final cpos = charPositionInLine;
    final eof = tokenFactory.create(
      Token.EOF,
      null,
      _tokenFactorySourcePair,
      Token.DEFAULT_CHANNEL,
      _input.index,
      _input.index - 1,
      line,
      cpos,
    );
    emitToken(eof);
    return eof;
  }

  @override
  int get charPositionInLine {
    return interpreter!.charPositionInLine;
  }

  @override
  int get line {
    return interpreter!.line;
  }

  set line(int line) {
    interpreter!.line = line;
  }

  set charPositionInLine(int charPositionInLine) {
    interpreter!.charPositionInLine = charPositionInLine;
  }

  /// What is the index of the current character of lookahead?
  int get charIndex {
    return _input.index;
  }

  /// Return the text matched so far for the current token or any
  ///  text override.
  String get text {
    if (_text != null) {
      return _text!;
    }
    return interpreter!.getText(_input);
  }

  /// Set the complete text of this token; it wipes any previous
  ///  changes to the text.
  set text(String text) {
    _text = text;
  }

  /// Override if emitting multiple tokens.
  Token? get token {
    return _token;
  }

  void setToken(Token _token) {
    this._token = _token;
  }

  List<String>? get channelNames => null;

  List<String>? get modeNames => null;

  /// Return a list of all Token objects in input char stream.
  ///  Forces load of all tokens. Does not include EOF token.
  List<Token> get allTokens {
    final tokens = <Token>[];
    var t = nextToken();
    while (t.type != Token.EOF) {
      tokens.add(t);
      t = nextToken();
    }
    return tokens;
  }

  void notifyListeners(LexerNoViableAltException e) {
    final text = _input.getText(Interval.of(tokenStartCharIndex, _input.index));
    final msg = "token recognition error at: '" + getErrorDisplay(text) + "'";

    final listener = errorListenerDispatch;
    listener.syntaxError(
      this,
      null,
      tokenStartLine,
      tokenStartCharPositionInLine,
      msg,
      e,
    );
  }

  String getErrorDisplay(String s) {
    return escapeWhitespace(s);
  }

  String getCharErrorDisplay(int c) {
    final s = getErrorDisplay(String.fromCharCode(c));
    return "'$s'";
  }

  /// Lexers can normally match any char in it's vocabulary after matching
  ///  a token, so do the easy thing and just kill a character and hope
  ///  it all works out.  You can instead use the rule invocation stack
  ///  to do sophisticated error recovery if you are in a fragment rule.
  void recover(RecognitionException re) {
    if (re is LexerNoViableAltException) {
      if (_input.LA(1) != IntStream.EOF) {
        // skip a char and try again
        interpreter!.consume(_input);
      }
    } else {
      //System.out.println("consuming char "+(char)input.LA(1)+" during recovery");
      //re.printStackTrace();
      // TODO: Do we lose character or line position information?
      _input.consume();
    }
  }
}
