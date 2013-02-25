namespace Antlr4.Runtime.Test
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using Antlr4.Runtime.Misc;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Directory = System.IO.Directory;
    using DirectoryInfo = System.IO.DirectoryInfo;
    using File = System.IO.File;
    using IOException = System.IO.IOException;
    using Path = System.IO.Path;
    using RegistryHive = Microsoft.Win32.RegistryHive;
    using RegistryKey = Microsoft.Win32.RegistryKey;
    using RegistryView = Microsoft.Win32.RegistryView;
    using StreamReader = System.IO.StreamReader;
    using TextReader = System.IO.TextReader;
    using Thread = System.Threading.Thread;

    public abstract class BaseTest
    {
#if false
        // -J-Dorg.antlr.v4.test.BaseTest.level=FINE
        private static readonly Logger LOGGER = Logger.getLogger(typeof(BaseTest).getName());

        public static readonly string newline = System.getProperty("line.separator");
        public static readonly string pathSep = System.getProperty("path.separator");

        public static readonly bool TEST_IN_SAME_PROCESS = Boolean.parseBoolean(System.getProperty("antlr.testinprocess"));
        public static readonly bool STRICT_COMPILE_CHECKS = Boolean.parseBoolean(System.getProperty("antlr.strictcompile"));

        /**
         * Build up the full classpath we need, including the surefire path (if present)
         */
        public static readonly string CLASSPATH = System.getProperty("java.class.path");
#endif

        public string tmpdir = null;

        /** If error during parser execution, store stderr here; can't return
         *  stdout and stderr.  This doesn't trap errors from running antlr.
         */
        protected string stderrDuringParse;

        public TestContext TestContext
        {
            get;
            set;
        }

        [TestCleanup]
        public void TestCleanup()
        {
            if (TestContext.CurrentTestOutcome == UnitTestOutcome.Passed)
            {
                // remove tmpdir if no error.
                eraseTempDir();
            }
        }

        [TestInitialize]
        public void TestInitialize()
        {
            // new output dir for each test
            string tempTestFolder = GetType().Name + "-" + (DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond);
            string dir = Path.Combine(Path.GetTempPath(), tempTestFolder);
            if (Directory.Exists(dir))
                throw new InvalidOperationException();

            tmpdir = dir;
        }

        /** Wow! much faster than compiling outside of VM. Finicky though.
         *  Had rules called r and modulo. Wouldn't compile til I changed to 'a'.
         */
        protected virtual bool compile(params string[] fileNames)
        {
            DirectoryInfo outputDir = new DirectoryInfo(tmpdir);
            try
            {
                string windows = Environment.GetFolderPath(Environment.SpecialFolder.Windows);
                string compiler = Path.Combine(windows, "Microsoft.NET", "Framework64", "v4.0.30319", "csc.exe");

                List<string> args = new List<string>();
                args.AddRange(getCompileOptions());

                bool hasTestClass = false;
                foreach (String fileName in fileNames)
                {
                    if (fileName.Equals("Test.cs"))
                    {
                        hasTestClass = true;
                    }

                    if (fileName.EndsWith(".dll"))
                    {
                        args.Add("/reference:" + fileName);
                    }
                    else
                    {
                        args.Add(fileName);
                    }
                }

                if (hasTestClass)
                {
                    args.Insert(1, "/target:exe");
                    args.Insert(1, "/reference:Parser.dll");
                    args.Insert(1, "/out:Test.exe");
                }
                else
                {
                    args.Insert(1, "/target:library");
                    args.Insert(1, "/out:Parser.dll");
                }

                System.Diagnostics.Process process = System.Diagnostics.Process.Start(new System.Diagnostics.ProcessStartInfo(compiler, '"' + string.Join("\" \"", args) + '"')
                {
                    UseShellExecute = false,
                    CreateNoWindow = true,
                    RedirectStandardOutput = true,
                    RedirectStandardError = true,
                    WorkingDirectory = tmpdir
                });

                StreamVacuum stdout = new StreamVacuum(process.StandardOutput);
                StreamVacuum stderr = new StreamVacuum(process.StandardError);
                stdout.start();
                stderr.start();
                process.WaitForExit();
                stdout.join();
                stderr.join();
                if (stdout.ToString().Length > 0)
                {
                    Console.Error.WriteLine("compile stdout from: " + string.Join(" ", args));
                    Console.Error.WriteLine(stdout);
                }
                if (stderr.ToString().Length > 0)
                {
                    Console.Error.WriteLine("compile stderr from: " + string.Join(" ", args));
                    Console.Error.WriteLine(stderr);
                }
                int ret = process.ExitCode;
                return ret == 0;
            }
            catch (Exception e)
            {
                Console.Error.WriteLine("can't exec compilation");
                //e.printStackTrace(System.err);
                return false;
            }
        }

        public virtual IList<string> getCompileOptions()
        {
            IList<string> compileOptions = new List<string>();
            compileOptions.Add("/debug");
            compileOptions.Add("/warn:4");
            compileOptions.Add("/nologo");
            compileOptions.Add("/reference:" + typeof(Lexer).Assembly.Location);
            return compileOptions;
        }

#if false
        public string getBootClassPath()
        {
            string path = System.getProperty("bootclasspath.java6");
            if (path != null)
            {
                return path;
            }

            path = System.getProperty("java6.home");
            if (path == null)
            {
                path = System.getenv("JAVA6_HOME");
            }

            if (path != null)
            {
                return path + File.separatorChar + "lib" + File.separatorChar + "rt.jar";
            }

            return null;
        }
#endif

        protected virtual string JavaHome
        {
            get
            {
                string javaKey = "SOFTWARE\\JavaSoft\\Java Runtime Environment";
                using (var baseKey = RegistryKey.OpenBaseKey(RegistryHive.LocalMachine, RegistryView.Default).OpenSubKey(javaKey))
                {
                    string currentVersion = baseKey.GetValue("CurrentVersion").ToString();
                    using (var homeKey = baseKey.OpenSubKey(currentVersion))
                        return homeKey.GetValue("JavaHome").ToString();
                }
            }
        }

        protected virtual string MavenHome
        {
            get
            {
                string mavenHome = Environment.GetEnvironmentVariable("M2_HOME");
                if (!Directory.Exists(mavenHome))
                    mavenHome = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.UserProfile), ".m2");

                return mavenHome;
            }
        }

        protected virtual string GetMavenArtifact(string groupId, string artifactId, string version, string classifier = null)
        {
            string folder = Path.Combine(MavenHome, "repository", groupId.Replace('.', Path.DirectorySeparatorChar), artifactId, version);
            string fileNameFormat = string.IsNullOrEmpty(classifier) ? "{0}-{1}.jar" : "{0}-{1}-{2}.jar";
            string fileName = string.Format(fileNameFormat, artifactId, version, classifier);
            return Path.Combine(folder, fileName);
        }

        /** Return true if all is ok, no errors */
        protected virtual bool antlr(string fileName, string grammarFileName, string grammarStr, bool defaultListener, params string[] extraOptions)
        {
            mkdir(tmpdir);
            writeFile(tmpdir, fileName, grammarStr);
            try
            {
                string compiler = Path.Combine(JavaHome, "bin", "java.exe");

                List<string> classpath = new List<string>();
                classpath.Add(GetMavenArtifact("com.tunnelvisionlabs", "antlr4-csharp", "4.0.1-SNAPSHOT"));
                classpath.Add(GetMavenArtifact("com.tunnelvisionlabs", "antlr4-runtime", "4.0.1-SNAPSHOT"));
                classpath.Add(GetMavenArtifact("com.tunnelvisionlabs", "antlr4", "4.0.1-SNAPSHOT"));
                classpath.Add(GetMavenArtifact("org.antlr", "antlr-runtime", "3.5"));
                classpath.Add(GetMavenArtifact("org.antlr", "ST4", "4.0.7"));

                List<string> options = new List<string>();
                options.Add("-cp");
                options.Add(string.Join(";", classpath));
                options.Add("org.antlr.v4.Tool");

                options.AddRange(extraOptions);
                options.Add("-o");
                options.Add(tmpdir);
                options.Add("-lib");
                options.Add(tmpdir);
                options.Add("-Dlanguage=CSharp");
                options.Add(grammarFileName);

                System.Diagnostics.Process process = System.Diagnostics.Process.Start(new System.Diagnostics.ProcessStartInfo(compiler, '"' + string.Join("\" \"", options) + '"')
                {
                    UseShellExecute = false,
                    CreateNoWindow = true,
                    RedirectStandardOutput = true,
                    RedirectStandardError = true,
                    WorkingDirectory = tmpdir
                });

                StreamVacuum stdout = new StreamVacuum(process.StandardOutput);
                StreamVacuum stderr = new StreamVacuum(process.StandardError);
                stdout.start();
                stderr.start();
                process.WaitForExit();
                stdout.join();
                stderr.join();
                if (stdout.ToString().Length > 0)
                {
                    Console.Error.WriteLine("compile stdout from: " + string.Join(" ", options));
                    Console.Error.WriteLine(stdout);
                }
                if (stderr.ToString().Length > 0)
                {
                    Console.Error.WriteLine("compile stderr from: " + string.Join(" ", options));
                    Console.Error.WriteLine(stderr);
                }
                int ret = process.ExitCode;
                return ret == 0;
            }
            catch (Exception e)
            {
                Console.Error.WriteLine("can't exec compilation");
                //e.printStackTrace(System.err);
                return false;
            }
        }

#if false
        protected string execLexer(string grammarFileName,
                                   string grammarStr,
                                   string lexerName,
                                   string input)
        {
            return execLexer(grammarFileName, grammarStr, lexerName, input, false);
        }

        protected string execLexer(string grammarFileName,
                                   string grammarStr,
                                   string lexerName,
                                   string input,
                                   bool showDFA)
        {
            bool success = rawGenerateAndBuildRecognizer(grammarFileName,
                                          grammarStr,
                                          null,
                                          lexerName);
            Assert.IsTrue(success);
            writeFile(tmpdir, "input", input);
            writeLexerTestFile(lexerName, showDFA);
            compile("Test.java");
            string output = execClass("Test");
            if (stderrDuringParse != null && stderrDuringParse.Length > 0)
            {
                Console.Error.WriteLine(stderrDuringParse);
            }
            return output;
        }

        protected string execParser(string grammarFileName,
                                    string grammarStr,
                                    string parserName,
                                    string lexerName,
                                    string startRuleName,
                                    string input, bool debug)
        {
            bool success = rawGenerateAndBuildRecognizer(grammarFileName,
                                                            grammarStr,
                                                            parserName,
                                                            lexerName,
                                                            "-visitor");
            Assert.IsTrue(success);
            writeFile(tmpdir, "input", input);
            return rawExecRecognizer(parserName,
                                     lexerName,
                                     startRuleName,
                                     debug);
        }
#endif

        /** Return true if all is well */
        protected virtual bool rawGenerateAndBuildRecognizer(string grammarFileName,
                                                        string grammarStr,
                                                        [Nullable] string parserName,
                                                        string lexerName,
                                                        params string[] extraOptions)
        {
            return rawGenerateAndBuildRecognizer(grammarFileName, grammarStr, parserName, lexerName, false, extraOptions);
        }

        /** Return true if all is well */
        protected virtual bool rawGenerateAndBuildRecognizer(string grammarFileName,
                                                        string grammarStr,
                                                        [Nullable] string parserName,
                                                        string lexerName,
                                                        bool defaultListener,
                                                        params string[] extraOptions)
        {
            bool allIsWell =
                antlr(grammarFileName, grammarFileName, grammarStr, defaultListener, extraOptions);
            if (!allIsWell)
            {
                return false;
            }

            List<string> files = new List<string>();
            if (lexerName != null)
            {
                files.Add(lexerName + ".cs");
            }
            if (parserName != null)
            {
                files.Add(parserName + ".cs");
                ISet<string> optionsSet = new HashSet<string>(extraOptions);
                if (!optionsSet.Contains("-no-listener"))
                {
                    files.Add(grammarFileName.Substring(0, grammarFileName.LastIndexOf('.')) + "BaseListener.cs");
                    files.Add(grammarFileName.Substring(0, grammarFileName.LastIndexOf('.')) + "Listener.cs");
                }
                if (optionsSet.Contains("-visitor"))
                {
                    files.Add(grammarFileName.Substring(0, grammarFileName.LastIndexOf('.')) + "BaseVisitor.cs");
                    files.Add(grammarFileName.Substring(0, grammarFileName.LastIndexOf('.')) + "Visitor.cs");
                }
            }
            allIsWell = compile(files.ToArray());
            return allIsWell;
        }

#if false
        protected string rawExecRecognizer(string parserName,
                                           string lexerName,
                                           string parserStartRuleName,
                                           bool debug)
        {
            this.stderrDuringParse = null;
            if (parserName == null)
            {
                writeLexerTestFile(lexerName, false);
            }
            else
            {
                writeTestFile(parserName,
                              lexerName,
                              parserStartRuleName,
                              debug);
            }

            compile("Test.java");
            return execClass("Test");
        }

        public string execRecognizer()
        {
            return execClass("Test");
        }

        public string execClass(string className)
        {
            if (TEST_IN_SAME_PROCESS)
            {
                try
                {
                    ClassLoader loader = new URLClassLoader(new URL[] { new File(tmpdir).toURI().toURL() }, ClassLoader.getSystemClassLoader());
                    Class mainClass = (Class)loader.loadClass(className);
                    Method mainMethod = mainClass.getDeclaredMethod("main", typeof(string[]));
                    PipedInputStream stdoutIn = new PipedInputStream();
                    PipedInputStream stderrIn = new PipedInputStream();
                    PipedOutputStream stdoutOut = new PipedOutputStream(stdoutIn);
                    PipedOutputStream stderrOut = new PipedOutputStream(stderrIn);
                    StreamVacuum stdoutVacuum = new StreamVacuum(stdoutIn);
                    StreamVacuum stderrVacuum = new StreamVacuum(stderrIn);

                    PrintStream originalOut = Console.Out;
                    System.setOut(new PrintStream(stdoutOut));
                    try
                    {
                        PrintStream originalErr = System.err;
                        try
                        {
                            System.setErr(new PrintStream(stderrOut));
                            stdoutVacuum.start();
                            stderrVacuum.start();
                            mainMethod.invoke(null, (Object)new string[] { new File(tmpdir, "input").getAbsolutePath() });
                        }
                        finally
                        {
                            System.setErr(originalErr);
                        }
                    }
                    finally
                    {
                        System.setOut(originalOut);
                    }

                    stdoutOut.close();
                    stderrOut.close();
                    stdoutVacuum.join();
                    stderrVacuum.join();
                    string output = stdoutVacuum.tostring();
                    if (stderrVacuum.tostring().length() > 0)
                    {
                        this.stderrDuringParse = stderrVacuum.tostring();
                        Console.Error.WriteLine("exec stderrVacuum: " + stderrVacuum);
                    }
                    return output;
                }
                catch (MalformedURLException ex)
                {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
                catch (IOException ex)
                {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
                catch (InterruptedException ex)
                {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
                catch (IllegalAccessException ex)
                {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
                catch (IllegalArgumentException ex)
                {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
                catch (InvocationTargetException ex)
                {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
                catch (NoSuchMethodException ex)
                {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
                catch (SecurityException ex)
                {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
                catch (ClassNotFoundException ex)
                {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }

            try
            {
                string[] args = new string[] {
				"java", "-classpath", tmpdir+pathSep+CLASSPATH,
				className, new File(tmpdir, "input").getAbsolutePath()
			};
                //string cmdLine = "java -classpath "+CLASSPATH+pathSep+tmpdir+" Test " + new File(tmpdir, "input").getAbsolutePath();
                //Console.WriteLine("execParser: "+cmdLine);
                Process process =
                    Runtime.getRuntime().exec(args, null, new File(tmpdir));
                StreamVacuum stdoutVacuum = new StreamVacuum(process.getInputStream());
                StreamVacuum stderrVacuum = new StreamVacuum(process.getErrorStream());
                stdoutVacuum.start();
                stderrVacuum.start();
                process.waitFor();
                stdoutVacuum.join();
                stderrVacuum.join();
                string output = stdoutVacuum.tostring();
                if (stderrVacuum.tostring().length() > 0)
                {
                    this.stderrDuringParse = stderrVacuum.tostring();
                    Console.Error.WriteLine("exec stderrVacuum: " + stderrVacuum);
                }
                return output;
            }
            catch (Exception e)
            {
                Console.Error.WriteLine("can't exec recognizer");
                e.printStackTrace(System.err);
            }
            return null;
        }

        public void testErrors(string[] pairs, bool printTree)
        {
            for (int i = 0; i < pairs.length; i += 2)
            {
                string input = pairs[i];
                string expect = pairs[i + 1];
                ErrorQueue equeue = new ErrorQueue();
                Grammar g = null;
                try
                {
                    string[] lines = input.split("\n");
                    string fileName = getFilenameFromFirstLineOfGrammar(lines[0]);
                    if (input.startsWith("lexer "))
                    {
                        g = new LexerGrammar(fileName, input, equeue);
                    }
                    else
                    {
                        g = new Grammar(fileName, input, equeue);
                    }
                }
                catch (UnsupportedOperationException ex)
                {
                }
                catch (org.antlr.runtime.RecognitionException re)
                {
                    re.printStackTrace(System.err);
                }
                string actual = equeue.tostring(g != null ? g.tool : new Tool());
                Console.Error.WriteLine(actual);
                string msg = input;
                msg = msg.replaceAll("\n", "\\\\n");
                msg = msg.replaceAll("\r", "\\\\r");
                msg = msg.replaceAll("\t", "\\\\t");

                assertEquals("error in: " + msg, expect, actual);
            }
        }

        public string getFilenameFromFirstLineOfGrammar(string line)
        {
            string fileName = "<string>";
            int grIndex = line.lastIndexOf("grammar");
            int semi = line.lastIndexOf(';');
            if (grIndex >= 0 && semi >= 0)
            {
                int space = line.indexOf(' ', grIndex);
                fileName = line.substring(space + 1, semi) + Tool.GRAMMAR_EXTENSION;
            }
            if (fileName.length() == Tool.GRAMMAR_EXTENSION.length())
                fileName = "<string>";
            return fileName;
        }

        //	void ambig(IList<Message> msgs, int[] expectedAmbigAlts, string expectedAmbigInput)
        //		throws Exception
        //	{
        //		ambig(msgs, 0, expectedAmbigAlts, expectedAmbigInput);
        //	}

        //	void ambig(IList<Message> msgs, int i, int[] expectedAmbigAlts, string expectedAmbigInput)
        //		throws Exception
        //	{
        //		IList<Message> amsgs = getMessagesOfType(msgs, AmbiguityMessage.class);
        //		AmbiguityMessage a = (AmbiguityMessage)amsgs.get(i);
        //		if ( a==null ) assertNull(expectedAmbigAlts);
        //		else {
        //			assertEquals(a.conflictingAlts.tostring(), Arrays.tostring(expectedAmbigAlts));
        //		}
        //		assertEquals(expectedAmbigInput, a.input);
        //	}

        //	void unreachable(IList<Message> msgs, int[] expectedUnreachableAlts)
        //		throws Exception
        //	{
        //		unreachable(msgs, 0, expectedUnreachableAlts);
        //	}

        //	void unreachable(IList<Message> msgs, int i, int[] expectedUnreachableAlts)
        //		throws Exception
        //	{
        //		IList<Message> amsgs = getMessagesOfType(msgs, UnreachableAltsMessage.class);
        //		UnreachableAltsMessage u = (UnreachableAltsMessage)amsgs.get(i);
        //		if ( u==null ) assertNull(expectedUnreachableAlts);
        //		else {
        //			assertEquals(u.conflictingAlts.tostring(), Arrays.tostring(expectedUnreachableAlts));
        //		}
        //	}

        IList<ANTLRMessage> getMessagesOfType(IList<ANTLRMessage> msgs, Class<ANTLRMessage> c)
        {
            IList<ANTLRMessage> filtered = new List<ANTLRMessage>();
            foreach (ANTLRMessage m in msgs)
            {
                if (m.getClass() == c)
                    filtered.add(m);
            }
            return filtered;
        }

        void checkRuleATN(Grammar g, string ruleName, string expecting)
        {
            ParserATNFactory f = new ParserATNFactory(g);
            ATN atn = f.createATN();

            DOTGenerator dot = new DOTGenerator(g);
            Console.WriteLine(dot.getDOT(atn.ruleToStartState[g.getRule(ruleName).index]));

            Rule r = g.getRule(ruleName);
            ATNState startState = atn.ruleToStartState[r.index];
            ATNPrinter serializer = new ATNPrinter(g, startState);
            string result = serializer.asstring();

            //System.out.print(result);
            assertEquals(expecting, result);
        }

        public void testActions(string templates, string actionName, string action, string expected)
        {
            int lp = templates.indexOf('(');
            string name = templates.substring(0, lp);
            STGroup group = new STGroupstring(templates);
            ST st = group.getInstanceOf(name);
            st.add(actionName, action);
            string grammar = st.render();
            ErrorQueue equeue = new ErrorQueue();
            Grammar g = new Grammar(grammar);
            if (g.ast != null && !g.ast.hasErrors)
            {
                SemanticPipeline sem = new SemanticPipeline(g);
                sem.process();

                ATNFactory factory = new ParserATNFactory(g);
                if (g.isLexer())
                    factory = new LexerATNFactory((LexerGrammar)g);
                g.atn = factory.createATN();

                CodeGenerator gen = new CodeGenerator(g);
                ST outputFileST = gen.generateParser();
                string output = outputFileST.render();
                //Console.WriteLine(output);
                string b = "#" + actionName + "#";
                int start = output.indexOf(b);
                string e = "#end-" + actionName + "#";
                int end = output.indexOf(e);
                string snippet = output.substring(start + b.length(), end);
                assertEquals(expected, snippet);
            }
            if (equeue.Count > 0)
            {
                Console.Error.WriteLine(equeue.tostring(g.tool));
            }
        }
#endif

        public class StreamVacuum
        {
            private readonly StringBuilder buf = new StringBuilder();
            private readonly TextReader @in;
            private Thread sucker;

            public StreamVacuum(StreamReader @in)
            {
                this.@in = @in;
            }

            public void start()
            {
                sucker = new Thread(run);
                sucker.Start();
            }

            public void run()
            {
                try
                {
                    string line = @in.ReadLine();
                    while (line != null)
                    {
                        buf.AppendLine(line);
                        line = @in.ReadLine();
                    }
                }
                catch (IOException)
                {
                    Console.Error.WriteLine("can't read output from process");
                }
            }

            /** wait for the thread to finish */
            public void join() /*throws InterruptedException*/ {
                sucker.Join();
            }

            public override string ToString()
            {
                return buf.ToString();
            }
        }

#if false
        protected void checkGrammarSemanticsError(ErrorQueue equeue,
                                                  GrammarSemanticsMessage expectedMessage)
        {
            ANTLRMessage foundMsg = null;
            for (int i = 0; i < equeue.errors.Count; i++)
            {
                ANTLRMessage m = equeue.errors.get(i);
                if (m.getErrorType() == expectedMessage.getErrorType())
                {
                    foundMsg = m;
                }
            }
            assertNotNull("no error; " + expectedMessage.getErrorType() + " expected", foundMsg);
            Assert.IsTrue("error is not a GrammarSemanticsMessage",
                       foundMsg is GrammarSemanticsMessage);
            assertEquals(Arrays.tostring(expectedMessage.getArgs()), Arrays.tostring(foundMsg.getArgs()));
            if (equeue.Count != 1)
            {
                Console.Error.WriteLine(equeue);
            }
        }

        protected void checkGrammarSemanticsWarning(ErrorQueue equeue,
                                                    GrammarSemanticsMessage expectedMessage)
        {
            ANTLRMessage foundMsg = null;
            for (int i = 0; i < equeue.warnings.Count; i++)
            {
                ANTLRMessage m = equeue.warnings.get(i);
                if (m.getErrorType() == expectedMessage.getErrorType())
                {
                    foundMsg = m;
                }
            }
            assertNotNull("no error; " + expectedMessage.getErrorType() + " expected", foundMsg);
            Assert.IsTrue("error is not a GrammarSemanticsMessage",
                       foundMsg is GrammarSemanticsMessage);
            assertEquals(Arrays.tostring(expectedMessage.getArgs()), Arrays.tostring(foundMsg.getArgs()));
            if (equeue.Count != 1)
            {
                Console.Error.WriteLine(equeue);
            }
        }

        protected void checkError(ErrorQueue equeue,
                                  ANTLRMessage expectedMessage)
        {
            //Console.WriteLine("errors="+equeue);
            ANTLRMessage foundMsg = null;
            for (int i = 0; i < equeue.errors.Count; i++)
            {
                ANTLRMessage m = equeue.errors.get(i);
                if (m.getErrorType() == expectedMessage.getErrorType())
                {
                    foundMsg = m;
                }
            }
            Assert.IsTrue("no error; " + expectedMessage.getErrorType() + " expected", !equeue.errors.isEmpty());
            Assert.IsTrue("too many errors; " + equeue.errors, equeue.errors.Count <= 1);
            assertNotNull("couldn't find expected error: " + expectedMessage.getErrorType(), foundMsg);
            /*
            Assert.IsTrue("error is not a GrammarSemanticsMessage",
                       foundMsg is GrammarSemanticsMessage);
             */
            assertArrayEquals(expectedMessage.getArgs(), foundMsg.getArgs());
        }

        public class FilteringTokenStream : CommonTokenStream
        {
            public FilteringTokenStream(TokenSource src)
            {
                super(src);
            }
            Set<Integer> hide = new HashSet<Integer>();
            protected override bool sync(int i)
            {
                if (!super.sync(i))
                {
                    return false;
                }

                Token t = get(i);
                if (hide.contains(t.getType()))
                {
                    ((WritableToken)t).setChannel(Token.HIDDEN_CHANNEL);
                }

                return true;
            }
            public void setTokenTypeChannel(int ttype, int channel)
            {
                hide.add(ttype);
            }
        }
#endif

        public static void writeFile(string dir, string fileName, string content)
        {
            File.WriteAllText(Path.Combine(dir, fileName), content);
        }

        protected void mkdir(string dir)
        {
            Directory.CreateDirectory(dir);
        }

#if false
        protected void writeTestFile(string parserName,
                                     string lexerName,
                                     string parserStartRuleName,
                                     bool debug)
        {
            ST outputFileST = new ST(
                "import org.antlr.v4.runtime.*;\n" +
                "import org.antlr.v4.runtime.tree.*;\n" +
                "\n" +
                "public class Test {\n" +
                "    public static void main(string[] args) throws Exception {\n" +
                "        CharStream input = new ANTLRFileStream(args[0]);\n" +
                "        <lexerName> lex = new <lexerName>(input);\n" +
                "        CommonTokenStream tokens = new CommonTokenStream(lex);\n" +
                "        <createParser>\n" +
                "		 parser.setBuildParseTree(true);\n" +
                "		 parser.getInterpreter().reportAmbiguities = true;\n" +
                "        ParserRuleContext tree = parser.<parserStartRuleName>();\n" +
                "        ParseTreeWalker.DEFAULT.walk(new TreeShapeListener(), tree);\n" +
                "    }\n" +
                "\n" +
                "	static class TreeShapeListener implements ParseTreeListener {\n" +
                "		@Override public void visitTerminal(TerminalNode node) { }\n" +
                "		@Override public void visitErrorNode(ErrorNode node) { }\n" +
                "		@Override public void exitEveryRule(ParserRuleContext ctx) { }\n" +
                "\n" +
                "		@Override\n" +
                "		public void enterEveryRule(ParserRuleContext ctx) {\n" +
                "			for (int i = 0; i \\< ctx.getChildCount(); i++) {\n" +
                "				ParseTree parent = ctx.getChild(i).getParent();\n" +
                "				if (!(parent is RuleNode) || ((RuleNode)parent).getRuleContext() != ctx) {\n" +
                "					throw new IllegalStateException(\"Invalid parse tree shape detected.\");\n" +
                "				}\n" +
                "			}\n" +
                "		}\n" +
                "	}\n" +
                "}"
                );
            ST createParserST = new ST("        <parserName> parser = new <parserName>(tokens);\n");
            if (debug)
            {
                createParserST =
                    new ST(
                    "        <parserName> parser = new <parserName>(tokens);\n" +
                    "        parser.addErrorListener(new DiagnosticErrorListener());\n");
            }
            outputFileST.add("createParser", createParserST);
            outputFileST.add("parserName", parserName);
            outputFileST.add("lexerName", lexerName);
            outputFileST.add("parserStartRuleName", parserStartRuleName);
            writeFile(tmpdir, "Test.java", outputFileST.render());
        }

        protected void writeLexerTestFile(string lexerName, bool showDFA)
        {
            ST outputFileST = new ST(
                "import org.antlr.v4.runtime.*;\n" +
                "\n" +
                "public class Test {\n" +
                "    public static void main(string[] args) throws Exception {\n" +
                "        CharStream input = new ANTLRFileStream(args[0]);\n" +
                "        <lexerName> lex = new <lexerName>(input);\n" +
                "        CommonTokenStream tokens = new CommonTokenStream(lex);\n" +
                "        tokens.fill();\n" +
                "        for (Object t : tokens.getTokens()) Console.WriteLine(t);\n" +
                (showDFA ? "System.out.print(lex.getInterpreter().getDFA(Lexer.DEFAULT_MODE).toLexerstring());\n" : "") +
                "    }\n" +
                "}"
                );

            outputFileST.add("lexerName", lexerName);
            writeFile(tmpdir, "Test.java", outputFileST.render());
        }

        public void writeRecognizerAndCompile(string parserName, string lexerName,
                                              string parserStartRuleName,
                                              bool debug)
        {
            if (parserName == null)
            {
                writeLexerTestFile(lexerName, debug);
            }
            else
            {
                writeTestFile(parserName,
                              lexerName,
                              parserStartRuleName,
                              debug);
            }

            compile("Test.java");
        }


        protected void eraseFiles(string filesEndingWith)
        {
            File tmpdirF = new File(tmpdir);
            string[] files = tmpdirF.list();
            for (int i = 0; files != null && i < files.length; i++)
            {
                if (files[i].endsWith(filesEndingWith))
                {
                    new File(tmpdir + "/" + files[i]).delete();
                }
            }
        }

        protected void eraseFiles()
        {
            if (tmpdir == null)
            {
                return;
            }

            File tmpdirF = new File(tmpdir);
            string[] files = tmpdirF.list();
            for (int i = 0; files != null && i < files.length; i++)
            {
                new File(tmpdir + "/" + files[i]).delete();
            }
        }
#endif

        protected virtual void eraseTempDir()
        {
            if (!Path.GetTempPath().Equals(Path.GetDirectoryName(tmpdir) + Path.DirectorySeparatorChar))
            {
                throw new InvalidOperationException();
            }

            Directory.Delete(tmpdir, true);
        }

#if false
        public string getFirstLineOfException()
        {
            if (this.stderrDuringParse == null)
            {
                return null;
            }
            string[] lines = this.stderrDuringParse.split("\n");
            string prefix = "Exception in thread \"main\" ";
            return lines[0].substring(prefix.length(), lines[0].length());
        }

        /**
         * When looking at a result set that consists of a Map/HashTable
         * we cannot rely on the output order, as the hashing algorithm or other aspects
         * of the implementation may be different on differnt JDKs or platforms. Hence
         * we take the Map, convert the keys to a IList, sort them and stringify the Map, which is a
         * bit of a hack, but guarantees that we get the same order on all systems. We assume that
         * the keys are strings.
         *
         * @param m The Map that contains keys we wish to return in sorted order
         * @return A string that represents all the keys in sorted order.
         */
        public string sortMapTostring<K, V>(Map<K, V> m)
        {
            // Pass in crap, and get nothing back
            //
            if (m == null)
            {
                return null;
            }

            Console.WriteLine("Map tostring looks like: " + m.tostring());

            // Sort the keys in the Map
            //
            TreeMap<K, V> nset = new TreeMap<K, V>(m);

            Console.WriteLine("Tree map looks like: " + nset.tostring());
            return nset.tostring();
        }

        public IList<string> realElements(IList<string> elements)
        {
            return elements.subList(Token.MIN_USER_TOKEN_TYPE, elements.Count);
        }

        public void assertNotNullOrEmpty(string message, string text)
        {
            assertNotNull(message, text);
            assertFalse(message, text.isEmpty());
        }

        public void assertNotNullOrEmpty(string text)
        {
            assertNotNull(text);
            assertFalse(text.isEmpty());
        }

        public class IntTokenStream : TokenStream
        {
            IntegerList types;
            int p = 0;
            public IntTokenStream(IntegerList types)
            {
                this.types = types;
            }

            public override void consume()
            {
                p++;
            }

            public override int LA(int i)
            {
                return LT(i).getType();
            }

            public override int mark()
            {
                return index();
            }

            public override int index()
            {
                return p;
            }

            public override void release(int marker)
            {
                seek(marker);
            }

            public override void seek(int index)
            {
                p = index;
            }

            public override int size()
            {
                return types.Count;
            }

            public override string getSourceName()
            {
                return null;
            }

            public override Token LT(int i)
            {
                CommonToken t;
                int rawIndex = p + i - 1;
                if (rawIndex >= types.Count)
                    t = new CommonToken(Token.EOF);
                else
                    t = new CommonToken(types.get(rawIndex));
                t.setTokenIndex(rawIndex);
                return t;
            }

            public override Token get(int i)
            {
                return new org.antlr.v4.runtime.CommonToken(types.get(i));
            }

            public override TokenSource getTokenSource()
            {
                return null;
            }

            [return: NotNull]
            public override string getText()
            {
                throw new UnsupportedOperationException("can't give strings");
            }

            [return: NotNull]
            public override string getText(Interval interval)
            {
                throw new UnsupportedOperationException("can't give strings");
            }

            [return: NotNull]
            public override string getText(RuleContext ctx)
            {
                throw new UnsupportedOperationException("can't give strings");
            }

            [return: NotNull]
            public override string getText(Token start, Token stop)
            {
                throw new UnsupportedOperationException("can't give strings");
            }
        }

        /** Sort a list */
        public IList<T> sort<T>(IList<T> data)
        {
            IList<T> dup = new List<T>();
            dup.addAll(data);
            Collections.sort(dup);
            return dup;
        }

        /** Return map sorted by key */
        public LinkedHashMap<K, V> sort<K, V>(Map<K, V> data)
        {
            LinkedHashMap<K, V> dup = new LinkedHashMap<K, V>();
            IList<K> keys = new List<K>();
            keys.addAll(data.keySet());
            Collections.sort(keys);
            foreach (K k in keys)
            {
                dup.put(k, data.get(k));
            }
            return dup;
        }
#endif
    }
}
