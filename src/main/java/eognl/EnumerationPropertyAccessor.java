/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ObjectPropertyAccessor;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.PropertyAccessor;
import java.util.Enumeration;

public class EnumerationPropertyAccessor
extends ObjectPropertyAccessor
implements PropertyAccessor {
    @Override
    public Object getProperty(OgnlContext context, Object target, Object name) throws OgnlException {
        Enumeration e = (Enumeration)target;
        Object result = name instanceof String ? ("next".equals(name) || "nextElement".equals(name) ? e.nextElement() : ("hasNext".equals(name) || "hasMoreElements".equals(name) ? (e.hasMoreElements() ? Boolean.TRUE : Boolean.FALSE) : super.getProperty(context, target, name))) : super.getProperty(context, target, name);
        return result;
    }

    @Override
    public void setProperty(OgnlContext context, Object target, Object name, Object value) throws OgnlException {
        throw new IllegalArgumentException("can't set property " + name + " on Enumeration");
    }
}

