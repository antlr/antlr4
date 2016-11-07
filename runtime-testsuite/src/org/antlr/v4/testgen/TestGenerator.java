/*
 * [The "BSD license"]
 *  Copyright (c) 2015 Terence Parr
 *  Copyright (c) 2015 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.antlr.v4.testgen;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.StringRenderer;
import org.stringtemplate.v4.gui.STViz;
import org.stringtemplate.v4.misc.ErrorBuffer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestGenerator {
	public final static String[] targets = {
		"Cpp", "Java", "Go", "CSharp", "Python2", "Python3",
		"JavaScript/Node", "JavaScript/Safari", "JavaScript/Firefox",
		"JavaScript/Explorer", "JavaScript/Chrome"
	};

	/** Execute from antlr4 root dir:
	 * *
	 * $ java TestGenerator -o output-root-dir -templates -viz
	 *
	 * Example:
	 *
	 * $ java org.antlr.v4.testgen.TestGenerator -root /Users/parrt/antlr/code/antlr4
	 */
	public static void main(String[] args) {
		String rootDir = null;
		String outDir = null;
		String templatesRoot = null;
		String target = "ALL";
		boolean browsers = false;
		boolean viz = false;

		int i = 0;
		while (args != null && i < args.length) {
			String arg = args[i];
			if (arg.startsWith("-root")) {
				i++;
				rootDir = args[i];
			}
			else if (arg.startsWith("-outdir")) {
				i++;
				outDir = args[i];
			}
			else if (arg.startsWith("-templates")) {
				i++;
				templatesRoot = args[i];
			}
			else if (arg.startsWith("-target")) {
				i++;
				target = args[i];
			}
			else if (arg.startsWith("-browsers")) {
				browsers = true;
			}
			else if (arg.startsWith("-viz")) {
				viz = true;
			}
			i++;
		}

		System.out.println("rootDir = " + rootDir);
		System.out.println("outputDir = " + outDir);
		System.out.println("templates = " + templatesRoot);
		System.out.println("target = " + target);
		System.out.println("browsers = " + browsers);
		System.out.println("viz = " + viz);

		if(rootDir==null) {
			System.out.println("rootDir is mandatory!" + rootDir);
			return;
		}
		if(outDir==null)
			outDir = rootDir + "/test";

		if(templatesRoot==null)
			templatesRoot = rootDir + "/resources/org/antlr/v4/test/runtime/templates";

		if ( "ALL".equalsIgnoreCase(target)) {
			genAllTargets(rootDir, outDir, templatesRoot, browsers, viz);
		}
		else {
			genTarget(rootDir, outDir, target, templatesRoot, viz);
		}
	}

	public static void genAllTargets(String rootDir, String outDirRoot, String templatesRoot, boolean browsers, boolean viz) {
		for(String target : targets) {
			if(!browsers && "JavaScript/Safari".equals(target)) {
				return;
			}
			genTarget(rootDir, outDirRoot, target, templatesRoot, viz);
		}
	}

	public static void genTarget(final String rootDir, final String outDir, final String fullTarget, final String templatesDir, boolean viz) {
		String[] parts = fullTarget.split("/");
		String target = parts[0];
		String subTarget = parts.length>1 ? parts[1] : target;
		String targetPackage = rootDir + "/resources/org/antlr/v4/test/runtime/" + fullTarget.toLowerCase();
		String targetTemplate = targetPackage + "/" + subTarget + ".test.stg";
		TestGenerator gen = new TestGenerator("UTF-8",
					fullTarget,
					rootDir,
					new File(outDir),
					new File(templatesDir),
					new File(targetTemplate),
					viz);
		gen.info("Generating target " + gen.getTargetName());
		gen.execute();
	}

	// This project uses UTF-8, but the plugin might be used in another project
	// which is not. Always load templates with UTF-8, but write using the
	// specified encoding.
	protected final String encoding;
	protected final String targetName;
	protected final String rootDir;
	protected final File outputDir;
	protected final File testTemplates;
	protected final File runtimeTemplate;
	protected final boolean visualize;

	public TestGenerator(String encoding, String targetName, String rootDir, File outputDir, File testTemplates, File runtimeTemplate, boolean visualize) {
		this.encoding = encoding;
		this.targetName = targetName;
		this.rootDir = rootDir;
		this.outputDir = outputDir;
		this.testTemplates = testTemplates;
		this.runtimeTemplate = runtimeTemplate;
		this.visualize = visualize;
	}

	private String getTargetName() {
		return targetName;
	}


	public void execute() {
		STGroup targetGroup = new STGroupFile(runtimeTemplate.getPath());
		targetGroup.registerModelAdaptor(STGroup.class, new STGroupModelAdaptor());
		targetGroup.registerRenderer(String.class, new StringRenderer(), true);
		targetGroup.defineDictionary("escape", new JavaEscapeStringMap());
		targetGroup.defineDictionary("lines", new LinesStringMap());
		targetGroup.defineDictionary("strlen", new StrlenStringMap());
		generateCodeForFoldersInIndex(targetGroup);
	}

	protected void generateCodeForFoldersInIndex(STGroup targetGroup) {
		File targetFolder = getOutputDir(testTemplates+"");
		STGroup index = new STGroupFile(testTemplates+"/Index.stg");
		index.load(); // make sure the index group is loaded since we call rawGetDictionary
		Map<String, Object> folders = index.rawGetDictionary("TestFolders");
		if (folders != null) {
			for (String key : folders.keySet()) {
				final String testDir = testTemplates + "/" + key;
				STGroup testIndex = new STGroupFile(testDir + "/Index.stg");
				testIndex.load();
				Map<String, Object> templateNames = testIndex.rawGetDictionary("TestTemplates");
				if ( templateNames != null && !templateNames.isEmpty() ) {
					final ArrayList<String> sortedTemplateNames = new ArrayList<String>(templateNames.keySet());
					Collections.sort(sortedTemplateNames);
					generateTestFile(testIndex, targetGroup, testDir, sortedTemplateNames, targetFolder);
				}
			}
		}
	}

	protected void generateTestFile(STGroup index,
									STGroup targetGroup,
									String testDir,
									Collection<String> testTemplates,
									File targetFolder)
	{
		ErrorBuffer errors = new ErrorBuffer();
		targetGroup.setListener(errors);

		String testName = testDir.substring(testDir.lastIndexOf('/') + 1);
		File targetFile = new File(targetFolder, "Test" + testName + ".java");
//		System.out.println("Generating file "+targetFile.getAbsolutePath());
		List<ST> templates = new ArrayList<ST>();
		for (String template : testTemplates) {
			STGroup testGroup = new STGroupFile(testDir + "/" + template + STGroup.GROUP_FILE_EXTENSION);
			importLanguageTemplates(testGroup, targetGroup);
			ST testType = testGroup.getInstanceOf("TestType");
			if (testType == null) {
				warn(String.format("Unable to generate tests for %s: no TestType specified.", template));
				continue;
			}

			ST testMethodTemplate = targetGroup.getInstanceOf(testType.render() + "TestMethod");
			if (testMethodTemplate == null) {
				warn(String.format("Unable to generate tests for %s: TestType '%s' is not supported by the current runtime.", template, testType.render()));
				continue;
			}

			testMethodTemplate.add(testMethodTemplate.impl.formalArguments.keySet().iterator().next(), testGroup);
			templates.add(testMethodTemplate);
		}

		ST testFileTemplate = targetGroup.getInstanceOf("TestFile");
		testFileTemplate.addAggr("file.{Options,name,tests}",
		                         index.rawGetDictionary("Options"),
		                         testName,
		                         templates);

		if (visualize) {
			STViz viz = testFileTemplate.inspect();
			try {
				viz.waitForClose();
			}
			catch (InterruptedException ex) { }
		}

		try {
			String output = testFileTemplate.render();
			if ( errors.errors.size()>0 ) {
				System.err.println("errors in "+targetName+": "+errors);
			}
			writeFile(targetFile, output);
		}
		catch (IOException ex) {
			error(String.format("Failed to write output file: %s", targetFile), ex);
		}
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

	public void writeFile(File file, String content) throws IOException {
		file.getParentFile().mkdirs();

		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter osw = new OutputStreamWriter(fos, encoding != null ? encoding : "UTF-8");
		try {
			osw.write(content);
		}
		finally {
			osw.close();
		}
	}

	public File getOutputDir(String templateFolder) {
		if(templateFolder.startsWith(rootDir)) {
			templateFolder = templateFolder.substring(rootDir.length());
		}
		if(templateFolder.startsWith("/resources")) {
			templateFolder = templateFolder.substring("/resources".length());
		}
		templateFolder = templateFolder.substring(0, templateFolder.indexOf("/templates"));
		templateFolder += "/" + targetName.toLowerCase();
		return new File(outputDir, templateFolder);
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
