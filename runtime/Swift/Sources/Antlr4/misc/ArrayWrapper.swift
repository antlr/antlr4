/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.

//
//  ArrayWrapper.swift
//  Antlr4
//
//  Created by janyou on 16/6/21.
//

import Foundation

public final class ArrayWrapper<T: Hashable>: ExpressibleByArrayLiteral, Hashable  {
    public var array: Array<T>
    public init(slice: ArraySlice<T>) {
        array = Array<T>()
        for element in slice {
            array.append(element)
        }
    }
    public init(_ elements: T...) {
        array = Array<T>()
        for element in elements {
            array.append(element)
        }

    }
    public init(_ elements: [T]) {
        array = elements
    }
    public init(count: Int, repeatedValue: T) {
        array =  Array<T>(repeating: repeatedValue, count: count)
    }

    public init(arrayLiteral elements: T...) {
        array = Array<T>()
        for element in elements {
            array.append(element)
        }
    }
    public subscript(index: Int) -> T {
        get {
            return array[index]
        }
        set {
            array[index] = newValue
        }
    }
    public subscript(subRange: Range<Int>) -> ArrayWrapper<T> {
        return ArrayWrapper<T>(slice: array[subRange])
    }
    public var count: Int { return array.count }

    public var hashValue: Int {
        if count == 0 {
            return 0
        }

        var result = 1

        for element in array {
            result = 31 &* result &+ element.hashValue
        }
        return result
    }

}

public func == <Element: Equatable>(lhs: ArrayWrapper<Element>, rhs: ArrayWrapper<Element>) -> Bool {
    if lhs === rhs {
        return true
    }

    if lhs.count != rhs.count {
        return false
    }

    let length = lhs.count
    for i in 0..<length {
        if lhs[i] != rhs[i] {
            return false
        }
    }

    return true
}


