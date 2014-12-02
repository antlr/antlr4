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
		list.add(buildCompositeLexers());
		list.add(buildCompositeParsers());
		list.add(buildFullContextParsing());
		list.add(buildLeftRecursion());
		list.add(buildLexerErrors());
		list.add(buildLexerExec());
		list.add(buildListeners());
		list.add(buildParserErrors());
		list.add(buildParserExec());
		list.add(buildParseTrees());
		list.add(buildSemPredEvalLexer());
		list.add(buildSemPredEvalParser());
		list.add(buildSets());
		return list;
	}

	private JUnitTestFile buildSets() throws Exception {
		JUnitTestFile file = new JUnitTestFile("Sets");
		// this must return A not I to the parser; calling a nonfragment rule
		// from a nonfragment rule does not set the overall token.
		file.addParserTest(input, "SeqDoesNotBecomeSet", "T", "a",
				"34",
				"34\n",
				null);
		file.addParserTest(input, "ParserSet", "T", "a",
				"x",
				"x\n",
				null);
		file.addParserTest(input, "ParserNotSet", "T", "a",
				"zz",
				"z\n",
				null);
		file.addParserTest(input, "ParserNotToken", "T", "a",
				"zz",
				"zz\n",
				null);
		file.addParserTest(input, "ParserNotTokenWithLabel", "T", "a",
				"zz",
				"z\n",
				null);
		file.addParserTest(input, "RuleAsSet", "T", "a",
				"b",
				"b\n",
				null);
		file.addParserTest(input, "NotChar", "T", "a",
				"x",
				"x\n",
				null);
		file.addParserTest(input, "OptionalSingleElement", "T", "a",
				"bc",
				"bc\n",
				null);
		file.addParserTest(input, "OptionalLexerSingleElement", "T", "a",
				"bc",
				"bc\n",
				null);
		file.addParserTests(input, "StarLexerSingleElement", "T", "a",
				"bbbbc", "bbbbc\n",
				"c", "c\n");
		file.addParserTest(input, "PlusLexerSingleElement", "T", "a",
				"bbbbc",
				"bbbbc\n",
				null);
		file.addParserTest(input, "OptionalSet", "T", "a",
				"ac",
				"ac\n",
				null);
		file.addParserTest(input, "StarSet", "T", "a",
				"abaac",
				"abaac\n",
				null);
		file.addParserTest(input, "PlusSet", "T", "a",
				"abaac",
				"abaac\n",
				null);
		file.addParserTest(input, "LexerOptionalSet", "T", "a",
				"ac",
				"ac\n",
				null);
		file.addParserTest(input, "LexerStarSet", "T", "a",
				"abaac",
				"abaac\n",
				null);
		file.addParserTest(input, "LexerPlusSet", "T", "a",
				"abaac",
				"abaac\n",
				null);
		file.addParserTest(input, "NotCharSet", "T", "a",
				"x",
				"x\n",
				null);
		file.addParserTest(input, "NotCharSetWithLabel", "T", "a",
				"x",
				"x\n",
				null);
		file.addParserTest(input, "NotCharSetWithRuleRef3", "T", "a",
				"x",
				"x\n",
				null);
		file.addParserTest(input, "CharSetLiteral", "T", "a",
				"A a B b",
				"A\n" + "a\n" + "B\n" + "b\n",
				null);
		file.addParserTest(input, "ComplementSet", "T", "parse",
				"a",
				"",
				"line 1:0 token recognition error at: 'a'\n" +
				"line 1:1 missing {} at '<EOF>'\n");
		return file;
	}

	private JUnitTestFile buildSemPredEvalParser() throws Exception {
		JUnitTestFile file = new JUnitTestFile("SemPredEvalParser");
		JUnitTestMethod tm = file.addParserTest(input, "SimpleValidate", "T", "s",
				"x",
				"",
				"line 1:0 no viable alternative at input 'x'\n");
		tm.debug = true;
		tm = file.addParserTest(input, "SimpleValidate2", "T", "s",
				"3 4 x",
				"alt 2\n" + "alt 2\n",
				"line 1:4 no viable alternative at input 'x'\n");
		tm.debug = true;
		file.addParserTest(input, "AtomWithClosureInTranslatedLRRule", "T", "start",
				"a+b+a",
				"",
				null);
		tm = file.addParserTest(input, "ValidateInDFA", "T", "s",
				"x ; y",
				"",
				"line 1:0 no viable alternative at input 'x'\n" +
				"line 1:4 no viable alternative at input 'y'\n");
		tm.debug = true;
		tm = file.addParserTest(input, "Simple", "T", "s",
				"x y 3",
				"alt 2\n" + "alt 2\n" + "alt 3\n",
				null);
		// Under new predicate ordering rules (see antlr/antlr4#29), the first
		// alt with an acceptable config (unpredicated, or predicated and evaluates
		// to true) is chosen.
		tm.debug = true;
		file.addParserTest(input, "Order", "T", "s",
				"x y",
				"alt 1\n" + "alt 1\n",
				null);
		// We have n-2 predicates for n alternatives. pick first alt
		tm = file.addParserTest(input, "2UnpredicatedAlts", "T", "s",
				"x; y",
				"alt 1\n" +
				"alt 1\n",
				"line 1:0 reportAttemptingFullContext d=0 (a), input='x'\n" +
				"line 1:0 reportAmbiguity d=0 (a): ambigAlts={1, 2}, input='x'\n" +
				"line 1:3 reportAttemptingFullContext d=0 (a), input='y'\n" +
				"line 1:3 reportAmbiguity d=0 (a): ambigAlts={1, 2}, input='y'\n");
		tm.debug = true;
		tm = file.addParserTest(input, "2UnpredicatedAltsAndOneOrthogonalAlt", "T", "s",
				"34; x; y",
				"alt 1\n" + "alt 2\n" + "alt 2\n",
				"line 1:4 reportAttemptingFullContext d=0 (a), input='x'\n" +
				"line 1:4 reportAmbiguity d=0 (a): ambigAlts={2, 3}, input='x'\n" +
				"line 1:7 reportAttemptingFullContext d=0 (a), input='y'\n" +
				"line 1:7 reportAmbiguity d=0 (a): ambigAlts={2, 3}, input='y'\n");
		// The parser consumes ID and moves to the 2nd token INT.
		// To properly evaluate the predicates after matching ID INT,
		// we must correctly see come back to starting index so LT(1) works
		tm.debug = true;
		tm = file.addParserTest(input, "RewindBeforePredEval", "T", "s",
				"y 3 x 4",
				"alt 2\n" + "alt 1\n",
				null);
		// checks that we throw exception if all alts
		// are covered with a predicate and none succeeds
		tm.debug = true;
		file.addParserTest(input, "NoTruePredsThrowsNoViableAlt", "T", "s",
				"y 3 x 4",
				"",
				"line 1:0 no viable alternative at input 'y'\n");
		tm = file.addParserTest(input, "ToLeft", "T", "s",
				"x x y",
				"alt 2\n" + "alt 2\n" + "alt 2\n",
				null);
		tm.debug = true;
		tm = file.addParserTest(input, "UnpredicatedPathsInAlt", "T", "s",
				"x 4",
				"alt 1\n",
				null);
		tm.debug = true;
		file.addParserTest(input, "ActionHidesPreds", "T", "s",
				"x x y",
				"alt 1\n" + "alt 1\n" + "alt 1\n",
				null);
		/** In this case, we use predicates that depend on global information
		 *  like we would do for a symbol table. We simply execute
		 *  the predicates assuming that all necessary information is available.
		 *  The i++ action is done outside of the prediction and so it is executed.
		 */
		tm = file.addParserTest(input, "ToLeftWithVaryingPredicate", "T", "s",
				"x x y",
				"i=1\n" + "alt 2\n" + "i=2\n" + "alt 1\n" + "i=3\n" + "alt 2\n",
				null);
		tm.debug = true;
		/**
		 * In this case, we're passing a parameter into a rule that uses that
		 * information to predict the alternatives. This is the special case
		 * where we know exactly which context we are in. The context stack
		 * is empty and we have not dipped into the outer context to make a decision.
		 */
		tm = file.addParserTest(input, "PredicateDependentOnArg", "T", "s",
				"a b",
				"alt 2\n" + "alt 1\n",
				null);
		tm.debug = true;
		/** In this case, we have to ensure that the predicates are not
		 tested during the closure after recognizing the 1st ID. The
		 closure will fall off the end of 'a' 1st time and reach into the
		 a[1] rule invocation. It should not execute predicates because it
		 does not know what the parameter is. The context stack will not
		 be empty and so they should be ignored. It will not affect
		 recognition, however. We are really making sure the ATN
	     simulation doesn't crash with context object issues when it
	     encounters preds during FOLLOW.
	     */
		tm = file.addParserTest(input, "PredicateDependentOnArg2", "T", "s",
				"a b",
				"",
				null);
		tm.debug = true;
		// uses ID ';' or ID '.' lookahead to solve s. preds not tested.
		tm = file.addParserTest(input, "DependentPredNotInOuterCtxShouldBeIgnored", "T", "s",
				"a;",
				"alt 2\n",
				null);
		tm.debug = true;
		tm = file.addParserTest(input, "IndependentPredNotPassedOuterCtxToAvoidCastException", "T", "s",
				"a;",
				"alt 2\n",
				null);
		tm.debug = true;
		/** During a global follow operation, we still collect semantic
	     *  predicates as long as they are not dependent on local context
	     */
		tm = file.addParserTest(input, "PredsInGlobalFOLLOW", "T", "s",
				"a!",
				"eval=true\n" + /* now we are parsing */ "parse\n",
				null);
		tm.debug = true;
		/** We cannot collect predicates that are dependent on local context if
	   	 *  we are doing a global follow. They appear as if they were not there at all.
	   	 */
		tm = file.addParserTest(input, "DepedentPredsInGlobalFOLLOW","T", "s",
				"a!",
				"eval=true\n" + "parse\n",
				null);
		tm.debug = true;
		/** Regular non-forced actions can create side effects used by semantic
		 *  predicates and so we cannot evaluate any semantic predicate
		 *  encountered after having seen a regular action. This includes
		 *  during global follow operations.
		 */
		tm = file.addParserTest(input, "ActionsHidePredsInGlobalFOLLOW", "T", "s",
				"a!",
				"eval=true\n" + "parse\n",
				null);
		tm.debug = true;
		tm = file.addParserTestsWithErrors(input, "PredTestedEvenWhenUnAmbig", "T", "primary",
				"abc", "ID abc\n", null,
				"enum", "", "line 1:0 no viable alternative at input 'enum'\n");
		tm.debug = true;
		/**
		 * This is a regression test for antlr/antlr4#218 "ANTLR4 EOF Related Bug".
		 * https://github.com/antlr/antlr4/issues/218
		 */
		tm = file.addParserTest(input, "DisabledAlternative", "T", "cppCompilationUnit",
				"hello",
				"",
				null);
		tm.debug = true;
		/** Loopback doesn't eval predicate at start of alt */
		tm = file.addParserTestsWithErrors(input, "PredFromAltTestedInLoopBack", "T", "file_",
				"s\n\n\nx\n",
				"(file_ (para (paraContent s) \\n \\n) (para (paraContent \\n x \\n)) <EOF>)\n",
				"line 5:0 mismatched input '<EOF>' expecting '\n'\n",
				"s\n\n\nx\n\n",
				"(file_ (para (paraContent s) \\n \\n) (para (paraContent \\n x) \\n \\n) <EOF>)\n",
				null);
		tm.debug = true;
		return file;
	}

	private JUnitTestFile buildSemPredEvalLexer() throws Exception {
		JUnitTestFile file = new JUnitTestFile("SemPredEvalLexer");
		LexerTestMethod tm = file.addLexerTest(input, "DisableRule", "L",
				"enum abc",
				"[@0,0:3='enum',<2>,1:0]\n" +
				"[@1,5:7='abc',<3>,1:5]\n" +
				"[@2,8:7='<EOF>',<-1>,1:8]\n" +
				"s0-' '->:s5=>4\n" +
				"s0-'a'->:s6=>3\n" +
				"s0-'e'->:s1=>3\n" +
				":s1=>3-'n'->:s2=>3\n" +
				":s2=>3-'u'->:s3=>3\n" +
				":s6=>3-'b'->:s6=>3\n" +
				":s6=>3-'c'->:s6=>3\n",
				null);
		tm.showDFA = true;
		tm = file.addLexerTest(input, "IDvsEnum", "L",
				"enum abc enum",
				"[@0,0:3='enum',<2>,1:0]\n" +
				"[@1,5:7='abc',<2>,1:5]\n" +
				"[@2,9:12='enum',<2>,1:9]\n" +
				"[@3,13:12='<EOF>',<-1>,1:13]\n" +
				"s0-' '->:s5=>3\n" +
				"s0-'a'->:s4=>2\n" +
				"s0-'e'->:s1=>2\n" +
				":s1=>2-'n'->:s2=>2\n" +
				":s2=>2-'u'->:s3=>2\n" +
				":s4=>2-'b'->:s4=>2\n" +
				":s4=>2-'c'->:s4=>2\n", // no 'm'-> transition...conflicts with pred
				null);
		tm.showDFA = true;
		tm = file.addLexerTest(input, "IDnotEnum", "L",
				"enum abc enum",
				"[@0,0:3='enum',<2>,1:0]\n" +
				"[@1,5:7='abc',<2>,1:5]\n" +
				"[@2,9:12='enum',<2>,1:9]\n" +
				"[@3,13:12='<EOF>',<-1>,1:13]\n" +
				"s0-' '->:s2=>3\n", // no edges in DFA for enum/id. all paths lead to pred.
				null);
		tm.showDFA = true;
		tm = file.addLexerTest(input, "EnumNotID", "L",
				"enum abc enum",
				"[@0,0:3='enum',<1>,1:0]\n" +
				"[@1,5:7='abc',<2>,1:5]\n" +
				"[@2,9:12='enum',<1>,1:9]\n" +
				"[@3,13:12='<EOF>',<-1>,1:13]\n" +
				"s0-' '->:s3=>3\n", // no edges in DFA for enum/id. all paths lead to pred.
				null);
		tm.showDFA = true;
		tm = file.addLexerTest(input, "Indent", "L",
				"abc\n  def  \n",
				"INDENT\n" +                        // action output
				"[@0,0:2='abc',<1>,1:0]\n" +		// ID
				"[@1,3:3='\\n',<3>,1:3]\n" +  		// NL
				"[@2,4:5='  ',<2>,2:0]\n" +			// INDENT
				"[@3,6:8='def',<1>,2:2]\n" +		// ID
				"[@4,9:10='  ',<4>,2:5]\n" +		// WS
				"[@5,11:11='\\n',<3>,2:7]\n" +
				"[@6,12:11='<EOF>',<-1>,3:0]\n" +
				"s0-'\n" +
				"'->:s2=>3\n" +
				"s0-'a'->:s1=>1\n" +
				"s0-'d'->:s1=>1\n" +
				":s1=>1-'b'->:s1=>1\n" +
				":s1=>1-'c'->:s1=>1\n" +
				":s1=>1-'e'->:s1=>1\n" +
				":s1=>1-'f'->:s1=>1\n",
				null);
		tm.showDFA = true;
		tm = file.addLexerTest(input, "LexerInputPositionSensitivePredicates", "L",
				"a cde\nabcde\n",
				"a\n" +
				"cde\n" +
				"ab\n" +
				"cde\n" +
				"[@0,0:0='a',<1>,1:0]\n" +
				"[@1,2:4='cde',<2>,1:2]\n" +
				"[@2,6:7='ab',<1>,2:0]\n" +
				"[@3,8:10='cde',<2>,2:2]\n" +
				"[@4,12:11='<EOF>',<-1>,3:0]\n",
				null);
		tm.showDFA = true;
		file.addLexerTest(input, "PredicatedKeywords", "L",
				"enum enu a",
				"enum!\n" +
				"ID enu\n" +
				"ID a\n" +
				"[@0,0:3='enum',<1>,1:0]\n" +
				"[@1,5:7='enu',<2>,1:5]\n" +
				"[@2,9:9='a',<2>,1:9]\n" +
				"[@3,10:9='<EOF>',<-1>,1:10]\n",
				null);
		return file;
	}

	private JUnitTestFile buildParseTrees() throws Exception {
		JUnitTestFile file = new JUnitTestFile("ParseTrees");
		file.addParserTest(input, "TokenAndRuleContextString", "T", "s",
				"x",
				"[a, s]\n(a x)\n",
				null);
		file.addParserTest(input, "Token2", "T", "s",
				"xy",
				"(a x y)\n",
				null);
		file.addParserTest(input, "2Alts", "T", "s",
				"y",
				"(a y)\n",
				null);
		file.addParserTest(input, "2AltLoop", "T", "s",
				"xyyxyxz",
				"(a x y y x y x z)\n",
				null);
		file.addParserTest(input, "RuleRef", "T", "s",
				"yx",
				"(a (b y) x)\n",
				null);
		file.addParserTest(input, "ExtraToken", "T", "s",
				"xzy",
				"(a x z y)\n", // ERRORs not shown. z is colored red in tree view
				"line 1:1 extraneous input 'z' expecting 'y'\n");
		file.addParserTest(input, "NoViableAlt", "T", "s",
				"z",
				"(a z)\n",
				"line 1:0 mismatched input 'z' expecting {'x', 'y'}\n");
		file.addParserTest(input, "Sync", "T", "s",
				"xzyy!",
				"(a x z y y !)\n",
				"line 1:1 extraneous input 'z' expecting {'y', '!'}\n");
		return file;
	}

	private JUnitTestFile buildParserErrors() throws Exception {
		JUnitTestFile file = new JUnitTestFile("ParserErrors");
		file.addParserTest(input, "TokenMismatch", "T", "a",
				"aa",
				"",
				"line 1:1 mismatched input 'a' expecting 'b'\n");
		file.addParserTest(input, "SingleTokenDeletion", "T", "a",
				"aab",
				"",
				"line 1:1 extraneous input 'a' expecting 'b'\n");
		file.addParserTest(input, "SingleTokenDeletionExpectingSet", "T", "a",
				"aab",
				"",
				"line 1:1 extraneous input 'a' expecting {'b', 'c'}\n");
		file.addParserTest(input, "SingleTokenInsertion", "T", "a",
				"ac",
				"",
				"line 1:1 missing 'b' at 'c'\n");
		file.addParserTest(input, "ConjuringUpToken", "T", "a",
				"ac",
				"conjured=[@-1,-1:-1='<missing 'b'>',<2>,1:1]\n",
				"line 1:1 missing 'b' at 'c'\n");
		file.addParserTest(input, "SingleSetInsertion", "T", "a",
				"ad",
				"",
				"line 1:1 missing {'b', 'c'} at 'd'\n");
		file.addParserTest(input, "ConjuringUpTokenFromSet", "T", "a",
				"ad",
				"conjured=[@-1,-1:-1='<missing 'b'>',<2>,1:1]\n",
				"line 1:1 missing {'b', 'c'} at 'd'\n");
		file.addParserTest(input, "LL2", "T", "a",
				"ae",
				"",
				"line 1:1 no viable alternative at input 'ae'\n");
		file.addParserTest(input, "LL3", "T", "a",
				"abe",
				"",
				"line 1:2 no viable alternative at input 'abe'\n");
		file.addParserTest(input, "LLStar", "T", "a",
				"aaae",
				"",
				"line 1:3 no viable alternative at input 'aaae'\n");
		file.addParserTest(input, "SingleTokenDeletionBeforeLoop", "T", "a",
				"aabc",
				"",
				"line 1:1 extraneous input 'a' expecting {<EOF>, 'b'}\n" +
				"line 1:3 token recognition error at: 'c'\n");
		file.addParserTest(input, "MultiTokenDeletionBeforeLoop", "T", "a",
				"aacabc",
				"",
				"line 1:1 extraneous input 'a' expecting {'b', 'c'}\n");
		file.addParserTest(input, "SingleTokenDeletionDuringLoop", "T", "a",
				"ababbc",
				"",
				"line 1:2 extraneous input 'a' expecting {'b', 'c'}\n");
		file.addParserTest(input, "MultiTokenDeletionDuringLoop", "T", "a",
				"abaaababc",
				"",
				"line 1:2 extraneous input 'a' expecting {'b', 'c'}\n" +
				"line 1:6 extraneous input 'a' expecting {'b', 'c'}\n");
		file.addParserTest(input, "SingleTokenDeletionBeforeLoop2", "T", "a",
				"aabc",
				"",
				"line 1:1 extraneous input 'a' expecting {<EOF>, 'b', 'z'}\n" +
				"line 1:3 token recognition error at: 'c'\n");
		file.addParserTest(input, "MultiTokenDeletionBeforeLoop2", "T", "a",
				"aacabc",
				"",
				"line 1:1 extraneous input 'a' expecting {'b', 'z', 'c'}\n");
		file.addParserTest(input, "SingleTokenDeletionDuringLoop2", "T", "a",
				"ababbc",
				"",
				"line 1:2 extraneous input 'a' expecting {'b', 'z', 'c'}\n");
		file.addParserTest(input, "MultiTokenDeletionDuringLoop2", "T", "a",
				"abaaababc",
				"",
				"line 1:2 extraneous input 'a' expecting {'b', 'z', 'c'}\n" +
				"line 1:6 extraneous input 'a' expecting {'b', 'z', 'c'}\n");
		file.addParserTest(input, "LL1ErrorInfo", "T", "start",
				"dog and software",
				"{'hardware', 'software'}\n",
				null);
		file.addParserTest(input, "InvalidEmptyInput", "T", "start",
				"",
				"",
				"line 1:0 missing ID at '<EOF>'\n");
		file.addParserTest(input, "ContextListGetters", "T", "s",
				"abab",
				"abab\n",
				null);
		file.addParserTestsWithErrors(input, "DuplicatedLeftRecursiveCall", "T", "start",
				"xx", "", null,
				"xxx", "", null,
				"xxxx", "", null);
		file.addParserTest(input, "InvalidATNStateRemoval", "T", "start",
				"x:x",
				"",
				null);
		// "a." matches 'a' to rule e but then realizes '.' won't match.
		// previously would cause noviablealt. now prediction pretends to
		// have "a' predict 2nd alt of e. Will get syntax error later so
		// let it get farther.
		file.addParserTest(input, "NoViableAltAvoidance", "T", "s",
				"a.",
				"",
				"line 1:1 mismatched input '.' expecting '!'\n");
		return file;
	}

	private JUnitTestFile buildListeners() throws Exception {
		JUnitTestFile file = new JUnitTestFile("Listeners");
		file.addParserTest(input, "Basic", "T", "s",
				"1 2",
				"(a 1 2)\n" + "1\n" + "2\n",
				null);
		file.addParserTests(input, "TokenGetters", "T", "s",
				"1 2",
				"(a 1 2)\n" +
				"1 2 [1, 2]\n",
				"abc",
				"(a abc)\n" +
				"[@0,0:2='abc',<4>,1:0]\n");
		file.addParserTests(input, "RuleGetters", "T", "s",
				"1 2",
				"(a (b 1) (b 2))\n" +
				"1 2 1\n",
				"abc",
				"(a (b abc))\n" +
				"abc\n");
		file.addParserTest(input, "LR", "T", "s",
				"1+2*3",
				"(e (e 1) + (e (e 2) * (e 3)))\n" +
				"1\n" +
				"2\n" +
				"3\n" +
				"2 3 2\n" +
				"1 2 1\n",
				null);
		file.addParserTest(input, "LRWithLabels", "T", "s",
				"1(2,3)",
				"(e (e 1) ( (eList (e 2) , (e 3)) ))\n" +
				"1\n" + "2\n" + "3\n" + "1 [13 6]\n",
				null);
		return file;
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

	private JUnitTestFile buildCompositeLexers() throws Exception {
		JUnitTestFile file = new JUnitTestFile("CompositeLexers");
		file.addCompositeLexerTest(input, "LexerDelegatorInvokesDelegateRule", "M", "abc",
				"S.A\n" +
				"[@0,0:0='a',<3>,1:0]\n" +
				"[@1,1:1='b',<1>,1:1]\n" +
				"[@2,2:2='c',<4>,1:2]\n" +
				"[@3,3:2='<EOF>',<-1>,1:3]\n", null, "S");
		file.addCompositeLexerTest(input, "LexerDelegatorRuleOverridesDelegate", "M", "ab",
				"M.A\n" +
				"[@0,0:1='ab',<1>,1:0]\n" +
				"[@1,2:1='<EOF>',<-1>,1:2]\n", null, "S");
		return file;
	}

	private JUnitTestFile buildLexerExec() throws Exception {
		JUnitTestFile file = new JUnitTestFile("LexerExec");
		file.addLexerTest(input, "QuoteTranslation", "L", "\"",
				"[@0,0:0='\"',<1>,1:0]\n" +
				"[@1,1:0='<EOF>',<-1>,1:1]\n", null);
		file.addLexerTest(input, "RefToRuleDoesNotSetTokenNorEmitAnother", "L", "34 -21 3",
				"[@0,0:1='34',<2>,1:0]\n" +
				"[@1,3:5='-21',<1>,1:3]\n" +
				"[@2,7:7='3',<2>,1:7]\n" +
				"[@3,8:7='<EOF>',<-1>,1:8]\n", null);
		file.addLexerTest(input, "Slashes", "L", "\\ / \\/ /\\",
				"[@0,0:0='\\',<1>,1:0]\n" +
				"[@1,2:2='/',<2>,1:2]\n" +
				"[@2,4:5='\\/',<3>,1:4]\n" +
				"[@3,7:8='/\\',<4>,1:7]\n" +
				"[@4,9:8='<EOF>',<-1>,1:9]\n", null);
		file.addLexerTest(input, "Parentheses", "L", "-.-.-!",
				"[@0,0:4='-.-.-',<1>,1:0]\n" +
				"[@1,5:5='!',<3>,1:5]\n" +
				"[@2,6:5='<EOF>',<-1>,1:6]\n", null);
		file.addLexerTest(input, "NonGreedyTermination1", "L", "\"hi\"\"mom\"",
				"[@0,0:3='\"hi\"',<1>,1:0]\n" +
				"[@1,4:8='\"mom\"',<1>,1:4]\n" +
				"[@2,9:8='<EOF>',<-1>,1:9]\n", null);
		file.addLexerTest(input, "NonGreedyTermination2", "L", "\"\"\"mom\"",
				"[@0,0:6='\"\"\"mom\"',<1>,1:0]\n" +
				"[@1,7:6='<EOF>',<-1>,1:7]\n", null);
		file.addLexerTest(input, "GreedyOptional", "L", "//blah\n//blah\n",
				"[@0,0:13='//blah\\n//blah\\n',<1>,1:0]\n" +
				"[@1,14:13='<EOF>',<-1>,3:0]\n", null);
		file.addLexerTest(input, "NonGreedyOptional", "L", "//blah\n//blah\n",
				"[@0,0:6='//blah\\n',<1>,1:0]\n" +
				"[@1,7:13='//blah\\n',<1>,2:0]\n" +
				"[@2,14:13='<EOF>',<-1>,3:0]\n", null);
		file.addLexerTest(input, "GreedyClosure", "L", "//blah\n//blah\n",
				"[@0,0:13='//blah\\n//blah\\n',<1>,1:0]\n" +
				"[@1,14:13='<EOF>',<-1>,3:0]\n", null);
		file.addLexerTest(input, "NonGreedyClosure", "L", "//blah\n//blah\n",
				"[@0,0:6='//blah\\n',<1>,1:0]\n" +
				"[@1,7:13='//blah\\n',<1>,2:0]\n" +
				"[@2,14:13='<EOF>',<-1>,3:0]\n", null);
		file.addLexerTest(input, "GreedyPositiveClosure", "L", "//blah\n//blah\n",
				"[@0,0:13='//blah\\n//blah\\n',<1>,1:0]\n" +
				"[@1,14:13='<EOF>',<-1>,3:0]\n", null);
		file.addLexerTest(input, "NonGreedyPositiveClosure", "L", "//blah\n//blah\n",
				"[@0,0:6='//blah\\n',<1>,1:0]\n" +
				"[@1,7:13='//blah\\n',<1>,2:0]\n" +
				"[@2,14:13='<EOF>',<-1>,3:0]\n", null);
		file.addLexerTest(input, "RecursiveLexerRuleRefWithWildcardStar", "L",
				"/* ick */\n" +
				"/* /* */\n" +
				"/* /*nested*/ */\n",
				"[@0,0:8='/* ick */',<1>,1:0]\n" +
				"[@1,9:9='\\n',<2>,1:9]\n" +
				"[@2,10:34='/* /* */\\n/* /*nested*/ */',<1>,2:0]\n" +
				"[@3,35:35='\\n',<2>,3:16]\n" +
				"[@4,36:35='<EOF>',<-1>,4:0]\n", null, 1);
		file.addLexerTest(input, "RecursiveLexerRuleRefWithWildcardStar", "L",
				"/* ick */x\n" +
				"/* /* */x\n" +
				"/* /*nested*/ */x\n",
				"[@0,0:8='/* ick */',<1>,1:0]\n" +
				"[@1,10:10='\\n',<2>,1:10]\n" +
				"[@2,11:36='/* /* */x\\n/* /*nested*/ */',<1>,2:0]\n" +
				"[@3,38:38='\\n',<2>,3:17]\n" +
				"[@4,39:38='<EOF>',<-1>,4:0]\n",
				"line 1:9 token recognition error at: 'x'\n" +
				"line 3:16 token recognition error at: 'x'\n", 2);
		file.addLexerTest(input, "RecursiveLexerRuleRefWithWildcardPlus", "L",
				"/* ick */\n" +
				"/* /* */\n" +
				"/* /*nested*/ */\n",
				"[@0,0:8='/* ick */',<1>,1:0]\n" +
				"[@1,9:9='\\n',<2>,1:9]\n" +
				"[@2,10:34='/* /* */\\n/* /*nested*/ */',<1>,2:0]\n" +
				"[@3,35:35='\\n',<2>,3:16]\n" +
				"[@4,36:35='<EOF>',<-1>,4:0]\n", null, 1);
		file.addLexerTest(input, "RecursiveLexerRuleRefWithWildcardPlus", "L",
				"/* ick */x\n" +
				"/* /* */x\n" +
				"/* /*nested*/ */x\n",
				"[@0,0:8='/* ick */',<1>,1:0]\n" +
				"[@1,10:10='\\n',<2>,1:10]\n" +
				"[@2,11:36='/* /* */x\\n/* /*nested*/ */',<1>,2:0]\n" +
				"[@3,38:38='\\n',<2>,3:17]\n" +
				"[@4,39:38='<EOF>',<-1>,4:0]\n",
				"line 1:9 token recognition error at: 'x'\n" +
				"line 3:16 token recognition error at: 'x'\n", 2);
		file.addLexerTest(input, "ActionPlacement", "L", "ab",
				"stuff0: \n" +
				"stuff1: a\n" +
				"stuff2: ab\n" +
				"ab\n" +
				"[@0,0:1='ab',<1>,1:0]\n" +
				"[@1,2:1='<EOF>',<-1>,1:2]\n", null);
		file.addLexerTest(input, "GreedyConfigs", "L", "ab",
				"ab\n" +
				"[@0,0:1='ab',<1>,1:0]\n" +
				"[@1,2:1='<EOF>',<-1>,1:2]\n", null);
		file.addLexerTest(input, "NonGreedyConfigs", "L", "ab",
				"a\n" +
				"b\n" +
				"[@0,0:0='a',<1>,1:0]\n" +
				"[@1,1:1='b',<3>,1:1]\n" +
				"[@2,2:1='<EOF>',<-1>,1:2]\n", null);
		file.addLexerTest(input, "KeywordID", "L", "end eend ending a",
				"[@0,0:2='end',<1>,1:0]\n" +
				"[@1,3:3=' ',<3>,1:3]\n" +
				"[@2,4:7='eend',<2>,1:4]\n" +
				"[@3,8:8=' ',<3>,1:8]\n" +
				"[@4,9:14='ending',<2>,1:9]\n" +
				"[@5,15:15=' ',<3>,1:15]\n" +
				"[@6,16:16='a',<2>,1:16]\n" +
				"[@7,17:16='<EOF>',<-1>,1:17]\n", null);
		file.addLexerTest(input, "HexVsID", "L", "x 0 1 a.b a.l",
				"[@0,0:0='x',<5>,1:0]\n" +
				"[@1,1:1=' ',<6>,1:1]\n" +
				"[@2,2:2='0',<2>,1:2]\n" +
				"[@3,3:3=' ',<6>,1:3]\n" +
				"[@4,4:4='1',<2>,1:4]\n" +
				"[@5,5:5=' ',<6>,1:5]\n" +
				"[@6,6:6='a',<5>,1:6]\n" +
				"[@7,7:7='.',<4>,1:7]\n" +
				"[@8,8:8='b',<5>,1:8]\n" +
				"[@9,9:9=' ',<6>,1:9]\n" +
				"[@10,10:10='a',<5>,1:10]\n" +
				"[@11,11:11='.',<4>,1:11]\n" +
				"[@12,12:12='l',<5>,1:12]\n" +
				"[@13,13:12='<EOF>',<-1>,1:13]\n",null);
		file.addLexerTest(input, "EOFByItself", "L", "",
				"[@0,0:-1='<EOF>',<1>,1:0]\n" +
				"[@1,0:-1='<EOF>',<-1>,1:0]\n", null);
		file.addLexerTest(input, "EOFSuffixInFirstRule", "L", "",
				"[@0,0:-1='<EOF>',<-1>,1:0]\n", null, 1);
		file.addLexerTest(input, "EOFSuffixInFirstRule", "L", "a",
				"[@0,0:0='a',<1>,1:0]\n" +
				"[@1,1:0='<EOF>',<-1>,1:1]\n", null, 2);
		file.addLexerTest(input, "CharSet", "L", "34\n 34",
				"I\n" +
				"I\n" +
				"[@0,0:1='34',<1>,1:0]\n" +
				"[@1,4:5='34',<1>,2:1]\n" +
				"[@2,6:5='<EOF>',<-1>,2:3]\n", null);
		file.addLexerTest(input, "CharSetPlus", "L", "34\n 34",
				"I\n" +
				"I\n" +
				"[@0,0:1='34',<1>,1:0]\n" +
				"[@1,4:5='34',<1>,2:1]\n" +
				"[@2,6:5='<EOF>',<-1>,2:3]\n", null);
		file.addLexerTest(input, "CharSetNot", "L", "xaf",
				"I\n" +
				"[@0,0:2='xaf',<1>,1:0]\n" +
				"[@1,3:2='<EOF>',<-1>,1:3]\n", null);
		file.addLexerTest(input, "CharSetInSet", "L", "a x",
				"I\n" +
				"I\n" +
				"[@0,0:0='a',<1>,1:0]\n" +
				"[@1,2:2='x',<1>,1:2]\n" +
				"[@2,3:2='<EOF>',<-1>,1:3]\n", null);
		file.addLexerTest(input, "CharSetRange", "L", "34\n 34 a2 abc \n   ",
				"I\n" +
				"I\n" +
				"ID\n" +
				"ID\n" +
				"[@0,0:1='34',<1>,1:0]\n" +
				"[@1,4:5='34',<1>,2:1]\n" +
				"[@2,7:8='a2',<2>,2:4]\n" +
				"[@3,10:12='abc',<2>,2:7]\n" +
				"[@4,18:17='<EOF>',<-1>,3:3]\n", null);
		file.addLexerTest(input, "CharSetWithMissingEndRange", "L", "00\n",
				"I\n" +
				"[@0,0:1='00',<1>,1:0]\n" +
				"[@1,3:2='<EOF>',<-1>,2:0]\n", null);
		file.addLexerTest(input, "CharSetWithMissingEscapeChar", "L", "34 ",
				"I\n" +
				"[@0,0:1='34',<1>,1:0]\n" +
				"[@1,3:2='<EOF>',<-1>,1:3]\n", null);
		file.addLexerTest(input, "CharSetWithEscapedChar", "L", "- ] ",
				"DASHBRACK\n" +
				"DASHBRACK\n" +
				"[@0,0:0='-',<1>,1:0]\n" +
				"[@1,2:2=']',<1>,1:2]\n" +
				"[@2,4:3='<EOF>',<-1>,1:4]\n", null);
		file.addLexerTest(input, "CharSetWithReversedRange", "L", "9",
				"A\n" +
				"[@0,0:0='9',<1>,1:0]\n" +
				"[@1,1:0='<EOF>',<-1>,1:1]\n", null);
		file.addLexerTest(input, "CharSetWithQuote1", "L", "b\"a",
				"A\n" +
				"[@0,0:2='b\"a',<1>,1:0]\n" +
				"[@1,3:2='<EOF>',<-1>,1:3]\n", null);
		file.addLexerTest(input, "CharSetWithQuote2", "L", "b\"\\a",
				"A\n" +
				"[@0,0:3='b\"\\a',<1>,1:0]\n" +
				"[@1,4:3='<EOF>',<-1>,1:4]\n", null);
		final int TOKENS = 4;
		final int LABEL = 5;
		final int IDENTIFIER = 6;
		file.addLexerTest(input, "PositionAdjustingLexer", "PositionAdjustingLexer",
				"tokens\n" +
				"tokens {\n" +
				"notLabel\n" +
				"label1 =\n" +
				"label2 +=\n" +
				"notLabel\n",
				"[@0,0:5='tokens',<" + IDENTIFIER + ">,1:0]\n" +
				"[@1,7:12='tokens',<" + TOKENS + ">,2:0]\n" +
				"[@2,14:14='{',<3>,2:7]\n" +
				"[@3,16:23='notLabel',<" + IDENTIFIER + ">,3:0]\n" +
				"[@4,25:30='label1',<" + LABEL + ">,4:0]\n" +
				"[@5,32:32='=',<1>,4:7]\n" +
				"[@6,34:39='label2',<" + LABEL + ">,5:0]\n" +
				"[@7,41:42='+=',<2>,5:7]\n" +
				"[@8,44:51='notLabel',<" + IDENTIFIER + ">,6:0]\n" +
				"[@9,53:52='<EOF>',<-1>,7:0]\n", null);
		file.addLexerTest(input, "LargeLexer", "L", "KW400",
				"[@0,0:4='KW400',<402>,1:0]\n" +
				"[@1,5:4='<EOF>',<-1>,1:5]\n", null);
		/**
		 * This is a regression test for antlr/antlr4#687 "Empty zero-length tokens
		 * cannot have lexer commands" and antlr/antlr4#688 "Lexer cannot match
		 * zero-length tokens" */
		file.addLexerTest(input, "ZeroLengthToken", "L", "'xxx'",
				"[@0,0:4=''xxx'',<1>,1:0]\n" +
				"[@1,5:4='<EOF>',<-1>,1:5]\n", null);
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
		file.addParserTest(input, "ParserProperty", "T", "a", "abc", "valid\n", null);
		/*CompositeParserTestMethod tm = file.addCompositeParserTest(input, "AlternateQuotes", "ModeTagsParser", "file_", "", "", null, "ModeTagsLexer");
		tm.slaveIsLexer = true;*/
		return file;
	}



}
