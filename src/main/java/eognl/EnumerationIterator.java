/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import java.util.Enumeration;
import java.util.Iterator;

public class EnumerationIterator<E>
implements Iterator<E> {
    private final Enumeration<E> e;

    public static <T> Iterator<T> newIterator(Enumeration<T> e) {
        return new EnumerationIterator<T>(e);
    }

    public EnumerationIterator(Enumeration<E> e) {
        this.e = e;
    }

    @Override
    public boolean hasNext() {
        return this.e.hasMoreElements();
    }

    @Override
    public E next() {
        return this.e.nextElement();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() not supported by Enumeration");
    }
}

