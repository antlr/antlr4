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
package org.abego.treelayout;

/**
 * Represents a tree to be used by the {@link TreeLayout}.
 * <p>
 * The TreeForTreeLayout interface is designed to best match the implemented
 * layout algorithm and to ensure the algorithm's time complexity promises in
 * all possible cases. However in most situation a client must not deal with all
 * details of this interface and can directly use the
 * {@link org.abego.treelayout.util.AbstractTreeForTreeLayout} to implement this
 * interface or even use the
 * {@link org.abego.treelayout.util.DefaultTreeForTreeLayout} class directly.
 * 
 * @author Udo Borkowski (ub@abego.org)
 * 
 * @param <TreeNode>
 */
public interface TreeForTreeLayout<TreeNode> {

	/**
	 * Returns the the root of the tree.
	 * <p>
	 * Time Complexity: O(1)
	 * 
	 * @return the root of the tree
	 */
	TreeNode getRoot();

	/**
	 * Tells if a node is a leaf in the tree.
	 * <p>
	 * Time Complexity: O(1)
	 * 
	 * @param node
	 * @return true iff node is a leaf in the tree, i.e. has no children.
	 */
	boolean isLeaf(TreeNode node);

	/**
	 * Tells if a node is a child of a given parentNode.
	 * <p>
	 * Time Complexity: O(1)
	 * 
	 * @param node
	 * @param parentNode
	 * @return true iff the node is a child of the given parentNode
	 */
	boolean isChildOfParent(TreeNode node, TreeNode parentNode);

	/**
	 * Returns the children of a parent node.
	 * <p>
	 * Time Complexity: O(1)
	 * 
	 * @param parentNode
	 *            [!isLeaf(parentNode)]
	 * @return the children of the given parentNode, from first to last
	 */
	Iterable<TreeNode> getChildren(TreeNode parentNode);

	/**
	 * Returns the children of a parent node, in reverse order.
	 * <p>
	 * Time Complexity: O(1)
	 * 
	 * @param parentNode
	 *            [!isLeaf(parentNode)]
	 * @return the children of given parentNode, from last to first
	 */
	Iterable<TreeNode> getChildrenReverse(TreeNode parentNode);

	/**
	 * Returns the first child of a parent node.
	 * <p>
	 * Time Complexity: O(1)
	 * 
	 * @param parentNode
	 *            [!isLeaf(parentNode)]
	 * @return the first child of the parentNode
	 */
	TreeNode getFirstChild(TreeNode parentNode);

	/**
	 * Returns the last child of a parent node.
	 * <p>
	 * 
	 * Time Complexity: O(1)
	 * 
	 * @param parentNode
	 *            [!isLeaf(parentNode)]
	 * @return the last child of the parentNode
	 */
	TreeNode getLastChild(TreeNode parentNode);
}