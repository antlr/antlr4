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
import org.antlr.v4.runtime.misc.FastQueue;

import java.util.Iterator;

/** Return a node stream from a doubly-linked tree whose nodes
 *  know what child index they are.  No remove() is supported.
 *
 *  Emit navigation nodes (DOWN, UP, and EOF) to let show tree structure.
 */
public class TreeIterator implements Iterator {
    protected ASTAdaptor adaptor;
    protected Object root;
    protected Object tree;
    protected boolean firstTime = true;

    // navigation nodes to return during walk and at end
    public Object up;
    public Object down;
    public Object eof;

    /** If we emit UP/DOWN nodes, we need to spit out multiple nodes per
     *  next() call.
     */
    protected FastQueue nodes;

    public TreeIterator(Object tree) {
        this(new CommonASTAdaptor(),tree);
    }

    public TreeIterator(ASTAdaptor adaptor, Object tree) {
        this.adaptor = adaptor;
        this.tree = tree;
        this.root = tree;
        nodes = new FastQueue();
        down = adaptor.create(Token.DOWN, "DOWN");
        up = adaptor.create(Token.UP, "UP");
        eof = adaptor.create(Token.EOF, "EOF");
    }

    public void reset() {
        firstTime = true;
        tree = root;
        nodes.clear();
    }

    public boolean hasNext() {
        if ( firstTime ) return root!=null;
        if ( nodes!=null && nodes.size()>0 ) return true;
        if ( tree==null ) return false;
        if ( adaptor.getChildCount(tree)>0 ) return true;
        return adaptor.getParent(tree)!=null; // back at root?
    }

    public Object next() {
        if ( firstTime ) { // initial condition
            firstTime = false;
            if ( adaptor.getChildCount(tree)==0 ) { // single node tree (special)
                nodes.add(eof);
                return tree;
            }
            return tree;
        }
        // if any queued up, use those first
        if ( nodes!=null && nodes.size()>0 ) return nodes.remove();

        // no nodes left?
        if ( tree==null ) return eof;

        // next node will be child 0 if any children
        if ( adaptor.getChildCount(tree)>0 ) {
            tree = adaptor.getChild(tree, 0);
            nodes.add(tree); // real node is next after DOWN
            return down;
        }
        // if no children, look for next sibling of tree or ancestor
        Object parent = adaptor.getParent(tree);
        // while we're out of siblings, keep popping back up towards root
        while ( parent!=null &&
                adaptor.getChildIndex(tree)+1 >= adaptor.getChildCount(parent) )
        {
            nodes.add(up); // we're moving back up
            tree = parent;
            parent = adaptor.getParent(tree);
        }
        // no nodes left?
        if ( parent==null ) {
            tree = null; // back at root? nothing left then
            nodes.add(eof); // add to queue, might have UP nodes in there
            return nodes.remove();
        }

        // must have found a node with an unvisited sibling
        // move to it and return it
        int nextSiblingIndex = adaptor.getChildIndex(tree) + 1;
        tree = adaptor.getChild(parent, nextSiblingIndex);
        nodes.add(tree); // add to queue, might have UP nodes in there
        return nodes.remove();
    }

    public void remove() { throw new UnsupportedOperationException(); }
}
