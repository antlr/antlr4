/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.gui;

import org.abego.treelayout.Configuration;
import org.abego.treelayout.NodeExtentProvider;
import org.abego.treelayout.TreeForTreeLayout;
import org.abego.treelayout.TreeLayout;
import org.abego.treelayout.util.DefaultConfiguration;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.Tree;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class TreePostScriptGenerator {
	public class VariableExtentProvide implements NodeExtentProvider<Tree> {
		@Override
		public double getWidth(Tree tree) {
			String s = getText(tree);
			return doc.getWidth(s) + nodeWidthPadding*2;
		}

		@Override
		public double getHeight(Tree tree) {
			String s = getText(tree);
			double h =
				doc.getLineHeight() + nodeHeightPaddingAbove + nodeHeightPaddingBelow;
			String[] lines = s.split("\n");
			return h * lines.length;
		}
	}

	protected double gapBetweenLevels = 17;
	protected double gapBetweenNodes = 7;
	protected int nodeWidthPadding = 1;  // added to left/right
	protected int nodeHeightPaddingAbove = 0;
	protected int nodeHeightPaddingBelow = 5;

	protected Tree root;
	protected TreeTextProvider treeTextProvider;
	protected TreeLayout<Tree> treeLayout;

	protected PostScriptDocument doc;

	public TreePostScriptGenerator(List<String> ruleNames, Tree root) {
		this(ruleNames, root, PostScriptDocument.DEFAULT_FONT, 11);
	}

	public TreePostScriptGenerator(List<String> ruleNames, Tree root,
								   String fontName, int fontSize)
	{
		this.root = root;
		setTreeTextProvider(new TreeViewer.DefaultTreeTextProvider(ruleNames));
		doc = new PostScriptDocument(fontName, fontSize);
		boolean compareNodeIdentities = true;
		this.treeLayout =
			new TreeLayout<Tree>(getTreeLayoutAdaptor(root),
								 new VariableExtentProvide(),
								 new DefaultConfiguration<Tree>(gapBetweenLevels,
																gapBetweenNodes,
																Configuration.Location.Bottom),
                                 compareNodeIdentities);
	}

	/** Get an adaptor for root that indicates how to walk ANTLR trees.
	 *  Override to change the adapter from the default of {@link TreeLayoutAdaptor}  */
	public TreeForTreeLayout<Tree> getTreeLayoutAdaptor(Tree root) {
		return new TreeLayoutAdaptor(root);
	}

	public String getPS() {
		// generate the edges and boxes (with text)
		generateEdges(getTree().getRoot());
		for (Tree node : treeLayout.getNodeBounds().keySet()) {
			generateNode(node);
		}

		Dimension size = treeLayout.getBounds().getBounds().getSize();
		doc.boundingBox(size.width, size.height);
		doc.close();
		return doc.getPS();
	}

	protected void generateEdges(Tree parent) {
		if (!getTree().isLeaf(parent)) {
			Rectangle2D.Double parentBounds = getBoundsOfNode(parent);
//			System.out.println("%% parent("+getText(parent)+")="+parentBounds);
			double x1 = parentBounds.getCenterX();
			double y1 = parentBounds.y;
			for (Tree child : getChildren(parent)) {
				Rectangle2D.Double childBounds = getBoundsOfNode(child);
//				System.out.println("%% child("+getText(child)+")="+childBounds);
				double x2 = childBounds.getCenterX();
				double y2 = childBounds.getMaxY();
				doc.line(x1, y1, x2, y2);
				generateEdges(child);
			}
		}
	}

	protected void generateNode(Tree t) {
		// draw the text on top of the box (possibly multiple lines)
		String[] lines = getText(t).split("\n");
		Rectangle2D.Double box = getBoundsOfNode(t);
		// for debugging, turn this on to see boundingbox of nodes
		//doc.rect(box.x, box.y, box.width, box.height);
		// make error nodes from parse tree red by default
		if ( t instanceof ErrorNode ) {
			doc.highlight(box.x, box.y, box.width, box.height);
		}
		double x = box.x+nodeWidthPadding;
		double y = box.y+nodeHeightPaddingBelow;
		for (int i = 0; i < lines.length; i++) {
			doc.text(lines[i], x, y);
			y += doc.getLineHeight();
		}
	}

	protected TreeForTreeLayout<Tree> getTree() {
		return treeLayout.getTree();
	}

	protected Iterable<Tree> getChildren(Tree parent) {
		return getTree().getChildren(parent);
	}

	protected Rectangle2D.Double getBoundsOfNode(Tree node) {
		return treeLayout.getNodeBounds().get(node);
	}

	protected String getText(Tree tree) {
		String s = treeTextProvider.getText(tree);
		s = Utils.escapeWhitespace(s, false);
		return s;
	}

	public TreeTextProvider getTreeTextProvider() {
		return treeTextProvider;
	}

	public void setTreeTextProvider(TreeTextProvider treeTextProvider) {
		this.treeTextProvider = treeTextProvider;
	}

}
