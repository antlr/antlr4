package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.BaseRecognizer;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PredictionContext {
    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);

    public static final PredictionContext EMPTY = new PredictionContext();

    private static final boolean DISABLE_CACHE = false;
    public static final Map<Long, PredictionContext> contextCache = new HashMap<Long, PredictionContext>();

    @Nullable
    public final PredictionContext parent;

    public final int invokingState;

    private final int id;
    private final int cachedHashCode;

    private PredictionContext() {
        this(null, -1, true);
    }

    private PredictionContext(@Nullable PredictionContext parent, int invokingState, boolean cached) {
        this.parent = parent;
        this.invokingState = invokingState;
        this.id = cached ? NEXT_ID.getAndIncrement() : 0;

        int hashCode = 7;
        hashCode = 5 * hashCode + (parent != null ? parent.hashCode() : 0);
        hashCode = 5 * hashCode + invokingState;
        hashCode = Math.max(1, Math.abs(hashCode));
        this.cachedHashCode = cached ? -hashCode : hashCode;
    }

    public static PredictionContext fromRuleContext(@NotNull RuleContext outerContext) {
        return fromRuleContext(outerContext, false);
    }

    public static PredictionContext fromRuleContext(@NotNull RuleContext outerContext, boolean cached) {
        if (outerContext.isEmpty()) {
            return PredictionContext.EMPTY;
        }

        PredictionContext parent;
        if (outerContext.parent != null) {
            parent = PredictionContext.fromRuleContext(outerContext.parent, cached);
        } else {
            parent = PredictionContext.EMPTY;
        }

        return parent.getChild(outerContext.invokingState, cached);
    }

    public PredictionContext getAsCached() {
        if (DISABLE_CACHE || isCached()) {
            return this;
        }

        assert parent != null;
        return parent.getAsCached().getChild(invokingState, true);
    }

    public final PredictionContext getChild(int invokingState) {
        return getChild(invokingState, false);
    }

    public PredictionContext getChild(int invokingState, boolean cached) {
        if (DISABLE_CACHE || !isCached() || invokingState < 0) {
            return new PredictionContext(this, invokingState, false);
        }

        long parent = (long)(this.id) << 32;
        long key = parent + invokingState;

        synchronized (contextCache) {
            PredictionContext child = contextCache.get(key);

            if (child == null) {
                child = new PredictionContext(this, invokingState, cached);
                if (cached) {
                    contextCache.put(key, child);
                }
            }

            return child;
        }
    }

    public boolean isCached() {
        return isEmpty() || cachedHashCode < 0;
    }

    public boolean isEmpty() {
        return parent == null;
    }

    /** Two contexts conflict() if they are equals() or one is a stack suffix
     *  of the other.  For example, contexts [21 12 $] and [21 9 $] do not
     *  conflict, but [21 $] and [21 12 $] do conflict.  Note that I should
     *  probably not show the $ in this case.  There is a dummy node for each
     *  stack that just means empty; $ is a marker that's all.
     *
     *  This is used in relation to checking conflicts associated with a
     *  single NFA state's configurations within a single DFA state.
     *  If there are configurations s and t within a DFA state such that
     *  s.state=t.state && s.alt != t.alt && s.ctx conflicts t.ctx then
     *  the DFA state predicts more than a single alt--it's nondeterministic.
     *  Two contexts conflict if they are the same or if one is a suffix
     *  of the other.
     *
     *  When comparing contexts, if one context has a stack and the other
     *  does not then they should be considered the same context.  The only
     *  way for an NFA state p to have an empty context and a nonempty context
     *  is the case when closure falls off end of rule without a call stack
     *  and re-enters the rule with a context.  This resolves the issue I
     *  discussed with Sriram Srinivasan Feb 28, 2005 about not terminating
     *  fast enough upon nondeterminism.
     */
    public boolean conflictsWith(PredictionContext other) {
        return this.suffix(other); // || this.equals(other);
    }

    /** [$] suffix any context
     *  [21 $] suffix [21 12 $]
     *  [21 12 $] suffix [21 $]
     *  [21 18 $] suffix [21 18 12 9 $]
     *  [21 18 12 9 $] suffix [21 18 $]
     *  [21 12 $] not suffix [21 9 $]
     *
     *  Example "[21 $] suffix [21 12 $]" means: rule r invoked current rule
     *  from state 21.  Rule s invoked rule r from state 12 which then invoked
     *  current rule also via state 21.  While the context prior to state 21
     *  is different, the fact that both contexts emanate from state 21 implies
     *  that they are now going to track perfectly together.  Once they
     *  converged on state 21, there is no way they can separate.  In other
     *  words, the prior stack state is not consulted when computing where to
     *  go in the closure operation.  ?$ and ??$ are considered the same stack.
     *  If ? is popped off then $ and ?$ remain; they are now an empty and
     *  nonempty context comparison.  So, if one stack is a suffix of
     *  another, then it will still degenerate to the simple empty stack
     *  comparison case.
     */
    protected boolean suffix(PredictionContext other) {
        PredictionContext sp = this;
        // if one of the contexts is empty, it never enters loop and returns true
        while ( sp.parent!=null && other.parent!=null ) {
            if ( sp.invokingState != other.invokingState ) {
                return false;
            }
            sp = sp.parent;
            other = other.parent;
        }
        //System.out.println("suffix");
        return true;
    }

    @Override
    public int hashCode() {
        return Math.abs(cachedHashCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof PredictionContext)) {
            return false;
        }

        PredictionContext other = (PredictionContext)o;
        if ( this.hashCode() != other.hashCode() ) {
            return false; // can't be same if hash is different
        }

        PredictionContext sp = this;
        while ( sp!=null && other!=null ) {
            if (sp == other) {
                return true;
            }

            if (sp.isCached() && other.isCached()) {
                return false;
            }

            if ( sp.invokingState != other.invokingState) {
                return false;
            }

            sp = sp.parent;
            other = other.parent;
        }

        return sp == null && other == null;
    }

    @Override
    public String toString() {
        return toString(null, -1);
    }

    public String toString(BaseRecognizer<?> recognizer, int currentState) {
        return toString(recognizer, PredictionContext.EMPTY, currentState);
    }

    public String toString(BaseRecognizer<?> recognizer, PredictionContext stop, int currentState) {
        StringBuilder buf = new StringBuilder();
        PredictionContext p = this;
        int stateNumber = currentState;
        buf.append("[");
        while ( p != null && p != stop ) {
            if ( recognizer!=null ) {
                ATN atn = recognizer.getATN();
                ATNState s = atn.states.get(stateNumber);
                String ruleName = recognizer.getRuleNames()[s.ruleIndex];
                buf.append(ruleName);
                if ( p.parent != null ) buf.append(" ");
//				ATNState invoker = atn.states.get(ctx.invokingState);
//				RuleTransition rt = (RuleTransition)invoker.transition(0);
//				buf.append(recog.getRuleNames()[rt.target.ruleIndex]);
            }
            else {
                if ( !p.isEmpty() ) buf.append(p.invokingState);
                if ( p.parent != null && !p.parent.isEmpty() ) buf.append(" ");
            }
            stateNumber = p.invokingState;
            p = p.parent;
        }
        buf.append("]");
        return buf.toString();
    }
}
