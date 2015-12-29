// This implementation of {@link TokenStream} loads tokens from a
// {@link TokenSource} on-demand, and places the tokens in a buffer to provide
// access to any previous token by index.
//
// <p>
// This token stream ignores the value of {@link Token//getChannel}. If your
// parser requires the token stream filter tokens to only those on a particular
// channel, such as {@link Token//DEFAULT_CHANNEL} or
// {@link Token//HIDDEN_CHANNEL}, use a filtering token stream such a
// {@link CommonTokenStream}.</p>

package antlr4

import (
	"strconv"
	"fmt"
)

// bt is just to keep meaningful parameter types to Parser
type BufferedTokenStream struct {
	tokenSource TokenSource

	tokens     []IToken
	index      int
	fetchedEOF bool
	channel    int
}

func NewBufferedTokenStream(tokenSource TokenSource) *BufferedTokenStream {

	ts := new(BufferedTokenStream)

	// The {@link TokenSource} from which tokens for bt stream are fetched.
	ts.tokenSource = tokenSource

	// A collection of all tokens fetched from the token source. The list is
	// considered a complete view of the input once {@link //fetchedEOF} is set
	// to {@code true}.
	ts.tokens = make([]IToken, 0)

	// The index into {@link //tokens} of the current token (next token to
	// {@link //consume}). {@link //tokens}{@code [}{@link //p}{@code ]} should
	// be
	// {@link //LT LT(1)}.
	//
	// <p>This field is set to -1 when the stream is first constructed or when
	// {@link //SetTokenSource} is called, indicating that the first token has
	// not yet been fetched from the token source. For additional information,
	// see the documentation of {@link IntStream} for a description of
	// Initializing Methods.</p>
	ts.index = -1

	// Indicates whether the {@link Token//EOF} token has been fetched from
	// {@link //tokenSource} and added to {@link //tokens}. This field improves
	// performance for the following cases:
	//
	// <ul>
	// <li>{@link //consume}: The lookahead check in {@link //consume} to
	// prevent
	// consuming the EOF symbol is optimized by checking the values of
	// {@link //fetchedEOF} and {@link //p} instead of calling {@link
	// //LA}.</li>
	// <li>{@link //fetch}: The check to prevent adding multiple EOF symbols
	// into
	// {@link //tokens} is trivial with bt field.</li>
	// <ul>
	ts.fetchedEOF = false

	return ts
}

func (bt *BufferedTokenStream) Mark() int {
	return 0
}

func (bt *BufferedTokenStream) Release(marker int) {
	// no resources to release
}

func (bt *BufferedTokenStream) reset() {
	bt.Seek(0)
}

func (bt *BufferedTokenStream) Seek(index int) {
	bt.lazyInit()
	bt.index = bt.adjustSeekIndex(index)
}

func (bt *BufferedTokenStream) Get(index int) IToken {
	bt.lazyInit()
	return bt.tokens[index]
}

func (bt *BufferedTokenStream) Consume() {
	var skipEofCheck = false
	if bt.index >= 0 {
		if bt.fetchedEOF {
			// the last token in tokens is EOF. skip check if p indexes any
			// fetched token except the last.
			skipEofCheck = bt.index < len(bt.tokens)-1
		} else {
			// no EOF token in tokens. skip check if p indexes a fetched token.
			skipEofCheck = bt.index < len(bt.tokens)
		}
	} else {
		// not yet initialized
		skipEofCheck = false
	}

	fmt.Println("Consume 1")
	if !skipEofCheck && bt.LA(1) == TokenEOF {
		panic("cannot consume EOF")
	}
	if bt.Sync(bt.index + 1) {
		fmt.Println("Consume 2")
		bt.index = bt.adjustSeekIndex(bt.index + 1)
	}
}

// Make sure index {@code i} in tokens has a token.
//
// @return {@code true} if a token is located at index {@code i}, otherwise
// {@code false}.
// @see //Get(int i)
// /
func (bt *BufferedTokenStream) Sync(i int) bool {
	var n = i - len(bt.tokens) + 1 // how many more elements we need?
	if n > 0 {
		var fetched = bt.fetch(n)
		fmt.Println("Sync done")
		return fetched >= n
	}
	return true
}

// Add {@code n} elements to buffer.
//
// @return The actual number of elements added to the buffer.
// /
func (bt *BufferedTokenStream) fetch(n int) int {
	if bt.fetchedEOF {
		return 0
	}

	for i := 0; i < n; i++ {
		var t IToken = bt.tokenSource.nextToken()
		fmt.Println("fetch loop")
		t.SetTokenIndex( len(bt.tokens) )
		bt.tokens = append(bt.tokens, t)
		if t.GetTokenType() == TokenEOF {
			bt.fetchedEOF = true
			return i + 1
		}
	}

	fmt.Println("fetch done")
	return n
}

// Get all tokens from start..stop inclusively///
func (bt *BufferedTokenStream) GetTokens(start int, stop int, types *IntervalSet) []IToken {

	if start < 0 || stop < 0 {
		return nil
	}
	bt.lazyInit()
	var subset = make([]IToken, 0)
	if stop >= len(bt.tokens) {
		stop = len(bt.tokens) - 1
	}
	for i := start; i < stop; i++ {
		var t = bt.tokens[i]
		if t.GetTokenType() == TokenEOF {
			break
		}
		if types == nil || types.contains(t.GetTokenType()) {
			subset = append(subset, t)
		}
	}
	return subset
}

func (bt *BufferedTokenStream) LA(i int) int {
	return bt.LT(i).GetTokenType()
}

func (bt *BufferedTokenStream) LB(k int) IToken {
	if bt.index-k < 0 {
		return nil
	}
	return bt.tokens[bt.index-k]
}

func (bt *BufferedTokenStream) LT(k int) IToken {
	bt.lazyInit()
	if k == 0 {
		return nil
	}
	if k < 0 {
		return bt.LB(-k)
	}
	var i = bt.index + k - 1
	bt.Sync(i)
	if i >= len(bt.tokens) { // return EOF token
		// EOF must be last token
		return bt.tokens[len(bt.tokens)-1]
	}
	return bt.tokens[i]
}

// Allowed derived classes to modify the behavior of operations which change
// the current stream position by adjusting the target token index of a seek
// operation. The default implementation simply returns {@code i}. If an
// exception is panic(n in bt method, the current stream index should not be
// changed.
//
// <p>For example, {@link CommonTokenStream} overrides bt method to ensure
// that
// the seek target is always an on-channel token.</p>
//
// @param i The target token index.
// @return The adjusted target token index.

func (bt *BufferedTokenStream) adjustSeekIndex(i int) int {
	return i
}

func (bt *BufferedTokenStream) lazyInit() {
	if bt.index == -1 {
		bt.setup()
	}
}

func (bt *BufferedTokenStream) setup() {
	bt.Sync(0)
	bt.index = bt.adjustSeekIndex(0)
}

func (bt *BufferedTokenStream) GetTokenSource() TokenSource {
	return bt.tokenSource
}

// Reset bt token stream by setting its token source.///
func (bt *BufferedTokenStream) SetTokenSource(tokenSource TokenSource) {
	bt.tokenSource = tokenSource
	bt.tokens = make([]IToken, 0)
	bt.index = -1
}

// Given a starting index, return the index of the next token on channel.
// Return i if tokens[i] is on channel. Return -1 if there are no tokens
// on channel between i and EOF.
// /
func (bt *BufferedTokenStream) nextTokenOnChannel(i, channel int) int {
	bt.Sync(i)
	if i >= len(bt.tokens) {
		return -1
	}
	var token = bt.tokens[i]
	for token.GetChannel() != bt.channel {
		if token.GetTokenType() == TokenEOF {
			return -1
		}
		i += 1
		bt.Sync(i)
		token = bt.tokens[i]
	}
	return i
}

// Given a starting index, return the index of the previous token on channel.
// Return i if tokens[i] is on channel. Return -1 if there are no tokens
// on channel between i and 0.
func (bt *BufferedTokenStream) previousTokenOnChannel(i, channel int) int {
	for i >= 0 && bt.tokens[i].GetChannel() != channel {
		i -= 1
	}
	return i
}

// Collect all tokens on specified channel to the right of
// the current token up until we see a token on DEFAULT_TOKEN_CHANNEL or
// EOF. If channel is -1, find any non default channel token.
func (bt *BufferedTokenStream) getHiddenTokensToRight(tokenIndex, channel int) []IToken {
	bt.lazyInit()
	if tokenIndex < 0 || tokenIndex >= len(bt.tokens) {
		panic(strconv.Itoa(tokenIndex) + " not in 0.." + strconv.Itoa(len(bt.tokens)-1))
	}
	var nextOnChannel = bt.nextTokenOnChannel(tokenIndex+1, LexerDefaultTokenChannel)
	var from_ = tokenIndex + 1
	// if none onchannel to right, nextOnChannel=-1 so set to = last token
	var to int
	if nextOnChannel == -1 {
		to = len(bt.tokens) - 1
	} else {
		to = nextOnChannel
	}
	return bt.filterForChannel(from_, to, channel)
}

// Collect all tokens on specified channel to the left of
// the current token up until we see a token on DEFAULT_TOKEN_CHANNEL.
// If channel is -1, find any non default channel token.
func (bt *BufferedTokenStream) getHiddenTokensToLeft(tokenIndex, channel int) []IToken {
	bt.lazyInit()
	if tokenIndex < 0 || tokenIndex >= len(bt.tokens) {
		panic(strconv.Itoa(tokenIndex) + " not in 0.." + strconv.Itoa(len(bt.tokens)-1))
	}
	var prevOnChannel = bt.previousTokenOnChannel(tokenIndex-1, LexerDefaultTokenChannel)
	if prevOnChannel == tokenIndex-1 {
		return nil
	}
	// if none on channel to left, prevOnChannel=-1 then from=0
	var from_ = prevOnChannel + 1
	var to = tokenIndex - 1
	return bt.filterForChannel(from_, to, channel)
}

func (bt *BufferedTokenStream) filterForChannel(left, right, channel int) []IToken {
	var hidden = make([]IToken, 0)
	for i := left; i < right+1; i++ {
		var t = bt.tokens[i]
		if channel == -1 {
			if t.GetChannel() != LexerDefaultTokenChannel {
				hidden = append(hidden, t)
			}
		} else if t.GetChannel() == channel {
			hidden = append(hidden, t)
		}
	}
	if len(hidden) == 0 {
		return nil
	}
	return hidden
}

func (bt *BufferedTokenStream) GetSourceName() string {
	return bt.tokenSource.GetSourceName()
}

func (bt *BufferedTokenStream) Size() int {
	return len(bt.tokens)
}

func (bt *BufferedTokenStream) Index() int {
	return bt.index
}

func (bt *BufferedTokenStream) GetAllText() string {
	return bt.GetTextFromInterval(nil)
}

func (bt *BufferedTokenStream) GetTextFromTokens(start, end IToken) string {
	return bt.GetTextFromInterval(NewInterval(start.GetTokenIndex(), end.GetTokenIndex()))
}

func (bt *BufferedTokenStream) GetTextFromRuleContext(interval IRuleContext) string {
	return bt.GetTextFromInterval(interval.GetSourceInterval())
}

func (bt *BufferedTokenStream) GetTextFromInterval(interval *Interval) string {

	bt.lazyInit()
	bt.fill()
	if interval == nil {
		interval = NewInterval(0, len(bt.tokens)-1)
	}

	var start = interval.start
	var stop = interval.stop
	if start < 0 || stop < 0 {
		return ""
	}
	if stop >= len(bt.tokens) {
		stop = len(bt.tokens) - 1
	}

	var s = ""
	for i := start; i < stop+1; i++ {
		var t = bt.tokens[i]
		if t.GetTokenType() == TokenEOF {
			break
		}
		s += t.GetText()
	}

	return s
}

// Get all tokens from lexer until EOF///
func (bt *BufferedTokenStream) fill() {
	bt.lazyInit()
	for bt.fetch(1000) == 1000 {
		continue
	}
}
