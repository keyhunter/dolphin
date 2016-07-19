package com.dolphin.rpc.core.utils;

import java.lang.reflect.Method;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Loader;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

public class ClassPoolUtil {

    public static void addAnnotation() {
        ClassPool pool = ClassPool.getDefault();
        Loader cl = new Loader(pool);
        try {
            CtClass cc = pool.get("com.dolphin.rpc.test.service.OrderService1");
            ClassFile ccFile = cc.getClassFile();
            ConstPool constpool = ccFile.getConstPool();

            // create the annotation
            AnnotationsAttribute attr = new AnnotationsAttribute(constpool,
                AnnotationsAttribute.visibleTag);
            Annotation annot = new Annotation("RPCService", constpool);
            StringMemberValue stringMemberValue = new StringMemberValue(constpool);
            stringMemberValue.setValue("orderService");
            annot.addMemberValue("value", stringMemberValue);
            attr.addAnnotation(annot);

            //            // create the method
            CtMethod mthd = CtNewMethod.make("public Integer getInteger() { return null; }", cc);
            cc.addMethod(mthd);
            mthd.getMethodInfo().addAttribute(attr);

            cc.writeFile("./");
            // generate the class
            Class<?> clazz = cc.toClass();

            // length is zero
            java.lang.annotation.Annotation[] annots = clazz.getAnnotations();
            Method[] methods = clazz.getMethods();
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
