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

package org.antlr.v4.parse;

import org.antlr.v4.Tool;
import org.antlr.v4.tool.ErrorType;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import org.antlr.v4.codegen.CodeGenerator;

/** */
public class TokenVocabParser {
	Tool tool;
	String vocabName;

	public TokenVocabParser(Tool tool, String vocabName) {
		this.tool = tool;
		this.vocabName = vocabName;
	}

	/** Load a vocab file {@code <vocabName>.tokens} and return mapping. */
	public Map<String,Integer> load() {
		Map<String,Integer> tokens = new LinkedHashMap<String,Integer>();
		int maxTokenType = -1;
		File fullFile = getImportedVocabFile();
		try {
			FileReader fr = new FileReader(fullFile);
			BufferedReader br = new BufferedReader(fr);
			StreamTokenizer tokenizer = new StreamTokenizer(br);
			tokenizer.parseNumbers();
			tokenizer.wordChars('_', '_');
			tokenizer.eolIsSignificant(true);
			tokenizer.slashSlashComments(true);
			tokenizer.slashStarComments(true);
			tokenizer.ordinaryChar('=');
			tokenizer.quoteChar('\'');
			tokenizer.whitespaceChars(' ',' ');
			tokenizer.whitespaceChars('\t','\t');
			int lineNum = 1;
			int token = tokenizer.nextToken();
			while (token != StreamTokenizer.TT_EOF) {
				String tokenID;
				if ( token == StreamTokenizer.TT_WORD ) {
					tokenID = tokenizer.sval;
				}
				else if ( token == '\'' ) {
					tokenID = "'"+tokenizer.sval+"'";
				}
				else {
					tool.errMgr.toolError(ErrorType.TOKENS_FILE_SYNTAX_ERROR,
										  vocabName + CodeGenerator.VOCAB_FILE_EXTENSION,
										  lineNum);
					while ( tokenizer.nextToken() != StreamTokenizer.TT_EOL ) {;}
					token = tokenizer.nextToken();
					continue;
				}
				token = tokenizer.nextToken();
				if ( token != '=' ) {
					tool.errMgr.toolError(ErrorType.TOKENS_FILE_SYNTAX_ERROR,
										  vocabName+CodeGenerator.VOCAB_FILE_EXTENSION,
										  lineNum);
					while ( tokenizer.nextToken() != StreamTokenizer.TT_EOL ) {;}
					token = tokenizer.nextToken();
					continue;
				}
				token = tokenizer.nextToken(); // skip '='
				if ( token != StreamTokenizer.TT_NUMBER ) {
					tool.errMgr.toolError(ErrorType.TOKENS_FILE_SYNTAX_ERROR,
										  vocabName+CodeGenerator.VOCAB_FILE_EXTENSION,
										  lineNum);
					while ( tokenizer.nextToken() != StreamTokenizer.TT_EOL ) {;}
					token = tokenizer.nextToken();
					continue;
				}
				int tokenType = (int)tokenizer.nval;
				token = tokenizer.nextToken();
				tool.log("grammar", "import "+tokenID+"="+tokenType);
				tokens.put(tokenID, tokenType);
				maxTokenType = Math.max(maxTokenType,tokenType);
				lineNum++;
				if ( token != StreamTokenizer.TT_EOL ) {
					tool.errMgr.toolError(ErrorType.TOKENS_FILE_SYNTAX_ERROR,
										  vocabName+CodeGenerator.VOCAB_FILE_EXTENSION,
										  lineNum);
					while ( tokenizer.nextToken() != StreamTokenizer.TT_EOL ) {;}
					token = tokenizer.nextToken();
					continue;
				}
				token = tokenizer.nextToken(); // skip newline
			}
			br.close();
		}
		catch (FileNotFoundException fnfe) {
			tool.errMgr.toolError(ErrorType.CANNOT_FIND_TOKENS_FILE,
								  fullFile);
		}
		catch (IOException ioe) {
			tool.errMgr.toolError(ErrorType.ERROR_READING_TOKENS_FILE,
								  fullFile,
								  ioe);
		}
		catch (Exception e) {
			tool.errMgr.toolError(ErrorType.ERROR_READING_TOKENS_FILE,
								  fullFile,
								  e);
		}
		return tokens;
	}

	/** Return a File descriptor for vocab file.  Look in library or
	 *  in -o output path.  antlr -o foo T.g U.g where U needs T.tokens
	 *  won't work unless we look in foo too. If we do not find the
	 *  file in the lib directory then must assume that the .tokens file
	 *  is going to be generated as part of this build and we have defined
	 *  .tokens files so that they ALWAYS are generated in the base output
	 *  directory, which means the current directory for the command line tool if there
	 *  was no output directory specified.
	 */
	public File getImportedVocabFile() {

		File f = new File(tool.libDirectory,
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
		//
		f = new File(tool.outputDirectory, vocabName + CodeGenerator.VOCAB_FILE_EXTENSION);
		return f;
	}
}
