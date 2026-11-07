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
    /// using <see cref="ReadOnlySpan{T}"/> for zero-copy slicing and efficient text extraction.
    /// 
    /// This class is designed for scenarios where the input is immutable and can be efficiently accessed as a span.
    /// 
    /// It sees no performance-benefit against the default AntlrInputStream in the typical LT(1) hotpath.
    /// The benefits are in allocations in all other regions, and performance of GetText().
    /// </summary>
#if NET8_0_OR_GREATER
    public class CharSpanInputStream : ICharStream
    {
        private readonly char[] _data;
        private readonly int _length;
        private int _index;

        /// <summary>What is name or source of this char stream?</summary>
        public string name;

        /// <summary>
        /// Creates a stream directly from a <c>char[]</c>. The array is used
        /// directly (no copy) — caller must not mutate it after construction.
        /// </summary>
        public CharSpanInputStream(char[] data, int length)
        {
            _data = data ?? throw new ArgumentNullException(nameof(data));
            if (length < 0 || length > data.Length)
                throw new ArgumentOutOfRangeException(nameof(length));
            _length = length;
            _index = 0;
        }

        /// <summary>
        /// Creates a stream by reading the entire contents of a
        /// <see cref="TextReader"/>, then closing it.
        /// </summary>
        public CharSpanInputStream(TextReader reader)
        {
            if (reader == null) throw new ArgumentNullException(nameof(reader));
            _data = reader.ReadToEnd().ToCharArray();
            _length = _data.Length;
            _index = 0;
        }

        /// <summary>
        /// Creates a stream by reading the entire contents of a
        /// <see cref="Stream"/> using the specified encoding.
        /// </summary>
        public CharSpanInputStream(Stream stream, Encoding encoding)
        {
            if (stream == null) throw new ArgumentNullException(nameof(stream));
            if (encoding == null) throw new ArgumentNullException(nameof(encoding));
            using (var reader = new StreamReader(stream, encoding, detectEncodingFromByteOrderMarks: false))
            {
                _data = reader.ReadToEnd().ToCharArray();
            }
            _length = _data.Length;
            _index = 0;
        }

        /// <summary>
        /// Creates a stream by reading a UTF-8 encoded <see cref="Stream"/>.
        /// </summary>
        public CharSpanInputStream(Stream stream)
            : this(stream, Encoding.UTF8)
        {
        }

        /// <summary>
        /// Creates a stream from a UTF-8 encoded file on disk.
        /// </summary>
        public static CharSpanInputStream FromPath(string path)
        {
            return FromPath(path, Encoding.UTF8);
        }

        /// <summary>
        /// Creates a stream from a file on disk with the specified encoding.
        /// </summary>
        public static CharSpanInputStream FromPath(string path, Encoding encoding)
        {
            var contents = File.ReadAllText(path, encoding);
            var chars = contents.ToCharArray(); return new CharSpanInputStream(chars, chars.Length) { name = path };
        }

        /// <summary>
        /// Returns the backing data as a <see cref="ReadOnlySpan{T}"/>.
        /// </summary>
        public ReadOnlySpan<char> Data => _data.AsSpan(0, _length);

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

        public override string ToString() => new string(Data);
#else
    public class CharSpanInputStream : BaseInputCharStream
    {
        private readonly char[] _data;

        /// <summary>
        /// Creates a stream directly from a <c>char[]</c>. The array is used
        /// directly (no copy) — caller must not mutate it after construction.
        /// </summary>
        public CharSpanInputStream(char[] data, int length)
        {
            _data = data ?? throw new ArgumentNullException(nameof(data));
            if (length < 0 || length > data.Length)
                throw new ArgumentOutOfRangeException(nameof(length));
            n = length;
        }

        /// <summary>
        /// Creates a stream by reading the entire contents of a
        /// <see cref="TextReader"/>, then closing it.
        /// </summary>
        public CharSpanInputStream(TextReader reader)
        {
            if (reader == null) throw new ArgumentNullException(nameof(reader));
            _data = reader.ReadToEnd().ToCharArray();
            n = _data.Length;
        }

        /// <summary>
        /// Creates a stream by reading the entire contents of a
        /// <see cref="Stream"/> using the specified encoding.
        /// </summary>
        public CharSpanInputStream(Stream stream, Encoding encoding)
        {
            if (stream == null) throw new ArgumentNullException(nameof(stream));
            if (encoding == null) throw new ArgumentNullException(nameof(encoding));
            using (var reader = new StreamReader(stream, encoding, detectEncodingFromByteOrderMarks: false))
            {
                _data = reader.ReadToEnd().ToCharArray();
            }
            n = _data.Length;
        }

        /// <summary>
        /// Creates a stream by reading a UTF-8 encoded <see cref="Stream"/>.
        /// </summary>
        public CharSpanInputStream(Stream stream)
            : this(stream, Encoding.UTF8)
        {
        }

        /// <summary>
        /// Creates a stream from a UTF-8 encoded file on disk.
        /// </summary>
        public static CharSpanInputStream FromPath(string path)
        {
            return FromPath(path, Encoding.UTF8);
        }

        /// <summary>
        /// Creates a stream from a file on disk with the specified encoding.
        /// </summary>
        public static CharSpanInputStream FromPath(string path, Encoding encoding)
        {
            var contents = File.ReadAllText(path, encoding);
            var chars = contents.ToCharArray(); return new CharSpanInputStream(chars, chars.Length) { name = path };
        }

        protected override int ValueAt(int i) => _data[i];

        protected override string ConvertDataToString(int start, int count) => new string(_data, start, count);

#endif
    }
}
