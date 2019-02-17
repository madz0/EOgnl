package eognl.exenhance;

import java.util.Map;

public interface ObjectConstructor {
  Object createObject(Class<?> cls, Class<?> componentType) throws InstantiationException, IllegalAccessException;
  Object processObject(Map<Class, Object> getterAnnotationMap, Object getterObject, Object object) throws InstantiationException, IllegalAccessException;
}
