/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ASTOr;
import eognl.ASTProperty;
import eognl.Node;
import eognl.NodeType;
import eognl.NodeVisitor;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlParser;
import eognl.SimpleNode;
import eognl.enhance.ExpressionCompiler;
import eognl.enhance.OrderedReturn;

public class ASTSequence
extends SimpleNode
implements NodeType,
OrderedReturn {
    private Class<?> getterClass;
    private String lastExpression;
    private String coreExpression;

    public ASTSequence(int id) {
        super(id);
    }

    public ASTSequence(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    public void jjtClose() {
        this.flattenTree();
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object result = null;
        for (int i = 0; i < this.children.length; ++i) {
            result = this.children[i].getValue(context, source);
        }
        return result;
    }

    @Override
    protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
        int last = this.children.length - 1;
        for (int i = 0; i < last; ++i) {
            this.children[i].getValue(context, target);
        }
        this.children[last].setValue(context, target, value);
    }

    @Override
    public Class<?> getGetterClass() {
        return this.getterClass;
    }

    @Override
    public Class<?> getSetterClass() {
        return null;
    }

    @Override
    public String getLastExpression() {
        return this.lastExpression;
    }

    @Override
    public String getCoreExpression() {
        return this.coreExpression;
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        return "";
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        String result = "";
        NodeType lastType = null;
        for (int i = 0; i < this.children.length; ++i) {
            String seqValue = this.children[i].toGetSourceString(context, target);
            if (i + 1 < this.children.length && ASTOr.class.isInstance(this.children[i])) {
                seqValue = "(" + seqValue + ")";
            }
            if (i > 0 && ASTProperty.class.isInstance(this.children[i]) && seqValue != null && seqValue.trim().length() > 0) {
                String pre = (String)context.get("_currentChain");
                if (pre == null) {
                    pre = "";
                }
                seqValue = String.valueOf(ExpressionCompiler.getRootExpression(this.children[i], context.getRoot(), context)) + pre + seqValue;
                context.setCurrentAccessor(context.getRoot().getClass());
            }
            if (i + 1 >= this.children.length) {
                this.coreExpression = result;
                this.lastExpression = seqValue;
            }
            if (seqValue != null && seqValue.trim().length() > 0 && i + 1 < this.children.length) {
                result = String.valueOf(result) + seqValue + ";";
            } else if (seqValue != null && seqValue.trim().length() > 0) {
                result = String.valueOf(result) + seqValue;
            }
            if (!NodeType.class.isInstance(this.children[i]) || ((NodeType)((Object)this.children[i])).getGetterClass() == null) continue;
            lastType = (NodeType)((Object)this.children[i]);
        }
        if (lastType != null) {
            this.getterClass = lastType.getGetterClass();
        }
        return result;
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }
}

