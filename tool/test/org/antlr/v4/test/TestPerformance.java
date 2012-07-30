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
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.SimulatorState;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.dfa.DFAState;
import org.antlr.v4.runtime.misc.IntervalSet;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestPerformance extends BaseTest {
    /** Parse all java files under this package within the JDK_SOURCE_ROOT. */
    private static final String TOP_PACKAGE = "java.lang";
    /** True to load java files from sub-packages of {@link #TOP_PACKAGE}. */
    private static final boolean RECURSIVE = true;

    /**
     *  True to use the Java grammar with expressions in the v4 left-recursive syntax (Java-LR.g4). False to use
     *  the standard grammar (Java.g4). In either case, the grammar is renamed in the temporary directory to Java.g4
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

	private static final boolean ENABLE_LEXER_DFA = true;

	private static final boolean ENABLE_PARSER_DFA = true;

    private static final boolean DISABLE_GLOBAL_CONTEXT = false;
    private static final boolean FORCE_GLOBAL_CONTEXT = false;
    private static final boolean TRY_LOCAL_CONTEXT_FIRST = true;
	private static final boolean OPTIMIZE_UNIQUE_CLOSURE = true;

    private static final boolean SHOW_CONFIG_STATS = false;

	private static final boolean REPORT_AMBIGUITIES = false;
	private static final boolean REPORT_FULL_CONTEXT = false;
	private static final boolean REPORT_CONTEXT_SENSITIVITY = REPORT_FULL_CONTEXT;

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
    private static Parser<Token> sharedParser;
    private static ParseTreeListener<Token> sharedListener;

    private int tokenCount;
    private int currentPass;

    @Test
    //@org.junit.Ignore
    public void compileJdk() throws IOException {
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
    protected void parse1(ParserFactory factory, Collection<CharStream> sources) {
        System.gc();
        parseSources(factory, sources);
    }

    /**
     *  This method is separate from {@link #parse1} so the first pass can be distinguished when analyzing
     *  profiler results.
     */
    protected void parse2(ParserFactory factory, Collection<CharStream> sources) {
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
	protected void parseSources(ParserFactory factory, Collection<CharStream> sources) {
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

		final LexerATNSimulator lexerInterpreter = sharedLexer.getInterpreter();
		final DFA[] modeToDFA = lexerInterpreter.dfa;
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

			System.out.format("There are %d lexer DFAState instances, %d configs (%d unique).\n", states, configs, uniqueConfigs.size());
		}

        if (RUN_PARSER) {
            // make sure the individual DFAState objects actually have unique ATNConfig arrays
            final ParserATNSimulator<?> interpreter = sharedParser.getInterpreter();
            final DFA[] decisionToDFA = interpreter.decisionToDFA;

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

                System.out.format("There are %d parser DFAState instances, %d configs (%d unique).\n", states, configs, uniqueConfigs.size());
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
            TestPerformance.sharedListener = listenerClass.newInstance();

            final Constructor<? extends Lexer> lexerCtor = lexerClass.getConstructor(CharStream.class);
			@SuppressWarnings("rawtypes")
            final Constructor<? extends Parser> parserCtor = parserClass.getConstructor(TokenStream.class);

            // construct initial instances of the lexer and parser to deserialize their ATNs
            TokenSource<Token> tokenSource = lexerCtor.newInstance(new ANTLRInputStream(""));
            parserCtor.newInstance(new CommonTokenStream(tokenSource));

            return new ParserFactory() {
                @SuppressWarnings("unused")
				@Override
                public void parseFile(CharStream input) {
                    try {
                        if (REUSE_LEXER && sharedLexer != null) {
                            sharedLexer.setInputStream(input);
                        } else {
                            sharedLexer = lexerCtor.newInstance(input);
							if (!ENABLE_LEXER_DFA) {
								sharedLexer.setInterpreter(new NonCachingLexerATNSimulator(sharedLexer, sharedLexer.getATN()));
							}
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
							@SuppressWarnings("unchecked")
							Parser<Token> parser = parserCtor.newInstance(tokens);
                            sharedParser = parser;
							sharedParser.removeErrorListeners();
							sharedParser.addErrorListener(DescriptiveErrorListener.INSTANCE);
							sharedParser.addErrorListener(new SummarizingDiagnosticErrorListener());
							if (!ENABLE_PARSER_DFA) {
								sharedParser.setInterpreter(new NonCachingParserATNSimulator<Token>(sharedParser, sharedParser.getATN()));
							}
                            sharedParser.getInterpreter().disable_global_context = DISABLE_GLOBAL_CONTEXT;
                            sharedParser.getInterpreter().force_global_context = FORCE_GLOBAL_CONTEXT;
                            sharedParser.getInterpreter().always_try_local_context = TRY_LOCAL_CONTEXT_FIRST;
							sharedParser.getInterpreter().optimize_unique_closure = OPTIMIZE_UNIQUE_CLOSURE;
                            sharedParser.setBuildParseTree(BUILD_PARSE_TREES);
                            if (!BUILD_PARSE_TREES && BLANK_LISTENER) {
                                sharedParser.addParseListener(sharedListener);
                            }
                            if (BAIL_ON_ERROR) {
                                sharedParser.setErrorHandler(new BailErrorStrategy<Token>());
                            }
                        }

                        Method parseMethod = parserClass.getMethod(entryPoint);
                        Object parseResult = parseMethod.invoke(sharedParser);
                        Assert.assertTrue(parseResult instanceof ParseTree);

                        if (BUILD_PARSE_TREES && BLANK_LISTENER) {
                            ParseTreeWalker.DEFAULT.walk(sharedListener, (ParserRuleContext<?>)parseResult);
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

    protected interface ParserFactory {
        void parseFile(CharStream input);
    }

	private static class DescriptiveErrorListener extends BaseErrorListener<Token> {
		public static DescriptiveErrorListener INSTANCE = new DescriptiveErrorListener();

		@Override
		public <T extends Token> void syntaxError(Recognizer<T, ?> recognizer, T offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
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
		protected DFAState addDFAState(@NotNull DFA dfa, @NotNull ATNConfigSet configs) {
			DFAState proposed = new DFAState(configs, -1, -1);
			DFAState existing = dfa.states.get(proposed);
			if ( existing!=null ) return existing;

			configs.optimizeConfigs(this);
			DFAState newState = new DFAState(configs.clone(true), -1, -1);
			newState.stateNumber = dfa.states.size();
			dfa.states.put(newState, newState);
			if ( debug ) System.out.println("adding new DFA state: "+newState);
			return newState;
		}

	}

}
