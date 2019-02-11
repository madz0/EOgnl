/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.OgnlException;

public class MethodFailedException
extends OgnlException {
    private static final long serialVersionUID = -8537354635249153386L;

    public MethodFailedException(Object source, String name) {
        super(String.format("Method \"%s\" failed for object %s", name, source));
    }

    public MethodFailedException(Object source, String name, Throwable reason) {
        super(String.format("Method \"%s\" failed for object %s", name, source), reason);
    }
}

