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

import static org.abego.treelayout.internal.util.Contract.*;

import org.abego.treelayout.NodeExtentProvider;

/**
 * A {@link NodeExtentProvider} returning the same width and height for each
 * node.
 * 
 * @author Udo Borkowski (ub@abego.org)
 * 
 * @param <T>
 */
public class FixedNodeExtentProvider<T> implements NodeExtentProvider<T> {

	private final double width;
	private final double height;

	/**
	 * Specifies the constants to be used as the width and height of the nodes.
	 * 
	 * @param width
	 *            [default=0]
	 * 
	 * @param height
	 *            [default=0]
	 */
	public FixedNodeExtentProvider(double width, double height) {
		checkArg(width >= 0, "width must be >= 0");
		checkArg(height >= 0, "height must be >= 0");

		this.width = width;
		this.height = height;
	}

	/**
	 * see {@link #FixedNodeExtentProvider(double, double)}
	 */
	public FixedNodeExtentProvider() {
		this(0, 0);
	}

	@Override
	public double getWidth(T treeNode) {
		return width;
	}

	@Override
	public double getHeight(T treeNode) {
		return height;
	}

}
