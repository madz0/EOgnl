/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.EOgnl;
import eognl.Node;
import eognl.NodeVisitor;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlParser;
import eognl.SimpleNode;
import eognl.enhance.UnsupportedCompilationException;

class ASTEval
extends SimpleNode {
    public ASTEval(int id) {
        super(id);
    }

    public ASTEval(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object result;
        Object expr = this.children[0].getValue(context, source);
        Object previousRoot = context.getRoot();
        source = this.children[1].getValue(context, source);
        Node node = expr instanceof Node ? (Node)expr : (Node)EOgnl.parseExpression(expr.toString());
        try {
            context.setRoot(source);
            result = node.getValue(context, source);
        }
        finally {
            context.setRoot(previousRoot);
        }
        return result;
    }

    @Override
    protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
        Object expr = this.children[0].getValue(context, target);
        Object previousRoot = context.getRoot();
        target = this.children[1].getValue(context, target);
        Node node = expr instanceof Node ? (Node)expr : (Node)EOgnl.parseExpression(expr.toString());
        try {
            context.setRoot(target);
            node.setValue(context, target, value);
        }
        finally {
            context.setRoot(previousRoot);
        }
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        throw new UnsupportedCompilationException("Eval expressions not supported as native java yet.");
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        throw new UnsupportedCompilationException("Map expressions not supported as native java yet.");
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }

    @Override
    public boolean isEvalChain(OgnlContext context) throws OgnlException {
        return true;
    }
}

