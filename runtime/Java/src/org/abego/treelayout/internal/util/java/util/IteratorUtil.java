/*
 * [The "BSD license"]
 * Copyright (c) 2011, abego Software GmbH, Germany (http://www.abego.org)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the abego Software GmbH nor the names of its 
 *    contributors may be used to endorse or promote products derived from this 
 *    software without specific prior written permission.
 *    
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.abego.treelayout.internal.util.java.util;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Util (general purpose) methods dealing with {@link Iterator}.
 * 
 * @author Udo Borkowski (ub@abego.org)
 * 
 * 
 */
public class IteratorUtil {

	private static class ReverseIterator<T> implements Iterator<T> {
		private ListIterator<T> listIterator;

		public ReverseIterator(List<T> list) {
			this.listIterator = list.listIterator(list.size());
		}

		@Override
		public boolean hasNext() {
			return listIterator.hasPrevious();
		}

		@Override
		public T next() {
			return listIterator.previous();
		}

		@Override
		public void remove() {
			listIterator.remove();
		}
	}

	/**
	 * Returns an {@link Iterator} iterating the given list from the end to the
	 * start.
	 * <p>
	 * I.e. the iterator does the reverse of the {@link List#iterator()}.
	 * 
	 * @param <T>
	 * @param list
	 * @return a reverse {@link Iterator} of the list
	 */
	public static <T> Iterator<T> createReverseIterator(List<T> list) {
		return new ReverseIterator<T>(list);
	}
}
