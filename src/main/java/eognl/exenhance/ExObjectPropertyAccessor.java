/*
 * Decompiled with CFR 0.139.
 */
package eognl.exenhance;

import eognl.EOgnlRuntime;
import eognl.ObjectPropertyAccessor;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.PropertyAccessor;
import eognl.exinternal.util.ArraySourceContainer;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.Annotation;
import java.util.Map;

public class ExObjectPropertyAccessor
        extends ObjectPropertyAccessor
        implements PropertyAccessor {
    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public Object getProperty(OgnlContext context, Object target, Object name) throws OgnlException {
        try {
            int level = this.incIndex(context);
            if (level == 1 && this.isFirstAlwaysIgnored(context) && target.getClass().isAssignableFrom(context.getRoot().getClass())) {
                return target;
            }
            if (!this.hasGetProperty(context, target, name)) {
                if (level == 1 && this.isFirstUnknownIgnored(context) && target.getClass().isAssignableFrom(context.getRoot().getClass())) {
                    return target;
                }
                if (!this.isUnknownIsLiteral(context)) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("Could not find property ").append(name).append("  of ").append(target.getClass());
                    throw new OgnlException(sb.toString());
                }
                if (name == null) return null;
                String string = name.toString();
                return string;
            }
            if (!this.isSetChain(context)) {
                return super.getProperty(context, target, name);
            }
            if (!this.isNullInited(context)) {
                return super.getProperty(context, target, name);
            }
            Object value = this.getPossibleProperty(context, target, (String) name);
            Type[] generics = this.getPossibleSetGenericTypes(context, target, (String) name);
            if (generics != null) {
                this.checkSetGenericTypes(context, generics, level);
            }
            Class cls = this.getPropertyClass(context, target, name);
            Class<?> componentType = null;
            if (cls == null || cls == Void.TYPE) {
                if (!this.isUnknownInited(context)) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("Could not determine type of the ").append(name).append(" getter of ").append(target.getClass());
                    throw new OgnlException(sb.toString());
                }
                cls = Object.class;
            } else if (cls.isArray()) {
                componentType = cls.getComponentType();
                this.keepArraySource(context, target, (String) name, level);
            }
            if (value != null) {
                return value;
            }
            value = this.createProperObject(context, cls, componentType);
            if (this.setPossibleProperty(context, target, (String) name, value) != EOgnlRuntime.NotFound) return value;
            return generateException(target, name, value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Object generateException(Object target, Object name, Object value) throws OgnlException {
        StringBuffer sb = new StringBuffer();
        sb.append("Could not set value ").append(value).append(" with property ").append(name).append(" to ").append(target.getClass());
        throw new OgnlException(sb.toString());
    }

    @Override
    public void setProperty(OgnlContext context, Object target, Object name, Object value) throws OgnlException {
        int level = this.incIndex(context);
        if (level == 1 && this.isFirstAlwaysIgnored(context) && target.getClass().isAssignableFrom(context.getRoot().getClass())) {
            return;
        }
        if (this.setPossibleProperty(context, target, (String) name, value) == EOgnlRuntime.NotFound) {
            if (level == 1 && this.isFirstUnknownIgnored(context) && target.getClass().isAssignableFrom(context.getRoot().getClass())) {
                return;
            }
            generateException(target, name, value);
        }
    }

    public int incIndex(OgnlContext context) {
        return EOgnlRuntime.incIndex(context);
    }

    public int decIndex(OgnlContext context) {
        return EOgnlRuntime.decIndex(context);
    }

    public boolean isSetChain(OgnlContext context) {
        return EOgnlRuntime.isSetChain(context);
    }

    public boolean isNullInited(OgnlContext context) {
        return EOgnlRuntime.isNullInited(context);
    }

    public boolean isExpanded(OgnlContext context) {
        return EOgnlRuntime.isExpanded(context);
    }

    public boolean isUnknownInited(OgnlContext context) {
        return EOgnlRuntime.isUnknownInited(context);
    }

    public boolean isFirstUnknownIgnored(OgnlContext context) {
        return EOgnlRuntime.isFirstUnknownIgnored(context);
    }

    public boolean isFirstAlwaysIgnored(OgnlContext context) {
        return EOgnlRuntime.isFirstAlwaysIgnored(context);
    }

    public boolean isUnknownIsLiteral(OgnlContext context) {
        return EOgnlRuntime.isUnknownIsLiteral(context);
    }

    public void checkSetGenericTypes(OgnlContext context, Type[] genericTypes, int level) {
        if (genericTypes.length == 0) {
            return;
        }
        if (genericTypes[0] instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType) genericTypes[0];
            genericTypes = ptype.getActualTypeArguments();
            if (genericTypes == null || genericTypes.length == 0) {
                return;
            }
            StringBuffer key = new StringBuffer();
            key.append(OgnlContext.GENERIC_PREFIX_KEY).append(String.valueOf(level + 1));
            context.put(key.toString(), (Object) genericTypes);
        } else if (genericTypes[0] instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType) genericTypes[0];
            ParameterizedType ptype = null;
            Type tmp_type = genericArrayType.getGenericComponentType();
            do {
                if (tmp_type instanceof GenericArrayType) {
                    ++level;
                    tmp_type = ((GenericArrayType) tmp_type).getGenericComponentType();
                    continue;
                }
                if (tmp_type instanceof ParameterizedType) break;
            } while (true);
            ptype = (ParameterizedType) tmp_type;
            genericTypes = ptype.getActualTypeArguments();
            if (genericTypes == null || genericTypes.length == 0) {
                return;
            }
            StringBuffer key = new StringBuffer();
            key.append(OgnlContext.GENERIC_PREFIX_KEY).append(String.valueOf(level + 2));
            context.put(key.toString(), (Object) genericTypes);
        }
    }

    public Object createProperObject(OgnlContext context, Class<?> cls, Class<?> componentType)
            throws InstantiationException, IllegalAccessException {
        return ((ObjectConstructor) context.get(OgnlContext.OBJECT_CONSTRUCTOR_KEY)).createObject(cls, componentType);
    }

    public Object processObject(OgnlContext context, Object objectGetter, Object object)
            throws InstantiationException, IllegalAccessException {
        Pair<Object, Map<Class, Object>> m = ((Map<Object, Pair<Object, Map<Class, Object>>>)
                context.get(OgnlContext.OBJECT_CONSTRUCTOR_KEY)).get(objectGetter);
        return ((ObjectConstructor) context.get(OgnlContext.OBJECT_CONSTRUCTOR_KEY)).processObject(m.getRight(), m.getLeft(), object);
    }

    public void keepArraySource(OgnlContext context, Object target, String propertyName, int level) {
        StringBuffer key = new StringBuffer();
        key.append(OgnlContext.ARRAR_SOURCE_PREFIX_KEY).append(String.valueOf(level + 1));
        ArraySourceContainer a = new ArraySourceContainer();
        a.setSetterName(propertyName);
        a.setTarget(target);
        context.put(key.toString(), a);
    }

    public Type[] getPossibleSetGenericTypes(OgnlContext context, Object target, String name) throws Exception {
        Type g;
        Method setMethod = EOgnlRuntime.getSetMethod(context, target.getClass(), name);
        if (setMethod != null) {
            return setMethod.getGenericParameterTypes();
        }
        Field setField = EOgnlRuntime.getField(target.getClass(), name);
        if (setField != null && (g = setField.getGenericType()) != null) {
            Type[] generics = new Type[]{g};
            return generics;
        }
        return null;
    }

    public void shiftGenericParameters(OgnlContext context, int level) {
        StringBuffer key = new StringBuffer();
        key.append(OgnlContext.GENERIC_PREFIX_KEY).append(String.valueOf(level));
        Type[] genericParameterTypes = (Type[]) context.get(key.toString());
        if (genericParameterTypes != null && genericParameterTypes.length > 0) {
            key = new StringBuffer();
            key.append(OgnlContext.GENERIC_PREFIX_KEY).append(String.valueOf(level + 1));
            context.put(key.toString(), (Object) genericParameterTypes);
        }
    }

    public int getGenericArgumentsCount() {
        return 0;
    }

    public Object getParameterizedType(OgnlContext context, int level, int paramIndex) {
        int next_classes_len;
        if (this.getGenericArgumentsCount() < 1 || paramIndex < 0) {
            return null;
        }
        StringBuffer key = new StringBuffer().append(OgnlContext.GENERIC_PREFIX_KEY).append(String.valueOf(level));
        Type[] genericParameterTypes = (Type[]) context.get(key.toString());
        if (genericParameterTypes == null || genericParameterTypes.length < this.getGenericArgumentsCount() || genericParameterTypes.length <= paramIndex) {
            return null;
        }
        if (genericParameterTypes instanceof Class[] && (next_classes_len = genericParameterTypes.length - this.getGenericArgumentsCount()) > 0) {
            Class[] classes = new Class[next_classes_len];
            System.arraycopy(genericParameterTypes, this.getGenericArgumentsCount(), classes, 0, next_classes_len);
            key = new StringBuffer().append(OgnlContext.GENERIC_PREFIX_KEY).append(String.valueOf(level + 1));
            context.put(key.toString(), (Object) classes);
            return (Class) genericParameterTypes[paramIndex];
        }
        if (genericParameterTypes[paramIndex] instanceof Class) {
            return (Class) genericParameterTypes[paramIndex];
        }
        ParameterizedType ptype = (ParameterizedType) genericParameterTypes[paramIndex];
        Class myCls = (Class) ptype.getRawType();
        genericParameterTypes = ptype.getActualTypeArguments();
        if (genericParameterTypes == null || genericParameterTypes.length == 0) {
            return myCls;
        }
        key = new StringBuffer().append(OgnlContext.GENERIC_PREFIX_KEY).append(String.valueOf(level + 1));
        context.put(key.toString(), (Object) genericParameterTypes);
        return myCls;
    }

    public void keepArraySource(OgnlContext context, Object target, int index, int level) {
        StringBuffer key = new StringBuffer();
        key.append(OgnlContext.ARRAR_SOURCE_PREFIX_KEY).append(String.valueOf(level + 1));
        ArraySourceContainer a = new ArraySourceContainer();
        a.setIndex(index);
        a.setTarget(target);
        context.put(key.toString(), a);
    }

}

