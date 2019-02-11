/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ASTProperty;
import eognl.Node;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.PropertyAccessor;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class MapPropertyAccessor
implements PropertyAccessor {
    @Override
    public Object getProperty(OgnlContext context, Object target, Object name) throws OgnlException {
        Map map = (Map)target;
        Node currentNode = context.getCurrentNode().jjtGetParent();
        boolean indexedAccess = false;
        if (currentNode == null) {
            throw new OgnlException("node is null for '" + name + "'");
        }
        if (!(currentNode instanceof ASTProperty)) {
            currentNode = currentNode.jjtGetParent();
        }
        if (currentNode instanceof ASTProperty) {
            indexedAccess = ((ASTProperty)currentNode).isIndexedAccess();
        }
        Object result = name instanceof String && !indexedAccess ? ("size".equals(name) ? Integer.valueOf(map.size()) : ("keys".equals(name) || "keySet".equals(name) ? map.keySet() : ("values".equals(name) ? map.values() : ("isEmpty".equals(name) ? (map.isEmpty() ? Boolean.TRUE : Boolean.FALSE) : map.get(name))))) : map.get(name);
        return result;
    }

    @Override
    public void setProperty(OgnlContext context, Object target, Object name, Object value) throws OgnlException {
        Map map = (Map)target;
        map.put(name, value);
    }

    @Override
    public String getSourceAccessor(OgnlContext context, Object target, Object index) {
        Node currentNode = context.getCurrentNode().jjtGetParent();
        boolean indexedAccess = false;
        if (currentNode == null) {
            throw new RuntimeException("node is null for '" + index + "'");
        }
        if (!(currentNode instanceof ASTProperty)) {
            currentNode = currentNode.jjtGetParent();
        }
        if (currentNode instanceof ASTProperty) {
            indexedAccess = ((ASTProperty)currentNode).isIndexedAccess();
        }
        String indexStr = index.toString();
        context.setCurrentAccessor(Map.class);
        context.setCurrentType(Object.class);
        if (String.class.isInstance(index) && !indexedAccess) {
            String key = indexStr.replaceAll("\"", "");
            if ("size".equals(key)) {
                context.setCurrentType(Integer.TYPE);
                return ".size()";
            }
            if ("keys".equals(key) || "keySet".equals(key)) {
                context.setCurrentType(Set.class);
                return ".keySet()";
            }
            if ("values".equals(key)) {
                context.setCurrentType(Collection.class);
                return ".values()";
            }
            if ("isEmpty".equals(key)) {
                context.setCurrentType(Boolean.TYPE);
                return ".isEmpty()";
            }
        }
        return ".get(" + indexStr + ")";
    }

    @Override
    public String getSourceSetter(OgnlContext context, Object target, Object index) {
        String key;
        context.setCurrentAccessor(Map.class);
        context.setCurrentType(Object.class);
        String indexStr = index.toString();
        if (String.class.isInstance(index) && ("size".equals(key = indexStr.replaceAll("\"", "")) || "keys".equals(key) || "keySet".equals(key) || "values".equals(key) || "isEmpty".equals(key))) {
            return "";
        }
        return ".put(" + indexStr + ", $3)";
    }
}

