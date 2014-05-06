package org.technozor.jzon.internal.processor;


import org.technozor.jzon.Writer;
import org.technozor.jzon.Jzon;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementScanner8;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import static org.technozor.jzon.internal.processor.JzonAnnotationVerifier.*;


/**
 * Created by slim on 4/28/14.
 */
public class CodeAnalyzerTreeVisitor extends ElementScanner8  {

    final ProcessingEnvironment pe;
    final Elements elementUtils ;

    final Types typeUtils;
    final Predicate<TypeElement> writerPredicate;
    final Predicate<TypeElement> jzonPredicate;
    static final Predicate<TypeMirror> declaredPredicate = x -> x.getKind().equals(TypeKind.DECLARED);
    static final Predicate<Element> annotationPredicate = x -> x.getKind().equals(ElementKind.ANNOTATION_TYPE);
    static final Function<Element,TypeElement> castToTypeElement = x -> (TypeElement) x;
    private final Predicate<TypeElement >isJzonAnnotationPresent ;
    private final Messager messager;





    public CodeAnalyzerTreeVisitor(ProcessingEnvironment pe) {
        this.pe = pe;
        elementUtils = pe.getElementUtils();
        writerPredicate = x ->  elementUtils.getName(Writer.class.getCanonicalName()).equals(x.getQualifiedName());
        jzonPredicate   = x ->   elementUtils.getName(Jzon.class.getCanonicalName()).equals(x.getQualifiedName());
        typeUtils = pe.getTypeUtils();
        isJzonAnnotationPresent = x -> isJzonAnnotationPresent(x);
        messager = pe.getMessager();
    }


   @Override
    public Object visitVariable(VariableElement e, Object aVoid) {

       try {
           TypeMirror typeMirror = e.asType();
           Element asElement = typeUtils.asElement(typeMirror);
           if (asElement.getKind().isInterface()
                   &&  writerPredicate.test((TypeElement) asElement) ==true
                   &&  declaredPredicate.test(typeMirror)) {

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

       return super.visitVariable(e, aVoid);
    }


    boolean isJzonAnnotationPresent(TypeElement typeElement) {
        return  typeElement.getAnnotationMirrors()
                .stream()
                .map(AnnotationMirror::getAnnotationType)
                .map(DeclaredType::asElement)
                .filter(annotationPredicate)
                .map(castToTypeElement)
                .filter(jzonPredicate)
                .findFirst()
                .isPresent();

    }

    private BiConsumer<ProcessingEnvironment,Exception> note = (p,e) -> p.getMessager().printMessage(Diagnostic.Kind.NOTE, e.getMessage());

}
