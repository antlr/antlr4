import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class TestT {
	public static void main(String[] args) throws Exception {
		CharStream input = new ANTLRFileStream(args[0]);
		TLexer lex = new TLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lex);
//		tokens.fill();
//		System.out.println(tokens);
		TParser parser = new TParser(tokens);
		parser.setBuildParseTree(true);
		parser.s();
	}
}
