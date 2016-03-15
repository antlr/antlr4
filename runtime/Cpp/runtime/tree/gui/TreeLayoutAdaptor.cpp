#include "TreeLayoutAdaptor.h"

namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {
                namespace tree {
                    namespace gui {
                        using org::abego::treelayout::TreeForTreeLayout;
                        using org::antlr::v4::runtime::tree::Tree;

                        TreeLayoutAdaptor::AntlrTreeChildrenIterable::AntlrTreeChildrenIterable(Tree *tree) : tree(tree) {
                        }

                        Iterator<Tree*> *TreeLayoutAdaptor::AntlrTreeChildrenIterable::iterator() {
                            return new IteratorAnonymousInnerClassHelper(this);
                        }

                        TreeLayoutAdaptor::AntlrTreeChildrenIterable::IteratorAnonymousInnerClassHelper::IteratorAnonymousInnerClassHelper(AntlrTreeChildrenIterable *outerInstance) {
                            this->outerInstance = outerInstance;
                            i = 0;
                        }

                        bool TreeLayoutAdaptor::AntlrTreeChildrenIterable::IteratorAnonymousInnerClassHelper::hasNext() {
                            return outerInstance->tree->getChildCount() > i;
                        }

                        org::antlr::v4::runtime::tree::Tree *TreeLayoutAdaptor::AntlrTreeChildrenIterable::IteratorAnonymousInnerClassHelper::next() {
                            if (!hasNext()) {
                                throw NoSuchElementException();
                            }

                            return outerInstance->tree->getChild(i++);
                        }

                        void TreeLayoutAdaptor::AntlrTreeChildrenIterable::IteratorAnonymousInnerClassHelper::remove() {
                            throw UnsupportedOperationException();
                        }

                        TreeLayoutAdaptor::AntlrTreeChildrenReverseIterable::AntlrTreeChildrenReverseIterable(Tree *tree) : tree(tree) {
                        }

                        Iterator<Tree*> *TreeLayoutAdaptor::AntlrTreeChildrenReverseIterable::iterator() {
                            return new IteratorAnonymousInnerClassHelper(this);
                        }

                        TreeLayoutAdaptor::AntlrTreeChildrenReverseIterable::IteratorAnonymousInnerClassHelper::IteratorAnonymousInnerClassHelper(AntlrTreeChildrenReverseIterable *outerInstance) {
                            this->outerInstance = outerInstance;
                            i = outerInstance->tree->getChildCount();
                        }

                        bool TreeLayoutAdaptor::AntlrTreeChildrenReverseIterable::IteratorAnonymousInnerClassHelper::hasNext() {
                            return i > 0;
                        }

                        org::antlr::v4::runtime::tree::Tree *TreeLayoutAdaptor::AntlrTreeChildrenReverseIterable::IteratorAnonymousInnerClassHelper::next() {
                            if (!hasNext()) {
                                throw NoSuchElementException();
                            }

                            return outerInstance->tree->getChild(--i);
                        }

                        void TreeLayoutAdaptor::AntlrTreeChildrenReverseIterable::IteratorAnonymousInnerClassHelper::remove() {
                            throw UnsupportedOperationException();
                        }

                        TreeLayoutAdaptor::TreeLayoutAdaptor(Tree *root) {
                            this->root = root;
                        }

                        bool TreeLayoutAdaptor::isLeaf(Tree *node) {
                            return node->getChildCount() == 0;
                        }

                        bool TreeLayoutAdaptor::isChildOfParent(Tree *node, Tree *parentNode) {
                            return node->getParent() == parentNode;
                        }

                        org::antlr::v4::runtime::tree::Tree *TreeLayoutAdaptor::getRoot() {
                            return root;
                        }

                        org::antlr::v4::runtime::tree::Tree *TreeLayoutAdaptor::getLastChild(Tree *parentNode) {
                            return parentNode->getChild(parentNode->getChildCount() - 1);
                        }

                        org::antlr::v4::runtime::tree::Tree *TreeLayoutAdaptor::getFirstChild(Tree *parentNode) {
                            return parentNode->getChild(0);
                        }

                        Iterable<Tree*> *TreeLayoutAdaptor::getChildrenReverse(Tree *node) {
                            return new AntlrTreeChildrenReverseIterable(node);
                        }

                        Iterable<Tree*> *TreeLayoutAdaptor::getChildren(Tree *node) {
                            return new AntlrTreeChildrenIterable(node);
                        }
                    }
                }
            }
        }
    }
}
