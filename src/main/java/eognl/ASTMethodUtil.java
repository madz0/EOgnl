/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ASTConst;
import eognl.EOgnlRuntime;
import eognl.Node;
import eognl.NodeType;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.enhance.ExpressionCompiler;
import eognl.enhance.OgnlExpressionCompiler;

class ASTMethodUtil {
    private ASTMethodUtil() {
    }

    static String getParmString(OgnlContext context, Object root, Node child, Class<?> prevType) throws OgnlException {
        String parmString = child.toGetSourceString(context, root);
        if (parmString == null || parmString.trim().length() < 1) {
            parmString = "null";
        }
        if (ASTConst.class.isInstance(child)) {
            context.setCurrentType(prevType);
        }
        parmString = String.valueOf(ExpressionCompiler.getRootExpression(child, root, context)) + parmString;
        String cast = "";
        if (ExpressionCompiler.shouldCast(child)) {
            cast = (String)context.remove("_preCast");
        }
        if (cast == null) {
            cast = "";
        }
        if (!ASTConst.class.isInstance(child)) {
            parmString = String.valueOf(cast) + parmString;
        }
        return parmString;
    }

    static Class<?> getValueClass(OgnlContext context, Object root, Node child) throws OgnlException {
        Class<?> valueClass;
        Object value = child.getValue(context, root);
        Class<?> class_ = valueClass = value != null ? value.getClass() : null;
        if (NodeType.class.isAssignableFrom(child.getClass())) {
            valueClass = ((NodeType)((Object)child)).getGetterClass();
        }
        return valueClass;
    }

    static String getParmString(OgnlContext context, Class<?> parm, String parmString, Node child, Class<?> valueClass, String endParam) {
        OgnlExpressionCompiler compiler = EOgnlRuntime.getCompiler(context);
        if (parm.isArray()) {
            parmString = compiler.createLocalReference(context, "(" + ExpressionCompiler.getCastString(parm) + ")org.apache.commons.ognl.OgnlOps#toArray(" + parmString + ", " + parm.getComponentType().getName() + endParam, parm);
        } else if (parm.isPrimitive()) {
            Class<?> wrapClass = EOgnlRuntime.getPrimitiveWrapperClass(parm);
            parmString = compiler.createLocalReference(context, "((" + wrapClass.getName() + ")org.apache.commons.ognl.OgnlOps#convertValue(" + parmString + "," + wrapClass.getName() + ".class, true))." + EOgnlRuntime.getNumericValueGetter(wrapClass), parm);
        } else if (parm != Object.class) {
            parmString = compiler.createLocalReference(context, "(" + parm.getName() + ")org.apache.commons.ognl.OgnlOps#convertValue(" + parmString + "," + parm.getName() + ".class)", parm);
        } else if (NodeType.class.isInstance(child) && ((NodeType)((Object)child)).getGetterClass() != null && Number.class.isAssignableFrom(((NodeType)((Object)child)).getGetterClass()) || valueClass != null && valueClass.isPrimitive()) {
            parmString = " ($w) " + parmString;
        } else if (valueClass != null && valueClass.isPrimitive()) {
            parmString = "($w) " + parmString;
        }
        return parmString;
    }
}

