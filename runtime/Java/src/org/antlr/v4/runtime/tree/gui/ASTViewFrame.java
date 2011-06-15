package org.antlr.v4.runtime.tree.gui;

import javax.swing.*;
import java.awt.*;
/*
 * Created by JFormDesigner on Mon Jan 18 14:54:16 PST 2010
 */

/**
 * @author Terence Parr
 */
public class ASTViewFrame extends JFrame {
	public ASTViewFrame() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		scrollPane1 = new JScrollPane();
		tree = new JTree();

		//======== this ========
		setTitle("ANTLR AST Viewer");
		Container contentPane = getContentPane();
		contentPane.setLayout(new GridLayout(1, 1));

		//======== scrollPane1 ========
		{
			scrollPane1.setViewportView(tree);
		}
		contentPane.add(scrollPane1);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JScrollPane scrollPane1;
	public JTree tree;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
