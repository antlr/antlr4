//
//  OrderedATNConfig.swift
//  objc2swiftwithswith
//
//  Created by janyou on 15/9/14.
//  Copyright © 2015 jlabs. All rights reserved.
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