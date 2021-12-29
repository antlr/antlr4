/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import '../../util/utils.dart';
import '../../vocabulary.dart';
import 'dfa.dart';
import 'dfa_state.dart';

/// A DFA walker that knows how to dump them to serialized strings. */
class DFASerializer {
  final DFA dfa;

  final Vocabulary vocabulary;

  DFASerializer(this.dfa, this.vocabulary);

  @override
  String toString() {
    if (dfa.s0 == null) return 'null';
    final buf = StringBuffer();
    final states = dfa.getStates();
    for (var s in states) {
      var n = 0;
      if (s.edges != null) n = s.edges!.length;
      for (var i = 0; i < n; i++) {
        final t = s.edges![i];
        if (t != null && t.stateNumber != 0x7FFFFFFF) {
          buf.write(getStateString(s));
          final label = getEdgeLabel(i);
          buf.write('-');
          buf.write(label);
          buf.write('->');
          buf.write(getStateString(t));
          buf.write('\n');
        }
      }
    }

    final output = buf.toString();
    if (output.isEmpty) return 'null';
    //return Utils.sortLinesInString(output);
    return output;
  }

  String getEdgeLabel(int i) {
    return vocabulary.getDisplayName(i - 1);
  }

  String getStateString(DFAState s) {
    final n = s.stateNumber;
    final baseStateStr = (s.isAcceptState ? ':' : '') +
        's$n' +
        (s.requiresFullContext ? '^' : '');
    if (s.isAcceptState) {
      if (s.predicates != null) {
        return baseStateStr + '=>${arrayToString(s.predicates)}';
      } else {
        return baseStateStr + '=>${s.prediction}';
      }
    } else {
      return baseStateStr;
    }
  }
}

class LexerDFASerializer extends DFASerializer {
  LexerDFASerializer(dfa) : super(dfa, VocabularyImpl.EMPTY_VOCABULARY);

  @override
  String getEdgeLabel(i) {
    return "'" + String.fromCharCode(i) + "'";
  }
}
