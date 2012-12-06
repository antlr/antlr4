/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
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
package org.antlr.v4.tool;

/**
 * Abstracts away the definition of Message severity and the text that should
 * display to represent that severity if there is no StringTemplate available
 * to do it.
 *
 * @author Jim Idle - Temporal Wave LLC (jimi@temporal-wave.com)
 */
public enum ErrorSeverity {
    INFO    ("info"),
	WARNING ("warning"),
	WARNING_ONE_OFF ("warning"),
	ERROR   ("error"),
	ERROR_ONE_OFF   ("error"),
    FATAL   ("fatal"),  // TODO: add fatal for which phase? sync with ErrorManager
    ;

    /**
     * The text version of the ENUM value, used for display purposes
     */
    private final String text;

    /**
     * Standard getter method for the text that should be displayed in order to
     * represent the severity to humans and product modelers.
     *
     * @return The human readable string representing the severity level
     */
    public String getText() { return text; }

    /**
     * Standard constructor to build an instance of the Enum entries
     *
     * @param text The human readable string representing the serverity level
     */
    private ErrorSeverity(String text) { this.text = text; }
}

