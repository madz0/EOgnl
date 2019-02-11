/*
 * Decompiled with CFR 0.139.
 */
package eognl.enhance;

public class EnhancedClassLoader
extends ClassLoader {
    public EnhancedClassLoader(ClassLoader parentClassLoader) {
        super(parentClassLoader);
    }

    public Class<?> defineClass(String enhancedClassName, byte[] byteCode) {
        return this.defineClass(enhancedClassName, byteCode, 0, byteCode.length);
    }
}

