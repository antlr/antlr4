/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
namespace Antlr4.Runtime.Test
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using Antlr4.Runtime.Misc;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    [TestClass]
    public class TestIntervalSet
    {
        [TestMethod]
        public void TestSingleElement()
        {
            IntervalSet s = IntervalSet.Of(99);
            String expecting = "99";
            Assert.AreEqual(s.ToString(), expecting);
        }

        [TestMethod]
        public void TestIsolatedElements()
        {
            IntervalSet s = new IntervalSet();
            s.Add(1);
            s.Add('z');
            s.Add('\uFFF0');
            String expecting = "{1, 122, 65520}";
            Assert.AreEqual(s.ToString(), expecting);
        }

        [TestMethod]
        public void TestMixedRangesAndElements()
        {
            IntervalSet s = new IntervalSet();
            s.Add(1);
            s.Add('a', 'z');
            s.Add('0', '9');
            String expecting = "{1, 48..57, 97..122}";
            Assert.AreEqual(s.ToString(), expecting);
        }

        [TestMethod]
        public void TestSimpleAnd()
        {
            IntervalSet s = IntervalSet.Of(10, 20);
            IntervalSet s2 = IntervalSet.Of(13, 15);
            String expecting = "{13..15}";
            String result = (s.And(s2)).ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestRangeAndIsolatedElement()
        {
            IntervalSet s = IntervalSet.Of('a', 'z');
            IntervalSet s2 = IntervalSet.Of('d');
            String expecting = "100";
            String result = (s.And(s2)).ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestEmptyIntersection()
        {
            IntervalSet s = IntervalSet.Of('a', 'z');
            IntervalSet s2 = IntervalSet.Of('0', '9');
            String expecting = "{}";
            String result = (s.And(s2)).ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestEmptyIntersectionSingleElements()
        {
            IntervalSet s = IntervalSet.Of('a');
            IntervalSet s2 = IntervalSet.Of('d');
            String expecting = "{}";
            String result = (s.And(s2)).ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestNotSingleElement()
        {
            IntervalSet vocabulary = IntervalSet.Of(1, 1000);
            vocabulary.Add(2000, 3000);
            IntervalSet s = IntervalSet.Of(50, 50);
            String expecting = "{1..49, 51..1000, 2000..3000}";
            String result = (s.Complement(vocabulary)).ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestNotSet()
        {
            IntervalSet vocabulary = IntervalSet.Of(1, 1000);
            IntervalSet s = IntervalSet.Of(50, 60);
            s.Add(5);
            s.Add(250, 300);
            String expecting = "{1..4, 6..49, 61..249, 301..1000}";
            String result = (s.Complement(vocabulary)).ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestNotEqualSet()
        {
            IntervalSet vocabulary = IntervalSet.Of(1, 1000);
            IntervalSet s = IntervalSet.Of(1, 1000);
            String expecting = "{}";
            String result = (s.Complement(vocabulary)).ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestNotSetEdgeElement()
        {
            IntervalSet vocabulary = IntervalSet.Of(1, 2);
            IntervalSet s = IntervalSet.Of(1);
            String expecting = "2";
            String result = (s.Complement(vocabulary)).ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestNotSetFragmentedVocabulary()
        {
            IntervalSet vocabulary = IntervalSet.Of(1, 255);
            vocabulary.Add(1000, 2000);
            vocabulary.Add(9999);
            IntervalSet s = IntervalSet.Of(50, 60);
            s.Add(3);
            s.Add(250, 300);
            s.Add(10000); // this is outside range of vocab and should be ignored
            String expecting = "{1..2, 4..49, 61..249, 1000..2000, 9999}";
            String result = (s.Complement(vocabulary)).ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestSubtractOfCompletelyContainedRange()
        {
            IntervalSet s = IntervalSet.Of(10, 20);
            IntervalSet s2 = IntervalSet.Of(12, 15);
            String expecting = "{10..11, 16..20}";
            String result = (s.Subtract(s2)).ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestSubtractOfOverlappingRangeFromLeft()
        {
            IntervalSet s = IntervalSet.Of(10, 20);
            IntervalSet s2 = IntervalSet.Of(5, 11);
            String expecting = "{12..20}";
            String result = (s.Subtract(s2)).ToString();
            Assert.AreEqual(expecting, result);

            IntervalSet s3 = IntervalSet.Of(5, 10);
            expecting = "{11..20}";
            result = (s.Subtract(s3)).ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestSubtractOfOverlappingRangeFromRight()
        {
            IntervalSet s = IntervalSet.Of(10, 20);
            IntervalSet s2 = IntervalSet.Of(15, 25);
            String expecting = "{10..14}";
            String result = (s.Subtract(s2)).ToString();
            Assert.AreEqual(expecting, result);

            IntervalSet s3 = IntervalSet.Of(20, 25);
            expecting = "{10..19}";
            result = (s.Subtract(s3)).ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestSubtractOfCompletelyCoveredRange()
        {
            IntervalSet s = IntervalSet.Of(10, 20);
            IntervalSet s2 = IntervalSet.Of(1, 25);
            String expecting = "{}";
            String result = (s.Subtract(s2)).ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestSubtractOfRangeSpanningMultipleRanges()
        {
            IntervalSet s = IntervalSet.Of(10, 20);
            s.Add(30, 40);
            s.Add(50, 60); // s has 3 ranges now: 10..20, 30..40, 50..60
            IntervalSet s2 = IntervalSet.Of(5, 55); // covers one and touches 2nd range
            String expecting = "{56..60}";
            String result = (s.Subtract(s2)).ToString();
            Assert.AreEqual(expecting, result);

            IntervalSet s3 = IntervalSet.Of(15, 55); // touches both
            expecting = "{10..14, 56..60}";
            result = (s.Subtract(s3)).ToString();
            Assert.AreEqual(expecting, result);
        }

        /** The following was broken:
            {0..113, 115..65534}-{0..115, 117..65534}=116..65534
         */
        [TestMethod]
        public void TestSubtractOfWackyRange()
        {
            IntervalSet s = IntervalSet.Of(0, 113);
            s.Add(115, 200);
            IntervalSet s2 = IntervalSet.Of(0, 115);
            s2.Add(117, 200);
            String expecting = "116";
            String result = (s.Subtract(s2)).ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestSimpleEquals()
        {
            IntervalSet s = IntervalSet.Of(10, 20);
            IntervalSet s2 = IntervalSet.Of(10, 20);
            Assert.AreEqual(s, s2);

            IntervalSet s3 = IntervalSet.Of(15, 55);
            Assert.AreNotEqual(s, s3);
        }

        [TestMethod]
        public void TestEquals()
        {
            IntervalSet s = IntervalSet.Of(10, 20);
            s.Add(2);
            s.Add(499, 501);
            IntervalSet s2 = IntervalSet.Of(10, 20);
            s2.Add(2);
            s2.Add(499, 501);
            Assert.AreEqual(s, s2);

            IntervalSet s3 = IntervalSet.Of(10, 20);
            s3.Add(2);
            Assert.AreNotEqual(s, s3);
        }

        [TestMethod]
        public void TestSingleElementMinusDisjointSet()
        {
            IntervalSet s = IntervalSet.Of(15, 15);
            IntervalSet s2 = IntervalSet.Of(1, 5);
            s2.Add(10, 20);
            String expecting = "{}"; // 15 - {1..5, 10..20} = {}
            String result = s.Subtract(s2).ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestMembership()
        {
            IntervalSet s = IntervalSet.Of(15, 15);
            s.Add(50, 60);
            Assert.IsFalse(s.Contains(0));
            Assert.IsFalse(s.Contains(20));
            Assert.IsFalse(s.Contains(100));
            Assert.IsTrue(s.Contains(15));
            Assert.IsTrue(s.Contains(55));
            Assert.IsTrue(s.Contains(50));
            Assert.IsTrue(s.Contains(60));
        }

        // {2,15,18} & 10..20
        [TestMethod]
        public void TestIntersectionWithTwoContainedElements()
        {
            IntervalSet s = IntervalSet.Of(10, 20);
            IntervalSet s2 = IntervalSet.Of(2, 2);
            s2.Add(15);
            s2.Add(18);
            String expecting = "{15, 18}";
            String result = (s.And(s2)).ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestIntersectionWithTwoContainedElementsReversed()
        {
            IntervalSet s = IntervalSet.Of(10, 20);
            IntervalSet s2 = IntervalSet.Of(2, 2);
            s2.Add(15);
            s2.Add(18);
            String expecting = "{15, 18}";
            String result = (s2.And(s)).ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestComplement()
        {
            IntervalSet s = IntervalSet.Of(100, 100);
            s.Add(101, 101);
            IntervalSet s2 = IntervalSet.Of(100, 102);
            String expecting = "102";
            String result = (s.Complement(s2)).ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestComplement2()
        {
            IntervalSet s = IntervalSet.Of(100, 101);
            IntervalSet s2 = IntervalSet.Of(100, 102);
            String expecting = "102";
            String result = (s.Complement(s2)).ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestComplement3()
        {
            IntervalSet s = IntervalSet.Of(1, 96);
            s.Add(99, Lexer.MaxCharValue);
            String expecting = "{97..98}";
            String result = (s.Complement(1, Lexer.MaxCharValue)).ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestMergeOfRangesAndSingleValues()
        {
            // {0..41, 42, 43..65534}
            IntervalSet s = IntervalSet.Of(0, 41);
            s.Add(42);
            s.Add(43, 65534);
            String expecting = "{0..65534}";
            String result = s.ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestMergeOfRangesAndSingleValuesReverse()
        {
            IntervalSet s = IntervalSet.Of(43, 65534);
            s.Add(42);
            s.Add(0, 41);
            String expecting = "{0..65534}";
            String result = s.ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestMergeWhereAdditionMergesTwoExistingIntervals()
        {
            // 42, 10, {0..9, 11..41, 43..65534}
            IntervalSet s = IntervalSet.Of(42);
            s.Add(10);
            s.Add(0, 9);
            s.Add(43, 65534);
            s.Add(11, 41);
            String expecting = "{0..65534}";
            String result = s.ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestMergeWithDoubleOverlap()
        {
            IntervalSet s = IntervalSet.Of(1, 10);
            s.Add(20, 30);
            s.Add(5, 25); // overlaps two!
            String expecting = "{1..30}";
            String result = s.ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestSize()
        {
            IntervalSet s = IntervalSet.Of(20, 30);
            s.Add(50, 55);
            s.Add(5, 19);
            String expecting = "32";
            String result = s.Size().ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestToList()
        {
            IntervalSet s = IntervalSet.Of(20, 25);
            s.Add(50, 55);
            s.Add(5, 5);
            int[] expecting = { 5, 20, 21, 22, 23, 24, 25, 50, 51, 52, 53, 54, 55 };
            IList<int> result = s.ToList();
            CollectionAssert.AreEquivalent(expecting, result.ToArray());
        }

        /** The following was broken:
         *  {'\u0000'..'s', 'u'..'\uFFFE'} & {'\u0000'..'q', 's'..'\uFFFE'}=
         *  {'\u0000'..'q', 's'}!!!! broken...
         *  'q' is 113 ascii
         *  'u' is 117
         */
        [TestMethod]
        public void TestNotRIntersectionNotT()
        {
            IntervalSet s = IntervalSet.Of(0, 's');
            s.Add('u', 200);
            IntervalSet s2 = IntervalSet.Of(0, 'q');
            s2.Add('s', 200);
            String expecting = "{0..113, 115, 117..200}";
            String result = (s.And(s2)).ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestRmSingleElement()
        {
            IntervalSet s = IntervalSet.Of(1, 10);
            s.Add(-3, -3);
            s.Remove(-3);
            String expecting = "{1..10}";
            String result = s.ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestRmLeftSide()
        {
            IntervalSet s = IntervalSet.Of(1, 10);
            s.Add(-3, -3);
            s.Remove(1);
            String expecting = "{-3, 2..10}";
            String result = s.ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestRmRightSide()
        {
            IntervalSet s = IntervalSet.Of(1, 10);
            s.Add(-3, -3);
            s.Remove(10);
            String expecting = "{-3, 1..9}";
            String result = s.ToString();
            Assert.AreEqual(expecting, result);
        }

        [TestMethod]
        public void TestRmMiddleRange()
        {
            IntervalSet s = IntervalSet.Of(1, 10);
            s.Add(-3, -3);
            s.Remove(5);
            String expecting = "{-3, 1..4, 6..10}";
            String result = s.ToString();
            Assert.AreEqual(expecting, result);
        }
    }
}
