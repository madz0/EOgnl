/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ASTChain;
import eognl.ASTConst;
import eognl.ClassCacheInspector;
import eognl.ClassResolver;
import eognl.DefaultClassResolver;
import eognl.ExPrimitiveDefaults;
import eognl.ExPrimitiveType;
import eognl.MemberAccess;
import eognl.MethodAccessor;
import eognl.MethodFailedException;
import eognl.NoSuchPropertyException;
import eognl.Node;
import eognl.NumericCasts;
import eognl.NumericDefaults;
import eognl.NumericLiterals;
import eognl.NumericValues;
import eognl.ObjectIndexedPropertyDescriptor;
import eognl.OgnlCache;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlOps;
import eognl.PrimitiveDefaults;
import eognl.PrimitiveTypes;
import eognl.PrimitiveWrapperClasses;
import eognl.PrimitiveWrapperType;
import eognl.PropertyAccessor;
import eognl.TypeConverter;
import eognl.enhance.ExpressionCompiler;
import eognl.enhance.OgnlExpressionCompiler;
import eognl.exenhance.DefaultObjectConstructor;
import eognl.exenhance.ObjectConstructor;
import eognl.exinternal.util.MutableInt;
import eognl.internal.CacheException;
import eognl.internal.entry.DeclaredMethodCacheEntry;
import eognl.internal.entry.GenericMethodParameterTypeCacheEntry;
import eognl.internal.entry.MethodAccessEntryValue;
import eognl.internal.entry.PermissionCacheEntry;
import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class EOgnlRuntime {
    public static final Object NotFound = new Object();
    public static final Object[] NoArguments = new Object[0];
    public static final Object NoConversionPossible = "ognl.NoConversionPossible";
    public static final int INDEXED_PROPERTY_NONE = 0;
    public static final int INDEXED_PROPERTY_INT = 1;
    public static final int INDEXED_PROPERTY_OBJECT = 2;
    public static final String NULL_STRING = "" + null;
    public static final String SET_PREFIX = "set";
    public static final String GET_PREFIX = "get";
    public static final String IS_PREFIX = "is";
    private static final Map<Integer, String> HEX_PADDING = new HashMap<Integer, String>();
    private static final int HEX_LENGTH = 8;
    private static final String NULL_OBJECT_STRING = "<null>";
    static OgnlCache cache = new OgnlCache();
    private static final PrimitiveTypes primitiveTypes = new PrimitiveTypes();
    private static final PrimitiveDefaults primitiveDefaults = new PrimitiveDefaults();
    private static SecurityManager securityManager = System.getSecurityManager();
    private static OgnlExpressionCompiler compiler;
    private static final PrimitiveWrapperClasses primitiveWrapperClasses;
    private static final NumericCasts numericCasts;
    private static final NumericValues numericValues;
    private static final NumericLiterals numericLiterals;
    private static final NumericDefaults numericDefaults;
    private static ExPrimitiveType exPrimitiveType;
    private static ExPrimitiveDefaults exPrimitiveDefults;
    private static PrimitiveWrapperType primitiveWrapperType;
    public static final String EXPAND_SIZE_KEY = OgnlContext.EXPAND_SIZE_KEY;
    public static final String GENERIC_PREFIX_KEY = OgnlContext.GENERIC_PREFIX_KEY;
    public static final String ARRAR_SOURCE_PREFIX_KEY = OgnlContext.ARRAR_SOURCE_PREFIX_KEY;
    public static final String EXPANDED_ARRAY_KEY = OgnlContext.EXPANDED_ARRAY_KEY;

    static {
        primitiveWrapperClasses = new PrimitiveWrapperClasses();
        numericCasts = new NumericCasts();
        numericValues = new NumericValues();
        numericLiterals = new NumericLiterals();
        numericDefaults = new NumericDefaults();
        exPrimitiveType = new ExPrimitiveType();
        exPrimitiveDefults = new ExPrimitiveDefaults();
        primitiveWrapperType = new PrimitiveWrapperType();
    }

    public static void clearCache() {
        cache.clear();
    }

    public static String getNumericValueGetter(Class<?> type) {
        return numericValues.get(type);
    }

    public static Class<?> getPrimitiveWrapperClass(Class<?> primitiveClass) {
        return primitiveWrapperClasses.get(primitiveClass);
    }

    public static String getNumericCast(Class<? extends Number> type) {
        return numericCasts.get(type);
    }

    public static String getNumericLiteral(Class<? extends Number> type) {
        return numericLiterals.get(type);
    }

    public static void setCompiler(OgnlExpressionCompiler compiler) {
        EOgnlRuntime.compiler = compiler;
    }

    public static OgnlExpressionCompiler getCompiler(OgnlContext ognlContext) {
        if (compiler == null) {
            try {
                EOgnlRuntime.classForName(ognlContext, "javassist.ClassPool");
                compiler = new ExpressionCompiler();
            }
            catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Javassist library is missing in classpath! Please add missed dependency!", e);
            }
        }
        return compiler;
    }

    public static void compileExpression(OgnlContext context, Node expression, Object root) throws Exception {
        EOgnlRuntime.getCompiler(context).compileExpression(context, expression, root);
    }

    public static Class<?> getTargetClass(Object o) {
        return o == null ? null : (o instanceof Class ? (Class<?>)o : o.getClass());
    }

    public static String getBaseName(Object o) {
        return o == null ? null : EOgnlRuntime.getClassBaseName(o.getClass());
    }

    public static String getClassBaseName(Class<?> clazz) {
        String className = clazz.getName();
        return className.substring(className.lastIndexOf(46) + 1);
    }

    public static String getClassName(Object object, boolean fullyQualified) {
        if (!(object instanceof Class)) {
            object = object.getClass();
        }
        return EOgnlRuntime.getClassName(object, fullyQualified);
    }

    public static String getClassName(Class<?> clazz, boolean fullyQualified) {
        return fullyQualified ? clazz.getName() : EOgnlRuntime.getClassBaseName(clazz);
    }

    public static String getPackageName(Object object) {
        return object == null ? null : EOgnlRuntime.getClassPackageName(object.getClass());
    }

    public static String getClassPackageName(Class<?> clazz) {
        String className = clazz.getName();
        int index = className.lastIndexOf(46);
        return index < 0 ? null : className.substring(0, index);
    }

    public static String getPointerString(int num) {
        String hex = Integer.toHexString(num);
        Integer l = hex.length();
        String pad = HEX_PADDING.get(l);
        if (pad == null) {
            StringBuilder paddingStringBuilder = new StringBuilder();
            for (int i = hex.length(); i < 8; ++i) {
                paddingStringBuilder.append('0');
            }
            pad = paddingStringBuilder.toString();
            HEX_PADDING.put(l, pad);
        }
        return pad + hex;
    }

    public static String getPointerString(Object object) {
        return EOgnlRuntime.getPointerString(object == null ? 0 : System.identityHashCode(object));
    }

    public static String getUniqueDescriptor(Object object, boolean fullyQualified) {
        StringBuilder stringBuilder = new StringBuilder();
        if (object != null) {
            if (object instanceof Proxy) {
                Class<?> interfaceClass = object.getClass().getInterfaces()[0];
                String className = EOgnlRuntime.getClassName(interfaceClass, fullyQualified);
                stringBuilder.append(className).append('^');
                object = Proxy.getInvocationHandler(object);
            }
            String className = EOgnlRuntime.getClassName(object, fullyQualified);
            String pointerString = EOgnlRuntime.getPointerString(object);
            stringBuilder.append(className).append('@').append(pointerString);
        } else {
            stringBuilder.append(NULL_OBJECT_STRING);
        }
        return stringBuilder.toString();
    }

    public static String getUniqueDescriptor(Object object) {
        return EOgnlRuntime.getUniqueDescriptor(object, false);
    }

    public static <T> Object[] toArray(List<T> list) {
        Object[] array;
        int size = list.size();
        if (size == 0) {
            array = NoArguments;
        } else {
            array = new Object[size];
            for (int i = 0; i < size; ++i) {
                array[i] = list.get(i);
            }
        }
        return array;
    }

    public static Class<?>[] getParameterTypes(Method method) throws CacheException {
        return cache.getMethodParameterTypes(method);
    }

    public static Class<?>[] findParameterTypes(Class<?> type, Method method) throws CacheException {
        if (type == null || type.getGenericSuperclass() == null || !ParameterizedType.class.isInstance(type.getGenericSuperclass()) || method.getDeclaringClass().getTypeParameters() == null) {
            return EOgnlRuntime.getParameterTypes(method);
        }
        GenericMethodParameterTypeCacheEntry key = new GenericMethodParameterTypeCacheEntry(method, type);
        return cache.getGenericMethodParameterTypes(key);
    }

    public static Class<?>[] getParameterTypes(Constructor<?> constructor) throws CacheException {
        return cache.getParameterTypes(constructor);
    }

    public static SecurityManager getSecurityManager() {
        return securityManager;
    }

    public static void setSecurityManager(SecurityManager securityManager) {
        EOgnlRuntime.securityManager = securityManager;
        cache.setSecurityManager(securityManager);
    }

    public static Permission getPermission(Method method) throws CacheException {
        PermissionCacheEntry key = new PermissionCacheEntry(method);
        return cache.getInvokePermission(key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Object invokeMethod(Object target, Method method, Object[] argsArray) throws InvocationTargetException, IllegalAccessException, CacheException {
        Object result;
        if (securityManager != null && !cache.getMethodPerm(method)) {
            throw new IllegalAccessException("Method [" + method + "] cannot be accessed.");
        }
        MethodAccessEntryValue entry = cache.getMethodAccess(method);
        if (!entry.isAccessible()) {
            Method method2 = method;
            synchronized (method2) {
                if (entry.isNotPublic() && !entry.isAccessible()) {
                    method.setAccessible(true);
                }
                result = method.invoke(target, argsArray);
                if (!entry.isAccessible()) {
                    method.setAccessible(false);
                }
            }
        } else {
            result = method.invoke(target, argsArray);
        }
        return result;
    }

    public static Class<?> getArgClass(Object arg) {
        if (arg == null) {
            return null;
        }
        Class<?> clazz = arg.getClass();
        if (clazz == Boolean.class) {
            return Boolean.TYPE;
        }
        if (clazz.getSuperclass() == Number.class) {
            if (clazz == Integer.class) {
                return Integer.TYPE;
            }
            if (clazz == Double.class) {
                return Double.TYPE;
            }
            if (clazz == Byte.class) {
                return Byte.TYPE;
            }
            if (clazz == Long.class) {
                return Long.TYPE;
            }
            if (clazz == Float.class) {
                return Float.TYPE;
            }
            if (clazz == Short.class) {
                return Short.TYPE;
            }
        } else if (clazz == Character.class) {
            return Character.TYPE;
        }
        return clazz;
    }

    public static boolean isTypeCompatible(Object object, Class<?> clazz) {
        boolean result = true;
        if (object != null) {
            if (clazz.isPrimitive()) {
                if (EOgnlRuntime.getArgClass(object) != clazz) {
                    result = false;
                }
            } else if (!clazz.isInstance(object)) {
                result = false;
            }
        }
        return result;
    }

    public static boolean areArgsCompatible(Object[] args, Class<?>[] classes) {
        return EOgnlRuntime.areArgsCompatible(args, classes, null);
    }

    public static boolean areArgsCompatible(Object[] args, Class<?>[] classes, Method method) {
        boolean result;
        boolean varArgs;
        result = true;
        boolean bl = varArgs = method != null && method.isVarArgs();
        if (args.length != classes.length && !varArgs) {
            result = false;
        } else if (varArgs) {
            for (int index = 0; result && index < args.length; ++index) {
                if (index < classes.length) {
                    result = EOgnlRuntime.isTypeCompatible(args[index], classes[index]);
                    if (result || !classes[index].isArray()) continue;
                    result = EOgnlRuntime.isTypeCompatible(args[index], classes[index].getComponentType());
                    continue;
                }
                break;
            }
        } else {
            for (int index = 0; result && index < args.length; ++index) {
                result = EOgnlRuntime.isTypeCompatible(args[index], classes[index]);
            }
        }
        return result;
    }

    public static boolean isMoreSpecific(Class<?>[] classes1, Class<?>[] classes2) {
        for (int index = 0; index < classes1.length; ++index) {
            Class<?> class1 = classes1[index];
            Class<?> class2 = classes2[index];
            if (class1 == class2) continue;
            if (class1.isPrimitive()) {
                return true;
            }
            if (class1.isAssignableFrom(class2)) {
                return false;
            }
            if (!class2.isAssignableFrom(class1)) continue;
            return true;
        }
        return false;
    }

    public static Class<?> classForName(OgnlContext context, String className) throws ClassNotFoundException {
        Class<?> result = primitiveTypes.get(className);
        if (result == null) {
            ClassResolver resolver;
            if (context == null || (resolver = context.getClassResolver()) == null) {
                resolver = OgnlContext.DEFAULT_CLASS_RESOLVER;
            }
            result = resolver.classForName(className, context);
        }
        if (result == null) {
            throw new ClassNotFoundException("Unable to resolve class: " + className);
        }
        return result;
    }

    public static boolean isInstance(OgnlContext context, Object value, String className) throws OgnlException {
        try {
            Class<?> clazz = EOgnlRuntime.classForName(context, className);
            return clazz.isInstance(value);
        }
        catch (ClassNotFoundException e) {
            throw new OgnlException("No such class: " + className, e);
        }
    }

    public static Object getPrimitiveDefaultValue(Class<?> forClass) {
        return primitiveDefaults.get(forClass);
    }

    public static Object getNumericDefaultValue(Class<?> forClass) {
        return numericDefaults.get(forClass);
    }

    public static Object getConvertedType(OgnlContext context, Object target, Member member, String propertyName, Object value, Class<?> type) {
        return context.getTypeConverter().convertValue(context, target, member, propertyName, value, type);
    }

    public static boolean getConvertedTypes(OgnlContext context, Object target, Member member, String propertyName, Class<?>[] parameterTypes, Object[] args, Object[] newArgs) {
        boolean result = false;
        if (parameterTypes.length == args.length) {
            result = true;
            for (int i = 0; result && i <= parameterTypes.length - 1; ++i) {
                Object arg = args[i];
                Class<?> type = parameterTypes[i];
                if (EOgnlRuntime.isTypeCompatible(arg, type)) {
                    newArgs[i] = arg;
                    continue;
                }
                Object convertedType = EOgnlRuntime.getConvertedType(context, target, member, propertyName, arg, type);
                if (convertedType == NoConversionPossible) {
                    result = false;
                    continue;
                }
                newArgs[i] = convertedType;
            }
        }
        return result;
    }

    public static Method getConvertedMethodAndArgs(OgnlContext context, Object target, String propertyName, List<Method> methods, Object[] args, Object[] newArgs) {
        Method convertedMethod = null;
        TypeConverter typeConverter = context.getTypeConverter();
        if (typeConverter != null && methods != null) {
            int methodsSize = methods.size();
            for (int i = 0; convertedMethod == null && i < methodsSize; ++i) {
                Class<?>[] parameterTypes;
                Method method = methods.get(i);
                if (!EOgnlRuntime.getConvertedTypes(context, target, method, propertyName, parameterTypes = EOgnlRuntime.findParameterTypes(target != null ? target.getClass() : null, method), args, newArgs)) continue;
                convertedMethod = method;
            }
        }
        return convertedMethod;
    }

    public static Constructor<?> getConvertedConstructorAndArgs(OgnlContext context, Object target, List<Constructor<?>> constructors, Object[] args, Object[] newArgs) {
        Constructor<?> constructor = null;
        TypeConverter typeConverter = context.getTypeConverter();
        if (typeConverter != null && constructors != null) {
            for (int i = 0; constructor == null && i < constructors.size(); ++i) {
                Class<?>[] parameterTypes;
                Constructor<?> ctor = constructors.get(i);
                if (!EOgnlRuntime.getConvertedTypes(context, target, ctor, null, parameterTypes = EOgnlRuntime.getParameterTypes(ctor), args, newArgs)) continue;
                constructor = ctor;
            }
        }
        return constructor;
    }

    public static Method getAppropriateMethod(OgnlContext context, Object source, Object target, String propertyName, List<Method> methods, Object[] args, Object[] actualArgs) {
        Method appropriateMethod = null;
        Class<?>[] resultParameterTypes = null;
        if (methods != null) {
            for (Method method : methods) {
                Class<?>[] mParameterTypes;
                Class typeClass;
                Class class_ = typeClass = target != null ? target.getClass() : null;
                if (typeClass == null && source != null && Class.class.isInstance(source)) {
                    typeClass = (Class)source;
                }
                if (!EOgnlRuntime.areArgsCompatible(args, mParameterTypes = EOgnlRuntime.findParameterTypes(typeClass, method), method) || appropriateMethod != null && !EOgnlRuntime.isMoreSpecific(mParameterTypes, resultParameterTypes)) continue;
                appropriateMethod = method;
                resultParameterTypes = mParameterTypes;
                System.arraycopy(args, 0, actualArgs, 0, args.length);
                for (int i = 0; i < mParameterTypes.length; ++i) {
                    Class<?> type = mParameterTypes[i];
                    if (!type.isPrimitive() || actualArgs[i] != null) continue;
                    actualArgs[i] = EOgnlRuntime.getConvertedType(context, source, appropriateMethod, propertyName, null, type);
                }
            }
        }
        if (appropriateMethod == null) {
            appropriateMethod = EOgnlRuntime.getConvertedMethodAndArgs(context, target, propertyName, methods, args, actualArgs);
        }
        return appropriateMethod;
    }

    public static Object callAppropriateMethod(OgnlContext context, Object source, Object target, String methodName, String propertyName, List<Method> methods, Object[] args) throws MethodFailedException {
        Throwable cause = null;
        Object[] actualArgs = new Object[args.length];
        try {
            Method method = EOgnlRuntime.getAppropriateMethod(context, source, target, propertyName, methods, args, actualArgs);
            if (method == null || !EOgnlRuntime.isMethodAccessible(context, source, method, propertyName)) {
                StringBuilder buffer = new StringBuilder();
                String className = "";
                if (target != null) {
                    className = String.valueOf(target.getClass().getName()) + ".";
                }
                int ilast = args.length - 1;
                for (int i = 0; i <= ilast; ++i) {
                    Object arg = args[i];
                    buffer.append(arg == null ? NULL_STRING : arg.getClass().getName());
                    if (i >= ilast) continue;
                    buffer.append(", ");
                }
                throw new NoSuchMethodException(String.valueOf(className) + methodName + "(" + buffer + ")");
            }
            Object[] convertedArgs = actualArgs;
            if (method.isVarArgs()) {
                Class<?>[] parmTypes = method.getParameterTypes();
                for (int i = 0; i < parmTypes.length; ++i) {
                    Object[] varArgs;
                    if (!parmTypes[i].isArray()) continue;
                    convertedArgs = new Object[i + 1];
                    System.arraycopy(actualArgs, 0, convertedArgs, 0, convertedArgs.length);
                    if (actualArgs.length > i) {
                        ArrayList<Object> varArgsList = new ArrayList<Object>();
                        for (int j = i; j < actualArgs.length; ++j) {
                            if (actualArgs[j] == null) continue;
                            varArgsList.add(actualArgs[j]);
                        }
                        varArgs = varArgsList.toArray();
                    } else {
                        varArgs = new Object[]{};
                    }
                    convertedArgs[i] = varArgs;
                    break;
                }
            }
            return EOgnlRuntime.invokeMethod(target, method, convertedArgs);
        }
        catch (NoSuchMethodException e) {
            cause = e;
        }
        catch (IllegalAccessException e) {
            cause = e;
        }
        catch (InvocationTargetException e) {
            cause = e.getTargetException();
        }
        throw new MethodFailedException(source, methodName, cause);
    }

    public static Object callStaticMethod(OgnlContext context, String className, String methodName, Object[] args) throws OgnlException {
        try {
            Class<?> targetClass = EOgnlRuntime.classForName(context, className);
            MethodAccessor methodAccessor = context.getMethodAccessor(targetClass);
            return methodAccessor.callStaticMethod(context, targetClass, methodName, args);
        }
        catch (ClassNotFoundException ex) {
            throw new MethodFailedException(className, methodName, ex);
        }
    }

    public static Object callMethod(OgnlContext context, Object target, String methodName, Object[] args) throws OgnlException {
        if (target == null) {
            throw new NullPointerException("target is null for method " + methodName);
        }
        return context.getMethodAccessor(target.getClass()).callMethod(context, target, methodName, args);
    }

    public static Object callConstructor(OgnlContext context, String className, Object[] args) throws OgnlException {
        Throwable cause = null;
        Object[] actualArgs = args;
        try {
            Constructor<?> ctor = null;
            Class<?>[] ctorParameterTypes = null;
            Class<?> target = EOgnlRuntime.classForName(context, className);
            List<Constructor<?>> constructors = EOgnlRuntime.getConstructors(target);
            for (Constructor<?> constructor : constructors) {
                Class<?>[] cParameterTypes = EOgnlRuntime.getParameterTypes(constructor);
                if (!EOgnlRuntime.areArgsCompatible(args, cParameterTypes) || ctor != null && !EOgnlRuntime.isMoreSpecific(cParameterTypes, ctorParameterTypes)) continue;
                ctor = constructor;
                ctorParameterTypes = cParameterTypes;
            }
            if (ctor == null && (ctor = EOgnlRuntime.getConvertedConstructorAndArgs(context, target, constructors, args, actualArgs = new Object[args.length])) == null) {
                throw new NoSuchMethodException();
            }
            if (!context.getMemberAccess().isAccessible(context, target, ctor, null)) {
                throw new IllegalAccessException("access denied to " + target.getName() + "()");
            }
            return ctor.newInstance(actualArgs);
        }
        catch (ClassNotFoundException e) {
            cause = e;
        }
        catch (NoSuchMethodException e) {
            cause = e;
        }
        catch (IllegalAccessException e) {
            cause = e;
        }
        catch (InvocationTargetException e) {
            cause = e.getTargetException();
        }
        catch (InstantiationException e) {
            cause = e;
        }
        throw new MethodFailedException(className, "new", cause);
    }

    public static Object getMethodValue(OgnlContext context, Object target, String propertyName) throws OgnlException, IllegalAccessException, NoSuchMethodException, IntrospectionException {
        return EOgnlRuntime.getMethodValue(context, target, propertyName, false);
    }

    public static Object getMethodValue(OgnlContext context, Object target, String propertyName, boolean checkAccessAndExistence) throws OgnlException, IllegalAccessException, NoSuchMethodException, IntrospectionException {
        Object methodValue = null;
        Class<?> targetClass = target == null ? null : target.getClass();
        Method method = EOgnlRuntime.getGetMethod(context, targetClass, propertyName);
        if (method == null) {
            method = EOgnlRuntime.getReadMethod(targetClass, propertyName, 0);
        }
        if (checkAccessAndExistence && (method == null || !context.getMemberAccess().isAccessible(context, target, method, propertyName))) {
            methodValue = NotFound;
        }
        if (methodValue == null) {
            if (method != null) {
                try {
                    methodValue = EOgnlRuntime.invokeMethod(target, method, NoArguments);
                }
                catch (InvocationTargetException ex) {
                    throw new OgnlException(propertyName, ex.getTargetException());
                }
            } else {
                throw new NoSuchMethodException(propertyName);
            }
        }
        return methodValue;
    }

    public static boolean setMethodValue(OgnlContext context, Object target, String propertyName, Object value) throws OgnlException, IllegalAccessException, NoSuchMethodException, IntrospectionException {
        return EOgnlRuntime.setMethodValue(context, target, propertyName, value, false);
    }

    public static boolean setMethodValue(OgnlContext context, Object target, String propertyName, Object value, boolean checkAccessAndExistence) throws OgnlException, IllegalAccessException, NoSuchMethodException, IntrospectionException {
        boolean result = true;
        Method method = EOgnlRuntime.getSetMethod(context, target == null ? null : target.getClass(), propertyName);
        if (checkAccessAndExistence && (method == null || !context.getMemberAccess().isAccessible(context, target, method, propertyName))) {
            result = false;
        }
        if (result) {
            if (method != null) {
                Object[] args = new Object[]{value};
                EOgnlRuntime.callAppropriateMethod(context, target, target, method.getName(), propertyName, Collections.nCopies(1, method), args);
            } else {
                result = false;
            }
        }
        return result;
    }

    public static List<Constructor<?>> getConstructors(Class<?> targetClass) {
        return cache.getConstructor(targetClass);
    }

    public static Map<String, List<Method>> getMethods(Class<?> targetClass, boolean staticMethods) {
        DeclaredMethodCacheEntry.MethodType type = staticMethods ? DeclaredMethodCacheEntry.MethodType.STATIC : DeclaredMethodCacheEntry.MethodType.NON_STATIC;
        DeclaredMethodCacheEntry key = new DeclaredMethodCacheEntry(targetClass, type);
        return cache.getMethod(key);
    }

    public static List<Method> getMethods(Class<?> targetClass, String name, boolean staticMethods) {
        return EOgnlRuntime.getMethods(targetClass, staticMethods).get(name);
    }

    public static Map<String, Field> getFields(Class<?> targetClass) {
        return cache.getField(targetClass);
    }

    public static Field getField(Class<?> inClass, String name) {
        Field field = EOgnlRuntime.getFields(inClass).get(name);
        if (field == null) {
            for (Class<?> superClass = inClass.getSuperclass(); superClass != null; superClass = superClass.getSuperclass()) {
                field = EOgnlRuntime.getFields(superClass).get(name);
                if (field == null) continue;
                return field;
            }
        }
        return field;
    }

    public static Object getFieldValue(OgnlContext context, Object target, String propertyName) throws NoSuchFieldException {
        return EOgnlRuntime.getFieldValue(context, target, propertyName, false);
    }

    public static Object getFieldValue(OgnlContext context, Object target, String propertyName, boolean checkAccessAndExistence) throws NoSuchFieldException {
        Object result = null;
        Class<?> targetClass = target == null ? null : target.getClass();
        Field field = EOgnlRuntime.getField(targetClass, propertyName);
        if (checkAccessAndExistence && (field == null || !context.getMemberAccess().isAccessible(context, target, field, propertyName))) {
            result = NotFound;
        }
        if (result == null) {
            if (field == null) {
                throw new NoSuchFieldException(propertyName);
            }
            try {
                if (Modifier.isStatic(field.getModifiers())) {
                    throw new NoSuchFieldException(propertyName);
                }
                Object state = context.getMemberAccess().setup(context, target, field, propertyName);
                result = field.get(target);
                context.getMemberAccess().restore(context, target, field, propertyName, state);
            }
            catch (IllegalAccessException ex) {
                throw new NoSuchFieldException(propertyName);
            }
        }
        return result;
    }

    public static boolean setFieldValue(OgnlContext context, Object target, String propertyName, Object value) throws OgnlException {
        boolean result;
        block6 : {
            result = false;
            try {
                Class<?> targetClass = target == null ? null : target.getClass();
                Field field = EOgnlRuntime.getField(targetClass, propertyName);
                if (field == null || Modifier.isStatic(field.getModifiers())) break block6;
                Object state = context.getMemberAccess().setup(context, target, field, propertyName);
                try {
                    if (EOgnlRuntime.isTypeCompatible(value, field.getType()) || (value = EOgnlRuntime.getConvertedType(context, target, field, propertyName, value, field.getType())) != null) {
                        field.set(target, value);
                        result = true;
                    }
                }
                finally {
                    context.getMemberAccess().restore(context, target, field, propertyName, state);
                }
            }
            catch (IllegalAccessException ex) {
                throw new NoSuchPropertyException(target, propertyName, ex);
            }
        }
        return result;
    }

    public static boolean isFieldAccessible(OgnlContext context, Object target, Class<?> inClass, String propertyName) {
        return EOgnlRuntime.isFieldAccessible(context, target, EOgnlRuntime.getField(inClass, propertyName), propertyName);
    }

    public static boolean isFieldAccessible(OgnlContext context, Object target, Field field, String propertyName) {
        return context.getMemberAccess().isAccessible(context, target, field, propertyName);
    }

    public static boolean hasField(OgnlContext context, Object target, Class<?> inClass, String propertyName) {
        Field field = EOgnlRuntime.getField(inClass, propertyName);
        return field != null && EOgnlRuntime.isFieldAccessible(context, target, field, propertyName);
    }

    public static Object getStaticField(OgnlContext context, String className, String fieldName) throws OgnlException {
        Exception cause;
        try {
            Class clazz = EOgnlRuntime.classForName(context, className);
            if ("class".equals(fieldName)) {
                return clazz;
            }
            if (clazz.isEnum()) {
                return Enum.valueOf(clazz, fieldName);
            }
            Field field = clazz.getField(fieldName);
            if (!Modifier.isStatic(field.getModifiers())) {
                throw new OgnlException("Field " + fieldName + " of class " + className + " is not static");
            }
            return field.get(null);
        }
        catch (ClassNotFoundException e) {
            cause = e;
        }
        catch (NoSuchFieldException e) {
            cause = e;
        }
        catch (SecurityException e) {
            cause = e;
        }
        catch (IllegalAccessException e) {
            cause = e;
        }
        throw new OgnlException("Could not get static field " + fieldName + " from class " + className, cause);
    }

    public static List<Method> getDeclaredMethods(Class<?> targetClass, String propertyName, boolean findSets) {
        String baseName = String.valueOf(Character.toUpperCase(propertyName.charAt(0))) + propertyName.substring(1);
        ArrayList<Method> methods = new ArrayList<Method>();
        ArrayList<String> methodNames = new ArrayList<String>(2);
        if (findSets) {
            methodNames.add(SET_PREFIX + baseName);
        } else {
            methodNames.add(IS_PREFIX + baseName);
            methodNames.add(GET_PREFIX + baseName);
        }
        for (String methodName : methodNames) {
            DeclaredMethodCacheEntry key = new DeclaredMethodCacheEntry(targetClass);
            List<Method> methodList = cache.getMethod(key).get(methodName);
            if (methodList == null) continue;
            methods.addAll(methodList);
        }
        return methods;
    }

    public static boolean isMethodCallable(Method method) {
        return !method.isSynthetic() && !Modifier.isVolatile(method.getModifiers());
    }

    public static Method getGetMethod(OgnlContext unused, Class<?> targetClass, String propertyName) throws IntrospectionException, OgnlException {
        Method result = null;
        List<Method> methods = EOgnlRuntime.getDeclaredMethods(targetClass, propertyName, false);
        if (methods != null) {
            for (Method method : methods) {
                Class<?>[] mParameterTypes = EOgnlRuntime.findParameterTypes(targetClass, method);
                if (mParameterTypes.length != 0) continue;
                result = method;
                break;
            }
        }
        return result;
    }

    public static boolean isMethodAccessible(OgnlContext context, Object target, Method method, String propertyName) {
        return method != null && context.getMemberAccess().isAccessible(context, target, method, propertyName);
    }

    public static boolean hasGetMethod(OgnlContext context, Object target, Class<?> targetClass, String propertyName) throws IntrospectionException, OgnlException {
        return EOgnlRuntime.isMethodAccessible(context, target, EOgnlRuntime.getGetMethod(context, targetClass, propertyName), propertyName);
    }

    public static Method getSetMethod(OgnlContext context, Class<?> targetClass, String propertyName) throws IntrospectionException, OgnlException {
        Method setMethod = null;
        List<Method> methods = EOgnlRuntime.getDeclaredMethods(targetClass, propertyName, true);
        if (methods != null) {
            for (Method method : methods) {
                Class<?>[] mParameterTypes = EOgnlRuntime.findParameterTypes(targetClass, method);
                if (mParameterTypes.length != 1) continue;
                setMethod = method;
                break;
            }
        }
        return setMethod;
    }

    public static boolean hasSetMethod(OgnlContext context, Object target, Class<?> targetClass, String propertyName) throws IntrospectionException, OgnlException {
        return EOgnlRuntime.isMethodAccessible(context, target, EOgnlRuntime.getSetMethod(context, targetClass, propertyName), propertyName);
    }

    public static boolean hasGetProperty(OgnlContext context, Object target, Object oname) throws IntrospectionException, OgnlException {
        String name;
        Class<?> targetClass = target == null ? null : target.getClass();
        return EOgnlRuntime.hasGetMethod(context, target, targetClass, name = oname.toString()) || EOgnlRuntime.hasField(context, target, targetClass, name);
    }

    public static boolean hasSetProperty(OgnlContext context, Object target, Object oname) throws IntrospectionException, OgnlException {
        String name;
        Class<?> targetClass = target == null ? null : target.getClass();
        return EOgnlRuntime.hasSetMethod(context, target, targetClass, name = oname.toString()) || EOgnlRuntime.hasField(context, target, targetClass, name);
    }

    public static Map<String, PropertyDescriptor> getPropertyDescriptors(Class<?> targetClass) throws IntrospectionException, OgnlException {
        return cache.getPropertyDescriptor(targetClass);
    }

    public static PropertyDescriptor getPropertyDescriptor(Class<?> targetClass, String propertyName) throws IntrospectionException, OgnlException {
        if (targetClass == null) {
            return null;
        }
        return EOgnlRuntime.getPropertyDescriptors(targetClass).get(propertyName);
    }

    public static PropertyDescriptor[] getPropertyDescriptorsArray(Class<?> targetClass) throws IntrospectionException, OgnlException {
        Collection<PropertyDescriptor> propertyDescriptors = EOgnlRuntime.getPropertyDescriptors(targetClass).values();
        return propertyDescriptors.toArray(new PropertyDescriptor[propertyDescriptors.size()]);
    }

    public static PropertyDescriptor getPropertyDescriptorFromArray(Class<?> targetClass, String name) throws IntrospectionException, OgnlException {
        PropertyDescriptor[] propertyDescriptors;
        PropertyDescriptor result = null;
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors = EOgnlRuntime.getPropertyDescriptorsArray(targetClass)) {
            if (result != null) break;
            if (propertyDescriptor.getName().compareTo(name) != 0) continue;
            result = propertyDescriptor;
        }
        return result;
    }

    public static Object getProperty(OgnlContext context, Object source, Object name) throws OgnlException {
        if (source == null) {
            throw new OgnlException("source is null for getProperty(null, \"" + name + "\")");
        }
        PropertyAccessor accessor = context.getPropertyAccessor(EOgnlRuntime.getTargetClass(source));
        if (accessor == null) {
            throw new OgnlException("No property accessor for " + EOgnlRuntime.getTargetClass(source).getName());
        }
        return accessor.getProperty(context, source, name);
    }

    public static void setProperty(OgnlContext context, Object target, Object name, Object value) throws OgnlException {
        if (target == null) {
            throw new OgnlException("target is null for setProperty(null, \"" + name + "\", " + value + ")");
        }
        PropertyAccessor accessor = context.getPropertyAccessor(EOgnlRuntime.getTargetClass(target));
        if (accessor == null) {
            throw new OgnlException("No property accessor for " + EOgnlRuntime.getTargetClass(target).getName());
        }
        accessor.setProperty(context, target, name, value);
    }

    public static int getIndexedPropertyType(OgnlContext context, Class<?> sourceClass, String name) throws OgnlException {
        int result = 0;
        try {
            PropertyDescriptor propertyDescriptor = EOgnlRuntime.getPropertyDescriptor(sourceClass, name);
            if (propertyDescriptor != null) {
                if (propertyDescriptor instanceof IndexedPropertyDescriptor) {
                    result = 1;
                } else if (propertyDescriptor instanceof ObjectIndexedPropertyDescriptor) {
                    result = 2;
                }
            }
        }
        catch (Exception ex) {
            throw new OgnlException("problem determining if '" + name + "' is an indexed property", ex);
        }
        return result;
    }

    public static Object getIndexedProperty(OgnlContext context, Object source, String name, Object index) throws OgnlException {
        Object[] args = new Object[]{index};
        try {
            Method method;
            PropertyDescriptor propertyDescriptor = EOgnlRuntime.getPropertyDescriptor(source == null ? null : source.getClass(), name);
            if (propertyDescriptor instanceof IndexedPropertyDescriptor) {
                method = ((IndexedPropertyDescriptor)propertyDescriptor).getIndexedReadMethod();
            } else if (propertyDescriptor instanceof ObjectIndexedPropertyDescriptor) {
                method = ((ObjectIndexedPropertyDescriptor)propertyDescriptor).getIndexedReadMethod();
            } else {
                throw new OgnlException("property '" + name + "' is not an indexed property");
            }
            return EOgnlRuntime.callMethod(context, source, method.getName(), args);
        }
        catch (OgnlException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new OgnlException("getting indexed property descriptor for '" + name + "'", ex);
        }
    }

    public static void setIndexedProperty(OgnlContext context, Object source, String name, Object index, Object value) throws OgnlException {
        Object[] args = new Object[]{index, value};
        try {
            Method method;
            PropertyDescriptor propertyDescriptor = EOgnlRuntime.getPropertyDescriptor(source == null ? null : source.getClass(), name);
            if (propertyDescriptor instanceof IndexedPropertyDescriptor) {
                method = ((IndexedPropertyDescriptor)propertyDescriptor).getIndexedWriteMethod();
            } else if (propertyDescriptor instanceof ObjectIndexedPropertyDescriptor) {
                method = ((ObjectIndexedPropertyDescriptor)propertyDescriptor).getIndexedWriteMethod();
            } else {
                throw new OgnlException("property '" + name + "' is not an indexed property");
            }
            EOgnlRuntime.callMethod(context, source, method.getName(), args);
        }
        catch (OgnlException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new OgnlException("getting indexed property descriptor for '" + name + "'", ex);
        }
    }

    public static void setClassCacheInspector(ClassCacheInspector inspector) {
        cache.setClassCacheInspector(inspector);
    }

    public static Method getMethod(OgnlContext context, Class<?> target, String name, Node[] children, boolean includeStatic) throws Exception {
        Class[] parms;
        if (children != null && children.length > 0) {
            parms = new Class[children.length];
            Class<?> currType = context.getCurrentType();
            Class<?> currAccessor = context.getCurrentAccessor();
            Object cast = context.get("_preCast");
            context.setCurrentObject(context.getRoot());
            context.setCurrentType(context.getRoot() != null ? context.getRoot().getClass() : null);
            context.setCurrentAccessor(null);
            context.setPreviousType(null);
            for (int i = 0; i < children.length; ++i) {
                children[i].toGetSourceString(context, context.getRoot());
                parms[i] = context.getCurrentType();
            }
            context.put("_preCast", cast);
            context.setCurrentType(currType);
            context.setCurrentAccessor(currAccessor);
            context.setCurrentObject(target);
        } else {
            parms = new Class[]{};
        }
        List<Method> methods = EOgnlRuntime.getMethods(target, name, includeStatic);
        if (methods == null) {
            return null;
        }
        for (Method method : methods) {
            boolean varArgs = method.isVarArgs();
            if (parms.length != method.getParameterTypes().length && !varArgs) continue;
            Class<?>[] methodParameterTypes = method.getParameterTypes();
            boolean matched = true;
            for (int i = 0; i < methodParameterTypes.length; ++i) {
                Class<?> methodParameterType = methodParameterTypes[i];
                if (varArgs && methodParameterType.isArray()) continue;
                Class parm = parms[i];
                if (parm == null) {
                    matched = false;
                    break;
                }
                if (parm == methodParameterType || methodParameterType.isPrimitive() && Character.TYPE != methodParameterType && Byte.TYPE != methodParameterType && Number.class.isAssignableFrom(parm) && EOgnlRuntime.getPrimitiveWrapperClass(parm) == methodParameterType) continue;
                matched = false;
                break;
            }
            if (!matched) continue;
            return method;
        }
        return null;
    }

    public static Method getReadMethod(Class<?> target, String name) {
        return EOgnlRuntime.getReadMethod(target, name, -1);
    }

    public static Method getReadMethod(Class<?> target, String name, int numParms) {
        try {
            name = name.replaceAll("\"", "").toLowerCase();
            BeanInfo info = Introspector.getBeanInfo(target);
            MethodDescriptor[] methodDescriptors = info.getMethodDescriptors();
            Method method = null;
            for (MethodDescriptor methodDescriptor : methodDescriptors) {
                if (!EOgnlRuntime.isMethodCallable(methodDescriptor.getMethod())) continue;
                String methodName = methodDescriptor.getName();
                String lowerMethodName = methodName.toLowerCase();
                int methodParamLen = methodDescriptor.getMethod().getParameterTypes().length;
                if (!methodName.equalsIgnoreCase(name) && !lowerMethodName.equals(GET_PREFIX + name) && !lowerMethodName.equals("has" + name) && !lowerMethodName.equals(IS_PREFIX + name) || methodName.startsWith(SET_PREFIX)) continue;
                if (numParms > 0 && methodParamLen == numParms) {
                    return methodDescriptor.getMethod();
                }
                if (numParms >= 0) continue;
                if (methodName.equals(name)) {
                    return methodDescriptor.getMethod();
                }
                if (method != null && method.getParameterTypes().length <= methodParamLen) continue;
                method = methodDescriptor.getMethod();
            }
            if (method != null) {
                return method;
            }
            for (MethodDescriptor methodDescriptor : methodDescriptors) {
                if (!EOgnlRuntime.isMethodCallable(methodDescriptor.getMethod()) || !methodDescriptor.getName().toLowerCase().endsWith(name) || methodDescriptor.getName().startsWith(SET_PREFIX) || methodDescriptor.getMethod().getReturnType() == Void.TYPE) continue;
                if (numParms > 0 && methodDescriptor.getMethod().getParameterTypes().length == numParms) {
                    return methodDescriptor.getMethod();
                }
                if (numParms >= 0 || method != null && method.getParameterTypes().length <= methodDescriptor.getMethod().getParameterTypes().length) continue;
                method = methodDescriptor.getMethod();
            }
            if (method != null) {
                return method;
            }
            if (!name.startsWith(GET_PREFIX)) {
                return EOgnlRuntime.getReadMethod(target, GET_PREFIX + name, numParms);
            }
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        return null;
    }

    public static Method getWriteMethod(Class<?> target, String name) {
        return EOgnlRuntime.getWriteMethod(target, name, -1);
    }

    public static Method getWriteMethod(Class<?> target, String name, int numParms) {
        try {
            MethodDescriptor[] methods;
            Method[] cmethods;
            name = name.replaceAll("\"", "");
            BeanInfo info = Introspector.getBeanInfo(target);
            MethodDescriptor[] arrmethodDescriptor = methods = info.getMethodDescriptors();
            int n = arrmethodDescriptor.length;
            for (int i = 0; i < n; ++i) {
                MethodDescriptor method = arrmethodDescriptor[i];
                if (!EOgnlRuntime.isMethodCallable(method.getMethod()) || !method.getName().equalsIgnoreCase(name) && !method.getName().toLowerCase().equals(name.toLowerCase()) && !method.getName().toLowerCase().equals(SET_PREFIX + name.toLowerCase()) || method.getName().startsWith(GET_PREFIX)) continue;
                if (numParms > 0 && method.getMethod().getParameterTypes().length == numParms) {
                    return method.getMethod();
                }
                if (numParms >= 0) continue;
                return method.getMethod();
            }
            for (Method cmethod : cmethods = target.getClass().getMethods()) {
                if (!EOgnlRuntime.isMethodCallable(cmethod) || !cmethod.getName().equalsIgnoreCase(name) && !cmethod.getName().toLowerCase().equals(name.toLowerCase()) && !cmethod.getName().toLowerCase().equals(SET_PREFIX + name.toLowerCase()) || cmethod.getName().startsWith(GET_PREFIX)) continue;
                if (numParms > 0 && cmethod.getParameterTypes().length == numParms) {
                    return cmethod;
                }
                if (numParms >= 0) continue;
                return cmethod;
            }
            if (!name.startsWith(SET_PREFIX)) {
                return EOgnlRuntime.getReadMethod(target, SET_PREFIX + name, numParms);
            }
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        return null;
    }

    public static PropertyDescriptor getProperty(Class<?> target, String name) {
        try {
            PropertyDescriptor[] propertyDescriptors;
            BeanInfo info = Introspector.getBeanInfo(target);
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors = info.getPropertyDescriptors()) {
                String propertyDescriptorName = propertyDescriptor.getName();
                if (!propertyDescriptorName.equalsIgnoreCase(name) && !propertyDescriptorName.toLowerCase().equals(name.toLowerCase()) && !propertyDescriptorName.toLowerCase().endsWith(name.toLowerCase())) continue;
                return propertyDescriptor;
            }
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        return null;
    }

    public static boolean isBoolean(String expression) {
        return expression != null && ("true".equals(expression) || "false".equals(expression) || "!true".equals(expression) || "!false".equals(expression) || "(true)".equals(expression) || "!(true)".equals(expression) || "(false)".equals(expression) || "!(false)".equals(expression) || expression.startsWith("org.apache.commons.ognl.OgnlOps"));
    }

    public static boolean shouldConvertNumericTypes(OgnlContext context) {
        Class<?> currentType = context.getCurrentType();
        Class<?> previousType = context.getPreviousType();
        return currentType == null || previousType == null || (currentType != previousType || !currentType.isPrimitive() || !previousType.isPrimitive()) && !currentType.isArray() && !previousType.isArray();
    }

    public static String getChildSource(OgnlContext context, Object target, Node child) throws OgnlException {
        return EOgnlRuntime.getChildSource(context, target, child, false);
    }

    public static String getChildSource(OgnlContext context, Object target, Node child, boolean forceConversion) throws OgnlException {
        String source;
        String pre = (String)context.get("_currentChain");
        if (pre == null) {
            pre = "";
        }
        try {
            child.getValue(context, target);
        }
        catch (NullPointerException nullPointerException) {
        }
        catch (ArithmeticException e) {
            context.setCurrentType(Integer.TYPE);
            return "0";
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        try {
            source = child.toGetSourceString(context, target);
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        if (!(ASTConst.class.isInstance(child) || target != null && context.getRoot() == target)) {
            source = String.valueOf(pre) + source;
        }
        if (context.getRoot() != null) {
            source = String.valueOf(ExpressionCompiler.getRootExpression(child, context.getRoot(), context)) + source;
            context.setCurrentAccessor(context.getRoot().getClass());
        }
        if (ASTChain.class.isInstance(child)) {
            String cast = (String)context.remove("_preCast");
            if (cast == null) {
                cast = "";
            }
            source = String.valueOf(cast) + source;
        }
        if (source == null || source.trim().length() < 1) {
            source = "null";
        }
        return source;
    }

    public static int incIndex(OgnlContext context) {
        return ((MutableInt)context.get(OgnlContext.CURRENT_INDEX_KEY)).incGet();
    }

    public static int decIndex(OgnlContext context) {
        return ((MutableInt)context.get(OgnlContext.CURRENT_INDEX_KEY)).decGet();
    }

    public static boolean isSetChain(OgnlContext context) {
        return context.containsKey(OgnlContext.EXPRESSION_SET);
    }

    public static boolean isNullInited(OgnlContext context) {
        return context.get(OgnlContext.INIT_NULLS_KEY) != null;
    }

    public static boolean isExpanded(OgnlContext context) {
        return context.get(EXPAND_SIZE_KEY) != null;
    }

    public static boolean isUnknownInited(OgnlContext context) {
        return context.get(OgnlContext.INIT_UNKNOWN_NULLS_KEY) != null;
    }

    public static boolean isFirstUnknownIgnored(OgnlContext context) {
        return context.get(OgnlContext.IGNORE_FIRST_UNKNOWN_KEY) != null;
    }

    public static boolean isFirstAlwaysIgnored(OgnlContext context) {
        return context.get(OgnlContext.ALWAYS_IGNORE_FIRST_KEY) != null;
    }

    public static boolean isUnknownIsLiteral(OgnlContext context) {
        return context.get(OgnlContext.UNKNOWN_TO_LITERAL_KEY) != null;
    }

    public static boolean isPrimitivesCasted(OgnlContext context) {
        return context.get(OgnlContext.CAST_PRIMITIVES_KEY) != null;
    }

    @Deprecated
    public static Object createProperObject(Class<?> cls, Class<?> componentType) throws InstantiationException, IllegalAccessException {
        ObjectConstructor objectConstructor = new DefaultObjectConstructor();
        return objectConstructor.createObject(cls, componentType);
    }

    public static boolean isPrimitiveOrWrapper(Type type) {
        if (type instanceof Class) {
            Class cls = (Class)type;
            return exPrimitiveType.get(cls.getName()) != null;
        }
        return false;
    }

    public static Object getPrimitivesDefult(Type type) {
        if (type instanceof Class) {
            Class cls = (Class)type;
            return exPrimitiveDefults.get(cls);
        }
        return null;
    }

    public static Object castPrimitive(Class<?> pCls, Object value) {
        if (!EOgnlRuntime.isPrimitiveOrWrapper(pCls)) {
            return value;
        }
        if (value.getClass() == pCls) {
            return value;
        }
        try {
            String castValue = String.valueOf(value);
            Method m = primitiveWrapperType.get(pCls).getMethod("valueOf", castValue.getClass());
            return m.invoke(null, castValue);
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            e.printStackTrace();
            return value;
        }
    }
}

