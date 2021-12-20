package antlr

import "testing"

func TestBitSet(t *testing.T) {
	bs1 := NewBitSet()
	if got, want := bs1.String(), "{}"; got != want {
		t.Errorf("String() = %q, want %q", got, want)
	}
	if got, want := bs1.length(), 0; got != want {
		t.Errorf("length() = %q, want %q", got, want)
	}
	if got, want := bs1.contains(1), false; got != want {
		t.Errorf("contains(%v) = %v, want %v", 1, got, want)
	}
	if got, want := bs1.minValue(), 2147483647; got != want {
		t.Errorf("minValue() = %v, want %v", got, want)
	}
	bs1.add(0)
	if got, want := bs1.String(), "{0}"; got != want {
		t.Errorf("String() = %q, want %q", got, want)
	}
	if got, want := bs1.length(), 1; got != want {
		t.Errorf("length() = %q, want %q", got, want)
	}
	if got, want := bs1.contains(0), true; got != want {
		t.Errorf("contains(%v) = %v, want %v", 1, got, want)
	}
	if got, want := bs1.minValue(), 0; got != want {
		t.Errorf("minValue() = %v, want %v", got, want)
	}
	bs1.add(63)
	if got, want := bs1.String(), "{0, 63}"; got != want {
		t.Errorf("String() = %q, want %q", got, want)
	}
	if got, want := bs1.length(), 2; got != want {
		t.Errorf("length() = %q, want %q", got, want)
	}
	if got, want := bs1.contains(1), false; got != want {
		t.Errorf("contains(%v) = %v, want %v", 1, got, want)
	}
	if got, want := bs1.contains(0), true; got != want {
		t.Errorf("contains(%v) = %v, want %v", 1, got, want)
	}
	if got, want := bs1.contains(63), true; got != want {
		t.Errorf("contains(%v) = %v, want %v", 1, got, want)
	}
	if got, want := bs1.minValue(), 0; got != want {
		t.Errorf("minValue() = %v, want %v", got, want)
	}
	bs1.remove(0)
	if got, want := bs1.String(), "{63}"; got != want {
		t.Errorf("String() = %q, want %q", got, want)
	}
	if got, want := bs1.length(), 1; got != want {
		t.Errorf("length() = %q, want %q", got, want)
	}
	if got, want := bs1.contains(0), false; got != want {
		t.Errorf("contains(%v) = %v, want %v", 1, got, want)
	}
	if got, want := bs1.contains(63), true; got != want {
		t.Errorf("contains(%v) = %v, want %v", 1, got, want)
	}
	if got, want := bs1.minValue(), 63; got != want {
		t.Errorf("minValue() = %v, want %v", got, want)
	}
	bs1.add(20)
	if got, want := bs1.String(), "{20, 63}"; got != want {
		t.Errorf("String() = %q, want %q", got, want)
	}
	if got, want := bs1.length(), 2; got != want {
		t.Errorf("length() = %q, want %q", got, want)
	}
	if got, want := bs1.contains(0), false; got != want {
		t.Errorf("contains(%v) = %v, want %v", 1, got, want)
	}
	if got, want := bs1.contains(20), true; got != want {
		t.Errorf("contains(%v) = %v, want %v", 1, got, want)
	}
	if got, want := bs1.contains(63), true; got != want {
		t.Errorf("contains(%v) = %v, want %v", 1, got, want)
	}
	if got, want := bs1.minValue(), 20; got != want {
		t.Errorf("minValue() = %v, want %v", got, want)
	}
	bs1.clear(63)
	if got, want := bs1.String(), "{20}"; got != want {
		t.Errorf("String() = %q, want %q", got, want)
	}
	if got, want := bs1.length(), 1; got != want {
		t.Errorf("length() = %q, want %q", got, want)
	}
	if got, want := bs1.contains(0), false; got != want {
		t.Errorf("contains(%v) = %v, want %v", 1, got, want)
	}
	if got, want := bs1.contains(20), true; got != want {
		t.Errorf("contains(%v) = %v, want %v", 1, got, want)
	}
	if got, want := bs1.contains(63), false; got != want {
		t.Errorf("contains(%v) = %v, want %v", 1, got, want)
	}
	if got, want := bs1.minValue(), 20; got != want {
		t.Errorf("minValue() = %v, want %v", got, want)
	}
	bs2 := NewBitSet()
	bs2.add(64)
	bs1.or(bs2)
	if got, want := bs1.String(), "{20, 64}"; got != want {
		t.Errorf("String() = %q, want %q", got, want)
	}
	if got, want := bs1.length(), 2; got != want {
		t.Errorf("length() = %q, want %q", got, want)
	}
	if got, want := bs1.contains(0), false; got != want {
		t.Errorf("contains(%v) = %v, want %v", 1, got, want)
	}
	if got, want := bs1.contains(20), true; got != want {
		t.Errorf("contains(%v) = %v, want %v", 1, got, want)
	}
	if got, want := bs1.contains(63), false; got != want {
		t.Errorf("contains(%v) = %v, want %v", 1, got, want)
	}
	if got, want := bs1.contains(64), true; got != want {
		t.Errorf("contains(%v) = %v, want %v", 1, got, want)
	}
	if got, want := bs1.minValue(), 20; got != want {
		t.Errorf("minValue() = %v, want %v", got, want)
	}
	bs1.remove(20)
	if got, want := bs1.String(), "{64}"; got != want {
		t.Errorf("String() = %q, want %q", got, want)
	}
	if got, want := bs1.length(), 1; got != want {
		t.Errorf("length() = %q, want %q", got, want)
	}
	if got, want := bs1.contains(0), false; got != want {
		t.Errorf("contains(%v) = %v, want %v", 1, got, want)
	}
	if got, want := bs1.contains(20), false; got != want {
		t.Errorf("contains(%v) = %v, want %v", 1, got, want)
	}
	if got, want := bs1.contains(63), false; got != want {
		t.Errorf("contains(%v) = %v, want %v", 1, got, want)
	}
	if got, want := bs1.contains(64), true; got != want {
		t.Errorf("contains(%v) = %v, want %v", 1, got, want)
	}
	if got, want := bs1.minValue(), 64; got != want {
		t.Errorf("minValue() = %v, want %v", got, want)
	}
}
