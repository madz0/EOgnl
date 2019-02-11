/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.DynamicSubscript;
import eognl.EOgnlRuntime;
import eognl.NoSuchPropertyException;
import eognl.ObjectPropertyAccessor;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.PropertyAccessor;
import eognl.TypeConverter;
import java.lang.reflect.Array;
import java.lang.reflect.Member;

public class ArrayPropertyAccessor
extends ObjectPropertyAccessor
implements PropertyAccessor {
    @Override
    public Object getProperty(OgnlContext context, Object target, Object name) throws OgnlException {
        Object result = null;
        if (name instanceof String) {
            result = "length".equals(name) ? Integer.valueOf(Array.getLength(target)) : super.getProperty(context, target, name);
        } else {
            Object index = name;
            if (index instanceof DynamicSubscript) {
                int len = Array.getLength(target);
                switch (((DynamicSubscript)index).getFlag()) {
                    case 3: {
                        result = Array.newInstance(target.getClass().getComponentType(), len);
                        System.arraycopy(target, 0, result, 0, len);
                        break;
                    }
                    case 0: {
                        index = len > 0 ? 0 : -1;
                        break;
                    }
                    case 1: {
                        index = len > 0 ? len / 2 : -1;
                        break;
                    }
                    case 2: {
                        index = len > 0 ? len - 1 : -1;
                        break;
                    }
                }
            }
            if (result == null) {
                if (index instanceof Number) {
                    int i = ((Number)index).intValue();
                    result = i >= 0 ? Array.get(target, i) : null;
                } else {
                    throw new NoSuchPropertyException(target, index);
                }
            }
        }
        return result;
    }

    @Override
    public void setProperty(OgnlContext context, Object target, Object name, Object value) throws OgnlException {
        boolean isNumber = name instanceof Number;
        if (isNumber || name instanceof DynamicSubscript) {
            TypeConverter converter = context.getTypeConverter();
            Object convertedValue = converter.convertValue(context, target, null, name.toString(), value, target.getClass().getComponentType());
            if (isNumber) {
                int i = ((Number)name).intValue();
                if (i >= 0) {
                    Array.set(target, i, convertedValue);
                }
            } else {
                int len = Array.getLength(target);
                switch (((DynamicSubscript)name).getFlag()) {
                    case 3: {
                        System.arraycopy(target, 0, convertedValue, 0, len);
                        return;
                    }
                }
            }
        } else if (name instanceof String) {
            super.setProperty(context, target, name, value);
        } else {
            throw new NoSuchPropertyException(target, name);
        }
    }

    @Override
    public String getSourceAccessor(OgnlContext context, Object target, Object index) {
        String indexStr = ArrayPropertyAccessor.getIndexString(context, index);
        context.setCurrentAccessor(target.getClass());
        context.setCurrentType(target.getClass().getComponentType());
        return String.format("[%s]", indexStr);
    }

    @Override
    public String getSourceSetter(OgnlContext context, Object target, Object index) {
        String indexStr = ArrayPropertyAccessor.getIndexString(context, index);
        Class<?> type = target.getClass().isArray() ? target.getClass().getComponentType() : target.getClass();
        context.setCurrentAccessor(target.getClass());
        context.setCurrentType(target.getClass().getComponentType());
        if (type.isPrimitive()) {
            Class<?> wrapClass = EOgnlRuntime.getPrimitiveWrapperClass(type);
            return String.format("[%s]=((%s)org.apache.commons.ognl.OgnlOps.convertValue($3,%s.class, true)).%s", indexStr, wrapClass.getName(), wrapClass.getName(), EOgnlRuntime.getNumericValueGetter(wrapClass));
        }
        return String.format("[%s]=org.apache.commons.ognl.OgnlOps.convertValue($3,%s.class)", indexStr, type.getName());
    }

    private static String getIndexString(OgnlContext context, Object index) {
        String indexStr = index.toString();
        if (context.getCurrentType() != null && !context.getCurrentType().isPrimitive() && Number.class.isAssignableFrom(context.getCurrentType())) {
            indexStr = String.valueOf(indexStr) + "." + EOgnlRuntime.getNumericValueGetter(context.getCurrentType());
        } else if (context.getCurrentObject() != null && Number.class.isAssignableFrom(context.getCurrentObject().getClass()) && !context.getCurrentType().isPrimitive()) {
            String toString = String.class.isInstance(index) && context.getCurrentType() != Object.class ? "" : ".toString()";
            indexStr = String.format("org.apache.commons.ognl.OgnlOps#getIntValue(%s%s)", indexStr, toString);
        }
        return indexStr;
    }
}

