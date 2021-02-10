package org.antlr.v4.test.runtime;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.IntegerList;
import org.antlr.v4.tool.LexerGrammar;

import java.io.*;
import java.util.*;

public abstract class RuntimeTestUtils {

	/** Sort a list */
	public static <T extends Comparable<? super T>> List<T> sort(List<T> data) {
		List<T> dup = new ArrayList<T>(data);
		dup.addAll(data);
		Collections.sort(dup);
		return dup;
	}

	/** Return map sorted by key */
	public static <K extends Comparable<? super K>,V> LinkedHashMap<K,V> sort(Map<K,V> data) {
		LinkedHashMap<K,V> dup = new LinkedHashMap<K, V>();
		List<K> keys = new ArrayList<K>(data.keySet());
		Collections.sort(keys);
		for (K k : keys) {
			dup.put(k, data.get(k));
		}
		return dup;
	}

	public static List<String> getTokenTypes(LexerGrammar lg,
									  ATN atn,
									  CharStream input) {
		LexerATNSimulator interp = new LexerATNSimulator(atn, new DFA[]{new DFA(atn.modeToStartState.get(Lexer.DEFAULT_MODE))}, null);
		List<String> tokenTypes = new ArrayList<String>();
		int ttype;
		boolean hitEOF = false;
		do {
			if ( hitEOF ) {
				tokenTypes.add("EOF");
				break;
			}
			int t = input.LA(1);
			ttype = interp.match(input, Lexer.DEFAULT_MODE);
			if ( ttype==Token.EOF ) {
				tokenTypes.add("EOF");
			}
			else {
				tokenTypes.add(lg.typeToTokenList.get(ttype));
			}

			if ( t== IntStream.EOF ) {
				hitEOF = true;
			}
		} while ( ttype!=Token.EOF );
		return tokenTypes;
	}

	public static IntegerList getTokenTypesViaATN(String input, LexerATNSimulator lexerATN) {
		ANTLRInputStream in = new ANTLRInputStream(input);
		IntegerList tokenTypes = new IntegerList();
		int ttype;
		do {
			ttype = lexerATN.match(in, Lexer.DEFAULT_MODE);
			tokenTypes.add(ttype);
		} while ( ttype!= Token.EOF );
		return tokenTypes;
	}

	public static void copyFile(File source, File dest) throws IOException {
		InputStream is = new FileInputStream(source);
		OutputStream os = new FileOutputStream(dest);
		byte[] buf = new byte[4 << 10];
		int l;
		while ((l = is.read(buf)) > -1) {
			os.write(buf, 0, l);
		}
		is.close();
		os.close();
	}

    public static void mkdir(String dir) {
        File f = new File(dir);
        f.mkdirs();
    }
}
