// Template generated code from Antlr4BuildTasks.dotnet-antlr v 2.2

using Antlr4.Runtime;
using Antlr4.Runtime.Misc;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;

public class ErrorListener<S> : ConsoleErrorListener<S>
{
    public bool had_error;

    public override void SyntaxError(TextWriter output, IRecognizer recognizer, S offendingSymbol, int line,
        int col, string msg, RecognitionException e)
    {
        had_error = true;
        base.SyntaxError(output, recognizer, offendingSymbol, line, col, msg, e);
    }
}
