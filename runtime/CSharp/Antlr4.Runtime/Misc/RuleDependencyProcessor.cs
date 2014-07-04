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
using Antlr4.Runtime.Sharpen;
using Antlr4.Runtime.Sharpen.Annotation;
using Javax.Annotation.Processing;
using Javax.Lang.Model.Element;
using Javax.Lang.Model.Type;
using Javax.Tools;

namespace Antlr4.Runtime.Misc
{
    /// <summary>A compile-time validator for rule dependencies.</summary>
    /// <remarks>A compile-time validator for rule dependencies.</remarks>
    /// <seealso cref="Antlr4.Runtime.RuleDependency"/>
    /// <seealso cref="Antlr4.Runtime.RuleDependencies"/>
    /// <author>Sam Harwell</author>
    public class RuleDependencyProcessor : AbstractProcessor
    {
        public const string RuleDependencyClassName = "org.antlr.v4.runtime.RuleDependency";

        public const string RuleDependenciesClassName = "org.antlr.v4.runtime.RuleDependencies";

        public const string RuleVersionClassName = "org.antlr.v4.runtime.RuleVersion";

        public RuleDependencyProcessor()
        {
        }

        public override bool Process<_T0>(HashSet<_T0> annotations, IRoundEnvironment roundEnv)
        {
            if (!CheckClassNameConstants())
            {
                return true;
            }
            IList<Tuple<RuleDependency, IElement>> dependencies = GetDependencies(roundEnv);
            IDictionary<ITypeMirror, IList<Tuple<RuleDependency, IElement>>> recognizerDependencies = new Dictionary<ITypeMirror, IList<Tuple<RuleDependency, IElement>>>();
            foreach (Tuple<RuleDependency, IElement> dependency in dependencies)
            {
                ITypeMirror recognizerType = GetRecognizerType(dependency.Item1);
                IList<Tuple<RuleDependency, IElement>> list = recognizerDependencies.Get(recognizerType);
                if (list == null)
                {
                    list = new List<Tuple<RuleDependency, IElement>>();
                    recognizerDependencies.Put(recognizerType, list);
                }
                list.Add(dependency);
            }
            foreach (KeyValuePair<ITypeMirror, IList<Tuple<RuleDependency, IElement>>> entry in recognizerDependencies.EntrySet())
            {
                processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Note, string.Format("ANTLR 4: Validating {0} dependencies on rules in {1}.", entry.Value.Count, entry.Key.ToString()));
                CheckDependencies(entry.Value, entry.Key);
            }
            return true;
        }

        private bool CheckClassNameConstants()
        {
            bool success = CheckClassNameConstant(RuleDependencyClassName, typeof(RuleDependency));
            success &= CheckClassNameConstant(RuleDependenciesClassName, typeof(RuleDependencies));
            success &= CheckClassNameConstant(RuleVersionClassName, typeof(RuleVersion));
            return success;
        }

        private bool CheckClassNameConstant<_T0>(string className, System.Type<_T0> clazz)
        {
            Args.NotNull("className", className);
            Args.NotNull("clazz", clazz);
            if (!className.Equals(clazz.GetCanonicalName()))
            {
                processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Error, string.Format("Unable to process rule dependencies due to class name mismatch: {0} != {1}", className, clazz.GetCanonicalName()));
                return false;
            }
            return true;
        }

        private static ITypeMirror GetRecognizerType(RuleDependency dependency)
        {
            try
            {
                dependency.Recognizer();
                string message = string.Format("Expected {0} to get the {1}.", typeof(MirroredTypeException).Name, typeof(ITypeMirror).Name);
                throw new NotSupportedException(message);
            }
            catch (MirroredTypeException ex)
            {
                return ex.GetTypeMirror();
            }
        }

        private void CheckDependencies(IList<Tuple<RuleDependency, IElement>> dependencies, ITypeMirror recognizerType)
        {
            string[] ruleNames = GetRuleNames(recognizerType);
            int[] ruleVersions = GetRuleVersions(recognizerType, ruleNames);
            RuleDependencyProcessor.RuleRelations relations = ExtractRuleRelations(recognizerType);
            foreach (Tuple<RuleDependency, IElement> dependency in dependencies)
            {
                try
                {
                    if (!processingEnv.GetTypeUtils().IsAssignable(GetRecognizerType(dependency.Item1), recognizerType))
                    {
                        continue;
                    }
                    // this is the rule in the dependency set with the highest version number
                    int effectiveRule = dependency.Item1.Rule();
                    if (effectiveRule < 0 || effectiveRule >= ruleVersions.Length)
                    {
                        Tuple<IAnnotationMirror, IAnnotationValue> ruleReferenceElement = FindRuleDependencyProperty(dependency, RuleDependencyProcessor.RuleDependencyProperty.Rule);
                        string message = string.Format("Rule dependency on unknown rule {0}@{1} in {2}", dependency.Item1.Rule(), dependency.Item1.Version(), GetRecognizerType(dependency.Item1).ToString());
                        if (ruleReferenceElement != null)
                        {
                            processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Error, message, dependency.Item2, ruleReferenceElement.Item1, ruleReferenceElement.Item2);
                        }
                        else
                        {
                            processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Error, message, dependency.Item2);
                        }
                        continue;
                    }
                    EnumSet<Dependents> dependents = EnumSet.Of(Dependents.Self, dependency.Item1.Dependents());
                    ReportUnimplementedDependents(dependency, dependents);
                    BitSet @checked = new BitSet();
                    int highestRequiredDependency = CheckDependencyVersion(dependency, ruleNames, ruleVersions, effectiveRule, null);
                    if (dependents.Contains(Dependents.Parents))
                    {
                        BitSet parents = relations.parents[dependency.Item1.Rule()];
                        for (int parent = parents.NextSetBit(0); parent >= 0; parent = parents.NextSetBit(parent + 1))
                        {
                            if (parent < 0 || parent >= ruleVersions.Length || @checked.Get(parent))
                            {
                                continue;
                            }
                            @checked.Set(parent);
                            int required = CheckDependencyVersion(dependency, ruleNames, ruleVersions, parent, "parent");
                            highestRequiredDependency = Math.Max(highestRequiredDependency, required);
                        }
                    }
                    if (dependents.Contains(Dependents.Children))
                    {
                        BitSet children = relations.children[dependency.Item1.Rule()];
                        for (int child = children.NextSetBit(0); child >= 0; child = children.NextSetBit(child + 1))
                        {
                            if (child < 0 || child >= ruleVersions.Length || @checked.Get(child))
                            {
                                continue;
                            }
                            @checked.Set(child);
                            int required = CheckDependencyVersion(dependency, ruleNames, ruleVersions, child, "child");
                            highestRequiredDependency = Math.Max(highestRequiredDependency, required);
                        }
                    }
                    if (dependents.Contains(Dependents.Ancestors))
                    {
                        BitSet ancestors = relations.GetAncestors(dependency.Item1.Rule());
                        for (int ancestor = ancestors.NextSetBit(0); ancestor >= 0; ancestor = ancestors.NextSetBit(ancestor + 1))
                        {
                            if (ancestor < 0 || ancestor >= ruleVersions.Length || @checked.Get(ancestor))
                            {
                                continue;
                            }
                            @checked.Set(ancestor);
                            int required = CheckDependencyVersion(dependency, ruleNames, ruleVersions, ancestor, "ancestor");
                            highestRequiredDependency = Math.Max(highestRequiredDependency, required);
                        }
                    }
                    if (dependents.Contains(Dependents.Descendants))
                    {
                        BitSet descendants = relations.GetDescendants(dependency.Item1.Rule());
                        for (int descendant = descendants.NextSetBit(0); descendant >= 0; descendant = descendants.NextSetBit(descendant + 1))
                        {
                            if (descendant < 0 || descendant >= ruleVersions.Length || @checked.Get(descendant))
                            {
                                continue;
                            }
                            @checked.Set(descendant);
                            int required = CheckDependencyVersion(dependency, ruleNames, ruleVersions, descendant, "descendant");
                            highestRequiredDependency = Math.Max(highestRequiredDependency, required);
                        }
                    }
                    int declaredVersion = dependency.Item1.Version();
                    if (declaredVersion > highestRequiredDependency)
                    {
                        Tuple<IAnnotationMirror, IAnnotationValue> versionElement = FindRuleDependencyProperty(dependency, RuleDependencyProcessor.RuleDependencyProperty.Version);
                        string message = string.Format("Rule dependency version mismatch: {0} has maximum dependency version {1} (expected {2}) in {3}", ruleNames[dependency.Item1.Rule()], highestRequiredDependency, declaredVersion, GetRecognizerType(dependency.Item1).ToString());
                        if (versionElement != null)
                        {
                            processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Error, message, dependency.Item2, versionElement.Item1, versionElement.Item2);
                        }
                        else
                        {
                            processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Error, message, dependency.Item2);
                        }
                    }
                }
                catch (AnnotationTypeMismatchException)
                {
                    processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Warning, string.Format("Could not validate rule dependencies for element {0}", dependency.Item2.ToString()), dependency.Item2);
                }
            }
        }

        private static readonly HashSet<Dependents> ImplementedDependents = EnumSet.Of(Dependents.Self, Dependents.Parents, Dependents.Children, Dependents.Ancestors, Dependents.Descendants);

        private void ReportUnimplementedDependents(Tuple<RuleDependency, IElement> dependency, EnumSet<Dependents> dependents)
        {
            EnumSet<Dependents> unimplemented = dependents.Clone();
            unimplemented.RemoveAll(ImplementedDependents);
            if (!unimplemented.IsEmpty())
            {
                Tuple<IAnnotationMirror, IAnnotationValue> dependentsElement = FindRuleDependencyProperty(dependency, RuleDependencyProcessor.RuleDependencyProperty.Dependents);
                if (dependentsElement == null)
                {
                    dependentsElement = FindRuleDependencyProperty(dependency, RuleDependencyProcessor.RuleDependencyProperty.Rule);
                }
                string message = string.Format("Cannot validate the following dependents of rule {0}: {1}", dependency.Item1.Rule(), unimplemented);
                if (dependentsElement != null)
                {
                    processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Warning, message, dependency.Item2, dependentsElement.Item1, dependentsElement.Item2);
                }
                else
                {
                    processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Warning, message, dependency.Item2);
                }
            }
        }

        private int CheckDependencyVersion(Tuple<RuleDependency, IElement> dependency, string[] ruleNames, int[] ruleVersions, int relatedRule, string relation)
        {
            string ruleName = ruleNames[dependency.Item1.Rule()];
            string path;
            if (relation == null)
            {
                path = ruleName;
            }
            else
            {
                string mismatchedRuleName = ruleNames[relatedRule];
                path = string.Format("rule {0} ({1} of {2})", mismatchedRuleName, relation, ruleName);
            }
            int declaredVersion = dependency.Item1.Version();
            int actualVersion = ruleVersions[relatedRule];
            if (actualVersion > declaredVersion)
            {
                Tuple<IAnnotationMirror, IAnnotationValue> versionElement = FindRuleDependencyProperty(dependency, RuleDependencyProcessor.RuleDependencyProperty.Version);
                string message = string.Format("Rule dependency version mismatch: {0} has version {1} (expected <= {2}) in {3}", path, actualVersion, declaredVersion, GetRecognizerType(dependency.Item1).ToString());
                if (versionElement != null)
                {
                    processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Error, message, dependency.Item2, versionElement.Item1, versionElement.Item2);
                }
                else
                {
                    processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Error, message, dependency.Item2);
                }
            }
            return actualVersion;
        }

        private int[] GetRuleVersions(ITypeMirror recognizerClass, string[] ruleNames)
        {
            int[] versions = new int[ruleNames.Length];
            IList<IElement> elements = processingEnv.GetElementUtils().GetAllMembers((ITypeElement)processingEnv.GetTypeUtils().AsElement(recognizerClass));
            foreach (IElement element in elements)
            {
                if (element.GetKind() != ElementKind.Field)
                {
                    continue;
                }
                IVariableElement field = (IVariableElement)element;
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
                            string message = string.Format("Rule index {0} for rule '{1}' out of bounds for recognizer {2}.", index, name, recognizerClass.ToString());
                            processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Error, message, element);
                            continue;
                        }
                        if (name.IndexOf(ATNSimulator.RuleVariantDelimiter) >= 0)
                        {
                            // ignore left-factored pseudo-rules
                            continue;
                        }
                        IExecutableElement ruleMethod = GetRuleMethod(recognizerClass, name);
                        if (ruleMethod == null)
                        {
                            string message = string.Format("Could not find rule method for rule '{0}' in recognizer {1}.", name, recognizerClass.ToString());
                            processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Error, message, element);
                            continue;
                        }
                        RuleVersion ruleVersion = ruleMethod.GetAnnotation<RuleVersion>();
                        int version = ruleVersion != null ? ruleVersion.Value() : 0;
                        versions[index] = version;
                    }
                    catch (ArgumentException)
                    {
                        processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Error, "Exception occurred while validating rule dependencies.", element);
                    }
                }
            }
            return versions;
        }

        private IExecutableElement GetRuleMethod(ITypeMirror recognizerClass, string name)
        {
            IList<IElement> elements = processingEnv.GetElementUtils().GetAllMembers((ITypeElement)processingEnv.GetTypeUtils().AsElement(recognizerClass));
            foreach (IElement element in elements)
            {
                if (element.GetKind() != ElementKind.Method)
                {
                    continue;
                }
                IExecutableElement method = (IExecutableElement)element;
                if (method.GetSimpleName().ContentEquals(name) && HasRuleVersionAnnotation(method))
                {
                    return method;
                }
            }
            return null;
        }

        private bool HasRuleVersionAnnotation(IExecutableElement method)
        {
            ITypeElement ruleVersionAnnotationElement = processingEnv.GetElementUtils().GetTypeElement(RuleVersionClassName);
            if (ruleVersionAnnotationElement == null)
            {
                return false;
            }
            foreach (IAnnotationMirror annotation in method.GetAnnotationMirrors())
            {
                if (processingEnv.GetTypeUtils().IsSameType(annotation.GetAnnotationType(), ruleVersionAnnotationElement.AsType()))
                {
                    return true;
                }
            }
            return false;
        }

        private string[] GetRuleNames(ITypeMirror recognizerClass)
        {
            IList<string> result = new List<string>();
            IList<IElement> elements = processingEnv.GetElementUtils().GetAllMembers((ITypeElement)processingEnv.GetTypeUtils().AsElement(recognizerClass));
            foreach (IElement element in elements)
            {
                if (element.GetKind() != ElementKind.Field)
                {
                    continue;
                }
                IVariableElement field = (IVariableElement)element;
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
                            result.Add(string.Empty);
                        }
                        result.Set(index, name);
                    }
                    catch (ArgumentException)
                    {
                        processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Error, "Exception occurred while validating rule dependencies.", element);
                    }
                }
            }
            return Sharpen.Collections.ToArray(result, new string[result.Count]);
        }

        public static IList<Tuple<RuleDependency, IElement>> GetDependencies(IRoundEnvironment roundEnv)
        {
            IList<Tuple<RuleDependency, IElement>> result = new List<Tuple<RuleDependency, IElement>>();
            HashSet<IElement> elements = roundEnv.GetElementsAnnotatedWith(typeof(RuleDependency));
            foreach (IElement element in elements)
            {
                RuleDependency dependency = element.GetAnnotation<RuleDependency>();
                if (dependency == null)
                {
                    continue;
                }
                result.Add(Tuple.Create(dependency, element));
            }
            elements = roundEnv.GetElementsAnnotatedWith(typeof(RuleDependencies));
            foreach (IElement element_1 in elements)
            {
                RuleDependencies dependencies = element_1.GetAnnotation<RuleDependencies>();
                if (dependencies == null || dependencies.Value() == null)
                {
                    continue;
                }
                foreach (RuleDependency dependency in dependencies.Value())
                {
                    result.Add(Tuple.Create(dependency, element_1));
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
        private Tuple<IAnnotationMirror, IAnnotationValue> FindRuleDependencyProperty(Tuple<RuleDependency, IElement> dependency, RuleDependencyProcessor.RuleDependencyProperty property)
        {
            ITypeElement ruleDependencyTypeElement = processingEnv.GetElementUtils().GetTypeElement(RuleDependencyClassName);
            ITypeElement ruleDependenciesTypeElement = processingEnv.GetElementUtils().GetTypeElement(RuleDependenciesClassName);
            IList<IAnnotationMirror> mirrors = dependency.Item2.GetAnnotationMirrors();
            foreach (IAnnotationMirror annotationMirror in mirrors)
            {
                if (processingEnv.GetTypeUtils().IsSameType(ruleDependencyTypeElement.AsType(), annotationMirror.GetAnnotationType()))
                {
                    IAnnotationValue element = FindRuleDependencyProperty(dependency, annotationMirror, property);
                    if (element != null)
                    {
                        return Tuple.Create(annotationMirror, element);
                    }
                }
                else
                {
                    if (processingEnv.GetTypeUtils().IsSameType(ruleDependenciesTypeElement.AsType(), annotationMirror.GetAnnotationType()))
                    {
                        IDictionary<IExecutableElement, IAnnotationValue> values = annotationMirror.GetElementValues();
                        foreach (KeyValuePair<IExecutableElement, IAnnotationValue> value in values.EntrySet())
                        {
                            if ("value()".Equals(value.Key.ToString()))
                            {
                                IAnnotationValue annotationValue = value.Value;
                                if (!(annotationValue.GetValue() is IList))
                                {
                                    processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Warning, "Expected array of RuleDependency annotations for annotation property 'value()'.", dependency.Item2, annotationMirror, annotationValue);
                                    break;
                                }
                                IList<object> annotationValueList = (IList<object>)annotationValue.GetValue();
                                foreach (object obj in annotationValueList)
                                {
                                    if (!(obj is IAnnotationMirror))
                                    {
                                        processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Warning, "Expected RuleDependency annotation mirror for element of property 'value()'.", dependency.Item2, annotationMirror, annotationValue);
                                        break;
                                    }
                                    IAnnotationValue element = FindRuleDependencyProperty(dependency, (IAnnotationMirror)obj, property);
                                    if (element != null)
                                    {
                                        return Tuple.Create((IAnnotationMirror)obj, element);
                                    }
                                }
                            }
                            else
                            {
                                processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Error, string.Format("Unexpected annotation property {0}.", value.Key.ToString()), dependency.Item2, annotationMirror, value.Value);
                            }
                        }
                    }
                }
            }
            return null;
        }

        [Nullable]
        private IAnnotationValue FindRuleDependencyProperty(Tuple<RuleDependency, IElement> dependency, IAnnotationMirror annotationMirror, RuleDependencyProcessor.RuleDependencyProperty property)
        {
            IAnnotationValue recognizerValue = null;
            IAnnotationValue ruleValue = null;
            IAnnotationValue versionValue = null;
            IAnnotationValue dependentsValue = null;
            IDictionary<IExecutableElement, IAnnotationValue> values = annotationMirror.GetElementValues();
            foreach (KeyValuePair<IExecutableElement, IAnnotationValue> value in values.EntrySet())
            {
                IAnnotationValue annotationValue = value.Value;
                if ("rule()".Equals(value.Key.ToString()))
                {
                    ruleValue = annotationValue;
                    if (!(annotationValue.GetValue() is int))
                    {
                        processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Warning, "Expected int constant for annotation property 'rule()'.", dependency.Item2, annotationMirror, annotationValue);
                        return null;
                    }
                    if ((int)annotationValue.GetValue() != dependency.Item1.Rule())
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
                        if (!(annotationValue.GetValue() is ITypeMirror))
                        {
                            processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Warning, "Expected Class constant for annotation property 'recognizer()'.", dependency.Item2, annotationMirror, annotationValue);
                            return null;
                        }
                        ITypeMirror annotationRecognizer = (ITypeMirror)annotationValue.GetValue();
                        ITypeMirror expectedRecognizer = GetRecognizerType(dependency.Item1);
                        if (!processingEnv.GetTypeUtils().IsSameType(expectedRecognizer, annotationRecognizer))
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
                                processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Warning, "Expected int constant for annotation property 'version()'.", dependency.Item2, annotationMirror, annotationValue);
                                return null;
                            }
                            if ((int)annotationValue.GetValue() != dependency.Item1.Version())
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
                processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Warning, "Could not find 'recognizer()' element in annotation.", dependency.Item2, annotationMirror);
            }
            if (property == RuleDependencyProcessor.RuleDependencyProperty.Recognizer)
            {
                return null;
            }
            if (ruleValue == null)
            {
                processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Warning, "Could not find 'rule()' element in annotation.", dependency.Item2, annotationMirror);
            }
            if (property == RuleDependencyProcessor.RuleDependencyProperty.Rule)
            {
                return null;
            }
            if (versionValue == null)
            {
                processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Warning, "Could not find 'version()' element in annotation.", dependency.Item2, annotationMirror);
            }
            return null;
        }

        private RuleDependencyProcessor.RuleRelations ExtractRuleRelations(ITypeMirror recognizer)
        {
            string serializedATN = GetSerializedATN(recognizer);
            if (serializedATN == null)
            {
                return null;
            }
            ATN atn = new ATNDeserializer().Deserialize(serializedATN.ToCharArray());
            RuleDependencyProcessor.RuleRelations relations = new RuleDependencyProcessor.RuleRelations(atn.ruleToStartState.Length);
            foreach (ATNState state in atn.states)
            {
                if (!state.epsilonOnlyTransitions)
                {
                    continue;
                }
                foreach (Transition transition in state.Transitions)
                {
                    if (transition.TransitionType != TransitionType.Rule)
                    {
                        continue;
                    }
                    RuleTransition ruleTransition = (RuleTransition)transition;
                    relations.AddRuleInvocation(state.ruleIndex, ruleTransition.target.ruleIndex);
                }
            }
            return relations;
        }

        private string GetSerializedATN(ITypeMirror recognizerClass)
        {
            IList<IElement> elements = processingEnv.GetElementUtils().GetAllMembers((ITypeElement)processingEnv.GetTypeUtils().AsElement(recognizerClass));
            foreach (IElement element in elements)
            {
                if (element.GetKind() != ElementKind.Field)
                {
                    continue;
                }
                IVariableElement field = (IVariableElement)element;
                bool isStatic = element.GetModifiers().Contains(Modifier.Static);
                object constantValue = field.GetConstantValue();
                bool isString = constantValue is string;
                string name = field.GetSimpleName().ToString();
                if (isStatic && isString && name.Equals("_serializedATN"))
                {
                    return (string)constantValue;
                }
            }
            processingEnv.GetMessager().PrintMessage(Diagnostic.Kind.Error, "Could not retrieve serialized ATN from grammar.");
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
