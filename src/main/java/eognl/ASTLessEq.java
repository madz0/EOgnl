/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ComparisonExpression;
import eognl.Node;
import eognl.NodeVisitor;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlOps;
import eognl.OgnlParser;

class ASTLessEq
extends ComparisonExpression {
    public ASTLessEq(int id) {
        super(id);
    }

    public ASTLessEq(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object v2;
        Object v1 = this.children[0].getValue(context, source);
        return OgnlOps.greater(v1, v2 = this.children[1].getValue(context, source)) ? Boolean.FALSE : Boolean.TRUE;
    }

    @Override
    public String getExpressionOperator(int index) {
        return "<=";
    }

    @Override
    public String getComparisonFunction() {
        return "!org.apache.commons.ognl.OgnlOps.greater";
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }
}

