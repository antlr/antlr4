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

/**
 Efficiently create compact, highly customizable tree layouts.


 <h2>Introduction</h2>

 The TreeLayout creates tree layouts for arbitrary trees. It is not restricted 
 to a specific output or format, but can be used for any kind of two dimensional
 diagram. Examples are Swing based components, SVG files, and many more. This is
 possible because TreeLayout separates the layout of a tree from the actual 
 rendering.<p>

 To use the TreeLayout you mainly need to supply an instance of the  
 {@link org.abego.treelayout.TreeLayout TreeLayout} class with the nodes of the tree (including "children" 
 links), together with the "size" of each node. In addition you can configure 
 the layout by specifying parameters like "gap between levels" etc..<p>

 Based on this information TreeLayout creates a compact, nice looking layout. 
 The layout has the following properties [2]:

 <ol>
 <li>The layout displays the hierarchical structure of the tree, i.e. the 
 y-coordinate of a node is given by its level. (*)</li>
 <li>The edges do not cross each other and nodes on the same level have a 
 minimal horizontal distance.</li>
 <li>The drawing of a subtree does not depend on its position in the tree, i.e. 
 isomorphic subtrees are drawn identically up to translation.</li>
 <li>The order of the children of a node is displayed in the drawing.</li>
 <li>The algorithm works symmetrically, i.e. the drawing of the reflection of a 
 tree is the reflected drawing of the original tree.</li>
 </ol>
 <i> (*) When the root is at the left or right (see "Root Location") the 
 x-coordinate of a node is given by its level</i><p>

 Here an example tree layout:<p>
 <img src="doc-files/TreeGraphView-Top.png">



 <h2>Usage</h2>

 To use the TreeLayout you will create a {@link org.abego.treelayout.TreeLayout TreeLayout} instance with:
 <ul>
 <li>a tree, accessible through the {@link org.abego.treelayout.TreeForTreeLayout TreeForTreeLayout} interface, </li>
 <li>a {@link org.abego.treelayout.NodeExtentProvider NodeExtentProvider}, and</li>
 <li>a {@link org.abego.treelayout.Configuration Configuration}.</li>
 </ul>
 Using these objects the TreeLayout will then calculate the layout and provide 
 the result through the method {@link org.abego.treelayout.TreeLayout#getNodeBounds() getNodeBounds}.


 <h3>TreeForTreeLayout</h3>

 The TreeLayout works on any kind of tree and uses the {@link org.abego.treelayout.TreeForTreeLayout TreeForTreeLayout} 
 interface to access such a tree.<p>

 To use the TreeLayout you therefore need to provide it with a TreeForTreeLayout 
 implementation. In most situations you will not need to deal with all details 
 of that interface, but create an implementation by extending 
 {@link org.abego.treelayout.util.AbstractTreeForTreeLayout AbstractTreeForTreeLayout}, or even use the class 
 {@link org.abego.treelayout.util.DefaultTreeForTreeLayout DefaultTreeForTreeLayout} directly.


 <h4>Example: Extending AbstractTreeForTreeLayout</h4>

 Assume you have a tree consisting of nodes of type StringTreeNode:<p>
 <img src="doc-files/StringTreeNodeUML.png"><p>

 As StringTreeNode provides the children in a list and you can get the parent for
 each node you can extend {@link org.abego.treelayout.util.AbstractTreeForTreeLayout AbstractTreeForTreeLayout} to create your 
 TreeForTreeLayout implementation. You only need to implement two methods and
 the constructor:<p>
 <pre>
 public class StringTreeAsTreeForTreeLayout extends
 &nbsp;&nbsp;&nbsp;&nbsp;AbstractTreeForTreeLayout&lt;StringTreeNode&gt; {

 &nbsp;&nbsp;&nbsp;&nbsp;public StringTreeAsTreeForTreeLayout(StringTreeNode root) {
 &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;super(root);
 &nbsp;&nbsp;&nbsp;&nbsp;}

 &nbsp;&nbsp;&nbsp;&nbsp;public StringTreeNode getParent(StringTreeNode node) {
 &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;return node.getParent();
 &nbsp;&nbsp;&nbsp;&nbsp;}

 &nbsp;&nbsp;&nbsp;&nbsp;public List&lt;StringTreeNode&gt; getChildrenList(StringTreeNode parentNode) {
 &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;return parentNode.getChildren();
 &nbsp;&nbsp;&nbsp;&nbsp;}
 } 
 </pre>
 <i>(Make sure to check out the performance constraints of {@link org.abego.treelayout.util.AbstractTreeForTreeLayout#getChildrenList(Object) getChildrenList} and  {@link org.abego.treelayout.util.AbstractTreeForTreeLayout#getParent(Object) getParent}.)</i> 



 <h4>Example: Using the DefaultTreeForTreeLayout</h4>

 Assume you want to create a tree with TextInBox items as nodes:<p>
 <img src="doc-files/TextInBoxUML.png"><p>

 As you have no own tree implementation yet you may as well use 
 {@link org.abego.treelayout.util.DefaultTreeForTreeLayout DefaultTreeForTreeLayout} to create the tree:<p>
 <pre>
 TextInBox root = new TextInBox("root", 40, 20);
 TextInBox n1 = new TextInBox("n1", 30, 20);
 TextInBox n1_1 = new TextInBox("n1.1\n(first node)", 80, 36);
 TextInBox n1_2 = new TextInBox("n1.2", 40, 20);
 TextInBox n1_3 = new TextInBox("n1.3\n(last node)", 80, 36);
 TextInBox n2 = new TextInBox("n2", 30, 20);
 TextInBox n2_1 = new TextInBox("n2", 30, 20);

 DefaultTreeForTreeLayout&lt;TextInBox&gt; tree = 
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new DefaultTreeForTreeLayout&lt;TextInBox&gt;(root);
 tree.addChild(root, n1);
 tree.addChild(n1, n1_1);
 tree.addChild(n1, n1_2);
 tree.addChild(n1, n1_3);
 tree.addChild(root, n2);
 tree.addChild(n2, n2_1);
 </pre><p>
 This will create a tree like this:<p>
 <img src="doc-files/svgdemo.png">


 <h3>NodeExtentProvider</h3>

 TreeLayout also needs to know the extent (width and height) of each node in the 
 tree. This information is provided through the {@link org.abego.treelayout.NodeExtentProvider NodeExtentProvider}.<p>

 If all nodes have the same size you can use a {@link org.abego.treelayout.util.FixedNodeExtentProvider FixedNodeExtentProvider} 
 instance with the proper width and height.<p>

 In general you will create your own NodeExtentProvider implementation.
 <h4>Example</h4>

 Assume you want to create a tree with TextInBox items as nodes:<p>
 <img src="doc-files/TextInBoxUML.png"><p>

 Here each node contains its width and height. So your NodeExtentProvider may 
 look like this:<p><pre>
 public class TextInBoxNodeExtentProvider implements
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;NodeExtentProvider&lt;TextInBox&gt; {

 &nbsp;&nbsp;&nbsp;&nbsp;@Override
 &nbsp;&nbsp;&nbsp;&nbsp;public double getWidth(TextInBox treeNode) {
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return treeNode.width;
 &nbsp;&nbsp;&nbsp;&nbsp;}

 &nbsp;&nbsp;&nbsp;&nbsp;@Override
 &nbsp;&nbsp;&nbsp;&nbsp;public double getHeight(TextInBox treeNode) {
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return treeNode.height;
 &nbsp;&nbsp;&nbsp;&nbsp;}
 }</pre>

 
 
 <h3>Configuration</h3>
 
 You can use a {@link org.abego.treelayout.Configuration Configuration} to customize various aspects of the 
 TreeLayout:
 <ul>
 <li>the gap between levels,</li>
 <li>the minimal gap between nodes,</li>
 <li>the position of the root node,</li>
 <li>the alignment of smaller nodes within a level.</li>
 </ul>
 Most of the times using the {@link org.abego.treelayout.util.DefaultConfiguration DefaultConfiguration} class will be sufficient.

 <h4>Root Position</h4>

 By default the root of the tree is located at the top of the diagram. However 
 one may also put it at the left, right or bottom of the diagram. 
 <p>
 <table border="1">
 <tr>
 <th>Top (Default)</th>
 <th>Left</th>
 <th>Right</th>
 <th>Bottom</th>
 </tr>
 <tr>
 <td style="padding:10px;"><img src="doc-files/TreeGraphView-Top.png"></td>
 <td style="padding:10px;"><img src="doc-files/TreeGraphView-Left.png"></td>
 <td style="padding:10px;"><img src="doc-files/TreeGraphView-Right.png"></td>
 <td style="padding:10px;"><img src="doc-files/TreeGraphView-Bottom.png"></td>
 </tr>
 </table>
 <p>
 See {@link org.abego.treelayout.Configuration#getRootLocation() getRootLocation}.




 <h4>Alignment in Level</h4>

 By default all nodes of one level are centered in the level. However 
 one may also align them "towards the root" or "away from the root". When the 
 root is located at the top this means the nodes are aligned "to the top of the 
 level" or "to the bottom of the level".
 <p>
 <table border="1">
 <tr>
 <th>Center (Default)</th>
 <th>TowardsRoot ("top of level")</th>
 <th>AwayFromRoot ("bottom of level")</th>
 </tr>
 <tr>
 <td style="padding:10px;"><img src="doc-files/TreeGraphView-Center.png"></td>
 <td style="padding:10px;"><img src="doc-files/TreeGraphView-TowardsRoot.png"></td>
 <td style="padding:10px;"><img src="doc-files/TreeGraphView-AwayFromRoot.png"></td>
 </tr>
 </table>
 <p>Alignment in level when root is at the left:</p>
 <table border="1">
 <tr>
 <th>Center (Default)</th>
 <th>TowardsRoot ("left of level")</th>
 <th>AwayFromRoot<br>("right of level")</th>
 </tr>
 <tr>
 <td style="padding:10px;"><img src="doc-files/TreeGraphView-Center-RootLeft.png"></td>
 <td style="padding:10px;"><img src="doc-files/TreeGraphView-TowardsRoot-RootLeft.png"></td>
 <td style="padding:10px;"><img src="doc-files/TreeGraphView-AwayFromRoot-RootLeft.png"></td>
 </tr>
 </table>

 <p>Of cause the alignment also works when the root is at the bottom or at the right side.</p>
 See {@link org.abego.treelayout.Configuration#getAlignmentInLevel() getAlignmentInLevel}.



 <h4 style="page-break-before:always">Gap between Levels and Nodes</h4>

 The gap between subsequent levels and the minimal gap between nodes can be configured.
 <p>
 <img src="doc-files/gapsAndLevels.png">
 <p>
 See {@link org.abego.treelayout.Configuration#getGapBetweenLevels(int) getGapBetweenLevels} and 
 {@link org.abego.treelayout.Configuration#getGapBetweenNodes(Object, Object) getGapBetweenNodes}.


 <h2>Examples</h2>
 In the "demo" package you will find examples using the TreeLayout.
 <ul>
 <li>SVGDemo - Demonstrates how to use the TreeLayout to create a tree diagram with SVG (Scalable Vector Graphic)</li>
 <li>SwingDemo - Demonstrates how to use the TreeLayout to render a tree in a Swing application</li>
 </ul>


 <h2 style="page-break-before:always">Performance</h2>

 Based on Walker's algorithm [1] with enhancements suggested by Buchheim, 
 J&uuml;nger, and Leipert [2] the software builds tree layouts in linear time. 
 I.e. even trees with many nodes are built fast. Other than with the 
 Reingold–Tilford algorithm [3] one is not limited to binary trees.

 <p>
 The following figure show the results running the TreeLayout algorithm on a 
 MacBook Pro 2.4 GHz Intel Core 2 Duo (2 GB Memory (-Xmx2000m)). The variously 
 sized trees were created randomly. 
 <p>
 <img src="doc-files/performance.png">
 <p>
 The picture illustrates the linear time behavior of the algorithm and shows 
 the applicability also for large number of nodes. In this setting it takes 
 approx. 5 &micro;s to place one node.





 <h2>License</h2>

 TreeLayout is distributed under a BSD license of 
 <a href="http://www.abego-software.de">abego Software</a>. 
 (<a href="doc-files/LICENSE.TXT">License text</a>)




 <h2>Sponsor</h2>

 The development of TreeLayout was generously sponsored by Terence Parr 
 "The ANTLR Guy" (parrt at cs dot usfca dot edu).





 <h2>References</h2>

 [1] Walker JQ II. A node-positioning algorithm for general trees. 
 <i>Software—Practice and Experience</i> 1990; <b>20</b>(7):685–705.
 <p>
 [2] Buchheim C, J&uuml;nger M, Leipert S. Drawing rooted trees in linear time.
 <i>Software—Practice and Experience</i> 2006; <b>36</b>(6):651–665
 <p>
 [3] Reingold EM, Tilford JS. Tidier drawings of trees. 
 <i>IEEE Transactions on Software Engineering</i> 1981; <b>7</b>(2):223–228.
 <p>


 @author Udo Borkowski (ub@abego.org)

 */
package org.abego.treelayout;


