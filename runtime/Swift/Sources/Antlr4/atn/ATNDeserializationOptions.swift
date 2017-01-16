/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.



/// 
/// -  Sam Harwell

public class ATNDeserializationOptions {

    static let defaultOptions: ATNDeserializationOptions = {

        let defaultOptions = ATNDeserializationOptions()
        defaultOptions.makeReadOnly()
        return defaultOptions

    }()


    private var readOnly: Bool = false
    private var verifyATN: Bool
    private var generateRuleBypassTransitions: Bool

    public init() {
        self.verifyATN = true
        self.generateRuleBypassTransitions = false
    }

    public init(_ options: ATNDeserializationOptions) {
        self.verifyATN = options.verifyATN
        self.generateRuleBypassTransitions = options.generateRuleBypassTransitions
    }


    public static func getDefaultOptions() -> ATNDeserializationOptions {
        return defaultOptions
    }

    public final func isReadOnly() -> Bool {
        return readOnly
    }

    public final func makeReadOnly() {
        readOnly = true
    }

    public final func isVerifyATN() -> Bool {
        return verifyATN
    }

    public final func setVerifyATN(_ verifyATN: Bool) throws {
        try throwIfReadOnly()
        self.verifyATN = verifyATN
    }

    public final func isGenerateRuleBypassTransitions() -> Bool {
        return generateRuleBypassTransitions
    }

    public final func setGenerateRuleBypassTransitions(_ generateRuleBypassTransitions: Bool) throws {
        try throwIfReadOnly()
        self.generateRuleBypassTransitions = generateRuleBypassTransitions
    }

    internal func throwIfReadOnly() throws {
        if isReadOnly() {
            throw ANTLRError.illegalState(msg: "This object is readonly")

        }
    }
}
