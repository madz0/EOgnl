/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.BooleanExpression;
import eognl.Node;
import eognl.NodeVisitor;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlOps;
import eognl.OgnlParser;

class ASTNot
extends BooleanExpression {
    public ASTNot(int id) {
        super(id);
    }

    public ASTNot(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        return OgnlOps.booleanValue(this.children[0].getValue(context, source)) ? Boolean.FALSE : Boolean.TRUE;
    }

    @Override
    public String getExpressionOperator(int index) {
        return "!";
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        try {
            String srcString = super.toGetSourceString(context, target);
            if (srcString == null || srcString.trim().length() < 1) {
                srcString = "null";
            }
            context.setCurrentType(Boolean.TYPE);
            return "(! org.apache.commons.ognl.OgnlOps.booleanValue(" + srcString + ") )";
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

