/*
 * Copyright (c) 2012-2019 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.RewriteOperation;

import antlr.v4.runtime.TokenStreamRewriter;
import std.conv;
import std.format;
import std.variant;

/**
 * TODO add class description
 */
class RewriteOperation : TokenStreamRewriter
{

    /**
     * What index into rewrites List are we?
     */
    public size_t instructionIndex;

    /**
     * Token buffer index.
     */
    public size_t index;

    public Variant text;

    public this(size_t index)
    {
        this.index = index;
    }

    public this(size_t index, Variant text)
    {
        this.index = index;
        this.text = text;
    }

    /**
     * Execute the rewrite operation by possibly adding to the buffer.
     *  Return the index of the next token to operate on.
     */
    public size_t execute(ref Variant buf)
    {
        return index;
    }

    /**
     * @uml
     * @override
     */
    public override string toString()
    {
        import std.array : split;
        auto opName = this.classinfo.name.split(".")[$-1];
        return format("<%s@%s:\"%s\">", opName, tokens.get(to!int(index)), text);
    }

}
