#include "TreePostScriptGenerator.h"
#include "Java/src/org/antlr/v4/runtime/tree/gui/TreeLayoutAdaptor.h"
#include "Java/src/org/antlr/v4/runtime/tree/ErrorNode.h"
#include "Java/src/org/antlr/v4/runtime/misc/Utils.h"

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

                        TreePostScriptGenerator::VariableExtentProvide::VariableExtentProvide(TreePostScriptGenerator *outerInstance) : outerInstance(outerInstance) {
                        }

                        double TreePostScriptGenerator::VariableExtentProvide::getWidth(Tree *tree) {
                            std::wstring s = outerInstance->getText(tree);
                            return outerInstance->doc->getWidth(s) + outerInstance->nodeWidthPadding*2;
                        }

                        double TreePostScriptGenerator::VariableExtentProvide::getHeight(Tree *tree) {
                            std::wstring s = outerInstance->getText(tree);
                            double h = outerInstance->doc->getLineHeight() + outerInstance->nodeHeightPaddingAbove + outerInstance->nodeHeightPaddingBelow;
//JAVA TO C++ CONVERTER WARNING: Since the array size is not known in this declaration, Java to C++ Converter has converted this array to a pointer.  You will need to call 'delete[]' where appropriate:
//ORIGINAL LINE: String[] lines = s.split("\n");
//JAVA TO C++ CONVERTER TODO TASK: There is no direct native C++ equivalent to the Java String 'split' method:
                            std::wstring *lines = s.split(L"\n");
                            return h * lines->length;
                        }

//JAVA TO C++ CONVERTER TODO TASK: Calls to same-class constructors are not supported in C++ prior to C++11:
                        TreePostScriptGenerator::TreePostScriptGenerator(std::vector<std::wstring> &ruleNames, Tree *root) {
                        }

                        TreePostScriptGenerator::TreePostScriptGenerator(std::vector<std::wstring> &ruleNames, Tree *root, const std::wstring &fontName, int fontSize) {
                            InitializeInstanceFields();
                            this->root = root;
                            setTreeTextProvider(new TreeViewer::DefaultTreeTextProvider(ruleNames));
                            doc = new PostScriptDocument(fontName, fontSize);
                            bool compareNodeIdentities = true;
                            this->treeLayout = new TreeLayout<Tree*>(new TreeLayoutAdaptor(root), new VariableExtentProvide(this), new DefaultConfiguration<Tree*>(gapBetweenLevels, gapBetweenNodes, Configuration::Location::Bottom), compareNodeIdentities);
                        }

                        std::wstring TreePostScriptGenerator::getPS() {
                            // generate the edges and boxes (with text)
                            generateEdges(getTree()->getRoot());
                            for (Tree *node : treeLayout->getNodeBounds()->keySet()) {
                                generateNode(node);
                            }

                            Dimension *size = treeLayout->getBounds()->getBounds()->getSize();
                            doc->boundingBox(size->width, size->height);
                            doc->close();
                            return doc->getPS();
                        }

                        void TreePostScriptGenerator::generateEdges(Tree *parent) {
                            if (!getTree()->isLeaf(parent)) {
                                Rectangle2D::Double *parentBounds = getBoundsOfNode(parent);
                                                //			System.out.println("%% parent("+getText(parent)+")="+parentBounds);
                                double x1 = parentBounds->getCenterX();
                                double y1 = parentBounds->y;
                                for (auto child : getChildren(parent)) {
                                    Rectangle2D::Double *childBounds = getBoundsOfNode(child);
                                                //				System.out.println("%% child("+getText(child)+")="+childBounds);
                                    double x2 = childBounds->getCenterX();
                                    double y2 = childBounds->getMaxY();
                                    doc->line(x1, y1, x2, y2);
                                    generateEdges(child);
                                }
                            }
                        }

                        void TreePostScriptGenerator::generateNode(Tree *t) {
                            // draw the text on top of the box (possibly multiple lines)
//JAVA TO C++ CONVERTER WARNING: Since the array size is not known in this declaration, Java to C++ Converter has converted this array to a pointer.  You will need to call 'delete[]' where appropriate:
//ORIGINAL LINE: String[] lines = getText(t).split("\n");
//JAVA TO C++ CONVERTER TODO TASK: There is no direct native C++ equivalent to the Java String 'split' method:
                            std::wstring *lines = getText(t).split(L"\n");
                            Rectangle2D::Double *box = getBoundsOfNode(t);
                            // for debugging, turn this on to see boundingbox of nodes
                            //doc.rect(box.x, box.y, box.width, box.height);
                            // make error nodes from parse tree red by default
                            if (dynamic_cast<ErrorNode*>(t) != nullptr) {
                                doc->highlight(box->x, box->y, box->width, box->height);
                            }
                            double x = box->x + nodeWidthPadding;
                            double y = box->y + nodeHeightPaddingBelow;
                            for (int i = 0; i < lines->length; i++) {
                                doc->text(lines[i], x, y);
                                y += doc->getLineHeight();
                            }
                        }

                        TreeForTreeLayout<Tree*> *TreePostScriptGenerator::getTree() {
                            return treeLayout->getTree();
                        }

                        Iterable<Tree*> *TreePostScriptGenerator::getChildren(Tree *parent) {
                            return getTree()->getChildren(parent);
                        }

                        Rectangle2D::Double *TreePostScriptGenerator::getBoundsOfNode(Tree *node) {
                            return treeLayout->getNodeBounds()->get(node);
                        }

                        std::wstring TreePostScriptGenerator::getText(Tree *tree) {
                            std::wstring s = treeTextProvider->getText(tree);
                            s = Utils::escapeWhitespace(s, false);
                            return s;
                        }

                        org::antlr::v4::runtime::tree::gui::TreeTextProvider *TreePostScriptGenerator::getTreeTextProvider() {
                            return treeTextProvider;
                        }

                        void TreePostScriptGenerator::setTreeTextProvider(TreeTextProvider *treeTextProvider) {
                            this->treeTextProvider = treeTextProvider;
                        }

                        void TreePostScriptGenerator::InitializeInstanceFields() {
                            gapBetweenLevels = 17;
                            gapBetweenNodes = 7;
                            nodeWidthPadding = 1;
                            nodeHeightPaddingAbove = 0;
                            nodeHeightPaddingBelow = 5;
                        }
                    }
                }
            }
        }
    }
}
