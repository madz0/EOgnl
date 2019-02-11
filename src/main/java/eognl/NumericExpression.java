/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ASTConst;
import eognl.EOgnlRuntime;
import eognl.ExpressionNode;
import eognl.Node;
import eognl.NodeType;
import eognl.OgnlContext;
import eognl.OgnlOps;
import eognl.OgnlParser;
import eognl.enhance.ExpressionCompiler;

public abstract class NumericExpression
extends ExpressionNode
implements NodeType {
    private static final long serialVersionUID = -174952564587478850L;
    protected Class<?> getterClass;

    public NumericExpression(int id) {
        super(id);
    }

    public NumericExpression(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    public Class<?> getGetterClass() {
        if (this.getterClass != null) {
            return this.getterClass;
        }
        return Double.TYPE;
    }

    @Override
    public Class<?> getSetterClass() {
        return null;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        StringBuilder result = new StringBuilder("");
        try {
            Object value = this.getValueBody(context, target);
            if (value != null) {
                this.getterClass = value.getClass();
            }
            for (int i = 0; i < this.children.length; ++i) {
                if (i > 0) {
                    result.append(" ").append(this.getExpressionOperator(i)).append(" ");
                }
                String str = EOgnlRuntime.getChildSource(context, target, this.children[i]);
                result.append(this.coerceToNumeric(str, context, this.children[i]));
            }
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        return result.toString();
    }

    public String coerceToNumeric(String source, OgnlContext context, Node child) {
        StringBuilder ret = new StringBuilder(source);
        Object value = context.getCurrentObject();
        if (ASTConst.class.isInstance(child) && value != null) {
            return value.toString();
        }
        if (context.getCurrentType() != null && !context.getCurrentType().isPrimitive() && context.getCurrentObject() != null && Number.class.isInstance(context.getCurrentObject())) {
            ret = new StringBuilder("((").append(ExpressionCompiler.getCastString(context.getCurrentObject().getClass())).append(")").append(ret).append(").").append(EOgnlRuntime.getNumericValueGetter(context.getCurrentObject().getClass()));
        } else if (context.getCurrentType() != null && context.getCurrentType().isPrimitive() && (ASTConst.class.isInstance(child) || NumericExpression.class.isInstance(child))) {
            ret.append(EOgnlRuntime.getNumericLiteral(context.getCurrentType()));
        } else if (context.getCurrentType() != null && String.class.isAssignableFrom(context.getCurrentType())) {
            ret = new StringBuilder("Double.parseDouble(").append(ret).append(")");
            context.setCurrentType(Double.TYPE);
        }
        if (NumericExpression.class.isInstance(child)) {
            ret = new StringBuilder("(").append(ret).append(")");
        }
        return ret.toString();
    }
}

