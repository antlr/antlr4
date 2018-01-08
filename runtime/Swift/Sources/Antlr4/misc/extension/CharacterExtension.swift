/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 

//
//  CharacterEextension.swift
//  Antlr.swift
//
//  Created by janyou on 15/9/4.
//

import Foundation

extension Character {

    //"1" -> 1 "2"  -> 2
    var integerValue: Int {
        return Int(String(self)) ?? 0
    }
    public init(integerLiteral value: IntegerLiteralType) {
        self = Character(UnicodeScalar(value)!)
    }
    var utf8Value: UInt8 {
        for s in String(self).utf8 {
            return s
        }
        return 0
    }

    var utf16Value: UInt16 {
        for s in String(self).utf16 {
            return s
        }
        return 0
    }

    //char ->  int
    var unicodeValue: Int {
        return Int(String(self).unicodeScalars.first?.value ?? 0)
    }

    public static var MAX_VALUE: Int {
        let c: Character = "\u{10FFFF}"
        return c.unicodeValue
    }
    public static var MIN_VALUE: Int {
        let c: Character = "\u{0000}"
        return c.unicodeValue
    }

    public static func isJavaIdentifierStart(_ char: Int) -> Bool {
        let ch = Character(integerLiteral: char)
        return ch == "_" || ch == "$" || ("a" <= ch && ch <= "z")
                || ("A" <= ch && ch <= "Z")

    }

    public static func isJavaIdentifierPart(_ char: Int) -> Bool {
        let ch = Character(integerLiteral: char)
        return isJavaIdentifierStart(char) || ("0" <= ch && ch <= "9")
    }

    public static func toCodePoint(_ high: Int, _ low: Int) -> Int {
        let MIN_SUPPLEMENTARY_CODE_POINT = 65536 // 0x010000
        let MIN_HIGH_SURROGATE = 0xd800 //"\u{dbff}"  //"\u{DBFF}"  //"\u{DBFF}"
        let MIN_LOW_SURROGATE = 0xdc00 //"\u{dc00}" //"\u{DC00}"
        return ((high << 10) + low) + (MIN_SUPPLEMENTARY_CODE_POINT
                - (MIN_HIGH_SURROGATE << 10)
                - MIN_LOW_SURROGATE)
    }


}
