/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime;

import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 I think I figured out how to use annotation processors in maven.  It's
 more or less automatic and you don't even need to tell maven, with one minor
 exception. The idea is to create a project for the annotation and another
 for the annotation processor. Then, a project that uses the annotation
 can simply set up the dependency on the other projects. You have to turn
 off processing, -proc:none on the processor project itself but other than
 that, java 6+ more or less tries to apply any processors it finds during
 compilation. maven just works.

 Also you need a META-INF/services/javax.annotation.processing.Processor file
 with "org.antlr.v4.test.runtime.CommentHasStringValueProcessor" in it.
 */
@SupportedAnnotationTypes({"org.antlr.v4.test.runtime.CommentHasStringValue"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class CommentHasStringValueProcessor extends AbstractProcessor {
	protected JavacElements utilities;
	protected TreeMaker treeMaker;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
//		Messager messager = processingEnv.getMessager();
//		messager.printMessage(Diagnostic.Kind.NOTE, "WOW INIT--------------------");
		JavacProcessingEnvironment javacProcessingEnv = (JavacProcessingEnvironment) processingEnv;
		utilities = javacProcessingEnv.getElementUtils();
		treeMaker = TreeMaker.instance(javacProcessingEnv.getContext());
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//		Messager messager = processingEnv.getMessager();
//		messager.printMessage(Diagnostic.Kind.NOTE, "PROCESS--------------------");
		Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(CommentHasStringValue.class);
		for (Element annotatedElement : annotatedElements) {
			String docComment = utilities.getDocComment(annotatedElement);
			JCTree.JCLiteral literal = treeMaker.Literal(docComment!=null ? docComment : "");
			JCTree elementTree = utilities.getTree(annotatedElement);
			if ( elementTree instanceof JCTree.JCVariableDecl ) {
				((JCTree.JCVariableDecl)elementTree).init = literal;
			}
			else if ( elementTree instanceof JCTree.JCMethodDecl ) {
				JCTree.JCStatement[] statements = new JCTree.JCStatement[1];
				statements[0] = treeMaker.Return(literal);
				JCTree.JCBlock body = treeMaker.Block(0, List.from(statements));
				((JCTree.JCMethodDecl)elementTree).body = body;
			}
		}
		return true;
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}
}
