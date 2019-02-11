/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ClassResolver;
import eognl.EOgnlContext;
import eognl.EOgnlRuntime;
import eognl.Evaluation;
import eognl.ExpressionSyntaxException;
import eognl.MemberAccess;
import eognl.Node;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlParser;
import eognl.ParseException;
import eognl.SimpleNode;
import eognl.TokenMgrError;
import eognl.TypeConverter;
import eognl.enhance.ExpressionAccessor;
import eognl.exinternal.util.MutableInt;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Member;
import java.util.Map;

public abstract class EOgnl {
    public static Object parseExpression(String expression) throws OgnlException {
        try {
            OgnlParser parser = new OgnlParser(new StringReader(expression));
            return parser.topLevelExpression();
        }
        catch (ParseException e) {
            throw new ExpressionSyntaxException(expression, e);
        }
        catch (TokenMgrError e) {
            throw new ExpressionSyntaxException(expression, e);
        }
    }

    public static Node compileExpression(OgnlContext context, Object root, String expression) throws Exception {
        Node expr = (Node)EOgnl.parseExpression(expression);
        EOgnlRuntime.compileExpression(context, expr, root);
        return expr;
    }

    public static OgnlContext createDefaultContext(Object root) {
        return EOgnl.addDefaultContext(root, null, null, null, new EOgnlContext());
    }

    public static OgnlContext createDefaultContext(Object root, ClassResolver classResolver) {
        return EOgnl.addDefaultContext(root, classResolver, null, null, new EOgnlContext());
    }

    public static OgnlContext createDefaultContext(Object root, ClassResolver classResolver, TypeConverter converter) {
        return EOgnl.addDefaultContext(root, classResolver, converter, null, new EOgnlContext());
    }

    public static OgnlContext createDefaultContext(Object root, ClassResolver classResolver, TypeConverter converter, MemberAccess memberAccess) {
        return EOgnl.addDefaultContext(root, classResolver, converter, memberAccess, new EOgnlContext());
    }

    public static OgnlContext addDefaultContext(Object root, OgnlContext context) {
        return EOgnl.addDefaultContext(root, null, null, null, context);
    }

    public static OgnlContext addDefaultContext(Object root, ClassResolver classResolver, OgnlContext context) {
        return EOgnl.addDefaultContext(root, classResolver, null, null, context);
    }

    public static OgnlContext addDefaultContext(Object root, ClassResolver classResolver, TypeConverter converter, OgnlContext context) {
        return EOgnl.addDefaultContext(root, classResolver, converter, null, context);
    }

    public static OgnlContext addDefaultContext(Object root, ClassResolver classResolver, TypeConverter converter, MemberAccess memberAccess, OgnlContext context) {
        OgnlContext result;
        if (!(context instanceof OgnlContext)) {
            result = new EOgnlContext();
            result.setValues(context);
        } else {
            result = context;
        }
        if (classResolver != null) {
            result.setClassResolver(classResolver);
        }
        if (converter != null) {
            result.setTypeConverter(converter);
        }
        if (memberAccess != null) {
            result.setMemberAccess(memberAccess);
        }
        result.setRoot(root);
        MutableInt mInt = (MutableInt)result.get("H0ldZzZz2@Id*4Cain");
        if (mInt != null) {
            StringBuffer key = null;
            for (int i = 0; i < mInt.get() + 5; ++i) {
                key = new StringBuffer();
                key.append("R3PpeZzenGnr#iK?").append(String.valueOf(i));
                result.remove(key.toString());
                key = new StringBuffer();
                key.append("R3PpeZzenArrAayS3etEEer#iK?").append(String.valueOf(i));
                result.remove(key.toString());
            }
        }
        result.put("H0ldZzZz2@Id*4Cain", (Object)new MutableInt());
        result.remove(OgnlContext.EXPANDED_ARRAY_KEY);
        Object root_param_info = context.get("PaArmRoo#eZInfo&r?");
        if (root_param_info != null) {
            StringBuffer key = new StringBuffer();
            key.append("R3PpeZzenGnr#iK?").append(String.valueOf(1));
            context.put(key.toString(), root_param_info);
        }
        return result;
    }

    public static void setClassResolver(OgnlContext context, ClassResolver classResolver) {
        context.put("_classResolver", (Object)classResolver);
    }

    public static ClassResolver getClassResolver(OgnlContext context) {
        return (ClassResolver)context.get("_classResolver");
    }

    public static void setTypeConverter(OgnlContext context, TypeConverter converter) {
        context.put("_typeConverter", (Object)converter);
    }

    public static TypeConverter getTypeConverter(OgnlContext context) {
        return (TypeConverter)context.get("_typeConverter");
    }

    public static void setMemberAccess(OgnlContext context, MemberAccess memberAccess) {
        context.put("_memberAccess", (Object)memberAccess);
    }

    public static MemberAccess getMemberAccess(OgnlContext context) {
        return (MemberAccess)context.get("_memberAccess");
    }

    public static void setRoot(OgnlContext context, Object root) {
        context.put("root", root);
    }

    public static Object getRoot(OgnlContext context) {
        return context.get("root");
    }

    public static Evaluation getLastEvaluation(OgnlContext context) {
        return (Evaluation)context.get("_lastEvaluation");
    }

    public static <T> T getValue(Object tree, OgnlContext context, Object root) throws OgnlException {
        return EOgnl.getValue(tree, context, root, null);
    }

    public static <T> T getValue(Object tree, OgnlContext context, Object root, Class<T> resultType) throws OgnlException {
        OgnlContext ognlContext = EOgnl.addDefaultContext(root, context);
        Node node = (Node)tree;
        Object result = node.getAccessor() != null ? node.getAccessor().get(ognlContext, root) : node.getValue(ognlContext, root);
        if (resultType != null) {
            result = EOgnl.getTypeConverter(context).convertValue(context, root, null, null, result, resultType);
        } else if (EOgnlRuntime.isPrimitivesCasted(ognlContext)) {
            result = EOgnl.getTypeConverter(ognlContext).convertValue(context, root, null, null, result, null);
        }
        return (T)result;
    }

    public static <T> T getValue(ExpressionAccessor expression, OgnlContext context, Object root) {
        return (T)expression.get(context, root);
    }

    public static <T> T getValue(ExpressionAccessor expression, OgnlContext context, Object root, Class<T> resultType) throws OgnlException {
        return EOgnl.getTypeConverter(context).convertValue(context, root, null, null, expression.get(context, root), resultType);
    }

    public static <T> T getValue(String expression, OgnlContext context, Object root) throws OgnlException {
        return EOgnl.getValue(expression, context, root, null);
    }

    public static <T> T getValue(String expression, OgnlContext context, Object root, Class<T> resultType) throws OgnlException {
        return EOgnl.getValue(EOgnl.parseExpression(expression), context, root, resultType);
    }

    public static <T> T getValue(Object tree, Object root) throws OgnlException {
        return EOgnl.getValue(tree, root, null);
    }

    public static <T> T getValue(Object tree, Object root, Class<T> resultType) throws OgnlException {
        return EOgnl.getValue(tree, EOgnl.createDefaultContext(root), root, resultType);
    }

    public static <T> T getValue(String expression, Object root) throws OgnlException {
        return EOgnl.getValue(expression, root, null);
    }

    public static <T> T getValue(String expression, Object root, Class<T> resultType) throws OgnlException {
        return EOgnl.getValue(EOgnl.parseExpression(expression), root, resultType);
    }

    public static void setValue(Object tree, OgnlContext context, Object root, Object value) throws OgnlException {
        OgnlContext ognlContext = EOgnl.addDefaultContext(root, context);
        Node n = (Node)tree;
        if (n.getAccessor() != null) {
            n.getAccessor().set(ognlContext, root, value);
            return;
        }
        n.setValue(ognlContext, root, value);
    }

    public static void setValue(ExpressionAccessor expression, OgnlContext context, Object root, Object value) {
        expression.set(context, root, value);
    }

    public static void setValue(String expression, OgnlContext context, Object root, Object value) throws OgnlException {
        EOgnl.setValue(EOgnl.parseExpression(expression), context, root, value);
    }

    public static void setValue(Object tree, Object root, Object value) throws OgnlException {
        EOgnl.setValue(tree, EOgnl.createDefaultContext(root), root, value);
    }

    public static void setValue(String expression, Object root, Object value) throws OgnlException {
        EOgnl.setValue(EOgnl.parseExpression(expression), root, value);
    }

    public static boolean isConstant(Object tree, OgnlContext context) throws OgnlException {
        return ((SimpleNode)tree).isConstant(EOgnl.addDefaultContext(null, context));
    }

    public static boolean isConstant(String expression, OgnlContext context) throws OgnlException {
        return EOgnl.isConstant(EOgnl.parseExpression(expression), context);
    }

    public static boolean isConstant(Object tree) throws OgnlException {
        return EOgnl.isConstant(tree, EOgnl.createDefaultContext(null));
    }

    public static boolean isConstant(String expression) throws OgnlException {
        return EOgnl.isConstant(EOgnl.parseExpression(expression), EOgnl.createDefaultContext(null));
    }

    public static boolean isSimpleProperty(Object tree, OgnlContext context) throws OgnlException {
        return ((SimpleNode)tree).isSimpleProperty(EOgnl.addDefaultContext(null, context));
    }

    public static boolean isSimpleProperty(String expression, OgnlContext context) throws OgnlException {
        return EOgnl.isSimpleProperty(EOgnl.parseExpression(expression), context);
    }

    public static boolean isSimpleProperty(Object tree) throws OgnlException {
        return EOgnl.isSimpleProperty(tree, EOgnl.createDefaultContext(null));
    }

    public static boolean isSimpleProperty(String expression) throws OgnlException {
        return EOgnl.isSimpleProperty(EOgnl.parseExpression(expression), EOgnl.createDefaultContext(null));
    }

    public static boolean isSimpleNavigationChain(Object tree, OgnlContext context) throws OgnlException {
        return ((SimpleNode)tree).isSimpleNavigationChain(EOgnl.addDefaultContext(null, context));
    }

    public static boolean isSimpleNavigationChain(String expression, OgnlContext context) throws OgnlException {
        return EOgnl.isSimpleNavigationChain(EOgnl.parseExpression(expression), context);
    }

    public static boolean isSimpleNavigationChain(Object tree) throws OgnlException {
        return EOgnl.isSimpleNavigationChain(tree, EOgnl.createDefaultContext(null));
    }

    public static boolean isSimpleNavigationChain(String expression) throws OgnlException {
        return EOgnl.isSimpleNavigationChain(EOgnl.parseExpression(expression), EOgnl.createDefaultContext(null));
    }

    private EOgnl() {
    }

    public static <T> T getExpandedRootArray(OgnlContext context) {
        return (T)context.get(OgnlContext.EXPANDED_ARRAY_KEY);
    }
}

