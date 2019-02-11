/*
 * Decompiled with CFR 0.139.
 * 
 * Could not load the following classes:
 *  javassist.CannotCompileException
 *  javassist.ClassPath
 *  javassist.ClassPool
 *  javassist.CtClass
 *  javassist.CtConstructor
 *  javassist.CtField
 *  javassist.CtMethod
 *  javassist.CtNewConstructor
 *  javassist.CtNewMethod
 *  javassist.LoaderClassPath
 *  javassist.NotFoundException
 */
package eognl.enhance;

import eognl.ASTAnd;
import eognl.ASTChain;
import eognl.ASTConst;
import eognl.ASTCtor;
import eognl.ASTList;
import eognl.ASTMethod;
import eognl.ASTOr;
import eognl.ASTProperty;
import eognl.ASTRootVarRef;
import eognl.ASTStaticField;
import eognl.ASTStaticMethod;
import eognl.ASTThisVarRef;
import eognl.ASTVarRef;
import eognl.ClassResolver;
import eognl.EOgnlRuntime;
import eognl.ExpressionNode;
import eognl.Node;
import eognl.OgnlContext;
import eognl.enhance.ContextClassLoader;
import eognl.enhance.EnhancedClassLoader;
import eognl.enhance.ExpressionAccessor;
import eognl.enhance.LocalReference;
import eognl.enhance.LocalReferenceImpl;
import eognl.enhance.OgnlExpressionCompiler;
import eognl.enhance.OrderedReturn;
import eognl.enhance.UnsupportedCompilationException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javassist.CannotCompileException;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

public class ExpressionCompiler
implements OgnlExpressionCompiler {
    public static final String PRE_CAST = "_preCast";
    protected Map<ClassResolver, EnhancedClassLoader> loaders = new HashMap<ClassResolver, EnhancedClassLoader>();
    protected ClassPool pool;
    protected int classCounter = 0;

    public static void addCastString(OgnlContext context, String cast) {
        String value = (String)context.get(PRE_CAST);
        value = value != null ? String.valueOf(cast) + value : cast;
        context.put(PRE_CAST, (Object)value);
    }

    public static String getCastString(Class<?> type) {
        if (type == null) {
            return null;
        }
        return type.isArray() ? String.valueOf(type.getComponentType().getName()) + "[]" : type.getName();
    }

    public static String getRootExpression(Node expression, Object root, OgnlContext context) {
        String rootExpr = "";
        if (!ExpressionCompiler.shouldCast(expression)) {
            return rootExpr;
        }
        if (!((ASTList.class.isInstance(expression) || ASTVarRef.class.isInstance(expression) || ASTStaticMethod.class.isInstance(expression) || ASTStaticField.class.isInstance(expression) || ASTConst.class.isInstance(expression) || ExpressionNode.class.isInstance(expression) || ASTCtor.class.isInstance(expression) || ASTStaticMethod.class.isInstance(expression) || root == null) && (root == null || !ASTRootVarRef.class.isInstance(expression)))) {
            Class<?> castClass = EOgnlRuntime.getCompiler(context).getRootExpressionClass(expression, context);
            if (castClass.isArray() || ASTRootVarRef.class.isInstance(expression) || ASTThisVarRef.class.isInstance(expression)) {
                rootExpr = "((" + ExpressionCompiler.getCastString(castClass) + ")$2)";
                if (ASTProperty.class.isInstance(expression) && !((ASTProperty)expression).isIndexedAccess()) {
                    rootExpr = String.valueOf(rootExpr) + ".";
                }
            } else {
                rootExpr = ASTProperty.class.isInstance(expression) && ((ASTProperty)expression).isIndexedAccess() || ASTChain.class.isInstance(expression) ? "((" + ExpressionCompiler.getCastString(castClass) + ")$2)" : "((" + ExpressionCompiler.getCastString(castClass) + ")$2).";
            }
        }
        return rootExpr;
    }

    public static boolean shouldCast(Node expression) {
        Node child;
        if (ASTChain.class.isInstance(expression) && (ASTConst.class.isInstance(child = expression.jjtGetChild(0)) || ASTStaticMethod.class.isInstance(child) || ASTStaticField.class.isInstance(child) || ASTVarRef.class.isInstance(child) && !ASTRootVarRef.class.isInstance(child))) {
            return false;
        }
        return !ASTConst.class.isInstance(expression);
    }

    @Override
    public String castExpression(OgnlContext context, Node expression, String body) {
        if (context.getCurrentAccessor() == null || context.getPreviousType() == null || context.getCurrentAccessor().isAssignableFrom(context.getPreviousType()) || context.getCurrentType() != null && context.getCurrentObject() != null && context.getCurrentType().isAssignableFrom(context.getCurrentObject().getClass()) && context.getCurrentAccessor().isAssignableFrom(context.getPreviousType()) || body == null || body.trim().length() < 1 || context.getCurrentType() != null && context.getCurrentType().isArray() && (context.getPreviousType() == null || context.getPreviousType() != Object.class) || ASTOr.class.isInstance(expression) || ASTAnd.class.isInstance(expression) || ASTRootVarRef.class.isInstance(expression) || context.getCurrentAccessor() == Class.class || context.get(PRE_CAST) != null && ((String)context.get(PRE_CAST)).startsWith("new") || ASTStaticField.class.isInstance(expression) || ASTStaticMethod.class.isInstance(expression) || OrderedReturn.class.isInstance(expression) && ((OrderedReturn)((Object)expression)).getLastExpression() != null) {
            return body;
        }
        ExpressionCompiler.addCastString(context, "((" + ExpressionCompiler.getCastString(context.getCurrentAccessor()) + ")");
        return ")" + body;
    }

    @Override
    public String getClassName(Class<?> clazz) {
        Class<?>[] interfaces;
        if ("java.util.AbstractList$Itr".equals(clazz.getName())) {
            return Iterator.class.getName();
        }
        if (Modifier.isPublic(clazz.getModifiers()) && clazz.isInterface()) {
            return clazz.getName();
        }
        for (Class<?> intface : interfaces = clazz.getInterfaces()) {
            if (intface.getName().indexOf("util.List") > 0) {
                return intface.getName();
            }
            if (intface.getName().indexOf("Iterator") <= 0) continue;
            return intface.getName();
        }
        if (clazz.getSuperclass() != null && clazz.getSuperclass().getInterfaces().length > 0) {
            return this.getClassName(clazz.getSuperclass());
        }
        return clazz.getName();
    }

    @Override
    public Class<?> getSuperOrInterfaceClass(Method m, Class<?> clazz) {
        Class<?> superClass;
        if (clazz.getInterfaces() != null && clazz.getInterfaces().length > 0) {
            Class<?>[] intfs;
            for (Class<?> intf : intfs = clazz.getInterfaces()) {
                Class<?> intClass = this.getSuperOrInterfaceClass(m, intf);
                if (intClass != null) {
                    return intClass;
                }
                if (!Modifier.isPublic(intf.getModifiers()) || !this.containsMethod(m, intf)) continue;
                return intf;
            }
        }
        if (clazz.getSuperclass() != null && (superClass = this.getSuperOrInterfaceClass(m, clazz.getSuperclass())) != null) {
            return superClass;
        }
        if (Modifier.isPublic(clazz.getModifiers()) && this.containsMethod(m, clazz)) {
            return clazz;
        }
        return null;
    }

    public boolean containsMethod(Method m, Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        if (methods == null) {
            return false;
        }
        for (Method method : methods) {
            Class<?>[] mparms;
            Class<?>[] mexceptions;
            Class<?>[] exceptions;
            Class<?>[] parms;
            if (!method.getName().equals(m.getName()) || method.getReturnType() != m.getReturnType() || (parms = m.getParameterTypes()) == null || (mparms = method.getParameterTypes()) == null || mparms.length != parms.length) continue;
            boolean parmsMatch = true;
            for (int p = 0; p < parms.length; ++p) {
                if (parms[p] == mparms[p]) continue;
                parmsMatch = false;
                break;
            }
            if (!parmsMatch || (exceptions = m.getExceptionTypes()) == null || (mexceptions = method.getExceptionTypes()) == null || mexceptions.length != exceptions.length) continue;
            boolean exceptionsMatch = true;
            for (int e = 0; e < exceptions.length; ++e) {
                if (exceptions[e] == mexceptions[e]) continue;
                exceptionsMatch = false;
                break;
            }
            if (!exceptionsMatch) continue;
            return true;
        }
        return false;
    }

    @Override
    public Class<?> getInterfaceClass(Class<?> clazz) {
        Class<?>[] intf;
        if ("java.util.AbstractList$Itr".equals(clazz.getName())) {
            return Iterator.class;
        }
        if (Modifier.isPublic(clazz.getModifiers()) && clazz.isInterface() || clazz.isPrimitive()) {
            return clazz;
        }
        for (Class<?> anIntf : intf = clazz.getInterfaces()) {
            if (List.class.isAssignableFrom(anIntf)) {
                return List.class;
            }
            if (Iterator.class.isAssignableFrom(anIntf)) {
                return Iterator.class;
            }
            if (Map.class.isAssignableFrom(anIntf)) {
                return Map.class;
            }
            if (Set.class.isAssignableFrom(anIntf)) {
                return Set.class;
            }
            if (!Collection.class.isAssignableFrom(anIntf)) continue;
            return Collection.class;
        }
        if (clazz.getSuperclass() != null && clazz.getSuperclass().getInterfaces().length > 0) {
            return this.getInterfaceClass(clazz.getSuperclass());
        }
        return clazz;
    }

    @Override
    public Class<?> getRootExpressionClass(Node rootNode, OgnlContext context) {
        if (context.getRoot() == null) {
            return null;
        }
        Class<?> ret = context.getRoot().getClass();
        if (context.getFirstAccessor() != null && context.getFirstAccessor().isInstance(context.getRoot())) {
            ret = context.getFirstAccessor();
        }
        return ret;
    }

    @Override
    public void compileExpression(OgnlContext context, Node expression, Object root) throws Exception {
        CtField nodeMember;
        CtClass newClass;
        String setBody;
        String getBody;
        ClassPool classPool;
        block9 : {
            if (expression.getAccessor() != null) {
                return;
            }
            EnhancedClassLoader loader = this.getClassLoader(context);
            classPool = this.getClassPool(context, loader);
            newClass = classPool.makeClass(String.valueOf(expression.getClass().getName()) + expression.hashCode() + this.classCounter++ + "Accessor");
            newClass.addInterface(this.getCtClass(ExpressionAccessor.class));
            CtClass ognlClass = this.getCtClass(OgnlContext.class);
            CtClass objClass = this.getCtClass(Object.class);
            CtMethod valueGetter = new CtMethod(objClass, "get", new CtClass[]{ognlClass, objClass}, newClass);
            CtMethod valueSetter = new CtMethod(CtClass.voidType, "set", new CtClass[]{ognlClass, objClass, objClass}, newClass);
            nodeMember = null;
            CtClass nodeClass = this.getCtClass(Node.class);
            CtMethod setExpression = null;
            try {
                getBody = this.generateGetter(context, newClass, objClass, classPool, valueGetter, expression, root);
            }
            catch (UnsupportedCompilationException uc) {
                nodeMember = new CtField(nodeClass, "_node", newClass);
                newClass.addField(nodeMember);
                getBody = this.generateOgnlGetter(newClass, valueGetter, nodeMember);
                setExpression = CtNewMethod.setter((String)"setExpression", (CtField)nodeMember);
                newClass.addMethod(setExpression);
            }
            try {
                setBody = this.generateSetter(context, newClass, objClass, classPool, valueSetter, expression, root);
            }
            catch (UnsupportedCompilationException uc) {
                if (nodeMember == null) {
                    nodeMember = new CtField(nodeClass, "_node", newClass);
                    newClass.addField(nodeMember);
                }
                setBody = this.generateOgnlSetter(newClass, valueSetter, nodeMember);
                if (setExpression != null) break block9;
                setExpression = CtNewMethod.setter((String)"setExpression", (CtField)nodeMember);
                newClass.addMethod(setExpression);
            }
        }
        try {
            newClass.addConstructor(CtNewConstructor.defaultConstructor((CtClass)newClass));
            Class clazz = classPool.toClass(newClass);
            newClass.detach();
            expression.setAccessor((ExpressionAccessor)clazz.newInstance());
            if (nodeMember != null) {
                expression.getAccessor().setExpression(expression);
            }
        }
        catch (Throwable t) {
            throw new RuntimeException("Error compiling expression on object " + root + " with expression node " + expression + " getter body: " + getBody + " setter body: " + setBody, t);
        }
    }

    protected String generateGetter(OgnlContext context, CtClass newClass, CtClass objClass, ClassPool classPool, CtMethod valueGetter, Node expression, Object root) throws Exception {
        String pre = "";
        String post = "";
        context.setRoot(root);
        context.remove(PRE_CAST);
        String getterCode = expression.toGetSourceString(context, root);
        if (getterCode == null || getterCode.trim().length() <= 0 && !ASTVarRef.class.isAssignableFrom(expression.getClass())) {
            getterCode = "null";
        }
        String castExpression = (String)context.get(PRE_CAST);
        if (context.getCurrentType() == null || context.getCurrentType().isPrimitive() || Character.class.isAssignableFrom(context.getCurrentType()) || Object.class == context.getCurrentType()) {
            pre = String.valueOf(pre) + " ($w) (";
            post = String.valueOf(post) + ")";
        }
        String rootExpr = !"null".equals(getterCode) ? ExpressionCompiler.getRootExpression(expression, root, context) : "";
        String noRoot = (String)context.remove("_noRoot");
        if (noRoot != null) {
            rootExpr = "";
        }
        this.createLocalReferences(context, classPool, newClass, objClass, valueGetter.getParameterTypes());
        String body = OrderedReturn.class.isInstance(expression) && ((OrderedReturn)((Object)expression)).getLastExpression() != null ? "{ " + (ASTMethod.class.isInstance(expression) || ASTChain.class.isInstance(expression) ? rootExpr : "") + (castExpression != null ? castExpression : "") + ((OrderedReturn)((Object)expression)).getCoreExpression() + " return " + pre + ((OrderedReturn)((Object)expression)).getLastExpression() + post + ";}" : "{  return " + pre + (castExpression != null ? castExpression : "") + rootExpr + getterCode + post + ";}";
        body = body.replaceAll("\\.\\.", ".");
        valueGetter.setBody(body);
        newClass.addMethod(valueGetter);
        return body;
    }

    @Override
    public String createLocalReference(OgnlContext context, String expression, Class<?> type) {
        String referenceName = "ref" + context.incrementLocalReferenceCounter();
        context.addLocalReference(referenceName, new LocalReferenceImpl(referenceName, expression, type));
        String castString = "";
        if (!type.isPrimitive()) {
            castString = "(" + ExpressionCompiler.getCastString(type) + ") ";
        }
        return String.valueOf(castString) + referenceName + "($$)";
    }

    void createLocalReferences(OgnlContext context, ClassPool classPool, CtClass clazz, CtClass unused, CtClass[] params) throws NotFoundException, CannotCompileException {
        Map<String, LocalReference> referenceMap = context.getLocalReferences();
        if (referenceMap == null || referenceMap.isEmpty()) {
            return;
        }
        Iterator<LocalReference> it = referenceMap.values().iterator();
        while (it.hasNext()) {
            LocalReference ref = it.next();
            String widener = ref.getType().isPrimitive() ? " " : " ($w) ";
            String body = String.format("{ return %s %s; }", widener, ref.getExpression()).replaceAll("\\.\\.", ".");
            CtMethod method = new CtMethod(classPool.get(ExpressionCompiler.getCastString(ref.getType())), ref.getName(), params, clazz);
            method.setBody(body);
            clazz.addMethod(method);
            it.remove();
        }
    }

    protected String generateSetter(OgnlContext context, CtClass newClass, CtClass objClass, ClassPool classPool, CtMethod valueSetter, Node expression, Object root) throws Exception {
        if (ExpressionNode.class.isInstance(expression) || ASTConst.class.isInstance(expression)) {
            throw new UnsupportedCompilationException("Can't compile expression/constant setters.");
        }
        context.setRoot(root);
        context.remove(PRE_CAST);
        String setterCode = expression.toSetSourceString(context, root);
        String castExpression = (String)context.get(PRE_CAST);
        if (setterCode == null || setterCode.trim().length() < 1) {
            throw new UnsupportedCompilationException("Can't compile null setter body.");
        }
        if (root == null) {
            throw new UnsupportedCompilationException("Can't compile setters with a null root object.");
        }
        String pre = ExpressionCompiler.getRootExpression(expression, root, context);
        String noRoot = (String)context.remove("_noRoot");
        if (noRoot != null) {
            pre = "";
        }
        this.createLocalReferences(context, classPool, newClass, objClass, valueSetter.getParameterTypes());
        String body = "{" + (castExpression != null ? castExpression : "") + pre + setterCode + ";}";
        body = body.replaceAll("\\.\\.", ".");
        valueSetter.setBody(body);
        newClass.addMethod(valueSetter);
        return body;
    }

    protected String generateOgnlGetter(CtClass clazz, CtMethod valueGetter, CtField node) throws Exception {
        String body = "return " + node.getName() + ".getValue($1, $2);";
        valueGetter.setBody(body);
        clazz.addMethod(valueGetter);
        return body;
    }

    protected String generateOgnlSetter(CtClass clazz, CtMethod valueSetter, CtField node) throws Exception {
        String body = String.valueOf(node.getName()) + ".setValue($1, $2, $3);";
        valueSetter.setBody(body);
        clazz.addMethod(valueSetter);
        return body;
    }

    protected EnhancedClassLoader getClassLoader(OgnlContext context) {
        EnhancedClassLoader ret = this.loaders.get(context.getClassResolver());
        if (ret != null) {
            return ret;
        }
        ContextClassLoader classLoader = new ContextClassLoader(OgnlContext.class.getClassLoader(), context);
        ret = new EnhancedClassLoader(classLoader);
        this.loaders.put(context.getClassResolver(), ret);
        return ret;
    }

    protected CtClass getCtClass(Class<?> searchClass) throws NotFoundException {
        return this.pool.get(searchClass.getName());
    }

    protected ClassPool getClassPool(OgnlContext context, EnhancedClassLoader loader) {
        if (this.pool != null) {
            return this.pool;
        }
        this.pool = ClassPool.getDefault();
        this.pool.insertClassPath((ClassPath)new LoaderClassPath(loader.getParent()));
        return this.pool;
    }
}

