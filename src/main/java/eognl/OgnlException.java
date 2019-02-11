/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.Evaluation;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;

public class OgnlException
extends Exception {
    private static final long serialVersionUID = -842845048743721078L;
    static Method initCause;
    private Evaluation evaluation;
    private Throwable reason;

    static {
        try {
            initCause = OgnlException.class.getMethod("initCause", Throwable.class);
        }
        catch (NoSuchMethodException noSuchMethodException) {
            // empty catch block
        }
    }

    public OgnlException() {
        this(null, null);
    }

    public OgnlException(String msg) {
        this(msg, null);
    }

    public OgnlException(String msg, Throwable reason) {
        super(msg);
        this.reason = reason;
        if (initCause != null) {
            try {
                initCause.invoke(this, reason);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public Throwable getReason() {
        return this.reason;
    }

    public Evaluation getEvaluation() {
        return this.evaluation;
    }

    public void setEvaluation(Evaluation value) {
        this.evaluation = value;
    }

    @Override
    public String toString() {
        if (this.reason == null) {
            return super.toString();
        }
        return String.valueOf(super.toString()) + " [" + this.reason + "]";
    }

    @Override
    public void printStackTrace() {
        this.printStackTrace(System.err);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void printStackTrace(PrintStream s) {
        PrintStream printStream = s;
        synchronized (printStream) {
            super.printStackTrace(s);
            if (this.reason != null) {
                s.println("/-- Encapsulated exception ------------\\");
                this.reason.printStackTrace(s);
                s.println("\\--------------------------------------/");
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void printStackTrace(PrintWriter s) {
        PrintWriter printWriter = s;
        synchronized (printWriter) {
            super.printStackTrace(s);
            if (this.reason != null) {
                s.println("/-- Encapsulated exception ------------\\");
                this.reason.printStackTrace(s);
                s.println("\\--------------------------------------/");
            }
        }
    }
}

