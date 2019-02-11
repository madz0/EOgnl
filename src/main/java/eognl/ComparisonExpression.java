/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.BooleanExpression;
import eognl.EOgnlRuntime;
import eognl.Node;
import eognl.OgnlContext;
import eognl.OgnlOps;
import eognl.OgnlParser;
import eognl.enhance.UnsupportedCompilationException;

public abstract class ComparisonExpression
extends BooleanExpression {
    private static final long serialVersionUID = -5945855000509930682L;

    public ComparisonExpression(int id) {
        super(id);
    }

    public ComparisonExpression(OgnlParser p, int id) {
        super(p, id);
    }

    public abstract String getComparisonFunction();

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        if (target == null) {
            throw new UnsupportedCompilationException("Current target is null, can't compile.");
        }
        try {
            Object value = this.getValueBody(context, target);
            this.getterClass = value != null && Boolean.class.isAssignableFrom(value.getClass()) ? Boolean.TYPE : (value != null ? value.getClass() : Boolean.TYPE);
            EOgnlRuntime.getChildSource(context, target, this.children[0]);
            EOgnlRuntime.getChildSource(context, target, this.children[1]);
            boolean conversion = EOgnlRuntime.shouldConvertNumericTypes(context);
            StringBuilder result = new StringBuilder("(");
            if (conversion) {
                result.append(this.getComparisonFunction()).append("( ($w) (");
            }
            result.append(EOgnlRuntime.getChildSource(context, target, this.children[0], conversion)).append(" ");
            if (conversion) {
                result.append("), ($w) ");
            } else {
                result.append(this.getExpressionOperator(0));
            }
            result.append("").append(EOgnlRuntime.getChildSource(context, target, this.children[1], conversion));
            if (conversion) {
                result.append(")");
            }
            context.setCurrentType(Boolean.TYPE);
            result.append(")");
            return result.toString();
        }
        catch (NullPointerException e) {
            throw new UnsupportedCompilationException("evaluation resulted in null expression.");
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
    }
}

