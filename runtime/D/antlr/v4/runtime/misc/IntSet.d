module antlr.v4.runtime.misc.IntSet;

/**
 * TODO add interface description
 */
interface IntSet
{

    /**
     * @uml
     * Adds the specified value to the current set.
     *
     *  @param el the value to add
     *
     *  @exception IllegalStateException if the current set is read-only
     */
    public void add(int el);

    /**
     * @uml
     * Modify the current {@link IntSet} object to contain all elements that are
     * present in itself, the specified {@code set}, or both.
     *
     *  @param set The set to add to the current set. A {@code null} argument is
     *  treated as though it were an empty set.
     *  @return {@code this} (to support chained calls)
     *
     *  @exception IllegalStateException if the current set is read-only
     */
    public IntSet addAll(IntSet set);

    /**
     * @uml
     * Return a new {@link IntSet} object containing all elements that are
     *  present in both the current set and the specified set {@code a}.
     *
     *  @param a The set to intersect with the current set. A {@code null}
     *  argument is treated as though it were an empty set.
     *  @return A new {@link IntSet} instance containing the intersection of the
     *  current set and {@code a}. The value {@code null} may be returned in
     *  place of an empty result set.
     */
    public IntSet and(IntSet a);

    public IntSet complement(IntSet elements);

    public IntSet or(IntSet a);

    public IntSet subtract(IntSet a);

    private int size();

    public bool isNil();

    public bool opEquals(Object obj);

    public int getSingleElement();

    public bool contains(int el);

    public void remove(int el);

    public int[] toList();

    public string toString();

}
