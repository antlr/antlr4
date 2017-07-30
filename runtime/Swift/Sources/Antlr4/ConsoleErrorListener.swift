/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


///
/// -  Sam Harwell

public class ConsoleErrorListener: BaseErrorListener {
    /// Provides a default instance of {@link org.antlr.v4.runtime.ConsoleErrorListener}.
    public static let INSTANCE: ConsoleErrorListener = ConsoleErrorListener()

    ///
    /// This implementation prints messages to {@link System#err} containing the
    /// values of {@code line}, {@code charPositionInLine}, and {@code msg} using
    /// the following format.
    ///
    /// line __line__:__charPositionInLine__ __msg__
    ///
    override public func syntaxError<T:ATNSimulator>(_ recognizer: Recognizer<T>,
                                            _ offendingSymbol: AnyObject?,
                                            _ line: Int,
                                            _ charPositionInLine: Int,
                                            _ msg: String,
                                            _ e: AnyObject?
    ) {
        if Parser.ConsoleError {
            errPrint("line \(line):\(charPositionInLine) \(msg)")
        }
    }

}
