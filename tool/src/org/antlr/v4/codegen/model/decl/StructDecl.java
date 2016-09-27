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
import org.antlr.v4.codegen.model.ModelElement;
import org.antlr.v4.codegen.model.OutputModelObject;
import org.antlr.v4.codegen.model.VisitorDispatchMethod;
import org.antlr.v4.runtime.misc.OrderedHashSet;
import org.antlr.v4.tool.Attribute;
import org.antlr.v4.tool.Rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** This object models the structure holding all of the parameters,
 *  return values, local variables, and labels associated with a rule.
 */
public class StructDecl extends Decl {
	public String derivedFromName; // rule name or label name
	public boolean provideCopyFrom;
	@ModelElement public OrderedHashSet<Decl> attrs = new OrderedHashSet<Decl>();
	@ModelElement public OrderedHashSet<Decl> getters = new OrderedHashSet<Decl>();
	@ModelElement public Collection<AttributeDecl> ctorAttrs;
	@ModelElement public List<? super DispatchMethod> dispatchMethods;
	@ModelElement public List<OutputModelObject> interfaces;
	@ModelElement public List<OutputModelObject> extensionMembers;

	public StructDecl(OutputModelFactory factory, Rule r) {
		super(factory, factory.getGenerator().getTarget().getRuleFunctionContextStructName(r));
		addDispatchMethods(r);
		derivedFromName = r.name;
		provideCopyFrom = r.hasAltSpecificContexts();
	}

	public void addDispatchMethods(Rule r) {
		dispatchMethods = new ArrayList<DispatchMethod>();
		if ( !r.hasAltSpecificContexts() ) {
			// no enter/exit for this ruleContext if rule has labels
			if ( factory.getGrammar().tool.gen_listener ) {
				dispatchMethods.add(new ListenerDispatchMethod(factory, true));
				dispatchMethods.add(new ListenerDispatchMethod(factory, false));
			}
			if ( factory.getGrammar().tool.gen_visitor ) {
				dispatchMethods.add(new VisitorDispatchMethod(factory));
			}
		}
	}

	public void addDecl(Decl d) {
		d.ctx = this;
		if ( d instanceof ContextGetterDecl ) getters.add(d);
		else attrs.add(d);
	}

	public void addDecl(Attribute a) {
		addDecl(new AttributeDecl(factory, a));
	}

	public void addDecls(Collection<Attribute> attrList) {
		for (Attribute a : attrList) addDecl(a);
	}

	public void implementInterface(OutputModelObject value) {
		if (interfaces == null) {
			interfaces = new ArrayList<OutputModelObject>();
		}

		interfaces.add(value);
	}

	public void addExtensionMember(OutputModelObject member) {
		if (extensionMembers == null) {
			extensionMembers = new ArrayList<OutputModelObject>();
		}

		extensionMembers.add(member);
	}

	public boolean isEmpty() { return attrs.isEmpty(); }
}
