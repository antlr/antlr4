#pragma once

#include "TreeTextProvider.h"
#include "Java/src/org/antlr/v4/runtime/tree/Tree.h"
#include "TreePostScriptGenerator.h"
#include <string>
#include <vector>
#include <stdexcept>
#include "stringconverter.h"

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

                        using org::abego::treelayout::NodeExtentProvider;
                        using org::abego::treelayout::TreeForTreeLayout;
                        using org::abego::treelayout::TreeLayout;
                        using org::abego::treelayout::util::DefaultConfiguration;
                        using org::antlr::v4::runtime::misc::GraphicsSupport;
                        using org::antlr::v4::runtime::misc::JFileChooserConfirmOverwrite;
                        using org::antlr::v4::runtime::misc::NotNull;
                        using org::antlr::v4::runtime::misc::Nullable;
                        using org::antlr::v4::runtime::misc::Utils;
                        using org::antlr::v4::runtime::tree::ErrorNode;
                        using org::antlr::v4::runtime::tree::Tree;
                        using org::antlr::v4::runtime::tree::Trees;


                        class TreeViewer : public JComponent {
                        public:
                            class DefaultTreeTextProvider : public TreeTextProvider {
                            private:
                                const std::vector<std::wstring> ruleNames;

                            public:
                                DefaultTreeTextProvider(std::vector<std::wstring> &ruleNames);

                                virtual std::wstring getText(Tree *node) override;
                            };

                        public:
                            class VariableExtentProvide : public NodeExtentProvider<Tree*> {
                            public:
                                TreeViewer *viewer;
                                VariableExtentProvide(TreeViewer *viewer);
                                virtual double getWidth(Tree *tree) override;

                                virtual double getHeight(Tree *tree) override;
                            };

                        private:
                            class TreeNodeWrapper : public DefaultMutableTreeNode {

                            public:
                                TreeViewer *const viewer;

                                TreeNodeWrapper(Tree *tree, TreeViewer *viewer);

                                virtual std::wstring toString() override;
                            };

                        private:
                            class EmptyIcon : public Icon {

                            public:
                                virtual int getIconWidth() override;

                                virtual int getIconHeight() override;

                                virtual void paintIcon(Component *c, Graphics *g, int x, int y) override;
                            };
                        public:
                            static Color *const LIGHT_RED;

                        protected:
                            TreeTextProvider *treeTextProvider;
                            TreeLayout<Tree*> *treeLayout;
                            std::vector<Tree*> highlightedNodes;

                            std::wstring fontName; //Font.SANS_SERIF;
                            int fontStyle;
                            int fontSize;
                            Font *font;

                            double gapBetweenLevels;
                            double gapBetweenNodes;
                            int nodeWidthPadding; // added to left/right
                            int nodeHeightPadding; // added above/below
                            int arcSize; // make an arc in node outline?

                            double scale;

                            Color *boxColor; // set to a color to make it draw background

                            Color *highlightedBoxColor;
                            Color *borderColor;
                            Color *textColor;

                        public:
                            TreeViewer(std::vector<std::wstring> &ruleNames, Tree *tree);

                        private:
                            void updatePreferredSize();

                            // ---------------- PAINT -----------------------------------------------

                            bool useCurvedEdges;

                        public:
                            virtual bool getUseCurvedEdges();

                            virtual void setUseCurvedEdges(bool useCurvedEdges);

                        protected:
                            virtual void paintEdges(Graphics *g, Tree *parent);

                            virtual void paintBox(Graphics *g, Tree *tree);

                        public:
                            virtual void text(Graphics *g, const std::wstring &s, int x, int y);

                            virtual void paint(Graphics *g) override;

                        protected:
                            virtual Graphics *getComponentGraphics(Graphics *g) override;

                            // ----------------------------------------------------------------------

                            static JDialog *showInDialog(TreeViewer *const viewer);

                        private:
                            class ActionListenerAnonymousInnerClassHelper : public ActionListener {
                            private:
                                JDialog *dialog;

                            public:
                                ActionListenerAnonymousInnerClassHelper(JDialog *dialog);

                                virtual void actionPerformed(ActionEvent *e) override;
                            };

                        private:
                            class ActionListenerAnonymousInnerClassHelper2 : public ActionListener {
                            private:
                                org::antlr::v4::runtime::tree::gui::TreeViewer *viewer;
                                JDialog *dialog;

                            public:
                                ActionListenerAnonymousInnerClassHelper2(org::antlr::v4::runtime::tree::gui::TreeViewer *viewer, JDialog *dialog);

                                virtual void actionPerformed(ActionEvent *e) override;
                            };

                        private:
                            class ChangeListenerAnonymousInnerClassHelper : public ChangeListener {
                            private:
                                org::antlr::v4::runtime::tree::gui::TreeViewer *viewer;
                                JSlider *scaleSlider;

                            public:
                                ChangeListenerAnonymousInnerClassHelper(org::antlr::v4::runtime::tree::gui::TreeViewer *viewer, JSlider *scaleSlider);

                                virtual void stateChanged(ChangeEvent *e) override;
                            };

                        private:
                            class TreeSelectionListenerAnonymousInnerClassHelper : public TreeSelectionListener {
                            private:
                                org::antlr::v4::runtime::tree::gui::TreeViewer *viewer;

                            public:
                                TreeSelectionListenerAnonymousInnerClassHelper(org::antlr::v4::runtime::tree::gui::TreeViewer *viewer);

                                virtual void valueChanged(TreeSelectionEvent *e) override;
                            };

                        private:
                            static void generatePNGFile(TreeViewer *viewer, JDialog *dialog);

                        private:
                            class FileFilterAnonymousInnerClassHelper : public FileFilter {
                            public:
                                FileFilterAnonymousInnerClassHelper();


                                virtual bool accept(File *pathname) override;

                                virtual std::wstring getDescription() override;
                            };

                        private:
                            static File *generateNonExistingPngFile();

                            static void fillTree(TreeNodeWrapper *node, Tree *tree, TreeViewer *viewer);

                            Dimension *getScaledTreeSize();

                        public:
                            virtual Future<JDialog*> *open();

                        private:
                            class CallableAnonymousInnerClassHelper : public Callable<JDialog*> {
                            private:
                                TreeViewer *const outerInstance;

                                org::antlr::v4::runtime::tree::gui::TreeViewer *viewer;

                            public:
                                CallableAnonymousInnerClassHelper(TreeViewer *outerInstance, org::antlr::v4::runtime::tree::gui::TreeViewer *viewer);

                                JDialog *result;

                                virtual JDialog *call() throw(std::exception) override;

                            private:
                                class RunnableAnonymousInnerClassHelper : public Runnable {
                                private:
                                    CallableAnonymousInnerClassHelper *const outerInstance;

                                public:
                                    RunnableAnonymousInnerClassHelper(CallableAnonymousInnerClassHelper *outerInstance);

                                    virtual void run() override;
                                };
                            };

                        public:
                            virtual void save(const std::wstring &fileName) throw(IOException, PrintException);

                            // ---------------------------------------------------

                        protected:
                            virtual Rectangle2D::Double *getBoundsOfNode(Tree *node);

                            virtual std::wstring getText(Tree *tree);

                        public:
                            virtual TreeTextProvider *getTreeTextProvider();

                            virtual void setTreeTextProvider(TreeTextProvider *treeTextProvider);

                            virtual void setFontSize(int sz);

                            virtual void setFontName(const std::wstring &name);

                            /// <summary>
                            /// Slow for big lists of highlighted nodes </summary>
                            virtual void addHighlightedNodes(Collection<Tree*> *nodes);

                            virtual void removeHighlightedNodes(Collection<Tree*> *nodes);

                        protected:
                            virtual bool isHighlighted(Tree *node);

                            virtual int getHighlightedNodeIndex(Tree *node);

                        public:
                            virtual Font *getFont() override;

                            virtual void setFont(Font *font) override;

                            virtual int getArcSize();

                            virtual void setArcSize(int arcSize);

                            virtual Color *getBoxColor();

                            virtual void setBoxColor(Color *boxColor);

                            virtual Color *getHighlightedBoxColor();

                            virtual void setHighlightedBoxColor(Color *highlightedBoxColor);

                            virtual Color *getBorderColor();

                            virtual void setBorderColor(Color *borderColor);

                            virtual Color *getTextColor();

                            virtual void setTextColor(Color *textColor);

                        protected:
                            virtual TreeForTreeLayout<Tree*> *getTree();

                        public:
                            virtual double getScale();

                            virtual void setScale(double scale);


                        private:
                            void InitializeInstanceFields();
                        };

                    }
                }
            }
        }
    }
}
