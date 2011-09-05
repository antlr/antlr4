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

import static org.abego.treelayout.internal.util.Contract.checkArg;

import org.abego.treelayout.Configuration;

/**
 * Specify a {@link Configuration} through configurable parameters, or falling
 * back to some frequently used defaults.
 * 
 * @author Udo Borkowski (ub@abego.org)
 * 
 * 
 * @param <TreeNode>
 */
public class DefaultConfiguration<TreeNode> implements
		Configuration<TreeNode> {

	/**
	 * Specifies the constants to be used for this Configuration.
	 * 
	 * @param gapBetweenLevels
	 * @param gapBetweenNodes
	 * @param location
	 *            [default: {@link org.abego.treelayout.Configuration.Location#Top Top}]
	 * @param alignmentInLevel
	 *            [default: {@link org.abego.treelayout.Configuration.AlignmentInLevel#Center Center}]
	 */
	public DefaultConfiguration(double gapBetweenLevels,
			double gapBetweenNodes, Location location,
			AlignmentInLevel alignmentInLevel) {
		checkArg(gapBetweenLevels >= 0, "gapBetweenLevels must be >= 0");
		checkArg(gapBetweenNodes >= 0, "gapBetweenNodes must be >= 0");

		this.gapBetweenLevels = gapBetweenLevels;
		this.gapBetweenNodes = gapBetweenNodes;
		this.location = location;
		this.alignmentInLevel = alignmentInLevel;
	}

	/**
	 * Convenience constructor, using a default for the alignmentInLevel.
	 * <p>
	 * see
	 * {@link #DefaultConfiguration(double, double, org.abego.treelayout.Configuration.Location, org.abego.treelayout.Configuration.AlignmentInLevel)}
	 */
	public DefaultConfiguration(double gapBetweenLevels,
			double gapBetweenNodes, Location location) {
		this(gapBetweenLevels, gapBetweenNodes, location,
				AlignmentInLevel.Center);
	}

	/**
	 * Convenience constructor, using a default for the rootLocation and the
	 * alignmentInLevel.
	 * <p>
	 * see
	 * {@link #DefaultConfiguration(double, double,  org.abego.treelayout.Configuration.Location, org.abego.treelayout.Configuration.AlignmentInLevel)}
	 */
	public DefaultConfiguration(double gapBetweenLevels,
			double gapBetweenNodes) {
		this(gapBetweenLevels, gapBetweenNodes, Location.Top,
				AlignmentInLevel.Center);
	}

	// -----------------------------------------------------------------------
	// gapBetweenLevels

	private final double gapBetweenLevels;

	@Override
	public double getGapBetweenLevels(int nextLevel) {
		return gapBetweenLevels;
	}

	// -----------------------------------------------------------------------
	// gapBetweenNodes

	private final double gapBetweenNodes;

	@Override
	public double getGapBetweenNodes(TreeNode node1, TreeNode node2) {
		return gapBetweenNodes;
	}

	// -----------------------------------------------------------------------
	// location

	private final Location location;

	@Override
	public Location getRootLocation() {
		return location;
	}

	// -----------------------------------------------------------------------
	// alignmentInLevel

	private AlignmentInLevel alignmentInLevel;

	@Override
	public AlignmentInLevel getAlignmentInLevel() {
		return alignmentInLevel;
	}

}
