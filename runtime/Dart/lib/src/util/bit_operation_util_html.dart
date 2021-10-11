import 'dart:typed_data';

/// Dart2JS does not support int64; use the alternatives.
/// http://graphics.stanford.edu/~seander/bithacks.html#CountBitsSetParallel

int getBitCount(Uint32List value) {
  var bitCount = 0;
  for (var v in value) {
    v -= ((v >> 1) & 0x55555555);
    v = (v & 0x33333333) + ((v >> 2) & 0x33333333);
    bitCount += (((v + (v >> 4) & 0xf0f0f0f) * 0x1010101) >> 24);
  }
  return bitCount;
}

final List<int> index32 = [
  0,
  9,
  1,
  10,
  13,
  21,
  2,
  29,
  11,
  14,
  16,
  18,
  22,
  25,
  3,
  30,
  8,
  12,
  20,
  28,
  15,
  17,
  24,
  7,
  19,
  27,
  23,
  6,
  26,
  5,
  4,
  31
];

int BitScanForward(int value) {
  if (value == 0) return -1;

  const debruijn32 = 0x07c4acdd;
  return index32[((value ^ (value - 1)) * debruijn32) >> 27];
}
