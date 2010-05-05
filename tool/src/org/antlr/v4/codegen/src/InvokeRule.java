package org.antlr.v4.codegen.src;

import org.antlr.v4.misc.IntervalSet;

import java.util.List;

/** */
public class InvokeRule extends SrcOp {
	public String name;
	public String label;
	public List<String> args;
	public IntervalSet[] follow;

	public InvokeRule(String name, String argAction, IntervalSet[] follow) {
		// split and translate argAction
		// compute follow
	}

	public InvokeRule(String name, IntervalSet[] follow) {
		// split and translate argAction
		// compute follow
	}
}
