/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.

//import Cocoa

#if os(OSX)

import Cocoa

#elseif os(iOS)

import UIKit

#endif

//http://stackoverflow.com/questions/28182441/swift-how-to-get-substring-from-start-to-last-index-of-character
//https://github.com/williamFalcon/Bolt_Swift/blob/master/Bolt/BoltLibrary/String/String.swift

public extension String {

    func trim() -> String {
        return self.trimmingCharacters(in: CharacterSet.whitespaces)
    }

    func split(_ separator: String) -> [String] {
        return self.components(separatedBy: separator)
    }

    func replaceAll(_ from: String, replacement: String) -> String {

        return self.replacingOccurrences(of: from, with: replacement, options: NSString.CompareOptions.literal, range: nil)
    }

    func contains(_ find: String) -> Bool {
        return self.range(of: find) != nil
    }

    func containsIgnoreCase(_ find: String) -> Bool {
        return self.lowercased().range(of: find.lowercased()) != nil
    }

    var length: Int {
        return self.characters.count
    }

    func indexOf(_ target: String) -> Int {
        let range = self.range(of: target)
        if let range = range {
            return self.characters.distance(from: self.startIndex, to: range.lowerBound)

        } else {
            return -1
        }
    }

    func indexOf(_ target: String, startIndex: Int) -> Int {

        let startRange = self.characters.index(self.startIndex, offsetBy: startIndex)
        let range = self.range(of: target, options: NSString.CompareOptions.literal, range: startRange..<self.endIndex)

        if let range = range {

            return self.characters.distance(from: self.startIndex, to: range.lowerBound)
        } else {
            return -1
        }
    }

    func lastIndexOf(_ target: String) -> Int {
        var index = -1
        var stepIndex = self.indexOf(target)
        while stepIndex > -1 {
            index = stepIndex
            if stepIndex + target.length < self.length {
                stepIndex = indexOf(target, startIndex: stepIndex + target.length)
            } else {
                stepIndex = -1
            }
        }
        return index
    }

    func substringAfter(_ string: String) -> String {
        if let range = self.range(of: string) {
            let intIndex: Int = self.characters.distance(from: self.startIndex, to: range.upperBound)
            return self.substring(from: self.characters.index(self.startIndex, offsetBy: intIndex))
        }
        return self

    }

    var lowercaseFirstChar: String {
        var result = self
        if self.length > 0 {
            let startIndex = self.startIndex
            result.replaceSubrange(startIndex ... startIndex, with: String(self[startIndex]).lowercased())
        }
        return result
    }
    func substringWithRange(_ range: Range<Int>) -> String {


        let start = self.characters.index(self.startIndex, offsetBy: range.lowerBound)

        let end = self.characters.index(self.startIndex, offsetBy: range.upperBound)
        return self.substring(with: start ..< end)
    }

    subscript(integerIndex: Int) -> Character {
        let index = characters.index(startIndex, offsetBy: integerIndex)
        return self[index]
    }

    subscript(integerRange: Range<Int>) -> String {
        let start = characters.index(startIndex, offsetBy: integerRange.lowerBound)
        let end = characters.index(startIndex, offsetBy: integerRange.upperBound)
        let range = start ..< end
        return self[range]
    }

    func charAt(_ index: Int) -> Character {
       return self[self.characters.index(self.startIndex, offsetBy: index)]
    }

}

// Mapping from XML/HTML character entity reference to character
// From http://en.wikipedia.org/wiki/List_of_XML_and_HTML_character_entity_references
private let characterEntities: [String:Character] = [
        // XML predefined entities:
        "&quot;": "\"",
        "&amp;": "&",
        "&apos;": "'",
        "&lt;": "<",
        "&gt;": ">",

        // HTML character entity references:
        "&nbsp;": "\u{00a0}",
        // ...
        "&diams;": "♦",
]

extension String {

    /// Returns a new string made by replacing in the `String`
    /// all HTML character entity references with the corresponding
    /// character.
    var stringByDecodingHTMLEntities: String {


        // Convert the number in the string to the corresponding
        // Unicode character, e.g.
        //    decodeNumeric("64", 10)   --> "@"
        //    decodeNumeric("20ac", 16) --> "€"
        func decodeNumeric(_ string: String, base: Int32) -> Character? {
            let code = UInt32(strtoul(string, nil, base))
            return Character(UnicodeScalar(code)!)
        }

        // Decode the HTML character entity to the corresponding
        // Unicode character, return `nil` for invalid input.
        //     decode("&#64;")    --> "@"
        //     decode("&#x20ac;") --> "€"
        //     decode("&lt;")     --> "<"
        //     decode("&foo;")    --> nil
        func decode(_ entity: String) -> Character? {

            if entity.hasPrefix("&#x") || entity.hasPrefix("&#X") {
                return decodeNumeric(entity.substring(from: entity.characters.index(entity.startIndex, offsetBy: 3)), base: 16)
            } else if entity.hasPrefix("&#") {
                return decodeNumeric(entity.substring(from: entity.characters.index(entity.startIndex, offsetBy: 2)), base: 10)
            } else {
                return characterEntities[entity]
            }
        }


        var result = ""
        var position = startIndex

        // Find the next '&' and copy the characters preceding it to `result`:
        while let ampRange = self.range(of: "&", range: position ..< endIndex) {
            result.append(self[position ..< ampRange.lowerBound])
            position = ampRange.lowerBound

            // Find the next ';' and copy everything from '&' to ';' into `entity`
            if let semiRange = self.range(of: ";", range: position ..< endIndex) {
                let entity = self[position ..< semiRange.upperBound]
                position = semiRange.upperBound

                if let decoded = decode(entity) {
                    // Replace by decoded character:
                    result.append(decoded)
                } else {
                    // Invalid entity, copy verbatim:
                    result.append(entity)
                }
            } else {
                // No matching ';'.
                break
            }
        }
        // Copy remaining characters to `result`:
        result.append(self[position ..< endIndex])
        return result
    }
}

extension String {
    static let htmlEscapedDictionary = [

            "&amp;": "&",
            "&quot;": "\"",
            "&#x27;": "'",
            "&#x39;": "'",
            "&#x92;": "'",
            "&#x96;": "'",
            "&gt;": ">",
            "&lt;": "<"]

    public var escapedHtmlString: String {
        var newString = "\(self)"

        for (key, value) in String.htmlEscapedDictionary {
            newString = newString.replaceAll(value, replacement: key)
        }
        return newString
    }

}
