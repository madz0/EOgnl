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

class ASTBitNegate
extends NumericExpression {
    public ASTBitNegate(int id) {
        super(id);
    }

    public ASTBitNegate(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        return OgnlOps.bitNegate(this.children[0].getValue(context, source));
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        String source = this.children[0].toGetSourceString(context, target);
        if (!ASTBitNegate.class.isInstance(this.children[0])) {
            return "~(" + super.coerceToNumeric(source, context, this.children[0]) + ")";
        }
        return "~(" + source + ")";
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }
}

