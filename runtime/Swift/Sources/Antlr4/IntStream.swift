/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 



/// 
/// A simple stream of symbols whose values are represented as integers. This
/// interface provides __marked ranges__ with support for a minimum level
/// of buffering necessary to implement arbitrary lookahead during prediction.
/// For more information on marked ranges, see _#mark_.
/// 
/// __Initializing Methods:__ Some methods in this interface have
/// unspecified behavior if no call to an initializing method has occurred after
/// the stream was constructed. The following is a list of initializing methods:
/// 
/// * _#LA_
/// * _#consume_
/// * _#size_
/// 
public protocol IntStream: class {

    /// 
    /// Consumes the current symbol in the stream. This method has the following
    /// effects:
    /// 
    /// * __Forward movement:__ The value of _#index index()_
    /// before calling this method is less than the value of `index()`
    /// after calling this method.
    /// * __Ordered lookahead:__ The value of `LA(1)` before
    /// calling this method becomes the value of `LA(-1)` after calling
    /// this method.
    /// 
    /// Note that calling this method does not guarantee that `index()` is
    /// incremented by exactly 1, as that would preclude the ability to implement
    /// filtering streams (e.g. _org.antlr.v4.runtime.CommonTokenStream_ which distinguishes
    /// between "on-channel" and "off-channel" tokens).
    /// 
    /// - throws: _ANTLRError.illegalState_ if an attempt is made to consume the the
    /// end of the stream (i.e. if `LA(1)==`_#EOF EOF_ before calling
    /// `consume`).
    /// 
    func consume() throws

    /// 
    /// Gets the value of the symbol at offset `i` from the current
    /// position. When `i==1`, this method returns the value of the current
    /// symbol in the stream (which is the next symbol to be consumed). When
    /// `i==-1`, this method returns the value of the previously read
    /// symbol in the stream. It is not valid to call this method with
    /// `i==0`, but the specific behavior is unspecified because this
    /// method is frequently called from performance-critical code.
    /// 
    /// This method is guaranteed to succeed if any of the following are true:
    /// 
    /// * `i>0`
    /// * `i==-1` and _#index index()_ returns a value greater
    /// than the value of `index()` after the stream was constructed
    /// and `LA(1)` was called in that order. Specifying the current
    /// `index()` relative to the index after the stream was created
    /// allows for filtering implementations that do not return every symbol
    /// from the underlying source. Specifying the call to `LA(1)`
    /// allows for lazily initialized streams.
    /// * `LA(i)` refers to a symbol consumed within a marked region
    /// that has not yet been released.
    /// 
    /// If `i` represents a position at or beyond the end of the stream,
    /// this method returns _#EOF_.
    /// 
    /// The return value is unspecified if `i<0` and fewer than `-i`
    /// calls to _#consume consume()_ have occurred from the beginning of
    /// the stream before calling this method.
    /// 
    /// - throws: _ANTLRError.unsupportedOperation_ if the stream does not support
    /// retrieving the value of the specified symbol
    /// 
    func LA(_ i: Int) throws -> Int

    /// 
    /// A mark provides a guarantee that _#seek seek()_ operations will be
    /// valid over a "marked range" extending from the index where `mark()`
    /// was called to the current _#index index()_. This allows the use of
    /// streaming input sources by specifying the minimum buffering requirements
    /// to support arbitrary lookahead during prediction.
    /// 
    /// The returned mark is an opaque handle (type `int`) which is passed
    /// to _#release release()_ when the guarantees provided by the marked
    /// range are no longer necessary. When calls to
    /// `mark()`/`release()` are nested, the marks must be released
    /// in reverse order of which they were obtained. Since marked regions are
    /// used during performance-critical sections of prediction, the specific
    /// behavior of invalid usage is unspecified (i.e. a mark is not released, or
    /// a mark is released twice, or marks are not released in reverse order from
    /// which they were created).
    /// 
    /// The behavior of this method is unspecified if no call to an
    /// _org.antlr.v4.runtime.IntStream initializing method_ has occurred after this stream was
    /// constructed.
    /// 
    /// This method does not change the current position in the input stream.
    /// 
    /// The following example shows the use of _#mark mark()_,
    /// _#release release(mark)_, _#index index()_, and
    /// _#seek seek(index)_ as part of an operation to safely work within a
    /// marked region, then restore the stream position to its original value and
    /// release the mark.
    /// 
    /// IntStream stream = ...;
    /// int index = -1;
    /// int mark = stream.mark();
    /// try {
    /// index = stream.index();
    /// // perform work here...
    /// } finally {
    /// if (index != -1) {
    /// stream.seek(index);
    /// }
    /// stream.release(mark);
    /// }
    /// 
    /// 
    /// - returns: An opaque marker which should be passed to
    /// _#release release()_ when the marked range is no longer required.
    /// 
    func mark() -> Int

    /// 
    /// This method releases a marked range created by a call to
    /// _#mark mark()_. Calls to `release()` must appear in the
    /// reverse order of the corresponding calls to `mark()`. If a mark is
    /// released twice, or if marks are not released in reverse order of the
    /// corresponding calls to `mark()`, the behavior is unspecified.
    /// 
    /// For more information and an example, see _#mark_.
    /// 
    /// - parameter marker: A marker returned by a call to `mark()`.
    /// - seealso: #mark
    /// 
    func release(_ marker: Int) throws

    /// 
    /// Return the index into the stream of the input symbol referred to by
    /// `LA(1)`.
    /// 
    /// The behavior of this method is unspecified if no call to an
    /// _org.antlr.v4.runtime.IntStream initializing method_ has occurred after this stream was
    /// constructed.
    /// 
    func index() -> Int

    /// 
    /// Set the input cursor to the position indicated by `index`. If the
    /// specified index lies past the end of the stream, the operation behaves as
    /// though `index` was the index of the EOF symbol. After this method
    /// returns without throwing an exception, then at least one of the following
    /// will be true.
    /// 
    /// * _#index index()_ will return the index of the first symbol
    /// appearing at or after the specified `index`. Specifically,
    /// implementations which filter their sources should automatically
    /// adjust `index` forward the minimum amount required for the
    /// operation to target a non-ignored symbol.
    /// * `LA(1)` returns _#EOF_
    /// 
    /// This operation is guaranteed to not throw an exception if `index`
    /// lies within a marked region. For more information on marked regions, see
    /// _#mark_. The behavior of this method is unspecified if no call to
    /// an _org.antlr.v4.runtime.IntStream initializing method_ has occurred after this stream
    /// was constructed.
    /// 
    /// - parameter index: The absolute index to seek to.
    /// 
    /// - throws: _ANTLRError.illegalArgument_ if `index` is less than 0
    /// - throws: _ANTLRError.unsupportedOperation_ if the stream does not support
    /// seeking to the specified index
    /// 
    func seek(_ index: Int) throws

    /// 
    /// Returns the total number of symbols in the stream, including a single EOF
    /// symbol.
    /// 
    /// - throws: _ANTLRError.unsupportedOperation_ if the size of the stream is
    /// unknown.
    /// 
    func size() -> Int

    /// 
    /// Gets the name of the underlying symbol source. This method returns a
    /// non-null, non-empty string. If such a name is not known, this method
    /// returns _#UNKNOWN_SOURCE_NAME_.
    /// 
    func getSourceName() -> String
}
