//
// This default implementation of {@link TokenFactory} creates
// {@link CommonToken} objects.
//

package antlr4

type TokenFactory interface {
	Create(source *TokenSourceCharStreamPair, ttype int, text string, channel, start, stop, line, column int) *Token
}

type CommonTokenFactory struct {
	copyText bool
}

func NewCommonTokenFactory(copyText bool) *CommonTokenFactory {

	tf := new(CommonTokenFactory)

	// Indicates whether {@link CommonToken//setText} should be called after
	// constructing tokens to explicitly set the text. This is useful for cases
	// where the input stream might not be able to provide arbitrary substrings
	// of text from the input after the lexer creates a token (e.g. the
	// implementation of {@link CharStream//GetText} in
	// {@link UnbufferedCharStream} panics an
	// {@link UnsupportedOperationException}). Explicitly setting the token text
	// allows {@link Token//GetText} to be called at any time regardless of the
	// input stream implementation.
	//
	// <p>
	// The default value is {@code false} to avoid the performance and memory
	// overhead of copying text for every token unless explicitly requested.</p>
	//
	tf.copyText = copyText

	return tf
}

//
// The default {@link CommonTokenFactory} instance.
//
// <p>
// This token factory does not explicitly copy token text when constructing
// tokens.</p>
//
var CommonTokenFactoryDEFAULT = NewCommonTokenFactory(false)

func (this *CommonTokenFactory) Create(source *TokenSourceCharStreamPair, ttype int, text string, channel, start, stop, line, column int) *Token {
	var t = NewCommonToken(source, ttype, channel, start, stop)
	t.line = line
	t.column = column
	if text != "" {
		t.setText(text)
	} else if this.copyText && source.charStream != nil {
		t.setText(source.charStream.GetTextFromInterval(NewInterval(start, stop)))
	}
	return t.Token
}

func (this *CommonTokenFactory) createThin(ttype int, text string) *Token {
	var t = NewCommonToken(nil, ttype, TokenDefaultChannel, -1, -1)
	t.setText(text)
	return t.Token
}
