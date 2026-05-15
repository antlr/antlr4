/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.IO;
using System.Text;
using Antlr4.Runtime.Misc;

namespace Antlr4.Runtime
{
    /// <summary>
    /// A high-performance <see cref="ICharStream"/> that stores input as a
    /// <see cref="string"/> directly, avoiding the <c>char[]</c> copy that
    /// <see cref="AntlrInputStream"/> performs.
    ///
    /// <para>This is a drop-in replacement for <see cref="AntlrInputStream"/>.
    /// It handles BMP characters (U+0000 to U+FFFF) only. If your input may
    /// contain supplementary Unicode code points (e.g. emoji, historic scripts),
    /// use <see cref="CodePointCharStream"/> instead.</para>
    /// </summary>
    public class SpanInputStream : BaseInputCharStream
    {
        private readonly string _data;

        /// <summary>
        /// Creates a stream from a string. No copy is made; the string is
        /// stored directly and accessed via indexing.
        /// </summary>
        public SpanInputStream(string input)
        {
            _data = input ?? throw new ArgumentNullException(nameof(input));
            n = input.Length;
        }

        /// <summary>
        /// Creates a stream by reading the entire contents of a
        /// <see cref="TextReader"/>, then closing it.
        /// </summary>
        public SpanInputStream(TextReader reader)
        {
            if (reader == null)
                throw new ArgumentNullException(nameof(reader));
            _data = reader.ReadToEnd();
            n = _data.Length;
        }

        /// <summary>
        /// Creates a stream by reading the entire contents of a
        /// <see cref="Stream"/> using the specified encoding.
        /// </summary>
        public SpanInputStream(Stream stream, Encoding encoding)
        {
            if (stream == null)
                throw new ArgumentNullException(nameof(stream));
            if (encoding == null)
                throw new ArgumentNullException(nameof(encoding));
            using (var reader = new StreamReader(stream, encoding, detectEncodingFromByteOrderMarks: false))
            {
                _data = reader.ReadToEnd();
            }
            n = _data.Length;
        }

        /// <summary>
        /// Creates a stream by reading a UTF-8 encoded <see cref="Stream"/>.
        /// </summary>
        public SpanInputStream(Stream stream)
            : this(stream, Encoding.UTF8)
        {
        }

        /// <summary>
        /// Creates a stream from a UTF-8 encoded file on disk.
        /// </summary>
        public static SpanInputStream FromPath(string path)
        {
            return FromPath(path, Encoding.UTF8);
        }

        /// <summary>
        /// Creates a stream from a file on disk with the specified encoding.
        /// </summary>
        public static SpanInputStream FromPath(string path, Encoding encoding)
        {
            var contents = File.ReadAllText(path, encoding);
            return new SpanInputStream(contents) { name = path };
        }

        protected override int ValueAt(int i)
        {
            return _data[i];
        }

        protected override string ConvertDataToString(int start, int count)
        {
            return _data.Substring(start, count);
        }
    }
}
