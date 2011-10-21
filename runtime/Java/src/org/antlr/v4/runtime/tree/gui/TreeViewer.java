package org.antlr.v4.runtime.tree.gui;

import org.abego.treelayout.*;
import org.antlr.v4.runtime.tree.Tree;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class TreeViewer extends JComponent {
	private Font font = new Font(Font.MONOSPACED, Font.PLAIN, 12);

	private int arcSize = 10;

	public int getArcSize() {
		return arcSize;
	}

	public void setArcSize(int arcSize) {
		this.arcSize = arcSize;
	}

	// ----------------------------------------------------------------------

	private Color boxColor = Color.orange;

	public Color getBoxColor() {
		return boxColor;
	}

	public void setBoxColor(Color boxColor) {
		this.boxColor = boxColor;
	}

	// ----------------------------------------------------------------------

	private Color borderColor = Color.darkGray;

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	// ----------------------------------------------------------------------

	private Color textColor = Color.black;

	public Color getTextColor() {
		return textColor;
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}

	private TreeViewer.AntlrTreeLayout treeLayout;

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
		return treeTextProvider.getText(tree);
	}

	public TreeViewer(Tree tree) {
		this.treeLayout = layouter.layout(tree);
		Dimension size = treeLayout.getBounds().getBounds().getSize();
		setPreferredSize(size);
		setFont(font);
	}

	// -------------------------------------------------------------------
	// painting

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
		g.setColor(boxColor);
		Rectangle2D.Double box = getBoundsOfNode(tree);
		g.fillRoundRect((int) box.x, (int) box.y, (int) box.width - 1,
						(int) box.height - 1, arcSize, arcSize);
		g.setColor(borderColor);
		g.drawRoundRect((int) box.x, (int) box.y, (int) box.width - 1,
						(int) box.height - 1, arcSize, arcSize);

		// draw the text on top of the box (possibly multiple lines)
		g.setColor(textColor);
		String s = getText(tree);
		String[] lines = s.split("\n");
		FontMetrics m = getFontMetrics(font);
		int x = (int) box.x + arcSize / 2;
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

	// ----------------------------------------------------------------------

	public static class VariableExtentProvide<Tree> implements NodeExtentProvider<Tree> {
		@Override
		public double getHeight(Tree tree) {
//			FontMetrics m = getFontMetrics(font);
//			String text = treeTextProvider.getText(tree);
//			int x = (int) box.x + arcSize / 2;
//			int y = (int) box.y + m.getAscent() + m.getLeading() + 1;
//			int hgt = metrics.getHeight();
//			// get the advance of my text in this font and render context
//			int adv = metrics.stringWidth(text);
			return 0;
		}

		@Override
		public double getWidth(Tree tree) {
			return 0;
		}
	}
	public static class AntlrTreeLayout extends TreeLayout<Tree> {
		public AntlrTreeLayout(TreeForTreeLayout<Tree> tree,
				NodeExtentProvider<Tree> nodeExtentProvider,
				Configuration<Tree> configuration) {
			super(tree, nodeExtentProvider, configuration);
		}
	}

	// ----------------------------------------------------------------------

	private TreeTextProvider treeTextProvider = new DefaultTreeTextProvider();

	public TreeTextProvider getTreeTextProvider() {
		return treeTextProvider;
	}

	public void setTreeTextProvider(TreeTextProvider treeTextProvider) {
		this.treeTextProvider = treeTextProvider;
	}

	// ----------------------------------------------------------------------

	private AntlrTreeLayouter layouter = new AntlrTreeLayouter();

	// ----------------------------------------------------------------------

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

	public void open() {
		showInDialog(this);
	}

}
