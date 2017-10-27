/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import Foundation


/** Do not buffer up the entire char stream. It does keep a small buffer
 *  for efficiency and also buffers while a mark exists (set by the
 *  lookahead prediction in parser). "Unbuffered" here refers to fact
 *  that it doesn't buffer all data, not that's it's on demand loading of char.
 *
 *  Before 4.7, this class used the default environment encoding to convert
 *  bytes to UTF-16, and held the UTF-16 bytes in the buffer as chars.
 *
 *  As of 4.7, the class uses UTF-8 by default, and the buffer holds Unicode
 *  code points in the buffer as ints.
 */
open class UnbufferedCharStream: CharStream {
    /**
     * A moving window buffer of the data being scanned. While there's a marker,
     * we keep adding to buffer. Otherwise, {@link #consume consume()} resets so
     * we start filling at index 0 again.
     */
    internal var data: [Int]

    /**
     * The number of characters currently in {@link #data data}.
     *
     * <p>This is not the buffer capacity, that's {@code data.length}.</p>
     */
    internal var n = 0

    /**
     * 0..n-1 index into {@link #data data} of next character.
     *
     * <p>The {@code LA(1)} character is {@code data[p]}. If {@code p == n}, we are
     * out of buffered characters.</p>
     */
    internal var p = 0

    /**
     * Count up with {@link #mark mark()} and down with
     * {@link #release release()}. When we {@code release()} the last mark,
     * {@code numMarkers} reaches 0 and we reset the buffer. Copy
     * {@code data[p]..data[n-1]} to {@code data[0]..data[(n-1)-p]}.
     */
    internal var numMarkers = 0

    /**
     * This is the {@code LA(-1)} character for the current position.
     */
    internal var lastChar = -1

    /**
     * When {@code numMarkers > 0}, this is the {@code LA(-1)} character for the
     * first character in {@link #data data}. Otherwise, this is unspecified.
     */
    internal var lastCharBufferStart = 0

    /**
     * Absolute character index. It's the index of the character about to be
     * read via {@code LA(1)}. Goes from 0 to the number of characters in the
     * entire stream, although the stream size is unknown before the end is
     * reached.
     */
    internal var currentCharIndex = 0

    internal let input: InputStream
    private var unicodeIterator: UnicodeScalarStreamIterator


    /** The name or source of this char stream. */
    public var name: String = ""

    public init(_ input: InputStream, _ bufferSize: Int = 256) {
        self.input = input
        self.data = [Int](repeating: 0, count: bufferSize)
        let si = UInt8StreamIterator(input)
        self.unicodeIterator = UnicodeScalarStreamIterator(si)
    }

    public func consume() throws {
        if try LA(1) == CommonToken.EOF {
            throw ANTLRError.illegalState(msg: "cannot consume EOF")
        }

        // buf always has at least data[p==0] in this method due to ctor
        lastChar = data[p]   // track last char for LA(-1)

        if p == n - 1 && numMarkers == 0 {
            n = 0
            p = -1 // p++ will leave this at 0
            lastCharBufferStart = lastChar
        }

        p += 1
        currentCharIndex += 1
        sync(1)
    }

    /**
     * Make sure we have 'need' elements from current position {@link #p p}.
     * Last valid {@code p} index is {@code data.length-1}. {@code p+need-1} is
     * the char index 'need' elements ahead. If we need 1 element,
     * {@code (p+1-1)==p} must be less than {@code data.length}.
     */
    internal func sync(_ want: Int) {
        let need = (p + want - 1) - n + 1 // how many more elements we need?
        if need > 0 {
            fill(need)
        }
    }

    /**
     * Add {@code n} characters to the buffer. Returns the number of characters
     * actually added to the buffer. If the return value is less than {@code n},
     * then EOF was reached before {@code n} characters could be added.
     */
    @discardableResult internal func fill(_ toAdd: Int) -> Int {
        for i in 0 ..< toAdd {
            if n > 0 && data[n - 1] == CommonToken.EOF {
                return i
            }

            guard let c = nextChar() else {
                return i
            }
            add(c)
        }

        return n
    }

    /**
     * Override to provide different source of characters than
     * {@link #input input}.
     */
    internal func nextChar() -> Int? {
        if let next = unicodeIterator.next() {
            return Int(next.value)
        }
        else if unicodeIterator.hasErrorOccurred {
            return nil
        }
        else {
            return nil
        }
    }

    internal func add(_ c: Int) {
        if n >= data.count {
            data += [Int](repeating: 0, count: data.count)
        }
        data[n] = c
        n += 1
    }

    public func LA(_ i: Int) throws -> Int {
        let result = try LA_(i)
        print("LA(\(i)) -> \(result)")
        return result
    }

    private func LA_(_ i: Int) throws -> Int {

        if i == -1 {
            return lastChar // special case
        }
        sync(i)
        let index = p + i - 1
        if index < 0 {
            throw ANTLRError.indexOutOfBounds(msg: "")
        }
        if index >= n {
            return CommonToken.EOF
        }
        return data[index]
    }

    /**
     * Return a marker that we can release later.
     *
     * <p>The specific marker value used for this class allows for some level of
     * protection against misuse where {@code seek()} is called on a mark or
     * {@code release()} is called in the wrong order.</p>
     */
    public func mark() -> Int {
        if numMarkers == 0 {
            lastCharBufferStart = lastChar
        }

        let mark = -numMarkers - 1
        numMarkers += 1
        return mark
    }

    /** Decrement number of markers, resetting buffer if we hit 0.
     * @param marker
     */
    public func release(_ marker: Int) throws {
        let expectedMark = -numMarkers
        if marker != expectedMark {
            preconditionFailure("release() called with an invalid marker.")
        }

        numMarkers -= 1
        if numMarkers == 0 && p > 0 {
            // release buffer when we can, but don't do unnecessary work

            // Copy data[p]..data[n-1] to data[0]..data[(n-1)-p], reset ptrs
            // p is last valid char; move nothing if p==n as we have no valid char
            let dataCapacity = data.capacity
            data = Array(data[p ..< n])
            data += [Int](repeating: 0, count: dataCapacity - (n - p))
            precondition(data.capacity == dataCapacity)
            n = n - p
            p = 0
            lastCharBufferStart = lastChar
        }
    }

    public func index() -> Int {
        return currentCharIndex
    }

    /** Seek to absolute character index, which might not be in the current
     *  sliding window.  Move {@code p} to {@code index-bufferStartIndex}.
     */
    public func seek(_ index_: Int) throws {
        var index = index_

        if index == currentCharIndex {
            return
        }

        if index > currentCharIndex {
            sync(index - currentCharIndex)
            index = min(index, getBufferStartIndex() + n - 1)
        }

        // index == to bufferStartIndex should set p to 0
        let i = index - getBufferStartIndex()
        if i < 0 {
            throw ANTLRError.illegalArgument(msg: "cannot seek to negative index \(index)")
        }
        else if i >= n {
            let si = getBufferStartIndex()
            let ei = si + n
            let msg = "seek to index outside buffer: \(index) not in \(si)..\(ei)"
            throw ANTLRError.unsupportedOperation(msg: msg)
        }

        p = i
        currentCharIndex = index
        if p == 0 {
            lastChar = lastCharBufferStart
        }
        else {
            lastChar = data[p - 1]
        }
    }

    public func size() -> Int {
        preconditionFailure("Unbuffered stream cannot know its size")
    }

    public func getSourceName() -> String {
        return name
    }

    public func getText(_ interval: Interval) throws -> String {
        if interval.a < 0 || interval.b < interval.a - 1 {
            throw ANTLRError.illegalArgument(msg: "invalid interval")
        }

        let bufferStartIndex = getBufferStartIndex()
        if n > 0 &&
            data[n - 1] == CommonToken.EOF &&
            interval.a + interval.length() > bufferStartIndex + n {
            throw ANTLRError.illegalArgument(msg: "the interval extends past the end of the stream")
        }

        if interval.a < bufferStartIndex || interval.b >= bufferStartIndex + n {
            let msg = "interval \(interval) outside buffer: \(bufferStartIndex)...\(bufferStartIndex + n - 1)"
            throw ANTLRError.unsupportedOperation(msg: msg)
        }

        if interval.b < interval.a {
            // The EOF token.
            return ""
        }

        // convert from absolute to local index
        let i = interval.a - bufferStartIndex
        let j = interval.b - bufferStartIndex

        // Convert from Int codepoints to a String.
        let codepoints = data[i ... j].map { Character(Unicode.Scalar($0)!) }
        return String(codepoints)
    }

    internal func getBufferStartIndex() -> Int {
        return currentCharIndex - p
    }
}


fileprivate struct UInt8StreamIterator: IteratorProtocol {
    private static let bufferSize = 1024

    private let stream: InputStream
    private var buffer = [UInt8](repeating: 0, count: UInt8StreamIterator.bufferSize)
    private var buffGen: IndexingIterator<ArraySlice<UInt8>>

    var hasErrorOccurred = false


    init(_ stream: InputStream) {
        self.stream = stream
        self.buffGen = buffer[0..<0].makeIterator()
    }

    mutating func next() -> UInt8? {
        if let result = buffGen.next() {
            return result
        }

        if hasErrorOccurred {
            return nil
        }

        switch stream.streamStatus {
        case .notOpen, .writing, .closed:
            preconditionFailure()
        case .atEnd:
            return nil
        case .error:
            hasErrorOccurred = true
            return nil
        case .opening, .open, .reading:
            break
        }

        let count = stream.read(&buffer, maxLength: buffer.capacity)
        if count <= 0 {
            hasErrorOccurred = true
            return nil
        }

        buffGen = buffer.prefix(count).makeIterator()
        return buffGen.next()
    }
}


fileprivate struct UnicodeScalarStreamIterator: IteratorProtocol {
    private var streamIterator: UInt8StreamIterator
    private var codec = Unicode.UTF8()

    var hasErrorOccurred = false

    init(_ streamIterator: UInt8StreamIterator) {
        self.streamIterator = streamIterator
    }

    mutating func next() -> Unicode.Scalar? {
        if streamIterator.hasErrorOccurred {
            hasErrorOccurred = true
            return nil
        }

        switch codec.decode(&streamIterator) {
        case .scalarValue(let scalar):
            return scalar
        case .emptyInput:
            return nil
        case .error:
            hasErrorOccurred = true
            return nil
        }
    }
}
