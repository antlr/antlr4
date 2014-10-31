package org.antlr.v4.test.rt.py3;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestSemPredEvalParser extends BasePython3Test {

	@Test
	public void testSimpleValidate() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : a ;\n" +
	                  "a : {False}? ID  {print(\"alt 1\")}\n" +
	                  "  | {True}?  INT {print(\"alt 2\")}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x", true);
		assertEquals("", found);
		assertEquals("line 1:0 no viable alternative at input 'x'\n", this.stderrDuringParse);
	}

	@Test
	public void testSimpleValidate2() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : a a a;\n" +
	                  "a : {False}? ID  {print(\"alt 1\")}\n" +
	                  "  | {True}?  INT {print(\"alt 2\")}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "3 4 x", true);
		assertEquals("alt 2\nalt 2\n", found);
		assertEquals("line 1:4 no viable alternative at input 'x'\n", this.stderrDuringParse);
	}

	@Test
	public void testAtomWithClosureInTranslatedLRRule() throws Exception {
		String grammar = "grammar T;\n" +
	                  "start : e[0] EOF;\n" +
	                  "e[int _p]\n" +
	                  "    :   ( 'a' | 'b'+ ) ( {3 >= $_p}? '+' e[4] )*\n" +
	                  "    ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "start", "a+b+a", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testValidateInDFA() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : a ';' a;\n" +
	                  "// ';' helps us to resynchronize without consuming\n" +
	                  "// 2nd 'a' reference. We our testing that the DFA also\n" +
	                  "// throws an exception if the validating predicate fails\n" +
	                  "a : {False}? ID  {print(\"alt 1\")}\n" +
	                  "  | {True}?  INT {print(\"alt 2\")}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x ; y", true);
		assertEquals("", found);
		assertEquals("line 1:0 no viable alternative at input 'x'\nline 1:4 no viable alternative at input 'y'\n", this.stderrDuringParse);
	}

	@Test
	public void testSimple() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : a a a; // do 3x: once in ATN, next in DFA then INT in ATN\n" +
	                  "a : {False}? ID {print(\"alt 1\")}\n" +
	                  "  | {True}?  ID {print(\"alt 2\")}\n" +
	                  "  | INT         {print(\"alt 3\")}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x y 3", true);
		assertEquals("alt 2\nalt 2\nalt 3\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testOrder() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : a {} a; // do 2x: once in ATN, next in DFA;\n" +
	                  "// action blocks lookahead from falling off of 'a'\n" +
	                  "// and looking into 2nd 'a' ref. !ctx dependent pred\n" +
	                  "a : ID {print(\"alt 1\")}\n" +
	                  "  | {True}?  ID {print(\"alt 2\")}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x y", false);
		assertEquals("alt 1\nalt 1\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void test2UnpredicatedAlts() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : {self._interp.predictionMode =  PredictionMode.LL_EXACT_AMBIG_DETECTION} a ';' a; // do 2x: once in ATN, next in DFA\n" +
	                  "a : ID {print(\"alt 1\")}\n" +
	                  "  | ID {print(\"alt 2\")}\n" +
	                  "  | {False}? ID {print(\"alt 3\")}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x; y", true);
		assertEquals("alt 1\nalt 1\n", found);
		assertEquals("line 1:0 reportAttemptingFullContext d=0 (a), input='x'\nline 1:0 reportAmbiguity d=0 (a): ambigAlts={1, 2}, input='x'\nline 1:3 reportAttemptingFullContext d=0 (a), input='y'\nline 1:3 reportAmbiguity d=0 (a): ambigAlts={1, 2}, input='y'\n", this.stderrDuringParse);
	}

	@Test
	public void test2UnpredicatedAltsAndOneOrthogonalAlt() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : {self._interp.predictionMode =  PredictionMode.LL_EXACT_AMBIG_DETECTION} a ';' a ';' a;\n" +
	                  "a : INT {print(\"alt 1\")}\n" +
	                  "  | ID {print(\"alt 2\")} // must pick this one for ID since pred is false\n" +
	                  "  | ID {print(\"alt 3\")}\n" +
	                  "  | {False}? ID {print(\"alt 4\")}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "34; x; y", true);
		assertEquals("alt 1\nalt 2\nalt 2\n", found);
		assertEquals("line 1:4 reportAttemptingFullContext d=0 (a), input='x'\nline 1:4 reportAmbiguity d=0 (a): ambigAlts={2, 3}, input='x'\nline 1:7 reportAttemptingFullContext d=0 (a), input='y'\nline 1:7 reportAmbiguity d=0 (a): ambigAlts={2, 3}, input='y'\n", this.stderrDuringParse);
	}

	@Test
	public void testRewindBeforePredEval() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : a a;\n" +
	                  "a : {self._input.LT(1).text==\"x\"}? ID INT {print(\"alt 1\")}\n" +
	                  "  | {self._input.LT(1).text==\"y\"}? ID INT {print(\"alt 2\")}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "y 3 x 4", true);
		assertEquals("alt 2\nalt 1\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testNoTruePredsThrowsNoViableAlt() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : a a;\n" +
	                  "a : {False}? ID INT {print(\"alt 1\")}\n" +
	                  "  | {False}? ID INT {print(\"alt 2\")}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "y 3 x 4", false);
		assertEquals("", found);
		assertEquals("line 1:0 no viable alternative at input 'y'\n", this.stderrDuringParse);
	}

	@Test
	public void testToLeft() throws Exception {
		String grammar = "grammar T;\n" +
	                  "	s : a+ ;\n" +
	                  "a : {False}? ID {print(\"alt 1\")}\n" +
	                  "  | {True}?  ID {print(\"alt 2\")}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x x y", true);
		assertEquals("alt 2\nalt 2\nalt 2\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testUnpredicatedPathsInAlt() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : a {print(\"alt 1\")}\n" +
	                  "  | b {print(\"alt 2\")}\n" +
	                  "  ;\n" +
	                  "a : {False}? ID INT\n" +
	                  "  | ID INT\n" +
	                  "  ;\n" +
	                  "b : ID ID\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x 4", true);
		assertEquals("alt 1\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testActionHidesPreds() throws Exception {
		String grammar = "grammar T;\n" +
	                  "@members {i = 0}\n" +
	                  "s : a+ ;\n" +
	                  "a : {self.i = 1} ID {self.i == 1}? {print(\"alt 1\")}\n" +
	                  "  | {self.i = 2} ID {self.i == 2}? {print(\"alt 2\")}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x x y", false);
		assertEquals("alt 1\nalt 1\nalt 1\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testToLeftWithVaryingPredicate() throws Exception {
		String grammar = "grammar T;\n" +
	                  "@members {i = 0}\n" +
	                  "s : ({self.i += 1\n" +
	                  "print(\"i=\" + str(self.i))} a)+ ;\n" +
	                  "a : {self.i % 2 == 0}? ID {print(\"alt 1\")}\n" +
	                  "  | {self.i % 2 != 0}? ID {print(\"alt 2\")}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x x y", true);
		assertEquals("i=1\nalt 2\ni=2\nalt 1\ni=3\nalt 2\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testPredicateDependentOnArg() throws Exception {
		String grammar = "grammar T;\n" +
	                  "@members {i = 0}\n" +
	                  "s : a[2] a[1];\n" +
	                  "a[int i]\n" +
	                  "  : {$i==1}? ID {print(\"alt 1\")}\n" +
	                  "  | {$i==2}? ID {print(\"alt 2\")}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "a b", true);
		assertEquals("alt 2\nalt 1\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testPredicateDependentOnArg2() throws Exception {
		String grammar = "grammar T;\n" +
	                  "@members {i = 0}\n" +
	                  "s : a[2] a[1];\n" +
	                  "a[int i]\n" +
	                  "  : {$i==1}? ID \n" +
	                  "  | {$i==2}? ID \n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "a b", true);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testDependentPredNotInOuterCtxShouldBeIgnored() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : b[2] ';' |  b[2] '.' ; // decision in s drills down to ctx-dependent pred in a;\n" +
	                  "b[int i] : a[i] ;\n" +
	                  "a[int i]\n" +
	                  "  : {$i==1}? ID {print(\"alt 1\")}\n" +
	                  "    | {$i==2}? ID {print(\"alt 2\")}\n" +
	                  "    ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "a;", true);
		assertEquals("alt 2\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testIndependentPredNotPassedOuterCtxToAvoidCastException() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : b ';' |  b '.' ;\n" +
	                  "b : a ;\n" +
	                  "a\n" +
	                  "  : {False}? ID {print(\"alt 1\")}\n" +
	                  "  | {True}? ID {print(\"alt 2\")}\n" +
	                  " ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "a;", true);
		assertEquals("alt 2\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testPredsInGlobalFOLLOW() throws Exception {
		String grammar = "grammar T;\n" +
	                  "@members {\n" +
	                  "def pred(self, v):\n" +
	                  "	print('eval=' + str(v).lower())\n" +
	                  "	return v\n" +
	                  "\n" +
	                  "}\n" +
	                  "s : e {self.pred(True)}? {print(\"parse\")} '!' ;\n" +
	                  "t : e {self.pred(False)}? ID ;\n" +
	                  "e : ID | ; // non-LL(1) so we use ATN\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "a!", true);
		assertEquals("eval=true\nparse\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testDepedentPredsInGlobalFOLLOW() throws Exception {
		String grammar = "grammar T;\n" +
	                  "@members {\n" +
	                  "def pred(self, v):\n" +
	                  "	print('eval=' + str(v).lower())\n" +
	                  "	return v\n" +
	                  "\n" +
	                  "}\n" +
	                  "s : a[99] ;\n" +
	                  "a[int i] : e {self.pred($i==99)}? {print(\"parse\")} '!' ;\n" +
	                  "b[int i] : e {self.pred($i==99)}? ID ;\n" +
	                  "e : ID | ; // non-LL(1) so we use ATN\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "a!", true);
		assertEquals("eval=true\nparse\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testActionsHidePredsInGlobalFOLLOW() throws Exception {
		String grammar = "grammar T;\n" +
	                  "@members {\n" +
	                  "def pred(self, v):\n" +
	                  "	print('eval=' + str(v).lower())\n" +
	                  "	return v\n" +
	                  "\n" +
	                  "}\n" +
	                  "s : e {} {self.pred(True)}? {print(\"parse\")} '!' ;\n" +
	                  "t : e {} {self.pred(False)}? ID ;\n" +
	                  "e : ID | ; // non-LL(1) so we use ATN\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "a!", true);
		assertEquals("eval=true\nparse\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testPredTestedEvenWhenUnAmbig(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "@members {enumKeyword = True}\n" +
	                  "primary\n" +
	                  "    :   ID {print(\"ID \"+$ID.text)}\n" +
	                  "    |   {not self.enumKeyword}? 'enum' {print(\"enum\")}\n" +
	                  "    ;\n" +
	                  "ID : [a-z]+ ;\n" +
	                  "WS : [ \\t\\n\\r]+ -> skip ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "primary", input, true);
	}

	@Test
	public void testPredTestedEvenWhenUnAmbig_1() throws Exception {
		String found = testPredTestedEvenWhenUnAmbig("abc");
		assertEquals("ID abc\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testPredTestedEvenWhenUnAmbig_2() throws Exception {
		String found = testPredTestedEvenWhenUnAmbig("enum");
		assertEquals("", found);
		assertEquals("line 1:0 no viable alternative at input 'enum'\n", this.stderrDuringParse);
	}

	@Test
	public void testDisabledAlternative() throws Exception {
		String grammar = "grammar T;\n" +
	                  "cppCompilationUnit : content+ EOF;\n" +
	                  "content: anything | {False}? .;\n" +
	                  "anything: ANY_CHAR;\n" +
	                  "ANY_CHAR: [_a-zA-Z0-9];";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "cppCompilationUnit", "hello", true);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	String testPredFromAltTestedInLoopBack(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "file_\n" +
	                  "@after {print($ctx.toStringTree(recog=self))}\n" +
	                  "  : para para EOF ;\n" +
	                  "para: paraContent NL NL ;\n" +
	                  "paraContent : ('s'|'x'|{self._input.LA(2)!=NL}? NL)+ ;\n" +
	                  "NL : '\\n' ;\n" +
	                  "s : 's' ;\n" +
	                  "X : 'x' ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "file_", input, true);
	}

	@Test
	public void testPredFromAltTestedInLoopBack_1() throws Exception {
		String found = testPredFromAltTestedInLoopBack("s\n\n\nx\n");
		assertEquals("(file_ (para (paraContent s) \\n \\n) (para (paraContent \\n x \\n)) <EOF>)\n", found);
		assertEquals("line 5:2 mismatched input '<EOF>' expecting '\n'\n", this.stderrDuringParse);
	}

	@Test
	public void testPredFromAltTestedInLoopBack_2() throws Exception {
		String found = testPredFromAltTestedInLoopBack("s\n\n\nx\n\n");
		assertEquals("(file_ (para (paraContent s) \\n \\n) (para (paraContent \\n x) \\n \\n) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}


}