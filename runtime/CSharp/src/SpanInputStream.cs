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
    /// A high-performance <see cref="ICharStream"/> backed by a <c>char[]</c> array,
    /// using <see cref="ReadOnlyMemory{T}"/> and <see cref="ReadOnlySpan{T}"/> for
    /// zero-copy slicing and efficient text extraction.
    ///
    /// <para>This is a drop-in replacement for <see cref="AntlrInputStream"/>.
    /// It handles BMP characters (U+0000 to U+FFFF) only. If your input may
    /// contain supplementary Unicode code points (e.g. emoji, historic scripts),
    /// use <see cref="CodePointCharStream"/> instead.</para>
    /// </summary>
#if NET8_0_OR_GREATER
    public class SpanInputStream : ICharStream
#else
    public class SpanInputStream : BaseInputCharStream
#endif
    {
        private readonly char[] _data;
#if NET8_0_OR_GREATER
        private readonly int _length;
        private int _index;

        /// <summary>What is name or source of this char stream?</summary>
        public string name;
#endif

        /// <summary>
        /// Creates a stream from a string. The string's internal buffer is copied
        /// into a <c>char[]</c> for span-based access.
        /// </summary>
        public SpanInputStream(string input)
        {
            if (input == null) throw new ArgumentNullException(nameof(input));
            _data = input.ToCharArray();
#if NET8_0_OR_GREATER
            _length = _data.Length;
            _index = 0;
#else
            n = _data.Length;
#endif
        }

        /// <summary>
        /// Creates a stream directly from a <c>char[]</c>. The array is used
        /// directly (no copy) — caller must not mutate it after construction.
        /// </summary>
        public SpanInputStream(char[] data, int length)
        {
            _data = data ?? throw new ArgumentNullException(nameof(data));
            if (length < 0 || length > data.Length)
                throw new ArgumentOutOfRangeException(nameof(length));
#if NET8_0_OR_GREATER
            _length = length;
            _index = 0;
#else
            n = length;
#endif
        }

        /// <summary>
        /// Creates a stream by reading the entire contents of a
        /// <see cref="TextReader"/>, then closing it.
        /// </summary>
        public SpanInputStream(TextReader reader)
        {
            if (reader == null) throw new ArgumentNullException(nameof(reader));
            _data = reader.ReadToEnd().ToCharArray();
#if NET8_0_OR_GREATER
            _length = _data.Length;
            _index = 0;
#else
            n = _data.Length;
#endif
        }

        /// <summary>
        /// Creates a stream by reading the entire contents of a
        /// <see cref="Stream"/> using the specified encoding.
        /// </summary>
        public SpanInputStream(Stream stream, Encoding encoding)
        {
            if (stream == null) throw new ArgumentNullException(nameof(stream));
            if (encoding == null) throw new ArgumentNullException(nameof(encoding));
            using (var reader = new StreamReader(stream, encoding, detectEncodingFromByteOrderMarks: false))
            {
                _data = reader.ReadToEnd().ToCharArray();
            }
#if NET8_0_OR_GREATER
            _length = _data.Length;
            _index = 0;
#else
            n = _data.Length;
#endif
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

        /// <summary>
        /// Returns the backing data as a <see cref="ReadOnlySpan{T}"/>.
        /// </summary>
        public ReadOnlySpan<char> Data => _data.AsSpan(0,
#if NET8_0_OR_GREATER
            _length
#else
            n
#endif
        );

        /// <summary>
        /// Returns the backing data as a <see cref="ReadOnlyMemory{T}"/>.
        /// </summary>
        public ReadOnlyMemory<char> AsMemory()
        {
#if NET8_0_OR_GREATER
            return new ReadOnlyMemory<char>(_data, 0, _length);
#else
            return new ReadOnlyMemory<char>(_data, 0, n);
#endif
        }

#if NET8_0_OR_GREATER
        // ──────────────────────────────────────────────────────────
        // net8.0+: Full ICharStream implementation using Span directly
        // ──────────────────────────────────────────────────────────

        public void Reset()
        {
            _index = 0;
        }

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
            // Branchless position: when i < 0, adjust by +1 (extracts sign bit)
            int pos = _index + i - 1 + (int)((uint)i >> 31);
            return (uint)pos < (uint)_length ? Data[pos] : IntStreamConstants.EOF;
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
            return new string(Data.Slice(start, count));
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

        public override string ToString()
        {
            return new string(Data);
        }
#else
        // ──────────────────────────────────────────────────────────
        // netstandard2.0/2.1: Override abstract methods from BaseInputCharStream
        // ──────────────────────────────────────────────────────────

        protected override int ValueAt(int i)
        {
            return _data[i];
        }

        protected override string ConvertDataToString(int start, int count)
        {
            return new string(_data, start, count);
        }
#endif
    }
}
