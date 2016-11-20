//
//  IntStreamExtension.swift
//  Antlr.swift
//
//  Created by janyou on 15/9/3.
//  Copyright © 2015 jlabs. All rights reserved.
//

import Foundation

extension IntStream {

    /**
    * The value returned by {@link #LA LA()} when the end of the stream is
    * reached.
    */
    public static var EOF: Int {
        return -1
    }

    /**
    * The value returned by {@link #getSourceName} when the actual name of the
    * underlying source is not known.
    */
    public static var UNKNOWN_SOURCE_NAME: String {
        return "<unknown>"
    }

}