__author__ = 'ericvergnaud'

import unittest
import time
import uuid
import io

class TestAtomicPerformance(unittest.TestCase):

    # test to check that on the current platform, a < b < c is faster than b in range(a,c)
    # x in xrange is intensively used in Interval
    def test_in_range(self):
        xstart = time.time()
        # check with 20 various range sizes
        for max in range(0,1000,50):
            r = range(0,max)
            for i in r:
                ok = i in r
        xend = time.time()
        print(str((xend-xstart)*1000000))
        ystart = time.time()
        # check with 20 various range sizes
        for max in range(0,1000,50):
            r = range(0,max)
            for i in r:
                ok = max > i >= 0
        yend = time.time()
        print(str((yend-ystart)*1000000))
        self.assertTrue((yend-ystart)<(xend-xstart))

    # test to check that on the current platform, hashing string tuples is faster than hashing strings
    def test_tuple_hash(self):
        # create an array of random strings
        s = []
        for i in range(0,10000):
            s.append(str(uuid.uuid4()))
        # hash then using string concat
        xstart = time.time()
        for i in range(0,9999):
            a = hash(s[i] + s[i+1])
        for i in range(0,9998):
            a = hash(s[i] + s[i+1] + s[i+2])
        for i in range(0,9997):
            a = hash(s[i] + s[i+1] + s[i+2] + s[i+3])
        xend = time.time()
        print(str((xend-xstart)*1000000))
        ystart = time.time()
        # hash then using string tuple
        for i in range(0,9999):
            a = hash((s[i],s[i+1]))
        for i in range(0,9998):
            a = hash((s[i],s[i+1],s[i+2]))
        for i in range(0,9997):
            a = hash((s[i],s[i+1],s[i+2],s[i+3]))
        yend = time.time()
        print(str((yend-ystart)*1000000))
        self.assertTrue((yend-ystart)<(xend-xstart))
        zstart = time.time()
        # hash then using string tuple
        for i in range(0,9999):
            b = io.StringIO()
            b.write(s[i])
            b.write(s[i+1])
            a = hash(b.getvalue())
            b.close()
        for i in range(0,9998):
            b = io.StringIO()
            b.write(s[i])
            b.write(s[i+1])
            b.write(s[i+2])
            a = hash(b.getvalue())
            b.close()
        for i in range(0,9997):
            b = io.StringIO()
            b.write(s[i])
            b.write(s[i+1])
            b.write(s[i+2])
            b.write(s[i+3])
            a = hash(b.getvalue())
            b.close()
        zend = time.time()
        print(str((zend-zstart)*1000000))
        self.assertTrue((yend-ystart)<(zend-zstart))


if __name__ == '__main__':
    unittest.main()
