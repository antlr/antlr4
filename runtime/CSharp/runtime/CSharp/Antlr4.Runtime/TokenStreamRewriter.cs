/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Antlr4.Runtime.Misc;

namespace Antlr4.Runtime
{
    /// <summary>
    /// Useful for rewriting out a buffered input token stream after doing some
    /// augmentation or other manipulations on it.
    /// </summary>
    /// <remarks>
    /// Useful for rewriting out a buffered input token stream after doing some
    /// augmentation or other manipulations on it.
    /// <p>
    /// You can insert stuff, replace, and delete chunks. Note that the operations
    /// are done lazily--only if you convert the buffer to a
    /// <see cref="string"/>
    /// with
    /// <see cref="ITokenStream.GetText()"/>
    /// . This is very efficient because you are not
    /// moving data around all the time. As the buffer of tokens is converted to
    /// strings, the
    /// <see cref="GetText()"/>
    /// method(s) scan the input token stream and
    /// check to see if there is an operation at the current index. If so, the
    /// operation is done and then normal
    /// <see cref="string"/>
    /// rendering continues on the
    /// buffer. This is like having multiple Turing machine instruction streams
    /// (programs) operating on a single input tape. :)</p>
    /// <p>
    /// This rewriter makes no modifications to the token stream. It does not ask the
    /// stream to fill itself up nor does it advance the input cursor. The token
    /// stream
    /// <see cref="IIntStream.Index()"/>
    /// will return the same value before and
    /// after any
    /// <see cref="GetText()"/>
    /// call.</p>
    /// <p>
    /// The rewriter only works on tokens that you have in the buffer and ignores the
    /// current input cursor. If you are buffering tokens on-demand, calling
    /// <see cref="GetText()"/>
    /// halfway through the input will only do rewrites for those
    /// tokens in the first half of the file.</p>
    /// <p>
    /// Since the operations are done lazily at
    /// <see cref="GetText()"/>
    /// -time, operations do
    /// not screw up the token index values. That is, an insert operation at token
    /// index
    /// <c>i</c>
    /// does not change the index values for tokens
    /// <c>i</c>
    /// +1..n-1.</p>
    /// <p>
    /// Because operations never actually alter the buffer, you may always get the
    /// original token stream back without undoing anything. Since the instructions
    /// are queued up, you can easily simulate transactions and roll back any changes
    /// if there is an error just by removing instructions. For example,</p>
    /// <pre>
    /// CharStream input = new ANTLRFileStream("input");
    /// TLexer lex = new TLexer(input);
    /// CommonTokenStream tokens = new CommonTokenStream(lex);
    /// T parser = new T(tokens);
    /// TokenStreamRewriter rewriter = new TokenStreamRewriter(tokens);
    /// parser.startRule();
    /// </pre>
    /// <p>
    /// Then in the rules, you can execute (assuming rewriter is visible):</p>
    /// <pre>
    /// Token t,u;
    /// ...
    /// rewriter.insertAfter(t, "text to put after t");}
    /// rewriter.insertAfter(u, "text after u");}
    /// System.out.println(tokens.toString());
    /// </pre>
    /// <p>
    /// You can also have multiple "instruction streams" and get multiple rewrites
    /// from a single pass over the input. Just name the instruction streams and use
    /// that name again when printing the buffer. This could be useful for generating
    /// a C file and also its header file--all from the same buffer:</p>
    /// <pre>
    /// tokens.insertAfter("pass1", t, "text to put after t");}
    /// tokens.insertAfter("pass2", u, "text after u");}
    /// System.out.println(tokens.toString("pass1"));
    /// System.out.println(tokens.toString("pass2"));
    /// </pre>
    /// <p>
    /// If you don't use named rewrite streams, a "default" stream is used as the
    /// first example shows.</p>
    /// </remarks>
    public class TokenStreamRewriter
    {
        public const string DefaultProgramName = "default";

        public const int ProgramInitSize = 100;

        public const int MinTokenIndex = 0;

        public class RewriteOperation
        {
            protected internal readonly ITokenStream tokens;

            /// <summary>What index into rewrites List are we?</summary>
            protected internal int instructionIndex;

            /// <summary>Token buffer index.</summary>
            /// <remarks>Token buffer index.</remarks>
            protected internal int index;

            protected internal object text;

            protected internal RewriteOperation(ITokenStream tokens, int index)
            {
                // Define the rewrite operation hierarchy
                this.tokens = tokens;
                this.index = index;
            }

            protected internal RewriteOperation(ITokenStream tokens, int index, object text)
            {
                this.tokens = tokens;
                this.index = index;
                this.text = text;
            }

            /// <summary>Execute the rewrite operation by possibly adding to the buffer.</summary>
            /// <remarks>
            /// Execute the rewrite operation by possibly adding to the buffer.
            /// Return the index of the next token to operate on.
            /// </remarks>
            public virtual int Execute(StringBuilder buf)
            {
                return index;
            }

            public override string ToString()
            {
                string opName = GetType().FullName;
                int index = opName.IndexOf('$');
                opName = Sharpen.Runtime.Substring(opName, index + 1, opName.Length);
                return "<" + opName + "@" + tokens.Get(this.index) + ":\"" + text + "\">";
            }
        }

        internal class InsertBeforeOp : TokenStreamRewriter.RewriteOperation
        {
            public InsertBeforeOp(ITokenStream tokens, int index, object text)
                : base(tokens, index, text)
            {
            }

            public override int Execute(StringBuilder buf)
            {
                buf.Append(text);
                if (tokens.Get(index).Type != TokenConstants.EOF)
                {
                    buf.Append(tokens.Get(index).Text);
                }
                return index + 1;
            }
        }

        /// <summary>
        /// I'm going to try replacing range from x..y with (y-x)+1 ReplaceOp
        /// instructions.
        /// </summary>
        /// <remarks>
        /// I'm going to try replacing range from x..y with (y-x)+1 ReplaceOp
        /// instructions.
        /// </remarks>
        internal class ReplaceOp : TokenStreamRewriter.RewriteOperation
        {
            protected internal int lastIndex;

            public ReplaceOp(ITokenStream tokens, int from, int to, object text)
                : base(tokens, from, text)
            {
                lastIndex = to;
            }

            public override int Execute(StringBuilder buf)
            {
                if (text != null)
                {
                    buf.Append(text);
                }
                return lastIndex + 1;
            }

            public override string ToString()
            {
                if (text == null)
                {
                    return "<DeleteOp@" + tokens.Get(index) + ".." + tokens.Get(lastIndex) + ">";
                }
                return "<ReplaceOp@" + tokens.Get(index) + ".." + tokens.Get(lastIndex) + ":\"" + text + "\">";
            }
        }

        /// <summary>Our source stream</summary>
        protected internal readonly ITokenStream tokens;

        /// <summary>You may have multiple, named streams of rewrite operations.</summary>
        /// <remarks>
        /// You may have multiple, named streams of rewrite operations.
        /// I'm calling these things "programs."
        /// Maps String (name) &#x2192; rewrite (List)
        /// </remarks>
        protected internal readonly IDictionary<string, IList<TokenStreamRewriter.RewriteOperation>> programs;

        /// <summary>Map String (program name) &#x2192; Integer index</summary>
        protected internal readonly IDictionary<string, int> lastRewriteTokenIndexes;

        public TokenStreamRewriter(ITokenStream tokens)
        {
            this.tokens = tokens;
            programs = new Dictionary<string, IList<TokenStreamRewriter.RewriteOperation>>();
            programs[DefaultProgramName] = new List<TokenStreamRewriter.RewriteOperation>(ProgramInitSize);
            lastRewriteTokenIndexes = new Dictionary<string, int>();
        }

        public ITokenStream TokenStream
        {
            get
            {
                return tokens;
            }
        }

        public virtual void Rollback(int instructionIndex)
        {
            Rollback(DefaultProgramName, instructionIndex);
        }

        /// <summary>
        /// Rollback the instruction stream for a program so that
        /// the indicated instruction (via instructionIndex) is no
        /// longer in the stream.
        /// </summary>
        /// <remarks>
        /// Rollback the instruction stream for a program so that
        /// the indicated instruction (via instructionIndex) is no
        /// longer in the stream. UNTESTED!
        /// </remarks>
        public virtual void Rollback(string programName, int instructionIndex)
        {
            IList<TokenStreamRewriter.RewriteOperation> @is;
            if (programs.TryGetValue(programName, out @is))
            {
                programs[programName] = new List<RewriteOperation>(@is.Skip(MinTokenIndex).Take(instructionIndex - MinTokenIndex));
            }
        }

        public virtual void DeleteProgram()
        {
            DeleteProgram(DefaultProgramName);
        }

        /// <summary>Reset the program so that no instructions exist</summary>
        public virtual void DeleteProgram(string programName)
        {
            Rollback(programName, MinTokenIndex);
        }

        public virtual void InsertAfter(IToken t, object text)
        {
            InsertAfter(DefaultProgramName, t, text);
        }

        public virtual void InsertAfter(int index, object text)
        {
            InsertAfter(DefaultProgramName, index, text);
        }

        public virtual void InsertAfter(string programName, IToken t, object text)
        {
            InsertAfter(programName, t.TokenIndex, text);
        }

        public virtual void InsertAfter(string programName, int index, object text)
        {
            // to insert after, just insert before next index (even if past end)
            InsertBefore(programName, index + 1, text);
        }

        public virtual void InsertBefore(IToken t, object text)
        {
            InsertBefore(DefaultProgramName, t, text);
        }

        public virtual void InsertBefore(int index, object text)
        {
            InsertBefore(DefaultProgramName, index, text);
        }

        public virtual void InsertBefore(string programName, IToken t, object text)
        {
            InsertBefore(programName, t.TokenIndex, text);
        }

        public virtual void InsertBefore(string programName, int index, object text)
        {
            TokenStreamRewriter.RewriteOperation op = new TokenStreamRewriter.InsertBeforeOp(tokens, index, text);
            IList<TokenStreamRewriter.RewriteOperation> rewrites = GetProgram(programName);
            op.instructionIndex = rewrites.Count;
            rewrites.Add(op);
        }

        public virtual void Replace(int index, object text)
        {
            Replace(DefaultProgramName, index, index, text);
        }

        public virtual void Replace(int from, int to, object text)
        {
            Replace(DefaultProgramName, from, to, text);
        }

        public virtual void Replace(IToken indexT, object text)
        {
            Replace(DefaultProgramName, indexT, indexT, text);
        }

        public virtual void Replace(IToken from, IToken to, object text)
        {
            Replace(DefaultProgramName, from, to, text);
        }

        public virtual void Replace(string programName, int from, int to, object text)
        {
            if (from > to || from < 0 || to < 0 || to >= tokens.Size)
            {
                throw new ArgumentException("replace: range invalid: " + from + ".." + to + "(size=" + tokens.Size + ")");
            }
            TokenStreamRewriter.RewriteOperation op = new TokenStreamRewriter.ReplaceOp(tokens, from, to, text);
            IList<TokenStreamRewriter.RewriteOperation> rewrites = GetProgram(programName);
            op.instructionIndex = rewrites.Count;
            rewrites.Add(op);
        }

        public virtual void Replace(string programName, IToken from, IToken to, object text)
        {
            Replace(programName, from.TokenIndex, to.TokenIndex, text);
        }

        public virtual void Delete(int index)
        {
            Delete(DefaultProgramName, index, index);
        }

        public virtual void Delete(int from, int to)
        {
            Delete(DefaultProgramName, from, to);
        }

        public virtual void Delete(IToken indexT)
        {
            Delete(DefaultProgramName, indexT, indexT);
        }

        public virtual void Delete(IToken from, IToken to)
        {
            Delete(DefaultProgramName, from, to);
        }

        public virtual void Delete(string programName, int from, int to)
        {
            Replace(programName, from, to, null);
        }

        public virtual void Delete(string programName, IToken from, IToken to)
        {
            Replace(programName, from, to, null);
        }

        public virtual int LastRewriteTokenIndex
        {
            get
            {
                return GetLastRewriteTokenIndex(DefaultProgramName);
            }
        }

        protected internal virtual int GetLastRewriteTokenIndex(string programName)
        {
            int I;
            if (!lastRewriteTokenIndexes.TryGetValue(programName, out I))
            {
                return -1;
            }
            return I;
        }

        protected internal virtual void SetLastRewriteTokenIndex(string programName, int i)
        {
            lastRewriteTokenIndexes[programName] = i;
        }

        protected internal virtual IList<TokenStreamRewriter.RewriteOperation> GetProgram(string name)
        {
            IList<TokenStreamRewriter.RewriteOperation> @is;
            if (!programs.TryGetValue(name, out @is))
            {
                @is = InitializeProgram(name);
            }
            return @is;
        }

        private IList<TokenStreamRewriter.RewriteOperation> InitializeProgram(string name)
        {
            IList<TokenStreamRewriter.RewriteOperation> @is = new List<TokenStreamRewriter.RewriteOperation>(ProgramInitSize);
            programs[name] = @is;
            return @is;
        }

        /// <summary>
        /// Return the text from the original tokens altered per the
        /// instructions given to this rewriter.
        /// </summary>
        /// <remarks>
        /// Return the text from the original tokens altered per the
        /// instructions given to this rewriter.
        /// </remarks>
        public virtual string GetText()
        {
            return GetText(DefaultProgramName, Interval.Of(0, tokens.Size - 1));
        }

        /// <summary>
        /// Return the text associated with the tokens in the interval from the
        /// original token stream but with the alterations given to this rewriter.
        /// </summary>
        /// <remarks>
        /// Return the text associated with the tokens in the interval from the
        /// original token stream but with the alterations given to this rewriter.
        /// The interval refers to the indexes in the original token stream.
        /// We do not alter the token stream in any way, so the indexes
        /// and intervals are still consistent. Includes any operations done
        /// to the first and last token in the interval. So, if you did an
        /// insertBefore on the first token, you would get that insertion.
        /// The same is true if you do an insertAfter the stop token.
        /// </remarks>
        public virtual string GetText(Interval interval)
        {
            return GetText(DefaultProgramName, interval);
        }

        public virtual string GetText(string programName, Interval interval)
        {
            IList<TokenStreamRewriter.RewriteOperation> rewrites;
            if (!programs.TryGetValue(programName, out rewrites))
                rewrites = null;

            int start = interval.a;
            int stop = interval.b;
            // ensure start/end are in range
            if (stop > tokens.Size - 1)
            {
                stop = tokens.Size - 1;
            }
            if (start < 0)
            {
                start = 0;
            }
            if (rewrites == null || rewrites.Count == 0)
            {
                return tokens.GetText(interval);
            }
            // no instructions to execute
            StringBuilder buf = new StringBuilder();
            // First, optimize instruction stream
            IDictionary<int, TokenStreamRewriter.RewriteOperation> indexToOp = ReduceToSingleOperationPerIndex(rewrites);
            // Walk buffer, executing instructions and emitting tokens
            int i = start;
            while (i <= stop && i < tokens.Size)
            {
                TokenStreamRewriter.RewriteOperation op;
                if (indexToOp.TryGetValue(i, out op))
                    indexToOp.Remove(i);

                // remove so any left have index size-1
                IToken t = tokens.Get(i);
                if (op == null)
                {
                    // no operation at that index, just dump token
                    if (t.Type != TokenConstants.EOF)
                    {
                        buf.Append(t.Text);
                    }
                    i++;
                }
                else
                {
                    // move to next token
                    i = op.Execute(buf);
                }
            }
            // execute operation and skip
            // include stuff after end if it's last index in buffer
            // So, if they did an insertAfter(lastValidIndex, "foo"), include
            // foo if end==lastValidIndex.
            if (stop == tokens.Size - 1)
            {
                // Scan any remaining operations after last token
                // should be included (they will be inserts).
                foreach (TokenStreamRewriter.RewriteOperation op in indexToOp.Values)
                {
                    if (op.index >= tokens.Size - 1)
                    {
                        buf.Append(op.text);
                    }
                }
            }
            return buf.ToString();
        }

        /// <summary>
        /// We need to combine operations and report invalid operations (like
        /// overlapping replaces that are not completed nested).
        /// </summary>
        /// <remarks>
        /// We need to combine operations and report invalid operations (like
        /// overlapping replaces that are not completed nested). Inserts to
        /// same index need to be combined etc...  Here are the cases:
        /// I.i.u I.j.v								leave alone, nonoverlapping
        /// I.i.u I.i.v								combine: Iivu
        /// R.i-j.u R.x-y.v	| i-j in x-y			delete first R
        /// R.i-j.u R.i-j.v							delete first R
        /// R.i-j.u R.x-y.v	| x-y in i-j			ERROR
        /// R.i-j.u R.x-y.v	| boundaries overlap	ERROR
        /// Delete special case of replace (text==null):
        /// D.i-j.u D.x-y.v	| boundaries overlap	combine to max(min)..max(right)
        /// I.i.u R.x-y.v | i in (x+1)-y			delete I (since insert before
        /// we're not deleting i)
        /// I.i.u R.x-y.v | i not in (x+1)-y		leave alone, nonoverlapping
        /// R.x-y.v I.i.u | i in x-y				ERROR
        /// R.x-y.v I.x.u 							R.x-y.uv (combine, delete I)
        /// R.x-y.v I.i.u | i not in x-y			leave alone, nonoverlapping
        /// I.i.u = insert u before op @ index i
        /// R.x-y.u = replace x-y indexed tokens with u
        /// First we need to examine replaces. For any replace op:
        /// 1. wipe out any insertions before op within that range.
        /// 2. Drop any replace op before that is contained completely within
        /// that range.
        /// 3. Throw exception upon boundary overlap with any previous replace.
        /// Then we can deal with inserts:
        /// 1. for any inserts to same index, combine even if not adjacent.
        /// 2. for any prior replace with same left boundary, combine this
        /// insert with replace and delete this replace.
        /// 3. throw exception if index in same range as previous replace
        /// Don't actually delete; make op null in list. Easier to walk list.
        /// Later we can throw as we add to index &#x2192; op map.
        /// Note that I.2 R.2-2 will wipe out I.2 even though, technically, the
        /// inserted stuff would be before the replace range. But, if you
        /// add tokens in front of a method body '{' and then delete the method
        /// body, I think the stuff before the '{' you added should disappear too.
        /// Return a map from token index to operation.
        /// </remarks>
        protected internal virtual IDictionary<int, TokenStreamRewriter.RewriteOperation> ReduceToSingleOperationPerIndex(IList<TokenStreamRewriter.RewriteOperation> rewrites)
        {
            //		System.out.println("rewrites="+rewrites);
            // WALK REPLACES
            for (int i = 0; i < rewrites.Count; i++)
            {
                TokenStreamRewriter.RewriteOperation op = rewrites[i];
                if (op == null)
                {
                    continue;
                }
                if (!(op is TokenStreamRewriter.ReplaceOp))
                {
                    continue;
                }
                TokenStreamRewriter.ReplaceOp rop = (TokenStreamRewriter.ReplaceOp)rewrites[i];
                // Wipe prior inserts within range
                IList<TokenStreamRewriter.InsertBeforeOp> inserts = GetKindOfOps<TokenStreamRewriter.InsertBeforeOp>(rewrites, i);
                foreach (TokenStreamRewriter.InsertBeforeOp iop in inserts)
                {
                    if (iop.index == rop.index)
                    {
                        // E.g., insert before 2, delete 2..2; update replace
                        // text to include insert before, kill insert
                        rewrites[iop.instructionIndex] = null;
                        rop.text = iop.text.ToString() + (rop.text != null ? rop.text.ToString() : string.Empty);
                    }
                    else
                    {
                        if (iop.index > rop.index && iop.index <= rop.lastIndex)
                        {
                            // delete insert as it's a no-op.
                            rewrites[iop.instructionIndex] = null;
                        }
                    }
                }
                // Drop any prior replaces contained within
                IList<TokenStreamRewriter.ReplaceOp> prevReplaces = GetKindOfOps<TokenStreamRewriter.ReplaceOp>(rewrites, i);
                foreach (TokenStreamRewriter.ReplaceOp prevRop in prevReplaces)
                {
                    if (prevRop.index >= rop.index && prevRop.lastIndex <= rop.lastIndex)
                    {
                        // delete replace as it's a no-op.
                        rewrites[prevRop.instructionIndex] = null;
                        continue;
                    }
                    // throw exception unless disjoint or identical
                    bool disjoint = prevRop.lastIndex < rop.index || prevRop.index > rop.lastIndex;
                    bool same = prevRop.index == rop.index && prevRop.lastIndex == rop.lastIndex;
                    // Delete special case of replace (text==null):
                    // D.i-j.u D.x-y.v	| boundaries overlap	combine to max(min)..max(right)
                    if (prevRop.text == null && rop.text == null && !disjoint)
                    {
                        //System.out.println("overlapping deletes: "+prevRop+", "+rop);
                        rewrites[prevRop.instructionIndex] = null;
                        // kill first delete
                        rop.index = Math.Min(prevRop.index, rop.index);
                        rop.lastIndex = Math.Max(prevRop.lastIndex, rop.lastIndex);
#if !PORTABLE
                        System.Console.Out.WriteLine("new rop " + rop);
#endif
                    }
                    else
                    {
                        if (!disjoint && !same)
                        {
                            throw new ArgumentException("replace op boundaries of " + rop + " overlap with previous " + prevRop);
                        }
                    }
                }
            }
            // WALK INSERTS
            for (int i_1 = 0; i_1 < rewrites.Count; i_1++)
            {
                TokenStreamRewriter.RewriteOperation op = rewrites[i_1];
                if (op == null)
                {
                    continue;
                }
                if (!(op is TokenStreamRewriter.InsertBeforeOp))
                {
                    continue;
                }
                TokenStreamRewriter.InsertBeforeOp iop = (TokenStreamRewriter.InsertBeforeOp)rewrites[i_1];
                // combine current insert with prior if any at same index
                IList<TokenStreamRewriter.InsertBeforeOp> prevInserts = GetKindOfOps<TokenStreamRewriter.InsertBeforeOp>(rewrites, i_1);
                foreach (TokenStreamRewriter.InsertBeforeOp prevIop in prevInserts)
                {
                    if (prevIop.index == iop.index)
                    {
                        // combine objects
                        // convert to strings...we're in process of toString'ing
                        // whole token buffer so no lazy eval issue with any templates
                        iop.text = CatOpText(iop.text, prevIop.text);
                        // delete redundant prior insert
                        rewrites[prevIop.instructionIndex] = null;
                    }
                }
                // look for replaces where iop.index is in range; error
                IList<TokenStreamRewriter.ReplaceOp> prevReplaces = GetKindOfOps<TokenStreamRewriter.ReplaceOp>(rewrites, i_1);
                foreach (TokenStreamRewriter.ReplaceOp rop in prevReplaces)
                {
                    if (iop.index == rop.index)
                    {
                        rop.text = CatOpText(iop.text, rop.text);
                        rewrites[i_1] = null;
                        // delete current insert
                        continue;
                    }
                    if (iop.index >= rop.index && iop.index <= rop.lastIndex)
                    {
                        throw new ArgumentException("insert op " + iop + " within boundaries of previous " + rop);
                    }
                }
            }
            // System.out.println("rewrites after="+rewrites);
            IDictionary<int, TokenStreamRewriter.RewriteOperation> m = new Dictionary<int, TokenStreamRewriter.RewriteOperation>();
            for (int i_2 = 0; i_2 < rewrites.Count; i_2++)
            {
                TokenStreamRewriter.RewriteOperation op = rewrites[i_2];
                if (op == null)
                {
                    continue;
                }
                // ignore deleted ops
                if (m.ContainsKey(op.index))
                {
                    throw new InvalidOperationException("should only be one op per index");
                }
                m[op.index] = op;
            }
            //System.out.println("index to op: "+m);
            return m;
        }

        protected internal virtual string CatOpText(object a, object b)
        {
            string x = string.Empty;
            string y = string.Empty;
            if (a != null)
            {
                x = a.ToString();
            }
            if (b != null)
            {
                y = b.ToString();
            }
            return x + y;
        }

        /// <summary>Get all operations before an index of a particular kind</summary>
        protected internal virtual IList<T> GetKindOfOps<T>(IList<RewriteOperation> rewrites, int before)
        {
            return rewrites.Take(before).OfType<T>().ToList();
        }
    }
}
