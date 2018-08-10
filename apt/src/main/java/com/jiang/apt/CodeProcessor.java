package com.jiang.apt;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
public class CodeProcessor extends AbstractProcessor {

    private static final String SUFFIX = "$requestInfo";

    private Messager messager;

    private Filer filer;

    private Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        this.messager = processingEnvironment.getMessager();
        this.filer = processingEnvironment.getFiler();
        this.typeUtils = processingEnvironment.getTypeUtils();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Code.class.getCanonicalName());
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(Code.class)) {
            Code code = element.getAnnotation(Code.class);
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            try {
                generateCode(element, code, typeElement);
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
                return false;
            }
        }
        return true;
    }

    private void generateCode(Element element, Code code, TypeElement clazz) throws IOException {
        JavaFileObject fileObject = filer.createSourceFile(clazz.getQualifiedName() + SUFFIX);
        messager.printMessage(Diagnostic.Kind.NOTE, "Creating" + fileObject.toUri());
        Writer writer = fileObject.openWriter();
        try {
            String pack = clazz.getQualifiedName().toString();
            PrintWriter pw = new PrintWriter(writer);
            pw.println("package " + pack.substring(0, pack.lastIndexOf('.')) + ";"); //create package element
            pw.println("\n class " + clazz.getSimpleName() + "Autogenerate {");//create class element
            pw.println("\n    protected " + clazz.getSimpleName() + "Autogenerate() {}");//create class construction
            pw.println("    protected final void message() {");//create method
            pw.println("\n//" + element);
            pw.println("//" + code);
            pw.println("\n        System.out.println(\"author:" + code.author() + "\");");
            pw.println("\n        System.out.println(\"date:" + code.date() + "\");");
            pw.println("    }");
            pw.println("}");
            pw.flush();
        } finally {
            writer.close();
        }
    }
}
