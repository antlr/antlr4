package org.antlr.v4.test.rt.gen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.test.rt.java.BaseTest;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

public class Generator {

	public static void main(String[] args) throws Exception {
		Map<String, File> configs = readConfigs();
		File source = configs.get("Source");
		for(Map.Entry<String, File> item : configs.entrySet()) {
			if("Source".equals(item.getKey()))
				continue;
			Generator gen = new Generator(item.getKey(), source, item.getValue());
			gen.generateTests();
		}
	}

	private static Map<String, File> readConfigs() throws Exception {
		Map<String, File> configs = new HashMap<String, File>();
		configs.put("Source", readGrammarDir()); // source of test templates
		configs.put("Java", readJavaDir()); // generated Java tests
		configs.put("CSharp", readCSharpDir()); // generated CSharp tests
		configs.put("Python2", readPython2Dir()); // generated Python2 tests
		configs.put("Python3", readPython3Dir()); // generated Python3 tests
		configs.put("NodeJS", readNodeJSDir()); // generated NodeJS tests
		configs.put("Safari", readSafariDir()); // generated Safari tests
		configs.put("Firefox", readFirefoxDir()); // generated Firefox tests
		configs.put("Chrome", readChromeDir()); // generated Chrome tests
		configs.put("Explorer", readExplorerDir()); // generated Explorer tests
		return configs;
	}

	private static File readJavaDir() throws Exception {
		String className = BaseTest.class.getName().replace(".", "/");
		className = className.substring(0, className.lastIndexOf("/") + 1);
		URL url = ClassLoader.getSystemResource(className);
		String uri = url.toURI().toString().replace("target/test-classes", "test");
		return new File(new URI(uri));
	}

	private static File readCSharpDir() {
		return new File("../../antlr4-csharp/tool/test/org/antlr/v4/test/rt/csharp");
	}

	private static File readPython2Dir() {
		return new File("../../antlr4-python2/tool/test/org/antlr/v4/test/rt/py2");
	}

	private static File readPython3Dir() {
		return new File("../../antlr4-python3/tool/test/org/antlr/v4/test/rt/py3");
	}

	private static File readNodeJSDir() {
		return new File("../../antlr4-javascript/tool/test/org/antlr/v4/test/rt/js/node");
	}

	private static File readSafariDir() {
		return new File("../../antlr4-javascript/tool/test/org/antlr/v4/test/rt/js/safari");
	}

	private static File readFirefoxDir() {
		return new File("../../antlr4-javascript/tool/test/org/antlr/v4/test/rt/js/firefox");
	}
	
	private static File readChromeDir() {
		return new File("../../antlr4-javascript/tool/test/org/antlr/v4/test/rt/js/chrome");
	}

	private static File readExplorerDir() {
		return new File("../../antlr4-javascript/tool/test/org/antlr/v4/test/rt/js/explorer");
	}

	private static File readGrammarDir() throws Exception {
		File parent = readThisDir();
		return new File(parent, "grammars");
	}

	private static File readThisDir() throws Exception {
		String className = Generator.class.getName().replace(".", "/");
		className = className.substring(0, className.lastIndexOf("/") + 1);
		URL url = ClassLoader.getSystemResource(className);
		return new File(url.toURI());
	}

	public static String escape(String s) {
		return s==null ? null : s.replace("\\","\\\\").replace("\r", "\\r").replace("\n", "\\n").replace("\"","\\\"");
	}

	String target;
	File input;
	File output;
	STGroup group;

	public Generator(String target, File input, File output) {
		this.target = target;
		this.input = input;
		this.output = output;
	}

	private void generateTests() throws Exception {
		this.group = readTemplates();
		Collection<JUnitTestFile> tests = buildTests();
		for(JUnitTestFile test : tests) {
			String code = generateTestCode(test);
			writeTestFile(test, code);
		}
	}

	private STGroup readTemplates() throws Exception {
		if(!output.exists())
			throw new FileNotFoundException(output.getAbsolutePath());
		String name = target + ".test.stg";
		File file = new File(output, name);
		if(!file.exists())
			throw new FileNotFoundException(file.getAbsolutePath());
		return new STGroupFile(file.getAbsolutePath());
	}

	private String generateTestCode(JUnitTestFile test) throws Exception {
		test.generateUnitTests(group);
		ST template = group.getInstanceOf("TestFile");
		template.add("file", test);
		return template.render();
	}

	private void writeTestFile(JUnitTestFile test, String code) throws Exception {
		File file = new File(output, "Test" + test.getName() + ".java");
		OutputStream stream = new FileOutputStream(file);
		try {
			stream.write(code.getBytes());
		} finally {
			stream.close();
		}
	}

	private Collection<JUnitTestFile> buildTests() throws Exception {
		List<JUnitTestFile> list = new ArrayList<JUnitTestFile>();
		list.add(buildCompositeParsers());
		list.add(buildFullContextParsing());
		list.add(buildLeftRecursion());
		list.add(buildLexerErrors());
		list.add(buildParserExec());
		return list;
	}

	private JUnitTestFile buildLexerErrors() throws Exception {
		JUnitTestFile file = new JUnitTestFile("LexerErrors");
		file.addLexerTest(input, "InvalidCharAtStart", "L",
				"x",
				"[@0,1:0='<EOF>',<-1>,1:1]\n",
				"line 1:0 token recognition error at: 'x'\n");
		file.addLexerTest(input, "StringsEmbeddedInActions", "L",
				"[\"foo\"]",
				"[@0,0:6='[\"foo\"]',<1>,1:0]\n" +
				"[@1,7:6='<EOF>',<-1>,1:7]\n",
				null, 1);
		file.addLexerTest(input, "StringsEmbeddedInActions", "L",
				"[\"foo]",
				"[@0,6:5='<EOF>',<-1>,1:6]\n",
				"line 1:0 token recognition error at: '[\"foo]'\n",
				2);
		file.addLexerTest(input, "EnforcedGreedyNestedBrances", "L",
				"{ { } }",
				"[@0,0:6='{ { } }',<1>,1:0]\n" +
				"[@1,7:6='<EOF>',<-1>,1:7]\n",
				null, 1);
		file.addLexerTest(input, "EnforcedGreedyNestedBrances", "L",
				"{ { }",
				"[@0,5:4='<EOF>',<-1>,1:5]\n",
				"line 1:0 token recognition error at: '{ { }'\n",
				2);
		file.addLexerTest(input, "InvalidCharAtStartAfterDFACache", "L",
				"abx",
				"[@0,0:1='ab',<1>,1:0]\n" +
				"[@1,3:2='<EOF>',<-1>,1:3]\n",
				"line 1:2 token recognition error at: 'x'\n");
		file.addLexerTest(input, "InvalidCharInToken", "L",
				"ax",
				"[@0,2:1='<EOF>',<-1>,1:2]\n",
				"line 1:0 token recognition error at: 'ax'\n");
		file.addLexerTest(input, "InvalidCharInTokenAfterDFACache", "L",
				"abax",
				"[@0,0:1='ab',<1>,1:0]\n" +
				"[@1,4:3='<EOF>',<-1>,1:4]\n",
				"line 1:2 token recognition error at: 'ax'\n");
		// The first ab caches the DFA then abx goes through the DFA but
		// into the ATN for the x, which fails. Must go back into DFA
		// and return to previous dfa accept state
		file.addLexerTest(input, "DFAToATNThatFailsBackToDFA", "L",
				"ababx",
				"[@0,0:1='ab',<1>,1:0]\n" +
				"[@1,2:3='ab',<1>,1:2]\n" +
				"[@2,5:4='<EOF>',<-1>,1:5]\n",
				"line 1:4 token recognition error at: 'x'\n");
		// The first ab caches the DFA then abx goes through the DFA but
		// into the ATN for the c.  It marks that hasn't except state
		// and then keeps going in the ATN. It fails on the x, but
		// uses the previous accepted in the ATN not DFA
		file.addLexerTest(input, "DFAToATNThatMatchesThenFailsInATN", "L",
				"ababcx",
				"[@0,0:1='ab',<1>,1:0]\n" +
				"[@1,2:4='abc',<2>,1:2]\n" +
				"[@2,6:5='<EOF>',<-1>,1:6]\n",
				"line 1:5 token recognition error at: 'x'\n");
		file.addLexerTest(input, "ErrorInMiddle", "L",
				"abx",
				"[@0,3:2='<EOF>',<-1>,1:3]\n",
				"line 1:0 token recognition error at: 'abx'\n");
		LexerTestMethod tm = file.addLexerTest(input, "LexerExecDFA", "L",
				"x : x",
				"[@0,0:0='x',<3>,1:0]\n" +
				"[@1,2:2=':',<1>,1:2]\n" +
				"[@2,4:4='x',<3>,1:4]\n" +
				"[@3,5:4='<EOF>',<-1>,1:5]\n",
				"line 1:1 token recognition error at: ' '\n" +
				"line 1:3 token recognition error at: ' '\n");
		tm.lexerOnly = false;
		return file;
	}

	private JUnitTestFile buildLeftRecursion() throws Exception {
		JUnitTestFile file = new JUnitTestFile("LeftRecursion");
		file.addParserTests(input, "Simple", "T", "s",
				"x", "(s (a x))\n",
				"x y", "(s (a (a x) y))\n",
				"x y z", "(s (a (a (a x) y) z))\n");
		file.addParserTests(input, "DirectCallToLeftRecursiveRule", "T", "a",
				"x", "(a x)\n",
				"x y", "(a (a x) y)\n",
				"x y z", "(a (a (a x) y) z)\n");
		file.addParserTest(input, "SemPred", "T", "s", "x y z",
				"(s (a (a (a x) y) z))\n", null);
		file.addParserTests(input, "TernaryExpr", "T", "s",
				"a",			"(s (e a) <EOF>)\n",
				"a+b",			"(s (e (e a) + (e b)) <EOF>)\n",
				"a*b",			"(s (e (e a) * (e b)) <EOF>)\n",
				"a?b:c",		"(s (e (e a) ? (e b) : (e c)) <EOF>)\n",
				"a=b=c",		"(s (e (e a) = (e (e b) = (e c))) <EOF>)\n",
				"a?b+c:d",		"(s (e (e a) ? (e (e b) + (e c)) : (e d)) <EOF>)\n",
				"a?b=c:d",		"(s (e (e a) ? (e (e b) = (e c)) : (e d)) <EOF>)\n",
				"a? b?c:d : e",	"(s (e (e a) ? (e (e b) ? (e c) : (e d)) : (e e)) <EOF>)\n",
				"a?b: c?d:e",	"(s (e (e a) ? (e b) : (e (e c) ? (e d) : (e e))) <EOF>)\n");
		file.addParserTests(input, "Expressions", "T", "s",
				"a",		"(s (e a) <EOF>)\n",
				"1",		"(s (e 1) <EOF>)\n",
				"a-1",		"(s (e (e a) - (e 1)) <EOF>)\n",
				"a.b",		"(s (e (e a) . b) <EOF>)\n",
				"a.this",	"(s (e (e a) . this) <EOF>)\n",
				"-a",		"(s (e - (e a)) <EOF>)\n",
				"-a+b",		"(s (e (e - (e a)) + (e b)) <EOF>)\n");
		file.addParserTests(input, "JavaExpressions", "T", "s",
				"a|b&c",	"(s (e (e a) | (e (e b) & (e c))) <EOF>)\n",
				"(a|b)&c",	"(s (e (e ( (e (e a) | (e b)) )) & (e c)) <EOF>)\n",
	            "a > b",	"(s (e (e a) > (e b)) <EOF>)\n",
				"a >> b",	"(s (e (e a) >> (e b)) <EOF>)\n",
				"a=b=c",	"(s (e (e a) = (e (e b) = (e c))) <EOF>)\n",
				"a^b^c",	"(s (e (e a) ^ (e (e b) ^ (e c))) <EOF>)\n",
				"(T)x",							"(s (e ( (type_ T) ) (e x)) <EOF>)\n",
				"new A().b",					"(s (e (e new (type_ A) ( )) . b) <EOF>)\n",
				"(T)t.f()",						"(s (e (e ( (type_ T) ) (e (e t) . f)) ( )) <EOF>)\n",
				"a.f(x)==T.c",					"(s (e (e (e (e a) . f) ( (expressionList (e x)) )) == (e (e T) . c)) <EOF>)\n",
				"a.f().g(x,1)",					"(s (e (e (e (e (e a) . f) ( )) . g) ( (expressionList (e x) , (e 1)) )) <EOF>)\n",
				"new T[((n-1) * x) + 1]",		"(s (e new (type_ T) [ (e (e ( (e (e ( (e (e n) - (e 1)) )) * (e x)) )) + (e 1)) ]) <EOF>)\n");
		file.addParserTests(input, "Declarations", "T", "s",
				"a",		"(s (declarator a) <EOF>)\n",
				"*a",		"(s (declarator * (declarator a)) <EOF>)\n",
				"**a",		"(s (declarator * (declarator * (declarator a))) <EOF>)\n",
				"a[3]",		"(s (declarator (declarator a) [ (e 3) ]) <EOF>)\n",
				"b[]",		"(s (declarator (declarator b) [ ]) <EOF>)\n",
				"(a)",		"(s (declarator ( (declarator a) )) <EOF>)\n",
				"a[]()",	"(s (declarator (declarator (declarator a) [ ]) ( )) <EOF>)\n",
				"a[][]",	"(s (declarator (declarator (declarator a) [ ]) [ ]) <EOF>)\n",
				"*a[]",		"(s (declarator * (declarator (declarator a) [ ])) <EOF>)\n",
				"(*a)[]",	"(s (declarator (declarator ( (declarator * (declarator a)) )) [ ]) <EOF>)\n");
		file.addParserTests(input, "ReturnValueAndActions", "T", "s",
				"4",		"4\n",
				"1+2",		"3\n",
				"1+2*3",	"7\n",
				"(1+2)*3",	"9\n");
		file.addParserTests(input, "LabelsOnOpSubrule", "T", "s",
				"4",		"(s (e 4))\n",
				"1*2/3",	"(s (e (e (e 1) * (e 2)) / (e 3)))\n",
				"(1/2)*3",	"(s (e (e ( (e (e 1) / (e 2)) )) * (e 3)))\n");
		file.addParserTests(input, "ReturnValueAndActionsAndLabels", "T", "s",
				"4",			"4\n",
				"1+2",			"3\n",
				"1+2*3",		"7\n",
				"i++*3",		"12\n");
		/**
		 * This is a regression test for antlr/antlr4#433 "Not all context accessor
		 * methods are generated when an alternative rule label is used for multiple
		 * alternatives".
		 * https://github.com/antlr/antlr4/issues/433
		 */
		file.addParserTests(input, "MultipleAlternativesWithCommonLabel", "T", "s",
				"4",			"4\n",
				"1+2",			"3\n",
				"1+2*3",		"7\n",
				"i++*3",		"12\n");
		file.addParserTests(input, "PrefixOpWithActionAndLabel", "T", "s",
				"a",			"a\n",
				"a+b",			"(a+b)\n",
				"a=b+c",		"((a=b)+c)\n");
		file.addParserTests(input, "AmbigLR", "Expr", "prog",
				"1\n", "",
				"a = 5\n", "",
				"b = 6\n", "",
				"a+b*2\n", "",
				"(1+2)*3\n", "");
		/**
		 * This is a regression test for #239 "recoursive parser using implicit
		 * tokens ignore white space lexer rule".
		 * https://github.com/antlr/antlr4/issues/239
		 */
		file.addParserTests(input, "WhitespaceInfluence", "Expr", "prog",
				"Test(1,3)", "",
				"Test(1, 3)", "");
		/**
		 * This is a regression test for antlr/antlr4#509 "Incorrect rule chosen in
		 * unambiguous grammar".
		 * https://github.com/antlr/antlr4/issues/509
		 */
		file.addParserTest(input, "PrecedenceFilterConsidersContext", "T", "prog",
				"aa",
				"(prog (statement (letterA a)) (statement (letterA a)) <EOF>)\n", null);
		/**
		 * This is a regression test for antlr/antlr4#625 "Duplicate action breaks
		 * operator precedence"
		 * https://github.com/antlr/antlr4/issues/625
		 */
		file.addParserTests(input, "MultipleActions", "T", "s",
				"4", "(s (e 4))\n",
				"1*2/3", "(s (e (e (e 1) * (e 2)) / (e 3)))\n",
				"(1/2)*3", "(s (e (e ( (e (e 1) / (e 2)) )) * (e 3)))\n");
		/**
		 * This is a regression test for antlr/antlr4#625 "Duplicate action breaks
		 * operator precedence"
		 * https://github.com/antlr/antlr4/issues/625
		 */
		file.addParserTests(input, "MultipleActionsPredicatesOptions", "T", "s",
				"4", "(s (e 4))\n",
				"1*2/3", "(s (e (e (e 1) * (e 2)) / (e 3)))\n",
				"(1/2)*3", "(s (e (e ( (e (e 1) / (e 2)) )) * (e 3)))\n");
		file.addParserTest(input, "SemPredFailOption", "T", "s",
				"x y z",
				"(s (a (a x) y z))\n",
				"line 1:4 rule a custom message\n");
		/**
		 * This is a regression test for antlr/antlr4#542 "First alternative cannot
		 * be right-associative".
		 * https://github.com/antlr/antlr4/issues/542
		 */
		file.addParserTests(input, "TernaryExprExplicitAssociativity", "T", "s",
				"a",			"(s (e a) <EOF>)\n",
				"a+b",			"(s (e (e a) + (e b)) <EOF>)\n",
				"a*b",			"(s (e (e a) * (e b)) <EOF>)\n",
				"a?b:c",		"(s (e (e a) ? (e b) : (e c)) <EOF>)\n",
				"a=b=c",		"(s (e (e a) = (e (e b) = (e c))) <EOF>)\n",
				"a?b+c:d",		"(s (e (e a) ? (e (e b) + (e c)) : (e d)) <EOF>)\n",
				"a?b=c:d",		"(s (e (e a) ? (e (e b) = (e c)) : (e d)) <EOF>)\n",
				"a? b?c:d : e",	"(s (e (e a) ? (e (e b) ? (e c) : (e d)) : (e e)) <EOF>)\n",
				"a?b: c?d:e",	"(s (e (e a) ? (e b) : (e (e c) ? (e d) : (e e))) <EOF>)\n");
		/**
		 * This is a regression test for antlr/antlr4#677 "labels not working in
		 * grammar file".
		 * https://github.com/antlr/antlr4/issues/677
		 *
		 * <p>This test treats {@code ,} and {@code >>} as part of a single compound
		 * operator (similar to a ternary operator).</p>
		 */
		file.addParserTests(input, "ReturnValueAndActionsList1", "T", "s",
				"a*b",			"(s (expr (expr a) * (expr b)) <EOF>)\n",
				"a,c>>x",		"(s (expr (expr a) , (expr c) >> (expr x)) <EOF>)\n",
				"x",			"(s (expr x) <EOF>)\n",
				"a*b,c,x*y>>r",	"(s (expr (expr (expr a) * (expr b)) , (expr c) , (expr (expr x) * (expr y)) >> (expr r)) <EOF>)\n");

		/**
		 * This is a regression test for antlr/antlr4#677 "labels not working in
		 * grammar file".
		 * https://github.com/antlr/antlr4/issues/677
		 *
		 * <p>This test treats the {@code ,} and {@code >>} operators separately.</p>
		 */
		file.addParserTests(input, "ReturnValueAndActionsList2", "T", "s",
				"a*b",			"(s (expr (expr a) * (expr b)) <EOF>)\n",
				"a,c>>x",		"(s (expr (expr (expr a) , (expr c)) >> (expr x)) <EOF>)\n",
				"x",			"(s (expr x) <EOF>)\n",
				"a*b,c,x*y>>r",	"(s (expr (expr (expr (expr (expr a) * (expr b)) , (expr c)) , (expr (expr x) * (expr y))) >> (expr r)) <EOF>)\n");
		return file;
	}

	private JUnitTestFile buildFullContextParsing() throws Exception {
		JUnitTestFile file = new JUnitTestFile("FullContextParsing");
		JUnitTestMethod tm = file.addParserTest(input, "AmbigYieldsCtxSensitiveDFA", "T", "s", "abc",
				"Decision 0:\n" +
				"s0-ID->:s1^=>1\n",
				"line 1:0 reportAttemptingFullContext d=0 (s), input='abc'\n");
		tm.debug = true;
		tm = file.addParserTestsWithErrors(input, "CtxSensitiveDFA", "T", "s",
				"$ 34 abc",
				"Decision 1:\n" +
				"s0-INT->s1\n" +
				"s1-ID->:s2^=>1\n",
				"line 1:5 reportAttemptingFullContext d=1 (e), input='34abc'\n" +
				"line 1:2 reportContextSensitivity d=1 (e), input='34'\n",
				"@ 34 abc",
				"Decision 1:\n" +
				"s0-INT->s1\n" +
				"s1-ID->:s2^=>1\n",
				"line 1:5 reportAttemptingFullContext d=1 (e), input='34abc'\n" +
				"line 1:5 reportContextSensitivity d=1 (e), input='34abc'\n");
		tm.debug = true;
		tm = file.addParserTest(input, "CtxSensitiveDFATwoDiffInput", "T", "s",
				"$ 34 abc @ 34 abc",
				"Decision 2:\n" +
				"s0-INT->s1\n" +
				"s1-ID->:s2^=>1\n",
				"line 1:5 reportAttemptingFullContext d=2 (e), input='34abc'\n" +
				 "line 1:2 reportContextSensitivity d=2 (e), input='34'\n" +
				 "line 1:14 reportAttemptingFullContext d=2 (e), input='34abc'\n" +
				 "line 1:14 reportContextSensitivity d=2 (e), input='34abc'\n");
		tm.debug = true;
		tm = file.addParserTest(input, "SLLSeesEOFInLLGrammar", "T", "s",
				"34 abc",
				"Decision 0:\n" +
				"s0-INT->s1\n" +
				"s1-ID->:s2^=>1\n",
				"line 1:3 reportAttemptingFullContext d=0 (e), input='34abc'\n" +
				"line 1:0 reportContextSensitivity d=0 (e), input='34'\n");
		tm.debug = true;
		tm = file.addParserTestsWithErrors(input, "FullContextIF_THEN_ELSEParse", "T", "s",
			"{ if x then return }",
				"Decision 1:\n" +
				"s0-'}'->:s1=>2\n",
					null,
			"{ if x then return else foo }",
				"Decision 1:\n" +
				"s0-'else'->:s1^=>1\n",
					"line 1:19 reportAttemptingFullContext d=1 (stat), input='else'\n" +
					"line 1:19 reportContextSensitivity d=1 (stat), input='else'\n",
			"{ if x then if y then return else foo }",
				"Decision 1:\n" +
				"s0-'}'->:s2=>2\n" +
				"s0-'else'->:s1^=>1\n",
					"line 1:29 reportAttemptingFullContext d=1 (stat), input='else'\n" +
					"line 1:38 reportAmbiguity d=1 (stat): ambigAlts={1, 2}, input='elsefoo}'\n",
			// should not be ambiguous because the second 'else bar' clearly
			// indicates that the first else should match to the innermost if.
			// LL_EXACT_AMBIG_DETECTION makes us keep going to resolve
			"{ if x then if y then return else foo else bar }",
				"Decision 1:\n" +
				"s0-'else'->:s1^=>1\n",
					"line 1:29 reportAttemptingFullContext d=1 (stat), input='else'\n" +
					"line 1:38 reportContextSensitivity d=1 (stat), input='elsefooelse'\n" +
					"line 1:38 reportAttemptingFullContext d=1 (stat), input='else'\n" +
					"line 1:38 reportContextSensitivity d=1 (stat), input='else'\n",
			"{ if x then return else foo\n" +
				"if x then if y then return else foo }",
				"Decision 1:\n" +
				"s0-'}'->:s2=>2\n" +
				"s0-'else'->:s1^=>1\n",
					"line 1:19 reportAttemptingFullContext d=1 (stat), input='else'\n" +
					"line 1:19 reportContextSensitivity d=1 (stat), input='else'\n" +
					"line 2:27 reportAttemptingFullContext d=1 (stat), input='else'\n" +
					"line 2:36 reportAmbiguity d=1 (stat): ambigAlts={1, 2}, input='elsefoo}'\n",
			"{ if x then return else foo\n" +
				"if x then if y then return else foo }",
				"Decision 1:\n" +
				"s0-'}'->:s2=>2\n" +
				"s0-'else'->:s1^=>1\n",
					"line 1:19 reportAttemptingFullContext d=1 (stat), input='else'\n" +
					"line 1:19 reportContextSensitivity d=1 (stat), input='else'\n" +
					"line 2:27 reportAttemptingFullContext d=1 (stat), input='else'\n" +
					"line 2:36 reportAmbiguity d=1 (stat): ambigAlts={1, 2}, input='elsefoo}'\n");
		tm.debug = true;
		tm = file.addParserTest(input, "LoopsSimulateTailRecursion", "T", "prog",
				"a(i)<-x",
				"pass: a(i)<-x\n",
				"line 1:3 reportAttemptingFullContext d=3 (expr_primary), input='a(i)'\n" +
				"line 1:7 reportAmbiguity d=3 (expr_primary): ambigAlts={2, 3}, input='a(i)<-x'\n");
		tm.debug = true;
		tm = file.addParserTest(input, "AmbiguityNoLoop", "T", "prog",
				"a@",
				"alt 1\n",
				"line 1:2 reportAttemptingFullContext d=0 (prog), input='a@'\n" +
				"line 1:2 reportAmbiguity d=0 (prog): ambigAlts={1, 2}, input='a@'\n" +
				"line 1:2 reportAttemptingFullContext d=1 (expr), input='a@'\n" +
				"line 1:2 reportContextSensitivity d=1 (expr), input='a@'\n");
		tm.debug = true;
		tm = file.addParserTestsWithErrors(input, "ExprAmbiguity", "T", "s",
			"a+b",
				"(expr a + (expr b))\n",
					"line 1:1 reportAttemptingFullContext d=1 (expr), input='+'\n" +
					"line 1:2 reportContextSensitivity d=1 (expr), input='+b'\n",
			"a+b*c",
				"(expr a + (expr b * (expr c)))\n",
					"line 1:1 reportAttemptingFullContext d=1 (expr), input='+'\n" +
					"line 1:2 reportContextSensitivity d=1 (expr), input='+b'\n" +
					"line 1:3 reportAttemptingFullContext d=1 (expr), input='*'\n" +
					"line 1:5 reportAmbiguity d=1 (expr): ambigAlts={1, 2}, input='*c'\n");
		tm.debug = true;
		return file;
	}

	private JUnitTestFile buildCompositeParsers() throws Exception {
		JUnitTestFile file = new JUnitTestFile("CompositeParsers");
		file.importErrorQueue = true;
		file.importGrammar = true;
		file.addCompositeParserTest(input, "DelegatorInvokesDelegateRule", "M", "s", "b", "S.a\n", null, "S");
		file.addCompositeParserTest(input, "BringInLiteralsFromDelegate", "M", "s", "=a", "S.a\n", null, "S");
		file.addCompositeParserTest(input, "DelegatorInvokesDelegateRuleWithArgs", "M", "s", "b", "S.a1000\n", null, "S");
		file.addCompositeParserTest(input, "DelegatorInvokesDelegateRuleWithReturnStruct", "M", "s", "b", "S.ab\n", null, "S");
		file.addCompositeParserTest(input, "DelegatorAccessesDelegateMembers", "M", "s", "b", "foo\n", null, "S");
		file.addCompositeParserTest(input, "DelegatorInvokesFirstVersionOfDelegateRule", "M", "s", "b", "S.a\n", null, "S", "T");
		CompositeParserTestMethod ct = file.addCompositeParserTest(input, "DelegatesSeeSameTokenType", "M", "s", "aa", "S.x\nT.y\n", null, "S", "T");
		ct.afterGrammar = "writeFile(tmpdir, \"M.g4\", grammar);\n" +
			"ErrorQueue equeue = new ErrorQueue();\n" +
			"Grammar g = new Grammar(tmpdir+\"/M.g4\", grammar, equeue);\n" +
			"String expectedTokenIDToTypeMap = \"{EOF=-1, B=1, A=2, C=3, WS=4}\";\n" +
			"String expectedStringLiteralToTypeMap = \"{'a'=2, 'b'=1, 'c'=3}\";\n" +
			"String expectedTypeToTokenList = \"[B, A, C, WS]\";\n" +
			"assertEquals(expectedTokenIDToTypeMap, g.tokenNameToTypeMap.toString());\n" +
			"assertEquals(expectedStringLiteralToTypeMap, sort(g.stringLiteralToTypeMap).toString());\n" +
			"assertEquals(expectedTypeToTokenList, realElements(g.typeToTokenList).toString());\n" +
			"assertEquals(\"unexpected errors: \"+equeue, 0, equeue.errors.size());\n";
		ct = file.addCompositeParserTest(input, "CombinedImportsCombined", "M", "s", "x 34 9", "S.x\n", null, "S");
		ct.afterGrammar = "writeFile(tmpdir, \"M.g4\", grammar);\n" +
				"ErrorQueue equeue = new ErrorQueue();\n" +
				"new Grammar(tmpdir+\"/M.g4\", grammar, equeue);\n" +
				"assertEquals(\"unexpected errors: \" + equeue, 0, equeue.errors.size());\n";
		file.addCompositeParserTest(input, "DelegatorRuleOverridesDelegate", "M", "a", "c", "S.a\n", null, "S");
		file.addCompositeParserTest(input, "DelegatorRuleOverridesLookaheadInDelegate", "M", "prog", "float x = 3;", "Decl: floatx=3;\n", null, "S");
		file.addCompositeParserTest(input, "DelegatorRuleOverridesDelegates", "M", "a", "c", "M.b\nS.a\n", null, "S", "T");
		file.addCompositeParserTest(input, "KeywordVSIDOrder", "M", "a", "abc",
				"M.A\n" +
				"M.a: [@0,0:2='abc',<1>,1:0]\n", null, "S");
		file.addCompositeParserTest(input, "ImportedRuleWithAction", "M", "s", "b", "", null, "S");
		file.addCompositeParserTest(input, "ImportedGrammarWithEmptyOptions", "M", "s", "b", "", null, "S");
		file.addCompositeParserTest(input, "ImportLexerWithOnlyFragmentRules", "M", "program", "test test", "", null, "S");
		return file;
	}

	private JUnitTestFile buildParserExec() throws Exception {
		JUnitTestFile file = new JUnitTestFile("ParserExec");
		file.addParserTest(input, "Labels", "T", "a", "abc 34;", "", null);
		file.addParserTest(input, "ListLabelsOnSet", "T", "a", "abc 34;", "", null);
		file.addParserTest(input, "AorB", "T", "a", "34", "alt 2\n", null);
		file.addParserTest(input, "Basic", "T", "a", "abc 34", "abc34\n", null);
		file.addParserTest(input, "APlus", "T", "a", "a b c", "abc\n", null);
		file.addParserTest(input, "AorAPlus", "T", "a", "a b c", "abc\n", null);
		file.addParserTest(input, "IfIfElseGreedyBinding1", "T", "start",
				"if y if y x else x", "if y x else x\nif y if y x else x\n", null);
		file.addParserTest(input, "IfIfElseGreedyBinding2", "T", "start",
				"if y if y x else x", "if y x else x\nif y if y x else x\n", null);
		file.addParserTest(input, "IfIfElseNonGreedyBinding1", "T", "start",
				"if y if y x else x", "if y x\nif y if y x else x\n", null);
		file.addParserTest(input, "IfIfElseNonGreedyBinding2", "T", "start",
				"if y if y x else x", "if y x\nif y if y x else x\n", null);
		file.addParserTests(input, "AStar", "T", "a",
				"", "\n",
				"a b c", "abc\n");
		file.addParserTests(input, "LL1OptionalBlock", "T", "a",
				"", "\n",
				"a", "a\n");
		file.addParserTests(input, "AorAStar", "T", "a",
				"", "\n",
				"a b c", "abc\n");
		file.addParserTest(input, "AorBPlus", "T", "a", "a 34 c", "a34c\n", null);
		file.addParserTests(input, "AorBStar", "T", "a",
				"", "\n",
				"a 34 c", "a34c\n");
		file.addParserTests(input, "Optional", "T", "stat",
				"x", "",
				"if x", "",
				"if x else x", "",
				"if if x else x", "");
		file.addParserTest(input, "PredicatedIfIfElse", "T", "s", "if x if x a else b", "", null);
		/* file.addTest(input, "StartRuleWithoutEOF", "T", "s", "abc 34",
				"Decision 0:\n" + "s0-ID->s1\n" + "s1-INT->s2\n" + "s2-EOF->:s3=>1\n", null); */
		file.addParserTest(input, "LabelAliasingAcrossLabeledAlternatives", "T", "start", "xy", "x\ny\n", null);
		file.addParserTest(input, "PredictionIssue334", "T", "file_", "a", "(file_ (item a) <EOF>)\n", null);
		file.addParserTest(input, "ListLabelForClosureContext", "T", "expression", "a", "", null);
		/**
		 * This test ensures that {@link ParserATNSimulator} produces a correct
		 * result when the grammar contains multiple explicit references to
		 * {@code EOF} inside of parser rules.
		 */
		file.addParserTest(input, "MultipleEOFHandling", "T", "prog", "x", "", null);
		/**
		 * This test ensures that {@link ParserATNSimulator} does not produce a
		 * {@link StackOverflowError} when it encounters an {@code EOF} transition
		 * inside a closure.
		 */
		file.addParserTest(input, "EOFInClosure", "T", "prog", "x", "", null);
		/**
		 * This is a regression test for antlr/antlr4#561 "Issue with parser
		 * generation in 4.2.2"
		 * https://github.com/antlr/antlr4/issues/561
		 */
		file.addParserTests(input, "ReferenceToATN", "T", "a",
			"", "\n",
			"a 34 c", "a34c\n");
		/*CompositeParserTestMethod tm = file.addCompositeParserTest(input, "AlternateQuotes", "ModeTagsParser", "file_", "", "", null, "ModeTagsLexer");
		tm.slaveIsLexer = true;*/
		return file;
	}



}
