package org.antlr.v4.runtime.atn;

/** ATN simulation thread state */
public class ThreadState {
	public int addr;
	public int alt; // or speculatively matched token type for lexers
	public ATNStack context;
	public int inputIndex = -1; // char (or token?) index from 0
	public int inputMarker = -1; // accept states track input markers in case we need to rewind

	public ThreadState(int addr, int alt, ATNStack context) {
		this.addr = addr;
		this.alt = alt;
		this.context = context;
	}

	public ThreadState(ThreadState t) {
		this.addr = t.addr;
		this.alt = t.alt;
		this.context = t.context;
		this.inputIndex = t.inputIndex;
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
