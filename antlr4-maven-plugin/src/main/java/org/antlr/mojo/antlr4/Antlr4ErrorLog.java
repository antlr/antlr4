/*
 [The "BSD license"]
 Copyright (c) 2012 Terence Parr
 Copyright (c) 2012 Sam Harwell
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.antlr.mojo.antlr4;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.tool.ANTLRMessage;
import org.antlr.v4.tool.ANTLRToolListener;
import org.apache.maven.plugin.logging.Log;

/**
 * This implementation of {@link ANTLRToolListener} reports messages to the
 * {@link Log} instance provided by Maven.
 *
 * @author Sam Harwell
 */
public class Antlr4ErrorLog implements ANTLRToolListener {

    private final Log log;

    /**
     * Creates an instance of {@link Antlr4ErrorLog}.
     *
     * @param log The Maven log
     */
    public Antlr4ErrorLog(@NotNull Log log) {
        this.log = log;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * This implementation passes the message to the Maven log.
     *
     * @param message The message to send to Maven
     */
    @Override
    public void info(String message) {
        log.info(message);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * This implementation passes the message to the Maven log.
     *
     * @param message The message to send to Maven.
     */
    @Override
    public void error(ANTLRMessage message) {
        log.error(message.toString());
    }

    /**
     * {@inheritDoc}
     * <p/>
     * This implementation passes the message to the Maven log.
     *
     * @param message
     */
    @Override
    public void warning(ANTLRMessage message) {
        log.warn(message.toString());
    }
}
