/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.EOgnlRuntime;
import eognl.NoSuchPropertyException;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlOps;
import eognl.PropertyAccessor;
import eognl.enhance.ExpressionCompiler;
import eognl.enhance.OgnlExpressionCompiler;
import eognl.enhance.UnsupportedCompilationException;
import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ObjectPropertyAccessor
implements PropertyAccessor {
    public Object getPossibleProperty(OgnlContext context, Object target, String name) throws OgnlException {
        Object result;
        try {
            result = EOgnlRuntime.getMethodValue(context, target, name, true);
            if (result == EOgnlRuntime.NotFound) {
                result = EOgnlRuntime.getFieldValue(context, target, name, true);
            }
        }
        catch (IntrospectionException ex) {
            throw new OgnlException(name, ex);
        }
        catch (OgnlException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new OgnlException(name, ex);
        }
        return result;
    }

    public Object setPossibleProperty(OgnlContext context, Object target, String name, Object value) throws OgnlException {
        Object result = null;
        try {
            Method m;
            if (!EOgnlRuntime.setMethodValue(context, target, name, value, true)) {
                Object object = result = EOgnlRuntime.setFieldValue(context, target, name, value) ? null : EOgnlRuntime.NotFound;
            }
            if (result == EOgnlRuntime.NotFound && (m = EOgnlRuntime.getWriteMethod(target.getClass(), name)) != null) {
                result = m.invoke(target, value);
            }
        }
        catch (IntrospectionException ex) {
            throw new OgnlException(name, ex);
        }
        catch (OgnlException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new OgnlException(name, ex);
        }
        return result;
    }

    public boolean hasGetProperty(OgnlContext context, Object target, Object oname) throws OgnlException {
        try {
            return EOgnlRuntime.hasGetProperty(context, target, oname);
        }
        catch (IntrospectionException ex) {
            throw new OgnlException("checking if " + target + " has gettable property " + oname, ex);
        }
    }

    public boolean hasSetProperty(OgnlContext context, Object target, Object oname) throws OgnlException {
        try {
            return EOgnlRuntime.hasSetProperty(context, target, oname);
        }
        catch (IntrospectionException ex) {
            throw new OgnlException("checking if " + target + " has settable property " + oname, ex);
        }
    }

    @Override
    public Object getProperty(OgnlContext context, Object target, Object oname) throws OgnlException {
        String name = oname.toString();
        Object result = this.getPossibleProperty(context, target, name);
        if (result == EOgnlRuntime.NotFound) {
            throw new NoSuchPropertyException(target, name);
        }
        return result;
    }

    @Override
    public void setProperty(OgnlContext context, Object target, Object oname, Object value) throws OgnlException {
        String name = oname.toString();
        Object result = this.setPossibleProperty(context, target, name, value);
        if (result == EOgnlRuntime.NotFound) {
            throw new NoSuchPropertyException(target, name);
        }
    }

    public Class<?> getPropertyClass(OgnlContext context, Object target, Object index) {
        Method m;
        block7 : {
            try {
                m = EOgnlRuntime.getReadMethod(target.getClass(), index.toString());
                if (m != null) break block7;
                if (String.class.isAssignableFrom(index.getClass()) && !target.getClass().isArray()) {
                    String key = ((String)index).replaceAll("\"", "");
                    try {
                        Field f = target.getClass().getField(key);
                        if (f != null) {
                            return f.getType();
                        }
                    }
                    catch (NoSuchFieldException e) {
                        return null;
                    }
                }
                return null;
            }
            catch (Throwable t) {
                throw OgnlOps.castToRuntime(t);
            }
        }
        return m.getReturnType();
    }

    @Override
    public String getSourceAccessor(OgnlContext context, Object target, Object index) {
        Method m;
        block7 : {
            try {
                String methodName = index.toString().replaceAll("\"", "");
                m = EOgnlRuntime.getReadMethod(target.getClass(), methodName);
                if (m == null && context.getCurrentObject() != null) {
                    m = EOgnlRuntime.getReadMethod(target.getClass(), context.getCurrentObject().toString().replaceAll("\"", ""));
                }
                if (m != null) break block7;
                try {
                    Field f;
                    if (String.class.isAssignableFrom(index.getClass()) && !target.getClass().isArray() && (f = target.getClass().getField(methodName)) != null) {
                        context.setCurrentType(f.getType());
                        context.setCurrentAccessor(f.getDeclaringClass());
                        return "." + f.getName();
                    }
                }
                catch (NoSuchFieldException f) {
                    // empty catch block
                }
                return "";
            }
            catch (Throwable t) {
                throw OgnlOps.castToRuntime(t);
            }
        }
        context.setCurrentType(m.getReturnType());
        OgnlExpressionCompiler compiler = EOgnlRuntime.getCompiler(context);
        context.setCurrentAccessor(compiler.getSuperOrInterfaceClass(m, m.getDeclaringClass()));
        return "." + m.getName() + "()";
    }

    @Override
    public String getSourceSetter(OgnlContext context, Object target, Object index) {
        try {
            String conversion;
            String methodName = index.toString().replaceAll("\"", "");
            Method m = EOgnlRuntime.getWriteMethod(target.getClass(), methodName);
            if (m == null && context.getCurrentObject() != null && context.getCurrentObject().toString() != null) {
                m = EOgnlRuntime.getWriteMethod(target.getClass(), context.getCurrentObject().toString().replaceAll("\"", ""));
            }
            if (m == null || m.getParameterTypes() == null || m.getParameterTypes().length <= 0) {
                throw new UnsupportedCompilationException("Unable to determine setting expression on " + context.getCurrentObject() + " with index of " + index);
            }
            Class<?> parm = m.getParameterTypes()[0];
            if (m.getParameterTypes().length > 1) {
                throw new UnsupportedCompilationException("Object property accessors can only support single parameter setters.");
            }
            OgnlExpressionCompiler compiler = EOgnlRuntime.getCompiler(context);
            if (parm.isPrimitive()) {
                Class<?> wrapClass = EOgnlRuntime.getPrimitiveWrapperClass(parm);
                conversion = compiler.createLocalReference(context, "((" + wrapClass.getName() + ")org.apache.commons.ognl.OgnlOps#convertValue($3," + wrapClass.getName() + ".class, true))." + EOgnlRuntime.getNumericValueGetter(wrapClass), parm);
            } else {
                conversion = parm.isArray() ? compiler.createLocalReference(context, "(" + ExpressionCompiler.getCastString(parm) + ")org.apache.commons.ognl.OgnlOps#toArray($3," + parm.getComponentType().getName() + ".class)", parm) : compiler.createLocalReference(context, "(" + parm.getName() + ")org.apache.commons.ognl.OgnlOps#convertValue($3," + parm.getName() + ".class)", parm);
            }
            context.setCurrentType(m.getReturnType());
            context.setCurrentAccessor(compiler.getSuperOrInterfaceClass(m, m.getDeclaringClass()));
            return "." + m.getName() + "(" + conversion + ")";
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
    }
}

