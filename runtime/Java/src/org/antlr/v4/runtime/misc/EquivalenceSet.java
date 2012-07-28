package org.antlr.v4.runtime.misc;

import java.util.Set;

/** A set that allows us to override equivalence.  For a single set, we might
 *  want multiple subset perspectives as defined by different hash code
 *  and equivalence methods. HashSet does not allow us to subclass and
 *  override the equivalence operations, so we have to implement our own
 *  sets that are flexible in terms of equivalence.
 */
public interface EquivalenceSet<T> extends Set<T>, EquivalenceRelation<T> {
}
