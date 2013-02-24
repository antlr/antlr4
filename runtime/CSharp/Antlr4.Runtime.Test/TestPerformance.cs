using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Tree;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Debug = System.Diagnostics.Debug;

namespace Antlr4.Runtime.Test
{
    public class TestPerformance
    {
        /**
         * Parse all java files under this package within the JDK_SOURCE_ROOT
         * (environment variable or property defined on the Java command line).
         */
        private const String TOP_PACKAGE = "java.lang";
        /**
         * {@code true} to load java files from sub-packages of
         * {@link #TOP_PACKAGE}.
         */
        private const bool RECURSIVE = true;

        /**
         * {@code true} to use the Java grammar with expressions in the v4
         * left-recursive syntax (Java-LR.g4). {@code false} to use the standard
         * grammar (Java.g4). In either case, the grammar is renamed in the
         * temporary directory to Java.g4 before compiling.
         */
        private const bool USE_LR_GRAMMAR = true;
        /**
         * {@code true} to specify the {@code -Xforce-atn} option when generating
         * the grammar, forcing all decisions in {@code JavaParser} to be handled by
         * {@link ParserATNSimulator#adaptivePredict}.
         */
        private const bool FORCE_ATN = false;
        /**
         * {@code true} to specify the {@code -atn} option when generating the
         * grammar. This will cause ANTLR to export the ATN for each decision as a
         * DOT (GraphViz) file.
         */
        private const bool EXPORT_ATN_GRAPHS = true;
        /**
         * {@code true} to specify the {@code -XdbgST} option when generating the
         * grammar.
         */
        private const bool DEBUG_TEMPLATES = false;
        /**
         * {@code true} to specify the {@code -XdbgSTWait} option when generating the
         * grammar.
         */
        private const bool DEBUG_TEMPLATES_WAIT = DEBUG_TEMPLATES;
        /**
         * {@code true} to delete temporary (generated and compiled) files when the
         * test completes.
         */
        private const bool DELETE_TEMP_FILES = true;

        /**
         * {@code true} to call {@link System#gc} and then wait for 5 seconds at the
         * end of the test to make it easier for a profiler to grab a heap dump at
         * the end of the test run.
         */
        private const bool PAUSE_FOR_HEAP_DUMP = false;

        /**
         * Parse each file with {@code JavaParser.compilationUnit}.
         */
        private const bool RUN_PARSER = true;
        /**
         * {@code true} to use {@link BailErrorStrategy}, {@code false} to use
         * {@link DefaultErrorStrategy}.
         */
        private const bool BAIL_ON_ERROR = true;
        /**
         * {@code true} to compute a checksum for verifying consistency across
         * optimizations and multiple passes.
         */
        private const bool COMPUTE_CHECKSUM = true;
        /**
         * This value is passed to {@link Parser#setBuildParseTree}.
         */
        private const bool BUILD_PARSE_TREES = false;
        /**
         * Use
         * {@link ParseTreeWalker#DEFAULT}{@code .}{@link ParseTreeWalker#walk walk}
         * with the {@code JavaParserBaseListener} to show parse tree walking
         * overhead. If {@link #BUILD_PARSE_TREES} is {@code false}, the listener
         * will instead be called during the parsing process via
         * {@link Parser#addParseListener}.
         */
        private const bool BLANK_LISTENER = false;

        private const bool EXPORT_LARGEST_CONFIG_CONTEXTS = false;

        /**
         * Shows the number of {@link DFAState} and {@link ATNConfig} instances in
         * the DFA cache at the end of each pass. If {@link #REUSE_LEXER_DFA} and/or
         * {@link #REUSE_PARSER_DFA} are false, the corresponding instance numbers
         * will only apply to one file (the last file if {@link #NUMBER_OF_THREADS}
         * is 0, otherwise the last file which was parsed on the first thread).
         */
        private const bool SHOW_DFA_STATE_STATS = true;

        private const bool ENABLE_LEXER_DFA = true;

        private const bool ENABLE_PARSER_DFA = true;

        private static readonly PredictionMode PREDICTION_MODE = PredictionMode.LL;
        private const bool FORCE_GLOBAL_CONTEXT = false;
        private const bool TRY_LOCAL_CONTEXT_FIRST = true;
        private const bool OPTIMIZE_LL1 = true;
        private const bool OPTIMIZE_UNIQUE_CLOSURE = true;
        private const bool OPTIMIZE_HIDDEN_CONFLICTED_CONFIGS = false;
        private const bool OPTIMIZE_TAIL_CALLS = true;
        private const bool TAIL_CALL_PRESERVES_SLL = true;
        private const bool TREAT_SLLK1_CONFLICT_AS_AMBIGUITY = false;

        private const bool TWO_STAGE_PARSING = true;

        private const bool SHOW_CONFIG_STATS = false;

        private const bool REPORT_SYNTAX_ERRORS = true;
        private const bool REPORT_AMBIGUITIES = false;
        private const bool REPORT_FULL_CONTEXT = false;
        private const bool REPORT_CONTEXT_SENSITIVITY = REPORT_FULL_CONTEXT;

        /**
         * If {@code true}, a single {@code JavaLexer} will be used, and
         * {@link Lexer#setInputStream} will be called to initialize it for each
         * source file. Otherwise, a new instance will be created for each file.
         */
        private const bool REUSE_LEXER = false;
        /**
         * If {@code true}, a single DFA will be used for lexing which is shared
         * across all threads and files. Otherwise, each file will be lexed with its
         * own DFA which is accomplished by creating one ATN instance per thread and
         * clearing its DFA cache before lexing each file.
         */
        private const bool REUSE_LEXER_DFA = true;
        /**
         * If {@code true}, a single {@code JavaParser} will be used, and
         * {@link Parser#setInputStream} will be called to initialize it for each
         * source file. Otherwise, a new instance will be created for each file.
         */
        private const bool REUSE_PARSER = false;
        /**
         * If {@code true}, a single DFA will be used for parsing which is shared
         * across all threads and files. Otherwise, each file will be parsed with
         * its own DFA which is accomplished by creating one ATN instance per thread
         * and clearing its DFA cache before parsing each file.
         */
        private const bool REUSE_PARSER_DFA = true;
        /**
         * If {@code true}, the shared lexer and parser are reset after each pass.
         * If {@code false}, all passes after the first will be fully "warmed up",
         * which makes them faster and can compare them to the first warm-up pass,
         * but it will not distinguish bytecode load/JIT time from warm-up time
         * during the first pass.
         */
        private const bool CLEAR_DFA = false;
        /**
         * Total number of passes to make over the source.
         */
        private const int PASSES = 4;

        /**
         * Number of parser threads to use.
         */
        private const int NUMBER_OF_THREADS = 1;

        private static readonly Lexer[] sharedLexers = new Lexer[NUMBER_OF_THREADS];
        private static readonly ATN[] sharedLexerATNs = new ATN[NUMBER_OF_THREADS];

        private static readonly Parser[] sharedParsers = new Parser[NUMBER_OF_THREADS];
        private static readonly ATN[] sharedParserATNs = new ATN[NUMBER_OF_THREADS];

        private static readonly ParseTreeListener[] sharedListeners = new ParseTreeListener[NUMBER_OF_THREADS];

        private readonly AtomicInteger tokenCount = new AtomicInteger();
        private int currentPass;

        [TestMethod]
        //[Ignore]
        public void compileJdk() {
            String jdkSourceRoot = getSourceRoot("JDK");
            assertTrue("The JDK_SOURCE_ROOT environment variable must be set for performance testing.", jdkSourceRoot != null && !jdkSourceRoot.isEmpty());

            compileJavaParser(USE_LR_GRAMMAR);
            String lexerName = "JavaLexer";
            String parserName = "JavaParser";
            String listenerName = "JavaBaseListener";
            String entryPoint = "compilationUnit";
            ParserFactory factory = getParserFactory(lexerName, parserName, listenerName, entryPoint);

            if (!TOP_PACKAGE.isEmpty()) {
                jdkSourceRoot = jdkSourceRoot + '/' + TOP_PACKAGE.replace('.', '/');
            }

            File directory = new File(jdkSourceRoot);
            assertTrue(directory.isDirectory());

            Collection<CharStream> sources = loadSources(directory, new FileExtensionFilenameFilter(".java"), RECURSIVE);

            Console.Out.Write(getOptionsDescription(TOP_PACKAGE));

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
                Console.Out.WriteLine("Pausing before application exit.");
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(typeof(TestPerformance).getName()).log(Level.SEVERE, null, ex);
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

        protected override void eraseTempDir() {
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

            builder.append(newline);

            builder.append("Op=Lex").append(RUN_PARSER ? "+Parse" : " only");
            builder.append(", Strategy=").append(BAIL_ON_ERROR ? typeof(BailErrorStrategy).getSimpleName() : typeof(DefaultErrorStrategy).getSimpleName());
            builder.append(", BuildParseTree=").append(BUILD_PARSE_TREES);
            builder.append(", WalkBlankListener=").append(BLANK_LISTENER);

            builder.append(newline);

            builder.append("Lexer=").append(REUSE_LEXER ? "setInputStream" : "newInstance");
            builder.append(", Parser=").append(REUSE_PARSER ? "setInputStream" : "newInstance");
            builder.append(", AfterPass=").append(CLEAR_DFA ? "newInstance" : "setInputStream");

            builder.append('\n');

            builder.append("UniqueClosure=").append(OPTIMIZE_UNIQUE_CLOSURE ? "optimize" : "complete");

            builder.append(newline);

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

        protected Collection<CharStream> loadSources(File directory, FilenameFilter filter, bool recursive) {
            return loadSources(directory, filter, null, recursive);
        }

        protected Collection<CharStream> loadSources(File directory, FilenameFilter filter, String encoding, bool recursive) {
            Collection<CharStream> result = new ArrayList<CharStream>();
            loadSources(directory, filter, encoding, recursive, result);
            return result;
        }

        protected void loadSources(File directory, FilenameFilter filter, String encoding, bool recursive, Collection<CharStream> result) {
            Debug.Assert(directory.isDirectory());

            File[] sources = directory.listFiles(filter);
            foreach (File file in sources) {
                try {
                    CharStream input = new ANTLRFileStream(file.getAbsolutePath(), encoding);
                    result.add(input);
                } catch (IOException ex) {
                }
            }

            if (recursive) {
                File[] children = directory.listFiles();
                foreach (File child in children) {
                    if (child.isDirectory()) {
                        loadSources(child, filter, encoding, true, result);
                    }
                }
            }
        }

        int configOutputSize = 0;

        protected void parseSources(ParserFactory factory, Collection<CharStream> sources) {
            long startTime = System.currentTimeMillis();
            tokenCount.set(0);
            int inputSize = 0;

            Collection<Future<Integer>> results = new ArrayList<Future<Integer>>();
            ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS, new NumberedThreadFactory());
            foreach (CharStream input in sources) {
                input.seek(0);
                inputSize += input.size();
                Future<Integer> futureChecksum = executorService.submit(new Callable_1(input, factory));

                results.add(futureChecksum);
            }

            Checksum checksum = new CRC32();
            foreach (Future<Integer> future in results) {
                int value = 0;
                try {
                    value = future.get();
                } catch (ExecutionException ex) {
                    Logger.getLogger(typeof(TestPerformance).getName()).log(Level.SEVERE, null, ex);
                }

                if (COMPUTE_CHECKSUM) {
                    updateChecksum(checksum, value);
                }
            }

            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            Console.Out.WriteLine("Total parse time for {0} files ({1} KB, {2} tokens, checksum 0x{3:8X}): {4}ms",
                              sources.size(),
                              inputSize / 1024,
                              tokenCount.get(),
                              COMPUTE_CHECKSUM ? checksum.getValue() : 0,
                              System.currentTimeMillis() - startTime);

            if (sharedLexers.length > 0) {
                Lexer lexer = sharedLexers[0];
                LexerATNSimulator lexerInterpreter = lexer.getInterpreter();
                DFA[] modeToDFA = lexerInterpreter.atn.modeToDFA;
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
                        foreach (DFAState state in dfa.states.values()) {
                            configs += state.configs.size();
                            uniqueConfigs.addAll(state.configs);
                        }
                    }

                    Console.Out.WriteLine("There are {0} lexer DFAState instances, {1} configs ({2} unique), {3} prediction contexts.", states, configs, uniqueConfigs.size(), lexerInterpreter.atn.getContextCacheSize());
                }
            }

            if (RUN_PARSER && sharedParsers.length > 0) {
                Parser parser = sharedParsers[0];
                // make sure the individual DFAState objects actually have unique ATNConfig arrays
                ParserATNSimulator interpreter = parser.getInterpreter();
                DFA[] decisionToDFA = interpreter.atn.decisionToDFA;

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
                        foreach (DFAState state in dfa.states.values()) {
                            configs += state.configs.size();
                            uniqueConfigs.addAll(state.configs);
                        }
                    }

                    Console.Out.WriteLine("There are {0} parser DFAState instances, {1} configs ({2} unique), {3} prediction contexts.", states, configs, uniqueConfigs.size(), interpreter.atn.getContextCacheSize());
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
                        foreach (DFAState state in dfa.states.keySet()) {
                            if (state.configs.size() >= contextsInDFAState.length) {
                                contextsInDFAState = Arrays.copyOf(contextsInDFAState, state.configs.size() + 1);
                            }

                            if (state.isAcceptState) {
                                bool hasGlobal = false;
                                foreach (ATNConfig config in state.configs) {
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

                            contextsInDFAState[state.configs.size()]++;
                        }
                    }

                    if (EXPORT_LARGEST_CONFIG_CONTEXTS) {
                        foreach (DFAState state in dfa.states.keySet()) {
                            foreach (ATNConfig config in state.configs) {
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
                    Console.Out.WriteLine("  DFA accept states: {0} total, {1} with only local context, {2} with a global context", localDfaCount + globalDfaCount, localDfaCount, globalDfaCount);
                    Console.Out.WriteLine("  Config stats: {0} total, {1} local, {2} global", localConfigCount + globalConfigCount, localConfigCount, globalConfigCount);
                    if (SHOW_DFA_STATE_STATS) {
                        for (int i = 0; i < contextsInDFAState.length; i++) {
                            if (contextsInDFAState[i] != 0) {
                                Console.Out.WriteLine("  {0} configs = {1}", i, contextsInDFAState[i]);
                            }
                        }
                    }
                }
            }
        }

        private class Callable_1 : Callable<Integer> {
            private readonly CharStream input;
            private readonly ParserFactory factory;

            public Callable_1(CharStream input, ParserFactory factory)
            {
                this.input = input;
                this.factory = factory;
            }

            public override Integer call() {
                // this incurred a great deal of overhead and was causing significant variations in performance results.
                //Console.Out.WriteLine("Parsing file {0}", input.getSourceName());
                try {
                    return factory.parseFile(input, ((NumberedThread)Thread.currentThread()).getThreadNumber());
                } catch (IllegalStateException ex) {
                    ex.printStackTrace(System.err);
                } catch (Throwable t) {
                    t.printStackTrace(System.err);
                }

                return -1;
            }
        }

        protected void compileJavaParser(bool leftRecursive) {
            String grammarFileName = "Java.g4";
            String sourceName = leftRecursive ? "Java-LR.g4" : "Java.g4";
            String body = load(sourceName, null);
            List<String> extraOptions = new ArrayList<String>();
            extraOptions.add("-Werror");
            if (FORCE_ATN) {
                extraOptions.add("-Xforce-atn");
            }
            if (EXPORT_ATN_GRAPHS) {
                extraOptions.add("-atn");
            }
            if (DEBUG_TEMPLATES) {
                extraOptions.add("-XdbgST");
                if (DEBUG_TEMPLATES_WAIT) {
                    extraOptions.add("-XdbgSTWait");
                }
            }
            extraOptions.add("-visitor");
            String[] extraOptionsArray = extraOptions.toArray(new String[extraOptions.size()]);
            bool success = rawGenerateAndBuildRecognizer(grammarFileName, body, "JavaParser", "JavaLexer", true, extraOptionsArray);
            assertTrue(success);
        }

        protected String load(String fileName, [Nullable] String encoding)
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

        private static void updateChecksum(Checksum checksum, int value) {
            checksum.update((value) & 0xFF);
            checksum.update(((uint)value >> 8) & 0xFF);
            checksum.update(((uint)value >> 16) & 0xFF);
            checksum.update(((uint)value >> 24) & 0xFF);
        }

        private static void updateChecksum(Checksum checksum, Token token) {
            if (token == null) {
                checksum.update(0);
                return;
            }

            updateChecksum(checksum, token.getStartIndex());
            updateChecksum(checksum, token.getStopIndex());
            updateChecksum(checksum, token.getLine());
            updateChecksum(checksum, token.getCharPositionInLine());
            updateChecksum(checksum, token.getType());
            updateChecksum(checksum, token.getChannel());
        }

        protected ParserFactory getParserFactory(String lexerName, String parserName, String listenerName, String entryPoint) {
            try {
                ClassLoader loader = new URLClassLoader(new URL[] { new File(tmpdir).toURI().toURL() }, ClassLoader.getSystemClassLoader());
                Class<? extends Lexer> lexerClass = loader.loadClass(lexerName).asSubclass(typeof(Lexer));
                Class<? extends Parser> parserClass = loader.loadClass(parserName).asSubclass(typeof(Parser));
                Class<? extends ParseTreeListener> listenerClass = (Class<? extends ParseTreeListener>)loader.loadClass(listenerName).asSubclass(typeof(ParseTreeListener));

                Constructor<? extends Lexer> lexerCtor = lexerClass.getConstructor(typeof(CharStream));
                Constructor<? extends Parser> parserCtor = parserClass.getConstructor(typeof(TokenStream));

                // construct initial instances of the lexer and parser to deserialize their ATNs
                TokenSource tokenSource = lexerCtor.newInstance(new ANTLRInputStream(""));
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

                return new ParserFactory_1();
            } catch (Exception e) {
                e.printStackTrace(System.out);
                Assert.fail(e.getMessage());
                throw new IllegalStateException(e);
            }
        }

        private class ParserFactory_1 : ParserFactory {
            public override int parseFile(CharStream input, int thread) {
                Checksum checksum = new CRC32();

                Debug.Assert(thread >= 0 && thread < NUMBER_OF_THREADS);

                try {
                    ParseTreeListener listener = sharedListeners[thread];
                    if (listener == null) {
                        listener = listenerClass.newInstance();
                        sharedListeners[thread] = listener;
                    }

                    Lexer lexer = sharedLexers[thread];
                    if (REUSE_LEXER && lexer != null) {
                        lexer.setInputStream(input);
                    } else {
                        lexer = lexerCtor.newInstance(input);
                        sharedLexers[thread] = lexer;
                        if (!ENABLE_LEXER_DFA) {
                            lexer.setInterpreter(new NonCachingLexerATNSimulator(lexer, lexer.getATN()));
                        } else if (!REUSE_LEXER_DFA) {
                            lexer.setInterpreter(new LexerATNSimulator(lexer, sharedLexerATNs[thread]));
                        }
                    }

                    lexer.getInterpreter().optimize_tail_calls = OPTIMIZE_TAIL_CALLS;
                    if (ENABLE_LEXER_DFA && !REUSE_LEXER_DFA) {
                        lexer.getInterpreter().atn.clearDFA();
                    }

                    CommonTokenStream tokens = new CommonTokenStream(lexer);
                    tokens.fill();
                    tokenCount.addAndGet(tokens.size());

                    if (COMPUTE_CHECKSUM) {
                        foreach (Token token in tokens.getTokens()) {
                            updateChecksum(checksum, token);
                        }
                    }

                    if (!RUN_PARSER) {
                        return (int)checksum.getValue();
                    }

                    Parser parser = sharedParsers[thread];
                    if (REUSE_PARSER && parser != null) {
                        parser.setInputStream(tokens);
                    } else {
                        Parser newParser = parserCtor.newInstance(tokens);
                        parser = newParser;
                        sharedParsers[thread] = parser;
                    }

                    parser.removeErrorListeners();
                    if (!TWO_STAGE_PARSING) {
                        parser.addErrorListener(DescriptiveErrorListener.INSTANCE);
                        parser.addErrorListener(new SummarizingDiagnosticErrorListener());
                    }

                    if (!ENABLE_PARSER_DFA) {
                        parser.setInterpreter(new NonCachingParserATNSimulator(parser, parser.getATN()));
                    } else if (!REUSE_PARSER_DFA) {
                        parser.setInterpreter(new ParserATNSimulator(parser, sharedParserATNs[thread]));
                    }

                    if (ENABLE_PARSER_DFA && !REUSE_PARSER_DFA) {
                        parser.getInterpreter().atn.clearDFA();
                    }

                    parser.getInterpreter().setPredictionMode(TWO_STAGE_PARSING ? PredictionMode.SLL : PREDICTION_MODE);
                    parser.getInterpreter().force_global_context = FORCE_GLOBAL_CONTEXT && !TWO_STAGE_PARSING;
                    parser.getInterpreter().always_try_local_context = TRY_LOCAL_CONTEXT_FIRST || TWO_STAGE_PARSING;
                    parser.getInterpreter().optimize_ll1 = OPTIMIZE_LL1;
                    parser.getInterpreter().optimize_unique_closure = OPTIMIZE_UNIQUE_CLOSURE;
                    parser.getInterpreter().optimize_hidden_conflicted_configs = OPTIMIZE_HIDDEN_CONFLICTED_CONFIGS;
                    parser.getInterpreter().optimize_tail_calls = OPTIMIZE_TAIL_CALLS;
                    parser.getInterpreter().tail_call_preserves_sll = TAIL_CALL_PRESERVES_SLL;
                    parser.getInterpreter().treat_sllk1_conflict_as_ambiguity = TREAT_SLLK1_CONFLICT_AS_AMBIGUITY;
                    parser.setBuildParseTree(BUILD_PARSE_TREES);
                    if (!BUILD_PARSE_TREES && BLANK_LISTENER) {
                        parser.addParseListener(listener);
                    }
                    if (BAIL_ON_ERROR || TWO_STAGE_PARSING) {
                        parser.setErrorHandler(new BailErrorStrategy());
                    }

                    Method parseMethod = parserClass.getMethod(entryPoint);
                    Object parseResult;

                    ParseTreeListener checksumParserListener = null;

                    try {
                        if (COMPUTE_CHECKSUM) {
                            checksumParserListener = new ChecksumParseTreeListener(checksum);
                            parser.addParseListener(checksumParserListener);
                        }
                        parseResult = parseMethod.invoke(parser);
                    } catch (InvocationTargetException ex) {
                        if (!TWO_STAGE_PARSING) {
                            throw ex;
                        }

                        String sourceName = tokens.getSourceName();
                        sourceName = sourceName != null && !sourceName.isEmpty() ? sourceName+": " : "";
                        Console.Error.WriteLine(sourceName+"Forced to retry with full context.");

                        if (!(ex.getCause() instanceof ParseCancellationException)) {
                            throw ex;
                        }

                        tokens.reset();
                        if (REUSE_PARSER && sharedParsers[thread] != null) {
                            parser.setInputStream(tokens);
                        } else {
                            Parser newParser = parserCtor.newInstance(tokens);
                            parser = newParser;
                            sharedParsers[thread] = parser;
                        }

                        parser.removeErrorListeners();
                        parser.addErrorListener(DescriptiveErrorListener.INSTANCE);
                        parser.addErrorListener(new SummarizingDiagnosticErrorListener());
                        if (!ENABLE_PARSER_DFA) {
                            parser.setInterpreter(new NonCachingParserATNSimulator(parser, parser.getATN()));
                        }
                        parser.getInterpreter().setPredictionMode(PREDICTION_MODE);
                        parser.getInterpreter().force_global_context = FORCE_GLOBAL_CONTEXT;
                        parser.getInterpreter().always_try_local_context = TRY_LOCAL_CONTEXT_FIRST;
                        parser.getInterpreter().optimize_ll1 = OPTIMIZE_LL1;
                        parser.getInterpreter().optimize_unique_closure = OPTIMIZE_UNIQUE_CLOSURE;
                        parser.getInterpreter().optimize_hidden_conflicted_configs = OPTIMIZE_HIDDEN_CONFLICTED_CONFIGS;
                        parser.getInterpreter().optimize_tail_calls = OPTIMIZE_TAIL_CALLS;
                        parser.getInterpreter().tail_call_preserves_sll = TAIL_CALL_PRESERVES_SLL;
                        parser.getInterpreter().treat_sllk1_conflict_as_ambiguity = TREAT_SLLK1_CONFLICT_AS_AMBIGUITY;
                        parser.setBuildParseTree(BUILD_PARSE_TREES);
                        if (!BUILD_PARSE_TREES && BLANK_LISTENER) {
                            parser.addParseListener(listener);
                        }
                        if (BAIL_ON_ERROR) {
                            parser.setErrorHandler(new BailErrorStrategy());
                        }

                        parseResult = parseMethod.invoke(parser);
                    }
                    finally {
                        if (checksumParserListener != null) {
                            parser.removeParseListener(checksumParserListener);
                        }
                    }

                    assertThat(parseResult, instanceOf(typeof(ParseTree)));
                    if (BUILD_PARSE_TREES && BLANK_LISTENER) {
                        ParseTreeWalker.DEFAULT.walk(listener, (ParserRuleContext)parseResult);
                    }
                } catch (Exception e) {
                    if (!REPORT_SYNTAX_ERRORS && e instanceof ParseCancellationException) {
                        return (int)checksum.getValue();
                    }

                    e.printStackTrace(System.out);
                    throw new IllegalStateException(e);
                }

                return (int)checksum.getValue();
            }
        }

        protected interface ParserFactory {
            int parseFile(CharStream input, int thread);
        }

        private static class DescriptiveErrorListener extends BaseErrorListener {
            public static DescriptiveErrorListener INSTANCE = new DescriptiveErrorListener();

            public override <T extends Token> void syntaxError(Recognizer<T, ?> recognizer, T offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                if (!REPORT_SYNTAX_ERRORS) {
                    return;
                }

                String sourceName = recognizer.getInputStream().getSourceName();
                if (!sourceName.isEmpty()) {
                    sourceName = String.format("%s:%d:%d: ", sourceName, line, charPositionInLine);
                }

                Console.Error.WriteLine(sourceName+"line "+line+":"+charPositionInLine+" "+msg);
            }

        }

        private static class SummarizingDiagnosticErrorListener extends DiagnosticErrorListener {

            public override void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet ambigAlts, ATNConfigSet configs) {
                if (!REPORT_AMBIGUITIES) {
                    return;
                }

                super.reportAmbiguity(recognizer, dfa, startIndex, stopIndex, ambigAlts, configs);
            }

            public override void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, SimulatorState initialState) {
                if (!REPORT_FULL_CONTEXT) {
                    return;
                }

                super.reportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, initialState);
            }

            public override void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, SimulatorState acceptState) {
                if (!REPORT_CONTEXT_SENSITIVITY) {
                    return;
                }

                super.reportContextSensitivity(recognizer, dfa, startIndex, stopIndex, acceptState);
            }

            protected override String getDecisionDescription(Parser recognizer, int decision) {
                String format = "%d(%s)";
                String ruleName = recognizer.getRuleNames()[recognizer.getATN().decisionToState.get(decision).ruleIndex];
                return String.format(format, decision, ruleName);
            }

        }

        protected static class FileExtensionFilenameFilter implements FilenameFilter {

            private readonly String extension;

            public FileExtensionFilenameFilter(String extension) {
                if (!extension.startsWith(".")) {
                    extension = '.' + extension;
                }

                this.extension = extension;
            }

            public override bool accept(File dir, String name) {
                return name.toLowerCase().endsWith(extension);
            }

        }

        protected static class NonCachingLexerATNSimulator extends LexerATNSimulator {

            public NonCachingLexerATNSimulator(Lexer recog, ATN atn) {
                super(recog, atn);
            }

            protected override DFAState addDFAState(ATNConfigSet configs) {
                return null;
            }

        }

        protected static class NonCachingParserATNSimulator extends ParserATNSimulator {

            public NonCachingParserATNSimulator(Parser parser, ATN atn) {
                super(parser, atn);
            }

            [return: NotNull]
            protected override DFAState createDFAState(@NotNull ATNConfigSet configs) {
                return new DFAState(configs, -1, -1);
            }

        }

        protected static class NumberedThread extends Thread {
            private readonly int threadNumber;

            public NumberedThread(Runnable target, int threadNumber) {
                super(target);
                this.threadNumber = threadNumber;
            }

            public int getThreadNumber() {
                return threadNumber;
            }

        }

        protected static class NumberedThreadFactory implements ThreadFactory {
            private readonly AtomicInteger nextThread = new AtomicInteger();

            public override Thread newThread(Runnable r) {
                int threadNumber = nextThread.getAndIncrement();
                Debug.Assert(threadNumber < NUMBER_OF_THREADS);
                return new NumberedThread(r, threadNumber);
            }

        }

        protected static class ChecksumParseTreeListener implements ParseTreeListener {
            private const int VISIT_TERMINAL = 1;
            private const int VISIT_ERROR_NODE = 2;
            private const int ENTER_RULE = 3;
            private const int EXIT_RULE = 4;

            private readonly Checksum checksum;

            public ChecksumParseTreeListener(Checksum checksum) {
                this.checksum = checksum;
            }

            public override void visitTerminal(TerminalNode node) {
                checksum.update(VISIT_TERMINAL);
                updateChecksum(checksum, node.getSymbol());
            }

            public override void visitErrorNode(ErrorNode node) {
                checksum.update(VISIT_ERROR_NODE);
                updateChecksum(checksum, node.getSymbol());
            }

            public override void enterEveryRule(ParserRuleContext ctx) {
                checksum.update(ENTER_RULE);
                updateChecksum(checksum, ctx.getRuleIndex());
                updateChecksum(checksum, ctx.getStart());
            }

            public override void exitEveryRule(ParserRuleContext ctx) {
                checksum.update(EXIT_RULE);
                updateChecksum(checksum, ctx.getRuleIndex());
                updateChecksum(checksum, ctx.getStop());
            }

        }
    }
}
