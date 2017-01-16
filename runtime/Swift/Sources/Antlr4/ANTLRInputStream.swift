/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// Vacuum all input from a {@link java.io.Reader}/{@link java.io.InputStream} and then treat it
/// like a {@code char[]} buffer. Can also pass in a {@link String} or
/// {@code char[]} to use.
/// 
/// <p>If you need encoding, pass in stream/reader with correct encoding.</p>

public class ANTLRInputStream: CharStream {
    public static let READ_BUFFER_SIZE: Int = 1024
    public static let INITIAL_BUFFER_SIZE: Int = 1024

    /// The data being scanned
    internal var data: [Character]

    /// How many characters are actually in the buffer
    internal var n: Int

    /// 0..n-1 index into string of next char
    internal var p: Int = 0

    /// What is name or source of this char stream?
    public var name: String?

    public init() {
        n = 0
        data = [Character]()
    }

    /// Copy data in string to a local char array
    public init(_ input: String) {
        self.data = Array(input.characters) // input.toCharArray();
        self.n = input.length
    }

    /// This is the preferred constructor for strings as no data is copied
    public init(_ data: [Character], _ numberOfActualCharsInArray: Int) {
        self.data = data
        self.n = numberOfActualCharsInArray
    }
    /// public convenience init(_ r : Reader) throws; IOException {
    /// self.init(r, INITIAL_BUFFER_SIZE, READ_BUFFER_SIZE);
    /// }
    /// 
    /// public convenience init(_ r : Reader, _ initialSize : Int) throws; IOException {
    /// self.init(r, initialSize, READ_BUFFER_SIZE);
    /// }
    /// 
    /// public init(_ r : Reader, _ initialSize : Int, _ readChunkSize : Int) throws; IOException {
    /// load(r, initialSize, readChunkSize);
    /// }
    /// 
    /// public convenience init(_ input : InputStream) throws; IOException {
    /// self.init(InputStreamReader(input), INITIAL_BUFFER_SIZE);
    /// }
    /// 
    /// public convenience init(_ input : InputStream, _ initialSize : Int) throws; IOException {
    /// self.init(InputStreamReader(input), initialSize);
    /// }
    /// 
    /// public convenience init(_ input : InputStream, _ initialSize : Int, _ readChunkSize : Int) throws; IOException {
    /// self.init(InputStreamReader(input), initialSize, readChunkSize);
    /// }
    /// 
    /// public func load(r : Reader, _ size : Int, _ readChunkSize : Int)
    /// throws; IOException
    /// {
    /// if ( r==nil ) {
    /// return;
    /// }
    /// if ( size<=0 ) {
    /// size = INITIAL_BUFFER_SIZE;
    /// }
    /// if ( readChunkSize<=0 ) {
    /// readChunkSize = READ_BUFFER_SIZE;
    /// }
    /// // print("load "+size+" in chunks of "+readChunkSize);
    /// try {
    /// // alloc initial buffer size.
    /// data = new char[size];
    /// // read all the data in chunks of readChunkSize
    /// var numRead : Int=0;
    /// var p : Int = 0;
    /// do {
    /// if ( p+readChunkSize > data.length ) { // overflow?
    /// // print("### overflow p="+p+", data.length="+data.length);
    /// data = Arrays.copyOf(data, data.length * 2);
    /// }
    /// numRead = r.read(data, p, readChunkSize);
    /// // print("read "+numRead+" chars; p was "+p+" is now "+(p+numRead));
    /// p += numRead;
    /// } while (numRead!=-1); // while not EOF
    /// // set the actual size of the data available;
    /// // EOF subtracted one above in p+=numRead; add one back
    /// n = p+1;
    /// //print("n="+n);
    /// }
    /// finally {
    /// r.close();
    /// }
    /// }
    /// Reset the stream so that it's in the same state it was
    /// when the object was created *except* the data array is not
    /// touched.

    public func reset() {
        p = 0
    }


    public func consume() throws {
        if p >= n {
            assert(LA(1) == ANTLRInputStream.EOF, "Expected: LA(1)==IntStream.EOF")

            throw ANTLRError.illegalState(msg: "annot consume EOF")

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

    /// Return the current input symbol index 0..n where n indicates the
    /// last symbol has been read.  The index is the index of char to
    /// be returned from LA(1).
    public func index() -> Int {
        return p
    }

    public func size() -> Int {
        return n
    }

    /// mark/release do nothing; we have entire buffer

    public func mark() -> Int {
        return -1
    }

    public func release(_ marker: Int) {
    }

    /// consume() ahead until p==index; can't just set p=index as we must
    /// update line and charPositionInLine. If we seek backwards, just set p

    public func seek(_ index: Int) throws {
        var index = index
        if index <= p {
            p = index // just jump; don't update stream state (line, ...)
            return
        }
        // seek forward, consume until p hits index or n (whichever comes first)
        index = min(index, n)
        while p < index {
            try  consume()
        }
    }


    public func getText(_ interval: Interval) -> String {
        let start: Int = interval.a
        var stop: Int = interval.b
        if stop >= n {
            stop = n - 1
        }
        let count = stop - start + 1;
        if start >= n {
            return ""
        }

        return String(data[start ..< (start + count)])
    }


    public func getSourceName() -> String {
        guard let name = name , !name.isEmpty else {
             return ANTLRInputStream.UNKNOWN_SOURCE_NAME
        }
        return name
    }


    public func toString() -> String {
        return String(data)
    }
}
