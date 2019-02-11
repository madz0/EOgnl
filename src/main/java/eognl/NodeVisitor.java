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
import eognl.OgnlException;

public interface NodeVisitor<R, P> {
    public R visit(ASTSequence var1, P var2) throws OgnlException;

    public R visit(ASTAssign var1, P var2) throws OgnlException;

    public R visit(ASTTest var1, P var2) throws OgnlException;

    public R visit(ASTOr var1, P var2) throws OgnlException;

    public R visit(ASTAnd var1, P var2) throws OgnlException;

    public R visit(ASTBitOr var1, P var2) throws OgnlException;

    public R visit(ASTXor var1, P var2) throws OgnlException;

    public R visit(ASTBitAnd var1, P var2) throws OgnlException;

    public R visit(ASTEq var1, P var2) throws OgnlException;

    public R visit(ASTNotEq var1, P var2) throws OgnlException;

    public R visit(ASTLess var1, P var2) throws OgnlException;

    public R visit(ASTGreater var1, P var2) throws OgnlException;

    public R visit(ASTLessEq var1, P var2) throws OgnlException;

    public R visit(ASTGreaterEq var1, P var2) throws OgnlException;

    public R visit(ASTIn var1, P var2) throws OgnlException;

    public R visit(ASTNotIn var1, P var2) throws OgnlException;

    public R visit(ASTShiftLeft var1, P var2) throws OgnlException;

    public R visit(ASTShiftRight var1, P var2) throws OgnlException;

    public R visit(ASTUnsignedShiftRight var1, P var2) throws OgnlException;

    public R visit(ASTAdd var1, P var2) throws OgnlException;

    public R visit(ASTSubtract var1, P var2) throws OgnlException;

    public R visit(ASTMultiply var1, P var2) throws OgnlException;

    public R visit(ASTDivide var1, P var2) throws OgnlException;

    public R visit(ASTRemainder var1, P var2) throws OgnlException;

    public R visit(ASTNegate var1, P var2) throws OgnlException;

    public R visit(ASTBitNegate var1, P var2) throws OgnlException;

    public R visit(ASTNot var1, P var2) throws OgnlException;

    public R visit(ASTInstanceof var1, P var2) throws OgnlException;

    public R visit(ASTChain var1, P var2) throws OgnlException;

    public R visit(ASTEval var1, P var2) throws OgnlException;

    public R visit(ASTConst var1, P var2) throws OgnlException;

    public R visit(ASTThisVarRef var1, P var2) throws OgnlException;

    public R visit(ASTRootVarRef var1, P var2) throws OgnlException;

    public R visit(ASTVarRef var1, P var2) throws OgnlException;

    public R visit(ASTList var1, P var2) throws OgnlException;

    public R visit(ASTMap var1, P var2) throws OgnlException;

    public R visit(ASTKeyValue var1, P var2) throws OgnlException;

    public R visit(ASTStaticField var1, P var2) throws OgnlException;

    public R visit(ASTCtor var1, P var2) throws OgnlException;

    public R visit(ASTProperty var1, P var2) throws OgnlException;

    public R visit(ASTStaticMethod var1, P var2) throws OgnlException;

    public R visit(ASTMethod var1, P var2) throws OgnlException;

    public R visit(ASTProject var1, P var2) throws OgnlException;

    public R visit(ASTSelect var1, P var2) throws OgnlException;

    public R visit(ASTSelectFirst var1, P var2) throws OgnlException;

    public R visit(ASTSelectLast var1, P var2) throws OgnlException;
}

