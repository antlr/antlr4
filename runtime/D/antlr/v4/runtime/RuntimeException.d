module antlr.v4.runtime.RuntimeException;

/**
 * TODO add class description
 */
class RuntimeException : Exception
{

    public string elementDescription;

    private Exception cause;

    public this()
    {
        super("");
    }

    public this(string elementDescription)
    {
        this.elementDescription = elementDescription;
        super(elementDescription);
    }

    public this(Exception cause)
    {
        super("");
        this.cause = cause;
        this.elementDescription = cause.msg;
    }

    public string msg()
    {
        if (elementDescription !is null) {
            return elementDescription;
        }
        return null;
    }

    public void initCause(Exception e)
    {
        cause = e;
    }

    public Exception getCause()
    {
        return cause;
    }

}
