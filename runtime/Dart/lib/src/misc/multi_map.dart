/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import 'dart:collection';

import 'package:collection/collection.dart';

import 'pair.dart';

class MultiMap<K, V> extends DelegatingMap<K, List<V>> {
  MultiMap() : super(LinkedHashMap());

  void put(K key, V value) {
    List<V> elementsForKey = this[key];
    if (elementsForKey == null) {
      elementsForKey = [];
      this[key] = elementsForKey;
    }
    elementsForKey.add(value);
  }

  List<Pair<K, V>> get pairs {
    List<Pair<K, V>> pairs = [];
    for (K key in keys) {
      for (V value in this[key]) {
        pairs.add(new Pair<K, V>(key, value));
      }
    }
    return pairs;
  }
}
