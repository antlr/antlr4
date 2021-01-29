/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
    public interface IWritableToken : IToken
    {
        new string Text
        {
            set;
        }

        new int Type
        {
            set;
        }

        new int Line
        {
            set;
        }

        new int Column
        {
            set;
        }

        new int Channel
        {
            set;
        }

        new int TokenIndex
        {
            set;
        }
    }
}
