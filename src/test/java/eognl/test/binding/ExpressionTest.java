package eognl.test.binding;

import eognl.exenhance.DefaultObjectConstructor;
import org.junit.Test;

import eognl.EOgnl;
import eognl.EOgnlContext;
import eognl.EOgnlRuntime;
import eognl.OgnlContext;
import eognl.OgnlException;

import static org.junit.Assert.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExpressionTest {

    public static class MyTest {
        private List<Object> objects;

        public List<Object> getObjects() {
            return objects;
        }

        public void setObjects(List<Object> objects) {
            this.objects = objects;
        }
    }

    public static class InsideTest {
        private String name;
        private String name2;

        public String getName() {
            return name;
        }

        public String getName2() {
            return name2;
        }

        public void setName2(String name2) {
            this.name2 = name2;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface MyTestAnnotation {
        String test() default "test";
    }

    public static class MyTestObjectConstructor extends DefaultObjectConstructor {
        @Override
        public Object processObject(Map<Class, Object> getterAnnotationMap, Object getterObject, Object object) {
            if (object instanceof InsideTest) {
                assertTrue(getterObject instanceof List || getterObject instanceof MyTest3 ||
                        getterObject instanceof Map || getterObject instanceof Set);
                assertTrue(getterAnnotationMap.containsKey(MyTestAnnotation.class));
            }

            return super.processObject(getterAnnotationMap, getterObject, object);
        }
    }

    public static class MyTest2 {
        @MyTestAnnotation
        private List<InsideTest> objects;
        @MyTestAnnotation
        private Map<String, InsideTest> map;
        @MyTestAnnotation
        private Set<InsideTest> set;

        public Set<InsideTest> getSet() {
            return set;
        }

        public void setSet(Set<InsideTest> set) {
            this.set = set;
        }

        public Map<String, InsideTest> getMap() {
            return map;
        }

        public void setMap(Map<String, InsideTest> map) {
            this.map = map;
        }

        public List<InsideTest> getObjects() {
            return objects;
        }

        public void setObjects(List<InsideTest> objects) {
            this.objects = objects;
        }
    }

    public static class MyTest3 {
        private MyTest2 myTest2;
        @MyTestAnnotation
        private InsideTest insideTest;

        public InsideTest getInsideTest() {
            return insideTest;
        }

        public void setInsideTest(InsideTest insideTest) {
            this.insideTest = insideTest;
        }

        public MyTest2 getMyTest2() {
            return myTest2;
        }

        public void setMyTest2(MyTest2 myTest2) {
            this.myTest2 = myTest2;
        }
    }

    @Test
    public void testBinding() throws OgnlException, InstantiationException, IllegalAccessException {
        Object expression = EOgnl.parseExpression("name=test");
        EOgnlContext context = new EOgnlContext(OgnlContext.CONF_AUTO_EXPAND | OgnlContext.CONF_INIT_NULLS
                | OgnlContext.CONF_INIT_UNKNOWN | OgnlContext.CONF_UNKNOWN_TO_LITERAL | OgnlContext.CONF_CAST_PRIMITIVES);
        BindingModel obj = (BindingModel) EOgnlRuntime.createProperObject(BindingModel.class, BindingModel.class.getComponentType());
        EOgnl.getValue(expression, context, obj);
        assertEquals("test", obj.getName());
    }

    @Test
    public void testBinding2() throws OgnlException, InstantiationException, IllegalAccessException {
        Object expression = EOgnl.parseExpression("objects[0]=salam");
        EOgnlContext context = new EOgnlContext(OgnlContext.CONF_AUTO_EXPAND | OgnlContext.CONF_INIT_NULLS
                | OgnlContext.CONF_INIT_UNKNOWN | OgnlContext.CONF_UNKNOWN_TO_LITERAL | OgnlContext.CONF_CAST_PRIMITIVES);
        MyTest root = new MyTest();
        EOgnl.getValue(expression, context, root);
        assertEquals("salam", ((List) root.getObjects()).get(0));
    }

    @Test
    public void testBinding3() throws OgnlException, InstantiationException, IllegalAccessException {
        EOgnlContext context = new EOgnlContext(OgnlContext.CONF_AUTO_EXPAND | OgnlContext.CONF_INIT_NULLS
                | OgnlContext.CONF_INIT_UNKNOWN | OgnlContext.CONF_UNKNOWN_TO_LITERAL | OgnlContext.CONF_CAST_PRIMITIVES);
        context.addObjectConstructor(new MyTestObjectConstructor());
        MyTest2 root = new MyTest2();
        Object expression = EOgnl.parseExpression("objects[0].name=salam");
        EOgnl.getValue(expression, context, root);
        expression = EOgnl.parseExpression("map[test].name=salam");
        EOgnl.getValue(expression, context, root);
        expression = EOgnl.parseExpression("set[0].name=salam");
        EOgnl.getValue(expression, context, root);
        assertEquals("salam", root.getMap().get("test").getName());
        assertEquals("salam", root.getObjects().get(0).getName());
        assertEquals("salam", root.getSet().iterator().next().getName());
    }

    @Test
    public void testBinding4() throws OgnlException {
        Object expression = EOgnl.parseExpression("myTest2.objects[0].name=salam");
        EOgnlContext context = new EOgnlContext(OgnlContext.CONF_AUTO_EXPAND | OgnlContext.CONF_INIT_NULLS
                | OgnlContext.CONF_INIT_UNKNOWN | OgnlContext.CONF_UNKNOWN_TO_LITERAL | OgnlContext.CONF_CAST_PRIMITIVES);
        context.addObjectConstructor(new MyTestObjectConstructor());
        MyTest3 root = new MyTest3();
        EOgnl.getValue(expression, context, root);
        assertEquals("salam", root.getMyTest2().getObjects().get(0).getName());
        expression = EOgnl.parseExpression("myTest2.objects[0].name2=xodafiz");
        EOgnl.getValue(expression, context, root);
        assertEquals("xodafiz", root.getMyTest2().getObjects().get(0).getName2());
        expression = EOgnl.parseExpression("insideTest.name2=hi");
        EOgnl.getValue(expression, context, root);
        assertEquals("hi", root.getInsideTest().getName2());
    }

    @Test
    public void testBinding_multiIndex() throws OgnlException {
        EOgnlContext context = new EOgnlContext(OgnlContext.CONF_AUTO_EXPAND | OgnlContext.CONF_INIT_NULLS
                | OgnlContext.CONF_INIT_UNKNOWN | OgnlContext.CONF_UNKNOWN_TO_LITERAL | OgnlContext.CONF_CAST_PRIMITIVES);
        context.addObjectConstructor(new MyTestObjectConstructor());
        MyTest3 root = new MyTest3();
        Object expression = EOgnl.parseExpression("myTest2.objects[0].name=obj0");
        EOgnl.getValue(expression, context, root);
        expression = EOgnl.parseExpression("myTest2.objects[1].name=obj1");
        EOgnl.getValue(expression, context, root);
        expression = EOgnl.parseExpression("myTest2.objects[0].name2=bye");
        EOgnl.getValue(expression, context, root);
        expression = EOgnl.parseExpression("myTest2.set[0].name=set0");
        EOgnl.getValue(expression, context, root);
        expression = EOgnl.parseExpression("myTest2.set[1].name=set1");
        EOgnl.getValue(expression, context, root);
        expression = EOgnl.parseExpression("myTest2.map[map0].name=map0");
        EOgnl.getValue(expression, context, root);
        expression = EOgnl.parseExpression("myTest2.map[map1].name=map1");
        EOgnl.getValue(expression, context, root);
        assertEquals("obj0", root.getMyTest2().getObjects().get(0).getName());
        assertEquals("bye", root.getMyTest2().getObjects().get(0).getName2());
        assertEquals("obj1", root.getMyTest2().getObjects().get(1).getName());
        Iterator<InsideTest> it = root.getMyTest2().getSet().iterator();
        assertEquals("set0", it.next().getName());
        assertEquals("set1", it.next().getName());
        assertEquals("map0", root.getMyTest2().getMap().get("map0").getName());
        assertEquals("map1", root.getMyTest2().getMap().get("map1").getName());
    }
}
