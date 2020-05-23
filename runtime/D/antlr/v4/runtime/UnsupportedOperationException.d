module antlr.v4.runtime.UnsupportedOperationException;

import antlr.v4.runtime.RuntimeException;

/**
 * TODO add class description
 */
class UnsupportedOperationException : RuntimeException
{

    public this(string elementDescription)
    {
        super("Unsupported Operation Exception!");
    }

}
