/*
 * Decompiled with CFR 0.139.
 */
package eognl;

import eognl.ClassResolver;
import eognl.DefaultClassResolver;
import eognl.DefaultMemberAccess;
import eognl.DefaultTypeConverter;
import eognl.ElementsAccessor;
import eognl.Evaluation;
import eognl.MemberAccess;
import eognl.MethodAccessor;
import eognl.Node;
import eognl.NullHandler;
import eognl.OgnlCache;
import eognl.OgnlException;
import eognl.OgnlOps;
import eognl.PropertyAccessor;
import eognl.TypeConverter;
import eognl.enhance.LocalReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public abstract class OgnlContext
implements Map<String, Object> {
    public static final int CONF_INIT_NULLS = 1;
    public static final int CONF_AUTO_EXPAND = 2;
    public static final int CONF_INIT_UNKNOWN = 4;
    public static final int CONF_IGNORE_FIRST_UNKNOWN = 8;
    public static final int CONF_ALWAYS_IGNORE_FIRST = 16;
    public static final int CONF_UNKNOWN_TO_LITERAL = 32;
    public static final int CONF_CAST_PRIMITIVES = 64;
    public static final int FIRST_LEVEL = 1;
    public static final String EXPANDED_ARRAY_KEY = "sele-c gs@in+=ruti!mefags";
    public static final String EXPRESSION_SET = "HsaSet%un!tZ^imefl!ags";
    public static final String INIT_NULLS_KEY = "ShOudH^and%Len#ul4Cain";
    public static final String INIT_UNKNOWN_NULLS_KEY = "SZudINIi#^n{[knwn!en#ul4Cain";
    public static final String CURRENT_INDEX_KEY = "H0ldZzZz2@Id*4Cain";
    public static final String EXPAND_SIZE_KEY = "NaIndIdIdx@Id*4Lis";
    public static final String GENERIC_PREFIX_KEY = "R3PpeZzenGnr#iK?";
    public static final String ARRAR_SOURCE_PREFIX_KEY = "R3PpeZzenArrAayS3etEEer#iK?";
    public static final String PARAMETERIZED_ROOT_TYPE_KEY = "PaArmRoo#eZInfo&r?";
    public static final String IGNORE_FIRST_UNKNOWN_KEY = "IgG%boZer7&is94E3";
    public static final String ALWAYS_IGNORE_FIRST_KEY = "AlwaAa^Ay)i)zIgG%boZElA";
    public static final String UNKNOWN_TO_LITERAL_KEY = "UuUkwWn2LlLte*(9l";
    public static final String CAST_PRIMITIVES_KEY = "H^7yCa3TtH***";
    public static final String OBJECT_CONSTRUCTOR_KEY = OgnlContext.class.getName()+"_OBJECT_CONSTRUCTOR_KEY"; 
    public static final String CONTEXT_CONTEXT_KEY = "context";
    public static final String ROOT_CONTEXT_KEY = "root";
    public static final String THIS_CONTEXT_KEY = "this";
    public static final String TRACE_EVALUATIONS_CONTEXT_KEY = "_traceEvaluations";
    public static final String LAST_EVALUATION_CONTEXT_KEY = "_lastEvaluation";
    public static final String KEEP_LAST_EVALUATION_CONTEXT_KEY = "_keepLastEvaluation";
    public static final String CLASS_RESOLVER_CONTEXT_KEY = "_classResolver";
    public static final String TYPE_CONVERTER_CONTEXT_KEY = "_typeConverter";
    public static final String MEMBER_ACCESS_CONTEXT_KEY = "_memberAccess";
    private static boolean defaultTraceEvaluations = false;
    private static boolean defaultKeepLastEvaluation = false;
    public static final DefaultClassResolver DEFAULT_CLASS_RESOLVER = new DefaultClassResolver();
    public static final TypeConverter DEFAULT_TYPE_CONVERTER = new DefaultTypeConverter();
    public static final MemberAccess DEFAULT_MEMBER_ACCESS = new DefaultMemberAccess(false);
    private static final Set<String> RESERVED_KEYS = new HashSet<String>(11);
    private Object root;
    private Object currentObject;
    private Node currentNode;
    private boolean traceEvaluations = defaultTraceEvaluations;
    private Evaluation rootEvaluation;
    private Evaluation currentEvaluation;
    private Evaluation lastEvaluation;
    private boolean keepLastEvaluation = defaultKeepLastEvaluation;
    private Map<String, Object> values = new HashMap<String, Object>(23);
    private ClassResolver classResolver = DEFAULT_CLASS_RESOLVER;
    private TypeConverter typeConverter = DEFAULT_TYPE_CONVERTER;
    private MemberAccess memberAccess = DEFAULT_MEMBER_ACCESS;
    protected OgnlCache cache = new OgnlCache();
    private Stack<Class<?>> typeStack = new Stack();
    private Stack<Class<?>> accessorStack = new Stack();
    private int localReferenceCounter = 0;
    private Map<String, LocalReference> localReferenceMap = null;

    static {
        RESERVED_KEYS.add(CONTEXT_CONTEXT_KEY);
        RESERVED_KEYS.add(ROOT_CONTEXT_KEY);
        RESERVED_KEYS.add(THIS_CONTEXT_KEY);
        RESERVED_KEYS.add(TRACE_EVALUATIONS_CONTEXT_KEY);
        RESERVED_KEYS.add(LAST_EVALUATION_CONTEXT_KEY);
        RESERVED_KEYS.add(KEEP_LAST_EVALUATION_CONTEXT_KEY);
        RESERVED_KEYS.add(CLASS_RESOLVER_CONTEXT_KEY);
        RESERVED_KEYS.add(TYPE_CONVERTER_CONTEXT_KEY);
        RESERVED_KEYS.add(MEMBER_ACCESS_CONTEXT_KEY);
        try {
            String s = System.getProperty("ognl.traceEvaluations");
            if (s != null) {
                defaultTraceEvaluations = Boolean.valueOf(s.trim());
            }
            if ((s = System.getProperty("ognl.keepLastEvaluation")) != null) {
                defaultKeepLastEvaluation = Boolean.valueOf(s.trim());
            }
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
    }

    public void setMethodAccessor(Class<?> clazz, MethodAccessor accessor) {
        this.cache.setMethodAccessor(clazz, accessor);
    }

    public MethodAccessor getMethodAccessor(Class<?> clazz) throws OgnlException {
        return this.cache.getMethodAccessor(clazz);
    }

    public void setPropertyAccessor(Class<?> clazz, PropertyAccessor accessor) {
        this.cache.setPropertyAccessor(clazz, accessor);
    }

    public PropertyAccessor getPropertyAccessor(Class<?> clazz) throws OgnlException {
        return this.cache.getPropertyAccessor(clazz);
    }

    public ElementsAccessor getElementsAccessor(Class<?> clazz) throws OgnlException {
        return this.cache.getElementsAccessor(clazz);
    }

    public void setElementsAccessor(Class<?> clazz, ElementsAccessor accessor) {
        this.cache.setElementsAccessor(clazz, accessor);
    }

    public NullHandler getNullHandler(Class<?> clazz) throws OgnlException {
        return this.cache.getNullHandler(clazz);
    }

    public void setNullHandler(Class<?> clazz, NullHandler handler) {
        this.cache.setNullHandler(clazz, handler);
    }

    public OgnlContext() {
    }

    public OgnlContext(ClassResolver classResolver, TypeConverter typeConverter, MemberAccess memberAccess) {
        this();
        if (classResolver != null) {
            this.classResolver = classResolver;
        }
        if (typeConverter != null) {
            this.typeConverter = typeConverter;
        }
        if (memberAccess != null) {
            this.memberAccess = memberAccess;
        }
    }

    public OgnlContext(Map<String, Object> values) {
        this.values = values;
    }

    public OgnlContext(ClassResolver classResolver, TypeConverter typeConverter, MemberAccess memberAccess, Map<String, Object> values) {
        this(classResolver, typeConverter, memberAccess);
        this.values = values;
    }

    public void setValues(Map<String, Object> value) {
        this.values.putAll(value);
    }

    public Map<String, Object> getValues() {
        return this.values;
    }

    public void setClassResolver(ClassResolver value) {
        if (value == null) {
            throw new IllegalArgumentException("cannot set ClassResolver to null");
        }
        this.classResolver = value;
    }

    public ClassResolver getClassResolver() {
        return this.classResolver;
    }

    public void setTypeConverter(TypeConverter value) {
        if (value == null) {
            throw new IllegalArgumentException("cannot set TypeConverter to null");
        }
        this.typeConverter = value;
    }

    public TypeConverter getTypeConverter() {
        return this.typeConverter;
    }

    public void setMemberAccess(MemberAccess value) {
        if (value == null) {
            throw new IllegalArgumentException("cannot set MemberAccess to null");
        }
        this.memberAccess = value;
    }

    public MemberAccess getMemberAccess() {
        return this.memberAccess;
    }

    public void setRoot(Object value) {
        this.root = value;
        this.accessorStack.clear();
        this.typeStack.clear();
        this.currentObject = value;
        if (this.currentObject != null) {
            this.setCurrentType(this.currentObject.getClass());
        }
    }

    public Object getRoot() {
        return this.root;
    }

    public boolean getTraceEvaluations() {
        return this.traceEvaluations;
    }

    public void setTraceEvaluations(boolean value) {
        this.traceEvaluations = value;
    }

    public Evaluation getLastEvaluation() {
        return this.lastEvaluation;
    }

    public void setLastEvaluation(Evaluation value) {
        this.lastEvaluation = value;
    }

    public void recycleLastEvaluation() {
        this.lastEvaluation = null;
    }

    public boolean getKeepLastEvaluation() {
        return this.keepLastEvaluation;
    }

    public void setKeepLastEvaluation(boolean value) {
        this.keepLastEvaluation = value;
    }

    public void setCurrentObject(Object value) {
        this.currentObject = value;
    }

    public Object getCurrentObject() {
        return this.currentObject;
    }

    public void setCurrentAccessor(Class<?> type) {
        this.accessorStack.add(type);
    }

    public Class<?> getCurrentAccessor() {
        if (this.accessorStack.isEmpty()) {
            return null;
        }
        return this.accessorStack.peek();
    }

    public Class<?> getPreviousAccessor() {
        if (this.accessorStack.isEmpty()) {
            return null;
        }
        if (this.accessorStack.size() > 1) {
            return (Class)this.accessorStack.get(this.accessorStack.size() - 2);
        }
        return null;
    }

    public Class<?> getFirstAccessor() {
        if (this.accessorStack.isEmpty()) {
            return null;
        }
        return (Class)this.accessorStack.get(0);
    }

    public Class getCurrentType() {
        if (this.typeStack.isEmpty()) {
            return null;
        }
        return this.typeStack.peek();
    }

    public void setCurrentType(Class<?> type) {
        this.typeStack.add(type);
    }

    public Class getPreviousType() {
        if (this.typeStack.isEmpty()) {
            return null;
        }
        if (this.typeStack.size() > 1) {
            return (Class)this.typeStack.get(this.typeStack.size() - 2);
        }
        return null;
    }

    public void setPreviousType(Class<?> type) {
        if (this.typeStack.isEmpty() || this.typeStack.size() < 2) {
            return;
        }
        this.typeStack.set(this.typeStack.size() - 2, type);
    }

    public Class getFirstType() {
        if (this.typeStack.isEmpty()) {
            return null;
        }
        return (Class)this.typeStack.get(0);
    }

    public void setCurrentNode(Node value) {
        this.currentNode = value;
    }

    public Node getCurrentNode() {
        return this.currentNode;
    }

    public Evaluation getCurrentEvaluation() {
        return this.currentEvaluation;
    }

    public void setCurrentEvaluation(Evaluation value) {
        this.currentEvaluation = value;
    }

    public Evaluation getRootEvaluation() {
        return this.rootEvaluation;
    }

    public void setRootEvaluation(Evaluation value) {
        this.rootEvaluation = value;
    }

    public Evaluation getEvaluation(int relativeIndex) {
        Evaluation result = null;
        if (relativeIndex <= 0) {
            for (result = this.currentEvaluation; ++relativeIndex < 0 && result != null; result = result.getParent()) {
            }
        }
        return result;
    }

    public void pushEvaluation(Evaluation value) {
        if (this.currentEvaluation != null) {
            this.currentEvaluation.addChild(value);
        } else {
            this.setRootEvaluation(value);
        }
        this.setCurrentEvaluation(value);
    }

    public Evaluation popEvaluation() {
        Evaluation result = this.currentEvaluation;
        this.setCurrentEvaluation(result.getParent());
        if (this.currentEvaluation == null) {
            this.setLastEvaluation(this.getKeepLastEvaluation() ? result : null);
            this.setRootEvaluation(null);
            this.setCurrentNode(null);
        }
        return result;
    }

    public int incrementLocalReferenceCounter() {
        return ++this.localReferenceCounter;
    }

    public void addLocalReference(String key, LocalReference reference) {
        if (this.localReferenceMap == null) {
            this.localReferenceMap = new LinkedHashMap<String, LocalReference>();
        }
        this.localReferenceMap.put(key, reference);
    }

    public Map<String, LocalReference> getLocalReferences() {
        return this.localReferenceMap;
    }

    @Override
    public int size() {
        return this.values.size();
    }

    @Override
    public boolean isEmpty() {
        return this.values.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.values.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.values.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        Object result = null;
        if (RESERVED_KEYS.contains(key)) {
            if (THIS_CONTEXT_KEY.equals(key)) {
                result = this.getCurrentObject();
            } else if (ROOT_CONTEXT_KEY.equals(key)) {
                result = this.getRoot();
            } else if (CONTEXT_CONTEXT_KEY.equals(key)) {
                result = this;
            } else if (TRACE_EVALUATIONS_CONTEXT_KEY.equals(key)) {
                result = this.getTraceEvaluations() ? Boolean.TRUE : Boolean.FALSE;
            } else if (LAST_EVALUATION_CONTEXT_KEY.equals(key)) {
                result = this.getLastEvaluation();
            } else if (KEEP_LAST_EVALUATION_CONTEXT_KEY.equals(key)) {
                result = this.getKeepLastEvaluation() ? Boolean.TRUE : Boolean.FALSE;
            } else if (CLASS_RESOLVER_CONTEXT_KEY.equals(key)) {
                result = this.getClassResolver();
            } else if (TYPE_CONVERTER_CONTEXT_KEY.equals(key)) {
                result = this.getTypeConverter();
            } else if (MEMBER_ACCESS_CONTEXT_KEY.equals(key)) {
                result = this.getMemberAccess();
            }
        } else {
            result = this.values.get(key);
        }
        return result;
    }

    @Override
    public Object put(String key, Object value) {
        Object result = null;
        if (RESERVED_KEYS.contains(key)) {
            if (CONTEXT_CONTEXT_KEY.equals(key)) {
                throw new IllegalArgumentException("can't change context in context");
            }
            if (THIS_CONTEXT_KEY.equals(key)) {
                result = this.getCurrentObject();
                this.setCurrentObject(value);
            } else if (ROOT_CONTEXT_KEY.equals(key)) {
                result = this.getRoot();
                this.setRoot(value);
            } else if (TRACE_EVALUATIONS_CONTEXT_KEY.equals(key)) {
                result = this.getTraceEvaluations() ? Boolean.TRUE : Boolean.FALSE;
                this.setTraceEvaluations(OgnlOps.booleanValue(value));
            } else if (LAST_EVALUATION_CONTEXT_KEY.equals(key)) {
                result = this.getLastEvaluation();
                this.lastEvaluation = (Evaluation)value;
            } else if (KEEP_LAST_EVALUATION_CONTEXT_KEY.equals(key)) {
                result = this.getKeepLastEvaluation() ? Boolean.TRUE : Boolean.FALSE;
                this.setKeepLastEvaluation(OgnlOps.booleanValue(value));
            } else if (CLASS_RESOLVER_CONTEXT_KEY.equals(key)) {
                result = this.getClassResolver();
                this.setClassResolver((ClassResolver)value);
            } else if (TYPE_CONVERTER_CONTEXT_KEY.equals(key)) {
                result = this.getTypeConverter();
                this.setTypeConverter((TypeConverter)value);
            } else if (MEMBER_ACCESS_CONTEXT_KEY.equals(key)) {
                result = this.getMemberAccess();
                this.setMemberAccess((MemberAccess)value);
            }
        } else {
            result = this.values.put(key, value);
        }
        return result;
    }

    @Override
    public Object remove(Object key) {
        Object result = null;
        if (RESERVED_KEYS.contains(key)) {
            if (CONTEXT_CONTEXT_KEY.equals(key) || TRACE_EVALUATIONS_CONTEXT_KEY.equals(key) || KEEP_LAST_EVALUATION_CONTEXT_KEY.equals(key)) {
                throw new IllegalArgumentException("can't remove " + key + " from context");
            }
            if (THIS_CONTEXT_KEY.equals(key)) {
                result = this.getCurrentObject();
                this.setCurrentObject(null);
            } else if (ROOT_CONTEXT_KEY.equals(key)) {
                result = this.getRoot();
                this.setRoot(null);
            } else if (LAST_EVALUATION_CONTEXT_KEY.equals(key)) {
                result = this.lastEvaluation;
                this.setLastEvaluation(null);
            } else if (CLASS_RESOLVER_CONTEXT_KEY.equals(key)) {
                result = this.getClassResolver();
                this.setClassResolver(null);
            } else if (TYPE_CONVERTER_CONTEXT_KEY.equals(key)) {
                result = this.getTypeConverter();
                this.setTypeConverter(null);
            } else if (MEMBER_ACCESS_CONTEXT_KEY.equals(key)) {
                result = this.getMemberAccess();
                this.setMemberAccess(null);
            }
        } else {
            result = this.values.remove(key);
        }
        return result;
    }

    @Override
    public void putAll(Map<? extends String, ?> t) {
        for (Map.Entry<? extends String, ?> entry : t.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        this.values.clear();
        this.typeStack.clear();
        this.accessorStack.clear();
        this.localReferenceCounter = 0;
        if (this.localReferenceMap != null) {
            this.localReferenceMap.clear();
        }
        this.setRoot(null);
        this.setCurrentObject(null);
        this.setRootEvaluation(null);
        this.setCurrentEvaluation(null);
        this.setLastEvaluation(null);
        this.setCurrentNode(null);
        this.setClassResolver(DEFAULT_CLASS_RESOLVER);
        this.setTypeConverter(DEFAULT_TYPE_CONVERTER);
        this.setMemberAccess(DEFAULT_MEMBER_ACCESS);
    }

    @Override
    public Set<String> keySet() {
        return this.values.keySet();
    }

    @Override
    public Collection<Object> values() {
        return this.values.values();
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return this.values.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return this.values.equals(o);
    }

    @Override
    public int hashCode() {
        return this.values.hashCode();
    }
}

