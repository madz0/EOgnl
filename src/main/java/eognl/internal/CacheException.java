/*
 * Decompiled with CFR 0.139.
 */
package eognl.internal;

public class CacheException
extends RuntimeException {
    public CacheException(Throwable e) {
        super(e.getMessage(), e);
    }
}

