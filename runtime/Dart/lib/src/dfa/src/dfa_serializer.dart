/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import '../../vocabulary.dart';
import '../../util/utils.dart';
import 'dfa.dart';
import 'dfa_state.dart';

/** A DFA walker that knows how to dump them to serialized strings. */
class DFASerializer {
  final DFA dfa;

  final Vocabulary vocabulary;

  DFASerializer(this.dfa, this.vocabulary);

  String toString() {
    if (dfa.s0 == null) return null;
    StringBuffer buf = new StringBuffer();
    List<DFAState> states = dfa.getStates();
    for (DFAState s in states) {
      int n = 0;
      if (s.edges != null) n = s.edges.length;
      for (int i = 0; i < n; i++) {
        DFAState t = s.edges[i];
        if (t != null && t.stateNumber != 0x7FFFFFFF) {
          buf.write(getStateString(s));
          String label = getEdgeLabel(i);
          buf.write("-");
          buf.write(label);
          buf.write("->");
          buf.write(getStateString(t));
          buf.write('\n');
        }
      }
    }

    String output = buf.toString();
    if (output.length == 0) return null;
    //return Utils.sortLinesInString(output);
    return output;
  }

  String getEdgeLabel(int i) {
    return vocabulary.getDisplayName(i - 1);
  }

  String getStateString(DFAState s) {
    int n = s.stateNumber;
    final String baseStateStr = (s.isAcceptState ? ":" : "") +
        "s$n" +
        (s.requiresFullContext ? "^" : "");
    if (s.isAcceptState) {
      if (s.predicates != null) {
        return baseStateStr + "=>${arrayToString(s.predicates)}";
      } else {
        return baseStateStr + "=>${s.prediction}";
      }
    } else {
      return baseStateStr;
    }
  }
}

class LexerDFASerializer extends DFASerializer {
  LexerDFASerializer(dfa) : super(dfa, VocabularyImpl.EMPTY_VOCABULARY);

  String getEdgeLabel(i) {
    return "'" + String.fromCharCode(i) + "'";
  }
}
