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
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import org.antlr.v4.runtime.Dependents;
import org.antlr.v4.runtime.RuleDependencies;
import org.antlr.v4.runtime.RuleDependency;
import org.antlr.v4.runtime.RuleVersion;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNSimulator;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.RuleTransition;
import org.antlr.v4.runtime.atn.Transition;

import java.lang.annotation.AnnotationTypeMismatchException;
import java.util.BitSet;
import java.util.EnumSet;

/**
 * A compile-time validator for rule dependencies.
 *
 * @see RuleDependency
 * @see RuleDependencies
 * @author Sam Harwell
 */
@SupportedAnnotationTypes({RuleDependencyProcessor.RuleDependencyClassName, RuleDependencyProcessor.RuleDependenciesClassName, RuleDependencyProcessor.RuleVersionClassName})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class RuleDependencyProcessor extends AbstractProcessor {
	public static final String RuleDependencyClassName = "org.antlr.v4.runtime.RuleDependency";
	public static final String RuleDependenciesClassName = "org.antlr.v4.runtime.RuleDependencies";
	public static final String RuleVersionClassName = "org.antlr.v4.runtime.RuleVersion";

	public RuleDependencyProcessor() {
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (!checkClassNameConstants()) {
			return true;
		}

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

	private boolean checkClassNameConstants() {
		boolean success = checkClassNameConstant(RuleDependencyClassName, RuleDependency.class);
		success &= checkClassNameConstant(RuleDependenciesClassName, RuleDependencies.class);
		success &= checkClassNameConstant(RuleVersionClassName, RuleVersion.class);
		return success;
	}

	private boolean checkClassNameConstant(String className, Class<?> clazz) {
		Args.notNull("className", className);
		Args.notNull("clazz", clazz);
		if (!className.equals(clazz.getCanonicalName())) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format("Unable to process rule dependencies due to class name mismatch: %s != %s", className, clazz.getCanonicalName()));
			return false;
		}

		return true;
	}

	private static TypeMirror getRecognizerType(RuleDependency dependency) {
		try {
			dependency.recognizer();
			String message = String.format("Expected %s to get the %s.", MirroredTypeException.class.getSimpleName(), TypeMirror.class.getSimpleName());
			throw new UnsupportedOperationException(message);
		}
		catch (MirroredTypeException ex) {
			return ex.getTypeMirror();
		}
	}

	private void checkDependencies(List<Tuple2<RuleDependency, Element>> dependencies, TypeMirror recognizerType) {
		String[] ruleNames = getRuleNames(recognizerType);
		int[] ruleVersions = getRuleVersions(recognizerType, ruleNames);
		RuleRelations relations = extractRuleRelations(recognizerType);

		for (Tuple2<RuleDependency, Element> dependency : dependencies) {
			try {
				if (!processingEnv.getTypeUtils().isAssignable(getRecognizerType(dependency.getItem1()), recognizerType)) {
					continue;
				}

				// this is the rule in the dependency set with the highest version number
				int effectiveRule = dependency.getItem1().rule();
				if (effectiveRule < 0 || effectiveRule >= ruleVersions.length) {
					Tuple2<AnnotationMirror, AnnotationValue> ruleReferenceElement = findRuleDependencyProperty(dependency, RuleDependencyProperty.RULE);
					String message = String.format("Rule dependency on unknown rule %d@%d in %s",
												   dependency.getItem1().rule(),
												   dependency.getItem1().version(),
												   getRecognizerType(dependency.getItem1()).toString());

					if (ruleReferenceElement != null) {
						processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message,
													  dependency.getItem2(), ruleReferenceElement.getItem1(), ruleReferenceElement.getItem2());
					}
					else {
						processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message,
													  dependency.getItem2());
					}

					continue;
				}

				EnumSet<Dependents> dependents = EnumSet.of(Dependents.SELF, dependency.getItem1().dependents());
				reportUnimplementedDependents(dependency, dependents);

				BitSet checked = new BitSet();

				int highestRequiredDependency = checkDependencyVersion(dependency, ruleNames, ruleVersions, effectiveRule, null);

				if (dependents.contains(Dependents.PARENTS)) {
					BitSet parents = relations.parents[dependency.getItem1().rule()];
					for (int parent = parents.nextSetBit(0); parent >= 0; parent = parents.nextSetBit(parent + 1)) {
						if (parent < 0 || parent >= ruleVersions.length || checked.get(parent)) {
							continue;
						}

						checked.set(parent);
						int required = checkDependencyVersion(dependency, ruleNames, ruleVersions, parent, "parent");
						highestRequiredDependency = Math.max(highestRequiredDependency, required);
					}
				}

				if (dependents.contains(Dependents.CHILDREN)) {
					BitSet children = relations.children[dependency.getItem1().rule()];
					for (int child = children.nextSetBit(0); child >= 0; child = children.nextSetBit(child + 1)) {
						if (child < 0 || child >= ruleVersions.length || checked.get(child)) {
							continue;
						}

						checked.set(child);
						int required = checkDependencyVersion(dependency, ruleNames, ruleVersions, child, "child");
						highestRequiredDependency = Math.max(highestRequiredDependency, required);
					}
				}

				if (dependents.contains(Dependents.ANCESTORS)) {
					BitSet ancestors = relations.getAncestors(dependency.getItem1().rule());
					for (int ancestor = ancestors.nextSetBit(0); ancestor >= 0; ancestor = ancestors.nextSetBit(ancestor + 1)) {
						if (ancestor < 0 || ancestor >= ruleVersions.length || checked.get(ancestor)) {
							continue;
						}

						checked.set(ancestor);
						int required = checkDependencyVersion(dependency, ruleNames, ruleVersions, ancestor, "ancestor");
						highestRequiredDependency = Math.max(highestRequiredDependency, required);
					}
				}

				if (dependents.contains(Dependents.DESCENDANTS)) {
					BitSet descendants = relations.getDescendants(dependency.getItem1().rule());
					for (int descendant = descendants.nextSetBit(0); descendant >= 0; descendant = descendants.nextSetBit(descendant + 1)) {
						if (descendant < 0 || descendant >= ruleVersions.length || checked.get(descendant)) {
							continue;
						}

						checked.set(descendant);
						int required = checkDependencyVersion(dependency, ruleNames, ruleVersions, descendant, "descendant");
						highestRequiredDependency = Math.max(highestRequiredDependency, required);
					}
				}

				int declaredVersion = dependency.getItem1().version();
				if (declaredVersion > highestRequiredDependency) {
					Tuple2<AnnotationMirror, AnnotationValue> versionElement = findRuleDependencyProperty(dependency, RuleDependencyProperty.VERSION);
					String message = String.format("Rule dependency version mismatch: %s has maximum dependency version %d (expected %d) in %s",
												   ruleNames[dependency.getItem1().rule()],
												   highestRequiredDependency,
												   declaredVersion,
												   getRecognizerType(dependency.getItem1()).toString());

					if (versionElement != null) {
						processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message,
														  dependency.getItem2(), versionElement.getItem1(), versionElement.getItem2());
					}
					else {
						processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message,
														  dependency.getItem2());
					}
				}
			}
			catch (AnnotationTypeMismatchException ex) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, String.format("Could not validate rule dependencies for element %s", dependency.getItem2().toString()),
														 dependency.getItem2());
			}
		}
	}

	private static final Set<Dependents> IMPLEMENTED_DEPENDENTS = EnumSet.of(Dependents.SELF, Dependents.PARENTS, Dependents.CHILDREN, Dependents.ANCESTORS, Dependents.DESCENDANTS);

	private void reportUnimplementedDependents(Tuple2<RuleDependency, Element> dependency, EnumSet<Dependents> dependents) {
		EnumSet<Dependents> unimplemented = dependents.clone();
		unimplemented.removeAll(IMPLEMENTED_DEPENDENTS);
		if (!unimplemented.isEmpty()) {
			Tuple2<AnnotationMirror, AnnotationValue> dependentsElement = findRuleDependencyProperty(dependency, RuleDependencyProperty.DEPENDENTS);
			if (dependentsElement == null) {
				dependentsElement = findRuleDependencyProperty(dependency, RuleDependencyProperty.RULE);
			}

			String message = String.format("Cannot validate the following dependents of rule %d: %s",
										   dependency.getItem1().rule(),
										   unimplemented);

			if (dependentsElement != null) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, message,
											  dependency.getItem2(), dependentsElement.getItem1(), dependentsElement.getItem2());
			}
			else {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, message,
											  dependency.getItem2());
			}
		}
	}

	private int checkDependencyVersion(Tuple2<RuleDependency, Element> dependency, String[] ruleNames, int[] ruleVersions, int relatedRule, String relation) {
		String ruleName = ruleNames[dependency.getItem1().rule()];
		String path;
		if (relation == null) {
			path = ruleName;
		}
		else {
			String mismatchedRuleName = ruleNames[relatedRule];
			path = String.format("rule %s (%s of %s)", mismatchedRuleName, relation, ruleName);
		}

		int declaredVersion = dependency.getItem1().version();
		int actualVersion = ruleVersions[relatedRule];
		if (actualVersion > declaredVersion) {
			Tuple2<AnnotationMirror, AnnotationValue> versionElement = findRuleDependencyProperty(dependency, RuleDependencyProperty.VERSION);
			String message = String.format("Rule dependency version mismatch: %s has version %d (expected <= %d) in %s",
										   path,
										   actualVersion,
										   declaredVersion,
										   getRecognizerType(dependency.getItem1()).toString());

			if (versionElement != null) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message,
												  dependency.getItem2(), versionElement.getItem1(), versionElement.getItem2());
			}
			else {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message,
												  dependency.getItem2());
			}
		}

		return actualVersion;
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

					if (name.indexOf(ATNSimulator.RULE_VARIANT_DELIMITER) >= 0) {
						// ignore left-factored pseudo-rules
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
		TypeElement ruleVersionAnnotationElement = processingEnv.getElementUtils().getTypeElement(RuleVersionClassName);
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

	public enum RuleDependencyProperty {
		RECOGNIZER,
		RULE,
		VERSION,
		DEPENDENTS,
	}

	@Nullable
	private Tuple2<AnnotationMirror, AnnotationValue> findRuleDependencyProperty(@NotNull Tuple2<RuleDependency, Element> dependency, @NotNull RuleDependencyProperty property) {
		TypeElement ruleDependencyTypeElement = processingEnv.getElementUtils().getTypeElement(RuleDependencyClassName);
		TypeElement ruleDependenciesTypeElement = processingEnv.getElementUtils().getTypeElement(RuleDependenciesClassName);
		List<? extends AnnotationMirror> mirrors = dependency.getItem2().getAnnotationMirrors();
		for (AnnotationMirror annotationMirror : mirrors) {
			if (processingEnv.getTypeUtils().isSameType(ruleDependencyTypeElement.asType(), annotationMirror.getAnnotationType())) {
				AnnotationValue element = findRuleDependencyProperty(dependency, annotationMirror, property);
				if (element != null) {
					return Tuple.create(annotationMirror, element);
				}
			}
			else if (processingEnv.getTypeUtils().isSameType(ruleDependenciesTypeElement.asType(), annotationMirror.getAnnotationType())) {
				Map<? extends ExecutableElement, ? extends AnnotationValue> values = annotationMirror.getElementValues();
				for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> value : values.entrySet()) {
					if ("value()".equals(value.getKey().toString())) {
						AnnotationValue annotationValue = value.getValue();
						if (!(annotationValue.getValue() instanceof List)) {
							processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Expected array of RuleDependency annotations for annotation property 'value()'.", dependency.getItem2(), annotationMirror, annotationValue);
							break;
						}

						List<?> annotationValueList = (List<?>)annotationValue.getValue();
						for (Object obj : annotationValueList) {
							if (!(obj instanceof AnnotationMirror)) {
								processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Expected RuleDependency annotation mirror for element of property 'value()'.", dependency.getItem2(), annotationMirror, annotationValue);
								break;
							}

							AnnotationValue element = findRuleDependencyProperty(dependency, (AnnotationMirror)obj, property);
							if (element != null) {
								return Tuple.create((AnnotationMirror)obj, element);
							}
						}
					}
					else {
						processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format("Unexpected annotation property %s.", value.getKey().toString()), dependency.getItem2(), annotationMirror, value.getValue());
					}
				}
			}
		}

		return null;
	}

	@Nullable
	private AnnotationValue findRuleDependencyProperty(@NotNull Tuple2<RuleDependency, Element> dependency, @NotNull AnnotationMirror annotationMirror, @NotNull RuleDependencyProperty property) {
		AnnotationValue recognizerValue = null;
		AnnotationValue ruleValue = null;
		AnnotationValue versionValue = null;
		AnnotationValue dependentsValue = null;

		Map<? extends ExecutableElement, ? extends AnnotationValue> values = annotationMirror.getElementValues();
		for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> value : values.entrySet()) {
			AnnotationValue annotationValue = value.getValue();
			if ("rule()".equals(value.getKey().toString())) {
				ruleValue = annotationValue;
				if (!(annotationValue.getValue() instanceof Integer)) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Expected int constant for annotation property 'rule()'.", dependency.getItem2(), annotationMirror, annotationValue);
					return null;
				}

				if ((Integer)annotationValue.getValue() != dependency.getItem1().rule()) {
					// this is a valid dependency annotation, but not the one we're looking for
					return null;
				}
			}
			else if ("recognizer()".equals(value.getKey().toString())) {
				recognizerValue = annotationValue;
				if (!(annotationValue.getValue() instanceof TypeMirror)) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Expected Class constant for annotation property 'recognizer()'.", dependency.getItem2(), annotationMirror, annotationValue);
					return null;
				}

				TypeMirror annotationRecognizer = (TypeMirror)annotationValue.getValue();
				TypeMirror expectedRecognizer = getRecognizerType(dependency.getItem1());
				if (!processingEnv.getTypeUtils().isSameType(expectedRecognizer, annotationRecognizer)) {
					// this is a valid dependency annotation, but not the one we're looking for
					return null;
				}
			}
			else if ("version()".equals(value.getKey().toString())) {
				versionValue = annotationValue;
				if (!(annotationValue.getValue() instanceof Integer)) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Expected int constant for annotation property 'version()'.", dependency.getItem2(), annotationMirror, annotationValue);
					return null;
				}

				if ((Integer)annotationValue.getValue() != dependency.getItem1().version()) {
					// this is a valid dependency annotation, but not the one we're looking for
					return null;
				}
			}
		}

		if (recognizerValue != null) {
			if (property == RuleDependencyProperty.RECOGNIZER) {
				return recognizerValue;
			}
			else if (ruleValue != null) {
				if (property == RuleDependencyProperty.RULE) {
					return ruleValue;
				}
				else if (versionValue != null) {
					if (property == RuleDependencyProperty.VERSION) {
						return versionValue;
					}
					else if (property == RuleDependencyProperty.DEPENDENTS) {
						return dependentsValue;
					}
				}
			}
		}

		if (recognizerValue == null) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Could not find 'recognizer()' element in annotation.", dependency.getItem2(), annotationMirror);
		}

		if (property == RuleDependencyProperty.RECOGNIZER) {
			return null;
		}

		if (ruleValue == null) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Could not find 'rule()' element in annotation.", dependency.getItem2(), annotationMirror);
		}

		if (property == RuleDependencyProperty.RULE) {
			return null;
		}

		if (versionValue == null) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Could not find 'version()' element in annotation.", dependency.getItem2(), annotationMirror);
		}

		return null;
	}

	private RuleRelations extractRuleRelations(TypeMirror recognizer) {
		String serializedATN = getSerializedATN(recognizer);
		if (serializedATN == null) {
			return null;
		}

		ATN atn = ATNSimulator.deserialize(serializedATN.toCharArray());
		RuleRelations relations = new RuleRelations(atn.ruleToStartState.length);
		for (ATNState state : atn.states) {
			if (!state.epsilonOnlyTransitions) {
				continue;
			}

			for (Transition transition : state.getTransitions()) {
				if (transition.getSerializationType() != Transition.RULE) {
					continue;
				}

				RuleTransition ruleTransition = (RuleTransition)transition;
				relations.addRuleInvocation(state.ruleIndex, ruleTransition.target.ruleIndex);
			}
		}

		return relations;
	}

	private String getSerializedATN(TypeMirror recognizerClass) {
		List<? extends Element> elements = processingEnv.getElementUtils().getAllMembers((TypeElement)processingEnv.getTypeUtils().asElement(recognizerClass));
		for (Element element : elements) {
			if (element.getKind() != ElementKind.FIELD) {
				continue;
			}

			VariableElement field = (VariableElement)element;
			boolean isStatic = element.getModifiers().contains(Modifier.STATIC);
			Object constantValue = field.getConstantValue();
			boolean isString = constantValue instanceof String;
			String name = field.getSimpleName().toString();
			if (isStatic && isString && name.equals("_serializedATN")) {
				return (String)constantValue;
			}
		}

		processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Could not retrieve serialized ATN from grammar.");
		return null;
	}

	private static final class RuleRelations {
		private final BitSet[] parents;
		private final BitSet[] children;

		public RuleRelations(int ruleCount) {
			parents = new BitSet[ruleCount];
			for (int i = 0; i < ruleCount; i++) {
				parents[i] = new BitSet();
			}

			children = new BitSet[ruleCount];
			for (int i = 0; i < ruleCount; i++) {
				children[i] = new BitSet();
			}
		}

		public boolean addRuleInvocation(int caller, int callee) {
			if (caller < 0) {
				// tokens rule
				return false;
			}

			if (children[caller].get(callee)) {
				// already added
				return false;
			}

			children[caller].set(callee);
			parents[callee].set(caller);
			return true;
		}

		public BitSet getAncestors(int rule) {
			BitSet ancestors = new BitSet();
			ancestors.or(parents[rule]);
			while (true) {
				int cardinality = ancestors.cardinality();
				for (int i = ancestors.nextSetBit(0); i >= 0; i = ancestors.nextSetBit(i + 1)) {
					ancestors.or(parents[i]);
				}

				if (ancestors.cardinality() == cardinality) {
					// nothing changed
					break;
				}
			}

			return ancestors;
		}

		public BitSet getDescendants(int rule) {
			BitSet descendants = new BitSet();
			descendants.or(children[rule]);
			while (true) {
				int cardinality = descendants.cardinality();
				for (int i = descendants.nextSetBit(0); i >= 0; i = descendants.nextSetBit(i + 1)) {
					descendants.or(children[i]);
				}

				if (descendants.cardinality() == cardinality) {
					// nothing changed
					break;
				}
			}

			return descendants;
		}
	}
}
