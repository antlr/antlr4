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
    /// A high-performance <see cref="ICharStream"/> backed directly by a <c>string</c>,
    /// avoiding the <c>ToCharArray()</c> copy that <see cref="CharSpanInputStream"/> and
    /// <see cref="AntlrInputStream"/> perform on construction.
    ///
    /// <para>Use this class when your input is already a <c>string</c> in memory and you
    /// want the lowest-cost parse setup. The string is stored by reference — no copy is
    /// ever made.</para>
    ///
    /// <para><b>Unicode note:</b> This class handles BMP characters (U+0000–U+FFFF) only,
    /// matching the behaviour of <see cref="AntlrInputStream"/> and
    /// <see cref="CharSpanInputStream"/>. Supplementary code points (U+10000 and above,
    /// e.g. emoji, historic scripts) are represented as surrogate pairs and will be seen
    /// as two separate <c>char</c> values rather than a single code point. If your grammar
    /// targets supplementary code points, use <see cref="CodePointCharStream"/> instead.</para>
    /// </summary>
#if NET8_0_OR_GREATER

    public class StringSpanInputStream : ICharStream
    {
        private readonly string _data;
        private readonly int _length;
        private int _index;

        /// <summary>What is name or source of this char stream?</summary>
        public string name;

        /// <summary>
        /// Creates a stream from a string. The string is used directly — no copy is made.
        /// </summary>
        public StringSpanInputStream(string input)
        {
            _data = input ?? throw new ArgumentNullException(nameof(input));
            _length = input.Length;
            _index = 0;
        }

        /// <summary>
        /// Creates a stream from a UTF-8 encoded file on disk.
        /// </summary>
        public static StringSpanInputStream FromPath(string path)
            => FromPath(path, Encoding.UTF8);

        /// <summary>
        /// Creates a stream from a file on disk with the specified encoding.
        /// </summary>
        public static StringSpanInputStream FromPath(string path, Encoding encoding)
        {
            var contents = File.ReadAllText(path, encoding);
            return new StringSpanInputStream(contents) { name = path };
        }

        /// <summary>
        /// Returns the backing data as a <see cref="ReadOnlySpan{T}"/> — zero allocation,
        /// zero copy.
        /// </summary>
        public ReadOnlySpan<char> Data => _data.AsSpan(0, _length);

        /// <summary>
        /// Returns the backing data as a <see cref="ReadOnlyMemory{T}"/>.
        /// </summary>
        public ReadOnlyMemory<char> AsMemory() => _data.AsMemory(0, _length);

        public void Reset() => _index = 0;

        public void Consume()
        {
            if (_index >= _length)
            {
                System.Diagnostics.Debug.Assert(LA(1) == IntStreamConstants.EOF);
                throw new InvalidOperationException("cannot consume EOF");
            }
            _index++;
        }

        public int LA(int i)
        {
            if (i == 0) return 0;
            if (i < 0)
            {
                i++;
                if ((_index + i - 1) < 0) return IntStreamConstants.EOF;
            }
            int pos = _index + i - 1;
            return (uint)pos < (uint)_length ? _data[pos] : IntStreamConstants.EOF;
        }

        public int Index => _index;

        public int Size => _length;

        public int Mark() => -1;

        public void Release(int marker) { }

        public void Seek(int index)
        {
            _index = Math.Min(index, _length);
        }

        public string GetText(Interval interval)
        {
            int start = interval.a;
            int stop = interval.b;
            if (stop >= _length) stop = _length - 1;
            int count = stop - start + 1;
            if (start >= _length || count <= 0) return string.Empty;
            return _data.Substring(start, count);
        }

        /// <summary>
        /// Returns the text for the given interval as a <see cref="ReadOnlySpan{T}"/>
        /// without allocating a string. Use this in hot paths where the caller can
        /// operate on the span directly.
        /// </summary>
        public ReadOnlySpan<char> GetTextSpan(Interval interval)
        {
            int start = interval.a;
            int stop = interval.b;
            if (stop >= _length) stop = _length - 1;
            int count = stop - start + 1;
            if (start >= _length || count <= 0) return ReadOnlySpan<char>.Empty;
            return Data.Slice(start, count);
        }

        public string SourceName
        {
            get
            {
                if (string.IsNullOrEmpty(name))
                    return IntStreamConstants.UnknownSourceName;
                return name;
            }
        }

        /// <summary>
        /// Returns the original string — zero allocation.
        /// </summary>
        public override string ToString() => _data;

#else

    public class StringSpanInputStream : BaseInputCharStream
    {
        private readonly string _data;

        /// <summary>
        /// Creates a stream from a string. The string is used directly — no copy is made.
        /// </summary>
        public StringSpanInputStream(string input)
        {
            _data = input ?? throw new ArgumentNullException(nameof(input));
            n = input.Length;
        }

        /// <summary>
        /// Creates a stream from a UTF-8 encoded file on disk.
        /// </summary>
        public static StringSpanInputStream FromPath(string path)
            => FromPath(path, Encoding.UTF8);

        /// <summary>
        /// Creates a stream from a file on disk with the specified encoding.
        /// </summary>
        public static StringSpanInputStream FromPath(string path, Encoding encoding)
        {
            var contents = File.ReadAllText(path, encoding);
            return new StringSpanInputStream(contents) { name = path };
        }

        protected override int ValueAt(int i) => _data[i];

        protected override string ConvertDataToString(int start, int count)
            => _data.Substring(start, count);

#endif
    }
}
