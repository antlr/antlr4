/*
 * Copyright 2019 The ANTLR Project. All rights reserved.
 * Licensed under the BSD-3-Clause license. See LICENSE file in the project root for license information.
 */
package org.antlr.v4.runtime;

/**
 * Simple builder class for TokenChange
 */
public class TokenChangeBuilder {
	private TokenChangeType changeType;
	private CommonToken oldToken;
	private CommonToken newToken;

	public TokenChangeBuilder setChangeType(TokenChangeType changeType) {
		this.changeType = changeType;
		return this;
	}

	public TokenChangeBuilder setOldToken(CommonToken oldToken) {
		this.oldToken = oldToken;
		return this;
	}

	public TokenChangeBuilder setNewToken(CommonToken newToken) {
		this.newToken = newToken;
		return this;
	}

	public TokenChange createTokenChange() {
		return new TokenChange(changeType, oldToken, newToken);
	}
}
