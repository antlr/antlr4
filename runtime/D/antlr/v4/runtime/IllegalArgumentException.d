module antlr.v4.runtime.IllegalArgumentException;

import antlr.v4.runtime.RuntimeException;

/**
 * TODO add class description
 */
class IllegalArgumentException : RuntimeException
{

    public this(string elementDescription)
    {
        super(elementDescription);
    }

    public this(string elementDescription, Exception e)
    {
        super(elementDescription);
    }

}
