/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 

//
//  IntStreamExtension.swift
//  Antlr.swift
//
//  Created by janyou on 15/9/3.
//

import Foundation

extension IntStream {

    /// 
    /// The value returned by _#LA LA()_ when the end of the stream is
    /// reached.
    /// 
    public static var EOF: Int {
        return -1
    }

    /// 
    /// The value returned by _#getSourceName_ when the actual name of the
    /// underlying source is not known.
    /// 
    public static var UNKNOWN_SOURCE_NAME: String {
        return "<unknown>"
    }

}
