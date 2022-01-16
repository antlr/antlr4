package antlr

import "testing"

func testBitSet(t *testing.T, bs *BitSet, str string, length int, contains []int, minValue int, minLen int) {
	t.Helper()
	if got, want := bs.String(), str; got != want {
		t.Errorf("%+v.String() = %q, want %q", bs, got, want)
	}
	if got, want := bs.length(), length; got != want {
		t.Errorf("%+v.length() = %q, want %q", bs, got, want)
	}
	for i := 0; i < len(bs.data)*bitsPerWord; i++ {
		var want bool
		for _, val := range contains {
			if i == val {
				want = true
				break
			}
		}
		if got := bs.contains(i); got != want {
			t.Errorf("%+v.contains(%v) = %v, want %v", bs, i, got, want)
		}
	}
	if got, want := bs.minValue(), minValue; got != want {
		t.Errorf("%+v.minValue() = %v, want %v", bs, got, want)
	}
	if got, want := bs.minLen(), minLen; got != want {
		t.Errorf("%+v.minLen() = %v, want %v", bs, got, want)
	}
}

func TestBitSet(t *testing.T) {
	bs1 := NewBitSet()
	testBitSet(t, bs1, "{}", 0, []int{}, 2147483647, 0)
	bs1.add(0)
	testBitSet(t, bs1, "{0}", 1, []int{0}, 0, 1)
	bs1.add(63)
	testBitSet(t, bs1, "{0, 63}", 2, []int{0, 63}, 0, 1)
	bs1.remove(0)
	testBitSet(t, bs1, "{63}", 1, []int{63}, 63, 1)
	bs1.add(20)
	testBitSet(t, bs1, "{20, 63}", 2, []int{20, 63}, 20, 1)
	bs1.clear(63)
	testBitSet(t, bs1, "{20}", 1, []int{20}, 20, 1)
	bs2 := NewBitSet()
	bs2.add(64)
	bs1.or(bs2)
	testBitSet(t, bs1, "{20, 64}", 2, []int{20, 64}, 20, 2)
	bs1.remove(20)
	testBitSet(t, bs1, "{64}", 1, []int{64}, 64, 2)
	bs3 := NewBitSet()
	bs3.add(63)
	bs1.or(bs3)
	testBitSet(t, bs1, "{63, 64}", 2, []int{63, 64}, 63, 2)
	bs1.clear(64)
	bs4 := NewBitSet()
	bs4.or(bs1)
	if got, want := bs4.equals(bs1), true; got != want {
		t.Errorf("%+v.equals(%+v) = %v, want %v", bs4, bs1, got, want)
	}
}
