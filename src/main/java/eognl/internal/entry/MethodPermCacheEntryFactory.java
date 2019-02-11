/*
 * Decompiled with CFR 0.139.
 */
package eognl.internal.entry;

import eognl.EOgnlRuntime;
import eognl.internal.CacheException;
import eognl.internal.entry.CacheEntryFactory;
import java.lang.reflect.Method;
import java.security.Permission;

public class MethodPermCacheEntryFactory
implements CacheEntryFactory<Method, Boolean> {
    private SecurityManager securityManager;

    public MethodPermCacheEntryFactory(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    @Override
    public Boolean create(Method key) throws CacheException {
        try {
            this.securityManager.checkPermission(EOgnlRuntime.getPermission(key));
            return true;
        }
        catch (SecurityException ex) {
            return false;
        }
    }

    public void setSecurityManager(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }
}

