namespace Antlr4.Runtime
{
    using Antlr4.Runtime.Atn;

    public interface IRecognizer
    {
        string[] TokenNames
        {
            get;
        }

        string[] RuleNames
        {
            get;
        }

        string GrammarFileName
        {
            get;
        }

        ATN Atn
        {
            get;
        }

        int State
        {
            get;
        }

        IIntStream InputStream
        {
            get;
        }
    }
}
