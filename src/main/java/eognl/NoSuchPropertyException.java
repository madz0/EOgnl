/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.OgnlException;

public class NoSuchPropertyException
extends OgnlException {
    private static final long serialVersionUID = 2228428181127177178L;
    private Object target;
    private Object name;

    public NoSuchPropertyException(Object target, Object name) {
        super(NoSuchPropertyException.getReason(target, name));
    }

    public NoSuchPropertyException(Object target, Object name, Throwable reason) {
        super(NoSuchPropertyException.getReason(target, name), reason);
        this.target = target;
        this.name = name;
    }

    static String getReason(Object target, Object name) {
        StringBuilder ret = new StringBuilder();
        if (target == null) {
            ret.append("null");
        } else if (target instanceof Class) {
            ret.append(((Class)target).getName());
        } else {
            ret.append(target.getClass().getName());
        }
        ret.append(".").append(name);
        return ret.toString();
    }

    public Object getTarget() {
        return this.target;
    }

    public Object getName() {
        return this.name;
    }
}

