using System;
using Antlr4.Runtime.Misc;

namespace Antlr4.Runtime
{
    public class CaseChangingCharStream : ICharStream
    {
        private ICharStream stream;
        private bool upper;

        public CaseChangingCharStream(ICharStream stream, bool upper)
        {
            this.stream = stream;
            this.upper = upper;
        }

        public int Index => stream.Index;

        public int Size => stream.Size;

        public string SourceName => stream.SourceName;

        public void Consume() => stream.Consume();

        [return: NotNull]
        public string GetText(Interval interval) => stream.GetText(interval);

        public int LA(int i)
        {
            int c = stream.LA(i);

            if (c <= 0) return c;

            var o = Convert.ToChar(c);

            if (upper) return Convert.ToInt32(char.ToUpper(o));

            return Convert.ToInt32(char.ToLower(o));
        }

        public int Mark() => stream.Mark();

        public void Release(int marker) => stream.Release(marker);

        public void Seek(int index) => stream.Seek(index);
    }
}
