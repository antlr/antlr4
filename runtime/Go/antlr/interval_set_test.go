package antlr

import (
	"testing"
)

func assertInterval(t *testing.T, i *Interval, l int){
	if i.length() != l{
		t.Errorf("For interval [%s] [%d] length is expected, [%d] is actual", i.String(), l, i.length())
	}
}

func assertString(t *testing.T, result string, expected string){
	if result != expected{
		t.Errorf("expected: %s, result:%s", result, expected)
	}
}

func TestDefaultIntervalLength(t *testing.T){
	assertInterval(t, NewInterval(0,0), 1)
	assertInterval(t, NewInterval(100, 100), 1)

}

func TestIntervalSetAbsorb(t *testing.T){
	s := NewIntervalSet()
	s.addRange(10,20)
	s.addRange(11,19)
	assertString(t, "10..20", s.toIndexString())
}

func TestIntervalSetOverlap(t *testing.T){
	s := NewIntervalSet()
	s.addRange(10, 20)
	s.addRange(15, 25)
	assertString(t, "10..25", s.toIndexString())
}

func TestIntervalSetIndependent(t *testing.T)  {
	s := NewIntervalSet()
	s.addRange(10, 20)
	s.addRange(30, 40)
	assertString(t, "{10..20, 30..40}", s.toIndexString())
}

func TestIntervalSetAdjoint(t *testing.T)  {
	s := NewIntervalSet()
	s.addRange(10,20)
	s.addRange(20, 30)
	assertString(t, "10..30", s.toIndexString())
}