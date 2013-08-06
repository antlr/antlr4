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
#if NET_4_0
    using RegistryView = Microsoft.Win32.RegistryView;
#else
    using Registry = Microsoft.Win32.Registry;
#endif
    using StringBuilder = System.Text.StringBuilder;

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

        public string JavaVendor
        {
            get;
            set;
        }

        public string JavaInstallation
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

#if NET_4_0
        private string JavaHome
        {
            get
            {
                string javaHome;
                if (TryGetJavaHome(RegistryView.Default, JavaVendor, JavaInstallation, out javaHome))
                    return javaHome;

                if (TryGetJavaHome(RegistryView.Registry64, JavaVendor, JavaInstallation, out javaHome))
                    return javaHome;

                if (TryGetJavaHome(RegistryView.Registry32, JavaVendor, JavaInstallation, out javaHome))
                    return javaHome;

                throw new NotSupportedException("Could not locate a Java installation.");
            }
        }

        private static bool TryGetJavaHome(RegistryView registryView, string vendor, string installation, out string javaHome)
        {
            javaHome = null;

            string javaKeyName = "SOFTWARE\\" + vendor + "\\" + installation;
            using (var baseKey = RegistryKey.OpenBaseKey(RegistryHive.LocalMachine, registryView))
            {
                using (RegistryKey javaKey = baseKey.OpenSubKey(javaKeyName))
                {
                    if (javaKey == null)
                        return false;

                    object currentVersion = javaKey.GetValue("CurrentVersion");
                    if (currentVersion == null)
                        return false;

                    using (var homeKey = javaKey.OpenSubKey(currentVersion.ToString()))
                    {
                        if (homeKey == null || homeKey.GetValue("JavaHome") == null)
                            return false;

                        javaHome = homeKey.GetValue("JavaHome").ToString();
                        return !string.IsNullOrEmpty(javaHome);
                    }
                }
            }
        }
#else
        private string JavaHome
        {
            get
            {
                string javaHome;
                if (TryGetJavaHome(Registry.LocalMachine, JavaVendor, JavaInstallation, out javaHome))
                    return javaHome;

                throw new NotSupportedException("Could not locate a Java installation.");
            }
        }

        private static bool TryGetJavaHome(RegistryKey baseKey, string vendor, string installation, out string javaHome)
        {
            javaHome = null;

            string javaKeyName = "SOFTWARE\\" + vendor + "\\" + installation;
            using (RegistryKey javaKey = baseKey.OpenSubKey(javaKeyName))
            {
                if (javaKey == null)
                    return false;

                object currentVersion = javaKey.GetValue("CurrentVersion");
                if (currentVersion == null)
                    return false;

                using (var homeKey = javaKey.OpenSubKey(currentVersion.ToString()))
                {
                    if (homeKey == null || homeKey.GetValue("JavaHome") == null)
                        return false;

                    javaHome = homeKey.GetValue("JavaHome").ToString();
                    return !string.IsNullOrEmpty(javaHome);
                }
            }
        }
#endif

        public bool Execute()
        {
            try
            {
                string javaHome = JavaHome;
                string java = Path.Combine(Path.Combine(javaHome, "bin"), "java.exe");

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

                ProcessStartInfo startInfo = new ProcessStartInfo(java, JoinArguments(arguments))
                {
                    UseShellExecute = false,
                    CreateNoWindow = true,
                    RedirectStandardInput = true,
                    RedirectStandardOutput = true,
                    RedirectStandardError = true,
                };

                this.BuildMessages.Add(new BuildMessage(TraceLevel.Info, "Executing command: \"" + startInfo.FileName + "\" " + startInfo.Arguments, "", 0, 0));

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

        private static string JoinArguments(IEnumerable<string> arguments)
        {
            if (arguments == null)
                throw new ArgumentNullException("arguments");

            StringBuilder builder = new StringBuilder();
            foreach (string argument in arguments)
            {
                if (builder.Length > 0)
                    builder.Append(' ');

                if (argument.IndexOfAny(new[] { '"', ' ' }) < 0)
                {
                    builder.Append(argument);
                    continue;
                }

                // escape a backslash appearing before a quote
                string arg = argument.Replace("\\\"", "\\\\\"");
                // escape double quotes
                arg = arg.Replace("\"", "\\\"");

                // wrap the argument in outer quotes
                builder.Append('"').Append(arg).Append('"');
            }

            return builder.ToString();
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

                _buildMessages.Add(new BuildMessage(ex.Message));
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

                _buildMessages.Add(new BuildMessage(ex.Message));
            }
        }
    }
}
