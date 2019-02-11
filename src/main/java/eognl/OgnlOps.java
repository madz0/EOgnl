/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.EOgnlRuntime;
import eognl.ElementsAccessor;
import eognl.NumericTypes;
import eognl.OgnlContext;
import eognl.OgnlException;
import eognl.enhance.UnsupportedCompilationException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Enumeration;

public abstract class OgnlOps
implements NumericTypes {
    public static int compareWithConversion(Object v1, Object v2) {
        int result;
        if (v1 == v2) {
            result = 0;
        } else {
            int t1 = OgnlOps.getNumericType(v1);
            int t2 = OgnlOps.getNumericType(v2);
            int type = OgnlOps.getNumericType(t1, t2, true);
            switch (type) {
                case 6: {
                    result = OgnlOps.bigIntValue(v1).compareTo(OgnlOps.bigIntValue(v2));
                    break;
                }
                case 9: {
                    result = OgnlOps.bigDecValue(v1).compareTo(OgnlOps.bigDecValue(v2));
                    break;
                }
                case 10: {
                    if (t1 == 10 && t2 == 10) {
                        if (v1 instanceof Comparable && v1.getClass().isAssignableFrom(v2.getClass())) {
                            result = ((Comparable)v1).compareTo(v2);
                            break;
                        }
                        throw new IllegalArgumentException("invalid comparison: " + v1.getClass().getName() + " and " + v2.getClass().getName());
                    }
                }
                case 7: 
                case 8: {
                    double dv1 = OgnlOps.doubleValue(v1);
                    double dv2 = OgnlOps.doubleValue(v2);
                    return dv1 == dv2 ? 0 : (dv1 < dv2 ? -1 : 1);
                }
                default: {
                    long lv1 = OgnlOps.longValue(v1);
                    long lv2 = OgnlOps.longValue(v2);
                    return lv1 == lv2 ? 0 : (lv1 < lv2 ? -1 : 1);
                }
            }
        }
        return result;
    }

    public static boolean isEqual(Object object1, Object object2) {
        boolean result = false;
        if (object1 == object2) {
            result = true;
        } else if (object1 != null && object1.getClass().isArray()) {
            if (object2 != null && object2.getClass().isArray() && object2.getClass() == object1.getClass()) {
                boolean bl = result = Array.getLength(object1) == Array.getLength(object2);
                if (result) {
                    int icount = Array.getLength(object1);
                    for (int i = 0; result && i < icount; ++i) {
                        result = OgnlOps.isEqual(Array.get(object1, i), Array.get(object2, i));
                    }
                }
            }
        } else {
            result = object1 != null && object2 != null && (object1.equals(object2) || OgnlOps.compareWithConversion(object1, object2) == 0);
        }
        return result;
    }

    public static boolean booleanValue(boolean value) {
        return value;
    }

    public static boolean booleanValue(int value) {
        return value > 0;
    }

    public static boolean booleanValue(float value) {
        return value > 0.0f;
    }

    public static boolean booleanValue(long value) {
        return value > 0L;
    }

    public static boolean booleanValue(double value) {
        return value > 0.0;
    }

    public static boolean booleanValue(Object value) {
        if (value == null) {
            return false;
        }
        Class<?> c = value.getClass();
        if (c == Boolean.class) {
            return (Boolean)value;
        }
        if (c == Character.class) {
            return ((Character)value).charValue() != '\u0000';
        }
        return !(value instanceof Number) || ((Number)value).doubleValue() != 0.0;
    }

    public static long longValue(Object value) {
        if (value == null) {
            return 0L;
        }
        Class<?> c = value.getClass();
        if (c.getSuperclass() == Number.class) {
            return ((Number)value).longValue();
        }
        if (c == Boolean.class) {
            return (Boolean)value != false ? 1 : 0;
        }
        if (c == Character.class) {
            return ((Character)value).charValue();
        }
        return Long.parseLong(OgnlOps.stringValue(value, true));
    }

    public static double doubleValue(Object value) {
        if (value == null) {
            return 0.0;
        }
        Class<?> c = value.getClass();
        if (c.getSuperclass() == Number.class) {
            return ((Number)value).doubleValue();
        }
        if (c == Boolean.class) {
            return ((Boolean) value).booleanValue() ? 1 : 0;
        }
        if (c == Character.class) {
            return ((Character)value).charValue();
        }
        String s = OgnlOps.stringValue(value, true);
        return s.length() == 0 ? 0.0 : Double.parseDouble(s);
    }

    public static BigInteger bigIntValue(Object value) {
        if (value == null) {
            return BigInteger.valueOf(0L);
        }
        Class<?> c = value.getClass();
        if (c == BigInteger.class) {
            return (BigInteger)value;
        }
        if (c == BigDecimal.class) {
            return ((BigDecimal)value).toBigInteger();
        }
        if (c.getSuperclass() == Number.class) {
            return BigInteger.valueOf(((Number)value).longValue());
        }
        if (c == Boolean.class) {
            return BigInteger.valueOf((Boolean)value != false ? 1 : 0);
        }
        if (c == Character.class) {
            return BigInteger.valueOf(((Character)value).charValue());
        }
        return new BigInteger(OgnlOps.stringValue(value, true));
    }

    public static BigDecimal bigDecValue(Object value) {
        if (value == null) {
            return BigDecimal.valueOf(0L);
        }
        Class<?> c = value.getClass();
        if (c == BigDecimal.class) {
            return (BigDecimal)value;
        }
        if (c == BigInteger.class) {
            return new BigDecimal((BigInteger)value);
        }
        if (c == Boolean.class) {
            return BigDecimal.valueOf((Boolean)value != false ? 1 : 0);
        }
        if (c == Character.class) {
            return BigDecimal.valueOf(((Character)value).charValue());
        }
        return new BigDecimal(OgnlOps.stringValue(value, true));
    }

    public static String stringValue(Object value, boolean trim) {
        String result;
        if (value == null) {
            result = EOgnlRuntime.NULL_STRING;
        } else {
            result = value.toString();
            if (trim) {
                result = result.trim();
            }
        }
        return result;
    }

    public static String stringValue(Object value) {
        return OgnlOps.stringValue(value, false);
    }

    public static int getNumericType(Object value) {
        if (value != null) {
            Class<?> c = value.getClass();
            if (c == Integer.class) {
                return 4;
            }
            if (c == Double.class) {
                return 8;
            }
            if (c == Boolean.class) {
                return 0;
            }
            if (c == Byte.class) {
                return 1;
            }
            if (c == Character.class) {
                return 2;
            }
            if (c == Short.class) {
                return 3;
            }
            if (c == Long.class) {
                return 5;
            }
            if (c == Float.class) {
                return 7;
            }
            if (c == BigInteger.class) {
                return 6;
            }
            if (c == BigDecimal.class) {
                return 9;
            }
        }
        return 10;
    }

    public static Object toArray(char value, Class<?> toType) throws OgnlException {
        return OgnlOps.toArray(Character.valueOf(value), toType);
    }

    public static Object toArray(byte value, Class<?> toType) throws OgnlException {
        return OgnlOps.toArray((Object)value, toType);
    }

    public static Object toArray(int value, Class<?> toType) throws OgnlException {
        return OgnlOps.toArray((Object)value, toType);
    }

    public static Object toArray(long value, Class<?> toType) throws OgnlException {
        return OgnlOps.toArray((Object)value, toType);
    }

    public static Object toArray(float value, Class<?> toType) throws OgnlException {
        return OgnlOps.toArray(Float.valueOf(value), toType);
    }

    public static Object toArray(double value, Class<?> toType) throws OgnlException {
        return OgnlOps.toArray((Object)value, toType);
    }

    public static Object toArray(boolean value, Class<?> toType) throws OgnlException {
        return OgnlOps.toArray((Object)value, toType);
    }

    public static <T> Object convertValue(char value, Class<T> toType) throws OgnlException {
        return OgnlOps.convertValue(Character.valueOf(value), toType);
    }

    public static <T> Object convertValue(byte value, Class<T> toType) throws OgnlException {
        return OgnlOps.convertValue((Object)value, toType);
    }

    public static <T> Object convertValue(int value, Class<T> toType) throws OgnlException {
        return OgnlOps.convertValue((Object)value, toType);
    }

    public static <T> Object convertValue(long value, Class<T> toType) throws OgnlException {
        return OgnlOps.convertValue((Object)value, toType);
    }

    public static <T> Object convertValue(float value, Class<T> toType) throws OgnlException {
        return OgnlOps.convertValue(Float.valueOf(value), toType);
    }

    public static <T> Object convertValue(double value, Class<T> toType) throws OgnlException {
        return OgnlOps.convertValue((Object)value, toType);
    }

    public static <T> Object convertValue(boolean value, Class<T> toType) throws OgnlException {
        return OgnlOps.convertValue((Object)value, toType);
    }

    public static <T> Object convertValue(char value, Class<T> toType, boolean preventNull) throws OgnlException {
        return OgnlOps.convertValue(Character.valueOf(value), toType, preventNull);
    }

    public static <T> Object convertValue(byte value, Class<T> toType, boolean preventNull) throws OgnlException {
        return OgnlOps.convertValue((Object)value, toType, preventNull);
    }

    public static <T> Object convertValue(int value, Class<T> toType, boolean preventNull) throws OgnlException {
        return OgnlOps.convertValue((Object)value, toType, preventNull);
    }

    public static <T> Object convertValue(long value, Class<T> toType, boolean preventNull) throws OgnlException {
        return OgnlOps.convertValue((Object)value, toType, preventNull);
    }

    public static <T> Object convertValue(float value, Class<T> toType, boolean preventNull) throws OgnlException {
        return OgnlOps.convertValue(new Float(value), toType, preventNull);
    }

    public static <T> Object convertValue(double value, Class<T> toType, boolean preventNull) throws OgnlException {
        return OgnlOps.convertValue(new Double(value), toType, preventNull);
    }

    public static <T> Object convertValue(boolean value, Class<T> toType, boolean preventNull) throws OgnlException {
        return OgnlOps.convertValue((Object)value, toType, preventNull);
    }

    public static Object toArray(char value, Class<?> toType, boolean preventNull) throws OgnlException {
        return OgnlOps.toArray(Character.valueOf(value), toType, preventNull);
    }

    public static Object toArray(byte value, Class<?> toType, boolean preventNull) throws OgnlException {
        return OgnlOps.toArray((Object)value, toType, preventNull);
    }

    public static Object toArray(int value, Class<?> toType, boolean preventNull) throws OgnlException {
        return OgnlOps.toArray((Object)value, toType, preventNull);
    }

    public static Object toArray(long value, Class<?> toType, boolean preventNull) throws OgnlException {
        return OgnlOps.toArray((Object)value, toType, preventNull);
    }

    public static Object toArray(float value, Class<?> toType, boolean preventNull) throws OgnlException {
        return OgnlOps.toArray(Float.valueOf(value), toType, preventNull);
    }

    public static Object toArray(double value, Class<?> toType, boolean preventNull) throws OgnlException {
        return OgnlOps.toArray((Object)value, toType, preventNull);
    }

    public static Object toArray(boolean value, Class<?> toType, boolean preventNull) throws OgnlException {
        return OgnlOps.toArray((Object)value, toType, preventNull);
    }

    public static Object convertValue(Object value, Class<?> toType) {
        return OgnlOps.convertValue(value, toType, false);
    }

    public static Object toArray(Object value, Class<?> toType) throws OgnlException {
        return OgnlOps.toArray(value, toType, false);
    }

    public static Object toArray(Object value, Class<?> toType, boolean preventNulls) throws OgnlException {
        if (value == null) {
            return null;
        }
        Class<?> aClass = value.getClass();
        if (aClass.isArray() && toType.isAssignableFrom(aClass.getComponentType())) {
            return value;
        }
        if (!aClass.isArray()) {
            if (toType == Character.TYPE) {
                return OgnlOps.stringValue(value).toCharArray();
            }
            Object arr = Array.newInstance(toType, 1);
            Array.set(arr, 0, OgnlOps.convertValue(value, toType, preventNulls));
            return arr;
        }
        Object result = Array.newInstance(toType, Array.getLength(value));
        int icount = Array.getLength(value);
        for (int i = 0; i < icount; ++i) {
            Array.set(result, i, OgnlOps.convertValue(Array.get(value, i), toType));
        }
        if (result == null && preventNulls) {
            return value;
        }
        return result;
    }

    public static <T> Object convertValue(Object value, Class<T> toType, boolean preventNulls) {
        Object result = null;
        if (value != null && toType.isAssignableFrom(value.getClass())) {
            return value;
        }
        if (value != null) {
            boolean classIsArray = value.getClass().isArray();
            boolean toTypeIsArray = toType.isArray();
            if (classIsArray && toTypeIsArray) {
                Class<?> componentType = toType.getComponentType();
                result = Array.newInstance(componentType, Array.getLength(value));
                int icount = Array.getLength(value);
                for (int i = 0; i < icount; ++i) {
                    Array.set(result, i, OgnlOps.convertValue(Array.get(value, i), componentType));
                }
            } else {
                if (classIsArray && !toTypeIsArray) {
                    return OgnlOps.convertValue(Array.get(value, 0), toType);
                }
                if (toTypeIsArray) {
                    if (toType.getComponentType() == Character.TYPE) {
                        result = OgnlOps.stringValue(value).toCharArray();
                    } else if (toType.getComponentType() == Object.class) {
                        return new Object[]{value};
                    }
                } else if (toType == Integer.class || toType == Integer.TYPE) {
                    result = (int)OgnlOps.longValue(value);
                } else if (toType == Double.class || toType == Double.TYPE) {
                    result = OgnlOps.doubleValue(value);
                } else if (toType == Boolean.class || toType == Boolean.TYPE) {
                    result = OgnlOps.booleanValue(value) ? Boolean.TRUE : Boolean.FALSE;
                } else if (toType == Byte.class || toType == Byte.TYPE) {
                    result = (byte)OgnlOps.longValue(value);
                } else if (toType == Character.class || toType == Character.TYPE) {
                    result = Character.valueOf((char)OgnlOps.longValue(value));
                } else if (toType == Short.class || toType == Short.TYPE) {
                    result = (short)OgnlOps.longValue(value);
                } else if (toType == Long.class || toType == Long.TYPE) {
                    result = OgnlOps.longValue(value);
                } else if (toType == Float.class || toType == Float.TYPE) {
                    result = new Float(OgnlOps.doubleValue(value));
                } else if (toType == BigInteger.class) {
                    result = OgnlOps.bigIntValue(value);
                } else if (toType == BigDecimal.class) {
                    result = OgnlOps.bigDecValue(value);
                } else if (toType == String.class) {
                    result = OgnlOps.stringValue(value);
                }
            }
        } else if (toType.isPrimitive()) {
            result = EOgnlRuntime.getPrimitiveDefaultValue(toType);
        } else if (preventNulls && toType == Boolean.class) {
            result = Boolean.FALSE;
        } else if (preventNulls && Number.class.isAssignableFrom(toType)) {
            result = EOgnlRuntime.getNumericDefaultValue(toType);
        }
        if (result == null && preventNulls) {
            return value;
        }
        if (value != null && result == null) {
            throw new IllegalArgumentException("Unable to convert type " + value.getClass().getName() + " of " + value + " to type of " + toType.getName());
        }
        return result;
    }

    public static int getIntValue(Object value) {
        block4 : {
            try {
                if (value != null) break block4;
                return -1;
            }
            catch (Throwable t) {
                throw new RuntimeException("Error converting " + value + " to integer:", t);
            }
        }
        if (Number.class.isInstance(value)) {
            return ((Number)value).intValue();
        }
        String str = String.class.isInstance(value) ? (String)value : value.toString();
        return Integer.parseInt(str);
    }

    public static int getNumericType(Object v1, Object v2) {
        return OgnlOps.getNumericType(v1, v2, false);
    }

    public static int getNumericType(int t1, int t2, boolean canBeNonNumeric) {
        if (t1 == t2) {
            return t1;
        }
        if (canBeNonNumeric && (t1 == 10 || t2 == 10 || t1 == 2 || t2 == 2)) {
            return 10;
        }
        if (t1 == 10) {
            t1 = 8;
        }
        if (t2 == 10) {
            t2 = 8;
        }
        if (t1 >= 7) {
            if (t2 >= 7) {
                return Math.max(t1, t2);
            }
            if (t2 < 4) {
                return t1;
            }
            if (t2 == 6) {
                return 9;
            }
            return Math.max(8, t1);
        }
        if (t2 >= 7) {
            if (t1 < 4) {
                return t2;
            }
            if (t1 == 6) {
                return 9;
            }
            return Math.max(8, t2);
        }
        return Math.max(t1, t2);
    }

    public static int getNumericType(Object v1, Object v2, boolean canBeNonNumeric) {
        return OgnlOps.getNumericType(OgnlOps.getNumericType(v1), OgnlOps.getNumericType(v2), canBeNonNumeric);
    }

    public static Number newInteger(int type, long value) {
        switch (type) {
            case 0: 
            case 2: 
            case 4: {
                return (int)value;
            }
            case 7: {
                if ((long)value == value) {
                    return Float.valueOf(value);
                }
            }
            case 8: {
                if ((long)value == value) {
                    return (double)value;
                }
            }
            case 5: {
                return value;
            }
            case 1: {
                return (byte)value;
            }
            case 3: {
                return (short)value;
            }
        }
        return BigInteger.valueOf(value);
    }

    public static Number newReal(int type, double value) {
        if (type == 7) {
            return Float.valueOf((float)value);
        }
        return value;
    }

    public static Object binaryOr(Object v1, Object v2) {
        int type = OgnlOps.getNumericType(v1, v2);
        if (type == 6 || type == 9) {
            return OgnlOps.bigIntValue(v1).or(OgnlOps.bigIntValue(v2));
        }
        return OgnlOps.newInteger(type, OgnlOps.longValue(v1) | OgnlOps.longValue(v2));
    }

    public static Object binaryXor(Object v1, Object v2) {
        int type = OgnlOps.getNumericType(v1, v2);
        if (type == 6 || type == 9) {
            return OgnlOps.bigIntValue(v1).xor(OgnlOps.bigIntValue(v2));
        }
        return OgnlOps.newInteger(type, OgnlOps.longValue(v1) ^ OgnlOps.longValue(v2));
    }

    public static Object binaryAnd(Object v1, Object v2) {
        int type = OgnlOps.getNumericType(v1, v2);
        if (type == 6 || type == 9) {
            return OgnlOps.bigIntValue(v1).and(OgnlOps.bigIntValue(v2));
        }
        return OgnlOps.newInteger(type, OgnlOps.longValue(v1) & OgnlOps.longValue(v2));
    }

    public static boolean equal(Object v1, Object v2) {
        if (v1 == null) {
            return v2 == null;
        }
        if (v1 == v2 || OgnlOps.isEqual(v1, v2)) {
            return true;
        }
        if (v1 instanceof Number && v2 instanceof Number) {
            return ((Number)v1).doubleValue() == ((Number)v2).doubleValue();
        }
        return false;
    }

    public static boolean less(Object v1, Object v2) {
        return OgnlOps.compareWithConversion(v1, v2) < 0;
    }

    public static boolean greater(Object v1, Object v2) {
        return OgnlOps.compareWithConversion(v1, v2) > 0;
    }

    public static boolean in(OgnlContext context, Object v1, Object v2) throws OgnlException {
        if (v2 == null) {
            return false;
        }
        ElementsAccessor elementsAccessor = context.getElementsAccessor(EOgnlRuntime.getTargetClass(v2));
        Enumeration<?> e = elementsAccessor.getElements(v2);
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            if (!OgnlOps.equal(v1, o)) continue;
            return true;
        }
        return false;
    }

    public static Object shiftLeft(Object v1, Object v2) {
        int type = OgnlOps.getNumericType(v1);
        if (type == 6 || type == 9) {
            return OgnlOps.bigIntValue(v1).shiftLeft((int)OgnlOps.longValue(v2));
        }
        return OgnlOps.newInteger(type, OgnlOps.longValue(v1) << (int)OgnlOps.longValue(v2));
    }

    public static Object shiftRight(Object v1, Object v2) {
        int type = OgnlOps.getNumericType(v1);
        if (type == 6 || type == 9) {
            return OgnlOps.bigIntValue(v1).shiftRight((int)OgnlOps.longValue(v2));
        }
        return OgnlOps.newInteger(type, OgnlOps.longValue(v1) >> (int)OgnlOps.longValue(v2));
    }

    public static Object unsignedShiftRight(Object v1, Object v2) {
        int type = OgnlOps.getNumericType(v1);
        if (type == 6 || type == 9) {
            return OgnlOps.bigIntValue(v1).shiftRight((int)OgnlOps.longValue(v2));
        }
        if (type <= 4) {
            return OgnlOps.newInteger(4, (int)OgnlOps.longValue(v1) >>> (int)OgnlOps.longValue(v2));
        }
        return OgnlOps.newInteger(type, OgnlOps.longValue(v1) >>> (int)OgnlOps.longValue(v2));
    }

    public static Object add(Object v1, Object v2) {
        int type = OgnlOps.getNumericType(v1, v2, true);
        switch (type) {
            case 6: {
                return OgnlOps.bigIntValue(v1).add(OgnlOps.bigIntValue(v2));
            }
            case 9: {
                return OgnlOps.bigDecValue(v1).add(OgnlOps.bigDecValue(v2));
            }
            case 7: 
            case 8: {
                return OgnlOps.newReal(type, OgnlOps.doubleValue(v1) + OgnlOps.doubleValue(v2));
            }
            case 10: {
                int t1 = OgnlOps.getNumericType(v1);
                int t2 = OgnlOps.getNumericType(v2);
                if (t1 != 10 && v2 == null || t2 != 10 && v1 == null) {
                    throw new NullPointerException("Can't add values " + v1 + " , " + v2);
                }
                return String.valueOf(OgnlOps.stringValue(v1)) + OgnlOps.stringValue(v2);
            }
        }
        return OgnlOps.newInteger(type, OgnlOps.longValue(v1) + OgnlOps.longValue(v2));
    }

    public static Object subtract(Object v1, Object v2) {
        int type = OgnlOps.getNumericType(v1, v2);
        switch (type) {
            case 6: {
                return OgnlOps.bigIntValue(v1).subtract(OgnlOps.bigIntValue(v2));
            }
            case 9: {
                return OgnlOps.bigDecValue(v1).subtract(OgnlOps.bigDecValue(v2));
            }
            case 7: 
            case 8: {
                return OgnlOps.newReal(type, OgnlOps.doubleValue(v1) - OgnlOps.doubleValue(v2));
            }
        }
        return OgnlOps.newInteger(type, OgnlOps.longValue(v1) - OgnlOps.longValue(v2));
    }

    public static Object multiply(Object v1, Object v2) {
        int type = OgnlOps.getNumericType(v1, v2);
        switch (type) {
            case 6: {
                return OgnlOps.bigIntValue(v1).multiply(OgnlOps.bigIntValue(v2));
            }
            case 9: {
                return OgnlOps.bigDecValue(v1).multiply(OgnlOps.bigDecValue(v2));
            }
            case 7: 
            case 8: {
                return OgnlOps.newReal(type, OgnlOps.doubleValue(v1) * OgnlOps.doubleValue(v2));
            }
        }
        return OgnlOps.newInteger(type, OgnlOps.longValue(v1) * OgnlOps.longValue(v2));
    }

    public static Object divide(Object v1, Object v2) {
        int type = OgnlOps.getNumericType(v1, v2);
        switch (type) {
            case 6: {
                return OgnlOps.bigIntValue(v1).divide(OgnlOps.bigIntValue(v2));
            }
            case 9: {
                return OgnlOps.bigDecValue(v1).divide(OgnlOps.bigDecValue(v2), 6);
            }
            case 7: 
            case 8: {
                return OgnlOps.newReal(type, OgnlOps.doubleValue(v1) / OgnlOps.doubleValue(v2));
            }
        }
        return OgnlOps.newInteger(type, OgnlOps.longValue(v1) / OgnlOps.longValue(v2));
    }

    public static Object remainder(Object v1, Object v2) {
        int type = OgnlOps.getNumericType(v1, v2);
        switch (type) {
            case 6: 
            case 9: {
                return OgnlOps.bigIntValue(v1).remainder(OgnlOps.bigIntValue(v2));
            }
        }
        return OgnlOps.newInteger(type, OgnlOps.longValue(v1) % OgnlOps.longValue(v2));
    }

    public static Object negate(Object value) {
        int type = OgnlOps.getNumericType(value);
        switch (type) {
            case 6: {
                return OgnlOps.bigIntValue(value).negate();
            }
            case 9: {
                return OgnlOps.bigDecValue(value).negate();
            }
            case 7: 
            case 8: {
                return OgnlOps.newReal(type, -OgnlOps.doubleValue(value));
            }
        }
        return OgnlOps.newInteger(type, -OgnlOps.longValue(value));
    }

    public static Object bitNegate(Object value) {
        int type = OgnlOps.getNumericType(value);
        switch (type) {
            case 6: 
            case 9: {
                return OgnlOps.bigIntValue(value).not();
            }
        }
        return OgnlOps.newInteger(type, OgnlOps.longValue(value) ^ -1L);
    }

    public static String getEscapeString(String value) {
        StringBuilder result = new StringBuilder();
        int length = value.length();
        for (int i = 0; i < length; ++i) {
            result.append(OgnlOps.getEscapedChar(value.charAt(i)));
        }
        return result.toString();
    }

    public static String getEscapedChar(char ch) {
        String result;
        switch (ch) {
            case '\b': {
                result = "\b";
                break;
            }
            case '\t': {
                result = "\\t";
                break;
            }
            case '\n': {
                result = "\\n";
                break;
            }
            case '\f': {
                result = "\\f";
                break;
            }
            case '\r': {
                result = "\\r";
                break;
            }
            case '\"': {
                result = "\\\"";
                break;
            }
            case '\'': {
                result = "\\'";
                break;
            }
            case '\\': {
                result = "\\\\";
                break;
            }
            default: {
                if (Character.isISOControl(ch)) {
                    String hc = Integer.toString(ch, 16);
                    int hcl = hc.length();
                    result = "\\u";
                    if (hcl < 4) {
                        result = hcl == 3 ? String.valueOf(result) + "0" : (hcl == 2 ? String.valueOf(result) + "00" : String.valueOf(result) + "000");
                    }
                    result = String.valueOf(result) + hc;
                    break;
                }
                result = String.valueOf(ch);
            }
        }
        return result;
    }

    public static Object returnValue(Object ignore, Object returnValue) {
        return returnValue;
    }

    public static RuntimeException castToRuntime(Throwable t) {
        if (RuntimeException.class.isInstance(t)) {
            return (RuntimeException)t;
        }
        if (OgnlException.class.isInstance(t)) {
            throw new UnsupportedCompilationException("Error evluating expression: " + t.getMessage(), t);
        }
        return new RuntimeException(t);
    }
}

