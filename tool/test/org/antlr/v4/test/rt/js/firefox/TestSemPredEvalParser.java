package org.antlr.v4.test.rt.js.firefox;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestSemPredEvalParser extends BaseTest {

	@Test
	public void testSimpleValidate() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : a ;\n" +
	                  "a : {false}? ID  {document.getElementById('output').value += \"alt 1\" + '\\n';}\n" +
	                  "  | {true}?  INT {document.getElementById('output').value += \"alt 2\" + '\\n';}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x");
		assertEquals("", found);
		assertEquals("line 1:0 no viable alternative at input 'x'\n", this.stderrDuringParse);
	}

	@Test
	public void testSimpleValidate2() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : a a a;\n" +
	                  "a : {false}? ID  {document.getElementById('output').value += \"alt 1\" + '\\n';}\n" +
	                  "  | {true}?  INT {document.getElementById('output').value += \"alt 2\" + '\\n';}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "3 4 x");
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
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "start", "a+b+a");
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
	                  "a : {false}? ID  {document.getElementById('output').value += \"alt 1\" + '\\n';}\n" +
	                  "  | {true}?  INT {document.getElementById('output').value += \"alt 2\" + '\\n';}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x ; y");
		assertEquals("", found);
		assertEquals("line 1:0 no viable alternative at input 'x'\nline 1:4 no viable alternative at input 'y'\n", this.stderrDuringParse);
	}

	@Test
	public void testSimple() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : a a a; // do 3x: once in ATN, next in DFA then INT in ATN\n" +
	                  "a : {false}? ID {document.getElementById('output').value += \"alt 1\" + '\\n';}\n" +
	                  "  | {true}?  ID {document.getElementById('output').value += \"alt 2\" + '\\n';}\n" +
	                  "  | INT         {document.getElementById('output').value += \"alt 3\" + '\\n';}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x y 3");
		assertEquals("alt 2\nalt 2\nalt 3\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testOrder() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : a {} a; // do 2x: once in ATN, next in DFA;\n" +
	                  "// action blocks lookahead from falling off of 'a'\n" +
	                  "// and looking into 2nd 'a' ref. !ctx dependent pred\n" +
	                  "a : ID {document.getElementById('output').value += \"alt 1\" + '\\n';}\n" +
	                  "  | {true}?  ID {document.getElementById('output').value += \"alt 2\" + '\\n';}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x y");
		assertEquals("alt 1\nalt 1\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void test2UnpredicatedAlts() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : {this._interp.predictionMode = antlr4.atn.PredictionMode.LL_EXACT_AMBIG_DETECTION;} a ';' a; // do 2x: once in ATN, next in DFA\n" +
	                  "a : ID {document.getElementById('output').value += \"alt 1\" + '\\n';}\n" +
	                  "  | ID {document.getElementById('output').value += \"alt 2\" + '\\n';}\n" +
	                  "  | {false}? ID {document.getElementById('output').value += \"alt 3\" + '\\n';}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x; y");
		assertEquals("alt 1\nalt 1\n", found);
		assertEquals("line 1:0 reportAttemptingFullContext d=0 (a), input='x'\nline 1:0 reportAmbiguity d=0 (a): ambigAlts={1, 2}, input='x'\nline 1:3 reportAttemptingFullContext d=0 (a), input='y'\nline 1:3 reportAmbiguity d=0 (a): ambigAlts={1, 2}, input='y'\n", this.stderrDuringParse);
	}

	@Test
	public void test2UnpredicatedAltsAndOneOrthogonalAlt() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : {this._interp.predictionMode = antlr4.atn.PredictionMode.LL_EXACT_AMBIG_DETECTION;} a ';' a ';' a;\n" +
	                  "a : INT {document.getElementById('output').value += \"alt 1\" + '\\n';}\n" +
	                  "  | ID {document.getElementById('output').value += \"alt 2\" + '\\n';} // must pick this one for ID since pred is false\n" +
	                  "  | ID {document.getElementById('output').value += \"alt 3\" + '\\n';}\n" +
	                  "  | {false}? ID {console.log(\\\"alt 4\\\");}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "34; x; y");
		assertEquals("alt 1\nalt 2\nalt 2\n", found);
		assertEquals("line 1:4 reportAttemptingFullContext d=0 (a), input='x'\nline 1:4 reportAmbiguity d=0 (a): ambigAlts={2, 3}, input='x'\nline 1:7 reportAttemptingFullContext d=0 (a), input='y'\nline 1:7 reportAmbiguity d=0 (a): ambigAlts={2, 3}, input='y'\n", this.stderrDuringParse);
	}

	@Test
	public void testRewindBeforePredEval() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : a a;\n" +
	                  "a : {this._input.LT(1).text===\"x\"}? ID INT {document.getElementById('output').value += \"alt 1\" + '\\n';}\n" +
	                  "  | {this._input.LT(1).text===\"y\"}? ID INT {document.getElementById('output').value += \"alt 2\" + '\\n';}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "y 3 x 4");
		assertEquals("alt 2\nalt 1\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testNoTruePredsThrowsNoViableAlt() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : a a;\n" +
	                  "a : {false}? ID INT {document.getElementById('output').value += \"alt 1\" + '\\n';}\n" +
	                  "  | {false}? ID INT {document.getElementById('output').value += \"alt 2\" + '\\n';}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "y 3 x 4");
		assertEquals("", found);
		assertEquals("line 1:0 no viable alternative at input 'y'\n", this.stderrDuringParse);
	}

	@Test
	public void testToLeft() throws Exception {
		String grammar = "grammar T;\n" +
	                  "	s : a+ ;\n" +
	                  "a : {false}? ID {document.getElementById('output').value += \"alt 1\" + '\\n';}\n" +
	                  "  | {true}?  ID {document.getElementById('output').value += \"alt 2\" + '\\n';}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x x y");
		assertEquals("alt 2\nalt 2\nalt 2\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testUnpredicatedPathsInAlt() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : a {document.getElementById('output').value += \"alt 1\" + '\\n';}\n" +
	                  "  | b {document.getElementById('output').value += \"alt 2\" + '\\n';}\n" +
	                  "  ;\n" +
	                  "a : {false}? ID INT\n" +
	                  "  | ID INT\n" +
	                  "  ;\n" +
	                  "b : ID ID\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x 4");
		assertEquals("alt 1\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testActionHidesPreds() throws Exception {
		String grammar = "grammar T;\n" +
	                  "@members {this.i = 0;}\n" +
	                  "s : a+ ;\n" +
	                  "a : {this.i = 1;} ID {this.i === 1}? {document.getElementById('output').value += \"alt 1\" + '\\n';}\n" +
	                  "  | {this.i = 2;} ID {this.i === 2}? {document.getElementById('output').value += \"alt 2\" + '\\n';}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x x y");
		assertEquals("alt 1\nalt 1\nalt 1\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testToLeftWithVaryingPredicate() throws Exception {
		String grammar = "grammar T;\n" +
	                  "@members {this.i=0}\n" +
	                  "s : ({this.i += 1;\\ndocument.getElementById('output').value += \"i=\" + this.i + '\\n';} a)+ ;\n" +
	                  "a : {this.i % m === 0}? ID {document.getElementById('output').value += \"alt 1\" + '\\n';}\n" +
	                  "  | {this.i % m != 0}? ID {document.getElementById('output').value += \"alt 2\" + '\\n';}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x x y");
		assertEquals("i=1\nalt 2\ni=2\nalt 1\ni=3\nalt 2\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testPredicateDependentOnArg() throws Exception {
		String grammar = "grammar T;\n" +
	                  "@members {i=0}\n" +
	                  "s : a[2] a[1];\n" +
	                  "\"a[int i]\" +\n" +
	                  "\"  : {$i===1}? ID {document.getElementById('output').value += \"alt 1\" + '\\n';}\n" +
	                  "  | {$i===2}? ID {document.getElementById('output').value += \"alt 2\" + '\\n';}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "a b");
		assertEquals("alt 2\nalt 1\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testPredicateDependentOnArg2() throws Exception {
		String grammar = "grammar T;\n" +
	                  "@members {i=0}\n" +
	                  "s : a[2] a[1];\n" +
	                  "a[int i]\" +\n" +
	                  "  : {$i===1}? ID {document.getElementById('output').value += \"alt 1\" + '\\n';}\n" +
	                  "  | {$i===2}? ID {document.getElementById('output').value += \"alt 2\" + '\\n';}\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "a b");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testDependentPredNotInOuterCtxShouldBeIgnored() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : b[2] ';' |  b[2] '.' ; // decision in s drills down to ctx-dependent pred in a;\n" +
	                  "b[int i] : a[i] ;\n" +
	                  "a[int i]\" +\n" +
	                  "  : {$i===1}? ID {document.getElementById('output').value += \"alt 1\" + '\\n';}\n" +
	                  "    | {$i===2}? ID {document.getElementById('output').value += \"alt 2\" + '\\n';}\n" +
	                  "    ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "a;");
		assertEquals("alt 2\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testIndependentPredNotPassedOuterCtxToAvoidCastException() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s : b ';' |  b '.' ;\n" +
	                  "b : a ;\n" +
	                  "a\n" +
	                  "  : {false}? ID {document.getElementById('output').value += \"alt 1\" + '\\n';}\n" +
	                  "  | {true}? ID {document.getElementById('output').value += \"alt 2\" + '\\n';}\n" +
	                  " ;\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "a;");
		assertEquals("alt 2\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testPredsInGlobalFOLLOW() throws Exception {
		String grammar = "grammar T;\n" +
	                  "@members {\n" +
	                  "this.pred = function(v) {\n" +
	                  "	document.getElementById('output').value += 'eval=\" + v.toString() + '\\n';\n" +
	                  "	return v;\n" +
	                  "};\n" +
	                  "}\n" +
	                  "s : e {this.pred(true)}? {document.getElementById('output').value += \"parse\" + '\\n';} '!' ;\n" +
	                  "t : e {this.pred(false)}? ID ;\n" +
	                  "e : ID | ; // non-LL(1) so we use ATN\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "a!");
		assertEquals("eval=true\nparse\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testDepedentPredsInGlobalFOLLOW() throws Exception {
		String grammar = "grammar T;\n" +
	                  "@members {\n" +
	                  "this.pred = function(v) {\n" +
	                  "	document.getElementById('output').value += 'eval=\" + v.toString() + '\\n';\n" +
	                  "	return v;\n" +
	                  "};\n" +
	                  "}\n" +
	                  "s : a[99] ;\n" +
	                  "a[int i] : e {this.pred($i===99)}? {document.getElementById('output').value += \"parse\" + '\\n';} '!' ;\n" +
	                  "b[int i] : e {this.pred($i===99)}? ID ;\n" +
	                  "e : ID | ; // non-LL(1) so we use ATN\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "a!");
		assertEquals("eval=true\nparse\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testActionsHidePredsInGlobalFOLLOW() throws Exception {
		String grammar = "grammar T;\n" +
	                  "@members {\n" +
	                  "this.p = function(v) {\n" +
	                  "this.pred = function(v) {\n" +
	                  "	document.getElementById('output').value += 'eval=\" + v.toString() + '\\n';\n" +
	                  "	return v;\n" +
	                  "};\n" +
	                  "}\n" +
	                  "s : e {} {this.pred(true)}? {document.getElementById('output').value += \"parse\" + '\\n';} '!' ;\n" +
	                  "t : e {} {this.pred(false)}? ID ;\n" +
	                  "e : ID | ; // non-LL(1) so we use ATN\n" +
	                  "ID : 'a'..'z'+ ;\n" +
	                  "INT : '0'..'9'+;\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "a!");
		assertEquals("eval=true\nparse\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testPredTestedEvenWhenUnAmbig(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "@members {this.enumKeyword = true;}\n" +
	                  "primary\n" +
	                  "    :   ID {document.getElementById('output').value += \"ID \"+$ID.text + '\\n';}\n" +
	                  "    |   {!this.enumKeyword}? 'enum' {document.getElementById('output').value += \"enum\" + '\\n';}\n" +
	                  "    ;\n" +
	                  "ID : [a-z]+ ;\n" +
	                  "WS : [ \\t\\n\\r]+ -> skip ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "primary", input);
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
	                  "content: anything | {false}? .;\n" +
	                  "anything: ANY_CHAR;\n" +
	                  "ANY_CHAR: [_a-zA-Z0-9];";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "cppCompilationUnit", "hello");
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	String testPredFromAltTestedInLoopBack(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "file_\n" +
	                  "@after {document.getElementById('output').value += $ctx.toStringTree(null, this); + '\\n';}\n" +
	                  "  : para para EOF ;\n" +
	                  "para: paraContent NL NL ;\n" +
	                  "paraContent : ('s'|'x'|{this._input.LA(2)!=NL}? NL)+ ;\n" +
	                  "NL : '\\n' ;\n" +
	                  "s : 's' ;\n" +
	                  "X : 'x' ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "file_", input);
	}

	@Test
	public void testPredFromAltTestedInLoopBack_1() throws Exception {
		String found = testPredFromAltTestedInLoopBack("s\n\n\nx\n");
		assertEquals("(file_ (para (paraContent s) \n \n) (para (paraContent \n x \n)) <EOF>)\n", found);
		assertEquals("line 5:2 mismatched input '<EOF>' expecting '\n'\n", this.stderrDuringParse);
	}

	@Test
	public void testPredFromAltTestedInLoopBack_2() throws Exception {
		String found = testPredFromAltTestedInLoopBack("s\n\n\nx\n\n");
		assertEquals("(file_ (para (paraContent s) \n \n) (para (paraContent \n x) \n \n) <EOF>)\n", found);
		assertNull(this.stderrDuringParse);
	}


}