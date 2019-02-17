/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ASTAnd;
import eognl.ASTConst;
import eognl.ASTCtor;
import eognl.ASTOr;
import eognl.ASTProperty;
import eognl.ASTSequence;
import eognl.ASTStaticField;
import eognl.ASTVarRef;
import eognl.DynamicSubscript;
import eognl.EOgnlRuntime;
import eognl.Node;
import eognl.NodeType;
import eognl.NodeVisitor;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlOps;
import eognl.OgnlParser;
import eognl.SimpleNode;
import eognl.enhance.OrderedReturn;
import eognl.enhance.UnsupportedCompilationException;
import java.lang.reflect.Array;

public class ASTChain
extends SimpleNode
implements NodeType,
OrderedReturn {
    private Class<?> getterClass;
    private Class<?> setterClass;
    private String lastExpression;
    private String coreExpression;

    public ASTChain(int id) {
        super(id);
    }

    public ASTChain(OgnlParser p, int id) {
        super(p, id);
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
    public void jjtClose() {
        this.flattenTree();
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object result = source;
        int ilast = this.children.length - 1;
        for (int i = 0; i <= ilast; ++i) {
            ASTProperty propertyNode;
            ASTProperty indexNode;
            int indexType;
            boolean handled = false;
            if (i < ilast && this.children[i] instanceof ASTProperty && (indexType = (propertyNode = (ASTProperty)this.children[i]).getIndexedPropertyType(context, result)) != 0 && this.children[i + 1] instanceof ASTProperty && (indexNode = (ASTProperty)this.children[i + 1]).isIndexedAccess()) {
                Object index = indexNode.getProperty(context, result);
                if (index instanceof DynamicSubscript) {
                    if (indexType == 1) {
                        Object array = propertyNode.getValue(context, result);
                        int len = Array.getLength(array);
                        switch (((DynamicSubscript)index).getFlag()) {
                            case 3: {
                                result = Array.newInstance(array.getClass().getComponentType(), len);
                                System.arraycopy(array, 0, result, 0, len);
                                handled = true;
                                ++i;
                                break;
                            }
                            case 0: {
                                index = len > 0 ? 0 : -1;
                                break;
                            }
                            case 1: {
                                index = len > 0 ? len / 2 : -1;
                                break;
                            }
                            case 2: {
                                index = len > 0 ? len - 1 : -1;
                                break;
                            }
                            default: {
                                break;
                            }
                        }
                    } else if (indexType == 2) {
                        throw new OgnlException("DynamicSubscript '" + indexNode + "' not allowed for object indexed property '" + propertyNode + "'");
                    }
                }
                if (!handled) {
                    result = EOgnlRuntime.getIndexedProperty(context, result, propertyNode.getProperty(context, result).toString(), index);
                    handled = true;
                    ++i;
                }
            }
            if (handled) continue;
            result = this.children[i].getValue(context, result);
        }
        return result;
    }

    @Override
    protected void setValueBody(OgnlContext context, Object target, Object value) throws OgnlException {
        boolean handled = false;
        Object lastTarget = null;
        Node lastNode = null;
        context.put(OgnlContext.EXPRESSION_SET, value);
        int ilast = this.children.length - 2;
        for (int i = 0; i <= ilast; ++i) {
            ASTProperty propertyNode;
            ASTProperty indexNode;
            int indexType;
            if (i <= ilast && this.children[i] instanceof ASTProperty && (indexType = (propertyNode = (ASTProperty)this.children[i]).getIndexedPropertyType(context, target)) != 0 && this.children[i + 1] instanceof ASTProperty && (indexNode = (ASTProperty)this.children[i + 1]).isIndexedAccess()) {
                Object index = indexNode.getProperty(context, target);
                if (index instanceof DynamicSubscript) {
                    if (indexType == 1) {
                        Object array = propertyNode.getValue(context, target);
                        int len = Array.getLength(array);
                        switch (((DynamicSubscript)index).getFlag()) {
                            case 3: {
                                System.arraycopy(target, 0, value, 0, len);
                                handled = true;
                                ++i;
                                break;
                            }
                            case 0: {
                                index = len > 0 ? 0 : -1;
                                break;
                            }
                            case 1: {
                                index = len > 0 ? len / 2 : -1;
                                break;
                            }
                            case 2: {
                                index = len > 0 ? len - 1 : -1;
                                break;
                            }
                            default: {
                                break;
                            }
                        }
                    } else if (indexType == 2) {
                        throw new OgnlException("DynamicSubscript '" + indexNode + "' not allowed for object indexed property '" + propertyNode + "'");
                    }
                }
                if (!handled && i == ilast) {
                    EOgnlRuntime.setIndexedProperty(context, target, propertyNode.getProperty(context, target).toString(), index, value);
                    handled = true;
                    ++i;
                } else if (!handled) {
                    target = EOgnlRuntime.getIndexedProperty(context, target, propertyNode.getProperty(context, target).toString(), index);
                    ++i;
                    continue;
                }
            }
            if (handled) continue;
            lastTarget = target;
            lastNode = this.children[i];
            target = lastNode.getValue(context, lastTarget);
        }
        if (!handled) {
            this.children[this.children.length - 1].setValue(context, target, value);
            lastNode.getValue(context, lastTarget);
        }
    }

    @Override
    public boolean isSimpleNavigationChain(OgnlContext context) throws OgnlException {
        boolean result = false;
        if (this.children != null && this.children.length > 0) {
            result = true;
            for (int i = 0; result && i < this.children.length; ++i) {
                result = this.children[i] instanceof SimpleNode && ((SimpleNode)this.children[i]).isSimpleProperty(context);
            }
        }
        return result;
    }

    @Override
    public Class<?> getGetterClass() {
        return this.getterClass;
    }

    @Override
    public Class<?> getSetterClass() {
        return this.setterClass;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        String prevChain = (String)context.get("_currentChain");
        if (target != null) {
            context.setCurrentObject(target);
            context.setCurrentType(target.getClass());
        }
        String result = "";
        NodeType lastType = null;
        boolean ordered = false;
        boolean constructor = false;
        try {
            if (this.children != null && this.children.length > 0) {
                for (Node child : this.children) {
                    String value = child.toGetSourceString(context, context.getCurrentObject());
                    if (ASTCtor.class.isInstance(child)) {
                        constructor = true;
                    }
                    if (NodeType.class.isInstance(child) && ((NodeType)((Object)child)).getGetterClass() != null) {
                        lastType = (NodeType)((Object)child);
                    }
                    if (!(ASTVarRef.class.isInstance(child) || constructor || OrderedReturn.class.isInstance(child) && ((OrderedReturn)((Object)child)).getLastExpression() != null || this.parent != null && ASTSequence.class.isInstance(this.parent))) {
                        value = EOgnlRuntime.getCompiler(context).castExpression(context, child, value);
                    }
                    if (OrderedReturn.class.isInstance(child) && ((OrderedReturn)((Object)child)).getLastExpression() != null) {
                        ordered = true;
                        OrderedReturn or = (OrderedReturn)((Object)child);
                        result = or.getCoreExpression() == null || or.getCoreExpression().trim().length() <= 0 ? "" : String.valueOf(result) + or.getCoreExpression();
                        this.lastExpression = or.getLastExpression();
                        if (context.get("_preCast") != null) {
                            this.lastExpression = context.remove("_preCast") + this.lastExpression;
                        }
                    } else if (ASTOr.class.isInstance(child) || ASTAnd.class.isInstance(child) || ASTCtor.class.isInstance(child) || ASTStaticField.class.isInstance(child) && this.parent == null) {
                        context.put("_noRoot", (Object)"true");
                        result = value;
                    } else {
                        result = String.valueOf(result) + value;
                    }
                    context.put("_currentChain", (Object)result);
                }
            }
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        if (lastType != null) {
            this.getterClass = lastType.getGetterClass();
            this.setterClass = lastType.getSetterClass();
        }
        if (ordered) {
            this.coreExpression = result;
        }
        context.put("_currentChain", (Object)prevChain);
        return result;
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        String prevChain = (String)context.get("_currentChain");
        String prevChild = (String)context.get("_lastChild");
        if (prevChain != null) {
            throw new UnsupportedCompilationException("Can't compile nested chain expressions.");
        }
        if (target != null) {
            context.setCurrentObject(target);
            context.setCurrentType(target.getClass());
        }
        String result = "";
        NodeType lastType = null;
        boolean constructor = false;
        try {
            if (this.children != null && this.children.length > 0) {
                if (ASTConst.class.isInstance(this.children[0])) {
                    throw new UnsupportedCompilationException("Can't modify constant values.");
                }
                for (int i = 0; i < this.children.length; ++i) {
                    if (i == this.children.length - 1) {
                        context.put("_lastChild", (Object)"true");
                    }
                    String value = this.children[i].toSetSourceString(context, context.getCurrentObject());
                    if (ASTCtor.class.isInstance(this.children[i])) {
                        constructor = true;
                    }
                    if (NodeType.class.isInstance(this.children[i]) && ((NodeType)((Object)this.children[i])).getGetterClass() != null) {
                        lastType = (NodeType)((Object)this.children[i]);
                    }
                    if (!(ASTVarRef.class.isInstance(this.children[i]) || constructor || OrderedReturn.class.isInstance(this.children[i]) && ((OrderedReturn)((Object)this.children[i])).getLastExpression() != null || this.parent != null && ASTSequence.class.isInstance(this.parent))) {
                        value = EOgnlRuntime.getCompiler(context).castExpression(context, this.children[i], value);
                    }
                    if (ASTOr.class.isInstance(this.children[i]) || ASTAnd.class.isInstance(this.children[i]) || ASTCtor.class.isInstance(this.children[i]) || ASTStaticField.class.isInstance(this.children[i])) {
                        context.put("_noRoot", (Object)"true");
                        result = value;
                    } else {
                        result = String.valueOf(result) + value;
                    }
                    context.put("_currentChain", (Object)result);
                }
            }
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        context.put("_lastChild", (Object)prevChild);
        context.put("_currentChain", (Object)prevChain);
        if (lastType != null) {
            this.setterClass = lastType.getSetterClass();
        }
        return result;
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }
}

