/*
 * Decompiled with CFR 0.139.
 */
package eognl.exenhance;

import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.PropertyAccessor;
import eognl.exenhance.ExObjectPropertyAccessor;
import java.util.Enumeration;

public class ExEnumerationPropertyAccessor
extends ExObjectPropertyAccessor
implements PropertyAccessor {
    @Override
    public Object getProperty(OgnlContext context, Object target, Object name) throws OgnlException {
        Object result;
        int level = this.incIndex(context);
        if (level == 1 && this.isFirstAlwaysIgnored(context) && target.getClass().isAssignableFrom(context.getRoot().getClass())) {
            this.shiftGenericParameters(context, level);
            return target;
        }
        Enumeration e = (Enumeration)target;
        if (name instanceof String) {
            if ("next".equals(name) || "nextElement".equals(name)) {
                result = e.nextElement();
            } else if ("hasNext".equals(name) || "hasMoreElements".equals(name)) {
                result = e.hasMoreElements() ? Boolean.TRUE : Boolean.FALSE;
            } else {
                if (level == 1 && this.isFirstUnknownIgnored(context) && target.getClass().isAssignableFrom(context.getRoot().getClass())) {
                    this.shiftGenericParameters(context, level);
                    return target;
                }
                this.decIndex(context);
                result = super.getProperty(context, target, name);
            }
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

    @Override
    public void setProperty(OgnlContext context, Object target, Object name, Object value) throws OgnlException {
        throw new IllegalArgumentException("can't set property " + name + " on Enumeration");
    }
}

