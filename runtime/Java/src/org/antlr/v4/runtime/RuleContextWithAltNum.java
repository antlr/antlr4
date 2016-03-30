package org.antlr.v4.runtime;

/** A handy class for use with
 *
 *  options {contextSuperClass=org.antlr.v4.runtime.RuleContextWithAltNum;}
 *
 *  that provides a backing field / impl for the outer alternative number
 *  matched for an internal parse tree node.
 *
 *  I'm only putting into Java runtime as I'm certain I'm the only one that
 *  will really every use this.
 */
public class RuleContextWithAltNum extends ParserRuleContext {
	public int altNum;
	public RuleContextWithAltNum(ParserRuleContext parent, int invokingStateNumber) {
		super(parent, invokingStateNumber);
	}
	@Override public int getAltNumber() { return altNum; }
	@Override public void setAltNumber(int altNum) { this.altNum = altNum; }
}
