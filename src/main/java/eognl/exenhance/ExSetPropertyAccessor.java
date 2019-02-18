/*
 * Decompiled with CFR 0.139.
 */
package eognl.exenhance;

import eognl.NoSuchPropertyException;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.PropertyAccessor;
import eognl.exenhance.ExObjectPropertyAccessor;

import java.util.*;

public class ExSetPropertyAccessor
extends ExObjectPropertyAccessor
implements PropertyAccessor {
    @Override
    public Object getProperty(OgnlContext context, Object target, Object name) throws OgnlException {
        int level = this.incIndex(context);
        if (level == 1 && this.isFirstAlwaysIgnored(context) && target.getClass().isAssignableFrom(context.getRoot().getClass())) {
            this.shiftGenericParameters(context, level);
            return target;
        }
        Set set = (Set)target;
        if (name instanceof String) {
            Object result;
            if ("size".equals(name)) {
                result = set.size();
            } else if ("iterator".equals(name)) {
                result = set.iterator();
            } else if ("isEmpty".equals(name)) {
                result = set.isEmpty() ? Boolean.TRUE : Boolean.FALSE;
            } else {
                if (level == 1 && this.isFirstUnknownIgnored(context) && target.getClass().isAssignableFrom(context.getRoot().getClass())) {
                    this.shiftGenericParameters(context, level);
                    return target;
                }
                this.decIndex(context);
                result = super.getProperty(context, target, name);
            }
            return result;
        }
        else if (name instanceof Number) {
            this.decIndex(context);
            ExListPropertyAccessor exListPropertyAccessor = new ExListPropertyAccessor();
            return exListPropertyAccessor.getProperty(context, target, name);
        }
        if (level == 1 && this.isFirstUnknownIgnored(context) && target.getClass().isAssignableFrom(context.getRoot().getClass())) {
            this.shiftGenericParameters(context, level);
            return target;
        }
        throw new NoSuchPropertyException(target, name);
    }
}

