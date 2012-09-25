import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenFactory;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.UnbufferedCharStream;

import java.io.FileInputStream;
import java.io.InputStream;

public class TestA {
	public static void main(String[] args) throws Exception {
		String inputFile = null;
		if ( args.length>0 ) inputFile = args[0];
		InputStream is = System.in;
		if ( inputFile!=null ) {
			is = new FileInputStream(inputFile);
		}
		CharStream input = new UnbufferedCharStream(is);

		A lex = new A(input);
		lex.setTokenFactory(new CommonTokenFactory(true));

		CommonTokenStream tokens = new CommonTokenStream(lex);
		tokens.fill();
		System.out.println(tokens.getTokens());
	}
}
