#if WINDOWS_UWP

using System.Diagnostics;
using System.IO;
using System.Text;

namespace Antlr4.Runtime
{
    /// <summary>
    /// Mimics Console.Out and Console.Err for UWP apps.
    /// </summary>
    public static class UwpConsole
    {
        /// <summary>
        /// TextWriter that forwards to diagnostics, which is the preferred way
        /// of writing to stdout on UWP.
        /// </summary>
        public class UwpOutWriter: TextWriter
        {
            /// <inheritdoc />
            public override Encoding Encoding { get; }

            /// <summary>
            /// Creates a new writer.
            /// </summary>
            public UwpOutWriter()
            {
                Encoding = Encoding.UTF8;
            }

            /// <inheritdoc />
            public override void Write(char value)
            {
                Debug.Write(value);
            }
        }

        /// <summary>
        /// TextWriter that forwards to diagnostics, which is the preferred way
        /// of writing to stderr UWP.
        /// </summary>
        public class UwpErrWriter: TextWriter
        {
            /// <inheritdoc />
            public override Encoding Encoding { get; }

            /// <summary>
            /// Creates a new writer.
            /// </summary>
            public UwpErrWriter()
            {
                Encoding = Encoding.UTF8;
            }

            /// <inheritdoc />
            public override void Write(char value)
            {
                Debug.Fail(value.ToString());
            }
        }

        /// <summary>
        /// Writes to StdOut.
        /// </summary>
        public readonly static TextWriter Out = new UwpOutWriter();

        /// <summary>
        /// Writes to StdErr.
        /// </summary>
        public readonly static TextWriter Error = new UwpErrWriter();
    }
}

#endif