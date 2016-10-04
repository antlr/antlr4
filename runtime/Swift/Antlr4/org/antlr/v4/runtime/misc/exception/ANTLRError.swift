//
//  ANTLRError.swift
//  antlr.swift
//
//  Created by janyou on 15/9/4.
//  Copyright © 2015 jlabs. All rights reserved.
//

import Foundation

public enum ANTLRError: Error {
    case nullPointer(msg:String)
    case unsupportedOperation(msg:String)
    case indexOutOfBounds(msg:String)
    case illegalState(msg:String)
    case illegalArgument(msg:String)
    case negativeArraySize(msg:String)
    case parseCancellation
}
