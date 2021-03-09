/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import 'dart:math';

import 'token.dart';

/// This interface provides information about the vocabulary used by a
/// recognizer.
///
/// @see Recognizer#getVocabulary()
abstract class Vocabulary {
  /// Returns the highest token type value. It can be used to iterate from
  /// zero to that number, inclusively, thus querying all stored entries.
  /// @return the highest token type value
  int get maxTokenType;

  /// Gets the string literal associated with a token type. The string returned
  /// by this method, when not null, can be used unaltered in a parser
  /// grammar to represent this token type.
  ///
  /// <p>The following table shows examples of lexer rules and the literal
  /// names assigned to the corresponding token types.</p>
  ///
  /// <table>
  ///  <tr>
  ///   <th>Rule</th>
  ///   <th>Literal Name</th>
  ///   <th>Java String Literal</th>
  ///  </tr>
  ///  <tr>
  ///   <td>{@code THIS : 'this';}</td>
  ///   <td>{@code 'this'}</td>
  ///   <td>{@code "'this'"}</td>
  ///  </tr>
  ///  <tr>
  ///   <td>{@code SQUOTE : '\'';}</td>
  ///   <td>{@code '\''}</td>
  ///   <td>{@code "'\\''"}</td>
  ///  </tr>
  ///  <tr>
  ///   <td>{@code ID : [A-Z]+;}</td>
  ///   <td>n/a</td>
  ///   <td>null</td>
  ///  </tr>
  /// </table>
  ///
  /// @param tokenType The token type.
  ///
  /// @return The string literal associated with the specified token type, or
  /// null if no string literal is associated with the type.
  String? getLiteralName(int tokenType);

  /// Gets the symbolic name associated with a token type. The string returned
  /// by this method, when not null, can be used unaltered in a parser
  /// grammar to represent this token type.
  ///
  /// <p>This method supports token types defined by any of the following
  /// methods:</p>
  ///
  /// <ul>
  ///  <li>Tokens created by lexer rules.</li>
  ///  <li>Tokens defined in a <code>tokens{}</code> block in a lexer or parser
  ///  grammar.</li>
  ///  <li>The implicitly defined [EOF] token, which has the token type
  ///  {@link Token#EOF}.</li>
  /// </ul>
  ///
  /// <p>The following table shows examples of lexer rules and the literal
  /// names assigned to the corresponding token types.</p>
  ///
  /// <table>
  ///  <tr>
  ///   <th>Rule</th>
  ///   <th>Symbolic Name</th>
  ///  </tr>
  ///  <tr>
  ///   <td>{@code THIS : 'this';}</td>
  ///   <td>[THIS]</td>
  ///  </tr>
  ///  <tr>
  ///   <td>{@code SQUOTE : '\'';}</td>
  ///   <td>[SQUOTE]</td>
  ///  </tr>
  ///  <tr>
  ///   <td>{@code ID : [A-Z]+;}</td>
  ///   <td>[ID]</td>
  ///  </tr>
  /// </table>
  ///
  /// @param tokenType The token type.
  ///
  /// @return The symbolic name associated with the specified token type, or
  /// null if no symbolic name is associated with the type.
  String? getSymbolicName(int tokenType);

  /// Gets the display name of a token type.
  ///
  /// <p>ANTLR provides a default implementation of this method, but
  /// applications are free to override the behavior in any manner which makes
  /// sense for the application. The default implementation returns the first
  /// result from the following list which produces a non-null
  /// result.</p>
  ///
  /// <ol>
  ///  <li>The result of {@link #getLiteralName}</li>
  ///  <li>The result of {@link #getSymbolicName}</li>
  ///  <li>The result of {@link Integer#toString}</li>
  /// </ol>
  ///
  /// @param tokenType The token type.
  ///
  /// @return The display name of the token type, for use in error reporting or
  /// other user-visible messages which reference specific token types.
  String getDisplayName(int tokenType);
}

/// This class provides a default implementation of the [Vocabulary]
/// interface.
class VocabularyImpl implements Vocabulary {
  static const List<String> EMPTY_NAMES = [];

  /// Gets an empty [Vocabulary] instance.
  ///
  /// <p>
  /// No literal or symbol names are assigned to token types, so
  /// {@link #getDisplayName(int)} returns the numeric value for all tokens
  /// except {@link Token#EOF}.</p>
  static final VocabularyImpl EMPTY_VOCABULARY =
      VocabularyImpl(EMPTY_NAMES, EMPTY_NAMES, EMPTY_NAMES);

  final List<String?> literalNames;

  final List<String?> symbolicNames;

  final List<String?> displayNames;

  @override
  late int maxTokenType;

  /// Constructs a new instance of [VocabularyImpl] from the specified
  /// literal, symbolic, and display token names.
  ///
  /// @param literalNames The literal names assigned to tokens, or null
  /// if no literal names are assigned.
  /// @param symbolicNames The symbolic names assigned to tokens, or
  /// null if no symbolic names are assigned.
  /// @param displayNames The display names assigned to tokens, or null
  /// to use the values in [literalNames] and [symbolicNames] as
  /// the source of display names, as described in
  /// {@link #getDisplayName(int)}.
  ///
  /// @see #getLiteralName(int)
  /// @see #getSymbolicName(int)
  /// @see #getDisplayName(int)
  VocabularyImpl(
    this.literalNames,
    this.symbolicNames, [
    this.displayNames = EMPTY_NAMES,
  ]) {
    // See note here on -1 part: https://github.com/antlr/antlr4/pull/1146
    maxTokenType = max(displayNames.length,
            max(literalNames.length, symbolicNames.length)) -
        1;
  }

  /// Returns a [VocabularyImpl] instance from the specified set of token
  /// names. This method acts as a compatibility layer for the single
  /// [tokenNames] array generated by previous releases of ANTLR.
  ///
  /// <p>The resulting vocabulary instance returns null for
  /// {@link #getLiteralName(int)} and {@link #getSymbolicName(int)}, and the
  /// value from [tokenNames] for the display names.</p>
  ///
  /// @param tokenNames The token names, or null if no token names are
  /// available.
  /// @return A [Vocabulary] instance which uses [tokenNames] for
  /// the display names of tokens.
  static Vocabulary fromTokenNames(List<String?>? tokenNames) {
    if (tokenNames == null || tokenNames.isEmpty) {
      return EMPTY_VOCABULARY;
    }

    final literalNames = List<String?>.from(tokenNames);
    final symbolicNames = List<String?>.from(tokenNames);
    for (var i = 0; i < tokenNames.length; i++) {
      final tokenName = tokenNames[i];
      if (tokenName == null) {
        continue;
      }

      if (tokenName.isNotEmpty) {
        final firstChar = tokenName[0];
        if (firstChar == '\'') {
          symbolicNames[i] = null;
          continue;
        } else if (firstChar.toUpperCase() == firstChar) {
          literalNames[i] = null;
          continue;
        }
      }

      // wasn't a literal or symbolic name
      literalNames[i] = null;
      symbolicNames[i] = null;
    }

    return VocabularyImpl(literalNames, symbolicNames, tokenNames);
  }

  @override
  String? getLiteralName(int tokenType) {
    if (tokenType >= 0 && tokenType < literalNames.length) {
      return literalNames[tokenType];
    }

    return null;
  }

  @override
  String? getSymbolicName(int tokenType) {
    if (tokenType >= 0 && tokenType < symbolicNames.length) {
      return symbolicNames[tokenType];
    }

    if (tokenType == Token.EOF) {
      return 'EOF';
    }

    return null;
  }

  @override
  String getDisplayName(int tokenType) {
    if (tokenType >= 0 && tokenType < displayNames.length) {
      final displayName = displayNames[tokenType];
      if (displayName != null) {
        return displayName;
      }
    }

    final literalName = getLiteralName(tokenType);
    if (literalName != null) {
      return literalName;
    }

    final symbolicName = getSymbolicName(tokenType);
    if (symbolicName != null) {
      return symbolicName;
    }

    return tokenType.toString();
  }
}
