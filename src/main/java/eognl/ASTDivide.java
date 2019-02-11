/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.Node;
import eognl.NodeVisitor;
import eognl.NumericExpression;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlOps;
import eognl.OgnlParser;

class ASTDivide
extends NumericExpression {
    public ASTDivide(int id) {
        super(id);
    }

    public ASTDivide(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object v1 = this.children[0].getValue(context, source);
        Object v2 = this.children[1].getValue(context, source);
        return OgnlOps.divide(v1, v2);
    }

    @Override
    public String getExpressionOperator(int index) {
        return "/";
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }
}

