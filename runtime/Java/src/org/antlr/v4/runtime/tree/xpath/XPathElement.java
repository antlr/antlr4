package org.antlr.v4.runtime.tree.xpath;

import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Collection;

public abstract class XPathElement {
	protected String nodeName;
	protected boolean invert;

	/** Construct element like /ID or or ID or "/*" etc...
	 *  op is null if just node
	 */
	public XPathElement(String nodeName) {
		this.nodeName = nodeName;
	}

	/** Given tree rooted at t return all nodes matched by this path element */
	public abstract Collection<ParseTree> evaluate(ParseTree t);

	@Override
	public String toString() {
		String inv = invert ? "!" : "";
		return getClass().getSimpleName()+"["+inv+nodeName+"]";
	}
}
