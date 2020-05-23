module antlr.v4.runtime.misc.ObjectEqualityComparator;

import antlr.v4.runtime.misc.AbstractEqualityComparator;

/**
 * This default implementation of {@link EqualityComparator} uses object equality
 * for comparisons by calling {@link Object#hashCode} and {@link Object#equals}.
 *
 * @author Sam Harwell
 */
class ObjectEqualityComparator : AbstractEqualityComparator!Object
{

    /**
     * The single instance of ObjectEqualityComparator.
     */
    private static __gshared ObjectEqualityComparator instance_;

    /**
     * <p>This implementation returns
     * {@code obj.}{@link Object#hashCode hashCode()}.</p>
     * @uml
     * @safe
     * @nothrow
     */
    public static size_t hashOf(Object obj) @safe nothrow
    {
        if (obj is null)
        {
            return 0;
        }
        return obj.toHash >> 3;
    }

    public static bool opEquals(Object a, Object b)
    {
        if (a is null)
        {
            return b is null;
        }
        return a is b;
    }

    /**
     * Creates the single instance of ObjectEqualityComparator.
     */
    private shared static this()
    {
        instance_ = new ObjectEqualityComparator;
    }

    /**
     * Returns: A single instance of ObjectEqualityComparator.
     */
    public static ObjectEqualityComparator instance()
    {
        return instance_;
    }

}
