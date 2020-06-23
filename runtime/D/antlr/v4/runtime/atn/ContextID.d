module antlr.v4.runtime.atn.ContextID;

/**
 * TODO add class description
 */
class ContextID
{

    public static int globalNodeCount = 0;

    /**
     * The single instance of ContextID.
     */
    private static __gshared ContextID instance_;

    public int getNextId()
    {
        return globalNodeCount++;
    }

    /**
     * Creates the single instance of ContextID.
     */
    private shared static this()
    {
        instance_ = new ContextID;
    }

    /**
     * Returns: A single instance of ContextID.
     */
    public static ContextID instance()
    {
        return instance_;
    }

}
