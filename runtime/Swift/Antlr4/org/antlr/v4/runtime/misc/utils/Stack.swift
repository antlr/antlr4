/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

//
//  Stack.swift
//  antlr.swift
//
//  Created by janyou on 15/9/8.
//

import Foundation

public struct Stack<T> {
    var items = [T]()
    public mutating func push(_ item: T) {
        items.append(item)
    }
    @discardableResult
    public mutating func pop() -> T {
        return items.removeLast()
    }

    public mutating func clear() {
        return items.removeAll()
    }

    public func peek() -> T? {
        return items.last
    }
    public var isEmpty: Bool {
        return items.isEmpty
    }

}
