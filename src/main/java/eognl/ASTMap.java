/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ASTKeyValue;
import eognl.EOgnlRuntime;
import eognl.Node;
import eognl.NodeVisitor;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlParser;
import eognl.SimpleNode;
import eognl.enhance.UnsupportedCompilationException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

class ASTMap
extends SimpleNode {
    private String className;
    private Map<OgnlContext, Class<?>> defaultMapClassMap = new HashMap();

    public ASTMap(int id) {
        super(id);
    }

    public ASTMap(OgnlParser p, int id) {
        super(p, id);
    }

    protected void setClassName(String value) {
        this.className = value;
    }

    String getClassName() {
        return this.className;
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Map answer;
        if (this.className == null) {
            Class<?> defaultMapClass = this.getDefaultMapClass(context);
            try {
                answer = (Map)defaultMapClass.newInstance();
            }
            catch (Exception ex) {
                throw new OgnlException("Default Map class '" + defaultMapClass.getName() + "' instantiation error", ex);
            }
        }
        try {
            answer = (Map)EOgnlRuntime.classForName(context, this.className).newInstance();
        }
        catch (Exception ex) {
            throw new OgnlException("Map implementor '" + this.className + "' not found", ex);
        }
        for (int i = 0; i < this.jjtGetNumChildren(); ++i) {
            Node v;
            ASTKeyValue kv = (ASTKeyValue)this.children[i];
            Node k = kv.getKey();
            answer.put(k.getValue(context, source), (v = kv.getValue()) == null ? null : v.getValue(context, source));
        }
        return answer;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        throw new UnsupportedCompilationException("Map expressions not supported as native java yet.");
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        throw new UnsupportedCompilationException("Map expressions not supported as native java yet.");
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }

    private Class<?> getDefaultMapClass(OgnlContext context) {
        Class defaultMapClass = this.defaultMapClassMap.get(context);
        if (defaultMapClass != null) {
            return defaultMapClass;
        }
        defaultMapClass = LinkedHashMap.class;
        this.defaultMapClassMap.put(context, defaultMapClass);
        return defaultMapClass;
    }
}

