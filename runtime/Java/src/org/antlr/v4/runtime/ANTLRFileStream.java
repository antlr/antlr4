/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Utils;

import java.io.IOException;

/**
 * This is an {@link ANTLRInputStream} that is loaded from a file all at once
 * when you construct the object.
 *
 * @deprecated as of 4.7 Please use {@link CharStreams} interface.
 */
public class ANTLRFileStream extends ANTLRInputStream {
	@Deprecated
	protected String fileName;
	private String encoding;

	public ANTLRFileStream(String fileName) throws IOException {
		this(fileName, null);
	}

	public ANTLRFileStream(String fileName, String encoding) throws IOException {
		this.fileName = fileName;
		this.name=fileName;
		this.encoding=encoding;
		load();
	}

	@Deprecated
	public void load(String fileName, String encoding)
		throws IOException
	{
		data = Utils.readFile(fileName, encoding);
		this.n = data.length;
	}

	public void load() throws IOException
		{
			this.data = Utils.readFile(this.name, this.encoding);
			this.n = data.length;
		}
}
