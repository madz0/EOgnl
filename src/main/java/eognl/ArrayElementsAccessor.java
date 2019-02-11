/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ElementsAccessor;
import java.lang.reflect.Array;
import java.util.Enumeration;

public class ArrayElementsAccessor
implements ElementsAccessor {
    @Override
    public Enumeration<?> getElements(final Object target) {
        return new Enumeration<Object>(){
            private int count;
            private int index;
            {
                this.count = Array.getLength(target);
                this.index = 0;
            }

            @Override
            public boolean hasMoreElements() {
                return this.index < this.count;
            }

            @Override
            public Object nextElement() {
                return Array.get(target, this.index++);
            }
        };
    }

}

