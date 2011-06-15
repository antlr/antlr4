/*
 [The "BSD license"]
 Copyright (c) 2005-2009 Terence Parr
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

/** A TreeAdaptor that works with any Tree implementation.  It provides
 *  really just factory methods; all the work is done by BaseTreeAdaptor.
 *  If you would like to have different tokens created than ClassicToken
 *  objects, you need to override this and then set the parser tree adaptor to
 *  use your subclass.
 *
 *  To get your parser to build nodes of a different type, override
 *  create(Token), errorNode(), and to be safe, YourTreeClass.dupNode().
 *  dupNode is called to duplicate nodes during rewrite operations.
 */
public class CommonTreeAdaptor extends BaseTreeAdaptor {
	/** Duplicate a node.  This is part of the factory;
	 *	override if you want another kind of node to be built.
	 *
	 *  I could use reflection to prevent having to override this
	 *  but reflection is slow.
	 */
	public Object dupNode(Object t) {
		if ( t==null ) return null;
		return ((Tree)t).dupNode();
	}

	public Object create(Token payload) {
		return new CommonTree(payload);
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
	public Token createToken(int tokenType, String text) {
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
	public Token createToken(Token fromToken) {
		return new CommonToken(fromToken);
	}

	/** Track start/stop token for subtree root created for a rule.
	 *  Only works with Tree nodes.  For rules that match nothing,
	 *  seems like this will yield start=i and stop=i-1 in a nil node.
	 *  Might be useful info so I'll not force to be i..i.
	 */
	public void setTokenBoundaries(Object t, Token startToken, Token stopToken) {
		if ( t==null ) return;
		int start = 0;
		int stop = 0;
		if ( startToken!=null ) start = startToken.getTokenIndex();
		if ( stopToken!=null ) stop = stopToken.getTokenIndex();
		((Tree)t).setTokenStartIndex(start);
		((Tree)t).setTokenStopIndex(stop);
	}

	public int getTokenStartIndex(Object t) {
		if ( t==null ) return -1;
		return ((Tree)t).getTokenStartIndex();
	}

	public int getTokenStopIndex(Object t) {
		if ( t==null ) return -1;
		return ((Tree)t).getTokenStopIndex();
	}

	public String getText(Object t) {
		if ( t==null ) return null;
		return ((Tree)t).getText();
	}

    public int getType(Object t) {
		if ( t==null ) return Token.INVALID_TYPE;
		return ((Tree)t).getType();
	}

	/** What is the Token associated with this node?  If
	 *  you are not using CommonTree, then you must
	 *  override this in your own adaptor.
	 */
	public Token getToken(Object t) {
		if ( t instanceof CommonTree ) {
			return ((CommonTree)t).getToken();
		}
		return null; // no idea what to do
	}

	public Object getChild(Object t, int i) {
		if ( t==null ) return null;
        return ((Tree)t).getChild(i);
    }

    public int getChildCount(Object t) {
		if ( t==null ) return 0;
        return ((Tree)t).getChildCount();
    }

	public Object getParent(Object t) {
		if ( t==null ) return null;
        return ((Tree)t).getParent();
	}

	public void setParent(Object t, Object parent) {
        if ( t!=null ) ((Tree)t).setParent((Tree)parent);
	}

	public int getChildIndex(Object t) {
        if ( t==null ) return 0;
		return ((Tree)t).getChildIndex();
	}

	public void setChildIndex(Object t, int index) {
        if ( t!=null ) ((Tree)t).setChildIndex(index);
	}

	public void replaceChildren(Object parent, int startChildIndex, int stopChildIndex, Object t) {
		if ( parent!=null ) {
			((Tree)parent).replaceChildren(startChildIndex, stopChildIndex, t);
		}
	}
}
