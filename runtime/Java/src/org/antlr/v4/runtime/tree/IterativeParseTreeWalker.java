/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.runtime.tree;

/**
 * An iterative (read: non-recursive) pre-order and post-order tree walker that
 * doesn't use the thread stack but it's own heap-based stack using linked data
 * structures. Makes it possible to process deeply nested parse trees.
 */
public class IterativeParseTreeWalker extends ParseTreeWalker {
    public static final ParseTreeWalker DEFAULT = new IterativeParseTreeWalker();

    @Override
    public void walk(final ParseTreeListener listener, final ParseTree t) {
        for (WalkerNode<?> currentNode = createNode(null, t, 0); currentNode != null;) {
            currentNode.enter(listener); // Pre order visit

            // Move down to first child
            WalkerNode<?> nextNode = currentNode.getFirstChild();
            if (nextNode != null) {
                currentNode = nextNode;
                continue;
            }

            // No child nodes, so walk tree
            while (currentNode != null) {
                currentNode.exit(listener); // Post order visit

                // Move to sibling if possible
                nextNode = currentNode.getNextSibling();
                if (nextNode != null) {
                    currentNode = nextNode;
                    break;
                }

                // Move up
                currentNode = currentNode.getParentNode();
            }
        }
    }

    protected WalkerNode<?> createNode(final WalkerNode<?> parent, final ParseTree self, final int pos) {
        if (self instanceof ErrorNode) {
            return new ErrorWalkerNode(parent, (ErrorNode) self, pos);
        } else if (self instanceof TerminalNode) {
            return new TerminalWalkerNode(parent, (TerminalNode) self, pos);
        } else {
            return new RuleWalkerNode(parent, (RuleNode) self, pos);
        }
    }

    protected abstract class WalkerNode<N extends ParseTree> {
        private final WalkerNode<?> parent;
        protected final N self;
        private final int pos;

        protected WalkerNode(final WalkerNode<?> parent, final N self, final int pos) {
            this.parent = parent;
            this.self = self;
            this.pos = pos;
        }

        public WalkerNode<?> getFirstChild() {
            return self.getChildCount() > 0 ? createChildNode(0) : null;
        }

        public WalkerNode<?> getNextSibling() {
            return parent != null && parent.self.getChildCount() > pos + 1 ? parent.createChildNode(pos + 1) : null;
        }

        public WalkerNode<?> getParentNode() {
            return parent;
        }

        public abstract void enter(ParseTreeListener listener);

        public void exit(final ParseTreeListener listener) {
            // defaults to do nothing
        }

        protected WalkerNode<?> createChildNode(final int pos) {
            return createNode(this, self.getChild(pos), pos);
        }
    }

    protected class RuleWalkerNode extends WalkerNode<RuleNode> {
        public RuleWalkerNode(final WalkerNode<?> parent, final RuleNode self, final int pos) {
            super(parent, self, pos);
        }

        @Override
        public void enter(final ParseTreeListener listener) {
            enterRule(listener, self);
        }

        @Override
        public void exit(final ParseTreeListener listener) {
            exitRule(listener, self);
        }
    }

    protected class ErrorWalkerNode extends WalkerNode<ErrorNode> {
        public ErrorWalkerNode(final WalkerNode<?> parent, final ErrorNode self, final int pos) {
            super(parent, self, pos);
        }

        @Override
        public void enter(final ParseTreeListener listener) {
            listener.visitErrorNode(self);
        }
    }

    protected class TerminalWalkerNode extends WalkerNode<TerminalNode> {
        public TerminalWalkerNode(final WalkerNode<?> parent, final TerminalNode self, final int pos) {
            super(parent, self, pos);
        }

        @Override
        public void enter(final ParseTreeListener listener) {
            listener.visitTerminal(self);
        }
    }
}
