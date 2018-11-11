/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 

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
    private let type: LookupDictionaryType
    private var cache = [Int: ATNConfig]()

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

            return
                lhs.state.stateNumber == rhs.state.stateNumber &&
                    lhs.alt == rhs.alt &&
                    lhs.semanticContext == rhs.semanticContext
        }
        else {
            //Ordered
            return lhs == rhs
        }
    }

    public mutating func getOrAdd(_ config: ATNConfig) -> ATNConfig {
        let h = hash(config)

        if let configList = cache[h] {
            return configList
        }
        else {
            cache[h] = config
        }

        return config
    }

    public var isEmpty: Bool {
        return cache.isEmpty
    }

    public func contains(_ config: ATNConfig) -> Bool {
        let h = hash(config)
        return cache[h] != nil
    }

    public mutating func removeAll() {
        cache.removeAll()
    }

}




