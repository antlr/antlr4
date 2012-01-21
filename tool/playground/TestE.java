import org.antlr.v4.runtime.*;

public class TestE {
    public static void main(String[] args) throws Exception {
        CharStream input = new ANTLRFileStream(args[0]);
        E lex = new E(input);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        tokens.fill();
        for (Object t : tokens.getTokens()) System.out.println(t);
    }
}
