/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
 *  Copyright (c) 2015 Janyou
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */



/**
 * Represents the serialization type of a {@link org.antlr.v4.runtime.atn.LexerAction}.
 *
 * @author Sam Harwell
 * @since 4.2
 */

public enum LexerActionType: Int {
    /**
     * The type of a {@link org.antlr.v4.runtime.atn.LexerChannelAction} action.
     */
    case channel = 0
    /**
     * The type of a {@link org.antlr.v4.runtime.atn.LexerCustomAction} action.
     */
    case custom
    /**
     * The type of a {@link org.antlr.v4.runtime.atn.LexerModeAction} action.
     */
    case mode
    /**
     * The type of a {@link org.antlr.v4.runtime.atn.LexerMoreAction} action.
     */
    case more
    /**
     * The type of a {@link org.antlr.v4.runtime.atn.LexerPopModeAction} action.
     */
    case popMode
    /**
     * The type of a {@link org.antlr.v4.runtime.atn.LexerPushModeAction} action.
     */
    case pushMode
    /**
     * The type of a {@link org.antlr.v4.runtime.atn.LexerSkipAction} action.
     */
    case skip
    /**
     * The type of a {@link org.antlr.v4.runtime.atn.LexerTypeAction} action.
     */
    case type
}
