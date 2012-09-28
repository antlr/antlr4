import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenFactory;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.UnbufferedCharStream;

import java.io.FileInputStream;
import java.io.InputStream;

public class TestT {
	public static void main(String[] args) throws Exception {
		String inputFile = null;
		if ( args.length>0 ) inputFile = args[0];
		InputStream is = System.in;
		if ( inputFile!=null ) {
			is = new FileInputStream(inputFile);
		}
		CharStream input = new UnbufferedCharStream(is);

		TLexer lex = new TLexer(input);
		lex.setTokenFactory(new CommonTokenFactory(true));

		CommonTokenStream tokens = new CommonTokenStream(lex);
		TParser parser = new TParser(tokens);

		parser.addErrorListener(new DiagnosticErrorListener());

		ParserRuleContext tree = null;
		parser.getInterpreter().setSLL(true); // try with just SLL(*)
		// no errors messages or recovery wanted during first try
		parser.removeErrorListeners();
		parser.setErrorHandler(new BailErrorStrategy());
		try {
			tree = parser.s();
		}
		catch (RuntimeException ex) {
			if (ex.getClass() == RuntimeException.class &&
				ex.getCause() instanceof RecognitionException)
			{
				System.out.println("trying with LL(*)");
				tokens.reset(); // rewind
				// back to standard listeners/handlers
				parser.addErrorListener(ConsoleErrorListener.INSTANCE);
				parser.setErrorHandler(new DefaultErrorStrategy());
				parser.getInterpreter().setSLL(false); // try full LL(*)
				tree = parser.s();
			}
		}

//		parser.getInterpreter().setSLL(true);
//		parser.setTrace(true);

		System.out.println(tree.toStringTree(parser));
//		tree.save(parser, "/tmp/t.ps");
	}
}
