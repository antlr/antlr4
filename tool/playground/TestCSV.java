import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenFactory;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.UnbufferedCharStream;
import org.antlr.v4.runtime.UnbufferedTokenStream;

import java.io.FileInputStream;
import java.io.InputStream;

public class TestCSV {
	public static void main(String[] args) throws Exception {
		String inputFile = null;
		if ( args.length>0 ) inputFile = args[0];
		InputStream is = System.in;
		if ( inputFile!=null ) {
			is = new FileInputStream(inputFile);
		}

		CharStream input = new UnbufferedCharStream(is);
		CSVLexer lex = new CSVLexer(input);
		// copy text out of sliding buffer and store in tokens
		lex.setTokenFactory(new CommonTokenFactory(true));
		TokenStream tokens = new UnbufferedTokenStream<CommonToken>(lex);
		CSVParser parser = new CSVParser(tokens);
		parser.setBuildParseTree(false);
//		parser.file();
		Token t = tokens.LT(1);
		while ( t.getType()!=-1 ) {
			tokens.consume();
			t = tokens.LT(1);
		}
	}
}
