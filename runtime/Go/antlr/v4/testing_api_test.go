package antlr

import (
	"testing"
)

func next(t *testing.T, lexer *LexerB, want string) {
	var token = lexer.NextToken()
	var got = token.String()
	if got != want {
		t.Errorf("got %q, wanted %q", got, want)
	}
}

func TestString(t *testing.T) {
	str := NewInputStream("a b c 1 2 3")
	lexer := NewLexerB(str)
	next(t, lexer, "[@-1,0:0='a',<1>,1:0]")
	next(t, lexer, "[@-1,1:1=' ',<7>,1:1]")
	next(t, lexer, "[@-1,2:2='b',<1>,1:2]")
	next(t, lexer, "[@-1,3:3=' ',<7>,1:3]")
	next(t, lexer, "[@-1,4:4='c',<1>,1:4]")
	next(t, lexer, "[@-1,5:5=' ',<7>,1:5]")
	next(t, lexer, "[@-1,6:6='1',<2>,1:6]")
	next(t, lexer, "[@-1,7:7=' ',<7>,1:7]")
	next(t, lexer, "[@-1,8:8='2',<2>,1:8]")
	next(t, lexer, "[@-1,9:9=' ',<7>,1:9]")
	next(t, lexer, "[@-1,10:10='3',<2>,1:10]")
	next(t, lexer, "[@-1,11:10='<EOF>',<-1>,1:11]")
}
