/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ASTAdd;
import eognl.ASTAnd;
import eognl.ASTAssign;
import eognl.ASTBitAnd;
import eognl.ASTBitNegate;
import eognl.ASTBitOr;
import eognl.ASTChain;
import eognl.ASTConst;
import eognl.ASTCtor;
import eognl.ASTDivide;
import eognl.ASTEq;
import eognl.ASTEval;
import eognl.ASTGreater;
import eognl.ASTGreaterEq;
import eognl.ASTIn;
import eognl.ASTInstanceof;
import eognl.ASTKeyValue;
import eognl.ASTLess;
import eognl.ASTLessEq;
import eognl.ASTList;
import eognl.ASTMap;
import eognl.ASTMethod;
import eognl.ASTMultiply;
import eognl.ASTNegate;
import eognl.ASTNot;
import eognl.ASTNotEq;
import eognl.ASTNotIn;
import eognl.ASTOr;
import eognl.ASTProject;
import eognl.ASTProperty;
import eognl.ASTRemainder;
import eognl.ASTRootVarRef;
import eognl.ASTSelect;
import eognl.ASTSelectFirst;
import eognl.ASTSelectLast;
import eognl.ASTSequence;
import eognl.ASTShiftLeft;
import eognl.ASTShiftRight;
import eognl.ASTStaticField;
import eognl.ASTStaticMethod;
import eognl.ASTSubtract;
import eognl.ASTTest;
import eognl.ASTThisVarRef;
import eognl.ASTUnsignedShiftRight;
import eognl.ASTVarRef;
import eognl.ASTXor;
import eognl.ExpressionNode;
import eognl.Node;
import eognl.NodeVisitor;
import eognl.OgnlException;
import eognl.OgnlOps;
import eognl.SimpleNode;
import java.math.BigDecimal;
import java.math.BigInteger;

public class ToStringVisitor
implements NodeVisitor<StringBuilder, StringBuilder> {
    static final ToStringVisitor INSTANCE = new ToStringVisitor();

    @Override
    public StringBuilder visit(ASTSequence node, StringBuilder data) {
        return this.commaSeparatedChildren(node, data);
    }

    private StringBuilder commaSeparatedChildren(SimpleNode node, StringBuilder data) {
        if (node.children != null) {
            for (int i = 0; i < node.children.length; ++i) {
                if (i > 0) {
                    data.append(", ");
                }
                this.recurse(node.children[i], data);
            }
        }
        return data;
    }

    @Override
    public StringBuilder visit(ASTAssign node, StringBuilder data) {
        return this.concatInfix(node, " = ", data);
    }

    @Override
    public StringBuilder visit(ASTTest node, StringBuilder data) {
        return this.visitExpressionNode(node, data);
    }

    private StringBuilder visitExpressionNode(ExpressionNode node, StringBuilder data) {
        if (node.parent != null) {
            data.append("(");
        }
        if (node.children != null && node.children.length > 0) {
            for (int i = 0; i < node.children.length; ++i) {
                if (i > 0) {
                    data.append(" ").append(node.getExpressionOperator(i)).append(" ");
                }
                this.recurse(node.children[i], data);
            }
        }
        if (node.parent != null) {
            data.append(')');
        }
        return data;
    }

    @Override
    public StringBuilder visit(ASTOr node, StringBuilder data) {
        return this.visitExpressionNode(node, data);
    }

    @Override
    public StringBuilder visit(ASTAnd node, StringBuilder data) {
        return this.visitExpressionNode(node, data);
    }

    @Override
    public StringBuilder visit(ASTBitOr node, StringBuilder data) {
        return this.visitExpressionNode(node, data);
    }

    @Override
    public StringBuilder visit(ASTXor node, StringBuilder data) {
        return this.visitExpressionNode(node, data);
    }

    @Override
    public StringBuilder visit(ASTBitAnd node, StringBuilder data) {
        return this.visitExpressionNode(node, data);
    }

    @Override
    public StringBuilder visit(ASTEq node, StringBuilder data) {
        return this.visitExpressionNode(node, data);
    }

    @Override
    public StringBuilder visit(ASTNotEq node, StringBuilder data) {
        return this.visitExpressionNode(node, data);
    }

    @Override
    public StringBuilder visit(ASTLess node, StringBuilder data) {
        return this.visitExpressionNode(node, data);
    }

    @Override
    public StringBuilder visit(ASTGreater node, StringBuilder data) {
        return this.visitExpressionNode(node, data);
    }

    @Override
    public StringBuilder visit(ASTLessEq node, StringBuilder data) {
        return this.visitExpressionNode(node, data);
    }

    @Override
    public StringBuilder visit(ASTGreaterEq node, StringBuilder data) {
        return this.visitExpressionNode(node, data);
    }

    @Override
    public StringBuilder visit(ASTIn node, StringBuilder data) {
        String infix = " in ";
        return this.concatInfix(node, " in ", data);
    }

    private StringBuilder concatInfix(SimpleNode node, String infix, StringBuilder data) {
        return this.concatInfix(node.children[0], infix, node.children[1], data);
    }

    private StringBuilder concatInfix(Node left, String infix, Node right, StringBuilder data) {
        this.recurse(left, data).append(infix);
        return this.recurse(right, data);
    }

    @Override
    public StringBuilder visit(ASTNotIn node, StringBuilder data) {
        return this.concatInfix(node, " not in ", data);
    }

    @Override
    public StringBuilder visit(ASTShiftLeft node, StringBuilder data) {
        return this.visitExpressionNode(node, data);
    }

    @Override
    public StringBuilder visit(ASTShiftRight node, StringBuilder data) {
        return this.visitExpressionNode(node, data);
    }

    @Override
    public StringBuilder visit(ASTUnsignedShiftRight node, StringBuilder data) {
        return this.visitExpressionNode(node, data);
    }

    @Override
    public StringBuilder visit(ASTAdd node, StringBuilder data) {
        return this.visitExpressionNode(node, data);
    }

    @Override
    public StringBuilder visit(ASTSubtract node, StringBuilder data) {
        return this.visitExpressionNode(node, data);
    }

    @Override
    public StringBuilder visit(ASTMultiply node, StringBuilder data) {
        return this.visitExpressionNode(node, data);
    }

    @Override
    public StringBuilder visit(ASTDivide node, StringBuilder data) {
        return this.visitExpressionNode(node, data);
    }

    @Override
    public StringBuilder visit(ASTRemainder node, StringBuilder data) {
        return this.visitExpressionNode(node, data);
    }

    @Override
    public StringBuilder visit(ASTNegate node, StringBuilder data) {
        return this.appendPrefixed("-", node, data);
    }

    @Override
    public StringBuilder visit(ASTBitNegate node, StringBuilder data) {
        return this.appendPrefixed("~", node, data);
    }

    private StringBuilder appendPrefixed(String prefix, SimpleNode node, StringBuilder data) {
        data.append(prefix);
        return this.recurse(node.children[0], data);
    }

    @Override
    public StringBuilder visit(ASTNot node, StringBuilder data) {
        return this.visitExpressionNode(node, data);
    }

    @Override
    public StringBuilder visit(ASTInstanceof node, StringBuilder data) {
        return this.recurse(node.children[0], data).append(" instanceof ").append(node.getTargetType());
    }

    @Override
    public StringBuilder visit(ASTChain node, StringBuilder data) {
        if (node.children != null && node.children.length > 0) {
            for (int i = 0; i < node.children.length; ++i) {
                if (i > 0 && !(node.children[i] instanceof ASTProperty) || !((ASTProperty)node.children[i]).isIndexedAccess()) {
                    data.append(".");
                }
                this.recurse(node.children[i], data);
            }
        }
        return data;
    }

    @Override
    public StringBuilder visit(ASTEval node, StringBuilder data) {
        data.append("(");
        this.concatInfix(node, ")(", data);
        return data.append(")");
    }

    @Override
    public StringBuilder visit(ASTConst node, StringBuilder data) {
        Object value = node.getValue();
        if (value == null) {
            data.append("null");
        } else if (value instanceof String) {
            data.append('\"').append(OgnlOps.getEscapeString(value.toString())).append('\"');
        } else if (value instanceof Character) {
            data.append('\'').append(OgnlOps.getEscapedChar(((Character)value).charValue())).append('\'');
        } else if (value instanceof Node) {
            data.append(":[ ");
            this.recurse((Node)value, data);
            data.append(" ]");
        } else {
            data.append(value);
            if (value instanceof Long) {
                data.append('L');
            } else if (value instanceof BigDecimal) {
                data.append('B');
            } else if (value instanceof BigInteger) {
                data.append('H');
            }
        }
        return data;
    }

    @Override
    public StringBuilder visit(ASTThisVarRef node, StringBuilder data) {
        return data.append("#this");
    }

    @Override
    public StringBuilder visit(ASTRootVarRef node, StringBuilder data) {
        return data.append("#root");
    }

    @Override
    public StringBuilder visit(ASTVarRef node, StringBuilder data) {
        return data.append("#").append(node.getName());
    }

    @Override
    public StringBuilder visit(ASTList node, StringBuilder data) {
        return this.wrappedCommaSeparatedChildren("{ ", node, " }", data);
    }

    @Override
    public StringBuilder visit(ASTMap node, StringBuilder data) {
        data.append("#");
        if (node.getClassName() != null) {
            data.append("@").append(node.getClassName()).append("@");
        }
        data.append("{ ");
        for (int i = 0; i < node.jjtGetNumChildren(); ++i) {
            ASTKeyValue kv = (ASTKeyValue)node.children[i];
            if (i > 0) {
                data.append(", ");
            }
            this.concatInfix(kv.getKey(), " : ", kv.getValue(), data);
        }
        return data.append(" }");
    }

    @Override
    public StringBuilder visit(ASTKeyValue node, StringBuilder data) {
        return this.concatInfix(node.getKey(), " -> ", node.getValue(), data);
    }

    @Override
    public StringBuilder visit(ASTStaticField node, StringBuilder data) {
        return data.append("@").append(node.getClassName()).append("@").append(node.getFieldName());
    }

    @Override
    public StringBuilder visit(ASTCtor node, StringBuilder data) {
        data.append("new ").append(node.getClassName());
        if (node.isArray()) {
            if (node.children[0] instanceof ASTConst) {
                this.indexedChild(node, data);
            } else {
                this.appendPrefixed("[] ", node, data);
            }
        } else {
            this.wrappedCommaSeparatedChildren("(", node, ")", data);
        }
        return data;
    }

    private StringBuilder wrappedCommaSeparatedChildren(String prefix, SimpleNode node, String suffix, StringBuilder data) {
        data.append(prefix);
        return this.commaSeparatedChildren(node, data).append(suffix);
    }

    @Override
    public StringBuilder visit(ASTProperty node, StringBuilder data) {
        if (node.isIndexedAccess()) {
            this.indexedChild(node, data);
        } else {
            data.append(((ASTConst)node.children[0]).getValue());
        }
        return data;
    }

    private StringBuilder indexedChild(SimpleNode node, StringBuilder data) {
        return this.surroundedNode("[", node.children[0], "]", data);
    }

    @Override
    public StringBuilder visit(ASTStaticMethod node, StringBuilder data) {
        data.append("@").append(node.getClassName()).append("@").append(node.getMethodName());
        return this.wrappedCommaSeparatedChildren("(", node, ")", data);
    }

    @Override
    public StringBuilder visit(ASTMethod node, StringBuilder data) {
        data.append(node.getMethodName());
        return this.wrappedCommaSeparatedChildren("(", node, ")", data);
    }

    @Override
    public StringBuilder visit(ASTProject node, StringBuilder data) {
        return this.surroundedNode("{ ", node.children[0], " }", data);
    }

    private StringBuilder surroundedNode(String open, Node inner, String close, StringBuilder data) {
        data.append(open);
        return this.recurse(inner, data).append(close);
    }

    @Override
    public StringBuilder visit(ASTSelect node, StringBuilder data) {
        return this.surroundedNode("{? ", node.children[0], " }", data);
    }

    @Override
    public StringBuilder visit(ASTSelectFirst node, StringBuilder data) {
        return this.surroundedNode("{^ ", node.children[0], " }", data);
    }

    @Override
    public StringBuilder visit(ASTSelectLast node, StringBuilder data) {
        return this.surroundedNode("{$ ", node.children[0], " }", data);
    }

    private StringBuilder recurse(Node child, StringBuilder data) {
        try {
            return child == null ? data.append("null") : child.accept(this, data);
        }
        catch (OgnlException e) {
            throw new RuntimeException(e);
        }
    }
}

