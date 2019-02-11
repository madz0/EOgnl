/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.Node;
import eognl.NodeVisitor;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlParser;
import eognl.SimpleNode;

class ASTKeyValue
extends SimpleNode {
    public ASTKeyValue(int id) {
        super(id);
    }

    public ASTKeyValue(OgnlParser p, int id) {
        super(p, id);
    }

    protected Node getKey() {
        return this.children[0];
    }

    protected Node getValue() {
        return this.jjtGetNumChildren() > 1 ? this.children[1] : null;
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        return null;
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }
}

