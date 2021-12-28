import 'dart:typed_data';

int getBitCount(Uint32List value) {
  var data = 0;
  final size = value.length;
  const m1 = 0x5555555555555555;
  const m2 = 0x3333333333333333;
  const m4 = 0x0F0F0F0F0F0F0F0F;
  const m8 = 0x00FF00FF00FF00FF;
  const m16 = 0x0000FFFF0000FFFF;
  const h01 = 0x0101010101010101;

  var bitCount = 0;
  final limit30 = size - size % 30;

  // 64-bit tree merging (merging3)
  for (var i = 0; i < limit30; i += 30, data += 30) {
    var acc = 0;
    for (var j = 0; j < 30; j += 3) {
      var count1 = value[data + j];
      var count2 = value[data + j + 1];
      var half1 = value[data + j + 2];
      var half2 = half1;
      half1 &= m1;
      half2 = (half2 >> 1) & m1;
      count1 -= (count1 >> 1) & m1;
      count2 -= (count2 >> 1) & m1;
      count1 += half1;
      count2 += half2;
      count1 = (count1 & m2) + ((count1 >> 2) & m2);
      count1 += (count2 & m2) + ((count2 >> 2) & m2);
      acc += (count1 & m4) + ((count1 >> 4) & m4);
    }

    acc = (acc & m8) + ((acc >> 8) & m8);
    acc = (acc + (acc >> 16)) & m16;
    acc = acc + (acc >> 32);
    bitCount += acc;
  }

  // count the bits of the remaining bytes (MAX 29*8) using
  // "Counting bits set, in parallel" from the "Bit Twiddling Hacks",
  // the code uses wikipedia's 64-bit popcount_3() implementation:
  // http://en.wikipedia.org/wiki/Hamming_weight#Efficient_implementation
  for (var i = 0; i < size - limit30; i++) {
    var x = value[data + i];
    x = x - ((x >> 1) & m1);
    x = (x & m2) + ((x >> 2) & m2);
    x = (x + (x >> 4)) & m4;
    bitCount += ((x * h01) >> 56);
  }

  return bitCount;
}

final List<int> index64 = [
  0,
  47,
  1,
  56,
  48,
  27,
  2,
  60,
  57,
  49,
  41,
  37,
  28,
  16,
  3,
  61,
  54,
  58,
  35,
  52,
  50,
  42,
  21,
  44,
  38,
  32,
  29,
  23,
  17,
  11,
  4,
  62,
  46,
  55,
  26,
  59,
  40,
  36,
  15,
  53,
  34,
  51,
  20,
  43,
  31,
  22,
  10,
  45,
  25,
  39,
  14,
  33,
  19,
  30,
  9,
  24,
  13,
  18,
  8,
  12,
  7,
  6,
  5,
  63
];

int BitScanForward(int value) {
  if (value == 0) return -1;

  const debruijn64 = 0x03f79d71b4cb0a89;
  return index64[(((value ^ (value - 1)) * debruijn64) >> 58) % 64];
}
