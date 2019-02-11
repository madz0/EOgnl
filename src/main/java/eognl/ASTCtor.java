/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ASTChain;
import eognl.ASTConst;
import eognl.ASTProperty;
import eognl.ASTRootVarRef;
import eognl.EOgnlRuntime;
import eognl.Node;
import eognl.NodeVisitor;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.OgnlOps;
import eognl.OgnlParser;
import eognl.SimpleNode;
import eognl.TypeConverter;
import eognl.enhance.ExpressionCompiler;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.util.List;

public class ASTCtor
extends SimpleNode {
    private String className;
    private boolean isArray;

    public ASTCtor(int id) {
        super(id);
    }

    public ASTCtor(OgnlParser p, int id) {
        super(p, id);
    }

    void setClassName(String className) {
        this.className = className;
    }

    String getClassName() {
        return this.className;
    }

    void setArray(boolean value) {
        this.isArray = value;
    }

    public boolean isArray() {
        return this.isArray;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        Object root = context.getRoot();
        int count = this.jjtGetNumChildren();
        Object[] args = new Object[count];
        for (int i = 0; i < count; ++i) {
            args[i] = this.children[i].getValue(context, root);
        }
        if (!this.isArray) return EOgnlRuntime.callConstructor(context, this.className, args);
        if (args.length != 1) throw new OgnlException("only expect array size or fixed initializer list");
        try {
            int size;
            Class<?> componentClass = EOgnlRuntime.classForName(context, this.className);
            List sourceList = null;
            if (args[0] instanceof List) {
                sourceList = (List)args[0];
                size = sourceList.size();
            } else {
                size = (int)OgnlOps.longValue(args[0]);
            }
            Object result = Array.newInstance(componentClass, size);
            if (sourceList == null) return result;
            TypeConverter converter = context.getTypeConverter();
            int icount = sourceList.size();
            for (int i = 0; i < icount; ++i) {
                Object o = sourceList.get(i);
                if (o == null || componentClass.isInstance(o)) {
                    Array.set(result, i, o);
                    continue;
                }
                Array.set(result, i, converter.convertValue(context, null, null, null, o, componentClass));
            }
            return result;
        }
        catch (ClassNotFoundException ex) {
            throw new OgnlException("array component class '" + this.className + "' not found", ex);
        }
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        String result = "new " + this.className;
        Class<?> clazz = null;
        Object ctorValue = null;
        try {
            clazz = EOgnlRuntime.classForName(context, this.className);
            ctorValue = this.getValueBody(context, target);
            context.setCurrentObject(ctorValue);
            if (ctorValue != null) {
                context.setCurrentType(ctorValue.getClass());
                context.setCurrentAccessor(ctorValue.getClass());
            }
            if (this.isArray) {
                context.put("_ctorClass", clazz);
            }
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        try {
            if (this.isArray) {
                result = this.children[0] instanceof ASTConst ? String.valueOf(result) + "[" + this.children[0].toGetSourceString(context, target) + "]" : (ASTProperty.class.isInstance(this.children[0]) ? String.valueOf(result) + "[" + ExpressionCompiler.getRootExpression(this.children[0], target, context) + this.children[0].toGetSourceString(context, target) + "]" : (ASTChain.class.isInstance(this.children[0]) ? String.valueOf(result) + "[" + this.children[0].toGetSourceString(context, target) + "]" : String.valueOf(result) + "[] " + this.children[0].toGetSourceString(context, target)));
            } else {
                result = String.valueOf(result) + "(";
                if (this.children != null && this.children.length > 0) {
                    int i;
                    Object[] values = new Object[this.children.length];
                    String[] expressions = new String[this.children.length];
                    Class[] types = new Class[this.children.length];
                    for (int i2 = 0; i2 < this.children.length; ++i2) {
                        Object objValue = this.children[i2].getValue(context, context.getRoot());
                        String value = this.children[i2].toGetSourceString(context, target);
                        if (!ASTRootVarRef.class.isInstance(this.children[i2])) {
                            value = String.valueOf(ExpressionCompiler.getRootExpression(this.children[i2], target, context)) + value;
                        }
                        String cast = "";
                        if (ExpressionCompiler.shouldCast(this.children[i2])) {
                            cast = (String)context.remove("_preCast");
                        }
                        if (cast == null) {
                            cast = "";
                        }
                        if (!ASTConst.class.isInstance(this.children[i2])) {
                            value = String.valueOf(cast) + value;
                        }
                        values[i2] = objValue;
                        expressions[i2] = value;
                        types[i2] = context.getCurrentType();
                    }
                    Constructor<?>[] cons = clazz.getConstructors();
                    Constructor<?> ctor = null;
                    Class<?>[] ctorParamTypes = null;
                    for (i = 0; i < cons.length; ++i) {
                        Class<?>[] ctorTypes = cons[i].getParameterTypes();
                        if (!EOgnlRuntime.areArgsCompatible(values, ctorTypes) || ctor != null && !EOgnlRuntime.isMoreSpecific(ctorTypes, ctorParamTypes)) continue;
                        ctor = cons[i];
                        ctorParamTypes = ctorTypes;
                    }
                    if (ctor == null) {
                        ctor = EOgnlRuntime.getConvertedConstructorAndArgs(context, clazz, EOgnlRuntime.getConstructors(clazz), values, new Object[values.length]);
                    }
                    if (ctor == null) {
                        throw new NoSuchMethodException("Unable to find constructor appropriate for arguments in class: " + clazz);
                    }
                    ctorParamTypes = ctor.getParameterTypes();
                    for (i = 0; i < this.children.length; ++i) {
                        String literal;
                        if (i > 0) {
                            result = String.valueOf(result) + ", ";
                        }
                        String value = expressions[i];
                        if (types[i].isPrimitive() && (literal = EOgnlRuntime.getNumericLiteral(types[i])) != null) {
                            value = String.valueOf(value) + literal;
                        }
                        if (ctorParamTypes[i] != types[i]) {
                            if (!(values[i] == null || types[i].isPrimitive() || values[i].getClass().isArray() || ASTConst.class.isInstance(this.children[i]))) {
                                value = "(" + EOgnlRuntime.getCompiler(context).getInterfaceClass(values[i].getClass()).getName() + ")" + value;
                            } else if (!ASTConst.class.isInstance(this.children[i]) || ASTConst.class.isInstance(this.children[i]) && !types[i].isPrimitive()) {
                                value = !types[i].isArray() && types[i].isPrimitive() && !ctorParamTypes[i].isPrimitive() ? "new " + ExpressionCompiler.getCastString(EOgnlRuntime.getPrimitiveWrapperClass(types[i])) + "(" + value + ")" : " ($w) " + value;
                            }
                        }
                        result = String.valueOf(result) + value;
                    }
                }
                result = String.valueOf(result) + ")";
            }
            context.setCurrentType(ctorValue != null ? ctorValue.getClass() : clazz);
            context.setCurrentAccessor(clazz);
            context.setCurrentObject(ctorValue);
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        context.remove("_ctorClass");
        return result;
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        return "";
    }

    @Override
    public <R, P> R accept(NodeVisitor<? extends R, ? super P> visitor, P data) throws OgnlException {
        return visitor.visit(this, data);
    }
}

