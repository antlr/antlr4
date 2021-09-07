/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.IO;
using System.Collections.Generic;
using System.Text;
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
    /// <summary>A lexer is recognizer that draws input symbols from a character stream.</summary>
    /// <remarks>
    /// A lexer is recognizer that draws input symbols from a character stream.
    /// lexer grammars result in a subclass of this object. A Lexer object
    /// uses simplified match() and error recovery mechanisms in the interest
    /// of speed.
    /// </remarks>
    public abstract class Lexer : Recognizer<int, LexerATNSimulator>, ITokenSource
    {
        public const int DEFAULT_MODE = 0;

        public const int DefaultTokenChannel = TokenConstants.DefaultChannel;

        public const int Hidden = TokenConstants.HiddenChannel;

        public const int MinCharValue = 0x0000;

        public const int MaxCharValue = 0x10FFFF;

        private ICharStream _input;

        protected readonly TextWriter Output;

        protected readonly TextWriter ErrorOutput;

		private Tuple<ITokenSource, ICharStream> _tokenFactorySourcePair;

        /// <summary>How to create token objects</summary>
		private ITokenFactory _factory = CommonTokenFactory.Default;

        /// <summary>The goal of all lexer rules/methods is to create a token object.</summary>
        /// <remarks>
        /// The goal of all lexer rules/methods is to create a token object.
        /// This is an instance variable as multiple rules may collaborate to
        /// create a single token.  nextToken will return this object after
        /// matching lexer rule(s).  If you subclass to allow multiple token
        /// emissions, then set this to the last token to be matched or
        /// something nonnull so that the auto token emit mechanism will not
        /// emit another token.
        /// </remarks>
        private IToken _token;

        /// <summary>
        /// What character index in the stream did the current token start at?
        /// Needed, for example, to get the text for current token.
        /// </summary>
        /// <remarks>
        /// What character index in the stream did the current token start at?
        /// Needed, for example, to get the text for current token.  Set at
        /// the start of nextToken.
        /// </remarks>
        private int _tokenStartCharIndex = -1;

        /// <summary>The line on which the first character of the token resides</summary>
		private int _tokenStartLine;

        /// <summary>The character position of first character within the line</summary>
		private int _tokenStartColumn;

        /// <summary>Once we see EOF on char stream, next token will be EOF.</summary>
        /// <remarks>
        /// Once we see EOF on char stream, next token will be EOF.
        /// If you have DONE : EOF ; then you see DONE EOF.
        /// </remarks>
		private bool _hitEOF;

        /// <summary>The channel number for the current token</summary>
		private int _channel;

        /// <summary>The token type for the current token</summary>
		private int _type;

        private readonly Stack<int> _modeStack = new Stack<int>();

		private int _mode = Antlr4.Runtime.Lexer.DEFAULT_MODE;

        /// <summary>
        /// You can set the text for the current token to override what is in
        /// the input char buffer.
        /// </summary>
        /// <remarks>
        /// You can set the text for the current token to override what is in
        /// the input char buffer.  Use setText() or can set this instance var.
        /// </remarks>
		private string _text;

        public Lexer(ICharStream input) : this(input, Console.Out, Console.Error) { }

        public Lexer(ICharStream input, TextWriter output, TextWriter errorOutput)
        {
            this._input = input;
            this.Output = output;
            this.ErrorOutput = errorOutput;
            this._tokenFactorySourcePair = Tuple.Create((ITokenSource)this, input);
        }

        public virtual void Reset()
        {
            // wack Lexer state variables
            if (_input != null)
            {
                _input.Seek(0);
            }
            // rewind the input
            _token = null;
            _type = TokenConstants.InvalidType;
            _channel = TokenConstants.DefaultChannel;
            _tokenStartCharIndex = -1;
            _tokenStartColumn = -1;
            _tokenStartLine = -1;
            _text = null;
            _hitEOF = false;
            _mode = Antlr4.Runtime.Lexer.DEFAULT_MODE;
            _modeStack.Clear();
            Interpreter.Reset();
        }

        /// <summary>
        /// Return a token from this source; i.e., match a token on the char
        /// stream.
        /// </summary>
        /// <remarks>
        /// Return a token from this source; i.e., match a token on the char
        /// stream.
        /// </remarks>
        public virtual IToken NextToken()
        {
            if (_input == null)
            {
                throw new InvalidOperationException("nextToken requires a non-null input stream.");
            }
            // Mark start location in char stream so unbuffered streams are
            // guaranteed at least have text of current token
            int tokenStartMarker = _input.Mark();
            try
            {
                while (true)
                {
                    if (_hitEOF)
                    {
                        EmitEOF();
                        return _token;
                    }
                    _token = null;
                    _channel = TokenConstants.DefaultChannel;
                    _tokenStartCharIndex = _input.Index;
                    _tokenStartColumn = Interpreter.Column;
                    _tokenStartLine = Interpreter.Line;
                    _text = null;
                    do
                    {
                        _type = TokenConstants.InvalidType;
                        //				System.out.println("nextToken line "+tokenStartLine+" at "+((char)input.LA(1))+
                        //								   " in mode "+mode+
                        //								   " at index "+input.index());
                        int ttype;
                        try
                        {
                            ttype = Interpreter.Match(_input, _mode);
                        }
                        catch (LexerNoViableAltException e)
                        {
                            NotifyListeners(e);
                            // report error
                            Recover(e);
                            ttype = TokenTypes.Skip;
                        }
                        if (_input.LA(1) == IntStreamConstants.EOF)
                        {
                            _hitEOF = true;
                        }
                        if (_type == TokenConstants.InvalidType)
                        {
                            _type = ttype;
                        }
                        if (_type == TokenTypes.Skip)
                        {
                            goto outer_continue;
                        }
                    }
                    while (_type == TokenTypes.More);
                    if (_token == null)
                    {
                        Emit();
                    }
                    return _token;
outer_continue: ;
                }
            }
            finally
            {
                // make sure we release marker after match or
                // unbuffered char stream will keep buffering
                _input.Release(tokenStartMarker);
            }
        }

        /// <summary>
        /// Instruct the lexer to skip creating a token for current lexer rule
        /// and look for another token.
        /// </summary>
        /// <remarks>
        /// Instruct the lexer to skip creating a token for current lexer rule
        /// and look for another token.  nextToken() knows to keep looking when
        /// a lexer rule finishes with token set to SKIP_TOKEN.  Recall that
        /// if token==null at end of any token rule, it creates one for you
        /// and emits it.
        /// </remarks>
        public virtual void Skip()
        {
            _type = TokenTypes.Skip;
        }

        public virtual void More()
        {
            _type = TokenTypes.More;
        }

        public virtual void Mode(int m)
        {
            _mode = m;
        }

        public virtual void PushMode(int m)
        {
            _modeStack.Push(_mode);
            Mode(m);
        }

        public virtual int PopMode()
        {
            if (_modeStack.Count == 0)
            {
                throw new InvalidOperationException();
            }

            int mode = _modeStack.Pop();
            Mode(mode);
            return _mode;
        }

        public virtual ITokenFactory TokenFactory
        {
            get
            {
                return _factory;
            }
            set
            {
                ITokenFactory factory = value;
                this._factory = factory;
            }
        }

        /// <summary>Set the char stream and reset the lexer</summary>
        public virtual void SetInputStream(ICharStream input)
        {
            this._input = null;
            this._tokenFactorySourcePair = Tuple.Create((ITokenSource)this, _input);
            Reset();
            this._input = input;
            this._tokenFactorySourcePair = Tuple.Create((ITokenSource)this, _input);
        }

        public virtual string SourceName
        {
            get
            {
                return _input.SourceName;
            }
        }

        public override IIntStream InputStream
        {
            get
            {
                return _input;
            }
        }

        ICharStream ITokenSource.InputStream
        {
            get
            {
				return _input;
            }
        }

        /// <summary>
        /// By default does not support multiple emits per nextToken invocation
        /// for efficiency reasons.
        /// </summary>
        /// <remarks>
        /// By default does not support multiple emits per nextToken invocation
        /// for efficiency reasons.  Subclass and override this method, nextToken,
        /// and getToken (to push tokens into a list and pull from that list
        /// rather than a single variable as this implementation does).
        /// </remarks>
        public virtual void Emit(IToken token)
        {
            //System.err.println("emit "+token);
            this._token = token;
        }

        /// <summary>
        /// The standard method called to automatically emit a token at the
        /// outermost lexical rule.
        /// </summary>
        /// <remarks>
        /// The standard method called to automatically emit a token at the
        /// outermost lexical rule.  The token object should point into the
        /// char buffer start..stop.  If there is a text override in 'text',
        /// use that to set the token's text.  Override this method to emit
        /// custom Token objects or provide a new factory.
        /// </remarks>
        public virtual IToken Emit()
        {
            IToken t = _factory.Create(_tokenFactorySourcePair, _type, _text, _channel, _tokenStartCharIndex, CharIndex - 1, _tokenStartLine, _tokenStartColumn);
            Emit(t);
            return t;
        }

        public virtual IToken EmitEOF()
        {
            int cpos = Column;
			int line = Line;
            IToken eof = _factory.Create(_tokenFactorySourcePair, TokenConstants.EOF, null, TokenConstants.DefaultChannel, _input.Index, _input.Index - 1, line, cpos);
            Emit(eof);
            return eof;
        }

        public virtual int Line
        {
            get
            {
                return Interpreter.Line;
            }
            set
            {
                int line = value;
                Interpreter.Line = line;
            }
        }

        public virtual int Column
        {
            get
            {
                return Interpreter.Column;
            }
            set
            {
                int charPositionInLine = value;
                Interpreter.Column = charPositionInLine;
            }
        }

        /// <summary>What is the index of the current character of lookahead?</summary>
        public virtual int CharIndex
        {
            get
            {
                return _input.Index;
            }
        }

		public virtual int TokenStartCharIndex
		{
			get
			{
				return _tokenStartCharIndex;
			}
		}

		public virtual int TokenStartLine
		{
			get
			{
				return _tokenStartLine;
			}
		}

		public virtual int TokenStartColumn
		{
			get
			{
				return _tokenStartColumn;
			}
		}

        /// <summary>
        /// Return the text matched so far for the current token or any text
        /// override.
        /// </summary>
        /// <remarks>
        /// Return the text matched so far for the current token or any text
        /// override.
        /// </remarks>
        /// <summary>
        /// Set the complete text of this token; it wipes any previous changes to the
        /// text.
        /// </summary>
        /// <remarks>
        /// Set the complete text of this token; it wipes any previous changes to the
        /// text.
        /// </remarks>
        public virtual string Text
        {
            get
            {
                if (_text != null)
                {
                    return _text;
                }
                return Interpreter.GetText(_input);
            }
            set
            {
                string text = value;
                this._text = text;
            }
        }

        /// <summary>Override if emitting multiple tokens.</summary>
        /// <remarks>Override if emitting multiple tokens.</remarks>
        public virtual IToken Token
        {
            get
            {
                return _token;
            }
            set
            {
                IToken _token = value;
                this._token = _token;
            }
        }

        public virtual int Type
        {
            get
            {
                return _type;
            }
            set
            {
                int ttype = value;
                _type = ttype;
            }
        }

        public virtual int Channel
        {
            get
            {
                return _channel;
            }
            set
            {
                int channel = value;
                _channel = channel;
            }
        }

        public virtual Stack<int> ModeStack
        {
            get
            {
                return _modeStack;
            }
        }

        public virtual int CurrentMode
        {
            get
            {
                return _mode;
            }
            set
            {
                int mode = value;
                _mode = mode;
            }
        }

        public virtual bool HitEOF
        {
            get
            {
                return _hitEOF;
            }
            set
            {
                bool hitEOF = value;
                _hitEOF = hitEOF;
            }
        }

        public virtual string[] ChannelNames
        {
            get
            {
                return null;
            }
        }

        public virtual string[] ModeNames
        {
            get
            {
                return null;
            }
        }

        /// <summary>Return a list of all Token objects in input char stream.</summary>
        /// <remarks>
        /// Return a list of all Token objects in input char stream.
        /// Forces load of all tokens. Does not include EOF token.
        /// </remarks>
        public virtual IList<IToken> GetAllTokens()
        {
            IList<IToken> tokens = new List<IToken>();
            IToken t = NextToken();
            while (t.Type != TokenConstants.EOF)
            {
                tokens.Add(t);
                t = NextToken();
            }
            return tokens;
        }

        public virtual void Recover(LexerNoViableAltException e)
        {
            if (_input.LA(1) != IntStreamConstants.EOF)
            {
                // skip a char and try again
                Interpreter.Consume(_input);
            }
        }

        public virtual void NotifyListeners(LexerNoViableAltException e)
        {
            string text = _input.GetText(Interval.Of(_tokenStartCharIndex, _input.Index));
            string msg = "token recognition error at: '" + GetErrorDisplay(text) + "'";
            IAntlrErrorListener<int> listener = ErrorListenerDispatch;
            listener.SyntaxError(ErrorOutput, this, 0, _tokenStartLine, _tokenStartColumn, msg, e);
        }

        public virtual string GetErrorDisplay(string s)
        {
            StringBuilder buf = new StringBuilder();
            for (var i = 0; i < s.Length; ) {
                var codePoint = Char.ConvertToUtf32(s, i);
                buf.Append(GetErrorDisplay(codePoint));
                i += (codePoint > 0xFFFF) ? 2 : 1;
            }
            return buf.ToString();
        }

        public virtual string GetErrorDisplay(int c)
        {
            string s;
            switch (c)
            {
                case TokenConstants.EOF:
                {
                    s = "<EOF>";
                    break;
                }

                case '\n':
                {
                    s = "\\n";
                    break;
                }

                case '\t':
                {
                    s = "\\t";
                    break;
                }

                case '\r':
                {
                    s = "\\r";
                    break;
                }

                default:
                {
                    s = Char.ConvertFromUtf32(c);
                    break;
                }
            }
            return s;
        }

        public virtual string GetCharErrorDisplay(int c)
        {
            string s = GetErrorDisplay(c);
            return "'" + s + "'";
        }

        /// <summary>
        /// Lexers can normally match any char in it's vocabulary after matching
        /// a token, so do the easy thing and just kill a character and hope
        /// it all works out.
        /// </summary>
        /// <remarks>
        /// Lexers can normally match any char in it's vocabulary after matching
        /// a token, so do the easy thing and just kill a character and hope
        /// it all works out.  You can instead use the rule invocation stack
        /// to do sophisticated error recovery if you are in a fragment rule.
        /// </remarks>
        public virtual void Recover(RecognitionException re)
        {
            //System.out.println("consuming char "+(char)input.LA(1)+" during recovery");
            //re.printStackTrace();
            // TODO: Do we lose character or line position information?
            _input.Consume();
        }
    }
}
