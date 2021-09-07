/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import 'package:collection/collection.dart';

import 'pair.dart';

class MultiMap<K, V> extends DelegatingMap<K, List<V>> {
  MultiMap() : super({});

  void put(K key, V value) {
    var elementsForKey = this[key];
    if (elementsForKey == null) {
      elementsForKey = [];
      this[key] = elementsForKey;
    }
    elementsForKey.add(value);
  }

  List<Pair<K, V>> get pairs {
    final pairs = <Pair<K, V>>[];
    for (var key in keys) {
      for (var value in this[key]!) {
        pairs.add(Pair<K, V>(key, value));
      }
    }
    return pairs;
  }
}
