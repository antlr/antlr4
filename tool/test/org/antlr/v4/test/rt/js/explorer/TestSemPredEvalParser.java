package org.antlr.v4.test.rt.js.explorer;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestSemPredEvalParser extends BaseTest {

	@Test
	public void testSimpleValidate() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s : a ;\r\n" +
	                  "a : {false}? ID  {document.getElementById('output').value += \"alt 1\" + '\\n';}\r\n" +
	                  "  | {true}?  INT {document.getElementById('output').value += \"alt 2\" + '\\n';}\r\n" +
	                  "  ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x", true);
		assertEquals("", found);
		assertEquals("line 1:0 no viable alternative at input 'x'\n", this.stderrDuringParse);
	}

	@Test
	public void testSimpleValidate2() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s : a a a;\r\n" +
	                  "a : {false}? ID  {document.getElementById('output').value += \"alt 1\" + '\\n';}\r\n" +
	                  "  | {true}?  INT {document.getElementById('output').value += \"alt 2\" + '\\n';}\r\n" +
	                  "  ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "3 4 x", true);
		assertEquals("alt 2\nalt 2\n", found);
		assertEquals("line 1:4 no viable alternative at input 'x'\n", this.stderrDuringParse);
	}

	@Test
	public void testAtomWithClosureInTranslatedLRRule() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "start : e[0] EOF;\r\n" +
	                  "e[int _p]\r\n" +
	                  "    :   ( 'a' | 'b'+ ) ( {3 >= $_p}? '+' e[4] )*\r\n" +
	                  "    ;\r\n" +
	                  "\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "start", "a+b+a", false);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testValidateInDFA() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s : a ';' a;\r\n" +
	                  "// ';' helps us to resynchronize without consuming\r\n" +
	                  "// 2nd 'a' reference. We our testing that the DFA also\r\n" +
	                  "// throws an exception if the validating predicate fails\r\n" +
	                  "a : {false}? ID  {document.getElementById('output').value += \"alt 1\" + '\\n';}\r\n" +
	                  "  | {true}?  INT {document.getElementById('output').value += \"alt 2\" + '\\n';}\r\n" +
	                  "  ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x ; y", true);
		assertEquals("", found);
		assertEquals("line 1:0 no viable alternative at input 'x'\nline 1:4 no viable alternative at input 'y'\n", this.stderrDuringParse);
	}

	@Test
	public void testSimple() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s : a a a; // do 3x: once in ATN, next in DFA then INT in ATN\r\n" +
	                  "a : {false}? ID {document.getElementById('output').value += \"alt 1\" + '\\n';}\r\n" +
	                  "  | {true}?  ID {document.getElementById('output').value += \"alt 2\" + '\\n';}\r\n" +
	                  "  | INT         {document.getElementById('output').value += \"alt 3\" + '\\n';}\r\n" +
	                  "  ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x y 3", true);
		assertEquals("alt 2\nalt 2\nalt 3\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testOrder() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s : a {} a; // do 2x: once in ATN, next in DFA;\r\n" +
	                  "// action blocks lookahead from falling off of 'a'\r\n" +
	                  "// and looking into 2nd 'a' ref. !ctx dependent pred\r\n" +
	                  "a : ID {document.getElementById('output').value += \"alt 1\" + '\\n';}\r\n" +
	                  "  | {true}?  ID {document.getElementById('output').value += \"alt 2\" + '\\n';}\r\n" +
	                  "  ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x y", false);
		assertEquals("alt 1\nalt 1\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void test2UnpredicatedAlts() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s : {this._interp.predictionMode = antlr4.atn.PredictionMode.LL_EXACT_AMBIG_DETECTION;} a ';' a; // do 2x: once in ATN, next in DFA\r\n" +
	                  "a : ID {document.getElementById('output').value += \"alt 1\" + '\\n';}\r\n" +
	                  "  | ID {document.getElementById('output').value += \"alt 2\" + '\\n';}\r\n" +
	                  "  | {false}? ID {document.getElementById('output').value += \"alt 3\" + '\\n';}\r\n" +
	                  "  ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x; y", true);
		assertEquals("alt 1\nalt 1\n", found);
		assertEquals("line 1:0 reportAttemptingFullContext d=0 (a), input='x'\nline 1:0 reportAmbiguity d=0 (a): ambigAlts={1, 2}, input='x'\nline 1:3 reportAttemptingFullContext d=0 (a), input='y'\nline 1:3 reportAmbiguity d=0 (a): ambigAlts={1, 2}, input='y'\n", this.stderrDuringParse);
	}

	@Test
	public void test2UnpredicatedAltsAndOneOrthogonalAlt() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s : {this._interp.predictionMode = antlr4.atn.PredictionMode.LL_EXACT_AMBIG_DETECTION;} a ';' a ';' a;\r\n" +
	                  "a : INT {document.getElementById('output').value += \"alt 1\" + '\\n';}\r\n" +
	                  "  | ID {document.getElementById('output').value += \"alt 2\" + '\\n';} // must pick this one for ID since pred is false\r\n" +
	                  "  | ID {document.getElementById('output').value += \"alt 3\" + '\\n';}\r\n" +
	                  "  | {false}? ID {document.getElementById('output').value += \"alt 4\" + '\\n';}\r\n" +
	                  "  ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "34; x; y", true);
		assertEquals("alt 1\nalt 2\nalt 2\n", found);
		assertEquals("line 1:4 reportAttemptingFullContext d=0 (a), input='x'\nline 1:4 reportAmbiguity d=0 (a): ambigAlts={2, 3}, input='x'\nline 1:7 reportAttemptingFullContext d=0 (a), input='y'\nline 1:7 reportAmbiguity d=0 (a): ambigAlts={2, 3}, input='y'\n", this.stderrDuringParse);
	}

	@Test
	public void testRewindBeforePredEval() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s : a a;\r\n" +
	                  "a : {this._input.LT(1).text===\"x\"}? ID INT {document.getElementById('output').value += \"alt 1\" + '\\n';}\r\n" +
	                  "  | {this._input.LT(1).text===\"y\"}? ID INT {document.getElementById('output').value += \"alt 2\" + '\\n';}\r\n" +
	                  "  ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "y 3 x 4", true);
		assertEquals("alt 2\nalt 1\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testNoTruePredsThrowsNoViableAlt() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s : a a;\r\n" +
	                  "a : {false}? ID INT {document.getElementById('output').value += \"alt 1\" + '\\n';}\r\n" +
	                  "  | {false}? ID INT {document.getElementById('output').value += \"alt 2\" + '\\n';}\r\n" +
	                  "  ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "y 3 x 4", false);
		assertEquals("", found);
		assertEquals("line 1:0 no viable alternative at input 'y'\n", this.stderrDuringParse);
	}

	@Test
	public void testToLeft() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "	s : a+ ;\r\n" +
	                  "a : {false}? ID {document.getElementById('output').value += \"alt 1\" + '\\n';}\r\n" +
	                  "  | {true}?  ID {document.getElementById('output').value += \"alt 2\" + '\\n';}\r\n" +
	                  "  ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x x y", true);
		assertEquals("alt 2\nalt 2\nalt 2\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testUnpredicatedPathsInAlt() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s : a {document.getElementById('output').value += \"alt 1\" + '\\n';}\r\n" +
	                  "  | b {document.getElementById('output').value += \"alt 2\" + '\\n';}\r\n" +
	                  "  ;\r\n" +
	                  "a : {false}? ID INT\r\n" +
	                  "  | ID INT\r\n" +
	                  "  ;\r\n" +
	                  "b : ID ID\r\n" +
	                  "  ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x 4", true);
		assertEquals("alt 1\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testActionHidesPreds() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "@members {this.i = 0;}\r\n" +
	                  "s : a+ ;\r\n" +
	                  "a : {this.i = 1;} ID {this.i === 1}? {document.getElementById('output').value += \"alt 1\" + '\\n';}\r\n" +
	                  "  | {this.i = 2;} ID {this.i === 2}? {document.getElementById('output').value += \"alt 2\" + '\\n';}\r\n" +
	                  "  ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x x y", false);
		assertEquals("alt 1\nalt 1\nalt 1\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testToLeftWithVaryingPredicate() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "@members {this.i = 0;}\r\n" +
	                  "s : ({this.i += 1;\r\n" +
	                  "document.getElementById('output').value += \"i=\" + this.i + '\\n';} a)+ ;\r\n" +
	                  "a : {this.i % 2 === 0}? ID {document.getElementById('output').value += \"alt 1\" + '\\n';}\r\n" +
	                  "  | {this.i % 2 != 0}? ID {document.getElementById('output').value += \"alt 2\" + '\\n';}\r\n" +
	                  "  ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x x y", true);
		assertEquals("i=1\nalt 2\ni=2\nalt 1\ni=3\nalt 2\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testPredicateDependentOnArg() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "@members {this.i = 0;}\r\n" +
	                  "s : a[2] a[1];\r\n" +
	                  "a[int i]\r\n" +
	                  "  : {$i===1}? ID {document.getElementById('output').value += \"alt 1\" + '\\n';}\r\n" +
	                  "  | {$i===2}? ID {document.getElementById('output').value += \"alt 2\" + '\\n';}\r\n" +
	                  "  ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "a b", true);
		assertEquals("alt 2\nalt 1\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testPredicateDependentOnArg2() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "@members {this.i = 0;}\r\n" +
	                  "s : a[2] a[1];\r\n" +
	                  "a[int i]\r\n" +
	                  "  : {$i===1}? ID \r\n" +
	                  "  | {$i===2}? ID \r\n" +
	                  "  ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "a b", true);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testDependentPredNotInOuterCtxShouldBeIgnored() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s : b[2] ';' |  b[2] '.' ; // decision in s drills down to ctx-dependent pred in a;\r\n" +
	                  "b[int i] : a[i] ;\r\n" +
	                  "a[int i]\r\n" +
	                  "  : {$i===1}? ID {document.getElementById('output').value += \"alt 1\" + '\\n';}\r\n" +
	                  "    | {$i===2}? ID {document.getElementById('output').value += \"alt 2\" + '\\n';}\r\n" +
	                  "    ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r\n" +
	                  "\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "a;", true);
		assertEquals("alt 2\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testIndependentPredNotPassedOuterCtxToAvoidCastException() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s : b ';' |  b '.' ;\r\n" +
	                  "b : a ;\r\n" +
	                  "a\r\n" +
	                  "  : {false}? ID {document.getElementById('output').value += \"alt 1\" + '\\n';}\r\n" +
	                  "  | {true}? ID {document.getElementById('output').value += \"alt 2\" + '\\n';}\r\n" +
	                  " ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "a;", true);
		assertEquals("alt 2\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testPredsInGlobalFOLLOW() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "@members {\r\n" +
	                  "this.pred = function(v) {\r\n" +
	                  "	document.getElementById('output').value += 'eval=' + v.toString() + '\\n';\r\n" +
	                  "	return v;\r\n" +
	                  "};\r\n" +
	                  "}\r\n" +
	                  "s : e {this.pred(true)}? {document.getElementById('output').value += \"parse\" + '\\n';} '!' ;\r\n" +
	                  "t : e {this.pred(false)}? ID ;\r\n" +
	                  "e : ID | ; // non-LL(1) so we use ATN\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "a!", true);
		assertEquals("eval=true\nparse\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testDepedentPredsInGlobalFOLLOW() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "@members {\r\n" +
	                  "this.pred = function(v) {\r\n" +
	                  "	document.getElementById('output').value += 'eval=' + v.toString() + '\\n';\r\n" +
	                  "	return v;\r\n" +
	                  "};\r\n" +
	                  "}\r\n" +
	                  "s : a[99] ;\r\n" +
	                  "a[int i] : e {this.pred($i===99)}? {document.getElementById('output').value += \"parse\" + '\\n';} '!' ;\r\n" +
	                  "b[int i] : e {this.pred($i===99)}? ID ;\r\n" +
	                  "e : ID | ; // non-LL(1) so we use ATN\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "a!", true);
		assertEquals("eval=true\nparse\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testActionsHidePredsInGlobalFOLLOW() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "@members {\r\n" +
	                  "this.pred = function(v) {\r\n" +
	                  "	document.getElementById('output').value += 'eval=' + v.toString() + '\\n';\r\n" +
	                  "	return v;\r\n" +
	                  "};\r\n" +
	                  "}\r\n" +
	                  "s : e {} {this.pred(true)}? {document.getElementById('output').value += \"parse\" + '\\n';} '!' ;\r\n" +
	                  "t : e {} {this.pred(false)}? ID ;\r\n" +
	                  "e : ID | ; // non-LL(1) so we use ATN\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "INT : '0'..'9'+;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "a!", true);
		assertEquals("eval=true\nparse\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testPredTestedEvenWhenUnAmbig(String input) throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "@members {this.enumKeyword = true;}\r\n" +
	                  "primary\r\n" +
	                  "    :   ID {document.getElementById('output').value += \"ID \"+$ID.text + '\\n';}\r\n" +
	                  "    |   {!this.enumKeyword}? 'enum' {document.getElementById('output').value += \"enum\" + '\\n';}\r\n" +
	                  "    ;\r\n" +
	                  "ID : [a-z]+ ;\r\n" +
	                  "WS : [ \\t\\n\\r]+ -> skip ;\r";
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
		String grammar = "grammar T;\r\n" +
	                  "cppCompilationUnit : content+ EOF;\r\n" +
	                  "content: anything | {false}? .;\r\n" +
	                  "anything: ANY_CHAR;\r\n" +
	                  "ANY_CHAR: [_a-zA-Z0-9];\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "cppCompilationUnit", "hello", true);
		assertEquals("", found);
		assertNull(this.stderrDuringParse);
	}

	String testPredFromAltTestedInLoopBack(String input) throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "file_\r\n" +
	                  "@after {document.getElementById('output').value += $ctx.toStringTree(null, this) + '\\n';}\r\n" +
	                  "  : para para EOF ;\r\n" +
	                  "para: paraContent NL NL ;\r\n" +
	                  "paraContent : ('s'|'x'|{this._input.LA(2)!=NL}? NL)+ ;\r\n" +
	                  "NL : '\\n' ;\r\n" +
	                  "s : 's' ;\r\n" +
	                  "X : 'x' ;\r";
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