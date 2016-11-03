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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.stringtemplate.v4.AutoIndentWriter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.STWriter;
import org.stringtemplate.v4.gui.STViz;

public class TestGenerator {
	
	public final static String[] targets = {"CSharp", "Java", "Python2", "Python3", "JavaScript/Node", "JavaScript/Safari", "JavaScript/Firefox", "JavaScript/Explorer", "JavaScript/Chrome"};

	/** Execute from antlr4 root dir:
	 * *
	 * $ java TestGenerator -o output-root-dir -templates -viz
	 *
	 * Example:
	 *
	 * $ java org.antlr.v4.testgen.TestGenerator -root /Users/parrt/antlr/code/antlr4
	 */
	public static void main(String[] args) {
		File rootDir = null;
		File outDir = null;
		File testTemplatesRoot = null;
		String target = "ALL";
		boolean browsers = false;
		boolean viz = false;

		int i = 0;
		while (args != null && i < args.length) {
			String arg = args[i];
			if (arg.startsWith("-root")) {
				i++;
				rootDir = new File(args[i]);
			}
			else if (arg.startsWith("-outdir")) {
				i++;
				outDir = new File(args[i]);
			}
			else if (arg.startsWith("-templates")) {
				i++;
				testTemplatesRoot = new File(args[i]);
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

        rootDir = absolutePath(rootDir, null, "");
        outDir = absolutePath(outDir, rootDir, "test");
        testTemplatesRoot = absolutePath(testTemplatesRoot, rootDir,
                              "resources/org/antlr/v4/test/runtime/templates");

		System.out.println("root = " + rootDir);
		System.out.println("outdir = " + outDir);
		System.out.println("templates = " + testTemplatesRoot);
		System.out.println("target = " + target);
		System.out.println("browsers = " + browsers);
		System.out.println("viz = " + viz);
		
		if ( "ALL".equalsIgnoreCase(target)) {
			genAllTargets(rootDir, outDir, testTemplatesRoot, browsers, viz);
		} else
			genTarget(rootDir, outDir, target, testTemplatesRoot, viz);
	}

	public static void genAllTargets(File rootDir, File outDir, File testTemplatesRoot, boolean browsers, boolean viz) {
		for(String target : targets) {
			if(!browsers && "JavaScript/Safari".equals(target))
				return;
			genTarget(rootDir, outDir, target, testTemplatesRoot, viz);
		}
	}
	
	public static void genTarget(File rootDir, File outDir, String fullTarget, File testTemplatesRoot, boolean viz) {
		TestGenerator gen = new TestGenerator("UTF-8",
					fullTarget,
					rootDir,
					outDir,
					testTemplatesRoot,
					viz);
		gen.execute();
	}

	// This project uses UTF-8, but the plugin might be used in another project
	// which is not. Always load templates with UTF-8, but write using the
	// specified encoding.
	protected final String encoding;
	protected final String targetName;
	protected final File rootDir;
	protected final File outDir;
	protected final File testTemplatesRoot;
	protected final boolean visualize;
    protected String lineSeparator = System.getProperty("line.separator");

	public TestGenerator(String encoding, String targetName, File rootDir, File outDir, File testTemplatesRoot, boolean visualize) {
		this.encoding = encoding;
		this.targetName = targetName;
		this.rootDir = rootDir;
		this.outDir = outDir;
		this.testTemplatesRoot = testTemplatesRoot;
		this.visualize = visualize;
	}

	private String getTargetName() {
		return targetName;
	}


	public void execute() {
		File targetOutputDir = outputDir(targetName);
		info(String.format("Generating target %s => %s", targetName, targetOutputDir));

        File targetTemplatesRoot = new File(rootDir, "resources/org/antlr/v4/test/runtime");
        File targetTemplateFile = targetTemplateFile(targetTemplatesRoot, targetName);

		STGroup targetGroup = new STGroupFile(targetTemplateFile.getPath());

		targetGroup.registerModelAdaptor(STGroup.class, new STGroupModelAdaptor());
		targetGroup.defineDictionary("escape", new JavaEscapeStringMap());
		targetGroup.defineDictionary("lines", new LinesStringMap());
		targetGroup.defineDictionary("strlen", new StrlenStringMap());

		generateCodeForFoldersInIndex(targetGroup, targetOutputDir);
	}

	protected void generateCodeForFoldersInIndex(STGroup targetGroup, File targetOutputDir) {
        Collection<String> testFolders = new ArrayList<String>(20);
        Collection<String> excludeFolders = new TreeSet<String>();

		STGroup rootIndex = new STGroupFile(new File(testTemplatesRoot, "Index.stg").getPath());

        // let output file line endings be the same kind found in top Index.stg
        lineSeparator = detectLineSeparator(rootIndex.getFileName(), lineSeparator);

        filterIndexEntries(testFolders, excludeFolders, rootIndex, "TestFolders");

        if (! excludeFolders.isEmpty()) {
            info(String.format("  Excluding %s tests", excludeFolders));
            testFolders.addAll(excludeFolders);
        }

        for (String testName : testFolders) {
            Collection<String> sortedTemplateNames = new TreeSet<String>();
            Collection<String> excludeTemplates = new TreeSet<String>();
            Map<String, Object> fileOptions = Collections.emptyMap();

            File outputFile = new File(targetOutputDir, "Test" + testName + ".java");
            File testTemplateDir = new File(testTemplatesRoot, testName);

            if (! excludeFolders.contains(testName)) {
                STGroup testIndex = new STGroupFile(new File(testTemplateDir, "Index.stg").getPath());
                filterIndexEntries(sortedTemplateNames, excludeTemplates, testIndex, "TestTemplates");
                fileOptions = testIndex.rawGetDictionary("Options");
            }

            detail(String.format("  Generating %3d tests to file %s",
                                 sortedTemplateNames.size(), outputFile));
            if (! excludeTemplates.isEmpty())
                detail(String.format("                  - excluding %s", excludeTemplates));

            generateTestFile(outputFile, fileOptions, targetGroup,
                             testTemplateDir, sortedTemplateNames);
        }
	}

	protected void generateTestFile(File outputFile,
									Map<String, Object> fileOptions,
									STGroup targetGroup,
									File testTemplateDir,
									Collection<String> testTemplates)
	{
		String testName = testTemplateDir.getName();
		List<ST> methodTemplates = new ArrayList<ST>();
		for (String templateName : testTemplates) {
            File testGroupFile = new File(testTemplateDir, templateName + STGroup.GROUP_FILE_EXTENSION);
			STGroup testGroup = new STGroupFile(testGroupFile.getPath());
			importLanguageTemplates(testGroup, targetGroup);
			ST testType = testGroup.getInstanceOf("TestType");
			if (testType == null) {
				warn(String.format("Unable to generate tests for %s: no TestType specified.", templateName));
				continue;
			}

			ST testMethodTemplate = targetGroup.getInstanceOf(testType.render() + "TestMethod");
			if (testMethodTemplate == null) {
				warn(String.format("Unable to generate tests for %s: TestType '%s' is not supported by the current runtime.", templateName, testType.render()));
				continue;
			}

			testMethodTemplate.add(testMethodTemplate.impl.formalArguments.keySet().iterator().next(), testGroup);
			methodTemplates.add(testMethodTemplate);
		}

		ST testFileTemplate = targetGroup.getInstanceOf("TestFile");
		testFileTemplate.addAggr("file.{Options,name,tests}", fileOptions, testName, methodTemplates);

		if (visualize) {
			STViz viz = testFileTemplate.inspect();
			try {
				viz.waitForClose();
			}
			catch (InterruptedException ex) { }
		}

		try {
			writeFile(outputFile, render(testFileTemplate));
		}
		catch (IOException ex) {
			error(String.format("Failed to write output file: %s", outputFile), ex);
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

    public String render(ST template) {
        // like ST.render() but uses our lineSeparator
        StringWriter out = new StringWriter();
        STWriter wr = new AutoIndentWriter(out, lineSeparator);
        wr.setLineWidth(STWriter.NO_WRAP);
        template.write(wr, Locale.getDefault());
        return out.toString();
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

    public static File absolutePath(File pathName, File defaultParent, String defaultChild) {
        if (pathName == null || pathName.getPath().isEmpty())
            pathName = new File(defaultParent, defaultChild);
        return pathName.isAbsolute() ? pathName : pathName.getAbsoluteFile();
    }

    /**
     * Return a file's first line ending.
     *
     * @param fileName  pathname to the file
     * @param orElse    default returned in case file can't be read or doesn't
     *                    have \n within its first 400 bytes
     *
     * @return one of: "\n", "\r\n", {@code orElse}
     */
    public static String detectLineSeparator(String fileName, String orElse) {
		try {
            FileInputStream fis = new FileInputStream(fileName);
            try {
                byte[] data = new byte[400];
                int n = fis.read(data);
                int i = 0;
                while (i < n && data[i] != '\n')
                    i++;
                if (i < n)
                    return (i > 0 && data[i-1] == '\r') ? "\r\n" : "\n";
            } finally {
                fis.close();
            }
		}
		catch (IOException ioe) {
        }
        return orElse;
    }

  /**
     * Collect "Index" dictionary keys to be included for the current target.
     *
     * <p>A dictionary entry's key is added to the includedKeys collection if the
     * value is any of: true; [] (empty list); or a regex pattern string that
     * fully matches the current target name (case-insensitive).  Otherwise the
     * key is added to the excludedKeys collection.
     *
     * <p>An exclamation mark (!) can precede the regex to invert the meaning, so
     * the key will be excluded rather than included when the pattern matches the
     * target name.
     *
     * @param includedKeys  OUT: included keys are added to this collection
     * @param excludedKeys  OUT: excluded keys are added to this collection
     * @param dictGroup     STGroup in which the dictionary should be found
     * @param dictName      dictionary name
     */
    protected void filterIndexEntries(Collection<String> includedKeys,
                                      Collection<String> excludedKeys,
                                      STGroup dictGroup,
                                      String dictName) {
		dictGroup.load(); // make sure the index group is loaded since we call rawGetDictionary
        Map<String, Object> dict = dictGroup.rawGetDictionary(dictName);

        if (dict == null) {
            warn(String.format("Dictionary '%s' not found in %s",
                               dictName, dictGroup.getFileName()));
            return;
        }

        for (Map.Entry<String, Object> e : dict.entrySet()) {
            String k = e.getKey();
            Object v = e.getValue();
            boolean include = false;
            if (v instanceof Boolean)
                include = (Boolean) v;
            else if (v instanceof Collection &&
                     ((Collection) v).isEmpty())
                include = true;
            else if (v instanceof String) {
                String s = (String) v;
                char first = s.isEmpty() ? 0 : s.charAt(0);
                if (first == '!')
                    s = s.substring(1);
                Pattern pat = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
                include = pat.matcher(targetName).matches();
                if (first == '!')
                    include = !include;
            }
            else
                warn(String.format("%s dictionary %s has invalid entry %s : %s",
                                   dictGroup.getFileName(), dictName, k, v));
            if (include)
                includedKeys.add(k);
            else
                excludedKeys.add(k);
        }
    }

    protected File outputDir(String targetName) {
        // NB. In MS Windows pathnames, forward (/) or backward (\) slashes are
        // equivalent.  However, when stored in a File object, all are converted
        // to just one kind of slash (File.separatorChar).  A pathname obtained
        // from a File object does not end with a slash.
        char slash = File.separatorChar;
        String templateFolder = testTemplatesRoot.getPath() + slash;

        String chomp = rootDir.getPath() + slash;
		if(templateFolder.startsWith(chomp))
			templateFolder = templateFolder.substring(chomp.length());

        chomp = "resources" + slash;
		if(templateFolder.startsWith(chomp))
			templateFolder = templateFolder.substring(chomp.length());

        int i = (slash + templateFolder).indexOf(slash + "templates" + slash);
        if (i >= 0)
            templateFolder = templateFolder.substring(0, i);

		templateFolder += slash + targetName.toLowerCase();
		return new File(outDir, templateFolder);
	}

	protected File targetTemplateFile(File targetTemplatesRoot, String targetName) {
		String subTarget = targetName.substring(targetName.lastIndexOf('/')+1);
		File templateDir = new File(targetTemplatesRoot, targetName.toLowerCase());
        File templateFile = new File(templateDir, subTarget + ".test.stg");
        return templateFile;
    }

	protected void detail(String message) {
		info(message);
	}

	protected void info(String message) {
		System.out.println(message);
	}

	protected void warn(String message) {
		System.err.println("WARNING: " + message);
	}

	protected void error(String message, Throwable throwable) {
		System.err.println("ERROR: " + message);
	}
}
