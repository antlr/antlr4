package org.antlr.v4.runtime.pda;

/** Identical to ANTLR's static grammar analysis NFAContext object */
public class NFAStack {
	public static final NFAStack EMPTY = new NFAStack(null, -1);

	public NFAStack parent;

    /** The NFA state following state that invoked another rule's start state
	 *  is recorded on the rule invocation context stack.
     */
    public int returnAddr;

    /** Computing the hashCode is very expensive and NFA.addToClosure()
     *  uses it to track when it's seen a state|ctx before to avoid
     *  infinite loops.  As we add new contexts, record the hash code
     *  as this + parent.cachedHashCode.  Avoids walking
     *  up the tree for every hashCode().  Note that this caching works
     *  because a context is a monotonically growing tree of context nodes
     *  and nothing on the stack is ever modified...ctx just grows
     *  or shrinks.
     */
    protected int cachedHashCode;

	public NFAStack(NFAStack parent, int returnAddr) {
        this.parent = parent;
        this.returnAddr = returnAddr;
        if ( returnAddr >= 0 ) {
            this.cachedHashCode = returnAddr;
        }
        if ( parent!=null ) {
            this.cachedHashCode += parent.cachedHashCode;
        }
    }

	public int hashCode() {	return cachedHashCode; }
	
	/** Two contexts are equals() if both have
	 *  same call stack; walk upwards to the root.
	 *  Recall that the root sentinel node has no parent.
	 *  Note that you may be comparing contextsv in different alt trees.
	 */
	public boolean equals(Object o) {
		NFAStack other = ((NFAStack)o);
		if ( this.cachedHashCode != other.cachedHashCode ) {
			return false; // can't be same if hash is different
		}
		if ( this==other ) return true;

		// System.out.println("comparing "+this+" with "+other);
		NFAStack sp = this;
		while ( sp.parent!=null && other.parent!=null ) {
			if ( sp.returnAddr != other.returnAddr) return false;
			sp = sp.parent;
			other = other.parent;
		}
		if ( !(sp.parent==null && other.parent==null) ) {
			return false; // both pointers must be at their roots after walk
		}
		return true;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		NFAStack sp = this;
		buf.append("[");
		while ( sp.parent!=null ) {
			buf.append(sp.returnAddr);
			buf.append(" ");
			sp = sp.parent;
		}
		buf.append("$]");
		return buf.toString();
	}
}
