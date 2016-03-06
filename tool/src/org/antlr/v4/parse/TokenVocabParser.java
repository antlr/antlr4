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

package org.antlr.v4.parse;

import org.antlr.runtime.Token;
import org.antlr.v4.Tool;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.ast.GrammarAST;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** */
public class TokenVocabParser {
	protected final Grammar g;

	public TokenVocabParser(Grammar g) {
		this.g = g;
	}

	/** Load a vocab file {@code <vocabName>.tokens} and return mapping. */
	public Map<String,Integer> load() {
		Map<String,Integer> tokens = new LinkedHashMap<String,Integer>();
		int maxTokenType = -1;
		File fullFile = getImportedVocabFile();
		FileInputStream fis = null;
		BufferedReader br = null;
		Tool tool = g.tool;
		String vocabName = g.getOptionString("tokenVocab");
		try {
			Pattern tokenDefPattern = Pattern.compile("([^\n]+?)[ \\t]*?=[ \\t]*?([0-9]+)");
			fis = new FileInputStream(fullFile);
			InputStreamReader isr;
			if (tool.grammarEncoding != null) {
				isr = new InputStreamReader(fis, tool.grammarEncoding);
			}
			else {
				isr = new InputStreamReader(fis);
			}

			br = new BufferedReader(isr);
			String tokenDef = br.readLine();
			int lineNum = 1;
			while ( tokenDef!=null ) {
				Matcher matcher = tokenDefPattern.matcher(tokenDef);
				if ( matcher.find() ) {
					String tokenID = matcher.group(1);
					String tokenTypeS = matcher.group(2);
					int tokenType;
					try {
						tokenType = Integer.valueOf(tokenTypeS);
					}
					catch (NumberFormatException nfe) {
						tool.errMgr.toolError(ErrorType.TOKENS_FILE_SYNTAX_ERROR,
											  vocabName + CodeGenerator.VOCAB_FILE_EXTENSION,
											  " bad token type: "+tokenTypeS,
											  lineNum);
						tokenType = Token.INVALID_TOKEN_TYPE;
					}
					tool.log("grammar", "import "+tokenID+"="+tokenType);
					tokens.put(tokenID, tokenType);
					maxTokenType = Math.max(maxTokenType,tokenType);
					lineNum++;
				}
				else {
					if ( tokenDef.length()>0 ) { // ignore blank lines
						tool.errMgr.toolError(ErrorType.TOKENS_FILE_SYNTAX_ERROR,
											  vocabName + CodeGenerator.VOCAB_FILE_EXTENSION,
											  " bad token def: " + tokenDef,
											  lineNum);
					}
				}
				tokenDef = br.readLine();
			}
		}
		catch (FileNotFoundException fnfe) {
			GrammarAST inTree = g.ast.getOptionAST("tokenVocab");
			String inTreeValue = inTree.getToken().getText();
			if ( vocabName.equals(inTreeValue) ) {
				tool.errMgr.grammarError(ErrorType.CANNOT_FIND_TOKENS_FILE_REFD_IN_GRAMMAR,
										 g.fileName,
										 inTree.getToken(),
										 fullFile);
			}
			else { // must be from -D option on cmd-line not token in tree
				tool.errMgr.toolError(ErrorType.CANNOT_FIND_TOKENS_FILE_GIVEN_ON_CMDLINE,
									  fullFile,
									  g.name);
			}
		}
		catch (Exception e) {
			tool.errMgr.toolError(ErrorType.ERROR_READING_TOKENS_FILE,
								  e,
								  fullFile,
								  e.getMessage());
		}
		finally {
			try {
				if ( br!=null ) br.close();
			}
			catch (IOException ioe) {
				tool.errMgr.toolError(ErrorType.ERROR_READING_TOKENS_FILE,
									  ioe,
									  fullFile,
									  ioe.getMessage());
			}
		}
		return tokens;
	}

	/** Return a File descriptor for vocab file.  Look in library or
	 *  in -o output path.  antlr -o foo T.g4 U.g4 where U needs T.tokens
	 *  won't work unless we look in foo too. If we do not find the
	 *  file in the lib directory then must assume that the .tokens file
	 *  is going to be generated as part of this build and we have defined
	 *  .tokens files so that they ALWAYS are generated in the base output
	 *  directory, which means the current directory for the command line tool if there
	 *  was no output directory specified.
	 */
	public File getImportedVocabFile() {
		String vocabName = g.getOptionString("tokenVocab");
		File f = new File(g.tool.libDirectory,
						  File.separator +
						  vocabName +
						  CodeGenerator.VOCAB_FILE_EXTENSION);
		if (f.exists()) {
			return f;
		}

		// We did not find the vocab file in the lib directory, so we need
		// to look for it in the output directory which is where .tokens
		// files are generated (in the base, not relative to the input
		// location.)
		f = new File(g.tool.outputDirectory, vocabName + CodeGenerator.VOCAB_FILE_EXTENSION);
		return f;
	}
}
