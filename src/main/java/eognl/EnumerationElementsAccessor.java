/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ElementsAccessor;
import java.util.Enumeration;

public class EnumerationElementsAccessor
implements ElementsAccessor {
    @Override
    public Enumeration<?> getElements(Object target) {
        return (Enumeration)target;
    }
}

