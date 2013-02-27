/*
 * [The "BSD licence"]
 * Copyright (c) 2013 Sam Harwell
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

namespace Antlr4.Build.Tasks
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics;
    using System.IO;
    using System.Linq;
    using System.Reflection;
    using System.Text.RegularExpressions;
    using RegistryHive = Microsoft.Win32.RegistryHive;
    using RegistryKey = Microsoft.Win32.RegistryKey;
    using RegistryView = Microsoft.Win32.RegistryView;

    internal class AntlrClassGenerationTaskInternal : MarshalByRefObject
    {
        private List<string> _generatedCodeFiles = new List<string>();
        private IList<string> _sourceCodeFiles = new List<string>();
        private List<BuildMessage> _buildMessages = new List<BuildMessage>();

        public IList<string> GeneratedCodeFiles
        {
            get
            {
                return this._generatedCodeFiles;
            }
        }

        public string ToolPath
        {
            get;
            set;
        }

        public string TargetLanguage
        {
            get;
            set;
        }

        public string TargetFrameworkVersion
        {
            get;
            set;
        }

        public string OutputPath
        {
            get;
            set;
        }

        public string TargetNamespace
        {
            get;
            set;
        }

        public string[] LanguageSourceExtensions
        {
            get;
            set;
        }

        public bool GenerateListener
        {
            get;
            set;
        }

        public bool GenerateVisitor
        {
            get;
            set;
        }

        public bool ForceAtn
        {
            get;
            set;
        }

        public bool AbstractGrammar
        {
            get;
            set;
        }

        public IList<string> SourceCodeFiles
        {
            get
            {
                return this._sourceCodeFiles;
            }
            set
            {
                this._sourceCodeFiles = value;
            }
        }

        public IList<BuildMessage> BuildMessages
        {
            get
            {
                return _buildMessages;
            }
        }

        private string JavaHome
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

        public bool Execute()
        {
            try
            {
                string javaHome = JavaHome;
                string java = Path.Combine(javaHome, "bin", "java.exe");

                List<string> arguments = new List<string>();
                arguments.Add("-cp");
                arguments.Add(ToolPath);
                arguments.Add("org.antlr.v4.CSharpTool");

                arguments.Add("-o");
                arguments.Add(OutputPath);

                if (GenerateListener)
                    arguments.Add("-listener");
                else
                    arguments.Add("-no-listener");

                if (GenerateVisitor)
                    arguments.Add("-visitor");
                else
                    arguments.Add("-no-visitor");

                if (ForceAtn)
                    arguments.Add("-force-atn");

                if (AbstractGrammar)
                    arguments.Add("-Dabstract=true");

                if (!string.IsNullOrEmpty(TargetLanguage))
                {
                    string framework = TargetFrameworkVersion;
                    if (string.IsNullOrEmpty(framework))
                        framework = "v2.0";

                    string language;
                    if (TargetLanguage.Equals("CSharp"))
                        language = TargetLanguage + '_' + framework.Replace('.', '_');
                    else
                        language = TargetLanguage;

                    arguments.Add("-Dlanguage=" + language);
                }

                if (!string.IsNullOrEmpty(TargetNamespace))
                {
                    arguments.Add("-package");
                    arguments.Add(TargetNamespace);
                }

                arguments.AddRange(SourceCodeFiles);

                ProcessStartInfo startInfo = new ProcessStartInfo(java, string.Join(" ", arguments))
                {
                    UseShellExecute = false,
                    CreateNoWindow = true,
                    RedirectStandardInput = true,
                    RedirectStandardOutput = true,
                    RedirectStandardError = true,
                };

                Process process = new Process();
                process.StartInfo = startInfo;
                process.ErrorDataReceived += HandleErrorDataReceived;
                process.OutputDataReceived += HandleOutputDataReceived;
                process.Start();
                process.BeginErrorReadLine();
                process.BeginOutputReadLine();
                process.StandardInput.Close();
                process.WaitForExit();

                return process.ExitCode == 0;
                //using (LoggingTraceListener traceListener = new LoggingTraceListener(_buildMessages))
                //{
                //    SetTraceListener(traceListener);
                //    ProcessArgs(args.ToArray());
                //    process();
                //}

                //_generatedCodeFiles.AddRange(GetGeneratedFiles().Where(file => LanguageSourceExtensions.Contains(Path.GetExtension(file), StringComparer.OrdinalIgnoreCase)));

                //int errorCount = GetNumErrors();
                //return errorCount == 0;
            }
            catch (Exception e)
            {
                if (e is TargetInvocationException && e.InnerException != null)
                    e = e.InnerException;

                _buildMessages.Add(new BuildMessage(e.Message));
                throw;
            }
        }

        private static readonly Regex GeneratedFileMessageFormat = new Regex(@"^Generating file '(?<OUTPUT>.*?)' for grammar '(?<GRAMMAR>.*?)'$", RegexOptions.Compiled);

        private void HandleErrorDataReceived(object sender, DataReceivedEventArgs e)
        {
            if (string.IsNullOrEmpty(e.Data))
                return;

            try
            {
                _buildMessages.Add(new BuildMessage(e.Data));
            }
            catch (Exception ex)
            {
                if (Antlr4ClassGenerationTask.IsFatalException(ex))
                    throw;
            }
        }

        private void HandleOutputDataReceived(object sender, DataReceivedEventArgs e)
        {
            if (string.IsNullOrEmpty(e.Data))
                return;

            try
            {
                Match match = GeneratedFileMessageFormat.Match(e.Data);
                if (!match.Success)
                {
                    _buildMessages.Add(new BuildMessage(e.Data));
                    return;
                }

                string fileName = match.Groups["OUTPUT"].Value;
                if (LanguageSourceExtensions.Contains(Path.GetExtension(fileName), StringComparer.OrdinalIgnoreCase))
                    GeneratedCodeFiles.Add(match.Groups["OUTPUT"].Value);
            }
            catch (Exception ex)
            {
                if (Antlr4ClassGenerationTask.IsFatalException(ex))
                    throw;
            }
        }
    }
}
