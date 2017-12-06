/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 

//
//  ANTLRError.swift
//  antlr.swift
//
//  Created by janyou on 15/9/4.
//

import Foundation

public enum ANTLRError: Error {
    case unsupportedOperation(msg:String)
    case indexOutOfBounds(msg:String)
    case illegalState(msg:String)
    case illegalArgument(msg:String)
    case negativeArraySize(msg:String)
}
