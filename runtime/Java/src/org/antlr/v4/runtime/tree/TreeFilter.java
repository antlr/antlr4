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

/**
 Cut-n-paste from material I'm not using in the book anymore (edit later
 to make sense):

 Now, how are we going to test these tree patterns against every
subtree in our original tree?  In what order should we visit nodes?
For this application, it turns out we need a simple ``apply once''
rule application strategy and a ``down then up'' tree traversal
strategy.  Let's look at rule application first.

As we visit each node, we need to see if any of our patterns match. If
a pattern matches, we execute the associated tree rewrite and move on
to the next node. In other words, we only look for a single rule
application opportunity (we'll see below that we sometimes need to
repeatedly apply rules). The following method applies a rule in a @cl
TreeParser (derived from a tree grammar) to a tree:

here is where weReferenced code/walking/patterns/TreePatternMatcher.java

It uses reflection to lookup the appropriate rule within the generated
tree parser class (@cl Simplify in this case). Most of the time, the
rule will not match the tree.  To avoid issuing syntax errors and
attempting error recovery, it bumps up the backtracking level.  Upon
failure, the invoked rule immediately returns. If you don't plan on
using this technique in your own ANTLR-based application, don't sweat
the details. This method boils down to ``call a rule to match a tree,
executing any embedded actions and rewrite rules.''

At this point, we know how to define tree grammar rules and how to
apply them to a particular subtree. The final piece of the tree
pattern matcher is the actual tree traversal. We have to get the
correct node visitation order.  In particular, we need to perform the
scalar-vector multiply transformation on the way down (preorder) and
we need to reduce multiply-by-zero subtrees on the way up (postorder).

To implement a top-down visitor, we do a depth first walk of the tree,
executing an action in the preorder position. To get a bottom-up
visitor, we execute an action in the postorder position.  ANTLR
provides a standard @cl TreeVisitor class with a depth first search @v
visit method. That method executes either a @m pre or @m post method
or both. In our case, we need to call @m applyOnce in both. On the way
down, we'll look for @r vmult patterns. On the way up,
we'll look for @r mult0 patterns.
 */
public class TreeFilter extends TreeParser {
    public interface fptr {
        public void rule() throws RecognitionException;
    }

    protected TokenStream originalTokenStream;
    protected TreeAdaptor originalAdaptor;

    public TreeFilter(TreeNodeStream input) {
        this(input, new RecognizerSharedState());
    }
    public TreeFilter(TreeNodeStream input, RecognizerSharedState state) {
		super(input);
        originalAdaptor = (TreeAdaptor) input.getTreeAdaptor();
        originalTokenStream = input.getTokenStream();
    }

    public void applyOnce(Object t, fptr whichRule) {
        if ( t==null ) return;
        try {
            // share TreeParser object but not parsing-related state
            input = new CommonTreeNodeStream(originalAdaptor, t);
            ((CommonTreeNodeStream)input).setTokenStream(originalTokenStream);
            whichRule.rule();
        }
        catch (RecognitionException e) { ; }
    }

    public void downup(Object t) {
        TreeVisitor v = new TreeVisitor(new CommonTreeAdaptor());
        TreeVisitorAction actions = new TreeVisitorAction() {
            public Object pre(Object t)  { applyOnce(t, topdown_fptr); return t; }
            public Object post(Object t) { applyOnce(t, bottomup_fptr); return t; }
        };
        v.visit(t, actions);
    }

    fptr topdown_fptr = new fptr() {
        public void rule() throws RecognitionException {
            topdown();
        }
    };

    fptr bottomup_fptr = new fptr() {
        public void rule() throws RecognitionException {
            bottomup();
        }
    };

    // methods the downup strategy uses to do the up and down rules.
    // to override, just define tree grammar rule topdown and turn on
    // filter=true.
    public void topdown() throws RecognitionException {;}
    public void bottomup() throws RecognitionException {;}
}
