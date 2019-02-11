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
import eognl.OgnlParser;
import eognl.SimpleNode;
import eognl.enhance.UnsupportedCompilationException;
import java.util.ArrayList;
import java.util.Enumeration;

class ASTProject
extends SimpleNode {
    public ASTProject(int id) {
        super(id);
    }

    public ASTProject(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Node expr = this.children[0];
        ArrayList<Object> answer = new ArrayList<Object>();
        ElementsAccessor elementsAccessor = context.getElementsAccessor(EOgnlRuntime.getTargetClass(source));
        Enumeration<?> e = elementsAccessor.getElements(source);
        while (e.hasMoreElements()) {
            answer.add(expr.getValue(context, e.nextElement()));
        }
        return answer;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        throw new UnsupportedCompilationException("Projection expressions not supported as native java yet.");
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        throw new UnsupportedCompilationException("Projection expressions not supported as native java yet.");
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }
}

