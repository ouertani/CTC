package org.technozor.jzon.internal.processor;


import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static javax.tools.Diagnostic.Kind.ERROR;


/**
 * Created by slim on 4/24/14.
 */
@SupportedAnnotationTypes({"*"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class JzonAnnotationVerifier extends AbstractProcessor {

    public static Set<TypeElement> _notChecked = new HashSet<>();
    private Consumer<TypeElement> error = (e) -> processingEnv.getMessager().printMessage(ERROR, e.getQualifiedName() + " must be annotated by @Jzon annotation !");

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (roundEnv.processingOver()) return false;
        CodeAnalyzerTreeVisitor visitor = new CodeAnalyzerTreeVisitor(processingEnv);
        roundEnv.getRootElements().forEach(visitor::scan);
        _notChecked.forEach(error);
        return true;
    }

}
