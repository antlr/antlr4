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
package org.antlr.v4.runtime.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import org.antlr.v4.runtime.RuleDependencies;
import org.antlr.v4.runtime.RuleDependency;
import org.antlr.v4.runtime.RuleVersion;

import java.lang.annotation.AnnotationTypeMismatchException;

/**
 *
 * @author Sam Harwell
 */
@SupportedAnnotationTypes({"org.antlr.v4.runtime.RuleDependency", "org.antlr.v4.runtime.RuleDependencies"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class RuleDependencyProcessor extends AbstractProcessor {

	public RuleDependencyProcessor() {
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		List<Tuple2<RuleDependency, Element>> dependencies = getDependencies(roundEnv);
		Map<TypeMirror, List<Tuple2<RuleDependency, Element>>> recognizerDependencies
			= new HashMap<TypeMirror, List<Tuple2<RuleDependency, Element>>>();
		for (Tuple2<RuleDependency, Element> dependency : dependencies) {
			TypeMirror recognizerType = getRecognizerType(dependency.getItem1());
			List<Tuple2<RuleDependency, Element>> list = recognizerDependencies.get(recognizerType);
			if (list == null) {
				list = new ArrayList<Tuple2<RuleDependency, Element>>();
				recognizerDependencies.put(recognizerType, list);
			}

			list.add(dependency);
		}

		for (Map.Entry<TypeMirror, List<Tuple2<RuleDependency, Element>>> entry : recognizerDependencies.entrySet()) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format("ANTLR 4: Validating %d dependencies on rules in %s.", entry.getValue().size(), entry.getKey().toString()));
			checkDependencies(entry.getValue(), entry.getKey());
		}

		return true;
	}

	private static TypeMirror getRecognizerType(RuleDependency dependency) {
		try {
			dependency.recognizer();
			throw new UnsupportedOperationException("Expected MirroredTypeException to get the TypeMirror.");
		}
		catch (MirroredTypeException ex) {
			return ex.getTypeMirror();
		}
	}

	private void checkDependencies(List<Tuple2<RuleDependency, Element>> dependencies, TypeMirror recognizerType) {
		String[] ruleNames = getRuleNames(recognizerType);
		int[] ruleVersions = getRuleVersions(recognizerType, ruleNames);

		for (Tuple2<RuleDependency, Element> dependency : dependencies) {
			try {
				if (!processingEnv.getTypeUtils().isAssignable(getRecognizerType(dependency.getItem1()), recognizerType)) {
					continue;
				}

				if (dependency.getItem1().rule() < 0 || dependency.getItem1().rule() >= ruleVersions.length) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format("Element %s dependent on unknown rule %d@%d in %s\n",
													  dependency.getItem2().toString(),
													  dependency.getItem1().rule(),
													  dependency.getItem1().version(),
													  getRecognizerType(dependency.getItem1()).toString()),
													  dependency.getItem2());
				}
				else if (ruleVersions[dependency.getItem1().rule()] != dependency.getItem1().version()) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format("Element %s dependent on rule %s@%d (found @%d) in %s\n",
													  dependency.getItem2().toString(),
													  ruleNames[dependency.getItem1().rule()],
													  dependency.getItem1().version(),
													  ruleVersions[dependency.getItem1().rule()],
													  getRecognizerType(dependency.getItem1()).toString()),
													  dependency.getItem2());
				}
			}
			catch (AnnotationTypeMismatchException ex) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, String.format("Could not validate rule dependencies for element %s\n", dependency.getItem2().toString()),
														 dependency.getItem2());
			}
		}
	}

	private int[] getRuleVersions(TypeMirror recognizerClass, String[] ruleNames) {
		int[] versions = new int[ruleNames.length];

		List<? extends Element> elements = processingEnv.getElementUtils().getAllMembers((TypeElement)processingEnv.getTypeUtils().asElement(recognizerClass));
		for (Element element : elements) {
			if (element.getKind() != ElementKind.FIELD) {
				continue;
			}

			VariableElement field = (VariableElement)element;
			boolean isStatic = element.getModifiers().contains(Modifier.STATIC);
			Object constantValue = field.getConstantValue();
			boolean isInteger = constantValue instanceof Integer;
			String name = field.getSimpleName().toString();
			if (isStatic && isInteger && name.startsWith("RULE_")) {
				try {
					name = name.substring("RULE_".length());
					if (name.isEmpty() || !Character.isLowerCase(name.charAt(0))) {
						continue;
					}

					int index = (Integer)constantValue;
					if (index < 0 || index >= versions.length) {
						String message = String.format("Rule index %d for rule '%s' out of bounds for recognizer %s.", index, name, recognizerClass.toString());
						processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
						continue;
					}

					ExecutableElement ruleMethod = getRuleMethod(recognizerClass, name);
					if (ruleMethod == null) {
						String message = String.format("Could not find rule method for rule '%s' in recognizer %s.", name, recognizerClass.toString());
						processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
						continue;
					}

					RuleVersion ruleVersion = ruleMethod.getAnnotation(RuleVersion.class);
					int version = ruleVersion != null ? ruleVersion.value() : 0;
					versions[index] = version;
				} catch (IllegalArgumentException ex) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Exception occurred while validating rule dependencies.", element);
				}
			}
		}

		return versions;
	}

	private ExecutableElement getRuleMethod(TypeMirror recognizerClass, String name) {
		List<? extends Element> elements = processingEnv.getElementUtils().getAllMembers((TypeElement)processingEnv.getTypeUtils().asElement(recognizerClass));
		for (Element element : elements) {
			if (element.getKind() != ElementKind.METHOD) {
				continue;
			}

			ExecutableElement method = (ExecutableElement)element;
			if (method.getSimpleName().contentEquals(name) && hasRuleVersionAnnotation(method)) {
				return method;
			}
		}

		return null;
	}

	private boolean hasRuleVersionAnnotation(ExecutableElement method) {
		TypeElement ruleVersionAnnotationElement = processingEnv.getElementUtils().getTypeElement("org.antlr.v4.runtime.RuleVersion");
		if (ruleVersionAnnotationElement == null) {
			return false;
		}

		for (AnnotationMirror annotation : method.getAnnotationMirrors()) {
			if (processingEnv.getTypeUtils().isSameType(annotation.getAnnotationType(), ruleVersionAnnotationElement.asType())) {
				return true;
			}
		}

		return false;
	}

	private String[] getRuleNames(TypeMirror recognizerClass) {
		List<String> result = new ArrayList<String>();

		List<? extends Element> elements = processingEnv.getElementUtils().getAllMembers((TypeElement)processingEnv.getTypeUtils().asElement(recognizerClass));
		for (Element element : elements) {
			if (element.getKind() != ElementKind.FIELD) {
				continue;
			}

			VariableElement field = (VariableElement)element;
			boolean isStatic = element.getModifiers().contains(Modifier.STATIC);
			Object constantValue = field.getConstantValue();
			boolean isInteger = constantValue instanceof Integer;
			String name = field.getSimpleName().toString();
			if (isStatic && isInteger && name.startsWith("RULE_")) {
				try {
					name = name.substring("RULE_".length());
					if (name.isEmpty() || !Character.isLowerCase(name.charAt(0))) {
						continue;
					}

					int index = (Integer)constantValue;
					if (index < 0) {
						continue;
					}

					while (result.size() <= index) {
						result.add("");
					}

					result.set(index, name);
				} catch (IllegalArgumentException ex) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Exception occurred while validating rule dependencies.", element);
				}
			}
		}

		return result.toArray(new String[result.size()]);
	}

	public static List<Tuple2<RuleDependency, Element>> getDependencies(RoundEnvironment roundEnv) {
		List<Tuple2<RuleDependency, Element>> result = new ArrayList<Tuple2<RuleDependency, Element>>();
		Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(RuleDependency.class);
		for (Element element : elements) {
			RuleDependency dependency = element.getAnnotation(RuleDependency.class);
			if (dependency == null) {
				continue;
			}

			result.add(Tuple.create(dependency, element));
		}

		elements = roundEnv.getElementsAnnotatedWith(RuleDependencies.class);
		for (Element element : elements) {
			RuleDependencies dependencies = element.getAnnotation(RuleDependencies.class);
			if (dependencies == null || dependencies.value() == null) {
				continue;
			}

			for (RuleDependency dependency : dependencies.value()) {
				result.add(Tuple.create(dependency, element));
			}
		}

		return result;
	}

}
