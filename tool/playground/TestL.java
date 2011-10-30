import org.antlr.v4.runtime.*;

public class TestL {
	public static void main(String[] args) throws Exception {
		CharStream input = new ANTLRFileStream(args[0]);
		input = new ANTLRStringStream("3 3");
		L lexer = new L(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		tokens.fill();
//		System.out.println(tokens.getTokens());
		for (Object t : tokens.getTokens()) System.out.println(t);
	}
}
