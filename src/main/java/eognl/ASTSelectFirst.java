/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.EOgnlRuntime;
import eognl.ElementsAccessor;
import eognl.Node;
import eognl.NodeVisitor;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlOps;
import eognl.OgnlParser;
import eognl.SimpleNode;
import eognl.enhance.UnsupportedCompilationException;
import java.util.ArrayList;
import java.util.Enumeration;

class ASTSelectFirst
extends SimpleNode {
    public ASTSelectFirst(int id) {
        super(id);
    }

    public ASTSelectFirst(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Node expr = this.children[0];
        ArrayList answer = new ArrayList();
        ElementsAccessor elementsAccessor = context.getElementsAccessor(EOgnlRuntime.getTargetClass(source));
        Enumeration<?> e = elementsAccessor.getElements(source);
        while (e.hasMoreElements()) {
            Object next = e.nextElement();
            if (!OgnlOps.booleanValue(expr.getValue(context, next))) continue;
            answer.add(next);
            break;
        }
        return answer;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        throw new UnsupportedCompilationException("Eval expressions not supported as native java yet.");
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        throw new UnsupportedCompilationException("Eval expressions not supported as native java yet.");
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }
}

