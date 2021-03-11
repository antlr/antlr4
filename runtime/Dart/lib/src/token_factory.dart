/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import 'input_stream.dart';
import 'interval_set.dart';
import 'misc/pair.dart';
import 'token.dart';
import 'token_source.dart';

/// The default mechanism for creating tokens. It's used by default in Lexer and
///  the error handling strategy (to create missing tokens).  Notifying the parser
///  of a new factory means that it notifies its token source and error strategy.
abstract class TokenFactory<Symbol extends Token> {
  /// This is the method used to create tokens in the lexer and in the
  ///  error handling strategy. If text!=null, than the start and stop positions
  ///  are wiped to -1 in the text override is set in the CommonToken.
  Symbol create(
    int type,
    String? text,
    Pair<TokenSource?, CharStream?> source,
    int channel,
    int start,
    int stop,
    int? line,
    int charPositionInLine,
  );
}

/// This default implementation of [TokenFactory] creates
/// [CommonToken] objects.
class CommonTokenFactory implements TokenFactory<CommonToken> {
  /// The default [CommonTokenFactory] instance.
  ///
  /// <p>
  /// This token factory does not explicitly copy token text when constructing
  /// tokens.</p>
  static final TokenFactory<CommonToken> DEFAULT = CommonTokenFactory();

  /// Indicates whether {@link CommonToken#setText} should be called after
  /// constructing tokens to explicitly set the text. This is useful for cases
  /// where the input stream might not be able to provide arbitrary substrings
  /// of text from the input after the lexer creates a token (e.g. the
  /// implementation of {@link CharStream#getText} in
  /// [UnbufferedCharStream] throws an
  /// [UnsupportedOperationException]). Explicitly setting the token text
  /// allows {@link Token#getText} to be called at any time regardless of the
  /// input stream implementation.
  ///
  /// <p>
  /// The default value is [false] to avoid the performance and memory
  /// overhead of copying text for every token unless explicitly requested.</p>
  final bool copyText;

  /// Constructs a [CommonTokenFactory] with the specified value for
  /// {@link #copyText}.
  ///
  /// <p>
  /// When [copyText] is [false], the {@link #DEFAULT} instance
  /// should be used instead of constructing a new instance.</p>
  ///
  /// @param copyText The value for {@link #copyText}.
  CommonTokenFactory([this.copyText = false]);

  @override
  CommonToken create(
    int type,
    String? text,
    Pair<TokenSource?, CharStream?>? source,
    int channel,
    int start,
    int stop,
    int? line,
    int charPositionInLine,
  ) {
    if (source == null) {
      return CommonToken(type, text: text);
    }

    final t = CommonToken(
      type,
      source: source,
      channel: channel,
      startIndex: start,
      stopIndex: stop,
    );
    t.line = line;
    t.charPositionInLine = charPositionInLine;
    if (text != null) {
      t.text = text;
    } else if (copyText && source.b != null) {
      t.text = source.b!.getText(Interval.of(start, stop));
    }

    return t;
  }
}
