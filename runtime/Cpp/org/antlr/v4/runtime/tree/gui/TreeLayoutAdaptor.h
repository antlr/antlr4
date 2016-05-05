#pragma once

#include "Java/src/org/antlr/v4/runtime/tree/Tree.h"

/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Dan McLaughlin
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

namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {
                namespace tree {
                    namespace gui {

                        using org::abego::treelayout::TreeForTreeLayout;
                        using org::antlr::v4::runtime::tree::Tree;


                        /// <summary>
                        /// Adaptor ANTLR trees to <seealso cref="TreeForTreeLayout"/>. </summary>
                        class TreeLayoutAdaptor : public TreeForTreeLayout<Tree*> {
                        private:
                            class AntlrTreeChildrenIterable : public Iterable<Tree*> {
                            private:
                                Tree *const tree;

                            public:
                                AntlrTreeChildrenIterable(Tree *tree);

                                virtual Iterator<Tree*> *iterator() override;

                            private:
                                class IteratorAnonymousInnerClassHelper : public Iterator<Tree*> {
                                private:
                                    AntlrTreeChildrenIterable *const outerInstance;

                                public:
                                    IteratorAnonymousInnerClassHelper(AntlrTreeChildrenIterable *outerInstance);

                                private:
                                    int i;

                                public:
                                    virtual bool hasNext();

                                    virtual Tree *next();

                                    virtual void remove();
                                };
                            };

                        private:
                            class AntlrTreeChildrenReverseIterable : public Iterable<Tree*> {
                            private:
                                Tree *const tree;

                            public:
                                AntlrTreeChildrenReverseIterable(Tree *tree);

                                virtual Iterator<Tree*> *iterator() override;

                            private:
                                class IteratorAnonymousInnerClassHelper : public Iterator<Tree*> {
                                private:
                                    AntlrTreeChildrenReverseIterable *const outerInstance;

                                public:
                                    IteratorAnonymousInnerClassHelper(AntlrTreeChildrenReverseIterable *outerInstance);

                                private:
                                    int i;

                                public:
                                    virtual bool hasNext();

                                    virtual Tree *next();

                                    virtual void remove();
                                };
                            };

                        private:
                            Tree *root;

                        public:
                            TreeLayoutAdaptor(Tree *root);

                            virtual bool isLeaf(Tree *node) override;

                            virtual bool isChildOfParent(Tree *node, Tree *parentNode) override;

                            virtual Tree *getRoot() override;

                            virtual Tree *getLastChild(Tree *parentNode) override;

                            virtual Tree *getFirstChild(Tree *parentNode) override;

                            virtual Iterable<Tree*> *getChildrenReverse(Tree *node) override;

                            virtual Iterable<Tree*> *getChildren(Tree *node) override;
                        };

                    }
                }
            }
        }
    }
}
