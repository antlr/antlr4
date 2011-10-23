/*
 [The "BSD license"]
  Copyright (c) 2011 Udo Borkowski and Terence Parr
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

package org.antlr.v4.runtime.tree.gui;

import org.abego.treelayout.*;
import org.abego.treelayout.util.DefaultConfiguration;
import org.antlr.v4.runtime.BaseRecognizer;
import org.antlr.v4.runtime.tree.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class TreeViewer extends JComponent {
	public static class DefaultTreeTextProvider implements TreeTextProvider {
		BaseRecognizer parser;
		public DefaultTreeTextProvider(BaseRecognizer parser) {
			this.parser = parser;
		}

		@Override
		public String getText(Tree node) {
			return String.valueOf(Trees.getNodeText(node, parser));
		}
	}

	public static class VariableExtentProvide implements NodeExtentProvider<Tree> {
		TreeViewer viewer;
		public VariableExtentProvide(TreeViewer viewer) {
			this.viewer = viewer;
		}
		@Override
		public double getWidth(Tree tree) {
			FontMetrics fontMetrics = viewer.getFontMetrics(viewer.font);
			String s = viewer.getText(tree);
			int w = fontMetrics.stringWidth(s) + viewer.nodeWidthPadding*2;
			return w;
		}

		@Override
		public double getHeight(Tree tree) {
			FontMetrics fontMetrics = viewer.getFontMetrics(viewer.font);
			int h = fontMetrics.getHeight() + viewer.nodeHeightPadding*2;
			String s = viewer.getText(tree);
			String[] lines = s.split("\n");
			return h * lines.length;
		}
	}

	protected TreeTextProvider treeTextProvider;
	protected TreeLayout<Tree> treeLayout;

	protected String fontName = Font.MONOSPACED;
	protected int fontStyle = Font.PLAIN;
	protected int fontSize = 12;
	protected Font font = new Font(fontName, fontStyle, fontSize);

	protected double gapBetweenLevels = 12;
	protected double gapBetweenNodes = 7;
	protected int nodeWidthPadding = 2;  // added to left/right
	protected int nodeHeightPadding = 4; // added above/below
	protected int arcSize = 0;           // make an arc in node outline?

	protected Color boxColor = Color.white;
	protected Color borderColor = Color.white;
	protected Color textColor = Color.black;

	protected BaseRecognizer parser;

	public TreeViewer(BaseRecognizer parser, Tree tree) {
		this.parser = parser;
		setTreeTextProvider(new DefaultTreeTextProvider(parser));
		this.treeLayout =
			new TreeLayout<Tree>(new TreeLayoutAdaptor(tree),
								 new TreeViewer.VariableExtentProvide(this),
								 new DefaultConfiguration<Tree>(gapBetweenLevels,
																gapBetweenNodes));
		Dimension size = treeLayout.getBounds().getBounds().getSize();
		setPreferredSize(size);
		setFont(font);
	}

	// ---------------- PAINT -----------------------------------------------

	protected void paintEdges(Graphics g, Tree parent) {
		if (!getTree().isLeaf(parent)) {
			Rectangle2D.Double b1 = getBoundsOfNode(parent);
			double x1 = b1.getCenterX();
			double y1 = b1.getCenterY();
			for (Tree child : getTree().getChildren(parent)) {
				Rectangle2D.Double b2 = getBoundsOfNode(child);
				g.drawLine((int) x1, (int) y1, (int) b2.getCenterX(),
						   (int) b2.getCenterY());

				paintEdges(g, child);
			}
		}
	}

	protected void paintBox(Graphics g, Tree tree) {
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
		int x = (int) box.x + arcSize / 2 + nodeWidthPadding;
		int y = (int) box.y + m.getAscent() + m.getLeading() + 1 + nodeHeightPadding;
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

	protected Rectangle2D.Double getBoundsOfNode(Tree node) {
		return treeLayout.getNodeBounds().get(node);
	}

	protected String getText(Tree tree) {
		return treeTextProvider.getText(tree);
	}

	public TreeTextProvider getTreeTextProvider() {
		return treeTextProvider;
	}

	public void setTreeTextProvider(TreeTextProvider treeTextProvider) {
		this.treeTextProvider = treeTextProvider;
	}

	protected static void showInDialog(TreeViewer viewer) {
		JDialog dialog = new JDialog();
		Container contentPane = dialog.getContentPane();
		((JComponent) contentPane).setBorder(BorderFactory.createEmptyBorder(
				10, 10, 10, 10));
		contentPane.add(viewer);
		contentPane.setBackground(Color.white);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}

	public void open() {
		showInDialog(this);
	}

	// ---------------------------------------------------

	public void setFontSize(int sz) {
		fontSize = sz;
		font = new Font(fontName, fontStyle, fontSize);
	}

	public void setFontName(String name) {
		fontName = name;
		font = new Font(fontName, fontStyle, fontSize);
	}

	@Override
	public Font getFont() {
		return font;
	}

	@Override
	public void setFont(Font font) {
		this.font = font;
	}

	public int getArcSize() {
		return arcSize;
	}

	public void setArcSize(int arcSize) {
		this.arcSize = arcSize;
	}

	public Color getBoxColor() {
		return boxColor;
	}

	public void setBoxColor(Color boxColor) {
		this.boxColor = boxColor;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	public Color getTextColor() {
		return textColor;
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}

	protected TreeForTreeLayout<Tree> getTree() {
		return treeLayout.getTree();
	}
}
