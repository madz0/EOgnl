/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ASTVarRef;
import eognl.NodeVisitor;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlParser;
import eognl.enhance.UnsupportedCompilationException;

public class ASTThisVarRef
extends ASTVarRef {
    public ASTThisVarRef(int id) {
        super(id);
    }

    public ASTThisVarRef(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        return context.getCurrentObject();
    }

    @Override
    protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
        context.setCurrentObject(value);
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        throw new UnsupportedCompilationException("Unable to compile this references.");
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        throw new UnsupportedCompilationException("Unable to compile this references.");
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }
}

