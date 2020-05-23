/*
 * Copyright (c) 2012-2020 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.WritableToken;

import antlr.v4.runtime.Token : Token;
import std.variant : Variant;

/**
 * Add write functions for Token attributes
 */
interface WritableToken : Token
{

    public void setText(Variant text);

    public void setType(int ttype);

    public void setLine(int line);

    public void setCharPositionInLine(int charPositionInLine);

    public void setChannel(int channel);

    public void setTokenIndex(size_t index);

}
