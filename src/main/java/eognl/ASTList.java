/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ASTConst;
import eognl.ASTCtor;
import eognl.EOgnlRuntime;
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
import java.util.ArrayList;
import java.util.List;

public class ASTList
extends SimpleNode
implements NodeType {
    public ASTList(int id) {
        super(id);
    }

    public ASTList(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        ArrayList<Object> answer = new ArrayList<Object>(this.jjtGetNumChildren());
        for (int i = 0; i < this.jjtGetNumChildren(); ++i) {
            answer.add(this.children[i].getValue(context, source));
        }
        return answer;
    }

    @Override
    public Class<?> getGetterClass() {
        return null;
    }

    @Override
    public Class<?> getSetterClass() {
        return null;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        String result = "";
        boolean array = false;
        if (this.parent != null && ASTCtor.class.isInstance(this.parent) && ((ASTCtor)this.parent).isArray()) {
            array = true;
        }
        context.setCurrentType(List.class);
        context.setCurrentAccessor(List.class);
        if (!array) {
            if (this.jjtGetNumChildren() < 1) {
                return "java.util.Arrays.asList( new Object[0])";
            }
            result = String.valueOf(result) + "java.util.Arrays.asList( new Object[] ";
        }
        result = String.valueOf(result) + "{ ";
        try {
            for (int i = 0; i < this.jjtGetNumChildren(); ++i) {
                if (i > 0) {
                    result = String.valueOf(result) + ", ";
                }
                Class<?> prevType = context.getCurrentType();
                Object objValue = this.children[i].getValue(context, context.getRoot());
                String value = this.children[i].toGetSourceString(context, target);
                if (ASTConst.class.isInstance(this.children[i])) {
                    context.setCurrentType(prevType);
                }
                value = String.valueOf(ExpressionCompiler.getRootExpression(this.children[i], target, context)) + value;
                String cast = "";
                if (ExpressionCompiler.shouldCast(this.children[i])) {
                    cast = (String)context.remove("_preCast");
                }
                if (cast == null) {
                    cast = "";
                }
                if (!ASTConst.class.isInstance(this.children[i])) {
                    value = String.valueOf(cast) + value;
                }
                Class ctorClass = (Class)context.get("_ctorClass");
                if (array && ctorClass != null && !ctorClass.isPrimitive()) {
                    Class<?> valueClass;
                    Class<?> class_ = valueClass = value != null ? value.getClass() : null;
                    if (NodeType.class.isAssignableFrom(this.children[i].getClass())) {
                        valueClass = ((NodeType)((Object)this.children[i])).getGetterClass();
                    }
                    OgnlExpressionCompiler compiler = EOgnlRuntime.getCompiler(context);
                    if (valueClass != null && ctorClass.isArray()) {
                        value = compiler.createLocalReference(context, "(" + ExpressionCompiler.getCastString(ctorClass) + ")org.apache.commons.ognl.OgnlOps.toArray(" + value + ", " + ctorClass.getComponentType().getName() + ".class, true)", ctorClass);
                    } else if (ctorClass.isPrimitive()) {
                        Class<?> wrapClass = EOgnlRuntime.getPrimitiveWrapperClass(ctorClass);
                        value = compiler.createLocalReference(context, "((" + wrapClass.getName() + ")org.apache.commons.ognl.OgnlOps.convertValue(" + value + "," + wrapClass.getName() + ".class, true))." + EOgnlRuntime.getNumericValueGetter(wrapClass), ctorClass);
                    } else if (ctorClass != Object.class) {
                        value = compiler.createLocalReference(context, "(" + ctorClass.getName() + ")org.apache.commons.ognl.OgnlOps.convertValue(" + value + "," + ctorClass.getName() + ".class)", ctorClass);
                    } else if (NodeType.class.isInstance(this.children[i]) && ((NodeType)((Object)this.children[i])).getGetterClass() != null && Number.class.isAssignableFrom(((NodeType)((Object)this.children[i])).getGetterClass()) || valueClass.isPrimitive()) {
                        value = " ($w) (" + value + ")";
                    } else if (valueClass.isPrimitive()) {
                        value = "($w) (" + value + ")";
                    }
                } else if (ctorClass == null || !ctorClass.isPrimitive()) {
                    value = " ($w) (" + value + ")";
                }
                if (objValue == null || value.length() <= 0) {
                    value = "null";
                }
                result = String.valueOf(result) + value;
            }
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        context.setCurrentType(List.class);
        context.setCurrentAccessor(List.class);
        result = String.valueOf(result) + "}";
        if (!array) {
            result = String.valueOf(result) + ")";
        }
        return result;
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        throw new UnsupportedCompilationException("Can't generate setter for ASTList.");
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }
}

