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

#if !PORTABLE

using Antlr4.Runtime.Sharpen;
using Encoding = System.Text.Encoding;
using File = System.IO.File;

namespace Antlr4.Runtime
{
#if COMPACT
    using StreamReader = System.IO.StreamReader;
#endif

    /// <summary>
    /// This is an
    /// <see cref="AntlrInputStream"/>
    /// that is loaded from a file all at once
    /// when you construct the object.
    /// </summary>
    public class AntlrFileStream : AntlrInputStream
    {
        protected internal string fileName;

        /// <exception cref="System.IO.IOException"/>
        public AntlrFileStream(string fileName)
            : this(fileName, null)
        {
        }

        /// <exception cref="System.IO.IOException"/>
        public AntlrFileStream(string fileName, Encoding encoding)
        {
            this.fileName = fileName;
            Load(fileName, encoding);
        }

        /// <exception cref="System.IO.IOException"/>
        public virtual void Load(string fileName, Encoding encoding)
        {
            if (fileName == null)
            {
                return;
            }

            string text;
#if !COMPACT
            if (encoding != null)
                text = File.ReadAllText(fileName, encoding);
            else
                text = File.ReadAllText(fileName);
#else
            if (encoding != null)
                text = ReadAllText(fileName, encoding);
            else
                text = ReadAllText(fileName);
#endif

            data = text.ToCharArray();
            n = data.Length;
        }

        public override string SourceName
        {
            get
            {
                return fileName;
            }
        }

#if COMPACT
        private static string ReadAllText(string path)
        {
            using (var reader = new StreamReader(path))
            {
                return reader.ReadToEnd();
            }
        }

        private static string ReadAllText(string path, Encoding encoding)
        {
            using (var reader = new StreamReader(path, encoding ?? Encoding.Default))
            {
                return reader.ReadToEnd();
            }
        }
#endif
    }
}

#endif
