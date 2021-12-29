namespace Antlr4.Runtime
{
    public class LexerEmptyModeStackException : LexerException
    {
        public LexerEmptyModeStackException(Lexer lexer, ICharStream input, int startIndex, int length)
            : base(lexer, input, startIndex, length)
        {
        }

        public override string GetErrorMessage(string input)
        {
            return "Unable to pop mode because mode stack is empty at: '" + input + "'";
        }
    }
}