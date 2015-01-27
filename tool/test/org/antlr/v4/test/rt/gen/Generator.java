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
		return list;
	}

}
