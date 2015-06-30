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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.gui.STViz;

public class TestGenerator {
	public final static String[] targets = {"CSharp", "Java", "Python2", "Python3", "JavaScript"};

	// This project uses UTF-8, but the plugin might be used in another project
	// which is not. Always load templates with UTF-8, but write using the
	// specified encoding.
	protected final String encoding;

	protected final File runtimeTemplates;

	protected final File outputDirectory;

	protected final boolean visualize;

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
		String targetSpecificTemplateFile = null;
		boolean viz = false;

		int i = 0;
		while (args != null && i < args.length) {
			String arg = args[i];
			if (arg.startsWith("-root")) {
				i++;
				rootDir = args[i];
			}
			else if (arg.startsWith("-o")) {
				i++;
				outDir = args[i];
			}
			else if (arg.startsWith("-templates")) {
				i++;
				targetSpecificTemplateFile = args[i];
				templatesRoot = targetSpecificTemplateFile;
			}
			else if (arg.startsWith("-viz")) {
				viz = true;
			}
			i++;
		}
		
		System.out.println("rootDir = " + rootDir);
		System.out.println("outputDir = " + outDir);
		System.out.println("templates = " + targetSpecificTemplateFile);
		
		if ( rootDir!=null) {
			genAllTargets(outDir, rootDir, templatesRoot, viz);
			return;
		}

		if ( outDir==null || targetSpecificTemplateFile==null ) {
			System.err.println("You must give an output root dir and templates file");
			return;
		}

		genTarget(outDir, targetSpecificTemplateFile, templatesRoot, viz);
	}

	public static void genAllTargets(String outDirRoot, final String rootDir, final String templatesRoot, boolean viz) {
		for (String target : targets) {
			String templatesPackage = rootDir + "/org/antlr/v4/test/runtime/" + target.toLowerCase();
			String templates = templatesPackage + "/" + target + ".test.stg";
			if ( target.equals("JavaScript") ) {
				templates = templatesPackage+"/node/Node.test.stg";
			}
			String outDir = rootDir + "/runtime-testsuite/test";
			if ( outDirRoot!=null ) {
				outDir = outDirRoot;
			}
			genTarget(outDir, templates, templatesRoot, viz);
		}
	}

	public static void genTarget(final String outDir, final String targetSpecificTemplateFile, final String templates, boolean viz) {
		TestGenerator gen = new TestGenerator("UTF-8",
											  new File(targetSpecificTemplateFile),
											  new File(outDir),
											  viz)
		{
			@Override
			protected void info(String message) {
				System.err.println(message);
			}
			@Override
			public File getOutputDir(String templateFolder) {
				String targetName = getTargetNameFromTemplatesFileName();
				// compute package
				String templatePath = runtimeTemplates.getPath();
				int packageStart = templatePath.indexOf("org/antlr/v4/test/runtime");
				int packageEnd = templatePath.indexOf("/" + targetName + ".test.stg");
				String packageDir = templatePath.substring(packageStart, packageEnd);
				return new File(outputDirectory, packageDir);
			}
			@Override
			public String getTestTemplatesResourceDir() {
			  return templates;
			  //return "resources/org/antlr/v4/test/runtime/templates";
			}
		};

		// Somehow the templates directory is getting picked up so let's block that
		if(!targetSpecificTemplateFile.endsWith(".stg")) {
		    return;
		}
		
		gen.info("Generating target " + gen.getTargetNameFromTemplatesFileName());
		gen.execute();
	}

	public TestGenerator(String encoding, File runtimeTemplates, File outputDirectory, boolean visualize) {
		this.encoding = encoding;
		this.runtimeTemplates = runtimeTemplates;
		this.outputDirectory = outputDirectory;
		this.visualize = visualize;
	}

	public void execute() {
		STGroup targetGroup = new STGroupFile(runtimeTemplates.getPath());
		targetGroup.registerModelAdaptor(STGroup.class, new STGroupModelAdaptor());
		targetGroup.defineDictionary("escape", new JavaEscapeStringMap());
		targetGroup.defineDictionary("lines", new LinesStringMap());
		targetGroup.defineDictionary("strlen", new StrlenStringMap());

		String rootFolder = getTestTemplatesResourceDir();
		generateCodeForFoldersInIndex(targetGroup, rootFolder);
	}

	protected void generateCodeForFoldersInIndex(STGroup targetGroup, String rootFolder) {
		STGroup index = new STGroupFile(rootFolder+"/Index.stg");
		index.load(); // make sure the index group is loaded since we call rawGetDictionary

		Map<String, Object> folders = index.rawGetDictionary("TestFolders");
		if (folders != null) {
			for (String key : folders.keySet()) {
				final String testdir = rootFolder + "/" + key;
				STGroup testIndex = new STGroupFile(testdir + "/Index.stg");
				testIndex.load();
				Map<String, Object> templateNames = testIndex.rawGetDictionary("TestTemplates");
				if ( templateNames != null && !templateNames.isEmpty() ) {
					final ArrayList<String> sortedTemplateNames = new ArrayList<String>(templateNames.keySet());
					Collections.sort(sortedTemplateNames);
					generateTestFile(testIndex, targetGroup,
									 testdir,
									 sortedTemplateNames);
				}
			}
		}
	}

	protected void generateTestFile(STGroup index,
									STGroup targetGroup,
									String testdir,
									Collection<String> testTemplates)
	{
		File targetFolder = getOutputDir(testdir);
		String testName = testdir.substring(testdir.lastIndexOf('/') + 1);
		File targetFile = new File(targetFolder, "Test" + testName + ".java");
		info("Generating file "+targetFile.getAbsolutePath());
		List<ST> templates = new ArrayList<ST>();
		for (String template : testTemplates) {
			STGroup testGroup = new STGroupFile(testdir + "/" + template + STGroup.GROUP_FILE_EXTENSION);
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
		testFileTemplate.addAggr("file.{Options,name,tests}", index.rawGetDictionary("Options"), testName, templates);

		if (visualize) {
			STViz viz = testFileTemplate.inspect();
			try {
				viz.waitForClose();
			}
			catch (InterruptedException ex) { }
		}

		try {
			writeFile(targetFile, testFileTemplate.render());
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

	public String getTestTemplatesResourceDir() { return "org/antlr/v4/test/runtime/templates"; }

	public String getTargetNameFromTemplatesFileName() {
		// runtimeTemplates is like ~/antlr/code/antlr4/runtime-testsuite/resources/org/antlr/v4/test/runtime/java/Java.test.stg
		int targetEnd = runtimeTemplates.getName().indexOf(".test.stg");
		return runtimeTemplates.getName().substring(0, targetEnd);
	}

	public File getOutputDir(String templateFolder) {
		return new File(outputDirectory, templateFolder.substring(0, templateFolder.indexOf("/templates")));
	}

	protected void info(String message) {
	}

	protected void warn(String message) {
		System.err.println("WARNING: " + message);
	}

	protected void error(String message, Throwable throwable) {
		System.err.println("ERROR: " + message);
	}
}
