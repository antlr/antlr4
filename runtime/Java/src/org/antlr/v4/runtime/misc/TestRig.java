/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
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

package org.antlr.v4.runtime.misc;

import org.antlr.v4.runtime.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/** Run a lexer/parser combo, optionally printing tree string or generating
 *  postscript file. Optionally taking input file.
 *
 *  $ java org.antlr.v4.runtime.misc.TestRig GrammarName startRuleName [-tree | -ps file.ps] [input-filename]
 */
public class TestRig {
	public static void main(String[] args) throws Exception {
		String grammarName;
		String startRuleName;
		String inputFile = null;
		boolean printTree = false;
		String psFile = null;
		if ( args.length < 2 ) {
			System.err.println("java org.antlr.v4.runtime.misc.TestRig GrammarName startRuleName [-tree | -ps file.ps] [input-filename]");
			return;
		}
		int i=0;
		grammarName = args[i];
		i++;
		startRuleName = args[i];
		i++;
		while ( i<args.length ) {
			String arg = args[i];
			i++;
			if ( arg.charAt(0)!='-' ) { // input file name
				inputFile = arg;
				continue;
			}
			if ( arg.equals("-tree") ) {
				printTree = true;
			}
			else if ( arg.equals("-ps") ) {
				if ( (i+1)>=args.length ) {
					System.err.println("missing filename on -ps");
					return;
				}
				i++;
				psFile = args[i];
			}
		}
		exec(grammarName, startRuleName, inputFile, printTree, psFile);
	}

	public static void exec(String grammarName,
							String startRuleName,
							String inputFile,
							boolean printTree,
							String psFile)
		throws Exception
	{
//		System.out.println("exec "+grammarName+"."+startRuleName);
		String lexerName = grammarName+"Lexer";
		String parserName = grammarName+"Parser";
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Class lexerClass = cl.loadClass(lexerName);
		if ( lexerClass==null ) {
			System.err.println("Can't load "+lexerName);
		}
		Class parserClass = cl.loadClass(parserName);
		if ( parserClass==null ) {
			System.err.println("Can't load "+parserName);
		}

		InputStream is = System.in;
		if ( inputFile!=null ) {
			is = new FileInputStream(inputFile);
		}

		ANTLRInputStream input = new ANTLRInputStream(is);

		Constructor<Lexer> lexerCtor = lexerClass.getConstructor(CharStream.class);
		Lexer lexer = lexerCtor.newInstance(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		Constructor<Parser> parserCtor = parserClass.getConstructor(TokenStream.class);
		Parser parser = parserCtor.newInstance(tokens);
		parser.setBuildParseTree(true);


		Method startRule = parserClass.getMethod(startRuleName, (Class[])null);
		ParserRuleContext<Token> tree = (ParserRuleContext<Token>)startRule.invoke(parser, (Object[])null);

		if ( printTree ) {
			System.out.println(tree.toStringTree(parser));
		}
		if ( psFile!=null ) {
			tree.save(parser, psFile); // Generate postscript
		}
	}
}
