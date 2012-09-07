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
import org.antlr.v4.runtime.RuleDependencies;
import org.antlr.v4.runtime.RuleDependency;
import org.antlr.v4.runtime.RuleVersion;

import java.lang.annotation.AnnotationTypeMismatchException;

/**
 *
 * @author Sam Harwell
 */
@SupportedAnnotationTypes({RuleDependencyProcessor.RuleDependencyClassName, RuleDependencyProcessor.RuleDependenciesClassName})
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

		for (Tuple2<RuleDependency, Element> dependency : dependencies) {
			try {
				if (!processingEnv.getTypeUtils().isAssignable(getRecognizerType(dependency.getItem1()), recognizerType)) {
					continue;
				}

				if (dependency.getItem1().rule() < 0 || dependency.getItem1().rule() >= ruleVersions.length) {
					Tuple2<AnnotationMirror, AnnotationValue> ruleReferenceElement = findRuleDependencyProperty(dependency, RuleDependencyProperty.RULE);
					String message = String.format("Rule dependency on unknown rule %d@%d in %s\n",
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
				}
				else if (ruleVersions[dependency.getItem1().rule()] != dependency.getItem1().version()) {
					Tuple2<AnnotationMirror, AnnotationValue> versionElement = findRuleDependencyProperty(dependency, RuleDependencyProperty.VERSION);
					String message = String.format("Rule dependency version mismatch on rule %s@%d (found @%d) in %s\n",
												   ruleNames[dependency.getItem1().rule()],
												   dependency.getItem1().version(),
												   ruleVersions[dependency.getItem1().rule()],
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
				else if (versionValue != null && property == RuleDependencyProperty.VERSION) {
					return versionValue;
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

}
