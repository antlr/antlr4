/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.antlr.v4.test.runtime.legacy.java;

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

