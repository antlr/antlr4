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
using System.Collections.Generic;
using Antlr4.Runtime;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
    /// <summary>
    /// This implementation of
    /// <see cref="IAntlrErrorListener{Symbol}"/>
    /// dispatches all calls to a
    /// collection of delegate listeners. This reduces the effort required to support multiple
    /// listeners.
    /// </summary>
    /// <author>Sam Harwell</author>
    public class ProxyErrorListener<Symbol> : IAntlrErrorListener<Symbol>
    {
        private readonly IEnumerable<IAntlrErrorListener<Symbol>> delegates;

        public ProxyErrorListener(IEnumerable<IAntlrErrorListener<Symbol>> delegates)
        {
            if (delegates == null)
            {
                throw new ArgumentNullException("delegates");
            }
            this.delegates = delegates;
        }

        protected internal virtual IEnumerable<IAntlrErrorListener<Symbol>> Delegates
        {
            get
            {
                return delegates;
            }
        }

        public virtual void SyntaxError(IRecognizer recognizer, Symbol offendingSymbol, int line, int charPositionInLine, string msg, RecognitionException e)
        {
            foreach (IAntlrErrorListener<Symbol> listener in delegates)
            {
                listener.SyntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
            }
        }
    }
}
