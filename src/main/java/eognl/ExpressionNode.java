/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ASTChain;
import eognl.ASTMethod;
import eognl.ASTProperty;
import eognl.ASTSequence;
import eognl.Node;
import eognl.NumericExpression;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlParser;
import eognl.OgnlParserTreeConstants;
import eognl.SimpleNode;
import eognl.enhance.ExpressionCompiler;

public abstract class ExpressionNode
extends SimpleNode {
    private static final long serialVersionUID = 9076228016268317598L;

    public ExpressionNode(int i) {
        super(i);
    }

    public ExpressionNode(OgnlParser p, int i) {
        super(p, i);
    }

    @Override
    public boolean isNodeConstant(OgnlContext context) throws OgnlException {
        return false;
    }

    @Override
    public boolean isConstant(OgnlContext context) throws OgnlException {
        boolean result = this.isNodeConstant(context);
        if (this.children != null && this.children.length > 0) {
            result = true;
            for (int i = 0; result && i < this.children.length; ++i) {
                result = this.children[i] instanceof SimpleNode ? ((SimpleNode)this.children[i]).isConstant(context) : false;
            }
        }
        return result;
    }

    public String getExpressionOperator(int index) {
        throw new RuntimeException("unknown operator for " + OgnlParserTreeConstants.jjtNodeName[this.id]);
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        StringBuilder result = new StringBuilder(this.parent == null || NumericExpression.class.isAssignableFrom(this.parent.getClass()) ? "" : "(");
        if (this.children != null && this.children.length > 0) {
            for (int i = 0; i < this.children.length; ++i) {
                if (i > 0) {
                    result.append(" ").append(this.getExpressionOperator(i)).append(" ");
                }
                String value = this.children[i].toGetSourceString(context, target);
                if ((ASTProperty.class.isInstance(this.children[i]) || ASTMethod.class.isInstance(this.children[i]) || ASTSequence.class.isInstance(this.children[i]) || ASTChain.class.isInstance(this.children[i])) && value != null && value.trim().length() > 0) {
                    String cast;
                    String pre = null;
                    if (ASTMethod.class.isInstance(this.children[i])) {
                        pre = (String)context.get("_currentChain");
                    }
                    if (pre == null) {
                        pre = "";
                    }
                    if ((cast = (String)context.remove("_preCast")) == null) {
                        cast = "";
                    }
                    value = String.valueOf(cast) + ExpressionCompiler.getRootExpression(this.children[i], context.getRoot(), context) + pre + value;
                }
                result.append(value);
            }
        }
        if (this.parent != null && !NumericExpression.class.isAssignableFrom(this.parent.getClass())) {
            result.append(")");
        }
        return result.toString();
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        StringBuilder sourceStringBuilder = new StringBuilder(this.parent == null ? "" : "(");
        if (this.children != null && this.children.length > 0) {
            for (int i = 0; i < this.children.length; ++i) {
                if (i > 0) {
                    sourceStringBuilder.append(" ").append(this.getExpressionOperator(i)).append(' ');
                }
                sourceStringBuilder.append(this.children[i].toSetSourceString(context, target));
            }
        }
        if (this.parent != null) {
            sourceStringBuilder.append(")");
        }
        return sourceStringBuilder.toString();
    }
}

