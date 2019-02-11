/*
 * Decompiled with CFR 0.139.
 */
package eognl.internal.entry;

import eognl.EOgnlRuntime;
import eognl.ObjectIndexedPropertyDescriptor;
import eognl.OgnlException;
import eognl.internal.CacheException;
import eognl.internal.entry.ClassCacheEntryFactory;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PropertyDescriptorCacheEntryFactory
implements ClassCacheEntryFactory<Map<String, PropertyDescriptor>> {
    @Override
    public Map<String, PropertyDescriptor> create(Class<?> targetClass) throws CacheException {
        HashMap<String, PropertyDescriptor> result = new HashMap<String, PropertyDescriptor>(101);
        try {
            PropertyDescriptor[] pda;
            for (PropertyDescriptor aPda : pda = Introspector.getBeanInfo(targetClass).getPropertyDescriptors()) {
                if (aPda.getReadMethod() != null && !EOgnlRuntime.isMethodCallable(aPda.getReadMethod())) {
                    aPda.setReadMethod(PropertyDescriptorCacheEntryFactory.findClosestMatchingMethod(targetClass, aPda.getReadMethod(), aPda.getName(), aPda.getPropertyType(), true));
                }
                if (aPda.getWriteMethod() != null && !EOgnlRuntime.isMethodCallable(aPda.getWriteMethod())) {
                    aPda.setWriteMethod(PropertyDescriptorCacheEntryFactory.findClosestMatchingMethod(targetClass, aPda.getWriteMethod(), aPda.getName(), aPda.getPropertyType(), false));
                }
                result.put(aPda.getName(), aPda);
            }
            PropertyDescriptorCacheEntryFactory.findObjectIndexedPropertyDescriptors(targetClass, result);
        }
        catch (IntrospectionException e) {
            throw new CacheException(e);
        }
        catch (OgnlException e) {
            throw new CacheException(e);
        }
        return result;
    }

    static Method findClosestMatchingMethod(Class<?> targetClass, Method m, String propertyName, Class<?> propertyType, boolean isReadMethod) throws OgnlException {
        List<Method> methods = EOgnlRuntime.getDeclaredMethods(targetClass, propertyName, !isReadMethod);
        for (Method method : methods) {
            if (!method.getName().equals(m.getName()) || !m.getReturnType().isAssignableFrom(m.getReturnType()) || method.getReturnType() != propertyType || method.getParameterTypes().length != m.getParameterTypes().length) continue;
            return method;
        }
        return m;
    }

    private static void findObjectIndexedPropertyDescriptors(Class<?> targetClass, Map<String, PropertyDescriptor> intoMap) throws OgnlException {
        List<Method> methods;
        Map<String, List<Method>> allMethods = EOgnlRuntime.getMethods(targetClass, false);
        HashMap pairs = new HashMap(101);
        for (Map.Entry<String, List<Method>> entry : allMethods.entrySet()) {
            List<Method> pair;
            String methodName = entry.getKey();
            methods = entry.getValue();
            if (!PropertyDescriptorCacheEntryFactory.indexMethodCheck(methods)) continue;
            boolean isGet = false;
            Method method = methods.get(0);
            boolean isSet = methodName.startsWith("set");
            if (!isSet && !(isGet = methodName.startsWith("get")) || methodName.length() <= 3) continue;
            String propertyName = Introspector.decapitalize(methodName.substring(3));
            Class<?>[] parameterTypes = EOgnlRuntime.getParameterTypes(method);
            int parameterCount = parameterTypes.length;
            if (isGet && parameterCount == 1 && method.getReturnType() != Void.TYPE) {
                pair = (ArrayList<Method>)pairs.get(propertyName);
                if (pair == null) {
                    pair = new ArrayList<Method>();
                    pairs.put(propertyName, pair);
                }
                pair.add(method);
            }
            if (!isSet || parameterCount != 2 || method.getReturnType() != Void.TYPE) continue;
            pair = (List)pairs.get(propertyName);
            if (pair == null) {
                pair = new ArrayList();
                pairs.put(propertyName, pair);
            }
            pair.add(method);
        }
        for (Map.Entry<String, List<Method>> entry : ((Map<String, List<Method>>)pairs).entrySet()) {
            ObjectIndexedPropertyDescriptor propertyDescriptor;
            String propertyName = entry.getKey();
            methods = entry.getValue();
            if (methods.size() != 2) continue;
            Method method1 = methods.get(0);
            Method method2 = methods.get(1);
            Method setMethod = method1.getParameterTypes().length == 2 ? method1 : method2;
            Method getMethod = setMethod == method1 ? method2 : method1;
            Class<?> keyType = getMethod.getParameterTypes()[0];
            Class<?> propertyType = getMethod.getReturnType();
            if (keyType != setMethod.getParameterTypes()[0] || propertyType != setMethod.getParameterTypes()[1]) continue;
            try {
                propertyDescriptor = new ObjectIndexedPropertyDescriptor(propertyName, propertyType, getMethod, setMethod);
            }
            catch (Exception ex) {
                throw new OgnlException("creating object indexed property descriptor for '" + propertyName + "' in " + targetClass, ex);
            }
            intoMap.put(propertyName, propertyDescriptor);
        }
    }

    private static boolean indexMethodCheck(List<Method> methods) {
        boolean result = false;
        if (methods.size() > 0) {
            Method method = methods.get(0);
            Class<?>[] parameterTypes = EOgnlRuntime.getParameterTypes(method);
            int numParameterTypes = parameterTypes.length;
            Class<?> lastMethodClass = method.getDeclaringClass();
            result = true;
            for (int i = 1; result && i < methods.size(); ++i) {
                Class<?> clazz = methods.get(i).getDeclaringClass();
                if (lastMethodClass == clazz) {
                    result = false;
                } else {
                    Class<?>[] mpt = EOgnlRuntime.getParameterTypes(method);
                    int mpc = parameterTypes.length;
                    if (numParameterTypes != mpc) {
                        result = false;
                    }
                    for (int j = 0; j < numParameterTypes; ++j) {
                        if (parameterTypes[j] == mpt[j]) continue;
                        result = false;
                        break;
                    }
                }
                lastMethodClass = clazz;
            }
        }
        return result;
    }
}

