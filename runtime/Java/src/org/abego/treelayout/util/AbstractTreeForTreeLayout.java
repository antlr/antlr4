/*
 * [The "BSD license"]
 * Copyright (c) 2011, abego Software GmbH, Germany (http://www.abego.org)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the abego Software GmbH nor the names of its 
 *    contributors may be used to endorse or promote products derived from this 
 *    software without specific prior written permission.
 *    
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.abego.treelayout.util;

import java.util.List;

import org.abego.treelayout.TreeForTreeLayout;
import org.abego.treelayout.internal.util.java.lang.IterableUtil;
import org.abego.treelayout.internal.util.java.util.ListUtil;

/**
 * Provides an easy way to implement the {@link TreeForTreeLayout} interface by
 * defining just two simple methods and a constructor.
 * <p>
 * To use this class the underlying tree must provide the children as a list
 * (see {@link #getChildrenList(Object)} and give direct access to the parent of
 * a node (see {@link #getParent(Object)}).
 * <p>
 * 
 * See also {@link DefaultTreeForTreeLayout}.
 * 
 * @author Udo Borkowski (ub@abego.org)
 * 
 * @param <TreeNode>
 */
abstract public class AbstractTreeForTreeLayout<TreeNode> implements
		TreeForTreeLayout<TreeNode> {

	/**
	 * Returns the parent of a node, if it has one.
	 * <p>
	 * Time Complexity: O(1)
	 * 
	 * @param node
	 * @return [nullable] the parent of the node, or null when the node is a
	 *         root.
	 */
	abstract public TreeNode getParent(TreeNode node);

	/**
	 * Return the children of a node as a {@link List}.
	 * <p>
	 * Time Complexity: O(1)
	 * <p>
	 * Also the access to an item of the list must have time complexity O(1).
	 * <p>
	 * A client must not modify the returned list.
	 * 
	 * @param node
	 * @return the children of the given node. When node is a leaf the list is
	 *         empty.
	 */
	abstract public List<TreeNode> getChildrenList(TreeNode node);

	private final TreeNode root;

	public AbstractTreeForTreeLayout(TreeNode root) {
		this.root = root;
	}

	@Override
	public TreeNode getRoot() {
		return root;
	}

	@Override
	public boolean isLeaf(TreeNode node) {
		return getChildrenList(node).isEmpty();
	}

	@Override
	public boolean isChildOfParent(TreeNode node, TreeNode parentNode) {
		return getParent(node) == parentNode;
	}

	@Override
	public Iterable<TreeNode> getChildren(TreeNode node) {
		return getChildrenList(node);
	}

	@Override
	public Iterable<TreeNode> getChildrenReverse(TreeNode node) {
		return IterableUtil.createReverseIterable(getChildrenList(node));
	}

	@Override
	public TreeNode getFirstChild(TreeNode parentNode) {
		return getChildrenList(parentNode).get(0);
	}

	@Override
	public TreeNode getLastChild(TreeNode parentNode) {
		return ListUtil.getLast(getChildrenList(parentNode));
	}
}