/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ElementsAccessor;
import eognl.IteratorEnumeration;
import java.util.Enumeration;
import java.util.Iterator;

public class IteratorElementsAccessor
implements ElementsAccessor {
    @Override
    public Enumeration<?> getElements(Object target) {
        return IteratorEnumeration.newEnumeration((Iterator)target);
    }
}

