/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
using System;
using Antlr4.Runtime.Sharpen;

#if COMPACT
using OperationCanceledException = System.Exception;
#endif

namespace Antlr4.Runtime.Misc
{
    /// <summary>This exception is thrown to cancel a parsing operation.</summary>
    /// <remarks>
    /// This exception is thrown to cancel a parsing operation. This exception does
    /// not extend
    /// <see cref="Antlr4.Runtime.RecognitionException"/>
    /// , allowing it to bypass the standard
    /// error recovery mechanisms.
    /// <see cref="Antlr4.Runtime.BailErrorStrategy"/>
    /// throws this exception in
    /// response to a parse error.
    /// </remarks>
    /// <author>Sam Harwell</author>
    [System.Serializable]
    public class ParseCanceledException : OperationCanceledException
    {
        public ParseCanceledException()
        {
        }

        public ParseCanceledException(string message)
            : base(message)
        {
        }

        public ParseCanceledException(Exception cause)
            : base("The parse operation was cancelled.", cause)
        {
        }

        public ParseCanceledException(string message, Exception cause)
            : base(message, cause)
        {
        }
    }
}
