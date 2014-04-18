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

import org.antlr.v4.runtime.dfa.DFAState;
import org.antlr.v4.runtime.dfa.EmptyEdgeMap;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;
import java.util.UUID;

public abstract class ATNSimulator {
	/**
	 * @deprecated Use {@link ATNDeserializer#SERIALIZED_VERSION} instead.
	 */
	@Deprecated
	public static final int SERIALIZED_VERSION;
	static {
		SERIALIZED_VERSION = ATNDeserializer.SERIALIZED_VERSION;
	}

	/**
	 * This is the current serialized UUID.
	 * @deprecated Use {@link ATNDeserializer#checkCondition(boolean)} instead.
	 */
	@Deprecated
	public static final UUID SERIALIZED_UUID;
	static {
		SERIALIZED_UUID = ATNDeserializer.SERIALIZED_UUID;
	}

	public static final char RULE_VARIANT_DELIMITER = '$';
	public static final String RULE_LF_VARIANT_MARKER =  "$lf$";
	public static final String RULE_NOLF_VARIANT_MARKER = "$nolf$";

	/** Must distinguish between missing edge and edge we know leads nowhere */
	@NotNull
	public static final DFAState ERROR;
	@NotNull
	public final ATN atn;

	static {
		ERROR = new DFAState(new EmptyEdgeMap<DFAState>(0, -1), new EmptyEdgeMap<DFAState>(0, -1), new ATNConfigSet());
		ERROR.stateNumber = Integer.MAX_VALUE;
	}

	public ATNSimulator(@NotNull ATN atn) {
		this.atn = atn;
	}

	public abstract void reset();

	/**
	 * Clear the DFA cache used by the current instance. Since the DFA cache may
	 * be shared by multiple ATN simulators, this method may affect the
	 * performance (but not accuracy) of other parsers which are being used
	 * concurrently.
	 *
	 * @throws UnsupportedOperationException if the current instance does not
	 * support clearing the DFA.
	 *
	 * @since 4.3
	 */
	public void clearDFA() {
		atn.clearDFA();
	}

	/**
	 * @deprecated Use {@link ATNDeserializer#deserialize} instead.
	 */
	@Deprecated
	public static ATN deserialize(@NotNull char[] data) {
		return new ATNDeserializer().deserialize(data);
	}

	/**
	 * @deprecated Use {@link ATNDeserializer#checkCondition(boolean)} instead.
	 */
	@Deprecated
	public static void checkCondition(boolean condition) {
		new ATNDeserializer().checkCondition(condition);
	}

	/**
	 * @deprecated Use {@link ATNDeserializer#checkCondition(boolean, String)} instead.
	 */
	@Deprecated
	public static void checkCondition(boolean condition, String message) {
		new ATNDeserializer().checkCondition(condition, message);
	}

	/**
	 * @deprecated Use {@link ATNDeserializer#toInt} instead.
	 */
	@Deprecated
	public static int toInt(char c) {
		return ATNDeserializer.toInt(c);
	}

	/**
	 * @deprecated Use {@link ATNDeserializer#toInt32} instead.
	 */
	@Deprecated
	public static int toInt32(char[] data, int offset) {
		return ATNDeserializer.toInt32(data, offset);
	}

	/**
	 * @deprecated Use {@link ATNDeserializer#toLong} instead.
	 */
	@Deprecated
	public static long toLong(char[] data, int offset) {
		return ATNDeserializer.toLong(data, offset);
	}

	/**
	 * @deprecated Use {@link ATNDeserializer#toUUID} instead.
	 */
	@Deprecated
	public static UUID toUUID(char[] data, int offset) {
		return ATNDeserializer.toUUID(data, offset);
	}

	/**
	 * @deprecated Use {@link ATNDeserializer#edgeFactory} instead.
	 */
	@Deprecated
	@NotNull
	public static Transition edgeFactory(@NotNull ATN atn,
										 int type, int src, int trg,
										 int arg1, int arg2, int arg3,
										 List<IntervalSet> sets)
	{
		return new ATNDeserializer().edgeFactory(atn, type, src, trg, arg1, arg2, arg3, sets);
	}

	/**
	 * @deprecated Use {@link ATNDeserializer#stateFactory} instead.
	 */
	@Deprecated
	public static ATNState stateFactory(int type, int ruleIndex) {
		return new ATNDeserializer().stateFactory(type, ruleIndex);
	}

/*
	public static void dump(DFA dfa, Grammar g) {
		DOTGenerator dot = new DOTGenerator(g);
		String output = dot.getDOT(dfa, false);
		System.out.println(output);
	}

	public static void dump(DFA dfa) {
		dump(dfa, null);
	}
	 */
}
