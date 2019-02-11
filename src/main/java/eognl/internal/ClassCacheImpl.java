/*
 * Decompiled with CFR 0.139.
 */
package eognl.internal;

import eognl.ClassCacheInspector;
import eognl.internal.CacheException;
import eognl.internal.ClassCache;
import eognl.internal.Entry;
import java.util.Arrays;

public class ClassCacheImpl<V>
implements ClassCache<V> {
    private static final int TABLE_SIZE = 512;
    private static final int TABLE_SIZE_MASK = 511;
    private Entry<Class<?>, V>[] table = new Entry[512];
    private ClassCacheInspector classInspector;
    private int size = 0;

    @Override
    public void setClassInspector(ClassCacheInspector inspector) {
        this.classInspector = inspector;
    }

    @Override
    public void clear() {
        for (int i = 0; i < this.table.length; ++i) {
            this.table[i] = null;
        }
        this.size = 0;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public final V get(Class<?> key) throws CacheException {
        int i = key.hashCode() & 511;
        for (Entry<Class<?>, V> entry = this.table[i]; entry != null; entry = entry.getNext()) {
            if (key != entry.getKey()) continue;
            return entry.getValue();
        }
        return null;
    }

    @Override
    public final V put(Class<?> key, V value) {
        if (this.classInspector != null && !this.classInspector.shouldCache(key)) {
            return value;
        }
        V result = null;
        int i = key.hashCode() & 511;
        Entry<Class<?>, V> entry = this.table[i];
        if (entry == null) {
            this.table[i] = new Entry(key, value);
            ++this.size;
        } else if (key == entry.getKey()) {
            result = entry.getValue();
            entry.setValue(value);
        } else {
            do {
                if (key == entry.getKey()) {
                    result = entry.getValue();
                    entry.setValue(value);
                    break;
                }
                if (entry.getNext() == null) {
                    entry.setNext(new Entry(key, value));
                    break;
                }
                entry = entry.getNext();
            } while (true);
        }
        return result;
    }

    public String toString() {
        return "ClassCacheImpl[_table=" + (this.table == null ? null : Arrays.asList(this.table)) + '\n' + ", _classInspector=" + this.classInspector + '\n' + ", _size=" + this.size + '\n' + ']';
    }
}

