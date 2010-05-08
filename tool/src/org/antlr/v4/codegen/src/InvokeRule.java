package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.misc.IntervalSet;

import java.util.List;

/** */
public class InvokeRule extends SrcOp {
	public String name;
	public String label;
	public List<String> args;
	public IntervalSet[] follow;

	public InvokeRule(CodeGenerator gen, String name, String argAction, IntervalSet[] follow) {
		this.gen = gen;
		// split and translate argAction
		// compute follow
	}

	public InvokeRule(CodeGenerator gen, String name, IntervalSet[] follow) {
		this.gen = gen;
		// split and translate argAction
		// compute follow
	}
}
