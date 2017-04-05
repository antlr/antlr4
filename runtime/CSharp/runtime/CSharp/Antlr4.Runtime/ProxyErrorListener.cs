/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.Collections.Generic;
using System.IO;
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

        public virtual void SyntaxError(TextWriter output, IRecognizer recognizer, Symbol offendingSymbol, int line, int charPositionInLine, string msg, RecognitionException e)
        {
            foreach (IAntlrErrorListener<Symbol> listener in delegates)
            {
                listener.SyntaxError(output, recognizer, offendingSymbol, line, charPositionInLine, msg, e);
            }
        }
    }
}
