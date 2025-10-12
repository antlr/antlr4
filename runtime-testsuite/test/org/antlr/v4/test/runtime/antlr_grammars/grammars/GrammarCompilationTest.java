import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GrammarCompilationTest {

    @Test
    public void testArithmeticGrammarLoads() throws Exception {
        ArithmeticLexer lexer = new ArithmeticLexer(CharStreams.fromString("3+4*5"));
        ArithmeticParser parser = new ArithmeticParser(new CommonTokenStream(lexer));
        ParseTree tree = parser.expr();
        assertNotNull(tree, "Parse tree should not be null");
    }
}
