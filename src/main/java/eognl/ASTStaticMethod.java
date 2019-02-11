/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ASTMethodUtil;
import eognl.EOgnlRuntime;
import eognl.MemberAccess;
import eognl.Node;
import eognl.NodeType;
import eognl.NodeVisitor;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlOps;
import eognl.OgnlParser;
import eognl.SimpleNode;
import eognl.enhance.ExpressionCompiler;
import eognl.enhance.OgnlExpressionCompiler;
import eognl.enhance.UnsupportedCompilationException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Map;

public class ASTStaticMethod
extends SimpleNode
implements NodeType {
    private String className;
    private String methodName;
    private Class<?> getterClass;

    public ASTStaticMethod(int id) {
        super(id);
    }

    public ASTStaticMethod(OgnlParser p, int id) {
        super(p, id);
    }

    void init(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object[] args = new Object[this.jjtGetNumChildren()];
        Object root = context.getRoot();
        int icount = args.length;
        for (int i = 0; i < icount; ++i) {
            args[i] = this.children[i].getValue(context, root);
        }
        return EOgnlRuntime.callStaticMethod(context, this.className, this.methodName, args);
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
        String result = String.valueOf(this.className) + "#" + this.methodName + "(";
        try {
            Class<?> clazz = EOgnlRuntime.classForName(context, this.className);
            Method m = EOgnlRuntime.getMethod(context, clazz, this.methodName, this.children, true);
            if (m == null) {
                throw new UnsupportedCompilationException("Unable to find class/method combo " + this.className + " / " + this.methodName);
            }
            if (!context.getMemberAccess().isAccessible(context, clazz, m, this.methodName)) {
                throw new UnsupportedCompilationException("Method is not accessible, check your jvm runtime security settings. For static class method " + this.className + " / " + this.methodName);
            }
            OgnlExpressionCompiler compiler = EOgnlRuntime.getCompiler(context);
            if (this.children != null && this.children.length > 0) {
                Class<?>[] parms = m.getParameterTypes();
                for (int i = 0; i < this.children.length; ++i) {
                    if (i > 0) {
                        result = String.valueOf(result) + ", ";
                    }
                    Class<?> prevType = context.getCurrentType();
                    Node child = this.children[i];
                    Object root = context.getRoot();
                    String parmString = ASTMethodUtil.getParmString(context, root, child, prevType);
                    Class<?> valueClass = ASTMethodUtil.getValueClass(context, root, child);
                    if (valueClass != parms[i]) {
                        if (parms[i].isArray()) {
                            parmString = compiler.createLocalReference(context, "(" + ExpressionCompiler.getCastString(parms[i]) + ")org.apache.commons.ognl.OgnlOps.toArray(" + parmString + ", " + parms[i].getComponentType().getName() + ".class, true)", parms[i]);
                        } else if (parms[i].isPrimitive()) {
                            Class<?> wrapClass = EOgnlRuntime.getPrimitiveWrapperClass(parms[i]);
                            parmString = compiler.createLocalReference(context, "((" + wrapClass.getName() + ")org.apache.commons.ognl.OgnlOps.convertValue(" + parmString + "," + wrapClass.getName() + ".class, true))." + EOgnlRuntime.getNumericValueGetter(wrapClass), parms[i]);
                        } else if (parms[i] != Object.class) {
                            parmString = compiler.createLocalReference(context, "(" + parms[i].getName() + ")org.apache.commons.ognl.OgnlOps.convertValue(" + parmString + "," + parms[i].getName() + ".class)", parms[i]);
                        } else if (NodeType.class.isInstance(child) && ((NodeType)((Object)child)).getGetterClass() != null && Number.class.isAssignableFrom(((NodeType)((Object)child)).getGetterClass()) || valueClass.isPrimitive()) {
                            parmString = " ($w) " + parmString;
                        } else if (valueClass.isPrimitive()) {
                            parmString = "($w) " + parmString;
                        }
                    }
                    result = String.valueOf(result) + parmString;
                }
            }
            result = String.valueOf(result) + ")";
            try {
                Object contextObj = this.getValueBody(context, target);
                context.setCurrentObject(contextObj);
            }
            catch (Throwable contextObj) {
                // empty catch block
            }
            if (m != null) {
                this.getterClass = m.getReturnType();
                context.setCurrentType(m.getReturnType());
                context.setCurrentAccessor(compiler.getSuperOrInterfaceClass(m, m.getDeclaringClass()));
            }
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        return result;
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        return this.toGetSourceString(context, target);
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }

    public String getClassName() {
        return this.className;
    }

    public String getMethodName() {
        return this.methodName;
    }
}

