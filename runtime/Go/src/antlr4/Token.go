package antlr4

// A token has properties: text, type, line, character position in the line
// (so we can ignore tabs), token channel, index, and source from which
// we obtained this token.

type Token struct {
	source *TokenSource
	tokenType int // token type of the token
	channel int // The parser ignores everything not on DEFAULT_CHANNEL
	start int // optional return -1 if not implemented.
	stop int // optional return -1 if not implemented.
	tokenIndex int // from 0..n-1 of the token object in the input stream
	line int // line=1..n of the 1st character
	column int // beginning of the line at which it occurs, 0..n-1
	text string // text of the token.
}

func NewToken() *Token {
	return new(Token)
}

const (
	TokenInvalidType = 0

	// During lookahead operations, this "token" signifies we hit rule end ATN state
	// and did not follow it despite needing to.
	TokenEpsilon = -2

	TokenMinUserTokenType = 1

	TokenEOF = -1

	// All tokens go to the parser (unless skip() is called in that rule)
	// on a particular "channel". The parser tunes to a particular channel
	// so that whitespace etc... can go to the parser on a "hidden" channel.

	TokenDefaultChannel = 0

	// Anything on different channel than DEFAULT_CHANNEL is not parsed
	// by parser.

	TokenHiddenChannel = 1
)

// Explicitly set the text for this token. If {code text} is not
// {@code nil}, then {@link //getText} will return this value rather than
// extracting the text from the input.
//
// @param text The explicit text of the token, or {@code nil} if the text
// should be obtained from the input along with the start and stop indexes
// of the token.

//
//
//Object.defineProperty(Token.prototype, "text", {
//	get : function() {
//		return this._text
//	},
//	set : function(text) {
//		this._text = text
//	}
//})

func (this *Token) getTokenSource() {
	return this.source[0]
}

func (this *Token) getInputStream() {
	return this.source[1]
}

type CommonToken struct {
	Token
}

func NewCommonToken(source *InputStream, tokenType int, channel, start int, stop int) *CommonToken {

	t := NewToken()

	t.source = source
	t.tokenType = -1
	t.channel = channel
	t.start = start
	t.stop = stop
	t.tokenIndex = -1
	if (t.source[0] != nil) {
		t.line = source[0].line
		t.column = source[0].column
	} else {
		t.column = -1
	}
	return t
}

// An empty {@link Pair} which is used as the default value of
// {@link //source} for tokens that do not have a source.

//CommonToken.EMPTY_SOURCE = [ nil, nil ]

// Constructs a New{@link CommonToken} as a copy of another {@link Token}.
//
// <p>
// If {@code oldToken} is also a {@link CommonToken} instance, the newly
// constructed token will share a reference to the {@link //text} field and
// the {@link Pair} stored in {@link //source}. Otherwise, {@link //text} will
// be assigned the result of calling {@link //getText}, and {@link //source}
// will be constructed from the result of {@link Token//getTokenSource} and
// {@link Token//getInputStream}.</p>
//
// @param oldToken The token to copy.
//
func (ct *CommonToken) clone() {
	var t = NewCommonToken(ct.source, ct.tokenType, ct.channel, ct.start,
			ct.stop)
	t.tokenIndex = ct.tokenIndex
	t.line = ct.line
	t.column = ct.column
	t.text = ct.text
	return t
}

Object.defineProperty(CommonToken.prototype, "text", {
	get : function() {
		if (this._text != nil) {
			return this._text
		}
		var input = this.getInputStream()
		if (input == nil) {
			return nil
		}
		var n = input.size
		if (this.start < n && this.stop < n) {
			return input.getText(this.start, this.stop)
		} else {
			return "<EOF>"
		}
	},
	set : function(text) {
		this._text = text
	}
})

func (this *CommonToken) toString() string {
	var txt = this.text
	if (txt != nil) {
		txt = txt.replace(/\n/g, "\\n").replace(/\r/g, "\\r").replace(/\t/g, "\\t")
	} else {
		txt = "<no text>"
	}
	return "[@" + this.tokenIndex + "," + this.start + ":" + this.stop + "='" +
			txt + "',<" + this.tokenType + ">" +
			(this.channel > 0 ? ",channel=" + this.channel : "") + "," +
			this.line + ":" + this.column + "]"
}



