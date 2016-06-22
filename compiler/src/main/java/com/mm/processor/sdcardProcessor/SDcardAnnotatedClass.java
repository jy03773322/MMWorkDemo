package com.mm.processor.sdcardProcessor;


import com.mm.annotation.SDCardRootFile;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;


/**
 * Created by yeungeek on 2016/4/27.
 */
class SDcardAnnotatedClass {
    public final VariableElement typeElement;

    private String qualifiedSuperClassName;//规范命名
    private String annotatedClassName; //简单命名
    private String simpleTypeName; //简单命名

    private String[] fileNames;//属性
    private String appRootPathName; //路径命名

    public SDcardAnnotatedClass(VariableElement typeElement) {
        this.appRootPathName = (String) typeElement.getConstantValue();
        this.annotatedClassName = typeElement.getSimpleName().toString();
        this.typeElement = typeElement;
        SDCardRootFile annotation = typeElement.getAnnotation(SDCardRootFile.class);
        fileNames = annotation.fileNames();

        // Get the full QualifiedTypeName
        try {
            Class<?> clazz = annotation.annotationType();
            qualifiedSuperClassName = clazz.getCanonicalName();
            simpleTypeName = clazz.getSimpleName();
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            qualifiedSuperClassName = classTypeElement.getQualifiedName().toString();
            simpleTypeName = classTypeElement.getSimpleName().toString();
        }
    }

    public String getQualifiedSuperClassName() {
        return qualifiedSuperClassName;
    }

    public String getSimpleTypeName() {
        return simpleTypeName;
    }

    public VariableElement getTypeElement() {
        return typeElement;
    }

    public String getAnnotatedClassName() {
        return annotatedClassName;
    }

    public String[] getFileNames() {
        return fileNames;
    }

    public String getAppRootPathName() {
        return appRootPathName;
    }
}
