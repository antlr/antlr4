package org.antlr.v4.automata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** A FA (finite automata) walker that knows how to dump them to serialized
 *  strings.
 */
public class FASerializer {
	List<State> work;
	Set<State> marked;
	
	public String serialize(State s) {
		if ( s==null ) return null;
		work = new ArrayList<State>();
		marked = new HashSet<State>();
		work.add(s);
		
		while ( work.size()>0 ) {
			s = work.remove(work.size()-1); // pop
			System.out.println(s);
			marked.add(s);
			// add targets
			int n = s.getNumberOfTransitions();
			for (int i=0; i<n; i++) work.add( s.transition(i).target );
		}
		return "";
	}
}
