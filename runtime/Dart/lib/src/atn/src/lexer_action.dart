/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import '../../lexer.dart';
import '../../util/murmur_hash.dart';

/// Represents the serialization type of a [LexerAction].
///
/// @since 4.2
enum LexerActionType {
  /// The type of a [LexerChannelAction] action.
  CHANNEL,

  /// The type of a [LexerCustomAction] action.
  CUSTOM,

  /// The type of a [LexerModeAction] action.
  MODE,

  /// The type of a [LexerMoreAction] action.
  MORE,

  /// The type of a [LexerPopModeAction] action.
  POP_MODE,

  /// The type of a [LexerPushModeAction] action.
  PUSH_MODE,

  /// The type of a [LexerSkipAction] action.
  SKIP,

  /// The type of a [LexerTypeAction] action.
  TYPE,
}

/// Represents a single action which can be executed following the successful
/// match of a lexer rule. Lexer actions are used for both embedded action syntax
/// and ANTLR 4's new lexer command syntax.
///
/// @since 4.2
abstract class LexerAction {
  /// Gets the serialization type of the lexer action.
  ///
  /// @return The serialization type of the lexer action.
  LexerActionType get actionType;

  /// Gets whether the lexer action is position-dependent. Position-dependent
  /// actions may have different semantics depending on the [CharStream]
  /// index at the time the action is executed.
  ///
  /// <p>Many lexer commands, including [type], [skip], and
  /// [more], do not check the input index during their execution.
  /// Actions like this are position-independent, and may be stored more
  /// efficiently as part of the {@link LexerATNConfig#lexerActionExecutor}.</p>
  ///
  /// @return [true] if the lexer action semantics can be affected by the
  /// position of the input [CharStream] at the time it is executed;
  /// otherwise, [false].
  bool get isPositionDependent;

  /// Execute the lexer action in the context of the specified [Lexer].
  ///
  /// <p>For position-dependent actions, the input stream must already be
  /// positioned correctly prior to calling this method.</p>
  ///
  /// @param lexer The lexer instance.
  void execute(Lexer lexer);
}

/// Implements the [channel] lexer action by calling
/// {@link Lexer#setChannel} with the assigned channel.
///
/// @since 4.2
class LexerChannelAction implements LexerAction {
  /// Gets the channel to use for the [Token] created by the lexer.
  ///
  /// @return The channel to use for the [Token] created by the lexer.
  final int channel;

  /// Constructs a new [channel] action with the specified channel value.
  /// @param channel The channel value to pass to {@link Lexer#setChannel}.
  LexerChannelAction(this.channel);

  @override
  LexerActionType get actionType => LexerActionType.CHANNEL;

  @override
  bool get isPositionDependent => false;

  /// {@inheritDoc}
  ///
  /// <p>This action is implemented by calling {@link Lexer#setChannel} with the
  /// value provided by {@link #getChannel}.</p>
  @override
  void execute(Lexer lexer) {
    lexer.channel = channel;
  }

  @override
  int get hashCode {
    var hash = MurmurHash.initialize();
    hash = MurmurHash.update(hash, actionType.index);
    hash = MurmurHash.update(hash, channel);
    return MurmurHash.finish(hash, 2);
  }

  @override
  bool operator ==(Object obj) {
    if (identical(obj, this)) {
      return true;
    } else if (obj is LexerChannelAction) {
      return channel == obj.channel;
    }

    return false;
  }

  @override
  String toString() {
    return 'channel($channel)';
  }
}

/// Executes a custom lexer action by calling {@link Recognizer#action} with the
/// rule and action indexes assigned to the custom action. The implementation of
/// a custom action is added to the generated code for the lexer in an override
/// of {@link Recognizer#action} when the grammar is compiled.
///
/// <p>This class may represent embedded actions created with the <code>{...}</code>
/// syntax in ANTLR 4, as well as actions created for lexer commands where the
/// command argument could not be evaluated when the grammar was compiled.</p>
///
/// @since 4.2
class LexerCustomAction implements LexerAction {
  /// Gets the rule index to use for calls to {@link Recognizer#action}.
  ///
  /// @return The rule index for the custom action.
  final int ruleIndex;

  /// Gets the action index to use for calls to {@link Recognizer#action}.
  ///
  /// @return The action index for the custom action.
  final int actionIndex;

  /// Constructs a custom lexer action with the specified rule and action
  /// indexes.
  ///
  /// @param ruleIndex The rule index to use for calls to
  /// {@link Recognizer#action}.
  /// @param actionIndex The action index to use for calls to
  /// {@link Recognizer#action}.
  LexerCustomAction(this.ruleIndex, this.actionIndex);

  /// {@inheritDoc}
  ///
  /// @return This method returns {@link LexerActionType#CUSTOM}.

  @override
  LexerActionType get actionType => LexerActionType.CUSTOM;

  /// Gets whether the lexer action is position-dependent. Position-dependent
  /// actions may have different semantics depending on the [CharStream]
  /// index at the time the action is executed.
  ///
  /// <p>Custom actions are position-dependent since they may represent a
  /// user-defined embedded action which makes calls to methods like
  /// {@link Lexer#getText}.</p>
  ///
  /// @return This method returns [true].

  @override
  bool get isPositionDependent => true;

  /// {@inheritDoc}
  ///
  /// <p>Custom actions are implemented by calling {@link Lexer#action} with the
  /// appropriate rule and action indexes.</p>

  @override
  void execute(Lexer lexer) {
    lexer.action(null, ruleIndex, actionIndex);
  }

  @override
  int get hashCode {
    var hash = MurmurHash.initialize();
    hash = MurmurHash.update(hash, actionType.index);
    hash = MurmurHash.update(hash, ruleIndex);
    hash = MurmurHash.update(hash, actionIndex);
    return MurmurHash.finish(hash, 3);
  }

  @override
  bool operator ==(Object obj) {
    if (identical(obj, this)) {
      return true;
    } else if (obj is LexerCustomAction) {
      return ruleIndex == obj.ruleIndex && actionIndex == obj.actionIndex;
    }
    return false;
  }
}

/// Implements the [mode] lexer action by calling {@link Lexer#mode} with
/// the assigned mode.
///
/// @since 4.2
class LexerModeAction implements LexerAction {
  /// Get the lexer mode this action should transition the lexer to.
  ///
  /// @return The lexer mode for this [mode] command.
  final int mode;

  /// Constructs a new [mode] action with the specified mode value.
  /// @param mode The mode value to pass to {@link Lexer#mode}.
  LexerModeAction(this.mode);

  /// {@inheritDoc}
  /// @return This method returns {@link LexerActionType#MODE}.

  @override
  LexerActionType get actionType => LexerActionType.MODE;

  /// {@inheritDoc}
  /// @return This method returns [false].

  @override
  bool get isPositionDependent => false;

  /// {@inheritDoc}
  ///
  /// <p>This action is implemented by calling {@link Lexer#mode} with the
  /// value provided by {@link #getMode}.</p>

  @override
  void execute(Lexer lexer) {
    lexer.mode(mode);
  }

  @override
  int get hashCode {
    var hash = MurmurHash.initialize();
    hash = MurmurHash.update(hash, actionType.index);
    hash = MurmurHash.update(hash, mode);
    return MurmurHash.finish(hash, 2);
  }

  @override
  bool operator ==(Object obj) {
    if (identical(obj, this)) {
      return true;
    } else if (obj is LexerModeAction) {
      return mode == obj.mode;
    }
    return false;
  }

  @override
  String toString() {
    return 'mode($mode)';
  }
}

/// Implements the [more] lexer action by calling {@link Lexer#more}.
///
/// <p>The [more] command does not have any parameters, so this action is
/// implemented as a singleton instance exposed by {@link #INSTANCE}.</p>
///
/// @since 4.2
class LexerMoreAction implements LexerAction {
  /// Provides a singleton instance of this parameterless lexer action.
  static final LexerMoreAction INSTANCE = LexerMoreAction();

  /// {@inheritDoc}
  /// @return This method returns {@link LexerActionType#MORE}.
  @override
  LexerActionType get actionType => LexerActionType.MORE;

  /// {@inheritDoc}
  /// @return This method returns [false].

  @override
  bool get isPositionDependent => false;

  /// {@inheritDoc}
  ///
  /// <p>This action is implemented by calling {@link Lexer#more}.</p>

  @override
  void execute(Lexer lexer) {
    lexer.more();
  }

  @override
  int get hashCode {
    var hash = MurmurHash.initialize();
    hash = MurmurHash.update(hash, actionType.index);
    return MurmurHash.finish(hash, 1);
  }

  @override
  bool operator ==(Object obj) {
    return identical(obj, this);
  }

  @override
  String toString() {
    return 'more';
  }
}

/// Implements the [popMode] lexer action by calling {@link Lexer#popMode}.
///
/// <p>The [popMode] command does not have any parameters, so this action is
/// implemented as a singleton instance exposed by {@link #INSTANCE}.</p>
///
/// @since 4.2
class LexerPopModeAction implements LexerAction {
  /// Provides a singleton instance of this parameterless lexer action.
  static final LexerPopModeAction INSTANCE = LexerPopModeAction();

  /// {@inheritDoc}
  /// @return This method returns {@link LexerActionType#POP_MODE}.

  @override
  LexerActionType get actionType => LexerActionType.POP_MODE;

  /// {@inheritDoc}
  /// @return This method returns [false].

  @override
  bool get isPositionDependent => false;

  /// {@inheritDoc}
  ///
  /// <p>This action is implemented by calling {@link Lexer#popMode}.</p>

  @override
  void execute(Lexer lexer) {
    lexer.popMode();
  }

  @override
  int get hashCode {
    var hash = MurmurHash.initialize();
    hash = MurmurHash.update(hash, actionType.index);
    return MurmurHash.finish(hash, 1);
  }

  @override
  bool operator ==(Object obj) {
    return identical(obj, this);
  }

  @override
  String toString() {
    return 'popMode';
  }
}

/// Implements the [pushMode] lexer action by calling
/// {@link Lexer#pushMode} with the assigned mode.
///
/// @since 4.2
class LexerPushModeAction implements LexerAction {
  /// Get the lexer mode this action should transition the lexer to.
  ///
  /// @return The lexer mode for this [pushMode] command.
  final int mode;

  /// Constructs a new [pushMode] action with the specified mode value.
  /// @param mode The mode value to pass to {@link Lexer#pushMode}.
  LexerPushModeAction(this.mode);

  /// {@inheritDoc}
  /// @return This method returns {@link LexerActionType#PUSH_MODE}.

  @override
  LexerActionType get actionType => LexerActionType.PUSH_MODE;

  /// {@inheritDoc}
  /// @return This method returns [false].

  @override
  bool get isPositionDependent => false;

  /// {@inheritDoc}
  ///
  /// <p>This action is implemented by calling {@link Lexer#pushMode} with the
  /// value provided by {@link #getMode}.</p>

  @override
  void execute(Lexer lexer) {
    lexer.pushMode(mode);
  }

  @override
  int get hashCode {
    var hash = MurmurHash.initialize();
    hash = MurmurHash.update(hash, actionType.index);
    hash = MurmurHash.update(hash, mode);
    return MurmurHash.finish(hash, 2);
  }

  @override
  bool operator ==(Object obj) {
    if (identical(obj, this)) {
      return true;
    } else if (obj is LexerPushModeAction) {
      return mode == obj.mode;
    }
    return false;
  }

  @override
  String toString() {
    return 'pushMode($mode)';
  }
}

/// Implements the [skip] lexer action by calling {@link Lexer#skip}.
///
/// <p>The [skip] command does not have any parameters, so this action is
/// implemented as a singleton instance exposed by {@link #INSTANCE}.</p>
///
/// @since 4.2
class LexerSkipAction implements LexerAction {
  /// Provides a singleton instance of this parameterless lexer action.
  static final LexerSkipAction INSTANCE = LexerSkipAction();

  /// {@inheritDoc}
  /// @return This method returns {@link LexerActionType#SKIP}.

  @override
  LexerActionType get actionType => LexerActionType.SKIP;

  /// {@inheritDoc}
  /// @return This method returns [false].

  @override
  bool get isPositionDependent => false;

  /// {@inheritDoc}
  ///
  /// <p>This action is implemented by calling {@link Lexer#skip}.</p>
  @override
  void execute(Lexer lexer) {
    lexer.skip();
  }

  @override
  int get hashCode {
    var hash = MurmurHash.initialize();
    hash = MurmurHash.update(hash, actionType.index);
    return MurmurHash.finish(hash, 1);
  }

  @override
  bool operator ==(Object obj) {
    return identical(obj, this);
  }

  @override
  String toString() {
    return 'skip';
  }
}

/// Implements the [type] lexer action by calling {@link Lexer#setType}
/// with the assigned type.
///
/// @since 4.2
class LexerTypeAction implements LexerAction {
  /// Gets the type to assign to a token created by the lexer.
  /// @return The type to assign to a token created by the lexer.
  final int type;

  /// Constructs a new [type] action with the specified token type value.
  /// @param type The type to assign to the token using {@link Lexer#setType}.
  LexerTypeAction(this.type);

  /// {@inheritDoc}
  /// @return This method returns {@link LexerActionType#TYPE}.
  @override
  LexerActionType get actionType => LexerActionType.TYPE;

  /// {@inheritDoc}
  /// @return This method returns [false].

  @override
  bool get isPositionDependent => false;

  /// {@inheritDoc}
  ///
  /// <p>This action is implemented by calling {@link Lexer#setType} with the
  /// value provided by {@link #getType}.</p>

  @override
  void execute(Lexer lexer) {
    lexer.type = type;
  }

  @override
  int get hashCode {
    var hash = MurmurHash.initialize();
    hash = MurmurHash.update(hash, actionType.index);
    hash = MurmurHash.update(hash, type);
    return MurmurHash.finish(hash, 2);
  }

  @override
  bool operator ==(Object obj) {
    if (identical(obj, this)) {
      return true;
    } else if (obj is LexerTypeAction) {
      return type == obj.type;
    }
    return false;
  }

  @override
  String toString() {
    return 'type($type)';
  }
}

/// This implementation of [LexerAction] is used for tracking input offsets
/// for position-dependent actions within a [LexerActionExecutor].
///
/// <p>This action is not serialized as part of the ATN, and is only required for
/// position-dependent lexer actions which appear at a location other than the
/// end of a rule. For more information about DFA optimizations employed for
/// lexer actions, see {@link LexerActionExecutor#append} and
/// {@link LexerActionExecutor#fixOffsetBeforeMatch}.</p>
///
/// @since 4.2
class LexerIndexedCustomAction implements LexerAction {
  /// Gets the location in the input [CharStream] at which the lexer
  /// action should be executed. The value is interpreted as an offset relative
  /// to the token start index.
  ///
  /// @return The location in the input [CharStream] at which the lexer
  /// action should be executed.
  final int offset;

  /// Gets the lexer action to execute.
  ///
  /// @return A [LexerAction] object which executes the lexer action.
  final LexerAction action;

  /// Constructs a new indexed custom action by associating a character offset
  /// with a [LexerAction].
  ///
  /// <p>Note: This class is only required for lexer actions for which
  /// {@link LexerAction#isPositionDependent} returns [true].</p>
  ///
  /// @param offset The offset into the input [CharStream], relative to
  /// the token start index, at which the specified lexer action should be
  /// executed.
  /// @param action The lexer action to execute at a particular offset in the
  /// input [CharStream].
  LexerIndexedCustomAction(this.offset, this.action);

  /// {@inheritDoc}
  ///
  /// @return This method returns the result of calling {@link #getActionType}
  /// on the [LexerAction] returned by {@link #getAction}.
  @override
  LexerActionType get actionType => action.actionType;

  /// {@inheritDoc}
  /// @return This method returns [true].

  @override
  bool get isPositionDependent => true;

  /// {@inheritDoc}
  ///
  /// <p>This method calls {@link #execute} on the result of {@link #getAction}
  /// using the provided [lexer].</p>

  @override
  void execute(Lexer lexer) {
// assume the input stream position was properly set by the calling code
    action.execute(lexer);
  }

  @override
  int get hashCode {
    var hash = MurmurHash.initialize();
    hash = MurmurHash.update(hash, offset);
    hash = MurmurHash.update(hash, action);
    return MurmurHash.finish(hash, 2);
  }

  @override
  bool operator ==(Object obj) {
    if (obj == this) {
      return true;
    } else if (obj is LexerIndexedCustomAction) {
      return offset == obj.offset && action == obj.action;
    }
    return false;
  }
}
