/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

namespace Antlr4.Runtime
{
    /// <summary>
    /// This signifies any kind of mismatched input exceptions such as
    /// when the current input does not match the expected token.
    /// </summary>
    /// <remarks>
    /// This signifies any kind of mismatched input exceptions such as
    /// when the current input does not match the expected token.
    /// </remarks>
    [System.Serializable]
    public class InputMismatchException : RecognitionException
    {
        public InputMismatchException(Parser recognizer)
			: base(recognizer, ((ITokenStream)recognizer.InputStream), recognizer.RuleContext)
        {
            OffendingToken = recognizer.CurrentToken;
        }
    }
}
