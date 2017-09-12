package com.dolphin.core.utils;

import javassist.*;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

/**
 * 此类操作反射，现在不可用，请不要尝试
 *
 * @author keyhunter
 * @version $Id: ClassUtil.java, v 0.1 2016年7月20日 下午1:09:21 keyhunter Exp $
 */
@Deprecated
public class ClassUtil {

    private static final Logger logger = LoggerFactory.getLogger(ClassUtil.class);


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
            logger.error("newInstance error", e);
            return null;
        }
    }

    public static void main(String[] args) {
        Object newInstance = newInstance(Timestamp.class);
        logger.info(newInstance + "");
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
    //            logger.info();
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //    }

}
