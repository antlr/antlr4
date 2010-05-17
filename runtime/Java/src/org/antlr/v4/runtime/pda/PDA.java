package org.antlr.v4.runtime.pda;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.Token;
import org.antlr.v4.runtime.CommonToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** A (nondeterministic) pushdown bytecode machine for lexing and LL prediction.
 *  Derived partially from Cox' description of Thompson's 1960s work:
 *  http://swtch.com/~rsc/regexp/regexp2.html
 *
 *  Primary difference is that I've extended to have actions, semantic predicates
 *  and a stack for rule invocation.
 */
public class PDA {
	public interface action_fptr { void exec(int action); }
	public interface sempred_fptr { boolean eval(int predIndex); }

	public byte[] code;
	public Map<String, Integer> ruleToAddr;
	public int[] tokenTypeToAddr;
	public CommonToken[] labelValues;
	public int nLabels;

	/** If we hit an action, we'll have to rewind and do the winning rule again */
	boolean bypassedAction;

    public PDA() {;}
	
	public PDA(byte[] code, Map<String, Integer> ruleToAddr, int[] tokenTypeToAddr, int nLabels) {
		this.code = code;
		this.ruleToAddr = ruleToAddr;
		this.tokenTypeToAddr = tokenTypeToAddr;
		this.nLabels = nLabels;
		labelValues = new CommonToken[nLabels];
	}

	public PDA(byte[] code, int[] tokenTypeToAddr, int nLabels) {
		System.out.println("code="+Arrays.toString(code));
		this.code = code;
		this.tokenTypeToAddr = tokenTypeToAddr;
		this.nLabels = nLabels;
		labelValues = new CommonToken[nLabels];
	}

	public int execThompson(IntStream input) {
		int m = input.mark();
		Arrays.fill(labelValues, null);
		int ttype = execThompson(input, 0, false);
		System.out.println("first attempt ttype="+ttype);
		if ( bypassedAction ) {
			input.rewind(m);
			System.out.println("Bypassed action; rewinding to "+input.index()+" doing with feeling");
			bypassedAction = false;
			Arrays.fill(labelValues, null);
			int ttype2 = execThompson(input, tokenTypeToAddr[ttype], true);
			if ( ttype!=ttype2 ) {
				System.err.println("eh? token diff with action(s)");
			}
			else System.out.println("types are same");
		}
		else input.release(m);
		return ttype;
	}

	public int execThompson(IntStream input, int ip, boolean doActions) {
		int c = input.LA(1);
		if ( c==Token.EOF ) return Token.EOF;

		List<ThreadState> closure = computeStartState(ip);
		List<ThreadState> reach = new ArrayList<ThreadState>();
		ThreadState prevAccept = new ThreadState(Integer.MAX_VALUE, -1, NFAStack.EMPTY);
		ThreadState firstAccept = null;

		int firstCharIndex = input.index(); // use when creating Token

		do { // while more work
			c = input.LA(1);
			int i = 0;
			boolean accepted = false;
processOneChar:
			while ( i<closure.size() ) {
				ThreadState t = closure.get(i);
				ip = t.addr;
				NFAStack context = t.context;
				int alt = t.alt;
				//System.out.println("input["+input.index()+"]=="+(char)c+" closure="+closure+", i="+i+", reach="+ reach);
				trace(ip);
				short opcode = code[ip];
				ip++; // move to next instruction or first byte of operand
				switch (opcode) {
					case Bytecode.MATCH8 :
						if ( c == code[ip] ) {
							addToClosure(reach, ip+1, alt, context);
						}
						break;
					case Bytecode.MATCH16 :
						if ( c == getShort(code, ip) ) {
							addToClosure(reach, ip+2, alt, context);
						}
						break;
					case Bytecode.RANGE8 :
						if ( c>=code[ip] && c<=code[ip+1] ) {
							addToClosure(reach, ip+2, alt, context);
						}
						break;
					case Bytecode.RANGE16 :
						if ( c<getShort(code, ip) || c>getShort(code, ip+2) ) {
							addToClosure(reach, ip+4, alt, context);
						}
						break;
					case Bytecode.WILDCARD :
						if ( c!=Token.EOF ) {
							addToClosure(reach, ip, alt, context);
						}
						break;
					case Bytecode.LABEL : // lexers only
						int labelIndex = getShort(code, ip);
						labelValues[labelIndex] =
							new CommonToken(((CharStream)input), 0, 0, input.index(), -1);
						break;
					case Bytecode.SAVE :
						labelIndex = getShort(code, ip);
						labelValues[labelIndex].setStopIndex(input.index()-1);
						break;
					case Bytecode.ACTION :
						bypassedAction = true;
						if ( doActions ) {
							int ruleIndex = getShort(code, ip);
							int actionIndex = getShort(code, ip+2);
							action(ruleIndex, actionIndex);
						}
						break;
					case Bytecode.ACCEPT :
						if ( context != NFAStack.EMPTY ) break; // only do accept for outermost rule 						
						accepted = true;
						int tokenLastCharIndex = input.index() - 1;
						int ttype = getShort(code, ip);
						System.out.println("ACCEPT "+ ttype +" with last char position "+ tokenLastCharIndex);
						if ( tokenLastCharIndex > prevAccept.inputIndex ) {
							prevAccept.inputIndex = tokenLastCharIndex;
							// choose longest match so far regardless of rule priority
							System.out.println("replacing old best match @ "+prevAccept.addr);
							prevAccept.addr = ip-1;
							prevAccept.inputMarker = input.mark();
							if ( firstAccept==null ) firstAccept = prevAccept;
						}
						else if ( tokenLastCharIndex == prevAccept.inputIndex ) {
							// choose first rule matched if match is of same length
							if ( ip-1 < prevAccept.addr ) { // it will see both accepts for ambig rules
								System.out.println("replacing old best match @ "+prevAccept.addr);
								prevAccept.addr = ip-1;
								prevAccept.inputMarker = input.mark();
							}
						}
						// if we reach accept state, toss out any addresses in rest
						// of work list associated with accept's rule; that rule is done
						int j=i+1;
						while ( j<closure.size() ) {
							ThreadState cl = closure.get(j);
							//System.out.println("remaining "+ cl);
							if ( cl.alt==alt ) closure.remove(j);
							else j++;
						}
						// then, move to next char, looking for longer match
						// (we continue processing if there are states in reach)
						break;
//					case Bytecode.JMP : // ignore
//					case Bytecode.SPLIT :
//					case Bytecode.CALL :
//					case Bytecode.RET :
//					case Bytecode.SEMPRED :
//						break;
					default :
						throw new RuntimeException("invalid instruction @ "+ip+": "+opcode);
				}
				i++;
			}
			// if reach is empty, we didn't match anything but might have accepted
			if ( reach.size()>0 ) { // if we reached other states, consume and process them
				input.consume();
			}
			else if ( !accepted) {
				System.out.println("!!!!! no match for char "+(char)c+" at "+input.index());
				input.consume();
			}
			// else reach.size==0 && matched, don't consume: accepted
			
			// swap to avoid reallocating space
			List<ThreadState> tmp = reach;
			reach = closure;
			closure = tmp;
			reach.clear();
		} while ( closure.size()>0 );

		if ( prevAccept.addr >= code.length ) return Token.INVALID_TOKEN_TYPE;
		int ttype = getShort(code, prevAccept.addr+1);
		input.rewind(prevAccept.inputMarker); // does nothing if we accept'd at input.index() but might need to rewind
		if ( firstAccept.inputMarker < prevAccept.inputMarker ) {
			System.out.println("done at index "+input.index());
			System.out.println("accept marker="+prevAccept.inputMarker);
			input.release(firstAccept.inputMarker); // kill any other markers in stream we made
			System.out.println("leaving with index "+input.index());
		}
		return ttype;
	}

	void addToClosure(List<ThreadState> closure, int ip, int alt, NFAStack context) {
		ThreadState t = new ThreadState(ip, alt, context);
		//System.out.println("add to closure "+ip+" "+closure);
		if ( closure.contains(t) ) return;
		short opcode = code[ip];
		ip++; // move to next instruction or first byte of operand
		switch (opcode) {
			case Bytecode.JMP :
				addToClosure(closure, getShort(code, ip), alt, context);
				break;
			case Bytecode.ACTION :
				ip += 2; // has 2 more bytes than LABEL/SAVE
			case Bytecode.LABEL :
			case Bytecode.SAVE :
				// see through them for closure ops
				closure.add(t); // add to closure; need to execute during reach
				ip += 2;
				addToClosure(closure, ip, alt, context); // do closure past SAVE
				break;
			case Bytecode.SPLIT :
				int nopnds = getShort(code, ip);
				ip += 2;
				// add split addresses to work queue in reverse order ('cept first one)
				for (int i=0; i<nopnds; i++) {
					addToClosure(closure, getShort(code, ip+i*2), alt, context);
				}
				break;
			case Bytecode.CALL :
				int target = getShort(code, ip);
				int retaddr = ip+2;
				addToClosure(closure, target, alt, new NFAStack(context, retaddr));
				break;
			case Bytecode.ACCEPT :
				// accept is just a ret if we have a stack;
				// i.e., don't stop; someone called us and we need to use their
				// accept, not this one
				closure.add(t);	 // add to closure; need to execute during reach			
			case Bytecode.RET :
				if ( context != NFAStack.EMPTY ) {
					addToClosure(closure, context.returnAddr, alt, context.parent);
				}
				break;
			case Bytecode.SEMPRED :
				// add next instruction only if sempred succeeds
				int ruleIndex = getShort(code, ip);
				int predIndex = getShort(code, ip+2);
				System.out.println("eval sempred "+ ruleIndex+", "+predIndex);
				if ( sempred(ruleIndex, predIndex) ) {
					addToClosure(closure, ip+4, alt, context);
				}
				break;
			default :
				// optimization: only add edges of closure to closure list; reduces what we walk later
				// we don't want to have to ignore CALL, RET, etc... later
				closure.add(t);
				break;
		}
	}

	List<ThreadState> computeStartState(int ip) {
		// if we're starting at a SPLIT, add closure of all SPLIT targets
		// else just add closure of ip
		List<ThreadState> closure = new ArrayList<ThreadState>();
		if ( code[ip]!=Bytecode.SPLIT ) {
			addToClosure(closure, ip, 1, NFAStack.EMPTY);
			return closure;
		}
		ip++;
		int nalts = getShort(code, ip);
		ip += 2;
		// add split addresses to work queue in reverse order ('cept first one)
		for (int i=1; i<=nalts; i++) {
			addToClosure(closure, getShort(code, ip), i, NFAStack.EMPTY);
			ip += Bytecode.ADDR_SIZE;
		}
		return closure;
	}

	// ---------------------------------------------------------------------

	// this stuff below can't do SAVE nor CALL/RET but faster.  (nor preds)
	
	public int execThompson_no_stack(CharStream input, int ip) {
		int c = input.LA(1);
		if ( c==Token.EOF ) return Token.EOF;

		List<Integer> closure = new ArrayList<Integer>();
		List<Integer> reach = new ArrayList<Integer>();
		int prevAcceptAddr = Integer.MAX_VALUE;
		int prevAcceptLastCharIndex = -1;
		int prevAcceptInputMarker = -1;
		int firstAcceptInputMarker = -1;
		addToClosure_no_stack(closure, ip);
		do { // while more work
			c = input.LA(1);
			int i = 0;
processOneChar:
			while ( i<closure.size() ) {
			//for (int i=0; i<closure.size(); i++) {
				//System.out.println("input["+input.index()+"]=="+(char)c+" closure="+closure+", i="+i+", reach="+ reach);
				ip = closure.get(i); 
				trace(ip);
				short opcode = code[ip];
				ip++; // move to next instruction or first byte of operand
				switch (opcode) {
					case Bytecode.MATCH8 :
						if ( c == code[ip] ) {
							addToClosure_no_stack(reach, ip+1);
						}
						break;
					case Bytecode.MATCH16 :
						if ( c == getShort(code, ip) ) {
							addToClosure_no_stack(reach, ip+2);
						}
						break;
					case Bytecode.RANGE8 :
						if ( c>=code[ip] && c<=code[ip+1] ) {
							addToClosure_no_stack(reach, ip+2);
						}
						break;
					case Bytecode.RANGE16 :
						if ( c<getShort(code, ip) || c>getShort(code, ip+2) ) {
							addToClosure_no_stack(reach, ip+4);
						}
						break;
					case Bytecode.WILDCARD :
						if ( c!=Token.EOF ) addToClosure_no_stack(reach, ip);
						break;
					case Bytecode.ACCEPT :
						int tokenLastCharIndex = input.index() - 1;
						int ttype = getShort(code, ip);
						System.out.println("ACCEPT "+ ttype +" with last char position "+ tokenLastCharIndex);
						if ( tokenLastCharIndex > prevAcceptLastCharIndex ) {
							prevAcceptLastCharIndex = tokenLastCharIndex;
							// choose longest match so far regardless of rule priority
							System.out.println("replacing old best match @ "+prevAcceptAddr);
							prevAcceptAddr = ip-1;
							prevAcceptInputMarker = input.mark();
							firstAcceptInputMarker = prevAcceptInputMarker;
						}
						else if ( tokenLastCharIndex == prevAcceptLastCharIndex ) {
							// choose first rule matched if match is of same length
							if ( ip-1 < prevAcceptAddr ) { // it will see both accepts for ambig rules
								System.out.println("replacing old best match @ "+prevAcceptAddr);
								prevAcceptAddr = ip-1;
								prevAcceptInputMarker = input.mark();
							}
						}
						// if we reach accept state, toss out any addresses in rest
						// of work list associated with accept's rule; that rule is done
						int ruleStart = tokenTypeToAddr[ttype];
						int ruleStop = code.length;
						if ( ttype+1 < tokenTypeToAddr.length ) {
							ruleStop = tokenTypeToAddr[ttype+1]-1;
						}
						System.out.println("kill range "+ruleStart+".."+ruleStop);
						int j=i+1;
						while ( j<closure.size() ) {
							Integer cl = closure.get(j);
							System.out.println("remaining "+ cl);
							if ( cl>=ruleStart || cl<=ruleStop ) closure.remove(j);
							else j++;
						}
						// then, move to next char, looking for longer match
						// (we continue processing if there are states in reach)
						break;
						//break processOneChar;
					case Bytecode.JMP : // ignore
					case Bytecode.SPLIT :
						break;
					default :
						throw new RuntimeException("invalid instruction @ "+ip+": "+opcode);
				}
				i++;
			}
			if ( reach.size()>0 ) { // if we reached other states, consume and process them
				input.consume();
			}
			// swap to avoid reallocating space
			List<Integer> tmp = reach;
			reach = closure;
			closure = tmp;
			reach.clear();
		} while ( closure.size()>0 );

		if ( prevAcceptAddr >= code.length ) return Token.INVALID_TOKEN_TYPE;
		int ttype = getShort(code, prevAcceptAddr+1);
		System.out.println("done at index "+input.index());
		System.out.println("accept marker="+prevAcceptInputMarker);
		input.rewind(prevAcceptInputMarker); // does nothing if we accept'd at input.index() but might need to rewind
		input.release(firstAcceptInputMarker); // kill any other markers in stream we made
		System.out.println("leaving with index "+input.index());
		return ttype;
	}

	void addToClosure_no_stack(List<Integer> closure, int ip) {
		//System.out.println("add to closure "+ip+" "+closure);
		if ( closure.contains(ip) ) return; // TODO: VERY INEFFICIENT! use int[num-states] as set test
		closure.add(ip);
		short opcode = code[ip];
		ip++; // move to next instruction or first byte of operand
		switch (opcode) {
			case Bytecode.JMP :
				addToClosure_no_stack(closure, getShort(code, ip));
				break;
			case Bytecode.SAVE :
				int labelIndex = getShort(code, ip);
				ip += 2;
				addToClosure_no_stack(closure, ip); // do closure pass SAVE
				// TODO: impl
				break;
			case Bytecode.SPLIT :
				int nopnds = getShort(code, ip);
				ip += 2;
				// add split addresses to work queue in reverse order ('cept first one)
				for (int i=0; i<nopnds; i++) {
					addToClosure_no_stack(closure, getShort(code, ip+i*2));
				}
				break;
		}
	}

	// subclass needs to override these if there are sempreds or actions in lexer rules

	public boolean sempred(int ruleIndex, int actionIndex) {
		return true;
	}

	public void action(int ruleIndex, int actionIndex) {
	}
	
	void trace(int ip) {
		String instr = Bytecode.disassembleInstruction(code, ip);
		System.out.println(instr);
	}

	public static int getShort(byte[] memory, int index) {
		return (memory[index]&0xFF) <<(8*1) | (memory[index+1]&0xFF); // prevent sign extension with mask
	}

/*
	public int exec(CharStream input, String ruleName) {
		return exec(input, ruleToAddr.get(ruleName));
	}

	public int exec(CharStream input) { return exec(input, 0); }

	public int exec(CharStream input, int ip) {
		while ( ip < code.length ) {
			int c = input.LA(1);
			trace(ip);
			short opcode = code[ip];
			ip++; // move to next instruction or first byte of operand
			switch (opcode) {
				case Bytecode.MATCH8 :
					if ( c != code[ip] ) return 0;
					ip++;
					input.consume();
					break;
				case Bytecode.MATCH16 :
					if ( c != getShort(code, ip) ) return 0;
					ip += 2;
					input.consume();
					break;
				case Bytecode.RANGE8 :
					if ( c<code[ip] || c>code[ip+1] ) return 0;
					ip += 2;
					input.consume();
					break;
				case Bytecode.RANGE16 :
					if ( c<getShort(code, ip) || c>getShort(code, ip+2) ) return 0;
					ip += 4;
					input.consume();
					break;
				case Bytecode.ACCEPT :
					int ruleIndex = getShort(code, ip);
					ip += 2;
					System.out.println("accept "+ruleIndex);
					return ruleIndex;
				case Bytecode.JMP :
					int target = getShort(code, ip);
					ip = target;
					continue;
				case Bytecode.SPLIT :
					int nopnds = getShort(code, ip);
					ip += 2;
					for (int i=1; i<=nopnds-1; i++) {
						int addr = getShort(code, ip);
						ip += 2;
						//System.out.println("try alt "+i+" at "+addr);
						int m = input.mark();
						int r = exec(input, addr);
						if ( r>0 ) { input.release(m); return r; }
						input.rewind(m);
					}
					// try final alternative (w/o recursion)
					int addr = getShort(code, ip);
					ip = addr;
					//System.out.println("try alt "+nopnds+" at "+addr);
					continue;
				default :
					throw new RuntimeException("invalid instruction @ "+ip+": "+opcode);
			}
		}
		return 0;
	}

	public static class Context {
		public int ip;
		public int inputMarker;
		public Context(int ip, int inputMarker) {
			this.ip = ip;
			this.inputMarker = inputMarker;
		}
	}

	public int execNoRecursion(CharStream input, int ip) {
		List<Context> work = new ArrayList<Context>();
		work.add(new Context(ip, input.mark()));
workLoop:
		while ( work.size()>0 ) {
			Context ctx = work.remove(work.size()-1); // treat like stack
			ip = ctx.ip;
			input.rewind(ctx.inputMarker);
			while ( ip < code.length ) {
				int c = input.LA(1);
				trace(ip);
				short opcode = code[ip];
				ip++; // move to next instruction or first byte of operand
				switch (opcode) {
					case Bytecode.MATCH8 :
						if ( c != code[ip] ) continue workLoop;
						ip++;
						input.consume();
						break;
					case Bytecode.MATCH16 :
						if ( c != getShort(code, ip) ) continue workLoop;
						ip += 2;
						input.consume();
						break;
					case Bytecode.RANGE8 :
						if ( c<code[ip] || c>code[ip+1] ) continue workLoop;
						ip += 2;
						input.consume();
						break;
					case Bytecode.RANGE16 :
						if ( c<getShort(code, ip) || c>getShort(code, ip+2) ) continue workLoop;
						ip += 4;
						input.consume();
						break;
					case Bytecode.ACCEPT :
						int ruleIndex = getShort(code, ip);
						ip += 2;
						System.out.println("accept "+ruleIndex);
						// returning gives first match not longest; i.e., like PEG
						return ruleIndex;
					case Bytecode.JMP :
						int target = getShort(code, ip);
						ip = target;
						continue;
					case Bytecode.SPLIT :
						int nopnds = getShort(code, ip);
						ip += 2;
						// add split addresses to work queue in reverse order ('cept first one)
						for (int i=nopnds-1; i>=1; i--) {
							int addr = getShort(code, ip+i*2);
							//System.out.println("try alt "+i+" at "+addr);
							work.add(new Context(addr, input.mark()));
						}
						// try first alternative (w/o adding to work list)
						int addr = getShort(code, ip);
						ip = addr;
						//System.out.println("try alt "+nopnds+" at "+addr);
						continue;
					default :
						throw new RuntimeException("invalid instruction @ "+ip+": "+opcode);
				}
			}
		}
		return 0;
	}
*/
	
}
