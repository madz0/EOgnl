/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ExpressionNode;
import eognl.NodeType;
import eognl.OgnlContext;
import eognl.OgnlOps;
import eognl.OgnlParser;
import eognl.enhance.UnsupportedCompilationException;

public abstract class BooleanExpression
extends ExpressionNode
implements NodeType {
    private static final long serialVersionUID = 8630306635724834872L;
    protected Class<?> getterClass;

    public BooleanExpression(int id) {
        super(id);
    }

    public BooleanExpression(OgnlParser p, int id) {
        super(p, id);
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
    public String toGetSourceString(OgnlContext context, Object target) {
        String ret;
        block6 : {
            block5 : {
                Object value = null;
                try {
                  value = this.getValueBody(context, target);
                } catch (OgnlException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
                this.getterClass = value != null && Boolean.class.isAssignableFrom(value.getClass()) ? Boolean.TYPE : (value != null ? value.getClass() : Boolean.TYPE);
                ret = super.toGetSourceString(context, target);
                if (!"(false)".equals(ret)) break block5;
                return "false";
            }
            if (!"(true)".equals(ret)) break block6;
            return "true";
        }
        try {
            return ret;
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            throw new UnsupportedCompilationException("evaluation resulted in null expression.");
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
    }
}

