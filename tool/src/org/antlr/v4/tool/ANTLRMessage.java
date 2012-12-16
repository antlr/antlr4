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

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import java.util.Arrays;

public class ANTLRMessage {
	private static final Object[] EMPTY_ARGS = new Object[0];

	@NotNull
    private final ErrorType errorType;
	@Nullable
    private final Object[] args;
	@Nullable
    private final Throwable e;

    // used for location template
    public String fileName;
    public int line = -1;
    public int charPosition = -1;

    public ANTLRMessage(@NotNull ErrorType errorType) {
        this(errorType, (Throwable)null);
    }

    public ANTLRMessage(@NotNull ErrorType errorType, Object... args) {
        this(errorType, null, args);
    }

    public ANTLRMessage(@NotNull ErrorType errorType, @Nullable Throwable e, Object... args) {
        this.errorType = errorType;
        this.e = e;
        this.args = args;
    }

	@NotNull
    public ErrorType getErrorType() {
        return errorType;
    }

	@NotNull
    public Object[] getArgs() {
		if (args == null) {
			return EMPTY_ARGS;
		}

		return args;
    }

	@Nullable
    public Throwable getCause() {
        return e;
    }

	@Override
	public String toString() {
		return "Message{" +
			   "errorType=" + getErrorType() +
			   ", args=" + Arrays.asList(getArgs()) +
			   ", e=" + getCause() +
			   ", fileName='" + fileName + '\'' +
			   ", line=" + line +
			   ", charPosition=" + charPosition +
			   '}';
	}
}
