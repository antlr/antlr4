/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
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
    fputs(msg + "\n", stderr)
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

infix operator >>> : BitwiseShiftPrecedence

func >>>(lhs: Int32, rhs: Int32) -> Int32 {
    return lhs &>> rhs
}

func >>>(lhs: Int64, rhs: Int64) -> Int64 {
    return lhs &>> rhs
}

func >>>(lhs: Int, rhs: Int) -> Int {
    return lhs &>> rhs
}

func intChar2String(_ i: Int) -> String {
    return String(Character(integerLiteral: i))
}

func log(_ message: String = "", file: String = #file, function: String = #function, lineNum: Int = #line) {

    // #if DEBUG
    print("FILE: \(URL(fileURLWithPath: file).pathComponents.last!),FUNC: \(function), LINE: \(lineNum) MESSAGE: \(message)")
    //   #else
    // do nothing
    //   #endif
}

func toInt(_ c: Character) -> Int {
    return c.unicodeValue
}

func toInt32(_ data: [Character], _ offset: Int) -> Int {
    return data[offset].unicodeValue | (data[offset + 1].unicodeValue << 16)
}

func toLong(_ data: [Character], _ offset: Int) -> Int64 {
    let mask: Int64 = 0x00000000FFFFFFFF
    let lowOrder: Int64 = Int64(toInt32(data, offset)) & mask
    return lowOrder | Int64(toInt32(data, offset + 2) << 32)
}

func toUUID(_ data: [Character], _ offset: Int) -> UUID {
    let leastSigBits: Int64 = toLong(data, offset)
    let mostSigBits: Int64 = toLong(data, offset + 4)
    return UUID(mostSigBits: mostSigBits, leastSigBits: leastSigBits)
}
