package org.antlr.v4.testgen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		configs.put("Firefox", readFirefoxDir()); // generated Firefox tests
		return configs;
	}
	
	private static File readFirefoxDir() {
		// TODO read from env variable
		return new File("/Users/ericvergnaud/Development/antlr4/antlr/antlr4-javascript/tool/test/org/antlr/v4/js/test/firefox");
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
		return s==null ? null : s.replace("\\","\\\\").replace("\n", "\\n").replace("\"","\\\"");
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
		Collection<TestFile> tests = buildTests();
		for(TestFile test : tests) {
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

	private String generateTestCode(TestFile test) throws Exception {
		test.generateUnitTests(group);
		ST template = group.getInstanceOf("TestFile");
		template.add("file", test);
		return template.render();
	}
	
	private void writeTestFile(TestFile test, String code) throws Exception {
		File file = new File(output, "Test" + test.getName() + ".java");
		OutputStream stream = new FileOutputStream(file);
		try {
			stream.write(code.getBytes());
		} finally {
			stream.close();
		}
	}

	private Collection<TestFile> buildTests() throws Exception {
		List<TestFile> list = new ArrayList<TestFile>();
		list.add(buildLexerExec());
		list.add(buildParserExec());
		list.add(buildCompositeLexers());
		list.add(buildCompositeParsers());
		list.add(buildFullContextParsing());
		return list;
		
	}

	private TestFile buildFullContextParsing() throws Exception {
		TestFile file = new TestFile("FullContextParsing");
		file.addParserTest(input, "AmbigYieldsCtxSensitiveDFA", "T", "s", "abc", 
				"Decision 0:\n" +
				"s0-ID->:s1^=>1\n",
				"line 1:0 reportAttemptingFullContext d=0 (s), input='abc'\n", null);
		file.addParserTest(input, "CtxSensitiveDFA", "T", "s", "$ 34 abc",
				"Decision 1:\n" +
				"s0-INT->s1\n" +
				"s1-ID->:s2^=>1\n",
				"line 1:5 reportAttemptingFullContext d=1 (e), input='34abc'\n" +
				"line 1:2 reportContextSensitivity d=1 (e), input='34'\n", 1);
		file.addParserTest(input, "CtxSensitiveDFA", "T", "s", "@ 34 abc",
				"Decision 1:\n" +
				"s0-INT->s1\n" +
				"s1-ID->:s2^=>1\n",
				"line 1:5 reportAttemptingFullContext d=1 (e), input='34abc'\n" +
				"line 1:5 reportContextSensitivity d=1 (e), input='34abc'\n", 2);
		file.addParserTest(input, "CtxSensitiveDFATwoDiffInput", "T", "s",
				"$ 34 abc @ 34 abc",
				"Decision 2:\n" +
				"s0-INT->s1\n" +
				"s1-ID->:s2^=>1\n",
				"line 1:5 reportAttemptingFullContext d=2 (e), input='34abc'\n" +
				 "line 1:2 reportContextSensitivity d=2 (e), input='34'\n" +
				 "line 1:14 reportAttemptingFullContext d=2 (e), input='34abc'\n" +
				 "line 1:14 reportContextSensitivity d=2 (e), input='34abc'\n", null);
		file.addParserTest(input, "SLLSeesEOFInLLGrammar", "T", "s",
				"34 abc",
				"Decision 0:\n" +
				"s0-INT->s1\n" +
				"s1-ID->:s2^=>1\n",
				"line 1:3 reportAttemptingFullContext d=0 (e), input='34abc'\n" +
				"line 1:0 reportContextSensitivity d=0 (e), input='34'\n", null);
		file.addParserTest(input, "FullContextIF_THEN_ELSEParse", "T", "s",
				"{ if x then return }",
				"Decision 1:\n" +
				"s0-'}'->:s1=>2\n", null, 1);
		file.addParserTest(input, "FullContextIF_THEN_ELSEParse", "T", "s",
				"{ if x then return else foo }",
				"Decision 1:\n" +
				"s0-'else'->:s1^=>1\n",
				"line 1:19 reportAttemptingFullContext d=1 (stat), input='else'\n" +
				"line 1:19 reportContextSensitivity d=1 (stat), input='else'\n", 2);
		file.addParserTest(input, "FullContextIF_THEN_ELSEParse", "T", "s",
				"{ if x then if y then return else foo }",
				"Decision 1:\n" +
				"s0-'else'->:s1^=>1\n" +
				"s0-'}'->:s2=>2\n",
				"line 1:29 reportAttemptingFullContext d=1 (stat), input='else'\n" +
				"line 1:38 reportAmbiguity d=1 (stat): ambigAlts={1, 2}, input='elsefoo}'\n", 3);
		// should not be ambiguous because the second 'else bar' clearly
		// indicates that the first else should match to the innermost if.
		// LL_EXACT_AMBIG_DETECTION makes us keep going to resolve
		file.addParserTest(input, "FullContextIF_THEN_ELSEParse", "T", "s",
				"{ if x then if y then return else foo else bar }",
				"Decision 1:\n" +
				"s0-'else'->:s1^=>1\n",
				"line 1:29 reportAttemptingFullContext d=1 (stat), input='else'\n" +
				 "line 1:38 reportContextSensitivity d=1 (stat), input='elsefooelse'\n" +
				 "line 1:38 reportAttemptingFullContext d=1 (stat), input='else'\n" +
				 "line 1:38 reportContextSensitivity d=1 (stat), input='else'\n", 4);
		file.addParserTest(input, "FullContextIF_THEN_ELSEParse", "T", "s",
				"{ if x then return else foo\n" +
				"if x then if y then return else foo }",
				"Decision 1:\n" +
				"s0-'else'->:s1^=>1\n" +
				"s0-'}'->:s2=>2\n",
				"line 1:19 reportAttemptingFullContext d=1 (stat), input='else'\n" +
				 "line 1:19 reportContextSensitivity d=1 (stat), input='else'\n" +
				 "line 2:27 reportAttemptingFullContext d=1 (stat), input='else'\n" +
				 "line 2:36 reportAmbiguity d=1 (stat): ambigAlts={1, 2}, input='elsefoo}'\n", 5);
		file.addParserTest(input, "FullContextIF_THEN_ELSEParse", "T", "s",
				"{ if x then return else foo\n" +
				"if x then if y then return else foo }",
				"Decision 1:\n" +
				"s0-'else'->:s1^=>1\n" +
				"s0-'}'->:s2=>2\n",
				"line 1:19 reportAttemptingFullContext d=1 (stat), input='else'\n" +
				"line 1:19 reportContextSensitivity d=1 (stat), input='else'\n" +
				"line 2:27 reportAttemptingFullContext d=1 (stat), input='else'\n" +
				"line 2:36 reportAmbiguity d=1 (stat): ambigAlts={1, 2}, input='elsefoo}'\n", 6);
		file.addParserTest(input, "LoopsSimulateTailRecursion", "T", "prog", 
				"a(i)<-x",
				"pass: a(i)<-x\n",
				"line 1:3 reportAttemptingFullContext d=3 (expr_primary), input='a(i)'\n" +
				"line 1:7 reportAmbiguity d=3 (expr_primary): ambigAlts={2, 3}, input='a(i)<-x'\n", null);
		file.addParserTest(input, "AmbiguityNoLoop", "T", "prog", 
				"a@",
				"alt 1\n", 
				"line 1:2 reportAttemptingFullContext d=0 (prog), input='a@'\n" +
				"line 1:2 reportAmbiguity d=0 (prog): ambigAlts={1, 2}, input='a@'\n" +
				"line 1:2 reportAttemptingFullContext d=1 (expr), input='a@'\n" +
				"line 1:2 reportContextSensitivity d=1 (expr), input='a@'\n", null);
		file.addParserTest(input, "ExprAmbiguity", "T", "s", 
				"a+b",
				"(expr a + (expr b))\n",
				"line 1:1 reportAttemptingFullContext d=1 (expr), input='+'\n" +
				"line 1:2 reportContextSensitivity d=1 (expr), input='+b'\n", 1);
		file.addParserTest(input, "ExprAmbiguity", "T", "s", 
				"a+b*c",
				"(expr a + (expr b * (expr c)))\n",
				"line 1:1 reportAttemptingFullContext d=1 (expr), input='+'\n" +
				"line 1:2 reportContextSensitivity d=1 (expr), input='+b'\n" +
				"line 1:3 reportAttemptingFullContext d=1 (expr), input='*'\n" +
				"line 1:5 reportAmbiguity d=1 (expr): ambigAlts={1, 2}, input='*c'\n", 2);
		return file;
	}

	private TestFile buildCompositeLexers() throws Exception {
		TestFile file = new TestFile("CompositeLexers");
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

	private TestFile buildLexerExec() throws Exception {
		TestFile file = new TestFile("LexerExec");
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
		file.addLexerTest(input, "NonGreedyTermination", "L", "\"hi\"\"mom\"", 
				"[@0,0:3='\"hi\"',<1>,1:0]\n" +
				"[@1,4:8='\"mom\"',<1>,1:4]\n" +
				"[@2,9:8='<EOF>',<-1>,1:9]\n", null, 1);
		file.addLexerTest(input, "NonGreedyTermination", "L", "\"\"\"mom\"", 
				"[@0,0:6='\"\"\"mom\"',<1>,1:0]\n" +
				"[@1,7:6='<EOF>',<-1>,1:7]\n", null, 2);
		file.addLexerTest(input, "GreedyOptional", "L", "//blah\n//blah\n", 
				"[@0,0:13='//blah\\n//blah\\n',<1>,1:0]\n" +
				"[@1,14:13='<EOF>',<-1>,3:14]\n", null);
		file.addLexerTest(input, "NonGreedyOptional", "L", "//blah\n//blah\n", 
				"[@0,0:6='//blah\\n',<1>,1:0]\n" +
				"[@1,7:13='//blah\\n',<1>,2:0]\n" +
				"[@2,14:13='<EOF>',<-1>,3:7]\n", null);
		file.addLexerTest(input, "GreedyClosure", "L", "//blah\n//blah\n", 
				"[@0,0:13='//blah\\n//blah\\n',<1>,1:0]\n" +
				"[@1,14:13='<EOF>',<-1>,3:14]\n", null);
		file.addLexerTest(input, "NonGreedyClosure", "L", "//blah\n//blah\n", 
				"[@0,0:6='//blah\\n',<1>,1:0]\n" +
				"[@1,7:13='//blah\\n',<1>,2:0]\n" +
				"[@2,14:13='<EOF>',<-1>,3:7]\n", null);
		file.addLexerTest(input, "GreedyPositiveClosure", "L", "//blah\n//blah\n", 
				"[@0,0:13='//blah\\n//blah\\n',<1>,1:0]\n" +
				"[@1,14:13='<EOF>',<-1>,3:14]\n", null);
		file.addLexerTest(input, "NonGreedyPositiveClosure", "L", "//blah\n//blah\n", 
				"[@0,0:6='//blah\\n',<1>,1:0]\n" +
				"[@1,7:13='//blah\\n',<1>,2:0]\n" +
				"[@2,14:13='<EOF>',<-1>,3:7]\n", null);
		file.addLexerTest(input, "RecursiveLexerRuleRefWithWildcardStar", "L", 
				"/* ick */\n" +
				"/* /* */\n" +
				"/* /*nested*/ */\n", 
				"[@0,0:8='/* ick */',<1>,1:0]\n" +
				"[@1,9:9='\\n',<2>,1:9]\n" +
				"[@2,10:34='/* /* */\\n/* /*nested*/ */',<1>,2:0]\n" +
				"[@3,35:35='\\n',<2>,3:16]\n" +
				"[@4,36:35='<EOF>',<-1>,4:17]\n", null, 1);
		file.addLexerTest(input, "RecursiveLexerRuleRefWithWildcardStar", "L", 
				"/* ick */x\n" +
				"/* /* */x\n" +
				"/* /*nested*/ */x\n",
				"[@0,0:8='/* ick */',<1>,1:0]\n" +
				"[@1,10:10='\\n',<2>,1:10]\n" +
				"[@2,11:36='/* /* */x\\n/* /*nested*/ */',<1>,2:0]\n" +
				"[@3,38:38='\\n',<2>,3:17]\n" +
				"[@4,39:38='<EOF>',<-1>,4:18]\n", 
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
				"[@4,36:35='<EOF>',<-1>,4:17]\n", null, 1);
		file.addLexerTest(input, "RecursiveLexerRuleRefWithWildcardPlus", "L", 
				"/* ick */x\n" +
				"/* /* */x\n" +
				"/* /*nested*/ */x\n", 
				"[@0,0:8='/* ick */',<1>,1:0]\n" +
				"[@1,10:10='\\n',<2>,1:10]\n" +
				"[@2,11:36='/* /* */x\\n/* /*nested*/ */',<1>,2:0]\n" +
				"[@3,38:38='\\n',<2>,3:17]\n" +
				"[@4,39:38='<EOF>',<-1>,4:18]\n", 
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
		file.addLexerTest(input, "NonGreedyConfigs", "L", "qb",
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
		file.addLexerTest(input, "CharSet", "L", "34\r\n 34",
				"I\n" +
				"I\n" +
				"[@0,0:1='34',<1>,1:0]\n" +
				"[@1,5:6='34',<1>,2:1]\n" +
				"[@2,7:6='<EOF>',<-1>,2:3]\n", null);
		file.addLexerTest(input, "CharSetPlus", "L", "34\r\n 34",
				"I\n" +
				"I\n" +
				"[@0,0:1='34',<1>,1:0]\n" +
				"[@1,5:6='34',<1>,2:1]\n" +
				"[@2,7:6='<EOF>',<-1>,2:3]\n", null);
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
		file.addLexerTest(input, "CharSetRange", "L", "34\r 34 a2 abc \n   ",
				"I\n" +
				"I\n" +
				"ID\n" +
				"ID\n" +
				"[@0,0:1='34',<1>,1:0]\n" +
				"[@1,4:5='34',<1>,1:4]\n" +
				"[@2,7:8='a2',<2>,1:7]\n" +
				"[@3,10:12='abc',<2>,1:10]\n" +
				"[@4,18:17='<EOF>',<-1>,2:3]\n", null);
		file.addLexerTest(input, "CharSetWithMissingEndRange", "L", "00\r\n",
				"I\n" +
				"[@0,0:1='00',<1>,1:0]\n" +
				"[@1,4:3='<EOF>',<-1>,2:0]\n", null);
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
		file.addLexerTest(input, "CharSetWithQuote", "L", "b\"a",
				"A\n" +
				"[@0,0:2='b\"a',<1>,1:0]\n" +
				"[@1,3:2='<EOF>',<-1>,1:3]\n", null, 1);
		file.addLexerTest(input, "CharSetWithQuote", "L", "b\"\\a",
				"A\n" +
				"[@0,0:3='b\"\\a',<1>,1:0]\n" +
				"[@1,4:3='<EOF>',<-1>,1:4]\n", null, 2);
		final int TOKENS = 4;
		final int LABEL = 5;
		final int IDENTIFIER = 6;
		file.addLexerTest(input, "PositionAdjustingLexer", "L",
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
		return file;
	}

	private TestFile buildCompositeParsers() throws Exception {
		TestFile file = new TestFile("CompositeParsers");
		file.importErrorQueue = true;
		file.importGrammar = true;
		file.addCompositeParserTest(input, "DelegatorInvokesDelegateRule", "M", "s", "b", "S.a\n", null, "S");
		file.addCompositeParserTest(input, "BringInLiteralsFromDelegate", "M", "s", "=a", "S.a\n", null, "S");
		file.addCompositeParserTest(input, "DelegatorInvokesDelegateRuleWithArgs", "M", "s", "a", "S.a1000\n", null, "S");
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

	private TestFile buildParserExec() throws Exception {
		TestFile file = new TestFile("ParserExec");
		file.addParserTest(input, "Labels", "T", "a", "abc 34", "", null);
		file.addParserTest(input, "ListLabelsOnSet", "T", "a", "abc 34", "", null);
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
		file.addParserTest(input, "AStar", "T", "a", "", "\n", null, 1);
		file.addParserTest(input, "AStar", "T", "a", "a b c", "abc\n", null, 2);
		file.addParserTest(input, "LL1OptionalBlock", "T", "a", "", "\n", null, 1);
		file.addParserTest(input, "LL1OptionalBlock", "T", "a", "a", "a\n", null, 2);
		file.addParserTest(input, "AorAStar", "T", "a", "", "\n", null, 1);
		file.addParserTest(input, "AorAStar", "T", "a", "a b c", "abc\n", null, 2);
		file.addParserTest(input, "AorBPlus", "T", "a", "a 34 c", "a34c\n", null);
		file.addParserTest(input, "AorBStar", "T", "a", "", "\n", null, 1);
		file.addParserTest(input, "AorBStar", "T", "a", "a 34 c", "a34c\n", null, 2);
		file.addParserTest(input, "Optional", "T", "stat", "x", "", null, 1);
		file.addParserTest(input, "Optional", "T", "stat", "if x", "", null, 2);
		file.addParserTest(input, "Optional", "T", "stat", "if x else x", "", null, 3);
		file.addParserTest(input, "Optional", "T", "stat", "if if x else x", "", null, 4);
		file.addParserTest(input, "PredicatedIfIfElse", "T", "s", "if x if x a else b", "", null);
		/* file.addTest(input, "StartRuleWithoutEOF", "T", "s", "abc 34",
				"Decision 0:\n" + "s0-ID->s1\n" + "s1-INT->s2\n" + "s2-EOF->:s3=>1\n", null); */
		file.addParserTest(input, "LabelAliasingAcrossLabeledAlternatives", "T", "start", "xy", "x\ny\n", null);
		file.addParserTest(input, "PredictionIssue334", "T", "file_", "a", "(file_ (item a) <EOF>)\n", null);
		file.addParserTest(input, "ListLabelForClosureContext", "T", "expression", "a", "", null);
		return file;
	}



}
