using System;
using System.IO;

namespace Antlr4.Runtime
{
    /// <summary>
    /// Mimics needed Windows.Console API.
    /// </summary>
    public static class Consoles
    {
        /// <summary>
        /// Retrieves a writer for std output.
        /// </summary>
        public static TextWriter Out
        {
            get
            {
#if WINDOWS_UWP
                return UwpConsole.Out;
#else
                return Console.Out;
#endif
            }
        }

        /// <summary>
        /// Retrieves a writer for error logging.
        /// </summary>
        public static TextWriter Error
        {
            get
            {
#if WINDOWS_UWP
                return UwpConsole.Error;
#else
                return Console.Error;
#endif
            }
        }
    }
}