/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
using System;
using Antlr4.Runtime;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
    [System.Serializable]
    public class CommonToken : IWritableToken
    {
        private const long serialVersionUID = -6708843461296520577L;

        /// <summary>
        /// An empty
        /// <see cref="Tuple{T1, T2}"/>
        /// which is used as the default value of
        /// <see cref="source"/>
        /// for tokens that do not have a source.
        /// </summary>
        protected internal static readonly Tuple<ITokenSource, ICharStream> EmptySource = Tuple.Create<ITokenSource, ICharStream>(null, null);

        /// <summary>
        /// This is the backing field for the <see cref="Type"/> property.
        /// </summary>
        protected internal int type;

        /// <summary>
        /// This is the backing field for the <see cref="Line"/> property.
        /// </summary>
        protected internal int line;

        /// <summary>
        /// This is the backing field for the <see cref="Column"/> property.
        /// </summary>
        protected internal int charPositionInLine = -1;

        /// <summary>
        /// This is the backing field for the <see cref="Channel"/> property.
        /// </summary>
        protected internal int channel = TokenConstants.DefaultChannel;

        /// <summary>
        /// This is the backing field for
        /// <see cref="TokenSource()"/>
        /// and
        /// <see cref="InputStream()"/>
        /// .
        /// <p>
        /// These properties share a field to reduce the memory footprint of
        /// <see cref="CommonToken"/>
        /// . Tokens created by a
        /// <see cref="CommonTokenFactory"/>
        /// from
        /// the same source and input stream share a reference to the same
        /// <see cref="Tuple{T1, T2}"/>
        /// containing these values.</p>
        /// </summary>
        [NotNull]
        protected internal Tuple<ITokenSource, ICharStream> source;

        /// <summary>
        /// This is the backing field for the <see cref="Text"/> property.
        /// </summary>
        /// <seealso cref="Text"/>
        protected internal string text;

        /// <summary>
        /// This is the backing field for the <see cref="TokenIndex"/> property.
        /// </summary>
        protected internal int index = -1;

        /// <summary>
        /// This is the backing field for the <see cref="StartIndex"/> property.
        /// </summary>
        protected internal int start;

        /// <summary>
        /// This is the backing field for the <see cref="StopIndex"/> property.
        /// </summary>
        protected internal int stop;

        /// <summary>
        /// Constructs a new
        /// <see cref="CommonToken"/>
        /// with the specified token type.
        /// </summary>
        /// <param name="type">The token type.</param>
        public CommonToken(int type)
        {
            // set to invalid position
            this.type = type;
            this.source = EmptySource;
        }

        public CommonToken(Tuple<ITokenSource, ICharStream> source, int type, int channel, int start, int stop)
        {
            this.source = source;
            this.type = type;
            this.channel = channel;
            this.start = start;
            this.stop = stop;
            if (source.Item1 != null)
            {
                this.line = source.Item1.Line;
                this.charPositionInLine = source.Item1.Column;
            }
        }

        /// <summary>
        /// Constructs a new
        /// <see cref="CommonToken"/>
        /// with the specified token type and
        /// text.
        /// </summary>
        /// <param name="type">The token type.</param>
        /// <param name="text">The text of the token.</param>
        public CommonToken(int type, string text)
        {
            this.type = type;
            this.channel = TokenConstants.DefaultChannel;
            this.text = text;
            this.source = EmptySource;
        }

        /// <summary>
        /// Constructs a new
        /// <see cref="CommonToken"/>
        /// as a copy of another
        /// <see cref="IToken"/>
        /// .
        /// <p>
        /// If
        /// <code>oldToken</code>
        /// is also a
        /// <see cref="CommonToken"/>
        /// instance, the newly
        /// constructed token will share a reference to the
        /// <see cref="text"/>
        /// field and
        /// the
        /// <see cref="Tuple{T1, T2}"/>
        /// stored in
        /// <see cref="source"/>
        /// . Otherwise,
        /// <see cref="text"/>
        /// will
        /// be assigned the result of calling
        /// <see cref="Text()"/>
        /// , and
        /// <see cref="source"/>
        /// will be constructed from the result of
        /// <see cref="IToken.TokenSource()"/>
        /// and
        /// <see cref="IToken.InputStream()"/>
        /// .</p>
        /// </summary>
        /// <param name="oldToken">The token to copy.</param>
        public CommonToken(IToken oldToken)
        {
            type = oldToken.Type;
            line = oldToken.Line;
            index = oldToken.TokenIndex;
            charPositionInLine = oldToken.Column;
            channel = oldToken.Channel;
            start = oldToken.StartIndex;
            stop = oldToken.StopIndex;
            if (oldToken is Antlr4.Runtime.CommonToken)
            {
                text = ((Antlr4.Runtime.CommonToken)oldToken).text;
                source = ((Antlr4.Runtime.CommonToken)oldToken).source;
            }
            else
            {
                text = oldToken.Text;
                source = Tuple.Create(oldToken.TokenSource, oldToken.InputStream);
            }
        }

        public virtual int Type
        {
            get
            {
                return type;
            }
            set
            {
                int type = value;
                this.type = type;
            }
        }

        public virtual int Line
        {
            get
            {
                return line;
            }
            set
            {
                int line = value;
                this.line = line;
            }
        }

        /// <summary>Explicitly set the text for this token.</summary>
        /// <remarks>
        /// Explicitly set the text for this token. If {code text} is not
        /// <code>null</code>
        /// , then
        /// <see cref="Text()"/>
        /// will return this value rather than
        /// extracting the text from the input.
        /// </remarks>
        /// <value>
        /// The explicit text of the token, or
        /// <code>null</code>
        /// if the text
        /// should be obtained from the input along with the start and stop indexes
        /// of the token.
        /// </value>
        public virtual string Text
        {
            get
            {
                if (text != null)
                {
                    return text;
                }
                ICharStream input = InputStream;
                if (input == null)
                {
                    return null;
                }
                int n = input.Size;
                if (start < n && stop < n)
                {
                    return input.GetText(Interval.Of(start, stop));
                }
                else
                {
                    return "<EOF>";
                }
            }
            set
            {
                string text = value;
                this.text = text;
            }
        }

        public virtual int Column
        {
            get
            {
                return charPositionInLine;
            }
            set
            {
                int charPositionInLine = value;
                this.charPositionInLine = charPositionInLine;
            }
        }

        public virtual int Channel
        {
            get
            {
                return channel;
            }
            set
            {
                int channel = value;
                this.channel = channel;
            }
        }

        public virtual int StartIndex
        {
            get
            {
                return start;
            }
            set
            {
                int start = value;
                this.start = start;
            }
        }

        public virtual int StopIndex
        {
            get
            {
                return stop;
            }
            set
            {
                int stop = value;
                this.stop = stop;
            }
        }

        public virtual int TokenIndex
        {
            get
            {
                return index;
            }
            set
            {
                int index = value;
                this.index = index;
            }
        }

        public virtual ITokenSource TokenSource
        {
            get
            {
                return source.Item1;
            }
        }

        public virtual ICharStream InputStream
        {
            get
            {
                return source.Item2;
            }
        }

        public override string ToString()
        {
            string channelStr = string.Empty;
            if (channel > 0)
            {
                channelStr = ",channel=" + channel;
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
            return "[@" + TokenIndex + "," + start + ":" + stop + "='" + txt + "',<" + type + ">" + channelStr + "," + line + ":" + Column + "]";
        }
    }
}
