import org.antlr.v4.runtime.*;

public class TestU {
	public static void main(String[] args) throws Exception {
		ULexer t = new ULexer(new ANTLRFileStream(args[0]));
		CommonTokenStream tokens = new CommonTokenStream(t);
		UParser p = new UParser(tokens);
		p.setBuildParseTree(true);
		UParser.aContext ctx = p.a();
	}
}
