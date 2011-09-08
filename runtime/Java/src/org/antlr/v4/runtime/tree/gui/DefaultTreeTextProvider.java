package org.antlr.v4.runtime.tree.gui;

import org.antlr.v4.runtime.tree.Tree;

public class DefaultTreeTextProvider implements TreeTextProvider {

	@Override
	public String getText(Tree node) {
		return String.valueOf(node.getPayload());
	}
}
