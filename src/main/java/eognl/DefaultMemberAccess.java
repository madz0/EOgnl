/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.MemberAccess;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Map;

public class DefaultMemberAccess
implements MemberAccess {
    private boolean allowPrivateAccess = false;
    private boolean allowProtectedAccess = false;
    private boolean allowPackageProtectedAccess = false;

    public DefaultMemberAccess(boolean allowAllAccess) {
        this(allowAllAccess, allowAllAccess, allowAllAccess);
    }

    public DefaultMemberAccess(boolean allowPrivateAccess, boolean allowProtectedAccess, boolean allowPackageProtectedAccess) {
        this.allowPrivateAccess = allowPrivateAccess;
        this.allowProtectedAccess = allowProtectedAccess;
        this.allowPackageProtectedAccess = allowPackageProtectedAccess;
    }

    public boolean getAllowPrivateAccess() {
        return this.allowPrivateAccess;
    }

    public void setAllowPrivateAccess(boolean value) {
        this.allowPrivateAccess = value;
    }

    public boolean getAllowProtectedAccess() {
        return this.allowProtectedAccess;
    }

    public void setAllowProtectedAccess(boolean value) {
        this.allowProtectedAccess = value;
    }

    public boolean getAllowPackageProtectedAccess() {
        return this.allowPackageProtectedAccess;
    }

    public void setAllowPackageProtectedAccess(boolean value) {
        this.allowPackageProtectedAccess = value;
    }

    @Override
    public Object setup(Map<String, Object> context, Object target, Member member, String propertyName) {
        AccessibleObject accessible;
        Boolean result = null;
        if (this.isAccessible(context, target, member, propertyName) && !(accessible = (AccessibleObject)((Object)member)).isAccessible()) {
            result = Boolean.TRUE;
            accessible.setAccessible(true);
        }
        return result;
    }

    @Override
    public void restore(Map<String, Object> context, Object target, Member member, String propertyName, Object state) {
        if (state != null) {
            ((AccessibleObject)((Object)member)).setAccessible((Boolean)state);
        }
    }

    @Override
    public boolean isAccessible(Map<String, Object> context, Object target, Member member, String propertyName) {
        int modifiers = member.getModifiers();
        boolean result = Modifier.isPublic(modifiers);
        if (!result) {
            result = Modifier.isPrivate(modifiers) ? this.getAllowPrivateAccess() : (Modifier.isProtected(modifiers) ? this.getAllowProtectedAccess() : this.getAllowPackageProtectedAccess());
        }
        return result;
    }
}

