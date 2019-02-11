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
import eognl.OgnlOps;
import eognl.PropertyAccessor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ListPropertyAccessor
extends ObjectPropertyAccessor
implements PropertyAccessor {
    @Override
    public Object getProperty(OgnlContext context, Object target, Object name) throws OgnlException {
        List list = (List)target;
        if (name instanceof String) {
            Object result = "size".equals(name) ? Integer.valueOf(list.size()) : ("iterator".equals(name) ? list.iterator() : ("isEmpty".equals(name) || "empty".equals(name) ? (list.isEmpty() ? Boolean.TRUE : Boolean.FALSE) : super.getProperty(context, target, name)));
            return result;
        }
        if (name instanceof Number) {
            return list.get(((Number)name).intValue());
        }
        if (name instanceof DynamicSubscript) {
            int len = list.size();
            switch (((DynamicSubscript)name).getFlag()) {
                case 0: {
                    return len > 0 ? list.get(0) : null;
                }
                case 1: {
                    return len > 0 ? list.get(len / 2) : null;
                }
                case 2: {
                    return len > 0 ? list.get(len - 1) : null;
                }
                case 3: {
                    return new ArrayList(list);
                }
            }
        }
        throw new NoSuchPropertyException(target, name);
    }

    @Override
    public void setProperty(OgnlContext context, Object target, Object name, Object value) throws OgnlException {
        if (name instanceof String && !((String)name).contains("$")) {
            super.setProperty(context, target, name, value);
            return;
        }
        List list = (List)target;
        if (name instanceof Number) {
            list.set(((Number)name).intValue(), value);
            return;
        }
        if (name instanceof DynamicSubscript) {
            int len = list.size();
            switch (((DynamicSubscript)name).getFlag()) {
                case 0: {
                    if (len > 0) {
                        list.set(0, value);
                    }
                    return;
                }
                case 1: {
                    if (len > 0) {
                        list.set(len / 2, value);
                    }
                    return;
                }
                case 2: {
                    if (len > 0) {
                        list.set(len - 1, value);
                    }
                    return;
                }
                case 3: {
                    if (!(value instanceof Collection)) {
                        throw new OgnlException("Value must be a collection");
                    }
                    list.clear();
                    list.addAll((Collection)value);
                    return;
                }
            }
            return;
        }
        throw new NoSuchPropertyException(target, name);
    }

    @Override
    public Class<?> getPropertyClass(OgnlContext context, Object target, Object index) {
        if (index instanceof String) {
            String key = ((String)index).replaceAll("\"", "");
            if ("size".equals(key)) {
                return Integer.TYPE;
            }
            if ("iterator".equals(key)) {
                return Iterator.class;
            }
            if ("isEmpty".equals(key) || "empty".equals(key)) {
                return Boolean.TYPE;
            }
            return super.getPropertyClass(context, target, index);
        }
        if (index instanceof Number) {
            return Object.class;
        }
        return null;
    }

    @Override
    public String getSourceAccessor(OgnlContext context, Object target, Object index) {
        String indexStr = index.toString().replaceAll("\"", "");
        if (String.class.isInstance(index)) {
            if ("size".equals(indexStr)) {
                context.setCurrentAccessor(List.class);
                context.setCurrentType(Integer.TYPE);
                return ".size()";
            }
            if ("iterator".equals(indexStr)) {
                context.setCurrentAccessor(List.class);
                context.setCurrentType(Iterator.class);
                return ".iterator()";
            }
            if ("isEmpty".equals(indexStr) || "empty".equals(indexStr)) {
                context.setCurrentAccessor(List.class);
                context.setCurrentType(Boolean.TYPE);
                return ".isEmpty()";
            }
        }
        return this.getSourceBeanMethod(context, target, index, indexStr, false);
    }

    @Override
    public String getSourceSetter(OgnlContext context, Object target, Object index) {
        String indexStr = index.toString().replaceAll("\"", "");
        return this.getSourceBeanMethod(context, target, index, indexStr, true);
    }

    private String getSourceBeanMethod(OgnlContext context, Object target, Object index, String indexStr, boolean isSetter) {
        Object currentObject = context.getCurrentObject();
        Class<?> currentType = context.getCurrentType();
        if (currentObject != null && !Number.class.isInstance(currentObject)) {
            try {
                if (isSetter ? EOgnlRuntime.getWriteMethod(target.getClass(), indexStr) != null || !currentType.isPrimitive() : EOgnlRuntime.getReadMethod(target.getClass(), indexStr) != null) {
                    return super.getSourceSetter(context, target, index);
                }
            }
            catch (Throwable t) {
                throw OgnlOps.castToRuntime(t);
            }
        }
        context.setCurrentAccessor(List.class);
        if (!currentType.isPrimitive() && Number.class.isAssignableFrom(currentType)) {
            indexStr = String.valueOf(indexStr) + "." + EOgnlRuntime.getNumericValueGetter(currentType);
        } else if (currentObject != null && Number.class.isAssignableFrom(currentObject.getClass()) && !currentType.isPrimitive()) {
            String toString = String.class.isInstance(index) && currentType != Object.class ? "" : ".toString()";
            indexStr = "org.apache.commons.ognl.OgnlOps#getIntValue(" + indexStr + toString + ")";
        }
        context.setCurrentType(Object.class);
        return isSetter ? ".set(" + indexStr + ", $3)" : ".get(" + indexStr + ")";
    }
}

