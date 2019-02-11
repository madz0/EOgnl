/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ElementsAccessor;
import java.util.Enumeration;

public class ObjectElementsAccessor
implements ElementsAccessor {
    @Override
    public Enumeration<?> getElements(Object target) {
        final Object object = target;
        return new Enumeration<Object>(){
            private boolean seen = false;

            @Override
            public boolean hasMoreElements() {
                return !this.seen;
            }

            @Override
            public Object nextElement() {
                Object result = null;
                if (!this.seen) {
                    result = object;
                    this.seen = true;
                }
                return result;
            }
        };
    }

}

