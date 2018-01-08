/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Useful for rewriting out a buffered input token stream after doing some
 * augmentation or other manipulations on it.
 *
 * <p>
 * You can insert stuff, replace, and delete chunks. Note that the operations
 * are done lazily--only if you convert the buffer to a {@link String} with
 * {@link TokenStream#getText()}. This is very efficient because you are not
 * moving data around all the time. As the buffer of tokens is converted to
 * strings, the {@link #getText()} method(s) scan the input token stream and
 * check to see if there is an operation at the current index. If so, the
 * operation is done and then normal {@link String} rendering continues on the
 * buffer. This is like having multiple Turing machine instruction streams
 * (programs) operating on a single input tape. :)</p>
 *
 * <p>
 * This rewriter makes no modifications to the token stream. It does not ask the
 * stream to fill itself up nor does it advance the input cursor. The token
 * stream {@link TokenStream#index()} will return the same value before and
 * after any {@link #getText()} call.</p>
 *
 * <p>
 * The rewriter only works on tokens that you have in the buffer and ignores the
 * current input cursor. If you are buffering tokens on-demand, calling
 * {@link #getText()} halfway through the input will only do rewrites for those
 * tokens in the first half of the file.</p>
 *
 * <p>
 * Since the operations are done lazily at {@link #getText}-time, operations do
 * not screw up the token index values. That is, an insert operation at token
 * index {@code i} does not change the index values for tokens
 * {@code i}+1..n-1.</p>
 *
 * <p>
 * Because operations never actually alter the buffer, you may always get the
 * original token stream back without undoing anything. Since the instructions
 * are queued up, you can easily simulate transactions and roll back any changes
 * if there is an error just by removing instructions. For example,</p>
 *
 * <pre>
 * CharStream input = new ANTLRFileStream("input");
 * TLexer lex = new TLexer(input);
 * CommonTokenStream tokens = new CommonTokenStream(lex);
 * T parser = new T(tokens);
 * TokenStreamRewriter rewriter = new TokenStreamRewriter(tokens);
 * parser.startRule();
 * </pre>
 *
 * <p>
 * Then in the rules, you can execute (assuming rewriter is visible):</p>
 *
 * <pre>
 * Token t,u;
 * ...
 * rewriter.insertAfter(t, "text to put after t");}
 * rewriter.insertAfter(u, "text after u");}
 * System.out.println(rewriter.getText());
 * </pre>
 *
 * <p>
 * You can also have multiple "instruction streams" and get multiple rewrites
 * from a single pass over the input. Just name the instruction streams and use
 * that name again when printing the buffer. This could be useful for generating
 * a C file and also its header file--all from the same buffer:</p>
 *
 * <pre>
 * rewriter.insertAfter("pass1", t, "text to put after t");}
 * rewriter.insertAfter("pass2", u, "text after u");}
 * System.out.println(rewriter.getText("pass1"));
 * System.out.println(rewriter.getText("pass2"));
 * </pre>
 *
 * <p>
 * If you don't use named rewrite streams, a "default" stream is used as the
 * first example shows.</p>
 */
public class TokenStreamRewriter {
	public static final String DEFAULT_PROGRAM_NAME = "default";
	public static final int PROGRAM_INIT_SIZE = 100;
	public static final int MIN_TOKEN_INDEX = 0;

	// Define the rewrite operation hierarchy

	public class RewriteOperation {
		/** What index into rewrites List are we? */
		protected int instructionIndex;
		/** Token buffer index. */
		protected int index;
		protected Object text;

		protected RewriteOperation(int index) {
			this.index = index;
		}

		protected RewriteOperation(int index, Object text) {
			this.index = index;
			this.text = text;
		}
		/** Execute the rewrite operation by possibly adding to the buffer.
		 *  Return the index of the next token to operate on.
		 */
		public int execute(StringBuilder buf) {
			return index;
		}

		@Override
		public String toString() {
			String opName = getClass().getName();
			int $index = opName.indexOf('$');
			opName = opName.substring($index+1, opName.length());
			return "<"+opName+"@"+tokens.get(index)+
					":\""+text+"\">";
		}
	}

	class InsertBeforeOp extends RewriteOperation {
		public InsertBeforeOp(int index, Object text) {
			super(index,text);
		}

		@Override
		public int execute(StringBuilder buf) {
			buf.append(text);
			if ( tokens.get(index).getType()!=Token.EOF ) {
				buf.append(tokens.get(index).getText());
			}
			return index+1;
		}
	}

	/** Distinguish between insert after/before to do the "insert afters"
	 *  first and then the "insert befores" at same index. Implementation
	 *  of "insert after" is "insert before index+1".
	 */
    class InsertAfterOp extends InsertBeforeOp {
        public InsertAfterOp(int index, Object text) {
            super(index+1, text); // insert after is insert before index+1
        }
    }

	/** I'm going to try replacing range from x..y with (y-x)+1 ReplaceOp
	 *  instructions.
	 */
	class ReplaceOp extends RewriteOperation {
		protected int lastIndex;
		public ReplaceOp(int from, int to, Object text) {
			super(from,text);
			lastIndex = to;
		}
		@Override
		public int execute(StringBuilder buf) {
			if ( text!=null ) {
				buf.append(text);
			}
			return lastIndex+1;
		}
		@Override
		public String toString() {
			if ( text==null ) {
				return "<DeleteOp@"+tokens.get(index)+
						".."+tokens.get(lastIndex)+">";
			}
			return "<ReplaceOp@"+tokens.get(index)+
					".."+tokens.get(lastIndex)+":\""+text+"\">";
		}
	}

	/** Our source stream */
	protected final TokenStream tokens;

	/** You may have multiple, named streams of rewrite operations.
	 *  I'm calling these things "programs."
	 *  Maps String (name) &rarr; rewrite (List)
	 */
	protected final Map<String, List<RewriteOperation>> programs;

	/** Map String (program name) &rarr; Integer index */
	protected final Map<String, Integer> lastRewriteTokenIndexes;

	public TokenStreamRewriter(TokenStream tokens) {
		this.tokens = tokens;
		programs = new HashMap<String, List<RewriteOperation>>();
		programs.put(DEFAULT_PROGRAM_NAME,
					 new ArrayList<RewriteOperation>(PROGRAM_INIT_SIZE));
		lastRewriteTokenIndexes = new HashMap<String, Integer>();
	}

	public final TokenStream getTokenStream() {
		return tokens;
	}

	public void rollback(int instructionIndex) {
		rollback(DEFAULT_PROGRAM_NAME, instructionIndex);
	}

	/** Rollback the instruction stream for a program so that
	 *  the indicated instruction (via instructionIndex) is no
	 *  longer in the stream. UNTESTED!
	 */
	public void rollback(String programName, int instructionIndex) {
		List<RewriteOperation> is = programs.get(programName);
		if ( is!=null ) {
			programs.put(programName, is.subList(MIN_TOKEN_INDEX,instructionIndex));
		}
	}

	public void deleteProgram() {
		deleteProgram(DEFAULT_PROGRAM_NAME);
	}

	/** Reset the program so that no instructions exist */
	public void deleteProgram(String programName) {
		rollback(programName, MIN_TOKEN_INDEX);
	}

	public void insertAfter(Token t, Object text) {
		insertAfter(DEFAULT_PROGRAM_NAME, t, text);
	}

	public void insertAfter(int index, Object text) {
		insertAfter(DEFAULT_PROGRAM_NAME, index, text);
	}

	public void insertAfter(String programName, Token t, Object text) {
		insertAfter(programName,t.getTokenIndex(), text);
	}

	public void insertAfter(String programName, int index, Object text) {
		// to insert after, just insert before next index (even if past end)
        RewriteOperation op = new InsertAfterOp(index, text);
        List<RewriteOperation> rewrites = getProgram(programName);
        op.instructionIndex = rewrites.size();
        rewrites.add(op);
	}

	public void insertBefore(Token t, Object text) {
		insertBefore(DEFAULT_PROGRAM_NAME, t, text);
	}

	public void insertBefore(int index, Object text) {
		insertBefore(DEFAULT_PROGRAM_NAME, index, text);
	}

	public void insertBefore(String programName, Token t, Object text) {
		insertBefore(programName, t.getTokenIndex(), text);
	}

	public void insertBefore(String programName, int index, Object text) {
		RewriteOperation op = new InsertBeforeOp(index,text);
		List<RewriteOperation> rewrites = getProgram(programName);
		op.instructionIndex = rewrites.size();
		rewrites.add(op);
	}

	public void replace(int index, Object text) {
		replace(DEFAULT_PROGRAM_NAME, index, index, text);
	}

	public void replace(int from, int to, Object text) {
		replace(DEFAULT_PROGRAM_NAME, from, to, text);
	}

	public void replace(Token indexT, Object text) {
		replace(DEFAULT_PROGRAM_NAME, indexT, indexT, text);
	}

	public void replace(Token from, Token to, Object text) {
		replace(DEFAULT_PROGRAM_NAME, from, to, text);
	}

	public void replace(String programName, int from, int to, Object text) {
		if ( from > to || from<0 || to<0 || to >= tokens.size() ) {
			throw new IllegalArgumentException("replace: range invalid: "+from+".."+to+"(size="+tokens.size()+")");
		}
		RewriteOperation op = new ReplaceOp(from, to, text);
		List<RewriteOperation> rewrites = getProgram(programName);
		op.instructionIndex = rewrites.size();
		rewrites.add(op);
	}

	public void replace(String programName, Token from, Token to, Object text) {
		replace(programName,
				from.getTokenIndex(),
				to.getTokenIndex(),
				text);
	}

	public void delete(int index) {
		delete(DEFAULT_PROGRAM_NAME, index, index);
	}

	public void delete(int from, int to) {
		delete(DEFAULT_PROGRAM_NAME, from, to);
	}

	public void delete(Token indexT) {
		delete(DEFAULT_PROGRAM_NAME, indexT, indexT);
	}

	public void delete(Token from, Token to) {
		delete(DEFAULT_PROGRAM_NAME, from, to);
	}

	public void delete(String programName, int from, int to) {
		replace(programName,from,to,null);
	}

	public void delete(String programName, Token from, Token to) {
		replace(programName,from,to,null);
	}

	public int getLastRewriteTokenIndex() {
		return getLastRewriteTokenIndex(DEFAULT_PROGRAM_NAME);
	}

	protected int getLastRewriteTokenIndex(String programName) {
		Integer I = lastRewriteTokenIndexes.get(programName);
		if ( I==null ) {
			return -1;
		}
		return I;
	}

	protected void setLastRewriteTokenIndex(String programName, int i) {
		lastRewriteTokenIndexes.put(programName, i);
	}

	protected List<RewriteOperation> getProgram(String name) {
		List<RewriteOperation> is = programs.get(name);
		if ( is==null ) {
			is = initializeProgram(name);
		}
		return is;
	}

	private List<RewriteOperation> initializeProgram(String name) {
		List<RewriteOperation> is = new ArrayList<RewriteOperation>(PROGRAM_INIT_SIZE);
		programs.put(name, is);
		return is;
	}

	/** Return the text from the original tokens altered per the
	 *  instructions given to this rewriter.
 	 */
	public String getText() {
		return getText(DEFAULT_PROGRAM_NAME, Interval.of(0,tokens.size()-1));
	}

	/** Return the text from the original tokens altered per the
	 *  instructions given to this rewriter in programName.
 	 */
	public String getText(String programName) {
		return getText(programName, Interval.of(0,tokens.size()-1));
	}

	/** Return the text associated with the tokens in the interval from the
	 *  original token stream but with the alterations given to this rewriter.
	 *  The interval refers to the indexes in the original token stream.
	 *  We do not alter the token stream in any way, so the indexes
	 *  and intervals are still consistent. Includes any operations done
	 *  to the first and last token in the interval. So, if you did an
	 *  insertBefore on the first token, you would get that insertion.
	 *  The same is true if you do an insertAfter the stop token.
 	 */
	public String getText(Interval interval) {
		return getText(DEFAULT_PROGRAM_NAME, interval);
	}

	public String getText(String programName, Interval interval) {
		List<RewriteOperation> rewrites = programs.get(programName);
		int start = interval.a;
		int stop = interval.b;

		// ensure start/end are in range
		if ( stop>tokens.size()-1 ) stop = tokens.size()-1;
		if ( start<0 ) start = 0;

		if ( rewrites==null || rewrites.isEmpty() ) {
			return tokens.getText(interval); // no instructions to execute
		}
		StringBuilder buf = new StringBuilder();

		// First, optimize instruction stream
		Map<Integer, RewriteOperation> indexToOp = reduceToSingleOperationPerIndex(rewrites);

		// Walk buffer, executing instructions and emitting tokens
		int i = start;
		while ( i <= stop && i < tokens.size() ) {
			RewriteOperation op = indexToOp.get(i);
			indexToOp.remove(i); // remove so any left have index size-1
			Token t = tokens.get(i);
			if ( op==null ) {
				// no operation at that index, just dump token
				if ( t.getType()!=Token.EOF ) buf.append(t.getText());
				i++; // move to next token
			}
			else {
				i = op.execute(buf); // execute operation and skip
			}
		}

		// include stuff after end if it's last index in buffer
		// So, if they did an insertAfter(lastValidIndex, "foo"), include
		// foo if end==lastValidIndex.
		if ( stop==tokens.size()-1 ) {
			// Scan any remaining operations after last token
			// should be included (they will be inserts).
			for (RewriteOperation op : indexToOp.values()) {
				if ( op.index >= tokens.size()-1 ) buf.append(op.text);
			}
		}
		return buf.toString();
	}

	/** We need to combine operations and report invalid operations (like
	 *  overlapping replaces that are not completed nested). Inserts to
	 *  same index need to be combined etc...  Here are the cases:
	 *
	 *  I.i.u I.j.v								leave alone, nonoverlapping
	 *  I.i.u I.i.v								combine: Iivu
	 *
	 *  R.i-j.u R.x-y.v	| i-j in x-y			delete first R
	 *  R.i-j.u R.i-j.v							delete first R
	 *  R.i-j.u R.x-y.v	| x-y in i-j			ERROR
	 *  R.i-j.u R.x-y.v	| boundaries overlap	ERROR
	 *
	 *  Delete special case of replace (text==null):
	 *  D.i-j.u D.x-y.v	| boundaries overlap	combine to max(min)..max(right)
	 *
	 *  I.i.u R.x-y.v | i in (x+1)-y			delete I (since insert before
	 *											we're not deleting i)
	 *  I.i.u R.x-y.v | i not in (x+1)-y		leave alone, nonoverlapping
	 *  R.x-y.v I.i.u | i in x-y				ERROR
	 *  R.x-y.v I.x.u 							R.x-y.uv (combine, delete I)
	 *  R.x-y.v I.i.u | i not in x-y			leave alone, nonoverlapping
	 *
	 *  I.i.u = insert u before op @ index i
	 *  R.x-y.u = replace x-y indexed tokens with u
	 *
	 *  First we need to examine replaces. For any replace op:
	 *
	 * 		1. wipe out any insertions before op within that range.
	 *		2. Drop any replace op before that is contained completely within
	 *	 that range.
	 *		3. Throw exception upon boundary overlap with any previous replace.
	 *
	 *  Then we can deal with inserts:
	 *
	 * 		1. for any inserts to same index, combine even if not adjacent.
	 * 		2. for any prior replace with same left boundary, combine this
	 *	 insert with replace and delete this replace.
	 * 		3. throw exception if index in same range as previous replace
	 *
	 *  Don't actually delete; make op null in list. Easier to walk list.
	 *  Later we can throw as we add to index &rarr; op map.
	 *
	 *  Note that I.2 R.2-2 will wipe out I.2 even though, technically, the
	 *  inserted stuff would be before the replace range. But, if you
	 *  add tokens in front of a method body '{' and then delete the method
	 *  body, I think the stuff before the '{' you added should disappear too.
	 *
	 *  Return a map from token index to operation.
	 */
	protected Map<Integer, RewriteOperation> reduceToSingleOperationPerIndex(List<RewriteOperation> rewrites) {
//		System.out.println("rewrites="+rewrites);

		// WALK REPLACES
		for (int i = 0; i < rewrites.size(); i++) {
			RewriteOperation op = rewrites.get(i);
			if ( op==null ) continue;
			if ( !(op instanceof ReplaceOp) ) continue;
			ReplaceOp rop = (ReplaceOp)rewrites.get(i);
			// Wipe prior inserts within range
			List<? extends InsertBeforeOp> inserts = getKindOfOps(rewrites, InsertBeforeOp.class, i);
			for (InsertBeforeOp iop : inserts) {
				if ( iop.index == rop.index ) {
					// E.g., insert before 2, delete 2..2; update replace
					// text to include insert before, kill insert
					rewrites.set(iop.instructionIndex, null);
					rop.text = iop.text.toString() + (rop.text!=null?rop.text.toString():"");
				}
				else if ( iop.index > rop.index && iop.index <= rop.lastIndex ) {
					// delete insert as it's a no-op.
					rewrites.set(iop.instructionIndex, null);
				}
			}
			// Drop any prior replaces contained within
			List<? extends ReplaceOp> prevReplaces = getKindOfOps(rewrites, ReplaceOp.class, i);
			for (ReplaceOp prevRop : prevReplaces) {
				if ( prevRop.index>=rop.index && prevRop.lastIndex <= rop.lastIndex ) {
					// delete replace as it's a no-op.
					rewrites.set(prevRop.instructionIndex, null);
					continue;
				}
				// throw exception unless disjoint or identical
				boolean disjoint =
					prevRop.lastIndex<rop.index || prevRop.index > rop.lastIndex;
				// Delete special case of replace (text==null):
				// D.i-j.u D.x-y.v	| boundaries overlap	combine to max(min)..max(right)
				if ( prevRop.text==null && rop.text==null && !disjoint ) {
					//System.out.println("overlapping deletes: "+prevRop+", "+rop);
					rewrites.set(prevRop.instructionIndex, null); // kill first delete
					rop.index = Math.min(prevRop.index, rop.index);
					rop.lastIndex = Math.max(prevRop.lastIndex, rop.lastIndex);
					System.out.println("new rop "+rop);
				}
				else if ( !disjoint ) {
					throw new IllegalArgumentException("replace op boundaries of "+rop+" overlap with previous "+prevRop);
				}
			}
		}

		// WALK INSERTS
		for (int i = 0; i < rewrites.size(); i++) {
			RewriteOperation op = rewrites.get(i);
			if ( op==null ) continue;
			if ( !(op instanceof InsertBeforeOp) ) continue;
			InsertBeforeOp iop = (InsertBeforeOp)rewrites.get(i);
			// combine current insert with prior if any at same index
			List<? extends InsertBeforeOp> prevInserts = getKindOfOps(rewrites, InsertBeforeOp.class, i);
			for (InsertBeforeOp prevIop : prevInserts) {
				if ( prevIop.index==iop.index ) {
					if ( InsertAfterOp.class.isInstance(prevIop) ) {
						iop.text = catOpText(prevIop.text, iop.text);
						rewrites.set(prevIop.instructionIndex, null);
					}
					else if ( InsertBeforeOp.class.isInstance(prevIop) ) { // combine objects
						// convert to strings...we're in process of toString'ing
						// whole token buffer so no lazy eval issue with any templates
						iop.text = catOpText(iop.text, prevIop.text);
						// delete redundant prior insert
						rewrites.set(prevIop.instructionIndex, null);
					}
				}
			}
			// look for replaces where iop.index is in range; error
			List<? extends ReplaceOp> prevReplaces = getKindOfOps(rewrites, ReplaceOp.class, i);
			for (ReplaceOp rop : prevReplaces) {
				if ( iop.index == rop.index ) {
					rop.text = catOpText(iop.text,rop.text);
					rewrites.set(i, null);	// delete current insert
					continue;
				}
				if ( iop.index >= rop.index && iop.index <= rop.lastIndex ) {
					throw new IllegalArgumentException("insert op "+iop+" within boundaries of previous "+rop);
				}
			}
		}
		// System.out.println("rewrites after="+rewrites);
		Map<Integer, RewriteOperation> m = new HashMap<Integer, RewriteOperation>();
		for (int i = 0; i < rewrites.size(); i++) {
			RewriteOperation op = rewrites.get(i);
			if ( op==null ) continue; // ignore deleted ops
			if ( m.get(op.index)!=null ) {
				throw new Error("should only be one op per index");
			}
			m.put(op.index, op);
		}
		//System.out.println("index to op: "+m);
		return m;
	}

	protected String catOpText(Object a, Object b) {
		String x = "";
		String y = "";
		if ( a!=null ) x = a.toString();
		if ( b!=null ) y = b.toString();
		return x+y;
	}

	/** Get all operations before an index of a particular kind */
	protected <T extends RewriteOperation> List<? extends T> getKindOfOps(List<? extends RewriteOperation> rewrites, Class<T> kind, int before) {
		List<T> ops = new ArrayList<T>();
		for (int i=0; i<before && i<rewrites.size(); i++) {
			RewriteOperation op = rewrites.get(i);
			if ( op==null ) continue; // ignore deleted
			if ( kind.isInstance(op) ) {
				ops.add(kind.cast(op));
			}
		}
		return ops;
	}
}
