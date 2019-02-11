/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ElementsAccessor;
import eognl.NumericTypes;
import eognl.OgnlOps;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public class NumberElementsAccessor
implements ElementsAccessor,
NumericTypes {
    @Override
    public Enumeration getElements( final Object target )
    {
        return new Enumeration() {
            private int type = OgnlOps.getNumericType( target );
            private long next = 0;
            private long finish = OgnlOps.longValue( target );

            public boolean hasMoreElements() {
                return next < finish;
            }

            public Object nextElement() {
                if ( next >= finish )
                    throw new NoSuchElementException();
                return OgnlOps.newInteger( type, next++ );
            }
        };
    }

}

