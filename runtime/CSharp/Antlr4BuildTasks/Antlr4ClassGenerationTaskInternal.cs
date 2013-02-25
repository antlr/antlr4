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
    using System.Text;

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

        public string AntlrToolPath
        {
            get;
            set;
        }

        public string GeneratedSourceExtension
        {
            get;
            set;
        }

        public string TargetLanguage
        {
            get;
            set;
        }

        public string OutputPath
        {
            get;
            set;
        }

        public string RootNamespace
        {
            get;
            set;
        }

        public string[] LanguageSourceExtensions
        {
            get;
            set;
        }

        public bool DebugGrammar
        {
            get;
            set;
        }

        public bool ProfileGrammar
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

        public bool Execute()
        {
            try
            {
                Assembly antlrAssembly = Assembly.LoadFrom(AntlrToolPath);
                Type antlrToolType = antlrAssembly.GetType("Antlr3.AntlrTool");
                Type errorManagerType = antlrAssembly.GetType("Antlr3.Tool.ErrorManager");
                object tool = Activator.CreateInstance(antlrAssembly.GetType("Antlr3.AntlrTool"), new object[] { Path.GetDirectoryName(AntlrToolPath) });

                Action process = (Action)Delegate.CreateDelegate(typeof(Action), tool, antlrToolType.GetMethod("Process"));
                Action<string[]> ProcessArgs = (Action<string[]>)Delegate.CreateDelegate(typeof(Action<string[]>), tool, antlrToolType.GetMethod("ProcessArgs"));
                Func<IList<string>> GetGeneratedFiles = (Func<IList<string>>)Delegate.CreateDelegate(typeof(Func<IList<string>>), tool, antlrToolType.GetProperty("GeneratedFiles").GetGetMethod());

                Func<int> GetNumErrors = (Func<int>)Delegate.CreateDelegate(typeof(Func<int>), errorManagerType.GetMethod("GetNumErrors"));
                Action<TraceListener> SetTraceListener = (Action<TraceListener>)Delegate.CreateDelegate(typeof(Action<TraceListener>), errorManagerType.GetProperty("ExternalListener").GetSetMethod());

                TimeSpan conversionTimeout = TimeSpan.FromSeconds(10);

                List<string> args =
                    new List<string>()
                {
                    "-Xconversiontimeout", ((int)conversionTimeout.TotalMilliseconds).ToString(),
                    "-fo", OutputPath,
                    "-message-format", "vs2005"
                };

                if (DebugGrammar)
                    args.Add("-debug");

                if (ProfileGrammar)
                    args.Add("-profile");

                if (!string.IsNullOrEmpty(TargetLanguage))
                {
                    args.Add("-language");
                    args.Add(TargetLanguage);
                }

                args.AddRange(SourceCodeFiles);

                using (LoggingTraceListener traceListener = new LoggingTraceListener(_buildMessages))
                {
                    SetTraceListener(traceListener);
                    ProcessArgs(args.ToArray());
                    process();
                }

                _generatedCodeFiles.AddRange(GetGeneratedFiles().Where(file => LanguageSourceExtensions.Contains(Path.GetExtension(file), StringComparer.OrdinalIgnoreCase)));

                int errorCount = GetNumErrors();
                return errorCount == 0;
            }
            catch (Exception e)
            {
                if (e is TargetInvocationException && e.InnerException != null)
                    e = e.InnerException;

                _buildMessages.Add(new BuildMessage(e.Message));
                throw;
            }
        }

        private class LoggingTraceListener : TraceListener
        {
            private readonly ICollection<BuildMessage> _buildMessages;
            private StringBuilder _currentLine;

            public LoggingTraceListener(IList<BuildMessage> buildMessages)
            {
                _buildMessages = buildMessages;
                _currentLine = new StringBuilder();
            }

            protected override void Dispose(bool disposing)
            {
                if (disposing)
                    WriteLine(string.Empty);

                base.Dispose(disposing);
            }

            public override void Write(string message)
            {
                _currentLine.Append(message);
            }

            public override void WriteLine(string message)
            {
                if (_currentLine.Length > 0)
                {
                    _buildMessages.Add(new BuildMessage(_currentLine.ToString()));
                    _currentLine.Length = 0;
                }

                if (!string.IsNullOrEmpty(message))
                {
                    Write(message);
                    WriteLine(null);
                }
            }
        }
    }
}
