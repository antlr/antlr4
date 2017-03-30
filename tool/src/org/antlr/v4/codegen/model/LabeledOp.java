/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.model.decl.Decl;

import java.util.List;

/** All the rule elements we can label like tokens, rules, sets, wildcard. */
public interface LabeledOp {
	public List<Decl> getLabels();
}
