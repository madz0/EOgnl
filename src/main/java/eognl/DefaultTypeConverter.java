/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.OgnlContext;
import eognl.OgnlOps;
import eognl.TypeConverter;
import java.lang.reflect.Member;

public class DefaultTypeConverter
implements TypeConverter {
    public <T> T convertValue(OgnlContext context, Object value, Class<T> toType) {
        Object ret = OgnlOps.convertValue(value, toType);
        return (T)ret;
    }

    @Override
    public <T> T convertValue(OgnlContext context, Object target, Member member, String propertyName, Object value, Class<T> toType) {
        return this.convertValue(context, value, toType);
    }
}

