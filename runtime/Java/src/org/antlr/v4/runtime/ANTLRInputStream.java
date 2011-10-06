/*
 [The "BSD license"]
 Copyright (c) 2005-2009 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
     derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.antlr.v4.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/** A kind of ReaderStream that pulls from an InputStream.
 *  Useful for reading from stdin and specifying file encodings etc...
  */
public class ANTLRInputStream extends ANTLRReaderStream {
	public ANTLRInputStream() {
	}

	public ANTLRInputStream(InputStream input) throws IOException {
		this(input, null);
	}

	public ANTLRInputStream(InputStream input, int size) throws IOException {
		this(input, size, null);
	}

	public ANTLRInputStream(InputStream input, String encoding) throws IOException {
		this(input, INITIAL_BUFFER_SIZE, encoding);
	}

	public ANTLRInputStream(InputStream input, int size, String encoding) throws IOException {
		this(input, size, READ_BUFFER_SIZE, encoding);
	}

	public ANTLRInputStream(InputStream input,
							int size,
							int readBufferSize,
							String encoding)
		throws IOException
	{
		InputStreamReader isr;
		if ( encoding!=null ) {
			isr = new InputStreamReader(input, encoding);
		}
		else {
			isr = new InputStreamReader(input);
		}
		load(isr, size, readBufferSize);
	}
}
