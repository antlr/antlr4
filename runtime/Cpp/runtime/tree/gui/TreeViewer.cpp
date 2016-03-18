#include "TreeViewer.h"
#include "Java/src/org/antlr/v4/runtime/tree/Trees.h"
#include "Java/src/org/antlr/v4/runtime/tree/gui/TreeLayoutAdaptor.h"
#include "Java/src/org/antlr/v4/runtime/tree/ErrorNode.h"
#include "Java/src/org/antlr/v4/runtime/misc/Utils.h"
#include "Java/src/org/antlr/v4/runtime/misc/JFileChooserConfirmOverwrite.h"
#include "Java/src/org/antlr/v4/runtime/misc/GraphicsSupport.h"

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

                        TreeViewer::DefaultTreeTextProvider::DefaultTreeTextProvider(std::vector<std::wstring> &ruleNames) : ruleNames(ruleNames) {
                        }

                        std::wstring TreeViewer::DefaultTreeTextProvider::getText(Tree *node) {
                            return static_cast<std::wstring>(Trees::getNodeText(node, ruleNames));
                        }

                        TreeViewer::VariableExtentProvide::VariableExtentProvide(TreeViewer *viewer) {
                            this->viewer = viewer;
                        }

                        double TreeViewer::VariableExtentProvide::getWidth(Tree *tree) {
                            FontMetrics *fontMetrics = viewer->getFontMetrics(viewer->font);
                            std::wstring s = viewer->getText(tree);
                            int w = fontMetrics->stringWidth(s) + viewer->nodeWidthPadding*2;
                            return w;
                        }

                        double TreeViewer::VariableExtentProvide::getHeight(Tree *tree) {
                            FontMetrics *fontMetrics = viewer->getFontMetrics(viewer->font);
                            int h = fontMetrics->getHeight() + viewer->nodeHeightPadding*2;
                            std::wstring s = viewer->getText(tree);
//JAVA TO C++ CONVERTER WARNING: Since the array size is not known in this declaration, Java to C++ Converter has converted this array to a pointer.  You will need to call 'delete[]' where appropriate:
//ORIGINAL LINE: String[] lines = s.split("\n");
//JAVA TO C++ CONVERTER TODO TASK: There is no direct native C++ equivalent to the Java String 'split' method:
                            std::wstring *lines = s.split(L"\n");
                            return h * lines->length;
                        }

                        TreeViewer::TreeNodeWrapper::TreeNodeWrapper(Tree *tree, TreeViewer *viewer) : javax::swing::tree::DefaultMutableTreeNode(tree), viewer(viewer) {
                        }

                        std::wstring TreeViewer::TreeNodeWrapper::toString() {
                            return viewer->getText(static_cast<Tree*>(this->getUserObject()));
                        }

                        int TreeViewer::EmptyIcon::getIconWidth() {
                            return 0;
                        }

                        int TreeViewer::EmptyIcon::getIconHeight() {
                            return 0;
                        }

                        void TreeViewer::EmptyIcon::paintIcon(Component *c, Graphics *g, int x, int y) {
                            /* Do nothing. */
                        }

java::awt::Color *const TreeViewer::LIGHT_RED = new java::awt::Color(244, 213, 211);

                        TreeViewer::TreeViewer(std::vector<std::wstring> &ruleNames, Tree *tree) {
                            InitializeInstanceFields();
                            setTreeTextProvider(new DefaultTreeTextProvider(ruleNames));
                            bool useIdentity = true; // compare node identity
                            this->treeLayout = new TreeLayout<Tree*>(new TreeLayoutAdaptor(tree), new TreeViewer::VariableExtentProvide(this), new DefaultConfiguration<Tree*>(gapBetweenLevels, gapBetweenNodes), useIdentity);
                            updatePreferredSize();
                            setFont(font);
                        }

                        void TreeViewer::updatePreferredSize() {
                            setPreferredSize(getScaledTreeSize());
                            invalidate();
                            if (getParent() != nullptr) {
                                getParent()->validate();
                            }
                            repaint();
                        }

                        bool TreeViewer::getUseCurvedEdges() {
                            return useCurvedEdges;
                        }

                        void TreeViewer::setUseCurvedEdges(bool useCurvedEdges) {
                            this->useCurvedEdges = useCurvedEdges;
                        }

                        void TreeViewer::paintEdges(Graphics *g, Tree *parent) {
                            if (!getTree()->isLeaf(parent)) {
                                BasicStroke *stroke = new BasicStroke(1.0f, BasicStroke::CAP_ROUND, BasicStroke::JOIN_ROUND);
                                (static_cast<Graphics2D*>(g))->setStroke(stroke);

                                Rectangle2D::Double *parentBounds = getBoundsOfNode(parent);
                                double x1 = parentBounds->getCenterX();
                                double y1 = parentBounds->getMaxY();
                                for (Tree *child : getTree()->getChildren(parent)) {
                                    Rectangle2D::Double *childBounds = getBoundsOfNode(child);
                                    double x2 = childBounds->getCenterX();
                                    double y2 = childBounds->getMinY();
                                    if (getUseCurvedEdges()) {
                                        CubicCurve2D *c = new CubicCurve2D::Double();
                                        double ctrlx1 = x1;
                                        double ctrly1 = (y1 + y2) / 2;
                                        double ctrlx2 = x2;
                                        double ctrly2 = y1;
                                        c->setCurve(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2);
                                        (static_cast<Graphics2D*>(g))->draw(c);
                                    } else {
                                        g->drawLine(static_cast<int>(x1), static_cast<int>(y1), static_cast<int>(x2), static_cast<int>(y2));
                                    }
                                    paintEdges(g, child);
                                }
                            }
                        }

                        void TreeViewer::paintBox(Graphics *g, Tree *tree) {
                            Rectangle2D::Double *box = getBoundsOfNode(tree);
                            // draw the box in the background
                            if (isHighlighted(tree) || boxColor != nullptr || dynamic_cast<ErrorNode*>(tree) != nullptr) {
                                if (isHighlighted(tree)) {
                                    g->setColor(highlightedBoxColor);
                                } else if (dynamic_cast<ErrorNode*>(tree) != nullptr) {
                                    g->setColor(LIGHT_RED);
                                } else {
                                    g->setColor(boxColor);
                                }
                                g->fillRoundRect(static_cast<int>(box->x), static_cast<int>(box->y), static_cast<int>(box->width) - 1, static_cast<int>(box->height) - 1, arcSize, arcSize);
                            }
                            if (borderColor != nullptr) {
                                g->setColor(borderColor);
                                g->drawRoundRect(static_cast<int>(box->x), static_cast<int>(box->y), static_cast<int>(box->width) - 1, static_cast<int>(box->height) - 1, arcSize, arcSize);
                            }

                            // draw the text on top of the box (possibly multiple lines)
                            g->setColor(textColor);
                            std::wstring s = getText(tree);
//JAVA TO C++ CONVERTER WARNING: Since the array size is not known in this declaration, Java to C++ Converter has converted this array to a pointer.  You will need to call 'delete[]' where appropriate:
//ORIGINAL LINE: String[] lines = s.split("\n");
//JAVA TO C++ CONVERTER TODO TASK: There is no direct native C++ equivalent to the Java String 'split' method:
                            std::wstring *lines = s.split(L"\n");
                            FontMetrics *m = getFontMetrics(font);
                            int x = static_cast<int>(box->x) + arcSize / 2 + nodeWidthPadding;
                            int y = static_cast<int>(box->y) + m->getAscent() + m->getLeading() + 1 + nodeHeightPadding;
                            for (int i = 0; i < lines->length; i++) {
                                text(g, lines[i], x, y);
                                y += m->getHeight();
                            }
                        }

                        void TreeViewer::text(Graphics *g, const std::wstring &s, int x, int y) {
                                                //		System.out.println("drawing '"+s+"' @ "+x+","+y);
                            s = Utils::escapeWhitespace(s, true);
                            g->drawString(s, x, y);
                        }

                        void TreeViewer::paint(Graphics *g) {
                            JComponent::paint(g);

                            Graphics2D *g2 = static_cast<Graphics2D*>(g);
                            // anti-alias the lines
                            g2->setRenderingHint(RenderingHints::KEY_ANTIALIASING, RenderingHints::VALUE_ANTIALIAS_ON);

                            // Anti-alias the text
                            g2->setRenderingHint(RenderingHints::KEY_TEXT_ANTIALIASING, RenderingHints::VALUE_TEXT_ANTIALIAS_ON);

                                                //		AffineTransform at = g2.getTransform();
                                                //        g2.scale(
                                                //            (double) this.getWidth() / 400,
                                                //            (double) this.getHeight() / 400);
                                                //
                                                //		g2.setTransform(at);

                            paintEdges(g, getTree()->getRoot());

                            // paint the boxes
                            for (org.antlr::v4::runtime::tree::Tree *org : treeLayout->getNodeBounds()->keySet()) {
                                paintBox(g, org.antlr::v4::runtime::tree::Tree);
                            }
                        }

                        Graphics *TreeViewer::getComponentGraphics(Graphics *g) {
                            Graphics2D *g2d = static_cast<Graphics2D*>(g);
                            g2d->scale(scale, scale);
                            return JComponent::getComponentGraphics(g2d);
                        }

                        JDialog *TreeViewer::showInDialog(TreeViewer *const viewer) {
                            JDialog * const dialog = new JDialog();
                            dialog->setTitle(L"Parse Tree Inspector");

                            // Make new content panes
                            Container * const mainPane = new JPanel(new BorderLayout(5,5));
                            Container * const contentPane = new JPanel(new BorderLayout(0,0));
                            contentPane->setBackground(Color::white);

                            // Wrap viewer in scroll pane
                            JScrollPane *scrollPane = new JScrollPane(viewer);
                            // Make the scrollpane (containing the viewer) the center component
                            contentPane->add(scrollPane, BorderLayout::CENTER);

                            JPanel *wrapper = new JPanel(new FlowLayout());

                            // Add button to bottom
                            JPanel *bottomPanel = new JPanel(new BorderLayout(0,0));
                            contentPane->add(bottomPanel, BorderLayout::SOUTH);

                            JButton *ok = new JButton(L"OK");
                            ok->addActionListener(new ActionListenerAnonymousInnerClassHelper(dialog)
                           );
                            wrapper->add(ok);

                            // Add an export-to-png button right of the "OK" button
                            JButton *png = new JButton(L"png");
                            png->addActionListener(new ActionListenerAnonymousInnerClassHelper2(viewer, dialog)
                           );
                            wrapper->add(png);

                            bottomPanel->add(wrapper, BorderLayout::SOUTH);

                            // Add scale slider
                            int sliderValue = static_cast<int>((viewer->getScale() - 1.0) * 1000);
                            JSlider * const scaleSlider = new JSlider(JSlider::HORIZONTAL, -999,1000,sliderValue);
                            scaleSlider->addChangeListener(new ChangeListenerAnonymousInnerClassHelper(viewer, scaleSlider)
                           );
                            bottomPanel->add(scaleSlider, BorderLayout::CENTER);

                            // Add a JTree representing the parser tree of the input.
                            JPanel *treePanel = new JPanel(new BorderLayout(5, 5));

                            // An "empty" icon that will be used for the JTree's nodes.
                            Icon *empty = new EmptyIcon();

                            UIManager::put(L"Tree.closedIcon", empty);
                            UIManager::put(L"Tree.openIcon", empty);
                            UIManager::put(L"Tree.leafIcon", empty);

                            Tree *parseTreeRoot = viewer->getTree()->getRoot();
                            TreeNodeWrapper *nodeRoot = new TreeNodeWrapper(parseTreeRoot, viewer);
                            fillTree(nodeRoot, parseTreeRoot, viewer);
                            JTree * const tree = new JTree(nodeRoot);
                            tree->getSelectionModel()->setSelectionMode(TreeSelectionModel::SINGLE_TREE_SELECTION);

                            tree->addTreeSelectionListener(new TreeSelectionListenerAnonymousInnerClassHelper(viewer));

                            treePanel->add(new JScrollPane(tree));

                            // Create the pane for both the JTree and the AST
                            JSplitPane *splitPane = new JSplitPane(JSplitPane::HORIZONTAL_SPLIT, treePanel, contentPane);

                            mainPane->add(splitPane, BorderLayout::CENTER);

                            dialog->setContentPane(mainPane);

                            // make viz
                            dialog->setDefaultCloseOperation(JFrame::DISPOSE_ON_CLOSE);
                            dialog->setPreferredSize(new Dimension(600, 500));
                            dialog->pack();

                            // After pack(): set the divider at 1/3 of the frame.
                            splitPane->setDividerLocation(0.33);

                            dialog->setLocationRelativeTo(nullptr);
                            dialog->setVisible(true);
                            return dialog;
                        }

                        TreeViewer::ActionListenerAnonymousInnerClassHelper::ActionListenerAnonymousInnerClassHelper(JDialog *dialog) {
                            this->dialog = dialog;
                        }

                        void TreeViewer::ActionListenerAnonymousInnerClassHelper::actionPerformed(ActionEvent *e) {
                            dialog->setVisible(false);
                            dialog->dispose();
                        }

                        TreeViewer::ActionListenerAnonymousInnerClassHelper2::ActionListenerAnonymousInnerClassHelper2(org::antlr::v4::runtime::tree::gui::TreeViewer *viewer, JDialog *dialog) {
                            this->viewer = viewer;
                            this->dialog = dialog;
                        }

                        void TreeViewer::ActionListenerAnonymousInnerClassHelper2::actionPerformed(ActionEvent *e) {
                            generatePNGFile(viewer, dialog);
                        }

                        TreeViewer::ChangeListenerAnonymousInnerClassHelper::ChangeListenerAnonymousInnerClassHelper(org::antlr::v4::runtime::tree::gui::TreeViewer *viewer, JSlider *scaleSlider) {
                            this->viewer = viewer;
                            this->scaleSlider = scaleSlider;
                        }

                        void TreeViewer::ChangeListenerAnonymousInnerClassHelper::stateChanged(ChangeEvent *e) {
                            int v = scaleSlider->getValue();
                            viewer->setScale(v / 1000.0 + 1.0);
                        }

                        TreeViewer::TreeSelectionListenerAnonymousInnerClassHelper::TreeSelectionListenerAnonymousInnerClassHelper(org::antlr::v4::runtime::tree::gui::TreeViewer *viewer) {
                            this->viewer = viewer;
                        }

                        void TreeViewer::TreeSelectionListenerAnonymousInnerClassHelper::valueChanged(TreeSelectionEvent *e) {

                            JTree *selectedTree = static_cast<JTree*>(e->getSource());
                            TreePath *path = selectedTree->getSelectionPath();
                            TreeNodeWrapper *treeNode = static_cast<TreeNodeWrapper*>(path->getLastPathComponent());

                            // Set the clicked AST.
                            viewer->treeLayout = new TreeLayout<Tree*>(new TreeLayoutAdaptor(static_cast<Tree*>(treeNode->getUserObject())), new TreeViewer::VariableExtentProvide(viewer), new DefaultConfiguration<Tree*>(viewer->gapBetweenLevels, viewer->gapBetweenNodes), true);

                            // Let the UI display this new AST.
                            viewer->updatePreferredSize();
                        }

                        void TreeViewer::generatePNGFile(TreeViewer *viewer, JDialog *dialog) {
                            BufferedImage *bi = new BufferedImage(viewer->getSize()->width, viewer->getSize()->height, BufferedImage::TYPE_INT_ARGB);
                            Graphics *g = bi->createGraphics();
                            viewer->paint(g);
                            g->dispose();

                            try {
                                File *suggestedFile = generateNonExistingPngFile();
                                JFileChooser *fileChooser = new JFileChooserConfirmOverwrite();
                                fileChooser->setCurrentDirectory(suggestedFile->getParentFile());
                                fileChooser->setSelectedFile(suggestedFile);
                                FileFilter *pngFilter = new FileFilterAnonymousInnerClassHelper();

                                fileChooser->addChoosableFileFilter(pngFilter);
                                fileChooser->setFileFilter(pngFilter);

                                int returnValue = fileChooser->showSaveDialog(dialog);
                                if (returnValue == JFileChooser::APPROVE_OPTION) {
                                    File *pngFile = fileChooser->getSelectedFile();
                                    ImageIO::write(bi, L"png", pngFile);

                                    try {
                                        // Try to open the parent folder using the OS' native file manager.
                                        Desktop::getDesktop()->open(pngFile->getParentFile());
                                    } catch (std::exception &ex) {
                                        // We could not launch the file manager: just show a popup that we
                                        // succeeded in saving the PNG file.
                                        JOptionPane::showMessageDialog(dialog, std::wstring(L"Saved PNG to: ") + pngFile->getAbsolutePath());
                                        ex.printStackTrace();
                                    }
                                }
                            } catch (std::exception &ex) {
                                JOptionPane::showMessageDialog(dialog, std::wstring(L"Could not export to PNG: ") + ex.what(), L"Error", JOptionPane::ERROR_MESSAGE);
                                ex.printStackTrace();
                            }
                        }

                        TreeViewer::FileFilterAnonymousInnerClassHelper::FileFilterAnonymousInnerClassHelper() {
                        }

                        bool TreeViewer::FileFilterAnonymousInnerClassHelper::accept(File *pathname) {
                            if (pathname->isFile()) {
//JAVA TO C++ CONVERTER TODO TASK: There is no direct native C++ equivalent to the Java String 'endsWith' method:
                                return pathname->getName()->toLowerCase()->endsWith(L".png");
                            }

                            return true;
                        }

                        std::wstring TreeViewer::FileFilterAnonymousInnerClassHelper::getDescription() {
                            return L"PNG Files (*.png)";
                        }

                        File *TreeViewer::generateNonExistingPngFile() {

                            const std::wstring parent = L".";
                            const std::wstring name = L"antlr4_parse_tree";
                            const std::wstring extension = L".png";

                            File *pngFile = new File(parent, name + extension);

                            int counter = 1;

                            // Keep looping until we create a File that does not yet exist.
                            while (pngFile->exists()) {
                                pngFile = new File(parent, name + std::wstring(L"_") + StringConverterHelper::toString(counter) + extension);
                                counter++;
                            }

                            return pngFile;
                        }

                        void TreeViewer::fillTree(TreeNodeWrapper *node, Tree *tree, TreeViewer *viewer) {

                            if (tree == nullptr) {
                                return;
                            }

                            for (int i = 0; i < tree->getChildCount(); i++) {

                                Tree *childTree = tree->getChild(i);
                                TreeNodeWrapper *childNode = new TreeNodeWrapper(childTree, viewer);

                                node->add(childNode);

                                fillTree(childNode, childTree, viewer);
                            }
                        }

                        Dimension *TreeViewer::getScaledTreeSize() {
                            Dimension *scaledTreeSize = treeLayout->getBounds()->getBounds()->getSize();
                            scaledTreeSize = new Dimension(static_cast<int>(scaledTreeSize->width*scale), static_cast<int>(scaledTreeSize->height*scale));
                            return scaledTreeSize;
                        }

                        Future<JDialog*> *TreeViewer::open() {
                            TreeViewer * const viewer = this;
                            viewer->setScale(1.5);
                            Callable<JDialog*> *callable = new CallableAnonymousInnerClassHelper(this, viewer);

                            ExecutorService *executor = Executors::newSingleThreadExecutor();

                            try {
                                return executor->submit(callable);
                            } finally {
                                executor->shutdown();
                            }
                        }

                        TreeViewer::CallableAnonymousInnerClassHelper::CallableAnonymousInnerClassHelper(TreeViewer *outerInstance, org::antlr::v4::runtime::tree::gui::TreeViewer *viewer) {
                            this->outerInstance = outerInstance;
                            this->viewer = viewer;
                        }

                        JDialog *TreeViewer::CallableAnonymousInnerClassHelper::call() throw(std::exception) {
                            SwingUtilities::invokeAndWait(new RunnableAnonymousInnerClassHelper(this));

                            return result;
                        }

                        TreeViewer::CallableAnonymousInnerClassHelper::RunnableAnonymousInnerClassHelper::RunnableAnonymousInnerClassHelper(CallableAnonymousInnerClassHelper *outerInstance) {
                            this->outerInstance = outerInstance;
                        }

                        void TreeViewer::CallableAnonymousInnerClassHelper::RunnableAnonymousInnerClassHelper::run() {
                            result = showInDialog(outerInstance->viewer);
                        }

                        void TreeViewer::save(const std::wstring &fileName) throw(IOException, PrintException) {
                            JDialog *dialog = new JDialog();
                            Container *contentPane = dialog->getContentPane();
                            (static_cast<JComponent*>(contentPane))->setBorder(BorderFactory::createEmptyBorder(10, 10, 10, 10));
                            contentPane->add(this);
                            contentPane->setBackground(Color::white);
                            dialog->pack();
                            dialog->setLocationRelativeTo(nullptr);
                            dialog->dispose();
                            GraphicsSupport::saveImage(this, fileName);
                        }

                        Rectangle2D::Double *TreeViewer::getBoundsOfNode(Tree *node) {
                            return treeLayout->getNodeBounds()->get(node);
                        }

                        std::wstring TreeViewer::getText(Tree *tree) {
                            std::wstring s = treeTextProvider->getText(tree);
                            s = Utils::escapeWhitespace(s, true);
                            return s;
                        }

                        org::antlr::v4::runtime::tree::gui::TreeTextProvider *TreeViewer::getTreeTextProvider() {
                            return treeTextProvider;
                        }

                        void TreeViewer::setTreeTextProvider(TreeTextProvider *treeTextProvider) {
                            this->treeTextProvider = treeTextProvider;
                        }

                        void TreeViewer::setFontSize(int sz) {
                            fontSize = sz;
                            font = new Font(fontName, fontStyle, fontSize);
                        }

                        void TreeViewer::setFontName(const std::wstring &name) {
                            fontName = name;
                            font = new Font(fontName, fontStyle, fontSize);
                        }

                        void TreeViewer::addHighlightedNodes(Collection<Tree*> *nodes) {
                            highlightedNodes = std::vector<Tree*>();
                            highlightedNodes.addAll(nodes);
                        }

                        void TreeViewer::removeHighlightedNodes(Collection<Tree*> *nodes) {
                            if (highlightedNodes.size() > 0) {
                                // only remove exact objects defined by ==, not equals()
                                for (auto t : nodes) {
                                    int i = getHighlightedNodeIndex(t);
                                    if (i >= 0) {
                                        highlightedNodes.remove(i);
                                    }
                                }
                            }
                        }

                        bool TreeViewer::isHighlighted(Tree *node) {
                            return getHighlightedNodeIndex(node) >= 0;
                        }

                        int TreeViewer::getHighlightedNodeIndex(Tree *node) {
                            if (highlightedNodes.empty()) {
                                return -1;
                            }
                            for (int i = 0; i < highlightedNodes.size(); i++) {
                                Tree *t = highlightedNodes[i];
                                if (t == node) {
                                    return i;
                                }
                            }
                            return -1;
                        }

                        Font *TreeViewer::getFont() {
                            return font;
                        }

                        void TreeViewer::setFont(Font *font) {
                            this->font = font;
                        }

                        int TreeViewer::getArcSize() {
                            return arcSize;
                        }

                        void TreeViewer::setArcSize(int arcSize) {
                            this->arcSize = arcSize;
                        }

                        Color *TreeViewer::getBoxColor() {
                            return boxColor;
                        }

                        void TreeViewer::setBoxColor(Color *boxColor) {
                            this->boxColor = boxColor;
                        }

                        Color *TreeViewer::getHighlightedBoxColor() {
                            return highlightedBoxColor;
                        }

                        void TreeViewer::setHighlightedBoxColor(Color *highlightedBoxColor) {
                            this->highlightedBoxColor = highlightedBoxColor;
                        }

                        Color *TreeViewer::getBorderColor() {
                            return borderColor;
                        }

                        void TreeViewer::setBorderColor(Color *borderColor) {
                            this->borderColor = borderColor;
                        }

                        Color *TreeViewer::getTextColor() {
                            return textColor;
                        }

                        void TreeViewer::setTextColor(Color *textColor) {
                            this->textColor = textColor;
                        }

                        TreeForTreeLayout<Tree*> *TreeViewer::getTree() {
                            return treeLayout->getTree();
                        }

                        double TreeViewer::getScale() {
                            return scale;
                        }

                        void TreeViewer::setScale(double scale) {
                            if (scale <= 0) {
                                scale = 1;
                            }
                            this->scale = scale;
                            updatePreferredSize();
                        }

                        void TreeViewer::InitializeInstanceFields() {
                            fontName = L"Helvetica";
                            fontStyle = java::awt::Font::PLAIN;
                            fontSize = 11;
                            font = new java::awt::Font(fontName, fontStyle, fontSize);
                            gapBetweenLevels = 17;
                            gapBetweenNodes = 7;
                            nodeWidthPadding = 2;
                            nodeHeightPadding = 0;
                            arcSize = 0;
                            scale = 1.0;
                            boxColor = 0;
                            highlightedBoxColor = java::awt::Color::lightGray;
                            borderColor = 0;
                            textColor = java::awt::Color::black;
                            useCurvedEdges = false;
                        }
                    }
                }
            }
        }
    }
}
