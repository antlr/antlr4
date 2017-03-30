# Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
# Use of this file is governed by the BSD 3-clause license that
# can be found in the LICENSE.txt file in the project root.

import unittest

from antlr4.IntervalSet import Interval

from mocks.TestLexer import TestLexer, TestLexer2
from antlr4.TokenStreamRewriter import TokenStreamRewriter
from antlr4.InputStream import InputStream
from antlr4.CommonTokenStream import CommonTokenStream


class TestTokenStreamRewriter(unittest.TestCase):
    def testInsertBeforeIndexZero(self):
        input = InputStream('abc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)
        rewriter.insertBeforeIndex(0, '0')

        self.assertEquals(rewriter.getDefaultText(), '0abc')

    def testInsertAfterLastIndex(self):
        input = InputStream('abc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)
        rewriter.insertAfter(10, 'x')

        self.assertEquals(rewriter.getDefaultText(), 'abcx')

    def test2InsertBeforeAfterMiddleIndex(self):
        input = InputStream('abc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.insertBeforeIndex(1, 'x')
        rewriter.insertAfter(1, 'x')

        self.assertEquals(rewriter.getDefaultText(), 'axbxc')

    def testReplaceIndex(self):
        input = InputStream('abc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.replaceIndex(0, 'x')

        self.assertEquals(rewriter.getDefaultText(), 'xbc')

    def testReplaceLastIndex(self):
        input = InputStream('abc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.replaceIndex(2, 'x')

        self.assertEquals(rewriter.getDefaultText(), 'abx')

    def testReplaceMiddleIndex(self):
        input = InputStream('abc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.replaceIndex(1, 'x')

        self.assertEquals(rewriter.getDefaultText(), 'axc')

    def testToStringStartStop(self):
        input = InputStream('x = 3 * 0;')
        lexer = TestLexer2(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.replaceRange(4, 8, '0')

        self.assertEquals(rewriter.getDefaultText(), 'x = 0;')
        self.assertEquals(rewriter.getText('default', Interval(0, 9)), 'x = 0;')
        self.assertEquals(rewriter.getText('default', Interval(4, 8)), '0')

    def testToStringStartStop2(self):
        input = InputStream('x = 3 * 0 + 2 * 0;')
        lexer = TestLexer2(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        self.assertEquals('x = 3 * 0 + 2 * 0;', rewriter.getDefaultText())

        # replace 3 * 0 with 0
        rewriter.replaceRange(4, 8, '0')
        self.assertEquals('x = 0 + 2 * 0;', rewriter.getDefaultText())
        self.assertEquals('x = 0 + 2 * 0;', rewriter.getText('default', Interval(0, 17)))
        self.assertEquals('0', rewriter.getText('default', Interval(4, 8)))
        self.assertEquals('x = 0', rewriter.getText('default', Interval(0, 8)))
        self.assertEquals('2 * 0', rewriter.getText('default', Interval(12, 16)))

        rewriter.insertAfter(17, "// comment")
        self.assertEquals('2 * 0;// comment', rewriter.getText('default', Interval(12, 18)))

        self.assertEquals('x = 0', rewriter.getText('default', Interval(0, 8)))

    def test2ReplaceMiddleIndex(self):
        input = InputStream('abc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.replaceIndex(1, 'x')
        rewriter.replaceIndex(1, 'y')

        self.assertEquals('ayc', rewriter.getDefaultText())

    def test2ReplaceMiddleIndex1InsertBefore(self):
        input = InputStream('abc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.insertBeforeIndex(0, "_")
        rewriter.replaceIndex(1, 'x')
        rewriter.replaceIndex(1, 'y')

        self.assertEquals('_ayc', rewriter.getDefaultText())

    def test2InsertMiddleIndex(self):
        input = InputStream('abc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.insertBeforeIndex(1, 'x')
        rewriter.insertBeforeIndex(1, 'y')

        self.assertEquals('ayxbc', rewriter.getDefaultText())

    def testReplaceThenDeleteMiddleIndex(self):
        input = InputStream('abc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.replaceRange(0, 2, 'x')
        rewriter.insertBeforeIndex(1, '0')

        with self.assertRaises(ValueError) as ctx:
            rewriter.getDefaultText()
        self.assertEquals(
            'insert op <InsertBeforeOp@[@1,1:1=\'b\',<2>,1:1]:"0"> within boundaries of previous <ReplaceOp@[@0,0:0=\'a\',<1>,1:0]..[@2,2:2=\'c\',<3>,1:2]:"x">',
            ctx.exception.message
        )

    def testInsertThenReplaceSameIndex(self):
        input = InputStream('abc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.insertBeforeIndex(0, '0')
        rewriter.replaceIndex(0, 'x')

        self.assertEquals('0xbc', rewriter.getDefaultText())

    def test2InsertThenReplaceIndex0(self):
        input = InputStream('abc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.insertBeforeIndex(0, 'x')
        rewriter.insertBeforeIndex(0, 'y')
        rewriter.replaceIndex(0, 'z')

        self.assertEquals('yxzbc', rewriter.getDefaultText())

    def testReplaceThenInsertBeforeLastIndex(self):
        input = InputStream('abc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.replaceIndex(2, 'x')
        rewriter.insertBeforeIndex(2, 'y')

        self.assertEquals('abyx', rewriter.getDefaultText())

    def testReplaceThenInsertAfterLastIndex(self):
        input = InputStream('abc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.replaceIndex(2, 'x')
        rewriter.insertAfter(2, 'y')

        self.assertEquals('abxy', rewriter.getDefaultText())

    def testReplaceRangeThenInsertAtLeftEdge(self):
        input = InputStream('abcccba')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.replaceRange(2, 4, 'x')
        rewriter.insertBeforeIndex(2, 'y')

        self.assertEquals('abyxba', rewriter.getDefaultText())

    def testReplaceRangeThenInsertAtRightEdge(self):
        input = InputStream('abcccba')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.replaceRange(2, 4, 'x')
        rewriter.insertBeforeIndex(4, 'y')

        with self.assertRaises(ValueError) as ctx:
            rewriter.getDefaultText()
        msg = ctx.exception.message
        self.assertEquals(
            "insert op <InsertBeforeOp@[@4,4:4='c',<3>,1:4]:\"y\"> within boundaries of previous <ReplaceOp@[@2,2:2='c',<3>,1:2]..[@4,4:4='c',<3>,1:4]:\"x\">",
            msg
        )

    def testReplaceRangeThenInsertAfterRightEdge(self):
        input = InputStream('abcccba')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.replaceRange(2, 4, 'x')
        rewriter.insertAfter(4, 'y')

        self.assertEquals('abxyba', rewriter.getDefaultText())

    def testReplaceAll(self):
        input = InputStream('abcccba')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.replaceRange(0, 6, 'x')

        self.assertEquals('x', rewriter.getDefaultText())

    def testReplaceSubsetThenFetch(self):
        input = InputStream('abcccba')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.replaceRange(2, 4, 'xyz')

        self.assertEquals('abxyzba', rewriter.getDefaultText())

    def testReplaceThenReplaceSuperset(self):
        input = InputStream('abcccba')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.replaceRange(2, 4, 'xyz')
        rewriter.replaceRange(3, 5, 'foo')

        with self.assertRaises(ValueError) as ctx:
            rewriter.getDefaultText()
        msg = ctx.exception.message
        self.assertEquals(
            """replace op boundaries of <ReplaceOp@[@3,3:3='c',<3>,1:3]..[@5,5:5='b',<2>,1:5]:"foo"> overlap with previous <ReplaceOp@[@2,2:2='c',<3>,1:2]..[@4,4:4='c',<3>,1:4]:"xyz">""",
            msg
        )

    def testReplaceThenReplaceLowerIndexedSuperset(self):
        input = InputStream('abcccba')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.replaceRange(2, 4, 'xyz')
        rewriter.replaceRange(1, 3, 'foo')

        with self.assertRaises(ValueError) as ctx:
            rewriter.getDefaultText()
        msg = ctx.exception.message
        self.assertEquals(
            """replace op boundaries of <ReplaceOp@[@1,1:1='b',<2>,1:1]..[@3,3:3='c',<3>,1:3]:"foo"> overlap with previous <ReplaceOp@[@2,2:2='c',<3>,1:2]..[@4,4:4='c',<3>,1:4]:"xyz">""",
            msg
        )

    def testReplaceSingleMiddleThenOverlappingSuperset(self):
        input = InputStream('abcba')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.replaceIndex(2, 'xyz')
        rewriter.replaceRange(0, 3, 'foo')

        self.assertEquals('fooa', rewriter.getDefaultText())

    def testCombineInserts(self):
        input = InputStream('abc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.insertBeforeIndex(0, 'x')
        rewriter.insertBeforeIndex(0, 'y')

        self.assertEquals('yxabc', rewriter.getDefaultText())

    def testCombine3Inserts(self):
        input = InputStream('abc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.insertBeforeIndex(1, 'x')
        rewriter.insertBeforeIndex(0, 'y')
        rewriter.insertBeforeIndex(1, 'z')

        self.assertEquals('yazxbc', rewriter.getDefaultText())

    def testCombineInsertOnLeftWithReplace(self):
        input = InputStream('abc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.replaceRange(0, 2, 'foo')
        rewriter.insertBeforeIndex(0, 'z')

        self.assertEquals('zfoo', rewriter.getDefaultText())

    def testCombineInsertOnLeftWithDelete(self):
        input = InputStream('abc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.delete('default', 0, 2)
        rewriter.insertBeforeIndex(0, 'z')

        self.assertEquals('z', rewriter.getDefaultText())

    def testDisjointInserts(self):
        input = InputStream('abc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.insertBeforeIndex(1, 'x')
        rewriter.insertBeforeIndex(2, 'y')
        rewriter.insertBeforeIndex(0, 'z')

        self.assertEquals('zaxbyc', rewriter.getDefaultText())

    def testOverlappingReplace(self):
        input = InputStream('abc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.replaceRange(1, 2, 'foo')
        rewriter.replaceRange(0, 3, 'bar')

        self.assertEquals('bar', rewriter.getDefaultText())

    def testOverlappingReplace2(self):
        input = InputStream('abc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.replaceRange(0, 3, 'bar')
        rewriter.replaceRange(1, 2, 'foo')

        with self.assertRaises(ValueError) as ctx:
            rewriter.getDefaultText()

        self.assertEquals(
            """replace op boundaries of <ReplaceOp@[@1,1:1='b',<2>,1:1]..[@2,2:2='c',<3>,1:2]:"foo"> overlap with previous <ReplaceOp@[@0,0:0='a',<1>,1:0]..[@3,3:2='<EOF>',<-1>,1:3]:"bar">""",
            ctx.exception.message
        )

    def testOverlappingReplace3(self):
        input = InputStream('abcc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.replaceRange(1, 2, 'foo')
        rewriter.replaceRange(0, 2, 'bar')

        self.assertEquals('barc', rewriter.getDefaultText())

    def testOverlappingReplace4(self):
        input = InputStream('abcc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.replaceRange(1, 2, 'foo')
        rewriter.replaceRange(1, 3, 'bar')

        self.assertEquals('abar', rewriter.getDefaultText())

    def testDropIdenticalReplace(self):
        input = InputStream('abcc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.replaceRange(1, 2, 'foo')
        rewriter.replaceRange(1, 2, 'foo')

        self.assertEquals('afooc', rewriter.getDefaultText())

    def testDropPrevCoveredInsert(self):
        input = InputStream('abc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.insertBeforeIndex(1, 'foo')
        rewriter.replaceRange(1, 2, 'foo')

        self.assertEquals('afoofoo', rewriter.getDefaultText())

    def testLeaveAloneDisjointInsert(self):
        input = InputStream('abcc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.insertBeforeIndex(1, 'x')
        rewriter.replaceRange(2, 3, 'foo')

        self.assertEquals('axbfoo', rewriter.getDefaultText())

    def testLeaveAloneDisjointInsert2(self):
        input = InputStream('abcc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.replaceRange(2, 3, 'foo')
        rewriter.insertBeforeIndex(1, 'x')

        self.assertEquals('axbfoo', rewriter.getDefaultText())

    def testInsertBeforeTokenThenDeleteThatToken(self):
        input = InputStream('abc')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.insertBeforeIndex(1, 'foo')
        rewriter.replaceRange(1, 2, 'foo')

        self.assertEquals('afoofoo', rewriter.getDefaultText())

    def testPreservesOrderOfContiguousInserts(self):
        """
        Test for fix for: https://github.com/antlr/antlr4/issues/550
        """
        input = InputStream('aa')
        lexer = TestLexer(input)
        stream = CommonTokenStream(lexer=lexer)
        stream.fill()
        rewriter = TokenStreamRewriter(tokens=stream)

        rewriter.insertBeforeIndex(0, '<b>')
        rewriter.insertAfter(0, '</b>')
        rewriter.insertBeforeIndex(1, '<b>')
        rewriter.insertAfter(1, '</b>')

        self.assertEquals('<b>a</b><b>a</b>', rewriter.getDefaultText())


if __name__ == '__main__':
    unittest.main()
