/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.Evaluation;
import eognl.InappropriateExpressionException;
import eognl.Node;
import eognl.NodeVisitor;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlParser;
import eognl.OgnlParserTreeConstants;
import eognl.ToStringVisitor;
import eognl.enhance.ExpressionAccessor;
import java.io.PrintWriter;
import java.io.Serializable;

public abstract class SimpleNode
implements Node,
Serializable {
    private static final long serialVersionUID = 8305393337889433901L;
    protected Node parent;
    protected Node[] children;
    protected int id;
    protected OgnlParser parser;
    private boolean constantValueCalculated;
    private volatile boolean hasConstantValue;
    private Object constantValue;
    private ExpressionAccessor accessor;

    public SimpleNode(int i) {
        this.id = i;
    }

    public SimpleNode(OgnlParser p, int i) {
        this(i);
        this.parser = p;
    }

    @Override
    public void jjtOpen() {
    }

    @Override
    public void jjtClose() {
    }

    @Override
    public void jjtSetParent(Node n) {
        this.parent = n;
    }

    @Override
    public Node jjtGetParent() {
        return this.parent;
    }

    @Override
    public void jjtAddChild(Node n, int i) {
        if (this.children == null) {
            this.children = new Node[i + 1];
        } else if (i >= this.children.length) {
            Node[] c = new Node[i + 1];
            System.arraycopy(this.children, 0, c, 0, this.children.length);
            this.children = c;
        }
        this.children[i] = n;
    }

    @Override
    public Node jjtGetChild(int i) {
        return this.children[i];
    }

    @Override
    public int jjtGetNumChildren() {
        return this.children == null ? 0 : this.children.length;
    }

    public String toString() {
        StringBuilder data = new StringBuilder();
        try {
            this.accept(ToStringVisitor.INSTANCE, data);
        }
        catch (OgnlException ognlException) {
            // empty catch block
        }
        return data.toString();
    }

    public String toString(String prefix) {
        return String.valueOf(prefix) + OgnlParserTreeConstants.jjtNodeName[this.id] + " " + this.toString();
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        return this.toString();
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        return this.toString();
    }

    public void dump(PrintWriter writer, String prefix) {
        writer.println(this.toString(prefix));
        if (this.children != null) {
            for (int i = 0; i < this.children.length; ++i) {
                SimpleNode n = (SimpleNode)this.children[i];
                if (n == null) continue;
                n.dump(writer, String.valueOf(prefix) + "  ");
            }
        }
    }

    public int getIndexInParent() {
        int result = -1;
        if (this.parent != null) {
            int icount = this.parent.jjtGetNumChildren();
            for (int i = 0; i < icount; ++i) {
                if (this.parent.jjtGetChild(i) != this) continue;
                result = i;
                break;
            }
        }
        return result;
    }

    public Node getNextSibling() {
        int icount;
        Node result = null;
        int i = this.getIndexInParent();
        if (i >= 0 && i < (icount = this.parent.jjtGetNumChildren())) {
            result = this.parent.jjtGetChild(i + 1);
        }
        return result;
    }

    protected Object evaluateGetValueBody(OgnlContext context, Object source) throws OgnlException {
        context.setCurrentObject(source);
        context.setCurrentNode(this);
        if (!this.constantValueCalculated) {
            this.constantValueCalculated = true;
            boolean constant = this.isConstant(context);
            if (constant) {
                this.constantValue = this.getValueBody(context, source);
            }
            this.hasConstantValue = constant;
        }
        return this.hasConstantValue ? this.constantValue : this.getValueBody(context, source);
    }

    protected void evaluateSetValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
        context.setCurrentObject(target);
        context.setCurrentNode(this);
        this.setValueBody(context, target, value);
    }

    @Override
    public final Object getValue(OgnlContext context, Object source) throws OgnlException {
        Object result;
        block8 : {
            result = null;
            if (context.getTraceEvaluations()) {
                Exception evalException = null;
                Evaluation evaluation = new Evaluation(this, source);
                context.pushEvaluation(evaluation);
                try {
                    try {
                        result = this.evaluateGetValueBody(context, source);
                        break block8;
                    }
                    catch (OgnlException ex) {
                        evalException = ex;
                        throw ex;
                    }
                    catch (RuntimeException ex) {
                        evalException = ex;
                        throw ex;
                    }
                }
                finally {
                    Evaluation eval = context.popEvaluation();
                    eval.setResult(result);
                    if (evalException != null) {
                        eval.setException(evalException);
                    }
                }
            }
            result = this.evaluateGetValueBody(context, source);
        }
        return result;
    }

    protected abstract Object getValueBody(OgnlContext var1, Object var2) throws OgnlException;

    @Override
    public final void setValue(OgnlContext context, Object target, Object value) throws OgnlException {
        block8 : {
            if (context.getTraceEvaluations()) {
                Exception evalException = null;
                Evaluation evaluation = new Evaluation(this, target, true);
                context.pushEvaluation(evaluation);
                try {
                    try {
                        this.evaluateSetValueBody(context, target, value);
                        break block8;
                    }
                    catch (OgnlException ex) {
                        evalException = ex;
                        ex.setEvaluation(evaluation);
                        throw ex;
                    }
                    catch (RuntimeException ex) {
                        evalException = ex;
                        throw ex;
                    }
                }
                finally {
                    Evaluation eval = context.popEvaluation();
                    if (evalException != null) {
                        eval.setException(evalException);
                    }
                }
            }
            this.evaluateSetValueBody(context, target, value);
        }
    }

    protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
        throw new InappropriateExpressionException(this);
    }

    public boolean isNodeConstant(OgnlContext context) throws OgnlException {
        return false;
    }

    public boolean isConstant(OgnlContext context) throws OgnlException {
        return this.isNodeConstant(context);
    }

    public boolean isNodeSimpleProperty(OgnlContext context) throws OgnlException {
        return false;
    }

    public boolean isSimpleProperty(OgnlContext context) throws OgnlException {
        return this.isNodeSimpleProperty(context);
    }

    public boolean isSimpleNavigationChain(OgnlContext context) throws OgnlException {
        return this.isSimpleProperty(context);
    }

    public boolean isEvalChain(OgnlContext context) throws OgnlException {
        if (this.children == null) {
            return false;
        }
        for (Node child : this.children) {
            if (!(child instanceof SimpleNode) || !((SimpleNode)child).isEvalChain(context)) continue;
            return true;
        }
        return false;
    }

    protected boolean lastChild(OgnlContext context) {
        return this.parent == null || context.get("_lastChild") != null;
    }

    protected void flattenTree() {
        boolean shouldFlatten = false;
        int newSize = 0;
        for (Node aChildren : this.children) {
            if (aChildren.getClass() == this.getClass()) {
                shouldFlatten = true;
                newSize += aChildren.jjtGetNumChildren();
                continue;
            }
            ++newSize;
        }
        if (shouldFlatten) {
            Node[] newChildren = new Node[newSize];
            int j = 0;
            for (Node c : this.children) {
                if (c.getClass() == this.getClass()) {
                    for (int k = 0; k < c.jjtGetNumChildren(); ++k) {
                        newChildren[j++] = c.jjtGetChild(k);
                    }
                    continue;
                }
                newChildren[j++] = c;
            }
            if (j != newSize) {
                throw new Error("Assertion error: " + j + " != " + newSize);
            }
            this.children = newChildren;
        }
    }

    @Override
    public ExpressionAccessor getAccessor() {
        return this.accessor;
    }

    @Override
    public void setAccessor(ExpressionAccessor accessor) {
        this.accessor = accessor;
    }
}

