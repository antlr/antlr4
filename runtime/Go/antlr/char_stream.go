// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

// CharStream extends IntStream to be able to send text instead of just ints.
type CharStream interface {
	IntStream
	GetText(int, int) string
	GetTextFromTokens(start, end Token) string
	GetTextFromInterval(*Interval) string
}
