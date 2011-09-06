package org.antlr.v4.runtime.tree.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;

import org.abego.treelayout.Configuration;
import org.abego.treelayout.NodeExtentProvider;
import org.abego.treelayout.TreeForTreeLayout;
import org.abego.treelayout.TreeLayout;
import org.abego.treelayout.util.DefaultConfiguration;
import org.abego.treelayout.util.FixedNodeExtentProvider;
import org.antlr.v4.runtime.tree.Tree;

public class TreeViewer {
	private static class AntlrTreeLayout extends TreeLayout<Tree> {
		public AntlrTreeLayout(TreeForTreeLayout<Tree> tree,
				NodeExtentProvider<Tree> nodeExtentProvider,
				Configuration<Tree> configuration) {
			super(tree, nodeExtentProvider, configuration);
		}
	}

	private static class AntlrTreeLayouter {
		// TODO: provide public interface to the configuration/nodeExtent
		private double gapBetweenLevels = 50;
		private double gapBetweenNodes = 10;
		private double nodeWidth = 60;
		private double nodeHeight = 20;

		public AntlrTreeLayout layout(Tree tree) {
			return new AntlrTreeLayout(new AntlrTreeForTreeLayout(tree),
					new FixedNodeExtentProvider<Tree>(nodeWidth, nodeHeight),
					new DefaultConfiguration<Tree>(gapBetweenLevels,
							gapBetweenNodes));
		}

		private static class AntlrTreeForTreeLayout implements
				TreeForTreeLayout<Tree> {
			private static class AntlrTreeChildrenIterable implements
					Iterable<Tree> {
				private final Tree tree;

				public AntlrTreeChildrenIterable(Tree tree) {
					this.tree = tree;
				}

				@Override
				public Iterator<Tree> iterator() {
					return new Iterator<Tree>() {
						private int i = 0;

						@Override
						public boolean hasNext() {
							return tree.getChildCount() > i;
						}

						@Override
						public Tree next() {
							if (!hasNext())
								throw new NoSuchElementException();

							return tree.getChild(i++);
						}

						@Override
						public void remove() {
							throw new UnsupportedOperationException();
						}
					};
				}
			}

			private static class AntlrTreeChildrenReverseIterable implements
					Iterable<Tree> {
				private final Tree tree;

				public AntlrTreeChildrenReverseIterable(Tree tree) {
					this.tree = tree;
				}

				@Override
				public Iterator<Tree> iterator() {
					return new Iterator<Tree>() {
						private int i = tree.getChildCount();

						@Override
						public boolean hasNext() {
							return i > 0;
						}

						@Override
						public Tree next() {
							if (!hasNext())
								throw new NoSuchElementException();

							return tree.getChild(--i);
						}

						@Override
						public void remove() {
							throw new UnsupportedOperationException();
						}
					};
				}
			}

			private Tree root;

			public AntlrTreeForTreeLayout(Tree root) {
				this.root = root;
			}

			@Override
			public boolean isLeaf(Tree node) {
				return node.getChildCount() == 0;
			}

			@Override
			public boolean isChildOfParent(Tree node, Tree parentNode) {
				return node.getParent() == parentNode;
			}

			@Override
			public Tree getRoot() {
				return root;
			}

			@Override
			public Tree getLastChild(Tree parentNode) {
				return (Tree) parentNode
						.getChild(parentNode.getChildCount() - 1);
			}

			@Override
			public Tree getFirstChild(Tree parentNode) {
				return (Tree) parentNode.getChild(0);
			}

			@Override
			public Iterable<Tree> getChildrenReverse(Tree node) {
				return new AntlrTreeChildrenReverseIterable(node);
			}

			@Override
			public Iterable<Tree> getChildren(Tree node) {
				return new AntlrTreeChildrenIterable(node);
			}
		}
	}

	private static class AntlrTreePane extends JComponent {
		private final AntlrTreeLayout treeLayout;

		private TreeForTreeLayout<Tree> getTree() {
			return treeLayout.getTree();
		}

		private Iterable<Tree> getChildren(Tree parent) {
			return getTree().getChildren(parent);
		}

		private Rectangle2D.Double getBoundsOfNode(Tree node) {
			return treeLayout.getNodeBounds().get(node);
		}

		private String getText(Tree tree) {
			return String.valueOf(tree.getPayload());
		}

		/**
		 * Specifies the tree to be displayed by passing in a {@link TreeLayout}
		 * for that tree.
		 * 
		 * @param treeLayout
		 */
		public AntlrTreePane(AntlrTreeLayout treeLayout) {
			this.treeLayout = treeLayout;

			Dimension size = treeLayout.getBounds().getBounds().getSize();
			setPreferredSize(size);
		}

		// -------------------------------------------------------------------
		// painting

		private final static int ARC_SIZE = 10;
		private final static Color BOX_COLOR = Color.orange;
		private final static Color BORDER_COLOR = Color.darkGray;
		private final static Color TEXT_COLOR = Color.black;

		private void paintEdges(Graphics g, Tree parent) {
			if (!getTree().isLeaf(parent)) {
				Rectangle2D.Double b1 = getBoundsOfNode(parent);
				double x1 = b1.getCenterX();
				double y1 = b1.getCenterY();
				for (Tree child : getChildren(parent)) {
					Rectangle2D.Double b2 = getBoundsOfNode(child);
					g.drawLine((int) x1, (int) y1, (int) b2.getCenterX(),
							(int) b2.getCenterY());

					paintEdges(g, child);
				}
			}
		}

		private void paintBox(Graphics g, Tree tree) {
			// draw the box in the background
			g.setColor(BOX_COLOR);
			Rectangle2D.Double box = getBoundsOfNode(tree);
			g.fillRoundRect((int) box.x, (int) box.y, (int) box.width - 1,
					(int) box.height - 1, ARC_SIZE, ARC_SIZE);
			g.setColor(BORDER_COLOR);
			g.drawRoundRect((int) box.x, (int) box.y, (int) box.width - 1,
					(int) box.height - 1, ARC_SIZE, ARC_SIZE);

			// draw the text on top of the box (possibly multiple lines)
			g.setColor(TEXT_COLOR);
			String s = getText(tree);
			String[] lines = s.split("\n");
			FontMetrics m = getFontMetrics(getFont());
			int x = (int) box.x + ARC_SIZE / 2;
			int y = (int) box.y + m.getAscent() + m.getLeading() + 1;
			for (int i = 0; i < lines.length; i++) {
				g.drawString(lines[i], x, y);
				y += m.getHeight();
			}
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);

			paintEdges(g, getTree().getRoot());

			// paint the boxes
			for (Tree Tree : treeLayout.getNodeBounds().keySet()) {
				paintBox(g, Tree);
			}
		}
	}

	private AntlrTreeLayouter layouter = new AntlrTreeLayouter();

	private static void showInDialog(JComponent panel) {
		JDialog dialog = new JDialog();
		Container contentPane = dialog.getContentPane();
		((JComponent) contentPane).setBorder(BorderFactory.createEmptyBorder(
				10, 10, 10, 10));
		contentPane.add(panel);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}

	public void open(Tree tree) {
		AntlrTreeLayout layout = layouter.layout(tree);
		AntlrTreePane panel = new AntlrTreePane(layout);
		showInDialog(panel);
	}
}
