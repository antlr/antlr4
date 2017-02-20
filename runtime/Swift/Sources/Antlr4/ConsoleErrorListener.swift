/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


/// 
/// -  Sam Harwell

public class ConsoleErrorListener: BaseErrorListener {
    /// Provides a default instance of {@link org.antlr.v4.runtime.ConsoleErrorListener}.
    public static let INSTANCE: ConsoleErrorListener = ConsoleErrorListener()

    /// {@inheritDoc}
    /// 
    /// <p>
    /// This implementation prints messages to {@link System#err} containing the
    /// values of {@code line}, {@code charPositionInLine}, and {@code msg} using
    /// the following format.</p>
    /// 
    /// <pre>
    /// line <em>line</em>:<em>charPositionInLine</em> <em>msg</em>
    /// </pre>
    override
    public func syntaxError<T:ATNSimulator>(_ recognizer: Recognizer<T>,
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
