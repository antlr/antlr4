# [The "BSD license"]
#  Copyright (c) 2012 Terence Parr
#  Copyright (c) 2012 Sam Harwell
#  Copyright (c) 2014 Eric Vergnaud
#  All rights reserved.
#
#  Redistribution and use in source and binary forms, with or without
#  modification, are permitted provided that the following conditions
#  are met:
#
#  1. Redistributions of source code must retain the above copyright
#     notice, self list of conditions and the following disclaimer.
#  2. Redistributions in binary form must reproduce the above copyright
#     notice, self list of conditions and the following disclaimer in the
#     documentation and/or other materials provided with the distribution.
#  3. The name of the author may not be used to endorse or promote products
#     derived from self software without specific prior written permission.
#
#  self SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
#  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
#  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
#  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
#  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
#  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
#  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
#  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
#  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
#  self SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#/

# A lexer is recognizer that draws input symbols from a character stream.
#  lexer grammars result in a subclass of self object. A Lexer object
#  uses simplified match() and error recovery mechanisms in the interest
#  of speed.
#/
from io import StringIO
from antlr4.CommonTokenFactory import CommonTokenFactory
from antlr4.atn.LexerATNSimulator import LexerATNSimulator
from antlr4.InputStream import InputStream
from antlr4.Recognizer import Recognizer
from antlr4.Token import Token
from antlr4.error.Errors import IllegalStateException, LexerNoViableAltException, RecognitionException

class TokenSource(object):

    pass


class Lexer(Recognizer, TokenSource):

    DEFAULT_MODE = 0
    MORE = -2
    SKIP = -3

    DEFAULT_TOKEN_CHANNEL = Token.DEFAULT_CHANNEL
    HIDDEN = Token.HIDDEN_CHANNEL
    MIN_CHAR_VALUE = '\u0000'
    MAX_CHAR_VALUE = '\uFFFE'

    def __init__(self, input:InputStream):
        super().__init__()
        self._input = input
        self._factory = CommonTokenFactory.DEFAULT
        self._tokenFactorySourcePair = (self, input)

        self._interp = None # child classes must populate this
        
        # The goal of all lexer rules/methods is to create a token object.
        #  self is an instance variable as multiple rules may collaborate to
        #  create a single token.  nextToken will return self object after
        #  matching lexer rule(s).  If you subclass to allow multiple token
        #  emissions, then set self to the last token to be matched or
        #  something nonnull so that the auto token emit mechanism will not
        #  emit another token.
        self._token = None

        # What character index in the stream did the current token start at?
        #  Needed, for example, to get the text for current token.  Set at
        #  the start of nextToken.
        self._tokenStartCharIndex = -1

        # The line on which the first character of the token resides#/
        self._tokenStartLine = -1

        # The character position of first character within the line#/
        self._tokenStartColumn = -1

        # Once we see EOF on char stream, next token will be EOF.
        #  If you have DONE : EOF ; then you see DONE EOF.
        self._hitEOF = False

        # The channel number for the current token#/
        self._channel = Token.DEFAULT_CHANNEL

        # The token type for the current token#/
        self._type = Token.INVALID_TYPE

        self._modeStack = []
        self._mode = self.DEFAULT_MODE

        # You can set the text for the current token to override what is in
        #  the input char buffer.  Use setText() or can set self instance var.
        #/
        self._text = None


    def reset(self):
        # wack Lexer state variables
        if self._input is not None:
            self._input.seek(0) # rewind the input
        self._token = None
        self._type = Token.INVALID_TYPE
        self._channel = Token.DEFAULT_CHANNEL
        self._tokenStartCharIndex = -1
        self._tokenStartColumn = -1
        self._tokenStartLine = -1
        self._text = None

        self._hitEOF = False
        self._mode = Lexer.DEFAULT_MODE
        self._modeStack = []

        self._interp.reset()

    # Return a token from self source; i.e., match a token on the char
    #  stream.
    def nextToken(self):
        if self._input is None:
            raise IllegalStateException("nextToken requires a non-null input stream.")

        # Mark start location in char stream so unbuffered streams are
        # guaranteed at least have text of current token
        tokenStartMarker = self._input.mark()
        try:
            while True:
                if self._hitEOF:
                    self.emitEOF()
                    return self._token
                self._token = None
                self._channel = Token.DEFAULT_CHANNEL
                self._tokenStartCharIndex = self._input.index
                self._tokenStartColumn = self._interp.column
                self._tokenStartLine = self._interp.line
                self._text = None
                continueOuter = False
                while True:
                    self._type = Token.INVALID_TYPE
                    ttype = self.SKIP
                    try:
                        ttype = self._interp.match(self._input, self._mode)
                    except LexerNoViableAltException as e:
                        self.notifyListeners(e)		# report error
                        self.recover(e)
                    if self._input.LA(1)==Token.EOF:
                        self._hitEOF = True
                    if self._type == Token.INVALID_TYPE:
                        self._type = ttype
                    if self._type == self.SKIP:
                        continueOuter = True
                        break
                    if self._type!=self.MORE:
                        break
                if continueOuter:
                    continue
                if self._token is None:
                    self.emit()
                return self._token
        finally:
            # make sure we release marker after match or
            # unbuffered char stream will keep buffering
            self._input.release(tokenStartMarker)

    # Instruct the lexer to skip creating a token for current lexer rule
    #  and look for another token.  nextToken() knows to keep looking when
    #  a lexer rule finishes with token set to SKIP_TOKEN.  Recall that
    #  if token==null at end of any token rule, it creates one for you
    #  and emits it.
    #/
    def skip(self):
        self._type = self.SKIP

    def more(self):
        self._type = self.MORE

    def mode(self, m:int):
        self._mode = m

    def pushMode(self, m:int):
        if self._interp.debug:
            print("pushMode " + str(m))
        self._modeStack.append(self._mode)
        self.mode(m)

    def popMode(self):
        if len(self._modeStack)==0:
            raise Exception("Empty Stack")
        if self._interp.debug:
            print("popMode back to "+ self._modeStack[:-1])
        self.mode( self._modeStack.pop() )
        return self._mode

    # Set the char stream and reset the lexer#/
    @property
    def inputStream(self):
        return self._input

    @inputStream.setter
    def inputStream(self, input:InputStream):
        self._input = None
        self._tokenFactorySourcePair = (self, self._input)
        self.reset()
        self._input = input
        self._tokenFactorySourcePair = (self, self._input)

    @property
    def sourceName(self):
        return self._input.sourceName

    # By default does not support multiple emits per nextToken invocation
    #  for efficiency reasons.  Subclass and override self method, nextToken,
    #  and getToken (to push tokens into a list and pull from that list
    #  rather than a single variable as self implementation does).
    #/
    def emitToken(self, token:Token):
        self._token = token

    # The standard method called to automatically emit a token at the
    #  outermost lexical rule.  The token object should point into the
    #  char buffer start..stop.  If there is a text override in 'text',
    #  use that to set the token's text.  Override self method to emit
    #  custom Token objects or provide a new factory.
    #/
    def emit(self):
        t = self._factory.create(self._tokenFactorySourcePair, self._type, self._text, self._channel, self._tokenStartCharIndex,
                                 self.getCharIndex()-1, self._tokenStartLine, self._tokenStartColumn)
        self.emitToken(t)
        return t

    def emitEOF(self):
        cpos = self.column
        lpos = self.line
        eof = self._factory.create(self._tokenFactorySourcePair, Token.EOF, None, Token.DEFAULT_CHANNEL, self._input.index,
                                   self._input.index-1, lpos, cpos)
        self.emitToken(eof)
        return eof

    @property
    def type(self):
        return self._type

    @type.setter
    def type(self, type:int):
        self._type = type

    @property
    def line(self):
        return self._interp.line

    @line.setter
    def line(self, line:int):
        self._interp.line = line

    @property
    def column(self):
        return self._interp.column

    @column.setter
    def column(self, column:int):
        self._interp.column = column

    # What is the index of the current character of lookahead?#/
    def getCharIndex(self):
        return self._input.index

    # Return the text matched so far for the current token or any
    #  text override.
    @property
    def text(self):
        if self._text is not None:
            return self._text
        else:
            return self._interp.getText(self._input)

    # Set the complete text of self token; it wipes any previous
    #  changes to the text.
    @text.setter
    def text(self, txt:str):
        self._text = txt

    # Return a list of all Token objects in input char stream.
    #  Forces load of all tokens. Does not include EOF token.
    #/
    def getAllTokens(self):
        tokens = []
        t = self.nextToken()
        while t.type!=Token.EOF:
            tokens.append(t)
            t = self.nextToken()
        return tokens

    def notifyListeners(self, e:LexerNoViableAltException):
        start = self._tokenStartCharIndex
        stop = self._input.index
        text = self._input.getText(start, stop)
        msg = "token recognition error at: '" + self.getErrorDisplay(text) + "'"
        listener = self.getErrorListenerDispatch()
        listener.syntaxError(self, None, self._tokenStartLine, self._tokenStartColumn, msg, e)

    def getErrorDisplay(self, s:str):
        with StringIO() as buf:
            for c in s:
                buf.write(self.getErrorDisplayForChar(c))
            return buf.getvalue()

    def getErrorDisplayForChar(self, c:str):
        if ord(c[0])==Token.EOF:
            return "<EOF>"
        elif c=='\n':
            return "\\n"
        elif c=='\t':
            return "\\t"
        elif c=='\r':
            return "\\r"
        else:
            return c

    def getCharErrorDisplay(self, c:str):
        return "'" + self.getErrorDisplayForChar(c) + "'"

    # Lexers can normally match any char in it's vocabulary after matching
    #  a token, so do the easy thing and just kill a character and hope
    #  it all works out.  You can instead use the rule invocation stack
    #  to do sophisticated error recovery if you are in a fragment rule.
    #/
    def recover(self, re:RecognitionException):
        if self._input.LA(1) != Token.EOF:
            if isinstance(re, LexerNoViableAltException):
                    # skip a char and try again
                    self._interp.consume(self._input)
            else:
                # TODO: Do we lose character or line position information?
                self._input.consume()

