/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.EOgnlRuntime;
import eognl.NodeType;
import eognl.NodeVisitor;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlOps;
import eognl.OgnlParser;
import eognl.SimpleNode;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ASTStaticField
extends SimpleNode
implements NodeType {
    private String className;
    private String fieldName;
    private Class<?> getterClass;

    public ASTStaticField(int id) {
        super(id);
    }

    public ASTStaticField(OgnlParser p, int id) {
        super(p, id);
    }

    void init(String className, String fieldName) {
        this.className = className;
        this.fieldName = fieldName;
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        return EOgnlRuntime.getStaticField(context, this.className, this.fieldName);
    }

    @Override
    public boolean isNodeConstant(OgnlContext context) throws OgnlException {
        boolean result = false;
        Exception cause = null;
        try {
            Class<?> clazz = EOgnlRuntime.classForName(context, this.className);
            if ("class".equals(this.fieldName)) {
                result = true;
            } else if (clazz.isEnum()) {
                result = true;
            } else {
                Field field = clazz.getField(this.fieldName);
                if (!Modifier.isStatic(field.getModifiers())) {
                    throw new OgnlException("Field " + this.fieldName + " of class " + this.className + " is not static");
                }
                result = Modifier.isFinal(field.getModifiers());
            }
        }
        catch (ClassNotFoundException e) {
            cause = e;
        }
        catch (NoSuchFieldException e) {
            cause = e;
        }
        catch (SecurityException e) {
            cause = e;
        }
        if (cause != null) {
            throw new OgnlException("Could not get static field " + this.fieldName + " from class " + this.className, cause);
        }
        return result;
    }

    Class<?> getFieldClass(OgnlContext context) throws OgnlException {
        Exception cause;
        try {
            Class<?> clazz = EOgnlRuntime.classForName(context, this.className);
            if ("class".equals(this.fieldName)) {
                return clazz;
            }
            if (clazz.isEnum()) {
                return clazz;
            }
            Field field = clazz.getField(this.fieldName);
            return field.getType();
        }
        catch (ClassNotFoundException e) {
            cause = e;
        }
        catch (NoSuchFieldException e) {
            cause = e;
        }
        catch (SecurityException e) {
            cause = e;
        }
        throw new OgnlException("Could not get static field " + this.fieldName + " from class " + this.className, cause);
    }

    @Override
    public Class<?> getGetterClass() {
        return this.getterClass;
    }

    @Override
    public Class<?> getSetterClass() {
        return this.getterClass;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        try {
            Object obj = EOgnlRuntime.getStaticField(context, this.className, this.fieldName);
            context.setCurrentObject(obj);
            this.getterClass = this.getFieldClass(context);
            context.setCurrentType(this.getterClass);
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        return String.valueOf(this.className) + "." + this.fieldName;
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        try {
            Object obj = EOgnlRuntime.getStaticField(context, this.className, this.fieldName);
            context.setCurrentObject(obj);
            this.getterClass = this.getFieldClass(context);
            context.setCurrentType(this.getterClass);
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        return String.valueOf(this.className) + "." + this.fieldName;
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }

    String getFieldName() {
        return this.fieldName;
    }

    String getClassName() {
        return this.className;
    }
}

