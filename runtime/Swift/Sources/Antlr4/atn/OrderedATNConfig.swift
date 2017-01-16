/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.

//
//  OrderedATNConfig.swift
//  objc2swiftwithswith
//
//  Created by janyou on 15/9/14.
//

import Foundation

public class OrderedATNConfig: LookupATNConfig {
    override
    public var hashValue: Int {
        return config.hashValue
    }


}

//useless
public func ==(lhs: OrderedATNConfig, rhs: OrderedATNConfig) -> Bool {

    if lhs.config === rhs.config {
        return true
    }

    return lhs.config == rhs.config

}
