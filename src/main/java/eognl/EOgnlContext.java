/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import java.lang.reflect.Member;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

import eognl.exenhance.DefaultObjectConstructor;
import eognl.exenhance.ObjectConstructor;
import eognl.exinternal.util.MutableInt;

public class EOgnlContext extends OgnlContext {
  public EOgnlContext() {
    this(0);
  }

  public EOgnlContext(int CONF_FLAGS) {
    if ((CONF_FLAGS & CONF_INIT_NULLS) == CONF_INIT_NULLS) {
      this.put(OgnlContext.INIT_NULLS_KEY, (Object) Boolean.TRUE);
    }
    if ((CONF_FLAGS & CONF_AUTO_EXPAND) == CONF_AUTO_EXPAND) {
      this.put(OgnlContext.EXPAND_SIZE_KEY, (Object) Boolean.TRUE);
    }
    if ((CONF_FLAGS & CONF_INIT_UNKNOWN) == CONF_INIT_UNKNOWN) {
      this.put(OgnlContext.INIT_UNKNOWN_NULLS_KEY, (Object) Boolean.TRUE);
    }
    if ((CONF_FLAGS & CONF_IGNORE_FIRST_UNKNOWN) == CONF_IGNORE_FIRST_UNKNOWN) {
      this.put(OgnlContext.IGNORE_FIRST_UNKNOWN_KEY, (Object) Boolean.TRUE);
    }
    if ((CONF_FLAGS & CONF_ALWAYS_IGNORE_FIRST) == CONF_ALWAYS_IGNORE_FIRST) {
      this.put(OgnlContext.ALWAYS_IGNORE_FIRST_KEY, (Object) Boolean.TRUE);
    }
    if ((CONF_FLAGS & CONF_UNKNOWN_TO_LITERAL) == CONF_UNKNOWN_TO_LITERAL) {
      this.put(OgnlContext.UNKNOWN_TO_LITERAL_KEY, (Object) Boolean.TRUE);
    }
    if ((CONF_FLAGS & CONF_CAST_PRIMITIVES) == CONF_CAST_PRIMITIVES) {
      this.put(OgnlContext.CAST_PRIMITIVES_KEY, (Object) Boolean.TRUE);
      this.setTypeConverter(new TypeConverter() {

        @Override
        public <T> T convertValue(OgnlContext context, Object target, Member member, String propertyName, Object value,
            Class<T> toType) {
          if (toType != null) {
            return (T) EOgnlRuntime.castPrimitive(toType, value);
          }
          if (target != null) {
            return (T) EOgnlRuntime.castPrimitive(target.getClass(), value);
          }
          return (T) value;
        }
      });
    }
    if (CONF_FLAGS != 0) {
      put(OBJECT_CONSTRUCTOR_KEY, new DefaultObjectConstructor());
      put(CURRENT_INDEX_KEY, (Object) new MutableInt());
      cache.addEOgnlPropertyAccessors();
    } else {
      cache.addOgnlPropertyAccessors();
    }
    this.cache.addOgnlElementAccessors();
    this.cache.addOgnlMethodAccessors();
    this.cache.addOgnlNullHandlers();
  }

  public EOgnlContext(int CONF_FLAGS, ParameterizedType type) {
    this(CONF_FLAGS);
    if (type != null) {
      String key = OgnlContext.PARAMETERIZED_ROOT_TYPE_KEY;
      ParameterizedType ptype = type;
      Type[] genericTypes = ptype.getActualTypeArguments();
      if (genericTypes == null || genericTypes.length == 0) {
        return;
      }
      this.put(key.toString(), (Object) genericTypes);
    }
  }

  public EOgnlContext(int CONF_FLAGS, Type[] actualTypeArguments) {
    this(CONF_FLAGS);
    if (actualTypeArguments != null) {
      String key = OgnlContext.PARAMETERIZED_ROOT_TYPE_KEY;
      Type[] genericTypes = actualTypeArguments;
      if (genericTypes == null || genericTypes.length == 0) {
        return;
      }
      this.put(key.toString(), (Object) genericTypes);
    }
  }

  public /* varargs */ EOgnlContext(int CONF_FLAGS, Class<?>... parameterArguments) {
    this(CONF_FLAGS);
    if (parameterArguments != null && parameterArguments.length > 0) {
      String key = OgnlContext.PARAMETERIZED_ROOT_TYPE_KEY;
      this.put(key.toString(), parameterArguments);
    }
  }

  public void addObjectConstructor(ObjectConstructor objectConstructor) {
    put(OBJECT_CONSTRUCTOR_KEY, objectConstructor);
  }
}
