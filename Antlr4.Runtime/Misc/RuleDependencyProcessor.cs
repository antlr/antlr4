/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
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
using System;
using System.Collections;
using System.Collections.Generic;
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;
using Javax.Annotation.Processing;
using Javax.Lang.Model.Element;
using Javax.Lang.Model.Type;
using Javax.Tools;
using Sharpen;
using Sharpen.Annotation;

namespace Antlr4.Runtime.Misc
{
	/// <summary>A compile-time validator for rule dependencies.</summary>
	/// <remarks>A compile-time validator for rule dependencies.</remarks>
	/// <seealso cref="Antlr4.Runtime.RuleDependency">Antlr4.Runtime.RuleDependency</seealso>
	/// <seealso cref="Antlr4.Runtime.RuleDependencies">Antlr4.Runtime.RuleDependencies</seealso>
	/// <author>Sam Harwell</author>
	public class RuleDependencyProcessor : AbstractProcessor
	{
		public static readonly string RuleDependencyClassName = "org.antlr.v4.runtime.RuleDependency";

		public static readonly string RuleDependenciesClassName = "org.antlr.v4.runtime.RuleDependencies";

		public static readonly string RuleVersionClassName = "org.antlr.v4.runtime.RuleVersion";

		public RuleDependencyProcessor()
		{
		}

		public override bool Process<_T0>(ICollection<_T0> annotations, RoundEnvironment 
			roundEnv)
		{
			if (!CheckClassNameConstants())
			{
				return true;
			}
			IList<Tuple<RuleDependency, Javax.Lang.Model.Element.Element>> dependencies = GetDependencies
				(roundEnv);
			IDictionary<TypeMirror, IList<Tuple<RuleDependency, Javax.Lang.Model.Element.Element
				>>> recognizerDependencies = new Dictionary<TypeMirror, IList<Tuple<RuleDependency
				, Javax.Lang.Model.Element.Element>>>();
			foreach (Tuple<RuleDependency, Javax.Lang.Model.Element.Element> dependency in dependencies)
			{
				TypeMirror recognizerType = GetRecognizerType(dependency.GetItem1());
				IList<Tuple<RuleDependency, Javax.Lang.Model.Element.Element>> list = recognizerDependencies
					.Get(recognizerType);
				if (list == null)
				{
					list = new AList<Tuple<RuleDependency, Javax.Lang.Model.Element.Element>>();
					recognizerDependencies.Put(recognizerType, list);
				}
				list.AddItem(dependency);
			}
			foreach (KeyValuePair<TypeMirror, IList<Tuple<RuleDependency, Javax.Lang.Model.Element.Element
				>>> entry in recognizerDependencies.EntrySet())
			{
				processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Note, string.Format("ANTLR 4: Validating %d dependencies on rules in %s."
					, entry.Value.Count, entry.Key.ToString()));
				CheckDependencies(entry.Value, entry.Key);
			}
			return true;
		}

		private bool CheckClassNameConstants()
		{
			bool success = CheckClassNameConstant(RuleDependencyClassName, typeof(RuleDependency
				));
			success &= CheckClassNameConstant(RuleDependenciesClassName, typeof(RuleDependencies
				));
			success &= CheckClassNameConstant(RuleVersionClassName, typeof(RuleVersion));
			return success;
		}

		private bool CheckClassNameConstant<_T0>(string className, System.Type<_T0> clazz
			)
		{
			Args.NotNull("className", className);
			Args.NotNull("clazz", clazz);
			if (!className.Equals(clazz.GetCanonicalName()))
			{
				processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Error, string.Format("Unable to process rule dependencies due to class name mismatch: %s != %s"
					, className, clazz.GetCanonicalName()));
				return false;
			}
			return true;
		}

		private static TypeMirror GetRecognizerType(RuleDependency dependency)
		{
			try
			{
				dependency.Recognizer();
				string message = string.Format("Expected %s to get the %s.", typeof(MirroredTypeException
					).Name, typeof(TypeMirror).Name);
				throw new NotSupportedException(message);
			}
			catch (MirroredTypeException ex)
			{
				return ex.GetTypeMirror();
			}
		}

		private void CheckDependencies(IList<Tuple<RuleDependency, Javax.Lang.Model.Element.Element
			>> dependencies, TypeMirror recognizerType)
		{
			string[] ruleNames = GetRuleNames(recognizerType);
			int[] ruleVersions = GetRuleVersions(recognizerType, ruleNames);
			RuleDependencyProcessor.RuleRelations relations = ExtractRuleRelations(recognizerType
				);
			foreach (Tuple<RuleDependency, Javax.Lang.Model.Element.Element> dependency in dependencies)
			{
				try
				{
					if (!processingEnv.GetTypeUtils().IsAssignable(GetRecognizerType(dependency.GetItem1
						()), recognizerType))
					{
						continue;
					}
					// this is the rule in the dependency set with the highest version number
					int effectiveRule = dependency.GetItem1().Rule();
					if (effectiveRule < 0 || effectiveRule >= ruleVersions.Length)
					{
						Tuple<AnnotationMirror, AnnotationValue> ruleReferenceElement = FindRuleDependencyProperty
							(dependency, RuleDependencyProcessor.RuleDependencyProperty.Rule);
						string message = string.Format("Rule dependency on unknown rule %d@%d in %s", dependency
							.GetItem1().Rule(), dependency.GetItem1().Version(), GetRecognizerType(dependency
							.GetItem1()).ToString());
						if (ruleReferenceElement != null)
						{
							processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Error, message, dependency
								.GetItem2(), ruleReferenceElement.GetItem1(), ruleReferenceElement.GetItem2());
						}
						else
						{
							processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Error, message, dependency
								.GetItem2());
						}
						continue;
					}
					EnumSet<Dependents> dependents = EnumSet.Of(Dependents.Self, dependency.GetItem1(
						).Dependents());
					ReportUnimplementedDependents(dependency, dependents);
					BitSet checked = new BitSet();
					int highestRequiredDependency = CheckDependencyVersion(dependency, ruleNames, ruleVersions
						, effectiveRule, null);
					if (dependents.Contains(Dependents.Parents))
					{
						BitSet parents = relations.parents[dependency.GetItem1().Rule()];
						for (int parent = parents.NextSetBit(0); parent >= 0; parent = parents.NextSetBit
							(parent + 1))
						{
							if (parent < 0 || parent >= ruleVersions.Length || checked.Get(parent))
							{
								continue;
							}
							checked.Set(parent);
							int required = CheckDependencyVersion(dependency, ruleNames, ruleVersions, parent
								, "parent");
							highestRequiredDependency = Math.Max(highestRequiredDependency, required);
						}
					}
					if (dependents.Contains(Dependents.Children))
					{
						BitSet children = relations.children[dependency.GetItem1().Rule()];
						for (int child = children.NextSetBit(0); child >= 0; child = children.NextSetBit(
							child + 1))
						{
							if (child < 0 || child >= ruleVersions.Length || checked.Get(child))
							{
								continue;
							}
							checked.Set(child);
							int required = CheckDependencyVersion(dependency, ruleNames, ruleVersions, child, 
								"child");
							highestRequiredDependency = Math.Max(highestRequiredDependency, required);
						}
					}
					if (dependents.Contains(Dependents.Ancestors))
					{
						BitSet ancestors = relations.GetAncestors(dependency.GetItem1().Rule());
						for (int ancestor = ancestors.NextSetBit(0); ancestor >= 0; ancestor = ancestors.
							NextSetBit(ancestor + 1))
						{
							if (ancestor < 0 || ancestor >= ruleVersions.Length || checked.Get(ancestor))
							{
								continue;
							}
							checked.Set(ancestor);
							int required = CheckDependencyVersion(dependency, ruleNames, ruleVersions, ancestor
								, "ancestor");
							highestRequiredDependency = Math.Max(highestRequiredDependency, required);
						}
					}
					if (dependents.Contains(Dependents.Descendants))
					{
						BitSet descendants = relations.GetDescendants(dependency.GetItem1().Rule());
						for (int descendant = descendants.NextSetBit(0); descendant >= 0; descendant = descendants
							.NextSetBit(descendant + 1))
						{
							if (descendant < 0 || descendant >= ruleVersions.Length || checked.Get(descendant
								))
							{
								continue;
							}
							checked.Set(descendant);
							int required = CheckDependencyVersion(dependency, ruleNames, ruleVersions, descendant
								, "descendant");
							highestRequiredDependency = Math.Max(highestRequiredDependency, required);
						}
					}
					int declaredVersion = dependency.GetItem1().Version();
					if (declaredVersion > highestRequiredDependency)
					{
						Tuple<AnnotationMirror, AnnotationValue> versionElement = FindRuleDependencyProperty
							(dependency, RuleDependencyProcessor.RuleDependencyProperty.Version);
						string message = string.Format("Rule dependency version mismatch: %s has maximum dependency version %d (expected %d) in %s"
							, ruleNames[dependency.GetItem1().Rule()], highestRequiredDependency, declaredVersion
							, GetRecognizerType(dependency.GetItem1()).ToString());
						if (versionElement != null)
						{
							processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Error, message, dependency
								.GetItem2(), versionElement.GetItem1(), versionElement.GetItem2());
						}
						else
						{
							processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Error, message, dependency
								.GetItem2());
						}
					}
				}
				catch (AnnotationTypeMismatchException)
				{
					processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Warning, string.Format("Could not validate rule dependencies for element %s"
						, dependency.GetItem2().ToString()), dependency.GetItem2());
				}
			}
		}

		private static readonly ICollection<Dependents> ImplementedDependents = EnumSet.Of
			(Dependents.Self, Dependents.Parents, Dependents.Children, Dependents.Ancestors, 
			Dependents.Descendants);

		private void ReportUnimplementedDependents(Tuple<RuleDependency, Javax.Lang.Model.Element.Element
			> dependency, EnumSet<Dependents> dependents)
		{
			EnumSet<Dependents> unimplemented = dependents.Clone();
			unimplemented.RemoveAll(ImplementedDependents);
			if (!unimplemented.IsEmpty())
			{
				Tuple<AnnotationMirror, AnnotationValue> dependentsElement = FindRuleDependencyProperty
					(dependency, RuleDependencyProcessor.RuleDependencyProperty.Dependents);
				if (dependentsElement == null)
				{
					dependentsElement = FindRuleDependencyProperty(dependency, RuleDependencyProcessor.RuleDependencyProperty
						.Rule);
				}
				string message = string.Format("Cannot validate the following dependents of rule %d: %s"
					, dependency.GetItem1().Rule(), unimplemented);
				if (dependentsElement != null)
				{
					processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Warning, message, dependency
						.GetItem2(), dependentsElement.GetItem1(), dependentsElement.GetItem2());
				}
				else
				{
					processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Warning, message, dependency
						.GetItem2());
				}
			}
		}

		private int CheckDependencyVersion(Tuple<RuleDependency, Javax.Lang.Model.Element.Element
			> dependency, string[] ruleNames, int[] ruleVersions, int relatedRule, string relation
			)
		{
			string ruleName = ruleNames[dependency.GetItem1().Rule()];
			string path;
			if (relation == null)
			{
				path = ruleName;
			}
			else
			{
				string mismatchedRuleName = ruleNames[relatedRule];
				path = string.Format("rule %s (%s of %s)", mismatchedRuleName, relation, ruleName
					);
			}
			int declaredVersion = dependency.GetItem1().Version();
			int actualVersion = ruleVersions[relatedRule];
			if (actualVersion > declaredVersion)
			{
				Tuple<AnnotationMirror, AnnotationValue> versionElement = FindRuleDependencyProperty
					(dependency, RuleDependencyProcessor.RuleDependencyProperty.Version);
				string message = string.Format("Rule dependency version mismatch: %s has version %d (expected <= %d) in %s"
					, path, actualVersion, declaredVersion, GetRecognizerType(dependency.GetItem1())
					.ToString());
				if (versionElement != null)
				{
					processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Error, message, dependency
						.GetItem2(), versionElement.GetItem1(), versionElement.GetItem2());
				}
				else
				{
					processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Error, message, dependency
						.GetItem2());
				}
			}
			return actualVersion;
		}

		private int[] GetRuleVersions(TypeMirror recognizerClass, string[] ruleNames)
		{
			int[] versions = new int[ruleNames.Length];
			IList<Javax.Lang.Model.Element.Element> elements = processingEnv.GetElementUtils(
				).GetAllMembers((TypeElement)processingEnv.GetTypeUtils().AsElement(recognizerClass
				));
			foreach (Javax.Lang.Model.Element.Element element in elements)
			{
				if (element.GetKind() != ElementKind.Field)
				{
					continue;
				}
				VariableElement field = (VariableElement)element;
				bool isStatic = element.GetModifiers().Contains(Modifier.Static);
				object constantValue = field.GetConstantValue();
				bool isInteger = constantValue is int;
				string name = field.GetSimpleName().ToString();
				if (isStatic && isInteger && name.StartsWith("RULE_"))
				{
					try
					{
						name = Sharpen.Runtime.Substring(name, "RULE_".Length);
						if (name.IsEmpty() || !System.Char.IsLower(name[0]))
						{
							continue;
						}
						int index = (int)constantValue;
						if (index < 0 || index >= versions.Length)
						{
							string message = string.Format("Rule index %d for rule '%s' out of bounds for recognizer %s."
								, index, name, recognizerClass.ToString());
							processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Error, message, element);
							continue;
						}
						if (name.IndexOf(ATNSimulator.RuleVariantDelimiter) >= 0)
						{
							// ignore left-factored pseudo-rules
							continue;
						}
						ExecutableElement ruleMethod = GetRuleMethod(recognizerClass, name);
						if (ruleMethod == null)
						{
							string message = string.Format("Could not find rule method for rule '%s' in recognizer %s."
								, name, recognizerClass.ToString());
							processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Error, message, element);
							continue;
						}
						RuleVersion ruleVersion = ruleMethod.GetAnnotation<RuleVersion>();
						int version = ruleVersion != null ? ruleVersion.Value() : 0;
						versions[index] = version;
					}
					catch (ArgumentException)
					{
						processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Error, "Exception occurred while validating rule dependencies."
							, element);
					}
				}
			}
			return versions;
		}

		private ExecutableElement GetRuleMethod(TypeMirror recognizerClass, string name)
		{
			IList<Javax.Lang.Model.Element.Element> elements = processingEnv.GetElementUtils(
				).GetAllMembers((TypeElement)processingEnv.GetTypeUtils().AsElement(recognizerClass
				));
			foreach (Javax.Lang.Model.Element.Element element in elements)
			{
				if (element.GetKind() != ElementKind.Method)
				{
					continue;
				}
				ExecutableElement method = (ExecutableElement)element;
				if (method.GetSimpleName().ContentEquals(name) && HasRuleVersionAnnotation(method
					))
				{
					return method;
				}
			}
			return null;
		}

		private bool HasRuleVersionAnnotation(ExecutableElement method)
		{
			TypeElement ruleVersionAnnotationElement = processingEnv.GetElementUtils().GetTypeElement
				(RuleVersionClassName);
			if (ruleVersionAnnotationElement == null)
			{
				return false;
			}
			foreach (AnnotationMirror annotation in method.GetAnnotationMirrors())
			{
				if (processingEnv.GetTypeUtils().IsSameType(annotation.GetAnnotationType(), ruleVersionAnnotationElement
					.AsType()))
				{
					return true;
				}
			}
			return false;
		}

		private string[] GetRuleNames(TypeMirror recognizerClass)
		{
			IList<string> result = new AList<string>();
			IList<Javax.Lang.Model.Element.Element> elements = processingEnv.GetElementUtils(
				).GetAllMembers((TypeElement)processingEnv.GetTypeUtils().AsElement(recognizerClass
				));
			foreach (Javax.Lang.Model.Element.Element element in elements)
			{
				if (element.GetKind() != ElementKind.Field)
				{
					continue;
				}
				VariableElement field = (VariableElement)element;
				bool isStatic = element.GetModifiers().Contains(Modifier.Static);
				object constantValue = field.GetConstantValue();
				bool isInteger = constantValue is int;
				string name = field.GetSimpleName().ToString();
				if (isStatic && isInteger && name.StartsWith("RULE_"))
				{
					try
					{
						name = Sharpen.Runtime.Substring(name, "RULE_".Length);
						if (name.IsEmpty() || !System.Char.IsLower(name[0]))
						{
							continue;
						}
						int index = (int)constantValue;
						if (index < 0)
						{
							continue;
						}
						while (result.Count <= index)
						{
							result.AddItem(string.Empty);
						}
						result.Set(index, name);
					}
					catch (ArgumentException)
					{
						processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Error, "Exception occurred while validating rule dependencies."
							, element);
					}
				}
			}
			return Sharpen.Collections.ToArray(result, new string[result.Count]);
		}

		public static IList<Tuple<RuleDependency, Javax.Lang.Model.Element.Element>> GetDependencies
			(RoundEnvironment roundEnv)
		{
			IList<Tuple<RuleDependency, Javax.Lang.Model.Element.Element>> result = new AList
				<Tuple<RuleDependency, Javax.Lang.Model.Element.Element>>();
			ICollection<Javax.Lang.Model.Element.Element> elements = roundEnv.GetElementsAnnotatedWith
				(typeof(RuleDependency));
			foreach (Javax.Lang.Model.Element.Element element in elements)
			{
				RuleDependency dependency = element.GetAnnotation<RuleDependency>();
				if (dependency == null)
				{
					continue;
				}
				result.AddItem(Tuple.Create(dependency, element));
			}
			elements = roundEnv.GetElementsAnnotatedWith(typeof(RuleDependencies));
			foreach (Javax.Lang.Model.Element.Element element_1 in elements)
			{
				RuleDependencies dependencies = element_1.GetAnnotation<RuleDependencies>();
				if (dependencies == null || dependencies.Value() == null)
				{
					continue;
				}
				foreach (RuleDependency dependency in dependencies.Value())
				{
					result.AddItem(Tuple.Create(dependency, element_1));
				}
			}
			return result;
		}

		public enum RuleDependencyProperty
		{
			Recognizer,
			Rule,
			Version,
			Dependents
		}

		[Nullable]
		private Tuple<AnnotationMirror, AnnotationValue> FindRuleDependencyProperty(Tuple
			<RuleDependency, Javax.Lang.Model.Element.Element> dependency, RuleDependencyProcessor.RuleDependencyProperty
			 property)
		{
			TypeElement ruleDependencyTypeElement = processingEnv.GetElementUtils().GetTypeElement
				(RuleDependencyClassName);
			TypeElement ruleDependenciesTypeElement = processingEnv.GetElementUtils().GetTypeElement
				(RuleDependenciesClassName);
			IList<AnnotationMirror> mirrors = dependency.GetItem2().GetAnnotationMirrors();
			foreach (AnnotationMirror annotationMirror in mirrors)
			{
				if (processingEnv.GetTypeUtils().IsSameType(ruleDependencyTypeElement.AsType(), annotationMirror
					.GetAnnotationType()))
				{
					AnnotationValue element = FindRuleDependencyProperty(dependency, annotationMirror
						, property);
					if (element != null)
					{
						return Tuple.Create(annotationMirror, element);
					}
				}
				else
				{
					if (processingEnv.GetTypeUtils().IsSameType(ruleDependenciesTypeElement.AsType(), 
						annotationMirror.GetAnnotationType()))
					{
						IDictionary<ExecutableElement, AnnotationValue> values = annotationMirror.GetElementValues
							();
						foreach (KeyValuePair<ExecutableElement, AnnotationValue> value in values.EntrySet
							())
						{
							if ("value()".Equals(value.Key.ToString()))
							{
								AnnotationValue annotationValue = value.Value;
								if (!(annotationValue.GetValue() is IList))
								{
									processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Warning, "Expected array of RuleDependency annotations for annotation property 'value()'."
										, dependency.GetItem2(), annotationMirror, annotationValue);
									break;
								}
								IList<object> annotationValueList = (IList<object>)annotationValue.GetValue();
								foreach (object obj in annotationValueList)
								{
									if (!(obj is AnnotationMirror))
									{
										processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Warning, "Expected RuleDependency annotation mirror for element of property 'value()'."
											, dependency.GetItem2(), annotationMirror, annotationValue);
										break;
									}
									AnnotationValue element = FindRuleDependencyProperty(dependency, (AnnotationMirror
										)obj, property);
									if (element != null)
									{
										return Tuple.Create((AnnotationMirror)obj, element);
									}
								}
							}
							else
							{
								processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Error, string.Format("Unexpected annotation property %s."
									, value.Key.ToString()), dependency.GetItem2(), annotationMirror, value.Value);
							}
						}
					}
				}
			}
			return null;
		}

		[Nullable]
		private AnnotationValue FindRuleDependencyProperty(Tuple<RuleDependency, Javax.Lang.Model.Element.Element
			> dependency, AnnotationMirror annotationMirror, RuleDependencyProcessor.RuleDependencyProperty
			 property)
		{
			AnnotationValue recognizerValue = null;
			AnnotationValue ruleValue = null;
			AnnotationValue versionValue = null;
			AnnotationValue dependentsValue = null;
			IDictionary<ExecutableElement, AnnotationValue> values = annotationMirror.GetElementValues
				();
			foreach (KeyValuePair<ExecutableElement, AnnotationValue> value in values.EntrySet
				())
			{
				AnnotationValue annotationValue = value.Value;
				if ("rule()".Equals(value.Key.ToString()))
				{
					ruleValue = annotationValue;
					if (!(annotationValue.GetValue() is int))
					{
						processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Warning, "Expected int constant for annotation property 'rule()'."
							, dependency.GetItem2(), annotationMirror, annotationValue);
						return null;
					}
					if ((int)annotationValue.GetValue() != dependency.GetItem1().Rule())
					{
						// this is a valid dependency annotation, but not the one we're looking for
						return null;
					}
				}
				else
				{
					if ("recognizer()".Equals(value.Key.ToString()))
					{
						recognizerValue = annotationValue;
						if (!(annotationValue.GetValue() is TypeMirror))
						{
							processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Warning, "Expected Class constant for annotation property 'recognizer()'."
								, dependency.GetItem2(), annotationMirror, annotationValue);
							return null;
						}
						TypeMirror annotationRecognizer = (TypeMirror)annotationValue.GetValue();
						TypeMirror expectedRecognizer = GetRecognizerType(dependency.GetItem1());
						if (!processingEnv.GetTypeUtils().IsSameType(expectedRecognizer, annotationRecognizer
							))
						{
							// this is a valid dependency annotation, but not the one we're looking for
							return null;
						}
					}
					else
					{
						if ("version()".Equals(value.Key.ToString()))
						{
							versionValue = annotationValue;
							if (!(annotationValue.GetValue() is int))
							{
								processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Warning, "Expected int constant for annotation property 'version()'."
									, dependency.GetItem2(), annotationMirror, annotationValue);
								return null;
							}
							if ((int)annotationValue.GetValue() != dependency.GetItem1().Version())
							{
								// this is a valid dependency annotation, but not the one we're looking for
								return null;
							}
						}
					}
				}
			}
			if (recognizerValue != null)
			{
				if (property == RuleDependencyProcessor.RuleDependencyProperty.Recognizer)
				{
					return recognizerValue;
				}
				else
				{
					if (ruleValue != null)
					{
						if (property == RuleDependencyProcessor.RuleDependencyProperty.Rule)
						{
							return ruleValue;
						}
						else
						{
							if (versionValue != null)
							{
								if (property == RuleDependencyProcessor.RuleDependencyProperty.Version)
								{
									return versionValue;
								}
								else
								{
									if (property == RuleDependencyProcessor.RuleDependencyProperty.Dependents)
									{
										return dependentsValue;
									}
								}
							}
						}
					}
				}
			}
			if (recognizerValue == null)
			{
				processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Warning, "Could not find 'recognizer()' element in annotation."
					, dependency.GetItem2(), annotationMirror);
			}
			if (property == RuleDependencyProcessor.RuleDependencyProperty.Recognizer)
			{
				return null;
			}
			if (ruleValue == null)
			{
				processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Warning, "Could not find 'rule()' element in annotation."
					, dependency.GetItem2(), annotationMirror);
			}
			if (property == RuleDependencyProcessor.RuleDependencyProperty.Rule)
			{
				return null;
			}
			if (versionValue == null)
			{
				processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Warning, "Could not find 'version()' element in annotation."
					, dependency.GetItem2(), annotationMirror);
			}
			return null;
		}

		private RuleDependencyProcessor.RuleRelations ExtractRuleRelations(TypeMirror recognizer
			)
		{
			string serializedATN = GetSerializedATN(recognizer);
			if (serializedATN == null)
			{
				return null;
			}
			ATN atn = ATNSimulator.Deserialize(serializedATN.ToCharArray());
			RuleDependencyProcessor.RuleRelations relations = new RuleDependencyProcessor.RuleRelations
				(atn.ruleToStartState.Length);
			foreach (ATNState state in atn.states)
			{
				if (!state.epsilonOnlyTransitions)
				{
					continue;
				}
				foreach (Transition transition in state.GetTransitions())
				{
					if (transition.GetSerializationType() != Transition.Rule)
					{
						continue;
					}
					RuleTransition ruleTransition = (RuleTransition)transition;
					relations.AddRuleInvocation(state.ruleIndex, ruleTransition.target.ruleIndex);
				}
			}
			return relations;
		}

		private string GetSerializedATN(TypeMirror recognizerClass)
		{
			IList<Javax.Lang.Model.Element.Element> elements = processingEnv.GetElementUtils(
				).GetAllMembers((TypeElement)processingEnv.GetTypeUtils().AsElement(recognizerClass
				));
			foreach (Javax.Lang.Model.Element.Element element in elements)
			{
				if (element.GetKind() != ElementKind.Field)
				{
					continue;
				}
				VariableElement field = (VariableElement)element;
				bool isStatic = element.GetModifiers().Contains(Modifier.Static);
				object constantValue = field.GetConstantValue();
				bool isString = constantValue is string;
				string name = field.GetSimpleName().ToString();
				if (isStatic && isString && name.Equals("_serializedATN"))
				{
					return (string)constantValue;
				}
			}
			processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Error, "Could not retrieve serialized ATN from grammar."
				);
			return null;
		}

		private sealed class RuleRelations
		{
			private readonly BitSet[] parents;

			private readonly BitSet[] children;

			public RuleRelations(int ruleCount)
			{
				parents = new BitSet[ruleCount];
				for (int i = 0; i < ruleCount; i++)
				{
					parents[i] = new BitSet();
				}
				children = new BitSet[ruleCount];
				for (int i_1 = 0; i_1 < ruleCount; i_1++)
				{
					children[i_1] = new BitSet();
				}
			}

			public bool AddRuleInvocation(int caller, int callee)
			{
				if (caller < 0)
				{
					// tokens rule
					return false;
				}
				if (children[caller].Get(callee))
				{
					// already added
					return false;
				}
				children[caller].Set(callee);
				parents[callee].Set(caller);
				return true;
			}

			public BitSet GetAncestors(int rule)
			{
				BitSet ancestors = new BitSet();
				ancestors.Or(parents[rule]);
				while (true)
				{
					int cardinality = ancestors.Cardinality();
					for (int i = ancestors.NextSetBit(0); i >= 0; i = ancestors.NextSetBit(i + 1))
					{
						ancestors.Or(parents[i]);
					}
					if (ancestors.Cardinality() == cardinality)
					{
						// nothing changed
						break;
					}
				}
				return ancestors;
			}

			public BitSet GetDescendants(int rule)
			{
				BitSet descendants = new BitSet();
				descendants.Or(children[rule]);
				while (true)
				{
					int cardinality = descendants.Cardinality();
					for (int i = descendants.NextSetBit(0); i >= 0; i = descendants.NextSetBit(i + 1))
					{
						descendants.Or(children[i]);
					}
					if (descendants.Cardinality() == cardinality)
					{
						// nothing changed
						break;
					}
				}
				return descendants;
			}
		}
	}
}
