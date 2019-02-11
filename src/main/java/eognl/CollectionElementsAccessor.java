/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ElementsAccessor;
import eognl.IteratorEnumeration;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

public class CollectionElementsAccessor
implements ElementsAccessor {
    @Override
    public Enumeration<?> getElements(Object target) {
        return IteratorEnumeration.newEnumeration(((Collection)target).iterator());
    }
}

