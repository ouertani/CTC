package org.technozor.jzon.internal.processor;


import org.technozor.jzon.Jzon;
import org.technozor.jzon.Writer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementScanner8;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.technozor.jzon.internal.processor.JzonAnnotationVerifier._notChecked;


/**
 * Created by slim on 4/28/14.
 */
public class CodeAnalyzerTreeVisitor extends ElementScanner8<Void, ProcessingEnvironment> {

    static final Predicate<TypeMirror> declaredPredicate = x -> x.getKind().equals(TypeKind.DECLARED);
    static final Predicate<Element> annotationPredicate = x -> x.getKind().equals(ElementKind.ANNOTATION_TYPE);
    static final Function<Element, TypeElement> castToTypeElement = x -> (TypeElement) x;
    final ProcessingEnvironment pe;
    final Elements elementUtils;
    final Types typeUtils;
    final Predicate<TypeElement> writerPredicate;
    final Predicate<TypeElement> jzonPredicate;
    private final Predicate<TypeElement> isJzonAnnotationPresent;

    private final Function<ProcessingEnvironment, Elements> toElems = ProcessingEnvironment::getElementUtils;
    private final BiPredicate<TypeElement, Class> hasQualifiedName;


    public CodeAnalyzerTreeVisitor(ProcessingEnvironment pe) {
        this.pe = pe;
        elementUtils = toElems.apply(pe);
        hasQualifiedName = (x, y) -> elementUtils.getName(y.getCanonicalName()).equals(x.getQualifiedName());
        writerPredicate = x -> hasQualifiedName.test(x, Writer.class);
        jzonPredicate = x -> hasQualifiedName.test(x, Jzon.class);
        typeUtils = pe.getTypeUtils();
        isJzonAnnotationPresent = x -> isJzonAnnotationPresent(x);
    }

    @Override
    public Void visitVariable(VariableElement e, ProcessingEnvironment processingEnvironment) {

        try {
            TypeMirror typeMirror = e.asType();
            Element asElement = typeUtils.asElement(typeMirror);
            if (asElement.getKind().isInterface()
                    && writerPredicate.test((TypeElement) asElement) == true
                    && declaredPredicate.test(typeMirror)) {

                DeclaredType dt = (DeclaredType) typeMirror;

                dt.getTypeArguments()
                        .stream()
                        .map(typeUtils::asElement)
                        .filter(x -> x.getKind().isClass())
                        .map(castToTypeElement)
                        .filter(isJzonAnnotationPresent.negate())
                        .forEach(_notChecked::add);
            }

        } catch (Exception ex) {

        }
        return super.visitVariable(e, processingEnvironment);
    }

    boolean isJzonAnnotationPresent(TypeElement typeElement) {
        return typeElement.getAnnotationMirrors()
                .stream()
                .map(AnnotationMirror::getAnnotationType)
                .map(DeclaredType::asElement)
                .filter(annotationPredicate)
                .map(castToTypeElement)
                .filter(jzonPredicate)
                .findFirst()
                .isPresent();

    }


}
