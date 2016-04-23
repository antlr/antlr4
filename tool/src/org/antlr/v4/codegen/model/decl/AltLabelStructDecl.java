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

package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.model.DispatchMethod;
import org.antlr.v4.codegen.model.ListenerDispatchMethod;
import org.antlr.v4.codegen.model.VisitorDispatchMethod;
import org.antlr.v4.tool.Rule;

import java.util.ArrayList;

/** A StructDecl to handle a -&gt; label on alt */
public class AltLabelStructDecl extends StructDecl {
	public int altNum;
	public AltLabelStructDecl(OutputModelFactory factory, Rule r,
							  int altNum, String label)
	{
		super(factory, r);
		this.altNum = altNum;
		this.name = // override name set in super to the label ctx
			factory.getGenerator().getTarget().getAltLabelContextStructName(label);
		derivedFromName = label;
	}

	@Override
	public void addDispatchMethods(Rule r) {
		dispatchMethods = new ArrayList<DispatchMethod>();
		if ( factory.getGrammar().tool.gen_listener ) {
			dispatchMethods.add(new ListenerDispatchMethod(factory, true));
			dispatchMethods.add(new ListenerDispatchMethod(factory, false));
		}
		if ( factory.getGrammar().tool.gen_visitor ) {
			dispatchMethods.add(new VisitorDispatchMethod(factory));
		}
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if ( obj == this ) return true;
		if (!(obj instanceof AltLabelStructDecl)) return false;

		return name.equals(((AltLabelStructDecl)obj).name);
	}
}
