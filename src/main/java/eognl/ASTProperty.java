/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ASTChain;
import eognl.ASTConst;
import eognl.DynamicSubscript;
import eognl.EOgnlRuntime;
import eognl.MemberAccess;
import eognl.NoSuchPropertyException;
import eognl.Node;
import eognl.NodeType;
import eognl.NodeVisitor;
import eognl.NullHandler;
import eognl.ObjectIndexedPropertyDescriptor;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlOps;
import eognl.PropertyAccessor;
import eognl.SimpleNode;
import eognl.enhance.ExpressionCompiler;
import eognl.enhance.UnsupportedCompilationException;
import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

public class ASTProperty
extends SimpleNode
implements NodeType {
    private boolean indexedAccess = false;
    private Class<?> getterClass;
    private Class<?> setterClass;

    public ASTProperty(int id) {
        super(id);
    }

    public void setIndexedAccess(boolean value) {
        this.indexedAccess = value;
    }

    public boolean isIndexedAccess() {
        return this.indexedAccess;
    }

    public int getIndexedPropertyType(OgnlContext context, Object source) throws OgnlException {
        Class<?> type = context.getCurrentType();
        Class<?> prevType = context.getPreviousType();
        try {
            Object property;
            if (!this.isIndexedAccess() && (property = this.getProperty(context, source)) instanceof String) {
                int n = EOgnlRuntime.getIndexedPropertyType(context, source == null ? null : EOgnlRuntime.getCompiler(context).getInterfaceClass(source.getClass()), (String)property);
                return n;
            }
            return 0;
        }
        finally {
            context.setCurrentObject(source);
            context.setCurrentType(type);
            context.setPreviousType(prevType);
        }
    }

    public Object getProperty(OgnlContext context, Object source) throws OgnlException {
        return this.children[0].getValue(context, context.getRoot());
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object property = this.getProperty(context, source);
        Object result = EOgnlRuntime.getProperty(context, source, property);
        if (result == null) {
            result = context.getNullHandler(EOgnlRuntime.getTargetClass(source)).nullPropertyValue(context, source, property);
        }
        return result;
    }

    @Override
    protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
        EOgnlRuntime.setProperty(context, target, this.getProperty(context, target), value);
    }

    @Override
    public boolean isNodeSimpleProperty(OgnlContext context) throws OgnlException {
        return this.children != null && this.children.length == 1 && ((SimpleNode)this.children[0]).isConstant(context);
    }

    @Override
    public Class<?> getGetterClass() {
        return this.getterClass;
    }

    @Override
    public Class<?> getSetterClass() {
        return this.setterClass;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        if (context.getCurrentObject() == null) {
            throw new UnsupportedCompilationException("Current target is null.");
        }
        String result = "";
        Method m = null;
        try {
            Node child = this.children[0];
            if (this.isIndexedAccess()) {
                Object value = child.getValue(context, context.getRoot());
                if (value == null || DynamicSubscript.class.isAssignableFrom(value.getClass())) {
                    throw new UnsupportedCompilationException("Value passed as indexed property was null or not supported.");
                }
                String srcString = ASTProperty.getSourceString(context, child);
                if (context.get("_indexedMethod") != null) {
                    m = (Method)context.remove("_indexedMethod");
                    this.getterClass = m.getReturnType();
                    Object indexedValue = EOgnlRuntime.callMethod(context, target, m.getName(), new Object[]{value});
                    context.setCurrentType(this.getterClass);
                    context.setCurrentObject(indexedValue);
                    context.setCurrentAccessor(EOgnlRuntime.getCompiler(context).getSuperOrInterfaceClass(m, m.getDeclaringClass()));
                    return "." + m.getName() + "(" + srcString + ")";
                }
                PropertyAccessor propertyAccessor = context.getPropertyAccessor(target.getClass());
                Object currentObject = context.getCurrentObject();
                if (ASTConst.class.isInstance(child) && Number.class.isInstance(currentObject)) {
                    context.setCurrentType(EOgnlRuntime.getPrimitiveWrapperClass(currentObject.getClass()));
                }
                Object indexValue = propertyAccessor.getProperty(context, target, value);
                result = propertyAccessor.getSourceAccessor(context, target, srcString);
                this.getterClass = context.getCurrentType();
                context.setCurrentObject(indexValue);
                return result;
            }
            String name = ((ASTConst)child).getValue().toString();
            target = this.getTarget(context, target, name);
            PropertyDescriptor pd = EOgnlRuntime.getPropertyDescriptor(context.getCurrentObject().getClass(), name);
            if (pd != null && pd.getReadMethod() != null && !context.getMemberAccess().isAccessible(context, context.getCurrentObject(), pd.getReadMethod(), name)) {
                throw new UnsupportedCompilationException("Member access forbidden for property " + name + " on class " + context.getCurrentObject().getClass());
            }
            if (this.getIndexedPropertyType(context, context.getCurrentObject()) > 0 && pd != null) {
                if (pd instanceof IndexedPropertyDescriptor) {
                    m = ((IndexedPropertyDescriptor)pd).getIndexedReadMethod();
                } else if (pd instanceof ObjectIndexedPropertyDescriptor) {
                    m = ((ObjectIndexedPropertyDescriptor)pd).getIndexedReadMethod();
                } else {
                    throw new OgnlException("property '" + name + "' is not an indexed property");
                }
                if (this.parent == null) {
                    m = EOgnlRuntime.getReadMethod(context.getCurrentObject().getClass(), name);
                    result = String.valueOf(m.getName()) + "()";
                    this.getterClass = m.getReturnType();
                } else {
                    context.put("_indexedMethod", (Object)m);
                }
            } else {
                PropertyAccessor pa = context.getPropertyAccessor(context.getCurrentObject().getClass());
                if (context.getCurrentObject().getClass().isArray()) {
                    if (pd == null) {
                        pd = EOgnlRuntime.getProperty(context.getCurrentObject().getClass(), name);
                        if (pd != null && pd.getReadMethod() != null) {
                            m = pd.getReadMethod();
                            result = pd.getName();
                        } else {
                            this.getterClass = Integer.TYPE;
                            context.setCurrentAccessor(context.getCurrentObject().getClass());
                            context.setCurrentType(Integer.TYPE);
                            result = "." + name;
                        }
                    }
                } else if (pd != null && pd.getReadMethod() != null) {
                    m = pd.getReadMethod();
                    result = "." + m.getName() + "()";
                } else if (pa != null) {
                    Object currObj = context.getCurrentObject();
                    Class<?> currType = context.getCurrentType();
                    Class<?> prevType = context.getPreviousType();
                    String srcString = child.toGetSourceString(context, context.getRoot());
                    if (ASTConst.class.isInstance(child) && String.class.isInstance(context.getCurrentObject())) {
                        srcString = "\"" + srcString + "\"";
                    }
                    context.setCurrentObject(currObj);
                    context.setCurrentType(currType);
                    context.setPreviousType(prevType);
                    result = pa.getSourceAccessor(context, context.getCurrentObject(), srcString);
                    this.getterClass = context.getCurrentType();
                }
            }
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        if (m != null) {
            this.getterClass = m.getReturnType();
            context.setCurrentType(m.getReturnType());
            context.setCurrentAccessor(EOgnlRuntime.getCompiler(context).getSuperOrInterfaceClass(m, m.getDeclaringClass()));
        }
        context.setCurrentObject(target);
        return result;
    }

    Object getTarget(OgnlContext context, Object target, String name) throws OgnlException {
        Class<?> clazz = context.getCurrentObject().getClass();
        if (!Iterator.class.isAssignableFrom(clazz) || Iterator.class.isAssignableFrom(clazz) && !name.contains("next")) {
            Object currObj = target;
            try {
                try {
                    target = this.getValue(context, context.getCurrentObject());
                }
                catch (NoSuchPropertyException e) {
                    try {
                        target = this.getValue(context, context.getRoot());
                    }
                    catch (NoSuchPropertyException noSuchPropertyException) {
                        // empty catch block
                    }
                    context.setCurrentObject(currObj);
                }
            }
            finally {
                context.setCurrentObject(currObj);
            }
        }
        return target;
    }

    Method getIndexedWriteMethod(PropertyDescriptor pd) {
        if (IndexedPropertyDescriptor.class.isInstance(pd)) {
            return ((IndexedPropertyDescriptor)pd).getIndexedWriteMethod();
        }
        if (ObjectIndexedPropertyDescriptor.class.isInstance(pd)) {
            return ((ObjectIndexedPropertyDescriptor)pd).getIndexedWriteMethod();
        }
        return null;
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        String result = "";
        Method m = null;
        if (context.getCurrentObject() == null) {
            throw new UnsupportedCompilationException("Current target is null.");
        }
        try {
            Node child = this.children[0];
            if (this.isIndexedAccess()) {
                Object value = child.getValue(context, context.getRoot());
                if (value == null) {
                    throw new UnsupportedCompilationException("Value passed as indexed property is null, can't enhance statement to bytecode.");
                }
                String srcString = ASTProperty.getSourceString(context, child);
                if (context.get("_indexedMethod") != null) {
                    m = (Method)context.remove("_indexedMethod");
                    PropertyDescriptor pd = (PropertyDescriptor)context.remove("_indexedDescriptor");
                    boolean lastChild = this.lastChild(context);
                    if (lastChild && (m = this.getIndexedWriteMethod(pd)) == null) {
                        throw new UnsupportedCompilationException("Indexed property has no corresponding write method.");
                    }
                    this.setterClass = m.getParameterTypes()[0];
                    Object indexedValue = null;
                    if (!lastChild) {
                        indexedValue = EOgnlRuntime.callMethod(context, target, m.getName(), new Object[]{value});
                    }
                    context.setCurrentType(this.setterClass);
                    context.setCurrentAccessor(EOgnlRuntime.getCompiler(context).getSuperOrInterfaceClass(m, m.getDeclaringClass()));
                    if (!lastChild) {
                        context.setCurrentObject(indexedValue);
                        return "." + m.getName() + "(" + srcString + ")";
                    }
                    return "." + m.getName() + "(" + srcString + ", $3)";
                }
                PropertyAccessor propertyAccessor = context.getPropertyAccessor(target.getClass());
                Object currentObject = context.getCurrentObject();
                if (ASTConst.class.isInstance(child) && Number.class.isInstance(currentObject)) {
                    context.setCurrentType(EOgnlRuntime.getPrimitiveWrapperClass(currentObject.getClass()));
                }
                Object indexValue = propertyAccessor.getProperty(context, target, value);
                result = this.lastChild(context) ? propertyAccessor.getSourceSetter(context, target, srcString) : propertyAccessor.getSourceAccessor(context, target, srcString);
                this.getterClass = context.getCurrentType();
                context.setCurrentObject(indexValue);
                return result;
            }
            String name = ((ASTConst)child).getValue().toString();
            target = this.getTarget(context, target, name);
            PropertyDescriptor pd = EOgnlRuntime.getPropertyDescriptor(EOgnlRuntime.getCompiler(context).getInterfaceClass(context.getCurrentObject().getClass()), name);
            if (pd != null) {
                Method pdMethod;
                Method method = pdMethod = this.lastChild(context) ? pd.getWriteMethod() : pd.getReadMethod();
                if (pdMethod != null && !context.getMemberAccess().isAccessible(context, context.getCurrentObject(), pdMethod, name)) {
                    throw new UnsupportedCompilationException("Member access forbidden for property " + name + " on class " + context.getCurrentObject().getClass());
                }
            }
            if (pd != null && this.getIndexedPropertyType(context, context.getCurrentObject()) > 0) {
                if (pd instanceof IndexedPropertyDescriptor) {
                    IndexedPropertyDescriptor ipd = (IndexedPropertyDescriptor)pd;
                    m = this.lastChild(context) ? ipd.getIndexedWriteMethod() : ipd.getIndexedReadMethod();
                } else if (pd instanceof ObjectIndexedPropertyDescriptor) {
                    ObjectIndexedPropertyDescriptor opd = (ObjectIndexedPropertyDescriptor)pd;
                    m = this.lastChild(context) ? opd.getIndexedWriteMethod() : opd.getIndexedReadMethod();
                } else {
                    throw new OgnlException("property '" + name + "' is not an indexed property");
                }
                if (this.parent == null) {
                    m = EOgnlRuntime.getWriteMethod(context.getCurrentObject().getClass(), name);
                    Class<?> parm = m.getParameterTypes()[0];
                    String cast = parm.isArray() ? ExpressionCompiler.getCastString(parm) : parm.getName();
                    result = String.valueOf(m.getName()) + "((" + cast + ")$3)";
                    this.setterClass = parm;
                } else {
                    context.put("_indexedMethod", (Object)m);
                    context.put("_indexedDescriptor", (Object)pd);
                }
            } else {
                PropertyAccessor pa = context.getPropertyAccessor(context.getCurrentObject().getClass());
                if (target != null) {
                    this.setterClass = target.getClass();
                }
                if (this.parent != null && pd != null && pa == null) {
                    m = pd.getReadMethod();
                    result = String.valueOf(m.getName()) + "()";
                } else if (context.getCurrentObject().getClass().isArray()) {
                    result = "";
                } else if (pa != null) {
                    Object currObj = context.getCurrentObject();
                    String srcString = child.toGetSourceString(context, context.getRoot());
                    if (ASTConst.class.isInstance(child) && String.class.isInstance(context.getCurrentObject())) {
                        srcString = "\"" + srcString + "\"";
                    }
                    context.setCurrentObject(currObj);
                    result = !this.lastChild(context) ? pa.getSourceAccessor(context, context.getCurrentObject(), srcString) : pa.getSourceSetter(context, context.getCurrentObject(), srcString);
                    this.getterClass = context.getCurrentType();
                }
            }
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        context.setCurrentObject(target);
        if (m != null) {
            context.setCurrentType(m.getReturnType());
            context.setCurrentAccessor(EOgnlRuntime.getCompiler(context).getSuperOrInterfaceClass(m, m.getDeclaringClass()));
        }
        return result;
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }

    private static String getSourceString(OgnlContext context, Node child) {
        String cast;
        String srcString = child.toGetSourceString(context, context.getRoot());
        srcString = String.valueOf(ExpressionCompiler.getRootExpression(child, context.getRoot(), context)) + srcString;
        if (ASTChain.class.isInstance(child) && (cast = (String)context.remove("_preCast")) != null) {
            srcString = String.valueOf(cast) + srcString;
        }
        if (ASTConst.class.isInstance(child) && String.class.isInstance(context.getCurrentObject())) {
            srcString = "\"" + srcString + "\"";
        }
        return srcString;
    }
}

