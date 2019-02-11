/*
 * Decompiled with CFR 0.139.
 */
package eognl;

public interface OgnlParserConstants {
    public static final int EOF = 0;
    public static final int IDENT = 64;
    public static final int LETTER = 65;
    public static final int DIGIT = 66;
    public static final int DYNAMIC_SUBSCRIPT = 67;
    public static final int ESC = 71;
    public static final int CHAR_LITERAL = 73;
    public static final int BACK_CHAR_ESC = 74;
    public static final int BACK_CHAR_LITERAL = 76;
    public static final int STRING_ESC = 77;
    public static final int STRING_LITERAL = 79;
    public static final int INT_LITERAL = 80;
    public static final int FLT_LITERAL = 81;
    public static final int DEC_FLT = 82;
    public static final int DEC_DIGITS = 83;
    public static final int EXPONENT = 84;
    public static final int FLT_SUFF = 85;
    public static final int DEFAULT = 0;
    public static final int WithinCharLiteral = 1;
    public static final int WithinBackCharLiteral = 2;
    public static final int WithinStringLiteral = 3;
    public static final String[] tokenImage = new String[]{"<EOF>", "\",\"", "\"=\"", "\"?\"", "\":\"", "\"||\"", "\"or\"", "\"&&\"", "\"and\"", "\"|\"", "\"bor\"", "\"^\"", "\"xor\"", "\"&\"", "\"band\"", "\"==\"", "\"eq\"", "\"!=\"", "\"neq\"", "\"<\"", "\"lt\"", "\">\"", "\"gt\"", "\"<=\"", "\"lte\"", "\">=\"", "\"gte\"", "\"in\"", "\"not\"", "\"<<\"", "\"shl\"", "\">>\"", "\"shr\"", "\">>>\"", "\"ushr\"", "\"+\"", "\"-\"", "\"*\"", "\"/\"", "\"%\"", "\"~\"", "\"!\"", "\"instanceof\"", "\".\"", "\"(\"", "\")\"", "\"true\"", "\"false\"", "\"null\"", "\"#this\"", "\"#root\"", "\"#\"", "\"[\"", "\"]\"", "\"{\"", "\"}\"", "\"@\"", "\"new\"", "\"$\"", "\" \"", "\"\\t\"", "\"\\f\"", "\"\\r\"", "\"\\n\"", "<IDENT>", "<LETTER>", "<DIGIT>", "<DYNAMIC_SUBSCRIPT>", "\"`\"", "\"\\'\"", "\"\\\"\"", "<ESC>", "<token of kind 72>", "\"\\'\"", "<BACK_CHAR_ESC>", "<token of kind 75>", "\"`\"", "<STRING_ESC>", "<token of kind 78>", "\"\\\"\"", "<INT_LITERAL>", "<FLT_LITERAL>", "<DEC_FLT>", "<DEC_DIGITS>", "<EXPONENT>", "<FLT_SUFF>"};
}

