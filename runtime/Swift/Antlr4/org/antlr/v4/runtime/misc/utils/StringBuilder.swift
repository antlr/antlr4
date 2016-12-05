/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

//
//  StringBuilder.swift
//   antlr.swift
//
//  Created by janyou on 15/9/4.
//

import Foundation

public class StringBuilder {
    private var stringValue: String

    public init(string: String = "") {
        self.stringValue = string
    }

    public func toString() -> String {
        return stringValue
    }

    public var length: Int {
        return stringValue.length
    }
    @discardableResult
    public func append(_ string: String) -> StringBuilder {
        stringValue += string
        return self
    }
    @discardableResult
    public func append<T:CustomStringConvertible>(_ value: T) -> StringBuilder {
        stringValue += value.description
        return self
    }
    @discardableResult
    public func appendLine(_ string: String) -> StringBuilder {
        stringValue += string + "\n"
        return self
    }
    @discardableResult
    public func appendLine<T:CustomStringConvertible>(_ value: T) -> StringBuilder {
        stringValue += value.description + "\n"
        return self
    }
    @discardableResult
    public func clear() -> StringBuilder {
        stringValue = ""
        return self
    }
}

public func +=(lhs: StringBuilder, rhs: String) {
    lhs.append(rhs)
}

public func +=<T:CustomStringConvertible>(lhs: StringBuilder, rhs: T) {
    lhs.append(rhs.description)
}

public func +(lhs: StringBuilder, rhs: StringBuilder) -> StringBuilder {
    return StringBuilder(string: lhs.toString() + rhs.toString())
}

