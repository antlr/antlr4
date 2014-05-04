package org.antlr.v4.test;

import org.antlr.v4.tool.ErrorType;
import org.junit.Test;

public class TestTokenPositionOptions extends BaseTest {
    @Test
       public void testErrorPositionInLeftRecursiveRule() throws Exception {
           String grammar =
                   "grammar T;\n" +
                   "s : e ';' ;\n" +
                   "e : e '*' e\n" +
                   "  | e '+' e # ick\n" +
                   "  | e '.' ID\n" +
                   "  | '-' e\n" +
                   "  | ID\n" +
                   "  ;\n" +
                   "ID : [a-z]+ ;\n";
           String error = "error(" + ErrorType.RULE_WITH_TOO_FEW_ALT_LABELS.code +
                          "): T.g4:4:12: rule 'e': must label all alternatives or none\n";

           String[] pair = new String[] {grammar, error};
      		super.testErrors(pair, true);
       }

}
