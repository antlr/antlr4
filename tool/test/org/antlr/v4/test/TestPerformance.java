/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *      derived from this software without specific prior written permission.
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

package org.antlr.v4.test;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.antlr.v4.runtime.atn.ParserATNSimulator;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.v4.runtime.atn.ATNConfig;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.dfa.DFAState;

public class TestPerformance extends BaseTest {
    /** Parse all java files under this package within the JDK_SOURCE_ROOT. */
    private static final String TOP_PACKAGE = "java";
    /** True to load java files from sub-packages of {@link #TOP_PACKAGE}. */
    private static final boolean RECURSIVE = true;

    /**
     *  True to use the Java grammar with expressions in the v4 left-recursive syntax (Java-LR.g). False to use
     *  the standard grammar (Java.g). In either case, the grammar is renamed in the temporary directory to Java.g
     *  before compiling.
     */
    private static final boolean USE_LR_GRAMMAR = true;
    /**
     *  True to specify the -Xforce-atn option when generating the grammar, forcing all decisions in JavaParser to
     *  be handled by {@link ParserATNSimulator#adaptivePredict}.
     */
    private static final boolean FORCE_ATN = false;
    /**
     *  True to specify the -atn option when generating the grammar. This will cause ANTLR
     *  to export the ATN for each decision as a DOT (GraphViz) file.
     */
    private static final boolean EXPORT_ATN_GRAPHS = true;
    /**
     *  True to delete temporary (generated and compiled) files when the test completes.
     */
    private static final boolean DELETE_TEMP_FILES = true;

    private static final boolean PAUSE_FOR_HEAP_DUMP = false;

    /** Parse each file with JavaParser.compilationUnit */
    private static final boolean RUN_PARSER = true;
    /** True to use {@link BailErrorStrategy}, False to use {@link DefaultErrorStrategy} */
    private static final boolean BAIL_ON_ERROR = true;
    /** This value is passed to {@link Parser#setBuildParseTree}. */
    private static final boolean BUILD_PARSE_TREES = false;
    /**
     *  Use ParseTreeWalker.DEFAULT.walk with the BlankJavaParserListener to show parse tree walking overhead.
     *  If {@link #BUILD_PARSE_TREES} is false, the listener will instead be called during the parsing process via
     *  {@link Parser#addParseListener}.
     */
    private static final boolean BLANK_LISTENER = false;

    private static final boolean EXPORT_LARGEST_CONFIG_CONTEXTS = false;

    private static final boolean SHOW_DFA_STATE_STATS = true;

    private static final boolean DISABLE_GLOBAL_CONTEXT = true;
    private static final boolean FORCE_GLOBAL_CONTEXT = false;
    private static final boolean TRY_LOCAL_CONTEXT_FIRST = true;

    private static final boolean SHOW_CONFIG_STATS = false;

    /**
     *  If true, a single JavaLexer will be used, and {@link Lexer#setInputStream} will be called to initialize it
     *  for each source file. In this mode, the cached DFA will be persisted throughout the lexing process.
     */
    private static final boolean REUSE_LEXER = true;
    /**
     *  If true, a single JavaParser will be used, and {@link Parser#setInputStream} will be called to initialize it
     *  for each source file. In this mode, the cached DFA will be persisted throughout the parsing process.
     */
    private static final boolean REUSE_PARSER = true;
    /**
     * If true, the shared lexer and parser are reset after each pass. If false, all passes after the first will
     * be fully "warmed up", which makes them faster and can compare them to the first warm-up pass, but it will
     * not distinguish bytecode load/JIT time from warm-up time during the first pass.
     */
    private static final boolean CLEAR_DFA = false;

    /** Total number of passes to make over the source */
    private static final int PASSES = 4;

    private static Lexer sharedLexer;
    private static Parser sharedParser;
    @SuppressWarnings({"FieldCanBeLocal"})
    private static ParseTreeListener<Token> sharedListener;

    private int tokenCount;
    private int currentPass;

    @Test
    //@Ignore
    public void compileJdk() throws IOException {
        compileParser(USE_LR_GRAMMAR);
        JavaParserFactory factory = getParserFactory();
        String jdkSourceRoot = System.getenv("JDK_SOURCE_ROOT");
        if (jdkSourceRoot == null) {
            jdkSourceRoot = System.getProperty("JDK_SOURCE_ROOT");
        }
        if (jdkSourceRoot == null) {
            System.err.println("The JDK_SOURCE_ROOT environment variable must be set for performance testing.");
            return;
        }

        if (!TOP_PACKAGE.isEmpty()) {
            jdkSourceRoot = jdkSourceRoot + '/' + TOP_PACKAGE.replace('.', '/');
        }

        File directory = new File(jdkSourceRoot);
        assertTrue(directory.isDirectory());

        Collection<CharStream> sources = loadSources(directory, RECURSIVE);

        System.out.print(getOptionsDescription());

        currentPass = 0;
        parse1(factory, sources);
        for (int i = 0; i < PASSES - 1; i++) {
            currentPass = i + 1;
            if (CLEAR_DFA) {
                sharedLexer = null;
                sharedParser = null;
            }

            parse2(factory, sources);
        }

		sources.clear();
		if (PAUSE_FOR_HEAP_DUMP) {
			System.gc();
			System.out.println("Pausing before application exit.");
			try {
				Thread.sleep(4000);
			} catch (InterruptedException ex) {
				Logger.getLogger(TestPerformance.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
    }

    @Override
    protected void eraseTempDir() {
        if (DELETE_TEMP_FILES) {
            super.eraseTempDir();
        }
    }

    public static String getOptionsDescription() {
        StringBuilder builder = new StringBuilder();
        builder.append("Input=");
        if (TestPerformance.TOP_PACKAGE.isEmpty()) {
            builder.append("*");
        }
        else {
            builder.append(TOP_PACKAGE).append(".*");
        }

        builder.append(", Grammar=").append(USE_LR_GRAMMAR ? "LR" : "Standard");
        builder.append(", ForceAtn=").append(FORCE_ATN);

        builder.append('\n');

        builder.append("Op=Lex").append(RUN_PARSER ? "+Parse" : " only");
        builder.append(", Strategy=").append(BAIL_ON_ERROR ? BailErrorStrategy.class.getSimpleName() : DefaultErrorStrategy.class.getSimpleName());
        builder.append(", BuildParseTree=").append(BUILD_PARSE_TREES);
        builder.append(", WalkBlankListener=").append(BLANK_LISTENER);

        builder.append('\n');

        builder.append(", Lexer=").append(REUSE_LEXER ? "setInputStream" : "newInstance");
        builder.append(", Parser=").append(REUSE_PARSER ? "setInputStream" : "newInstance");
        builder.append(", AfterPass=").append(CLEAR_DFA ? "newInstance" : "setInputStream");

        builder.append('\n');

        return builder.toString();
    }

    /**
     *  This method is separate from {@link #parse2} so the first pass can be distinguished when analyzing
     *  profiler results.
     */
    protected void parse1(JavaParserFactory factory, Collection<CharStream> sources) {
        System.gc();
        parseSources(factory, sources);
    }

    /**
     *  This method is separate from {@link #parse1} so the first pass can be distinguished when analyzing
     *  profiler results.
     */
    protected void parse2(JavaParserFactory factory, Collection<CharStream> sources) {
        System.gc();
        parseSources(factory, sources);
    }

    protected Collection<CharStream> loadSources(File directory, boolean recursive) {
        Collection<CharStream> result = new ArrayList<CharStream>();
        loadSources(directory, recursive, result);
        return result;
    }

    protected void loadSources(File directory, boolean recursive, Collection<CharStream> result) {
        assert directory.isDirectory();

        File[] sources = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".java");
            }
        });
        for (File file : sources) {
            try {
                CharStream input = new ANTLRFileStream(file.getAbsolutePath());
                result.add(input);
            } catch (IOException ex) {

            }
        }

        if (recursive) {
            File[] children = directory.listFiles();
            for (File child : children) {
                if (child.isDirectory()) {
                    loadSources(child, true, result);
                }
            }
        }
    }

    int configOutputSize = 0;

    protected void parseSources(JavaParserFactory factory, Collection<CharStream> sources) {
        long startTime = System.currentTimeMillis();
        tokenCount = 0;
        int inputSize = 0;

        for (CharStream input : sources) {
            input.seek(0);
            inputSize += input.size();
            // this incurred a great deal of overhead and was causing significant variations in performance results.
            //System.out.format("Parsing file %s\n", input.getSourceName());
            try {
                factory.parseFile(input);
            } catch (IllegalStateException ex) {
                ex.printStackTrace(System.out);
            }
        }

        System.out.format("Total parse time for %d files (%d KB, %d tokens): %dms\n",
                          sources.size(),
                          inputSize / 1024,
                          tokenCount,
                          System.currentTimeMillis() - startTime);

        if (RUN_PARSER) {
            // make sure the individual DFAState objects actually have unique ATNConfig arrays
            final ParserATNSimulator<?> interpreter = sharedParser.getInterpreter();
            final DFA[] decisionToDFA = interpreter.decisionToDFA;

            if (SHOW_DFA_STATE_STATS) {
                int states = 0;

                for (int i = 0; i < decisionToDFA.length; i++) {
                    DFA dfa = decisionToDFA[i];
                    if (dfa == null || dfa.states == null) {
                        continue;
                    }

                    states += dfa.states.size();
                }

                System.out.format("There are %d DFAState instances.\n", states);
            }

            int localDfaCount = 0;
            int globalDfaCount = 0;
            int localConfigCount = 0;
            int globalConfigCount = 0;
            int[] contextsInDFAState = new int[0];

            for (int i = 0; i < decisionToDFA.length; i++) {
                DFA dfa = decisionToDFA[i];
                if (dfa == null || dfa.states == null) {
                    continue;
                }

                if (SHOW_CONFIG_STATS) {
                    for (DFAState state : dfa.states.keySet()) {
                        if (state.configset.size() >= contextsInDFAState.length) {
                            contextsInDFAState = Arrays.copyOf(contextsInDFAState, state.configset.size() + 1);
                        }

                        if (state.isAcceptState) {
                            boolean hasGlobal = false;
                            for (ATNConfig config : state.configset) {
                                if (config.reachesIntoOuterContext > 0) {
                                    globalConfigCount++;
                                    hasGlobal = true;
                                } else {
                                    localConfigCount++;
                                }
                            }

                            if (hasGlobal) {
                                globalDfaCount++;
                            } else {
                                localDfaCount++;
                            }
                        }

                        contextsInDFAState[state.configset.size()]++;
                    }
                }

                if (EXPORT_LARGEST_CONFIG_CONTEXTS) {
                    for (DFAState state : dfa.states.keySet()) {
                        for (ATNConfig config : state.configset) {
                            String configOutput = config.toDotString();
                            if (configOutput.length() <= configOutputSize) {
                                continue;
                            }

                            configOutputSize = configOutput.length();
                            writeFile(tmpdir, "d" + dfa.decision + ".s" + state.stateNumber + ".a" + config.alt + ".config.dot", configOutput);
                        }
                    }
                }
            }

            if (SHOW_CONFIG_STATS && currentPass == 0) {
                System.out.format("  DFA accept states: %d total, %d with only local context, %d with a global context\n", localDfaCount + globalDfaCount, localDfaCount, globalDfaCount);
                System.out.format("  Config stats: %d total, %d local, %d global\n", localConfigCount + globalConfigCount, localConfigCount, globalConfigCount);
                if (SHOW_DFA_STATE_STATS) {
                    for (int i = 0; i < contextsInDFAState.length; i++) {
                        if (contextsInDFAState[i] != 0) {
                            System.out.format("  %d configs = %d\n", i, contextsInDFAState[i]);
                        }
                    }
                }
            }
        }
    }

    protected void compileParser(boolean leftRecursive) throws IOException {
        String grammarFileName = "Java.g";
        String sourceName = leftRecursive ? "Java-LR.g" : "Java.g";
        String body = load(sourceName, null);
        @SuppressWarnings({"ConstantConditions"})
        List<String> extraOptions = new ArrayList<String>();
        if (FORCE_ATN) {
            extraOptions.add("-Xforce-atn");
        }
        if (EXPORT_ATN_GRAPHS) {
            extraOptions.add("-atn");
        }
        String[] extraOptionsArray = extraOptions.toArray(new String[extraOptions.size()]);
        boolean success = rawGenerateAndBuildRecognizer(grammarFileName, body, "JavaParser", "JavaLexer", extraOptionsArray);
        assertTrue(success);
    }

    protected String load(String fileName, @Nullable String encoding)
        throws IOException
    {
        if ( fileName==null ) {
            return null;
        }

        String fullFileName = getClass().getPackage().getName().replace('.', '/') + '/' + fileName;
        int size = 65000;
        InputStreamReader isr;
        InputStream fis = getClass().getClassLoader().getResourceAsStream(fullFileName);
        if ( encoding!=null ) {
            isr = new InputStreamReader(fis, encoding);
        }
        else {
            isr = new InputStreamReader(fis);
        }
        try {
            char[] data = new char[size];
            int n = isr.read(data);
            return new String(data, 0, n);
        }
        finally {
            isr.close();
        }
    }

    protected JavaParserFactory getParserFactory() {
        try {
            ClassLoader loader = new URLClassLoader(new URL[] { new File(tmpdir).toURI().toURL() }, ClassLoader.getSystemClassLoader());
            @SuppressWarnings({"unchecked"})
            final Class<? extends Lexer> lexerClass = (Class<? extends Lexer>)loader.loadClass("JavaLexer");
            @SuppressWarnings({"unchecked"})
            final Class<? extends Parser> parserClass = (Class<? extends Parser>)loader.loadClass("JavaParser");
            @SuppressWarnings({"unchecked"})
            final Class<? extends ParseTreeListener<Token>> listenerClass = (Class<? extends ParseTreeListener<Token>>)loader.loadClass("BlankJavaListener");
            TestPerformance.sharedListener = listenerClass.newInstance();

            final Constructor<? extends Lexer> lexerCtor = lexerClass.getConstructor(CharStream.class);
            final Constructor<? extends Parser> parserCtor = parserClass.getConstructor(TokenStream.class);

            // construct initial instances of the lexer and parser to deserialize their ATNs
            lexerCtor.newInstance(new ANTLRInputStream(""));
            parserCtor.newInstance(new CommonTokenStream());

            return new JavaParserFactory() {
                @SuppressWarnings({"PointlessBooleanExpression"})
                @Override
                public void parseFile(CharStream input) {
                    try {
                        if (REUSE_LEXER && sharedLexer != null) {
                            sharedLexer.setInputStream(input);
                        } else {
                            sharedLexer = lexerCtor.newInstance(input);
                        }

                        CommonTokenStream tokens = new CommonTokenStream(sharedLexer);
                        tokens.fill();
                        tokenCount += tokens.size();

                        if (!RUN_PARSER) {
                            return;
                        }

                        if (REUSE_PARSER && sharedParser != null) {
                            sharedParser.setInputStream(tokens);
                        } else {
                            sharedParser = parserCtor.newInstance(tokens);
                            sharedParser.getInterpreter().disable_global_context = DISABLE_GLOBAL_CONTEXT;
                            sharedParser.getInterpreter().force_global_context = FORCE_GLOBAL_CONTEXT;
                            sharedParser.getInterpreter().always_try_local_context = TRY_LOCAL_CONTEXT_FIRST;
                            sharedParser.setBuildParseTree(BUILD_PARSE_TREES);
                            if (!BUILD_PARSE_TREES && BLANK_LISTENER) {
                                sharedParser.addParseListener(sharedListener);
                            }
                            if (BAIL_ON_ERROR) {
                                sharedParser.setErrorHandler(new BailErrorStrategy());
                            }
                        }

                        Method parseMethod = parserClass.getMethod("compilationUnit");
                        Object parseResult = parseMethod.invoke(sharedParser);
                        Assert.assertTrue(parseResult instanceof ParseTree);

                        if (BUILD_PARSE_TREES && BLANK_LISTENER) {
                            ParseTreeWalker.DEFAULT.walk(sharedListener, (ParseTree)parseResult);
                        }
                    } catch (Exception e) {
                        e.printStackTrace(System.out);
                        throw new IllegalStateException(e);
                    }
                }
            };
        } catch (Exception e) {
            e.printStackTrace(System.out);
            lastTestFailed = true;
            Assert.fail(e.getMessage());
            throw new IllegalStateException(e);
        }
    }

    protected interface JavaParserFactory {
        void parseFile(CharStream input);
    }
}
