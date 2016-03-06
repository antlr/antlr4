/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
