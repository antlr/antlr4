/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// This is an {@link org.antlr.v4.runtime.ANTLRInputStream} that is loaded from a file all at once
/// when you construct the object.

import Foundation

public class ANTLRFileStream: ANTLRInputStream {
    internal var fileName: String

    public convenience override init(_ fileName: String) {
        // throws; IOException
        self.init(fileName, nil)
    }

    public init(_ fileName: String, _ encoding: String.Encoding?) {
        self.fileName = fileName
        super.init()
        load(fileName, encoding)
    }

    public func load(_ fileName: String, _ encoding: String.Encoding?) {
        if encoding != nil {
            data = Utils.readFile(fileName, encoding!)
        } else {
            data = Utils.readFile(fileName)
        }
        self.n = data.count
    }

    override
    public func getSourceName() -> String {
        return fileName
    }

}
