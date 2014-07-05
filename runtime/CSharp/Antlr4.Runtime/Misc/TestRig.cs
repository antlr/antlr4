/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
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
using System;
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Sharpen;
using Antlr4.Runtime.Sharpen.Reflect;

namespace Antlr4.Runtime.Misc
{
    /// <summary>
    /// Run a lexer/parser combo, optionally printing tree string or generating
    /// postscript file.
    /// </summary>
    /// <remarks>
    /// Run a lexer/parser combo, optionally printing tree string or generating
    /// postscript file. Optionally taking input file.
    /// $ java org.antlr.v4.runtime.misc.TestRig GrammarName startRuleName
    /// [-tree]
    /// [-tokens] [-gui] [-ps file.ps]
    /// [-trace]
    /// [-diagnostics]
    /// [-SLL]
    /// [input-filename(s)]
    /// </remarks>
    public class TestRig
    {
        public const string LexerStartRuleName = "tokens";

        protected internal string grammarName;

        protected internal string startRuleName;

        protected internal readonly IList<string> inputFiles = new List<string>();

        protected internal bool printTree = false;

        protected internal bool gui = false;

        protected internal string psFile = null;

        protected internal bool showTokens = false;

        protected internal bool trace = false;

        protected internal bool diagnostics = false;

        protected internal string encoding = null;

        protected internal bool Sll = false;

        /// <exception cref="System.Exception"/>
        public TestRig(string[] args)
        {
            if (args.Length < 2)
            {
                System.Console.Error.WriteLine("java org.antlr.v4.runtime.misc.TestRig GrammarName startRuleName\n" + "  [-tokens] [-tree] [-gui] [-ps file.ps] [-encoding encodingname]\n" + "  [-trace] [-diagnostics] [-SLL]\n" + "  [input-filename(s)]");
                System.Console.Error.WriteLine("Use startRuleName='tokens' if GrammarName is a lexer grammar.");
                System.Console.Error.WriteLine("Omitting input-filename makes rig read from stdin.");
                return;
            }
            int i = 0;
            grammarName = args[i];
            i++;
            startRuleName = args[i];
            i++;
            while (i < args.Length)
            {
                string arg = args[i];
                i++;
                if (arg[0] != '-')
                {
                    // input file name
                    inputFiles.Add(arg);
                    continue;
                }
                if (arg.Equals("-tree"))
                {
                    printTree = true;
                }
                if (arg.Equals("-gui"))
                {
                    gui = true;
                }
                if (arg.Equals("-tokens"))
                {
                    showTokens = true;
                }
                else
                {
                    if (arg.Equals("-trace"))
                    {
                        trace = true;
                    }
                    else
                    {
                        if (arg.Equals("-SLL"))
                        {
                            Sll = true;
                        }
                        else
                        {
                            if (arg.Equals("-diagnostics"))
                            {
                                diagnostics = true;
                            }
                            else
                            {
                                if (arg.Equals("-encoding"))
                                {
                                    if (i >= args.Length)
                                    {
                                        System.Console.Error.WriteLine("missing encoding on -encoding");
                                        return;
                                    }
                                    encoding = args[i];
                                    i++;
                                }
                                else
                                {
                                    if (arg.Equals("-ps"))
                                    {
                                        if (i >= args.Length)
                                        {
                                            System.Console.Error.WriteLine("missing filename on -ps");
                                            return;
                                        }
                                        psFile = args[i];
                                        i++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        /// <exception cref="System.Exception"/>
        public static void Main(string[] args)
        {
            Antlr4.Runtime.Misc.TestRig testRig = new Antlr4.Runtime.Misc.TestRig(args);
            if (args.Length >= 2)
            {
                testRig.Process();
            }
        }

        /// <exception cref="System.Exception"/>
        public virtual void Process()
        {
            //		System.out.println("exec "+grammarName+"."+startRuleName);
            string lexerName = grammarName + "Lexer";
            ClassLoader cl = Antlr4.Runtime.Sharpen.Thread.CurrentThread().GetContextClassLoader();
            Type lexerClass = null;
            try
            {
                lexerClass = cl.LoadClass(lexerName).AsSubclass<Lexer>();
            }
            catch (TypeLoadException)
            {
                // might be pure lexer grammar; no Lexer suffix then
                lexerName = grammarName;
                try
                {
                    lexerClass = cl.LoadClass(lexerName).AsSubclass<Lexer>();
                }
                catch (TypeLoadException)
                {
                    System.Console.Error.WriteLine("Can't load " + lexerName + " as lexer or parser");
                    return;
                }
            }
            Constructor<Lexer> lexerCtor = lexerClass.GetConstructor(typeof(ICharStream));
            Lexer lexer = lexerCtor.NewInstance((ICharStream)null);
            Type parserClass = null;
            Parser parser = null;
            if (!startRuleName.Equals(LexerStartRuleName))
            {
                string parserName = grammarName + "Parser";
                parserClass = cl.LoadClass(parserName).AsSubclass<Parser>();
                if (parserClass == null)
                {
                    System.Console.Error.WriteLine("Can't load " + parserName);
                    return;
                }
                Constructor<Parser> parserCtor = parserClass.GetConstructor(typeof(ITokenStream));
                parser = parserCtor.NewInstance((ITokenStream)null);
            }
            if (inputFiles.IsEmpty())
            {
                Stream @is = Sharpen.Runtime.@in;
                TextReader r;
                if (encoding != null)
                {
                    r = new StreamReader(@is, encoding);
                }
                else
                {
                    r = new StreamReader(@is);
                }
                Process(lexer, parserClass, parser, @is, r);
                return;
            }
            foreach (string inputFile in inputFiles)
            {
                Stream @is = Sharpen.Runtime.@in;
                if (inputFile != null)
                {
                    @is = new FileInputStream(inputFile);
                }
                TextReader r;
                if (encoding != null)
                {
                    r = new StreamReader(@is, encoding);
                }
                else
                {
                    r = new StreamReader(@is);
                }
                if (inputFiles.Count > 1)
                {
                    System.Console.Error.WriteLine(inputFile);
                }
                Process(lexer, parserClass, parser, @is, r);
            }
        }

        /// <exception cref="System.IO.IOException"/>
        /// <exception cref="System.MemberAccessException"/>
        /// <exception cref="System.Reflection.TargetInvocationException"/>
        /// <exception cref="Javax.Print.PrintException"/>
        protected internal virtual void Process<_T0>(Lexer lexer, Type<_T0> parserClass, Parser parser, Stream @is, TextReader r)
            where _T0 : Parser
        {
            try
            {
                AntlrInputStream input = new AntlrInputStream(r);
                lexer.SetInputStream(input);
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                tokens.Fill();
                if (showTokens)
                {
                    foreach (object tok in tokens.GetTokens())
                    {
                        System.Console.Out.WriteLine(tok);
                    }
                }
                if (startRuleName.Equals(LexerStartRuleName))
                {
                    return;
                }
                if (diagnostics)
                {
                    parser.AddErrorListener(new DiagnosticErrorListener());
                    parser.Interpreter.PredictionMode = PredictionMode.LlExactAmbigDetection;
                }
                if (printTree || gui || psFile != null)
                {
                    parser.BuildParseTree = true;
                }
                if (Sll)
                {
                    // overrides diagnostics
                    parser.Interpreter.PredictionMode = PredictionMode.Sll;
                }
                parser.SetInputStream(tokens);
                parser.Trace = trace;
                try
                {
                    MethodInfo startRule = parserClass.GetMethod(startRuleName, (Type[])null);
                    ParserRuleContext tree = (ParserRuleContext)startRule.Invoke(parser, (object[])null);
                    if (printTree)
                    {
                        System.Console.Out.WriteLine(tree.ToStringTree(parser));
                    }
                    if (gui)
                    {
                        tree.Inspect(parser);
                    }
                    if (psFile != null)
                    {
                        tree.Save(parser, psFile);
                    }
                }
                catch (NoSuchMethodException)
                {
                    // Generate postscript
                    System.Console.Error.WriteLine("No method for rule " + startRuleName + " or it has arguments");
                }
            }
            finally
            {
                if (r != null)
                {
                    r.Close();
                }
                if (@is != null)
                {
                    @is.Close();
                }
            }
        }
    }
}
