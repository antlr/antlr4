/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


import Foundation

public class Utils {

    public static func escapeWhitespace(_ s: String, _ escapeSpaces: Bool) -> String {
        let buf: StringBuilder = StringBuilder()
        for c: Character in s.characters {
            if c == " " && escapeSpaces {
                buf.append("\u{00B7}")
            } else {
                if c == "\t" {
                    buf.append("\\t")
                } else {
                    if c == "\n" {
                        buf.append("\\n")
                    } else {
                        if c == "\r" {
                            buf.append("\\r")
                        } else {
                            buf.append(String(c))
                        }
                    }
                }
            }
        }
        return buf.toString()
    }


    public static func writeFile(_ fileName: String, _ content: String, _ encoding: String.Encoding = String.Encoding.utf8) {

        //writing
        do {
            try content.write(toFile: fileName, atomically: false, encoding: encoding)
        } catch {
            /* error handling here */
            RuntimeException(" write file fail \(error)")
        }

    }


    public static func readFile(_ path: String, _ encoding: String.Encoding = String.Encoding.utf8) -> [Character] {

        var fileContents: String

        do {
            fileContents = try String(contentsOfFile: path, encoding: encoding)
        } catch _ as NSError {
            return [Character]()
        }

        return Array(fileContents.characters)
    }

    public static func readFile2String(_ fileName: String, _ encoding: String.Encoding = String.Encoding.utf8) -> String {
        let path = Bundle.main.path(forResource: fileName, ofType: nil)
        if path == nil {
            return ""
        }

        var fileContents: String? = nil
        do {
            fileContents = try String(contentsOfFile: path!, encoding: encoding)
        } catch _ as NSError {
            return ""
        }



        return fileContents ?? ""
    }

    public static func readFile2StringByPath(_ path: String, _ encoding: String.Encoding = String.Encoding.utf8) -> String {

        //let path = fileName.stringByExpandingTildeInPath
        var fileContents: String? = nil
        do {
            fileContents = try NSString(contentsOfFile: path, encoding: String.Encoding.utf8.rawValue) as String          //try String(contentsOfFile: path!, encoding: encoding)
        } catch _ as NSError {
            return ""
        }

        return fileContents ?? ""
    }

    public static func toMap(_ keys: [String]) -> Dictionary<String, Int> {
        var m = Dictionary<String, Int>()
        for (index,v) in keys.enumerated() {
            m[v] = index
        }
        return m
    }

    public static func bitLeftShift(_ n: Int) -> Int64 {
       return (Int64(1) << Int64(n % 64))
    }


    public static func testBitLeftShiftArray(_ nArray: [Int],_ bitsShift: Int) -> Bool {
        let test: Bool = (((nArray[0] - bitsShift) & ~0x3f) == 0)

        var temp: Int64 =  Int64(nArray[0] - bitsShift)
        temp = (temp < 0) ? (64 + (temp % 64 )) : (temp % 64)
        let test1: Int64 = (Int64(1) << temp)

        var test2: Int64 = Utils.bitLeftShift(nArray[1] - bitsShift)

        for i in 1 ..< nArray.count {
            test2 = test2 |  Utils.bitLeftShift(nArray[i] - bitsShift)
        }
        return test && (( test1 & test2 ) != 0)
    }
}
