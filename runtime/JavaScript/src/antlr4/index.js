/* Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import StringHashCode from './utils/stringHashCode.js';
import CodePointAt from './polyfills/codepointat.js';
import FromCodePoint from './polyfills/fromcodepoint.js';
import { default as atn } from './atn/index.js';
import { default as dfa } from './dfa/index.js';
import { default as context } from './context/index.js';
import { default as tree } from './tree/index.js';
import { default as error } from './error/index.js';
import Token from './Token.js';
import CommonToken from './CommonToken.js';
import { default as CharStreams } from './CharStreams.js';
import InputStream from './InputStream.js';
import CharStream from './InputStream.js';
import FileStream from './FileStream.js';
import CommonTokenStream from './CommonTokenStream.js';
import Lexer from './Lexer.js';
import Parser from './Parser.js';
import ParserRuleContext from './context/ParserRuleContext.js';
import Interval from './misc/Interval.js';
import IntervalSet from './misc/IntervalSet.js';
import LL1Analyzer from './atn/LL1Analyzer.js';
import { default as Utils } from './utils/index.js';

const antlr4 = {
    atn, dfa, context, tree, error, Token, CommonToken, CharStreams, CharStream, InputStream, FileStream, CommonTokenStream, Lexer, Parser,
    ParserRuleContext, Interval, IntervalSet, LL1Analyzer, Utils
};

export default antlr4;
