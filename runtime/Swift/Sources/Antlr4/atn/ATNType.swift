/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 



/// 
/// Represents the type of recognizer an ATN applies to.
/// 
/// -  Sam Harwell
/// 

public enum ATNType: Int {

    /// 
    /// A lexer grammar.
    /// 
    case lexer = 0

    /// 
    /// A parser grammar.
    /// 
    case parser

}
