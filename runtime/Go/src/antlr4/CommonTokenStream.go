//
// This class extends {@link BufferedTokenStream} with functionality to filter
// token streams to tokens on a particular channel (tokens where
// {@link Token//getChannel} returns a particular value).
//
// <p>
// This token stream provides access to all tokens by index or when calling
// methods like {@link //getText}. The channel filtering is only used for code
// accessing tokens via the lookahead methods {@link //LA}, {@link //LT}, and
// {@link //LB}.</p>
//
// <p>
// By default, tokens are placed on the default channel
// ({@link Token//DEFAULT_CHANNEL}), but may be reassigned by using the
// {@code ->channel(HIDDEN)} lexer command, or by using an embedded action to
// call {@link Lexer//setChannel}.
// </p>
//
// <p>
// Note: lexer rules which use the {@code ->skip} lexer command or call
// {@link Lexer//skip} do not produce tokens at all, so input text matched by
// such a rule will not be available as part of the token stream, regardless of
// channel.</p>
///

package antlr

type CommonTokenStream struct {
    BufferedTokenStream
}

func NewCommonTokenStream(lexer Lexer, channel) {

    ts := new(BufferedTokenStream)

	BufferedTokenStream.call(ts, lexer)
    ts.channel = channel
    return ts
}

func (ts *CommonTokenStream) adjustSeekIndex(i int) {
    return ts.nextTokenOnChannel(i, ts.channel)
}

func (ts *CommonTokenStream) LB(k int) {
    if (k==0 || ts.index-k<0) {
        return nil
    }
    var i = ts.index
    var n = 1
    // find k good tokens looking backwards
    for (n <= k) {
        // skip off-channel tokens
        i = ts.previousTokenOnChannel(i - 1, ts.channel)
        n += 1
    }
    if (i < 0) {
        return nil
    }
    return ts.tokens[i]
}

func (ts *CommonTokenStream) LT(k int) {
    ts.lazyInit()
    if (k == 0) {
        return nil
    }
    if (k < 0) {
        return ts.LB(-k)
    }
    var i = ts.index
    var n = 1 // we know tokens[pos] is a good one
    // find k good tokens
    for n < k {
        // skip off-channel tokens, but make sure to not look past EOF
        if (ts.sync(i + 1)) {
            i = ts.nextTokenOnChannel(i + 1, ts.channel)
        }
        n += 1
    }
    return ts.tokens[i]
}

// Count EOF just once.///
func (ts *CommonTokenStream) getNumberOfOnChannelTokens() {
    var n = 0
    ts.fill()
    for i := 0; i < ts.tokens.length; i++ {
        var t = ts.tokens[i]
        if  t.channel==ts.channel {
            n += 1
        }
        if  t.type==Token.EOF {
            break
        }
    }
    return n
}

