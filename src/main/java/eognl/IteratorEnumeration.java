/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import java.util.Enumeration;
import java.util.Iterator;

public class IteratorEnumeration<T>
implements Enumeration<T> {
    private Iterator<T> it;

    public static <E> Enumeration<E> newEnumeration(Iterator<E> iterator) {
        return new IteratorEnumeration<E>(iterator);
    }

    private IteratorEnumeration(Iterator<T> it) {
        this.it = it;
    }

    @Override
    public boolean hasMoreElements() {
        return this.it.hasNext();
    }

    @Override
    public T nextElement() {
        return this.it.next();
    }
}

