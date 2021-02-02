/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
namespace Antlr4.Runtime
{
    using System;
    using Antlr4.Runtime.Atn;

    public interface IRecognizer
    {
        IVocabulary Vocabulary
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
