// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.
package antlr

import (
	"testing"
	"fmt"
	"unicode"
	"strings"
)


/* Assume the following grammar for this test.

lexer grammar LexerA;
A : 'a';
B : 'b';
C : 'c';

*/

func TestInsertBeforeIndex0(t *testing.T){
	input := NewInputStream("abc")
	lexer := NewLexerA(input)
	stream := NewCommonTokenStream(lexer, 0)
	stream.Fill()
	tokens := NewTokenStreamRewriter(stream)
	tokens.InsertBeforeDefault(0, "0")
	result := tokens.GetTextDefault()
	if result != "0abc"{
		t.Errorf("test failed, got %s", result)
	}
}

func prepare_rewriter(str string) *TokenStreamRewriter{
	input := NewInputStream(str)
	lexer := NewLexerA(input)
	stream := NewCommonTokenStream(lexer, 0)
	stream.Fill()
	return NewTokenStreamRewriter(stream)
}

type LexerTest struct {
	input string
	expected string
	description string
	expected_exception []string
	ops func(*TokenStreamRewriter)
}

func NewLexerTest(input, expected, desc string, ops func(*TokenStreamRewriter)) LexerTest{
	return LexerTest{input:input, expected:expected, description:desc, ops:ops}
}

func NewLexerExceptionTest(input string, expected_err []string, desc string, ops func(*TokenStreamRewriter)) LexerTest{
	return LexerTest{input:input, expected_exception:expected_err, description:desc, ops:ops}
}

func panic_tester(t *testing.T, expected_msg []string, r *TokenStreamRewriter){
	defer func() {
		r :=recover()
		if r == nil{
			t.Errorf("Panic is expected, but finished normally")
		}else
		{
			s_e := r.(string)
			for _, e := range expected_msg{
				if !strings.Contains(s_e, e){
					t.Errorf("Element [%s] is not in error message: [%s]", e, s_e )
				}
			}
		}
	}()
	r.GetTextDefault()
}

func TestLexerA(t *testing.T){
	tests := []LexerTest{
		NewLexerTest("abc", "0abc", "InsertBeforeIndex0",
		func(r *TokenStreamRewriter){
			r.InsertBeforeDefault(0, "0")
		}),
		NewLexerTest("abc", "abcx","InsertAfterLastIndex",
		func(r *TokenStreamRewriter){
			r.InsertAfterDefault(2, "x")
		}),
		NewLexerTest("abc", "axbxc", "2InsertBeforeAfterMiddleIndex",
		func(r *TokenStreamRewriter){
			r.InsertBeforeDefault(1, "x")
			r.InsertAfterDefault(1, "x")
		}),
		NewLexerTest("abc", "xbc", "ReplaceIndex0",
		func(r *TokenStreamRewriter){
			r.ReplaceDefaultPos(0, "x")
		}),
		NewLexerTest("abc", "abx", "ReplaceLastIndex",
		func(r *TokenStreamRewriter){
			r.ReplaceDefaultPos(2, "x")
		}),
		NewLexerTest("abc", "axc", "ReplaceMiddleIndex",
		func(r *TokenStreamRewriter){
			r.ReplaceDefaultPos(1, "x")
		}),
		NewLexerTest("abc", "ayc", "2ReplaceMiddleIndex",
		func(r *TokenStreamRewriter){
			r.ReplaceDefaultPos(1, "x")
			r.ReplaceDefaultPos(1, "y")
		}),
		NewLexerTest("abc", "_ayc", "2ReplaceMiddleIndex1InsertBefore",
		func(r *TokenStreamRewriter){
			r.InsertBeforeDefault(0, "_")
			r.ReplaceDefaultPos(1, "x")
			r.ReplaceDefaultPos(1, "y")
		}),
		NewLexerTest("abc", "ac", "ReplaceThenDeleteMiddleIndex",
		func(r *TokenStreamRewriter){
			r.ReplaceDefaultPos(1, "x")
			r.DeleteDefaultPos(1)
		}),
		NewLexerExceptionTest("abc", []string{"insert op", "within boundaries of previous"},
			"InsertInPriorReplace",
		func(r *TokenStreamRewriter){
			r.ReplaceDefault(0,2, "x")
			r.InsertBeforeDefault(1, "0")
		}),
		NewLexerTest("abc", "0xbc", "InsertThenReplaceSameIndex",
		func(r *TokenStreamRewriter){
			r.InsertBeforeDefault(0,"0")
			r.ReplaceDefaultPos(0, "x")
		}),
		NewLexerTest("abc", "ayxbc", "2InsertMiddleIndex",
		func(r *TokenStreamRewriter){
			r.InsertBeforeDefault(1, "x")
			r.InsertBeforeDefault(1, "y")
		}),
		NewLexerTest("abc", "yxzbc", "2InsertThenReplaceIndex0",
		func(r *TokenStreamRewriter){
			r.InsertBeforeDefault(0, "x")
			r.InsertBeforeDefault(0, "y")
			r.ReplaceDefaultPos(0,"z")
		}),
		NewLexerTest("abc", "abyx", "ReplaceThenInsertBeforeLastIndex",
		func(r *TokenStreamRewriter){
			r.ReplaceDefaultPos(2, "x")
			r.InsertBeforeDefault(2, "y")
		}),
		NewLexerTest("abc", "abyx", "InsertThenReplaceLastIndex",
		func(r *TokenStreamRewriter){
			r.InsertBeforeDefault(2, "y")
			r.ReplaceDefaultPos(2, "x")
		}),
		NewLexerTest("abc", "abxy", "ReplaceThenInsertAfterLastIndex",
		func(r *TokenStreamRewriter){
			r.ReplaceDefaultPos(2, "x")
			r.InsertAfterDefault(2, "y")
		}),
		NewLexerTest("abcccba", "abyxba", "ReplaceThenInsertAtLeftEdge",
		func(r *TokenStreamRewriter){
			r.ReplaceDefault(2, 4, "x")
			r.InsertBeforeDefault(2, "y")
		}),
		NewLexerTest("abcccba", "abyxba", "ReplaceThenInsertAtLeftEdge",
		func(r *TokenStreamRewriter){
			r.ReplaceDefault(2, 4, "x")
			r.InsertBeforeDefault(2, "y")
		}),
		NewLexerExceptionTest("abcccba",
			[]string{"insert op", "InsertBeforeOp", "within boundaries of previous", "ReplaceOp"},
			"ReplaceRangeThenInsertAtRightEdge",
		func(r *TokenStreamRewriter){
			r.ReplaceDefault(2, 4, "x")
			r.InsertBeforeDefault(4, "y")
		}),
		NewLexerTest("abcccba", "abxyba", "ReplaceRangeThenInsertAfterRightEdge",
		func(r *TokenStreamRewriter){
			r.ReplaceDefault(2, 4, "x")
			r.InsertAfterDefault(4, "y")
		}),
		NewLexerTest("abcccba", "x", "ReplaceAll",
		func(r *TokenStreamRewriter){
			r.ReplaceDefault(0, 6, "x")
		}),
		NewLexerTest("abcccba", "abxyzba", "ReplaceSubsetThenFetch",
		func(r *TokenStreamRewriter){
			r.ReplaceDefault(2, 4, "xyz")
		}),
		NewLexerExceptionTest("abcccba",
			[]string{"replace op boundaries of", "ReplaceOp", "overlap with previous"},
			"ReplaceThenReplaceSuperset",
		func(r *TokenStreamRewriter){
			r.ReplaceDefault(2, 4, "xyz")
			r.ReplaceDefault(3, 5, "foo")
		}),
		NewLexerExceptionTest("abcccba",
			[]string{"replace op boundaries of", "ReplaceOp", "overlap with previous"},
			"ReplaceThenReplaceLowerIndexedSuperset",
		func(r *TokenStreamRewriter){
			r.ReplaceDefault(2, 4, "xyz")
			r.ReplaceDefault(1, 3, "foo")
		}),
		NewLexerTest("abcba", "fooa", "ReplaceSingleMiddleThenOverlappingSuperset",
		func(r *TokenStreamRewriter){
			r.ReplaceDefault(2, 2, "xyz")
			r.ReplaceDefault(0, 3, "foo")
		}),
		NewLexerTest("abc", "yxabc", "CombineInserts",
		func(r *TokenStreamRewriter){
			r.InsertBeforeDefault(0, "x")
			r.InsertBeforeDefault(0, "y")
		}),
		NewLexerTest("abc", "yazxbc", "Combine3Inserts",
		func(r *TokenStreamRewriter){
			r.InsertBeforeDefault(1, "x")
			r.InsertBeforeDefault(0, "y")
			r.InsertBeforeDefault(1, "z")
		}),
		NewLexerTest("abc", "zfoo", "CombineInsertOnLeftWithReplace",
		func(r *TokenStreamRewriter){
			r.ReplaceDefault(0, 2, "foo")
			r.InsertBeforeDefault(0, "z")
		}),
		NewLexerTest("abc", "z", "CombineInsertOnLeftWithDelete",
		func(r *TokenStreamRewriter){
			r.DeleteDefault(0,2)
			r.InsertBeforeDefault(0, "z")
		}),
		NewLexerTest("abc", "zaxbyc", "DisjointInserts",
		func(r *TokenStreamRewriter){
			r.InsertBeforeDefault(1, "x")
			r.InsertBeforeDefault(2, "y")
			r.InsertBeforeDefault(0, "z")
		}),
		NewLexerTest("abcc", "bar", "OverlappingReplace",
		func(r *TokenStreamRewriter){
			r.ReplaceDefault(1,2, "foo")
			r.ReplaceDefault(0, 3, "bar")
		}),
		NewLexerExceptionTest("abcc",
			[]string{"replace op boundaries of", "ReplaceOp", "overlap with previous"},
			"OverlappingReplace2",
		func(r *TokenStreamRewriter){
			r.ReplaceDefault(0, 3, "bar")
			r.ReplaceDefault(1, 2, "foo")
		}),
		NewLexerTest("abcc", "barc", "OverlappingReplace3",
		func(r *TokenStreamRewriter){
			r.ReplaceDefault(1,2, "foo")
			r.ReplaceDefault(0, 2, "bar")
		}),
		NewLexerTest("abcc", "abar", "OverlappingReplace4",
		func(r *TokenStreamRewriter){
			r.ReplaceDefault(1,2, "foo")
			r.ReplaceDefault(1, 3, "bar")
		}),
		NewLexerTest("abcc", "afooc", "DropIdenticalReplace",
		func(r *TokenStreamRewriter){
			r.ReplaceDefault(1,2, "foo")
			r.ReplaceDefault(1, 2, "foo")
		}),
		NewLexerTest("abc", "afoofoo", "DropPrevCoveredInsert",
		func(r *TokenStreamRewriter){
			r.InsertBeforeDefault(1, "foo")
			r.ReplaceDefault(1, 2, "foo")
		}),
		NewLexerTest("abcc", "axbfoo", "LeaveAloneDisjointInsert",
		func(r *TokenStreamRewriter){
			r.InsertBeforeDefault(1, "x")
			r.ReplaceDefault(2, 3, "foo")
		}),
		NewLexerTest("abcc", "axbfoo", "LeaveAloneDisjointInsert2",
		func(r *TokenStreamRewriter){
			r.ReplaceDefault(2, 3, "foo")
			r.InsertBeforeDefault(1, "x")
		}),
		NewLexerTest("abc", "aby", "InsertBeforeTokenThenDeleteThatToken",
		func(r *TokenStreamRewriter){
			r.InsertBeforeDefault(2, "y")
			r.DeleteDefaultPos(2)
		}),
		NewLexerTest("aa", "<b>a</b><b>a</b>", "DistinguishBetweenInsertAfterAndInsertBeforeToPreserverOrder",
		func(r *TokenStreamRewriter){
			r.InsertBeforeDefault(0, "<b>")
			r.InsertAfterDefault(0, "</b>")
			r.InsertBeforeDefault(1, "<b>")
			r.InsertAfterDefault(1,"</b>")
		}),
		NewLexerTest("aa", "<b><p>a</p></b><b>a</b>", "DistinguishBetweenInsertAfterAndInsertBeforeToPreserverOrder2",
		func(r *TokenStreamRewriter){
			r.InsertBeforeDefault(0, "<p>")
			r.InsertBeforeDefault(0, "<b>")
			r.InsertAfterDefault(0, "</p>")
			r.InsertAfterDefault(0, "</b>")
			r.InsertBeforeDefault(1, "<b>")
			r.InsertAfterDefault(1,"</b>")
		}),
		NewLexerTest("ab", "<div><b><p>a</p></b></div>!b", "DistinguishBetweenInsertAfterAndInsertBeforeToPreserverOrder2",
		func(r *TokenStreamRewriter){
			r.InsertBeforeDefault(0, "<p>")
			r.InsertBeforeDefault(0, "<b>")
			r.InsertBeforeDefault(0, "<div>")
			r.InsertAfterDefault(0, "</p>")
			r.InsertAfterDefault(0, "</b>")
			r.InsertAfterDefault(0, "</div>")
			r.InsertBeforeDefault(1, "!")
		}),
	}


	for _,c := range tests{
		t.Run(c.description,func(t *testing.T) {
			rewriter := prepare_rewriter(c.input)
			c.ops(rewriter)
			if len(c.expected_exception)>0{
				panic_tester(t, c.expected_exception, rewriter)
			}else{
				result := rewriter.GetTextDefault()
				if result!=c.expected{
					t.Errorf("Expected:%s | Result: %s", c.expected, result)
				}
			}
		} )
	}
}


// Suppress unused import error
var _ = fmt.Printf
var _ = unicode.IsLetter

var serializedLexerAtn = []int32{
	4, 0, 3, 13, 6, 65535, 2, 0, 7, 0, 2, 1, 7, 1, 2, 2, 7, 2, 1, 0, 1, 0,
	1, 1, 1, 1, 1, 2, 1, 2, 0, 0, 3, 1, 1, 3, 2, 5, 3, 1, 0, 0, 0, 12, 0, 1,
	1, 0, 0, 0, 0, 3, 1, 0, 0, 0, 0, 5, 1, 0, 0, 0, 1, 7, 1, 0, 0, 0, 3, 9,
	1, 0, 0, 0, 5, 11, 1, 0, 0, 0, 7, 8, 5, 97, 0, 0, 8, 2, 1, 0, 0, 0, 9,
	10, 5, 98, 0, 0, 10, 4, 1, 0, 0, 0, 11, 12, 5, 99, 0, 0, 12, 6, 1, 0, 0,
	0, 1, 0, 0,
}

var lexerDeserializer = NewATNDeserializer(nil)
var lexerAtn = lexerDeserializer.Deserialize(serializedLexerAtn)

var lexerChannelNames = []string{
	"DEFAULT_TOKEN_CHANNEL", "HIDDEN",
}

var lexerModeNames = []string{
	"DEFAULT_MODE",
}

var lexerLiteralNames = []string{
	"", "'a'", "'b'", "'c'",
}

var lexerSymbolicNames = []string{
	"", "A", "B", "C",
}

var lexerRuleNames = []string{
	"A", "B", "C",
}

type LexerA struct {
	*BaseLexer
	channelNames []string
	modeNames    []string
	// TODO: EOF string
}

var lexerDecisionToDFA = make([]*DFA, len(lexerAtn.DecisionToState))

func init() {
	for index, ds := range lexerAtn.DecisionToState {
		lexerDecisionToDFA[index] = NewDFA(ds, index)
	}
}

func NewLexerA(input CharStream) *LexerA {

	l := new(LexerA)

	l.BaseLexer = NewBaseLexer(input)
	l.Interpreter = NewLexerATNSimulator(l, lexerAtn, lexerDecisionToDFA, NewPredictionContextCache())

	l.channelNames = lexerChannelNames
	l.modeNames = lexerModeNames
	l.RuleNames = lexerRuleNames
	l.LiteralNames = lexerLiteralNames
	l.SymbolicNames = lexerSymbolicNames
	l.GrammarFileName = "LexerA.g4"
	// TODO: l.EOF = antlr.TokenEOF

	return l
}

// LexerA tokens.
const (
	LexerAA = 1
	LexerAB = 2
	LexerAC = 3
)

