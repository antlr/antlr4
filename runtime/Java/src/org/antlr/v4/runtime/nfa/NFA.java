package org.antlr.v4.runtime.nfa;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** http://swtch.com/~rsc/regexp/regexp2.html */
public class NFA {
	public byte[] code;
	Map<String, Integer> ruleToAddr;
	public int[] tokenTypeToAddr;

	public NFA(byte[] code, Map<String, Integer> ruleToAddr, int[] tokenTypeToAddr) {
		this.code = code;
		this.ruleToAddr = ruleToAddr;
		this.tokenTypeToAddr = tokenTypeToAddr;
	}

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

	public int execThompson(CharStream input) {
		int ip = 0; // always start at SPLIT instr at address 0
		int c = input.LA(1);
		if ( c==Token.EOF ) return Token.EOF;

		List<ThreadState> closure = computeStartState(ip);
		List<ThreadState> reach = new ArrayList<ThreadState>();
		int prevAcceptAddr = Integer.MAX_VALUE;
		int prevAcceptLastCharIndex = -1;
		int prevAcceptInputMarker = -1;
		int firstAcceptInputMarker = -1;
		do { // while more work
			c = input.LA(1);
			int i = 0;
			boolean accepted = false;
processOneChar:
			while ( i<closure.size() ) {
				System.out.println("input["+input.index()+"]=="+(char)c+" closure="+closure+", i="+i+", reach="+ reach);
				ThreadState t = closure.get(i);
				ip = t.addr;
				NFAStack context = t.context;
				int alt = t.alt;
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
					case Bytecode.ACCEPT :
						if ( context != NFAStack.EMPTY ) break; // only do accept for outermost rule 						
						accepted = true;
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
						int j=i+1;
						while ( j<closure.size() ) {
							ThreadState cl = closure.get(j);
							System.out.println("remaining "+ cl);
							if ( cl.alt==alt ) closure.remove(j);
							else j++;
						}
						// then, move to next char, looking for longer match
						// (we continue processing if there are states in reach)
						break;
					case Bytecode.JMP : // ignore
					case Bytecode.SPLIT :
					case Bytecode.CALL :
					case Bytecode.RET :
						break;
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
				System.err.println("no match for char "+(char)c+" at "+input.index());
				input.consume();
			}
			// else reach.size==0 && matched, don't consume: accepted and

			// swap to avoid reallocating space
			List<ThreadState> tmp = reach;
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

	void addToClosure(List<ThreadState> closure, int ip, int alt, NFAStack context) {
		ThreadState t = new ThreadState(ip, alt, context);
		//System.out.println("add to closure "+ip+" "+closure);
		if ( closure.contains(t) ) return; // TODO: VERY INEFFICIENT! use int[num-states] as set test
		closure.add(t);
		short opcode = code[ip];
		ip++; // move to next instruction or first byte of operand
		switch (opcode) {
			case Bytecode.JMP :
				addToClosure(closure, getShort(code, ip), alt, context);
				break;
			case Bytecode.SAVE :
				int labelIndex = getShort(code, ip);
				ip += 2;
				addToClosure(closure, ip, alt, context); // do closure past SAVE
				// TODO: impl
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
			case Bytecode.RET :
				if ( context != NFAStack.EMPTY ) {
					addToClosure(closure, context.returnAddr, alt, context.parent);
				}
		}
	}

	List<ThreadState> computeStartState(int ip) { // assume SPLIT at ip
		List<ThreadState> closure = new ArrayList<ThreadState>();
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
				System.out.println("input["+input.index()+"]=="+(char)c+" closure="+closure+", i="+i+", reach="+ reach);
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

	void trace(int ip) {
		String instr = Bytecode.disassembleInstruction(code, ip);
		System.out.println(instr);
	}

	public static int getShort(byte[] memory, int index) {
		return (memory[index]&0xFF) <<(8*1) | (memory[index+1]&0xFF); // prevent sign extension with mask
	}
}
