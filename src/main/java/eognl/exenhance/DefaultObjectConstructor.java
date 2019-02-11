package eognl.exenhance;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import eognl.EOgnlRuntime;

public class DefaultObjectConstructor implements ObjectConstructor {

  @Override
  public Object createObject(Class<?> cls, Class<?> componentType)
      throws InstantiationException, IllegalAccessException {
    if (List.class.isAssignableFrom(cls)) {
      if (LinkedList.class.isAssignableFrom(cls)) {
        return new LinkedList();
      }
      return new ArrayList();
    }
    if (Map.class.isAssignableFrom(cls)) {
      if (LinkedHashMap.class.isAssignableFrom(cls)) {
        return new LinkedHashMap();
      }
      if (TreeMap.class.isAssignableFrom(cls)) {
        return new TreeMap();
      }
      return new HashMap();
    }
    if (ConcurrentMap.class.isAssignableFrom(cls)) {
      return new ConcurrentHashMap();
    }
    if (Set.class.isAssignableFrom(cls)) {
      if (LinkedHashSet.class.isAssignableFrom(cls)) {
        return new LinkedHashSet();
      }
      return new HashSet();
    }
    if (cls.isArray()) {
      return Array.newInstance(componentType, 1);
    }
    if (EOgnlRuntime.isPrimitiveOrWrapper(cls)) {
      return EOgnlRuntime.getPrimitivesDefult(cls);
    }
    return cls.newInstance();
  }

}
