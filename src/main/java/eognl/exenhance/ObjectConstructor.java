package eognl.exenhance;

public interface ObjectConstructor {
  Object createObject(Class<?> cls, Class<?> componentType) throws InstantiationException, IllegalAccessException;
}
