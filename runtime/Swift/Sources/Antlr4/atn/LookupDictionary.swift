/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.

//
//  LookupDictionary.swift
//   antlr.swift
//
//  Created by janyou on 15/9/23.
//

import Foundation

public enum LookupDictionaryType: Int {
    case lookup = 0
    case ordered
}

public struct LookupDictionary {
    private var type: LookupDictionaryType
//    private var cache: HashMap<Int, [ATNConfig]> = HashMap<Int, [ATNConfig]>()
//
    private var cache: HashMap<Int, ATNConfig> = HashMap<Int, ATNConfig>()
    public init(type: LookupDictionaryType = LookupDictionaryType.lookup) {
        self.type = type
    }

    private func hash(_ config: ATNConfig) -> Int {
        if type == LookupDictionaryType.lookup {

            var hashCode: Int = 7
            hashCode = 31 * hashCode + config.state.stateNumber
            hashCode = 31 * hashCode + config.alt
            hashCode = 31 * hashCode + config.semanticContext.hashValue
            return hashCode

        } else {
            //Ordered
            return config.hashValue
        }
    }

    private func equal(_ lhs: ATNConfig, _ rhs: ATNConfig) -> Bool {
        if type == LookupDictionaryType.lookup {
            if lhs === rhs {
                return true
            }


            let same: Bool =
            lhs.state.stateNumber == rhs.state.stateNumber &&
                    lhs.alt == rhs.alt &&
                    lhs.semanticContext == rhs.semanticContext

            return same

        } else {
            //Ordered
            return lhs == rhs
        }
    }

//    public mutating func getOrAdd(config: ATNConfig) -> ATNConfig {
//
//        let h = hash(config)
//
//        if let configList = cache[h] {
//            let length = configList.count
//            for i in 0..<length {
//                if equal(configList[i], config) {
//                    return configList[i]
//                }
//            }
//            cache[h]!.append(config)
//        } else {
//            cache[h] = [config]
//        }
//
//        return config
//
//    }
        public mutating func getOrAdd(_ config: ATNConfig) -> ATNConfig {

            let h = hash(config)

            if let configList = cache[h] {
                return configList
            } else {
                cache[h] = config
            }

            return config

        }
    public var isEmpty: Bool {
        return cache.isEmpty
    }

//    public func contains(config: ATNConfig) -> Bool {
//
//        let h = hash(config)
//        if let configList = cache[h] {
//            for c in configList {
//                if equal(c, config) {
//                    return true
//                }
//            }
//        }
//
//        return false
//
//    }
    public func contains(_ config: ATNConfig) -> Bool {

        let h = hash(config)
        if let _ = cache[h] {
            return true
        }

        return false

    }
    public mutating func removeAll() {
        cache.clear()
    }

}




