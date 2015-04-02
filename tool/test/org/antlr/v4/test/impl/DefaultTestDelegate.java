package org.antlr.v4.test.impl;

import org.antlr.v4.Tool;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.test.ErrorQueue;
import org.antlr.v4.test.TestUtils;
import org.antlr.v4.tool.ANTLRMessage;
import org.antlr.v4.tool.DefaultToolListener;
import org.junit.runner.Description;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.*;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by jason on 3/24/15.
 */
public class DefaultTestDelegate extends AbstractTestDelegate {
    public static final DefaultTestDelegate INSTANCE = new DefaultTestDelegate();


    String tmpdir;
    String stderrDuringParse;


    @Override
    public void testWillStart(Description description) {

        if (AntlrTestSettings.CREATE_PER_TEST_DIRECTORIES) {
            // new output dir for each test
            String testDirectory = description.getTestClass().getSimpleName() +
                                   File.separatorChar +
                                   description.getMethodName();
            tmpdir = new File(AntlrTestSettings.BASE_TEST_DIR, testDirectory).getAbsolutePath();

        } else {

            tmpdir = new File(AntlrTestSettings.BASE_TEST_DIR,
                              description.getTestClass().getSimpleName()).getAbsolutePath();
            if (!AntlrTestSettings.PRESERVE_TEST_DIR && new File(tmpdir).exists()) {
                eraseGeneratedFiles();
            }
        }
        stderrDuringParse = null;

    }

    @Override
    public void testDidFinish() {
        if (!AntlrTestSettings.PRESERVE_TEST_DIR && new File(tmpdir).exists()) {
            eraseGeneratedFiles();
        }
    }

    public Tool createTool(String... args) {
        return new Tool(args);
    }


    public boolean compile(String... fileNames) {
        List<File> files = new ArrayList<File>();
        for (String fileName : fileNames) {
            File f = new File(tmpdir, fileName);
            files.add(f);
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics =
                new DiagnosticCollector<JavaFileObject>();

        StandardJavaFileManager fileManager =
                compiler.getStandardFileManager(diagnostics, Locale.getDefault(), Charset.forName("UTF-8"));

        Iterable<? extends JavaFileObject> compilationUnits =
                fileManager.getJavaFileObjectsFromFiles(files);

        Iterable<String> compileOptions =
                Arrays.asList("-g",
                              "-source",
                              "1.6",
                              "-target",
                              "1.6",
                              "-implicit:class",
                              "-Xlint:-options",
                              "-d",
                              tmpdir,
                              "-cp",
                              tmpdir + AntlrTestSettings.PATH_SEP + AntlrTestSettings.CLASSPATH);

        StringWriter extraMessages = new StringWriter();
        JavaCompiler.CompilationTask task =
                compiler.getTask(extraMessages, fileManager, diagnostics, compileOptions, null,
                                 compilationUnits);
        boolean ok = task.call();

        try {
            fileManager.close();
        } catch (IOException ioe) {
            ioe.printStackTrace(System.err);
        }

        assertThat(diagnostics, TestUtils.hasNoErrors());
        return ok;

    }


    @Override
    public void mkdir(String path) {
        File f = new File(path);
        boolean ok = f.mkdirs();
    }

    @Override
    public void writeFile(String dir, String fileName, String content) {
        try {
            mkdir(dir);
            Utils.writeFile(dir + File.separatorChar + fileName, content, "UTF-8");
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public Class<?> loadCompiledClass(String name) {
        try {
            ClassLoader loader =
                    new URLClassLoader(new URL[]{new File(tmpdir).toURI().toURL()},
                                       ClassLoader.getSystemClassLoader());
            return loader.loadClass(name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String execClass(String className) {
        try {
            String[] args = new String[]{
                    "java", "-classpath", tmpdir + AntlrTestSettings.PATH_SEP + AntlrTestSettings.CLASSPATH,
                    className, new File(tmpdir, "input").getAbsolutePath()
            };
            //String cmdLine = "java -classpath "+CLASSPATH+pathSep+tmpdir+" Test " + new File(tmpdir, "input").getAbsolutePath();
            //System.out.println("execParser: "+cmdLine);
            Process process =
                    Runtime.getRuntime().exec(args, null, new File(tmpdir));
            StreamVacuum stdoutVacuum = new StreamVacuum(process.getInputStream());
            StreamVacuum stderrVacuum = new StreamVacuum(process.getErrorStream());
            stdoutVacuum.start();
            stderrVacuum.start();
            process.waitFor();
            stdoutVacuum.join();
            stderrVacuum.join();
            String output = stdoutVacuum.toString();
            if (stderrVacuum.toString().length() > 0) {
                this.stderrDuringParse = stderrVacuum.toString();
                System.err.println("exec stderrVacuum: " + stderrVacuum);
            }
            return output;
        } catch (Exception e) {
            System.err.println("can't exec recognizer");
            e.printStackTrace(System.err);
        }
        return null;
    }

//    public void eraseGeneratedFiles(String endingWith) {
//        File tmpdirF = new File(tmpdir);
//        String[] files = tmpdirF.list();
//        for (int i = 0; files != null && i < files.length; i++) {
//            if (files[i].endsWith(endingWith)) {
//              boolean deleted=  new File(tmpdir + File.separatorChar + files[i]).delete();
//            }
//        }
//    }


    public void eraseGeneratedFiles() {
        erase(new File(tmpdir));
    }

    static void erase(File dir) {
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    erase(file);
                } else {
                    boolean ignored = file.delete();
                }
            }
        }
        boolean ignored = dir.delete();
    }

    @Override
    public String execLexer(String grammarFileName,
                            String grammarStr,
                            String lexerName,
                            String input,
                            boolean showDFA) {
        boolean success = generateAndBuildRecognizer(grammarFileName,
                                                     grammarStr,
                                                     null,
                                                     lexerName);
        assertTrue(success);
        writeFile(tmpdir, "input", input);
        writeLexerTestFile(lexerName, showDFA);
        compile("Test.java");
        String output = execClass("Test");
        if (stderrDuringParse != null && stderrDuringParse.length() > 0) {
            System.err.println(stderrDuringParse);
        }
        return output;
    }

    @Override
    public String execParser(String grammarFileName,
                             String grammarStr,
                             String parserName,
                             String lexerName,
                             String startRuleName,
                             String input,
                             boolean debug,
                             boolean profile) {
        boolean success = generateAndBuildRecognizer(grammarFileName,
                                                     grammarStr,
                                                     parserName,
                                                     lexerName,
                                                     "-visitor");
        assertTrue(success);
        writeFile(tmpdir, "input", input);
        this.stderrDuringParse = null;
        if (parserName == null) {
            writeLexerTestFile(lexerName, false);
        } else {
            writeTestFile(parserName,
                          lexerName,
                          startRuleName,
                          debug,
                          profile);
        }

        compile("Test.java");
        return execClass("Test");
    }

    @Override
    public String getWorkingDir() {
        return tmpdir;
    }

    @Override
    public String getStdErrDuringParse() {
        return stderrDuringParse;
    }

    public ErrorQueue antlr(String grammarFileName,
                            String grammarStr,
                            boolean defaultListener,
                            String... extraOptions) {

        System.out.println("dir " + tmpdir);
        mkdir(tmpdir);
        writeFile(tmpdir, grammarFileName, grammarStr);


        final List<String> options = new ArrayList<String>();
        Collections.addAll(options, extraOptions);
        if (!options.contains("-o")) {
            options.add("-o");
            options.add(tmpdir);
        }
        if (!options.contains("-lib")) {
            options.add("-lib");
            options.add(tmpdir);
        }
        if (!options.contains("-encoding")) {
            options.add("-encoding");
            options.add("UTF-8");
        }
        options.add(new File(tmpdir, grammarFileName).toString());

        final String[] optionsA = new String[options.size()];
        options.toArray(optionsA);
        Tool antlr = createTool(optionsA);
        ErrorQueue equeue = new ErrorQueue(antlr);
        antlr.addListener(equeue);
        if (defaultListener) {
            antlr.addListener(new DefaultToolListener(antlr));
        }
        antlr.processGrammarsOnCommandLine();

        if (!defaultListener && !equeue.errors.isEmpty()) {
            System.err.println("antlr reports errors from " + options);
            for (int i = 0; i < equeue.errors.size(); i++) {
                ANTLRMessage msg = equeue.errors.get(i);
                System.err.println(msg);
            }
            System.out.println("!!!\ngrammar:");
            try {
                System.out.println(new String(Utils.readFile(tmpdir + "/" + grammarFileName)));
            } catch (IOException ioe) {
                System.err.println(ioe.toString());
            }
            System.out.println("###");
        }
        if (!defaultListener && !equeue.warnings.isEmpty()) {
            System.err.println("antlr reports warnings from " + options);
            for (int i = 0; i < equeue.warnings.size(); i++) {
                ANTLRMessage msg = equeue.warnings.get(i);
                System.err.println(msg);
            }
        }

        return equeue;
    }

    @Override
    public ErrorQueue antlr(String grammarFileName, boolean defaultListener, String... extraOptions) {
        final List<String> options = new ArrayList<String>();
        Collections.addAll(options, extraOptions);
        if (!options.contains("-o")) {
            options.add("-o");
            options.add(tmpdir);
        }
        if (!options.contains("-lib")) {
            options.add("-lib");
            options.add(tmpdir);
        }
        if (!options.contains("-encoding")) {
            options.add("-encoding");
            options.add("UTF-8");
        }
        options.add(tmpdir + File.separatorChar + grammarFileName);

        final String[] optionsA = new String[options.size()];
        options.toArray(optionsA);
        Tool antlr = createTool(optionsA);
        ErrorQueue equeue = new ErrorQueue(antlr);
        antlr.addListener(equeue);
        if (defaultListener) {
            antlr.addListener(new DefaultToolListener(antlr));
        }
        antlr.processGrammarsOnCommandLine();

        if (!defaultListener && !equeue.errors.isEmpty()) {
            System.err.println("antlr reports errors from " + options);
            for (int i = 0; i < equeue.errors.size(); i++) {
                ANTLRMessage msg = equeue.errors.get(i);
                System.err.println(msg);
            }
            System.out.println("!!!\ngrammar:");
            // System.out.println(new String(Utils.readFile(tmpdir + "/" + grammarFileName)));
            System.out.println("###");
        }
        if (!defaultListener && !equeue.warnings.isEmpty()) {
            System.err.println("antlr reports warnings from " + options);
            for (int i = 0; i < equeue.warnings.size(); i++) {
                ANTLRMessage msg = equeue.warnings.get(i);
                System.err.println(msg);
            }
        }

        return equeue;
    }

    /**
     * Return true if all is well
     */
    protected boolean generateAndBuildRecognizer(String grammarFileName,
                                                 String grammarStr,
                                                 String parserName,
                                                 String lexerName,
                                                 String... extraOptions) {
        return generateAndBuildRecognizer(grammarFileName, grammarStr, parserName, lexerName, false, extraOptions);
    }

    /**
     * Return true if all is well
     */
    @Override
    public boolean generateAndBuildRecognizer(String grammarFileName,
                                              String grammarStr,
                                              String parserName,
                                              String lexerName,
                                              boolean defaultListener,
                                              String... extraOptions) {
        ErrorQueue equeue =
                antlr(grammarFileName, grammarStr, defaultListener, extraOptions);
        if (!equeue.errors.isEmpty()) {
            return false;
        }

        List<String> files = new ArrayList<String>();
        if (lexerName != null) {
            files.add(lexerName + ".java");
        }
        if (parserName != null) {
            files.add(parserName + ".java");
            Set<String> optionsSet = new HashSet<String>(Arrays.asList(extraOptions));
            if (!optionsSet.contains("-no-listener")) {
                files.add(grammarFileName.substring(0, grammarFileName.lastIndexOf('.')) + "BaseListener.java");
            }
            if (optionsSet.contains("-visitor")) {
                files.add(grammarFileName.substring(0, grammarFileName.lastIndexOf('.')) + "BaseVisitor.java");
            }
        }
        return compile(files.toArray(new String[files.size()]));
    }

    protected void writeTestFile(String parserName,
                                 String lexerName,
                                 String parserStartRuleName,
                                 boolean debug,
                                 boolean profile) {
        String code = TestCodeGenerator.generateParserTestCode("Test",
                                                               parserName,
                                                               lexerName,
                                                               parserStartRuleName,
                                                               debug,
                                                               profile);
        writeFile(tmpdir, "Test.java", code);
    }


    protected void writeLexerTestFile(String lexerName, boolean showDFA) {
        String code = TestCodeGenerator.generateLexerTestCode("Test", showDFA, lexerName);
        writeFile(tmpdir, "Test.java", code);
    }
}
