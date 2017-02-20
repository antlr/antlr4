/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


public class LexerNoViableAltException: RecognitionException<LexerATNSimulator>, CustomStringConvertible {
    /// Matching attempted at what input index?
    private final var startIndex: Int

    /// Which configurations did we try at input.index() that couldn't match input.LA(1)?
    private final var deadEndConfigs: ATNConfigSet

    public init(_ lexer: Lexer?,
                _ input: CharStream,
                _ startIndex: Int,
                _ deadEndConfigs: ATNConfigSet) {
        let ctx: ParserRuleContext? = nil
        self.startIndex = startIndex
        self.deadEndConfigs = deadEndConfigs
        super.init(lexer, input as IntStream, ctx)

    }

    public func getStartIndex() -> Int {
        return startIndex
    }


    public func getDeadEndConfigs() -> ATNConfigSet {
        return deadEndConfigs
    }

    //override
//	public func getInputStream() -> CharStream {
//		return super.getInputStream() as! CharStream;
//	}


    public var description: String {
        var symbol: String = ""
        if startIndex >= 0 && startIndex < getInputStream().size() {
            let charStream: CharStream = getInputStream() as! CharStream
            let interval: Interval = Interval.of(startIndex, startIndex)
            symbol = charStream.getText(interval)
            symbol = Utils.escapeWhitespace(symbol, false)
        }

        return "\(LexerNoViableAltException.self)('\(symbol)')"
    }
}
