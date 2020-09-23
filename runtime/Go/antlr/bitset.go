package antlr

import (
	"strconv"
	"strings"
)

/*
	BitSet datatype is intentionally made without slice and map
	to reduce GC presure by avoiding pointers since values of this
	type created huge number of times.

	There is an assumption that bitset has no more than 1024 values.
	Length (cardinality) of this set is queued very frequently during parsing,
	so it is stored not calculated.
*/

type BitSet struct {
	a00 uint64
	a01 uint64
	a02 uint64
	a03 uint64
	a04 uint64
	a05 uint64
	a06 uint64
	a07 uint64
	a08 uint64
	a09 uint64
	a10 uint64
	a11 uint64
	a12 uint64
	a13 uint64
	a14 uint64
	a15 uint64

	_length int
}

func NewBitSet() *BitSet {
	return &BitSet{}
}

func incLen(value uint64, set uint64, len *int) uint64 {
	if value & set == 0 {
		*len++
	}
	return value | set
}

func decLen(value uint64, set uint64, len *int) uint64 {
	if value & set != 0 {
		*len--
	}
	return value & ^set
}

const log2Word64 = 6
const bitSetMaxIndex = 1024

func (b *BitSet) contains(value int) bool {
	if value > bitSetMaxIndex {
		panic("bitSetMaxIndex")
	}
	ind := uint64(value) >> log2Word64
	val := uint64(1 << (uint64(value) & 63))
	switch ind {
	case  0: return b.a00 & val != 0
	case  1: return b.a01 & val != 0
	case  2: return b.a02 & val != 0
	case  3: return b.a03 & val != 0
	case  4: return b.a04 & val != 0
	case  5: return b.a05 & val != 0
	case  6: return b.a06 & val != 0
	case  7: return b.a07 & val != 0
	case  8: return b.a08 & val != 0
	case  9: return b.a09 & val != 0
	case 10: return b.a10 & val != 0
	case 11: return b.a11 & val != 0
	case 12: return b.a12 & val != 0
	case 13: return b.a13 & val != 0
	case 14: return b.a14 & val != 0
	case 15: return b.a15 & val != 0
	}
	return false
}

func (b *BitSet) remove(value int) {
	if value > bitSetMaxIndex {
		panic("bitSetMaxIndex")
	}
	ind := uint64(value) >> log2Word64
	val := uint64(1 << (uint64(value) & 63))
	switch ind {
	case  0: b.a00 = decLen(b.a00, val, &b._length)
	case  1: b.a01 = decLen(b.a01, val, &b._length)
	case  2: b.a02 = decLen(b.a02, val, &b._length)
	case  3: b.a03 = decLen(b.a03, val, &b._length)
	case  4: b.a04 = decLen(b.a04, val, &b._length)
	case  5: b.a05 = decLen(b.a05, val, &b._length)
	case  6: b.a06 = decLen(b.a06, val, &b._length)
	case  7: b.a07 = decLen(b.a07, val, &b._length)
	case  8: b.a08 = decLen(b.a08, val, &b._length)
	case  9: b.a09 = decLen(b.a09, val, &b._length)
	case 10: b.a10 = decLen(b.a10, val, &b._length)
	case 11: b.a11 = decLen(b.a11, val, &b._length)
	case 12: b.a12 = decLen(b.a12, val, &b._length)
	case 13: b.a13 = decLen(b.a13, val, &b._length)
	case 14: b.a14 = decLen(b.a14, val, &b._length)
	case 15: b.a15 = decLen(b.a15, val, &b._length)
	}
}

func (b *BitSet) length() int {
	return b._length
}

func (b *BitSet) add(value int) {
	if value > bitSetMaxIndex {
		panic("bitSetMaxIndex")
	}
	ind := uint64(value) >> log2Word64
	val := uint64(1 << (uint64(value) & 63))
	switch ind {
	case  0: b.a00 = incLen(b.a00, val, &b._length)
	case  1: b.a01 = incLen(b.a01, val, &b._length)
	case  2: b.a02 = incLen(b.a02, val, &b._length)
	case  3: b.a03 = incLen(b.a03, val, &b._length)
	case  4: b.a04 = incLen(b.a04, val, &b._length)
	case  5: b.a05 = incLen(b.a05, val, &b._length)
	case  6: b.a06 = incLen(b.a06, val, &b._length)
	case  7: b.a07 = incLen(b.a07, val, &b._length)
	case  8: b.a08 = incLen(b.a08, val, &b._length)
	case  9: b.a09 = incLen(b.a09, val, &b._length)
	case 10: b.a10 = incLen(b.a10, val, &b._length)
	case 11: b.a11 = incLen(b.a11, val, &b._length)
	case 12: b.a12 = incLen(b.a12, val, &b._length)
	case 13: b.a13 = incLen(b.a13, val, &b._length)
	case 14: b.a14 = incLen(b.a14, val, &b._length)
	case 15: b.a15 = incLen(b.a15, val, &b._length)
	}
}

func (b *BitSet) minValue() int {
	for i := 0; i < bitSetMaxIndex; i++ {
		if b.contains(i) {
			return i
		}
	}
	return 0
}
func (b *BitSet) values() []int {
	ks := make([]int, 0)
	i := 0
	for i = 0; i < bitSetMaxIndex; i++ {
		if b.contains(i) {
			ks = append(ks, i)
		}
	}
	return ks
}

func (b *BitSet) String() string {
	vals := b.values()
	valsS := make([]string, len(vals))

	for i, val := range vals {
		valsS[i] = strconv.Itoa(val)
	}
	return "{" + strings.Join(valsS, ", ") + "}"
}

func (b *BitSet) clear() {
	b.a00 = 0
	b.a01 = 0
	b.a02 = 0
	b.a03 = 0
	b.a04 = 0
	b.a05 = 0
	b.a06 = 0
	b.a07 = 0
	b.a08 = 0
	b.a09 = 0
	b.a10 = 0
	b.a11 = 0
	b.a12 = 0
	b.a13 = 0
	b.a14 = 0
	b.a15 = 0
}

func (b *BitSet) or(o *BitSet) {
	b.a00 |= o.a00
	b.a01 |= o.a01
	b.a02 |= o.a02
	b.a03 |= o.a03
	b.a04 |= o.a04
	b.a05 |= o.a05
	b.a06 |= o.a06
	b.a07 |= o.a07
	b.a08 |= o.a08
	b.a09 |= o.a09
	b.a10 |= o.a10
	b.a11 |= o.a11
	b.a12 |= o.a12
	b.a13 |= o.a13
	b.a14 |= o.a14
	b.a15 |= o.a15
}
