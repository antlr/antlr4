/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
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
