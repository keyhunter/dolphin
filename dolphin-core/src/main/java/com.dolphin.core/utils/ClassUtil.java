package com.dolphin.core.utils;

import java.sql.Timestamp;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.Loader;
import javassist.Modifier;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;

/**
 * 此类操作反射，现在不可用，请不要尝试
 * @author jiujie
 * @version $Id: ClassUtil.java, v 0.1 2016年7月20日 下午1:09:21 jiujie Exp $
 */
@Deprecated
public class ClassUtil {

    public static Object newInstance(Class<?> clazz) {
        ClassPool pool = ClassPool.getDefault();
        Loader cl = new Loader(pool);
        try {
            CtClass cc = pool.get(clazz.getName());
            ClassFile ccFile = cc.getClassFile();
            ConstPool constpool = ccFile.getConstPool();
            // 为类设置构造器  
            // 无参构造器  
            CtConstructor constructor = new CtConstructor(null, cc);
            constructor.setModifiers(Modifier.PUBLIC);
            constructor.setBody("{}");
            cc.addConstructor(constructor);
//            cc.writeFile("./");
            // generate the class
            Class<?> newClazz = cc.toClass();
            return newClazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void main(String[] args) {
        Object newInstance = newInstance(Timestamp.class);
        System.out.println(newInstance);
    }

    //    public static void addAnnotation() {
    //        ClassPool pool = ClassPool.getDefault();
    //        Loader cl = new Loader(pool);
    //        try {
    //            CtClass cc = pool.get("com.dolphin.rpc.test.service.OrderService1");
    //            ClassFile ccFile = cc.getClassFile();
    //            ConstPool constpool = ccFile.getConstPool();
    //
    //            // create the annotation
    //            AnnotationsAttribute attr = new AnnotationsAttribute(constpool,
    //                AnnotationsAttribute.visibleTag);
    //            Annotation annot = new Annotation("RPCService", constpool);
    //            StringMemberValue stringMemberValue = new StringMemberValue(constpool);
    //            stringMemberValue.setValue("orderService");
    //            annot.addMemberValue("value", stringMemberValue);
    //            attr.addAnnotation(annot);
    //
    //            //            // create the method
    //            CtMethod mthd = CtNewMethod.make("public Integer getInteger() { return null; }", cc);
    //            cc.addMethod(mthd);
    //            mthd.getMethodInfo().addAttribute(attr);
    //
    //            cc.writeFile("./");
    //            // generate the class
    //            Class<?> clazz = cc.toClass();
    //
    //            // length is zero
    //            java.lang.annotation.Annotation[] annots = clazz.getAnnotations();
    //            Method[] methods = clazz.getMethods();
    //            System.out.println();
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //    }

}
