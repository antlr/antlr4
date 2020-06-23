module antlr.v4.runtime.misc.Utils;

import std.array;
import std.traits;
import std.conv;

/**
 * TODO add class description
 */
class Utils
{

    /**
     * @uml
     * @safe
     * @nothrow
     */
    public static size_t rank(T)(T e) @safe nothrow
    {
        foreach (i, member; EnumMembers!T)
        {
            if (e == member)
                return cast(int)(i);
        }
        assert(0, "Not an enum member");
    }

    public static string escapeWhitespace(string s, bool escapeSpaces)
    {
        auto buf = appender!string;
        foreach (char c; s)
        {
            if (c == ' ' && escapeSpaces)
                buf.put('\u00B7');
            else if (c == '\t')
                buf.put("\\t");
            else if (c == '\n')
                buf.put("\\n");
            else if (c == '\r')
                buf.put("\\r");
            else
                buf.put(c);
        }
        return buf.data;
    }

}
