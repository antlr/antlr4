/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
///

///
/// Vacuum all input from a _java.io.Reader_/_java.io.InputStream_ and then treat it
/// like a `char[]` buffer. Can also pass in a _String_ or
/// `char[]` to use.
/// 
/// If you need encoding, pass in stream/reader with correct encoding.
///
public class ANTLRInputStream: CharStream {
    ///
    /// The data being scanned
    /// 
    internal var data: [Character]

    /// 
    /// How many characters are actually in the buffer
    /// 
    internal var n: Int

    /// 
    /// 0...n-1 index into string of next char
    /// 
    internal var p = 0

    /// 
    /// What is name or source of this char stream?
    /// 
    public var name: String?

    public init() {
        n = 0
        data = [Character]()
    }

    /// 
    /// Copy data in string to a local char array
    /// 
    public init(_ input: String) {
        self.data = Array(input)
        self.n = data.count
    }

    /// 
    /// This is the preferred constructor for strings as no data is copied
    /// 
    public init(_ data: [Character], _ numberOfActualCharsInArray: Int) {
        self.data = data
        self.n = numberOfActualCharsInArray
    }

    public func reset() {
        p = 0
    }

    public func consume() throws {
        if p >= n {
            assert(LA(1) == ANTLRInputStream.EOF, "Expected: LA(1)==IntStream.EOF")

            throw ANTLRError.illegalState(msg: "cannot consume EOF")

        }

        // print("prev p="+p+", c="+(char)data[p]);
        if p < n {
            p += 1
            //print("p moves to "+p+" (c='"+(char)data[p]+"')");
        }
    }

    public func LA(_ i: Int) -> Int {
        var i = i
        if i == 0 {
            return 0 // undefined
        }
        if i < 0 {
            i += 1 // e.g., translate LA(-1) to use offset i=0; then data[p+0-1]
            if (p + i - 1) < 0 {
                return ANTLRInputStream.EOF// invalid; no char before first char
            }
        }

        if (p + i - 1) >= n {
            //print("char LA("+i+")=EOF; p="+p);
            return ANTLRInputStream.EOF
        }
        //print("char LA("+i+")="+(char)data[p+i-1]+"; p="+p);
        //print("LA("+i+"); p="+p+" n="+n+" data.length="+data.length);
        return data[p + i - 1].unicodeValue
    }

    public func LT(_ i: Int) -> Int {
        return LA(i)
    }

    /// 
    /// Return the current input symbol index 0...n where n indicates the
    /// last symbol has been read.  The index is the index of char to
    /// be returned from LA(1).
    /// 
    public func index() -> Int {
        return p
    }

    public func size() -> Int {
        return n
    }

    /// 
    /// mark/release do nothing; we have entire buffer
    /// 

    public func mark() -> Int {
        return -1
    }

    public func release(_ marker: Int) {
    }

    /// 
    /// consume() ahead until p==index; can't just set p=index as we must
    /// update line and charPositionInLine. If we seek backwards, just set p
    /// 

    public func seek(_ index: Int) throws {
        var index = index
        if index <= p {
            p = index // just jump; don't update stream state (line, ...)
            return
        }
        // seek forward, consume until p hits index or n (whichever comes first)
        index = min(index, n)
        while p < index {
            try consume()
        }
    }

    public func getText(_ interval: Interval) -> String {
        let start = interval.a
        if start >= n {
            return ""
        }
        let stop = min(n, interval.b + 1)
        return String(data[start ..< stop])
    }

    public func getSourceName() -> String {
        return name ?? ANTLRInputStream.UNKNOWN_SOURCE_NAME
    }

    public func toString() -> String {
        return String(data)
    }
}
