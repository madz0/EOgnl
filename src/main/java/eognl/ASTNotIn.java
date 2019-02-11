/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.EOgnlRuntime;
import eognl.Node;
import eognl.NodeType;
import eognl.NodeVisitor;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlOps;
import eognl.OgnlParser;
import eognl.SimpleNode;
import eognl.enhance.UnsupportedCompilationException;

class ASTNotIn
extends SimpleNode
implements NodeType {
    public ASTNotIn(int id) {
        super(id);
    }

    public ASTNotIn(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object v2;
        Object v1 = this.children[0].getValue(context, source);
        return OgnlOps.in(context, v1, v2 = this.children[1].getValue(context, source)) ? Boolean.FALSE : Boolean.TRUE;
    }

    @Override
    public Class<?> getGetterClass() {
        return Boolean.TYPE;
    }

    @Override
    public Class<?> getSetterClass() {
        return null;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        try {
            String result = "(! org.apache.commons.ognl.OgnlOps.in( ($w) ";
            result = String.valueOf(result) + EOgnlRuntime.getChildSource(context, target, this.children[0]) + ", ($w) " + EOgnlRuntime.getChildSource(context, target, this.children[1]);
            result = String.valueOf(result) + ") )";
            context.setCurrentType(Boolean.TYPE);
            return result;
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            throw new UnsupportedCompilationException("evaluation resulted in null expression.");
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }
}

