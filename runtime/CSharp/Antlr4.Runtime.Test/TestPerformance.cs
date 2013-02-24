namespace Antlr4.Runtime.Test
{
    using System;
    using System.Collections.Concurrent;
    using System.Collections.Generic;
    using System.Linq;
    using System.Reflection;
    using System.Text;
    using System.Threading.Tasks;
    using System.Threading.Tasks.Schedulers;
    using Antlr4.Runtime;
    using Antlr4.Runtime.Atn;
    using Antlr4.Runtime.Dfa;
    using Antlr4.Runtime.Misc;
    using Antlr4.Runtime.Tree;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Sharpen;
    using CancellationToken = System.Threading.CancellationToken;
    using Debug = System.Diagnostics.Debug;
    using DirectoryInfo = System.IO.DirectoryInfo;
    using FileInfo = System.IO.FileInfo;
    using Interlocked = System.Threading.Interlocked;
    using Path = System.IO.Path;
    using Stopwatch = System.Diagnostics.Stopwatch;
    using Stream = System.IO.Stream;
    using StreamReader = System.IO.StreamReader;
    using Thread = System.Threading.Thread;
    using Volatile = System.Threading.Volatile;

    public class TestPerformance
    {
        /**
         * Parse all java files under this package within the JDK_SOURCE_ROOT
         * (environment variable or property defined on the Java command line).
         */
        private const string TOP_PACKAGE = "java.lang";
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

        private static readonly PredictionMode PREDICTION_MODE = PredictionMode.Ll;
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

        private static readonly IParseTreeListener[] sharedListeners = new IParseTreeListener[NUMBER_OF_THREADS];

        private static int tokenCount;
        private int currentPass;

        [TestMethod]
        //[Ignore]
        public void compileJdk()
        {
            string jdkSourceRoot = getSourceRoot("JDK");
            Assert.IsTrue(jdkSourceRoot != null && !string.IsNullOrEmpty(jdkSourceRoot), "The JDK_SOURCE_ROOT environment variable must be set for performance testing.");

            compileJavaParser(USE_LR_GRAMMAR);
            string lexerName = "JavaLexer";
            string parserName = "JavaParser";
            string listenerName = "JavaBaseListener";
            string entryPoint = "compilationUnit";
            ParserFactory factory = getParserFactory(lexerName, parserName, listenerName, entryPoint);

            if (!string.IsNullOrEmpty(TOP_PACKAGE))
            {
                jdkSourceRoot = jdkSourceRoot + '/' + TOP_PACKAGE.Replace('.', '/');
            }

            DirectoryInfo directory = new DirectoryInfo(jdkSourceRoot);
            Assert.IsTrue(directory.Exists);

            IEnumerable<ICharStream> sources = loadSources(directory, "*.java", RECURSIVE);

            Console.Out.Write(getOptionsDescription(TOP_PACKAGE));

            currentPass = 0;
            parse1(factory, sources);
            for (int i = 0; i < PASSES - 1; i++)
            {
                currentPass = i + 1;
                if (CLEAR_DFA)
                {
                    if (sharedLexers.Length > 0)
                    {
                        sharedLexers[0].Atn.ClearDFA();
                    }

                    if (sharedParsers.Length > 0)
                    {
                        sharedParsers[0].Atn.ClearDFA();
                    }

                    Arrays.Fill(sharedLexers, null);
                    Arrays.Fill(sharedParsers, null);
                }

                parse2(factory, sources);
            }

            sources = null;
            if (PAUSE_FOR_HEAP_DUMP)
            {
                GC.Collect();
                Console.Out.WriteLine("Pausing before application exit.");
                Thread.Sleep(4000);
            }
        }

        private string getSourceRoot(string prefix)
        {
            string sourceRoot = Environment.GetEnvironmentVariable(prefix + "_SOURCE_ROOT");
            return sourceRoot;
        }

        protected override void eraseTempDir()
        {
            if (DELETE_TEMP_FILES)
            {
                base.eraseTempDir();
            }
        }

        public static string getOptionsDescription(string topPackage)
        {
            StringBuilder builder = new StringBuilder();
            builder.Append("Input=");
            if (string.IsNullOrEmpty(topPackage))
            {
                builder.Append("*");
            }
            else
            {
                builder.Append(topPackage).Append(".*");
            }

            builder.Append(", Grammar=").Append(USE_LR_GRAMMAR ? "LR" : "Standard");
            builder.Append(", ForceAtn=").Append(FORCE_ATN);
            builder.Append(", Lexer:").Append(ENABLE_LEXER_DFA ? "DFA" : "ATN");
            builder.Append(", Parser:").Append(ENABLE_PARSER_DFA ? "DFA" : "ATN");

            builder.AppendLine();

            builder.Append("Op=Lex").Append(RUN_PARSER ? "+Parse" : " only");
            builder.Append(", Strategy=").Append(BAIL_ON_ERROR ? typeof(BailErrorStrategy).Name : typeof(DefaultErrorStrategy).Name);
            builder.Append(", BuildParseTree=").Append(BUILD_PARSE_TREES);
            builder.Append(", WalkBlankListener=").Append(BLANK_LISTENER);

            builder.AppendLine();

            builder.Append("Lexer=").Append(REUSE_LEXER ? "setInputStream" : "newInstance");
            builder.Append(", Parser=").Append(REUSE_PARSER ? "setInputStream" : "newInstance");
            builder.Append(", AfterPass=").Append(CLEAR_DFA ? "newInstance" : "setInputStream");

            builder.AppendLine();

            builder.Append("UniqueClosure=").Append(OPTIMIZE_UNIQUE_CLOSURE ? "optimize" : "complete");

            builder.AppendLine();

            return builder.ToString();
        }

        /**
         *  This method is separate from {@link #parse2} so the first pass can be distinguished when analyzing
         *  profiler results.
         */
        protected void parse1(ParserFactory factory, IEnumerable<ICharStream> sources)
        {
            GC.Collect();
            parseSources(factory, sources);
        }

        /**
         *  This method is separate from {@link #parse1} so the first pass can be distinguished when analyzing
         *  profiler results.
         */
        protected void parse2(ParserFactory factory, IEnumerable<ICharStream> sources)
        {
            GC.Collect();
            parseSources(factory, sources);
        }

        protected IEnumerable<ICharStream> loadSources(DirectoryInfo directory, string filter, bool recursive)
        {
            return loadSources(directory, filter, null, recursive);
        }

        protected IEnumerable<ICharStream> loadSources(DirectoryInfo directory, string filter, Encoding encoding, bool recursive)
        {
            ICollection<ICharStream> result = new List<ICharStream>();
            loadSources(directory, filter, encoding, recursive, result);
            return result;
        }

        protected void loadSources(DirectoryInfo directory, string filter, Encoding encoding, bool recursive, ICollection<ICharStream> result)
        {
            Debug.Assert(directory.Exists);

            FileInfo[] sources = directory.GetFiles(filter);
            foreach (FileInfo file in sources)
            {
                ICharStream input = new AntlrFileStream(file.FullName, encoding);
                result.Add(input);
            }

            if (recursive)
            {
                DirectoryInfo[] children = directory.GetDirectories();
                foreach (DirectoryInfo child in children)
                {
                    loadSources(child, filter, encoding, true, result);
                }
            }
        }

        int configOutputSize = 0;

        protected void parseSources(ParserFactory factory, IEnumerable<ICharStream> sources)
        {
            Stopwatch startTime = Stopwatch.StartNew();
            Volatile.Write(ref tokenCount, 0);
            int sourceCount = 0;
            int inputSize = 0;

            ConcurrentBag<int> threadIdentifiers = new ConcurrentBag<int>(Enumerable.Range(0, NUMBER_OF_THREADS));
            ICollection<Task<int>> results = new List<Task<int>>();
            QueuedTaskScheduler executorServiceHost = new QueuedTaskScheduler(NUMBER_OF_THREADS);
            TaskScheduler executorService = executorServiceHost.ActivateNewQueue();
            foreach (ICharStream input in sources)
            {
                sourceCount++;
                input.Seek(0);
                inputSize += input.Size;
                Task<int> futureChecksum = Task.Factory.StartNew<int>(new Callable_1(input, factory, threadIdentifiers).call, CancellationToken.None, TaskCreationOptions.None, executorService);

                results.Add(futureChecksum);
            }

            Checksum checksum = new CRC32();
            foreach (Task<int> future in results)
            {
                int value = future.Result;
                if (COMPUTE_CHECKSUM)
                {
                    updateChecksum(checksum, value);
                }
            }

            executorServiceHost.Dispose();

            Console.Out.WriteLine("Total parse time for {0} files ({1} KB, {2} tokens, checksum 0x{3:8X}): {4}ms",
                              sourceCount,
                              inputSize / 1024,
                              Volatile.Read(ref tokenCount),
                              COMPUTE_CHECKSUM ? checksum.Value : 0,
                              startTime.ElapsedMilliseconds);

            if (sharedLexers.Length > 0)
            {
                Lexer lexer = sharedLexers[0];
                LexerATNSimulator lexerInterpreter = lexer.Interpreter;
                DFA[] modeToDFA = lexerInterpreter.atn.modeToDFA;
                if (SHOW_DFA_STATE_STATS)
                {
                    int states = 0;
                    int configs = 0;
                    ISet<ATNConfig> uniqueConfigs = new HashSet<ATNConfig>();

                    for (int i = 0; i < modeToDFA.Length; i++)
                    {
                        DFA dfa = modeToDFA[i];
                        if (dfa == null || dfa.states == null)
                        {
                            continue;
                        }

                        states += dfa.states.Count;
                        foreach (DFAState state in dfa.states.Values)
                        {
                            configs += state.configs.Count;
                            uniqueConfigs.UnionWith(state.configs);
                        }
                    }

                    Console.Out.WriteLine("There are {0} lexer DFAState instances, {1} configs ({2} unique), {3} prediction contexts.", states, configs, uniqueConfigs.Count, lexerInterpreter.atn.GetContextCacheSize());
                }
            }

            if (RUN_PARSER && sharedParsers.Length > 0)
            {
                Parser parser = sharedParsers[0];
                // make sure the individual DFAState objects actually have unique ATNConfig arrays
                ParserATNSimulator interpreter = parser.Interpreter;
                DFA[] decisionToDFA = interpreter.atn.decisionToDFA;

                if (SHOW_DFA_STATE_STATS)
                {
                    int states = 0;
                    int configs = 0;
                    ISet<ATNConfig> uniqueConfigs = new HashSet<ATNConfig>();

                    for (int i = 0; i < decisionToDFA.Length; i++)
                    {
                        DFA dfa = decisionToDFA[i];
                        if (dfa == null || dfa.states == null)
                        {
                            continue;
                        }

                        states += dfa.states.Count;
                        foreach (DFAState state in dfa.states.Values)
                        {
                            configs += state.configs.Count;
                            uniqueConfigs.UnionWith(state.configs);
                        }
                    }

                    Console.Out.WriteLine("There are {0} parser DFAState instances, {1} configs ({2} unique), {3} prediction contexts.", states, configs, uniqueConfigs.Count, interpreter.atn.GetContextCacheSize());
                }

                int localDfaCount = 0;
                int globalDfaCount = 0;
                int localConfigCount = 0;
                int globalConfigCount = 0;
                int[] contextsInDFAState = new int[0];

                for (int i = 0; i < decisionToDFA.Length; i++)
                {
                    DFA dfa = decisionToDFA[i];
                    if (dfa == null || dfa.states == null)
                    {
                        continue;
                    }

                    if (SHOW_CONFIG_STATS)
                    {
                        foreach (DFAState state in dfa.states.Keys)
                        {
                            if (state.configs.Count >= contextsInDFAState.Length)
                            {
                                Array.Resize(ref contextsInDFAState, state.configs.Count + 1);
                            }

                            if (state.isAcceptState)
                            {
                                bool hasGlobal = false;
                                foreach (ATNConfig config in state.configs)
                                {
                                    if (config.ReachesIntoOuterContext)
                                    {
                                        globalConfigCount++;
                                        hasGlobal = true;
                                    }
                                    else
                                    {
                                        localConfigCount++;
                                    }
                                }

                                if (hasGlobal)
                                {
                                    globalDfaCount++;
                                }
                                else
                                {
                                    localDfaCount++;
                                }
                            }

                            contextsInDFAState[state.configs.Count]++;
                        }
                    }

                    if (EXPORT_LARGEST_CONFIG_CONTEXTS)
                    {
                        foreach (DFAState state in dfa.states.Keys)
                        {
                            foreach (ATNConfig config in state.configs)
                            {
                                string configOutput = config.ToDotString();
                                if (configOutput.Length <= configOutputSize)
                                {
                                    continue;
                                }

                                configOutputSize = configOutput.Length;
                                writeFile(tmpdir, "d" + dfa.decision + ".s" + state.stateNumber + ".a" + config.Alt + ".config.dot", configOutput);
                            }
                        }
                    }
                }

                if (SHOW_CONFIG_STATS && currentPass == 0)
                {
                    Console.Out.WriteLine("  DFA accept states: {0} total, {1} with only local context, {2} with a global context", localDfaCount + globalDfaCount, localDfaCount, globalDfaCount);
                    Console.Out.WriteLine("  Config stats: {0} total, {1} local, {2} global", localConfigCount + globalConfigCount, localConfigCount, globalConfigCount);
                    if (SHOW_DFA_STATE_STATS)
                    {
                        for (int i = 0; i < contextsInDFAState.Length; i++)
                        {
                            if (contextsInDFAState[i] != 0)
                            {
                                Console.Out.WriteLine("  {0} configs = {1}", i, contextsInDFAState[i]);
                            }
                        }
                    }
                }
            }
        }

        private class Callable_1
        {
            private readonly ICharStream input;
            private readonly ParserFactory factory;
            private readonly BlockingCollection<int> threadNumbers;

            public Callable_1(ICharStream input, ParserFactory factory, IProducerConsumerCollection<int> threadNumbers)
            {
                this.input = input;
                this.factory = factory;
                this.threadNumbers = new BlockingCollection<int>(threadNumbers);
            }

            public int call()
            {
                // this incurred a great deal of overhead and was causing significant variations in performance results.
                //Console.Out.WriteLine("Parsing file {0}", input.getSourceName());
                int threadNumber = threadNumbers.Take();
                try
                {
                    return factory.parseFile(input, threadNumber);
                }
                finally
                {
                    threadNumbers.Add(threadNumber);
                }

                return -1;
            }
        }

        protected void compileJavaParser(bool leftRecursive)
        {
            string grammarFileName = "Java.g4";
            string sourceName = leftRecursive ? "Java-LR.g4" : "Java.g4";
            string body = load(sourceName, null);
            List<string> extraOptions = new List<string>();
            extraOptions.Add("-Werror");
            if (FORCE_ATN)
            {
                extraOptions.Add("-Xforce-atn");
            }
            if (EXPORT_ATN_GRAPHS)
            {
                extraOptions.Add("-atn");
            }
            if (DEBUG_TEMPLATES)
            {
                extraOptions.Add("-XdbgST");
                if (DEBUG_TEMPLATES_WAIT)
                {
                    extraOptions.Add("-XdbgSTWait");
                }
            }
            extraOptions.Add("-visitor");
            string[] extraOptionsArray = extraOptions.ToArray();
            bool success = rawGenerateAndBuildRecognizer(grammarFileName, body, "JavaParser", "JavaLexer", true, extraOptionsArray);
            Assert.IsTrue(success);
        }

        protected string load(string fileName, [Nullable] Encoding encoding)
        {
            if (fileName == null)
            {
                return null;
            }

            Stream stream = typeof(TestPerformance).Assembly.GetManifestResourceStream(typeof(TestPerformance), fileName);
            if (encoding == null)
                return new StreamReader(stream).ReadToEnd();
            else
                return new StreamReader(stream, encoding).ReadToEnd();
        }

        private static void updateChecksum(Checksum checksum, int value)
        {
            checksum.Update((value) & 0xFF);
            checksum.Update((int)((uint)value >> 8) & 0xFF);
            checksum.Update((int)((uint)value >> 16) & 0xFF);
            checksum.Update((int)((uint)value >> 24) & 0xFF);
        }

        private static void updateChecksum(Checksum checksum, IToken token)
        {
            if (token == null)
            {
                checksum.Update(0);
                return;
            }

            updateChecksum(checksum, token.StartIndex);
            updateChecksum(checksum, token.StopIndex);
            updateChecksum(checksum, token.Line);
            updateChecksum(checksum, token.Column);
            updateChecksum(checksum, token.Type);
            updateChecksum(checksum, token.Channel);
        }

        protected ParserFactory getParserFactory(string lexerName, string parserName, string listenerName, string entryPoint)
        {
            Assembly loader = Assembly.LoadFile(Path.Combine(tmpdir, "Parser.dll"));
            Type lexerClass = loader.GetType(lexerName);
            Type parserClass = loader.GetType(parserName);
            Type listenerClass = loader.GetType(listenerName);

            ConstructorInfo lexerCtor = lexerClass.GetConstructor(new Type[] { typeof(ICharStream) });
            ConstructorInfo parserCtor = parserClass.GetConstructor(new Type[] { typeof(ITokenStream) });

            // construct initial instances of the lexer and parser to deserialize their ATNs
            ITokenSource tokenSource = (ITokenSource)lexerCtor.Invoke(new object[] { new AntlrInputStream("") });
            parserCtor.Invoke(new object[] { new CommonTokenStream(tokenSource) });

            if (!REUSE_LEXER_DFA)
            {
                FieldInfo lexerSerializedATNField = lexerClass.GetField("_serializedATN");
                string lexerSerializedATN = (string)lexerSerializedATNField.GetValue(null);
                for (int i = 0; i < NUMBER_OF_THREADS; i++)
                {
                    sharedLexerATNs[i] = ATNSimulator.Deserialize(lexerSerializedATN.ToCharArray());
                }
            }

            if (RUN_PARSER && !REUSE_PARSER_DFA)
            {
                FieldInfo parserSerializedATNField = parserClass.GetField("_serializedATN");
                string parserSerializedATN = (string)parserSerializedATNField.GetValue(null);
                for (int i = 0; i < NUMBER_OF_THREADS; i++)
                {
                    sharedParserATNs[i] = ATNSimulator.Deserialize(parserSerializedATN.ToCharArray());
                }
            }

            return new ParserFactory_1();
        }

        private class ParserFactory_1 : ParserFactory
        {
            private readonly Type listenerClass;
            private readonly Type parserClass;

            private readonly ConstructorInfo lexerCtor;
            private readonly ConstructorInfo parserCtor;

            private readonly string entryPoint;

            public int parseFile(ICharStream input, int thread)
            {
                Checksum checksum = new CRC32();

                Debug.Assert(thread >= 0 && thread < NUMBER_OF_THREADS);

                try
                {
                    IParseTreeListener listener = sharedListeners[thread];
                    if (listener == null)
                    {
                        listener = (IParseTreeListener)Activator.CreateInstance(listenerClass);
                        sharedListeners[thread] = listener;
                    }

                    Lexer lexer = sharedLexers[thread];
                    if (REUSE_LEXER && lexer != null)
                    {
                        lexer.SetInputStream(input);
                    }
                    else
                    {
                        lexer = (Lexer)lexerCtor.Invoke(new object[] { input });
                        sharedLexers[thread] = lexer;
                        if (!ENABLE_LEXER_DFA)
                        {
                            lexer.Interpreter = new NonCachingLexerATNSimulator(lexer, lexer.Atn);
                        }
                        else if (!REUSE_LEXER_DFA)
                        {
                            lexer.Interpreter = new LexerATNSimulator(lexer, sharedLexerATNs[thread]);
                        }
                    }

                    lexer.Interpreter.optimize_tail_calls = OPTIMIZE_TAIL_CALLS;
                    if (ENABLE_LEXER_DFA && !REUSE_LEXER_DFA)
                    {
                        lexer.Interpreter.atn.ClearDFA();
                    }

                    CommonTokenStream tokens = new CommonTokenStream(lexer);
                    tokens.Fill();
                    Interlocked.Add(ref tokenCount, tokens.Size);

                    if (COMPUTE_CHECKSUM)
                    {
                        foreach (IToken token in tokens.GetTokens())
                        {
                            updateChecksum(checksum, token);
                        }
                    }

                    if (!RUN_PARSER)
                    {
                        return (int)checksum.Value;
                    }

                    Parser parser = sharedParsers[thread];
                    if (REUSE_PARSER && parser != null)
                    {
                        parser.SetInputStream(tokens);
                    }
                    else
                    {
                        Parser newParser = (Parser)parserCtor.Invoke(new object[] { tokens });
                        parser = newParser;
                        sharedParsers[thread] = parser;
                    }

                    parser.RemoveErrorListeners();
                    if (!TWO_STAGE_PARSING)
                    {
                        parser.AddErrorListener(DescriptiveErrorListener.INSTANCE);
                        parser.AddErrorListener(new SummarizingDiagnosticErrorListener());
                    }

                    if (!ENABLE_PARSER_DFA)
                    {
                        parser.Interpreter = new NonCachingParserATNSimulator(parser, parser.Atn);
                    }
                    else if (!REUSE_PARSER_DFA)
                    {
                        parser.Interpreter = new ParserATNSimulator(parser, sharedParserATNs[thread]);
                    }

                    if (ENABLE_PARSER_DFA && !REUSE_PARSER_DFA)
                    {
                        parser.Interpreter.atn.ClearDFA();
                    }

                    parser.Interpreter.PredictionMode = TWO_STAGE_PARSING ? PredictionMode.Sll : PREDICTION_MODE;
                    parser.Interpreter.force_global_context = FORCE_GLOBAL_CONTEXT && !TWO_STAGE_PARSING;
                    parser.Interpreter.always_try_local_context = TRY_LOCAL_CONTEXT_FIRST || TWO_STAGE_PARSING;
                    parser.Interpreter.optimize_ll1 = OPTIMIZE_LL1;
                    parser.Interpreter.optimize_unique_closure = OPTIMIZE_UNIQUE_CLOSURE;
                    parser.Interpreter.optimize_hidden_conflicted_configs = OPTIMIZE_HIDDEN_CONFLICTED_CONFIGS;
                    parser.Interpreter.optimize_tail_calls = OPTIMIZE_TAIL_CALLS;
                    parser.Interpreter.tail_call_preserves_sll = TAIL_CALL_PRESERVES_SLL;
                    parser.Interpreter.treat_sllk1_conflict_as_ambiguity = TREAT_SLLK1_CONFLICT_AS_AMBIGUITY;
                    parser.BuildParseTree = BUILD_PARSE_TREES;
                    if (!BUILD_PARSE_TREES && BLANK_LISTENER)
                    {
                        parser.AddParseListener(listener);
                    }
                    if (BAIL_ON_ERROR || TWO_STAGE_PARSING)
                    {
                        parser.ErrorHandler = new BailErrorStrategy();
                    }

                    MethodInfo parseMethod = parserClass.GetMethod(entryPoint);
                    object parseResult;

                    IParseTreeListener checksumParserListener = null;

                    try
                    {
                        if (COMPUTE_CHECKSUM)
                        {
                            checksumParserListener = new ChecksumParseTreeListener(checksum);
                            parser.AddParseListener(checksumParserListener);
                        }
                        parseResult = parseMethod.Invoke(parser, null);
                    }
                    catch (TargetInvocationException ex)
                    {
                        if (!TWO_STAGE_PARSING)
                        {
                            throw;
                        }

                        string sourceName = tokens.SourceName;
                        sourceName = !string.IsNullOrEmpty(sourceName) ? sourceName + ": " : "";
                        Console.Error.WriteLine(sourceName + "Forced to retry with full context.");

                        if (!(ex.InnerException is ParseCanceledException))
                        {
                            throw;
                        }

                        tokens.Reset();
                        if (REUSE_PARSER && sharedParsers[thread] != null)
                        {
                            parser.SetInputStream(tokens);
                        }
                        else
                        {
                            Parser newParser = (Parser)parserCtor.Invoke(new object[] { tokens });
                            parser = newParser;
                            sharedParsers[thread] = parser;
                        }

                        parser.RemoveErrorListeners();
                        parser.AddErrorListener(DescriptiveErrorListener.INSTANCE);
                        parser.AddErrorListener(new SummarizingDiagnosticErrorListener());
                        if (!ENABLE_PARSER_DFA)
                        {
                            parser.Interpreter = new NonCachingParserATNSimulator(parser, parser.Atn);
                        }
                        parser.Interpreter.PredictionMode = PREDICTION_MODE;
                        parser.Interpreter.force_global_context = FORCE_GLOBAL_CONTEXT;
                        parser.Interpreter.always_try_local_context = TRY_LOCAL_CONTEXT_FIRST;
                        parser.Interpreter.optimize_ll1 = OPTIMIZE_LL1;
                        parser.Interpreter.optimize_unique_closure = OPTIMIZE_UNIQUE_CLOSURE;
                        parser.Interpreter.optimize_hidden_conflicted_configs = OPTIMIZE_HIDDEN_CONFLICTED_CONFIGS;
                        parser.Interpreter.optimize_tail_calls = OPTIMIZE_TAIL_CALLS;
                        parser.Interpreter.tail_call_preserves_sll = TAIL_CALL_PRESERVES_SLL;
                        parser.Interpreter.treat_sllk1_conflict_as_ambiguity = TREAT_SLLK1_CONFLICT_AS_AMBIGUITY;
                        parser.BuildParseTree = BUILD_PARSE_TREES;
                        if (!BUILD_PARSE_TREES && BLANK_LISTENER)
                        {
                            parser.AddParseListener(listener);
                        }
                        if (BAIL_ON_ERROR)
                        {
                            parser.ErrorHandler = new BailErrorStrategy();
                        }

                        parseResult = parseMethod.Invoke(parser, null);
                    }
                    finally
                    {
                        if (checksumParserListener != null)
                        {
                            parser.RemoveParseListener(checksumParserListener);
                        }
                    }

                    Assert.IsInstanceOfType(parseResult, typeof(IParseTree));
                    if (BUILD_PARSE_TREES && BLANK_LISTENER)
                    {
                        ParseTreeWalker.Default.Walk(listener, (ParserRuleContext)parseResult);
                    }
                }
                catch (Exception e)
                {
                    if (!REPORT_SYNTAX_ERRORS && e is ParseCanceledException)
                    {
                        return (int)checksum.Value;
                    }

                    throw;
                }

                return (int)checksum.Value;
            }
        }

        protected interface ParserFactory
        {
            int parseFile(ICharStream input, int thread);
        }

        private class DescriptiveErrorListener : BaseErrorListener
        {
            public static DescriptiveErrorListener INSTANCE = new DescriptiveErrorListener();

            public override void SyntaxError(IRecognizer recognizer, IToken offendingSymbol, int line, int charPositionInLine, string msg, RecognitionException e)
            {
                if (!REPORT_SYNTAX_ERRORS)
                {
                    return;
                }

                string sourceName = recognizer.InputStream.SourceName;
                if (!string.IsNullOrEmpty(sourceName))
                {
                    sourceName = string.Format("{0}:{1}:{2}: ", sourceName, line, charPositionInLine);
                }

                Console.Error.WriteLine(sourceName + "line " + line + ":" + charPositionInLine + " " + msg);
            }
        }

        private class SummarizingDiagnosticErrorListener : DiagnosticErrorListener
        {
            public override void ReportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet ambigAlts, ATNConfigSet configs)
            {
                if (!REPORT_AMBIGUITIES)
                {
                    return;
                }

                base.ReportAmbiguity(recognizer, dfa, startIndex, stopIndex, ambigAlts, configs);
            }

            public override void ReportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, SimulatorState initialState)
            {
                if (!REPORT_FULL_CONTEXT)
                {
                    return;
                }

                base.ReportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, initialState);
            }

            public override void ReportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, SimulatorState acceptState)
            {
                if (!REPORT_CONTEXT_SENSITIVITY)
                {
                    return;
                }

                base.ReportContextSensitivity(recognizer, dfa, startIndex, stopIndex, acceptState);
            }

            protected override string GetDecisionDescription(Parser recognizer, int decision)
            {
                string format = "{0}({1})";
                string ruleName = recognizer.RuleNames[recognizer.Atn.decisionToState[decision].ruleIndex];
                return string.Format(format, decision, ruleName);
            }
        }

        protected class NonCachingLexerATNSimulator : LexerATNSimulator
        {
            public NonCachingLexerATNSimulator(Lexer recog, ATN atn)
                : base(recog, atn)
            {
            }

            protected override DFAState AddDFAState(ATNConfigSet configs)
            {
                return null;
            }
        }

        protected class NonCachingParserATNSimulator : ParserATNSimulator
        {
            public NonCachingParserATNSimulator(Parser parser, ATN atn)
                : base(parser, atn)
            {
            }

            [return: NotNull]
            protected override DFAState CreateDFAState([NotNull] ATNConfigSet configs)
            {
                return new DFAState(configs, -1, -1);
            }
        }

        protected class ChecksumParseTreeListener : IParseTreeListener
        {
            private const int VISIT_TERMINAL = 1;
            private const int VISIT_ERROR_NODE = 2;
            private const int ENTER_RULE = 3;
            private const int EXIT_RULE = 4;

            private readonly Checksum checksum;

            public ChecksumParseTreeListener(Checksum checksum)
            {
                this.checksum = checksum;
            }

            public void VisitTerminal(ITerminalNode node)
            {
                checksum.Update(VISIT_TERMINAL);
                updateChecksum(checksum, node.Symbol);
            }

            public void VisitErrorNode(IErrorNode node)
            {
                checksum.Update(VISIT_ERROR_NODE);
                updateChecksum(checksum, node.Symbol);
            }

            public void EnterEveryRule(ParserRuleContext ctx)
            {
                checksum.Update(ENTER_RULE);
                updateChecksum(checksum, ctx.GetRuleIndex());
                updateChecksum(checksum, ctx.Start);
            }

            public void ExitEveryRule(ParserRuleContext ctx)
            {
                checksum.Update(EXIT_RULE);
                updateChecksum(checksum, ctx.GetRuleIndex());
                updateChecksum(checksum, ctx.Stop);
            }
        }
    }
}
