/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime;
using Antlr4.Runtime.Sharpen;
using Antlr4.Runtime.Tree.Xpath;
using System.IO;

namespace Antlr4.Runtime.Tree.Xpath
{
    public class XPathLexerErrorListener : IAntlrErrorListener<int>
    {
        public virtual void SyntaxError(TextWriter output, IRecognizer recognizer, int offendingSymbol, int line, int charPositionInLine, string msg, RecognitionException e)
        {
        }
    }
}
