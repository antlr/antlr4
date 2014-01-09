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

package org.antlr.v4.test;

import org.antlr.v4.tool.ErrorType;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestToolOptions extends BaseTest {
	@Test public void testOptionIgnoreCaseParser() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"a : 'A' ;\n",
			"error(" + ErrorType.LEXER_OPTION_IGNORECASE.code + "):  option -ignorecase is only valid for Lexer grammars\n",
		};
		super.testErrors(pair, "-ignorecase");
	}
	@Test public void testOptionIgnoreCaseLexer() {
		String[] pair = new String[] {
			"lexer grammar A;\n" +
			"A : 'A' ;\n",
			"", /* no errors */
		};
		super.testErrors(pair, "-ignorecase");
	}
	 
	/*
	 * testOptionHelp disabled for now. The testError expect output to be writtent o System.err
	 */
	public void testOptionHelp() {
		String[] pair = new String[] {
			"lexer grammar A;\n" +
			"A : 'A' ;\n",
			"ANTLR Parser Generator Version 4.x\n"+
			" -o ___              specify output directory where all output is generated\n"+
			" -lib ___            specify location of grammars, tokens files\n"+
			" -atn                generate rule augmented transition network diagrams\n"+
			" -encoding ___       specify grammar file encoding; e.g., euc-jp\n"+
			" -message-format ___ specify output style for messages in antlr, gnu, vs2005\n"+
			" -long-messages      show exception details when available for errors and warnings\n"+
			" -listener           generate parse tree listener (default)\n"+
			" -no-listener        don't generate parse tree listener\n"+
			" -visitor            generate parse tree visitor\n"+
			" -no-visitor         don't generate parse tree visitor (default)\n"+
			" -package ___        specify a package/namespace for the generated code\n"+
			" -depend             generate file dependencies\n"+
			" -D<option>=value    set/override a grammar-level option\n"+
			" -Werror             treat warnings as errors\n"+
			" -XdbgST             launch StringTemplate visualizer on generated code\n"+
			" -XdbgSTWait         wait for STViz to close before continuing\n"+
			" -Xforce-atn         use the ATN simulator for all predictions\n"+
			" -Xlog               dump lots of logging info to antlr-timestamp.log\n"+
			" -ignorecase         the generated lexer will be case insensitive\n"+
			" -help               show tool help\n",
		};
		super.testErrors(pair, "-help");
	}
		
}
