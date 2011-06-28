package org.antlr.v4.runtime.tree.gui;

import org.antlr.v4.runtime.tree.*;

/** */
public class ASTViewer {
    TreeAdaptor adaptor;
    Object root;

    public ASTViewer(TreeAdaptor adaptor, Object root) {
        this.adaptor = adaptor;
        this.root = root;
    }

    public ASTViewer(Object root) {
        this.adaptor = new CommonTreeAdaptor();
        this.root = root;
    }

    public void open() {
        ASTViewFrame m = new ASTViewFrame();
        m.tree.setModel(new JTreeASTModel(adaptor, root));
        m.pack();
        m.setSize(800,600);
        m.setVisible(true);
    }
}
