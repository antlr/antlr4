/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.codegen;

import org.antlr.v4.codegen.model.*;
import org.antlr.v4.codegen.model.decl.CodeBlock;
import org.antlr.v4.tool.*;

import java.util.*;

/** Create output objects for elements *within* rule functions except
 *  buildOutputModel() which builds outer/root model object and any
 *  objects such as RuleFunction that surround elements in rule
 *  functions.
 */
public abstract class DefaultOutputModelFactory extends BlankOutputModelFactory {
	// Interface to outside world
	public Grammar g;
	public CodeGenerator gen;
	public OutputModelController controller;

	// Context ptrs
	public OutputModelObject root; // normally ParserFile, LexerFile, ...
	public Stack<RuleFunction> currentRule = new Stack<RuleFunction>();
	public Alternative currentAlt;
	public CodeBlock currentBlock;

	protected DefaultOutputModelFactory(CodeGenerator gen) {
		this.gen = gen;
		this.g = gen.g;
	}

	public Grammar getGrammar() { return g; }

	public CodeGenerator getGenerator() { return gen; }

	public OutputModelObject getRoot() { return root; }

	public void setRoot(OutputModelObject root) { this.root = root;	}

	public RuleFunction getCurrentRuleFunction() {
		if ( currentRule.size()>0 )	return currentRule.peek();
		return null;
	}

	public void pushCurrentRule(RuleFunction r) { currentRule.push(r); }

	public RuleFunction popCurrentRule() {
		if ( currentRule.size()>0 ) return currentRule.pop();
		return null;
	}

	public Alternative getCurrentAlt() { return currentAlt; }

	public void setCurrentAlt(Alternative currentAlt) { this.currentAlt = currentAlt; }

	public void setController(OutputModelController controller) {
		this.controller = controller;
	}

	public void setCurrentBlock(CodeBlock blk) {
		currentBlock = blk;
	}

	public CodeBlock getCurrentBlock() {
		return currentBlock;
	}

	// MISC

	public static List<SrcOp> list(Object... values) {
		List<SrcOp> x = new ArrayList<SrcOp>(values.length);
		for (Object v : values) {
			if ( v!=null ) {
				if ( v instanceof List<?> ) x.addAll((List) v);
				else x.add((SrcOp)v);
			}
		}
		return x;
	}
}

