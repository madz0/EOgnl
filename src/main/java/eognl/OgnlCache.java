/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ArrayElementsAccessor;
import eognl.ArrayPropertyAccessor;
import eognl.ClassCacheInspector;
import eognl.CollectionElementsAccessor;
import eognl.ElementsAccessor;
import eognl.EnumerationElementsAccessor;
import eognl.EnumerationPropertyAccessor;
import eognl.IteratorElementsAccessor;
import eognl.IteratorPropertyAccessor;
import eognl.ListPropertyAccessor;
import eognl.MapElementsAccessor;
import eognl.MapPropertyAccessor;
import eognl.MethodAccessor;
import eognl.NullHandler;
import eognl.NumberElementsAccessor;
import eognl.ObjectElementsAccessor;
import eognl.ObjectMethodAccessor;
import eognl.ObjectNullHandler;
import eognl.ObjectPropertyAccessor;
import eognl.OgnlException;
import eognl.PropertyAccessor;
import eognl.SetPropertyAccessor;
import eognl.exenhance.ExArrayPropertyAccessor;
import eognl.exenhance.ExEnumerationPropertyAccessor;
import eognl.exenhance.ExIteratorPropertyAccessor;
import eognl.exenhance.ExListPropertyAccessor;
import eognl.exenhance.ExMapPropertyAccessor;
import eognl.exenhance.ExObjectPropertyAccessor;
import eognl.exenhance.ExSetPropertyAccessor;
import eognl.internal.Cache;
import eognl.internal.CacheException;
import eognl.internal.CacheFactory;
import eognl.internal.ClassCache;
import eognl.internal.ClassCacheHandler;
import eognl.internal.HashMapCacheFactory;
import eognl.internal.entry.CacheEntryFactory;
import eognl.internal.entry.ClassCacheEntryFactory;
import eognl.internal.entry.DeclaredMethodCacheEntry;
import eognl.internal.entry.DeclaredMethodCacheEntryFactory;
import eognl.internal.entry.FieldCacheEntryFactory;
import eognl.internal.entry.GenericMethodParameterTypeCacheEntry;
import eognl.internal.entry.GenericMethodParameterTypeFactory;
import eognl.internal.entry.MethodAccessCacheEntryFactory;
import eognl.internal.entry.MethodAccessEntryValue;
import eognl.internal.entry.MethodPermCacheEntryFactory;
import eognl.internal.entry.PermissionCacheEntry;
import eognl.internal.entry.PermissionCacheEntryFactory;
import eognl.internal.entry.PropertyDescriptorCacheEntryFactory;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.Permission;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OgnlCache {
    private final CacheFactory cacheFactory = new HashMapCacheFactory();
    private final ClassCache<MethodAccessor> methodAccessors = this.cacheFactory.createClassCache();
    private final ClassCache<PropertyAccessor> propertyAccessors = this.cacheFactory.createClassCache();
    private final ClassCache<ElementsAccessor> elementsAccessors = this.cacheFactory.createClassCache();
    private final ClassCache<NullHandler> nullHandlers = this.cacheFactory.createClassCache();
    final ClassCache<Map<String, PropertyDescriptor>> propertyDescriptorCache = this.cacheFactory.createClassCache(new PropertyDescriptorCacheEntryFactory());
    private final ClassCache<List<Constructor<?>>> constructorCache = this.cacheFactory.createClassCache(new ClassCacheEntryFactory<List<Constructor<?>>>(){

        @Override
        public List<Constructor<?>> create(Class<?> key) throws CacheException {
            return Arrays.asList(key.getConstructors());
        }
    });
    private final Cache<DeclaredMethodCacheEntry, Map<String, List<Method>>> _methodCache = this.cacheFactory.createCache(new DeclaredMethodCacheEntryFactory());
    private final Cache<PermissionCacheEntry, Permission> _invokePermissionCache = this.cacheFactory.createCache(new PermissionCacheEntryFactory());
    private final ClassCache<Map<String, Field>> _fieldCache = this.cacheFactory.createClassCache(new FieldCacheEntryFactory());
    private final Cache<Method, Class<?>[]> _methodParameterTypesCache = this.cacheFactory.createCache(new CacheEntryFactory<Method, Class<?>[]>(){

        @Override
        public Class<?>[] create(Method key) throws CacheException {
            return key.getParameterTypes();
        }
    });
    private final Cache<GenericMethodParameterTypeCacheEntry, Class<?>[]> _genericMethodParameterTypesCache = this.cacheFactory.createCache(new GenericMethodParameterTypeFactory());
    private final Cache<Constructor<?>, Class<?>[]> _ctorParameterTypesCache = this.cacheFactory.createCache(new CacheEntryFactory<Constructor<?>, Class<?>[]>(){

        @Override
        public Class<?>[] create(Constructor<?> key) throws CacheException {
            return key.getParameterTypes();
        }
    });
    private final Cache<Method, MethodAccessEntryValue> _methodAccessCache = this.cacheFactory.createCache(new MethodAccessCacheEntryFactory());
    private final MethodPermCacheEntryFactory methodPermCacheEntryFactory = new MethodPermCacheEntryFactory(System.getSecurityManager());
    private final Cache<Method, Boolean> _methodPermCache = this.cacheFactory.createCache(this.methodPermCacheEntryFactory);

    public void addOgnlMethodAccessors() {
        ObjectMethodAccessor methodAccessor = new ObjectMethodAccessor();
        this.setMethodAccessor(Object.class, methodAccessor);
        this.setMethodAccessor(byte[].class, methodAccessor);
        this.setMethodAccessor(short[].class, methodAccessor);
        this.setMethodAccessor(char[].class, methodAccessor);
        this.setMethodAccessor(int[].class, methodAccessor);
        this.setMethodAccessor(long[].class, methodAccessor);
        this.setMethodAccessor(float[].class, methodAccessor);
        this.setMethodAccessor(double[].class, methodAccessor);
        this.setMethodAccessor(Object[].class, methodAccessor);
    }

    public void addOgnlPropertyAccessors() {
        ArrayPropertyAccessor propertyAccessor = new ArrayPropertyAccessor();
        this.setPropertyAccessor(Object.class, new ObjectPropertyAccessor());
        this.setPropertyAccessor(byte[].class, propertyAccessor);
        this.setPropertyAccessor(short[].class, propertyAccessor);
        this.setPropertyAccessor(char[].class, propertyAccessor);
        this.setPropertyAccessor(int[].class, propertyAccessor);
        this.setPropertyAccessor(long[].class, propertyAccessor);
        this.setPropertyAccessor(float[].class, propertyAccessor);
        this.setPropertyAccessor(double[].class, propertyAccessor);
        this.setPropertyAccessor(Object[].class, propertyAccessor);
        this.setPropertyAccessor(List.class, new ListPropertyAccessor());
        this.setPropertyAccessor(Map.class, new MapPropertyAccessor());
        this.setPropertyAccessor(Set.class, new SetPropertyAccessor());
        this.setPropertyAccessor(Iterator.class, new IteratorPropertyAccessor());
        this.setPropertyAccessor(Enumeration.class, new EnumerationPropertyAccessor());
    }

    public void addOgnlElementAccessors() {
        ArrayElementsAccessor elementsAccessor = new ArrayElementsAccessor();
        this.setElementsAccessor(Object.class, new ObjectElementsAccessor());
        this.setElementsAccessor(byte[].class, elementsAccessor);
        this.setElementsAccessor(short[].class, elementsAccessor);
        this.setElementsAccessor(char[].class, elementsAccessor);
        this.setElementsAccessor(int[].class, elementsAccessor);
        this.setElementsAccessor(long[].class, elementsAccessor);
        this.setElementsAccessor(float[].class, elementsAccessor);
        this.setElementsAccessor(double[].class, elementsAccessor);
        this.setElementsAccessor(Object[].class, elementsAccessor);
        this.setElementsAccessor(Collection.class, new CollectionElementsAccessor());
        this.setElementsAccessor(Map.class, new MapElementsAccessor());
        this.setElementsAccessor(Iterator.class, new IteratorElementsAccessor());
        this.setElementsAccessor(Enumeration.class, new EnumerationElementsAccessor());
        this.setElementsAccessor(Number.class, new NumberElementsAccessor());
    }

    public void addOgnlNullHandlers() {
        ObjectNullHandler nullHandler = new ObjectNullHandler();
        this.setNullHandler(Object.class, nullHandler);
        this.setNullHandler(byte[].class, nullHandler);
        this.setNullHandler(short[].class, nullHandler);
        this.setNullHandler(char[].class, nullHandler);
        this.setNullHandler(int[].class, nullHandler);
        this.setNullHandler(long[].class, nullHandler);
        this.setNullHandler(float[].class, nullHandler);
        this.setNullHandler(double[].class, nullHandler);
        this.setNullHandler(Object[].class, nullHandler);
    }

    public void addEOgnlPropertyAccessors() {
        ExArrayPropertyAccessor propertyAccessor = new ExArrayPropertyAccessor();
        this.setPropertyAccessor(Object.class, new ExObjectPropertyAccessor());
        this.setPropertyAccessor(byte[].class, propertyAccessor);
        this.setPropertyAccessor(short[].class, propertyAccessor);
        this.setPropertyAccessor(char[].class, propertyAccessor);
        this.setPropertyAccessor(int[].class, propertyAccessor);
        this.setPropertyAccessor(long[].class, propertyAccessor);
        this.setPropertyAccessor(float[].class, propertyAccessor);
        this.setPropertyAccessor(double[].class, propertyAccessor);
        this.setPropertyAccessor(Object[].class, propertyAccessor);
        this.setPropertyAccessor(List.class, new ExListPropertyAccessor());
        this.setPropertyAccessor(Map.class, new ExMapPropertyAccessor());
        this.setPropertyAccessor(Set.class, new ExSetPropertyAccessor());
        this.setPropertyAccessor(Iterator.class, new ExIteratorPropertyAccessor());
        this.setPropertyAccessor(Enumeration.class, new ExEnumerationPropertyAccessor());
    }

    public Class<?>[] getMethodParameterTypes(Method method) throws CacheException {
        return this._methodParameterTypesCache.get(method);
    }

    public Class<?>[] getParameterTypes(Constructor<?> constructor) throws CacheException {
        return this._ctorParameterTypesCache.get(constructor);
    }

    public List<Constructor<?>> getConstructor(Class<?> clazz) throws CacheException {
        return (List)this.constructorCache.get(clazz);
    }

    public Map<String, Field> getField(Class<?> clazz) throws CacheException {
        return (Map)this._fieldCache.get(clazz);
    }

    public Map<String, List<Method>> getMethod(DeclaredMethodCacheEntry declaredMethodCacheEntry) throws CacheException {
        return this._methodCache.get(declaredMethodCacheEntry);
    }

    public Map<String, PropertyDescriptor> getPropertyDescriptor(Class<?> clazz) throws CacheException {
        return (Map)this.propertyDescriptorCache.get(clazz);
    }

    public Permission getInvokePermission(PermissionCacheEntry permissionCacheEntry) throws CacheException {
        return this._invokePermissionCache.get(permissionCacheEntry);
    }

    public MethodAccessor getMethodAccessor(Class<?> clazz) throws OgnlException {
        MethodAccessor methodAccessor = ClassCacheHandler.getHandler(clazz, this.methodAccessors);
        if (methodAccessor != null) {
            return methodAccessor;
        }
        throw new OgnlException("No method accessor for " + clazz);
    }

    public void setMethodAccessor(Class<?> clazz, MethodAccessor accessor) {
        this.methodAccessors.put(clazz, accessor);
    }

    public void setPropertyAccessor(Class<?> clazz, PropertyAccessor accessor) {
        this.propertyAccessors.put(clazz, accessor);
    }

    public PropertyAccessor getPropertyAccessor(Class<?> clazz) throws OgnlException {
        PropertyAccessor propertyAccessor = ClassCacheHandler.getHandler(clazz, this.propertyAccessors);
        if (propertyAccessor != null) {
            return propertyAccessor;
        }
        throw new OgnlException("No property accessor for class " + clazz);
    }

    public void setClassCacheInspector(ClassCacheInspector inspector) {
        this.propertyDescriptorCache.setClassInspector(inspector);
        this.constructorCache.setClassInspector(inspector);
        this._fieldCache.setClassInspector(inspector);
    }

    public Class<?>[] getGenericMethodParameterTypes(GenericMethodParameterTypeCacheEntry key) throws CacheException {
        return this._genericMethodParameterTypesCache.get(key);
    }

    public boolean getMethodPerm(Method method) throws CacheException {
        return this._methodPermCache.get(method);
    }

    public MethodAccessEntryValue getMethodAccess(Method method) throws CacheException {
        return this._methodAccessCache.get(method);
    }

    public void clear() {
        this._methodParameterTypesCache.clear();
        this._ctorParameterTypesCache.clear();
        this.propertyDescriptorCache.clear();
        this.constructorCache.clear();
        this._methodCache.clear();
        this._invokePermissionCache.clear();
        this._fieldCache.clear();
        this._methodAccessCache.clear();
    }

    public ElementsAccessor getElementsAccessor(Class<?> clazz) throws OgnlException {
        ElementsAccessor answer = ClassCacheHandler.getHandler(clazz, this.elementsAccessors);
        if (answer != null) {
            return answer;
        }
        throw new OgnlException("No elements accessor for class " + clazz);
    }

    public void setElementsAccessor(Class<?> clazz, ElementsAccessor accessor) {
        this.elementsAccessors.put(clazz, accessor);
    }

    public NullHandler getNullHandler(Class<?> clazz) throws OgnlException {
        NullHandler answer = ClassCacheHandler.getHandler(clazz, this.nullHandlers);
        if (answer != null) {
            return answer;
        }
        throw new OgnlException("No null handler for class " + clazz);
    }

    public void setNullHandler(Class<?> clazz, NullHandler handler) {
        this.nullHandlers.put(clazz, handler);
    }

    public void setSecurityManager(SecurityManager securityManager) {
        this.methodPermCacheEntryFactory.setSecurityManager(securityManager);
    }

}

