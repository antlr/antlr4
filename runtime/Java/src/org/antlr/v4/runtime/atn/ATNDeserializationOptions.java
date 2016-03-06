/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
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

package org.antlr.v4.runtime.atn;

/**
 *
 * @author Sam Harwell
 */
public class ATNDeserializationOptions {
	private static final ATNDeserializationOptions defaultOptions;
	static {
		defaultOptions = new ATNDeserializationOptions();
		defaultOptions.makeReadOnly();
	}

	private boolean readOnly;
	private boolean verifyATN;
	private boolean generateRuleBypassTransitions;

	public ATNDeserializationOptions() {
		this.verifyATN = true;
		this.generateRuleBypassTransitions = false;
	}

	public ATNDeserializationOptions(ATNDeserializationOptions options) {
		this.verifyATN = options.verifyATN;
		this.generateRuleBypassTransitions = options.generateRuleBypassTransitions;
	}


	public static ATNDeserializationOptions getDefaultOptions() {
		return defaultOptions;
	}

	public final boolean isReadOnly() {
		return readOnly;
	}

	public final void makeReadOnly() {
		readOnly = true;
	}

	public final boolean isVerifyATN() {
		return verifyATN;
	}

	public final void setVerifyATN(boolean verifyATN) {
		throwIfReadOnly();
		this.verifyATN = verifyATN;
	}

	public final boolean isGenerateRuleBypassTransitions() {
		return generateRuleBypassTransitions;
	}

	public final void setGenerateRuleBypassTransitions(boolean generateRuleBypassTransitions) {
		throwIfReadOnly();
		this.generateRuleBypassTransitions = generateRuleBypassTransitions;
	}

	protected void throwIfReadOnly() {
		if (isReadOnly()) {
			throw new IllegalStateException("The object is read only.");
		}
	}
}
