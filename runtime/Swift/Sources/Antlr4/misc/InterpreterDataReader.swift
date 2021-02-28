/// Copyright (c) 2021 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.

// A class to read plain text interpreter data produced by ANTLR.
public class InterpreterDataReader {
    
    let filePath:String,
        atn:ATN,
        vocabulary:Vocabulary,
        ruleNames:[String],
        channelNames:[String], // Only valid for lexer grammars.
        modeNames:[String] // ditto
    
    /**
     * The structure of the data file is line based with empty lines
     * separating the different parts. For lexers the layout is:
     * token literal names:
     * ...
     *
     * token symbolic names:
     * ...
     *
     * rule names:
     * ...
     *
     * channel names:
     * ...
     *
     * mode names:
     * ...
     *
     * atn:
     * <a single line with comma separated int values> enclosed in a pair of squared brackets.
     *
     * Data for a parser does not contain channel and mode names.
     */
    enum Part {
        case partName
        case tokenLiteralNames
        case tokenSymbolicNames
        case ruleNames
        case channelNames
        case modeNames
        case atn
    }

    enum Error: Swift.Error {
        case dataError(String)
    }
    
    public init(_ filePath:String) throws {
        self.filePath = filePath
        let contents = try String(contentsOfFile: filePath, encoding: String.Encoding.utf8)
        var part = Part.partName,
            literalNames = [String](),
            symbolicNames = [String](),
            ruleNames = [String](),
            channelNames = [String](),
            modeNames = [String](),
            atnText = [Substring](),
            fail:Error?
        contents.enumerateLines { (line,stop) in
            // throws have to be moved outside the enumerateLines block
            if line == "" {
                part = .partName
            }
            switch part {
            case .partName:
                switch line {
                case "token literal names:":
                    part = .tokenLiteralNames
                case "token symbolic names:":
                    part = .tokenSymbolicNames
                case "rule names:":
                    part = .ruleNames
                case "channel names:":
                    part = .channelNames
                case "mode names:":
                    part = .modeNames
                case "atn:":
                    part = .atn
                case "":
                    break
                default:
                    fail = Error.dataError("Unrecognized interpreter data part at "+line)
                }
            case .tokenLiteralNames:
                literalNames.append((line == "null") ? "" : line)
            case .tokenSymbolicNames:
                symbolicNames.append((line == "null") ? "" : line)
            case .ruleNames:
                ruleNames.append(line)
            case .channelNames:
                channelNames.append(line)
            case .modeNames:
                modeNames.append(line)
            case .atn:
                if line.prefix(1) == "[" && line.suffix(1) == "]" {
                    atnText = line.dropFirst().dropLast().split(separator:",")
                } else {
                    fail = Error.dataError("Missing bracket(s) at "+line)
                }
                part = .partName
            }
        }
        if let fail = fail { throw fail }
        vocabulary = Vocabulary(literalNames, symbolicNames)
        self.ruleNames = ruleNames
        self.channelNames = channelNames
        self.modeNames = modeNames
        let atnSerialized = atnText.map{Character(UnicodeScalar(UInt16($0.trimmingCharacters(in:.whitespaces))!)!)}
        atn = try ATNDeserializer().deserialize(atnSerialized)
    }
        
    public func createLexer(input: CharStream)throws->LexerInterpreter {
        return try LexerInterpreter(filePath,
                                    vocabulary,
                                    ruleNames,
                                    channelNames,
                                    modeNames,
                                    atn,
                                    input)
    }
    
    public func createParser(input: TokenStream)throws->ParserInterpreter {
        return try ParserInterpreter(filePath,
                                    vocabulary,
                                    ruleNames,
                                    atn,
                                    input)
    }

}
