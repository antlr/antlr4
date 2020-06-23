module antlr.v4.runtime.atn.ContextMapObjectEqualityComparator;

import antlr.v4.runtime.atn.ATNConfig;
import antlr.v4.runtime.misc.MurmurHash;

/**
 * TODO add class description
 */
class ContextMapObjectEqualityComparator
{

    /**
     * The single instance of ContextMapObjectEqualityComparator.
     */
    private static __gshared ContextMapObjectEqualityComparator instance_;

    public static bool opEquals(Object a, Object b)
    {
        if (a is b) {
            return true;
        } else
            if (b is null || a is null) {
                return false;
            }
        auto objA = cast(ATNConfig)a;
        auto objB = cast(ATNConfig)b;
        return objA.state.stateNumber == objA.state.stateNumber
            && (objA.context is objB.context ||
                (objA.context !is null && objB.context !is null &&
                 objA.context.opEquals(objB.context)));
    }

    /**
     * @uml
     * @trusted
     * @nothrow
     */
    public static size_t toHash(Object o) @trusted nothrow
    {
        if (cast(ATNConfig)o)
            {
                auto obj = cast(ATNConfig)o;
                size_t hashCode = MurmurHash.initialize(7);
                hashCode = MurmurHash.update(hashCode, obj.state.stateNumber);
                hashCode = MurmurHash.update(hashCode, obj.context);
                hashCode = MurmurHash.finish(hashCode, 2);
                return hashCode;
            }
        return false;
    }

    /**
     * Creates the single instance of ContextMapObjectEqualityComparator.
     */
    private shared static this()
    {
        instance_ = new ContextMapObjectEqualityComparator;
    }

    /**
     * Returns: A single instance of ContextMapObjectEqualityComparator.
     */
    public static ContextMapObjectEqualityComparator instance()
    {
        return instance_;
    }

}
