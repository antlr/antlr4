/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.gui;

import org.abego.treelayout.NodeExtentProvider;
import org.abego.treelayout.TreeForTreeLayout;
import org.abego.treelayout.TreeLayout;
import org.abego.treelayout.util.DefaultConfiguration;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.Tree;
import org.antlr.v4.runtime.tree.Trees;

import javax.imageio.ImageIO;
import javax.print.PrintException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.prefs.Preferences;

public class TreeViewer extends JComponent {
	public static final Color LIGHT_RED = new Color(244, 213, 211);

	public static class DefaultTreeTextProvider implements TreeTextProvider {
		private final List<String> ruleNames;

		public DefaultTreeTextProvider(List<String> ruleNames) {
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

	public TreeViewer(List<String> ruleNames, Tree tree) {
		setRuleNames(ruleNames);
		if ( tree!=null ) {
			setTree(tree);
		}
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
				}
				else {
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
		boolean ruleFailedAndMatchedNothing = false;
		if ( tree instanceof ParserRuleContext ) {
			ParserRuleContext ctx = (ParserRuleContext) tree;
			ruleFailedAndMatchedNothing = ctx.exception != null &&
										  ctx.stop != null && ctx.stop.getTokenIndex() < ctx.start.getTokenIndex();
		}
		if ( isHighlighted(tree) || boxColor!=null ||
			 tree instanceof ErrorNode ||
			 ruleFailedAndMatchedNothing)
		{
			if ( isHighlighted(tree) ) g.setColor(highlightedBoxColor);
			else if ( tree instanceof ErrorNode || ruleFailedAndMatchedNothing ) g.setColor(LIGHT_RED);
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

		if ( treeLayout==null ) {
			return;
		}

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

	protected void generateEdges(Writer writer, Tree parent) throws IOException {
		if (!getTree().isLeaf(parent)) {
			Rectangle2D.Double b1 = getBoundsOfNode(parent);
			double x1 = b1.getCenterX();
			double y1 = b1.getCenterY();

			for (Tree child : getTree().getChildren(parent)) {
				Rectangle2D.Double childBounds = getBoundsOfNode(child);
				double x2 = childBounds.getCenterX();
				double y2 = childBounds.getMinY();
				writer.write(line(""+x1, ""+y1, ""+x2, ""+y2,
					"stroke:black; stroke-width:1px;"));
				generateEdges(writer, child);
			}
		}
	}

	protected void generateBox(Writer writer, Tree parent) throws IOException {

		// draw the box in the background
		Rectangle2D.Double box = getBoundsOfNode(parent);
		writer.write(rect(""+box.x, ""+box.y, ""+box.width, ""+box.height,
			"fill:orange; stroke:rgb(0,0,0);", "rx=\"1\""));

		// draw the text on top of the box (possibly multiple lines)
		String line = getText(parent).replace("<","&lt;").replace(">","&gt;");
		int fontSize = 10;
		int x = (int) box.x + 2;
		int y = (int) box.y + fontSize - 1;
		String style = String.format("font-family:sans-serif;font-size:%dpx;",
			fontSize);
		writer.write(text(""+x, ""+y, style, line));
	}

	private static String line(String x1, String y1, String x2, String y2,
		String style) {
		return String
			.format("<line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" style=\"%s\" />\n",
				x1, y1, x2, y2, style);
	}

	private static String rect(String x, String y, String width, String height,
		String style, String extraAttributes) {
		return String
			.format("<rect x=\"%s\" y=\"%s\" width=\"%s\" height=\"%s\" style=\"%s\" %s/>\n",
				x, y, width, height, style, extraAttributes);
	}

	private static String text(String x, String y, String style, String text) {
		return String.format(
			"<text x=\"%s\" y=\"%s\" style=\"%s\">\n%s\n</text>\n", x, y,
			style, text);
	}

	private void paintSVG(Writer writer) throws IOException {

		generateEdges(writer, getTree().getRoot());

		for (Tree tree : treeLayout.getNodeBounds().keySet()) {
			generateBox(writer, tree);
		}
	}

	@Override
	protected Graphics getComponentGraphics(Graphics g) {
		Graphics2D g2d=(Graphics2D)g;
		g2d.scale(scale, scale);
		return super.getComponentGraphics(g2d);
	}

	// ----------------------------------------------------------------------


    private static final String DIALOG_WIDTH_PREFS_KEY          = "dialog_width";
    private static final String DIALOG_HEIGHT_PREFS_KEY         = "dialog_height";
    private static final String DIALOG_X_PREFS_KEY              = "dialog_x";
    private static final String DIALOG_Y_PREFS_KEY              = "dialog_y";
    private static final String DIALOG_DIVIDER_LOC_PREFS_KEY    = "dialog_divider_location";
    private static final String DIALOG_VIEWER_SCALE_PREFS_KEY   = "dialog_viewer_scale";

	protected static JFrame showInDialog(final TreeViewer viewer) {
		final JFrame dialog = new JFrame();
		dialog.setTitle("Parse Tree Inspector");

        final Preferences prefs = Preferences.userNodeForPackage(TreeViewer.class);

		// Make new content panes
		final Container mainPane = new JPanel(new BorderLayout(5,5));
		final Container contentPane = new JPanel(new BorderLayout(0,0));
		contentPane.setBackground(Color.white);

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
                    dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
				}
			}
		);
		wrapper.add(ok);

		// Add an export-to-png button right of the "OK" button
		JButton png = new JButton("Export as PNG");
		png.addActionListener(
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					generatePNGFile(viewer, dialog);
				}
			}
		);
		wrapper.add(png);

		// Add an export-to-png button right of the "OK" button
		JButton svg = new JButton("Export as SVG");
		svg.addActionListener(
			new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				generateSVGFile(viewer, dialog);
			}
		}
		);
		wrapper.add(svg);

		bottomPanel.add(wrapper, BorderLayout.SOUTH);

		// Add scale slider
        double lastKnownViewerScale = prefs.getDouble(DIALOG_VIEWER_SCALE_PREFS_KEY, viewer.getScale());
        viewer.setScale(lastKnownViewerScale);

		int sliderValue = (int) ((lastKnownViewerScale - 1.0) * 1000);
		final JSlider scaleSlider = new JSlider(JSlider.HORIZONTAL, -999, 1000, sliderValue);

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

		// Add a JTree representing the parser tree of the input.
		JPanel treePanel = new JPanel(new BorderLayout(5, 5));

		// An "empty" icon that will be used for the JTree's nodes.
		Icon empty = new EmptyIcon();

		UIManager.put("Tree.closedIcon", empty);
		UIManager.put("Tree.openIcon", empty);
		UIManager.put("Tree.leafIcon", empty);

		Tree parseTreeRoot = viewer.getTree().getRoot();
		TreeNodeWrapper nodeRoot = new TreeNodeWrapper(parseTreeRoot, viewer);
		fillTree(nodeRoot, parseTreeRoot, viewer);
		final JTree tree = new JTree(nodeRoot);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {

				JTree selectedTree = (JTree) e.getSource();
				TreePath path = selectedTree.getSelectionPath();
				if (path!=null) {
					TreeNodeWrapper treeNode = (TreeNodeWrapper) path.getLastPathComponent();

					// Set the clicked AST.
					viewer.setTree((Tree) treeNode.getUserObject());
				}
			}
		});

		treePanel.add(new JScrollPane(tree));

		// Create the pane for both the JTree and the AST
		final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				treePanel, contentPane);

		mainPane.add(splitPane, BorderLayout.CENTER);

		dialog.setContentPane(mainPane);

		// make viz
        WindowListener exitListener = new WindowAdapter() {
	        @Override
            public void windowClosing(WindowEvent e) {
                prefs.putInt(DIALOG_WIDTH_PREFS_KEY, (int) dialog.getSize().getWidth());
                prefs.putInt(DIALOG_HEIGHT_PREFS_KEY, (int) dialog.getSize().getHeight());
                prefs.putDouble(DIALOG_X_PREFS_KEY, dialog.getLocationOnScreen().getX());
                prefs.putDouble(DIALOG_Y_PREFS_KEY, dialog.getLocationOnScreen().getY());
                prefs.putInt(DIALOG_DIVIDER_LOC_PREFS_KEY, splitPane.getDividerLocation());
                prefs.putDouble(DIALOG_VIEWER_SCALE_PREFS_KEY, viewer.getScale());

                dialog.setVisible(false);
                dialog.dispose();
            }
        };
        dialog.addWindowListener(exitListener);
		dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        int width = prefs.getInt(DIALOG_WIDTH_PREFS_KEY, 600);
        int height = prefs.getInt(DIALOG_HEIGHT_PREFS_KEY, 500);
		dialog.setPreferredSize(new Dimension(width, height));
		dialog.pack();

		// After pack(): set the divider at 1/3 (200/600) of the frame.
        int dividerLocation = prefs.getInt(DIALOG_DIVIDER_LOC_PREFS_KEY, 200);
		splitPane.setDividerLocation(dividerLocation);

        if (prefs.getDouble(DIALOG_X_PREFS_KEY, -1) != -1) {
            dialog.setLocation(
                    (int)prefs.getDouble(DIALOG_X_PREFS_KEY, 100),
                    (int)prefs.getDouble(DIALOG_Y_PREFS_KEY, 100)
            );
        }
        else {
            dialog.setLocationRelativeTo(null);
        }

		dialog.setVisible(true);
		return dialog;
	}

	private static void generatePNGFile(TreeViewer viewer, JFrame dialog) {
		BufferedImage bi = new BufferedImage(viewer.getSize().width,
											 viewer.getSize().height,
											 BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		viewer.paint(g);
		g.dispose();

		try {
			JFileChooser fileChooser = getFileChooser(".png", "PNG files");

			int returnValue = fileChooser.showSaveDialog(dialog);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File pngFile = fileChooser.getSelectedFile();
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
		catch (Exception ex) {
			JOptionPane.showMessageDialog(dialog,
										  "Could not export to PNG: " + ex.getMessage(),
										  "Error",
										  JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	private static JFileChooser getFileChooser(final String fileEnding,
												final String description) {
		File suggestedFile = generateNonExistingFile(fileEnding);
		JFileChooser fileChooser = new JFileChooserConfirmOverwrite();
		fileChooser.setCurrentDirectory(suggestedFile.getParentFile());
		fileChooser.setSelectedFile(suggestedFile);
		FileFilter filter = new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				if (pathname.isFile()) {
					return pathname.getName().toLowerCase().endsWith(fileEnding);
				}

				return true;
			}

			@Override
			public String getDescription() {
				return description+" (*"+fileEnding+")";
			}
		};
		fileChooser.addChoosableFileFilter(filter);
		fileChooser.setFileFilter(filter);
		return fileChooser;
	}

	private static void generateSVGFile(TreeViewer viewer, JFrame dialog) {

		try {
			JFileChooser fileChooser = getFileChooser(".svg", "SVG files");

			int returnValue = fileChooser.showSaveDialog(dialog);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File svgFile = fileChooser.getSelectedFile();
				// save the new svg file here!
				BufferedWriter writer = new BufferedWriter(new FileWriter(svgFile));
				// HACK: multiplying with 1.1 should be replaced wit an accurate number
				writer.write("<svg width=\"" + viewer.getSize().getWidth() * 1.1 + "\" height=\"" + viewer.getSize().getHeight() * 1.1 + "\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">");
				viewer.paintSVG(writer);
				writer.write("</svg>");
				writer.flush();
				writer.close();
				try {
					// Try to open the parent folder using the OS' native file manager.
					Desktop.getDesktop().open(svgFile.getParentFile());
				} catch (Exception ex) {
					// We could not launch the file manager: just show a popup that we
					// succeeded in saving the PNG file.
					JOptionPane.showMessageDialog(dialog, "Saved SVG to: "
						+ svgFile.getAbsolutePath());
					ex.printStackTrace();
				}
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(dialog,
				"Could not export to SVG: " + ex.getMessage(),
				"Error",
				JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	private static File generateNonExistingFile(String extension) {

		final String parent = ".";
		final String name = "antlr4_parse_tree";

		File file = new File(parent, name + extension);

		int counter = 1;

		// Keep looping until we create a File that does not yet exist.
		while (file.exists()) {
			file = new File(parent, name + "_" + counter + extension);
			counter++;
		}

		return file;
	}

	private static void fillTree(TreeNodeWrapper node, Tree tree, TreeViewer viewer) {

		if (tree == null) {
			return;
		}

		for (int i = 0; i < tree.getChildCount(); i++) {

			Tree childTree = tree.getChild(i);
			TreeNodeWrapper childNode = new TreeNodeWrapper(childTree, viewer);

			node.add(childNode);

			fillTree(childNode, childTree, viewer);
		}
	}

	private Dimension getScaledTreeSize() {
		Dimension scaledTreeSize =
			treeLayout.getBounds().getBounds().getSize();
		scaledTreeSize = new Dimension((int)(scaledTreeSize.width*scale),
									   (int)(scaledTreeSize.height*scale));
		return scaledTreeSize;
	}


	public Future<JFrame> open() {
		final TreeViewer viewer = this;
		viewer.setScale(1.5);
		Callable<JFrame> callable = new Callable<JFrame>() {
			JFrame result;

			@Override
			public JFrame call() throws Exception {
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
		JFrame dialog = new JFrame();
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

	public void setTree(Tree root) {
		if ( root!=null ) {
			boolean useIdentity = true; // compare node identity
			this.treeLayout =
				new TreeLayout<Tree>(getTreeLayoutAdaptor(root),
									 new TreeViewer.VariableExtentProvide(this),
									 new DefaultConfiguration<Tree>(gapBetweenLevels,
																	gapBetweenNodes),
									 useIdentity);
			// Let the UI display this new AST.
			updatePreferredSize();
		}
		else {
			this.treeLayout = null;
			repaint();
		}
	}

	/** Get an adaptor for root that indicates how to walk ANTLR trees.
	 *  Override to change the adapter from the default of {@link TreeLayoutAdaptor}  */
	public TreeForTreeLayout<Tree> getTreeLayoutAdaptor(Tree root) {
		return new TreeLayoutAdaptor(root);
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

	public void setRuleNames(List<String> ruleNames) {
		setTreeTextProvider(new DefaultTreeTextProvider(ruleNames));
	}

	private static class TreeNodeWrapper extends DefaultMutableTreeNode {

		final TreeViewer viewer;

		TreeNodeWrapper(Tree tree, TreeViewer viewer) {
			super(tree);
			this.viewer = viewer;
		}

		@Override
		public String toString() {
			return viewer.getText((Tree) this.getUserObject());
		}
	}

	private static class EmptyIcon implements Icon {

		@Override
		public int getIconWidth() {
			return 0;
		}

		@Override
		public int getIconHeight() {
			return 0;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			/* Do nothing. */
		}
	}
}
