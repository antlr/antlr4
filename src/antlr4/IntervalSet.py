from io import StringIO
import unittest
from antlr4.Token import Token

class Interval(object):

    def __init__(self, start, stop):
        self.start = start
        self.stop = stop
        self.range = xrange(start, stop)

    def __contains__(self, item):
        return item in self.range
    
    def __len__(self):
        return self.stop - self.start

    def __iter__(self):
        return iter(self.range)

class IntervalSet(object):

    def __init__(self):
        self.intervals = None
        self.readOnly = False

    def __iter__(self):
        if self.intervals is not None:
            for i in self.intervals:
                for c in i:
                    yield c

    def __getitem__(self, item):
        i = 0
        for k in self:
            if i==item:
                return k
            else:
                i += 1

    def addOne(self, v):
        self.addRange(Interval(v, v+1))

    def addRange(self, v):
        if self.intervals is None:
            self.intervals = list()
            self.intervals.append(v)
        else:
            # find insert pos
            k = 0
            for i in self.intervals:
                # distinct range -> insert
                if v.stop<i.start:
                    self.intervals.insert(k, v)
                    return
                # contiguous range -> adjust
                elif v.stop==i.start:
                    self.intervals[k] = Interval(v.start, i.stop)
                    return
                # overlapping range -> adjust and reduce
                elif v.start<=i.stop:
                    self.intervals[k] = Interval(min(i.start,v.start), max(i.stop,v.stop))
                    self.reduce(k)
                    return
                k += 1
            # greater than any existing
            self.intervals.append(v)

    def addSet(self, other):
        if other.intervals is not None:
            for i in other.intervals:
                self.addRange(i)
        return self

    def reduce(self, k):
        # only need to reduce if k is not the last
        if k<len(self.intervals)-1:
            l = self.intervals[k]
            r = self.intervals[k+1]
            # if r contained in l
            if l.stop >= r.stop:
                self.intervals.pop(k+1)
                self.reduce(k)
            elif l.stop >= r.start:
                self.intervals[k] = Interval(l.start, r.stop)
                self.intervals.pop(k+1)

    def __contains__(self, item):
        if self.intervals is None:
            return False
        else:
            for i in self.intervals:
                if item in i:
                    return True
            return False

    def __len__(self):
        xlen = 0
        for i in self.intervals:
            xlen += len(i)
        return xlen

    def remove(self, v):
        if self.intervals is not None:
            k = 0
            for i in self.intervals:
                # intervals is ordered
                if v<i.start:
                    return
                # check for single value range
                elif v==i.start and v==i.stop-1:
                    self.intervals.pop(k)
                    return
                # check for lower boundary
                elif v==i.start:
                    self.intervals[k] = Interval(i.start+1, i.stop)
                    return
                # check for upper boundary
                elif v==i.stop-1:
                    self.intervals[k] = Interval(i.start, i.stop-1)
                    return
                # split existing range
                elif v<i.stop-1:
                    x = Interval(i.start, v)
                    i.start = v + 1
                    self.intervals.insert(k, x)
                    return
                k += 1


    def toString(self, tokenNames):
        if self.intervals is None:
            return u"{}"
        with StringIO() as buf:
            if len(self)>1:
                buf.write(u"{")
            first = True
            for i in self.intervals:
                for j in i:
                    if not first:
                        buf.write(u", ")
                    buf.write(self.elementName(tokenNames, j))
                    first = False
            if len(self)>1:
                buf.write(u"}")
            return buf.getvalue()

    def elementName(self, tokenNames, a):
        if a==Token.EOF:
            return u"<EOF>"
        elif a==Token.EPSILON:
            return u"<EPSILON>"
        else:
            return tokenNames[a]


class TestIntervalSet(unittest.TestCase):

    def testEmpty(self):
        s = IntervalSet()
        self.assertIsNone(s.intervals)
        self.assertFalse(30 in s)

    def testOne(self):
        s = IntervalSet()
        s.addOne(30)
        self.assertTrue(30 in s)
        self.assertFalse(29 in s)
        self.assertFalse(31 in s)

    def testTwo(self):
        s = IntervalSet()
        s.addOne(30)
        s.addOne(40)
        self.assertTrue(30 in s)
        self.assertTrue(40 in s)
        self.assertFalse(35 in s)

    def testRange(self):
        s = IntervalSet()
        s.addRange(Interval(30,41))
        self.assertTrue(30 in s)
        self.assertTrue(40 in s)
        self.assertTrue(35 in s)

    def testDistinct1(self):
        s = IntervalSet()
        s.addRange(Interval(30,32))
        s.addRange(Interval(40,42))
        self.assertEquals(2,len(s.intervals))
        self.assertTrue(30 in s)
        self.assertTrue(40 in s)
        self.assertFalse(35 in s)

    def testDistinct2(self):
        s = IntervalSet()
        s.addRange(Interval(40,42))
        s.addRange(Interval(30,32))
        self.assertEquals(2,len(s.intervals))
        self.assertTrue(30 in s)
        self.assertTrue(40 in s)
        self.assertFalse(35 in s)

    def testContiguous1(self):
        s = IntervalSet()
        s.addRange(Interval(30,36))
        s.addRange(Interval(36,41))
        self.assertEquals(1,len(s.intervals))
        self.assertTrue(30 in s)
        self.assertTrue(40 in s)
        self.assertTrue(35 in s)

    def testContiguous2(self):
        s = IntervalSet()
        s.addRange(Interval(36,41))
        s.addRange(Interval(30,36))
        self.assertEquals(1,len(s.intervals))
        self.assertTrue(30 in s)
        self.assertTrue(40 in s)

    def testOverlapping1(self):
        s = IntervalSet()
        s.addRange(Interval(30,40))
        s.addRange(Interval(35,45))
        self.assertEquals(1,len(s.intervals))
        self.assertTrue(30 in s)
        self.assertTrue(44 in s)

    def testOverlapping2(self):
        s = IntervalSet()
        s.addRange(Interval(35,45))
        s.addRange(Interval(30,40))
        self.assertEquals(1,len(s.intervals))
        self.assertTrue(30 in s)
        self.assertTrue(44 in s)

    def testOverlapping3(self):
        s = IntervalSet()
        s.addRange(Interval(30,32))
        s.addRange(Interval(40,42))
        s.addRange(Interval(50,52))
        s.addRange(Interval(20,61))
        self.assertEquals(1,len(s.intervals))
        self.assertTrue(20 in s)
        self.assertTrue(60 in s)
