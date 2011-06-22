package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.*;

/** An ATN state, predicted alt, and syntactic/semantic context.
 *  The syntactic context is a pointer into the rule invocation
 *  chain used to arrive at the state.  The semantic context is
 *  the unordered set semantic predicates encountered before reaching
 *  an ATN state.
 */
public class ATNConfig {
	/** The ATN state associated with this configuration */
	public ATNState state;

	/** What alt (or lexer rule) is predicted by this configuration */
	public int alt;

	/** The stack of invoking states leading to the rule/states associated
	 *  wit this config.
	 */
	public RuleContext context;

	/**
	 Indicates that we have reached this ATN configuration after
	 traversing a predicate transition. This is important because we
	 cannot cache DFA states derived from such configurations
	 otherwise predicates would not get executed again (DFAs don't
	 have predicated edges in v4).
	 */
	public boolean traversedPredicate;

	/**
	 Indicates that we have reached this ATN configuration after
	 traversing a non-force action transition. We do not execute
	 predicates after such actions because the predicates could be
	 functions of the side effects. Force actions must be either side
	 effect free or automatically undone as the parse continues.
	 */
	public boolean traversedAction;

	public ATNConfig(ATNState state,
					 int alt,
					 RuleContext context)
	{
		this.state = state;
		this.alt = alt;
		this.context = context;
	}

	public ATNConfig(ATNConfig c) {
		this.state = c.state;
		this.alt = c.alt;
		this.context = c.context;
		this.traversedPredicate = c.traversedPredicate;
		this.traversedAction = c.traversedAction;
	}

	public ATNConfig(ATNConfig c, ATNState state) {
		this(c);
		this.state = state;
	}

	public ATNConfig(ATNConfig c, ATNState state, RuleContext context) {
		this(c);
		this.state = state;
		this.context = context;
	}

	public ATNConfig(ATNConfig c, RuleContext context) {
		this(c);
		this.context = context;
	}

	/** An ATN configuration is equal to another if both have
     *  the same state, they predict the same alternative, and
     *  syntactic/semantic contexts are the same.
     */
    public boolean equals(Object o) {
		if ( o==null ) return false;
		if ( this==o ) return true;
		ATNConfig other = (ATNConfig)o;
		return this.state.stateNumber==other.state.stateNumber &&
		this.alt==other.alt &&
		(this.context==other.context ||
		this.context.equals(other.context));
    }

    public int hashCode() {
		if ( state==null ) {
			System.out.println("eh?");
		}
        int h = state.stateNumber + alt;
		if ( context!=null ) h += context.hashCode();
        return h;
    }

	public String toString() {
		return toString(null, true);
	}

	public String toString(Recognizer<?> recog, boolean showAlt) {
		StringBuffer buf = new StringBuffer();
		if ( state.ruleIndex>0 ) {
			if ( recog!=null ) buf.append(recog.getRuleNames()[state.ruleIndex]+":");
			else buf.append(state.ruleIndex+":");
		}
		buf.append(state);
		if ( showAlt ) {
			buf.append("|");
			buf.append(alt);
		}
		if ( context!=null ) {
            buf.append("|");
            buf.append(context);
        }
//		if (isAccept) {
//			buf.append("|=>"+alt);
//		}
//		if ( context.approximated ) {
//			buf.append("|approx");
//		}
		return buf.toString();
    }
}
