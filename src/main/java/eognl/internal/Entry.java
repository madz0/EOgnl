/*
 * Decompiled with CFR 0.139.
 */
package eognl.internal;

class Entry<K, V> {
    private Entry<K, V> next;
    private final K key;
    private V value;

    public Entry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public Entry<K, V> getNext() {
        return this.next;
    }

    public void setNext(Entry<K, V> next) {
        this.next = next;
    }

    public String toString() {
        return "Entry[next=" + this.next + '\n' + ", key=" + this.key + '\n' + ", value=" + this.value + '\n' + ']';
    }
}

