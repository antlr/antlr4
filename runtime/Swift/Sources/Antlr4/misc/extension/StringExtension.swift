/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 

import Foundation

//http://stackoverflow.com/questions/28182441/swift-how-to-get-substring-from-start-to-last-index-of-character
//https://github.com/williamFalcon/Bolt_Swift/blob/master/Bolt/BoltLibrary/String/String.swift

extension String {

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
        let range = self.range(of: target, options: .literal, range: startRange..<self.endIndex)

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

    subscript(integerIndex: Int) -> Character {
        let index = characters.index(startIndex, offsetBy: integerIndex)
        return self[index]
    }

    subscript(integerRange: Range<Int>) -> String {
        let start = characters.index(startIndex, offsetBy: integerRange.lowerBound)
        let end = characters.index(startIndex, offsetBy: integerRange.upperBound)
        let range = start ..< end
        return String(self[range])
    }
}

