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
import org.stringtemplate.v4.gui.STViz;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TestGenerator {
	public static final String antlrRoot = "."; // assume antlr4 root dir is current working dir

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
	 * $ java org.antlr.v4.testgen.TestGenerator -o /tmp -templates /Users/parrt/antlr/code/antlr4/tool/test/org/antlr/v4/test/runtime/java/Java.test.stg
	 */
	public static void main(String[] args) {
		String rootDir = null;
		String outDir = null;
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
			}
			else if (arg.startsWith("-viz")) {
				viz = true;
			}
			i++;
		}
		if ( rootDir!=null) {
			genAllTargets(rootDir, viz);
			System.exit(0);
		}
		
		if ( outDir==null || targetSpecificTemplateFile==null ) {
			System.err.println("You must give an output root dir and templates file");
			System.exit(1);
		}

		genTarget(outDir, targetSpecificTemplateFile, viz);
	}
	
	public static void genAllTargets(final String rootDir, boolean viz) {
		for(TargetConfiguration config : TargetConfiguration.ALL) {
			String outDir = rootDir + config.outDir;
			String templates = rootDir + config.templates;
			genTarget(outDir, templates, viz);
		}
		
	}
	
	public static void genTarget(final String outDir, String targetSpecificTemplateFile, boolean viz) {
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
				return "runtime-testsuite/resources/org/antlr/v4/test/runtime/templates"; 
			}
		};
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
		STGroup index = new STGroupFile(rootFolder+"/Index.stg");
		generateCodeForFolder(targetGroup, rootFolder, index);
	}

	private void generateCodeForFolder(STGroup targetGroup, String folder, STGroup index) {
		// make sure the index group is loaded since we call rawGetDictionary
		index.load();

		Map<String, Object> folders = index.rawGetDictionary("TestFolders");
		if (folders != null) {
			for (String key : folders.keySet()) {
				String subfolder = folder + "/" + key;
				STGroup subindex = new STGroupFile(subfolder + "/Index.stg");
				generateCodeForFolder(targetGroup, folder + "/" + key, subindex);
			}
		}

		Map<String, Object> templates = index.rawGetDictionary("TestTemplates");
		if (templates != null && !templates.isEmpty()) {
			generateTestFile(index, targetGroup, folder.substring(folder.lastIndexOf('/') + 1), folder, new ArrayList<String>(templates.keySet()));
		}
	}

	private void generateTestFile(STGroup index, STGroup targetGroup, String testFile, String templateFolder, Collection<String> testTemplates) {
		File targetFolder = getOutputDir(templateFolder);
		File targetFile = new File(targetFolder, "Test" + testFile + ".java");
		info("Generating file "+targetFile.getAbsolutePath());
		List<ST> templates = new ArrayList<ST>();
		for (String template : testTemplates) {
			STGroup testGroup = new STGroupFile(templateFolder + "/" + template + STGroup.GROUP_FILE_EXTENSION);
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
		testFileTemplate.addAggr("file.{Options,name,tests}", index.rawGetDictionary("Options"), testFile, templates);

		if (visualize) {
			STViz viz = testFileTemplate.inspect();
			try {
				viz.waitForClose();
			} catch (InterruptedException ex) {
			}
		}

		try {
			writeFile(targetFile, testFileTemplate.render());
		} catch (IOException ex) {
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
		// runtimeTemplates is like /Users/parrt/antlr/code/antlr4/tool/test/org/antlr/v4/test/runtime/java/Java.test.stg
		// extra target name
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
