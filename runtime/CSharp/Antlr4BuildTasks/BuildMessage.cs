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
    using System.Diagnostics;
    using System.Text.RegularExpressions;

    [Serializable]
    internal struct BuildMessage
    {
        private static readonly Regex BuildMessageFormat = new Regex(@"^\s*(?<FILE>.*)\((?<LINE>[0-9]+),(?<COLUMN>[0-9]+)\) : (?<SEVERITY>[a-z]+)\s*(?<CODE>[0-9]+) : (?<MESSAGE>.*)$", RegexOptions.Compiled);

        public BuildMessage(string message)
            : this(TraceLevel.Error, message, null, 0, 0)
        {
            Match match = BuildMessageFormat.Match(message);
            if (match.Success)
            {
                FileName = match.Groups["FILE"].Value;
                LineNumber = int.Parse(match.Groups["LINE"].Value);
                ColumnNumber = int.Parse(match.Groups["COLUMN"].Value);

                switch (match.Groups["SEVERITY"].Value)
                {
                case "warning":
                    Severity = TraceLevel.Warning;
                    break;
                case "error":
                    Severity = TraceLevel.Error;
                    break;
                default:
                    Severity = TraceLevel.Info;
                    break;
                }

                int code = int.Parse(match.Groups["CODE"].Value);
                Message = string.Format("AC{0:0000}: {1}", code, match.Groups["MESSAGE"].Value);
            }
        }

        public BuildMessage(TraceLevel severity, string message, string fileName, int lineNumber, int columnNumber)
            : this()
        {
            Severity = severity;
            Message = message;
            FileName = fileName;
            LineNumber = lineNumber;
            ColumnNumber = columnNumber;
        }

        public TraceLevel Severity
        {
            get;
            set;
        }

        public string Message
        {
            get;
            set;
        }

        public string FileName
        {
            get;
            set;
        }

        public int LineNumber
        {
            get;
            set;
        }

        public int ColumnNumber
        {
            get;
            set;
        }
    }
}
