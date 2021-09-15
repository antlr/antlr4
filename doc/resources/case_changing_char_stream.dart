// @dart=2.12

import '../../runtime/Dart/lib/antlr4.dart';
import '../../runtime/Dart/lib/src/interval_set.dart';

/// This class supports case-insensitive lexing by wrapping an existing
/// {@link CharStream} and forcing the lexer to see either upper or
/// lowercase characters. Grammar literals should then be either upper or
/// lower case such as 'BEGIN' or 'begin'. The text of the character
/// stream is unaffected. Example: input 'BeGiN' would match lexer rule
/// 'BEGIN' if constructor parameter upper=true but getText() would return
/// 'BeGiN'.
class CaseChangingCharStream extends CharStream {
  final CharStream stream;
  final bool upper;

  /// Constructs a new CaseChangingCharStream wrapping the given [stream] forcing
  /// all characters to upper case or lower case depending on [upper].
  CaseChangingCharStream(this.stream, this.upper);

  @override
  int? LA(int i) {
    int? c = stream.LA(i);
    if (c == null || c <= 0) {
      return c;
    }
    String newCaseStr;
    if (upper) {
      newCaseStr = String.fromCharCode(c).toUpperCase();
    } else {
      newCaseStr = String.fromCharCode(c).toLowerCase();
    }
    // Skip changing case if length changes (e.g., ß -> SS).
    if (newCaseStr.length != 1) {
      return c;
    } else {
      return newCaseStr.codeUnitAt(0);
    }
  }

  @override
  String get sourceName => stream.sourceName;

  @override
  void consume() => stream.consume();

  @override
  String getText(Interval interval) => stream.getText(interval);

  @override
  int get index => stream.index;

  @override
  int mark() => stream.mark();

  @override
  void release(int marker) => stream.release(marker);

  @override
  void seek(int index) => stream.seek(index);

  @override
  int get size => stream.size;
}
