/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.IO;
using System.Text;
using Antlr4.Runtime;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
    /// <summary>Utility class to create <see cref="ICharStream"/>s from various sources of
    /// string data.
    ///
    /// The methods in this utility class support the full range of
    /// Unicode code points up to U+10FFFF, unlike <see cref="AntlrInputStream"/>,
    /// which is limited to 16-bit Unicode code units up to U+FFFF.
    /// </summary>
    public static class CharStreams
    {
        /// <summary>Creates an <see cref="ICharStream"/> given a path to a UTF-8
        /// encoded file on disk.
        ///
        /// Reads the entire contents of the file into the result before returning.
        /// </summary>
        public static ICharStream fromPath(string path)
        {
            return fromPath(path, Encoding.UTF8);
        }

        /// <summary>Creates an <see cref="ICharStream"/> given a path to a
        /// file on disk and the encoding of the bytes contained in the file.
        ///
        /// Reads the entire contents of the file into the result before returning.
        /// </summary>
        public static ICharStream fromPath(string path, Encoding encoding)
        {
            var pathContents = File.ReadAllText(path, encoding);
            var result = new CodePointCharStream(pathContents);
            result.name = path;
            return result;
        }

        /// <summary>Creates an <see cref="ICharStream"/> given an opened
        /// <see cref="TextReader"/>.
        ///
        /// Reads the entire contents of the TextReader then closes the reader before returning.
        /// </summary>
        public static ICharStream fromTextReader(TextReader textReader)
        {
            try {
                var textReaderContents = textReader.ReadToEnd();
                return new CodePointCharStream(textReaderContents);
            } finally {
                textReader.Dispose();
            }
        }

        /// <summary>Creates an <see cref="ICharStream"/> given an opened
        /// <see cref="Stream"/> from which UTF-8 encoded bytes can be read.
        ///
        /// Reads the entire contents of the stream into the result then
        /// closes the stream before returning.
        /// </summary>
        public static ICharStream fromStream(Stream stream)
        {
            return fromStream(stream, Encoding.UTF8);
        }

        /// <summary>Creates an <see cref="ICharStream"/> given an opened
        /// <see cref="Stream"/> as well as the encoding of the bytes
        /// to be read from the stream.
        ///
        /// Reads the entire contents of the stream into the result then
        /// closes the stream before returning.
        /// </summary>
        public static ICharStream fromStream(Stream stream, Encoding encoding)
        {
            using (StreamReader sr = new StreamReader(stream, encoding, false)) {
                return fromTextReader(sr);
            }
        }

        /// <summary>Creates an <see cref="ICharStream"/> given a <see cref="string"/>.
        /// </summary>
        public static ICharStream fromString(string s)
        {
            return new CodePointCharStream(s);
        }
    }
}
