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
import org.antlr.v4.runtime.misc.LookaheadStream;

import java.util.Stack;

public class CommonASTNodeStream<T> extends LookaheadStream<T>
	implements ASTNodeStream<T>
{
	public static final int DEFAULT_INITIAL_BUFFER_SIZE = 100;
	public static final int INITIAL_CALL_STACK_SIZE = 10;

	/** Pull nodes from which tree? */
	protected T root;

	/** If this tree (root) was created from a token stream, track it. */
	protected TokenStream tokens;

	/** What tree adaptor was used to build these trees */
	ASTAdaptor<T> adaptor;

    /** The tree iterator we using */
    protected ASTIterator<T> it;

    /** Stack of indexes used for push/pop calls */
    protected Stack<Integer> calls;

    /** Tree (nil A B C) trees like flat A B C streams */
    protected boolean hasNilRoot = false;

    /** Tracks tree depth.  Level=0 means we're at root node level. */
    protected int level = 0;

	public CommonASTNodeStream(T tree) {
		this((tree instanceof CommonAST)?(ASTAdaptor<T>)new CommonASTAdaptor():null,
			 tree);
	}

	public CommonASTNodeStream(ASTAdaptor<T> adaptor, T tree) {
		this.root = tree;
		this.adaptor = adaptor;
        it = new ASTIterator<T>(adaptor,root);
	}

    @Override
    public void reset() {
        super.reset();
        it.reset();
        hasNilRoot = false;
        level = 0;
        if ( calls != null ) calls.clear();
    }

    /** Pull elements from tree iterator.  Track tree level 0..max_level.
     *  If nil rooted tree, don't give initial nil and DOWN nor final UP.
     */
    @Override
    public T nextElement() {
        T t = it.next();
        //System.out.println("pulled "+adaptor.getType(t));
        if ( t == it.up ) {
            level--;
            if ( level==0 && hasNilRoot ) return it.next(); // don't give last UP; get EOF
        }
        else if ( t == it.down ) level++;
        if ( level==0 && adaptor.isNil(t) ) { // if nil root, scarf nil, DOWN
            hasNilRoot = true;
            t = it.next(); // t is now DOWN, so get first real node next
            level++;
            t = it.next();
        }
        return t;
    }

    @Override
    public boolean isEOF(T o) { return adaptor.getType(o) == Token.EOF; }

    @Override
    public void setUniqueNavigationNodes(boolean uniqueNavigationNodes) { }

	@Override
	public T getTreeSource() {	return root; }

	@Override
	public String getSourceName() { return getTokenStream().getSourceName(); }

	@Override
	public TokenStream getTokenStream() { return tokens; }

	public void setTokenStream(TokenStream tokens) { this.tokens = tokens; }

	@Override
	public ASTAdaptor<T> getTreeAdaptor() { return adaptor; }

	public void setTreeAdaptor(ASTAdaptor<T> adaptor) { this.adaptor = adaptor; }

    @Override
    public T get(int i) {
        throw new UnsupportedOperationException("Absolute node indexes are meaningless in an unbuffered stream");
    }

    @Override
    public int LA(int i) { return adaptor.getType(LT(i)); }

    /** Make stream jump to a new location, saving old location.
     *  Switch back with pop().
     */
    public void push(int index) {
        if ( calls==null ) {
            calls = new Stack<Integer>();
        }
        calls.push(p); // save current index
        seek(index);
    }

    /** Seek back to previous index saved during last push() call.
     *  Return top of stack (return index).
     */
    public int pop() {
        int ret = calls.pop();
        seek(ret);
        return ret;
    }

	// TREE REWRITE INTERFACE

	@Override
	public void replaceChildren(T parent, int startChildIndex, int stopChildIndex, T t) {
		if ( parent!=null ) {
			adaptor.replaceChildren(parent, startChildIndex, stopChildIndex, t);
		}
	}

	/** Print the token text between start and stop nodes. If stop is an UP
	 *  node, then we have to walk it back until we see the first non-UP node.
	 *  Then, just get the token indexes and look into the token stream.
	 */
	@Override
	public String toString(T start, T stop) {
		if ( tokens==null ) throw new UnsupportedOperationException("can't print from null token stream in node stream");
		if ( start==null || stop==null ) return "";
		Token startToken = adaptor.getToken(start);
		Token stopToken = adaptor.getToken(stop);
		while ( stopToken.getType()==Token.UP ) {
			stopToken = adaptor.getToken(stop);
		}
		return tokens.toString(startToken.getTokenIndex(), stopToken.getTokenIndex());
	}

    /** For debugging; destructive: moves tree iterator to end. */
    public String toTokenTypeString() {
        reset();
		StringBuffer buf = new StringBuffer();
        T o = LT(1);
        int type = adaptor.getType(o);
        while ( type!=Token.EOF ) {
            buf.append(" ");
            buf.append(type);
            consume();
            o = LT(1);
            type = adaptor.getType(o);
		}
		return buf.toString();
    }
}
