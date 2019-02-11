/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ASTChain;
import eognl.ASTConst;
import eognl.ASTMethodUtil;
import eognl.ASTProperty;
import eognl.ASTStaticMethod;
import eognl.ASTTest;
import eognl.EOgnlRuntime;
import eognl.Node;
import eognl.NodeType;
import eognl.NodeVisitor;
import eognl.NullHandler;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlOps;
import eognl.OgnlParser;
import eognl.SimpleNode;
import eognl.enhance.ExpressionCompiler;
import eognl.enhance.OgnlExpressionCompiler;
import eognl.enhance.OrderedReturn;
import eognl.enhance.UnsupportedCompilationException;
import java.lang.reflect.Method;
import java.util.Map;

public class ASTMethod
extends SimpleNode
implements OrderedReturn,
NodeType {
    private String methodName;
    private String lastExpression;
    private String coreExpression;
    private Class<?> getterClass;

    public ASTMethod(int id) {
        super(id);
    }

    public ASTMethod(OgnlParser p, int id) {
        super(p, id);
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return this.methodName;
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object[] args = new Object[this.jjtGetNumChildren()];
        Object root = context.getRoot();
        for (int i = 0; i < args.length; ++i) {
            args[i] = this.children[i].getValue(context, root);
        }
        Object result = EOgnlRuntime.callMethod(context, source, this.methodName, args);
        if (result == null) {
            NullHandler nullHandler = context.getNullHandler(EOgnlRuntime.getTargetClass(source));
            result = nullHandler.nullMethodResult(context, source, this.methodName, args);
        }
        return result;
    }

    @Override
    public String getLastExpression() {
        return this.lastExpression;
    }

    @Override
    public String getCoreExpression() {
        return this.coreExpression;
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
        Method method;
        String post;
        OgnlExpressionCompiler compiler;
        StringBuilder sourceStringBuilder;
        block16 : {
            if (target == null) {
                throw new UnsupportedCompilationException("Target object is null.");
            }
            post = "";
            compiler = EOgnlRuntime.getCompiler(context);
            try {
              method = EOgnlRuntime.getMethod(context, context.getCurrentType() != null ? context.getCurrentType() : target.getClass(), this.methodName, this.children, false);
              if (method == null) {
                method = EOgnlRuntime.getReadMethod(target.getClass(), this.methodName, this.children != null ? this.children.length : -1);
            }
            if (method != null) break block16;
            } catch (Exception e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            
            method = EOgnlRuntime.getWriteMethod(target.getClass(), this.methodName, this.children != null ? this.children.length : -1);
            if (method != null) {
                context.setCurrentType(method.getReturnType());
                context.setCurrentAccessor(compiler.getSuperOrInterfaceClass(method, method.getDeclaringClass()));
                this.coreExpression = this.toSetSourceString(context, target);
                if (this.coreExpression == null || this.coreExpression.length() < 1) {
                    throw new UnsupportedCompilationException("can't find suitable getter method");
                }
                this.coreExpression = String.valueOf(this.coreExpression) + ";";
                this.lastExpression = "null";
                return this.coreExpression;
            }
            return "";
        }
        try {
            this.getterClass = method.getReturnType();
            boolean varArgs = method.isVarArgs();
            if (varArgs) {
                throw new UnsupportedCompilationException("Javassist does not currently support varargs method calls");
            }
            sourceStringBuilder = new StringBuilder().append(".").append(method.getName()).append("(");
            if (this.children != null && this.children.length > 0) {
                Class<?>[] parms = method.getParameterTypes();
                String prevCast = (String)context.remove("_preCast");
                for (int i = 0; i < this.children.length; ++i) {
                    if (i > 0) {
                        sourceStringBuilder.append(", ");
                    }
                    Class<?> prevType = context.getCurrentType();
                    Object root = context.getRoot();
                    context.setCurrentObject(root);
                    context.setCurrentType(root != null ? root.getClass() : null);
                    context.setCurrentAccessor(null);
                    context.setPreviousType(null);
                    Node child = this.children[i];
                    String parmString = ASTMethodUtil.getParmString(context, root, child, prevType);
                    Class<?> valueClass = ASTMethodUtil.getValueClass(context, root, child);
                    if ((!varArgs || varArgs && i + 1 < parms.length) && valueClass != parms[i]) {
                        parmString = ASTMethodUtil.getParmString(context, parms[i], parmString, child, valueClass, ".class, true)");
                    }
                    sourceStringBuilder.append(parmString);
                }
                if (prevCast != null) {
                    context.put("_preCast", (Object)prevCast);
                }
            }
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        try {
            Object contextObj = this.getValueBody(context, target);
            context.setCurrentObject(contextObj);
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        sourceStringBuilder.append(")").append(post);
        if (method.getReturnType() == Void.TYPE) {
            this.coreExpression = String.valueOf(sourceStringBuilder.toString()) + ";";
            this.lastExpression = "null";
        }
        context.setCurrentType(method.getReturnType());
        context.setCurrentAccessor(compiler.getSuperOrInterfaceClass(method, method.getDeclaringClass()));
        return sourceStringBuilder.toString();
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        boolean varArgs;
        Method method = EOgnlRuntime.getWriteMethod(context.getCurrentType() != null ? context.getCurrentType() : target.getClass(), this.methodName, this.children != null ? this.children.length : -1);
        if (method == null) {
            throw new UnsupportedCompilationException("Unable to determine setter method generation for " + this.methodName);
        }
        String post = "";
        String result = "." + method.getName() + "(";
        if (method.getReturnType() != Void.TYPE && method.getReturnType().isPrimitive() && (this.parent == null || !ASTTest.class.isInstance(this.parent))) {
            Class<?> wrapper = EOgnlRuntime.getPrimitiveWrapperClass(method.getReturnType());
            ExpressionCompiler.addCastString(context, "new " + wrapper.getName() + "(");
            post = ")";
            this.getterClass = wrapper;
        }
        if (varArgs = method.isVarArgs()) {
            throw new UnsupportedCompilationException("Javassist does not currently support varargs method calls");
        }
        OgnlExpressionCompiler compiler = EOgnlRuntime.getCompiler(context);
        try {
            if (this.children != null && this.children.length > 0) {
                Class<?>[] parms = method.getParameterTypes();
                String prevCast = (String)context.remove("_preCast");
                for (int i = 0; i < this.children.length; ++i) {
                    Class<?> valueClass;
                    if (i > 0) {
                        result = String.valueOf(result) + ", ";
                    }
                    Class<?> prevType = context.getCurrentType();
                    context.setCurrentObject(context.getRoot());
                    context.setCurrentType(context.getRoot() != null ? context.getRoot().getClass() : null);
                    context.setCurrentAccessor(null);
                    context.setPreviousType(null);
                    Node child = this.children[i];
                    Object value = child.getValue(context, context.getRoot());
                    String parmString = child.toSetSourceString(context, context.getRoot());
                    if (context.getCurrentType() == Void.TYPE || context.getCurrentType() == Void.TYPE) {
                        throw new UnsupportedCompilationException("Method argument can't be a void type.");
                    }
                    if (parmString == null || parmString.trim().length() < 1) {
                        if (ASTProperty.class.isInstance(child) || ASTMethod.class.isInstance(child) || ASTStaticMethod.class.isInstance(child) || ASTChain.class.isInstance(child)) {
                            throw new UnsupportedCompilationException("ASTMethod setter child returned null from a sub property expression.");
                        }
                        parmString = "null";
                    }
                    if (ASTConst.class.isInstance(child)) {
                        context.setCurrentType(prevType);
                    }
                    parmString = String.valueOf(ExpressionCompiler.getRootExpression(child, context.getRoot(), context)) + parmString;
                    String cast = "";
                    if (ExpressionCompiler.shouldCast(child)) {
                        cast = (String)context.remove("_preCast");
                    }
                    if (cast == null) {
                        cast = "";
                    }
                    parmString = String.valueOf(cast) + parmString;
                    Class<?> class_ = valueClass = value != null ? value.getClass() : null;
                    if (NodeType.class.isAssignableFrom(child.getClass())) {
                        valueClass = ((NodeType)((Object)child)).getGetterClass();
                    }
                    if (valueClass != parms[i]) {
                        parmString = ASTMethodUtil.getParmString(context, parms[i], parmString, child, valueClass, ".class)");
                    }
                    result = String.valueOf(result) + parmString;
                }
                if (prevCast != null) {
                    context.put("_preCast", (Object)prevCast);
                }
            }
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        try {
            Object contextObj = this.getValueBody(context, target);
            context.setCurrentObject(contextObj);
        }
        catch (Throwable contextObj) {
            // empty catch block
        }
        context.setCurrentType(method.getReturnType());
        context.setCurrentAccessor(compiler.getSuperOrInterfaceClass(method, method.getDeclaringClass()));
        return String.valueOf(result) + ")" + post;
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }
}

