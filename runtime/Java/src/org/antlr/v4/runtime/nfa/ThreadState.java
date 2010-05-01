package org.antlr.v4.runtime.nfa;

/** NFA simulation thread state */
public class ThreadState {
	public int addr;
	public int alt; // or speculatively matched token type for lexers 
	public NFAStack context;
	public ThreadState(int addr, int alt, NFAStack context) {
		this.addr = addr;
		this.alt = alt;
		this.context = context;
	}
	
	public boolean equals(Object o) {
		if ( o==null ) return false;
		if ( this==o ) return true;
		ThreadState other = (ThreadState)o;
		return this.addr==other.addr &&
			   this.alt==other.alt &&
			   this.context.equals(other.context);
	}

	public int hashCode() {	return addr + context.hashCode(); }
	
	public String toString() {
		if ( context.parent==null ) {
			return "("+addr+","+alt+")";
		}
		return "("+addr+","+alt+","+context+")";
	}
}
