package org.antlr.v4.runtime;

import org.stringtemplate.v4.ST;

public class TreeParserRuleContext extends ParserRuleContext {
	// TODO: heh, these are duplicates of the fields above!
	public Object start, stop;
	public Object tree;
	public ST st;

	/** Set during parsing to identify which rule parser is in. */
	public int ruleIndex;

	/** Set during parsing to identify which alt of rule parser is in. */
	public int altNum;

	public TreeParserRuleContext() {
		super();
	}

	public TreeParserRuleContext(RuleContext parent, int stateNumber) {
		super(parent, stateNumber);
	}
}
