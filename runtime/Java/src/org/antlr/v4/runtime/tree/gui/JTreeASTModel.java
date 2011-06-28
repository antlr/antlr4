package org.antlr.v4.runtime.tree.gui;

import org.antlr.v4.runtime.tree.*;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;

public class JTreeASTModel implements TreeModel {
    TreeAdaptor adaptor;
    Object root;

    public JTreeASTModel(TreeAdaptor adaptor, Object root) {
        this.adaptor = adaptor;
        this.root = root;
    }

    public JTreeASTModel(Object root) {
        this.adaptor = new CommonTreeAdaptor();
        this.root = root;
    }

    public int getChildCount(Object parent) {
        return adaptor.getChildCount(parent);
    }

    public int getIndexOfChild(Object parent, Object child){
        if ( parent==null ) return -1;
        return adaptor.getChildIndex(child);
    }

    public Object getChild(Object parent, int index){
        return adaptor.getChild(parent, index);
    }

    public boolean isLeaf(Object node) {
        return getChildCount(node)==0;
    }

    public Object getRoot() { return root; }

    public void valueForPathChanged(TreePath treePath, Object o) {
    }

    public void addTreeModelListener(TreeModelListener treeModelListener) {
    }

    public void removeTreeModelListener(TreeModelListener treeModelListener) {
    }
}
