/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ObjectPropertyAccessor;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.PropertyAccessor;
import java.util.Iterator;

public class IteratorPropertyAccessor
extends ObjectPropertyAccessor
implements PropertyAccessor {
    @Override
    public Object getProperty(OgnlContext context, Object target, Object name) throws OgnlException {
        Iterator iterator = (Iterator)target;
        Object result = name instanceof String ? ("next".equals(name) ? iterator.next() : ("hasNext".equals(name) ? (iterator.hasNext() ? Boolean.TRUE : Boolean.FALSE) : super.getProperty(context, target, name))) : super.getProperty(context, target, name);
        return result;
    }

    @Override
    public void setProperty(OgnlContext context, Object target, Object name, Object value) throws OgnlException {
        throw new IllegalArgumentException("can't set property " + name + " on Iterator");
    }
}

