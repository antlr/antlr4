/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.analysis;

import org.antlr.v4.tool.ast.AltAST;

public class LeftRecursiveRuleAltInfo {
	public int altNum; // original alt index (from 1)
	public String leftRecursiveRuleRefLabel;
	public String altLabel;
	public final boolean isListLabel;
	public String altText;
	public AltAST altAST; // transformed ALT
	public AltAST originalAltAST;
	public int nextPrec;

	public LeftRecursiveRuleAltInfo(int altNum, String altText) {
		this(altNum, altText, null, null, false, null);
	}

	public LeftRecursiveRuleAltInfo(int altNum, String altText,
									String leftRecursiveRuleRefLabel,
									String altLabel,
									boolean isListLabel,
									AltAST originalAltAST)
	{
		this.altNum = altNum;
		this.altText = altText;
		this.leftRecursiveRuleRefLabel = leftRecursiveRuleRefLabel;
		this.altLabel = altLabel;
		this.isListLabel = isListLabel;
		this.originalAltAST = originalAltAST;
	}
}
