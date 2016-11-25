//
//  ArrayList.swift
//  Antlr4
//
//  Created by janyou on 16/2/24.
//  Copyright © 2016 jlabs. All rights reserved.
//

import Foundation

public final class ArrayList<T>: ExpressibleByArrayLiteral  {
    private var array: Array<T>
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
    public init(count: Int, repeatedValue: T) {
        array =  Array<T>( repeating: repeatedValue, count: count)
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
    public subscript(subRange: Range<Int>) -> ArrayList<T> {
        return ArrayList<T>(slice: array[subRange])
    }
     public var count: Int { return array.count }
}

public func == <Element: Equatable>(lhs: ArrayList<Element>, rhs: ArrayList<Element>) -> Bool {
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

public func == <Element: Equatable>(lhs: ArrayList<Element?>, rhs: ArrayList<Element?>) -> Bool {
    if lhs === rhs {
        return true
    }
    
    if lhs.count != rhs.count {
        return false
    }
    
    let length = lhs.count
    
    for i in 0..<length {
        if lhs[i] == nil && rhs[i] != nil {
            return false
        }
        if rhs[i] == nil && lhs[i] != nil {
            return false
        }
        if lhs[i] != nil && rhs[i] != nil && rhs[i]! != rhs[i]! {
            return false
        }
    }
    
    return true
}
