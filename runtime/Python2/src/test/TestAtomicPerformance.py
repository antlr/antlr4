__author__ = 'ericvergnaud'

import unittest
import time
import uuid

class TestAtomicPerformance(unittest.TestCase):

    # test to check that on the current platform, a < b < c is faster than b in xrange(a,c)
    # x in xrange is intensively used in Interval
    def test_in_xrange(self):
        xstart = time.time()
        # check with 20 various range sizes
        for max in xrange(0,1000,50):
            r = xrange(0,max)
            for i in r:
                ok = i in r
        xend = time.time()
        print str((xend-xstart)*1000000)
        ystart = time.time()
        # check with 20 various range sizes
        for max in xrange(0,1000,50):
            r = xrange(0,max)
            for i in r:
                ok = max > i >= 0
        yend = time.time()
        print str((yend-ystart)*1000000)
        self.assertTrue((yend-ystart)<(xend-xstart))

    # test to check that on the current platform, hashing string tuples is faster than hashing strings
    def test_tuple_hash(self):
        # create an array of random strings
        s = []
        for i in xrange(0,10000):
            s.append(str(uuid.uuid4()))
        # hash then using string concat
        xstart = time.time()
        for i in xrange(0,9999):
            a = hash(s[i] + s[i+1])
        for i in xrange(0,9998):
            a = hash(s[i] + s[i+1] + s[i+2])
        for i in xrange(0,9997):
            a = hash(s[i] + s[i+1] + s[i+2] + s[i+3])
        xend = time.time()
        print str((xend-xstart)*1000000)
        ystart = time.time()
        # hash then using string tuple
        for i in xrange(0,9999):
            a = hash((s[i],s[i+1]))
        for i in xrange(0,9998):
            a = hash((s[i],s[i+1],s[i+2]))
        for i in xrange(0,9997):
            a = hash((s[i],s[i+1],s[i+2],s[i+3]))
        yend = time.time()
        print str((yend-ystart)*1000000)
        self.assertTrue((yend-ystart)<(xend-xstart))


if __name__ == '__main__':
    unittest.main()
