/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.ATNType;
import org.antlr.v4.runtime.atn.AtomTransition;
import org.antlr.v4.runtime.atn.BasicState;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * {@link ATN#nextTokens(ATNState)} caches the lookahead set of a state in the
 * ATN, which every recognizer instance of a grammar shares. Parsers running on
 * different threads therefore race to fill the cache the first time each state
 * is reached, and {@code DefaultErrorStrategy.sync} acts on the answer at
 * once. A thread that is handed a set whose elements it cannot see yet reports
 * a syntax error on valid input.
 *
 * <p>The failure needs a processor that does not keep stores in order
 * (aarch64, for example). On x86 this test passes with or without safe
 * publication of the cached set.</p>
 */
public class TestATNNextTokensConcurrency {
	private static final int FIRST_TOKEN = 1;
	private static final int LAST_TOKEN = 3;

	private volatile int trial;

	@Test
	@Timeout(120)
	public void testConcurrentFirstUseSeesCompleteSet() throws Exception {
		int processors = Runtime.getRuntime().availableProcessors();
		assumeTrue(processors >= 3, "needs a spare processor for every spinning thread");
		final int threads = Math.min(8, processors - 1);
		final int trials = 300000;
		final long deadline = System.nanoTime() + 20L * 1000 * 1000 * 1000;

		// One state with two outgoing token transitions: nextTokens(s) is {1, 3}.
		final ATN atn = new ATN(ATNType.PARSER, LAST_TOKEN);
		final BasicState s = new BasicState();
		BasicState target = new BasicState();
		atn.addState(s);
		atn.addState(target);
		s.addTransition(new AtomTransition(target, FIRST_TOKEN));
		s.addTransition(new AtomTransition(target, LAST_TOKEN));

		final AtomicInteger done = new AtomicInteger();
		final AtomicLong incomplete = new AtomicLong();
		final AtomicReference<Throwable> thrown = new AtomicReference<Throwable>();
		final AtomicReferenceArray<long[]> shared = new AtomicReferenceArray<long[]>(threads);
		final AtomicLong sink = new AtomicLong();

		Thread[] workers = new Thread[threads];
		for (int i = 0; i < threads; i++) {
			final int index = i;
			workers[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					int seen = 0;
					while (true) {
						while (trial == seen) {
							Thread.yield();
						}
						seen = trial;
						if (seen < 0) {
							return;
						}
						try {
							// Publish an object allocated just before the lookahead set and
							// read the other threads' objects for a moment, staggering the
							// threads. The set is then built on a cache line that other
							// processors are reading, as it is in a parser whose threads
							// are also filling the shared DFA.
							long[] mine = new long[1];
							shared.set(index, mine);
							long sum = 0;
							for (int r = 0; r < index; r++) {
								for (int k = 0; k < threads; k++) {
									long[] other = shared.get(k);
									if (other != null) {
										sum += other[0];
									}
								}
							}
							sink.lazySet(sum);

							IntervalSet next = atn.nextTokens(s);
							if (!next.contains(LAST_TOKEN) || !next.contains(FIRST_TOKEN) || next.size() != 2) {
								incomplete.incrementAndGet();
							}
						}
						catch (Throwable t) {
							thrown.compareAndSet(null, t);
						}
						finally {
							done.incrementAndGet();
						}
					}
				}
			});
			workers[i].setDaemon(true);
			workers[i].start();
		}

		int completed = 0;
		for (int t = 1; t <= trials && System.nanoTime() < deadline; t++) {
			// Back to the state of a grammar that has not been used yet.
			s.nextTokenWithinRule = null;
			done.set(0);
			trial = t;
			while (done.get() < threads) {
				Thread.yield();
			}
			completed = t;
		}
		trial = -1;
		for (Thread worker : workers) {
			worker.join();
		}

		assertNull(thrown.get(), "nextTokens or the set it returned threw");
		assertEquals(0, incomplete.get(),
			"nextTokens returned a set that did not contain both tokens, in " + completed + " trials");
	}
}
