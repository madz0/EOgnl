/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ASTProperty;
import eognl.EOgnlRuntime;
import eognl.ExpressionNode;
import eognl.Node;
import eognl.NodeType;
import eognl.NodeVisitor;
import eognl.NumericExpression;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlOps;
import eognl.OgnlParser;
import eognl.SimpleNode;
import eognl.enhance.UnsupportedCompilationException;

public class ASTConst
extends SimpleNode
implements NodeType {
    private Object value;
    private Class<?> getterClass;

    public ASTConst(int id) {
        super(id);
    }

    public ASTConst(OgnlParser p, int id) {
        super(p, id);
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        return this.value;
    }

    @Override
    public boolean isNodeConstant(OgnlContext context) throws OgnlException {
        return true;
    }

    @Override
    public Class<?> getGetterClass() {
        if (this.getterClass == null) {
            return null;
        }
        return this.getterClass;
    }

    @Override
    public Class<?> getSetterClass() {
        return null;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        if (this.value == null && this.parent != null && ExpressionNode.class.isInstance(this.parent)) {
            context.setCurrentType(null);
            return "null";
        }
        if (this.value == null) {
            context.setCurrentType(null);
            return "";
        }
        this.getterClass = this.value.getClass();
        Object retval = this.value;
        if (this.parent != null && ASTProperty.class.isInstance(this.parent)) {
            context.setCurrentObject(this.value);
            return this.value.toString();
        }
        if (this.value != null && Number.class.isAssignableFrom(this.value.getClass())) {
            context.setCurrentType(EOgnlRuntime.getPrimitiveWrapperClass(this.value.getClass()));
            context.setCurrentObject(this.value);
            return this.value.toString();
        }
        if ((this.parent == null || this.value == null || !NumericExpression.class.isAssignableFrom(this.parent.getClass())) && String.class.isAssignableFrom(this.value.getClass())) {
            context.setCurrentType(String.class);
            retval = String.valueOf('\"') + OgnlOps.getEscapeString(this.value.toString()) + '\"';
            context.setCurrentObject(retval.toString());
            return retval.toString();
        }
        if (Character.class.isInstance(this.value)) {
            Character val = (Character)this.value;
            context.setCurrentType(Character.class);
            retval = Character.isLetterOrDigit(val.charValue()) ? "'" + ((Character)this.value).charValue() + "'" : "'" + OgnlOps.getEscapedChar(((Character)this.value).charValue()) + "'";
            context.setCurrentObject(retval);
            return retval.toString();
        }
        if (Boolean.class.isAssignableFrom(this.value.getClass())) {
            this.getterClass = Boolean.TYPE;
            context.setCurrentType(Boolean.TYPE);
            context.setCurrentObject(this.value);
            return this.value.toString();
        }
        return this.value.toString();
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        if (this.parent == null) {
            throw new UnsupportedCompilationException("Can't modify constant values.");
        }
        return this.toGetSourceString(context, target);
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }
}

