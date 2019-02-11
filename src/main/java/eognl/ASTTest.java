/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.EOgnlRuntime;
import eognl.ExpressionNode;
import eognl.Node;
import eognl.NodeVisitor;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlOps;
import eognl.OgnlParser;
import eognl.enhance.UnsupportedCompilationException;

class ASTTest
extends ExpressionNode {
    public ASTTest(int id) {
        super(id);
    }

    public ASTTest(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object test = this.children[0].getValue(context, source);
        int branch = OgnlOps.booleanValue(test) ? 1 : 2;
        return this.children[branch].getValue(context, source);
    }

    @Override
    protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
        Object test = this.children[0].getValue(context, target);
        int branch = OgnlOps.booleanValue(test) ? 1 : 2;
        this.children[branch].setValue(context, target, value);
    }

    @Override
    public String getExpressionOperator(int index) {
        return index == 1 ? "?" : ":";
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        if (target == null) {
            throw new UnsupportedCompilationException("evaluation resulted in null expression.");
        }
        if (this.children.length != 3) {
            throw new UnsupportedCompilationException("Can only compile test expressions with two children." + this.children.length);
        }
        String result = "";
        try {
            String first = EOgnlRuntime.getChildSource(context, target, this.children[0]);
            if (!EOgnlRuntime.isBoolean(first) && !context.getCurrentType().isPrimitive()) {
                first = EOgnlRuntime.getCompiler(context).createLocalReference(context, first, context.getCurrentType());
            }
            if (ExpressionNode.class.isInstance(this.children[0])) {
                first = "(" + first + ")";
            }
            String second = EOgnlRuntime.getChildSource(context, target, this.children[1]);
            Class<?> secondType = context.getCurrentType();
            if (!EOgnlRuntime.isBoolean(second) && !context.getCurrentType().isPrimitive()) {
                second = EOgnlRuntime.getCompiler(context).createLocalReference(context, second, context.getCurrentType());
            }
            if (ExpressionNode.class.isInstance(this.children[1])) {
                second = "(" + second + ")";
            }
            String third = EOgnlRuntime.getChildSource(context, target, this.children[2]);
            Class<?> thirdType = context.getCurrentType();
            if (!EOgnlRuntime.isBoolean(third) && !context.getCurrentType().isPrimitive()) {
                third = EOgnlRuntime.getCompiler(context).createLocalReference(context, third, context.getCurrentType());
            }
            if (ExpressionNode.class.isInstance(this.children[2])) {
                third = "(" + third + ")";
            }
            boolean mismatched = secondType.isPrimitive() && !thirdType.isPrimitive() || !secondType.isPrimitive() && thirdType.isPrimitive();
            result = String.valueOf(result) + "org.apache.commons.ognl.OgnlOps.booleanValue(" + first + ")";
            result = String.valueOf(result) + " ? ";
            result = String.valueOf(result) + (mismatched ? " ($w) " : "") + second;
            result = String.valueOf(result) + " : ";
            result = String.valueOf(result) + (mismatched ? " ($w) " : "") + third;
            context.setCurrentObject(target);
            context.setCurrentType(mismatched ? Object.class : secondType);
            return result;
        }
        catch (NullPointerException e) {
            throw new UnsupportedCompilationException("evaluation resulted in null expression.");
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }
}

