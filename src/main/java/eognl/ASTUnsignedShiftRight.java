/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.EOgnlRuntime;
import eognl.Node;
import eognl.NodeVisitor;
import eognl.NumericExpression;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlOps;
import eognl.OgnlParser;

class ASTUnsignedShiftRight
extends NumericExpression {
    public ASTUnsignedShiftRight(int id) {
        super(id);
    }

    public ASTUnsignedShiftRight(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object v1 = this.children[0].getValue(context, source);
        Object v2 = this.children[1].getValue(context, source);
        return OgnlOps.unsignedShiftRight(v1, v2);
    }

    @Override
    public String getExpressionOperator(int index) {
        return ">>>";
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        String result = "";
        try {
            String child1 = EOgnlRuntime.getChildSource(context, target, this.children[0]);
            child1 = this.coerceToNumeric(child1, context, this.children[0]);
            String child2 = EOgnlRuntime.getChildSource(context, target, this.children[1]);
            child2 = this.coerceToNumeric(child2, context, this.children[1]);
            Object v1 = this.children[0].getValue(context, target);
            int type = OgnlOps.getNumericType(v1);
            if (type <= 4) {
                child1 = "(int)" + child1;
                child2 = "(int)" + child2;
            }
            result = String.valueOf(child1) + " >>> " + child2;
            context.setCurrentType(Integer.TYPE);
            context.setCurrentObject(this.getValueBody(context, target));
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        return result;
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }
}

