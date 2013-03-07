/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
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

package org.antlr.v4.runtime.tree.gui;

import org.abego.treelayout.NodeExtentProvider;
import org.abego.treelayout.TreeForTreeLayout;
import org.abego.treelayout.TreeLayout;
import org.abego.treelayout.util.DefaultConfiguration;
import org.antlr.v4.runtime.misc.GraphicsSupport;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.Tree;
import org.antlr.v4.runtime.tree.Trees;

import javax.imageio.ImageIO;
import javax.print.PrintException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TreeViewer extends JComponent {
	public static final Color LIGHT_RED = new Color(244, 213, 211);

	public static class DefaultTreeTextProvider implements TreeTextProvider {
		private final List<String> ruleNames;

		public DefaultTreeTextProvider(@Nullable List<String> ruleNames) {
			this.ruleNames = ruleNames;
		}

		@Override
		public String getText(Tree node) {
			return String.valueOf(Trees.getNodeText(node, ruleNames));
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
	protected java.util.List<Tree> highlightedNodes;

	protected String fontName = "Helvetica"; //Font.SANS_SERIF;
	protected int fontStyle = Font.PLAIN;
	protected int fontSize = 11;
	protected Font font = new Font(fontName, fontStyle, fontSize);

	protected double gapBetweenLevels = 17;
	protected double gapBetweenNodes = 7;
	protected int nodeWidthPadding = 2;  // added to left/right
	protected int nodeHeightPadding = 0; // added above/below
	protected int arcSize = 0;           // make an arc in node outline?

	protected double scale = 1.0;

	protected Color boxColor = null;     // set to a color to make it draw background

	protected Color highlightedBoxColor = Color.lightGray;
	protected Color borderColor = null;
	protected Color textColor = Color.black;

	public TreeViewer(@Nullable List<String> ruleNames, Tree tree) {
		setTreeTextProvider(new DefaultTreeTextProvider(ruleNames));
        boolean useIdentity = true; // compare node identity
		this.treeLayout =
			new TreeLayout<Tree>(new TreeLayoutAdaptor(tree),
								 new TreeViewer.VariableExtentProvide(this),
								 new DefaultConfiguration<Tree>(gapBetweenLevels,
																gapBetweenNodes),
                                 useIdentity);
		updatePreferredSize();
		setFont(font);
	}

	private void updatePreferredSize() {
		setPreferredSize(getScaledTreeSize());
		invalidate();
		if (getParent() != null) {
			getParent().validate();
		}
		repaint();
	}

	// ---------------- PAINT -----------------------------------------------

	private boolean useCurvedEdges = false;

	public boolean getUseCurvedEdges() {
		return useCurvedEdges;
	}

	public void setUseCurvedEdges(boolean useCurvedEdges) {
		this.useCurvedEdges = useCurvedEdges;
	}

	protected void paintEdges(Graphics g, Tree parent) {
		if (!getTree().isLeaf(parent)) {
            BasicStroke stroke = new BasicStroke(1.0f, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND);
            ((Graphics2D)g).setStroke(stroke);

			Rectangle2D.Double parentBounds = getBoundsOfNode(parent);
			double x1 = parentBounds.getCenterX();
			double y1 = parentBounds.getMaxY();
			for (Tree child : getTree().getChildren(parent)) {
				Rectangle2D.Double childBounds = getBoundsOfNode(child);
				double x2 = childBounds.getCenterX();
				double y2 = childBounds.getMinY();
				if (getUseCurvedEdges()) {
					CubicCurve2D c = new CubicCurve2D.Double();
					double ctrlx1 = x1;
					double ctrly1 = (y1+y2)/2;
					double ctrlx2 = x2;
					double ctrly2 = y1;
					c.setCurve(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2);
					((Graphics2D) g).draw(c);
				} else {
					g.drawLine((int) x1, (int) y1,
							   (int) x2, (int) y2);
				}
				paintEdges(g, child);
			}
		}
	}

	protected void paintBox(Graphics g, Tree tree) {
		Rectangle2D.Double box = getBoundsOfNode(tree);
		// draw the box in the background
		if ( isHighlighted(tree) || boxColor!=null ||
			 tree instanceof ErrorNode )
		{
			if ( isHighlighted(tree) ) g.setColor(highlightedBoxColor);
			else if ( tree instanceof ErrorNode ) g.setColor(LIGHT_RED);
			else g.setColor(boxColor);
			g.fillRoundRect((int) box.x, (int) box.y, (int) box.width - 1,
							(int) box.height - 1, arcSize, arcSize);
		}
		if ( borderColor!=null ) {
            g.setColor(borderColor);
            g.drawRoundRect((int) box.x, (int) box.y, (int) box.width - 1,
                    (int) box.height - 1, arcSize, arcSize);
        }

		// draw the text on top of the box (possibly multiple lines)
		g.setColor(textColor);
		String s = getText(tree);
		String[] lines = s.split("\n");
		FontMetrics m = getFontMetrics(font);
		int x = (int) box.x + arcSize / 2 + nodeWidthPadding;
		int y = (int) box.y + m.getAscent() + m.getLeading() + 1 + nodeHeightPadding;
		for (int i = 0; i < lines.length; i++) {
			text(g, lines[i], x, y);
			y += m.getHeight();
		}
	}

	public void text(Graphics g, String s, int x, int y) {
//		System.out.println("drawing '"+s+"' @ "+x+","+y);
		s = Utils.escapeWhitespace(s, true);
		g.drawString(s, x, y);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2 = (Graphics2D)g;
		// anti-alias the lines
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
      						RenderingHints.VALUE_ANTIALIAS_ON);

		// Anti-alias the text
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                         	RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

//		AffineTransform at = g2.getTransform();
//        g2.scale(
//            (double) this.getWidth() / 400,
//            (double) this.getHeight() / 400);
//
//		g2.setTransform(at);

		paintEdges(g, getTree().getRoot());

		// paint the boxes
		for (Tree Tree : treeLayout.getNodeBounds().keySet()) {
			paintBox(g, Tree);
		}
	}

	@Override
	protected Graphics getComponentGraphics(Graphics g) {
		Graphics2D g2d=(Graphics2D)g;
		g2d.scale(scale, scale);
		return super.getComponentGraphics(g2d);
	}

	// ----------------------------------------------------------------------

	@NotNull
	protected static JDialog showInDialog(final TreeViewer viewer) {
		final JDialog dialog = new JDialog();

		// Make new content pane
		final Container contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(0,0));
		contentPane.setBackground(Color.white);
		dialog.setContentPane(contentPane);

		// Wrap viewer in scroll pane
		JScrollPane scrollPane = new JScrollPane(viewer);
		// Make the scrollpane (containing the viewer) the center component
		contentPane.add(scrollPane, BorderLayout.CENTER);

		JPanel wrapper = new JPanel(new FlowLayout());

		// Add button to bottom
		JPanel bottomPanel = new JPanel(new BorderLayout(0,0));
		contentPane.add(bottomPanel, BorderLayout.SOUTH);

		JButton ok = new JButton("OK");
		ok.addActionListener(
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dialog.setVisible(false);
					dialog.dispose();
				}
			}
		);
		wrapper.add(ok);

		// Add an export-to-png button right of the "OK" button
		JButton png = new JButton("png");
		png.addActionListener(
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					generatePNGFile(viewer, dialog);
				}
			}
		);
		wrapper.add(png);

		bottomPanel.add(wrapper, BorderLayout.SOUTH);

		// Add scale slider
		int sliderValue = (int) ((viewer.getScale()-1.0) * 1000);
		final JSlider scaleSlider = new JSlider(JSlider.HORIZONTAL,
										  -999,1000,sliderValue);
		scaleSlider.addChangeListener(
			new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					int v = scaleSlider.getValue();
					viewer.setScale(v / 1000.0 + 1.0);
				}
			}
		);
		bottomPanel.add(scaleSlider, BorderLayout.CENTER);

		// make viz
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		return dialog;
	}

	private static void generatePNGFile(TreeViewer viewer, JDialog dialog) {
		BufferedImage bi = new BufferedImage(viewer.getSize().width,
											 viewer.getSize().height,
											 BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		viewer.paint(g);
		g.dispose();

		try {
			File suggestedFile = generateNonExistingPngFile();
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(suggestedFile.getParentFile());
			fileChooser.setSelectedFile(suggestedFile);

			int returnValue = fileChooser.showSaveDialog(dialog);

			if (returnValue == JFileChooser.APPROVE_OPTION) {

				File pngFile = fileChooser.getSelectedFile();

				boolean writeFile = true;

				if (pngFile.exists()) {
					int answer = JOptionPane.showConfirmDialog(dialog,
															   "Overwrite existing file?",
															   "Overwrite?",
															   JOptionPane.YES_NO_OPTION);
					writeFile = (answer == JOptionPane.YES_OPTION);
				}

				if (writeFile) {

					ImageIO.write(bi, "png", pngFile);

					try {
						// Try to open the parent folder using the OS' native file manager.
						Desktop.getDesktop().open(pngFile.getParentFile());
					}
					catch (Exception ex) {
						// We could not launch the file manager: just show a popup that we
						// succeeded in saving the PNG file.
						JOptionPane.showMessageDialog(dialog, "Saved PNG to: " +
													  pngFile.getAbsolutePath());
						ex.printStackTrace();
					}
				}
			}
		}
		catch (Exception ex) {
			JOptionPane.showMessageDialog(dialog,
										  "Could not export to PNG: " + ex.getMessage(),
										  "Error",
										  JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	private static File generateNonExistingPngFile() {

		final String parent = ".";
		final String name = "antlr4_parse_tree";
		final String extension = ".png";

		File pngFile = new File(parent, name + extension);

		int counter = 1;

		// Keep looping until we create a File that does not yet exist.
		while (pngFile.exists()) {
			pngFile = new File(parent, name + "_"+ counter + extension);
			counter++;
		}

		return pngFile;
	}

	private Dimension getScaledTreeSize() {
		Dimension scaledTreeSize =
			treeLayout.getBounds().getBounds().getSize();
		scaledTreeSize = new Dimension((int)(scaledTreeSize.width*scale),
									   (int)(scaledTreeSize.height*scale));
		return scaledTreeSize;
	}

	@NotNull
	public Future<JDialog> open() {
		final TreeViewer viewer = this;
		viewer.setScale(1.5);
		Callable<JDialog> callable = new Callable<JDialog>() {
			JDialog result;

			@Override
			public JDialog call() throws Exception {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						result = showInDialog(viewer);
					}
				});

				return result;
			}
		};

		ExecutorService executor = Executors.newSingleThreadExecutor();

		try {
			return executor.submit(callable);
		}
		finally {
			executor.shutdown();
		}
	}

	public void save(String fileName) throws IOException, PrintException {
		JDialog dialog = new JDialog();
		Container contentPane = dialog.getContentPane();
		((JComponent) contentPane).setBorder(BorderFactory.createEmptyBorder(
				10, 10, 10, 10));
		contentPane.add(this);
		contentPane.setBackground(Color.white);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.dispose();
		GraphicsSupport.saveImage(this, fileName);
	}

	// ---------------------------------------------------

	protected Rectangle2D.Double getBoundsOfNode(Tree node) {
		return treeLayout.getNodeBounds().get(node);
	}

	protected String getText(Tree tree) {
		String s = treeTextProvider.getText(tree);
		s = Utils.escapeWhitespace(s, true);
		return s;
	}

	public TreeTextProvider getTreeTextProvider() {
		return treeTextProvider;
	}

	public void setTreeTextProvider(TreeTextProvider treeTextProvider) {
		this.treeTextProvider = treeTextProvider;
	}

	public void setFontSize(int sz) {
		fontSize = sz;
		font = new Font(fontName, fontStyle, fontSize);
	}

	public void setFontName(String name) {
		fontName = name;
		font = new Font(fontName, fontStyle, fontSize);
	}

	/** Slow for big lists of highlighted nodes */
	public void addHighlightedNodes(Collection<Tree> nodes) {
		highlightedNodes = new ArrayList<Tree>();
		highlightedNodes.addAll(nodes);
	}

	public void removeHighlightedNodes(Collection<Tree> nodes) {
		if ( highlightedNodes!=null ) {
			// only remove exact objects defined by ==, not equals()
			for (Tree t : nodes) {
				int i = getHighlightedNodeIndex(t);
				if ( i>=0 ) highlightedNodes.remove(i);
			}
		}
	}

	protected boolean isHighlighted(Tree node) {
		return getHighlightedNodeIndex(node) >= 0;
	}

	protected int getHighlightedNodeIndex(Tree node) {
		if ( highlightedNodes==null ) return -1;
		for (int i = 0; i < highlightedNodes.size(); i++) {
			Tree t = highlightedNodes.get(i);
			if ( t == node ) return i;
		}
		return -1;
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

	public Color getHighlightedBoxColor() {
		return highlightedBoxColor;
	}

	public void setHighlightedBoxColor(Color highlightedBoxColor) {
		this.highlightedBoxColor = highlightedBoxColor;
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

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		if(scale <= 0) {
			scale = 1;
		}
		this.scale = scale;
		updatePreferredSize();
	}
}
