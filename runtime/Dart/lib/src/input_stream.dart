/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import 'dart:async';
import 'dart:convert';
import 'dart:math';

import 'interval_set.dart';
import 'token.dart';

import 'util/platform_stub.dart' if (dart.library.io) 'util/platform_io.dart';

/// A simple stream of symbols whose values are represented as integers. This
/// interface provides <em>marked ranges</em> with support for a minimum level
/// of buffering necessary to implement arbitrary lookahead during prediction.
/// For more information on marked ranges, see {@link #mark}.
///
/// <p><strong>Initializing Methods:</strong> Some methods in this interface have
/// unspecified behavior if no call to an initializing method has occurred after
/// the stream was constructed. The following is a list of initializing methods:</p>
///
/// <ul>
///   <li>{@link #LA}</li>
///   <li>{@link #consume}</li>
///   <li>{@link #size}</li>
/// </ul>
abstract class IntStream {
  /// The value returned by {@link #LA LA()} when the end of the stream is
  /// reached.
  static const int EOF = -1;

  /// The value returned by {@link #getSourceName} when the actual name of the
  /// underlying source is not known.
  static const UNKNOWN_SOURCE_NAME = '<unknown>';

  /// Consumes the current symbol in the stream. This method has the following
  /// effects:
  ///
  /// <ul>
  ///   <li><strong>Forward movement:</strong> The value of {@link #index index()}
  ///		before calling this method is less than the value of {@code index()}
  ///		after calling this method.</li>
  ///   <li><strong>Ordered lookahead:</strong> The value of {@code LA(1)} before
  ///		calling this method becomes the value of {@code LA(-1)} after calling
  ///		this method.</li>
  /// </ul>
  ///
  /// Note that calling this method does not guarantee that {@code index()} is
  /// incremented by exactly 1, as that would preclude the ability to implement
  /// filtering streams (e.g. [CommonTokenStream] which distinguishes
  /// between "on-channel" and "off-channel" tokens).
  ///
  /// @throws IllegalStateException if an attempt is made to consume the
  /// end of the stream (i.e. if {@code LA(1)==}{@link #EOF EOF} before calling
  /// [consume]).
  void consume();

  /// Gets the value of the symbol at offset [i] from the current
  /// position. When {@code i==1}, this method returns the value of the current
  /// symbol in the stream (which is the next symbol to be consumed). When
  /// {@code i==-1}, this method returns the value of the previously read
  /// symbol in the stream. It is not valid to call this method with
  /// {@code i==0}, but the specific behavior is unspecified because this
  /// method is frequently called from performance-critical code.
  ///
  /// <p>This method is guaranteed to succeed if any of the following are true:</p>
  ///
  /// <ul>
  ///   <li>{@code i>0}</li>
  ///   <li>{@code i==-1} and {@link #index index()} returns a value greater
  ///     than the value of {@code index()} after the stream was constructed
  ///     and {@code LA(1)} was called in that order. Specifying the current
  ///     {@code index()} relative to the index after the stream was created
  ///     allows for filtering implementations that do not return every symbol
  ///     from the underlying source. Specifying the call to {@code LA(1)}
  ///     allows for lazily initialized streams.</li>
  ///   <li>{@code LA(i)} refers to a symbol consumed within a marked region
  ///     that has not yet been released.</li>
  /// </ul>
  ///
  /// <p>If [i] represents a position at or beyond the end of the stream,
  /// this method returns {@link #EOF}.</p>
  ///
  /// <p>The return value is unspecified if {@code i<0} and fewer than {@code -i}
  /// calls to {@link #consume consume()} have occurred from the beginning of
  /// the stream before calling this method.</p>
  ///
  /// @throws UnsupportedOperationException if the stream does not support
  /// retrieving the value of the specified symbol
  int? LA(int i);

  /// A mark provides a guarantee that {@link #seek seek()} operations will be
  /// valid over a "marked range" extending from the index where {@code mark()}
  /// was called to the current {@link #index index()}. This allows the use of
  /// streaming input sources by specifying the minimum buffering requirements
  /// to support arbitrary lookahead during prediction.
  ///
  /// <p>The returned mark is an opaque handle (type [int]) which is passed
  /// to {@link #release release()} when the guarantees provided by the marked
  /// range are no longer necessary. When calls to
  /// {@code mark()}/{@code release()} are nested, the marks must be released
  /// in reverse order of which they were obtained. Since marked regions are
  /// used during performance-critical sections of prediction, the specific
  /// behavior of invalid usage is unspecified (i.e. a mark is not released, or
  /// a mark is released twice, or marks are not released in reverse order from
  /// which they were created).</p>
  ///
  /// <p>The behavior of this method is unspecified if no call to an
  /// {@link IntStream initializing method} has occurred after this stream was
  /// constructed.</p>
  ///
  /// <p>This method does not change the current position in the input stream.</p>
  ///
  /// <p>The following example shows the use of {@link #mark mark()},
  /// {@link #release release(mark)}, {@link #index index()}, and
  /// {@link #seek seek(index)} as part of an operation to safely work within a
  /// marked region, then restore the stream position to its original value and
  /// release the mark.</p>
  /// <pre>
  /// IntStream stream = ...;
  /// int index = -1;
  /// int mark = stream.mark();
  /// try {
  ///   index = stream.index();
  ///   // perform work here...
  /// } finally {
  ///   if (index != -1) {
  ///     stream.seek(index);
  ///   }
  ///   stream.release(mark);
  /// }
  /// </pre>
  ///
  /// @return An opaque marker which should be passed to
  /// {@link #release release()} when the marked range is no longer required.
  int mark();

  /// This method releases a marked range created by a call to
  /// {@link #mark mark()}. Calls to {@code release()} must appear in the
  /// reverse order of the corresponding calls to {@code mark()}. If a mark is
  /// released twice, or if marks are not released in reverse order of the
  /// corresponding calls to {@code mark()}, the behavior is unspecified.
  ///
  /// <p>For more information and an example, see {@link #mark}.</p>
  ///
  /// @param marker A marker returned by a call to {@code mark()}.
  /// @see #mark
  void release(int marker);

  /// Return the index into the stream of the input symbol referred to by
  /// {@code LA(1)}.
  ///
  /// <p>The behavior of this method is unspecified if no call to an
  /// {@link IntStream initializing method} has occurred after this stream was
  /// constructed.</p>
  int get index;

  /// Set the input cursor to the position indicated by [index]. If the
  /// specified index lies past the end of the stream, the operation behaves as
  /// though [index] was the index of the EOF symbol. After this method
  /// returns without throwing an exception, then at least one of the following
  /// will be true.
  ///
  /// <ul>
  ///   <li>{@link #index index()} will return the index of the first symbol
  ///     appearing at or after the specified [index]. Specifically,
  ///     implementations which filter their sources should automatically
  ///     adjust [index] forward the minimum amount required for the
  ///     operation to target a non-ignored symbol.</li>
  ///   <li>{@code LA(1)} returns {@link #EOF}</li>
  /// </ul>
  ///
  /// This operation is guaranteed to not throw an exception if [index]
  /// lies within a marked region. For more information on marked regions, see
  /// {@link #mark}. The behavior of this method is unspecified if no call to
  /// an {@link IntStream initializing method} has occurred after this stream
  /// was constructed.
  ///
  /// @param index The absolute index to seek to.
  ///
  /// @throws IllegalArgumentException if [index] is less than 0
  /// @throws UnsupportedOperationException if the stream does not support
  /// seeking to the specified index
  void seek(int index);

  /// Returns the total number of symbols in the stream, including a single EOF
  /// symbol.
  ///
  /// @throws UnsupportedOperationException if the size of the stream is
  /// unknown.
  int get size;

  /// Gets the name of the underlying symbol source. This method returns a
  /// non-null, non-empty string. If such a name is not known, this method
  /// returns {@link #UNKNOWN_SOURCE_NAME}.

  String get sourceName;
}

/// A source of characters for an ANTLR lexer. */
abstract class CharStream extends IntStream {
  /// This method returns the text for a range of characters within this input
  /// stream. This method is guaranteed to not throw an exception if the
  /// specified [interval] lies entirely within a marked range. For more
  /// information about marked ranges, see {@link IntStream#mark}.
  ///
  /// @param interval an interval within the stream
  /// @return the text of the specified interval
  ///
  /// @throws NullPointerException if [interval] is null
  /// @throws IllegalArgumentException if {@code interval.a < 0}, or if
  /// {@code interval.b < interval.a - 1}, or if {@code interval.b} lies at or
  /// past the end of the stream
  /// @throws UnsupportedOperationException if the stream does not support
  /// getting the text of the specified interval
  String getText(Interval interval);
}

// Vacuum all input from a string and then treat it like a buffer.
class InputStream extends CharStream {
  final name = '<empty>';
  late List<int> data;
  int _index = 0;
  bool decodeToUnicodeCodePoints = false;

  InputStream(List<int> data) {
    this.data = data;
  }

  InputStream.fromString(String data) {
    this.data = data.runes.toList(growable: false);
  }

  static Future<InputStream> fromStringStream(Stream<String> stream) async {
    final data = StringBuffer();
    await stream.listen((buf) {
      data.write(buf);
    }).asFuture();
    return InputStream.fromString(data.toString());
  }

  static Future<InputStream> fromStream(Stream<List<int>> stream,
      {Encoding encoding = utf8}) {
    final data = stream.transform(encoding.decoder);
    return fromStringStream(data);
  }

  static Future<InputStream> fromPath(String path, {Encoding encoding = utf8}) {
    return fromStream(readStream(path), encoding: encoding);
  }

  @override
  int get index {
    return _index;
  }

  @override
  int get size {
    return data.length;
  }

  /// Reset the stream so that it's in the same state it was
  /// when the object was created *except* the data array is not
  /// touched.
  void reset() {
    _index = 0;
  }

  @override
  void consume() {
    if (_index >= size) {
      // assert this.LA(1) == Token.EOF
      throw ('cannot consume EOF');
    }
    _index += 1;
  }

  @override
  int LA(int offset) {
    if (offset == 0) {
      return 0; // undefined
    }
    if (offset < 0) {
      offset += 1; // e.g., translate LA(-1) to use offset=0
    }
    final pos = _index + offset - 1;
    if (pos < 0 || pos >= size) {
      // invalid
      return Token.EOF;
    }
    return data[pos];
  }

  /// mark/release do nothing; we have entire buffer
  @override
  int mark() {
    return -1;
  }

  @override
  void release(int marker) {}

  /// consume() ahead until p==_index; can't just set p=_index as we must
  /// update line and column. If we seek backwards, just set p
  @override
  void seek(int _index) {
    if (_index <= this._index) {
      this._index = _index; // just jump; don't update stream state (line,
      // ...)
      return;
    }
    // seek forward
    this._index = min(_index, size);
  }

  @override
  String getText(Interval interval) {
    final startIdx = min(interval.a, size);
    final len = min(interval.b - interval.a + 1, size - startIdx);
    return String.fromCharCodes(data, startIdx, startIdx + len);
  }

  @override
  String toString() {
    return String.fromCharCodes(data);
  }

  @override
  String get sourceName {
    // TODO: implement getSourceName
    return IntStream.UNKNOWN_SOURCE_NAME;
  }
}
