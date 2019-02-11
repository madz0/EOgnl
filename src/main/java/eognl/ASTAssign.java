/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.enhance.OrderedReturn;
import eognl.enhance.UnsupportedCompilationException;

class ASTAssign
extends SimpleNode {
    public ASTAssign(int id) {
        super(id);
    }

    public ASTAssign(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object result = this.children[1].getValue(context, source);
        this.children[0].setValue(context, source, result);
        return result;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        String result = "";
        String first = this.children[0].toGetSourceString(context, target);
        String second = "";
        if (ASTProperty.class.isInstance(this.children[1])) {
            second = String.valueOf(second) + "((" + EOgnlRuntime.getCompiler(context).getClassName(target.getClass()) + ")$2).";
        }
        second = String.valueOf(second) + this.children[1].toGetSourceString(context, target);
        if (ASTSequence.class.isAssignableFrom(this.children[1].getClass())) {
            ASTSequence seq = (ASTSequence)this.children[1];
            context.setCurrentType(Object.class);
            String core = seq.getCoreExpression();
            if (core.endsWith(";")) {
                core = core.substring(0, core.lastIndexOf(";"));
            }
            second = EOgnlRuntime.getCompiler(context).createLocalReference(context, "org.apache.commons.ognl.OgnlOps.returnValue(($w)" + core + ", ($w) " + seq.getLastExpression() + ")", Object.class);
        }
        if (NodeType.class.isInstance(this.children[1]) && !ASTProperty.class.isInstance(this.children[1]) && ((NodeType)((Object)this.children[1])).getGetterClass() != null && !OrderedReturn.class.isInstance(this.children[1])) {
            second = "new " + ((NodeType)((Object)this.children[1])).getGetterClass().getName() + "(" + second + ")";
        }
        if (OrderedReturn.class.isAssignableFrom(this.children[0].getClass()) && ((OrderedReturn)((Object)this.children[0])).getCoreExpression() != null) {
            context.setCurrentType(Object.class);
            result = String.valueOf(first) + second + ")";
            result = EOgnlRuntime.getCompiler(context).createLocalReference(context, "org.apache.commons.ognl.OgnlOps.returnValue(($w)" + result + ", ($w)" + ((OrderedReturn)((Object)this.children[0])).getLastExpression() + ")", Object.class);
        }
        return result;
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        String value;
        String result = "";
        result = String.valueOf(result) + this.children[0].toSetSourceString(context, target);
        if (ASTProperty.class.isInstance(this.children[1])) {
            result = String.valueOf(result) + "((" + EOgnlRuntime.getCompiler(context).getClassName(target.getClass()) + ")$2).";
        }
        if ((value = this.children[1].toSetSourceString(context, target)) == null) {
            throw new UnsupportedCompilationException("Value for assignment is null, can't enhance statement to bytecode.");
        }
        if (ASTSequence.class.isAssignableFrom(this.children[1].getClass())) {
            ASTSequence seq = (ASTSequence)this.children[1];
            result = String.valueOf(seq.getCoreExpression()) + result;
            value = seq.getLastExpression();
        }
        if (NodeType.class.isInstance(this.children[1]) && !ASTProperty.class.isInstance(this.children[1]) && ((NodeType)((Object)this.children[1])).getGetterClass() != null) {
            value = "new " + ((NodeType)((Object)this.children[1])).getGetterClass().getName() + "(" + value + ")";
        }
        return String.valueOf(result) + value + ")";
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }
}

