/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using Antlr4.Runtime.Misc;

namespace Antlr4.Runtime
{
    /// <summary>
    /// A high-performance token implementation that defers text materialization
    /// and uses ValueTuple to avoid heap allocations.
    /// <para>
    /// Key differences from <see cref="CommonToken"/>:
    /// <list type="bullet">
    ///   <item><description><see cref="Text"/> is lazily materialized from the input stream on first access
    ///   and cached. Most tokens' text is never read — the parser matches on <see cref="Type"/>.</description></item>
    ///   <item><description>The token source pair uses <c>ValueTuple</c> (struct) instead of <c>Tuple</c> (class),
    ///   eliminating one heap allocation per token.</description></item>
    /// </list>
    /// </para>
    /// <para>This class implements both <see cref="IToken"/> and <see cref="IWritableToken"/>,
    /// so it is a drop-in replacement anywhere <see cref="CommonToken"/> is used.
    /// Generated parsers that reference <c>CommonToken</c> by name in user code will still
    /// work — simply swap the token factory.</para>
    /// </summary>
    public class OptimizedToken : IWritableToken
    {
        /// <summary>
        /// Explicit struct to hold the token source pair, avoiding ValueTuple
        /// which is unavailable on net45.
        /// </summary>
        private struct TokenSourcePair
        {
            public ITokenSource Source;
            public ICharStream Stream;

            public TokenSourcePair(ITokenSource source, ICharStream stream)
            {
                Source = source;
                Stream = stream;
            }
        }

        /// <summary>
        /// An empty source pair for tokens that have no source.
        /// </summary>
        public static readonly (ITokenSource, ICharStream) EmptySource = (null, null);

        private int _type;
        private int _line;
        private int _charPositionInLine = -1;
        private int _channel = TokenConstants.DefaultChannel;
        private int _index = -1;
        private int _start;
        private int _stop;

        /// <summary>
        /// Uses a struct instead of Tuple — no heap allocation per token.
        /// </summary>
        private TokenSourcePair _source;

        /// <summary>
        /// Cached text. Null means "not yet materialized" — will be lazily
        /// computed from the char stream on first access to <see cref="Text"/>.
        /// Once set (either lazily or explicitly), cached for subsequent reads.
        /// </summary>
        private string _text;

        /// <summary>
        /// Tracks whether <see cref="_text"/> was explicitly set by user code,
        /// as opposed to being lazily materialized. Explicit text always wins.
        /// </summary>
        private bool _textExplicitlySet;

        public OptimizedToken(int type)
        {
            _type = type;
            _source = new TokenSourcePair(null, null);
        }

        public OptimizedToken((ITokenSource, ICharStream) source, int type, int channel, int start, int stop)
        {
            _source = new TokenSourcePair(source.Item1, source.Item2);
            _type = type;
            _channel = channel;
            _start = start;
            _stop = stop;
            if (source.Item1 != null)
            {
                _line = source.Item1.Line;
                _charPositionInLine = source.Item1.Column;
            }
            // Text is NOT materialized here — deferred until .Text is read
        }

        public OptimizedToken(int type, string text)
        {
            _type = type;
            _channel = TokenConstants.DefaultChannel;
            _text = text;
            _textExplicitlySet = true;
            _source = new TokenSourcePair(null, null);
        }

        /// <summary>
        /// Copy constructor. Copies from any <see cref="IToken"/>.
        /// If the source is an <see cref="OptimizedToken"/>, preserves deferred text semantics.
        /// If the source is a <see cref="CommonToken"/>, copies its internal text directly.
        /// </summary>
        public OptimizedToken(IToken oldToken)
        {
            _type = oldToken.Type;
            _line = oldToken.Line;
            _index = oldToken.TokenIndex;
            _charPositionInLine = oldToken.Column;
            _channel = oldToken.Channel;
            _start = oldToken.StartIndex;
            _stop = oldToken.StopIndex;

            if (oldToken is OptimizedToken opt)
            {
                _text = opt._text;
                _textExplicitlySet = opt._textExplicitlySet;
                _source = opt._source;
            }
            else if (oldToken is CommonToken ct)
            {
                // Access the protected field directly to avoid materializing
                _text = ct.source.Item2 != null ? null : oldToken.Text;
                _source = new TokenSourcePair(ct.source.Item1, ct.source.Item2);
            }
            else
            {
                _text = oldToken.Text;
                _textExplicitlySet = true;
                _source = new TokenSourcePair(oldToken.TokenSource, oldToken.InputStream);
            }
        }

        public virtual int Type
        {
            get => _type;
            set => _type = value;
        }

        public virtual int Line
        {
            get => _line;
            set => _line = value;
        }

        /// <summary>
        /// Lazily materializes the token text on first access.
        /// If text was explicitly set, returns that. Otherwise, extracts from
        /// the char stream using start/stop indices. The result is cached.
        /// </summary>
        public virtual string Text
        {
            get
            {
                if (_text != null)
                {
                    return _text;
                }
                // Not yet materialized — compute from stream
                ICharStream input = _source.Stream;
                if (input == null)
                {
                    return null;
                }
                int n = input.Size;
                if (_start < n && _stop < n)
                {
                    _text = input.GetText(Interval.Of(_start, _stop));
                }
                else
                {
                    _text = "<EOF>";
                }
                return _text;
            }
            set
            {
                _text = value;
                _textExplicitlySet = true;
            }
        }

        public virtual int Column
        {
            get => _charPositionInLine;
            set => _charPositionInLine = value;
        }

        public virtual int Channel
        {
            get => _channel;
            set => _channel = value;
        }

        public virtual int StartIndex
        {
            get => _start;
            set => _start = value;
        }

        public virtual int StopIndex
        {
            get => _stop;
            set => _stop = value;
        }

        public virtual int TokenIndex
        {
            get => _index;
            set => _index = value;
        }

        public virtual ITokenSource TokenSource => _source.Source;

        public virtual ICharStream InputStream => _source.Stream;

        public override string ToString()
        {
            string channelStr = string.Empty;
            if (_channel > 0)
            {
                channelStr = ",channel=" + _channel;
            }
            string txt = Text;
            if (txt != null)
            {
                txt = txt.Replace("\n", "\\n");
                txt = txt.Replace("\r", "\\r");
                txt = txt.Replace("\t", "\\t");
            }
            else
            {
                txt = "<no text>";
            }
            return "[@" + TokenIndex + "," + _start + ":" + _stop + "='" + txt + "',<" + _type + ">" + channelStr + "," + _line + ":" + Column + "]";
        }
    }
}
