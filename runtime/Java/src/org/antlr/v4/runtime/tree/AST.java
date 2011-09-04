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

import org.antlr.v4.runtime.Token;

import java.util.List;

/** An abstract syntax tree built by ANTLR during a parcel or tree parse. */
public interface AST extends SyntaxTree {
    /** Is there is a node above with token type ttype? */
    public boolean hasAncestor(int ttype);

    /** Walk upwards and get first ancestor with this token type. */
    public AST getAncestor(int ttype);

    /** Return a list of all ancestors of this node.  The first node of
     *  list is the root and the last is the parent of this node.
     */
    public List getAncestors();

    /** This node is what child index? 0..n-1 */
	public int getChildIndex();

	/** Set the parent and child index values for all children */
	public void freshenParentAndChildIndexes();

	/** Indicates the node is a nil node but may still have children, meaning
	 *  the tree is a flat list.
	 */
	boolean isNil();

	/** Return a token type; needed for tree parsing */
	int getType();

	/** Text for this node alone, not the subtree */
	String getText();

	// TODO: do we need line/charpos????
	/** In case we don't have a token payload, what is the line for errors? */
	int getLine();

	int getCharPositionInLine();

	/** Redefined from Tree interface so we can narrow the return type */
	AST getParent();

	/** Redefined from Tree interface so we can narrow the return type */
	Token getPayload();
}
