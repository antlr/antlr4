#pragma once

#include "Java/src/org/antlr/v4/runtime/tree/Tree.h"
#include "TreeViewer.h"
#include "TreeTextProvider.h"
#include "PostScriptDocument.h"
#include <string>
#include <vector>

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

                        using org::abego::treelayout::Configuration;
                        using org::abego::treelayout::NodeExtentProvider;
                        using org::abego::treelayout::TreeForTreeLayout;
                        using org::abego::treelayout::TreeLayout;
                        using org::abego::treelayout::util::DefaultConfiguration;
                        using org::antlr::v4::runtime::misc::Nullable;
                        using org::antlr::v4::runtime::misc::Utils;
                        using org::antlr::v4::runtime::tree::ErrorNode;
                        using org::antlr::v4::runtime::tree::Tree;


                        class TreePostScriptGenerator {
                        public:
                            class VariableExtentProvide : public NodeExtentProvider<Tree*> {
                                                private:
                                                    TreePostScriptGenerator *const outerInstance;

                                                public:
                                                    VariableExtentProvide(TreePostScriptGenerator *outerInstance);

                                virtual double getWidth(Tree *tree) override;

                                virtual double getHeight(Tree *tree) override;
                            };

                        protected:
                            double gapBetweenLevels;
                            double gapBetweenNodes;
                            int nodeWidthPadding; // added to left/right
                            int nodeHeightPaddingAbove;
                            int nodeHeightPaddingBelow;

                            Tree *root;
                            TreeTextProvider *treeTextProvider;
                            TreeLayout<Tree*> *treeLayout;

                            PostScriptDocument *doc;

                        public:
//JAVA TO C++ CONVERTER TODO TASK: Calls to same-class constructors are not supported in C++ prior to C++11:
                            TreePostScriptGenerator(std::vector<std::wstring> &ruleNames, Tree *root); //this(ruleNames, root, PostScriptDocument.DEFAULT_FONT, 11);

                            TreePostScriptGenerator(std::vector<std::wstring> &ruleNames, Tree *root, const std::wstring &fontName, int fontSize);

                            virtual std::wstring getPS();

                        protected:
                            virtual void generateEdges(Tree *parent);

                            virtual void generateNode(Tree *t);

                            virtual TreeForTreeLayout<Tree*> *getTree();

                            virtual Iterable<Tree*> *getChildren(Tree *parent);

                            virtual Rectangle2D::Double *getBoundsOfNode(Tree *node);

                            virtual std::wstring getText(Tree *tree);

                        public:
                            virtual TreeTextProvider *getTreeTextProvider();

                            virtual void setTreeTextProvider(TreeTextProvider *treeTextProvider);


                        private:
                            void InitializeInstanceFields();
                        };

                    }
                }
            }
        }
    }
}
