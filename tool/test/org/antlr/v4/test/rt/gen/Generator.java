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
		return list;
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

}
