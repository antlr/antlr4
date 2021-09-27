/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import '../util/murmur_hash.dart';

class Pair<A, B> {
  final A a;
  final B b;

  const Pair(this.a, this.b);

  @override
  bool operator ==(other) {
    return identical(this, other) ||
        other is Pair && a == other.a && b == other.b;
  }

  @override
  String toString() {
    return '($a, $b)';
  }

  @override
  int get hashCode {
    MurmurHash.initialize();

    var hash = MurmurHash.initialize();
    hash = MurmurHash.update(hash, a);
    hash = MurmurHash.update(hash, b);
    return MurmurHash.finish(hash, 2);
  }
}
