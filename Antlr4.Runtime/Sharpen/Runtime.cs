namespace Sharpen
{
    using System;

    internal static class Runtime
    {
        public static string Substring(string str, int beginOffset, int endOffset)
        {
            if (str == null)
                throw new ArgumentNullException("str");

            return str.Substring(beginOffset, endOffset - beginOffset);
        }
    }
}
