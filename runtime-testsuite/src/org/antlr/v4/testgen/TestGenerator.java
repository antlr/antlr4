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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.stringtemplate.v4.AutoIndentWriter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.STWriter;
import org.stringtemplate.v4.gui.STViz;
import org.stringtemplate.v4.misc.ErrorBuffer;
import org.stringtemplate.v4.misc.STMessage;

public class TestGenerator {
	
	public final static String[] targets = {"CSharp", "Java", "Python2", "Python3", "JavaScript/Node", "JavaScript/Safari", "JavaScript/Firefox", "JavaScript/Explorer", "JavaScript/Chrome"};

    /**
     * Generate test programs for ANTLR4 runtime.
     *
     * <p>Execute from antlr4 root dir:
     * <p><code>
     * $ java org.antlr.v4.testgen.TestGenerator [-root <i>dir</i>]
     * [-outdir <i>dir</i>] [-templates <i>dir</i>] [-encoding <i>string</i>]
     * [-target <i>string</i>] [-browsers] [-viz]
     * </code>
     *
     * <p>Example:<pre>
     *   $ java org.antlr.v4.testgen.TestGenerator -root /Users/parrt/antlr/code/antlr4
     * </pre></p>
     */
	public static void main(String[] args) {
        TestGenerator gen = new TestGenerator();

        try {
            ListIterator<String> moreArgs = Arrays.asList(args).listIterator(0);
            while (moreArgs.hasNext()) {
                String arg = moreArgs.next();
                if (!gen.acceptOption(arg, moreArgs))
                    throw new TestGenException("Option not recognized: %s", arg);
            }

            gen.applyDefaults();
            gen.showOptions();
	        gen.execute();

        } catch (TestGenException ex) {
            gen.errPrintln(ex.getClass().getSimpleName() + ": " + ex.getMessage());
            for (StackTraceElement ste : ex.getStackTrace())
                gen.errPrintln("\tat " + ste);
            System.exit(1);
        }
	}

    public TestGenerator() {
    }

    // set by acceptOption() and applyDefaults() ...
    public File rootDir;
    public File outDir;
    public File testTemplatesRoot;
    public String encoding = "UTF-8";
    public boolean visualize;
    public boolean browsers;
    public String targetOption = "ALL";

    // set during genTarget()
    protected String targetName;
    protected String lineSeparator = System.getProperty("line.separator");
    protected ErrorBuffer errorBuffer = new ErrorBuffer();

	// This project uses UTF-8, but the plugin might be used in another project
	// which is not. Always load templates with UTF-8, but write using the
	// specified encoding.

    /**
     * Consume a command line option.
     * 
     * @param arg       the argument to examine
     * @param moreArgs  The rest of the arguments.  If arg is recognized as an
     *                  option that requires a value, the value is consumed from
     *                  this iterator; TestGenException is thrown if not enough.
     * @return true if option was recognized and consumed; false if unrecognized
     * @throws TestGenException
     */
    public boolean acceptOption(String arg, Iterator<String> moreArgs) {
        try {
            if ("-root".equals(arg))
                rootDir = new File(moreArgs.next());
            else if ("-outdir".equals(arg))
                outDir = new File(moreArgs.next());
            else if ("-templates".equals(arg))
                testTemplatesRoot = new File(moreArgs.next());
            else if ("-encoding".equals(arg))
                encoding = moreArgs.next();
            else if ("viz".equals(arg))
                visualize = true;
            else if ("-browsers".equals(arg))
                browsers = true;
            else if ("-target".equals(arg))
                targetOption = moreArgs.next();
            else
                return false;   // option not recognized
            return true;        // ok, consumed
        } catch (NoSuchElementException ex) {
            throw new TestGenException("Option requires a value: %s", arg);
        }
    }

    /** 
     * Finish processing the options, and fill in defaults.
     */
    public void applyDefaults() {
        rootDir = absolutePath(rootDir, null, "");
        outDir = absolutePath(outDir, rootDir, "test");
        testTemplatesRoot = absolutePath(testTemplatesRoot, rootDir,
                              "resources/org/antlr/v4/test/runtime/templates");
    }

    public void showOptions() {
        info("TestGenerator");
		info("    -root      " + rootDir);
		info("    -outdir    " + outDir);
		info("    -templates " + testTemplatesRoot);
		info("    -encoding  " + encoding);
        info(visualize ? "    -viz" : " no -viz");
        info(browsers ? "    -browsers" : " no -browsers");
		info("    -target    " + targetOption);
    }

    public void execute() {
		if ( "ALL".equalsIgnoreCase(targetOption)) {
            for(String target : targets) {
                if(!browsers && "JavaScript/Safari".equals(target))
                    return;
                genTarget(target);
            }
		} 
        else
			genTarget(targetOption);       
    }

	public void genTarget(String target) {
        targetName = target;
		File targetOutputDir = outputDir(targetName);
		info(String.format("Generating target %s => %s", targetName, targetOutputDir));

        File targetTemplatesRoot = new File(rootDir, "resources/org/antlr/v4/test/runtime");
        File targetTemplateFile = targetTemplateFile(targetTemplatesRoot, targetName);

		STGroup targetGroup = new STGroupFile(targetTemplateFile.getPath());
        targetGroup.setListener(errorBuffer);

		targetGroup.registerModelAdaptor(STGroup.class, new STGroupModelAdaptor());
		targetGroup.defineDictionary("escape", new JavaEscapeStringMap());
		targetGroup.defineDictionary("lines", new LinesStringMap());
		targetGroup.defineDictionary("strlen", new StrlenStringMap());

		generateCodeForFoldersInIndex(targetGroup, targetOutputDir);

        if (errorBuffer.errors.size() > 0)
            throwTemplateError();
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

            try {
                generateTestFile(outputFile, fileOptions, targetGroup,
                                 testTemplateDir, sortedTemplateNames);
            } catch (RuntimeException ex) {
                // tell which file had trouble - in case detail messages are turned off
                error(String.format("while generating test file %s %n" +
                             "            using target template %s %n" +
                             "            and test templates in %s",
                          outputFile, targetGroup.getFileName(), testTemplateDir),
                      ex);
                throw ex;
            }
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
            testGroup.setListener(errorBuffer);
			importLanguageTemplates(testGroup, targetGroup);

			ST testType = testGroup.getInstanceOf("TestType");
			if (testType == null)
				throw new TestGenException("Unable to generate tests for %s: no TestType specified.",
                                           templateName);

			ST testMethodTemplate = targetGroup.getInstanceOf(testType.render() + "TestMethod");
			if (testMethodTemplate == null)
				throw new TestGenException("Unable to generate tests for %s: TestType '%s' is not supported by the current runtime.",
                                           templateName, testType.render());

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
			throw new TestGenException("Error writing output file %s", outputFile, ex);
		}

        if (errorBuffer.errors.size() > 0)
            throwTemplateError();
	}

	private void importLanguageTemplates(STGroup testGroup, STGroup languageGroup) {
		// make sure the test group is loaded
		testGroup.load();

		if (testGroup == languageGroup)
			throw new TestGenException("Attempted to import the language group into itself.");

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
		OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
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
     *
     * @throws TestGenException if dictionary isn't there
     */
    protected void filterIndexEntries(Collection<String> includedKeys,
                                      Collection<String> excludedKeys,
                                      STGroup dictGroup,
                                      String dictName) {
		dictGroup.load(); // make sure the index group is loaded since we call rawGetDictionary
        Map<String, Object> dict = dictGroup.rawGetDictionary(dictName);
        if (dict == null)
            throw new TestGenException("Dictionary '%s' not found in %s",
                                       dictName, dictGroup.getFileName());
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

    /**
     * If any ST errors have landed in errorBuffer, throw TestGenException.
     */
    protected void throwTemplateError() {
        if (errorBuffer.errors.isEmpty())
            return;

        // show the messages in full
        error(String.format("StringTemplate reported the following %d error(s):",
                            errorBuffer.errors.size()), null);
        for (STMessage message : errorBuffer.errors) {
            errPrintln("  " + message);
        }

        // abbreviate the first message
        STMessage m = errorBuffer.errors.get(0);
        String brief = String.format(m.error.message, m.arg, m.arg2, m.arg3);
        if (errorBuffer.errors.size() > 1)
            brief = String.format("%s (and %d more errors)",
                                  brief, errorBuffer.errors.size()-1);

        // reset so caller can carry on if desired
        errorBuffer.errors.clear();

        throw new TestGenException("Error from StringTemplate --> %s", brief);
    }

    public static class TestGenException extends RuntimeException {
        public TestGenException(String format, Object... args) {
            super(String.format(format, args),
                  (args.length > 0 && args[args.length-1] instanceof Throwable
                      ? (Throwable)args[args.length-1] : null) );
        }
    }

	protected void detail(String message) {
		info(message);
	}

	protected void info(String message) {
		outPrintln(message);
	}

	protected void warn(String message) {
		errPrintln("WARNING: " + message);
	}

	protected void error(String message, Throwable throwable) {
		errPrintln("ERROR: " + message);
	}

    protected void errPrintln(String message) {
        System.err.println(message);
    }

    protected void outPrintln(String message) {
        System.out.println(message);
    }
}
