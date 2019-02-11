/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.OgnlException;

public class ExpressionSyntaxException
extends OgnlException {
    private static final long serialVersionUID = 3828005676770762146L;

    public ExpressionSyntaxException(String expression, Throwable reason) {
        super("Malformed OGNL expression: " + expression, reason);
    }
}

