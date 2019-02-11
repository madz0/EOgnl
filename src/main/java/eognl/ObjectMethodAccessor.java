/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.EOgnlRuntime;
import eognl.MethodAccessor;
import eognl.MethodFailedException;
import eognl.OgnlContext;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class ObjectMethodAccessor
implements MethodAccessor {
    @Override
    public Object callStaticMethod(Map<String, Object> context, Class<?> targetClass, String methodName, Object[] args) throws MethodFailedException {
        List<Method> methods = EOgnlRuntime.getMethods(targetClass, methodName, true);
        return EOgnlRuntime.callAppropriateMethod((OgnlContext)context, targetClass, null, methodName, null, methods, args);
    }

    @Override
    public Object callMethod(Map<String, Object> context, Object target, String methodName, Object[] args) throws MethodFailedException {
        Class<?> targetClass = target == null ? null : target.getClass();
        List<Method> methods = EOgnlRuntime.getMethods(targetClass, methodName, false);
        if (methods == null || methods.size() == 0) {
            methods = EOgnlRuntime.getMethods(targetClass, methodName, true);
        }
        return EOgnlRuntime.callAppropriateMethod((OgnlContext)context, target, target, methodName, null, methods, args);
    }
}

