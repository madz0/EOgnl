/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ASTAssign;
import eognl.EOgnlRuntime;
import eognl.Node;
import eognl.NodeType;
import eognl.NodeVisitor;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlParser;
import eognl.SimpleNode;
import eognl.enhance.OrderedReturn;
import eognl.enhance.UnsupportedCompilationException;

public class ASTVarRef
extends SimpleNode
implements NodeType,
OrderedReturn {
    private String name;
    protected Class<?> getterClass;
    protected String core;
    protected String last;

    public ASTVarRef(int id) {
        super(id);
    }

    public ASTVarRef(OgnlParser p, int id) {
        super(p, id);
    }

    void setName(String name) {
        this.name = name;
    }

    String getName() {
        return this.name;
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        return context.get(this.name);
    }

    @Override
    protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
        context.put(this.name, value);
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
    public String getCoreExpression() {
        return this.core;
    }

    @Override
    public String getLastExpression() {
        return this.last;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        Object value = context.get(this.name);
        if (value != null) {
            this.getterClass = value.getClass();
        }
        context.setCurrentType(this.getterClass);
        context.setCurrentAccessor(context.getClass());
        context.setCurrentObject(value);
        if (context.getCurrentObject() == null) {
            throw new UnsupportedCompilationException("Current context object is null, can't compile var reference.");
        }
        String pre = "";
        String post = "";
        if (context.getCurrentType() != null) {
            pre = "((" + EOgnlRuntime.getCompiler(context).getInterfaceClass(context.getCurrentType()).getName() + ")";
            post = ")";
        }
        if (this.parent != null && ASTAssign.class.isInstance(this.parent)) {
            this.core = "$1.put(\"" + this.name + "\",";
            this.last = String.valueOf(pre) + "$1.get(\"" + this.name + "\")" + post;
            return this.core;
        }
        return String.valueOf(pre) + "$1.get(\"" + this.name + "\")" + post;
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        return this.toGetSourceString(context, target);
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }
}

