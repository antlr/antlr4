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
package org.antlr.v4;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;

/**
 *
 * @author Sam Harwell
 */
public class CSharpTool extends Tool {

	public CSharpTool() {
	}

	public CSharpTool(String[] args) {
		super(args);
	}

	public static void main(String[] args) {
		Tool antlr = new CSharpTool(args);
		if (args.length == 0) {
			antlr.help();
			antlr.exit(0);
		}

		try {
			antlr.processGrammarsOnCommandLine();
		} finally {
			if (antlr.log) {
				try {
					String logname = antlr.logMgr.save();
					System.out.println("wrote " + logname);
				}
				catch (IOException ioe) {
					antlr.errMgr.toolError(ErrorType.INTERNAL_ERROR, ioe);
				}
			}
		}

		if (antlr.return_dont_exit) {
			return;
		}

		if (antlr.errMgr.getNumErrors() > 0) {
			antlr.exit(1);
		}

		antlr.exit(0);
	}

	@Override
	public Writer getOutputFileWriter(Grammar g, String fileName) throws IOException {
		if (outputDirectory != null) {
			// output directory is a function of where the grammar file lives
			// for subdir/T.g4, you get subdir here.  Well, depends on -o etc...
			File outputDir = getOutputDirectory(g.fileName);
			File outputFile = new File(outputDir, fileName);
			System.out.format("Generating file '%s' for grammar '%s'%n", outputFile.getAbsolutePath(), g.fileName);
		}

		return super.getOutputFileWriter(g, fileName);
	}

}
