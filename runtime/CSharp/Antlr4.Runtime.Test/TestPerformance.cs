namespace Antlr4.Runtime.Test
{
    using System;
    using System.Collections.Generic;
    using System.Reflection;
    using System.Text;
    using Antlr4.Runtime;
    using Antlr4.Runtime.Atn;
    using Antlr4.Runtime.Dfa;
    using Antlr4.Runtime.Misc;
    using Antlr4.Runtime.Tree;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using BitSet = Sharpen.BitSet;
    using Checksum = Sharpen.Checksum;
    using CRC32 = Sharpen.CRC32;
    using Debug = System.Diagnostics.Debug;
    using DirectoryInfo = System.IO.DirectoryInfo;
    using File = System.IO.File;
    using FileInfo = System.IO.FileInfo;
    using Interlocked = System.Threading.Interlocked;
    using IOException = System.IO.IOException;
    using Path = System.IO.Path;
    using SearchOption = System.IO.SearchOption;
    using Stopwatch = System.Diagnostics.Stopwatch;
    using Stream = System.IO.Stream;
    using StreamReader = System.IO.StreamReader;
    using Thread = System.Threading.Thread;

#if NET40PLUS
    using System.Collections.Concurrent;
    using System.Threading.Tasks;
    using System.Threading.Tasks.Schedulers;
    using CancellationToken = System.Threading.CancellationToken;
#endif

    [TestClass]
    public class TestPerformance : BaseTest
    {
        /**
         * Parse all java files under this package within the JDK_SOURCE_ROOT
         * (environment variable or property defined on the Java command line).
         */
        private static readonly string TOP_PACKAGE = "java.lang";
        /**
         * {@code true} to load java files from sub-packages of
         * {@link #TOP_PACKAGE}.
         */
        private static readonly bool RECURSIVE = true;
        /**
         * {@code true} to read all source files from disk into memory before
         * starting the parse. The default value is {@code true} to help prevent
         * drive speed from affecting the performance results. This value may be set
         * to {@code false} to support parsing large input sets which would not
         * otherwise fit into memory.
         */
        private static readonly bool PRELOAD_SOURCES = true;
        /**
         * The encoding to use when reading source files.
         */
        private static readonly Encoding ENCODING = Encoding.UTF8;

        /**
         * {@code true} to use the Java grammar with expressions in the v4
         * left-recursive syntax (Java-LR.g4). {@code false} to use the standard
         * grammar (Java.g4). In either case, the grammar is renamed in the
         * temporary directory to Java.g4 before compiling.
         */
        private static readonly bool USE_LR_GRAMMAR = true;
        /**
         * {@code true} to specify the {@code -Xforce-atn} option when generating
         * the grammar, forcing all decisions in {@code JavaParser} to be handled by
         * {@link ParserATNSimulator#adaptivePredict}.
         */
        private static readonly bool FORCE_ATN = false;
        /**
         * {@code true} to specify the {@code -atn} option when generating the
         * grammar. This will cause ANTLR to export the ATN for each decision as a
         * DOT (GraphViz) file.
         */
        private static readonly bool EXPORT_ATN_GRAPHS = true;
        /**
         * {@code true} to specify the {@code -XdbgST} option when generating the
         * grammar.
         */
        private static readonly bool DEBUG_TEMPLATES = false;
        /**
         * {@code true} to specify the {@code -XdbgSTWait} option when generating the
         * grammar.
         */
        private static readonly bool DEBUG_TEMPLATES_WAIT = DEBUG_TEMPLATES;
        /**
         * {@code true} to delete temporary (generated and compiled) files when the
         * test completes.
         */
        private static readonly bool DELETE_TEMP_FILES = true;

        /**
         * {@code true} to call {@link System#gc} and then wait for 5 seconds at the
         * end of the test to make it easier for a profiler to grab a heap dump at
         * the end of the test run.
         */
        private static readonly bool PAUSE_FOR_HEAP_DUMP = false;

        /**
         * Parse each file with {@code JavaParser.compilationUnit}.
         */
        private static readonly bool RUN_PARSER = true;
        /**
         * {@code true} to use {@link BailErrorStrategy}, {@code false} to use
         * {@link DefaultErrorStrategy}.
         */
        private static readonly bool BAIL_ON_ERROR = false;
        /**
         * {@code true} to compute a checksum for verifying consistency across
         * optimizations and multiple passes.
         */
        private static readonly bool COMPUTE_CHECKSUM = true;
        /**
         * This value is passed to {@link Parser#setBuildParseTree}.
         */
        private static readonly bool BUILD_PARSE_TREES = false;
        /**
         * Use
         * {@link ParseTreeWalker#DEFAULT}{@code .}{@link ParseTreeWalker#walk walk}
         * with the {@code JavaParserBaseListener} to show parse tree walking
         * overhead. If {@link #BUILD_PARSE_TREES} is {@code false}, the listener
         * will instead be called during the parsing process via
         * {@link Parser#addParseListener}.
         */
        private static readonly bool BLANK_LISTENER = false;

        private static readonly bool EXPORT_LARGEST_CONFIG_CONTEXTS = false;

        /**
         * Shows the number of {@link DFAState} and {@link ATNConfig} instances in
         * the DFA cache at the end of each pass. If {@link #REUSE_LEXER_DFA} and/or
         * {@link #REUSE_PARSER_DFA} are false, the corresponding instance numbers
         * will only apply to one file (the last file if {@link #NUMBER_OF_THREADS}
         * is 0, otherwise the last file which was parsed on the first thread).
         */
        private static readonly bool SHOW_DFA_STATE_STATS = true;

        private static readonly bool ENABLE_LEXER_DFA = true;

        private static readonly bool ENABLE_PARSER_DFA = true;

        private static readonly PredictionMode PREDICTION_MODE = PredictionMode.Ll;
        private static readonly bool FORCE_GLOBAL_CONTEXT = false;
        private static readonly bool TRY_LOCAL_CONTEXT_FIRST = true;
        private static readonly bool OPTIMIZE_LL1 = true;
        private static readonly bool OPTIMIZE_UNIQUE_CLOSURE = true;
        private static readonly bool OPTIMIZE_HIDDEN_CONFLICTED_CONFIGS = false;
        private static readonly bool OPTIMIZE_TAIL_CALLS = true;
        private static readonly bool TAIL_CALL_PRESERVES_SLL = true;
        private static readonly bool TREAT_SLLK1_CONFLICT_AS_AMBIGUITY = false;

        private static readonly bool TWO_STAGE_PARSING = true;

        private static readonly bool SHOW_CONFIG_STATS = false;

        private static readonly bool REPORT_SYNTAX_ERRORS = true;
        private static readonly bool REPORT_AMBIGUITIES = false;
        private static readonly bool REPORT_FULL_CONTEXT = false;
        private static readonly bool REPORT_CONTEXT_SENSITIVITY = REPORT_FULL_CONTEXT;

        /**
         * If {@code true}, a single {@code JavaLexer} will be used, and
         * {@link Lexer#setInputStream} will be called to initialize it for each
         * source file. Otherwise, a new instance will be created for each file.
         */
        private static readonly bool REUSE_LEXER = false;
        /**
         * If {@code true}, a single DFA will be used for lexing which is shared
         * across all threads and files. Otherwise, each file will be lexed with its
         * own DFA which is accomplished by creating one ATN instance per thread and
         * clearing its DFA cache before lexing each file.
         */
        private static readonly bool REUSE_LEXER_DFA = true;
        /**
         * If {@code true}, a single {@code JavaParser} will be used, and
         * {@link Parser#setInputStream} will be called to initialize it for each
         * source file. Otherwise, a new instance will be created for each file.
         */
        private static readonly bool REUSE_PARSER = false;
        /**
         * If {@code true}, a single DFA will be used for parsing which is shared
         * across all threads and files. Otherwise, each file will be parsed with
         * its own DFA which is accomplished by creating one ATN instance per thread
         * and clearing its DFA cache before parsing each file.
         */
        private static readonly bool REUSE_PARSER_DFA = true;
        /**
         * If {@code true}, the shared lexer and parser are reset after each pass.
         * If {@code false}, all passes after the first will be fully "warmed up",
         * which makes them faster and can compare them to the first warm-up pass,
         * but it will not distinguish bytecode load/JIT time from warm-up time
         * during the first pass.
         */
        private static readonly bool CLEAR_DFA = false;
        /**
         * Total number of passes to make over the source.
         */
        private static readonly int PASSES = 4;

        /**
         * Number of parser threads to use.
         * 
         * <remarks>
         * This value is ignored for .NET Framework 3.5 and earlier.
         * </remarks>
         */
        private static readonly int NUMBER_OF_THREADS = 1;

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

            IEnumerable<InputDescriptor> sources = LoadSources(directory, "*.java", RECURSIVE);

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

                    for (int j = 0; j < sharedLexers.Length; j++)
                        sharedLexers[j] = null;
                    for (int j = 0; j < sharedParsers.Length; j++)
                        sharedParsers[j] = null;
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
        protected void parse1(ParserFactory factory, IEnumerable<InputDescriptor> sources)
        {
            GC.Collect();
            parseSources(factory, sources);
        }

        /**
         *  This method is separate from {@link #parse1} so the first pass can be distinguished when analyzing
         *  profiler results.
         */
        protected void parse2(ParserFactory factory, IEnumerable<InputDescriptor> sources)
        {
            GC.Collect();
            parseSources(factory, sources);
        }

        protected IList<InputDescriptor> LoadSources(DirectoryInfo directory, string filter, bool recursive)
        {
            IList<InputDescriptor> result = new List<InputDescriptor>();
            LoadSources(directory, filter, recursive, result);
            return result;
        }

        protected void LoadSources(DirectoryInfo directory, string filter, bool recursive, ICollection<InputDescriptor> result)
        {
            Debug.Assert(directory.Exists);

            FileInfo[] sources = directory.GetFiles(filter, recursive ? SearchOption.AllDirectories : SearchOption.TopDirectoryOnly);
            foreach (FileInfo file in sources)
            {
                result.Add(new InputDescriptor(file.FullName));
            }
        }

        int configOutputSize = 0;

        protected void parseSources(ParserFactory factory, IEnumerable<InputDescriptor> sources)
        {
            Stopwatch startTime = Stopwatch.StartNew();
            Thread.VolatileWrite(ref tokenCount, 0);
            int sourceCount = 0;
            int inputSize = 0;

#if NET40PLUS
            BlockingCollection<int> threadIdentifiers = new BlockingCollection<int>();
            for (int i = 0; i < NUMBER_OF_THREADS; i++)
                threadIdentifiers.Add(i);

            ICollection<Task<int>> results = new List<Task<int>>();
            QueuedTaskScheduler executorServiceHost = new QueuedTaskScheduler(NUMBER_OF_THREADS);
            TaskScheduler executorService = executorServiceHost.ActivateNewQueue();
#else
            ICollection<Func<int>> results = new List<Func<int>>();
#endif
            foreach (InputDescriptor inputDescriptor in sources)
            {
                ICharStream input = inputDescriptor.GetInputStream();
                sourceCount++;
                input.Seek(0);
                inputSize += input.Size;
#if NET40PLUS
                Task<int> futureChecksum = Task.Factory.StartNew<int>(new Callable_1(input, factory, threadIdentifiers).call, CancellationToken.None, TaskCreationOptions.None, executorService);
#else
                Func<int> futureChecksum = new Callable_1(input, factory).call;
#endif
                results.Add(futureChecksum);
            }

            Checksum checksum = new CRC32();
            foreach (var future in results)
            {
#if NET40PLUS
                int value = future.Result;
#else
                int value = future();
#endif
                if (COMPUTE_CHECKSUM)
                {
                    updateChecksum(checksum, value);
                }
            }

#if NET40PLUS
            executorServiceHost.Dispose();
#endif

            Console.Out.WriteLine("Total parse time for {0} files ({1} KB, {2} tokens, checksum 0x{3:X8}): {4}ms",
                              sourceCount,
                              inputSize / 1024,
                              Thread.VolatileRead(ref tokenCount),
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
                    HashSet<ATNConfig> uniqueConfigs = new HashSet<ATNConfig>();

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

                    Console.Out.WriteLine("There are {0} lexer DFAState instances, {1} configs ({2} unique), {3} prediction contexts.", states, configs, uniqueConfigs.Count, lexerInterpreter.atn.ContextCacheSize);
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
                    HashSet<ATNConfig> uniqueConfigs = new HashSet<ATNConfig>();

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

                    Console.Out.WriteLine("There are {0} parser DFAState instances, {1} configs ({2} unique), {3} prediction contexts.", states, configs, uniqueConfigs.Count, interpreter.atn.ContextCacheSize);
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
#if NET40PLUS
            private readonly BlockingCollection<int> threadNumbers;
#endif

#if NET40PLUS
            public Callable_1(ICharStream input, ParserFactory factory, BlockingCollection<int> threadNumbers)
#else
            public Callable_1(ICharStream input, ParserFactory factory)
#endif
            {
                this.input = input;
                this.factory = factory;
#if NET40PLUS
                this.threadNumbers = threadNumbers;
#endif
            }

            public int call()
            {
                // this incurred a great deal of overhead and was causing significant variations in performance results.
                //Console.Out.WriteLine("Parsing file {0}", input.getSourceName());
#if NET40PLUS
                int threadNumber = threadNumbers.Take();
#else
                int threadNumber = 0;
#endif
                try
                {
                    return factory.parseFile(input, threadNumber);
                }
                finally
                {
#if NET40PLUS
                    threadNumbers.Add(threadNumber);
#endif
                }
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
                    sharedLexerATNs[i] = new ATNDeserializer().Deserialize(lexerSerializedATN.ToCharArray());
                }
            }

            if (RUN_PARSER && !REUSE_PARSER_DFA)
            {
                FieldInfo parserSerializedATNField = parserClass.GetField("_serializedATN");
                string parserSerializedATN = (string)parserSerializedATNField.GetValue(null);
                for (int i = 0; i < NUMBER_OF_THREADS; i++)
                {
                    sharedParserATNs[i] = new ATNDeserializer().Deserialize(parserSerializedATN.ToCharArray());
                }
            }

            return new ParserFactory_1(listenerClass, parserClass, lexerCtor, parserCtor, entryPoint);
        }

        private class ParserFactory_1 : ParserFactory
        {
            private readonly Type listenerClass;
            private readonly Type parserClass;

            private readonly ConstructorInfo lexerCtor;
            private readonly ConstructorInfo parserCtor;

            private readonly string entryPoint;

            public ParserFactory_1(Type listenerClass, Type parserClass, ConstructorInfo lexerCtor, ConstructorInfo parserCtor, string entryPoint)
            {
                this.listenerClass = listenerClass;
                this.parserClass = parserClass;
                this.lexerCtor = lexerCtor;
                this.parserCtor = parserCtor;
                this.entryPoint = entryPoint;
            }

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
            public override void ReportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, bool exact, BitSet ambigAlts, ATNConfigSet configs)
            {
                if (!REPORT_AMBIGUITIES)
                {
                    return;
                }

                base.ReportAmbiguity(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs);
            }

            public override void ReportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, SimulatorState initialState)
            {
                if (!REPORT_FULL_CONTEXT)
                {
                    return;
                }

                base.ReportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, conflictingAlts, initialState);
            }

            public override void ReportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, SimulatorState acceptState)
            {
                if (!REPORT_CONTEXT_SENSITIVITY)
                {
                    return;
                }

                base.ReportContextSensitivity(recognizer, dfa, startIndex, stopIndex, prediction, acceptState);
            }

            protected override string GetDecisionDescription(Parser recognizer, DFA dfa)
            {
                string format = "{0}({1})";
                string ruleName = recognizer.RuleNames[recognizer.Atn.decisionToState[dfa.decision].ruleIndex];
                return string.Format(format, dfa.decision, ruleName);
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

        internal class ChecksumParseTreeListener : IParseTreeListener
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
                updateChecksum(checksum, ctx.RuleIndex);
                updateChecksum(checksum, ctx.Start);
            }

            public void ExitEveryRule(ParserRuleContext ctx)
            {
                checksum.Update(EXIT_RULE);
                updateChecksum(checksum, ctx.RuleIndex);
                updateChecksum(checksum, ctx.Stop);
            }
        }

        protected sealed class InputDescriptor
        {
            private readonly string source;
            private WeakReference<CloneableAntlrFileStream> inputStream;
            private CloneableAntlrFileStream strongInputStream;

            public InputDescriptor([NotNull] String source)
            {
                this.source = source;
                if (PRELOAD_SOURCES)
                {
                    GetInputStream();
                }
            }

            [return: NotNull]
            public ICharStream GetInputStream()
            {
                CloneableAntlrFileStream stream;
                if (!TryGetTarget(out stream))
                {
                    stream = new CloneableAntlrFileStream(source, ENCODING);
                    SetTarget(stream);
                }

                return new JavaUnicodeInputStream(stream.CreateCopy());
            }

            private void SetTarget(CloneableAntlrFileStream stream)
            {
                if (PRELOAD_SOURCES)
                {
                    strongInputStream = stream;
                }
                else
                {
                    inputStream = new WeakReference<CloneableAntlrFileStream>(stream);
                }
            }

            private bool TryGetTarget(out CloneableAntlrFileStream stream)
            {
                if (PRELOAD_SOURCES)
                {
                    stream = strongInputStream;
                    return strongInputStream != null;
                }
                else
                {
                    if (inputStream == null)
                    {
                        stream = null;
                        return false;
                    }

                    return inputStream.TryGetTarget(out stream);
                }
            }
        }

#if PORTABLE
        protected class CloneableAntlrFileStream : AntlrInputStream
#else
        protected class CloneableAntlrFileStream : AntlrFileStream
#endif
        {
            public CloneableAntlrFileStream(String fileName, Encoding encoding)
#if PORTABLE
                : base(File.ReadAllText(fileName, encoding))
#else
                : base(fileName, encoding)
#endif
            {
            }

            public AntlrInputStream CreateCopy()
            {
                AntlrInputStream stream = new AntlrInputStream(this.data, this.n);
                stream.name = this.SourceName;
                return stream;
            }
        }

#if !NET45
        private sealed class WeakReference<T>
            where T : class
        {
            private readonly WeakReference _reference;

            public WeakReference(T reference)
            {
                _reference = new WeakReference(reference);
            }

            public bool TryGetTarget(out T reference)
            {
                reference = (T)_reference.Target;
                return reference != null;
            }
        }
#endif
    }
}
