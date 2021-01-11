/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// This is an _org.antlr.v4.runtime.ANTLRInputStream_ that is loaded from a file all at once
/// when you construct the object.
/// 

import Foundation

public class ANTLRFileStream: ANTLRInputStream {
    private let fileName: String

    public init(_ fileName: String, _ encoding: String.Encoding? = nil) throws {
        self.fileName = fileName
        let fileContents = try String(contentsOfFile: fileName, encoding: encoding ?? .utf8)
        let data = Array(fileContents.unicodeScalars)
        super.init(data, data.count)
    }

    override
    public func getSourceName() -> String {
        return fileName
    }
}
