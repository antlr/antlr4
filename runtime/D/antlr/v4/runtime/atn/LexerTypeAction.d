module antlr.v4.runtime.atn.LexerTypeAction;

import std.format;
import antlr.v4.runtime.InterfaceLexer;
import antlr.v4.runtime.atn.LexerAction;
import antlr.v4.runtime.atn.LexerActionType;
import antlr.v4.runtime.misc.MurmurHash;
import antlr.v4.runtime.misc.Utils;

/**
 * @uml
 * Implements the {@code type} lexer action by calling {@link Lexer#setType}
 * with the assigned type.
 */
class LexerTypeAction : LexerAction
{

    private int type;

    /**
     * @uml
     * Constructs a new {@code type} action with the specified token type value.
     *  @param type The type to assign to the token using {@link Lexer#setType}.
     */
    public this(int type)
    {
        this.type = type;
    }

    /**
     * @uml
     * Gets the type to assign to a token created by the lexer.
     *  @return The type to assign to a token created by the lexer.
     */
    public int getType()
    {
        return type;
    }

    /**
     * @uml
     * {@inheritDoc}
     *  @return This method returns {@link LexerActionType#TYPE}.
     * @safe
     * @nothrow
     */
    public LexerActionType getActionType() @safe nothrow
    {
        return LexerActionType.TYPE;

    }

    /**
     * @uml
     * {@inheritDoc}
     *  @return This method returns {@code false}.
     */
    public bool isPositionDependent()
    {
        return false;
    }

    /**
     * @uml
     * {@inheritDoc}
     *
     *  <p>This action is implemented by calling {@link Lexer#setType} with the
     * value provided by {@link #getType}.</p>
     */
    public void execute(InterfaceLexer lexer)
    {
        lexer.setType(type);
    }

    /**
     * @uml
     * @safe
     * @nothrow
     * @override
     */
    public override size_t toHash() @safe nothrow
    {
        size_t hash = MurmurHash.initialize();
        hash = MurmurHash.update(hash, Utils.rank(getActionType));
        hash = MurmurHash.update(hash, type);
        return MurmurHash.finish(hash, 2);
    }

    public bool equals(Object obj)
    {
        if (obj == this) {
            return true;
        }
        else if (obj.classinfo != LexerTypeAction.classinfo) {
            return false;
        }
        return type == (cast(LexerTypeAction)obj).type;
    }

    /**
     * @uml
     * @override
     */
    public override string toString()
    {
        return format("type(%d)", type);
    }

}
