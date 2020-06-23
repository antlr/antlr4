module antlr.v4.runtime.atn.ATNConfigObjectEqualityComparator;

import antlr.v4.runtime.atn.ATNConfig;
import antlr.v4.runtime.misc.MurmurHash;

/**
 * TODO add class description
 */
class ATNConfigObjectEqualityComparator
{

    /**
     * The single instance of ATNConfigObjectEqualityComparator.
     */
    private static __gshared ATNConfigObjectEqualityComparator instance_;

    public static bool opEquals(Object a, Object b)
    {
        if (a is b) {
            return true;
        } else
            if (a is null || b is null) {
                return false;
            }
        auto objA = cast(ATNConfig)a;
        auto objB = cast(ATNConfig)b;
        bool scEqual = false;
        if (objA.semanticContext is objB.semanticContext)
            scEqual = true;
        else
            if (objA.semanticContext is null || objB.semanticContext is null)
                return false;
        return objA.state.stateNumber == objB.state.stateNumber
            && objA.alt == objB.alt
            && (objA.context is objB.context ||
                (objA.context !is null && objB.context !is null &&
                 objA.context.opEquals(objB.context)))
            && scEqual
            && objA.isPrecedenceFilterSuppressed == objB.isPrecedenceFilterSuppressed;
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
                hashCode = MurmurHash.update(hashCode, obj.alt);
                hashCode = MurmurHash.update(hashCode, obj.context);
                hashCode = MurmurHash.update(hashCode, obj.semanticContext);
                hashCode = MurmurHash.finish(hashCode, 4);
                return hashCode;
            }
        return false;
    }

    /**
     * Creates the single instance of ATNConfigObjectEqualityComparator.
     */
    private shared static this()
    {
        instance_ = new ATNConfigObjectEqualityComparator;
    }

    /**
     * Returns: A single instance of ATNConfigObjectEqualityComparator.
     */
    public static ATNConfigObjectEqualityComparator instance()
    {
        return instance_;
    }

}
