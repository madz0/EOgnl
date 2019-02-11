/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ASTVarRef;
import eognl.Node;
import eognl.NodeVisitor;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlParser;
import eognl.enhance.ExpressionCompiler;

public class ASTRootVarRef
extends ASTVarRef {
    public ASTRootVarRef(int id) {
        super(id);
    }

    public ASTRootVarRef(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        return context.getRoot();
    }

    @Override
    protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
        context.setRoot(value);
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        if (target != null) {
            this.getterClass = target.getClass();
        }
        if (this.getterClass != null) {
            context.setCurrentType(this.getterClass);
        }
        if (this.parent == null || this.getterClass != null && this.getterClass.isArray()) {
            return "";
        }
        return ExpressionCompiler.getRootExpression(this, target, context);
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        if (this.parent == null || this.getterClass != null && this.getterClass.isArray()) {
            return "";
        }
        return "$3";
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }
}

