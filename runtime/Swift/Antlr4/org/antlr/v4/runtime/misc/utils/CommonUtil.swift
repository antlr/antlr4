/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

//
//  CommonUtil.swift
//   antlr.swift
//
//  Created by janyou on 15/9/4.
//

import Foundation

func errPrint(_ msg: String) {
    fputs(msg + "\n", __stderrp)
}

public func +(lhs: String, rhs: Int) -> String {
    return lhs + String(rhs)
}

public func +(lhs: Int, rhs: String) -> String {
    return String(lhs) + rhs
}

public func +(lhs: String, rhs: Token) -> String {
    return lhs + rhs.description
}

public func +(lhs: Token, rhs: String) -> String {
    return lhs.description + rhs
}
infix operator >>>  : BitwiseShiftPrecedence
//infix operator >>> { associativity right precedence 160 }
func >>>(lhs: Int32, rhs: Int32) -> Int32 {
    let left = UInt32(bitPattern: lhs)
    let right = UInt32(bitPattern: rhs) % 32

    return Int32(bitPattern: left >> right)
}

func >>>(lhs: Int64, rhs: Int64) -> Int64 {
    let left = UInt64(bitPattern: lhs)
    let right = UInt64(bitPattern: rhs) % 64

    return Int64(bitPattern: left >> right)
}

func >>>(lhs: Int, rhs: Int) -> Int {
    let numberOfBits: UInt = MemoryLayout<UInt>.size == MemoryLayout<UInt64>.size ? 64 : 32

    let left = UInt(bitPattern: lhs)
    let right = UInt(bitPattern: rhs) % numberOfBits

    return Int(bitPattern: left >> right)
}


public func synced(_ lock: AnyObject, closure: () -> ()) {
    objc_sync_enter(lock)
    closure()
    objc_sync_exit(lock)
}


public func intChar2String(_ i: Int) -> String {
    return String(Character(integerLiteral: i))
}

public func log(_ message: String = "", file: String = #file, function: String = #function, lineNum: Int = #line) {

    // #if DEBUG
    print("FILE: \(URL(fileURLWithPath: file).pathComponents.last!),FUNC: \(function), LINE: \(lineNum) MESSAGE: \(message)")
    //   #else
    // do nothing
    //   #endif
}


public func RuntimeException(_ message: String = "", file: String = #file, function: String = #function, lineNum: Int = #line) {
    // #if DEBUG
    let info = "FILE: \(URL(fileURLWithPath: file).pathComponents.last!),FUNC: \(function), LINE: \(lineNum) MESSAGE: \(message)"
    //   #else
    // let info = "FILE: \(NSURL(fileURLWithPath: file).pathComponents!.last!),FUNC: \(function), LINE: \(lineNum) MESSAGE: \(message)"
    //   #endif

    fatalError(info)

}


public func toInt(_ c: Character) -> Int {
    return c.unicodeValue
}

public func toInt32(_ data: [Character], _ offset: Int) -> Int {
    return data[offset].unicodeValue | (data[offset + 1].unicodeValue << 16)
}

public func toLong(_ data: [Character], _ offset: Int) -> Int64 {
    let mask: Int64 = 0x00000000FFFFFFFF
    let lowOrder: Int64 = Int64(toInt32(data, offset)) & mask
    return lowOrder | Int64(toInt32(data, offset + 2) << 32)
}

public func toUUID(_ data: [Character], _ offset: Int) -> UUID {
    let leastSigBits: Int64 = toLong(data, offset)
    let mostSigBits: Int64 = toLong(data, offset + 4)
    //TODO:NSUUID(mostSigBits, leastSigBits);
    return UUID(mostSigBits: mostSigBits, leastSigBits: leastSigBits)
}
public func == <Element : Equatable>(
    lhs: Array<Element?>, rhs: Array<Element?>
    ) -> Bool {
        let lhsCount = lhs.count
        if lhsCount != rhs.count {
            return false
        }

        // Test referential equality.
        if lhsCount == 0 || lhs._buffer.identity == rhs._buffer.identity {
            return true
        }

        var streamLHS = lhs.makeIterator()
        var streamRHS = rhs.makeIterator()

        var nextLHS = streamLHS.next()
        while nextLHS != nil {
            let nextRHS = streamRHS.next()
            if nextLHS == nil && nextRHS != nil {
                return false
            }
            else if nextRHS == nil && nextLHS != nil {
                return false
            }
            else if nextLHS! != nextRHS! {
                return false
            }
            nextLHS = streamLHS.next()
        }

        return true

}
public func ArrayEquals<T:Equatable>(_ a: [T], _ a2: [T]) -> Bool {

    if a2.count != a.count {
        return false
    }

    let length = a.count
    for i in 0..<length {
        if a[i] != a2[i] {
            return false
        }


    }

    return true
}

public func ArrayEquals<T:Equatable>(_ a: [T?], _ a2: [T?]) -> Bool {

    if a2.count != a.count {
        return false
    }

    let length = a.count

    for i in 0..<length {
        if a[i] == nil && a2[i] != nil {
            return false
        }
        if a2[i] == nil && a[i] != nil {
            return false
        }
        if a2[i] != nil && a[i] != nil && a[i]! != a2[i]! {
            return false
        }
    }

    return true
}

