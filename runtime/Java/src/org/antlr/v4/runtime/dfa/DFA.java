/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
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
package org.antlr.v4.runtime.dfa;

import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class DFA {
	/** A set of all DFA states. Use Map so we can get old state back
	 *  (Set only allows you to see if it's there).
     */
    @NotNull
	public final Map<DFAState, DFAState> states = new LinkedHashMap<DFAState, DFAState>();
	@Nullable
	public DFAState s0;
	public int decision;

	/** From which ATN state did we create this DFA? */
	@NotNull
	public final ATNState atnStartState;

	/** Set of configs for a DFA state with at least one conflict? Mainly used as "return value"
	 *  from predictATN() for retry.
	 */
//	public OrderedHashSet<ATNConfig> conflictSet;

	public DFA(@NotNull ATNState atnStartState) { this.atnStartState = atnStartState; }

	@Override
	public String toString() { return toString(null); }

	public String toString(@Nullable String[] tokenNames) {
		if ( s0==null ) return "";
		DFASerializer serializer = new DFASerializer(this,tokenNames);
		return serializer.toString();
	}

	public String toLexerString() {
		if ( s0==null ) return "";
		DFASerializer serializer = new LexerDFASerializer(this);
		return serializer.toString();
	}

}
