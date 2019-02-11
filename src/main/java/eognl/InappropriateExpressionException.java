/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.Node;
import eognl.OgnlException;

public class InappropriateExpressionException
extends OgnlException {
    private static final long serialVersionUID = -101395753764475977L;

    public InappropriateExpressionException(Node tree) {
        super("Inappropriate OGNL expression: " + tree);
    }
}

