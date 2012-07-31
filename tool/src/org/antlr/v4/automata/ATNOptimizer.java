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

package org.antlr.v4.automata;

import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.tool.Grammar;

import java.util.List;

/**
 *
 * @author Sam Harwell
 */
public class ATNOptimizer {

	public static void optimize(@NotNull Grammar g, @NotNull ATN atn) {
		optimizeStates(atn);
	}

	private static void optimizeStates(ATN atn) {
		List<ATNState> states = atn.states;

		int current = 0;
		for (int i = 0; i < states.size(); i++) {
			ATNState state = states.get(i);
			if (state == null) {
				continue;
			}

			if (i != current) {
				state.stateNumber = current;
				states.set(current, state);
				states.set(i, null);
			}

			current++;
		}

		System.out.println("ATN optimizer removed " + (states.size() - current) + " null states.");
		states.subList(current, states.size()).clear();
	}

	private ATNOptimizer() {
	}

}
