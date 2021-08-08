import 'dart:io';

import 'error_listener.dart';

class ConsoleErrorListener extends BaseErrorListener {
  /// Provides a default instance of [ConsoleErrorListener].
  static final INSTANCE = ConsoleErrorListener();

  /// {@inheritDoc}
  ///
  /// <p>
  /// This implementation prints messages to {@link System//err} containing the
  /// values of [line], [charPositionInLine], and [msg] using
  /// the following format.</p>
  ///
  /// <pre>
  /// line <em>line</em>:<em>charPositionInLine</em> <em>msg</em>
  /// </pre>
  @override
  void syntaxError(recognizer, offendingSymbol, line, column, msg, e) =>
      stderr.writeln('line $line:$column $msg');
}
