/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public class ObjectIndexedPropertyDescriptor
extends PropertyDescriptor {
    private Method indexedReadMethod;
    private Method indexedWriteMethod;
    private Class<?> propertyType;

    public ObjectIndexedPropertyDescriptor(String propertyName, Class<?> propertyType, Method indexedReadMethod, Method indexedWriteMethod) throws IntrospectionException {
        super(propertyName, null, null);
        this.propertyType = propertyType;
        this.indexedReadMethod = indexedReadMethod;
        this.indexedWriteMethod = indexedWriteMethod;
    }

    public Method getIndexedReadMethod() {
        return this.indexedReadMethod;
    }

    public Method getIndexedWriteMethod() {
        return this.indexedWriteMethod;
    }

    @Override
    public Class<?> getPropertyType() {
        return this.propertyType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ObjectIndexedPropertyDescriptor)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ObjectIndexedPropertyDescriptor that = (ObjectIndexedPropertyDescriptor)o;
        if (this.indexedReadMethod != null ? !this.indexedReadMethod.equals(that.indexedReadMethod) : that.indexedReadMethod != null) {
            return false;
        }
        if (this.indexedWriteMethod != null ? !this.indexedWriteMethod.equals(that.indexedWriteMethod) : that.indexedWriteMethod != null) {
            return false;
        }
        return !(this.propertyType != null ? !this.propertyType.equals(that.propertyType) : that.propertyType != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.indexedReadMethod != null ? this.indexedReadMethod.hashCode() : 0);
        result = 31 * result + (this.indexedWriteMethod != null ? this.indexedWriteMethod.hashCode() : 0);
        result = 31 * result + (this.propertyType != null ? this.propertyType.hashCode() : 0);
        return result;
    }
}

