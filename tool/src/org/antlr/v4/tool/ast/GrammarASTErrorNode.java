/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.tool.ast;

import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonErrorNode;

/** A node representing erroneous token range in token stream */
public class GrammarASTErrorNode extends GrammarAST {
    CommonErrorNode delegate;
    public GrammarASTErrorNode(TokenStream input, Token start, Token stop,
                               org.antlr.runtime.RecognitionException e)
    {
        delegate = new CommonErrorNode(input,start,stop,e);
    }

    @Override
    public boolean isNil() { return delegate.isNil(); }

    @Override
    public int getType() { return delegate.getType(); }

    @Override
    public String getText() { return delegate.getText(); }
    @Override
    public String toString() { return delegate.toString(); }
}
