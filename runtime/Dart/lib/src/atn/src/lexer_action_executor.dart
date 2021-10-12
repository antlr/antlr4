/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import 'package:collection/collection.dart';

import '../../input_stream.dart';
import '../../lexer.dart';
import '../../util/murmur_hash.dart';
import 'lexer_action.dart';

/// Represents an executor for a sequence of lexer actions which traversed during
/// the matching operation of a lexer rule (token).
///
/// <p>The executor tracks position information for position-dependent lexer actions
/// efficiently, ensuring that actions appearing only at the end of the rule do
/// not cause bloating of the [DFA] created for the lexer.</p>
///
/// @since 4.2
class LexerActionExecutor {
  /// Gets the lexer actions to be executed by this executor.
  /// @return The lexer actions to be executed by this executor.
  final List<LexerAction> lexerActions;

  /// Caches the result of {@link #hashCode} since the hash code is an element
  /// of the performance-critical {@link LexerATNConfig#hashCode} operation.
  @override
  int get hashCode {
    var hash = MurmurHash.initialize();
    for (var lexerAction in lexerActions) {
      hash = MurmurHash.update(hash, lexerAction);
    }

    return MurmurHash.finish(hash, lexerActions.length);
  }

  /// Constructs an executor for a sequence of [LexerAction] actions.
  /// @param lexerActions The lexer actions to execute.
  LexerActionExecutor(this.lexerActions);

  /// Creates a [LexerActionExecutor] which executes the actions for
  /// the input [lexerActionExecutor] followed by a specified
  /// [lexerAction].
  ///
  /// @param lexerActionExecutor The executor for actions already traversed by
  /// the lexer while matching a token within a particular
  /// [LexerATNConfig]. If this is null, the method behaves as
  /// though it were an empty executor.
  /// @param lexerAction The lexer action to execute after the actions
  /// specified in [lexerActionExecutor].
  ///
  /// @return A [LexerActionExecutor] for executing the combine actions
  /// of [lexerActionExecutor] and [lexerAction].
  static LexerActionExecutor append(
    LexerActionExecutor? lexerActionExecutor,
    LexerAction lexerAction,
  ) {
    if (lexerActionExecutor == null) {
      return LexerActionExecutor([lexerAction]);
    }

    final lexerActions =
        List<LexerAction>.from(lexerActionExecutor.lexerActions);
    lexerActions.add(lexerAction);
    return LexerActionExecutor(lexerActions);
  }

  /// Creates a [LexerActionExecutor] which encodes the current offset
  /// for position-dependent lexer actions.
  ///
  /// <p>Normally, when the executor encounters lexer actions where
  /// {@link LexerAction#isPositionDependent} returns [true], it calls
  /// {@link IntStream#seek} on the input [CharStream] to set the input
  /// position to the <em>end</em> of the current token. This behavior provides
  /// for efficient DFA representation of lexer actions which appear at the end
  /// of a lexer rule, even when the lexer rule matches a variable number of
  /// characters.</p>
  ///
  /// <p>Prior to traversing a match transition in the ATN, the current offset
  /// from the token start index is assigned to all position-dependent lexer
  /// actions which have not already been assigned a fixed offset. By storing
  /// the offsets relative to the token start index, the DFA representation of
  /// lexer actions which appear in the middle of tokens remains efficient due
  /// to sharing among tokens of the same length, regardless of their absolute
  /// position in the input stream.</p>
  ///
  /// <p>If the current executor already has offsets assigned to all
  /// position-dependent lexer actions, the method returns [this].</p>
  ///
  /// @param offset The current offset to assign to all position-dependent
  /// lexer actions which do not already have offsets assigned.
  ///
  /// @return A [LexerActionExecutor] which stores input stream offsets
  /// for all position-dependent lexer actions.
  LexerActionExecutor fixOffsetBeforeMatch(int offset) {
    List<LexerAction>? updatedLexerActions;
    for (var i = 0; i < lexerActions.length; i++) {
      if (lexerActions[i].isPositionDependent &&
          lexerActions[i] is! LexerIndexedCustomAction) {
        updatedLexerActions ??= List.from(lexerActions);

        updatedLexerActions[i] =
            LexerIndexedCustomAction(offset, lexerActions[i]);
      }
    }

    if (updatedLexerActions == null) {
      return this;
    }

    return LexerActionExecutor(updatedLexerActions);
  }

  /// Execute the actions encapsulated by this executor within the context of a
  /// particular [Lexer].
  ///
  /// <p>This method calls {@link IntStream#seek} to set the position of the
  /// [input] [CharStream] prior to calling
  /// {@link LexerAction#execute} on a position-dependent action. Before the
  /// method returns, the input position will be restored to the same position
  /// it was in when the method was invoked.</p>
  ///
  /// @param lexer The lexer instance.
  /// @param input The input stream which is the source for the current token.
  /// When this method is called, the current {@link IntStream#index} for
  /// [input] should be the start of the following token, i.e. 1
  /// character past the end of the current token.
  /// @param startIndex The token start index. This value may be passed to
  /// {@link IntStream#seek} to set the [input] position to the beginning
  /// of the token.
  void execute(Lexer lexer, CharStream input, int startIndex) {
    var requiresSeek = false;
    final stopIndex = input.index;
    try {
      for (var lexerAction in lexerActions) {
        if (lexerAction is LexerIndexedCustomAction) {
          final offset = lexerAction.offset;
          input.seek(startIndex + offset);
          lexerAction = lexerAction.action;
          requiresSeek = (startIndex + offset) != stopIndex;
        } else if (lexerAction.isPositionDependent) {
          input.seek(stopIndex);
          requiresSeek = false;
        }

        lexerAction.execute(lexer);
      }
    } finally {
      if (requiresSeek) {
        input.seek(stopIndex);
      }
    }
  }

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) {
      return true;
    } else if (other is! LexerActionExecutor) {
      return false;
    }

    return hashCode == other.hashCode &&
        ListEquality().equals(
          lexerActions,
          other.lexerActions,
        );
  }
}
