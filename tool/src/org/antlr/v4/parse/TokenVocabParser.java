package org.antlr.v4.parse;

import org.antlr.codegen.CodeGenerator;
import org.antlr.misc.Utils;
import org.antlr.tool.ErrorManager;
import org.antlr.v4.Tool;

import java.io.*;
import java.util.*;

/** */
public class TokenVocabParser {
	Tool tool;
	String vocabName;

	public TokenVocabParser(Tool tool, String vocabName) {
		this.tool = tool;
		this.vocabName = vocabName;
	}

	/** Load a vocab file <vocabName>.tokens and return mapping. */
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
					ErrorManager.error(ErrorManager.MSG_TOKENS_FILE_SYNTAX_ERROR,
									   vocabName+ CodeGenerator.VOCAB_FILE_EXTENSION,
									   Utils.integer(lineNum));
					while ( tokenizer.nextToken() != StreamTokenizer.TT_EOL ) {;}
					token = tokenizer.nextToken();
					continue;
				}
				token = tokenizer.nextToken();
				if ( token != '=' ) {
					ErrorManager.error(ErrorManager.MSG_TOKENS_FILE_SYNTAX_ERROR,
									   vocabName+CodeGenerator.VOCAB_FILE_EXTENSION,
									   Utils.integer(lineNum));
					while ( tokenizer.nextToken() != StreamTokenizer.TT_EOL ) {;}
					token = tokenizer.nextToken();
					continue;
				}
				token = tokenizer.nextToken(); // skip '='
				if ( token != StreamTokenizer.TT_NUMBER ) {
					ErrorManager.error(ErrorManager.MSG_TOKENS_FILE_SYNTAX_ERROR,
									   vocabName+CodeGenerator.VOCAB_FILE_EXTENSION,
									   Utils.integer(lineNum));
					while ( tokenizer.nextToken() != StreamTokenizer.TT_EOL ) {;}
					token = tokenizer.nextToken();
					continue;
				}
				int tokenType = (int)tokenizer.nval;
				token = tokenizer.nextToken();
				System.out.println("import "+tokenID+"="+tokenType);
				tokens.put(tokenID, tokenType);
				maxTokenType = Math.max(maxTokenType,tokenType);
				lineNum++;
				if ( token != StreamTokenizer.TT_EOL ) {
					ErrorManager.error(ErrorManager.MSG_TOKENS_FILE_SYNTAX_ERROR,
									   vocabName+CodeGenerator.VOCAB_FILE_EXTENSION,
									   Utils.integer(lineNum));
					while ( tokenizer.nextToken() != StreamTokenizer.TT_EOL ) {;}
					token = tokenizer.nextToken();
					continue;
				}
				token = tokenizer.nextToken(); // skip newline
			}
			br.close();
		}
		catch (FileNotFoundException fnfe) {
			ErrorManager.error(ErrorManager.MSG_CANNOT_FIND_TOKENS_FILE,
							   fullFile);
		}
		catch (IOException ioe) {
			ErrorManager.error(ErrorManager.MSG_ERROR_READING_TOKENS_FILE,
							   fullFile,
							   ioe);
		}
		catch (Exception e) {
			ErrorManager.error(ErrorManager.MSG_ERROR_READING_TOKENS_FILE,
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

		File f = new File(tool.getLibraryDirectory(),
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
		if (tool.haveOutputDir) {
			f = new File(tool.getOutputDirectory(), vocabName + CodeGenerator.VOCAB_FILE_EXTENSION);
		}
		else {
			f = new File(vocabName + CodeGenerator.VOCAB_FILE_EXTENSION);
		}
		return f;
	}
}
