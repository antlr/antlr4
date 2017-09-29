package antlr

import (
	"github.com/kylelemons/godebug/pretty"
	"testing"
)

func TestCaseChangingStream(t *testing.T) {
	tests := []struct {
		input string
		upper bool
		want  []int
	}{
		{"abcd", true, []int{'A', 'B', 'C', 'D', TokenEOF}},
		{"ABCD", true, []int{'A', 'B', 'C', 'D', TokenEOF}},
		{"abcd", false, []int{'a', 'b', 'c', 'd', TokenEOF}},
		{"ABCD", false, []int{'a', 'b', 'c', 'd', TokenEOF}},
		{"", false, []int{TokenEOF}},
	}

	for _, test := range tests {
		var got []int
		is := NewCaseChangingStream(NewInputStream(test.input), test.upper)
		for i := 1; i <= is.Size()+1; i++ {
			got = append(got, is.LA(i))
		}

		if diff := pretty.Compare(test.want, got); diff != "" {
			t.Errorf("NewCaseChangingStream(%q, %v) diff: (-got +want)\n%s", test.input, test.upper, diff)
		}
	}
}
