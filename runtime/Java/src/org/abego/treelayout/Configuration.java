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
 * Used to configure the tree layout algorithm.
 * 
 * @author Udo Borkowski (ub@abego.org)
 * 
 * @param <TreeNode>
 */
public interface Configuration<TreeNode> {

	// ------------------------------------------------------------------------
	// rootLocation

	/**
	 * Identifies the sides of a rectangle (top, left, ...)
	 */
	public enum Location {
		Top, Left, Bottom, Right
	}

	// ------------------------------------------------------------------------
	// alignmentInLevel

	/**
	 * Returns the position of the root node in the diagram.
	 * <p>
	 * By default the root of the tree is located at the top of the diagram.
	 * However one may also put it at the left, right or bottom of the diagram.
	 * <p>
	 * <table border="1">
	 * <tr>
	 * <th>Top (Default)</th>
	 * <th>Left</th>
	 * <th>Right</th>
	 * <th>Bottom</th>
	 * </tr>
	 * <tr>
	 * <td style="padding:10px;"><img src="doc-files/TreeGraphView-Top.png"></td>
	 * <td style="padding:10px;"><img src="doc-files/TreeGraphView-Left.png"></td>
	 * <td style="padding:10px;"><img src="doc-files/TreeGraphView-Right.png"></td>
	 * <td style="padding:10px;"><img src="doc-files/TreeGraphView-Bottom.png"></td>
	 * </tr>
	 * </table>
	 * 
	 * @return the position of the root node in the diagram
	 */
	Location getRootLocation();

	/**
	 * Possible alignments of a node within a level (centered, towards or away
	 * from root)
	 */
	public enum AlignmentInLevel {
		Center, TowardsRoot, AwayFromRoot
	}

	/**
	 * Returns the alignment of "smaller" nodes within a level.
	 * <p>
	 * By default all nodes of one level are centered in the level. However one
	 * may also align them "towards the root" or "away from the root". When the
	 * root is located at the top this means the nodes are aligned "to the top
	 * of the level" or "to the bottom of the level".
	 * <p>
	 * <table border="1">
	 * <tr>
	 * <th>Center (Default)</th>
	 * <th>TowardsRoot ("top of level")</th>
	 * <th>AwayFromRoot ("bottom of level")</th>
	 * </tr>
	 * <tr>
	 * <td style="padding:10px;"><img src="doc-files/TreeGraphView-Center.png"></td>
	 * <td style="padding:10px;"><img
	 * src="doc-files/TreeGraphView-TowardsRoot.png"></td>
	 * <td style="padding:10px;"><img
	 * src="doc-files/TreeGraphView-AwayFromRoot.png"></td>
	 * </tr>
	 * </table>
	 * <p>
	 * Alignment in level when root is at the left:
	 * </p>
	 * <table border="1">
	 * <tr>
	 * <th>Center (Default)</th>
	 * <th>TowardsRoot ("left of level")</th>
	 * <th>AwayFromRoot<br>
	 * ("right of level")</th>
	 * </tr>
	 * <tr>
	 * <td style="padding:10px;"><img
	 * src="doc-files/TreeGraphView-Center-RootLeft.png"></td>
	 * <td style="padding:10px;"><img
	 * src="doc-files/TreeGraphView-TowardsRoot-RootLeft.png"></td>
	 * <td style="padding:10px;"><img
	 * src="doc-files/TreeGraphView-AwayFromRoot-RootLeft.png"></td>
	 * </tr>
	 * </table>
	 * 
	 * <p>
	 * Of cause the alignment also works when the root is at the bottom or at
	 * the right side.
	 * </p>
	 * 
	 * @return the alignment of "smaller" nodes within a level
	 */
	AlignmentInLevel getAlignmentInLevel();

	// ------------------------------------------------------------------------
	// gapBetweenLevels/Nodes

	/**
	 * Returns the size of the gap between subsequent levels.
	 * <p>
	 * <img src="doc-files/gapBetweenLevels.png">
	 * 
	 * @param nextLevel
	 *            [nextLevel > 0]
	 * 
	 * @return the size of the gap between level (nextLevel-1) and nextLevel
	 *         [result >= 0]
	 */
	double getGapBetweenLevels(int nextLevel);

	/**
	 * Returns the size of the minimal gap of nodes within a level.
	 * <p>
	 * In the layout there will be a gap of at least the returned size between
	 * both given nodes.
	 * <p>
	 * <img src="doc-files/gapBetweenNodes.png">
	 * <p>
	 * node1 and node2 are at the same level and are placed next to each other.
	 * 
	 * @param node1
	 * @param node2
	 * @return the minimal size of the gap between node1 and node2 [result >= 0]
	 */
	double getGapBetweenNodes(TreeNode node1, TreeNode node2);
}
