/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime;

import org.antlr.v4.Tool;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.tool.ANTLRMessage;
import org.antlr.v4.tool.ANTLRToolListener;
import org.antlr.v4.tool.ToolMessage;
import org.stringtemplate.v4.ST;

import java.util.ArrayList;
import java.util.List;

public class ErrorQueue implements ANTLRToolListener {
	public final Tool tool;
	public final List<String> infos = new ArrayList<String>();
	public final List<ANTLRMessage> errors = new ArrayList<ANTLRMessage>();
	public final List<ANTLRMessage> warnings = new ArrayList<ANTLRMessage>();
	public final List<ANTLRMessage> all = new ArrayList<ANTLRMessage>();

	public ErrorQueue() {
		this(null);
	}

	public ErrorQueue(Tool tool) {
		this.tool = tool;
	}

	@Override
	public void info(String msg) {
		infos.add(msg);
	}

	@Override
	public void error(ANTLRMessage msg) {
		errors.add(msg);
        all.add(msg);
	}

	@Override
	public void warning(ANTLRMessage msg) {
		warnings.add(msg);
        all.add(msg);
	}

	public void error(ToolMessage msg) {
		errors.add(msg);
		all.add(msg);
	}

	public int size() {
		return all.size() + infos.size();
	}

	@Override
	public String toString() {
		return toString(false);
	}

	public String toString(boolean rendered) {
		if (!rendered) {
			return Utils.join(all.iterator(), "\n");
		}

		if (tool == null) {
			throw new IllegalStateException(String.format("No %s instance is available.", Tool.class.getName()));
		}

		StringBuilder buf = new StringBuilder();
		for (ANTLRMessage m : all) {
			ST st = tool.errMgr.getMessageTemplate(m);
			buf.append(st.render());
			buf.append("\n");
		}

		return buf.toString();
	}

}

