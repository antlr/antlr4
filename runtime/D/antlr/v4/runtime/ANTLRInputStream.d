/*
 * Copyright (c) 2012-2020 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.ANTLRInputStream;

import antlr.v4.runtime.CharStream;
import antlr.v4.runtime.IntStream;
import antlr.v4.runtime.IntStreamConstant;
import antlr.v4.runtime.misc.Interval;
import std.algorithm;
import std.conv : to;
import std.file;
import std.format;
import std.range;
import std.stdio;
import std.utf;

/**
 * Vacuum all input from a {@link Reader}/{@link InputStream} and then treat it
 * like a {@code char[]} buffer. Can also pass in a {@link String} or
 * {@code char[]} to use.
 *
 * <p>If you need encoding, pass in stream/reader with correct encoding.</p>
 */
class ANTLRInputStream : CharStream
{

    /**
     * The UTF-8 data being scanned
     */
    protected char[] data;

    /**
     * How many UCS code_points are actually in the buffer
     */
    protected size_t cp_in_buffer;

    /**
     * index of next UTF-8 character
     */
    protected size_t index_of_next_char = 0;

    /**
     * What is name or source of this char stream?
     */
    public string name;

    public this()
    {
    }

    /**
     * Copy data in string to a local char array
     */
    public this(string input)
    {
        data = input.to!(char []);
        cp_in_buffer = data.toUCSindex(data.length);
    }

    /**
     * This is the preferred constructor for strings as no data is copied
     */
    public this(char[] data, size_t numberOfActualCharsInArray)
    {
        this.data = data.to!(char []);
        cp_in_buffer = data.toUCSindex(data.length);
    }

    public this(File r)
    {
        load(r);
    }

    public void load(File r)
    {
        name = r.name;
        data = to!(char[])(name.readText);
        // set the actual size of the data available;
        cp_in_buffer = data.toUCSindex(data.length);
        debug (ANTLRInputStreamStream)
            writefln!"name = %s; cp_in_buffer = $s"(
                     name, cp_in_buffer);
    }

    /**
     * Reset the stream so that it's in the same state it was
     * when the object was created *except* the data array is not
     * touched.
     */
    public void reset()
    {
        index_of_next_char = 0;
    }

    /**
     * @uml
     * @override
     */
    public override void consume()
    {
        if (index_of_next_char >= cp_in_buffer) {
            assert (LA(1) == IntStreamConstant.EOF, "cannot consume EOF");
        }

        debug (ANTLRInputStream)
        {
            import std.stdio;
            writefln!"consume; prev index_of_next_char= %s, data[index_of_next_character] = %s"(
                     index_of_next_char,
                     front(data[data.toUTFindex(index_of_next_char) .. $]));
        }

        if (index_of_next_char < cp_in_buffer)
        {
            index_of_next_char++;

            debug (ANTLRInputStream)
            {
                import std.stdio;
                writefln!"p moves to %s (c='%s')"(
                         index_of_next_char,
                         cast(char)data[index_of_next_char]);
            }

        }
    }

    /**
     * UTF-8 coded character mapped to UTF-32
     * @uml
     * @override
     */
    public override dchar LA(int i)
    {
        if (i == 0)
        {
            return to!dchar(0); // undefined
        }
        if (i < 0)
        {
            i++; // e.g., translate LA(-1) to use offset i=0; then data[index_of_next_character+0-1]
            if ((index_of_next_char + i - 1) < 0)
            {
                return to!dchar(IntStreamConstant.EOF); // invalid; no char before first char
            }
        }
        if (( index_of_next_char + i - 1) >= cp_in_buffer)
        {
            return to!dchar(IntStreamConstant.EOF);
        }
        return front(data[data.toUTFindex(index_of_next_char + i - 1) .. $]);
    }

    public dchar LT(int i)
    {
        return LA(i);
    }

    /**
     * @uml
     * @override
     */
    public override size_t index()
    {
        return index_of_next_char;
    }

    /**
     * @uml
     * @override
     */
    public override size_t size()
    {
        return cp_in_buffer;
    }

    /**
     * mark/release do nothing; we have entire buffer
     * @uml
     * @override
     */
    public override int mark()
    {
        return -1;
    }

    /**
     * @uml
     * @override
     */
    public override void release(int marker)
    {
    }

    /**
     * consume() ahead until index_of_next_character==index;
     * can't just set index_of_next_character=index as we must
     * update line and charPositionInLine. If we seek backwards,
     * just set index_of_next_character
     * @uml
     * @override
     */
    public override void seek(size_t index)
    {
        if (index <= index_of_next_char)
        {
            index_of_next_char= index; // just jump; don't update stream state (line, ...)
            return;
        }
        // seek forward, consume until next code point hits index or cp_in_buffer
        // (whichever comes first)
        index = min(index, cp_in_buffer);
        while (index_of_next_char < index)
        {
            consume();
        }
    }

    /**
     * @uml
     * @override
     */
    public override string getText(Interval interval)
    {
        int start = interval.a;
        int stop = interval.b;
        if (stop >= to!int(cp_in_buffer))
            stop = to!int(cp_in_buffer)-1;
        if (start >= to!int(cp_in_buffer)) return "";

        debug (ANTLRInputStream)
        {
            writefln!"data: start=%s, stop=%s, string = %s"(
                     start, stop,
                     data[data.toUTFindex(start)..data.toUTFindex(stop+1)]);
        }

        return to!string(data[data.toUTFindex(start)..data.toUTFindex(stop+1)]);
    }

    /**
     * @uml
     * @override
     */
    public override string getSourceName()
    {
        if (!name)
        {
            return IntStreamConstant.UNKNOWN_SOURCE_NAME;
        }
        return name;
    }

    /**
     * @uml
     * @override
     */
    public override string toString()
    {
        return to!string(data);
    }

}
