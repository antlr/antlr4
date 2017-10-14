package antlr

import (
	"testing"
)

func assertInterval(t *testing.T, i *Interval, l int){
	if i.length() != l{
		t.Errorf("For interval [%s] [%d] length is expected, [%d] is actual", i.String(), l, i.length())
	}
}

func assertString(t *testing.T, expected string, result string){
	if result != expected{
		t.Errorf("expected: %s, result:%s", result, expected)
	}
}

func TestDefaultIntervalLength(t *testing.T){
	assertInterval(t, NewInterval(0,0), 1)
	assertInterval(t, NewInterval(100, 100), 1)
	assertInterval(t, NewInterval(200,100),0)
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

func TestIntervalSetBridge(t *testing.T){
	s := NewIntervalSet()
	s.addRange(10,20)
	s.addRange(30,40)
	s.addRange(20,30)
	assertString(t, "10..40", s.toIndexString())
}

func TestIntervalSetBreak(t *testing.T){
	s := NewIntervalSet()
	s.addRange(10,40)
	s.removeOne(25)
	assertString(t, "{10..24, 26..40}", s.toIndexString())
}

func TestIntervalSetRemoveStart(t *testing.T){
	s := NewIntervalSet()
	s.addRange(10,40)
	s.removeOne(10)
	assertString(t, "11..40", s.toIndexString())
}

func TestIntervalSetRemoveEnd(t *testing.T){
	s := NewIntervalSet()
	s.addRange(10,40)
	s.removeOne(40)
	assertString(t, "10..39", s.toIndexString())
}

func TestIntervalSetRemoveSinglePoint(t *testing.T){
	s := NewIntervalSet()
	s.addOne(10)
	s.addRange(100,200)
	s.removeOne(10)
	assertString(t, "100..200", s.toIndexString())
}

func TestIntervalSetRemoveRangeMid(t *testing.T)  {
	s := NewIntervalSet()
	s.addRange(10,30)
	s.removeRange(NewInterval(15,20))
	assertString(t, "{10..14, 21..30}", s.toIndexString())
}

func TestIntervalSetRemoveEverything(t *testing.T){
	s := NewIntervalSet()
	s.addRange(10,20)
	s.addRange(30,40)
	s.removeRange(NewInterval(5, 50))
	assertString(t, "{}", s.toIndexString())
}