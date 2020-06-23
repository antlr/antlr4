module antlr.v4.runtime.IllegalStateException;

import antlr.v4.runtime.RuntimeException;

/**
 * TODO add class description
 */
class IllegalStateException : RuntimeException
{

    public this()
    {
        super();
    }

    public this(string elementDescription)
    {
        super(elementDescription);
    }

}
