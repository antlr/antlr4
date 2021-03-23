/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import 'input_stream.dart';
import 'interval_set.dart';
import 'misc/pair.dart';
import 'token_source.dart';

/// A token has properties: text, type, line, character position in the line
///  (so we can ignore tabs), token channel, index, and source from which
///  we obtained this token.
abstract class Token {
  static const int INVALID_TYPE = 0;

  /// During lookahead operations, this "token" signifies we hit rule end ATN state
  ///  and did not follow it despite needing to.
  static const int EPSILON = -2;

  static const int MIN_USER_TOKEN_TYPE = 1;

  static const int EOF = IntStream.EOF;

  /// All tokens go to the parser (unless skip() is called in that rule)
  ///  on a particular "channel".  The parser tunes to a particular channel
  ///  so that whitespace etc... can go to the parser on a "hidden" channel.
  static const int DEFAULT_CHANNEL = 0;

  /// Anything on different channel than DEFAULT_CHANNEL is not parsed
  ///  by parser.
  static const int HIDDEN_CHANNEL = 1;

  /// This is the minimum constant value which can be assigned to a
  /// user-defined token channel.
  ///
  /// <p>
  /// The non-negative numbers less than {@link #MIN_USER_CHANNEL_VALUE} are
  /// assigned to the predefined channels {@link #DEFAULT_CHANNEL} and
  /// {@link #HIDDEN_CHANNEL}.</p>
  ///
  /// @see Token#getChannel()
  static const int MIN_USER_CHANNEL_VALUE = 2;

  /// Get the text of the token.
  String? get text;

  /// Get the token type of the token */
  int get type;

  /// The line number on which the 1st character of this token was matched,
  ///  line=1..n
  int? get line;

  /// The index of the first character of this token relative to the
  ///  beginning of the line at which it occurs, 0..n-1
  int get charPositionInLine;

  /// Return the channel this token. Each token can arrive at the parser
  ///  on a different channel, but the parser only "tunes" to a single channel.
  ///  The parser ignores everything not on DEFAULT_CHANNEL.
  int get channel;

  /// An index from 0..n-1 of the token object in the input stream.
  ///  This must be valid in order to print token streams and
  ///  use TokenRewriteStream.
  ///
  ///  Return -1 to indicate that this token was conjured up since
  ///  it doesn't have a valid index.
  int get tokenIndex;

  /// The starting character index of the token
  ///  This method is optional; return -1 if not implemented.
  int get startIndex;

  /// The last character index of the token.
  ///  This method is optional; return -1 if not implemented.
  int get stopIndex;

  /// Gets the [TokenSource] which created this token.
  TokenSource? get tokenSource;

  /// Gets the [CharStream] from which this token was derived.
  CharStream? get inputStream;
}

abstract class WritableToken extends Token {
  set text(String? text);

  set type(int ttype);

  set line(int? line);

  set charPositionInLine(int pos);

  set channel(int channel);

  set tokenIndex(int index);
}

class CommonToken extends WritableToken {
  /// An empty [Pair] which is used as the default value of
  /// {@link #source} for tokens that do not have a source.
  static const EMPTY_SOURCE = Pair<TokenSource?, CharStream?>(null, null);

  @override
  int type;

  @override
  int? line;

  @override
  int charPositionInLine = -1; // set to invalid position

  @override
  int channel = Token.DEFAULT_CHANNEL;

  /// These properties share a field to reduce the memory footprint of
  /// [CommonToken]. Tokens created by a [CommonTokenFactory] from
  /// the same source and input stream share a reference to the same
  /// [Pair] containing these values.</p>
  late Pair<TokenSource?, CharStream?> source;

  /// This is the backing field for {@link #getText} when the token text is
  /// explicitly set in the constructor or via {@link #setText}.
  ///
  /// @see #getText()
  String? _text;

  @override
  int tokenIndex = -1;

  @override
  int startIndex;

  @override
  int stopIndex;

  /// Constructs a new [CommonToken] with the specified token type and
  /// text.
  ///
  /// @param type The token type.
  /// @param text The text of the token.
  CommonToken(
    this.type, {
    this.source = EMPTY_SOURCE,
    this.channel = Token.DEFAULT_CHANNEL,
    this.startIndex = -1,
    this.stopIndex = -1,
    text,
  }) {
    _text = text;
    if (source.a != null) {
      line = source.a!.line;
      charPositionInLine = source.a!.charPositionInLine;
    }
  }

  /// Constructs a new [CommonToken] as a copy of another [Token].
  ///
  /// <p>
  /// If [oldToken] is also a [CommonToken] instance, the newly
  /// constructed token will share a reference to the {@link #text} field and
  /// the [Pair] stored in {@link #source}. Otherwise, {@link #text} will
  /// be assigned the result of calling {@link #getText}, and {@link #source}
  /// will be constructed from the result of {@link Token#getTokenSource} and
  /// {@link Token#getInputStream}.</p>
  ///
  /// @param oldToken The token to copy.
  CommonToken.copy(Token oldToken)
      : type = oldToken.type,
        line = oldToken.line,
        tokenIndex = oldToken.tokenIndex,
        charPositionInLine = oldToken.charPositionInLine,
        channel = oldToken.channel,
        startIndex = oldToken.startIndex,
        stopIndex = oldToken.stopIndex {
    if (oldToken is CommonToken) {
      _text = oldToken.text;
      source = oldToken.source;
    } else {
      _text = oldToken.text;
      source = Pair<TokenSource?, CharStream?>(
        oldToken.tokenSource,
        oldToken.inputStream,
      );
    }
  }

  @override
  String? get text {
    if (_text != null) {
      return _text;
    }

    final input = inputStream;
    if (input == null) return null;
    final n = input.size;

    if (startIndex < n && stopIndex < n) {
      return input.getText(Interval.of(startIndex, stopIndex));
    } else {
      return '<EOF>';
    }
  }

  /// Explicitly set the text for this token. If {code text} is not
  /// null, then {@link #getText} will return this value rather than
  /// extracting the text from the input.
  ///
  /// @param text The explicit text of the token, or null if the text
  /// should be obtained from the input along with the start and stop indexes
  /// of the token.
  @override
  set text(String? text) {
    _text = text;
  }

  @override
  TokenSource? get tokenSource {
    return source.a;
  }

  @override
  CharStream? get inputStream {
    return source.b;
  }

  @override
  String toString([void _]) {
    var txt = text;
    if (txt != null) {
      txt = txt
          .replaceAll('\n', r'\n')
          .replaceAll('\r', r'\r')
          .replaceAll('\t', r'\t');
    } else {
      txt = '<no text>';
    }
    return "[@$tokenIndex,$startIndex:$stopIndex='$txt',<$type>" +
        (channel > 0 ? ',channel=$channel' : '') +
        ',$line:$charPositionInLine]';
  }
}

/// A [Token] object representing an entire subtree matched by a parser
/// rule; e.g., {@code <expr>}. These tokens are created for [TagChunk]
/// chunks where the tag corresponds to a parser rule.
class RuleTagToken implements Token {
  /// Gets the name of the rule associated with this rule tag.
  ///
  /// @return The name of the parser rule associated with this rule tag.
  final String ruleName;

  /// The token type for the current token. This is the token type assigned to
  /// the bypass alternative for the rule during ATN deserialization.
  final int bypassTokenType;

  /// Gets the label associated with the rule tag.
  ///
  /// @return The name of the label associated with the rule tag, or
  /// null if this is an unlabeled rule tag.
  final String? label;

  /// Constructs a new instance of [RuleTagToken] with the specified rule
  /// name, bypass token type, and label.
  ///
  /// @param ruleName The name of the parser rule this rule tag matches.
  /// @param bypassTokenType The bypass token type assigned to the parser rule.
  /// @param label The label associated with the rule tag, or null if
  /// the rule tag is unlabeled.
  ///
  /// @exception ArgumentError.value(value) if [ruleName] is null
  /// or empty.
  RuleTagToken(this.ruleName, this.bypassTokenType, [this.label]) {
    if (ruleName.isEmpty) {
      throw ArgumentError.value(
        ruleName,
        'ruleName',
        'cannot be empty.',
      );
    }
  }

  /// {@inheritDoc}
  ///
  /// <p>Rule tag tokens are always placed on the {@link #DEFAULT_CHANNEL}.</p>

  @override
  int get channel {
    return Token.DEFAULT_CHANNEL;
  }

  /// {@inheritDoc}
  ///
  /// <p>This method returns the rule tag formatted with {@code <} and {@code >}
  /// delimiters.</p>

  @override
  String get text {
    if (label != null) {
      return '<' + label! + ':' + ruleName + '>';
    }

    return '<' + ruleName + '>';
  }

  /// {@inheritDoc}
  ///
  /// <p>Rule tag tokens have types assigned according to the rule bypass
  /// transitions created during ATN deserialization.</p>

  @override
  int get type {
    return bypassTokenType;
  }

  /// {@inheritDoc}
  ///
  /// <p>The implementation for [RuleTagToken] always returns 0.</p>

  @override
  int get line {
    return 0;
  }

  /// {@inheritDoc}
  ///
  /// <p>The implementation for [RuleTagToken] always returns -1.</p>
  @override
  int get charPositionInLine {
    return -1;
  }

  /// {@inheritDoc}
  ///
  /// <p>The implementation for [RuleTagToken] always returns -1.</p>
  @override
  int get tokenIndex {
    return -1;
  }

  /// {@inheritDoc}
  ///
  /// <p>The implementation for [RuleTagToken] always returns -1.</p>
  @override
  int get startIndex {
    return -1;
  }

  /// {@inheritDoc}
  ///
  /// <p>The implementation for [RuleTagToken] always returns -1.</p>

  @override
  int get stopIndex {
    return -1;
  }

  /// {@inheritDoc}
  ///
  /// <p>The implementation for [RuleTagToken] always returns null.</p>

  @override
  TokenSource? get tokenSource {
    return null;
  }

  /// {@inheritDoc}
  ///
  /// <p>The implementation for [RuleTagToken] always returns null.</p>

  @override
  CharStream? get inputStream {
    return null;
  }

  /// {@inheritDoc}
  ///
  /// <p>The implementation for [RuleTagToken] returns a string of the form
  /// {@code ruleName:bypassTokenType}.</p>

  @override
  String toString() {
    return ruleName + ':$bypassTokenType';
  }
}

/// A [Token] object representing a token of a particular type; e.g.,
/// {@code <ID>}. These tokens are created for [TagChunk] chunks where the
/// tag corresponds to a lexer rule or token type.
class TokenTagToken extends CommonToken {
  /// Gets the token name.
  /// @return The token name.
  final String tokenName;

  /// Gets the label associated with the rule tag.
  ///
  /// @return The name of the label associated with the rule tag, or
  /// null if this is an unlabeled rule tag.
  final String? label;

  /// Constructs a new instance of [TokenTagToken] with the specified
  /// token name, type, and label.
  ///
  /// @param tokenName The token name.
  /// @param type The token type.
  /// @param label The label associated with the token tag, or null if
  /// the token tag is unlabeled.
  TokenTagToken(this.tokenName, type, [this.label]) : super(type);

  /// {@inheritDoc}
  ///
  /// <p>The implementation for [TokenTagToken] returns the token tag
  /// formatted with {@code <} and {@code >} delimiters.</p>

  @override
  String get text {
    if (label != null) {
      return '<' + label! + ':' + tokenName + '>';
    }

    return '<' + tokenName + '>';
  }

  /// {@inheritDoc}
  ///
  /// <p>The implementation for [TokenTagToken] returns a string of the form
  /// {@code tokenName:type}.</p>

  @override
  String toString([void _]) {
    return tokenName + ':$type';
  }
}
