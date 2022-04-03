import Token from "./Token.js";

export default class CommonToken extends Token {
    constructor(source, type, channel, start, stop) {
        super();
        this.source = source !== undefined ? source : CommonToken.EMPTY_SOURCE;
        this.type = type !== undefined ? type : null;
        this.channel = channel !== undefined ? channel : Token.DEFAULT_CHANNEL;
        this.start = start !== undefined ? start : -1;
        this.stop = stop !== undefined ? stop : -1;
        this.tokenIndex = -1;
        if (this.source[0] !== null) {
            this.line = source[0].line;
            this.column = source[0].column;
        } else {
            this.column = -1;
        }
    }

    /**
     * Constructs a new {@link CommonToken} as a copy of another {@link Token}.
     *
     * <p>
     * If {@code oldToken} is also a {@link CommonToken} instance, the newly
     * constructed token will share a reference to the {@link //text} field and
     * the {@link Pair} stored in {@link //source}. Otherwise, {@link //text} will
     * be assigned the result of calling {@link //getText}, and {@link //source}
     * will be constructed from the result of {@link Token//getTokenSource} and
     * {@link Token//getInputStream}.</p>
     *
     * @param oldToken The token to copy.
     */
    clone() {
        const t = new CommonToken(this.source, this.type, this.channel, this.start, this.stop);
        t.tokenIndex = this.tokenIndex;
        t.line = this.line;
        t.column = this.column;
        t.text = this.text;
        return t;
    }

    toString() {
        let txt = this.text;
        if (txt !== null) {
            txt = txt.replace(/\n/g, "\\n").replace(/\r/g, "\\r").replace(/\t/g, "\\t");
        } else {
            txt = "<no text>";
        }
        return "[@" + this.tokenIndex + "," + this.start + ":" + this.stop + "='" +
            txt + "',<" + this.type + ">" +
            (this.channel > 0 ? ",channel=" + this.channel : "") + "," +
            this.line + ":" + this.column + "]";
    }

    get text(){
        if (this._text !== null) {
            return this._text;
        }
        const input = this.getInputStream();
        if (input === null) {
            return null;
        }
        const n = input.size;
        if (this.start < n && this.stop < n) {
            return input.getText(this.start, this.stop);
        } else {
            return "<EOF>";
        }
    }

    set text(text) {
        this._text = text;
    }
}

/**
 * An empty {@link Pair} which is used as the default value of
 * {@link //source} for tokens that do not have a source.
 */
CommonToken.EMPTY_SOURCE = [ null, null ];
