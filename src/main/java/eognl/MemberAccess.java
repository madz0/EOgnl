/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import java.lang.reflect.Member;
import java.util.Map;

public interface MemberAccess {
    public Object setup(Map<String, Object> var1, Object var2, Member var3, String var4);

    public void restore(Map<String, Object> var1, Object var2, Member var3, String var4, Object var5);

    public boolean isAccessible(Map<String, Object> var1, Object var2, Member var3, String var4);
}

