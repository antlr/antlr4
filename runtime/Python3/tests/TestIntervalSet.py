import unittest
from antlr4.IntervalSet import IntervalSet


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
        s.addRange(range(30,41))
        self.assertTrue(30 in s)
        self.assertTrue(40 in s)
        self.assertTrue(35 in s)

    def testDistinct1(self):
        s = IntervalSet()
        s.addRange(range(30,32))
        s.addRange(range(40,42))
        self.assertEquals(2,len(s.intervals))
        self.assertTrue(30 in s)
        self.assertTrue(40 in s)
        self.assertFalse(35 in s)

    def testDistinct2(self):
        s = IntervalSet()
        s.addRange(range(40,42))
        s.addRange(range(30,32))
        self.assertEquals(2,len(s.intervals))
        self.assertTrue(30 in s)
        self.assertTrue(40 in s)
        self.assertFalse(35 in s)

    def testContiguous1(self):
        s = IntervalSet()
        s.addRange(range(30,36))
        s.addRange(range(36,41))
        self.assertEquals(1,len(s.intervals))
        self.assertTrue(30 in s)
        self.assertTrue(40 in s)
        self.assertTrue(35 in s)

    def testContiguous2(self):
        s = IntervalSet()
        s.addRange(range(36,41))
        s.addRange(range(30,36))
        self.assertEquals(1,len(s.intervals))
        self.assertTrue(30 in s)
        self.assertTrue(40 in s)

    def testOverlapping1(self):
        s = IntervalSet()
        s.addRange(range(30,40))
        s.addRange(range(35,45))
        self.assertEquals(1,len(s.intervals))
        self.assertTrue(30 in s)
        self.assertTrue(44 in s)

    def testOverlapping2(self):
        s = IntervalSet()
        s.addRange(range(35,45))
        s.addRange(range(30,40))
        self.assertEquals(1,len(s.intervals))
        self.assertTrue(30 in s)
        self.assertTrue(44 in s)

    def testOverlapping3(self):
        s = IntervalSet()
        s.addRange(range(30,32))
        s.addRange(range(40,42))
        s.addRange(range(50,52))
        s.addRange(range(20,61))
        self.assertEquals(1,len(s.intervals))
        self.assertTrue(20 in s)
        self.assertTrue(60 in s)

    def testComplement(self):
        s = IntervalSet()
        s.addRange(range(10,21))
        c = s.complement(1,100)
        self.assertTrue(1 in c)
        self.assertTrue(100 in c)
        self.assertTrue(10 not in c)
        self.assertTrue(20 not in c)
