/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.BooleanExpression;
import eognl.EOgnlRuntime;
import eognl.Node;
import eognl.NodeVisitor;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlOps;
import eognl.OgnlParser;
import eognl.enhance.ExpressionCompiler;
import eognl.enhance.UnsupportedCompilationException;

public class ASTAnd
extends BooleanExpression {
    private static final long serialVersionUID = -4585941425250141812L;

    public ASTAnd(int id) {
        super(id);
    }

    public ASTAnd(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    public void jjtClose() {
        this.flattenTree();
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object result = null;
        int last = this.children.length - 1;
        for (int i = 0; i <= last; ++i) {
            result = this.children[i].getValue(context, source);
            if (i != last && !OgnlOps.booleanValue(result)) break;
        }
        return result;
    }

    @Override
    protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
        int last = this.children.length - 1;
        for (int i = 0; i < last; ++i) {
            Object v = this.children[i].getValue(context, target);
            if (OgnlOps.booleanValue(v)) continue;
            return;
        }
        this.children[last].setValue(context, target, value);
    }

    @Override
    public String getExpressionOperator(int index) {
        return "&&";
    }

    @Override
    public Class<?> getGetterClass() {
        return null;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        if (this.children.length != 2) {
            throw new UnsupportedCompilationException("Can only compile boolean expressions with two children.");
        }
        String result = "";
        try {
            String second;
            String first = EOgnlRuntime.getChildSource(context, target, this.children[0]);
            if (!OgnlOps.booleanValue(context.getCurrentObject())) {
                throw new UnsupportedCompilationException("And expression can't be compiled until all conditions are true.");
            }
            if (!EOgnlRuntime.isBoolean(first) && !context.getCurrentType().isPrimitive()) {
                first = EOgnlRuntime.getCompiler(context).createLocalReference(context, first, context.getCurrentType());
            }
            if (!EOgnlRuntime.isBoolean(second = EOgnlRuntime.getChildSource(context, target, this.children[1])) && !context.getCurrentType().isPrimitive()) {
                second = EOgnlRuntime.getCompiler(context).createLocalReference(context, second, context.getCurrentType());
            }
            result = String.valueOf(result) + String.format("(org.apache.commons.ognl.OgnlOps.booleanValue(%s) ?  ($w) (%s) :  ($w) (%s))", first, second, first);
            context.setCurrentObject(target);
            context.setCurrentType(Object.class);
        }
        catch (NullPointerException e) {
            throw new UnsupportedCompilationException("evaluation resulted in null expression.");
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        return result;
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        if (this.children.length != 2) {
            throw new UnsupportedCompilationException("Can only compile boolean expressions with two children.");
        }
        String pre = (String)context.get("_currentChain");
        if (pre == null) {
            pre = "";
        }
        String result = "";
        try {
            if (!OgnlOps.booleanValue(this.children[0].getValue(context, target))) {
                throw new UnsupportedCompilationException("And expression can't be compiled until all conditions are true.");
            }
            String first = String.valueOf(ExpressionCompiler.getRootExpression(this.children[0], context.getRoot(), context)) + pre + this.children[0].toGetSourceString(context, target);
            this.children[1].getValue(context, target);
            String second = String.valueOf(ExpressionCompiler.getRootExpression(this.children[1], context.getRoot(), context)) + pre + this.children[1].toSetSourceString(context, target);
            result = !EOgnlRuntime.isBoolean(first) ? String.valueOf(result) + "if(org.apache.commons.ognl.OgnlOps.booleanValue(" + first + ")){" : String.valueOf(result) + "if(" + first + "){";
            result = String.valueOf(result) + second;
            result = String.valueOf(result) + "; } ";
            context.setCurrentObject(target);
            context.setCurrentType(Object.class);
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        return result;
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }
}

