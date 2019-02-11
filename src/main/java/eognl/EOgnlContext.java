/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.EOgnlRuntime;
import eognl.OgnlCache;
import eognl.OgnlContext;
import eognl.TypeConverter;
import eognl.exinternal.util.MutableInt;
import java.lang.reflect.Member;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class EOgnlContext
extends OgnlContext {
    public EOgnlContext() {
        this(0);
    }

    public EOgnlContext(int CONF_FLAGS) {
        if ((CONF_FLAGS & 1) == 1) {
            this.put(OgnlContext.INIT_NULLS_KEY, (Object)Boolean.TRUE);
        }
        if ((CONF_FLAGS & 2) == 2) {
            this.put("NaIndIdIdx@Id*4Lis", (Object)Boolean.TRUE);
        }
        if ((CONF_FLAGS & 4) == 4) {
            this.put(OgnlContext.INIT_UNKNOWN_NULLS_KEY, (Object)Boolean.TRUE);
        }
        if ((CONF_FLAGS & 8) == 8) {
            this.put("IgG%boZer7&is94E3", (Object)Boolean.TRUE);
        }
        if ((CONF_FLAGS & 16) == 16) {
            this.put("AlwaAa^Ay)i)zIgG%boZElA", (Object)Boolean.TRUE);
        }
        if ((CONF_FLAGS & 32) == 32) {
            this.put("UuUkwWn2LlLte*(9l", (Object)Boolean.TRUE);
        }
        if ((CONF_FLAGS & 64) == 64) {
            this.put("H^7yCa3TtH***", (Object)Boolean.TRUE);
            this.setTypeConverter(new TypeConverter(){

                @Override
                public <T> T convertValue(OgnlContext context, Object target, Member member, String propertyName, Object value, Class<T> toType) {
                    if (toType != null) {
                        return (T)EOgnlRuntime.castPrimitive(toType, value);
                    }
                    if (target != null) {
                        return (T)EOgnlRuntime.castPrimitive(target.getClass(), value);
                    }
                    return (T)value;
                }
            });
        }
        if (CONF_FLAGS != 0) {
            this.put("H0ldZzZz2@Id*4Cain", (Object)new MutableInt());
            this.cache.addEOgnlPropertyAccessors();
        } else {
            this.cache.addOgnlPropertyAccessors();
        }
        this.cache.addOgnlElementAccessors();
        this.cache.addOgnlMethodAccessors();
        this.cache.addOgnlNullHandlers();
    }

    public EOgnlContext(int CONF_FLAGS, ParameterizedType type) {
        this(CONF_FLAGS);
        if (type != null) {
            String key = "PaArmRoo#eZInfo&r?";
            ParameterizedType ptype = type;
            Type[] genericTypes = ptype.getActualTypeArguments();
            if (genericTypes == null || genericTypes.length == 0) {
                return;
            }
            this.put(key.toString(), (Object)genericTypes);
        }
    }

    public EOgnlContext(int CONF_FLAGS, Type[] actualTypeArguments) {
        this(CONF_FLAGS);
        if (actualTypeArguments != null) {
            String key = "PaArmRoo#eZInfo&r?";
            Type[] genericTypes = actualTypeArguments;
            if (genericTypes == null || genericTypes.length == 0) {
                return;
            }
            this.put(key.toString(), (Object)genericTypes);
        }
    }

    public /* varargs */ EOgnlContext(int CONF_FLAGS, Class<?> ... parameterArguments) {
        this(CONF_FLAGS);
        if (parameterArguments != null && parameterArguments.length > 0) {
            String key = "PaArmRoo#eZInfo&r?";
            this.put(key.toString(), parameterArguments);
        }
    }

}

