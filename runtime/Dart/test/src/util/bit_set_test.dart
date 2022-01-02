// Import the test package and Counter class
import 'package:test/test.dart';
import 'package:antlr4/src/util/bit_set.dart';

void main() {
  void _testBitSetBasicSet(int value) {
    test('Gets $value after setting position', () {
      var bs = BitSet();
      bs.set(value);
      expect(bs.toString(), '{$value}');
    });
  }
  _testBitSetBasicSet(15);
  _testBitSetBasicSet(31);
  _testBitSetBasicSet(32);
  _testBitSetBasicSet(57);
  _testBitSetBasicSet(63);
  _testBitSetBasicSet(64);
  _testBitSetBasicSet(128);
  _testBitSetBasicSet(256);
}
