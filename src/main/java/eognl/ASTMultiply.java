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

class ASTMultiply
extends NumericExpression {
    public ASTMultiply(int id) {
        super(id);
    }

    public ASTMultiply(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    public void jjtClose() {
        this.flattenTree();
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object result = this.children[0].getValue(context, source);
        for (int i = 1; i < this.children.length; ++i) {
            result = OgnlOps.multiply(result, this.children[i].getValue(context, source));
        }
        return result;
    }

    @Override
    public String getExpressionOperator(int index) {
        return "*";
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }
}

