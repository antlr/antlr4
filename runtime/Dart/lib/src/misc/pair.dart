/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

class Pair<A, B> {
  final A a;
  final B b;

  const Pair(this.a, this.b);

  @override
  bool operator ==(other) {
    return other is Pair<A, B> && a == other.a && b == other.b;
  }

  String toString() {
    return "($a, $b)";
  }

  @override
  int get hashCode {
    return a.hashCode ^ b.hashCode;
  }
}
