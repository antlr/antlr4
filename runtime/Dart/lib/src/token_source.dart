/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import 'dart:math';

import 'input_stream.dart';
import 'misc/pair.dart';
import 'token.dart';
import 'token_factory.dart';

/// A source of tokens must provide a sequence of tokens via {@link #nextToken()}
/// and also must reveal it's source of characters; [CommonToken]'s text is
/// computed from a [CharStream]; it only store indices into the char
/// stream.
///
/// <p>Errors from the lexer are never passed to the parser. Either you want to keep
/// going or you do not upon token recognition error. If you do not want to
/// continue lexing then you do not want to continue parsing. Just throw an
/// exception not under [RecognitionException] and Java will naturally toss
/// you all the way out of the recognizers. If you want to continue lexing then
/// you should not throw an exception to the parser--it has already requested a
/// token. Keep lexing until you get a valid one. Just report errors and keep
/// going, looking for a valid token.</p>
abstract class TokenSource {
  /// Return a [Token] object from your input stream (usually a
  /// [CharStream]). Do not fail/return upon lexing error; keep chewing
  /// on the characters until you get a good one; errors are not passed through
  /// to the parser.
  Token nextToken();

  /// Get the line number for the current position in the input stream. The
  /// first line in the input is line 1.
  ///
  /// @return The line number for the current position in the input stream, or
  /// 0 if the current token source does not track line numbers.
  int? get line;

  /// Get the index into the current line for the current position in the input
  /// stream. The first character on a line has position 0.
  ///
  /// @return The line number for the current position in the input stream, or
  /// -1 if the current token source does not track character positions.
  int get charPositionInLine;

  /// Get the [CharStream] from which this token source is currently
  /// providing tokens.
  ///
  /// @return The [CharStream] associated with the current position in
  /// the input, or null if no input stream is available for the token
  /// source.
  CharStream? get inputStream;

  /// Gets the name of the underlying input source. This method returns a
  /// non-null, non-empty string. If such a name is not known, this method
  /// returns {@link IntStream#UNKNOWN_SOURCE_NAME}.
  String get sourceName;

  /// Set the [TokenFactory] this token source should use for creating
  /// [Token] objects from the input.
  ///
  /// @param factory The [TokenFactory] to use for creating tokens.
  set tokenFactory(TokenFactory factory);

  /// Gets the [TokenFactory] this token source is currently using for
  /// creating [Token] objects from the input.
  ///
  /// @return The [TokenFactory] currently used by this token source.
  TokenFactory get tokenFactory;
}

/// Provides an implementation of [TokenSource] as a wrapper around a list
/// of [Token] objects.
///
/// <p>If the final token in the list is an {@link Token#EOF} token, it will be used
/// as the EOF token for every call to {@link #nextToken} after the end of the
/// list is reached. Otherwise, an EOF token will be created.</p>
class ListTokenSource implements TokenSource {
  /// The wrapped collection of [Token] objects to return.
  final List<Token> tokens;

  final String? _sourceName;

  /// The index into {@link #tokens} of token to return by the next call to
  /// {@link #nextToken}. The end of the input is indicated by this value
  /// being greater than or equal to the number of items in {@link #tokens}.
  late int i; // todo: uncertain

  /// This field caches the EOF token for the token source.
  Token? eofToken;

  /// This is the backing field for {@link #getTokenFactory} and
  /// [setTokenFactory].
  @override
  TokenFactory tokenFactory = CommonTokenFactory.DEFAULT;

  /**
   * Constructs a new [ListTokenSource] instance from the specified
   * collection of [Token] objects.
   *
   * @param tokens The collection of [Token] objects to provide as a
   * [TokenSource].
   * @exception NullPointerException if [tokens] is null
   */

  /// Constructs a new [ListTokenSource] instance from the specified
  /// collection of [Token] objects and source name.
  ///
  /// @param tokens The collection of [Token] objects to provide as a
  /// [TokenSource].
  /// @param sourceName The name of the [TokenSource]. If this value is
  /// null, {@link #getSourceName} will attempt to infer the name from
  /// the next [Token] (or the previous token if the end of the input has
  /// been reached).
  ///
  /// @exception NullPointerException if [tokens] is null
  ListTokenSource(this.tokens, [this._sourceName]);

  /// {@inheritDoc}

  @override
  int get charPositionInLine {
    if (i < tokens.length) {
      return tokens[i].charPositionInLine;
    } else if (eofToken != null) {
      return eofToken!.charPositionInLine;
    } else if (tokens.isNotEmpty) {
      // have to calculate the result from the line/column of the previous
      // token, along with the text of the token.
      final lastToken = tokens[tokens.length - 1];
      final tokenText = lastToken.text;
      if (tokenText != null) {
        final lastNewLine = tokenText.lastIndexOf('\n');
        if (lastNewLine >= 0) {
          return tokenText.length - lastNewLine - 1;
        }
      }

      return lastToken.charPositionInLine +
          lastToken.stopIndex -
          lastToken.startIndex +
          1;
    }

    // only reach this if tokens is empty, meaning EOF occurs at the first
    // position in the input
    return 0;
  }

  /// {@inheritDoc}

  @override
  Token nextToken() {
    if (i >= tokens.length) {
      if (eofToken == null) {
        var start = -1;
        if (tokens.isNotEmpty) {
          final previousStop = tokens[tokens.length - 1].stopIndex;
          if (previousStop != -1) {
            start = previousStop + 1;
          }
        }

        final stop = max(-1, start - 1);
        eofToken = tokenFactory.create(
          Token.EOF,
          'EOF',
          Pair(this, inputStream),
          Token.DEFAULT_CHANNEL,
          start,
          stop,
          line,
          charPositionInLine,
        );
      }

      return eofToken!;
    }

    final t = tokens[i];
    if (i == tokens.length - 1 && t.type == Token.EOF) {
      eofToken = t;
    }

    i++;
    return t;
  }

  /// {@inheritDoc}

  @override
  int? get line {
    if (i < tokens.length) {
      return tokens[i].line;
    } else if (eofToken != null) {
      return eofToken!.line;
    } else if (tokens.isNotEmpty) {
      // have to calculate the result from the line/column of the previous
      // token, along with the text of the token.
      final lastToken = tokens[tokens.length - 1];
      var line = lastToken.line ?? 0;

      final tokenText = lastToken.text;
      if (tokenText != null) {
        for (var i = 0; i < tokenText.length; i++) {
          if (tokenText[i] == '\n') {
            line++;
          }
        }
      }

      // if no text is available, assume the token did not contain any newline characters.
      return line;
    }

    // only reach this if tokens is empty, meaning EOF occurs at the first
    // position in the input
    return 1;
  }

  /// {@inheritDoc}

  @override
  CharStream? get inputStream {
    if (i < tokens.length) {
      return tokens[i].inputStream;
    } else if (eofToken != null) {
      return eofToken!.inputStream;
    } else if (tokens.isNotEmpty) {
      return tokens[tokens.length - 1].inputStream;
    }

    // no input stream information is available
    return null;
  }

  /// The name of the input source. If this value is null, a call to
  /// {@link #getSourceName} should return the source name used to create the
  /// the next token in {@link #tokens} (or the previous token if the end of
  /// the input has been reached).
  @override
  String get sourceName => _sourceName ?? inputStream?.sourceName ?? 'List';
}
