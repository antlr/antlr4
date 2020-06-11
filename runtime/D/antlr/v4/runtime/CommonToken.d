/*
 * Copyright (c) 2012-2020 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.CommonToken;

import antlr.v4.runtime.CharStream;
import antlr.v4.runtime.Token;
import antlr.v4.runtime.TokenConstantDefinition;
import antlr.v4.runtime.TokenSource;
import antlr.v4.runtime.WritableToken;
import antlr.v4.runtime.misc.Interval;
import std.array;
import std.container : DList;
import std.conv;
import std.typecons;
import std.variant;

alias TokenFactorySourcePair = Tuple!(TokenSource, "a", CharStream, "b");

/**
 * TODO add class description
 */
class CommonToken : WritableToken
{

    /**
     * An empty {@link Pair} which is used as the default value of
     * {@link #source} for tokens that do not have a source.
     */
    protected static TokenFactorySourcePair EMPTY_SOURCE;

    /**
     * This is the backing field for {@link #getType} and {@link #setType}.
     */
    protected int type;

    /**
     * This is the backing field for {@link #getLine} and {@link #setLine}.
     */
    protected int line;

    /**
     * This is the backing field for {@link #getCharPositionInLine} and
     * {@link #setCharPositionInLine}.
     */
    protected int charPositionInLine = -1;

    /**
     * This is the backing field for {@link #getChannel} and
     * {@link #setChannel}.
     */
    protected int channel = TokenConstantDefinition.DEFAULT_CHANNEL;

    /**
     * This is the backing field for {@link #getTokenSource} and
     * {@link #getInputStream}.
     *
     * <p>
     * These properties share a field to reduce the memory footprint of
     * {@link CommonToken}. Tokens created by a {@link CommonTokenFactory} from
     * the same source and input stream share a reference to the same
     * {@link Pair} containing these values.</p>
     */
    protected TokenFactorySourcePair source;

    /**
     * This is the backing field for {@link #getText} when the token text is
     * explicitly set in the constructor or via {@link #setText}.
     *
     *  @see #getText()
     */
    protected Variant text;

    /**
     * This is the backing field for {@link #getTokenIndex} and
     * {@link #setTokenIndex}.
     */
    protected size_t index = size_t.max;

    /**
     * This is the backing field for {@link #getStartIndex} and
     * {@link #setStartIndex}.
     * @uml
     * @read
     * @write
     */
    protected size_t startIndex_;

    /**
     * This is the backing field for {@link #getStopIndex} and
     * {@link #setStopIndex}.
     * @uml
     * @read
     * @write
     */
    protected size_t stopIndex_;

    /**
     * Constructs a new {@link CommonToken} with the specified token type.
     *
     *  @param type The token type.
     */
    public this(int type)
    {
        this.type = type;
        this.source = EMPTY_SOURCE;
    }

    public this(TokenFactorySourcePair source, int type, int channel, size_t start, size_t stop)
    {
        this.source = source;
        this.type = type;
        this.channel = channel;
        this.startIndex_ = start;
        this.stopIndex_ = stop;
        if (source.a)
        {
            this.line = source.a.getLine;
            this.charPositionInLine = source.a.getCharPositionInLine;
        }
    }

    /**
     * Constructs a new {@link CommonToken} with the specified token type and
     * text.
     *
     *  @param type The token type.
     *  @param text The text of the token.
     */
    public this(int type, Variant text)
    {
        this.type = type;
        this.channel = TokenConstantDefinition.DEFAULT_CHANNEL;
        this.text = text;
        this.source = EMPTY_SOURCE;
    }

    /**
     * Constructs a new {@link CommonToken} as a copy of another {@link Token}.
     *   *
     * <p>
     * If {@code oldToken} is also a {@link CommonToken} instance, the newly
     * constructed token will share a reference to the {@link #text} field and
     * the {@link Pair} stored in {@link #source}. Otherwise, {@link #text} will
     * be assigned the result of calling {@link #getText}, and {@link #source}
     * will be constructed from the result of {@link Token#getTokenSource} and
     * {@link Token#getInputStream}.</p>
     *
     *  @param oldToken The token to copy.
     */
    public this(Token oldToken)
    {
        type = oldToken.getType;
        line = oldToken.getLine;
        index = oldToken.getTokenIndex;
        charPositionInLine = oldToken.getCharPositionInLine;
        channel = oldToken.getChannel;
        startIndex_ = oldToken.startIndex;
        stopIndex_ = oldToken.stopIndex;

        if (cast(CommonToken) oldToken)
        {
            text = (cast(CommonToken) oldToken).text;
            source = (cast(CommonToken) oldToken).source;
        }
        else
        {
            text = oldToken.getText;
            TokenFactorySourcePair sourceNew = tuple(oldToken.getTokenSource,
                    oldToken.getInputStream);
            source = sourceNew;
        }
    }

    public int getType()
    {
        return type;
    }

    public void setLine(int line)
    {
        this.line = line;
    }

    /**
     * @uml
     * @override
     */
    public override Variant getText()
    {
        Variant Null;
        if (text !is Null)
        {
            return text;
        }

        CharStream input = getInputStream;
        if (input is null)
            return Null;
        auto n = input.size;
        if (startIndex_ < n && stopIndex_ < n)
        {
            Variant v = input.getText(Interval.of(to!int(startIndex_), to!int(stopIndex_)));
            return v;
        }
        else
        {
            Variant v = "<EOF>";
            return v;
        }
    }

    /**
     * Explicitly set the text for this token. If {code text} is not
     * {@code null}, then {@link #getText} will return this value rather than
     * extracting the text from the input.
     *
     *  @param text The explicit text of the token, or {@code null} if the text
     * should be obtained from the input along with the start and stop indexes
     * of the token.
     * @uml
     * @override
     */
    public override void setText(Variant text)
    {
        this.text = text;
    }

    /**
     * @uml
     * @override
     */
    public override int getLine()
    {
        return line;
    }

    /**
     * @uml
     * @override
     */
    public override int getCharPositionInLine()
    {
        return charPositionInLine;
    }

    public void setCharPositionInLine(int charPositionInLine)
    {
        this.charPositionInLine = charPositionInLine;
    }

    /**
     * @uml
     * @override
     */
    public override int getChannel()
    {
        return channel;
    }

    /**
     * @uml
     * @override
     */
    public override void setChannel(int channel)
    {
        this.channel = channel;
    }

    /**
     * @uml
     * @override
     */
    public override void setType(int type)
    {
        this.type = type;
    }

    /**
     * @uml
     * @override
     */
    public override size_t getTokenIndex()
    {
        return index;
    }

    /**
     * @uml
     * @override
     */
    public override void setTokenIndex(size_t index)
    {
        this.index = index;
    }

    /**
     * @uml
     * @override
     */
    public override TokenSource getTokenSource()
    {
        return source.a;
    }

    /**
     * @uml
     * @override
     */
    public override CharStream getInputStream()
    {
        return source.b;
    }

    /**
     * @uml
     * @override
     */
    public override string toString()
    {
        import std.format : format;

        string channelStr = "";
        if (channel > 0)
        {
            channelStr = ",channel=" ~ to!string(channel);
        }
        auto txt = getText.get!(string);
        if (txt.length > 0)
        {
            txt = txt.replace("\n", "\\n");
            txt = txt.replace("\r", "\\r");
            txt = txt.replace("\t", "\\t");
        }
        else
        {
            txt = "<no text>";
        }
        return format!"[@%s,%s:%s='%s',<%s>%s,%s:%s]"(cast(int)getTokenIndex,
            cast(int)startIndex_,
            cast(int)stopIndex_,
            txt, type, channelStr,
            line, getCharPositionInLine
            );
    }

    public final size_t startIndex()
    {
        return this.startIndex_;
    }

    public final void startIndex(size_t startIndex)
    {
        this.startIndex_ = startIndex;
    }

    public final size_t stopIndex()
    {
        return this.stopIndex_;
    }

    public final void stopIndex(size_t stopIndex)
    {
        this.stopIndex_ = stopIndex;
    }

}
