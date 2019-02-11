/*
 * Decompiled with CFR 0.139.
 */
package eognl.enhance;

import eognl.ClassResolver;
import eognl.OgnlContext;
import java.util.Map;

public class ContextClassLoader
extends ClassLoader {
    private final OgnlContext context;

    public ContextClassLoader(ClassLoader parentClassLoader, OgnlContext context) {
        super(parentClassLoader);
        this.context = context;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (this.context != null && this.context.getClassResolver() != null) {
            return this.context.getClassResolver().classForName(name, this.context);
        }
        return super.findClass(name);
    }
}

