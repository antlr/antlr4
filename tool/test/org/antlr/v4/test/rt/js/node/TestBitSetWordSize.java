package org.antlr.v4.test.rt.js.node;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestBitSetWordSize extends BaseTest {

	@Test
	public void testBitSetWordSize() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("grammar T;\n");
		// create 40 tokens so we exceed 32 bits size
		for(int i=1;i<=40;i++) {
			sb.append("T");
			sb.append(i);
			sb.append(" : 'x");
			sb.append(i);
			sb.append("';\n");
		}
		// create a rule which will generate bit set test
		sb.append("o : T1 | T2 | T3 | T4 | T5 | T6 | T7 | T8;\n");
		// create a rule where expected token - 32 uses same bit than optional token
		sb.append("a : o? T36 {console.log('ok');};\n");
		sb.append("WS : (' '|'\\n') -> skip ;");
		String grammar = sb.toString();
		// use input which triggers bug when word size is 64
		String input = "x36";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", input, false);
		assertEquals("ok\n", found);
		assertNull(this.stderrDuringParse);
	}



}
