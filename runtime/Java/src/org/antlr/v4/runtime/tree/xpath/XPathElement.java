package org.antlr.v4.runtime.tree.xpath;

public abstract class XPathElement {
	public String nodeName;

	/** Construct element like /ID or or ID or "/*" etc...
	 *  op is null if just node
	 */
	public XPathElement(String nodeName) {
		this.nodeName = nodeName;
	}
}
