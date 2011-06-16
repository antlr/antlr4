package org.antlr.v4.tool;

import org.antlr.v4.Tool;
import org.stringtemplate.v4.ST;

/** */
public class DefaultToolListener implements ANTLRToolListener {
	public Tool tool;

	public DefaultToolListener(Tool tool) { this.tool = tool; }

	public void info(String msg) {
		if (tool.errMgr.formatWantsSingleLineMessage()) {
			msg = msg.replaceAll("\n", " ");
		}
		System.out.println(msg);
	}

	public void error(ANTLRMessage msg) {
		ST msgST = tool.errMgr.getMessageTemplate(msg);
		String outputMsg = msgST.render();
		if (tool.errMgr.formatWantsSingleLineMessage()) {
			outputMsg = outputMsg.replaceAll("\n", " ");
		}
		System.err.println(outputMsg);
	}

	public void warning(ANTLRMessage msg) {
		ST msgST = tool.errMgr.getMessageTemplate(msg);
		String outputMsg = msgST.render();
		if (tool.errMgr.formatWantsSingleLineMessage()) {
			outputMsg = outputMsg.replaceAll("\n", " ");
		}
		System.err.println(outputMsg);
	}
}
