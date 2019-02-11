/*
 * Decompiled with CFR 0.139.
 */
package eognl.enhance;

import eognl.enhance.LocalReference;

public class LocalReferenceImpl
implements LocalReference {
    private final String name;
    private final Class<?> type;
    private final String expression;

    public LocalReferenceImpl(String name, String expression, Class<?> type) {
        this.name = name;
        this.type = type;
        this.expression = expression;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getExpression() {
        return this.expression;
    }

    @Override
    public Class<?> getType() {
        return this.type;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LocalReferenceImpl that = (LocalReferenceImpl)o;
        if (this.expression != null ? !this.expression.equals(that.expression) : that.expression != null) {
            return false;
        }
        if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
            return false;
        }
        return !(this.type != null ? !this.type.equals(that.type) : that.type != null);
    }

    public int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 31 * result + (this.type != null ? this.type.hashCode() : 0);
        result = 31 * result + (this.expression != null ? this.expression.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "LocalReferenceImpl[_name='" + this.name + '\'' + '\n' + ", _type=" + this.type + '\n' + ", _expression='" + this.expression + '\'' + '\n' + ']';
    }
}

