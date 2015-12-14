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

var Token = require('./Token').Token
var BufferedTokenStream = require('./BufferedTokenStream').BufferedTokenStream

func CommonTokenStream(lexer, channel) {
	BufferedTokenStream.call(this, lexer)
    this.channel = channel==undefined ? Token.DEFAULT_CHANNEL : channel
    return this
}

CommonTokenStream.prototype = Object.create(BufferedTokenStream.prototype)
CommonTokenStream.prototype.constructor = CommonTokenStream

func (this *CommonTokenStream) adjustSeekIndex(i) {
    return this.nextTokenOnChannel(i, this.channel)
}

func (this *CommonTokenStream) LB(k) {
    if (k==0 || this.index-k<0) {
        return null
    }
    var i = this.index
    var n = 1
    // find k good tokens looking backwards
    while (n <= k) {
        // skip off-channel tokens
        i = this.previousTokenOnChannel(i - 1, this.channel)
        n += 1
    }
    if (i < 0) {
        return null
    }
    return this.tokens[i]
}

func (this *CommonTokenStream) LT(k) {
    this.lazyInit()
    if (k == 0) {
        return null
    }
    if (k < 0) {
        return this.LB(-k)
    }
    var i = this.index
    var n = 1 // we know tokens[pos] is a good one
    // find k good tokens
    while (n < k) {
        // skip off-channel tokens, but make sure to not look past EOF
        if (this.sync(i + 1)) {
            i = this.nextTokenOnChannel(i + 1, this.channel)
        }
        n += 1
    }
    return this.tokens[i]
}

// Count EOF just once.///
func (this *CommonTokenStream) getNumberOfOnChannelTokens() {
    var n = 0
    this.fill()
    for (var i =0 i< this.tokens.lengthi++) {
        var t = this.tokens[i]
        if( t.channel==this.channel) {
            n += 1
        }
        if( t.type==Token.EOF) {
            break
        }
    }
    return n
}

