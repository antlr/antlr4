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

package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.model.*;
import org.antlr.v4.runtime.misc.OrderedHashSet;
import org.antlr.v4.tool.*;

import java.util.*;

/** This object models the structure holding all of the parameters,
 *  return values, local variables, and labels associated with a rule.
 */
public class StructDecl extends Decl {
	public String superClass;
	public boolean provideCopyFrom;
	@ModelElement public OrderedHashSet<Decl> attrs = new OrderedHashSet<Decl>();
	@ModelElement public Collection<Attribute> ctorAttrs;
	@ModelElement public List<VisitorDispatchMethod> visitorDispatchMethods;
	@ModelElement public List<OutputModelObject> interfaces;
	@ModelElement public List<OutputModelObject> extensionMembers;

	public StructDecl(OutputModelFactory factory, Rule r) {
		super(factory, factory.getGenerator().target.getRuleFunctionContextStructName(r));
		addVisitorDispatchMethods(r);
		provideCopyFrom = r.hasAltSpecificContexts();
	}

	public void addVisitorDispatchMethods(Rule r) {
		visitorDispatchMethods = new ArrayList<VisitorDispatchMethod>();
		visitorDispatchMethods.add(new VisitorDispatchMethod(factory, r, true));
		visitorDispatchMethods.add(new VisitorDispatchMethod(factory, r, false));
	}

	public void addDecl(Decl d) { attrs.add(d); d.ctx = this; }

	public void addDecl(Attribute a) {
		addDecl(new AttributeDecl(factory, a.name, a.decl));
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

	public boolean isEmpty() { return attrs.size()==0; }
}
