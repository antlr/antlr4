/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.dfa.DFAState;
import org.antlr.v4.runtime.misc.IntervalSet;

import java.util.IdentityHashMap;
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

	/** Must distinguish between missing edge and edge we know leads nowhere */

	public static final DFAState ERROR;

	public final ATN atn;

	/** The context cache maps all PredictionContext objects that are equals()
	 *  to a single cached copy. This cache is shared across all contexts
	 *  in all ATNConfigs in all DFA states.  We rebuild each ATNConfigSet
	 *  to use only cached nodes/graphs in addDFAState(). We don't want to
	 *  fill this during closure() since there are lots of contexts that
	 *  pop up but are not used ever again. It also greatly slows down closure().
	 *
	 *  <p>This cache makes a huge difference in memory and a little bit in speed.
	 *  For the Java grammar on java.*, it dropped the memory requirements
	 *  at the end from 25M to 16M. We don't store any of the full context
	 *  graphs in the DFA because they are limited to local context only,
	 *  but apparently there's a lot of repetition there as well. We optimize
	 *  the config contexts before storing the config set in the DFA states
	 *  by literally rebuilding them with cached subgraphs only.</p>
	 *
	 *  <p>I tried a cache for use during closure operations, that was
	 *  whacked after each adaptivePredict(). It cost a little bit
	 *  more time I think and doesn't save on the overall footprint
	 *  so it's not worth the complexity.</p>
 	 */
	protected final PredictionContextCache sharedContextCache;

	static {
		ERROR = new DFAState(new ATNConfigSet());
		ERROR.stateNumber = Integer.MAX_VALUE;
	}

	public ATNSimulator(ATN atn,
						PredictionContextCache sharedContextCache)
	{
		this.atn = atn;
		this.sharedContextCache = sharedContextCache;
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
		throw new UnsupportedOperationException("This ATN simulator does not support clearing the DFA.");
	}

	public PredictionContextCache getSharedContextCache() {
		return sharedContextCache;
	}

	public PredictionContext getCachedContext(PredictionContext context) {
		if ( sharedContextCache==null ) return context;

		synchronized (sharedContextCache) {
			IdentityHashMap<PredictionContext, PredictionContext> visited =
				new IdentityHashMap<PredictionContext, PredictionContext>();
			return PredictionContext.getCachedContext(context,
													  sharedContextCache,
													  visited);
		}
	}

	/**
	 * @deprecated Use {@link ATNDeserializer#deserialize} instead.
	 */
	@Deprecated
	public static ATN deserialize(char[] data) {
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

	public static Transition edgeFactory(ATN atn,
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

}
