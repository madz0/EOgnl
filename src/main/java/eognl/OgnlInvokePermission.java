/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import java.security.BasicPermission;

public class OgnlInvokePermission
extends BasicPermission {
    private static final long serialVersionUID = 1L;

    public OgnlInvokePermission(String name) {
        super(name);
    }

    public OgnlInvokePermission(String name, String actions) {
        super(name, actions);
    }
}

