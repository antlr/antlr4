/*
 * Copyright (c) 2012 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.testgen;

import org.antlr.v4.runtime.misc.MultiMap;
import org.antlr.v4.runtime.misc.Utils;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.StringRenderer;
import org.stringtemplate.v4.misc.ErrorBuffer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestGenerator {
	//	public static final String TEST_SRC = "runtime-testsuite/test/org/antlr/v4/test/runtime";
	public static final String TEST_SRC = "/tmp/gen";
	public static final String RESOURCES = "resources/org/antlr/v4/test/runtime";
	public static final String TEMPLATES = RESOURCES+"/templates";
	public static final String JUNIT_TEMPLATES = "JUnitGen.stg";

	public final static String[] Targets = {
		"Cpp",
		"Java",
		"Go",
		"CSharp",
		"Python2", "Python3",
		"Node", "Safari", "Firefox", "Explorer", "Chrome"
	};

	public final static String[] TargetSubDirs = {
		null,
		null,
		null,
		null,
		null, null,
		"javascript", "javascript", "javascript", "javascript", "javascript"
	};

	public final static String[] RequiredDictionariesPerTest = {
		"Grammar"
	};

	public final static String[] RequiredTemplatesPerTest = {
		"TestType", "Input", "Output", "Errors"
	};

	public final static Map<String,MultiMap<String,String>> IgnoredTests =
		new HashMap<String, MultiMap<String, String>>();

	static {
		// at moment, all but js ignore visitor tests
		for (String target : Targets) {
			MultiMap<String, String> m = new MultiMap<String, String>();
			m.map("Visitors", "Basic");
			m.map("Visitors", "LR");
			m.map("Visitors", "LRWithLabels");
			m.map("Visitors", "RuleGetters_1");
			m.map("Visitors", "RuleGetters_2");
			m.map("Visitors", "TokenGetters_1");
			m.map("Visitors", "TokenGetters_2");
			IgnoredTests.put(target, m);
		}
	}

	protected MultiMap<String,String> testGroupToTestList;
	protected File antlrRootDir;
	protected File resourcesDir;
	protected File templatesDir;

	public TestGenerator(File antlrRootDir) {
		this.antlrRootDir = antlrRootDir;
		File runtimeTestDir = new File(antlrRootDir, "runtime-testsuite/testgen");
		resourcesDir = new File(runtimeTestDir, RESOURCES);
		templatesDir = new File(runtimeTestDir, TEMPLATES);
		if ( !resourcesDir.exists() || !resourcesDir.isDirectory() ) {
			throw new IllegalArgumentException(antlrRootDir.getPath()+" is not an antlr4 project directory; missing resources dir");
		}
		if ( !templatesDir.exists() || !templatesDir.isDirectory() ) {
			throw new IllegalArgumentException(antlrRootDir.getPath()+" is not an antlr4 project directory; missing templates dir");
		}
		File junitTemplates = new File(templatesDir, JUNIT_TEMPLATES);
		if ( !junitTemplates.exists() || junitTemplates.isDirectory() ) {
			throw new IllegalArgumentException(antlrRootDir.getPath()+" is not an antlr4 project directory; missing junit templates");
		}
	}

	/** $ java org.antlr.v4.testgen.TestGenerator /Users/parrt/antlr/code/antlr4 */
	public static void main(String[] args) throws IOException {
		verifyArgAndDirectory(args);
		File antlrRootDir = new File(args[0]);
		TestGenerator gen = new TestGenerator(antlrRootDir);
		gen.execute();
	}

	protected void execute() throws IOException {
		testGroupToTestList = buildListOfTests();
//		System.out.println(testGroupToTestList);
		for (int i = 0; i<1/*Targets.length*/; i++) {
			String targetName = Targets[i];
			String targetSubdir = TargetSubDirs[i];
			for (String testGroupName : testGroupToTestList.keySet()) {
				if ( !testGroupName.equals("ParserExec") ) continue;
				String subdir = targetSubdir==null?"":targetSubdir+"/";
				File groupTestSrcDir = getOutputDir(subdir+targetName);
				File groupTestSrcFile = new File(groupTestSrcDir, "Test"+testGroupName+".java");
				File targetTemplatesDir = new File(resourcesDir, subdir+targetName.toLowerCase());
				File targetTemplates = new File(targetTemplatesDir, targetName+".test.stg");
//				generateTestGroupSrcFile(targetName,
//				                         targetSubdir,
//				                         testGroupName,
//				                         targetTemplates,
//				                         groupTestSrcFile,
//				                         testGroupToTestList.get(testGroupName));
				newStuff(targetName,
				         targetSubdir,
				         testGroupName,
				         targetTemplates,
				         groupTestSrcFile,
				         testGroupToTestList.get(testGroupName));
			}
		}
	}

	protected void generateTestGroupSrcFile(String targetName,
	                                        String targetSubdir,
	                                        String testGroupName,
	                                        File targetTemplatesFile,
	                                        File groupTestSrcFile,
	                                        List<String> testTemplateFileNames)
	{
		System.out.println("Generating "+groupTestSrcFile);
//		System.out.println(groupTestSrcFile.getPath());
//		System.out.println(testTemplateFileNames);
//		System.out.println();

		File junitTemplates = new File(templatesDir, JUNIT_TEMPLATES);
		STGroup junit = new STGroupFile(junitTemplates.getPath());
		STGroupFile targetTemplates = new STGroupFile(targetTemplatesFile.getPath());

		ErrorBuffer errors = new ErrorBuffer();
		junit.setListener(errors);

		junit.registerModelAdaptor(STGroup.class, new STGroupModelAdaptor());
		junit.registerRenderer(String.class, new StringRenderer(), true);
		junit.defineDictionary("escape", new JavaEscapeStringMap());
		junit.defineDictionary("lines", new LinesStringMap());
		junit.defineDictionary("strlen", new StrlenStringMap());

		final ArrayList<String> sortedTemplateNames = new ArrayList<String>(testTemplateFileNames);
		Collections.sort(sortedTemplateNames);

		List<ST> testMethodSrcs = new ArrayList<ST>();
		for (String testTemplateFile : sortedTemplateNames) {
			STGroup testInfo = new STGroupFile(testTemplateFile);
			importLanguageTemplates(testInfo, targetTemplates);
			// Base template groups like AmbigLR.stg don't have input/output defined
			// so it's a way to avoid considering them as tests
			if ( !(testInfo.isDefined("Input") && testInfo.isDefined("Output")) ) {
				continue;
			}
			ST testType = testInfo.getInstanceOf("TestType");
			if (testType == null) {
				warn(String.format("Unable to generate tests for %s: no TestType specified.", targetName));
				continue;
			}

			ST testMethodTemplate;

			List<String> ignore = IgnoredTests.get(targetName).get(testGroupName);
			if ( ignore!=null && ignore.contains(testInfo.getName()) ) {
				testMethodTemplate = junit.getInstanceOf("IgnoredTestMethod");
			}
			else {
				testMethodTemplate = junit.getInstanceOf(testType.render()+"TestMethod");
			}
			if (testMethodTemplate == null) {
				warn(String.format("Unable to generate tests for %s: TestType '%s' is not supported by the current runtime.", targetName, testType.render()));
				continue;
			}

			testMethodTemplate.add("test", testInfo);
			testMethodSrcs.add(testMethodTemplate);
		}

		ST testFileTemplate = junit.getInstanceOf("TestFile");
		testFileTemplate.addAggr("file.{target,targetSubdir,name,tests}",
		                         targetName,
		                         targetSubdir,
		                         testGroupName,
		                         testMethodSrcs);

		try {
			String output = testFileTemplate.render();
			if ( errors.errors.size()>0 ) {
				System.err.println("errors in "+targetName+": "+errors);
			}
//			writeFile(new File("/tmp",groupTestSrcFile.getName()), output);
			writeFile(groupTestSrcFile, output);
		}
		catch (IOException ex) {
			error(String.format("Failed to write output file: %s", new File("/tmp",groupTestSrcFile.getName())), ex);
		}
	}

	protected void newStuff(String targetName,
	                        String targetSubdir,
	                        String testGroupName,
	                        File targetTemplatesFile,
	                        File groupTestSrcFile,
	                        List<String> testTemplateFileNames)
	{
		System.out.println("Generating "+groupTestSrcFile);

		String[] templatesToGet =
			{
				"TestType", "Input", "Output", "Errors", "Rule",
				"AfterGrammar"
			};
		String[] dictToGet = {"Grammar", "SlaveGrammars"};

		final ArrayList<String> sortedTemplateNames = new ArrayList<String>(testTemplateFileNames);
		Collections.sort(sortedTemplateNames);

		List<String> testMethodSrcs = new ArrayList<String>();
		for (String testTemplateFile : sortedTemplateNames) {
			if ( !testTemplateFile.endsWith("Optional_1.stg") ) continue;

			STGroup testInfo = new STGroupFile(testTemplateFile);
			testInfo.registerModelAdaptor(STGroup.class, new STGroupModelAdaptor());
			// Base template groups like AmbigLR.stg don't have input/output defined
			// so it's a way to avoid considering them as tests
			if ( !(testInfo.isDefined("Input") && testInfo.isDefined("Output")) ) {
				continue;
			}

			// extract info from group
			final ClassLoader loader = Thread.currentThread().getContextClassLoader();
			final URL stuff = loader.getResource("org/antlr/v4/test/runtime/TestInfo.stg");
			STGroup outputTemplateGroupg = new STGroupFile(stuff.getPath());
			outputTemplateGroupg.load();
			ST st = outputTemplateGroupg.getInstanceOf("descriptor");
			st.add("testName", new File(testTemplateFile).getName().replace(".stg",""));
			Map<String, Object> grammarDict = testInfo.rawGetDictionary("Grammar");

			String grammarName;
			// do grammar stuff
			{
				//"T": {<grammar("T")>}
				Map<String, Object> gdict = getDict(testInfo, "Grammar");
				grammarName = gdict.keySet().iterator().next();
				ST ss = (ST)gdict.get(grammarName);
				String g = ss.render();
				st.add("grammar", g);
			}

			st.add("grammarName", grammarName);
			for (String elName : templatesToGet) {
				System.out.println(elName);
				ST st2 = testInfo.getInstanceOf(elName);
				if ( st2==null ) continue;

				String v = st2.impl.template;
				if ( Utils.count(v, '\n')>0 ) {
					elName = "Long"+elName;
					v = v.replaceAll("<\\\\n>", "");
				}
				else {
					v = v.replaceAll("<\\\\n>", "\\\\n");
				}
				st.add(elName, v);
			}
			String output = st.render();
			System.out.println(output);
			testMethodSrcs.add(output);
		}
	}

	public Map<String,Object> getDict(STGroup group, String dictName) {
		if ( group.isDictionary(dictName) ) {
			return group.rawGetDictionary(dictName);
		}

		for (STGroup importedGroup : group.getImportedGroups()) {
			Map<String,Object> result = getDict(importedGroup, dictName);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	// support

	protected MultiMap<String,String> buildListOfTests() throws IOException {
		MultiMap<String,String> testGroupToTestList = new MultiMap<String, String>();
		List<File> testDirs = getSubdirs(templatesDir);
		for (File testDir : testDirs) {
			List<String> testFilenames = getFilenames(testDir, ".*\\.stg");
			for (String testFilename : testFilenames) {
				if ( !testFilename.endsWith("Index.stg") ) {
					testGroupToTestList.map(testDir.getName(), testFilename);
				}
			}
		}
		return testGroupToTestList;
	}

	protected static File verifyArgAndDirectory(String[] args) {
		if ( args.length==0 ) {
			System.err.println("java org.antlr.v4.testgen.TestGenerator antlr4-project-root-dir");
			System.exit(1);
		}
		String ff = args[0];
		File antlrRootDir = new File(ff);
		if ( !antlrRootDir.exists() ) {
			System.err.println("antlr4-project-root-dir "+ff+" doesn't exist");
			System.exit(1);
		}
		if ( !antlrRootDir.isDirectory() ) {
			System.err.println("antlr4-project-root-dir "+ff+" is not a directory");
			System.exit(1);
		}
		File runtimeTestDir = new File(antlrRootDir, "runtime-testsuite");
		if ( !runtimeTestDir.exists() || !runtimeTestDir.isDirectory() ) {
			System.err.println(ff+" is not an antlr4 project directory; missing runtime-testsuite");
			System.exit(1);
		}
		return antlrRootDir;
	}

	protected boolean testIsComplete(STGroup g) {
		boolean completeTest = true;
		for (String tname : RequiredTemplatesPerTest) {
			if ( !g.isDefined(tname) ) {
				completeTest = false;
			}
		}
		for (String tname : RequiredDictionariesPerTest) {
			if ( !g.isDictionary(tname) ) {
				completeTest = false;
			}
		}
		return completeTest;
	}

	private void importLanguageTemplates(STGroup testGroup, STGroup languageGroup) {
		// make sure the test group is loaded
		testGroup.load();

		if (testGroup == languageGroup) {
			assert false : "Attempted to import the language group into itself.";
			return;
		}

		if (testGroup.getImportedGroups().isEmpty()) {
			testGroup.importTemplates(languageGroup);
			return;
		}

		if (testGroup.getImportedGroups().contains(languageGroup)) {
			return;
		}

		for (STGroup importedGroup : testGroup.getImportedGroups()) {
			importLanguageTemplates(importedGroup, languageGroup);
		}
	}

	public static List<File> getSubdirs(File dir) {
		List<File> dirs = new ArrayList<File>();
		String flist[] = dir.list();
		if ( flist!=null ) {
			for (String fname : flist) {
				File f = new File(dir, fname);
				if ( f.isDirectory() ) {
					dirs.add(f);
				}
			}
		}
		return dirs;
	}

	public static List<String> getFilenames(File f, String inputFilePattern) throws IOException {
		List<String> files = new ArrayList<String>();
		getFilenames_(f, inputFilePattern, files);
		return files;
	}

	public static void getFilenames_(File f, String inputFilePattern, List<String> files) {
		// If this is a directory, walk each file/dir in that directory
		if (f.isDirectory()) {
			String flist[] = f.list();
			for (String aFlist : flist) {
				getFilenames_(new File(f, aFlist), inputFilePattern, files);
			}
		}

		// otherwise, if this is an input file, load it!
		else if ( inputFilePattern==null || f.getName().matches(inputFilePattern) ) {
			files.add(f.getAbsolutePath());
		}
	}

	public File getOutputDir(String targetName) {
		return new File(TEST_SRC, targetName.toLowerCase());
	}

	public void writeFile(File file, String content) throws IOException {
		file.getParentFile().mkdirs();

		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
		try {
			osw.write(content);
		}
		finally {
			osw.close();
		}
	}

	protected void info(String message) {
		System.out.println("INFO: " + message);
	}

	protected void warn(String message) {
		System.err.println("WARNING: " + message);
	}

	protected void error(String message, Throwable throwable) {
		System.err.println("ERROR: " + message);
	}
}
