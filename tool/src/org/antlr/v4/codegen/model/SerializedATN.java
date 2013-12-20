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

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNSerializer;
import org.antlr.v4.runtime.misc.IntegerList;

import java.util.ArrayList;
import java.util.List;

public class SerializedATN extends OutputModelObject {
	// TODO: make this into a kind of decl or multiple?
	public List<String> serialized;
	public SerializedATN(OutputModelFactory factory, ATN atn) {
		super(factory);
		IntegerList data = ATNSerializer.getSerialized(atn);
		serialized = new ArrayList<String>(data.size());
		for (int c : data.toArray()) {
			String encoded = factory.getGenerator().getTarget().encodeIntAsCharEscape(c == -1 ? Character.MAX_VALUE : c);
			serialized.add(encoded);
		}
//		System.out.println(ATNSerializer.getDecoded(factory.getGrammar(), atn));
	}

	public String[][] getSegments() {
		List<String[]> segments = new ArrayList<String[]>();
		int segmentLimit = factory.getGenerator().getTarget().getSerializedATNSegmentLimit();
		for (int i = 0; i < serialized.size(); i += segmentLimit) {
			List<String> currentSegment = serialized.subList(i, Math.min(i + segmentLimit, serialized.size()));
			segments.add(currentSegment.toArray(new String[currentSegment.size()]));
		}

		return segments.toArray(new String[segments.size()][]);
	}
}
