/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ASTChain;
import eognl.ASTConst;
import eognl.ASTMethod;
import eognl.ASTProperty;
import eognl.ASTSequence;
import eognl.ASTStaticField;
import eognl.ASTStaticMethod;
import eognl.ASTTest;
import eognl.ASTVarRef;
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
import eognl.enhance.ExpressionCompiler;
import java.math.BigDecimal;
import java.math.BigInteger;

class ASTAdd
extends NumericExpression {
    public ASTAdd(int id) {
        super(id);
    }

    public ASTAdd(OgnlParser p, int id) {
        super(p, id);
    }

    @Override
    public void jjtClose() {
        this.flattenTree();
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object result = this.children[0].getValue(context, source);
        for (int i = 1; i < this.children.length; ++i) {
            result = OgnlOps.add(result, this.children[i].getValue(context, source));
        }
        return result;
    }

    @Override
    public String getExpressionOperator(int index) {
        return "+";
    }

    boolean isWider(NodeType type, NodeType lastType) {
        if (lastType == null) {
            return true;
        }
        if (String.class.isAssignableFrom(lastType.getGetterClass())) {
            return false;
        }
        if (String.class.isAssignableFrom(type.getGetterClass())) {
            return true;
        }
        if (this.parent != null && String.class.isAssignableFrom(type.getGetterClass())) {
            return true;
        }
        if (String.class.isAssignableFrom(lastType.getGetterClass()) && Object.class == type.getGetterClass()) {
            return false;
        }
        if (this.parent != null && String.class.isAssignableFrom(lastType.getGetterClass())) {
            return false;
        }
        if (this.parent == null && String.class.isAssignableFrom(lastType.getGetterClass())) {
            return true;
        }
        if (this.parent == null && String.class.isAssignableFrom(type.getGetterClass())) {
            return false;
        }
        if (BigDecimal.class.isAssignableFrom(type.getGetterClass()) || BigInteger.class.isAssignableFrom(type.getGetterClass())) {
            return true;
        }
        if (BigDecimal.class.isAssignableFrom(lastType.getGetterClass()) || BigInteger.class.isAssignableFrom(lastType.getGetterClass())) {
            return false;
        }
        if (Double.class.isAssignableFrom(type.getGetterClass())) {
            return true;
        }
        if (Integer.class.isAssignableFrom(type.getGetterClass()) && Double.class.isAssignableFrom(lastType.getGetterClass())) {
            return false;
        }
        if (Float.class.isAssignableFrom(type.getGetterClass()) && Integer.class.isAssignableFrom(lastType.getGetterClass())) {
            return true;
        }
        return true;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        try {
            String result = "";
            NodeType lastType = null;
            if (this.children != null && this.children.length > 0) {
                Class<?> currType = context.getCurrentType();
                Class<?> currAccessor = context.getCurrentAccessor();
                Object cast = context.get("_preCast");
                for (Node aChildren : this.children) {
                    aChildren.toGetSourceString(context, target);
                    if (!NodeType.class.isInstance(aChildren) || ((NodeType)((Object)aChildren)).getGetterClass() == null || !this.isWider((NodeType)((Object)aChildren), lastType)) continue;
                    lastType = (NodeType)((Object)aChildren);
                }
                context.put("_preCast", cast);
                context.setCurrentType(currType);
                context.setCurrentAccessor(currAccessor);
            }
            context.setCurrentObject(target);
            if (this.children != null && this.children.length > 0) {
                for (int i = 0; i < this.children.length; ++i) {
                    String expr;
                    if (i > 0) {
                        result = String.valueOf(result) + " " + this.getExpressionOperator(i) + " ";
                    }
                    if ((expr = this.children[i].toGetSourceString(context, target)) != null && "null".equals(expr) || !ASTConst.class.isInstance(this.children[i]) && (expr == null || expr.trim().length() <= 0)) {
                        expr = "null";
                    }
                    if (ASTProperty.class.isInstance(this.children[i])) {
                        expr = String.valueOf(ExpressionCompiler.getRootExpression(this.children[i], context.getRoot(), context)) + expr;
                        context.setCurrentAccessor(context.getRoot().getClass());
                    } else if (ASTMethod.class.isInstance(this.children[i])) {
                        String chain = (String)context.get("_currentChain");
                        String rootExpr = ExpressionCompiler.getRootExpression(this.children[i], context.getRoot(), context);
                        if (rootExpr.endsWith(".") && chain != null && chain.startsWith(").")) {
                            chain = chain.substring(1, chain.length());
                        }
                        expr = String.valueOf(rootExpr) + (chain != null ? new StringBuilder(String.valueOf(chain)).append(".").toString() : "") + expr;
                        context.setCurrentAccessor(context.getRoot().getClass());
                    } else if (ExpressionNode.class.isInstance(this.children[i])) {
                        expr = "(" + expr + ")";
                    } else if ((this.parent == null || !ASTChain.class.isInstance(this.parent)) && ASTChain.class.isInstance(this.children[i])) {
                        String rootExpr = ExpressionCompiler.getRootExpression(this.children[i], context.getRoot(), context);
                        if (!ASTProperty.class.isInstance(this.children[i].jjtGetChild(0)) && rootExpr.endsWith(")") && expr.startsWith(")")) {
                            expr = expr.substring(1, expr.length());
                        }
                        expr = String.valueOf(rootExpr) + expr;
                        context.setCurrentAccessor(context.getRoot().getClass());
                        String cast = (String)context.remove("_preCast");
                        if (cast == null) {
                            cast = "";
                        }
                        expr = String.valueOf(cast) + expr;
                    }
                    if (context.getCurrentType() != null && context.getCurrentType() == Character.class && ASTConst.class.isInstance(this.children[i])) {
                        expr = expr.replaceAll("'", "\"");
                        context.setCurrentType(String.class);
                    } else if (!(ASTVarRef.class.isAssignableFrom(this.children[i].getClass()) || ASTProperty.class.isInstance(this.children[i]) || ASTMethod.class.isInstance(this.children[i]) || ASTSequence.class.isInstance(this.children[i]) || ASTChain.class.isInstance(this.children[i]) || NumericExpression.class.isAssignableFrom(this.children[i].getClass()) || ASTStaticField.class.isInstance(this.children[i]) || ASTStaticMethod.class.isInstance(this.children[i]) || ASTTest.class.isInstance(this.children[i]) || lastType == null || !String.class.isAssignableFrom(lastType.getGetterClass()))) {
                        expr = expr.replaceAll("&quot;", "\"");
                        expr = expr.replaceAll("\"", "'");
                        expr = String.format("\"%s\"", expr);
                    }
                    result = String.valueOf(result) + expr;
                    if (!(lastType != null && String.class.isAssignableFrom(lastType.getGetterClass()) || ASTConst.class.isAssignableFrom(this.children[i].getClass()) || NumericExpression.class.isAssignableFrom(this.children[i].getClass()) || context.getCurrentType() == null || !Number.class.isAssignableFrom(context.getCurrentType()) || ASTMethod.class.isInstance(this.children[i]))) {
                        if (ASTVarRef.class.isInstance(this.children[i]) || ASTProperty.class.isInstance(this.children[i]) || ASTChain.class.isInstance(this.children[i])) {
                            result = String.valueOf(result) + ".";
                        }
                        result = String.valueOf(result) + EOgnlRuntime.getNumericValueGetter(context.getCurrentType());
                        context.setCurrentType(EOgnlRuntime.getPrimitiveWrapperClass(context.getCurrentType()));
                    }
                    if (lastType == null) continue;
                    context.setCurrentAccessor(lastType.getGetterClass());
                }
            }
            if (this.parent == null || ASTSequence.class.isAssignableFrom(this.parent.getClass())) {
                if (this.getterClass != null && String.class.isAssignableFrom(this.getterClass)) {
                    this.getterClass = Object.class;
                }
            } else {
                context.setCurrentType(this.getterClass);
            }
            try {
                Object contextObj = this.getValueBody(context, target);
                context.setCurrentObject(contextObj);
            }
            catch (Throwable t) {
                throw OgnlOps.castToRuntime(t);
            }
            return result;
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

