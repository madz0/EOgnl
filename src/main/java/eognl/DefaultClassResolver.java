/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ClassResolver;
import java.util.HashMap;
import java.util.Map;

public class DefaultClassResolver
implements ClassResolver {
    private final Map<String, Class<?>> classes = new HashMap(101);

    public Class<?> classForName(String className) throws ClassNotFoundException {
        return this.classForName(className, null);
    }

    @Override
    public Class<?> classForName(String className, Map<String, Object> unused) throws ClassNotFoundException {
        Class<?> result = this.classes.get(className);
        if (result == null) {
            block3 : {
                ClassLoader classLoader = ClassLoader.getSystemClassLoader();
                try {
                    result = classLoader.loadClass(className);
                }
                catch (ClassNotFoundException ex) {
                    if (className.indexOf(46) != -1) break block3;
                    result = classLoader.loadClass("java.lang." + className);
                    this.classes.put("java.lang." + className, result);
                }
            }
            this.classes.put(className, result);
        }
        return result;
    }
}

