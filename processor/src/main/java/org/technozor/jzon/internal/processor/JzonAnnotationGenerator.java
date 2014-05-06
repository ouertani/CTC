package org.technozor.jzon.internal.processor;

import com.squareup.javawriter.JavaWriter;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Created by slim on 5/5/14.
 */
@SupportedAnnotationTypes({"org.technozor.jzon.Jzon"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class JzonAnnotationGenerator extends AbstractProcessor {
    private Function<TypeElement, String> toPackage = x -> processingEnv.getElementUtils().getPackageOf(x).getQualifiedName().toString();
    private BiFunction<TypeElement, String, String> toClass = (x, y) -> new StringBuilder(toPackage.apply(x)).append(".").append(y).append(" implements  org.technozor.jzon.Writer<").append(x.getQualifiedName()).append(">").toString();
    private Function<TypeElement, List<? extends Element>> toParams = x -> ElementFilter.fieldsIn(x.getEnclosedElements());
    private Function<Element, String> toValue = x -> {
        String name = x.getSimpleName().toString();

        switch (x.asType().toString()) {
            case "java.lang.String":
                return "p." + name + "+\"\\\"";
            //TODO other type
            default:
                return "p." + name + "+\"\\\"";
        }

    };
    private Function<Element, String> elem2String = x -> new StringJoiner(": \\\"\" + ").add(x.getSimpleName().toString()).add(toValue.apply(x)).toString();
    private Function<List<? extends Element>, String> toJson = x -> "{" + x.stream().map(elem2String).collect(Collectors.joining(",")) + "}";
    private Consumer<Exception> warn = e -> processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, e.getMessage());

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(org.technozor.jzon.Jzon.class)
                .stream()
                .filter(element -> element.getKind().isClass())
                .map(x -> (TypeElement) x)
                .forEach(typeElement -> generateJsonclass(typeElement));
        return false;
    }

    private void generateJsonclass(TypeElement clazz) {
        String adapterName = new StringBuilder(clazz.getSimpleName()).append(JzonConstants.MAPPER_CLASS_SUFFIX).toString();

        try {
            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(adapterName, clazz);
            JavaWriter writer = new JavaWriter(sourceFile.openWriter());
            //  StringWriter stringWriter = new StringWriter();
            // JavaWriter writer = new JavaWriter(stringWriter);
            writer.emitSingleLineComment(JzonConstants.GENERATED_BY_JZON);

            writer.emitPackage(toPackage.apply(clazz))
                    .beginType(toClass.apply(clazz, adapterName), "class", EnumSet.of(PUBLIC, FINAL))
                    .emitJavadoc("Returns the json representation.")
                    .beginMethod("String", "apply", EnumSet.of(PUBLIC), clazz.getQualifiedName().toString(), "p")
                    .emitStatement("return \"" + toParams.andThen(toJson).apply(clazz) + "\" ")
                    .endMethod()
                    .endType();
            writer.close();


        } catch (Exception e) {
            warn.accept(e);
        }
    }
}
