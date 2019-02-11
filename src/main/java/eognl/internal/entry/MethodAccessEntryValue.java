/*
 * Decompiled with CFR 0.139.
 */
package eognl.internal.entry;

public class MethodAccessEntryValue {
    private boolean isAccessible;
    private boolean notPublic;

    public MethodAccessEntryValue(boolean accessible) {
        this.isAccessible = accessible;
    }

    public MethodAccessEntryValue(boolean accessible, boolean notPublic) {
        this.isAccessible = accessible;
        this.notPublic = notPublic;
    }

    public boolean isAccessible() {
        return this.isAccessible;
    }

    public boolean isNotPublic() {
        return this.notPublic;
    }
}

