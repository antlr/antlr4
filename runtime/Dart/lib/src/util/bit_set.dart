/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import 'dart:math';
import 'dart:typed_data';

import 'bit_operation_util.dart'
    if (dart.library.html) 'bit_operation_util_html.dart' as util;

class BitSet {
  static final Uint32List EmptyBits = Uint32List(0);
  static const BitsPerElement = 32;

  Uint32List _data = EmptyBits;

  BitSet([int nbits = 0]) {
    if (nbits == 0) {
      return;
    }
    if (nbits < 0) throw RangeError('nbits');

    if (nbits > 0) {
      final length = ((nbits + BitsPerElement - 1) / BitsPerElement).floor();
      _data = Uint32List(length);
    }
  }

  BitSet clone() {
    final result = BitSet();
    result._data = Uint32List.fromList(_data);
    return result;
  }

  void clear(int index) {
    if (index < 0) throw RangeError('index');

    final element = (index / BitsPerElement).floor();
    if (element >= _data.length) return;

    _data[element] &= ~(1 << (index % BitsPerElement));
  }

  bool operator [](int index) {
    return get(index);
  }

  bool get(int index) {
    if (index < 0) throw RangeError.value(index, 'index');

    final element = (index / BitsPerElement).floor();
    if (element >= _data.length) return false;

    return (_data[element] & (1 << (index % BitsPerElement))) != 0;
  }

  void set(int index) {
    if (index < 0) throw RangeError.value(index, 'index');

    final element = (index / BitsPerElement).floor();
    if (element >= _data.length) {
      final newList = Uint32List(max(_data.length * 2, element + 1))
        ..setRange(0, _data.length, _data);
      _data = newList;
    }
    _data[element] |= 1 << (index % BitsPerElement);
  }

  bool get isEmpty {
    for (var i = 0; i < _data.length; i++) {
      if (_data[i] != 0) return false;
    }

    return true;
  }

  int get cardinality {
    return util.getBitCount(_data);
  }

  int nextset(int fromIndex) {
    if (fromIndex < 0) throw RangeError('fromIndex');

    if (isEmpty) return -1;

    var i = (fromIndex / BitsPerElement).floor();
    if (i >= _data.length) return -1;

    var current = _data[i] & ~((1 << (fromIndex % BitsPerElement)) - 1);

    while (true) {
      final bit = util.BitScanForward(current);
      if (bit >= 0) return bit + i * BitsPerElement;

      i++;
      if (i >= _data.length) break;

      current = _data[i];
    }

    return -1;
  }

  void and(BitSet set) {
    final length = min(_data.length, set._data.length);
    for (var i = 0; i < length; i++) {
      _data[i] &= set._data[i];
    }

    for (var i = length; i < _data.length; i++) {
      _data[i] = 0;
    }
  }

  void or(BitSet set) {
    if (set._data.length > _data.length) {
      final newList = Uint32List(set._data.length)
        ..setRange(0, _data.length, _data);
      _data = newList;
    }

    for (var i = 0; i < set._data.length; i++) {
      _data[i] |= set._data[i];
    }
  }

  @override
  bool operator ==(obj) {
    final other = obj as BitSet;

    if (isEmpty) return other.isEmpty;

    final minlength = min(_data.length, other._data.length);
    for (var i = 0; i < minlength; i++) {
      if (_data[i] != other._data[i]) return false;
    }

    for (var i = minlength; i < _data.length; i++) {
      if (_data[i] != 0) return false;
    }

    for (var i = minlength; i < other._data.length; i++) {
      if (other._data[i] != 0) return false;
    }

    return true;
  }

  @override
  int get hashCode {
    var result = 1;
    for (var i = 0; i < _data.length; i++) {
      if (_data[i] != 0) {
        result = result * 31 ^ i;
        result = result * 31 ^ _data[i];
      }
    }

    return result.hashCode;
  }

  @override
  String toString() {
    final builder = StringBuffer();
    builder.write('{');

    for (var i = nextset(0); i >= 0; i = nextset(i + 1)) {
      if (builder.length > 1) builder.write(', ');

      builder.write(i);
    }

    builder.write('}');
    return builder.toString();
  }
}
