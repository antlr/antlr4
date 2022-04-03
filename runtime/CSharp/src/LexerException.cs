namespace Antlr4.Runtime
{
    public abstract class LexerException : RecognitionException
    {
        public readonly int StartIndex;
        public readonly int Length;

        protected LexerException(Lexer lexer, ICharStream input, int startIndex, int length) : base(lexer, input)
        {
            StartIndex = startIndex;
            Length = length;
        }

        public abstract string GetErrorMessage(string input);
    }
}