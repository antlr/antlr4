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
        super.init()
        let fileContents = try String(contentsOfFile: fileName, encoding: encoding ?? .utf8)
        data = Array(fileContents)
        n = data.count
    }

    override
    public func getSourceName() -> String {
        return fileName
    }
}
