//
//  Stack.swift
//  antlr.swift
//
//  Created by janyou on 15/9/8.
//  Copyright © 2015 jlabs. All rights reserved.
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
