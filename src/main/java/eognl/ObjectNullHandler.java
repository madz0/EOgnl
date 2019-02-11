/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.NullHandler;
import java.util.Map;

public class ObjectNullHandler
implements NullHandler {
    @Override
    public Object nullMethodResult(Map<String, Object> context, Object target, String methodName, Object[] args) {
        return null;
    }

    @Override
    public Object nullPropertyValue(Map<String, Object> context, Object target, Object property) {
        return null;
    }
}

