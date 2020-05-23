/*
 * Copyright (c) 2012-2019 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.ReplaceOp;

import antlr.v4.runtime.RewriteOperation;
import std.conv;
import std.format;
import std.variant;

/**
 * I'm going to try replacing range from x..y with (y-x)+1 ReplaceOp
 *  instructions.
 */
class ReplaceOp : RewriteOperation
{

    public size_t lastIndex;

    public this(size_t from, size_t to, Variant text)
    {
        super(from, text);
        lastIndex = to;
    }

    /**
     * @uml
     * @override
     */
    public override size_t execute(ref Variant buf)
    {
        Variant Null; // only for compare
        if (text !is Null) {
            buf is Null ? buf = text : (buf ~= text);
        }
        return lastIndex+1;
    }

    /**
     * @uml
     * @override
     */
    public override string toString()
    {
        Variant Null;
        if (text is Null)
            {
                return format("<DeleteOp@%s..%s>", tokens.get(to!int(index)),
                              tokens.get(to!int(lastIndex)));
            }
        return format("<ReplaceOp@%s..%s:\"%s\">", tokens.get(to!int(index)),
                      tokens.get(to!int(lastIndex)), text);
    }

}
