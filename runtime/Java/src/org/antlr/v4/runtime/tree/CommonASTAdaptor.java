/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.*;

/** An ASTAdaptor that works with CommonAST.  It provides
 *  really just factory methods; all the work is done by BaseTreeAdaptor.
 *  If you would like to have different tokens created than CommonToken
 *  objects, you need to override this and then set the parser tree adaptor to
 *  use your subclass.
 *
 *  To get your parser to build nodes of a different type, override
 *  create(Token).
 */
public class CommonASTAdaptor extends BaseASTAdaptor<CommonAST> {
	/** Duplicate a node.  This is part of the factory;
	 *	override if you want another kind of node to be built.
	 *
	 *  I could use reflection to prevent having to override this
	 *  but reflection is slow.
	 */
	public CommonAST dupNode(CommonAST t) {
		if ( t==null ) return null;
		return new CommonAST(t);
	}

	public CommonAST create(Token payload) {
		return new CommonAST(payload);
	}



	/** Tell me how to create a token for use with imaginary token nodes.
	 *  For example, there is probably no input symbol associated with imaginary
	 *  token DECL, but you need to create it as a payload or whatever for
	 *  the DECL node as in ^(DECL type ID).
	 *
	 *  If you care what the token payload objects' type is, you should
	 *  override this method and any other createToken variant.
	 */
	@Override
	public WritableToken createToken(int tokenType, String text) {
		return new CommonToken(tokenType, text);
	}

	/** Tell me how to create a token for use with imaginary token nodes.
	 *  For example, there is probably no input symbol associated with imaginary
	 *  token DECL, but you need to create it as a payload or whatever for
	 *  the DECL node as in ^(DECL type ID).
	 *
	 *  This is a variant of createToken where the new token is derived from
	 *  an actual real input token.  Typically this is for converting '{'
	 *  tokens to BLOCK etc...  You'll see
	 *
	 *    r : lc='{' ID+ '}' -> ^(BLOCK[$lc] ID+) ;
	 *
	 *  If you care what the token payload objects' type is, you should
	 *  override this method and any other createToken variant.
	 */
	@Override
	public WritableToken createToken(Token fromToken) {
		return new CommonToken(fromToken);
	}

	/** Track start/stop token for subtree root created for a rule.
	 *  Only works with CommonAST nodes.  For rules that match nothing,
	 *  seems like this will yield start=i and stop=i-1 in a nil node.
	 *  Might be useful info so I'll not force to be i..i.
	 */
	public void setTokenBoundaries(CommonAST t, Token startToken, Token stopToken) {
		if ( t==null ) return;
		int start = 0;
		int stop = 0;
		if ( startToken!=null ) start = startToken.getTokenIndex();
		if ( stopToken!=null ) stop = stopToken.getTokenIndex();
		t.setTokenStartIndex(start);
		t.setTokenStopIndex(stop);
	}

	public int getTokenStartIndex(CommonAST t) {
		if ( t==null ) return -1;
		return t.getTokenStartIndex();
	}

	public int getTokenStopIndex(CommonAST t) {
		if ( t==null ) return -1;
		return t.getTokenStopIndex();
	}

	public String getText(CommonAST t) {
		if ( t==null ) return null;
		return t.getText();
	}

    public int getType(CommonAST t) {
		if ( t==null ) return Token.INVALID_TYPE;
		return t.getType();
	}

	/** What is the Token associated with this node?  If
	 *  you are not using CommonAST, then you must
	 *  override this in your own adaptor.
	 */
	public Token getToken(CommonAST t) {
		if ( t==null ) return null;
		return t.getToken();
	}

	public CommonAST getChild(CommonAST t, int i) {
		if ( t==null ) return null;
        return t.getChild(i);
    }

    public int getChildCount(CommonAST t) {
		if ( t==null ) return 0;
        return t.getChildCount();
    }

	public CommonAST getParent(CommonAST t) {
		if ( t==null ) return null;
        return t.getParent();
	}

	public void setParent(CommonAST t, CommonAST parent) {
        if ( t!=null ) t.setParent((CommonAST)parent);
	}

	public int getChildIndex(CommonAST t) {
        if ( t==null ) return 0;
		return t.getChildIndex();
	}

	public void setChildIndex(CommonAST t, int index) {
        if ( t!=null ) t.setChildIndex(index);
	}

	public void replaceChildren(CommonAST parent, int startChildIndex, int stopChildIndex, CommonAST t) {
		if ( parent!=null ) {
			Trees.replaceChildren((CommonAST)parent, startChildIndex, stopChildIndex, t);
		}
	}
}
