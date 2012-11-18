import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenFactory;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.ParserRuleContext;

import java.io.FileInputStream;
import java.io.InputStream;

public class TransformEType {
	public static void main(String[] args) throws Exception {
		String inputFile = null;
		if ( args.length>0 ) inputFile = args[0];
		InputStream is = System.in;
		if ( inputFile!=null ) {
			is = new FileInputStream(inputFile);
		}
		CharStream input = new ANTLRInputStream(is);

		JavaLRLexer lex = new JavaLRLexer(input);
		lex.setTokenFactory(new CommonTokenFactory(true));

		CommonTokenStream tokens = new CommonTokenStream(lex);
		JavaLRParser parser = new JavaLRParser(tokens);

		parser.addErrorListener(new DiagnosticErrorListener());

		ParserRuleContext tree = parser.compilationUnit();
	}
}
