package org.antlr.v4.analysis;

import org.antlr.v4.automata.NFAState;

/** An NFA state, predicted alt, and syntactic/semantic context.
 *  The syntactic context is a pointer into the rule invocation
 *  chain used to arrive at the state.  The semantic context is
 *  the unordered set semantic predicates encountered before reaching
 *  an NFA state.
 */
public class NFAConfig {
	/** The NFA state associated with this configuration */
	public NFAState state;

	/** What alt is predicted by this configuration */
	public int alt;

	/** Record the NFA state that invoked another rule's start state */
	public NFAContext context;

	/** The set of semantic predicates associated with this NFA
	 *  configuration.  The predicates were found on the way to
	 *  the associated NFA state in this syntactic context.
	 */
	public SemanticContext semanticContext = SemanticContext.EMPTY_SEMANTIC_CONTEXT;

	/** Indicate that this configuration has been resolved and no further
	 *  DFA processing should occur with it.  Essentially, this is used
	 *  as an "ignore" bit so that upon a set of nondeterministic configurations
	 *  such as (s|2) and (s|3), I can set (s|3) to resolved=true (and any
	 *  other configuration associated with alt 3).
	 */
	public boolean resolved;

	/** This bit is used to indicate a semantic predicate will be
	 *  used to resolve the conflict.  Method
	 *  DFA.findNewDFAStatesAndAddDFATransitions will add edges for
	 *  the predicates after it performs the reach operation.  The
	 *  nondeterminism resolver sets this when it finds a set of
	 *  nondeterministic configurations (as it does for "resolved" field)
	 *  that have enough predicates to resolve the conflit.
	 */
	boolean resolvedWithPredicate;

	public NFAConfig(NFAState state,
					 int alt,
					 NFAContext context,
					 SemanticContext semanticContext)
	{
		this.state = state;
		this.alt = alt;
		this.context = context;
		this.semanticContext = semanticContext;
	}

	public NFAConfig(NFAConfig c) {
		this.state = c.state;
		this.alt = c.alt;
		this.context = c.context;
		this.semanticContext = c.semanticContext;
	}

	public NFAConfig(NFAConfig c, NFAState state) {
		this(c);
		this.state = state;
	}

	public NFAConfig(NFAConfig c, NFAState state, NFAContext context) {
		this(c);
		this.state = state;
		this.context = context;
	}

	public NFAConfig(NFAConfig c, NFAContext context) {
		this(c);
		this.context = context;
	}

	public NFAConfig(NFAConfig c, NFAState state, SemanticContext semanticContext) {
		this(c);
		this.state = state;
		this.semanticContext = semanticContext;
	}

	/** An NFA configuration is equal to another if both have
     *  the same state, they predict the same alternative, and
     *  syntactic/semantic contexts are the same.
     */
    public boolean equals(Object o) {
		if ( o==null ) return false;
		if ( this==o ) return true;
        NFAConfig other = (NFAConfig)o;
        return this.state==other.state &&
               this.alt==other.alt &&
               this.context.equals(other.context) &&
               this.semanticContext.equals(other.semanticContext);
    }

    public int hashCode() {
		if ( state==null || context==null ) {
			System.out.println("eh?");
		}
        int h = state.stateNumber + alt + context.hashCode();
        return h;
    }

	public String toString() {
		return toString(true);
	}

	public String toString(boolean showAlt) {
		StringBuffer buf = new StringBuffer();
		buf.append(state);
		if ( showAlt ) {
			buf.append("|");
			buf.append(alt);
		}
		if ( context!=null && !context.isEmpty() ) {
            buf.append("|");
            buf.append(context);
        }
		if ( semanticContext!=null && semanticContext!=SemanticContext.EMPTY_SEMANTIC_CONTEXT ) {
            buf.append("|");
            buf.append(semanticContext);
        }
        if ( resolved ) {
            buf.append("|resolved");
        }
		if (resolvedWithPredicate) {
			buf.append("|resolveWithPredicate");
		}
		return buf.toString();
    }
}
