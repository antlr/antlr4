package org.antlr.v4.runtime.tree.xpath;

public class XPathElement {
	public XPathOperator op;
	public String nodeName;

	/** Construct element like /ID or //ID or ID or "/*" etc...
	 *  op is null if just node
	 */
	public XPathElement(XPathOperator op, String nodeName) {
		this.nodeName = nodeName;
		this.op = op;
	}
}
