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

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNConfig;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.atn.ATNSimulator;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.SimulatorState;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.dfa.DFAState;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestPerformance extends BaseTest {
    /**
     * Parse all java files under this package within the JDK_SOURCE_ROOT
     * (environment variable or property defined on the Java command line).
     */
    private static final String TOP_PACKAGE = "java.lang";
    /**
     * {@code true} to load java files from sub-packages of
     * {@link #TOP_PACKAGE}.
     */
    private static final boolean RECURSIVE = true;

    /**
     * {@code true} to use the Java grammar with expressions in the v4
     * left-recursive syntax (Java-LR.g4). {@code false} to use the standard
     * grammar (Java.g4). In either case, the grammar is renamed in the
     * temporary directory to Java.g4 before compiling.
     */
    private static final boolean USE_LR_GRAMMAR = true;
    /**
     * {@code true} to specify the {@code -Xforce-atn} option when generating
     * the grammar, forcing all decisions in {@code JavaParser} to be handled by
     * {@link ParserATNSimulator#adaptivePredict}.
     */
    private static final boolean FORCE_ATN = false;
    /**
     * {@code true} to specify the {@code -atn} option when generating the
     * grammar. This will cause ANTLR to export the ATN for each decision as a
     * DOT (GraphViz) file.
     */
    private static final boolean EXPORT_ATN_GRAPHS = true;
    /**
     * {@code true} to delete temporary (generated and compiled) files when the
     * test completes.
     */
    private static final boolean DELETE_TEMP_FILES = true;

    private static final boolean PAUSE_FOR_HEAP_DUMP = false;

    /**
     * Parse each file with {@code JavaParser.compilationUnit}.
     */
    private static final boolean RUN_PARSER = true;
    /**
     * {@code true} to use {@link BailErrorStrategy}, {@code false} to use
     * {@link DefaultErrorStrategy}.
     */
    private static final boolean BAIL_ON_ERROR = true;
    /**
     * This value is passed to {@link Parser#setBuildParseTree}.
     */
    private static final boolean BUILD_PARSE_TREES = false;
    /**
     * Use
     * {@link ParseTreeWalker#DEFAULT}{@code .}{@link ParseTreeWalker#walk walk}
     * with the {@code BlankJavaParserListener} to show parse tree walking
     * overhead. If {@link #BUILD_PARSE_TREES} is {@code false}, the listener
     * will instead be called during the parsing process via
     * {@link Parser#addParseListener}.
     */
    private static final boolean BLANK_LISTENER = false;

    private static final boolean EXPORT_LARGEST_CONFIG_CONTEXTS = false;

    private static final boolean SHOW_DFA_STATE_STATS = true;

	private static final boolean ENABLE_LEXER_DFA = true;

	private static final boolean ENABLE_PARSER_DFA = true;

    private static final boolean DISABLE_GLOBAL_CONTEXT = false;
    private static final boolean FORCE_GLOBAL_CONTEXT = false;
    private static final boolean TRY_LOCAL_CONTEXT_FIRST = true;
	private static final boolean OPTIMIZE_LL1 = true;
	private static final boolean OPTIMIZE_UNIQUE_CLOSURE = true;
	private static final boolean OPTIMIZE_HIDDEN_CONFLICTED_CONFIGS = true;
	private static final boolean OPTIMIZE_TAIL_CALLS = true;

	private static final boolean TWO_STAGE_PARSING = true;

    private static final boolean SHOW_CONFIG_STATS = false;

	private static final boolean REPORT_SYNTAX_ERRORS = true;
	private static final boolean REPORT_AMBIGUITIES = false;
	private static final boolean REPORT_FULL_CONTEXT = false;
	private static final boolean REPORT_CONTEXT_SENSITIVITY = REPORT_FULL_CONTEXT;

    /**
     * If {@code true}, a single {@code JavaLexer} will be used, and
     * {@link Lexer#setInputStream} will be called to initialize it for each
     * source file. Otherwise, a new instance will be created for each file.
     */
    private static final boolean REUSE_LEXER = true;
	/**
	 * If {@code true}, a single DFA will be used for lexing which is shared
	 * across all threads and files. Otherwise, each file will be lexed with its
	 * own DFA which is accomplished by creating one ATN instance per thread and
	 * clearing its DFA cache before lexing each file.
	 */
	private static final boolean REUSE_LEXER_DFA = true;
    /**
     * If {@code true}, a single {@code JavaParser} will be used, and
     * {@link Parser#setInputStream} will be called to initialize it for each
     * source file. Otherwise, a new instance will be created for each file.
     */
    private static final boolean REUSE_PARSER = true;
	/**
	 * If {@code true}, a single DFA will be used for parsing which is shared
	 * across all threads and files. Otherwise, each file will be parsed with
	 * its own DFA which is accomplished by creating one ATN instance per thread
	 * and clearing its DFA cache before parsing each file.
	 */
	private static final boolean REUSE_PARSER_DFA = true;
    /**
     * If {@code true}, the shared lexer and parser are reset after each pass.
     * If {@code false}, all passes after the first will be fully "warmed up",
     * which makes them faster and can compare them to the first warm-up pass,
     * but it will not distinguish bytecode load/JIT time from warm-up time
     * during the first pass.
     */
    private static final boolean CLEAR_DFA = false;
    /**
     * Total number of passes to make over the source.
     */
    private static final int PASSES = 4;

	/**
	 * Number of parser threads to use.
	 */
	private static final int NUMBER_OF_THREADS = 1;

    private static final Lexer[] sharedLexers = new Lexer[NUMBER_OF_THREADS];
	private static final ATN[] sharedLexerATNs = new ATN[NUMBER_OF_THREADS];

	@SuppressWarnings("unchecked")
    private static final Parser<Token>[] sharedParsers = (Parser<Token>[])new Parser<?>[NUMBER_OF_THREADS];
	private static final ATN[] sharedParserATNs = new ATN[NUMBER_OF_THREADS];

	@SuppressWarnings("unchecked")
    private static final ParseTreeListener<Token>[] sharedListeners = (ParseTreeListener<Token>[])new ParseTreeListener<?>[NUMBER_OF_THREADS];

    private final AtomicInteger tokenCount = new AtomicInteger();
    private int currentPass;

    @Test
    //@org.junit.Ignore
    public void compileJdk() throws IOException, InterruptedException {
        String jdkSourceRoot = getSourceRoot("JDK");
		assertTrue("The JDK_SOURCE_ROOT environment variable must be set for performance testing.", jdkSourceRoot != null && !jdkSourceRoot.isEmpty());

        compileJavaParser(USE_LR_GRAMMAR);
		final String lexerName = "JavaLexer";
		final String parserName = "JavaParser";
		final String listenerName = "JavaBaseListener";
		final String entryPoint = "compilationUnit";
        ParserFactory factory = getParserFactory(lexerName, parserName, listenerName, entryPoint);

        if (!TOP_PACKAGE.isEmpty()) {
            jdkSourceRoot = jdkSourceRoot + '/' + TOP_PACKAGE.replace('.', '/');
        }

        File directory = new File(jdkSourceRoot);
        assertTrue(directory.isDirectory());

        Collection<CharStream> sources = loadSources(directory, new FileExtensionFilenameFilter(".java"), RECURSIVE);

		System.out.print(getOptionsDescription(TOP_PACKAGE));

        currentPass = 0;
        parse1(factory, sources);
        for (int i = 0; i < PASSES - 1; i++) {
            currentPass = i + 1;
            if (CLEAR_DFA) {
				if (sharedLexers.length > 0) {
					sharedLexers[0].getATN().clearDFA();
				}

				if (sharedParsers.length > 0) {
					sharedParsers[0].getATN().clearDFA();
				}

				Arrays.fill(sharedLexers, null);
				Arrays.fill(sharedParsers, null);
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

	private String getSourceRoot(String prefix) {
		String sourceRoot = System.getenv(prefix+"_SOURCE_ROOT");
		if (sourceRoot == null) {
			sourceRoot = System.getProperty(prefix+"_SOURCE_ROOT");
		}

		return sourceRoot;
	}

    @Override
    protected void eraseTempDir() {
        if (DELETE_TEMP_FILES) {
            super.eraseTempDir();
        }
    }

    public static String getOptionsDescription(String topPackage) {
        StringBuilder builder = new StringBuilder();
        builder.append("Input=");
        if (topPackage.isEmpty()) {
            builder.append("*");
        }
        else {
            builder.append(topPackage).append(".*");
        }

        builder.append(", Grammar=").append(USE_LR_GRAMMAR ? "LR" : "Standard");
        builder.append(", ForceAtn=").append(FORCE_ATN);
		builder.append(", Lexer:").append(ENABLE_LEXER_DFA ? "DFA" : "ATN");
		builder.append(", Parser:").append(ENABLE_PARSER_DFA ? "DFA" : "ATN");

        builder.append('\n');

        builder.append("Op=Lex").append(RUN_PARSER ? "+Parse" : " only");
        builder.append(", Strategy=").append(BAIL_ON_ERROR ? BailErrorStrategy.class.getSimpleName() : DefaultErrorStrategy.class.getSimpleName());
        builder.append(", BuildParseTree=").append(BUILD_PARSE_TREES);
        builder.append(", WalkBlankListener=").append(BLANK_LISTENER);

        builder.append('\n');

        builder.append("Lexer=").append(REUSE_LEXER ? "setInputStream" : "newInstance");
        builder.append(", Parser=").append(REUSE_PARSER ? "setInputStream" : "newInstance");
        builder.append(", AfterPass=").append(CLEAR_DFA ? "newInstance" : "setInputStream");

        builder.append('\n');

		builder.append("UniqueClosure=").append(OPTIMIZE_UNIQUE_CLOSURE ? "optimize" : "complete");

		builder.append('\n');

        return builder.toString();
    }

    /**
     *  This method is separate from {@link #parse2} so the first pass can be distinguished when analyzing
     *  profiler results.
     */
    protected void parse1(ParserFactory factory, Collection<CharStream> sources) throws InterruptedException {
        System.gc();
        parseSources(factory, sources);
    }

    /**
     *  This method is separate from {@link #parse1} so the first pass can be distinguished when analyzing
     *  profiler results.
     */
    protected void parse2(ParserFactory factory, Collection<CharStream> sources) throws InterruptedException {
        System.gc();
        parseSources(factory, sources);
    }

    protected Collection<CharStream> loadSources(File directory, FilenameFilter filter, boolean recursive) {
		return loadSources(directory, filter, null, recursive);
	}

    protected Collection<CharStream> loadSources(File directory, FilenameFilter filter, String encoding, boolean recursive) {
        Collection<CharStream> result = new ArrayList<CharStream>();
        loadSources(directory, filter, encoding, recursive, result);
        return result;
    }

    protected void loadSources(File directory, FilenameFilter filter, String encoding, boolean recursive, Collection<CharStream> result) {
        assert directory.isDirectory();

        File[] sources = directory.listFiles(filter);
        for (File file : sources) {
            try {
                CharStream input = new ANTLRFileStream(file.getAbsolutePath(), encoding);
                result.add(input);
            } catch (IOException ex) {

            }
        }

        if (recursive) {
            File[] children = directory.listFiles();
            for (File child : children) {
                if (child.isDirectory()) {
                    loadSources(child, filter, encoding, true, result);
                }
            }
        }
    }

    int configOutputSize = 0;

    @SuppressWarnings("unused")
	protected void parseSources(final ParserFactory factory, Collection<CharStream> sources) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        tokenCount.set(0);
        int inputSize = 0;

		ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS, new NumberedThreadFactory());
        for (final CharStream input : sources) {
            input.seek(0);
            inputSize += input.size();
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					// this incurred a great deal of overhead and was causing significant variations in performance results.
					//System.out.format("Parsing file %s\n", input.getSourceName());
					try {
						factory.parseFile(input, ((NumberedThread)Thread.currentThread()).getThreadNumber());
					} catch (IllegalStateException ex) {
						ex.printStackTrace(System.err);
					} catch (Throwable t) {
						t.printStackTrace(System.err);
					}
				}
			});
        }

		executorService.shutdown();
		executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        System.out.format("Total parse time for %d files (%d KB, %d tokens): %dms\n",
                          sources.size(),
                          inputSize / 1024,
                          tokenCount.get(),
                          System.currentTimeMillis() - startTime);

		if (sharedLexers.length > 0) {
			Lexer lexer = sharedLexers[0];
			final LexerATNSimulator lexerInterpreter = lexer.getInterpreter();
			final DFA[] modeToDFA = lexerInterpreter.atn.modeToDFA;
			if (SHOW_DFA_STATE_STATS) {
				int states = 0;
				int configs = 0;
				Set<ATNConfig> uniqueConfigs = new HashSet<ATNConfig>();

				for (int i = 0; i < modeToDFA.length; i++) {
					DFA dfa = modeToDFA[i];
					if (dfa == null || dfa.states == null) {
						continue;
					}

					states += dfa.states.size();
					for (DFAState state : dfa.states.values()) {
						configs += state.configset.size();
						uniqueConfigs.addAll(state.configset);
					}
				}

				System.out.format("There are %d lexer DFAState instances, %d configs (%d unique), %d prediction contexts.\n", states, configs, uniqueConfigs.size(), lexerInterpreter.atn.getContextCacheSize());
			}
		}

		if (RUN_PARSER && sharedParsers.length > 0) {
			Parser<?> parser = sharedParsers[0];
            // make sure the individual DFAState objects actually have unique ATNConfig arrays
			final ParserATNSimulator<?> interpreter = parser.getInterpreter();
            final DFA[] decisionToDFA = interpreter.atn.decisionToDFA;

            if (SHOW_DFA_STATE_STATS) {
                int states = 0;
				int configs = 0;
				Set<ATNConfig> uniqueConfigs = new HashSet<ATNConfig>();

                for (int i = 0; i < decisionToDFA.length; i++) {
                    DFA dfa = decisionToDFA[i];
                    if (dfa == null || dfa.states == null) {
                        continue;
                    }

                    states += dfa.states.size();
					for (DFAState state : dfa.states.values()) {
						configs += state.configset.size();
						uniqueConfigs.addAll(state.configset);
					}
                }

                System.out.format("There are %d parser DFAState instances, %d configs (%d unique), %d prediction contexts.\n", states, configs, uniqueConfigs.size(), interpreter.atn.getContextCacheSize());
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
                                if (config.getReachesIntoOuterContext()) {
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
                            writeFile(tmpdir, "d" + dfa.decision + ".s" + state.stateNumber + ".a" + config.getAlt() + ".config.dot", configOutput);
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

    protected void compileJavaParser(boolean leftRecursive) throws IOException {
        String grammarFileName = "Java.g4";
        String sourceName = leftRecursive ? "Java-LR.g4" : "Java.g4";
        String body = load(sourceName, null);
        List<String> extraOptions = new ArrayList<String>();
        if (FORCE_ATN) {
            extraOptions.add("-Xforce-atn");
        }
        if (EXPORT_ATN_GRAPHS) {
            extraOptions.add("-atn");
        }
		extraOptions.add("-visitor");
        String[] extraOptionsArray = extraOptions.toArray(new String[extraOptions.size()]);
        boolean success = rawGenerateAndBuildRecognizer(grammarFileName, body, "JavaParser", "JavaLexer", true, extraOptionsArray);
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

    protected ParserFactory getParserFactory(String lexerName, String parserName, String listenerName, final String entryPoint) {
        try {
            ClassLoader loader = new URLClassLoader(new URL[] { new File(tmpdir).toURI().toURL() }, ClassLoader.getSystemClassLoader());
            final Class<? extends Lexer> lexerClass = loader.loadClass(lexerName).asSubclass(Lexer.class);
			@SuppressWarnings("rawtypes")
            final Class<? extends Parser> parserClass = loader.loadClass(parserName).asSubclass(Parser.class);
            @SuppressWarnings("unchecked")
            final Class<? extends ParseTreeListener<Token>> listenerClass = (Class<? extends ParseTreeListener<Token>>)loader.loadClass(listenerName).asSubclass(ParseTreeListener.class);

            final Constructor<? extends Lexer> lexerCtor = lexerClass.getConstructor(CharStream.class);
			@SuppressWarnings("rawtypes")
            final Constructor<? extends Parser> parserCtor = parserClass.getConstructor(TokenStream.class);

            // construct initial instances of the lexer and parser to deserialize their ATNs
            TokenSource<Token> tokenSource = lexerCtor.newInstance(new ANTLRInputStream(""));
            parserCtor.newInstance(new CommonTokenStream(tokenSource));

			if (!REUSE_LEXER_DFA) {
				Field lexerSerializedATNField = lexerClass.getField("_serializedATN");
				String lexerSerializedATN = (String)lexerSerializedATNField.get(null);
				for (int i = 0; i < NUMBER_OF_THREADS; i++) {
					sharedLexerATNs[i] = ATNSimulator.deserialize(lexerSerializedATN.toCharArray());
				}
			}

			if (RUN_PARSER && !REUSE_PARSER_DFA) {
				Field parserSerializedATNField = parserClass.getField("_serializedATN");
				String parserSerializedATN = (String)parserSerializedATNField.get(null);
				for (int i = 0; i < NUMBER_OF_THREADS; i++) {
					sharedParserATNs[i] = ATNSimulator.deserialize(parserSerializedATN.toCharArray());
				}
			}

            return new ParserFactory() {
                @SuppressWarnings("unused")
				@Override
                public void parseFile(CharStream input, int thread) {
					assert thread >= 0 && thread < NUMBER_OF_THREADS;

                    try {
						if (sharedListeners[thread] == null) {
							sharedListeners[thread] = listenerClass.newInstance();
						}

                        if (REUSE_LEXER && sharedLexers[thread] != null) {
                            sharedLexers[thread].setInputStream(input);
                        } else {
                            sharedLexers[thread] = lexerCtor.newInstance(input);
							if (!ENABLE_LEXER_DFA) {
								sharedLexers[thread].setInterpreter(new NonCachingLexerATNSimulator(sharedLexers[thread], sharedLexers[thread].getATN()));
							} else if (!REUSE_LEXER_DFA) {
								sharedLexers[thread].setInterpreter(new LexerATNSimulator(sharedLexers[thread], sharedLexerATNs[thread]));
							}
                        }

						sharedLexers[thread].getInterpreter().optimize_tail_calls = OPTIMIZE_TAIL_CALLS;
						if (ENABLE_LEXER_DFA && !REUSE_LEXER_DFA) {
							sharedLexers[thread].getInterpreter().atn.clearDFA();
						}

                        CommonTokenStream tokens = new CommonTokenStream(sharedLexers[thread]);
                        tokens.fill();
                        tokenCount.addAndGet(tokens.size());

                        if (!RUN_PARSER) {
                            return;
                        }

                        if (REUSE_PARSER && sharedParsers[thread] != null) {
                            sharedParsers[thread].setInputStream(tokens);
                        } else {
							@SuppressWarnings("unchecked")
							Parser<Token> parser = parserCtor.newInstance(tokens);
                            sharedParsers[thread] = parser;
                        }

						sharedParsers[thread].removeErrorListeners();
						if (!TWO_STAGE_PARSING) {
							sharedParsers[thread].addErrorListener(DescriptiveErrorListener.INSTANCE);
							sharedParsers[thread].addErrorListener(new SummarizingDiagnosticErrorListener());
						}

						if (!ENABLE_PARSER_DFA) {
							sharedParsers[thread].setInterpreter(new NonCachingParserATNSimulator<Token>(sharedParsers[thread], sharedParsers[thread].getATN()));
						} else if (!REUSE_PARSER_DFA) {
							sharedParsers[thread].setInterpreter(new ParserATNSimulator<Token>(sharedParsers[thread], sharedParserATNs[thread]));
						}

						if (ENABLE_PARSER_DFA && !REUSE_PARSER_DFA) {
							sharedParsers[thread].getInterpreter().atn.clearDFA();
						}

						sharedParsers[thread].getInterpreter().disable_global_context = DISABLE_GLOBAL_CONTEXT || TWO_STAGE_PARSING;
						sharedParsers[thread].getInterpreter().force_global_context = FORCE_GLOBAL_CONTEXT && !TWO_STAGE_PARSING;
						sharedParsers[thread].getInterpreter().always_try_local_context = TRY_LOCAL_CONTEXT_FIRST || TWO_STAGE_PARSING;
						sharedParsers[thread].getInterpreter().optimize_ll1 = OPTIMIZE_LL1;
						sharedParsers[thread].getInterpreter().optimize_unique_closure = OPTIMIZE_UNIQUE_CLOSURE;
						sharedParsers[thread].getInterpreter().optimize_hidden_conflicted_configs = OPTIMIZE_HIDDEN_CONFLICTED_CONFIGS;
						sharedParsers[thread].getInterpreter().optimize_tail_calls = OPTIMIZE_TAIL_CALLS;
						sharedParsers[thread].setBuildParseTree(BUILD_PARSE_TREES);
						if (!BUILD_PARSE_TREES && BLANK_LISTENER) {
							sharedParsers[thread].addParseListener(sharedListeners[thread]);
						}
						if (BAIL_ON_ERROR || TWO_STAGE_PARSING) {
							sharedParsers[thread].setErrorHandler(new BailErrorStrategy<Token>());
						}

                        Method parseMethod = parserClass.getMethod(entryPoint);
                        Object parseResult;

						try {
							parseResult = parseMethod.invoke(sharedParsers[thread]);
						} catch (InvocationTargetException ex) {
							if (!TWO_STAGE_PARSING) {
								throw ex;
							}

							String sourceName = tokens.getSourceName();
							sourceName = sourceName != null && !sourceName.isEmpty() ? sourceName+": " : "";
							System.err.println(sourceName+"Forced to retry with full context.");

							if (!(ex.getCause() instanceof RuntimeException) || !(ex.getCause().getCause() instanceof RecognitionException)) {
								throw ex;
							}

							tokens.reset();
							if (REUSE_PARSER && sharedParsers[thread] != null) {
								sharedParsers[thread].setInputStream(tokens);
							} else {
								@SuppressWarnings("unchecked")
								Parser<Token> parser = parserCtor.newInstance(tokens);
								sharedParsers[thread] = parser;
							}

							sharedParsers[thread].removeErrorListeners();
							sharedParsers[thread].addErrorListener(DescriptiveErrorListener.INSTANCE);
							sharedParsers[thread].addErrorListener(new SummarizingDiagnosticErrorListener());
							if (!ENABLE_PARSER_DFA) {
								sharedParsers[thread].setInterpreter(new NonCachingParserATNSimulator<Token>(sharedParsers[thread], sharedParsers[thread].getATN()));
							}
							sharedParsers[thread].getInterpreter().disable_global_context = false;
							sharedParsers[thread].getInterpreter().force_global_context = FORCE_GLOBAL_CONTEXT;
							sharedParsers[thread].getInterpreter().always_try_local_context = TRY_LOCAL_CONTEXT_FIRST;
							sharedParsers[thread].getInterpreter().optimize_ll1 = OPTIMIZE_LL1;
							sharedParsers[thread].getInterpreter().optimize_unique_closure = OPTIMIZE_UNIQUE_CLOSURE;
							sharedParsers[thread].getInterpreter().optimize_hidden_conflicted_configs = OPTIMIZE_HIDDEN_CONFLICTED_CONFIGS;
							sharedParsers[thread].getInterpreter().optimize_tail_calls = OPTIMIZE_TAIL_CALLS;
							sharedParsers[thread].setBuildParseTree(BUILD_PARSE_TREES);
							if (!BUILD_PARSE_TREES && BLANK_LISTENER) {
								sharedParsers[thread].addParseListener(sharedListeners[thread]);
							}
							if (BAIL_ON_ERROR) {
								sharedParsers[thread].setErrorHandler(new BailErrorStrategy<Token>());
							}

							parseResult = parseMethod.invoke(sharedParsers[thread]);
						}

                        Assert.assertTrue(parseResult instanceof ParseTree);

                        if (BUILD_PARSE_TREES && BLANK_LISTENER) {
                            ParseTreeWalker.DEFAULT.walk(sharedListeners[thread], (ParserRuleContext<?>)parseResult);
                        }
                    } catch (Exception e) {
						if (BAIL_ON_ERROR && !REPORT_SYNTAX_ERRORS) {
							if ((e.getCause() instanceof RuntimeException) && (e.getCause().getCause() instanceof RecognitionException)) {
								return;
							}
						}

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

    protected interface ParserFactory {
        void parseFile(CharStream input, int thread);
    }

	private static class DescriptiveErrorListener extends BaseErrorListener<Token> {
		public static DescriptiveErrorListener INSTANCE = new DescriptiveErrorListener();

		@Override
		public <T extends Token> void syntaxError(Recognizer<T, ?> recognizer, T offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
			if (!REPORT_SYNTAX_ERRORS) {
				return;
			}

			String sourceName = recognizer.getInputStream().getSourceName();
			sourceName = sourceName != null && !sourceName.isEmpty() ? sourceName+": " : "";
			System.err.println(sourceName+"line "+line+":"+charPositionInLine+" "+msg);
		}

	}

	private static class SummarizingDiagnosticErrorListener extends DiagnosticErrorListener<Token> {

		@Override
		public void reportAmbiguity(Parser<? extends Token> recognizer, DFA dfa, int startIndex, int stopIndex, BitSet ambigAlts, ATNConfigSet configs) {
			if (!REPORT_AMBIGUITIES) {
				return;
			}

			super.reportAmbiguity(recognizer, dfa, startIndex, stopIndex, ambigAlts, configs);
		}

		@Override
		public <T extends Token> void reportAttemptingFullContext(Parser<T> recognizer, DFA dfa, int startIndex, int stopIndex, SimulatorState<T> initialState) {
			if (!REPORT_FULL_CONTEXT) {
				return;
			}

			super.reportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, initialState);
		}

		@Override
		public <T extends Token> void reportContextSensitivity(Parser<T> recognizer, DFA dfa, int startIndex, int stopIndex, SimulatorState<T> acceptState) {
			if (!REPORT_CONTEXT_SENSITIVITY) {
				return;
			}

			super.reportContextSensitivity(recognizer, dfa, startIndex, stopIndex, acceptState);
		}

		@Override
		protected <T extends Token> String getDecisionDescription(Parser<T> recognizer, int decision) {
			String format = "%d(%s)";
			String ruleName = recognizer.getRuleNames()[recognizer.getATN().decisionToState.get(decision).ruleIndex];
			return String.format(format, decision, ruleName);
		}

	}

	protected static class FileExtensionFilenameFilter implements FilenameFilter {

		private final String extension;

		public FileExtensionFilenameFilter(String extension) {
			if (!extension.startsWith(".")) {
				extension = '.' + extension;
			}

			this.extension = extension;
		}

		@Override
		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith(extension);
		}

	}

	protected static class NonCachingLexerATNSimulator extends LexerATNSimulator {

		public NonCachingLexerATNSimulator(Lexer recog, ATN atn) {
			super(recog, atn);
		}

		@Override
		protected DFAState addDFAState(ATNConfigSet configs) {
			return null;
		}

	}

	protected static class NonCachingParserATNSimulator<Symbol extends Token> extends ParserATNSimulator<Symbol> {

		public NonCachingParserATNSimulator(Parser<Symbol> parser, ATN atn) {
			super(parser, atn);
		}

		@NotNull
		@Override
		protected DFAState createDFAState(@NotNull ATNConfigSet configs) {
			return new DFAState(configs, -1, -1);
		}

	}

	protected static class NumberedThread extends Thread {
		private final int threadNumber;

		public NumberedThread(Runnable target, int threadNumber) {
			super(target);
			this.threadNumber = threadNumber;
		}

		public final int getThreadNumber() {
			return threadNumber;
		}

	}

	protected static class NumberedThreadFactory implements ThreadFactory {
		private final AtomicInteger nextThread = new AtomicInteger();

		@Override
		public Thread newThread(Runnable r) {
			int threadNumber = nextThread.getAndIncrement();
			assert threadNumber < NUMBER_OF_THREADS;
			return new NumberedThread(r, threadNumber);
		}

	}

}
