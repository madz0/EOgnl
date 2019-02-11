/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.NoSuchPropertyException;
import eognl.ObjectPropertyAccessor;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.PropertyAccessor;
import java.util.Iterator;
import java.util.Set;

public class SetPropertyAccessor
extends ObjectPropertyAccessor
implements PropertyAccessor {
    @Override
    public Object getProperty(OgnlContext context, Object target, Object name) throws OgnlException {
        Set set = (Set)target;
        if (name instanceof String) {
            Object result = "size".equals(name) ? Integer.valueOf(set.size()) : ("iterator".equals(name) ? set.iterator() : ("isEmpty".equals(name) ? (set.isEmpty() ? Boolean.TRUE : Boolean.FALSE) : super.getProperty(context, target, name)));
            return result;
        }
        throw new NoSuchPropertyException(target, name);
    }
}

